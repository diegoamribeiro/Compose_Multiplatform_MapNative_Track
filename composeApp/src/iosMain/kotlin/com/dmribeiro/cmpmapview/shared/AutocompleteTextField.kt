// In the iOS module
package com.dmribeiro.cmpmapview.shared

import androidx.compose.runtime.Composable
import cmpmapnative.composeapp.generated.resources.Res
import cmpmapnative.composeapp.generated.resources.text_origin
import com.dmribeiro.cmpmapview.ui.AutocompleteTextFieldHelper
import com.dmribeiro.cmpmapview.model.Place
import kotlinx.coroutines.coroutineScope
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

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
        label = "Destination",
        onPlaceSelected = onPlaceSelected
    )
}