package com.quickscanner.app

import android.app.Application
import androidx.room.Room
import com.quickscanner.app.data.database.AppDatabase

class QuickScannerApp : Application() {
    companion object {
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quick_scanner.db"
        ).fallbackToDestructiveMigration().build()
    }
}
