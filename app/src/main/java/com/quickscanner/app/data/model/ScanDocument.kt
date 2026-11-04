package com.quickscanner.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "scanned_documents")
@TypeConverters(StringListConverter::class)
data class ScanDocument(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val pagePaths: List<String>,
    val pdfPath: String? = null,
    val pageCount: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val ocrText: String? = null,
    val isFavorite: Boolean = false
)

class StringListConverter {
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(separator = "|||")
    }

    @TypeConverter
    fun toList(data: String): List<String> {
        if (data.isBlank()) return emptyList()
        return data.split("|||")
    }
}

