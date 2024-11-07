package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dmribeiro.cmpmapview.model.DirectionsResult
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.model.Place
import com.dmribeiro.cmpmapview.network.getRoute
import kotlinx.coroutines.launch

@Composable
fun MapWithRouteComponent(
    initialLocation: LocationModel,
    useMockData: Boolean = true
) {
    var originPlace by remember { mutableStateOf<Place?>(null) }
    var destinationPlace by remember { mutableStateOf<Place?>(null) }
    var transportationMode by remember { mutableStateOf(TransportationMode.DRIVING) }
    var routeInfo by remember { mutableStateOf<DirectionsResult?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(initialLocation) {
        originPlace = Place(
            name = "Localização Atual",
            address = null,
            latitude = initialLocation.latitude,
            longitude = initialLocation.longitude
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        RouteInputComponent(
            onOriginSelected = { place -> originPlace = place },
            onDestinationSelected = { place -> destinationPlace = place }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TransportationModeSelector(
            selectedMode = transportationMode,
            onModeSelected = { mode -> transportationMode = mode }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            coroutineScope.launch {
                try {
                    if (useMockData) {
                        // Definir locais de origem e destino mockados
                        if (originPlace == null) {
                            originPlace = Place(
                                name = "Localização Mock de Origem",
                                address = "Endereço Mock de Origem",
                                latitude = -12.9575794,
                                longitude = -38.4543532
                            )
                        }
                        if (destinationPlace == null) {
                            destinationPlace = Place(
                                name = "Localização Mock de Destino",
                                address = "Endereço Mock de Destino",
                                latitude = -13.0044708,
                                longitude = -38.4601264
                            )
                        }
                    } else {
                        // Se não estiver usando dados mockados, verificar se os lugares não são nulos
                        if (originPlace == null || destinationPlace == null) {
                            println("Origem e destino devem ser selecionados.")
                            return@launch
                        }
                    }

                    val result = getRoute(
                        origin = originPlace!!,
                        destination = destinationPlace!!,
                        mode = transportationMode,
                        useMockData = useMockData
                    )
                    routeInfo = result
                } catch (e: Exception) {
                    println("Erro ao obter a rota: ${e.message}")
                }
            }
        }) {
            Text("Obter Rota")
        }
        routeInfo?.let {
            RouteInfo(
                distanceText = it.distanceText,
                durationText = it.durationText
            )
        }

        MapsComponent(
            routePolylinePoints = routeInfo?.polylinePoints ?: emptyList(),
            latitude = originPlace?.latitude ?: 0.0,
            longitude = originPlace?.longitude ?: 0.0
        )
    }
}