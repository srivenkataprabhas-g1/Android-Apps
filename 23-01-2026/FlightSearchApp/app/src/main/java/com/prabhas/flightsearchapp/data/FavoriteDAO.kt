package com.prabhas.flightsearchapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDAO {

    @Query("SELECT * FROM favorite")
    fun getAllFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    fun getFavorite(departureCode: String, destinationCode: String): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite): Long

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String): Int

    @Query("DELETE FROM favorite")
    suspend fun deleteAllFavorites(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode)")
    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean>
}