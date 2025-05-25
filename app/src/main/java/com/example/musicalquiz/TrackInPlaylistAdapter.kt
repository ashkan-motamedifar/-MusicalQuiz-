package com.example.musicalquiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquiz.local.TrackEntity

class TrackInPlaylistAdapter(
    private var trackList: List<TrackEntity>,
    private val playlistId: Int,
    private val onDeleteTrackClick: (trackId: Long, playlistId: Int) -> Unit,
    private val onPlayClick: (previewUrl: String) -> Unit
) : RecyclerView.Adapter<TrackInPlaylistAdapter.TrackViewHolder>() {

    fun updateTracks(newTracks: List<TrackEntity>) {
        this.trackList = newTracks
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val tvArtist: TextView = itemView.findViewById(R.id.tvArtist)
        private val btnDeleteTrack: Button = itemView.findViewById(R.id.btnDeleteTrack)
        private val btnPlayTrack: Button = itemView.findViewById(R.id.btnPlayTrack)

        fun bind(track: TrackEntity) {
            tvTrackName.text = track.title
            tvArtist.text = track.artist

            btnDeleteTrack.setOnClickListener {
                onDeleteTrackClick(track.trackId, playlistId)
            }

            btnPlayTrack.setOnClickListener {
                onPlayClick(track.preview)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track_in_playlist, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount() = trackList.size
}
