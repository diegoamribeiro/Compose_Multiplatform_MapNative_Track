package com.dmribeiro.cmpmapview

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.dmribeiro.cmpmapview.model.LocationModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(latitude: Double, longitude: Double) {
    MaterialTheme {
        MapsComponent(
            routePolylinePoints = listOf(LocationModel(latitude, longitude)),
            latitude = latitude,
            longitude = longitude
        )
    }
}