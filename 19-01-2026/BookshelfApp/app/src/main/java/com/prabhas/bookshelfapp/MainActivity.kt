package com.prabhas.bookshelfapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.prabhas.bookshelfapp.ui.BookshelfApp
import com.prabhas.bookshelfapp.ui.theme.BookshelfAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as BookshelfApplication).container

        setContent {
            BookshelfAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BookshelfApp(container = container)
                }
            }
        }
    }
}
