package com.dmribeiro.cmpmapview.services

import com.dmribeiro.cmpmapview.model.LocationData

interface LocationService {
    suspend fun getCurrentLocation() : LocationData?
}