// In the iOS module
package com.dmribeiro.cmpmapview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cocoapods.GooglePlaces.GMSAutocompleteViewController
import cocoapods.GooglePlaces.GMSAutocompleteViewControllerDelegateProtocol
import cocoapods.GooglePlaces.GMSPlace
import com.dmribeiro.cmpmapview.model.Place
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.darwin.NSObject
import kotlin.native.concurrent.freeze

@Composable
actual fun OriginAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(
        label = "Origem",
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
actual fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(
        label = "Destino",
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
fun AutocompleteTextField(
    label: String,
    onPlaceSelected: (Place) -> Unit
) {
    var placeName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = placeName,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                println("Campo de $label clicado")
                coroutineScope.launch {
                    // Abre o BottomSheet ao clicar no campo
                    showAutocompleteBottomSheet { selectedPlace ->
                        placeName = selectedPlace.name
                        onPlaceSelected(selectedPlace)
                    }
                }
            }
    )
}