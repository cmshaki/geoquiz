package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.bignerdranch.android.geoquiz.R.*
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val CHEAT_INDEX = "cheat_index"
private const val REQUEST_CODE_CHEAT = 0
//private const val ANSWERS = "answers"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

   private val quizViewModel: QuizViewModel by lazy {
       ViewModelProvider(this).get(QuizViewModel::class.java)
   }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(layout.activity_main)



        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val currentIsCheater = savedInstanceState?.getBoolean(CHEAT_INDEX, false) ?: false
        val currentCheatCount = savedInstanceState?.getInt(CHEAT_COUNT_INDEX, 0) ?: 0
//        val answers = savedInstanceState?.getString(ANSWERS, "")?.toMutableList() ?: MutableList<Boolean>(0){false}
        quizViewModel.currentIndex = currentIndex
        quizViewModel.isCheater = currentIsCheater
        quizViewModel.cheatCount = currentCheatCount
//        quizViewModel.answers = answers

        trueButton = findViewById(id.true_button)
        falseButton = findViewById(id.false_button)
        nextButton = findViewById(id.next_button)
        prevButton = findViewById(id.previous_button)
        questionTextView = findViewById(id.question_text_view)
        cheatButton = findViewById(id.cheat_button)

        buttonFunctions(trueButton, true)
        buttonFunctions(falseButton, false)

        with(nextButton) {
            isEnabled = false
            setOnClickListener {
                quizViewModel.moveToNext()
                if(((quizViewModel.answersSize < quizViewModel.questionBankSize && quizViewModel.getCurrentIndex > quizViewModel.answersSize - 1) || (quizViewModel.answersSize == quizViewModel.questionBankSize && quizViewModel.getCurrentIndex == quizViewModel.questionBankSize - 1) || (quizViewModel.answersSize == quizViewModel.questionBankSize - 1)) && isEnabled) isEnabled = false
                if(!prevButton.isEnabled) prevButton.isEnabled = true
                cheatButton.isEnabled = quizViewModel.cheatCount < 3
                updateQuestion()
            }
        }
        with(prevButton) {
            isEnabled = false
            setOnClickListener {
                quizViewModel.moveToPrev()
                if (quizViewModel.getCurrentIndex == 0 && isEnabled) isEnabled = false
                if(!nextButton.isEnabled && (quizViewModel.answersSize > 0 && quizViewModel.getCurrentIndex < quizViewModel.answersSize)) nextButton.isEnabled = true
                updateQuestion()
            }
        }
        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        with(cheatButton) {
            setOnClickListener { view ->
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val options = ActivityOptions.makeClipRevealAnimation(view, 0, 0, view.width, view.height)
                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                } else {
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                }
            }
        }
        updateQuestion()
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
        cheatButton.isEnabled = false
        quizViewModel.cheatCount = quizViewModel.cheatCount + 1
        Log.d(TAG, "This is the current cheat Count ${quizViewModel.cheatCount}")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        with(savedInstanceState) {
            putInt(KEY_INDEX, quizViewModel.getCurrentIndex)
        }
        savedInstanceState.putBoolean(CHEAT_INDEX, quizViewModel.isCheater)
        savedInstanceState.putInt(CHEAT_COUNT_INDEX, quizViewModel.cheatCount)
//        savedInstanceState.putString(ANSWERS, quizViewModel.answers.toString())
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        if(quizViewModel.answersSize < quizViewModel.getCurrentIndex + 1) {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
        } else {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> string.judgment_toast
            userAnswer == correctAnswer -> string.correct_toast
            else -> string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showPercentage() {
        Toast.makeText(this, "You have got ${BigDecimal(quizViewModel.results).setScale(2, RoundingMode.HALF_EVEN)}%", Toast.LENGTH_LONG)
            .show()
    }

    private fun buttonFunctions(btn: Button, type: Boolean) {
        with(btn) {
            isEnabled = quizViewModel.answersSize < quizViewModel.getCurrentIndex + 1
            setOnClickListener {
                if (quizViewModel.answersSize < quizViewModel.getCurrentIndex + 1) quizViewModel.addAnswer(
                    type == quizViewModel.currentQuestionAnswer
                )
                isEnabled = false
                if (!type) trueButton.isEnabled = false else falseButton.isEnabled = false
                if (quizViewModel.getCurrentIndex < quizViewModel.questionBankSize - 1) nextButton.isEnabled = true
                if (quizViewModel.getCurrentIndex > 0) prevButton.isEnabled = true
                if (quizViewModel.answersSize < quizViewModel.questionBankSize) checkAnswer(type) else showPercentage()
            }
        }
    }
}
