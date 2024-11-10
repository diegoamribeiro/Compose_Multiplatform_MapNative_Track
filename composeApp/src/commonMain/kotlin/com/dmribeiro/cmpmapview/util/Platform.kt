package com.dmribeiro.cmpmapview.util

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform