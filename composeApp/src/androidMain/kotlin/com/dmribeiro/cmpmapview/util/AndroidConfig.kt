package com.dmribeiro.cmpmapview.util

import com.dmribeiro.cmpmapview.Config
import com.dmribeiro.currencyapp.BuildConfig

object AndroidConfig : Config {
    override val mapsApiKey: String
        get() = BuildConfig.MAPS_API_KEY
}