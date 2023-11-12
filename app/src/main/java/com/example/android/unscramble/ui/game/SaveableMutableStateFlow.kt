package com.example.android.unscramble.ui.game

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.StateFlow

class SaveableMutableStateFlow<T>(
    private val savedStateHandle: SavedStateHandle,
    private val key: String,
    initialValue: T
) {
    // 확신이 생기지 않으면 상속보다는 컴포지션
    private val state: StateFlow<T> = savedStateHandle.getStateFlow(key, initialValue)
    var value: T
        get() = state.value
        set(value) {
            savedStateHandle[key] = value
        }

    fun asStateFlow(): StateFlow<T> = state
}

// getStateFlow에 대응하는 헬퍼함수
fun <T> SavedStateHandle.getMutableStateFlow(
    key: String,
    initialValue: T
): SaveableMutableStateFlow<T> =
    SaveableMutableStateFlow(
        savedStateHandle = this,
        key = key,
        initialValue = initialValue
    )