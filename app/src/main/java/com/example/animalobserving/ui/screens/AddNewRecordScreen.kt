package com.example.animalobserving.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.animalobserving.AnimalObservingApplication
import com.example.animalobserving.data.species.Specie
import com.example.animalobserving.data.species.SpeciesRepository
import kotlinx.coroutines.launch

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
    val recordDescriptionText by remember { speciesViewModel.recordDescriptionText }
    val recordLatitude by remember { speciesViewModel.recordLatitude }
    val recordLongitude by remember { speciesViewModel.recordLongitude }
    //val selectedSpecie by remember {}

    when (speciesViewModel.speciesUiState) {
        is SpeciesUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is SpeciesUiState.Success -> {

            Column(modifier = Modifier.fillMaxSize()) {
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
                Text("Choose animal specie:")
                DynamicSelectTextField("F", (speciesViewModel.speciesUiState as SpeciesUiState.Success).species, "Label", {}, modifier = Modifier.weight(1f))
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Submit")
                }
            }
        }
        is SpeciesUiState.Error -> ErrorScreen(retryAction = { speciesViewModel.getSpecies() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedValue: String,
    options: List<Specie>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedValue,
            onValueChange = {},
            //label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val speciesNames: List<String> = options.map { it.getSpecieName() }
            speciesNames.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChangedEvent(option)
                    }
                )
            }
        }
    }
}