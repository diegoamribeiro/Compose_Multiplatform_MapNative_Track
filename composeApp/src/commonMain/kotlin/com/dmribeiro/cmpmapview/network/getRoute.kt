package com.dmribeiro.cmpmapview.network

import com.dmribeiro.cmpmapview.model.DirectionsResponse
import com.dmribeiro.cmpmapview.TransportationMode
import com.dmribeiro.cmpmapview.decodePolyline
import com.dmribeiro.cmpmapview.httpClient
import com.dmribeiro.cmpmapview.model.DirectionsResult
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.model.Place
import com.seu.pacote.BuildKonfig
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

import kotlin.math.pow
import kotlin.math.round

suspend fun getRoute(
    origin: Place,
    destination: Place,
    mode: TransportationMode,
    useMockData: Boolean = false
): DirectionsResult {

    val apiKey = BuildKonfig.MAPS_API_KEY


    if (useMockData) {
        // Return mock data as shown above
        val mockPolylinePoints = listOf(
            LocationModel(latitude = -12.9575794, longitude = -38.4543532),
            LocationModel(latitude = -12.961, longitude = -38.455),
            LocationModel(latitude = -12.970, longitude = -38.460),
            LocationModel(latitude = -12.980, longitude = -38.462),
            LocationModel(latitude = -13.0044708, longitude = -38.4601264)
        )

        val distanceText = "5.0 km"
        val durationText = "15 min"

        return DirectionsResult(
            distanceText = distanceText,
            durationText = durationText,
            polylinePoints = mockPolylinePoints
        )
    } else {
        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=${mode.mode}" +
                "&key=$apiKey"

        println("***URL da requisição: $url")

        try {
            val response = httpClient.get(url)
            val responseBody = response.bodyAsText()
            println("***Resposta da API: $responseBody")

            val directionsResponse = Json {
                ignoreUnknownKeys = true
            }.decodeFromString<DirectionsResponse>(responseBody)

            if (directionsResponse.routes.isNotEmpty()) {
                val route = directionsResponse.routes.first()
                val overviewPolyline = route.overviewPolyline.points
                val decodedPolyline = decodePolyline(overviewPolyline)

                val totalDistance = route.legs.sumOf { it.distance.value } // em metros
                val totalDuration = route.legs.sumOf { it.duration.value } // em segundos

                val distanceKm = (totalDistance / 1000.0).roundToDecimalPlace(2)
                val distanceText = "$distanceKm km"
                val durationMinutes = (totalDuration / 60.0).roundToDecimalPlace(0).toInt()
                val durationText = "$durationMinutes min"

                return DirectionsResult(
                    distanceText = distanceText,
                    durationText = durationText,
                    polylinePoints = decodedPolyline
                )
            } else {
                throw Exception("Nenhuma rota encontrada")
            }
        } catch (e: Exception) {
            println("Erro ao buscar direções: ${e.message}")
            throw e
        }
    }
}

private fun Double.roundToDecimalPlace(decimalPlaces: Int): Double {
    val factor = 10.0.pow(decimalPlaces)
    return round(this * factor) / factor
}