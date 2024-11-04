package com.dmribeiro.cmpmapview

import cocoapods.GooglePlaces.GMSAutocompleteViewController
import cocoapods.GooglePlaces.GMSAutocompleteViewControllerDelegateProtocol
import cocoapods.GooglePlaces.GMSPlace
import com.dmribeiro.cmpmapview.model.Place
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.useContents
import platform.Foundation.NSError
import platform.UIKit.*
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class)
suspend fun showAutocompleteBottomSheet(onPlaceSelected: (Place) -> Unit) {
    suspendCoroutine<Unit> { continuation ->
        val autocompleteController = GMSAutocompleteViewController().apply {
            delegate = AutocompleteDelegate(
                onPlaceSelected = { place ->
                    onPlaceSelected(place)
                    continuation.resume(Unit)
                },
                onCancelled = {
                    continuation.resume(Unit)
                }
            )
        }

        dispatch_async(dispatch_get_main_queue()) {
            val rootViewController = getRootViewController()
            rootViewController?.presentViewController(autocompleteController, true, null)
        }
    }
}

fun getRootViewController(): UIViewController? {
    return UIApplication.sharedApplication.connectedScenes
        .filterIsInstance<UIWindowScene>()
        .firstOrNull()?.keyWindow?.rootViewController
}

@OptIn(ExperimentalForeignApi::class)
class AutocompleteDelegate(
    private val onPlaceSelected: (Place) -> Unit,
    private val onCancelled: () -> Unit
) : NSObject(), GMSAutocompleteViewControllerDelegateProtocol {

    override fun viewController(viewController: GMSAutocompleteViewController, didAutocompleteWithPlace: GMSPlace) {
        val selectedPlace = Place(
            name = didAutocompleteWithPlace.name ?: "",
            address = didAutocompleteWithPlace.formattedAddress,
            latitude = didAutocompleteWithPlace.coordinate.useContents { latitude },
            longitude = didAutocompleteWithPlace.coordinate.useContents { longitude }
        )
        onPlaceSelected(selectedPlace)
        viewController.dismissViewControllerAnimated(true, null)
    }

    override fun viewController(viewController: GMSAutocompleteViewController, didFailAutocompleteWithError: NSError) {
        println("Autocomplete error: ${didFailAutocompleteWithError.localizedDescription}")
        viewController.dismissViewControllerAnimated(true, null)
        onCancelled()
    }

    override fun wasCancelled(viewController: GMSAutocompleteViewController) {
        viewController.dismissViewControllerAnimated(true, null)
        onCancelled()
    }
}