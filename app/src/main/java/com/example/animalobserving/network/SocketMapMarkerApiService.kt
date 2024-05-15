package com.example.animalobserving.network

import com.example.animalobserving.data.markers.MapMarker
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Calendar

interface SocketMapMarkerApiService {
    suspend fun getMarkers(): List<MapMarker>
}

class SocketMapMarkerApiServiceImpl(private val serverIP: String) : SocketMapMarkerApiService {

    override suspend fun getMarkers(): List<MapMarker> = withContext(Dispatchers.IO) {
        var navrat: MutableList<MapMarker> = mutableListOf()

        val socket = Socket(serverIP, 55557)
        socket.setSoTimeout(400)
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

        ///////// Send request message to server
        val newMessage = Message("", Calendar.getInstance().time, "", MsgType.RequestAllMarkers)
        writer.println(newMessage.toJsonString())

        ///////////// Read response from server

        var response: String? = null
        do {
            try {
                response = reader.readLine()
            } catch (e: Exception) {
                break
            }
            if (response != null) {
                val message = Gson().fromJson(response, Message::class.java)
                val values = message.Text.split(";").toTypedArray()

                val id = values[0].toInt()
                val lat = values[1].replace(',', '.').toDouble()
                val lng = values[2].replace(',', '.').toDouble()
                val recordLabel = values[3]
                val marker = MapMarker(id, lat, lng, recordLabel)
                navrat.add(marker)
            }
        } while (response != null)

        writer.close()
        reader.close()
        socket.close()

        return@withContext navrat
    }
}