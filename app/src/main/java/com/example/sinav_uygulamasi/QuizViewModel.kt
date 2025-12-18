package com.example.sinav_uygulamasi

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuizUiState(
    val screen: String = "MENU", // MENU, QUIZ, RESULT, SETTINGS
    val selectedType: QuizType = QuizType.KOTLIN,

    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,

    val answers: List<Int?> = emptyList(),
    val elapsedSeconds: Int = 0,

    val isAnswered: Boolean = false,

    val lastSummary: String = "",
    val bestKotlin: Int = 0,
    val bestCompose: Int = 0,
    val bestMixed: Int = 0,

    val soundEnabled: Boolean = true,
    val orangeTheme: Boolean = true,
    val largeText: Boolean = true
)

class QuizViewModel(app: Application) : AndroidViewModel(app) {

    private val store = PrefsDataStore(app.applicationContext)
    private val _ui = MutableStateFlow(QuizUiState())
    val ui: StateFlow<QuizUiState> = _ui.asStateFlow()

    private val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 80)

    init {
        viewModelScope.launch {
            store.flow.collect { s ->
                _ui.update {
                    it.copy(
                        lastSummary = s.lastSummary,
                        bestKotlin = s.bestKotlin,
                        bestCompose = s.bestCompose,
                        bestMixed = s.bestMixed,
                        soundEnabled = s.soundEnabled,
                        orangeTheme = s.orangeTheme,
                        largeText = s.largeText
                    )
                }
            }
        }
    }

    fun goMenu() = _ui.update { it.copy(screen = "MENU") }
    fun goSettings() = _ui.update { it.copy(screen = "SETTINGS") }

    fun updateType(type: QuizType) = _ui.update { it.copy(selectedType = type) }

    fun saveSettings(sound: Boolean, orange: Boolean, large: Boolean) {
        viewModelScope.launch { store.saveSettings(sound, orange, large) }
    }

    fun start() {
        val type = _ui.value.selectedType

        val bank = buildQuestionBank(type).shuffled()

        val shuffledQuestions = bank.map { q ->
            q.copy(options = q.options.shuffled())
        }

        _ui.value = _ui.value.copy(
            screen = "QUIZ",
            questions = shuffledQuestions,
            currentIndex = 0,
            answers = List(shuffledQuestions.size) { null },
            elapsedSeconds = 0,
            isAnswered = false
        )
    }

    fun tick() {
        val s = _ui.value
        if (s.screen != "QUIZ") return
        _ui.update { it.copy(elapsedSeconds = it.elapsedSeconds + 1) }
    }

    fun select(optionIndex: Int) {
        val s = _ui.value
        if (s.screen != "QUIZ") return

        val q = s.questions[s.currentIndex]
        val isCorrect = q.options.getOrNull(optionIndex)?.isCorrect == true

        val newAnswers = s.answers.toMutableList()
        newAnswers[s.currentIndex] = optionIndex

        _ui.update { it.copy(answers = newAnswers, isAnswered = true) }

        if (s.soundEnabled) {
            if (isCorrect) tone.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            else tone.startTone(ToneGenerator.TONE_PROP_NACK, 140)
        }
    }

    fun prev() {
        val s = _ui.value
        if (s.screen != "QUIZ" || s.currentIndex == 0) return

        val newIndex = s.currentIndex - 1
        _ui.update {
            it.copy(
                currentIndex = newIndex,
                isAnswered = (it.answers[newIndex] != null)
            )
        }
    }

    fun next() {
        val s = _ui.value
        if (s.screen != "QUIZ") return

        val last = s.currentIndex == s.questions.lastIndex
        if (last) {
            finishQuiz()
            return
        }

        val newIndex = s.currentIndex + 1
        _ui.update {
            it.copy(
                currentIndex = newIndex,
                isAnswered = (it.answers[newIndex] != null)
            )
        }
    }

    private fun finishQuiz() {
        val s = _ui.value
        val score = calculateScore(s.questions, s.answers)
        val total = s.questions.size

        viewModelScope.launch {
            store.saveResult(
                type = s.selectedType,
                score = score,
                total = total,
                seconds = s.elapsedSeconds
            )
        }

        _ui.update { it.copy(screen = "RESULT") }
    }

    fun restartSame() = start()

    fun buildWrongReview(): List<AnswerRecord> {
        val s = _ui.value
        if (s.questions.isEmpty()) return emptyList()

        return s.questions.mapIndexedNotNull { idx, q ->
            val selectedIdx = s.answers.getOrNull(idx)
            val selectedText = selectedIdx?.let { q.options.getOrNull(it)?.text }
            val correctText = q.options.firstOrNull { it.isCorrect }?.text ?: ""
            val isCorrect = selectedIdx != null && (q.options.getOrNull(selectedIdx)?.isCorrect == true)
            if (isCorrect) null
            else AnswerRecord(q.text, selectedText, correctText)
        }
    }

    fun getScoreText(): String {
        val s = _ui.value
        val sc = calculateScore(s.questions, s.answers)
        return "Skorum: $sc/${s.questions.size} – Süre: ${s.elapsedSeconds}s – ${s.selectedType.title}"
    }

    fun getScorePair(): Pair<Int, Int> {
        val s = _ui.value
        return calculateScore(s.questions, s.answers) to s.questions.size
    }

    private fun calculateScore(questions: List<Question>, answers: List<Int?>): Int {
        var score = 0
        questions.forEachIndexed { i, q ->
            val sel = answers.getOrNull(i)
            if (sel != null && q.options.getOrNull(sel)?.isCorrect == true) score++
        }
        return score
    }

    private fun buildQuestionBank(type: QuizType): List<Question> {
        val kotlin = listOf(
            Question(
                "Kotlin'de değiştirilemeyen değişken hangisiyle tanımlanır?",
                listOf(
                    Option("var", false),
                    Option("val", true),
                    Option("let", false),
                    Option("const", false)
                ),
                "val: değiştirilemeyen (immutable) değişken için kullanılır."
            ),
            Question(
                "Kotlin'de null güvenli çağrı operatörü hangisidir?",
                listOf(
                    Option("!!", false),
                    Option("?.", true),
                    Option("::", false),
                    Option("=>", false)
                ),
                "?. null ise hata vermez, null döndürür."
            ),
            Question(
                "Kotlin'de fonksiyon tanımlamak için kullanılan anahtar kelime hangisidir?",
                listOf(
                    Option("fun", true),
                    Option("def", false),
                    Option("function", false),
                    Option("method", false)
                ),
                "Kotlin fonksiyonları 'fun' ile tanımlar."
            )
        )

        val compose = listOf(
            Question(
                "Jetpack Compose’da arayüz hangi yapıyla yazılır?",
                listOf(
                    Option("XML", false),
                    Option("Composable fonksiyonlar", true),
                    Option("Fragment", false),
                    Option("WebView", false)
                ),
                "Compose arayüzü @Composable fonksiyonlarla oluşturur."
            ),
            Question(
                "Compose’da UI oluşturan fonksiyonlar hangi anotasyonla işaretlenir?",
                listOf(
                    Option("@UI", false),
                    Option("@Compose", false),
                    Option("@Composable", true),
                    Option("@Screen", false)
                ),
                "@Composable, Compose UI üreten fonksiyonları işaretler."
            ),
            Question(
                "Compose’da liste göstermek için en sık kullanılan yapı hangisidir?",
                listOf(
                    Option("LazyColumn", true),
                    Option("RecyclerView", false),
                    Option("ListView", false),
                    Option("GridView", false)
                ),
                "LazyColumn, Compose'da verimli liste için kullanılır."
            )
        )

        val mixed = listOf(kotlin[0], compose[1], kotlin[1])

        return when (type) {
            QuizType.KOTLIN -> kotlin
            QuizType.COMPOSE -> compose
            QuizType.KARISIK -> mixed
        }
    }
}
