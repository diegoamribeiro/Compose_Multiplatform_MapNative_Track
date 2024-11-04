package com.dmribeiro.cmpmapview


import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import androidx.compose.ui.graphics.Color
import com.dmribeiro.cmpmapview.model.LocationModel
import com.google.android.gms.maps.model.LatLng
import io.github.aakira.napier.Napier


@Composable
actual fun MapsComponent(
    routePolylinePoints: List<LocationModel>,
    latitude: Double,
    longitude: Double
) {
    val mapTypes = listOf("Normal", "Satellite", "Terrain", "Hybrid")
    val mapTypeValues = listOf(
        MapType.NORMAL,
        MapType.SATELLITE,
        MapType.TERRAIN,
        MapType.HYBRID
    )
    var selectedMapType by remember { mutableIntStateOf(0) } // Default to Normal map type

    // Map properties and UI settings
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
                myLocationButtonEnabled = true,
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

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Napier.d("***MapsComponent: $latitude: $longitude")

        val coordinates = LatLng(latitude, longitude)
        val markerState = rememberMarkerState(position = coordinates)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(coordinates, 15f)
        }

        LaunchedEffect(latitude, longitude) {
            val newPosition = LatLng(latitude, longitude)
            cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 15f)
            markerState.position = newPosition
        }

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
                    color = Color.Red,
                    width = 5f
                )
            }
            Marker(
                state = markerState,
                title = "Current Location",
                snippet = "Lat: $latitude, Lng: $longitude"
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
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

//@Composable
//actual fun MapsComponent(
//    routePolylinePoints: List<com.dmribeiro.cmpmapview.model.LocationModel>,
//    latitude: Double,
//    longitude: Double
//) {
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 12f)
//    }
//    GoogleMap(
//        cameraPositionState = cameraPositionState
//    ) {
//        if (routePolylinePoints.isNotEmpty()) {
//            Polyline(
//                points = routePolylinePoints.map { latLng ->
//                    LatLng(latLng.latitude, latLng.longitude)
//                },
//                color = Color.Blue,
//                width = 5f
//            )
//        }
//    }
//}

//@Composable
//fun MapTypeSelector(items: List<String>, selectedIndex: Int, onItemSelected: (Int) -> Unit) {
//    ScrollableTabRow(
//        selectedTabIndex = selectedIndex,
//        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.9f),
//        contentColor = MaterialTheme.colors.primary,
//        edgePadding = 0.dp,
//        modifier = Modifier
//            .padding(horizontal = 16.dp)
//            .fillMaxWidth()
//    ) {
//        items.forEachIndexed { index, item ->
//            Tab(
//                selected = selectedIndex == index,
//                onClick = { onItemSelected(index) },
//                text = { Text(item) },
//                selectedContentColor = MaterialTheme.colors.primary,
//                unselectedContentColor = Color.Gray,
//            )
//        }
//    }
//}