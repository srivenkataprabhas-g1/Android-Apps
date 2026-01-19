package com.prabhas.bookshelfapp.data

import com.prabhas.bookshelfapp.network.BooksApiService
import com.prabhas.bookshelfapp.ui.screens.BookshelfViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Application-level dependency container.
 *
 * This container is responsible for providing single instances of Retrofit, API service,
 * repository, and ViewModel across the app.
 */
class AppContainer {

    // Base URL for Google Books API
    private val BASE_URL = "https://www.googleapis.com/books/v1/"

    // Retrofit instance with Gson converter
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Service
    private val apiService: BooksApiService by lazy {
        retrofit.create(BooksApiService::class.java)
    }

    // Repository: Handles all data operations
    val repository: BooksRepository by lazy {
        DefaultBooksRepository(apiService)
    }

    // ViewModel provider
    fun getViewModel(): BookshelfViewModel = BookshelfViewModel(repository)
}
