package com.dmribeiro.cmpmapview.model

import com.dmribeiro.cmpmapview.network.httpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceResult,
    val status: String
)

@Serializable
data class PlaceResult(
    val geometry: Geometry,
    val name: String,
    @SerialName("formatted_address")
    val formattedAddress: String? = null
)

@Serializable
data class Geometry(
    val location: Location
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)