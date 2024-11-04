package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.Row
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

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
    Row {
        TransportationMode.entries.forEach { mode ->
            RadioButton(
                selected = mode == selectedMode,
                onClick = { onModeSelected(mode) }
            )
            Text(text = mode.name)
        }
    }
}