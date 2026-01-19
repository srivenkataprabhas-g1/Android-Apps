package com.prabhas.quadrant_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prabhas.quadrant_task.ui.theme.Quadrant_TaskTheme
import kotlin.arrayOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Quadrant_TaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    quadrants(
                        headings= arrayOf(stringResource(R.string.h1),stringResource(R.string.h2),stringResource(R.string.h3),stringResource(R.string.h4)),
                        paragraphs= arrayOf(stringResource(R.string.p1),stringResource(R.string.p2),stringResource(R.string.p3),stringResource(R.string.p4))
                    )
                }
            }
        }
    }
}

@Composable
fun ComposableInfoCard(
    title: String,
    description: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ){
        Text(
            text = title,
            modifier = Modifier.padding(bottom = 16.dp),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = description,
            textAlign = TextAlign.Justify
        )
    }
}
@Composable
fun quadrants(headings: Array<String>, paragraphs: Array<String>, modifier: Modifier = Modifier) {
    Column(modifier=modifier.fillMaxWidth()){
    Row(Modifier.weight(1f)){
        ComposableInfoCard(headings[0],paragraphs[0],colorResource(id=R.color.q1),Modifier.weight(1f))
        ComposableInfoCard(headings[1],paragraphs[1], colorResource(id=R.color.q2),Modifier.weight(1f))
    }
    Row(Modifier.weight(1f)){
        ComposableInfoCard(headings[2],paragraphs[2],colorResource(R.color.q3),Modifier.weight(1f))
        ComposableInfoCard(headings[3],paragraphs[3],colorResource(R.color.q4),Modifier.weight(1f))
    }
    }
}

@Preview(showBackground = true,
        showSystemUi = true)
@Composable
fun GreetingPreview() {
    val headings= arrayOf(stringResource(R.string.h1),stringResource(R.string.h2),stringResource(R.string.h3),stringResource(R.string.h4))
    val paragraphs= arrayOf(stringResource(R.string.p1),stringResource(R.string.p2),stringResource(R.string.p3),stringResource(R.string.p4))
    Quadrant_TaskTheme {
        quadrants(headings,paragraphs)
    }
}