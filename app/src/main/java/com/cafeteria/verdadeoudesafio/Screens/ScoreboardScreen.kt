package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.models.PlayerScore
import com.cafeteria.verdadeoudesafio.ui.theme.*

@Composable
fun ScoreboardScreen(
    playerScores: List<PlayerScore>,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val sortedPlayers = playerScores.sortedWith(
        compareByDescending<PlayerScore> { it.points }
            .thenByDescending { it.challengesCompleted }
            .thenBy { it.refusals }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = NeonRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PLACAR",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Podium para top 3
            if (sortedPlayers.isNotEmpty()) {
                PodiumView(sortedPlayers.take(3))
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Lista completa
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(sortedPlayers) { index, player ->
                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(
                            initialOffsetX = { 300 },
                            animationSpec = tween(300, delayMillis = index * 50)
                        ) + fadeIn(animationSpec = tween(300, delayMillis = index * 50))
                    ) {
                        PlayerScoreCard(
                            position = index + 1,
                            playerScore = player,
                            isLeader = index == 0
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão continuar
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "CONTINUAR JOGANDO",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun PodiumView(topPlayers: List<PlayerScore>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2º lugar
        if (topPlayers.size > 1) {
            PodiumPlace(2, topPlayers[1], 140.dp, Color(0xFFC0C0C0))
        }

        // 1º lugar
        if (topPlayers.isNotEmpty()) {
            PodiumPlace(1, topPlayers[0], 180.dp, Color(0xFFFFD700))
        }

        // 3º lugar
        if (topPlayers.size > 2) {
            PodiumPlace(3, topPlayers[2], 120.dp, Color(0xFFCD7F32))
        }
    }
}

@Composable
fun PodiumPlace(position: Int, player: PlayerScore, height: Dp, color: Color) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(position * 200L)
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .width(100.dp)
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone/Avatar
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color, CircleShape)
                .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when(position) {
                    1 -> "🥇"
                    2 -> "🥈"
                    else -> "🥉"
                },
                fontSize = 32.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = player.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${player.points} pts",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = color
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Pedestal
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(height)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = 0.7f), color.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .border(
                    2.dp,
                    color.copy(alpha = 0.5f),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$position°",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun PlayerScoreCard(position: Int, playerScore: PlayerScore, isLeader: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isLeader) NeonRed.copy(alpha = 0.2f) else DarkCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Posição
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isLeader) NeonRed else Color.Gray.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$position",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info do jogador
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = playerScore.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row {
                    Text(
                        text = "✓ ${playerScore.challengesCompleted + playerScore.truthsCompleted}",
                        fontSize = 12.sp,
                        color = Color.Green.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "✗ ${playerScore.refusals}",
                        fontSize = 12.sp,
                        color = Color.Red.copy(alpha = 0.7f)
                    )
                }
            }

            // Pontos
            Text(
                text = "${playerScore.points}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = if (isLeader) NeonRed else NeonBlue
            )
        }
    }
}