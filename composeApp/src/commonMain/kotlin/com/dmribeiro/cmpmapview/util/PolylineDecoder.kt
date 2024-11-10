package com.dmribeiro.cmpmapview.util

import com.dmribeiro.cmpmapview.model.LocationModel

class PolylineDecoder {

    fun decode(encoded: String): List<LocationModel> {
        val polylinePoints = mutableListOf<LocationModel>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val latitude = lat / 1E5
            val longitude = lng / 1E5
            polylinePoints.add(LocationModel(latitude, longitude))
        }
        return polylinePoints
    }

}