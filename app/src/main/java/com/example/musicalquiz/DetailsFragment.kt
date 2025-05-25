package com.example.musicalquiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class DetailsFragment : Fragment(R.layout.fragment_details) {
    private var mediaPlayer: MediaPlayer? = null
    private var previewUrl: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the passed track details from the bundle
        val title = arguments?.getString("title") ?: "N/A"
        val artist = arguments?.getString("artist") ?: "N/A"
        previewUrl = arguments?.getString("preview")
        // For improved image quality, assume you pass a higher-res cover URL (e.g., cover_big)
        val coverUrl = arguments?.getString("cover") // ensure this key is passed from SearchFragment

        // Set up the UI elements
        val tvTitle = view.findViewById<TextView>(R.id.tvDetailsTitle)
        val tvArtist = view.findViewById<TextView>(R.id.tvDetailsArtist)
        val ivCover = view.findViewById<ImageView>(R.id.ivCover)
        val btnPlay = view.findViewById<Button>(R.id.btnPlayPreview)
        val btnBack = view.findViewById<Button>(R.id.btnBack)


        tvTitle.text = title
        tvArtist.text = artist

        // Load the image using Glide; if coverUrl is null, default to a placeholder (ic_launcher)
        Glide.with(this)
            .load(coverUrl ?: R.mipmap.ic_launcher)
            .into(ivCover)

        // Set up play preview button
        btnPlay.setOnClickListener {
            playPreview()
        }

        // Set up back navigation
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun playPreview() {
        previewUrl?.let { url ->
            // Stop and release any existing MediaPlayer instance if playing
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(url)
                    // Set up a listener so that when preparation is done, playback starts
                    setOnPreparedListener { mp ->
                        mp.start()
                    }
                    // Optional: Handle errors gracefully
                    setOnErrorListener { mp, what, extra ->
                        mp.reset()
                        true
                    }
                    // Asynchronously prepare the MediaPlayer (does not block the main thread)
                    prepareAsync()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
