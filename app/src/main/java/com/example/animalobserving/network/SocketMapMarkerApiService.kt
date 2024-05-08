package com.example.animalobserving.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.animalobserving.data.MapMarkersRepository
import com.example.animalobserving.ui.screens.MapMarker
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
    fun sendRecord(marker: MapMarker)
}

class SocketMapMarkerApiServiceImpl() : SocketMapMarkerApiService {

    override suspend fun getMarkers(): List<MapMarker> = withContext(Dispatchers.IO) {
        var navrat: MutableList<MapMarker> = mutableListOf()

        val socket = Socket("192.168.100.196", 55557)
        socket.setSoTimeout(5000)
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

        ///////// Send request message to server
        val newMessage = Message("", Calendar.getInstance().time, "", MsgType.RequestAllMarkers)
        writer.println(newMessage.toJsonString())

        ///////////// Read response from server
        TODO("OPRAVIT ABY TO VEDELO SPRACOVAT VSETKY JSON STRINGY ZO SERVERU!!!")
        val response = reader.readLine()
        val message = Gson().fromJson(response, Message::class.java)
        //val text = message.Text
        val values = message.Text.split(";").toTypedArray()

        val id = values[0].toInt()
        val lat = values[1].replace(',', '.').toDouble()
        val lng = values[2].replace(',', '.').toDouble()
        val marker = MapMarker(id, lat, lng)
        navrat.add(marker)
        //Log.d(TAG, "RECEIVED: ${message.Text}")

        writer.close()
        reader.close()
        socket.close()

        return@withContext navrat
    }

    override fun sendRecord(marker: MapMarker) {
        //TODO("Not yet implemented")
    }

}