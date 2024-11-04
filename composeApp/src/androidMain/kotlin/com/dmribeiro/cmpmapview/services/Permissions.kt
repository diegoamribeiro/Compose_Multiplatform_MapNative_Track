package com.dmribeiro.cmpmapview.services

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity

const val LOCATION_PERMISSION_REQUEST_CODE = 1001

fun ComponentActivity.checkLocationPermissions(onPermissionGranted: () -> Unit) {
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
        ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE)
    }
}
