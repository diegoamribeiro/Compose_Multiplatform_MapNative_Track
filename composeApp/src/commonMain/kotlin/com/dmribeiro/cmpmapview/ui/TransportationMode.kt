package com.dmribeiro.cmpmapview.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import cmpmapnative.composeapp.generated.resources.Res
import cmpmapnative.composeapp.generated.resources.directions_bike_24dp_2854C5
import cmpmapnative.composeapp.generated.resources.directions_car_24dp_2854C5
import cmpmapnative.composeapp.generated.resources.directions_walk_24dp_2854C5
import org.jetbrains.compose.resources.painterResource

enum class TransportationMode(val mode: String) {
    DRIVING("driving"),
    WALKING("walking"),
    BICYCLING("bicycling")
}

@Composable
fun TransportationModeSelector(
    selectedMode: TransportationMode,
    onModeSelected: (TransportationMode) -> Unit
) {
    val transportationModes = listOf(
        TransportationMode.DRIVING,
        TransportationMode.WALKING,
        TransportationMode.BICYCLING
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        transportationModes.forEach { mode ->
            val isSelected = mode == selectedMode
            val icon = getTransportationModeIcon(mode)

            Image(
                painter = icon,
                contentDescription = mode.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colors.primary else Color.Transparent)
                    .clickable { onModeSelected(mode) }
                    .padding(8.dp),
                colorFilter = if (isSelected) ColorFilter.tint(Color.White) else ColorFilter.tint(Color.Gray)
            )
        }
    }
}

@Composable
fun getTransportationModeIcon(mode: TransportationMode): Painter {
    return when (mode) {
        TransportationMode.DRIVING -> painterResource(Res.drawable.directions_car_24dp_2854C5)
        TransportationMode.WALKING -> painterResource(Res.drawable.directions_walk_24dp_2854C5)
        TransportationMode.BICYCLING -> painterResource(Res.drawable.directions_bike_24dp_2854C5)
    }
}