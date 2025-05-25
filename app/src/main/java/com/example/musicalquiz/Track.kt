package com.example.musicalquiz

import com.google.gson.annotations.SerializedName

data class Track(
    val id: Long,
    val title: String,
    val preview: String,
    val artist: Artist,
    val album: Album
)

data class Artist(
    val name: String
)

data class Album(
    @SerializedName("cover")
    val cover: String,

    @SerializedName("cover_big")
    val coverBig: String,

    @SerializedName("cover_medium")
    val coverMedium: String,

    @SerializedName("cover_small")
    val coverSmall: String,

    @SerializedName("cover_xl")
    val coverXl: String
)
