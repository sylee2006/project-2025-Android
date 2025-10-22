package com.example.w06

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.w06.ui.theme.SyleeTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

// ✅ Bubble 데이터 클래스
data class Bubble(
    val id: Int,
    var position: Offset,
    val radius: Float,
    val color: Color,
    val creationTime: Long = System.currentTimeMillis(),
    var velocityX: Float = Random.nextFloat() * 4 - 2,
    var velocityY: Float = Random.nextFloat() * 4 - 2
)

// ✅ GameState 클래스
class GameState {
    var bubbles by mutableStateOf(listOf<Bubble>())
    var score by mutableStateOf(0)
    var timeLeft by mutableStateOf(60)
    var isGameOver by mutableStateOf(false)
    var isFeverTime by mutableStateOf(false) // 🔥 피버 타임 상태 추가
}

// ✅ 버블 생성 함수
fun makeNewBubble(maxWidth: Dp, maxHeight: Dp): Bubble {
    val radius = Random.nextFloat() * 30 + 30
    return Bubble(
        id = Random.nextInt(),
        position = Offset(
            x = Random.nextFloat() * maxWidth.value,
            y = Random.nextFloat() * maxHeight.value
        ),
        radius = radius,
        color = Color(
            red = Random.nextInt(256),
            green = Random.nextInt(256),
            blue = Random.nextInt(256),
            alpha = 200
        )
    )
}

// ✅ 버블 이동 업데이트 함수
fun updateBubblePositions(
    bubbles: List<Bubble>,
    canvasWidthPx: Float,
    canvasHeightPx: Float,
    density: Density
): List<Bubble> {
    return bubbles.map { bubble ->
        var newX = bubble.position.x + bubble.velocityX
        var newY = bubble.position.y + bubble.velocityY
        val radiusPx = with(density) { bubble.radius.dp.toPx() }

        if (newX < radiusPx || newX > canvasWidthPx - radiusPx) {
            bubble.velocityX *= -1
        }
        if (newY < radiusPx || newY > canvasHeightPx - radiusPx) {
            bubble.velocityY *= -1
        }

        bubble.copy(position = Offset(newX, newY))
    }
}

// ✅ 버블 UI
@Composable
fun BubbleComposable(bubble: Bubble, onClick: () -> Unit) {
    Canvas(
        modifier = Modifier
            .size((bubble.radius * 2).dp)
            .offset(x = bubble.position.x.dp, y = bubble.position.y.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        drawCircle(
            color = bubble.color,
            radius = size.width / 2,
            center = center
        )
    }
}

// ✅ 상단 점수/타이머 UI
@Composable
fun GameStatusRow(score: Int, timeLeft: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Time: ${timeLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

// ✅ 게임 화면
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BubbleGameScreen() {
    val gameState = remember { GameState() }

    // 타이머
    LaunchedEffect(gameState.isGameOver) {
        if (!gameState.isGameOver && gameState.timeLeft > 0) {
            while (true) {
                delay(1000L)
                gameState.timeLeft--

                if (gameState.timeLeft == 10) {
                    gameState.isFeverTime = true // 🔥 피버 타임 시작
                }

                if (gameState.timeLeft == 0) {
                    gameState.isGameOver = true
                    break
                }

                val currentTime = System.currentTimeMillis()
                gameState.bubbles = gameState.bubbles.filter {
                    currentTime - it.creationTime < 3000
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GameStatusRow(score = gameState.score, timeLeft = gameState.timeLeft)

        if (gameState.isFeverTime) {
            Text(
                text = "🔥 피버 타임! 🔥",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            val canvasWidthPx = with(density) { maxWidth.toPx() }
            val canvasHeightPx = with(density) { maxHeight.toPx() }

            LaunchedEffect(gameState.isGameOver) {
                if (!gameState.isGameOver) {
                    while (true) {
                        delay(16)
                        if (gameState.bubbles.isEmpty()) {
                            gameState.bubbles = List(3) {
                                makeNewBubble(maxWidth, maxHeight)
                            }
                        }
                        if (Random.nextFloat() < 0.05f && gameState.bubbles.size < 15) {
                            val newBubble = makeNewBubble(maxWidth, maxHeight)
                            gameState.bubbles = gameState.bubbles + newBubble
                        }
                        gameState.bubbles = updateBubblePositions(
                            gameState.bubbles,
                            canvasWidthPx,
                            canvasHeightPx,
                            density
                        )
                    }
                }
            }

            gameState.bubbles.forEach { bubble ->
                BubbleComposable(bubble = bubble) {
                    gameState.score += if (gameState.isFeverTime) 2 else 1 // 🔥 점수 증가
                    gameState.bubbles = gameState.bubbles.filterNot { it.id == bubble.id }
                }
            }

            if (gameState.isGameOver) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("게임 종료") },
                    text = { Text("당신의 점수는 ${gameState.score}점입니다.") },
                    confirmButton = {
                        TextButton(onClick = {
                            gameState.score = 0
                            gameState.timeLeft = 60
                            gameState.isGameOver = false
                            gameState.isFeverTime = false // 🔥 피버 타임 초기화
                            gameState.bubbles = emptyList()
                        }) {
                            Text("다시 시작")
                        }
                    }
                )
            }
        }
    }
}

// ✅ 메인 액티비티
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SyleeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BubbleGameScreen()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BubbleGamePreview() {
    SyleeTheme {
        BubbleGameScreen()
    }
}



