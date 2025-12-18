package com.example.sinav_uygulamasi.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
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
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sonuç", style = MaterialTheme.typography.titleLarge) }
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

            if (wrongs.isNotEmpty()) {
                Text("Yanlışlar", style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                wrongs.forEach { r ->
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

            Button(
                onClick = { vm.restartSame() },
                modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(Dimens.ButtonRadius),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Restart)
            ) {
                Icon(Icons.Filled.Refresh, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Tekrar Başla", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }

            Button(
                onClick = { vm.goMenu() },
                modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(Dimens.ButtonRadius),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Home)
            ) {
                Icon(Icons.Filled.Home, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Ana Menüye Dön", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }

            Button(
                onClick = {
                    val text = vm.getScoreText()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Paylaş"))
                },
                modifier = Modifier.fillMaxWidth().height(Dimens.ButtonHeight),
                shape = RoundedCornerShape(Dimens.ButtonRadius),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Share)
            ) {
                Icon(Icons.Filled.Share, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Paylaş", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }
    }
}
