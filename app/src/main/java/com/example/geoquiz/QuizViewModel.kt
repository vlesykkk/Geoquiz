package com.example.geoquiz

import androidx.lifecycle.ViewModel

/**
 * Holds all quiz state so it survives configuration changes (screen rotation).
 */
class QuizViewModel : ViewModel() {

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, false)
    )

    val questionCount: Int
        get() = questionBank.size

    var currentIndex = 0
        private set

    // Challenge: Tracking Cheat Status by Question (per-question, not a single global flag)
    private val cheatedQuestions = BooleanArray(questionBank.size)

    // Tracks which questions have been answered, so score is only counted once each
    private val answeredQuestions = BooleanArray(questionBank.size)

    var correctAnswersCount = 0
        private set

    // Challenge: Limited Cheats — a shared pool of cheats for the whole quiz
    var cheatsRemaining = MAX_CHEATS
        private set

    val currentQuestionTextResId: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val isCurrentQuestionCheated: Boolean
        get() = cheatedQuestions[currentIndex]

    val isCurrentQuestionAnswered: Boolean
        get() = answeredQuestions[currentIndex]

    val allQuestionsAnswered: Boolean
        get() = answeredQuestions.all { it }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrevious() {
        currentIndex = if (currentIndex == 0) questionBank.size - 1 else currentIndex - 1
    }

    /** Call after CheatActivity confirms the answer was actually revealed. */
    fun useCheatOnCurrentQuestion() {
        if (cheatsRemaining > 0) {
            cheatsRemaining--
            cheatedQuestions[currentIndex] = true
        }
    }

    fun answerCurrentQuestion(userAnswer: Boolean) {
        if (answeredQuestions[currentIndex]) return
        answeredQuestions[currentIndex] = true
        // A cheated question never counts as a correct answer, no matter what was tapped
        if (!cheatedQuestions[currentIndex] && userAnswer == currentQuestionAnswer) {
            correctAnswersCount++
        }
    }

    companion object {
        private const val MAX_CHEATS = 3
    }
}
