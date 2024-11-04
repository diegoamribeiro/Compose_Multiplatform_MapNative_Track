package com.dmribeiro.cmpmapview

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform