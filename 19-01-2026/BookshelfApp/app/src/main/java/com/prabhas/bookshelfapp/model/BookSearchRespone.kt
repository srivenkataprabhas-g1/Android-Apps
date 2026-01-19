package com.prabhas.bookshelfapp.model

data class BookSearchResponse(
    val items: List<BookItem> = emptyList()
)
