package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.cafeteria.verdadeoudesafio.managers.AudioManager
import com.cafeteria.verdadeoudesafio.models.ScoreRules
import com.cafeteria.verdadeoudesafio.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    challenged: String,
    option: String,
    question: String,
    onComplete: (Boolean) -> Unit,
    onTakePhoto: () -> Unit,
    onNext: () -> Unit,
    onBackToMenu: () -> Unit,
    allowPhotos: Boolean = true
) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }
    val accentColor = if (option == "Verdade") NeonBlue else NeonRed

    var showActions by remember { mutableStateOf(false) }
    var pointsToAdd by remember { mutableStateOf<Int?>(null) }
    var showPointsAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        showActions = true
    }

    LaunchedEffect(pointsToAdd) {
        if (pointsToAdd != null) {
            showPointsAnimation = true

            // Tocar som baseado nos pontos
            if ((pointsToAdd ?: 0) > 0) {
                audioManager.playSound(AudioManager.SoundEffect.SUCCESS)
            } else {
                audioManager.playSound(AudioManager.SoundEffect.FAIL)
            }

            delay(2000)
            onNext()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Efeito de luz neon de fundo
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.2f),
                            DarkBackground
                        ),
                        center = androidx.compose.ui.geometry.Offset(0.5f, 0.3f),
                        radius = 800f
                    )
                )
        )

        IconButton(
            onClick = {
                audioManager.playSound(AudioManager.SoundEffect.CLICK)
                onBackToMenu()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(2f)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar ao Menu",
                tint = NeonRed,
                modifier = Modifier.size(32.dp)
            )
        }

        // Animação de pontos - CENTRALIZADA E POR CIMA DE TUDO
        AnimatedVisibility(
            visible = showPointsAnimation,
            enter = slideInVertically(
                initialOffsetY = { -200 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.Center)
                .zIndex(10f)
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                if ((pointsToAdd ?: 0) > 0) Color.Green.copy(alpha = 0.3f) else NeonRed.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${if ((pointsToAdd ?: 0) > 0) "+" else ""}${pointsToAdd ?: 0}",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = if ((pointsToAdd ?: 0) > 0) Color.Green else NeonRed,
                    modifier = Modifier.shadow(
                        elevation = 40.dp,
                        spotColor = if ((pointsToAdd ?: 0) > 0) Color.Green else NeonRed
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = option.uppercase(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                modifier = Modifier.shadow(elevation = 30.dp, spotColor = accentColor)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp)
                        .border(
                            width = 2.dp,
                            color = accentColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = challenged.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonRed,
                        modifier = Modifier.shadow(elevation = 20.dp, spotColor = NeonRed)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = question,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = showActions,
                enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Botão Completou
                    Button(
                        onClick = {
                            val points = if (option == "Verdade") {
                                ScoreRules.COMPLETE_TRUTH
                            } else {
                                ScoreRules.COMPLETE_DARE
                            }
                            pointsToAdd = points
                            onComplete(true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .shadow(elevation = 20.dp, spotColor = Color.Green, shape = RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "✓ COMPLETOU (+${if (option == "Verdade") ScoreRules.COMPLETE_TRUTH else ScoreRules.COMPLETE_DARE} pts)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão Tirar Foto (se permitido)
                    if (allowPhotos && option == "Desafio") {
                        OutlinedButton(
                            onClick = {
                                audioManager.playSound(AudioManager.SoundEffect.CLICK)
                                onTakePhoto()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NeonBlue
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 2.dp,
                                brush = Brush.linearGradient(listOf(NeonBlue, NeonBlue.copy(alpha = 0.5f)))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TIRAR FOTO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Botão Recusou
                    OutlinedButton(
                        onClick = {
                            pointsToAdd = ScoreRules.REFUSE_CHALLENGE
                            onComplete(false)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonRed
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = Brush.linearGradient(listOf(NeonRed, NeonRed.copy(alpha = 0.5f)))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "✗ RECUSOU (${ScoreRules.REFUSE_CHALLENGE} pts)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}