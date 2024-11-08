package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RouteInfo(
    distanceText: String,
    durationText: String
) {
    Column {
        Text(text = "Distância: $distanceText", fontWeight = FontWeight.Bold)
        Text(text = "Tempo estimado: $durationText", fontWeight = FontWeight.Bold)
    }
}