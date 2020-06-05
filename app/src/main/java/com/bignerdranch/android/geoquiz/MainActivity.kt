package com.bignerdranch.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.view.View
import android.widget.ImageButton
import com.bignerdranch.android.geoquiz.R.*

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var questionTextView: TextView

    private val questionBank = listOf(
            Question(string.question_australia, true),
            Question(string.question_oceans, true),
            Question(string.question_mideast, false),
            Question(string.question_africa, false),
            Question(string.question_americas, true),
            Question(string.question_asia, true)
    )

    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        trueButton = findViewById(id.true_button)
        falseButton = findViewById(id.false_button)
        nextButton = findViewById(id.next_button)
        previousButton = findViewById(id.previous_button)
        questionTextView = findViewById(id.question_text_view)


        trueButton.setOnClickListener {
            view: View -> checkAnswer(true)
        }
        falseButton.setOnClickListener {
             view: View -> checkAnswer(false)
        }
        nextButton.setOnClickListener {
            currentIndex = (if(currentIndex < questionBank.size - 1) currentIndex + 1 else currentIndex) % questionBank.size
            updateQuestion()
        }
        previousButton.setOnClickListener {
            currentIndex = (if(currentIndex <= 1) 0 else currentIndex - 1) % questionBank.size
            updateQuestion()
        }
        questionTextView.setOnClickListener {
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }
        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = questionBank[currentIndex].textResId
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if (userAnswer == correctAnswer) {
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
            .show()
    }
    }
