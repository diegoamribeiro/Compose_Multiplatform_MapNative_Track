package com.dmribeiro.cmpmapview

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.services.LocationService
import kotlinx.coroutines.launch

@Composable
fun AppWithMap(locationService: LocationService) {

    val coroutineScope = rememberCoroutineScope()
    val locationState = remember { mutableStateOf<LocationModel?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val location = locationService.getCurrentLocation()

                if (location != null) {
                    locationState.value = location
                } else {
                    errorState.value = "Não foi possível obter a localização atual."
                }
            } catch (e: Exception) {
                errorState.value = e.message
            }
        }
    }

    when {
        locationState.value != null -> {
            val location = locationState.value!!
            MapWithRouteComponent(
                initialLocation = location,
                useMockData = false,
                locationService = locationService
            )

        }
        errorState.value != null -> {
            Text("Erro: ${errorState.value}")
        }
        else -> {
            Text("Obtendo localização...")
        }
    }
}