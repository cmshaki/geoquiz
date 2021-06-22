package com.bignerdranch.android.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider

private const val KEY_INDEX = "index"
private const val CHEAT_INDEX = "cheat_index"
private const val CHEAT_COUNT_INDEX = "cheat_count_index"
private const val TAG = "MainActivity"
//private const val ANSWERS = "answers"

class MainActivity : AppCompatActivity(), QuestionsFragment.Callbacks {
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = QuestionsFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onCheatButtonClicked(cheatAnswer: Boolean) {
        val fragment = CheatFragment.newInstance(cheatAnswer)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
