package com.example.sinav_uygulamasi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@Composable
fun QuizTypeCard(
    title: String,
    bestScore: Int,
    badgeColor: Color,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.CardRadius),
        colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeColor,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Text(
                    "?",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, style = MaterialTheme.typography.titleLarge, color = AppColors.TextDark)
                Text("En iyi skor: $bestScore", style = MaterialTheme.typography.bodyMedium, color = AppColors.TextMid)
            }

            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                shape = RoundedCornerShape(Dimens.ButtonRadius),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Text("Başla", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }
    }
}
