// In the Android module
// androidApp/src/main/java/com/dmribeiro/cmpmapview/AutocompleteFields.kt
package com.dmribeiro.cmpmapview

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dmribeiro.cmpmapview.model.Place
import com.dmribeiro.currencyapp.BuildConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.libraries.places.api.model.Place as GMSPlace


@Composable
actual fun OriginAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(onPlaceSelected, hint = "Origem")
}

@Composable
actual fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(onPlaceSelected, hint = "Destino")
}

@SuppressLint("ComposableNaming")
@Composable
fun AutocompleteTextField(onPlaceSelected: (Place) -> Unit, hint: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY)
        }
    }

    val placesClient = remember { Places.createClient(context) }
    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    Column {
        TextField(
            value = query,
            onValueChange = {
                query = it
                // Atualizar as previsões de autocomplete
                if (it.isNotEmpty()) {
                    coroutineScope.launch {
                        getAutocompletePredictions(it, placesClient) { results ->
                            predictions = results
                        }
                    }
                } else {
                    predictions = emptyList()
                }
            },
            label = {
                Text( modifier = Modifier.padding(vertical = 4.dp),
                text = hint
            ) },
            modifier = Modifier.fillMaxWidth()
        )

        // Exibir a lista de previsões
        predictions.forEach { prediction ->
            Text(
                text = prediction.getFullText(null).toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        coroutineScope.launch {
                            // Obter detalhes do lugar selecionado
                            getPlaceDetails(prediction.placeId, placesClient) { place ->
                                onPlaceSelected(place)
                                query = place.name
                                predictions = emptyList()
                            }
                        }
                    }
            )
        }
    }
}

suspend fun getAutocompletePredictions(
    query: String,
    placesClient: PlacesClient,
    onResult: (List<AutocompletePrediction>) -> Unit
) {
    withContext(Dispatchers.IO) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()
        val response = placesClient.findAutocompletePredictions(request).await()
        onResult(response.autocompletePredictions)
    }
}

suspend fun getPlaceDetails(
    placeId: String,
    placesClient: PlacesClient,
    onResult: (Place) -> Unit
) {
    withContext(Dispatchers.IO) {
        val placeFields = listOf(
            GMSPlace.Field.ID,
            GMSPlace.Field.NAME,
            GMSPlace.Field.ADDRESS,
            GMSPlace.Field.LAT_LNG
        )
        val request = com.google.android.libraries.places.api.net.FetchPlaceRequest.builder(placeId, placeFields).build()
        val response = placesClient.fetchPlace(request).await()
        val gmsPlace = response.place
        val place = Place(
            name = gmsPlace.name ?: "",
            address = gmsPlace.address,
            latitude = gmsPlace.latLng?.latitude ?: 0.0,
            longitude = gmsPlace.latLng?.longitude ?: 0.0
        )
        onResult(place)
    }
}

fun Context.findActivity(): AppCompatActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}