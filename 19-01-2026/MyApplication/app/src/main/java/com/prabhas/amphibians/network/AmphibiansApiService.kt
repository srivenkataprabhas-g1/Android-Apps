package com.prabhas.amphibians.network

import com.prabhas.amphibians.model.Amphibian
import retrofit2.http.GET

interface AmphibiansApiService {
    @GET("amphibians/")
    suspend fun getAmphibians(): List<Amphibian>
}