package com.bignerdranch.android.geoquiz

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel: ViewModel() {
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var answers = MutableList<Any>(0){false}
    var currentIndex = 0
    var isCheater = false
    var cheatCount = 0

    val getCurrentIndex: Int
        get() = currentIndex

    val questionBankSize: Int
        get() = questionBank.size

    val answersSize: Int
        get() = answers.size

    fun addAnswer(ans: Boolean) {
        answers.add(currentIndex, ans)
    }

    val results: Double
        get() = (if(answers.contains(true)) answers.count{it === true} else 0).toDouble() / answers.size * 100

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = if (currentIndex < answers.size && currentIndex < questionBank.size - 1) currentIndex + 1 else currentIndex
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex > 0) currentIndex - 1 else currentIndex
    }
}