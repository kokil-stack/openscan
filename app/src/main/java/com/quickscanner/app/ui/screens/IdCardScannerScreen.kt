package com.quickscanner.app.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun IdCardScannerScreen(
    onBack: () -> Unit,
    onComplete: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = QuickScannerApp.database.scanDocumentDao()

    var frontBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var backBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturingStep by remember { mutableStateOf(1) } // 1 = Front, 2 = Back
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = context.contentResolver.openInputStream(it)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }
            if (capturingStep == 1) {
                frontBitmap = bitmap
                capturingStep = 2
            } else {
                backBitmap = bitmap
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ID Card Scanner", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (frontBitmap == null) "Step 1: Capture FRONT of ID"
                    else if (backBitmap == null) "Step 2: Capture BACK of ID"
                    else "Ready to Generate Single Page Composite",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Card Slots Preview
                CardSlot(
                    label = "Front Side",
                    bitmap = frontBitmap,
                    isActive = capturingStep == 1 && frontBitmap == null,
                    onClick = {
                        capturingStep = 1
                        photoPickerLauncher.launch("image/*")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CardSlot(
                    label = "Back Side",
                    bitmap = backBitmap,
                    isActive = capturingStep == 2 && backBitmap == null,
                    onClick = {
                        capturingStep = 2
                        photoPickerLauncher.launch("image/*")
                    }
                )
            }

            if (frontBitmap != null && backBitmap != null) {
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            withContext(Dispatchers.IO) {
                                val composite = ImageProcessor.mergeIdCard(frontBitmap!!, backBitmap!!)
                                val imageFile = File(context.filesDir, "id_card_${System.currentTimeMillis()}.jpg")
                                FileOutputStream(imageFile).use { out ->
                                    composite.compress(Bitmap.CompressFormat.JPEG, 95, out)
                                }

                                val pdfFile = File(context.filesDir, "id_card_${System.currentTimeMillis()}.pdf")
                                PdfGenerator.createPdfFromBitmaps(listOf(composite), pdfFile)

                                val docId = dao.insertDocument(
                                    ScanDocument(
                                        title = "ID Card Scan",
                                        pagePaths = listOf(imageFile.absolutePath),
                                        pdfPath = pdfFile.absolutePath,
                                        pageCount = 1
                                    )
                                )
                                withContext(Dispatchers.Main) {
                                    isSaving = false
                                    onComplete(docId)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Done, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Composite Scan (1 Page)")
                    }
                }
            } else {
                Button(
                    onClick = { photoPickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (capturingStep == 1) "Select Front Side" else "Select Back Side")
                }
            }
        }
    }
}

@Composable
private fun CardSlot(
    label: String,
    bitmap: Bitmap?,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (bitmap != null) {
                AsyncImage(
                    model = bitmap,
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(label, fontWeight = FontWeight.SemiBold)
                    Text("Tap to capture or pick", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

