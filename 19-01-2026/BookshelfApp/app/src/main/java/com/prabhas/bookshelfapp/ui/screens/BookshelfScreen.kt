package com.prabhas.bookshelfapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.prabhas.bookshelfapp.model.BookItem
import com.prabhas.bookshelfapp.model.ImageLinks
import com.prabhas.bookshelfapp.model.VolumeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun BookshelfScreen(
    uiState: StateFlow<BookshelfUiState>,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val state by uiState.collectAsState()

    when (state) {
        is BookshelfUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BookshelfUiState.Success -> {
            val books = (state as BookshelfUiState.Success).books
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = contentPadding
            ) {
                items(books) { book ->
                    BookGridItem(book)
                }
            }
        }

        is BookshelfUiState.Error -> {
            Column(
                modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text((state as BookshelfUiState.Error).message)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = retryAction) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun BookGridItem(book: BookItem) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://"),
            contentDescription = book.volumeInfo.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(book.volumeInfo.title)
    }
}

@Preview(showBackground = true)
@Composable
fun BookshelfScreenPreview() {
    // Sample book for preview
    val sampleBook = BookItem(
        id = "1",
        volumeInfo = VolumeInfo(
            title = "Sample Book",
            authors = listOf("Author 1"),
            description = "This is a sample description",
            imageLinks = ImageLinks(
                thumbnail = "https://via.placeholder.com/150",
                smallThumbnail = "https://via.placeholder.com/100"
            )
        )
    )

    // Preview state with 4 sample books
    val previewState = MutableStateFlow(
        BookshelfUiState.Success(
            books = listOf(sampleBook, sampleBook, sampleBook, sampleBook)
        )
    )

    BookshelfScreen(
        uiState = previewState,
        retryAction = {}
    )
}
