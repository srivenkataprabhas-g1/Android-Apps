package com.example.busschedule

import android.app.Application
import com.example.busschedule.data.AppDatabase

class BusScheduleApp: Application() {
    val database: AppDatabase by lazy { AppDatabase.Companion.getDatabase(this) }
}