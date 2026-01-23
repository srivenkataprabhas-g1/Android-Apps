package com.prabhas.flightsearchapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prabhas.flightsearchapp.AirportApplication
import com.prabhas.flightsearchapp.data.Airport
import com.prabhas.flightsearchapp.data.AirportDAO
import com.prabhas.flightsearchapp.data.Favorite
import com.prabhas.flightsearchapp.data.FavoriteDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AirportViewModel(
    private val airportDao: AirportDAO,
    private val favoriteDao: FavoriteDAO
) : ViewModel() {

    fun getAirport(query: String): Flow<List<Airport>> = airportDao.getAirport(query)

    fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()

    fun getFavorite(departureCode: String, destinationCode: String): Flow<List<Favorite>> =
        favoriteDao.getFavorite(departureCode, destinationCode)

    fun insertFavorite(departureCode: String, destinationCode: String) {
        viewModelScope.launch {
            favoriteDao.insertFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
        }
    }

    fun deleteFavorite(favorite: Favorite) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(favorite.departureCode, favorite.destinationCode)
        }
    }

    fun deleteAllFavorites() {
        viewModelScope.launch {
            favoriteDao.deleteAllFavorites()
        }
    }

    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean> =
        favoriteDao.isFavorite(departureCode, destinationCode)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AirportApplication)
                AirportViewModel(application.database.airportDao(), application.database.favoriteDao())
            }
        }
    }
}