package com.dmribeiro.cmpmapview

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dmribeiro.cmpmapview.services.AndroidLocationService
import com.dmribeiro.currencyapp.BuildConfig
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {

    private val locationService by lazy { AndroidLocationService(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        checkLocationPermissions {
            setContent {
                AppWithMap(locationService = locationService)
            }
        }
    }

    private fun checkLocationPermissions(onPermissionGranted: () -> Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val allPermissionsGranted = permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
            onPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setContent {
                    AppWithMap(locationService = locationService)
                }
            } else {
                // Handle permission denial
            }
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(latitude = 37.7749, longitude = -122.4194)
}