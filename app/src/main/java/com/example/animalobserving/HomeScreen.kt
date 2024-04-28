package com.example.animalobserving

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(mapViewModel: MapViewModel ) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Animal Observing Client") }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                OsmMapView(mapViewModel)
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                mapViewModel.addMarker(48.6690, 19.6990, 1)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        bottomBar = {
            NavigationBar {
                val selectedItem = null
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "A") },
                    label = { Text("List") },
                    selected = selectedItem == "A",
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Build, contentDescription = "A") },
                    label = { Text("Filter") },
                    selected = selectedItem == "B",
                    onClick =
                    {
                        mapViewModel.drawMarkersOnMap()
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "A") },
                    label = { Text("Settings") },
                    selected = selectedItem == "C",
                    onClick = { }
                )
            }
        }
    )
}