package com.cafeteria.verdadeoudesafio.screens

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.cafeteria.verdadeoudesafio.managers.AudioManager
import com.cafeteria.verdadeoudesafio.ui.theme.*
import kotlin.random.Random

@Composable
fun SpinningScreen(
    players: List<String>,
    bottleImageUri: Uri?,
    onSpinComplete: (String, String) -> Unit,
    onBackToMenu: () -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }

    var rotation by remember { mutableStateOf(0f) }
    var isSpinning by remember { mutableStateOf(false) }

    val rotationAnim by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
        finishedListener = {
            if (isSpinning) {
                isSpinning = false
                val challenger = players.random()
                var challenged = players.random()
                while (challenged == challenger && players.size > 1) {
                    challenged = players.random()
                }
                onSpinComplete(challenger, challenged)
            }
        },
        label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBackToMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = NeonRed,
                modifier = Modifier.size(32.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "GIRE A GARRAFA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .shadow(elevation = 30.dp, spotColor = NeonRed)
            )

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotationAnim)
                    .clickable(enabled = !isSpinning) {
                        if (!isSpinning) {
                            isSpinning = true
                            audioManager.playSound(AudioManager.SoundEffect.SPIN_START)
                            rotation += Random
                                .nextInt(1080, 2160)
                                .toFloat()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    NeonRed.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                if (bottleImageUri != null) {
                    AsyncImage(
                        model = bottleImageUri,
                        contentDescription = "Garrafa",
                        modifier = Modifier.size(280.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(text = "🍾", fontSize = 120.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = if (!isSpinning) "TOQUE PARA GIRAR" else "GIRANDO...",
                fontSize = 20.sp,
                color = if (!isSpinning) Color.White.copy(alpha = 0.7f) else NeonRed,
                fontWeight = FontWeight.Bold,
                modifier = if (isSpinning) {
                    Modifier.shadow(elevation = 20.dp, spotColor = NeonRed)
                } else {
                    Modifier
                }
            )
        }
    }
}