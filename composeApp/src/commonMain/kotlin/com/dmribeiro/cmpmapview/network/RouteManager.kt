package com.dmribeiro.cmpmapview.network

import com.dmribeiro.cmpmapview.BuildKonfig
import com.dmribeiro.cmpmapview.ui.TransportationMode
import com.dmribeiro.cmpmapview.model.DirectionsResult
import com.dmribeiro.cmpmapview.model.Place
import com.dmribeiro.cmpmapview.model.PlaceDetailsResponse
import com.dmribeiro.cmpmapview.util.PolylineDecoder
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


class RouteManager {

    suspend fun getRoute(
        origin: Place,
        destination: Place,
        mode: TransportationMode
    ): DirectionsResult {
        val apiKey = BuildKonfig.MAPS_API_KEY
        val url = "https://maps.googleapis.com/maps/api/directions/json"

        val response: HttpResponse = httpClient.get(url) {
            parameter("origin", "${origin.latitude},${origin.longitude}")
            parameter("destination", "${destination.latitude},${destination.longitude}")
            parameter("mode", mode.mode)
            parameter("key", apiKey)
            parameter("language", "pt-BR")
        }

        val responseBody = response.bodyAsText()
        val responseJson = Json.parseToJsonElement(responseBody).jsonObject

        val routes = responseJson["routes"]?.jsonArray ?: JsonArray(emptyList())
        if (routes.isEmpty()) {
            throw Exception("Nenhuma rota encontrada")
        }

        val route = routes[0].jsonObject
        val legs = route["legs"]?.jsonArray
        val leg = legs?.get(0)?.jsonObject

        val distanceText = leg?.get("distance")?.jsonObject?.get("text")?.jsonPrimitive?.content ?: ""

        // Obter a duração em segundos
        val durationInSeconds = leg?.get("duration")?.jsonObject?.get("value")?.jsonPrimitive?.int ?: 0

        // Converter segundos para minutos, arredondando para cima
        val durationInMinutes = (durationInSeconds + 59) / 60 // Adiciona 59 para arredondar para cima

        // Formatar a duração
        val durationText = formatDuration(durationInMinutes)

        // Obter os pontos da polyline
        val overviewPolyline = route["overview_polyline"]?.jsonObject
        val points = overviewPolyline?.get("points")?.jsonPrimitive?.content

        val polylinePoints = PolylineDecoder().decode(points ?: "")

        return DirectionsResult(
            distanceText = distanceText,
            durationText = durationText,
            polylinePoints = polylinePoints
        )
    }

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

    private fun formatDuration(durationInMinutes: Int): String {
        var totalMinutes = durationInMinutes
        var days = 0
        var hours = 0
        var minutes = 0

        if (totalMinutes >= 1440) { // 24 horas
            days = totalMinutes / 1440
            totalMinutes %= 1440
        }

        if (totalMinutes >= 60) {
            hours = totalMinutes / 60
            minutes = totalMinutes % 60
        } else {
            minutes = totalMinutes
        }

        // Ajuste para horas e minutos
        if (hours == 24) {
            days += 1
            hours = 0
        }

        return when {
            days > 0 && hours > 0 && minutes > 0 -> {
                if (days == 1) "1 dia, $hours h e $minutes min"
                else "$days dias, $hours h e $minutes min"
            }
            days > 0 && hours > 0 -> {
                if (days == 1) "1 dia e $hours h"
                else "$days dias e $hours h"
            }
            days > 0 && minutes > 0 -> {
                if (days == 1) "1 dia e $minutes min"
                else "$days dias e $minutes min"
            }
            hours > 0 && minutes > 0 -> {
                "${hours}h ${minutes}min"
            }
            hours > 0 -> {
                "${hours}h"
            }
            else -> {
                "$minutes min"
            }
        }
    }
}