package com.example.sinav_uygulamasi

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("quiz_prefs")

data class PersistedStats(
    val lastSummary: String = "",
    val lastScore: Int = 0,
    val lastTotal: Int = 0,
    val lastSeconds: Int = 0,
    val lastType: String = "",

    val bestKotlin: Int = 0,
    val bestCompose: Int = 0,
    val bestMixed: Int = 0,

    val soundEnabled: Boolean = true,
    val orangeTheme: Boolean = true,
    val largeText: Boolean = true
)

class PrefsDataStore(private val context: Context) {

    private object K {
        val lastSummary = stringPreferencesKey("last_summary")
        val lastScore = intPreferencesKey("last_score")
        val lastTotal = intPreferencesKey("last_total")
        val lastSeconds = intPreferencesKey("last_seconds")
        val lastType = stringPreferencesKey("last_type")

        val bestKotlin = intPreferencesKey("best_kotlin")
        val bestCompose = intPreferencesKey("best_compose")
        val bestMixed = intPreferencesKey("best_mixed")

        val soundEnabled = booleanPreferencesKey("sound_enabled")
        val orangeTheme = booleanPreferencesKey("orange_theme")
        val largeText = booleanPreferencesKey("large_text")
    }

    val flow: Flow<PersistedStats> = context.dataStore.data.map { p ->
        PersistedStats(
            lastSummary = p[K.lastSummary] ?: "",
            lastScore = p[K.lastScore] ?: 0,
            lastTotal = p[K.lastTotal] ?: 0,
            lastSeconds = p[K.lastSeconds] ?: 0,
            lastType = p[K.lastType] ?: "",

            bestKotlin = p[K.bestKotlin] ?: 0,
            bestCompose = p[K.bestCompose] ?: 0,
            bestMixed = p[K.bestMixed] ?: 0,

            soundEnabled = p[K.soundEnabled] ?: true,
            orangeTheme = p[K.orangeTheme] ?: true,
            largeText = p[K.largeText] ?: true
        )
    }

    suspend fun saveSettings(sound: Boolean, orange: Boolean, large: Boolean) {
        context.dataStore.edit {
            it[K.soundEnabled] = sound
            it[K.orangeTheme] = orange
            it[K.largeText] = large
        }
    }

    suspend fun saveResult(type: QuizType, score: Int, total: Int, seconds: Int) {
        val summary = "Son skor: $score/$total – Süre: ${seconds}s – Tür: ${type.title}"

        context.dataStore.edit { p ->
            p[K.lastSummary] = summary
            p[K.lastScore] = score
            p[K.lastTotal] = total
            p[K.lastSeconds] = seconds
            p[K.lastType] = type.name

            when (type) {
                QuizType.KOTLIN -> p[K.bestKotlin] = maxOf(p[K.bestKotlin] ?: 0, score)
                QuizType.COMPOSE -> p[K.bestCompose] = maxOf(p[K.bestCompose] ?: 0, score)
                QuizType.KARISIK -> p[K.bestMixed] = maxOf(p[K.bestMixed] ?: 0, score)
            }
        }
    }
}
