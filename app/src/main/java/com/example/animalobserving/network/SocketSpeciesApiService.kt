package com.example.animalobserving.network

import android.content.ContentValues.TAG
import android.util.Log
import com.example.animalobserving.data.species.Specie
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.Calendar

interface SocketSpeciesApiService {
    suspend fun getSpecies(): List<Specie>
    suspend fun submitNewRecord(value: String)
}

class SocketSpeciesApiServiceImpl(private val serverIP: String) : SocketSpeciesApiService {
    override suspend fun submitNewRecord(value: String) {
        val socket = Socket(serverIP, 55557)
        socket.setSoTimeout(100)
        val writer = PrintWriter(socket.getOutputStream())
        val newMessage = Message(value, Calendar.getInstance().time, "Android client", MsgType.AddRecordWithMarker)
        writer.println(newMessage.toJsonString())
        writer.close()
        socket.close()
    }

    override suspend fun getSpecies(): List<Specie> = withContext(Dispatchers.IO) {
        var navrat: MutableList<Specie> = mutableListOf()

        val socket = Socket(serverIP, 55557)
        socket.setSoTimeout(100)
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

        ///////// Send request message to server
        val newMessage = Message("Requesting all species", Calendar.getInstance().time, "Android client", MsgType.RequestAllSpecies)
        writer.println(newMessage.toJsonString())

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
                val name = values[1]
                val specie = Specie(id, name)
                navrat.add(specie)
            }
        } while (response != null)

        writer.close()
        reader.close()
        socket.close()

        return@withContext navrat
    }
}