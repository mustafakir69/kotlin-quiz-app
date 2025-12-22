package com.example.sinav_uygulamasi

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("quiz_prefs")

data class PersistedStats(
    val lastSummary: String = "",

    val bestKotlin: Int = 0,
    val bestCompose: Int = 0,
    val bestMixed: Int = 0,

    val orangeTheme: Boolean = true,
    val largeText: Boolean = true,

    val examHistory: List<String> = emptyList()
)

class PrefsDataStore(private val context: Context) {

    private object K {
        val lastSummary = stringPreferencesKey("last_summary")

        val bestKotlin = intPreferencesKey("best_kotlin")
        val bestCompose = intPreferencesKey("best_compose")
        val bestMixed = intPreferencesKey("best_mixed")

        val orangeTheme = booleanPreferencesKey("orange_theme")
        val largeText = booleanPreferencesKey("large_text")

        val examHistory = stringSetPreferencesKey("exam_history")
    }

    val flow: Flow<PersistedStats> = context.dataStore.data.map { p ->
        PersistedStats(
            lastSummary = p[K.lastSummary] ?: "",

            bestKotlin = p[K.bestKotlin] ?: 0,
            bestCompose = p[K.bestCompose] ?: 0,
            bestMixed = p[K.bestMixed] ?: 0,

            orangeTheme = p[K.orangeTheme] ?: true,
            largeText = p[K.largeText] ?: true,

            examHistory = p[K.examHistory]?.toList() ?: emptyList()
        )
    }

    // ✅ artık sadece 2 ayar var
    suspend fun saveSettings(orange: Boolean, large: Boolean) {
        context.dataStore.edit {
            it[K.orangeTheme] = orange
            it[K.largeText] = large
        }
    }

    suspend fun saveResult(type: QuizType, score: Int, total: Int, seconds: Int) {
        val summary = "Skor: $score/$total – Süre: ${seconds}s – Tür: ${type.title}"
        val historyEntry = "Tür: ${type.title} | Skor: $score/$total | Süre: ${seconds}s"

        context.dataStore.edit { p ->
            p[K.lastSummary] = summary

            when (type) {
                QuizType.KOTLIN -> p[K.bestKotlin] = maxOf(p[K.bestKotlin] ?: 0, score)
                QuizType.COMPOSE -> p[K.bestCompose] = maxOf(p[K.bestCompose] ?: 0, score)
                QuizType.KARISIK -> p[K.bestMixed] = maxOf(p[K.bestMixed] ?: 0, score)
            }

            val current = p[K.examHistory] ?: emptySet()
            p[K.examHistory] = current + historyEntry
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { p -> p[K.examHistory] = emptySet() }
    }
}
