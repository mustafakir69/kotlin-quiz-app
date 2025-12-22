package com.example.sinav_uygulamasi.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.components.ActionButton
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(s: QuizUiState, vm: QuizViewModel) {
    val context = LocalContext.current

    val wrongs = remember(s.questions, s.answers) { vm.buildWrongReview() }
    val (score, total) = remember(s.questions, s.answers) { vm.getScorePair() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { CenterAlignedTopAppBar(title = { Text("Sonuç", style = MaterialTheme.typography.titleLarge) }) }
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
                        shape = RoundedCornerShape(Dimens.CardRadius),
                        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Özet", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                            Text("Skor: $score/$total", style = MaterialTheme.typography.bodyLarge, color = AppColors.TextDark)
                            Text("Süre: ${s.elapsedSeconds}s", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
                        }
                    }
                }

                if (wrongs.isNotEmpty()) {
                    item {
                        Text("Yanlışlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                    }

                    items(wrongs) { r ->
                        ElevatedCard(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
                        ) {
                            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(r.questionText, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "Senin cevabın: ${r.selectedText ?: "Boş"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.WrongBorder
                                )
                                Text(
                                    "Doğru cevap: ${r.correctText}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AppColors.CorrectBorder
                                )
                            }
                        }
                    }
                }

                item {
                    ActionButton(
                        text = "Tekrar Başla",
                        icon = Icons.Filled.Refresh,
                        color = AppColors.Restart,
                        onClick = { vm.restartSame() },
                        modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight)
                    )
                }

                item {
                    ActionButton(
                        text = "Ana Menüye Dön",
                        icon = Icons.Filled.Home,
                        color = AppColors.Home,
                        onClick = { vm.goMenu() },
                        modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight)
                    )
                }

                item {
                    ActionButton(
                        text = "Paylaş",
                        icon = Icons.Filled.Share,
                        color = AppColors.Share,
                        onClick = {
                            val text = vm.getScoreText()
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(intent, "Paylaş"))
                        },
                        modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight)
                    )
                }
            }
        }
    }
}
