package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.cafeteria.verdadeoudesafio.managers.PowerCardManager
import com.cafeteria.verdadeoudesafio.models.*
import com.cafeteria.verdadeoudesafio.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ResultScreen(
    challenged: String,
    challenger: String,
    option: String,
    question: String,
    players: List<String>,
    onComplete: (Boolean, Int) -> Unit,
    onRecordVideo: () -> Unit,
    onNext: (String?, Boolean) -> Unit,
    onBackToMenu: () -> Unit,
    allowPhotos: Boolean = true,
    playerCards: List<PowerCard> = emptyList(),
    activeCard: PowerCard? = null,
    onViewCards: () -> Unit = {},
    onStealPoints: (String, String, Int) -> Unit = { _, _, _ -> },
    onChooseNextPlayer: (List<String>) -> Unit = {}
) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }
    val accentColor = if (option == "Verdade") NeonBlue else NeonRed

    var showActions by remember { mutableStateOf(false) }
    var pointsToAdd by remember { mutableStateOf<Int?>(null) }
    var showPointsAnimation by remember { mutableStateOf(false) }
    var showPlayerSelection by remember { mutableStateOf(false) }
    var cardEffect by remember { mutableStateOf<PowerCardManager.CardEffect?>(null) }
    var actualChallenger by remember { mutableStateOf(challenger) }
    var actualChallenged by remember { mutableStateOf(challenged) }
    var showSkipButton by remember { mutableStateOf(false) }
    var stolenFromPlayer by remember { mutableStateOf<String?>(null) } // ✅ NOVO: Rastrear vítima do roubo

    // ✅ VARIÁVEL CHAVE: Determina se mostra o botão de cartas
    val shouldShowCardsButton = playerCards.isNotEmpty() && activeCard == null

    // ✅ PROCESSAMENTO ÚNICO DA CARTA - Executar efeitos imediatos
    var cardProcessed by remember { mutableStateOf(false) }

    LaunchedEffect(activeCard) {
        if (activeCard != null && !cardProcessed) {
            cardProcessed = true // Marca como processada para evitar repetição

            cardEffect = PowerCardManager.applyCardEffect(
                card = activeCard,
                challenged = challenged,
                challenger = challenger,
                players = players,
                currentType = option
            )

            cardEffect?.let { effect ->
                if (effect.reverseRoles) {
                    actualChallenger = challenged
                    actualChallenged = challenger
                }

                // ✅ ROUBO DE PONTOS - Execução imediata com jogador aleatório
                if (effect.stealFromChallenger && effect.stealPoints > 0) {
                    audioManager.playSound(AudioManager.SoundEffect.SUCCESS)

                    // Escolher vítima aleatória (qualquer jogador exceto o que ativou a carta)
                    val possibleVictims = players.filter { it != challenged }
                    val randomVictim = possibleVictims.randomOrNull()

                    if (randomVictim != null) {
                        stolenFromPlayer = randomVictim // ✅ Registrar vítima
                        onStealPoints(randomVictim, challenged, effect.stealPoints)
                    }

                    delay(1500)
                    onNext(null, false)
                    return@LaunchedEffect  // ✅ CORRIGIDO
                }

                if (effect.choosePlayer) {
                    showPlayerSelection = true
                }

                if (effect.skipChallenge) {
                    showSkipButton = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(500)
        showActions = true
    }

    LaunchedEffect(pointsToAdd) {
        if (pointsToAdd != null) {
            showPointsAnimation = true
            if ((pointsToAdd ?: 0) > 0) {
                audioManager.playSound(AudioManager.SoundEffect.SUCCESS)
            } else if ((pointsToAdd ?: 0) < 0) {
                audioManager.playSound(AudioManager.SoundEffect.FAIL)
            }
            delay(2000)

            if (cardEffect?.extraTurn == true) {
                onNext(actualChallenged, false)
            } else {
                onNext(null, cardEffect?.reverseRoles ?: false)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Botão voltar
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

        // ✅ BOTÃO DE CARTAS - CORRIGIDO
        // Agora mostra quando o jogador TEM cartas E NÃO há carta ativa
        if (shouldShowCardsButton) {
            FloatingActionButton(
                onClick = {
                    audioManager.playSound(AudioManager.SoundEffect.CLICK)
                    onViewCards()
                },
                containerColor = Color(0xFFFFD700),
                contentColor = Color.Black,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(10f)
                    .size(80.dp)
                    .shadow(
                        elevation = 20.dp,
                        spotColor = Color(0xFFFFD700),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎴",
                        fontSize = 32.sp
                    )
                    Text(
                        text = "${playerCards.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        // Indicador de carta ativa
        if (activeCard != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
                    .zIndex(3f)
                    .shadow(
                        elevation = 20.dp,
                        spotColor = activeCard.getColor(),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    activeCard.getColor().copy(alpha = 0.3f),
                                    DarkCard
                                )
                            )
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "CARTA ATIVA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = activeCard.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = activeCard.getColor()
                        )
                        // ✅ Mostrar vítima se houver roubo
                        if (stolenFromPlayer != null) {
                            Text(
                                text = "Roubando de $stolenFromPlayer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonRed
                            )
                        }
                    }
                }
            }
        }

        // Animação de pontos
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
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    if ((pointsToAdd ?: 0) > 0) Color.Green.copy(alpha = 0.5f)
                                    else if ((pointsToAdd ?: 0) < 0) NeonRed.copy(alpha = 0.5f)
                                    else Color.Gray.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Text(
                    text = when {
                        (pointsToAdd ?: 0) > 0 -> "+${pointsToAdd}"
                        (pointsToAdd ?: 0) < 0 -> "$pointsToAdd"
                        else -> "0"
                    },
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Black,
                    color = when {
                        (pointsToAdd ?: 0) > 0 -> Color.Green
                        (pointsToAdd ?: 0) < 0 -> NeonRed
                        else -> Color.Gray
                    },
                    modifier = Modifier.shadow(elevation = 40.dp)
                )
            }
        }

        // Conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = if (activeCard != null) 60.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(250.dp, 80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.4f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        )
                )
                Text(
                    text = option.uppercase(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    modifier = Modifier.shadow(elevation = 30.dp, spotColor = accentColor)
                )
            }

            // Card com pergunta
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
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(180.dp, 50.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            NeonRed.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(25.dp)
                                )
                        )
                        Text(
                            text = actualChallenged.uppercase(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonRed,
                            modifier = Modifier.shadow(elevation = 20.dp, spotColor = NeonRed)
                        )
                    }

                    if (cardEffect?.reverseRoles == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔄 PAPÉIS INVERTIDOS!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFAA00FF)
                        )
                    }

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

            // Botões de ação
            AnimatedVisibility(
                visible = showActions,
                enter = slideInVertically(initialOffsetY = { 100 }) + fadeIn()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // BOTÃO DE PULAR (Carta de Skip)
                    if (showSkipButton) {
                        Button(
                            onClick = {
                                audioManager.playSound(AudioManager.SoundEffect.CLICK)
                                pointsToAdd = 0
                                onComplete(true, 0)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .shadow(
                                    elevation = 20.dp,
                                    spotColor = Color(0xFFAA00FF),
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAA00FF)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "⚡ PULAR SEM PENALIDADE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Botão Completou
                    val basePoints = if (option == "Verdade") ScoreRules.COMPLETE_TRUTH else ScoreRules.COMPLETE_DARE
                    val finalPoints = (basePoints * (cardEffect?.pointsMultiplier ?: 1f)).toInt()

                    Button(
                        onClick = {
                            pointsToAdd = finalPoints
                            onComplete(true, finalPoints)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .shadow(
                                elevation = 20.dp,
                                spotColor = Color.Green,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Green.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "✓ COMPLETOU (+$finalPoints pts)${if (activeCard != null) " ⚡" else ""}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão Vídeo
                    if (allowPhotos && option == "Desafio") {
                        OutlinedButton(
                            onClick = {
                                audioManager.playSound(AudioManager.SoundEffect.CLICK)
                                onRecordVideo()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = NeonBlue
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 2.dp,
                                brush = Brush.linearGradient(
                                    listOf(NeonBlue, NeonBlue.copy(alpha = 0.5f))
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GRAVAR VÍDEO",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Botão Recusou
                    val refusalPoints = if (cardEffect?.hasShield == true) 0 else ScoreRules.REFUSE_CHALLENGE

                    OutlinedButton(
                        onClick = {
                            pointsToAdd = refusalPoints
                            onComplete(false, refusalPoints)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NeonRed
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(NeonRed, NeonRed.copy(alpha = 0.5f))
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = if (cardEffect?.hasShield == true)
                                "🛡️ RECUSAR (SEM PENALIDADE)"
                            else
                                "✗ RECUSOU ($refusalPoints pts)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Dialog de seleção de jogador
        if (showPlayerSelection) {
            PlayerSelectionDialog(
                players = players.filter { it != actualChallenged },
                onPlayerSelected = { selectedPlayer ->
                    showPlayerSelection = false
                    onChooseNextPlayer(listOf(actualChallenged, selectedPlayer))
                },
                onDismiss = { showPlayerSelection = false }
            )
        }
    }
}

@Composable
fun PlayerSelectionDialog(
    players: List<String>,
    onPlayerSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "ESCOLHA O PRÓXIMO DESAFIADO",
                fontWeight = FontWeight.Black,
                color = NeonRed,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                players.forEach { player ->
                    Button(
                        onClick = { onPlayerSelected(player) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = player.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        containerColor = DarkCard
    )
}