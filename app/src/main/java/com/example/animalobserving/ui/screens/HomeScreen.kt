package com.example.animalobserving.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.animalobserving.R
import com.example.animalobserving.data.markers.MapMarker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun HomeScreen(
    mapViewModel: MapViewModel,
    navController: NavHostController,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var mapUiState = mapViewModel.mapUiState

    when (mapUiState) {
        is MapUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MapUiState.Success -> OsmMap(mapUiState.markers, navController, mapViewModel, modifier)
        is MapUiState.Error -> ErrorScreen(retryAction = retryAction)
    }
}

@Composable
fun OsmMap(
    markers: List<MapMarker>,
    navController: NavHostController,
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier,
) {
    val currentLat by remember {
        mapViewModel.currentCentreLat
    }
    var currentLon by remember {
        mapViewModel.currentCentreLon
    }
    mapViewModel.mapView = MapView(LocalContext.current)

    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f),
            factory = { _ ->
                mapViewModel.mapView?.getMapCenter()
                mapViewModel.mapView?.apply {

                    setMultiTouchControls(true)
                    setBuiltInZoomControls(false)
                    setTileSource(TileSourceFactory.MAPNIK)
                    setOnClickListener { }
                }!!
            },
            update = { view ->
                val geoPoint = GeoPoint(currentLat, currentLon)
                view.controller.setCenter(geoPoint)
                view.controller.setZoom(9)
                markers.forEach {
                    val newMarker = Marker(mapViewModel.mapView)
                    val position = GeoPoint(it.getLatitude(), it.getLongitude())
                    newMarker.position = position

                    newMarker.setOnMarkerClickListener { marker, _ ->
                        Log.d(TAG, "CLICKED: ${it.getLabel()}")
                        navController.navigate("${AppScreen.RecordDetails.name}/${it.getID()}")
                        true
                    }
                    mapViewModel.mapView?.overlays?.add(newMarker)
                }
            }
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Text(text = stringResource(R.string.loading))
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.error), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}