package com.dmribeiro.cmpmapview.services

import com.dmribeiro.cmpmapview.model.LocationModel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class IOSLocationService : LocationService {
    private val locationManager = CLLocationManager().apply {
        desiredAccuracy = kCLLocationAccuracyBest
    }

    // Declare delegate as a property
    private val delegate = LocationManagerDelegate()

    init {
        locationManager.delegate = delegate
        locationManager.requestWhenInUseAuthorization()
    }

    override suspend fun getCurrentLocation(): LocationModel? {
        return suspendCancellableCoroutine { continuation ->
            delegate.locationContinuation = continuation
            val status = CLLocationManager.authorizationStatus()
            println("Authorization status: $status")
            when (status) {
                kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                    locationManager.requestLocation()
                }
                kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                    continuation.resumeWithException(Exception("Location permission denied"))
                }
                kCLAuthorizationStatusNotDetermined -> {
                    locationManager.requestWhenInUseAuthorization()
                    // The delegate method will handle the rest
                }
            }
        }
    }
}

class LocationManagerDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var locationContinuation: CancellableContinuation<LocationModel?>? = null

    @OptIn(ExperimentalForeignApi::class)
    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        println("locationManager didUpdateLocations called")
        val location = didUpdateLocations.lastOrNull() as? CLLocation
        if (location != null) {
            memScoped {
                val coordinate = location.coordinate.useContents {
                    println("***Location: $latitude, $longitude")
                    LocationModel(latitude, longitude)
                }
                locationContinuation?.resume(coordinate)
            }
        } else {
            println("Location is null")
            locationContinuation?.resumeWithException(Exception("Location not found"))
        }
        locationContinuation = null
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        println("locationManager didFailWithError called: ${didFailWithError.localizedDescription}")
        locationContinuation?.resumeWithException(Exception("Failed to get location: ${didFailWithError.localizedDescription}"))
        locationContinuation = null
    }

    override fun locationManager(manager: CLLocationManager, didChangeAuthorizationStatus: CLAuthorizationStatus) {
        println("Authorization status changed: $didChangeAuthorizationStatus")
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        println("locationManagerDidChangeAuthorization: $status")
        when (status) {
            kCLAuthorizationStatusAuthorizedWhenInUse, kCLAuthorizationStatusAuthorizedAlways -> {
                manager.requestLocation()
            }
            kCLAuthorizationStatusDenied, kCLAuthorizationStatusRestricted -> {
                locationContinuation?.resumeWithException(Exception("Location permission denied"))
            }
            kCLAuthorizationStatusNotDetermined -> {
                manager.requestWhenInUseAuthorization()
            }
        }
    }
}