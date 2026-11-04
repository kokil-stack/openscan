package com.quickscanner.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.quickscanner.app.data.dao.ScanDocumentDao
import com.quickscanner.app.data.model.ScanDocument

@Database(entities = [ScanDocument::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDocumentDao(): ScanDocumentDao
}
