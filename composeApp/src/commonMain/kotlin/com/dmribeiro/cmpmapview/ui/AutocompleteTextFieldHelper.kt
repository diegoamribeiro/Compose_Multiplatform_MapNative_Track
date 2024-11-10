package com.dmribeiro.cmpmapview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmribeiro.cmpmapview.BuildKonfig
import com.dmribeiro.cmpmapview.model.Place
import com.dmribeiro.cmpmapview.network.AutocompletePrediction
import com.dmribeiro.cmpmapview.network.RouteManager
import com.dmribeiro.cmpmapview.network.fetchAutocompleteSuggestions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AutocompleteTextFieldHelper(
    label: String,
    onPlaceSelected: (Place) -> Unit?
) {
    var placeName by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val apiKey = BuildKonfig.MAPS_API_KEY

    // Job para debounce
    var fetchJob by remember { mutableStateOf<Job?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = placeName,
            onValueChange = { newValue ->
                placeName = newValue

                // Cancelar o job anterior se existir
                fetchJob?.cancel()

                fetchJob = coroutineScope.launch {
                    // Adicionar um pequeno atraso para debounce
                    delay(300L)
                    if (newValue.isNotEmpty()) {
                        suggestions = fetchAutocompleteSuggestions(newValue, apiKey)
                    } else {
                        suggestions = emptyList()
                    }
                }
            },
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        if (suggestions.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(suggestions) { prediction ->
                    Text(
                        text = prediction.description,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    val place = RouteManager().fetchPlaceDetails(prediction.placeId, apiKey)
                                    placeName = place.name
                                    suggestions = emptyList()
                                    onPlaceSelected(place)
                                }
                            }
                            .padding(8.dp)
                    )
                    Divider()
                }
            }
        }
    }
}