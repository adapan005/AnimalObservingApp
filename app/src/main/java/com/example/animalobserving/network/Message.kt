package com.example.animalobserving.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.util.Date

enum class MsgType(val number: Int) {
    Informative(number = 1),
    RequestAllMarkers(number = 2),
    RequestDetailedRecord(number = 3),
    RequestAllSpecies(number = 5),
    AddRecordWithMarker(number = 6)
}

data class Message(val Text: String, val Date: Date, val SenderName: String, val MessageType: MsgType = MsgType.Informative) {

    fun toJsonString(): String {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX")
            .registerTypeAdapter(MsgType::class.java, MsgTypeAdapter())
            .create()
        return gson.toJson(this)
    }

    override fun toString(): String {
        throw NotImplementedError()
    }
}

class MsgTypeAdapter : JsonSerializer<MsgType> {
    override fun serialize(
        src: MsgType?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src != null) {
            JsonPrimitive(src.number)
        } else {
            JsonNull.INSTANCE
        }
    }
}