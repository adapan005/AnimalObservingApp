package com.example.animalobserving.data.records

import com.example.animalobserving.data.markers.MapMarker
import java.util.Date

class DetailedRecord(markerId: Int, latitude: Double, longitude: Double, label: String, private val specie: String, private val description: String, private val date: Date?) : MapMarker(markerId, latitude,
    longitude,
    label
) {
    fun getSpecieName(): String {
        return specie
    }

    fun getDescription(): String {
        return description
    }

    fun getDate(): String {
        return date.toString()
    }
}