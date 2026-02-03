package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.managers.AudioManager
import com.cafeteria.verdadeoudesafio.ui.theme.*

@Composable
fun ChooseScreen(challenged: String, onChoice: (String) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonRed.copy(alpha = 0.15f),
                            NeonBlue.copy(alpha = 0.15f),
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
                onBack()
            },
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
                text = challenged.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .shadow(elevation = 30.dp, spotColor = NeonRed)
            )

            Button(
                onClick = {
                    audioManager.playSound(AudioManager.SoundEffect.CLICK)
                    onChoice("Verdade")
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp)
                    .shadow(elevation = 30.dp, spotColor = NeonBlue, shape = RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "VERDADE",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    audioManager.playSound(AudioManager.SoundEffect.CLICK)
                    onChoice("Desafio")
                },
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp)
                    .shadow(elevation = 30.dp, spotColor = NeonRed, shape = RoundedCornerShape(20.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "DESAFIO",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}