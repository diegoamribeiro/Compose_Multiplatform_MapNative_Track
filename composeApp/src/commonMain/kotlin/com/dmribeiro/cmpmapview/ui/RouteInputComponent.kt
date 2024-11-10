package com.dmribeiro.cmpmapview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmribeiro.cmpmapview.model.Place
import com.dmribeiro.cmpmapview.shared.DestinationAutocompleteTextField
import com.dmribeiro.cmpmapview.shared.OriginAutocompleteTextField

@Composable
fun RouteInputComponent(
    onOriginSelected: (Place) -> Unit,
    onDestinationSelected: (Place) -> Unit
) {
    Column {
        OriginAutocompleteTextField(onPlaceSelected = onOriginSelected)
        Spacer(modifier = Modifier.height(8.dp))
        DestinationAutocompleteTextField(onPlaceSelected = onDestinationSelected)
    }
}