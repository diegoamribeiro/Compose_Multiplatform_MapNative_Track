// In the iOS module
package com.dmribeiro.cmpmapview

import androidx.compose.runtime.Composable
import com.dmribeiro.cmpmapview.model.Place

@Composable
actual fun OriginAutocompleteTextField(
    onPlaceSelected: (Place) -> Unit
) {
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