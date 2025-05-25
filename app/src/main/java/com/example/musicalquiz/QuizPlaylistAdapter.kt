package com.example.musicalquiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicalquiz.local.PlaylistWithTracks


class QuizPlaylistAdapter(
    private val onClick: (PlaylistWithTracks) -> Unit
) : ListAdapter<PlaylistWithTracks, QuizPlaylistAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<PlaylistWithTracks>() {
            override fun areItemsTheSame(a: PlaylistWithTracks, b: PlaylistWithTracks): Boolean =
                a.playlist.playlistId == b.playlist.playlistId

            override fun areContentsTheSame(a: PlaylistWithTracks, b: PlaylistWithTracks): Boolean =
                a == b
        }
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvQuizPlaylistName)

        fun bind(item: PlaylistWithTracks) {
            tvName.text = item.playlist.name
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_playlist, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
