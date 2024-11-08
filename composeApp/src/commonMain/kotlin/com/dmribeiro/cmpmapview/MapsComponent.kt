package com.dmribeiro.cmpmapview

import androidx.compose.runtime.Composable
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.services.LocationService

@Composable
expect fun MapsComponent(
    routePolylinePoints: List<LocationModel>,
    latitude: Double,
    longitude: Double,
    locationService: LocationService
)