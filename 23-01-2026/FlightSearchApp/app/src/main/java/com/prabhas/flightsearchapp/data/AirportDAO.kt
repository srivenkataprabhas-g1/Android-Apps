package com.prabhas.flightsearchapp.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDAO {
    @Query("""
        SELECT * FROM airport 
        WHERE iata_code LIKE '%' || :query || '%' 
        OR name LIKE '%' || :query || '%' 
        ORDER BY passengers DESC
    """)
    fun getAirport(query: String): Flow<List<Airport>>
}