package com.example.animalobserving.data.markers

import com.example.animalobserving.network.SocketMapMarkerApiService

interface MapMarkersRepository {
    suspend fun getMapMarkers(): List<MapMarker>
}

class NetworkMapMarkersRepository ( private val socketMapMarkerApiService: SocketMapMarkerApiService) :
    MapMarkersRepository {
    override suspend fun getMapMarkers(): List<MapMarker> = socketMapMarkerApiService.getMarkers()
}