package com.example.sinav_uygulamasi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@Composable
fun QuestionCard(
    question: String,
    explanation: String,
    showExplanation: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.CardRadius),
        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.BgTop),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(question, style = MaterialTheme.typography.headlineMedium, color = AppColors.TextDark)
            if (showExplanation) {
                Text(explanation, style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
            }
        }
    }
}
