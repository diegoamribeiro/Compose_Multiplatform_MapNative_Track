package com.dmribeiro.cmpmapview.shared

import androidx.compose.runtime.Composable
import com.dmribeiro.cmpmapview.model.Place

@Composable
expect fun OriginAutocompleteTextField(onPlaceSelected: (Place) -> Unit)

@Composable
expect fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit)