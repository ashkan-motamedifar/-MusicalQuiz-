package com.example.musicalquiz.local

import android.util.Log
import kotlinx.coroutines.flow.Flow

class MusicRepository(private val dao: MusicDao) {

    suspend fun ensureSinglePlaylistAndGetId(): Int {
        val name = "My Favorite Tracks"
        val existing = dao.getPlaylistByName(name)
        return if (existing != null) {
            existing.playlistId
        } else {
            dao.insertPlaylist(Playlist(name = name)).toInt()
        }
    }

    suspend fun addPlaylist(name: String) {
        val existing = dao.getPlaylistByName(name)
        if (existing == null) {
            dao.insertPlaylist(Playlist(name = name))
        }
    }

    //  Combined insert + cross-ref
    suspend fun addTrackToPlaylist(playlistId: Int, track: TrackEntity) {
        dao.insertTrack(track)
        dao.insertPlaylistTrackCrossRef(
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = track.trackId)
        )

        //  Debug log
        val tracks = dao.getTracksForPlaylist(playlistId)
        Log.d("PlaylistTracks", "Playlist $playlistId now has ${tracks.size} tracks: $tracks")
    }

    suspend fun deletePlaylist(id: Int) {
        dao.deletePlaylistById(id)
    }

    fun getAllPlaylists(): Flow<List<Playlist>> = dao.getAllPlaylists()

    fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>> = dao.getPlaylistsWithTracks()

    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long) {
        dao.deleteTrackFromPlaylist(playlistId, trackId)
    }

    suspend fun getPlaylistByName(name: String): Playlist? {
        return dao.getPlaylistByName(name)
    }

    suspend fun renamePlaylist(playlistId: Int, newName: String) {
        dao.renamePlaylist(playlistId, newName)
    }
}
