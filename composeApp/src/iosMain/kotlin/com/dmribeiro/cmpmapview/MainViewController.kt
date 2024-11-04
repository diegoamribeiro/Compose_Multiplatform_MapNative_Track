package com.dmribeiro.cmpmapview

import androidx.compose.ui.window.ComposeUIViewController
import com.dmribeiro.cmpmapview.services.IOSLocationService
import platform.UIKit.UIViewController


fun MainViewController(): UIViewController {
    return ComposeUIViewController {
        AppWithMap(locationService = IOSLocationService())
    }
}