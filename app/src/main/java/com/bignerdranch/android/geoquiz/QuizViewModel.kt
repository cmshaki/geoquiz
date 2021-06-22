package com.bignerdranch.android.geoquiz

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
private const val ANSWERS_KEY = "answers"
private const val CURRENT_INDEX_KEY = "current_index"
private const val IS_CHEATER_KEY = "is_cheater"
private const val CHEAT_COUNT_KEY = "cheat_count"

class QuizViewModel(state: SavedStateHandle): ViewModel() {
    private val savedStateHandle = state
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    fun saveAnswer(ans: Boolean) {
        val answers = getAnswers
        answers.add(getCurrentIndex, ans)
        savedStateHandle.set(ANSWERS_KEY, answers)
    }
    private val getAnswers: ArrayList<Any>
        get() = savedStateHandle.get(ANSWERS_KEY)?: ArrayList<Any>()
    val getAnswersSize: Int
        get() {
            val answers = getAnswers
            if(answers != null) {
                return answers.size
            }
            return 0
        }
    val getCurrentIndex: Int
        get() = savedStateHandle.get(CURRENT_INDEX_KEY)?: 0
    fun saveIsCheater(hasCheated: Boolean) {
        savedStateHandle.set(IS_CHEATER_KEY, hasCheated)
    }
    val getIsCheater: Boolean
        get() = savedStateHandle.get(IS_CHEATER_KEY)?: false
    fun saveCheatCount(cheatCount: Int) {
        savedStateHandle.set(CHEAT_COUNT_KEY, cheatCount)
    }
    val getCheatCount: Int
        get() = savedStateHandle.get(CHEAT_COUNT_KEY)?: 0
    val questionBankSize: Int
        get() = questionBank.size
    val results: Double
        get() {
            val answers = getAnswers
            return (if(answers.contains(true)) answers.count{it === true} else 0).toDouble() / answers.size * 100
        }
    val currentQuestionAnswer: Boolean
        get() = questionBank[getCurrentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[getCurrentIndex].textResId
    fun moveToNext() {
        val curIndex = getCurrentIndex
        val currentIndex = if (curIndex < getAnswers.size && curIndex < questionBank.size - 1) curIndex + 1 else curIndex
        savedStateHandle.set(CURRENT_INDEX_KEY, currentIndex)
    }
    fun moveToPrev() {
        val curIndex = getCurrentIndex
        val currentIndex = if (curIndex > 0) curIndex - 1 else curIndex
        savedStateHandle.set(CURRENT_INDEX_KEY, currentIndex)
    }
}