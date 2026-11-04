package com.quickscanner.app.engine

import android.graphics.*
import kotlin.math.max
import kotlin.math.min

object ImageProcessor {

    enum class FilterType {
        ORIGINAL,
        AUTO_COLOR,
        GRAYSCALE,
        BLACK_AND_WHITE,
        WHITEBOARD
    }

    fun applyFilter(bitmap: Bitmap, filter: FilterType): Bitmap {
        return when (filter) {
            FilterType.ORIGINAL -> bitmap
            FilterType.AUTO_COLOR -> enhanceAutoColor(bitmap)
            FilterType.GRAYSCALE -> toGrayscale(bitmap)
            FilterType.BLACK_AND_WHITE -> toBlackAndWhite(bitmap)
            FilterType.WHITEBOARD -> toWhiteboard(bitmap)
        }
    }

    fun toGrayscale(src: Bitmap): Bitmap {
        val dest = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return dest
    }

    fun enhanceAutoColor(src: Bitmap): Bitmap {
        val dest = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dest)
        val paint = Paint()
        // Contrast & Saturation boost
        val contrast = 1.25f
        val translate = (-0.5f * contrast + 0.5f) * 255f
        val colorMatrix = ColorMatrix(
            floatArrayOf(
                contrast, 0f, 0f, 0f, translate,
                0f, contrast, 0f, 0f, translate,
                0f, 0f, contrast, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(src, 0f, 0f, paint)
        return dest
    }

    fun toBlackAndWhite(src: Bitmap, threshold: Int = 135): Bitmap {
        val width = src.width
        val height = src.height
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            pixels[i] = if (lum > threshold) Color.WHITE else Color.BLACK
        }

        dest.setPixels(pixels, 0, width, 0, 0, width, height)
        return dest
    }

    fun toWhiteboard(src: Bitmap): Bitmap {
        // High-contrast clean paper effect: suppresses background shadows while keeping dark marker/ink crisp
        val width = src.width
        val height = src.height
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF

            // Stretches highlights to pure white
            val newR = min(255, max(0, ((r - 80) * 1.5).toInt()))
            val newG = min(255, max(0, ((g - 80) * 1.5).toInt()))
            val newB = min(255, max(0, ((b - 80) * 1.5).toInt()))

            pixels[i] = Color.rgb(newR, newG, newB)
        }

        dest.setPixels(pixels, 0, width, 0, 0, width, height)
        return dest
    }

    fun rotate(src: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    fun mergeIdCard(front: Bitmap, back: Bitmap): Bitmap {
        // Standard A4 aspect ratio 1:1.414 (e.g. 1240 x 1754 px at 150 DPI)
        val pageWidth = 1240
        val pageHeight = 1754
        val result = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawColor(Color.WHITE)

        val cardTargetWidth = (pageWidth * 0.75f).toInt()
        val frontRatio = front.height.toFloat() / front.width.toFloat()
        val frontTargetHeight = (cardTargetWidth * frontRatio).toInt()

        val backRatio = back.height.toFloat() / back.width.toFloat()
        val backTargetHeight = (cardTargetWidth * backRatio).toInt()

        val leftX = ((pageWidth - cardTargetWidth) / 2).toFloat()
        val topFront = (pageHeight * 0.15f)
        val topBack = topFront + frontTargetHeight + (pageHeight * 0.10f)

        val borderPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }

        // Draw Front
        val frontScaled = Bitmap.createScaledBitmap(front, cardTargetWidth, frontTargetHeight, true)
        canvas.drawBitmap(frontScaled, leftX, topFront, null)
        canvas.drawRect(leftX, topFront, leftX + cardTargetWidth, topFront + frontTargetHeight, borderPaint)

        // Draw Back
        val backScaled = Bitmap.createScaledBitmap(back, cardTargetWidth, backTargetHeight, true)
        canvas.drawBitmap(backScaled, leftX, topBack, null)
        canvas.drawRect(leftX, topBack, leftX + cardTargetWidth, topBack + backTargetHeight, borderPaint)

        return result
    }
}

