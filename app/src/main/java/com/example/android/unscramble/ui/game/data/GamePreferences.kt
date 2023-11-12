package com.example.android.unscramble.ui.game.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// 파일에 동적으로 저장되기 때문에 Context필요?
// DataStore 접근하기 위한 편리한 델리케이션 함수를 제공하고 있음
val Context.gameDataStore by preferencesDataStore(
    name = "GamePreferences"
)
object PreferenceKeys {
    val HIGH_SCORE = intPreferencesKey("high_score")
}
data class GamePreferences(
    val highScore: Int
)