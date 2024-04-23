package com.example.animalobserving

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.math.BigDecimal

class MapMarker(private val markerId: Int, private val latitude: Double, private val longitude: Double) {
    fun getLatitude(): Double {
        return latitude
    }

    fun getLongitude(): Double {
        return longitude
    }
    override fun toString(): String {
        return "$markerId;$latitude;$longitude"
    }

}

class MapViewModel : ViewModel() {
    private val markersList: MutableList<MapMarker> = mutableListOf()
    var mapView: MapView? = null

    fun drawMarkersOnMap() {
        markersList.forEach {
            mapView?.apply {
                val marker = Marker(mapView)
                marker.position = GeoPoint(it.getLatitude(), it.getLongitude())
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Marker"
                marker.snippet = "Lat: ${it.getLatitude()}, Lng: ${it.getLongitude()}"
                overlays.add(marker)
            }
        }
    }

    private fun clearMarkers() {
        mapView?.overlays?.clear()
    }

    fun addMarker(latitude: Double, longitude: Double, id: Int) {
        val newMarker = MapMarker(id, latitude, longitude)
        markersList.add(newMarker)
        clearMarkers()
        drawMarkersOnMap()
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun OsmMapView(mapViewModel: MapViewModel, centreLat: BigDecimal = BigDecimal("48.6690"), centreLng: BigDecimal = BigDecimal("19.6990")) {
    val geoPoint by remember { mutableStateOf(GeoPoint(centreLat.toDouble(), centreLng.toDouble())) }

    mapViewModel.mapView = MapView(LocalContext.current)

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { _ ->
                mapViewModel.mapView?.apply {
                    setTileSource(TileSourceFactory.MAPNIK)

                    setOnClickListener { }
                }!!
            },
            update = { view ->
                view.controller.setCenter(geoPoint)
                view.controller.setZoom(9)
            }
        )
    }
}