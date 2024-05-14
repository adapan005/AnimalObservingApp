package com.example.animalobserving.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.animalobserving.AnimalObservingApplication
import com.example.animalobserving.data.species.Specie
import com.example.animalobserving.data.species.SpeciesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    modifier: Modifier
) {
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
                .fillMaxWidth()
                .padding(32.dp)) {
                Text("Enter name of record:")
                TextField(
                    value = recordNameText,
                    onValueChange = { newText ->
                        speciesViewModel.recordNameText.value = newText
                    }
                )
                Text("Enter description of record:")
                TextField(
                    value = recordDescriptionText,
                    onValueChange = { newText ->
                        speciesViewModel.recordDescriptionText.value = newText
                    }
                )
                Text("Enter latitude:")
                TextField(
                    value = recordLatitude.toString(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { newText ->
                        speciesViewModel.recordLatitude.value = newText.toDouble()
                    }
                )
                Text("Enter longitude:")
                TextField(
                    value = recordLongitude.toString(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    onValueChange = { newText ->
                        speciesViewModel.recordLongitude.value = newText.toDouble()
                    }
                )
                Button(onClick = {
                    TODO("NOT IMPLEMENTED YET;")
                }) {
                    Text(text = "Get current coordinates")
                }
                Text("Choose animal specie:")
                //DropdownSelector((speciesViewModel.speciesUiState as SpeciesUiState.Success).species, "Label", {}, modifier = Modifier.weight(1f))
                SpecieSelectionMenu((speciesViewModel.speciesUiState as SpeciesUiState.Success).species, "Select animal specie", selectedSpecie, selectedSpecieID, modifier.weight(1f))
                Button(onClick = { speciesViewModel.submitNewRecord() }) {
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
                                //Toast.makeText(context, "Selected ID: ${specie.getID()}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}