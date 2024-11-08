package com.dmribeiro.cmpmapview

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication


@OptIn(ExperimentalForeignApi::class)
fun hideKeyboard() {
    UIApplication.sharedApplication.sendAction(
        NSSelectorFromString("resignFirstResponder"),
        to = null,
        from = null,
        forEvent = null
    )
}