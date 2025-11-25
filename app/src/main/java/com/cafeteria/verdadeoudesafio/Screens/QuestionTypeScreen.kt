package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.ui.theme.*

@Composable
fun QuestionTypeScreen(option: String, onCustom: () -> Unit, onList: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBack,
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
                text = option.uppercase(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = if (option == "Verdade") NeonBlue else NeonRed,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Text(
                text = "COMO SERÁ A ${option.uppercase()}?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 30.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onList,
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (option == "Verdade") NeonBlue else NeonRed
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "DA LISTA",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onCustom,
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 3.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            if (option == "Verdade") NeonBlue else NeonRed,
                            Color.White
                        )
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "FALADA",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "PESSOALMENTE",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}