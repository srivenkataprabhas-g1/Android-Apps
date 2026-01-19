package com.prabhas.business_card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prabhas.business_card.ui.theme.BusinessCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusinessCardTheme {
                BusinessCardApp()
            }
        }
    }
}
@Composable
fun BusinessCardApp() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDDEEDC)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Profile(
            name = stringResource(R.string.name),
            role = stringResource(R.string.role)
        )

        Contact(
            phno = stringResource(R.string.phno),
            id = stringResource(R.string.id),
            email = "prabhasg03@gmail.com"
        )
    }
}
@Composable
fun Profile(name: String,role:String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(46.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Image(
            painter = painterResource(R.drawable.android_logo),
            contentDescription = null,
            modifier=Modifier.width(110.dp).height(110.dp)
        )
        Text(
            text = name,
            modifier = modifier,
            textAlign = TextAlign.Center,
            fontSize = 44.sp,
            lineHeight = 40.sp
        )
        Text(
            text = role,
            modifier = modifier,
            textAlign = TextAlign.Center,
            color = Color(51, 204, 51)
        )
    }
}

@Composable
fun Contact(phno: String,id:String,email:String, modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.padding(10.dp,40.dp,20.dp,40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ContactRow(Icons.Default.Call, phno)
        ContactRow(Icons.Default.Share, id)
        ContactRow(Icons.Default.Email, email)
    }
}
@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    BusinessCardTheme {
        BusinessCardApp()
    }
}