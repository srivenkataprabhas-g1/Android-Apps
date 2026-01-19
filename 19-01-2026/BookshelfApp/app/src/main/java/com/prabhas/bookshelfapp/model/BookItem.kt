package com.prabhas.bookshelfapp.model

import com.google.gson.annotations.SerializedName

data class BookItem(
    val id: String,
    @SerializedName("volumeInfo")
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?,
    val description: String?,
    @SerializedName("imageLinks")
    val imageLinks: ImageLinks?
)