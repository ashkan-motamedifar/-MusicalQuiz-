package com.example.musicalquiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicalquiz.local.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistViewModel(application: Application) : AndroidViewModel(application) {

    private val musicDao = AppDatabase.getDatabase(application).musicDao()
    private val repository = MusicRepository(musicDao)

    val playlistsWithTracksFlow = repository.getPlaylistsWithTracks()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            repository.addPlaylist(name)
        }
    }

    fun addToMyPlaylist(track: TrackEntity) {
        viewModelScope.launch {
            val playlistId = repository.ensureSinglePlaylistAndGetId()
            repository.addTrackToPlaylist(playlistId, track) //  uses new combined method
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long) {
        viewModelScope.launch {
            repository.deleteTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun renamePlaylist(playlistId: Int, newName: String) {
        viewModelScope.launch {
            repository.renamePlaylist(playlistId, newName)
        }
    }

    fun addTrackToPlaylist(playlistId: Int, track: TrackEntity) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, track) // updated method
            musicDao.insertCrossRef(
                PlaylistTrackCrossRef(
                    playlistId = playlistId,
                    trackId = track.trackId
                )
            )
        }
    }
}
