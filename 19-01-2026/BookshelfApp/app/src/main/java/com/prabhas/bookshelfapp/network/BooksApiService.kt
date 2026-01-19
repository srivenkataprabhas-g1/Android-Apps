package com.prabhas.bookshelfapp.network

import com.prabhas.bookshelfapp.model.BookItem
import com.prabhas.bookshelfapp.model.BookSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BooksApiService {

    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BookSearchResponse

    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): BookItem
}