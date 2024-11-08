package com.dmribeiro.cmpmapview

import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AutocompletePrediction(
    val description: String,
    @SerialName("place_id")
    val placeId: String
)

@Serializable
data class AutocompleteResponse(
    val predictions: List<AutocompletePrediction>,
    val status: String
)

suspend fun fetchAutocompleteSuggestions(input: String, apiKey: String): List<AutocompletePrediction> {
    val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json"

    val response: AutocompleteResponse = httpClient.get(url) {
        parameter("input", input)
        parameter("key", apiKey)
        parameter("types", "geocode")
        parameter("language", "pt-BR")
    }.body()

    return if (response.status == "OK") {
        response.predictions
    } else {
        emptyList()
    }
}