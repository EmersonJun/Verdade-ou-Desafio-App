package com.cafeteria.verdadeoudesafio

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.cafeteria.verdadeoudesafio.models.GameState
import com.cafeteria.verdadeoudesafio.models.dareQuestions
import com.cafeteria.verdadeoudesafio.models.truthQuestions
import com.cafeteria.verdadeoudesafio.screens.*
import com.cafeteria.verdadeoudesafio.screensscreens.PlayersRevealScreen
import com.cafeteria.verdadeoudesafio.ui.theme.DarkBackground

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