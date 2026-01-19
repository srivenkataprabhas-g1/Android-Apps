package com.prabhas.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArtSpaceApp()
        }
    }
}

@Composable
fun ArtSpaceApp() {

    var currentIndex by remember { mutableIntStateOf(0) }

    val artworks = listOf(
        Artwork(R.drawable.art1, "Starry Night", "Vincent van Gogh", "1889"),
        Artwork(R.drawable.art2, "Mona Lisa", "Leonardo da Vinci", "1503"),
        Artwork(R.drawable.art3, "The Scream", "Edvard Munch", "1893"),
        Artwork(R.drawable.art4, "The Life", "Seenu","2016"),
        Artwork(R.drawable.art5, "Karuninchina Akasam", "Gyaneshwar Kale (Ghyanbhai)", "2008"),
        Artwork(R.drawable.art7, "Lady with Fruit", "Raja Ravi Varma", "Late 19th Century"),
        Artwork(R.drawable.art10, "The Great Wave off Kanagawa", "Hokusai", "1831"),
        Artwork(R.drawable.art9, "Brahmanandam Holding Phone", "John Abraham", "2005"),
        Artwork(R.drawable.art8, "Shakuntala Patra Lekhan", "Raja Ravi Varma", "1876"),
        Artwork(R.drawable.art6, "Jaya Surya Holding Guitar", "Gyaneshwar Kale (Ghyanbhai)", "2008"),
        )

    val artwork = artworks[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        // Artwork Image
        Image(
            painter = painterResource(id = artwork.imageRes),
            contentDescription = artwork.title,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Artwork Details
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = artwork.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${artwork.artist} (${artwork.year})",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                currentIndex =
                    if (currentIndex > 0) currentIndex - 1 else artworks.lastIndex
            }) {
                Text("Previous")
            }

            Button(onClick = {
                currentIndex =
                    if (currentIndex < artworks.lastIndex) currentIndex + 1 else 0
            }) {
                Text("Next")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ArtSpaceAppPreview() {
    ArtSpaceApp()
}

data class Artwork(
    val imageRes: Int,
    val title: String,
    val artist: String,
    val year: String
)
