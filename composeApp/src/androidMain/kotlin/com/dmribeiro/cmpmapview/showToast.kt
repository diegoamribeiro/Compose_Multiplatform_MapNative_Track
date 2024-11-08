package com.dmribeiro.cmpmapview

import android.annotation.SuppressLint
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
actual fun showToast(message: String) {
    ToastManager.showToast(message)
}

@SuppressLint("StaticFieldLeak")
object ToastManager {
    private var context: android.content.Context? = null

    fun init(context: android.content.Context) {
        this.context = context.applicationContext
    }

    fun showToast(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }
}