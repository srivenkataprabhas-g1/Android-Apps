package com.prabhas.bookshelfapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhas.bookshelfapp.R
import com.prabhas.bookshelfapp.data.AppContainer
import com.prabhas.bookshelfapp.ui.screens.BookshelfScreen
import com.prabhas.bookshelfapp.ui.screens.BookshelfViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookshelfApp(container: AppContainer) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val viewModel: BookshelfViewModel = viewModel(
                factory = BookshelfViewModel.Factory(container.repository)
            )

            BookshelfScreen(
                uiState = viewModel.uiState,
                retryAction = { viewModel.searchBooks("android") },
                modifier = Modifier.fillMaxSize(),
                contentPadding = paddingValues
            )
        }
    }
}
