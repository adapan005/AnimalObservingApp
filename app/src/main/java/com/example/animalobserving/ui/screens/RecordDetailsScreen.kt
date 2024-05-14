package com.example.animalobserving.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Card {
        Column(
            modifier.padding(20.dp)
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
            Text(text = stringResource(R.string.description), fontWeight = FontWeight.Bold)
            Text(text = detailedRecord.getDescription())
        }
    }
}