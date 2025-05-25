package com.example.musicalquiz

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquiz.local.PlaylistWithTracks
import kotlinx.coroutines.launch

class QuizFragment : Fragment(R.layout.fragment_quiz_list) {

    private val playlistViewModel: PlaylistViewModel by viewModels()
    private lateinit var adapter: QuizPlaylistAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvQuizPlaylists)

        adapter = QuizPlaylistAdapter { playlistWithTracks: PlaylistWithTracks ->
            val trackCount = playlistWithTracks.tracks.size

            if (trackCount < 5) {
                Toast.makeText(
                    requireContext(),
                    "You need at least 5 tracks to start a quiz.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val action = QuizFragmentDirections
                    .actionQuizFragmentToQuizPlayFragment(
                        playlistWithTracks.playlist.playlistId,
                        playlistWithTracks.playlist.name
                    )
                findNavController().navigate(action)
            }
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            playlistViewModel.playlistsWithTracksFlow.collect { list ->
                adapter.submitList(list)
            }
        }
    }
}
