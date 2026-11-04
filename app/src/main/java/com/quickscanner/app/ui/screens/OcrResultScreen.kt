package com.quickscanner.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quickscanner.app.QuickScannerApp
import com.quickscanner.app.engine.OcrEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrResultScreen(
    docId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val dao = QuickScannerApp.database.scanDocumentDao()
    val scope = rememberCoroutineScope()

    var extractedText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(docId) {
        withContext(Dispatchers.IO) {
            val doc = dao.getDocumentById(docId)
            if (doc != null) {
                if (!doc.ocrText.isNullOrBlank()) {
                    extractedText = doc.ocrText
                    isLoading = false
                } else {
                    val fullText = StringBuilder()
                    doc.pagePaths.forEachIndexed { index, path ->
                        val file = File(path)
                        if (file.exists()) {
                            try {
                                val text = OcrEngine.recognizeTextFromUri(context, android.net.Uri.fromFile(file))
                                if (text.isNotBlank()) {
                                    if (doc.pagePaths.size > 1) {
                                        fullText.append("--- PAGE ${index + 1} ---\n")
                                    }
                                    fullText.append(text).append("\n\n")
                                }
                            } catch (e: Exception) {
                                // Continue with other pages
                            }
                        }
                    }
                    val result = fullText.toString().trim()
                    extractedText = result.ifBlank { "No readable text detected in this document." }
                    dao.updateDocument(doc.copy(ocrText = extractedText))
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recognized Text (OCR)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Scanned Text", extractedText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                    }
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, extractedText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Extracted Text"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share Text")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Reading text with on-device OCR…", fontSize = 14.sp)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = extractedText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

