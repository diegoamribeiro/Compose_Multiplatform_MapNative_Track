// In the iOS module
package com.dmribeiro.cmpmapview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cocoapods.GooglePlaces.GMSAutocompleteViewController
import cocoapods.GooglePlaces.GMSAutocompleteViewControllerDelegateProtocol
import cocoapods.GooglePlaces.GMSPlace
import com.dmribeiro.cmpmapview.model.Place
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.UIKit.UIApplication
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.NSEC_PER_SEC
import platform.darwin.NSObject
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import kotlin.native.concurrent.freeze

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import platform.Foundation.NSNumber

import platform.UIKit.*

@Composable
actual fun OriginAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(
        label = "Origem",
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
actual fun DestinationAutocompleteTextField(onPlaceSelected: (Place) -> Unit) {
    AutocompleteTextField(
        label = "Destino",
        onPlaceSelected = onPlaceSelected
    )
}

@Composable
fun AutocompleteTextField(
    label: String,
    onPlaceSelected: (Place) -> Unit
) {
    var placeName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = placeName,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .zIndex(1f)
                .clickable {
                    coroutineScope.launch {
                        showAutocompleteBottomSheet { selectedPlace ->
                            placeName = selectedPlace.name
                            onPlaceSelected(selectedPlace)

                            // Mostra o toast com as informações do local selecionado
                            showToast(
                                message = "Selecionado: ${selectedPlace.name}\nLat: ${selectedPlace.latitude}, Lng: ${selectedPlace.longitude}"
                            )
                        }
                    }
                }
        )
    }
}

fun showToast(message: String) {
    val rootViewController = getRootViewController()
    val toastView = UIView().apply {
        backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.6)
        layer.cornerRadius = 10.0
        clipsToBounds = true
    }

    val toastLabel = UILabel().apply {
        text = message
        textColor = UIColor.whiteColor
        textAlignment = NSTextAlignmentCenter
        numberOfLines = 0
        font = UIFont.systemFontOfSize(14.0)
    }

    toastView.addSubview(toastLabel)
    toastLabel.translatesAutoresizingMaskIntoConstraints = false

    NSLayoutConstraint.activateConstraints(
        listOf(
            toastLabel.leadingAnchor.constraintEqualToAnchor(toastView.leadingAnchor, constant = 16.0),
            toastLabel.trailingAnchor.constraintEqualToAnchor(toastView.trailingAnchor, constant = -16.0),
            toastLabel.topAnchor.constraintEqualToAnchor(toastView.topAnchor, constant = 16.0),
            toastLabel.bottomAnchor.constraintEqualToAnchor(toastView.bottomAnchor, constant = -16.0)
        )
    )

    toastView.translatesAutoresizingMaskIntoConstraints = false
    rootViewController?.view?.addSubview(toastView)

    rootViewController?.view?.let { rootView ->
        NSLayoutConstraint.activateConstraints(
            listOf(
                toastView.centerXAnchor.constraintEqualToAnchor(rootView.centerXAnchor),
                toastView.bottomAnchor.constraintEqualToAnchor(rootView.bottomAnchor, constant = -100.0),
                toastView.widthAnchor.constraintLessThanOrEqualToConstant(250.0)
            )
        )
    }

    dispatch_after(
        dispatch_time(DISPATCH_TIME_NOW, (2L * NSEC_PER_SEC.toLong())),
        dispatch_get_main_queue()
    ) {
        toastView.removeFromSuperview()
    }
}