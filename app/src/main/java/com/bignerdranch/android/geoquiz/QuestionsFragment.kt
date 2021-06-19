package com.bignerdranch.android.geoquiz

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.math.BigDecimal
import java.math.RoundingMode

private const val TAG = "Questions Fragment"

class QuestionsFragment: Fragment() {
    interface Callbacks {
        fun onCheatButtonClicked(cheatAnswer: Boolean)
    }

    private var callbacks: Callbacks? = null

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var cheatButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    private lateinit var quizViewModel: QuizViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quizViewModel = activity.let { ViewModelProvider(requireActivity()).get(QuizViewModel::class.java) }
        val view = inflater.inflate(R.layout.fragment_questions, container, false)
        trueButton = view.findViewById(R.id.true_button)
        falseButton = view.findViewById(R.id.false_button)
        nextButton = view.findViewById(R.id.next_button)
        prevButton = view.findViewById(R.id.previous_button)
        questionTextView = view.findViewById(R.id.question_text_view)
        cheatButton = view.findViewById(R.id.cheat_button)

        buttonFunctions(trueButton, true)
        buttonFunctions(falseButton, false)

        nextButton.apply{
            isEnabled = false
            setOnClickListener {
                quizViewModel.moveToNext()
                if(((quizViewModel.answersSize < quizViewModel.questionBankSize && quizViewModel.getCurrentIndex > quizViewModel.answersSize - 1) || (quizViewModel.answersSize == quizViewModel.questionBankSize && quizViewModel.getCurrentIndex == quizViewModel.questionBankSize - 1) || (quizViewModel.answersSize == quizViewModel.questionBankSize - 1)) && isEnabled) isEnabled = false
                if(!prevButton.isEnabled) prevButton.isEnabled = true
                cheatButton.isEnabled = quizViewModel.cheatCount < 3
                updateQuestion()
            }
        }
        prevButton.apply{
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
        cheatButton.apply{
            isEnabled = !quizViewModel.isCheater && quizViewModel.cheatCount < 3
            setOnClickListener { _ ->
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                callbacks?.onCheatButtonClicked(answerIsTrue)
            }
        }
        updateQuestion()
        return view
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
        if(quizViewModel.answersSize < quizViewModel.getCurrentIndex + 1) {
            trueButton.isEnabled = true
            falseButton.isEnabled = true
            cheatButton.isEnabled = true
        } else {
            trueButton.isEnabled = false
            falseButton.isEnabled = false
            cheatButton.isEnabled = false
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }

        Toast.makeText(requireContext(), messageResId, Toast.LENGTH_SHORT)
            .show()
    }

    private fun showPercentage() {
        Toast.makeText(requireContext(), "You have got ${BigDecimal(quizViewModel.results).setScale(2, RoundingMode.HALF_EVEN)}%", Toast.LENGTH_LONG)
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
                if (quizViewModel.answersSize < quizViewModel.questionBankSize) {
                    checkAnswer(type)
                } else {
                    checkAnswer(type)
                    showPercentage()
                }
                cheatButton.isEnabled = false
            }
        }
    }

    companion object {
        fun newInstance(): QuestionsFragment {
            return QuestionsFragment()
        }
    }
}