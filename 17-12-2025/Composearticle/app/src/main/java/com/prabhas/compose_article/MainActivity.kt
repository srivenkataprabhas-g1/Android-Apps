package com.prabhas.compose_article

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prabhas.compose_article.ui.theme.ComposearticleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposearticleTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    Greeting(
                        name = arrayOf(stringResource(R.string.first),stringResource(R.string.second),stringResource(R.string.third)),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: Array<String>, modifier: Modifier = Modifier) {
    Column(modifier=modifier) {
        Spacer(modifier=modifier.padding(20.dp))
        Image(painter = painterResource(R.drawable.banner)
            , contentDescription = null
            , modifier=Modifier.fillMaxWidth()
            , Alignment.Center
            ,contentScale = androidx.compose.ui.layout.ContentScale.FillWidth
        )
        Text(
            text = name[0],
            modifier = modifier.padding(16.dp,16.dp,16.dp,16.dp),
            fontSize = 24.sp
        )
        Text(
            text = name[1],
            modifier = modifier.padding(16.dp,16.dp,16.dp,16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Justify
        )
        Text(
            text = name[2],
            modifier = modifier.padding(16.dp,16.dp,16.dp,16.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Justify
        )
    }
}

@Preview(showBackground = true,
    showSystemUi = true,
    name = "Dark Mode")
@Composable
fun GreetingPreview() {
    ComposearticleTheme {
        val first=stringResource(R.string.first)
        val second=stringResource(R.string.second)
        val third=stringResource(R.string.third)
        val name=arrayOf(first,second,third)
        Greeting(name)
    }
}