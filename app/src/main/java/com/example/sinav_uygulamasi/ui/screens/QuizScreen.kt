package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(s: QuizUiState, vm: QuizViewModel) {
    val haptic = LocalHapticFeedback.current

    val q = s.questions[s.currentIndex]
    val selected = s.answers[s.currentIndex]

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(s.selectedType.title, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = { IconButton(onClick = { vm.goMenu() }) { Icon(Icons.Filled.Close, null) } },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${s.elapsedSeconds}s", style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.AccessTime, null) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
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
            LinearProgressIndicator(
                progress = { (s.currentIndex + 1f) / s.questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(99.dp)),
                color = AppColors.Orange
            )

            Text(
                "Soru ${s.currentIndex + 1}/${s.questions.size}",
                style = MaterialTheme.typography.bodyMedium,
                color = AppColors.TextMid
            )

            ElevatedCard(
                shape = RoundedCornerShape(Dimens.CardRadius),
                colors = CardDefaults.elevatedCardColors(containerColor = AppColors.BgTop),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(q.text, style = MaterialTheme.typography.headlineMedium, color = AppColors.TextDark)
                    if (s.isAnswered) {
                        Text(q.explanation, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
                    }
                }
            }

            q.options.forEachIndexed { idx, opt ->
                val isSel = (selected == idx)
                val correct = opt.isCorrect

                val bg = when {
                    !s.isAnswered -> AppColors.Card
                    s.isAnswered && correct -> AppColors.CorrectBg
                    s.isAnswered && isSel -> AppColors.WrongBg
                    else -> AppColors.DisabledBg
                }

                val border = when {
                    !s.isAnswered && isSel -> BorderStroke(1.dp, AppColors.Orange)
                    s.isAnswered && correct -> BorderStroke(1.dp, AppColors.CorrectBorder)
                    s.isAnswered && isSel -> BorderStroke(1.dp, AppColors.WrongBorder)
                    else -> BorderStroke(1.dp, AppColors.BgBottom)
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.OptionRadius))
                        .clickable(enabled = !s.isAnswered) {
                            vm.select(idx)
                            haptic.performHapticFeedback(
                                if (opt.isCorrect) HapticFeedbackType.LongPress
                                else HapticFeedbackType.TextHandleMove
                            )
                        },
                    color = bg,
                    border = border,
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(Dimens.OptionRadius)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        val label = ('A' + idx).toString()

                        Surface(color = AppColors.CardWarm, shape = RoundedCornerShape(10.dp)) {
                            Text(
                                label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleMedium,
                                color = AppColors.TextMid
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            opt.text,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f),
                            color = AppColors.TextDark
                        )

                        if (s.isAnswered) {
                            when {
                                opt.isCorrect -> Icon(Icons.Filled.CheckCircle, null, tint = AppColors.CorrectBorder)
                                isSel && !opt.isCorrect -> Icon(Icons.Filled.Cancel, null, tint = AppColors.WrongBorder)
                            }
                        }

                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { vm.prev() },
                    enabled = (s.currentIndex > 0),
                    modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                    shape = RoundedCornerShape(Dimens.ButtonRadius)
                ) { Text("Önceki", style = MaterialTheme.typography.titleMedium) }

                Button(
                    onClick = { vm.next() },
                    enabled = (s.answers[s.currentIndex] != null),
                    modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                    shape = RoundedCornerShape(Dimens.ButtonRadius),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
                ) {
                    Text(
                        if (s.currentIndex == s.questions.lastIndex) "Bitir" else "Sonraki",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}
