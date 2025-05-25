package com.example.musicalquiz.local

import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).musicDao()

    private val _questions = MutableLiveData<List<TrackEntity>>()
    private val _currentIndex = MutableLiveData(0)
    private val _score = MutableLiveData(0)
    private val _quizCompleted = MutableLiveData(false)
    private val _quizType = MutableLiveData<QuizType>()

    val quizType: LiveData<QuizType> = _quizType
    val score: LiveData<Int> = _score
    val questionNumber: LiveData<Int> = _currentIndex.map { it + 1 }
    val totalQuestions: LiveData<Int> = _questions.map { it.size }
    val quizCompleted: LiveData<Boolean> = _quizCompleted

    val currentTrack: LiveData<TrackEntity?> = MediatorLiveData<TrackEntity?>().apply {
        addSource(_questions) { list -> value = list.getOrNull(_currentIndex.value ?: 0) }
        addSource(_currentIndex) { idx -> value = _questions.value?.getOrNull(idx) }
    }

    fun startQuiz(playlistId: Int, maxQuestions: Int = 5, type: QuizType = QuizType.TITLE) {
        viewModelScope.launch(Dispatchers.IO) {
            val all = dao.getTracksForPlaylist(playlistId)
            if (all.size < maxQuestions) {
                _questions.postValue(emptyList())
                _quizCompleted.postValue(true)
                return@launch
            }

            val shuffled = all.shuffled().take(maxQuestions)
            _quizType.postValue(type)
            _questions.postValue(shuffled)
            _currentIndex.postValue(0)
            _score.postValue(0)
            _quizCompleted.postValue(false)
        }
    }

    fun submitAnswer(answer: String) {
        val list = _questions.value ?: return
        val idx = _currentIndex.value ?: return
        if (_quizCompleted.value == true || idx >= list.size) return

        val track = list[idx]
        val correct = when (_quizType.value) {
            QuizType.TITLE -> track.title
            QuizType.ARTIST -> track.artist
            QuizType.ALBUM -> track.album
            else -> ""
        }

        if (answer == correct) {
            _score.value = (_score.value ?: 0) + 1
        }

        if (idx + 1 < list.size) {
            _currentIndex.value = idx + 1
        } else {
            _quizCompleted.value = true
        }
    }

    fun getChoices(): List<String> {
        val list = _questions.value ?: return emptyList()
        val idx = _currentIndex.value ?: return emptyList()
        if (idx >= list.size) return emptyList()

        val track = list[idx]
        val correct = when (_quizType.value) {
            QuizType.TITLE -> track.title
            QuizType.ARTIST -> track.artist
            QuizType.ALBUM -> track.album
            else -> ""
        }

        val allOptions = list.map {
            when (_quizType.value) {
                QuizType.TITLE -> it.title
                QuizType.ARTIST -> it.artist
                QuizType.ALBUM -> it.album
                else -> ""
            }
        }.filter { it != correct }.shuffled().take(3)

        return (allOptions + correct).shuffled()
    }

    private var mediaPlayer: MediaPlayer? = null

    fun playPreview(url: String) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { it.start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        mediaPlayer?.release()
        super.onCleared()
    }
}
