package com.example.animalobserving.ui.theme.networkCommunication

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class MessageType {
    Informative,
    RequestMarkers,
    RequestDetailedMarker,
    MapMarkerInfo
}

class Message(val text: String, val date: Long, val senderName: String, val messageType: MessageType = MessageType.Informative) {

    fun toJsonString(): String {
        throw NotImplementedError()
    }

    override fun toString(): String {
        throw NotImplementedError()
    }
}