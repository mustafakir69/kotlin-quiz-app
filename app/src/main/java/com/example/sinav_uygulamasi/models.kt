package com.example.sinav_uygulamasi

enum class QuizType(val title: String) {
    KOTLIN("Kotlin Temelleri"),
    COMPOSE("Jetpack Compose"),
    KARISIK("Karışık")
}

data class Option(
    val text: String,
    val isCorrect: Boolean
)

data class Question(
    val text: String,
    val options: List<Option>,
    val explanation: String
)

data class AnswerRecord(
    val questionText: String,
    val selectedText: String?,
    val correctText: String
)
