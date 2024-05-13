package com.example.animalobserving.ui.screens

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
import com.example.animalobserving.data.markers.MapMarkersRepository
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapMarker(private val markerId: Int, private val latitude: Double, private val longitude: Double, private val label: String) {
    fun getLatitude(): Double {
        return latitude
    }

    fun getLongitude(): Double {
        return longitude
    }

    fun getLabel(): String {
        return label
    }

    fun getID(): Int {
        return markerId
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

    var mapView: MapView? = null

}