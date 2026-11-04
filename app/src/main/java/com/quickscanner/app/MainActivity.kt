package com.quickscanner.app

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.quickscanner.app.data.model.ScanDocument
import com.quickscanner.app.engine.DocScannerEngine
import com.quickscanner.app.ui.navigation.NavGraph
import com.quickscanner.app.ui.navigation.Screen
import com.quickscanner.app.ui.theme.QuickScannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var scannerLauncher: ActivityResultLauncher<IntentSenderRequest>
    private var pendingNavigateToDocId: ((Long) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scanner = DocScannerEngine.createScanner(this)

        scannerLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                scanningResult?.let { handleScanSuccess(it) }
            }
        }

        setContent {
            QuickScannerTheme {
                val navController = rememberNavController()
                pendingNavigateToDocId = { docId ->
                    navController.navigate(Screen.DocumentViewer.createRoute(docId))
                }

                NavGraph(
                    navController = navController,
                    onLaunchScanner = {
                        DocScannerEngine.startScan(
                            scanner = scanner,
                            activity = this,
                            onIntentReady = { request -> scannerLauncher.launch(request) },
                            onError = { e ->
                                Toast.makeText(this, "Failed to launch scanner: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                )
            }
        }
    }

    private fun handleScanSuccess(scanningResult: GmsDocumentScanningResult) {
        lifecycleScope.launch(Dispatchers.IO) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val savedPagePaths = mutableListOf<String>()

            // Save scanned page JPEGs locally
            val pages = scanningResult.pages
            pages?.forEachIndexed { index, page ->
                val sourceUri = page.imageUri
                val targetFile = File(filesDir, "scan_${timestamp}_p${index + 1}.jpg")
                copyUriToFile(sourceUri, targetFile)
                savedPagePaths.add(targetFile.absolutePath)
            }

            // Save generated PDF if available
            val pdfUri = scanningResult.pdf?.uri
            val pdfPath = pdfUri?.let { uri ->
                val targetPdf = File(filesDir, "scan_${timestamp}.pdf")
                copyUriToFile(uri, targetPdf)
                targetPdf.absolutePath
            }

            val title = "Scan ${SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date())}"
            val doc = ScanDocument(
                title = title,
                pagePaths = savedPagePaths,
                pdfPath = pdfPath,
                pageCount = savedPagePaths.size
            )

            val docId = QuickScannerApp.database.scanDocumentDao().insertDocument(doc)

            withContext(Dispatchers.Main) {
                pendingNavigateToDocId?.invoke(docId)
            }
        }
    }

    private fun copyUriToFile(uri: Uri, dest: File) {
        contentResolver.openInputStream(uri)?.use { input: InputStream ->
            FileOutputStream(dest).use { output ->
                input.copyTo(output)
            }
        }
    }
}
