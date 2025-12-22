package com.example.sinav_uygulamasi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sinav_uygulamasi.QuizUiState
import com.example.sinav_uygulamasi.QuizViewModel
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.design.Dimens
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(s: QuizUiState, vm: QuizViewModel) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = { vm.goMenu() }) { Icon(Icons.Filled.ArrowBack, null) }
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
            ElevatedCard(
                shape = RoundedCornerShape(Dimens.CardRadius),
                colors = CardDefaults.elevatedCardColors(containerColor = AppColors.Card)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Turuncu Tema", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Switch(
                            checked = s.orangeTheme,
                            onCheckedChange = { vm.saveSettings(it, s.largeText) }
                        )
                    }

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Text("Büyük Yazı", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Switch(
                            checked = s.largeText,
                            onCheckedChange = { vm.saveSettings(s.orangeTheme, it) }
                        )
                    }
                }
            }
        }
    }
}
