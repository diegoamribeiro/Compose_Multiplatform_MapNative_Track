package com.dmribeiro.cmpmapview.shared


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cmpmapnative.composeapp.generated.resources.Res
import cmpmapnative.composeapp.generated.resources.gps_fixed_24dp_2854C5
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.services.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
actual fun MapsComponent(
    routePolylinePoints: List<LocationModel>,
    latitude: Double,
    longitude: Double,
    locationService: LocationService
) {
    val mapTypes = listOf("Normal", "Satellite", "Terrain", "Hybrid")
    val mapTypeValues = listOf(
        MapType.NORMAL,
        MapType.SATELLITE,
        MapType.TERRAIN,
        MapType.HYBRID
    )
    var selectedMapType by remember { mutableIntStateOf(0) }

    // Mantenha as propriedades do mapa e as configurações de UI
    val mapProperties = remember {
        mutableStateOf(
            MapProperties(
                mapType = mapTypeValues[selectedMapType],
                isTrafficEnabled = false,
                isIndoorEnabled = true,
                isBuildingEnabled = true,
                isMyLocationEnabled = true,
            )
        )
    }
    val uiSettings = remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                myLocationButtonEnabled = false, // Desabilita o botão padrão
                indoorLevelPickerEnabled = true,
                mapToolbarEnabled = true,
                zoomControlsEnabled = true,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = true,
                rotationGesturesEnabled = true,
            )
        )
    }

    // Defina um coroutineScope
    val coroutineScope = rememberCoroutineScope()

    // Estado para CameraPositionState
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 15f)
    }

    // Estado para MarkerState
    val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

    // Atualizar a posição da câmera e do marcador quando a localização muda
    LaunchedEffect(latitude, longitude) {
        val newPosition = LatLng(latitude, longitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 15f)
        markerState.position = newPosition
    }

    // Ajustar o zoom dinamicamente quando a polyline muda
    LaunchedEffect(routePolylinePoints) {
        if (routePolylinePoints.isNotEmpty()) {
            val routePoints = routePolylinePoints.map { LatLng(it.latitude, it.longitude) }
            val bounds = calculateBounds(routePoints)
            // Atualizar a câmera para enquadrar os limites da polyline
            cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100)) // 100 é o padding em pixels
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Napier.d("***MapsComponent: $latitude: $longitude")

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties.value.copy(mapType = mapTypeValues[selectedMapType]),
            uiSettings = uiSettings.value,
        ) {
            if (routePolylinePoints.isNotEmpty()) {
                Polyline(
                    points = routePolylinePoints.map { location ->
                        LatLng(location.latitude, location.longitude)
                    },
                    color = Color.Magenta,
                    width = 5f
                )
            }
            Marker(
                state = markerState,
                title = "Current Location",
                snippet = "Lat: $latitude, Lng: $longitude"
            )
        }

        // Seletor de Tipo de Mapa no Top Center
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MapTypeSelector(
                items = mapTypes,
                selectedIndex = selectedMapType,
                onItemSelected = { index ->
                    selectedMapType = index
                    mapProperties.value = mapProperties.value.copy(
                        mapType = mapTypeValues[selectedMapType]
                    )
                }
            )
        }

        IconButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        val location = locationService.getCurrentLocation()
                        if (location != null) {
                            val newLatLng = LatLng(location.latitude, location.longitude)
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    newLatLng,
                                    15f
                                )
                            )
                            markerState.position = newLatLng
                            println("Atualizando a localização atual: $location")
                        }
                    } catch (e: Exception) {
                        println("Erro ao obter a localização atual: ${e.message}")
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart) // Posiciona no canto inferior esquerdo
                .padding(start = 16.dp, bottom = 16.dp) // Ajusta o padding conforme necessário
        ) {
            Icon(
                painter = painterResource(Res.drawable.gps_fixed_24dp_2854C5),
                contentDescription = "My Location",
                tint = Color.Blue
            )
        }
    }
}

@Composable
fun MapTypeSelector(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
) {
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.9f),
        contentColor = MaterialTheme.colors.primary,
        edgePadding = 0.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        items.forEachIndexed { index, item ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onItemSelected(index) },
                text = { Text(item) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = Color.Gray,
            )
        }
    }
}

/**
 * Função para calcular os limites que englobam todos os pontos da polyline.
 */
fun calculateBounds(routePoints: List<LatLng>): LatLngBounds {
    val builder = LatLngBounds.builder()
    routePoints.forEach { point ->
        builder.include(point)
    }
    return builder.build()
}