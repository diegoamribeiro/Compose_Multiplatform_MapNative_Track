package com.dmribeiro.cmpmapview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.GMSCameraPosition
import cocoapods.GoogleMaps.GMSCameraUpdate
import cocoapods.GoogleMaps.GMSCoordinateBounds
import cocoapods.GoogleMaps.GMSMapView
import cocoapods.GoogleMaps.GMSMarker
import cocoapods.GoogleMaps.GMSMutablePath
import cocoapods.GoogleMaps.GMSPolyline
import cocoapods.GoogleMaps.animateWithCameraUpdate
import cocoapods.GoogleMaps.kGMSTypeHybrid
import cocoapods.GoogleMaps.kGMSTypeNormal
import cocoapods.GoogleMaps.kGMSTypeSatellite
import cocoapods.GoogleMaps.kGMSTypeTerrain
import com.dmribeiro.cmpmapview.model.LocationModel
import com.dmribeiro.cmpmapview.services.LocationService
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.StableRef
import platform.CoreLocation.CLLocationCoordinate2DMake
import platform.Foundation.NSAttributedString
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIBarMetrics
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventValueChanged
import platform.UIKit.UIControlState
import platform.UIKit.UIImage
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
    longitude: Double,
    locationService: LocationService
) {
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
            }

            // Habilitando a camada de localização e mapas internos
            mapView.myLocationEnabled = true
            mapView.trafficEnabled = false
            mapView.indoorEnabled = true

            // Adicionando um marcador inicial na posição atual
            val marker = GMSMarker().apply {
                position = initialCoordinates
                title = "Current Location"
                snippet = "Lat: $latitude, Lng: $longitude"
            }
            marker.map = mapView

            // Criação de um delegate para mudar o tipo de mapa
            val mapViewDelegate = MapViewDelegate(mapView)
            val delegatePtr = StableRef.create(mapViewDelegate).asCPointer()

            objc_setAssociatedObject(
                mapView,
                delegatePtr,
                mapViewDelegate,
                OBJC_ASSOCIATION_RETAIN_NONATOMIC
            )

            // Controle para seleção do tipo de mapa
            val mapTypeControl = UISegmentedControl(
                items = listOf<Any>(
                    "Normal",
                    "Satellite",
                    "Terrain",
                    "Hybrid"
                )
            ).apply {
                selectedSegmentIndex = 0 // Tipo Normal por padrão

                // Definindo posição do controle no layout
                translatesAutoresizingMaskIntoConstraints = false

                // Configurando cores
                backgroundColor = UIColor.whiteColor.colorWithAlphaComponent(0.9) // Fundo quase opaco
                selectedSegmentTintColor = UIColor.lightGrayColor // Cor de seleção

                layer.borderColor = UIColor.whiteColor.colorWithAlphaComponent(0.7).CGColor()
                layer.borderWidth = 1.0
                layer.cornerRadius = 5.0
                clipsToBounds = true

                // Remover efeito de transparência nos segmentos não selecionados
                setBackgroundImage(
                    UIImage(),
                    forState = UIControlState.MAX_VALUE,
                    barMetrics = UIBarMetrics.MAX_VALUE
                )
                setBackgroundImage(
                    UIImage(),
                    forState = UIControlState.MAX_VALUE,
                    barMetrics = UIBarMetrics.MAX_VALUE
                )

                // Definir altura mínima
                heightAnchor.constraintEqualToConstant(32.0).active = true

                // Configurando target-action
                addTarget(
                    target = mapViewDelegate,
                    action = NSSelectorFromString("mapTypeChanged:"),
                    forControlEvents = UIControlEventValueChanged
                )
            }

            // Adicionando o controle ao mapa
            mapView.addSubview(mapTypeControl)

            // Configurando restrições de layout
            mapTypeControl.leadingAnchor.constraintEqualToAnchor(
                anchor = mapView.leadingAnchor,
                constant = 16.0
            ).active = true

            mapTypeControl.trailingAnchor.constraintEqualToAnchor(
                anchor = mapView.trailingAnchor,
                constant = -16.0
            ).active = true

            mapTypeControl.topAnchor.constraintEqualToAnchor(
                anchor = mapView.safeAreaLayoutGuide.topAnchor,
                constant = 8.0
            ).active = true

            mapView
        },
        update = { view ->
            // Update the camera position and markers when the location changes
            if (lastLocation.value != Pair(latitude, longitude)) {
                lastLocation.value = Pair(latitude, longitude)

                val newCoordinates = CLLocationCoordinate2DMake(latitude, longitude)
                val cameraUpdate = GMSCameraUpdate.setCamera(
                    GMSCameraPosition.cameraWithTarget(newCoordinates, view.camera.zoom)
                )
                view.animateWithCameraUpdate(cameraUpdate)

                view.clear()
                val marker = GMSMarker().apply {
                    position = newCoordinates
                    title = "Current Location"
                    snippet = "Lat: $latitude, Lng: $longitude"
                }
                marker.map = view
            } else {
                // Se a localização não mudou, podemos querer limpar polylines anteriores
                view.clear()

                // Reposicionar o marcador atual
                val currentCoordinates = CLLocationCoordinate2DMake(latitude, longitude)
                val marker = GMSMarker().apply {
                    position = currentCoordinates
                    title = "Current Location"
                    snippet = "Lat: $latitude, Lng: $longitude"
                }
                marker.map = view
            }

            // Desenhando ou redesenhando a Polyline no mapa
            if (routePolylinePoints.isNotEmpty()) {
                println("***Desenhando Polyline no iOS com ${routePolylinePoints.size} pontos")
                val path = GMSMutablePath().apply {
                    routePolylinePoints.forEach { point ->
                        println("***Adicionando ponto: Lat=${point.latitude}, Lng=${point.longitude}")
                        addLatitude(point.latitude, point.longitude)
                    }
                }
                GMSPolyline().apply {
                    this.path = path
                    strokeColor = UIColor.orangeColor
                    strokeWidth = 5.0
                    map = view
                }

                // Ajustar a câmera para enquadrar a polyline
                var bounds: GMSCoordinateBounds? = null
                routePolylinePoints.forEach { point ->
                    val coordinate = CLLocationCoordinate2DMake(point.latitude, point.longitude)
                    bounds = if (bounds == null) {
                        // Inicializar bounds com a primeira coordenada
                        GMSCoordinateBounds(coordinate, coordinate)
                    } else {
                        // Expandir bounds para incluir a nova coordenada
                        bounds!!.includingCoordinate(coordinate)
                    }
                }
                if (bounds != null) {
                    val cameraUpdate = GMSCameraUpdate.fitBounds(bounds!!, withPadding = 50.0)
                    view.animateWithCameraUpdate(cameraUpdate)
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// Delegate para gerenciar o tipo de mapa
class MapViewDelegate @OptIn(ExperimentalForeignApi::class) constructor(private val mapView: GMSMapView) :
    NSObject() {
    @OptIn(ExperimentalForeignApi::class)
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
