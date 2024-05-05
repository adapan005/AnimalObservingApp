package com.example.animalobserving.data

import com.example.animalobserving.network.SocketMapMarkerApiService
import com.example.animalobserving.network.SocketMapMarkerApiServiceImpl
import java.net.Socket

interface AppContainer {
    val mapMarkersRepository: MapMarkersRepository
}


class DefaultAppContainer : AppContainer {

    // Create a singleton instance of the SocketMapMarkerApiService implementation
    private val socketMapMarkerApiService: SocketMapMarkerApiService by lazy {
        SocketMapMarkerApiServiceImpl() // You need to provide the actual implementation here
    }

    // Provide the implementation for the mapMarkersRepository property
    override val mapMarkersRepository: MapMarkersRepository by lazy {
        NetworkMapMarkersRepository(socketMapMarkerApiService)
    }
}