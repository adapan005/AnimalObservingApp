package com.example.animalobserving.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.example.animalobserving.data.species.Specie
import com.example.animalobserving.data.species.SpeciesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

sealed interface SpeciesUiState {
    data class Success(val species: List<Specie>) : SpeciesUiState
    object Error : SpeciesUiState
    object Loading : SpeciesUiState
}

class SpeciesViewModel(private val speciesRepository: SpeciesRepository) : ViewModel() {
    val recordNameText = mutableStateOf("")
    val recordDescriptionText = mutableStateOf("")
    val recordLatitude = mutableStateOf(0.0)
    val recordLongitude = mutableStateOf(0.0)
    val selectedSpecieID = mutableStateOf(0)
    var speciesUiState: SpeciesUiState by mutableStateOf(SpeciesUiState.Loading)
        private set
    init {
        getSpecies()
    }

    fun getSpecies() {
        viewModelScope.launch {
            speciesUiState = SpeciesUiState.Loading
            speciesUiState = try {
                SpeciesUiState.Success(speciesRepository.getSpecies())
            } catch (e: Exception) {
                SpeciesUiState.Error
            }
        }
    }

    fun submitNewRecord() {
        viewModelScope.launch {
            try {
                val latitudeWithComma = recordLatitude.value.toString().replace('.', ',')
                val longitudeWithComma = recordLongitude.value.toString().replace('.', ',')
                withContext(Dispatchers.IO) {
                    speciesRepository.submitNewRecord(
                        "" +
                                "${selectedSpecieID.value};" +
                                "${latitudeWithComma};" +
                                "${longitudeWithComma};" +
                                "${recordNameText.value};" +
                                "${recordDescriptionText.value}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AnimalObservingApplication)
                val speciesRepository = application.container.speciesRepository
                SpeciesViewModel(speciesRepository = speciesRepository)
            }
        }
    }

}

@Composable
fun AddingNewRecordScreen (
    navController: NavHostController,
    modifier: Modifier
) {
    val context = LocalContext.current
    val mapViewModel: MapViewModel = viewModel(factory = MapViewModel.Factory)
    mapViewModel.mapView = MapView(LocalContext.current)

    val speciesViewModel: SpeciesViewModel = viewModel(factory = SpeciesViewModel.Factory)
    val recordNameText by remember { speciesViewModel.recordNameText }
    val selectedSpecie = remember { mutableStateOf("") }
    val selectedSpecieID = remember { speciesViewModel.selectedSpecieID }
    val recordDescriptionText by remember { speciesViewModel.recordDescriptionText }
    val recordLatitude by remember { speciesViewModel.recordLatitude }
    val recordLongitude by remember { speciesViewModel.recordLongitude }
    //val selectedSpecie by remember {}

    when (speciesViewModel.speciesUiState) {
        is SpeciesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is SpeciesUiState.Success -> {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(32.dp)) {
                Text(stringResource(R.string.enter_name_of_record))
                TextField(
                    value = recordNameText,
                    onValueChange = { newText ->
                        speciesViewModel.recordNameText.value = newText
                    }
                )
                Text(stringResource(R.string.enter_description_of_record))
                TextField(
                    value = recordDescriptionText,
                    onValueChange = { newText ->
                        speciesViewModel.recordDescriptionText.value = newText
                    }
                )
                Spacer(modifier = Modifier.height(50.dp))
                Text(text = "Choose location of record:")
                Card {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            .padding(50.dp),
                        factory = { context ->
                            mapViewModel.mapView?.apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                isClickable = true
                                setBuiltInZoomControls(false)

                                setOnTouchListener { view, event ->
                                    if (event.action == MotionEvent.ACTION_UP) {
                                        mapViewModel.mapView?.overlays?.clear()
                                        val projection = this.projection
                                        val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
                                        speciesViewModel.recordLatitude.value = geoPoint.latitude
                                        speciesViewModel.recordLongitude.value = geoPoint.longitude
                                        Log.d(TAG, "${speciesViewModel.recordLatitude.value}")
                                        val marker = Marker(this).apply {
                                            position = geoPoint as GeoPoint?
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        }
                                        this.overlays.add(marker)
                                        this.invalidate()
                                        view.performClick()
                                        true
                                    } else {
                                        false
                                    }
                                }

                                // Override performClick to ensure accessibility services are notified
                                this.setOnClickListener {
                                    // You can add any additional behavior for click here if needed
                                }
                            }!!
                        },
                        update = { view ->
                            view.controller.setCenter(GeoPoint(48.6690, 19.6990))
                            view.controller.setZoom(8)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
                Text("Choose animal specie:")
                
                SpecieSelectionMenu(
                    species = (speciesViewModel.speciesUiState as SpeciesUiState.Success).species,
                    label = "Select animal specie",
                    selectedTextState = selectedSpecie,
                    selectedSpecieIdState = selectedSpecieID,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    speciesViewModel.submitNewRecord()
                    Toast.makeText(context, "Submitted!", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Submit")
                }
            }
        }
        is SpeciesUiState.Error -> ErrorScreen(retryAction = { speciesViewModel.getSpecies() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecieSelectionMenu(
    species: List<Specie>,
    label: String,
    selectedTextState: MutableState<String>,
    selectedSpecieIdState: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedTextState.value,
                onValueChange = { selectedTextState.value = it },
                label = { Text(text = label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            val filteredOptions = species.filter { it.getSpecieName().contains(selectedTextState.value, ignoreCase = true) }
            if (filteredOptions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {

                    }
                ) {
                    filteredOptions.forEach { specie ->
                        DropdownMenuItem(
                            text = { Text(text = specie.getSpecieName()) },
                            onClick = {
                                selectedTextState.value = specie.getSpecieName()
                                selectedSpecieIdState.value = specie.getID()
                                expanded = false
                                Toast.makeText(context, "Selected ID: ${specie.getID()}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}