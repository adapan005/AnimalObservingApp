package com.example.animalobserving

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.animalobserving.ui.theme.AnimalObservingTheme
import com.example.animalobserving.ui.theme.networkCommunication.NetworkClient
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    val mapViewModel: MapViewModel = MapViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = NetworkClient("192.168.0.191", 55557)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContent {
            AnimalObservingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen(mapViewModel)
                }
            }
        }
    }
}

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
            FloatingActionButton(onClick = { }) {
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
                        mapViewModel.addMarker(48.6690, 19.6990, 1)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AnimalObservingTheme {
    }
}