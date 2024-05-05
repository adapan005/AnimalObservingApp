package com.example.animalobserving.network

import com.google.gson.Gson
import java.util.Date

enum class MsgType {
    Informative,
    RequestAllMarkers,
    RequestMarkers,
    RequestDetailedMarker,
    MapMarkerInfo
}

data class Message(val Text: String, val Date: Date, val SenderName: String, val MessageType: MsgType = MsgType.Informative) {

    fun toJsonString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    override fun toString(): String {
        throw NotImplementedError()
    }
}