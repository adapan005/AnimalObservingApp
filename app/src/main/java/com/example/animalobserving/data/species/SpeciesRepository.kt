package com.example.animalobserving.data.species

import com.example.animalobserving.network.SocketSpeciesApiService

interface SpeciesRepository {
    suspend fun getSpecies(): List<Specie>
}

class NetworkSpeciesRepository ( private val socketSpeciesApiService: SocketSpeciesApiService) :
    SpeciesRepository {
    override suspend fun getSpecies(): List<Specie> = socketSpeciesApiService.getSpecies()
}