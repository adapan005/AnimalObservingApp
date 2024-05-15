package com.example.animalobserving.ui.screens

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.animalobserving.AnimalObservingApplication
import com.example.animalobserving.R
import com.example.animalobserving.data.records.DetailedRecord
import com.example.animalobserving.data.records.RecordsRepository
import com.example.animalobserving.data.species.Specie
import kotlinx.coroutines.launch
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

sealed interface RecordDetailsUiState {
    data class Success(val detailedRecord: DetailedRecord) : RecordDetailsUiState
    object Error : RecordDetailsUiState
    object Loading : RecordDetailsUiState
}

class RecordDetailsViewModel(private val recordsRepository: RecordsRepository, recordId: Int) : ViewModel() {

    var recordDetailsUiState: RecordDetailsUiState by mutableStateOf(RecordDetailsUiState.Loading)
        private set

    init {
        getDetailedRecord(recordId)
    }

    fun getDetailedRecord(recordId: Int) {
        viewModelScope.launch {
            recordDetailsUiState = RecordDetailsUiState.Loading
            recordDetailsUiState = try {
                RecordDetailsUiState.Success(recordsRepository.getDetailedRecord(recordId))
            } catch (e: Exception){
                RecordDetailsUiState.Error
            }
        }
    }

    companion object {
        fun Factory(recordId: Int): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalObservingApplication)
                val recordsRepository = application.container.recordsRepository
                RecordDetailsViewModel(recordsRepository = recordsRepository, recordId = recordId)
            }
        }
    }
}

@Composable
fun RecordDetailsScreen (
    recordId: Int,
    modifier: Modifier,
) {
    val recordDetailsViewModel: RecordDetailsViewModel = viewModel(factory = RecordDetailsViewModel.Factory(recordId))

    when (recordDetailsViewModel.recordDetailsUiState) {
        is RecordDetailsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is RecordDetailsUiState.Success -> DetailsOfRecord(
            (recordDetailsViewModel.recordDetailsUiState as RecordDetailsUiState.Success).detailedRecord,
            modifier = modifier.fillMaxSize()
        )
        is RecordDetailsUiState.Error -> ErrorScreen(retryAction = { recordDetailsViewModel.getDetailedRecord(recordId) })
    }

}

@Composable
fun DetailsOfRecord (
    detailedRecord: DetailedRecord,
    modifier: Modifier
) {
    val mapViewModel: MapViewModel = viewModel(factory = MapViewModel.Factory)
    mapViewModel.mapView = MapView(LocalContext.current)

    Column(
        modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = detailedRecord.getLabel(), fontSize = 30.sp)
        Row {
            Text(text = stringResource(R.string.recordDate), fontWeight = FontWeight.Bold)
            Text(text = detailedRecord.getDate())
        }
        Row {
            Text(text = stringResource(R.string.recordSpecie), fontWeight = FontWeight.Bold)
            Text(text = detailedRecord.getSpecieName())
        }
        Spacer(modifier = Modifier.height(66.dp))
        Card {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxSize()
                    .height(400.dp)
                    .padding(50.dp),
                factory = { _ ->
                    mapViewModel.mapView?.getMapCenter()
                    mapViewModel.mapView?.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        isClickable = true
                        setOnClickListener { }
                        setBuiltInZoomControls(false)
                    }!!
                },
                update = { view ->
                    val geoPoint = GeoPoint(detailedRecord.getLatitude(), detailedRecord.getLongitude())
                    view.controller.setCenter(geoPoint)
                    view.controller.setZoom(13)
                    val newMarker = Marker(mapViewModel.mapView)
                    val position = GeoPoint(detailedRecord.getLatitude(), detailedRecord.getLongitude())
                    newMarker.position = position
                    newMarker.title = detailedRecord.getLabel()
                    mapViewModel.mapView?.overlays?.add(newMarker)
                }
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(text = stringResource(R.string.description), fontWeight = FontWeight.Bold)
        Text(text = detailedRecord.getDescription())
    }
}