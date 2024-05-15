package com.example.animalobserving.network

import com.example.animalobserving.data.records.DetailedRecord
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Calendar

interface SocketRecordsApiService {
    suspend fun getDetailedRecord(recordId: Int): DetailedRecord
}

class SocketRecordsApiServiceImpl(private val serverIP: String) : SocketRecordsApiService {
    override suspend fun getDetailedRecord(recordId: Int): DetailedRecord = withContext(Dispatchers.IO) {
        var navrat: DetailedRecord = DetailedRecord(-1, 0.0, 0.0, "","","", Calendar.getInstance().time)

        val socket = Socket(serverIP, 55557)
        socket.setSoTimeout(100)
        val writer = PrintWriter(socket.getOutputStream(), true)
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

        val newMessage = Message("$recordId", Calendar.getInstance().time, "Android Client", MsgType.RequestDetailedRecord)
        writer.println(newMessage.toJsonString())

        var response: String? = null

        try {
            response = reader.readLine()
        } catch (e: Exception) {
            //TODO("NOT IMPLEMENTED YET")
        }
        if (response != null) {
            val message = Gson().fromJson(response, Message::class.java)
            val values = message.Text.split(";").toTypedArray()
            // Zobrazovanie mapy zatial nie je hotove...
            //val markerID
            val lat = values[1].replace(',', '.').toDouble()
            val lon = values[2].replace(',', '.').toDouble()

            //markerID, lat, lon, long, recordLabel, speciesID, date, description
            val name = values[3]
            val description = values[6]
            val speciesName = values[4]

            val dateFormat = SimpleDateFormat("dd. M. yyyy H:mm:ss")
            val recordDate = dateFormat.parse(values[5])
            navrat = DetailedRecord(-1, lat, lon, name, speciesName, description, recordDate)
        }
        writer.close()
        reader.close()
        socket.close()

        return@withContext navrat
    }

}