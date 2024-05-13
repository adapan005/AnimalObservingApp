package com.example.animalobserving.data

import com.example.animalobserving.data.markers.MapMarkersRepository
import com.example.animalobserving.data.markers.NetworkMapMarkersRepository
import com.example.animalobserving.data.species.NetworkSpeciesRepository
import com.example.animalobserving.data.species.SpeciesRepository
import com.example.animalobserving.network.SocketMapMarkerApiService
import com.example.animalobserving.network.SocketMapMarkerApiServiceImpl
import com.example.animalobserving.network.SocketSpeciesApiService
import com.example.animalobserving.network.SocketSpeciesApiServiceImpl

interface AppContainer {
    val mapMarkersRepository: MapMarkersRepository
    val speciesRepository: SpeciesRepository
}

class DefaultAppContainer : AppContainer {

    private val socketMapMarkerApiService: SocketMapMarkerApiService by lazy {
        SocketMapMarkerApiServiceImpl()
    }

    private val socketSpeciesApiService: SocketSpeciesApiService by lazy {
        SocketSpeciesApiServiceImpl()
    }

    override val mapMarkersRepository: MapMarkersRepository by lazy {
        NetworkMapMarkersRepository(socketMapMarkerApiService)
    }

    override val speciesRepository: SpeciesRepository by lazy {
        NetworkSpeciesRepository(socketSpeciesApiService)
    }
}