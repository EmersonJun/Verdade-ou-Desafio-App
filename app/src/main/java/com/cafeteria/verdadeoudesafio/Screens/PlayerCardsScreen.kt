package com.cafeteria.verdadeoudesafio.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.models.PowerCard
import com.cafeteria.verdadeoudesafio.models.CardType
import com.cafeteria.verdadeoudesafio.ui.theme.DarkCard
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRed

@Composable
fun PlayerCardsScreen(
    playerName: String,
    cards: List<PowerCard>,
    onCardSelected: (PowerCard) -> Unit,
    onBack: () -> Unit
) {
    var selectedCard by remember { mutableStateOf<PowerCard?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
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
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CARTAS DE PODER",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonRed
                    )
                    Text(
                        text = playerName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "${cards.size}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            }

            if (cards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📭",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma carta ainda",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Complete desafios para ganhar cartas!",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cards) { card ->
                        CardItem(
                            card = card,
                            onClick = { selectedCard = card }
                        )
                    }
                }
            }
        }

        // Dialog de confirmação
        selectedCard?.let { card ->
            AlertDialog(
                onDismissRequest = { selectedCard = null },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = card.name,
                            fontWeight = FontWeight.Black,
                            color = card.getColor(),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = card.description,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Usar esta carta agora?",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCardSelected(card)
                            selectedCard = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = card.getColor())
                    ) {
                        Text("USAR CARTA")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedCard = null }) {
                        Text("CANCELAR", color = Color.Gray)
                    }
                },
                containerColor = DarkCard
            )
        }
    }
}

@Composable
fun CardItem(
    card: PowerCard,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(200.dp)
            .clickable(onClick = onClick)
            .shadow(
                elevation = 15.dp,
                spotColor = card.getColor(),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            card.getColor().copy(alpha = 0.3f),
                            DarkCard
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = card.getRarityColor(),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Raridade
                Text(
                    text = card.rarity.name,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = card.getRarityColor()
                )

                // Ícone
                Text(
                    text = when (card.type) {
                        CardType.TRUTH -> "🔵"
                        CardType.DARE -> "🔴"
                        CardType.UNIVERSAL -> "🟣"
                    },
                    fontSize = 32.sp
                )

                // Nome
                Text(
                    text = card.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = card.getColor(),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    maxLines = 2
                )

                // Descrição curta
                Text(
                    text = card.description,
                    fontSize = 11.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    maxLines = 3
                )
            }
        }
    }
}