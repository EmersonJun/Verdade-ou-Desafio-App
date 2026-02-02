package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
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
import com.cafeteria.verdadeoudesafio.models.PowerCard
import com.cafeteria.verdadeoudesafio.models.CardType
import com.cafeteria.verdadeoudesafio.ui.theme.DarkBackground
import com.cafeteria.verdadeoudesafio.ui.theme.DarkCard
import kotlinx.coroutines.delay

@Composable
fun CardRevealScreen(
    card: PowerCard,
    playerName: String,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }

    var showCard by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (showCard) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (showCard) 0f else 180f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "rotation"
    )

    LaunchedEffect(Unit) {
        delay(300)
        audioManager.playSound(AudioManager.SoundEffect.SUCCESS)
        showCard = true
        delay(2000)
        showButton = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground.copy(alpha = 0.95f))
            .zIndex(100f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            AnimatedVisibility(
                visible = showCard,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -50 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🎉 CARTA CONQUISTADA! 🎉",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = card.getRarityColor(),
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .shadow(elevation = 20.dp, spotColor = card.getRarityColor())
                    )

                    Text(
                        text = playerName.uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(scale)
                    .rotate(rotation)
            ) {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .height(420.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    card.getRarityColor().copy(alpha = 0.6f),
                                    card.getColor().copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                )

                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(400.dp)
                        .shadow(
                            elevation = 30.dp,
                            spotColor = card.getColor(),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        card.getColor().copy(alpha = 0.3f),
                                        DarkCard,
                                        DarkCard
                                    )
                                )
                            )
                            .border(
                                width = 3.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        card.getRarityColor(),
                                        card.getColor(),
                                        card.getRarityColor()
                                    )
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = card.rarity.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = card.getRarityColor(),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = when (card.type) {
                                    CardType.TRUTH -> "🔵"
                                    CardType.DARE -> "🔴"
                                    CardType.UNIVERSAL -> "🟣"
                                },
                                fontSize = 48.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = card.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = card.getColor(),
                                textAlign = TextAlign.Center,
                                lineHeight = 28.sp,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .shadow(elevation = 15.dp, spotColor = card.getColor())
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = card.getColor().copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = card.description,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = when (card.type) {
                                    CardType.TRUTH -> "VERDADE"
                                    CardType.DARE -> "DESAFIO"
                                    CardType.UNIVERSAL -> "UNIVERSAL"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = card.getColor().copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 50 })
            ) {
                Button(
                    onClick = {
                        audioManager.playSound(AudioManager.SoundEffect.CLICK)
                        onContinue()
                    },
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                        .shadow(
                            elevation = 20.dp,
                            spotColor = card.getColor(),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = card.getColor()),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "CONTINUAR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}