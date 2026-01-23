package com.prabhas.flightsearchapp

import android.app.Application
import com.prabhas.flightsearchapp.data.AppDatabase

class AirportApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}