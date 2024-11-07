package com.dmribeiro.cmpmapview.services

import com.dmribeiro.cmpmapview.model.LocationModel

interface LocationService {
    suspend fun getCurrentLocation() : LocationModel?
}