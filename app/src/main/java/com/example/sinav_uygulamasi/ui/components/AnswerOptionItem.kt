package com.example.sinav_uygulamasi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens

@Composable
fun AnswerOptionItem(
    label: String,
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = when {
        !isAnswered -> AppColors.Card
        isCorrect -> AppColors.CorrectBg
        isSelected -> AppColors.WrongBg
        else -> AppColors.DisabledBg
    }

    val border = when {
        !isAnswered && isSelected -> BorderStroke(1.dp, AppColors.Orange)
        isAnswered && isCorrect -> BorderStroke(1.dp, AppColors.CorrectBorder)
        isAnswered && isSelected -> BorderStroke(1.dp, AppColors.WrongBorder)
        else -> BorderStroke(1.dp, AppColors.BgBottom)
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.OptionRadius))
            .clickable(enabled = !isAnswered) { onClick() },
        color = bg,
        border = border,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(Dimens.OptionRadius)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                color = AppColors.TextDark
            )

            if (isAnswered) {
                when {
                    isCorrect -> Icon(Icons.Filled.CheckCircle, null, tint = AppColors.CorrectBorder)
                    isSelected -> Icon(Icons.Filled.Cancel, null, tint = AppColors.WrongBorder)
                }
            }
        }
    }
}
