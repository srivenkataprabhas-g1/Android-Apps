package com.prabhas.mycity.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Recommendation(
    val id: Int,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @StringRes val addressRes: Int,
    @DrawableRes val imageRes: Int,
    val category: Category
)

enum class Category {
    CoffeeShops,
    Restaurants,
    Parks,
    Museums,
    Temples,
    Zoos
}
