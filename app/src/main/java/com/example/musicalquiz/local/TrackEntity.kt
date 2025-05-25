package com.example.musicalquiz.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrackEntity(
    @PrimaryKey val trackId: Long,
    val title: String,
    val artist: String,
    val album: String = "",         // Default in case album is missing
    val preview: String,
    val coverMedium: String = ""    // Default to avoid null crashes
)
