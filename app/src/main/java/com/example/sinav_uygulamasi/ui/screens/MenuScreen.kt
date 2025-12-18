package com.example.sinav_uygulamasi.ui.screens
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sinav_uygulamasi.*
import com.example.sinav_uygulamasi.ui.components.ScorePill
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

private fun iconBg(type: QuizType): Color = when (type) {
    QuizType.KOTLIN -> AppColors.Kotlin
    QuizType.COMPOSE -> AppColors.Compose
    QuizType.KARISIK -> AppColors.Mixed
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
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(Dimens.ScreenPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Dimens.Gap)
        ) {
            if (s.lastSummary.isNotBlank()) {
                ElevatedCard(
                    shape = RoundedCornerShape(Dimens.BigRadius),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppColors.CardWarm)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Son Sınav", style = MaterialTheme.typography.titleLarge, color = AppColors.TextWarmTitle)
                        Text(s.lastSummary, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
                    }
                }
            }

            ElevatedCard(
                shape = RoundedCornerShape(Dimens.BigRadius),
                colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Skorlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ScorePill("Kotlin", s.bestKotlin, AppColors.Kotlin, Modifier.weight(1f))
                        ScorePill("Compose", s.bestCompose, AppColors.Compose, Modifier.weight(1f))
                        ScorePill("Karışık", s.bestMixed, AppColors.Mixed, Modifier.weight(1f))
                    }
                }
            }

            Text("Sınav Çeşitleri", style = MaterialTheme.typography.titleLarge, color = AppColors.TextMenuTitle)

            QuizType.entries.forEach { type ->
                val best = when (type) {
                    QuizType.KOTLIN -> s.bestKotlin
                    QuizType.COMPOSE -> s.bestCompose
                    QuizType.KARISIK -> s.bestMixed
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.CardRadius),
                    colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = iconBg(type),
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 6.dp,
                            shadowElevation = 6.dp
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Help, null
                            , modifier = Modifier.padding(14.dp), tint = Color.White)
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(type.title, style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                            Text("En iyi skor: $best", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
                        }

                        Button(
                            onClick = {
                                vm.updateType(type)
                                vm.start()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                            shape = RoundedCornerShape(Dimens.ButtonRadius),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            Text("Başla", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
