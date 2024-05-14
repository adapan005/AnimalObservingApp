package com.example.animalobserving.data.species

import com.example.animalobserving.network.SocketSpeciesApiService

interface SpeciesRepository {
    suspend fun getSpecies(): List<Specie>
    suspend fun submitNewRecord(value: String)
}

class NetworkSpeciesRepository ( private val socketSpeciesApiService: SocketSpeciesApiService) : SpeciesRepository {
    override suspend fun getSpecies(): List<Specie> = socketSpeciesApiService.getSpecies()
    override suspend fun submitNewRecord(value: String) {
        socketSpeciesApiService.submitNewRecord(value)
    }
}