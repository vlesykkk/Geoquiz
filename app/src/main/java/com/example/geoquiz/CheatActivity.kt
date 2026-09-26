package com.example.geoquiz

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
private const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var apiLevelTextView: TextView
    private lateinit var showAnswerButton: Button
    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Challenge: Closing Loopholes for Cheaters — block screenshots and hide this
        // screen's content from the Recent Apps thumbnail so the answer can't leak
        // that way. Must be set before setContentView().
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        apiLevelTextView = findViewById(R.id.api_level_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        // Challenge: Show the Android version
        apiLevelTextView.text = getString(
            R.string.api_level_text,
            Build.VERSION.RELEASE,
            Build.VERSION.SDK_INT
        )

        // If the activity is recreated after rotation, keep the answer showing
        // and keep reporting that it was shown, rather than losing that state.
        val alreadyShown = savedInstanceState?.getBoolean(EXTRA_ANSWER_SHOWN, false) ?: false
        if (alreadyShown) {
            showAnswer()
        }

        showAnswerButton.setOnClickListener {
            showAnswer()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val isShown = answerTextView.text.isNotEmpty()
        outState.putBoolean(EXTRA_ANSWER_SHOWN, isShown)
    }

    private fun showAnswer() {
        answerTextView.text = if (answerIsTrue) {
            getString(R.string.true_button)
        } else {
            getString(R.string.false_button)
        }
        setAnswerShownResult(true)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }

        fun wasAnswerShown(result: Intent): Boolean {
            return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false)
        }
    }
}
