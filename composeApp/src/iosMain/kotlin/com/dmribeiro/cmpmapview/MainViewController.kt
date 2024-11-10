package com.dmribeiro.cmpmapview

import androidx.compose.ui.window.ComposeUIViewController
import com.dmribeiro.cmpmapview.services.IOSLocationService
import com.dmribeiro.cmpmapview.ui.AppWithMap
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        AppWithMap(locationService = IOSLocationService())
    }
}