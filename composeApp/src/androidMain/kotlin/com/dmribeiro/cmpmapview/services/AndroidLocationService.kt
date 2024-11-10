package com.dmribeiro.cmpmapview.services

import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.dmribeiro.cmpmapview.model.LocationModel
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidLocationService(private val context: Context) : LocationService {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCurrentLocation(): LocationModel? {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            val location = fusedLocationClient.lastLocation.await()
            return if (location != null) {
                LocationModel(location.latitude, location.longitude)
            } else {
                requestNewLocationData()
            }
        } else {
            return null
        }
    }

    private suspend fun requestNewLocationData(): LocationModel? {
        val locationInterval = 1000L
        val locationFastestInterval = 500L
        val locationMaxWaitTime = 500L

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setIntervalMillis(locationInterval)
            .setMinUpdateIntervalMillis(locationFastestInterval)
            .setMaxUpdateDelayMillis(locationMaxWaitTime)
            .build()

        return suspendCancellableCoroutine { cont ->
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this)
                    val location = result.lastLocation
                    if (location != null) {
                        cont.resume(LocationModel(location.latitude, location.longitude))
                    } else {
                        cont.resume(null)
                    }
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
}