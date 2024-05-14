package com.example.animalobserving.data.markers

open class MapMarker(private val markerId: Int, private val latitude: Double, private val longitude: Double, private val label: String) {
    fun getLatitude(): Double {
        return latitude
    }

    fun getLongitude(): Double {
        return longitude
    }

    fun getLabel(): String {
        return label
    }

    fun getID(): Int {
        return markerId
    }
    override fun toString(): String {
        return "$markerId;$latitude;$longitude"
    }
}