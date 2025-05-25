package com.example.musicalquiz

class DeezerRepository {
    suspend fun searchTracks(query: String): DeezerResponse? {
        val response = RetrofitInstance.api.searchTracks(query)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}
