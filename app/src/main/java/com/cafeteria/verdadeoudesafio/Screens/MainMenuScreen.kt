package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.ui.theme.*

@Composable
fun MainMenuScreen(
    onPlay: () -> Unit,
    onOptions: () -> Unit,
    onReset: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonRed.copy(alpha = 0.2f),
                        DarkBackground,
                        DarkBackground
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onReset,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Resetar Pontuações",
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "VERDADE",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = NeonBlue,
                letterSpacing = 4.sp,
                modifier = Modifier.shadow(elevation = 30.dp, spotColor = NeonBlue)
            )
            Text(
                text = "OU",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "DESAFIO",
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed,
                letterSpacing = 4.sp,
                modifier = Modifier.shadow(elevation = 30.dp, spotColor = NeonRed)
            )

            Spacer(modifier = Modifier.height(80.dp))

            Button(
                onClick = onPlay,
                modifier = Modifier
                    .width(280.dp)
                    .height(80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "JOGAR",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onOptions,
                modifier = Modifier
                    .width(280.dp)
                    .height(70.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 3.dp,
                    brush = Brush.linearGradient(colors = listOf(NeonRed, NeonBlue))
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "OPÇÕES",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Toque no ícone ↻ para resetar pontuações",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}