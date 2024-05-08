package com.example.animalobserving.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.math.BigDecimal

@Composable
fun HomeScreen(
    mapViewModel: MapViewModel,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    centreLat: BigDecimal = BigDecimal("48.6690"),
    centreLng: BigDecimal = BigDecimal("19.6990"),
) {
    var mapUiState = mapViewModel.mapUiState
    val geoPoint by remember { mutableStateOf(GeoPoint(centreLat.toDouble(), centreLng.toDouble())) }

    when (mapUiState) {
        is MapUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is MapUiState.Success -> OsmMap(mapUiState.markers, mapViewModel, modifier)
        is MapUiState.Error -> ErrorScreen(retryAction = retryAction)
    }
}

@Composable
fun OsmMap(
    markers: List<MapMarker>,
    mapViewModel: MapViewModel,
    modifier: Modifier = Modifier,
    centreLat: BigDecimal = BigDecimal("48.6690"),
    centreLng: BigDecimal = BigDecimal("19.6990")
) {
    val geoPoint by remember { mutableStateOf(GeoPoint(centreLat.toDouble(), centreLng.toDouble())) }
    mapViewModel.mapView = MapView(LocalContext.current)

    Column(modifier = modifier.fillMaxSize()) {
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
                markers.forEach {
                    val newMarker = Marker(mapViewModel.mapView)
                    val position = GeoPoint(it.getLatitude(), it.getLongitude())
                    newMarker.position = position
                    mapViewModel.mapView?.overlays?.add(newMarker)
                }
//                val marker = Marker(mapViewModel.mapView)
//                marker.position = geoPoint
//                marker.title = "Marker Title"
//                marker.snippet = "Marker Description"
//                mapViewModel.mapView?.overlays?.add(marker)
            }
        )
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Text(text = "Loading")
}

@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error", modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text("Retry")
        }
    }
}