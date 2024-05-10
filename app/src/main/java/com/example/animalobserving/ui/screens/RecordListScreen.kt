package com.example.animalobserving.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RecordListScreen (
    mapViewModel: MapViewModel,
    navController: NavHostController,
    modifier: Modifier
) {
    var mapUiState = mapViewModel.mapUiState
    Box(modifier = modifier.fillMaxSize()) {
        when (mapUiState) {
            is MapUiState.Loading -> LoadingScreen(modifier = Modifier.fillMaxSize())
            is MapUiState.Success -> RecordsGrid(mapUiState.markers, navController, Modifier.fillMaxSize())
            is MapUiState.Error -> ErrorScreen(retryAction = { })
        }
    }
}

@Composable
fun RecordsGrid(
    markers: List<MapMarker>,
    navController: NavHostController,
    modifier: Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier.padding(horizontal = 4.dp),
        contentPadding = contentPadding
    ) {
        items(items = markers, key = {marker -> marker.getID()}) { marker ->
            RecordButton(
                marker = marker,
                navController,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            )
        }
    }
}

@Composable
fun RecordButton(
    marker: MapMarker,
    navController: NavHostController,
    modifier: Modifier = Modifier,

) {
    Button(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
        onClick = {
            navController.navigate("TEST")
        }
    ) {
        Text(text = marker.getLabel())
    }
}