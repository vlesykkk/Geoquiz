package com.example.geoquiz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this)[QuizViewModel::class.java]
    }

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            if (data != null && CheatActivity.wasAnswerShown(data)) {
                quizViewModel.useCheatOnCurrentQuestion()
                updateCheatButton()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionTextView = findViewById(R.id.question_text_view)
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        cheatButton = findViewById(R.id.cheat_button)

        trueButton.setOnClickListener { checkAnswer(true) }
        falseButton.setOnClickListener { checkAnswer(false) }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }

        cheatButton.setOnClickListener {
            if (quizViewModel.cheatsRemaining > 0) {
                val intent = CheatActivity.newIntent(
                    this@MainActivity,
                    quizViewModel.currentQuestionAnswer
                )
                cheatLauncher.launch(intent)
            } else {
                // Challenge: Limited Cheats
                Toast.makeText(this, R.string.no_cheats_toast, Toast.LENGTH_SHORT).show()
            }
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        questionTextView.setText(quizViewModel.currentQuestionTextResId)
        updateCheatButton()
    }

    private fun updateCheatButton() {
        val remaining = quizViewModel.cheatsRemaining
        cheatButton.isEnabled = remaining > 0
        cheatButton.text = if (remaining > 0) {
            getString(R.string.cheat_button, remaining)
        } else {
            getString(R.string.cheat_button_none_left)
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val wasAlreadyAnswered = quizViewModel.isCurrentQuestionAnswered

        val messageResId = when {
            // Challenge: Tracking Cheat Status by Question — judged on this
            // question's own cheat history, not a single shared flag
            quizViewModel.isCurrentQuestionCheated -> R.string.judgment_toast
            userAnswer == quizViewModel.currentQuestionAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        quizViewModel.answerCurrentQuestion(userAnswer)

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        if (!wasAlreadyAnswered && quizViewModel.allQuestionsAnswered) {
            showFinalScore()
        }
    }

    private fun showFinalScore() {
        val percent = (quizViewModel.correctAnswersCount * 100) / quizViewModel.questionCount
        Toast.makeText(this, getString(R.string.score_toast, percent), Toast.LENGTH_LONG).show()
    }
}
