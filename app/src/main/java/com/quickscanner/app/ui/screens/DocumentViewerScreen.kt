package com.quickscanner.app.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.quickscanner.app.QuickScannerApp
import com.quickscanner.app.data.model.ScanDocument
import com.quickscanner.app.engine.ImageProcessor
import com.quickscanner.app.engine.PdfGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentViewerScreen(
    docId: Long,
    onBack: () -> Unit,
    onOpenOcr: (Long) -> Unit
) {
    val dao = QuickScannerApp.database.scanDocumentDao()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var document by remember { mutableStateOf<ScanDocument?>(null) }
    var currentPages by remember { mutableStateOf<List<String>>(emptyList()) }
    var isProcessing by remember { mutableStateOf(false) }

    LaunchedEffect(docId) {
        val doc = dao.getDocumentById(docId)
        document = doc
        currentPages = doc?.pagePaths ?: emptyList()
    }

    if (document == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val doc = document!!
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { currentPages.size })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(doc.title, maxLines = 1, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Page ${pagerState.currentPage + 1} of ${currentPages.size}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenOcr(doc.id) }) {
                        Icon(Icons.Default.TextFields, contentDescription = "OCR Text")
                    }
                    IconButton(onClick = { sharePdf(context, doc) }) {
                        Icon(Icons.Default.Share, contentDescription = "Share PDF")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    // Filter Mode Selection Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        FilterButton("Auto", Icons.Default.AutoFixHigh) {
                            applyFilterToCurrentPage(
                                context,
                                currentPages[pagerState.currentPage],
                                ImageProcessor.FilterType.AUTO_COLOR
                            ) { updatedPath ->
                                currentPages = currentPages.toMutableList().also { it[pagerState.currentPage] = updatedPath }
                                updateDocPages(dao, scope, doc, currentPages)
                            }
                        }
                        FilterButton("B&W", Icons.Default.Contrast) {
                            applyFilterToCurrentPage(
                                context,
                                currentPages[pagerState.currentPage],
                                ImageProcessor.FilterType.BLACK_AND_WHITE
                            ) { updatedPath ->
                                currentPages = currentPages.toMutableList().also { it[pagerState.currentPage] = updatedPath }
                                updateDocPages(dao, scope, doc, currentPages)
                            }
                        }
                        FilterButton("Gray", Icons.Default.FilterBAndW) {
                            applyFilterToCurrentPage(
                                context,
                                currentPages[pagerState.currentPage],
                                ImageProcessor.FilterType.GRAYSCALE
                            ) { updatedPath ->
                                currentPages = currentPages.toMutableList().also { it[pagerState.currentPage] = updatedPath }
                                updateDocPages(dao, scope, doc, currentPages)
                            }
                        }
                        FilterButton("Board", Icons.Default.Lightbulb) {
                            applyFilterToCurrentPage(
                                context,
                                currentPages[pagerState.currentPage],
                                ImageProcessor.FilterType.WHITEBOARD
                            ) { updatedPath ->
                                currentPages = currentPages.toMutableList().also { it[pagerState.currentPage] = updatedPath }
                                updateDocPages(dao, scope, doc, currentPages)
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // Action Controls: Rotate, Delete page, Re-generate PDF
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            val cur = currentPages[pagerState.currentPage]
                            rotateCurrentPage(context, cur) { updatedPath ->
                                currentPages = currentPages.toMutableList().also { it[pagerState.currentPage] = updatedPath }
                                updateDocPages(dao, scope, doc, currentPages)
                            }
                        }) {
                            Icon(Icons.Default.RotateRight, contentDescription = "Rotate 90°")
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    val pdfFile = File(context.filesDir, "quickscan_${doc.id}.pdf")
                                    PdfGenerator.createPdfFromImages(currentPages, pdfFile)
                                    dao.updateDocument(doc.copy(pdfPath = pdfFile.absolutePath))
                                    isProcessing = false
                                    Toast.makeText(context, "PDF Exported Successfully!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export PDF")
                        }

                        if (currentPages.size > 1) {
                            IconButton(onClick = {
                                val deleteIndex = pagerState.currentPage
                                val updated = currentPages.toMutableList().apply { removeAt(deleteIndex) }
                                currentPages = updated
                                updateDocPages(dao, scope, doc, updated)
                            }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Page", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val pagePath = currentPages.getOrNull(pageIndex)
                if (pagePath != null) {
                    AsyncImage(
                        model = File(pagePath),
                        contentDescription = "Page ${pageIndex + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            if (isProcessing) {
                Surface(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Processing document…", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        FilledTonalIconButton(onClick = onClick, modifier = Modifier.size(40.dp)) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 11.sp)
    }
}

private fun applyFilterToCurrentPage(
    context: Context,
    imagePath: String,
    filter: ImageProcessor.FilterType,
    onSuccess: (String) -> Unit
) {
    val srcBitmap = BitmapFactory.decodeFile(imagePath) ?: return
    val filtered = ImageProcessor.applyFilter(srcBitmap, filter)

    val updatedFile = File(context.filesDir, "img_${System.currentTimeMillis()}.jpg")
    FileOutputStream(updatedFile).use { out ->
        filtered.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
    }
    srcBitmap.recycle()
    filtered.recycle()
    onSuccess(updatedFile.absolutePath)
}

private fun rotateCurrentPage(
    context: Context,
    imagePath: String,
    onSuccess: (String) -> Unit
) {
    val srcBitmap = BitmapFactory.decodeFile(imagePath) ?: return
    val rotated = ImageProcessor.rotate(srcBitmap, 90f)

    val updatedFile = File(context.filesDir, "img_${System.currentTimeMillis()}.jpg")
    FileOutputStream(updatedFile).use { out ->
        rotated.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, out)
    }
    srcBitmap.recycle()
    rotated.recycle()
    onSuccess(updatedFile.absolutePath)
}

private fun updateDocPages(
    dao: com.quickscanner.app.data.dao.ScanDocumentDao,
    scope: kotlinx.coroutines.CoroutineScope,
    doc: ScanDocument,
    pages: List<String>
) {
    scope.launch {
        dao.updateDocument(
            doc.copy(
                pagePaths = pages,
                pageCount = pages.size
            )
        )
    }
}

private fun sharePdf(context: Context, doc: ScanDocument) {
    val pdfFile = doc.pdfPath?.let { File(it) } ?: return
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, doc.title)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}
