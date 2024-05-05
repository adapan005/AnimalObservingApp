package com.example.animalobserving.network

import com.example.animalobserving.data.MapMarkersRepository
import com.example.animalobserving.ui.screens.MapMarker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Calendar

interface SocketMapMarkerApiService {
    suspend fun getMarkers(): List<MapMarker>
    fun sendRecord(marker: MapMarker)
}

class SocketMapMarkerApiServiceImpl() : SocketMapMarkerApiService {

    override suspend fun getMarkers(): List<MapMarker> = withContext(Dispatchers.IO) {
        var navrat: MutableList<MapMarker> = mutableListOf()

        val socket = Socket("10.0.2.2", 55557)
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

        // Send request message to server

        val newMessage = Message("", Calendar.getInstance().time, "", MsgType.RequestAllMarkers)
        writer.println(newMessage.toJsonString())

        // Read response from server
        val markers = mutableListOf<String>()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            markers.add(line!!)
        }

        // Close resources
        writer.close()
        reader.close()
        socket.close()

        return@withContext navrat
    }

    override fun sendRecord(marker: MapMarker) {
        //TODO("Not yet implemented")
    }

}