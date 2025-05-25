package com.example.musicalquiz

import com.google.gson.annotations.SerializedName

data class DeezerTrack(
    val id: Int,
    val title: String,
    val preview: String,
    val artist: DeezerArtist,
    val album: DeezerAlbum
)

data class DeezerArtist(
    val name: String
)

data class DeezerAlbum(
    @SerializedName("cover_medium")
    val coverMedium: String
)
