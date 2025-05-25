package com.example.musicalquiz.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    // ---- PLAYLIST ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Query("SELECT * FROM Playlist")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Query("SELECT * FROM Playlist WHERE name = :name LIMIT 1")
    suspend fun getPlaylistByName(name: String): Playlist?

    @Query("DELETE FROM Playlist WHERE playlistId = :id")
    suspend fun deletePlaylistById(id: Int)

    // ---- TRACK ----
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    // ---- CROSS REF ----
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrackCrossRef(ref: PlaylistTrackCrossRef)

    // ADDED: duplicate method with shorter name to match usage
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PlaylistTrackCrossRef)

    // ---- PLAYLIST + TRACKS ----
    @Transaction
    @Query("SELECT * FROM Playlist")
    fun getPlaylistsWithTracks(): Flow<List<PlaylistWithTracks>>

    @Query("DELETE FROM PlaylistTrackCrossRef WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Long)

    @Query("UPDATE Playlist SET name = :newName WHERE playlistId = :playlistId")
    suspend fun renamePlaylist(playlistId: Int, newName: String)

    @Transaction
    @Query("""
        SELECT TrackEntity.* FROM TrackEntity
        INNER JOIN PlaylistTrackCrossRef ON TrackEntity.trackId = PlaylistTrackCrossRef.trackId
        WHERE PlaylistTrackCrossRef.playlistId = :playlistId
    """)
    suspend fun getTracksForPlaylist(playlistId: Int): List<TrackEntity>
}
