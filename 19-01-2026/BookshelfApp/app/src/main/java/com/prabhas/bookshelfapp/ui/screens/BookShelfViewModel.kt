package com.prabhas.bookshelfapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.prabhas.bookshelfapp.data.BooksRepository
import com.prabhas.bookshelfapp.model.BookItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class BookshelfUiState {
    object Loading : BookshelfUiState()
    data class Success(val books: List<BookItem>) : BookshelfUiState()
    data class Error(val message: String) : BookshelfUiState()
}

class BookshelfViewModel(private val repository: BooksRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BookshelfUiState>(BookshelfUiState.Loading)
    val uiState: StateFlow<BookshelfUiState> = _uiState

    init {
        searchBooks("jazz history")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch {
            _uiState.value = BookshelfUiState.Loading
            try {
                val books = repository.searchBooks(query)
                _uiState.value = BookshelfUiState.Success(books)
            } catch (e: Exception) {
                _uiState.value = BookshelfUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    class Factory(private val repository: BooksRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BookshelfViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BookshelfViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}