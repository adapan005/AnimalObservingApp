package com.example.animalobserving

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.animalobserving.ui.theme.AnimalObservingTheme
import com.example.animalobserving.ui.screens.AnimalObservingApp
import org.osmdroid.config.Configuration


class MainActivity : ComponentActivity() {
    //val mapViewModel: MapViewModel = MapViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Request markers from server
//        val client = NetworkClient("10.0.2.2", 55557)
//        Thread {
//            client.start { message ->
//                // Add markers to list or smth, idk
//                //TODO: pošli mu cez lambdu MapViewModel a že ma do neho pridať marker z messageu
//                val text: String = message.Text
//                val pattern: String = """\{d+\}""" //TENTO PATTERN JE ZLE
//                val regex = Regex(pattern)
//                val matches = regex.findAll(text)
//                //TODO: Urči hodonty pre marker z textu a pridaj mu všetko potrebné
//                //INICIALIZUJ NUMBERS DO ARRAYU
//                val id: Int = numbers[0]
//                val lat1: Double = numbers[1]
//                val lng1: Double = numbers[2]
//                mapViewModel.addMarker(lat1, lng1, id)
//            }
//            client.requestMarkers(38.0, 14.0, 50.0, 20.0)
//            client.stop()
//        }.start()

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContent {
            AnimalObservingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimalObservingApp()
                    //HomeScreen(mapViewModel)
                }
            }
        }
    }
}