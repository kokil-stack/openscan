package com.quickscanner.app.engine

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

object DocScannerEngine {

    fun createScanner(activity: Activity): GmsDocumentScanner {
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(100) // Unlimited batches
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .setScannerMode(SCANNER_MODE_FULL) // Auto-crop, perspective warp, edge detection
            .build()

        return GmsDocumentScanning.getClient(options)
    }

    fun startScan(
        scanner: GmsDocumentScanner,
        activity: Activity,
        onIntentReady: (IntentSenderRequest) -> Unit,
        onError: (Exception) -> Unit
    ) {
        scanner.getStartScanIntent(activity)
            .addOnSuccessListener { intentSender: IntentSender ->
                val request = IntentSenderRequest.Builder(intentSender).build()
                onIntentReady(request)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}
