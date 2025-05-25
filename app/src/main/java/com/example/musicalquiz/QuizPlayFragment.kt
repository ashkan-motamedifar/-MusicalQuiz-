package com.example.musicalquiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.musicalquiz.local.QuizType
import com.example.musicalquiz.local.QuizViewModel

class QuizPlayFragment : Fragment(R.layout.fragment_quiz_play) {

    private val vm: QuizViewModel by viewModels()

    private lateinit var ivCover: ImageView
    private lateinit var tvInstruction: TextView
    private lateinit var tvQuestionNum: TextView
    private lateinit var tvScore: TextView
    private lateinit var btns: List<Button>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = QuizPlayFragmentArgs.fromBundle(requireArguments())
        val quizType = QuizType.valueOf(args.quizType)

        ivCover = view.findViewById(R.id.ivQuizCover)
        tvInstruction = view.findViewById(R.id.tvQuizInstruction)
        tvQuestionNum = view.findViewById(R.id.tvQuizNumber)
        tvScore = view.findViewById(R.id.tvQuizScore)
        btns = listOf(
            view.findViewById(R.id.btnChoice1),
            view.findViewById(R.id.btnChoice2),
            view.findViewById(R.id.btnChoice3),
            view.findViewById(R.id.btnChoice4)
        )

        tvInstruction.text = when (quizType) {
            QuizType.TITLE -> "🎵 Guess the song title from the album cover"
            QuizType.ARTIST -> "👤 Guess the artist of this track"
            QuizType.ALBUM -> "💿 Guess the album this track belongs to"
        }

        vm.currentTrack.observe(viewLifecycleOwner) { track ->
            if (track != null) {
                Glide.with(this).load(track.coverMedium).into(ivCover)
                vm.playPreview(track.preview)
                val choices = vm.getChoices()
                btns.forEachIndexed { i, b ->
                    b.text = choices.getOrNull(i) ?: "N/A"
                    b.isEnabled = true
                }
            }
        }

        vm.questionNumber.observe(viewLifecycleOwner) { num ->
            val total = vm.totalQuestions.value ?: 0
            tvQuestionNum.text = "Q$num / $total"
        }

        vm.score.observe(viewLifecycleOwner) { sc ->
            tvScore.text = "Score: $sc"
        }

        vm.quizCompleted.observe(viewLifecycleOwner) { done ->
            if (done) {
                if (vm.totalQuestions.value == 0) {
                    Toast.makeText(requireContext(), "Not enough songs to start quiz!", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                } else {
                    Toast.makeText(requireContext(), "🎉 Quiz Complete!", Toast.LENGTH_LONG).show()
                    btns.forEach { it.isEnabled = false }
                }
            }
        }

        btns.forEach { b ->
            b.setOnClickListener {
                vm.submitAnswer(b.text.toString())
            }
        }

        vm.startQuiz(args.playlistId, maxQuestions = 5, type = quizType)
    }
}
