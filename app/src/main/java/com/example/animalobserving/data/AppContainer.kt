package com.example.animalobserving.data

import com.example.animalobserving.data.markers.MapMarkersRepository
import com.example.animalobserving.data.markers.NetworkMapMarkersRepository
import com.example.animalobserving.data.records.NetworkRecordsRepository
import com.example.animalobserving.data.records.RecordsRepository
import com.example.animalobserving.data.species.NetworkSpeciesRepository
import com.example.animalobserving.data.species.SpeciesRepository
import com.example.animalobserving.network.SocketMapMarkerApiService
import com.example.animalobserving.network.SocketMapMarkerApiServiceImpl
import com.example.animalobserving.network.SocketRecordsApiService
import com.example.animalobserving.network.SocketRecordsApiServiceImpl
import com.example.animalobserving.network.SocketSpeciesApiService
import com.example.animalobserving.network.SocketSpeciesApiServiceImpl

interface AppContainer {
    val mapMarkersRepository: MapMarkersRepository
    val speciesRepository: SpeciesRepository
    val recordsRepository: RecordsRepository
}

class DefaultAppContainer : AppContainer {
    private val SERVER_IP: String = "192.168.100.196"

    private val socketMapMarkerApiService: SocketMapMarkerApiService by lazy {
        SocketMapMarkerApiServiceImpl(SERVER_IP)
    }

    private val socketSpeciesApiService: SocketSpeciesApiService by lazy {
        SocketSpeciesApiServiceImpl(SERVER_IP)
    }

    private val socketRecordsApiService: SocketRecordsApiService by lazy {
        SocketRecordsApiServiceImpl(SERVER_IP)
    }

    override val mapMarkersRepository: MapMarkersRepository by lazy {
        NetworkMapMarkersRepository(socketMapMarkerApiService)
    }

    override val speciesRepository: SpeciesRepository by lazy {
        NetworkSpeciesRepository(socketSpeciesApiService)
    }

    override val recordsRepository: RecordsRepository by lazy {
        NetworkRecordsRepository(socketRecordsApiService)
    }

}