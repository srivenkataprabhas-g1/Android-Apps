package com.prabhas.flightsearchapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.prabhas.flightsearchapp.data.Airport
import com.prabhas.flightsearchapp.data.AirportDAO
import com.prabhas.flightsearchapp.data.Favorite
import com.prabhas.flightsearchapp.data.FavoriteDAO
@Database(entities = [Airport::class, Favorite::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun airportDao(): AirportDAO
    abstract fun favoriteDao(): FavoriteDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flight_search.db"
                )
                    .createFromAsset("database/flight_search.db") // <-- Copy pre-populated DB from assets
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}