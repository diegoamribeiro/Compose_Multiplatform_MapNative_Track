package com.dmribeiro.cmpmapview.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsResponse(
    val routes: List<Route>
)

@Serializable
data class Route(
    val legs: List<Leg>,
    @SerialName("overview_polyline")
    val overviewPolyline: OverviewPolyline
)

@Serializable
data class Leg(
    val distance: ValueText,
    val duration: ValueText,
    val steps: List<Step>
)

@Serializable
data class ValueText(
    val text: String,
    val value: Int
)

@Serializable
data class Step(
    val distance: ValueText,
    val duration: ValueText,
    val polyline: OverviewPolyline,
    @SerialName("start_location")
    val startLocation: LatLngLiteral,
    @SerialName("end_location")
    val endLocation: LatLngLiteral,
    @SerialName("html_instructions")
    val htmlInstructions: String,
    @SerialName("travel_mode")
    val travelMode: String
)

@Serializable
data class OverviewPolyline(
    val points: String
)

@Serializable
data class LatLngLiteral(
    val lat: Double,
    val lng: Double
)