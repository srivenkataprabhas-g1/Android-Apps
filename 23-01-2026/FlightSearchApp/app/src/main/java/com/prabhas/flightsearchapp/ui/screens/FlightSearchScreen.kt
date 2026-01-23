package com.prabhas.flightsearchapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prabhas.flightsearchapp.data.Airport
import com.prabhas.flightsearchapp.data.Favorite
import com.prabhas.flightsearchapp.ui.AirportViewModel
import com.prabhas.flightsearchapp.ui.components.FlightCard
import com.prabhas.flightsearchapp.ui.components.FlightSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightSearchScreen(
    airportViewModel: AirportViewModel = viewModel(factory = AirportViewModel.Factory),
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAirport by remember { mutableStateOf<Airport?>(null) }

    val airportList by airportViewModel.getAirport(searchQuery).collectAsState(initial = emptyList())
    val allAirports by airportViewModel.getAirport("").collectAsState(initial = emptyList())
    val favorites by airportViewModel.getAllFavorites().collectAsState(initial = emptyList())

    Column(modifier = modifier.fillMaxSize()) {
        FlightAppBar()

        FlightSearchBar(
            query = searchQuery,
            onQueryChange = { 
                searchQuery = it 
                if (it.isEmpty()) selectedAirport = null
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedAirport == null) {
            if (searchQuery.isEmpty()) {
                // Show Favorites if query is empty
                Text(
                    text = "Favorite Routes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(favorites) { fav ->
                        val departAirport = allAirports.find { it.iatacode == fav.departureCode }
                        val arriveAirport = allAirports.find { it.iatacode == fav.destinationCode }
                        
                        FlightCard(
                            departCode = fav.departureCode,
                            departName = departAirport?.name ?: "",
                            arriveCode = fav.destinationCode,
                            arriveName = arriveAirport?.name ?: "",
                            isFavorite = true,
                            onFavoriteClick = {
                                airportViewModel.deleteFavorite(fav)
                            }
                        )
                    }
                }
            } else {
                // Show Search Results
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(airportList) { airport ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAirport = airport }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row {
                                Text(
                                    text = airport.iatacode,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(60.dp)
                                )
                                Text(text = airport.name)
                            }
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        } else {
            selectedAirport?.let { departAirport ->
                Text(
                    text = "Flights from ${departAirport.iatacode}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(allAirports.filter { it.iatacode != departAirport.iatacode }) { arriveAirport ->
                        val isFav = favorites.any {
                            it.departureCode == departAirport.iatacode &&
                                    it.destinationCode == arriveAirport.iatacode
                        }

                        FlightCard(
                            departCode = departAirport.iatacode,
                            departName = departAirport.name,
                            arriveCode = arriveAirport.iatacode,
                            arriveName = arriveAirport.name,
                            isFavorite = isFav,
                            onFavoriteClick = {
                                if (isFav) {
                                    airportViewModel.deleteFavorite(
                                        Favorite(
                                            departureCode = departAirport.iatacode,
                                            destinationCode = arriveAirport.iatacode
                                        )
                                    )
                                } else {
                                    airportViewModel.insertFavorite(
                                        departAirport.iatacode,
                                        arriveAirport.iatacode
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}