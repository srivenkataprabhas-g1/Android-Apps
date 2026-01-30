package com.example.juicetracker.ui.homescreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * A composable which uses AndroidView to render AdView.
 * Showing a banner test ad
 */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                // Use test ad unit ID
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
            }
        },
        update = {
            it.loadAd(AdRequest.Builder().build())
        }
    )
}