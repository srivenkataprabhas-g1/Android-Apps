package com.prabhas.flightsearchapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FlightCard(
    departCode: String,
    departName: String,
    arriveCode: String,
    arriveName: String,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 24.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(224, 226, 236),
            contentColor = Color.Black
        ),
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "DEPART", color = Color.Gray, fontSize = 12.sp)
                Row {
                    Text(
                        text = departCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = departName, color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "ARRIVE", color = Color.Gray, fontSize = 12.sp)
                Row {
                    Text(
                        text = arriveCode,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = arriveName, color = Color.DarkGray)
                }
            }

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Favorite",
                tint = if (isFavorite) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onFavoriteClick() }
            )
        }
    }
}