package com.bignerdranch.android.geoquiz

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

private const val ARG_CHEAT_ANSWER = "cheat_answer"

class CheatFragment: Fragment() {
    private var callbacks: QuestionsFragment.Callbacks? = null

    private lateinit var answerTextView: TextView
    private lateinit var buildVersionTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var buildVersion: String

    private var answerIsTrue: Boolean = false
    private lateinit var quizViewModel: QuizViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as QuestionsFragment.Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        answerIsTrue = arguments?.getSerializable(ARG_CHEAT_ANSWER) as Boolean
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        quizViewModel = activity.let { ViewModelProvider(requireActivity()).get(QuizViewModel::class.java) }
        val view = inflater.inflate(R.layout.fragment_cheat, container, false)
        answerTextView = view.findViewById(R.id.answer_text_view)
        showAnswerButton = view.findViewById(R.id.show_answer_button)
        buildVersionTextView = view.findViewById(R.id.build_version_text_view)

        buildVersion = "${getString(R.string.api)} ${Build.VERSION.SDK_INT}"
        buildVersionTextView.text = buildVersion
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            quizViewModel.saveCheatCount(quizViewModel.getCheatCount + 1)
            quizViewModel.saveIsCheater(true)
        }
        return view
    }


    companion object {
        fun newInstance(cheatAnswer: Boolean): CheatFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CHEAT_ANSWER, cheatAnswer)
            }
            return CheatFragment().apply {
                arguments = args
            }
        }
    }
}