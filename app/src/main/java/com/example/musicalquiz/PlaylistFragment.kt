package com.example.musicalquiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.EditText
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.AppCompatImageButton
import kotlinx.coroutines.launch
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.musicalquiz.local.TrackEntity

class PlaylistFragment : Fragment(R.layout.fragment_playlist) {

    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCreatePlaylist = view.findViewById<Button>(R.id.btnCreatePlaylist)
        val fabCreatePlaylist = view.findViewById<AppCompatImageButton>(R.id.fabCreatePlaylist)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvPlaylists)

        btnCreatePlaylist.setOnClickListener {
            showCreatePlaylistDialog()
        }

        fabCreatePlaylist.setOnClickListener {
            Toast.makeText(requireContext(), "FAB Clicked", Toast.LENGTH_SHORT).show()
            showCreatePlaylistDialog()
        }

        playlistAdapter = PlaylistAdapter(
            onDeletePlaylistClick = { playlistId ->
                playlistViewModel.deletePlaylist(playlistId)
            },
            onDeleteTrackClick = { trackId, playlistId ->
                playlistViewModel.deleteTrackFromPlaylist(playlistId, trackId)
            },
            onRenamePlaylistClick = { playlistId, currentName ->
                showRenamePlaylistDialog(playlistId, currentName)
            },
            onOpenPlaylistClick = { playlistId ->
                findNavController().navigate(
                    PlaylistFragmentDirections.actionPlaylistFragmentToPlaylistDetailsFragment(playlistId)
                )
            }
        )

        recyclerView.adapter = playlistAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            playlistViewModel.playlistsWithTracksFlow.collect { playlists ->
                playlistAdapter.submitList(playlists.distinctBy { it.playlist.playlistId })
            }
        }
    }

    private fun showCreatePlaylistDialog() {
        val editText = EditText(requireContext())

        AlertDialog.Builder(requireContext())
            .setTitle("Enter Playlist Name")
            .setView(editText)
            .setPositiveButton("Create") { _, _ ->
                val playlistName = editText.text.toString()
                if (playlistName.isNotBlank()) {
                    playlistViewModel.createPlaylist(playlistName)
                    Toast.makeText(requireContext(), "Playlist Created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Playlist name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddTrackToPlaylistDialog(playlistId: Int) {
        val tracks = getAvailableTracks()
        val trackNames = tracks.map { it.title }

        AlertDialog.Builder(requireContext())
            .setTitle("Select a Track to Add")
            .setItems(trackNames.toTypedArray()) { _, which ->
                val selectedTrack = tracks[which]
                val trackEntity = TrackEntity(
                    trackId = selectedTrack.id,
                    title = selectedTrack.title,
                    artist = selectedTrack.artist.name,
                    album = "Unknown Album",  // No album title available in Album model
                    preview = selectedTrack.preview,
                    coverMedium = selectedTrack.album.coverMedium
                )

                playlistViewModel.addTrackToPlaylist(playlistId, trackEntity)
                Toast.makeText(requireContext(), "Track Added", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showRenamePlaylistDialog(playlistId: Int, currentName: String) {
        val input = EditText(requireContext()).apply {
            setText(currentName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Rename Playlist")
            .setView(input)
            .setPositiveButton("Rename") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotBlank()) {
                    playlistViewModel.renamePlaylist(playlistId, newName)
                    Toast.makeText(requireContext(), "Playlist renamed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getAvailableTracks(): List<Track> {
        return listOf(
            Track(
                id = 1L,
                title = "Song A",
                preview = "",
                artist = Artist("Artist A"),
                album = Album("", "", "", "", "")
            ),
            Track(
                id = 2L,
                title = "Song B",
                preview = "",
                artist = Artist("Artist B"),
                album = Album("", "", "", "", "")
            )
        )
    }
}
