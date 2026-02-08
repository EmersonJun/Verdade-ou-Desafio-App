package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PlayersRevealScreen(
    challenger: String,
    challenged: String,
    onContinue: () -> Unit
) {
    var showBlue by remember { mutableStateOf(false) }
    var showRed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        showBlue = true
        delay(400)
        showRed = true
        delay(3000)
        onContinue()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showBlue,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(800)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(NeonBlue, NeonBlue.copy(alpha = 0.8f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DESAFIADOR",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = challenger,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showRed,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(800)),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(NeonRed.copy(alpha = 0.8f), NeonRed)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "DESAFIADO",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = challenged,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}