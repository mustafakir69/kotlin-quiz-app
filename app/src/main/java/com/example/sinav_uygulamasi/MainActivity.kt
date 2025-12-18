package com.example.sinav_uygulamasi
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sinav_uygulamasi.ui.design.AppColors
import com.example.sinav_uygulamasi.ui.screens.MenuScreen
import com.example.sinav_uygulamasi.ui.screens.QuizScreen
import com.example.sinav_uygulamasi.ui.screens.ResultScreen
import com.example.sinav_uygulamasi.ui.screens.SettingsScreen
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppRoot() }
    }
}

@Composable
private fun AppRoot(vm: QuizViewModel = viewModel()) {
    val s by vm.ui.collectAsState()

    val typography = if (s.largeText) {
        Typography(
            headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp),
            titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp),
            titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
            bodyLarge = TextStyle(fontSize = 20.sp, lineHeight = 26.sp),
            bodyMedium = TextStyle(fontSize = 18.sp, lineHeight = 24.sp),
            labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        )
    } else Typography()

    MaterialTheme(typography = typography) {
        val bg = if (s.orangeTheme) {
            Brush.verticalGradient(listOf(AppColors.BgTop, AppColors.BgBottom))
        } else {
            Brush.verticalGradient(listOf(Color(0xFFEFF6FF), Color(0xFF93C5FD)))
        }

        LaunchedEffect(s.screen) {
            while (s.screen == "QUIZ") {
                delay(1000)
                vm.tick()
            }
        }

        Box(Modifier.fillMaxSize().background(bg)) {
            when (s.screen) {
                "MENU" -> MenuScreen(s, vm)
                "SETTINGS" -> SettingsScreen(s, vm)
                "QUIZ" -> QuizScreen(s, vm)
                "RESULT" -> ResultScreen(s, vm)
            }
        }
    }
}
