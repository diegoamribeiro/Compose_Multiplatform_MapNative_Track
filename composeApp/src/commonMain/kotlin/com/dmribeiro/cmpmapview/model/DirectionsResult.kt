package com.dmribeiro.cmpmapview.model


data class DirectionsResult(
    val distanceText: String,
    val durationText: String,
    val polylinePoints: List<LocationModel>
)