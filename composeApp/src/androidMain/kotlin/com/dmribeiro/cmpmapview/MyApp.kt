package com.dmribeiro.cmpmapview

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastManager.init(this)
    }
}