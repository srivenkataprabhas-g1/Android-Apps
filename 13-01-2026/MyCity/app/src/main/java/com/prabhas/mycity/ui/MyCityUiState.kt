package com.prabhas.mycity.ui

import com.prabhas.mycity.data.LocalDataProvider
import com.prabhas.mycity.model.Category
import com.prabhas.mycity.model.Recommendation

data class MyCityUiState(
    val categories: List<Category> = LocalDataProvider.categories,
    val currentCategory: Category = Category.CoffeeShops,
    val recommendations: List<Recommendation> = LocalDataProvider.recommendations.filter { it.category == Category.CoffeeShops },
    val selectedRecommendation: Recommendation? = null,
    val isShowingListPage: Boolean = true
)
