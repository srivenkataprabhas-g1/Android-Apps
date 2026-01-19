package com.prabhas.gridapp

import android.os.Bundle
import android.widget.GridView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.prabhas.gridapp.data.DataSource
import com.prabhas.gridapp.model.Topic
import com.prabhas.gridapp.ui.theme.GridAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GridAppTheme {
                    GridApp(modifier = Modifier.padding(
                        start = 8.dp,
                        top = 8.dp,
                        end = 8.dp,
                    ))
                }
            }
        }
    }
@Composable
fun GridApp(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier.padding(8.dp),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(DataSource.topics){
            topic -> TopicCard(topic = topic)
        }
    }
}
@Composable
fun TopicCard(
    topic: Topic,
    modifier: Modifier = Modifier
){
    Card(modifier = modifier) {
        Row(modifier= Modifier) {
            Box(modifier = Modifier) {
                Image(
                    painter = painterResource(topic.imageResourceId),
                    contentDescription = stringResource(topic.stringResourceId),
                    modifier = modifier.width(68.dp).height(68.dp),
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    text = stringResource(topic.stringResourceId),
                    modifier = modifier.padding(16.dp,16.dp,16.dp,8.dp),
                    style= MaterialTheme.typography.bodyMedium
                )
                Row(
                    modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_grain),
                            contentDescription = null,
                        )
                        Text(
                            text = topic.numberOfCourses.toString(),
                            modifier = modifier.align(Alignment.CenterVertically).padding(start=8.dp),
                            style= MaterialTheme.typography.labelMedium
                        )
                    }
            }
        }
    }
}
@Preview
@Composable
fun TopicCardPreview(){
    TopicCard(Topic(R.string.architecture, 58, R.drawable.architecture))
}