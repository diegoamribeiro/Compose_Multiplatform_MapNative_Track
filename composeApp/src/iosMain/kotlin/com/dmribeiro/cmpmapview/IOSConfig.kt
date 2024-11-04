package com.dmribeiro.cmpmapview// iosMain
import platform.Foundation.NSBundle

object IOSConfig : Config {
    override val mapsApiKey: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("MAPS_API_KEY") as? String ?: ""
}