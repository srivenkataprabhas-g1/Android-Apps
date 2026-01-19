package com.prabhas.amphibians.service

import androidx.lifecycle.ViewModel
import android.net.http.HttpException
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.prabhas.amphibians.AmphibiansApplication
import com.prabhas.amphibians.data.AmphibiansRepository
import com.prabhas.amphibians.service.AmphibiansUiState
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel containing the app data and method to retrieve the data
 */
class AmphibiansViewModel(private val amphibiansRepository: AmphibiansRepository) : ViewModel(){

    //Set UI state as Loading by default
    var amphibiansUiState: AmphibiansUiState by mutableStateOf(AmphibiansUiState.Loading)
        private set
    init {
        getAmphibians()
    }

    fun getAmphibians(){
        viewModelScope.launch {
            amphibiansUiState=AmphibiansUiState.Loading
            amphibiansUiState=try {
                AmphibiansUiState.Success(amphibiansRepository.getAmphibians())
            }catch (e: IOException){
                AmphibiansUiState.Error
            }catch (e: HttpException){
                AmphibiansUiState.Error
            }
        }
    }
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                        as AmphibiansApplication)
                val amphibiansRepository = application.container.amphibiansRepository
                AmphibiansViewModel(amphibiansRepository = amphibiansRepository)
            }
        }
    }
}