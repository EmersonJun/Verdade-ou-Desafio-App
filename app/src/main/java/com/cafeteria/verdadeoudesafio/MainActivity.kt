package com.example.truthordare

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                TruthOrDareGame()
            }
        }
    }
}

val NeonRed = Color(0xFFFF0040)
val NeonRedGlow = Color(0xFFFF1744)
val DarkBackground = Color(0xFF0A0A0A)
val DarkCard = Color(0xFF1A1A1A)
val NeonBlue = Color(0xFF00D4FF)

@Composable
fun TruthOrDareGame() {
    var gameState by remember { mutableStateOf(GameState.MAIN_MENU) }
    var players by remember { mutableStateOf(listOf<String>()) }
    var challenger by remember { mutableStateOf("") }
    var challenged by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var currentQuestion by remember { mutableStateOf("") }
    var bottleImageUri by remember { mutableStateOf<Uri?>(null) }
    var customTruths by remember { mutableStateOf(truthQuestions.toMutableList()) }
    var customDares by remember { mutableStateOf(dareQuestions.toMutableList()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (gameState) {
            GameState.MAIN_MENU -> MainMenuScreen(
                onPlay = { gameState = GameState.SETUP },
                onOptions = { gameState = GameState.OPTIONS }
            )
            GameState.OPTIONS -> OptionsScreen(
                bottleImageUri = bottleImageUri,
                onBottleImageChanged = { bottleImageUri = it },
                customTruths = customTruths,
                customDares = customDares,
                onTruthsChanged = { customTruths = it },
                onDaresChanged = { customDares = it },
                onBack = { gameState = GameState.MAIN_MENU }
            )
            GameState.SETUP -> SetupScreen(
                players = players,
                onPlayersChanged = { players = it },
                onStart = {
                    if (players.size >= 2) {
                        gameState = GameState.SPINNING
                    }
                },
                onBack = { gameState = GameState.MAIN_MENU }
            )
            GameState.SPINNING -> SpinningScreen(
                players = players,
                bottleImageUri = bottleImageUri,
                onSpinComplete = { ch, chd ->
                    challenger = ch
                    challenged = chd
                    gameState = GameState.PLAYERS_REVEAL
                },
                onBackToMenu = { gameState = GameState.SETUP }
            )
            GameState.PLAYERS_REVEAL -> PlayersRevealScreen(
                challenger = challenger,
                challenged = challenged,
                onContinue = { gameState = GameState.CHOOSE }
            )
            GameState.CHOOSE -> ChooseScreen(
                challenged = challenged,
                onChoice = { choice ->
                    selectedOption = choice
                    gameState = GameState.QUESTION_TYPE
                },
                onBack = { gameState = GameState.SPINNING }
            )
            GameState.QUESTION_TYPE -> QuestionTypeScreen(
                option = selectedOption ?: "",
                onCustom = {
                    currentQuestion = "O desafiador irá falar pessoalmente"
                    gameState = GameState.RESULT
                },
                onList = {
                    currentQuestion = if (selectedOption == "Verdade") {
                        customTruths.random()
                    } else {
                        customDares.random()
                    }
                    gameState = GameState.RESULT
                },
                onBack = { gameState = GameState.CHOOSE }
            )
            GameState.RESULT -> ResultScreen(
                challenged = challenged,
                option = selectedOption ?: "",
                question = currentQuestion,
                onNext = {
                    selectedOption = null
                    currentQuestion = ""
                    gameState = GameState.SPINNING
                },
                onBackToMenu = {
                    selectedOption = null
                    currentQuestion = ""
                    gameState = GameState.SETUP
                }
            )
        }
    }
}

@Composable
fun MainMenuScreen(onPlay: () -> Unit, onOptions: () -> Unit) {
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
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(
    bottleImageUri: Uri?,
    onBottleImageChanged: (Uri?) -> Unit,
    customTruths: MutableList<String>,
    customDares: MutableList<String>,
    onTruthsChanged: (MutableList<String>) -> Unit,
    onDaresChanged: (MutableList<String>) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var newTruth by remember { mutableStateOf("") }
    var newDare by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onBottleImageChanged(uri)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "OPÇÕES",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonRed
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonRed.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = if (bottleImageUri != null) "MUDAR IMAGEM DA GARRAFA" else "ESCOLHER IMAGEM DA GARRAFA",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCard,
                contentColor = NeonRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "VERDADES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (selectedTab == 0) NeonBlue else Color.Gray
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "DESAFIOS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (selectedTab == 1) NeonRed else Color.Gray
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> QuestionsListTab(
                    questions = customTruths,
                    newQuestion = newTruth,
                    onNewQuestionChanged = { newTruth = it },
                    onAdd = {
                        if (newTruth.isNotBlank()) {
                            customTruths.add(newTruth)
                            onTruthsChanged(customTruths)
                            newTruth = ""
                        }
                    },
                    onDelete = { index ->
                        customTruths.removeAt(index)
                        onTruthsChanged(customTruths)
                    },
                    color = NeonBlue
                )
                1 -> QuestionsListTab(
                    questions = customDares,
                    newQuestion = newDare,
                    onNewQuestionChanged = { newDare = it },
                    onAdd = {
                        if (newDare.isNotBlank()) {
                            customDares.add(newDare)
                            onDaresChanged(customDares)
                            newDare = ""
                        }
                    },
                    onDelete = { index ->
                        customDares.removeAt(index)
                        onDaresChanged(customDares)
                    },
                    color = NeonRed
                )
            }
        }
    }
}

@Composable
fun QuestionsListTab(
    questions: List<String>,
    newQuestion: String,
    onNewQuestionChanged: (String) -> Unit,
    onAdd: () -> Unit,
    onDelete: (Int) -> Unit,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ADICIONAR NOVA",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = newQuestion,
                    onValueChange = onNewQuestionChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    placeholder = { Text("Digite aqui...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = color,
                        unfocusedBorderColor = Color.Gray
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(12.dp),
                    enabled = newQuestion.isNotBlank()
                ) {
                    Text("ADICIONAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "LISTA (${questions.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(questions) { index, question ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question,
                            color = Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onDelete(index) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = NeonRed
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
    players: List<String>,
    onPlayersChanged: (List<String>) -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    var newPlayerName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        }

        Spacer(modifier = Modifier.weight(0.3f))

        Text(
            text = "ADICIONAR JOGADORES",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = NeonRed,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .border(
                        width = 2.dp,
                        color = NeonRed.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = newPlayerName,
                    onValueChange = { newPlayerName = it },
                    label = { Text("Nome do jogador") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = NeonRed,
                        unfocusedBorderColor = NeonRed.copy(alpha = 0.5f),
                        focusedLabelColor = NeonRed,
                        unfocusedLabelColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (newPlayerName.isNotBlank()) {
                            onPlayersChanged(players + newPlayerName)
                            newPlayerName = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("ADICIONAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (players.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(players) { player ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = NeonRed.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "👤 $player",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(
                                onClick = {
                                    onPlayersChanged(players.filter { it != player })
                                }
                            ) {
                                Text("REMOVER", color = NeonRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStart,
            enabled = players.size >= 2,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonRed,
                disabledContainerColor = Color.DarkGray
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (players.size < 2) "ADICIONE 2+ JOGADORES" else "COMEÇAR JOGO",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.weight(0.2f))
    }
}

@Composable
fun SpinningScreen(
    players: List<String>,
    bottleImageUri: Uri?,
    onSpinComplete: (String, String) -> Unit,
    onBackToMenu: () -> Unit
) {
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
                modifier = Modifier.padding(bottom = 40.dp)
            )

            Box(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotationAnim)
                    .clickable(enabled = !isSpinning) {
                        if (!isSpinning) {
                            isSpinning = true
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
                fontWeight = FontWeight.Bold
            )
        }
    }
}

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

    Column(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showBlue,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(800, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(800)),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
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
                .weight(1f)
                .fillMaxWidth()
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

@Composable
fun ChooseScreen(challenged: String, onChoice: (String) -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Botão de voltar no topo
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

@Composable
fun QuestionTypeScreen(option: String, onCustom: () -> Unit, onList: () -> Unit, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Botão de voltar no topo
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

@Composable
fun ResultScreen(
    challenged: String,
    option: String,
    question: String,
    onNext: () -> Unit,
    onBackToMenu: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = onBackToMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar ao Menu",
                tint = NeonRed,
                modifier = Modifier.size(32.dp)
            )
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
                color = if (option == "Verdade") NeonBlue else NeonRed
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
                            color = if (option == "Verdade") NeonBlue else NeonRed,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = challenged.uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonRed
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

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "PRÓXIMA RODADA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

enum class GameState {
    MAIN_MENU, OPTIONS, SETUP, SPINNING, PLAYERS_REVEAL, CHOOSE, QUESTION_TYPE, RESULT
}

val truthQuestions = listOf(
    "Qual foi a maior vergonha que você já passou em público?",
    "Qual foi a coisa mais idiota que você já fez por amor?",
    "Se pudesse trocar de vida com alguém por um dia, quem seria?",
    "Qual o seu maior crush de todos os tempos (famoso ou não)?",
    "Já mentiu para sair de um encontro? O que disse?",
    "Qual o apelido mais estranho que já te deram?",
    "Se tivesse que comer apenas uma coisa pro resto da vida, o que seria?",
    "Qual foi a última mensagem que te deixou com vergonha?",
    "Se pudesse apagar um momento da sua vida, qual seria?",
    "Qual foi o maior mico que você já pagou na escola ou trabalho?"
)

val dareQuestions = listOf(
    "Imite alguém famoso por 10 segundos.",
    "Cante o refrão da primeira música que vier na sua cabeça.",
    "Dance sem música por 20 segundos.",
    "Fale com voz de bebê até sua próxima vez.",
    "Conte uma piada ruim como se fosse a mais engraçada do mundo.",
    "Envie um emoji aleatório para a última pessoa do seu WhatsApp.",
    "Fale um trava-língua sem errar — se errar, faz uma careta!",
    "Finja que está dando uma entrevista para um programa de TV.",
    "Diga o nome de três frutas o mais rápido que conseguir (sem repetir).",
    "Faça uma pose de super-herói e mantenha por 10 segundos."
)
