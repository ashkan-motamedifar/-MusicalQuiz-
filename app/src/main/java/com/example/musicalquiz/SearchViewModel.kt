package com.example.musicalquiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val repository = DeezerRepository()

    // LiveData for the list of tracks
    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> get() = _tracks

    fun search(query: String) {
        viewModelScope.launch {
            val response = repository.searchTracks(query)
            _tracks.value = response?.data ?: emptyList()
        }
    }
}
