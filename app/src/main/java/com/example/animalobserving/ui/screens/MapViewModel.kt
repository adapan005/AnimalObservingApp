package com.example.animalobserving.ui.screens

import android.text.Spannable.Factory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.animalobserving.AnimalObservingApplication
import com.example.animalobserving.data.MapMarkersRepository
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException

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

sealed interface MapUiState {
    data class Success(val markers: List<MapMarker>) : MapUiState
    object Error : MapUiState
    object Loading : MapUiState
}

class MapViewModel(private val mapMarkersRepository: MapMarkersRepository) : ViewModel() {

    var mapUiState: MapUiState by mutableStateOf(MapUiState.Loading)
        private set

    init {
        getMapMarkers()
    }

    fun getMapMarkers() {
        viewModelScope.launch {
            mapUiState = MapUiState.Loading
            mapUiState = try {
                MapUiState.Success(mapMarkersRepository.getMapMarkers())
            } catch (e: Exception) {
                MapUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AnimalObservingApplication)
                val mapMarkersRepository = application.container.mapMarkersRepository
                MapViewModel(mapMarkersRepository = mapMarkersRepository)
            }
        }
    }

    private val markersList: MutableList<MapMarker> = mutableListOf()

    var mapView: MapView? = null

    private fun clearMarkers() {
        mapView?.overlays?.clear()
    }

    fun addMarker(latitude: Double, longitude: Double, id: Int) {
        val newMarker = MapMarker(id, latitude, longitude)
        markersList.add(newMarker)
        clearMarkers()
        drawMarkersOnMap()
    }

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
}