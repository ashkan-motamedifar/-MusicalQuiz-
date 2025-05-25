package com.example.musicalquiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquiz.local.PlaylistWithTracks

class PlaylistAdapter(
    private val onDeletePlaylistClick: (Int) -> Unit,
    private val onDeleteTrackClick: (trackId: Long, playlistId: Int) -> Unit,
    private val onRenamePlaylistClick: (playlistId: Int, currentName: String) -> Unit,
    private val onOpenPlaylistClick: (playlistId: Int) -> Unit
) : ListAdapter<PlaylistWithTracks, PlaylistAdapter.PlaylistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistWithTracks = getItem(position)
        holder.bind(
            playlistWithTracks,
            onDeleteTrackClick,
            onRenamePlaylistClick,
            onDeletePlaylistClick,
            onOpenPlaylistClick
        )
    }

    inner class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPlaylistName: TextView = itemView.findViewById(R.id.tvPlaylistName)
        private val tvTracks: TextView = itemView.findViewById(R.id.tvTracks)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        private val btnOpen: Button = itemView.findViewById(R.id.btnOpenPlaylist)

        fun bind(
            playlistWithTracks: PlaylistWithTracks,
            onTrackDeleteClick: (trackId: Long, playlistId: Int) -> Unit,
            onRenameClick: (playlistId: Int, name: String) -> Unit,
            onDeleteClick: (playlistId: Int) -> Unit,
            onOpenClick: (playlistId: Int) -> Unit
        ) {
            val playlist = playlistWithTracks.playlist
            val tracks = playlistWithTracks.tracks

            tvPlaylistName.text = playlist.name
            tvTracks.text = itemView.context.getString(
                R.string.track_count_placeholder, tracks.size
            )

            btnDelete.setOnClickListener {
                onDeleteClick(playlist.playlistId)
            }

            btnOpen.setOnClickListener {
                onOpenClick(playlist.playlistId)
            }

            itemView.setOnLongClickListener {
                onRenameClick(playlist.playlistId, playlist.name)
                true
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlaylistWithTracks>() {
        override fun areItemsTheSame(oldItem: PlaylistWithTracks, newItem: PlaylistWithTracks) =
            oldItem.playlist.playlistId == newItem.playlist.playlistId

        override fun areContentsTheSame(oldItem: PlaylistWithTracks, newItem: PlaylistWithTracks) =
            oldItem == newItem
    }
}
