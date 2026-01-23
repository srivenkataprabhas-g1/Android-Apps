package com.prabhas.flightsearchapp.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.prabhas.flightsearchapp.R

@PreviewScreenSizes
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightAppBar(){
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(32, 95, 166),
            titleContentColor = Color(255,255,255),
        ),
        title = {
            Text(stringResource(R.string.app_name))
        }
    )
}