package com.example.sinav_uygulamasi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class QuizType(val title: String) {
    KOTLIN("Kotlin Temelleri"),
    COMPOSE("Jetpack Compose"),
    KARISIK("Karışık")
}

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizUiState(
    val screen: String = "MENU",      // MENU, QUIZ, RESULT
    val quizType: String? = null,     // KOTLIN, COMPOSE, KARISIK
    val currentIndex: Int = 0,
    val selectedIndex: Int = -1,
    val isAnswered: Boolean = false,
    val isCorrect: Boolean? = null,
    val score: Int = 0,
    val finished: Boolean = false,
    val elapsedSeconds: Int = 0
)

class QuizViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val KEY_SCREEN = "screen"
    private val KEY_TYPE = "type"
    private val KEY_INDEX = "index"
    private val KEY_SELECTED = "selected"
    private val KEY_ANSWERED = "answered"
    private val KEY_CORRECT = "correct"
    private val KEY_SCORE = "score"
    private val KEY_FINISHED = "finished"
    private val KEY_SECONDS = "seconds"

    private val _uiState = MutableStateFlow(
        QuizUiState(
            screen = savedStateHandle[KEY_SCREEN] ?: "MENU",
            quizType = savedStateHandle[KEY_TYPE],
            currentIndex = savedStateHandle[KEY_INDEX] ?: 0,
            selectedIndex = savedStateHandle[KEY_SELECTED] ?: -1,
            isAnswered = savedStateHandle[KEY_ANSWERED] ?: false,
            isCorrect = savedStateHandle[KEY_CORRECT],
            score = savedStateHandle[KEY_SCORE] ?: 0,
            finished = savedStateHandle[KEY_FINISHED] ?: false,
            elapsedSeconds = savedStateHandle[KEY_SECONDS] ?: 0
        )
    )
    val uiState: StateFlow<QuizUiState> = _uiState

    // Şimdilik 3 soru
    private val kotlinQuestions = listOf(
        Question(
            "Kotlin'de değiştirilemeyen değişken hangisiyle tanımlanır?",
            listOf("var", "val", "let", "const"),
            1
        ),
        Question(
            "Kotlin'de null güvenli çağrı operatörü hangisidir?",
            listOf("!!", "?.", "::", "=>"),
            1
        ),
        Question(
            "Kotlin'de fonksiyon tanımlamak için kullanılan anahtar kelime hangisidir?",
            listOf("fun", "def", "function", "method"),
            0
        )
    )

    private val composeQuestions = listOf(
        Question(
            "Jetpack Compose’da arayüz hangi yapıyla yazılır?",
            listOf("XML", "Composable fonksiyonlar", "Fragment", "WebView"),
            1
        ),
        Question(
            "Compose’da UI oluşturan fonksiyonlar hangi anotasyonla işaretlenir?",
            listOf("@UI", "@Compose", "@Composable", "@Screen"),
            2
        ),
        Question(
            "Compose’da liste göstermek için en sık kullanılan yapı hangisidir?",
            listOf("LazyColumn", "RecyclerView", "ListView", "GridView"),
            0
        )
    )

    private val mixedQuestions = listOf(
        kotlinQuestions[0],
        composeQuestions[1],
        kotlinQuestions[1]
    )

    fun getQuestionsFor(type: QuizType): List<Question> = when (type) {
        QuizType.KOTLIN -> kotlinQuestions
        QuizType.COMPOSE -> composeQuestions
        QuizType.KARISIK -> mixedQuestions
    }

    private fun currentQuestions(): List<Question> {
        val t = _uiState.value.quizType ?: return emptyList()
        return getQuestionsFor(QuizType.valueOf(t))
    }

    fun goMenu() {
        _uiState.update { it.copy(screen = "MENU") }
        savedStateHandle[KEY_SCREEN] = "MENU"
    }

    fun startQuiz(type: QuizType) {
        _uiState.value = QuizUiState(
            screen = "QUIZ",
            quizType = type.name
        )
        savedStateHandle[KEY_SCREEN] = "QUIZ"
        savedStateHandle[KEY_TYPE] = type.name
        savedStateHandle[KEY_INDEX] = 0
        savedStateHandle[KEY_SELECTED] = -1
        savedStateHandle[KEY_ANSWERED] = false
        savedStateHandle[KEY_CORRECT] = null
        savedStateHandle[KEY_SCORE] = 0
        savedStateHandle[KEY_FINISHED] = false
        savedStateHandle[KEY_SECONDS] = 0
    }

    fun tick() {
        val s = _uiState.value
        if (s.screen != "QUIZ" || s.finished) return
        val next = s.elapsedSeconds + 1
        _uiState.update { it.copy(elapsedSeconds = next) }
        savedStateHandle[KEY_SECONDS] = next
    }

    fun selectOption(index: Int) {
        val s = _uiState.value
        if (s.screen != "QUIZ" || s.finished || s.isAnswered) return

        val questions = currentQuestions()
        if (questions.isEmpty()) return

        val q = questions[s.currentIndex]
        val correct = (index == q.correctIndex)
        val newScore = if (correct) s.score + 1 else s.score

        _uiState.update {
            it.copy(
                selectedIndex = index,
                isAnswered = true,
                isCorrect = correct,
                score = newScore
            )
        }

        savedStateHandle[KEY_SELECTED] = index
        savedStateHandle[KEY_ANSWERED] = true
        savedStateHandle[KEY_CORRECT] = correct
        savedStateHandle[KEY_SCORE] = newScore
    }

    fun next() {
        val s = _uiState.value
        if (s.screen != "QUIZ" || s.finished) return

        val questions = currentQuestions()
        val isLast = s.currentIndex == questions.lastIndex

        if (isLast) {
            _uiState.update { it.copy(finished = true, screen = "RESULT") }
            savedStateHandle[KEY_FINISHED] = true
            savedStateHandle[KEY_SCREEN] = "RESULT"
            return
        }

        val newIndex = s.currentIndex + 1
        _uiState.update {
            it.copy(
                currentIndex = newIndex,
                selectedIndex = -1,
                isAnswered = false,
                isCorrect = null
            )
        }

        savedStateHandle[KEY_INDEX] = newIndex
        savedStateHandle[KEY_SELECTED] = -1
        savedStateHandle[KEY_ANSWERED] = false
        savedStateHandle[KEY_CORRECT] = null
    }

    fun restartSameQuiz() {
        val typeName = _uiState.value.quizType ?: return
        startQuiz(QuizType.valueOf(typeName))
    }
}
