package com.example.musicalquiz.local

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Int,
    val trackId: Long
)

