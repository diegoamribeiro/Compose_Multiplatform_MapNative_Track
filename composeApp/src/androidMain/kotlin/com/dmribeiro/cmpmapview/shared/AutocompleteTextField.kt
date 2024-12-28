package com.dmribeiro.cmpmapview.shared

import androidx.compose.runtime.Composable
import cmpmapnative.composeapp.generated.resources.Res
import cmpmapnative.composeapp.generated.resources.text_origin
import com.dmribeiro.cmpmapview.ui.AutocompleteTextFieldHelper
import com.dmribeiro.cmpmapview.model.Place
import org.jetbrains.compose.resources.stringResource


@Composable
actual fun OriginAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextFieldHelper(
        label = stringResource(resource = Res.string.text_origin),
        onPlaceSelected
    )
}

@Composable
actual fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextFieldHelper(label = "Destination", onPlaceSelected)
}