package com.example.sinav_uygulamasi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    val appTypography = Typography(
        headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
        headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp),
        titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp),
        titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
        bodyLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal, lineHeight = 26.sp),
        bodyMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
        labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
    )

    MaterialTheme(
        typography = appTypography,
        content = content
    )
}

@Composable
fun AppRoot(vm: QuizViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    // TURUNCU ARKA PLAN (soft gradient)
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFFFFF3E0), // çok açık turuncu
            Color(0xFFFFC07A)  // sıcak turuncu
        )
    )

    // Süre sayacı sadece quiz ekranında aksın
    LaunchedEffect(state.screen) {
        while (state.screen == "QUIZ") {
            delay(1000)
            vm.tick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        when (state.screen) {
            "MENU" -> MenuScreen(onStart = vm::startQuiz)

            "QUIZ" -> {
                val type = state.quizType?.let { QuizType.valueOf(it) } ?: QuizType.KOTLIN
                val questions = vm.getQuestionsFor(type)
                QuizScreen(
                    title = type.title,
                    index = state.currentIndex,
                    total = questions.size,
                    seconds = state.elapsedSeconds,
                    question = questions[state.currentIndex],
                    selectedIndex = state.selectedIndex,
                    isAnswered = state.isAnswered,
                    isCorrect = state.isCorrect,
                    correctIndex = questions[state.currentIndex].correctIndex,
                    onSelect = vm::selectOption,
                    onNext = vm::next
                )
            }

            "RESULT" -> {
                val type = state.quizType?.let { QuizType.valueOf(it) } ?: QuizType.KOTLIN
                val total = vm.getQuestionsFor(type).size
                ResultScreen(
                    score = state.score,
                    total = total,
                    seconds = state.elapsedSeconds,
                    onRestart = vm::restartSameQuiz,
                    onMenu = vm::goMenu
                )
            }
        }
    }
}

/** Menü ikonlarının arka plan rengi (daha belirgin) */
private fun menuIconBg(type: QuizType): Color = when (type) {
    QuizType.KOTLIN -> Color(0xFF2563EB)   // canlı mavi
    QuizType.COMPOSE -> Color(0xFFDB2777)  // canlı pembe
    QuizType.KARISIK -> Color(0xFF16A34A)  // canlı yeşil
}

/** Menü ikonunun rengi */
private fun menuIconTint(): Color = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuScreen(onStart: (QuizType) -> Unit) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Ana Menü", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero alan (turuncu temaya uyumlu)
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFE0B2))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sınav Uygulaması",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF4E2600)
                    )
                    Text(
                        text = "Bir sınav seç ve hemen başla.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6B2F00)
                    )
                }
            }

            Text("Sınav Çeşitleri", style = MaterialTheme.typography.titleLarge, color = Color(0xFF2B1B10))

            QuizType.entries.forEach { type ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStart(type) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFFBF5))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Daha belirgin ikon alanı
                        Surface(
                            color = menuIconBg(type),
                            shape = RoundedCornerShape(16.dp),
                            tonalElevation = 6.dp,
                            shadowElevation = 6.dp
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Quiz,
                                contentDescription = null,
                                modifier = Modifier.padding(14.dp),
                                tint = menuIconTint()
                            )
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(type.title, style = MaterialTheme.typography.titleLarge, color = Color(0xFF1F130A))
                            Text("3 soru", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF6B2F00))
                        }

                        Button(
                            onClick = { onStart(type) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00)),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Text("Başla", style = MaterialTheme.typography.titleMedium, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizScreen(
    title: String,
    index: Int,
    total: Int,
    seconds: Int,
    question: Question,
    selectedIndex: Int,
    isAnswered: Boolean,
    isCorrect: Boolean?,
    correctIndex: Int,
    onSelect: (Int) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, style = MaterialTheme.typography.titleLarge) },
                actions = {
                    AssistChip(
                        onClick = {},
                        label = { Text("${seconds}s", style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.AccessTime, null) },
                        modifier = Modifier.padding(end = 12.dp)
                    )
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 14.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val progress = (index + 1f) / total.toFloat()
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = Color(0xFFFF7A00)
            )

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Soru ${index + 1} / $total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3A2416)
                )
                Spacer(Modifier.weight(1f))
                if (isAnswered) {
                    val txt = if (isCorrect == true) "Doğru" else "Yanlış"
                    AssistChip(
                        onClick = {},
                        label = { Text(txt, style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = { Icon(Icons.Filled.CheckCircle, null) }
                    )
                }
            }

            // SORU KARTI:
            // Arka planla aynı tona yakın, ama gölgeli + border ile belirgin
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = Color(0xFFFFF3E0) // arka planın açık tonu
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFFF7A00),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Quiz,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Text("Soru", style = MaterialTheme.typography.titleMedium, color = Color(0xFF6B2F00))
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        question.text,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFF1F130A)
                    )
                }
            }

            // Sabit renkler: doğru=yeşil, yanlış=kırmızı
            val correctBg = Color(0xFFD1FAE5)
            val correctBorder = Color(0xFF10B981)
            val wrongBg = Color(0xFFFEE2E2)
            val wrongBorder = Color(0xFFEF4444)

            // Şık kartlarının normal arka planı (turuncu temaya uygun kırık beyaz)
            val base = Color(0xFFFFFBF5)
            val disabledBg = Color(0xFFFFEAD3)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                question.options.forEachIndexed { i, opt ->
                    val isSel = (selectedIndex == i)
                    val isCorrectOption = (i == correctIndex)

                    val target = when {
                        !isAnswered -> base
                        isAnswered && isCorrectOption -> correctBg
                        isAnswered && isSel && !isCorrectOption -> wrongBg
                        else -> disabledBg
                    }
                    val bg by animateColorAsState(target, label = "optBg")

                    val borderColor = when {
                        !isAnswered && isSel -> Color(0xFFFF7A00)
                        isAnswered && isCorrectOption -> correctBorder
                        isAnswered && isSel && !isCorrectOption -> wrongBorder
                        else -> Color(0xFFFFC07A)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(enabled = !isAnswered) { onSelect(i) },
                        color = bg,
                        border = BorderStroke(1.dp, borderColor),
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val label = ('A' + i).toString()
                            Surface(
                                color = Color(0xFFFFE0B2),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    label,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF6B2F00)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Text(
                                opt,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F130A)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = onNext,
                enabled = isAnswered,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00))
            ) {
                Text(
                    if (index == total - 1) "Sınavı Bitir" else "Sonraki Soru",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultScreen(
    score: Int,
    total: Int,
    seconds: Int,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sonuç", style = MaterialTheme.typography.titleLarge) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFFBF5)),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
            ) {
                Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Skor", style = MaterialTheme.typography.titleMedium, color = Color(0xFF6B2F00))
                    Spacer(Modifier.height(8.dp))
                    Text("$score / $total", style = MaterialTheme.typography.headlineLarge, color = Color(0xFF1F130A))
                    Spacer(Modifier.height(10.dp))
                    Text("Süre: ${seconds}s", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF3A2416))
                }
            }

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
            ) {
                Text("Tekrar Başla", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onMenu,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFFA94D))
            ) {
                Text("Ana Menüye Dön", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F130A))
            }
        }
    }
}
