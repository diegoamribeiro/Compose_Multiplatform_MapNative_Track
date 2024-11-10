// In the iOS module
package com.dmribeiro.cmpmapview.shared

import androidx.compose.runtime.Composable
import com.dmribeiro.cmpmapview.ui.AutocompleteTextFieldHelper
import com.dmribeiro.cmpmapview.model.Place

@Composable
actual fun OriginAutocompleteTextField(
    onPlaceSelected: (Place) -> Unit
) {
    AutocompleteTextFieldHelper(
        label = "Origem",
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
actual fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextFieldHelper(
        label = "Destino",
        onPlaceSelected = onPlaceSelected
    )
}