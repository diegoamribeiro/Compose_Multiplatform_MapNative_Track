package com.dmribeiro.cmpmapview.network

import com.dmribeiro.cmpmapview.TransportationMode
import com.dmribeiro.cmpmapview.decodePolyline
import com.dmribeiro.cmpmapview.model.DirectionsResponse
import com.dmribeiro.cmpmapview.model.DirectionsResult
import com.dmribeiro.cmpmapview.model.Place
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

suspend fun getRoute(
    origin: Place,
    destination: Place,
    mode: TransportationMode,
    apiKey: String
): DirectionsResult {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val url = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=${origin.latitude},${origin.longitude}" +
            "&destination=${destination.latitude},${destination.longitude}" +
            "&mode=${mode.mode}" +
            "&key=$apiKey"

    println("URL: $url") // Log da URL para verificação

    val response: DirectionsResponse = client.get(url).body()

    println("Resposta: $response") // Log da resposta

    val route = response.routes.firstOrNull()
        ?: throw Exception("No routes found")

    val leg = route.legs.first()
    val polylinePoints = decodePolyline(route.overviewPolyline.points)

    return DirectionsResult(
        distanceText = leg.distance.text,
        durationText = leg.duration.text,
        polylinePoints = polylinePoints
    )
}