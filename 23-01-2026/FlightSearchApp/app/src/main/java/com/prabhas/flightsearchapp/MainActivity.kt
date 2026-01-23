package com.prabhas.flightsearchapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhas.flightsearchapp.ui.AirportViewModel
import com.prabhas.flightsearchapp.ui.screens.FlightSearchScreen
import com.prabhas.flightsearchapp.ui.theme.FlightSearchAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlightSearchAppTheme {
                FlightSearchApp()
            }
        }
    }
}

@Composable
fun FlightSearchApp() {
    val airportViewModel: AirportViewModel = viewModel(factory = AirportViewModel.Factory)

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        FlightSearchScreen(
            airportViewModel = airportViewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}