package com.example.musicalquiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquiz.local.TrackEntity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.AlertDialog
import com.example.musicalquiz.local.PlaylistWithTracks
import com.example.musicalquiz.TrackAdapter



class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()
    private val playlistViewModel by viewModels<PlaylistViewModel>()
    private lateinit var adapter: TrackAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etSearchQuery = view.findViewById<EditText>(R.id.etSearchQuery)
        val btnSearch = view.findViewById<Button>(R.id.btnSearch)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTracks)

        adapter = TrackAdapter(
            emptyList(),
            onItemClick = { track ->
                val bundle = Bundle().apply {
                    putString("title", track.title)
                    putString("artist", track.artist.name)
                    putString("preview", track.preview)
                    putString("cover", track.album.coverBig)
                }
                findNavController().navigate(R.id.detailsFragment, bundle)
            },
            onItemLongClick = { track ->
                showPlaylistPickerDialog(track)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnSearch.setOnClickListener {
            val query = etSearchQuery.text.toString()
            if (query.isNotEmpty()) {
                viewModel.search(query)
            }
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateData(tracks)
        }
    }

    private fun showPlaylistPickerDialog(track: Track) {
        viewLifecycleOwner.lifecycleScope.launch {
            playlistViewModel.playlistsWithTracksFlow.collect { playlists ->
                if (playlists.isEmpty()) {
                    Toast.makeText(requireContext(), "No playlists available. Create one first.", Toast.LENGTH_SHORT).show()
                    return@collect
                }
                showPlaylistDialog(track, playlists)
            }
        }
    }

    private fun showPlaylistDialog(track: Track, playlists: List<PlaylistWithTracks>) {
        val playlistNames = playlists.map { it.playlist.name }
        val fullList = playlistNames + "➕ Create New Playlist"

        AlertDialog.Builder(requireContext())
            .setTitle("Select Playlist")
            .setItems(fullList.toTypedArray()) { _, which ->
                if (which == playlistNames.size) {
                    showCreatePlaylistDialog(track)
                } else {
                    val selectedPlaylistId = playlists[which].playlist.playlistId
                    val trackEntity = TrackEntity(
                        trackId = track.id,
                        title = track.title,
                        artist = track.artist.name,
                        preview = track.preview,
                        coverMedium = track.album.coverMedium

                    )
                    val existingTracks = playlists[which].tracks
                    val isDuplicate = existingTracks.any { it.trackId == trackEntity.trackId }

                    if (isDuplicate) {
                        Toast.makeText(requireContext(), "Track already in playlist!", Toast.LENGTH_SHORT).show()
                    } else {
                        playlistViewModel.addTrackToPlaylist(selectedPlaylistId, trackEntity)
                        Toast.makeText(requireContext(), "Track added to ${playlists[which].playlist.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCreatePlaylistDialog(track: Track) {
        val input = EditText(requireContext())
        input.hint = "My New Playlist"

        AlertDialog.Builder(requireContext())
            .setTitle("Create New Playlist")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString()
                if (name.isNotBlank()) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        playlistViewModel.createPlaylist(name)
                        delay(200)
                        val playlists = playlistViewModel.playlistsWithTracksFlow.value
                        val created = playlists.find { it.playlist.name == name }

                        if (created != null) {
                            val trackEntity = TrackEntity(
                                trackId = track.id,
                                title = track.title,
                                artist = track.artist.name,
                                preview = track.preview,
                                coverMedium = track.album.coverMedium
                            )
                            playlistViewModel.addTrackToPlaylist(created.playlist.playlistId, trackEntity)
                            Toast.makeText(requireContext(), "Playlist '$name' created and track added!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Created playlist, but couldn't add track.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
