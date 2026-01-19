package com.prabhas.bookshelfapp.data

import com.prabhas.bookshelfapp.model.BookItem
import com.prabhas.bookshelfapp.network.BooksApiService

interface BooksRepository {
    suspend fun searchBooks(query: String): List<BookItem>
    suspend fun getBookById(id: String): BookItem
}

class DefaultBooksRepository(
    private val api: BooksApiService
) : BooksRepository {

    override suspend fun searchBooks(query: String): List<BookItem> {
        return api.searchBooks(query).items
    }

    override suspend fun getBookById(id: String): BookItem {
        return api.getBookById(id)
    }
}