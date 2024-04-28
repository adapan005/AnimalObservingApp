package com.example.animalobserving.ui.theme.networkCommunication

import java.util.Date

enum class MessageType {
    Informative,
    RequestMarkers,
    RequestDetailedMarker,
    MapMarkerInfo
}

data class Message(val text: String, val date: Date, val senderName: String, val messageType: MessageType = MessageType.Informative) {

    fun toJsonString(): String {
        throw NotImplementedError()
    }

    override fun toString(): String {
        throw NotImplementedError()
    }
}