package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.animateWithCameraUpdate
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import com.dmribeiro.cmpmapview.model.LocationModel
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.objcPtr
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGRectMake
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIScreen
import platform.UIKit.UISegmentedControl
import platform.UIKit.systemBlueColor
import platform.darwin.NSObject
import platform.objc.OBJC_ASSOCIATION_RETAIN_NONATOMIC
import platform.objc.objc_setAssociatedObject


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapsComponent(
    routePolylinePoints: List<LocationModel>,
    latitude: Double,
    longitude: Double
) {
    val mapViewState = remember { mutableStateOf<GMSMapView?>(null) }
    val lastLocation = remember { mutableStateOf<Pair<Double, Double>?>(null) }

    UIKitView(
        factory = {
            // Definindo coordenadas iniciais e configurando a câmera
            val initialCoordinates = CLLocationCoordinate2DMake(latitude, longitude)
            val camera = GMSCameraPosition.cameraWithTarget(initialCoordinates, zoom = 15.0f)
            val mapView = GMSMapView(frame = UIScreen.mainScreen.bounds, camera = camera)

            // Configurando controles e interações do mapa
            mapView.settings.apply {
                scrollGestures = true
                zoomGestures = true
                tiltGestures = true
                rotateGestures = true
                compassButton = true
                myLocationButton = true
                indoorPicker = true
                setAllGesturesEnabled(true)
            }

            // Habilitando a camada de localização e mapas internos
            mapView.myLocationEnabled = true
            mapView.trafficEnabled = false
            mapView.indoorEnabled = true

            mapViewState.value = mapView

            // Adicionando um marcador inicial na posição atual
            val marker = GMSMarker().apply {
                position = initialCoordinates
                title = "Current Location"
                snippet = "Lat: $latitude, Lng: $longitude"
            }
            marker.map = mapView

            // Criação de um delegate para mudar o tipo de mapa
            val mapViewDelegate = MapViewDelegate(mapView)
            val key = interpretCPointer<CPointed>(mapViewDelegate.objcPtr())

            objc_setAssociatedObject(
                mapView,
                key,
                mapViewDelegate,
                OBJC_ASSOCIATION_RETAIN_NONATOMIC
            )

            // Controle para seleção do tipo de mapa
            val mapTypeControl = UISegmentedControl(items = listOf<Any>("Normal", "Satellite", "Terrain", "Hybrid")).apply {
                selectedSegmentIndex = 0 // Tipo Normal por padrão

                // Definindo posição do controle no layout
                val safeAreaTop = mapView.safeAreaInsets.useContents { top }
                val screenWidth = UIScreen.mainScreen.bounds.useContents { size.width }

                setFrame(
                    CGRectMake(
                        x = 10.0,
                        y = safeAreaTop + 10.0,
                        width = screenWidth - 20.0,
                        height = 30.0
                    )
                )
                addTarget(
                    target = mapViewDelegate,
                    action = NSSelectorFromString("mapTypeChanged:"),
                    forControlEvents = UIControlEventValueChanged
                )
                tintColor = UIColor.systemBlueColor
                backgroundColor = UIColor.whiteColor
                layer.cornerRadius = 5.0
                clipsToBounds = true
            }

            // Adicionando o controle ao mapa
            mapView.addSubview(mapTypeControl)

            mapView
        },
        update = { view ->
            // Atualizando a posição da câmera e marcadores ao mudar de localização
            if (lastLocation.value != Pair(latitude, longitude)) {
                lastLocation.value = Pair(latitude, longitude)

                val newCoordinates = CLLocationCoordinate2DMake(latitude, longitude)
                val cameraUpdate = GMSCameraUpdate.setCamera(
                    GMSCameraPosition.cameraWithTarget(newCoordinates, view.camera.zoom)
                )
                view.animateWithCameraUpdate(cameraUpdate)

                // Limpando marcadores antigos e adicionando um novo marcador
                view.clear()
                val marker = GMSMarker().apply {
                    position = newCoordinates
                    title = "Current Location"
                    snippet = "Lat: $latitude, Lng: $longitude"
                }
                marker.map = view
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// Delegate para gerenciar o tipo de mapa
class MapViewDelegate @OptIn(ExperimentalForeignApi::class) constructor(val mapView: GMSMapView) : NSObject() {
    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    @ObjCAction
    fun mapTypeChanged(sender: UISegmentedControl) {
        println("mapTypeChanged called with index ${sender.selectedSegmentIndex}")
        mapView.mapType = when (sender.selectedSegmentIndex.toInt()) {
            0 -> kGMSTypeNormal
            1 -> kGMSTypeSatellite
            2 -> kGMSTypeTerrain
            3 -> kGMSTypeHybrid
            else -> kGMSTypeNormal
        }
    }
}