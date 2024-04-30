package com.example.animalobserving.network

import java.util.Date

enum class MsgType {
    Informative,
    RequestMarkers,
    RequestDetailedMarker,
    MapMarkerInfo
}

data class Message(val Text: String, val Date: Date, val SenderName: String, val MessageType: MsgType = MsgType.Informative) {

    fun toJsonString(): String {
        throw NotImplementedError()
    }

    override fun toString(): String {
        throw NotImplementedError()
    }
}