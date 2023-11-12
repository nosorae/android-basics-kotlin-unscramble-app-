/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel containing the app data and methods to process the data
 */
class GameViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _score = savedStateHandle.getMutableStateFlow(
        key = "score",
        initialValue = 0
    )
    val score: StateFlow<Int>
        get() = _score.asStateFlow()

    private val _highScore = savedStateHandle.getMutableStateFlow(
        key = "highScore",
        initialValue = 0
    )
    val highScore: StateFlow<Int>
        get() = _highScore.asStateFlow()


    private val _currentWordCount = savedStateHandle.getMutableStateFlow(
        key = "currentWordCount",
        initialValue = 0
    )
    val currentWordCount: StateFlow<Int>
        get() = _currentWordCount.asStateFlow()

    private val _currentScrambledWord = savedStateHandle.getMutableStateFlow(
        key = "currentScrambledWord",
        initialValue = ""
    )
    val currentScrambledWord: StateFlow<Spannable> =
        _currentScrambledWord
            .asStateFlow()
            .onSubscription {

            }
            .map {
                val scrambledWord = it
                val spannable: Spannable = SpannableString(scrambledWord)
                spannable.setSpan(
                    TtsSpan.VerbatimBuilder(scrambledWord).build(),
                    0,
                    scrambledWord.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                spannable
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = SpannableString("")
            )

    // List of words used in the game
    private var wordsList: List<String>
        get() = savedStateHandle["wordsList"] ?: emptyList()
        set(value) {
            savedStateHandle["wordsList"] = value
        }

    private var currentWord: String
        get() = savedStateHandle["currentWord"] ?: ""
        set(value) {
            savedStateHandle["currentWord"] = value
        }

    private var isGameOver: Boolean = false


    init {
        getNextWord()
    }

    /*
     * Updates currentWord and currentScrambledWord with the next word.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            Log.d("Unscramble", "currentWord= $currentWord")
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value.inc()
            wordsList += currentWord
        }
    }

    /*
     * Re-initializes the game data to restart the game.
     */
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList = emptyList()
        getNextWord()
        isGameOver = false
    }

    /*
    * Increases the game score if the player’s word is correct.
    */
    private fun increaseScore() {
        _score.value = _score.value.plus(SCORE_INCREASE)
    }

    /*
    * Returns true if the player word is correct.
    * Increases the score accordingly.
    */
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    /*
    * Returns true if the current word count is less than MAX_NO_OF_WORDS
    */
    fun nextWord(): Boolean {
        return if (_currentWordCount.value < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else {
            isGameOver = true
            false
        }
    }

    fun isGameOver() = isGameOver
}
