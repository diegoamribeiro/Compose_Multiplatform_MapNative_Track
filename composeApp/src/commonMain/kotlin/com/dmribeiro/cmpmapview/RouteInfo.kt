package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun RouteInfo(
    distanceText: String,
    durationText: String
) {
    Column {
        Text(text = "Distância: $distanceText")
        Text(text = "Tempo estimado: $durationText")
    }
}