package com.example.animalobserving.ui.theme.networkCommunication

import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.util.regex.Pattern

class NetworkClient(private val serverIp: String, private val serverPort: Int) {
    private var clientSocket: Socket = Socket()
    fun start() {
        val serverEndPoint = InetSocketAddress(serverIp, serverPort)
        try {
            clientSocket.connect(serverEndPoint)

            val receiveThread = Thread { listen() }
            receiveThread.start()
        } catch (ex: IOException) {
            println(ex.message)
        }
    }
    fun stop() {
        clientSocket.close()
    }
    fun requestMarkers(lat1: Double, lng1: Double, lat2: Double, lng2: Double) {
        val newMessage = Message("$lat1;$lng1;$lat2;$lng2", System.currentTimeMillis(), "", MessageType.RequestMarkers)
    }

    private fun sendMessage(message: Message) {
        try {
            val messageBytes = message.toJsonString().toByteArray(StandardCharsets.UTF_8)
            clientSocket?.outputStream?.write(messageBytes)
        } catch (ex: IOException) {
            println("Error: ${ex.message}")
        }
    }

    private fun listen() {
//        val clientSocket = clientSocket ?: return
//        val buffer = ByteArray(96000)
//        while (true) {
//            try {
//                val bytesRead = clientSocket.inputStream.read(buffer)
//                if (bytesRead > 0) {
//                    val jsonString = String(buffer, 0, bytesRead, StandardCharsets.UTF_8)
//                    val pattern = "\\{.*?\\}"
//                    val regex = Pattern.compile(pattern)
//                    val matcher = regex.matcher(jsonString)
//                    while (matcher.find()) {
//                        val receivedMessage = Message.fromJson(matcher.group())
//                        println("RECEIVED: $receivedMessage")
//                    }
//                }
//            } catch (ex: IOException) {
//                this.clientSocket = null
//                break
//            }
//        }
    }
}
