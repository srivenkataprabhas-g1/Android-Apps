package com.prabhas.flightsearchapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.material3.ExperimentalMaterial3Api
import com.prabhas.flightsearchapp.ui.components.FlightSearchBar

@PreviewScreenSizes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightHomeScreen() {
    var searchQuery by remember { mutableStateOf("") }

    Column {
        FlightAppBar()
        FlightSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
    }
}