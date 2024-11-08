package com.dmribeiro.cmpmapview

import com.dmribeiro.cmpmapview.model.Place
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

suspend fun fetchPlaceDetails(placeId: String, apiKey: String): Place {
    val url = "https://maps.googleapis.com/maps/api/place/details/json"

    val response: PlaceDetailsResponse = httpClient.get(url) {
        parameter("place_id", placeId)
        parameter("key", apiKey)
        parameter("language", "pt-BR")
        // Adicione outros parâmetros conforme necessário
    }.body()

    if (response.status == "OK") {
        val location = response.result.geometry.location
        return Place(
            name = response.result.name,
            address = response.result.formattedAddress,
            latitude = location.lat,
            longitude = location.lng
        )
    } else {
        throw Exception("Failed to fetch place details: ${response.status}")
    }
}