// DirectionsResponse.kt
package com.dmribeiro.cmpmapview.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DirectionsResponse(val routes: List<Route>)

@Serializable
data class Route(
    val legs: List<Leg>,
    @SerialName("overview_polyline") val overviewPolyline: OverviewPolyline
)

@Serializable
data class Leg(val distance: TextValue, val duration: TextValue)

@Serializable
data class TextValue(val text: String, val value: Int)

@Serializable
data class OverviewPolyline(val points: String)