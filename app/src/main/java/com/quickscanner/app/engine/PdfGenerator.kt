package com.quickscanner.app.engine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    // Standard A4 dimensions in points (72 DPI): 595 x 842 pt
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842

    suspend fun createPdfFromImages(
        imagePaths: List<String>,
        outputFile: File,
        onProgress: (current: Int, total: Int) -> Unit = { _, _ -> }
    ): File = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        try {
            outputFile.parentFile?.let { if (!it.exists()) it.mkdirs() }

            imagePaths.forEachIndexed { index, path ->
                val file = File(path)
                if (!file.exists()) return@forEachIndexed

                // Downsample ultra-high-res camera captures to prevent OutOfMemoryError
                val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                BitmapFactory.decodeFile(path, boundsOptions)

                var inSample = 1
                while (boundsOptions.outWidth / inSample > 2200 || boundsOptions.outHeight / inSample > 2200) {
                    inSample *= 2
                }

                val decodeOptions = BitmapFactory.Options().apply { inSampleSize = inSample }
                val bitmap = BitmapFactory.decodeFile(path, decodeOptions) ?: return@forEachIndexed

                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                // Maintain aspect ratio while fitting into A4
                val scale = minOf(
                    PAGE_WIDTH.toFloat() / bitmap.width.toFloat(),
                    PAGE_HEIGHT.toFloat() / bitmap.height.toFloat()
                )
                val destWidth = (bitmap.width * scale).toInt()
                val destHeight = (bitmap.height * scale).toInt()
                val left = (PAGE_WIDTH - destWidth) / 2
                val top = (PAGE_HEIGHT - destHeight) / 2

                val destRect = Rect(left, top, left + destWidth, top + destHeight)
                canvas.drawBitmap(bitmap, null, destRect, null)

                pdfDocument.finishPage(page)
                bitmap.recycle()
                onProgress(index + 1, imagePaths.size)
            }

            FileOutputStream(outputFile).use { fos ->
                pdfDocument.writeTo(fos)
            }
        } finally {
            pdfDocument.close()
        }

        outputFile
    }

    suspend fun createPdfFromBitmaps(
        bitmaps: List<Bitmap>,
        outputFile: File
    ): File = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        try {
            outputFile.parentFile?.let { if (!it.exists()) it.mkdirs() }

            bitmaps.forEachIndexed { index, bitmap ->
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                val scale = minOf(
                    PAGE_WIDTH.toFloat() / bitmap.width.toFloat(),
                    PAGE_HEIGHT.toFloat() / bitmap.height.toFloat()
                )
                val destWidth = (bitmap.width * scale).toInt()
                val destHeight = (bitmap.height * scale).toInt()
                val left = (PAGE_WIDTH - destWidth) / 2
                val top = (PAGE_HEIGHT - destHeight) / 2

                canvas.drawBitmap(bitmap, null, Rect(left, top, left + destWidth, top + destHeight), null)
                pdfDocument.finishPage(page)
            }

            FileOutputStream(outputFile).use { fos ->
                pdfDocument.writeTo(fos)
            }
        } finally {
            pdfDocument.close()
        }
        outputFile
    }
}

