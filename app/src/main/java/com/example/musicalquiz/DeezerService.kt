package com.example.musicalquiz

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerService {
    @GET("search")
    suspend fun searchTracks(@Query("q") query: String): Response<DeezerResponse>
}
