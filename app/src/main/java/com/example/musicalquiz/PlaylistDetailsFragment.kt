package com.example.musicalquiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistDetailsFragment : Fragment(R.layout.fragment_playlist_details) {

    private val playlistViewModel: PlaylistViewModel by viewModels()
    private val args: PlaylistDetailsFragmentArgs by navArgs()
    private lateinit var trackAdapter: TrackInPlaylistAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlaylistTracks)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        trackAdapter = TrackInPlaylistAdapter(
            emptyList(),
            args.playlistId,
            onDeleteTrackClick = { trackId, playlistId ->
                playlistViewModel.deleteTrackFromPlaylist(playlistId, trackId)
                Toast.makeText(requireContext(), "Track deleted", Toast.LENGTH_SHORT).show()
            },
            onPlayClick = { previewUrl ->
                playPreview(previewUrl)
            }
        )

        recyclerView.adapter = trackAdapter

        lifecycleScope.launch {
            playlistViewModel.playlistsWithTracksFlow.collectLatest { playlists ->
                val playlist = playlists.find { it.playlist.playlistId == args.playlistId }
                playlist?.let {
                    trackAdapter.updateTracks(it.tracks)
                }
            }
        }
    }

    private fun playPreview(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
