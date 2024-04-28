package com.example.animalobserving.ui.theme.networkCommunication

import android.content.ContentValues.TAG
import android.util.Log
import com.google.gson.Gson
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.Calendar
import kotlin.concurrent.thread

class NetworkClient(private val serverIp: String, private val serverPort: Int) {
    private var _clientSocket: Socket? = null
    fun start(onMessageReceived: (Message) -> Unit) {
        Thread(Runnable {
            try {
                _clientSocket = Socket(serverIp, serverPort)
                val serverEndpoint = InetSocketAddress(serverIp, serverPort)
                _clientSocket!!.connect(serverEndpoint)
                val receiveThread = thread(start = true) {
                    listen(onMessageReceived)
                }
            } catch (e: Exception) {
                Log.d(TAG, "DEB: ${e.toString()}")
            }
        }).start()
    }

    fun stop() {
        _clientSocket?.close()
    }

    fun sendMessage(message: Message) {
        try {
            val jsonString = Gson().toJson(message)
            val messageBytes = jsonString.toByteArray(Charsets.US_ASCII)
            if (_clientSocket != null) {
                val outputStream = _clientSocket!!.getOutputStream()
                outputStream.write(messageBytes)
            }
        } catch (e: Exception) {
            Log.d(TAG, "CHYBA: ${e.toString()}")
        }
    }

    fun requestMarkers(lat1: Double, lng1: Double, lat2: Double, lng2: Double) {
        val message = Message ("$lat1;$lng1;$lat2;$lng2", Calendar.getInstance().time, "", MessageType.RequestMarkers)
        sendMessage(message)
    }

    private fun listen(onMessageReceived: (Message) -> Unit) {
        if (_clientSocket == null) {
            return
        }
        while (true) {
            try {
                val bufferSize = 96000
                val buffer = ByteArray(bufferSize)
                val inputStream = _clientSocket!!.getInputStream()
                val bytesRead = receiveData(inputStream, buffer)
                if (bytesRead > 0)
                {
                    val jsonString = String(buffer, 0, bytesRead, StandardCharsets.US_ASCII)
                    val pattern : String = """\{.*?\}"""
                    val regex = Regex(pattern)
                    val matches = regex.findAll(jsonString)
                    for (match in matches) {
                        val matchValue = match.value
                        val receivedMessage: Message = Gson().fromJson(matchValue, Message::class.java)
                        if (receivedMessage != null) {
                            //Do smth
                            onMessageReceived(receivedMessage)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "DEB: ${e.toString()}")
                _clientSocket = null;
                break;
            }
        }
    }

    private fun receiveData(inputStream: InputStream, buffer: ByteArray): Int {
        return inputStream.read(buffer)
    }
}
