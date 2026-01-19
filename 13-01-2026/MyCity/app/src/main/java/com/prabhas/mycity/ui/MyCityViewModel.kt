package com.prabhas.mycity.ui

import androidx.lifecycle.ViewModel
import com.prabhas.mycity.data.LocalDataProvider
import com.prabhas.mycity.model.Category
import com.prabhas.mycity.model.Recommendation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class   MyCityViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyCityUiState())
    val uiState: StateFlow<MyCityUiState> = _uiState

    fun updateCurrentCategory(category: Category) {
        _uiState.update {
            it.copy(
                currentCategory = category,
                recommendations = LocalDataProvider.recommendations.filter { recommendation ->
                    recommendation.category == category
                },
                isShowingListPage = true
            )
        }
    }

    fun updateSelectedRecommendation(recommendation: Recommendation) {
        _uiState.update {
            it.copy(
                selectedRecommendation = recommendation,
                isShowingListPage = false
            )
        }
    }

    fun resetHomeScreenStates() {
        _uiState.update {
            it.copy(
                currentCategory = Category.CoffeeShops,
                recommendations = LocalDataProvider.recommendations.filter { recommendation ->
                    recommendation.category == Category.CoffeeShops
                },
                isShowingListPage = true
            )
        }
    }

    fun navigateToRecommendationList() {
        _uiState.update {
            it.copy(isShowingListPage = true)
        }
    }
}
