package com.dmribeiro.cmpmapview

import com.dmribeiro.currencyapp.BuildConfig

object AndroidConfig : Config {
    override val mapsApiKey: String
        get() = BuildConfig.MAPS_API_KEY
}