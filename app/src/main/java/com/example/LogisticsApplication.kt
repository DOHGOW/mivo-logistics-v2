package com.example

import android.app.Application
import com.example.db.AppDatabase
import com.example.db.LogisticsRepository
import com.example.firebase.FirebaseSyncManager

class LogisticsApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { LogisticsRepository(database) }

    override fun onCreate() {
        super.onCreate()
        FirebaseSyncManager.initialize(this)
    }
}
