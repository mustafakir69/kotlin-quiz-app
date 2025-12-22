package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizType
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.ExamHistoryCard
import com.example.sinav_uygulamasi.ui.components.QuizTypeCard
import com.example.sinav_uygulamasi.ui.components.ScorePill
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

private fun iconBg(type: QuizType): Color = when (type) {
    QuizType.KOTLIN -> AppColors.Kotlin
    QuizType.COMPOSE -> AppColors.Compose
    QuizType.KARISIK -> AppColors.Mixed
}

private fun historyTitle(entry: String): String {
    val type = entry.substringAfter("Tür:", "").substringBefore("|").trim()
    return if (type.isNotBlank()) type else "Sınav"
}

private fun historySubtitle(entry: String): String {
    val skor = entry.substringAfter("Skor:", "").substringBefore("|").trim()
    val sure = entry.substringAfter("Süre:", "").trim()

    val s1 = if (skor.isNotBlank()) "Skor: $skor" else ""
    val s2 = if (sure.isNotBlank()) "Süre: $sure" else ""

    return listOf(s1, s2).filter { it.isNotBlank() }
        .joinToString(" • ")
        .ifBlank { entry }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(s: QuizUiState, vm: QuizViewModel) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ana Menü", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { vm.goSettings() }) {
                        Icon(Icons.Filled.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.TopCenter)
                    .padding(Dimens.ScreenPadding)
                    .widthIn(max = 640.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.Gap),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    ElevatedCard(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.BigRadius),
                        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Skorlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ScorePill("Kotlin", s.bestKotlin, AppColors.Kotlin, Modifier.weight(1f))
                                ScorePill("Compose", s.bestCompose, AppColors.Compose, Modifier.weight(1f))
                                ScorePill("Karışık", s.bestMixed, AppColors.Mixed, Modifier.weight(1f))
                            }
                        }
                    }
                }

                item {
                    Text("Sınav Çeşitleri", style = MaterialTheme.typography.titleLarge, color = AppColors.TextMenuTitle)
                }

                items(QuizType.entries) { type ->
                    val best = when (type) {
                        QuizType.KOTLIN -> s.bestKotlin
                        QuizType.COMPOSE -> s.bestCompose
                        QuizType.KARISIK -> s.bestMixed
                    }

                    QuizTypeCard(
                        title = type.title,
                        bestScore = best,
                        badgeColor = iconBg(type),
                        onStart = {
                            vm.updateType(type)
                            vm.start()
                        }
                    )
                }

                item {
                    Text("Sınav Geçmişi", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                }

                val history = s.examHistory.reversed()
                if (history.isEmpty()) {
                    item {
                        ElevatedCard(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.CardRadius),
                            colors = CardDefaults.elevatedCardColors(containerColor = AppColors.CardWarm)
                        ) {
                            Text(
                                "Henüz sınav yapılmadı.",
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.TextMid
                            )
                        }
                    }
                } else {
                    items(history) { entry ->
                        ExamHistoryCard(
                            title = historyTitle(entry),
                            subtitle = historySubtitle(entry)
                        )
                    }
                }
            }
        }
    }
}
