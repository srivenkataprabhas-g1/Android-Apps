package com.prabhas.buttontexttoasttask

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prabhas.buttontexttoasttask.ui.theme.ButtonTextToastTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ButtonTextToastTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Task1(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Task1(modifier: Modifier = Modifier) {
    var submitTimes by remember { mutableStateOf(0) }
    var submitText by remember { mutableStateOf("") }
    var context = LocalContext.current
    Spacer(modifier=Modifier.padding(10.0.dp))

    Column(modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        TextField(
            value = submitText,
            onValueChange = { text -> submitText = text },
            label = { Text("Enter text to submit") },
        )
        Button(
            onClick = {
                submitTimes++
                if (submitTimes < 5) {
                    Toast.makeText(
                        context,
                        "Submitted $submitTimes times with text: $submitText",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "More than equal to 5 times submitted.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = modifier,
        ) {
            Text("Submit")
        }
    }
}
@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GreetingPreview() {
    ButtonTextToastTaskTheme {
        Task1()
    }
}