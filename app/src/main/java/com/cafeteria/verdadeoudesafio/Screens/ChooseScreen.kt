package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.ui.theme.*

@Composable
fun ChooseScreen(challenged: String, onChoice: (String) -> Unit, onBack: () -> Unit) {
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
                text = challenged.uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed,
                modifier = Modifier.padding(bottom = 60.dp)
            )

            Button(
                onClick = { onChoice("Verdade") },
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp),
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
                onClick = { onChoice("Desafio") },
                modifier = Modifier
                    .width(280.dp)
                    .height(100.dp),
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