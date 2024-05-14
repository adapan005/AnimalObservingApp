package com.example.animalobserving.data.records

import com.example.animalobserving.network.SocketRecordsApiService

interface RecordsRepository {
    suspend fun getDetailedRecord(recordId: Int): DetailedRecord
}

class NetworkRecordsRepository (private val socketRecordsApiService: SocketRecordsApiService) : RecordsRepository {
    override suspend fun getDetailedRecord(recordId: Int): DetailedRecord {
        return socketRecordsApiService.getDetailedRecord(recordId)
    }
}