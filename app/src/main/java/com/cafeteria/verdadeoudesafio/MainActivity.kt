package com.cafeteria.verdadeoudesafio

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.cafeteria.verdadeoudesafio.database.AppDatabase
import com.cafeteria.verdadeoudesafio.database.CustomDareEntity
import com.cafeteria.verdadeoudesafio.database.CustomTruthEntity
import com.cafeteria.verdadeoudesafio.managers.AudioManager
import com.cafeteria.verdadeoudesafio.models.*
import com.cafeteria.verdadeoudesafio.repository.GameRepository
import com.cafeteria.verdadeoudesafio.screens.*
import com.cafeteria.verdadeoudesafio.ui.theme.DarkBackground
import com.cafeteria.verdadeoudesafio.ui.theme.DarkCard
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRed
import com.cafeteria.verdadeoudesafio.ui.theme.VerdadeOuDesafioTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var database: AppDatabase
    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        audioManager = AudioManager.getInstance(this)
        database = AppDatabase.getDatabase(this)
        repository = GameRepository(database)

        setContent {
            VerdadeOuDesafioTheme {
                TruthOrDareGame(
                    audioManager = audioManager,
                    repository = repository
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        audioManager.pauseBackgroundMusic()
    }

    override fun onResume() {
        super.onResume()
        audioManager.resumeBackgroundMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.release()
    }
}

@Composable
fun TruthOrDareGame(
    audioManager: AudioManager,
    repository: GameRepository
) {
    var gameState by rememberSaveable { mutableStateOf(GameState.MAIN_MENU) }
    var players by rememberSaveable { mutableStateOf(listOf<String>()) }
    val playerScores by repository.allScores.collectAsState(initial = emptyList())
    var challenger by rememberSaveable { mutableStateOf("") }
    var challenged by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf<String?>(null) }
    var currentQuestion by rememberSaveable { mutableStateOf("") }
    var bottleImageUri by remember { mutableStateOf<Uri?>(null) }
    val customTruths by repository.allTruths.collectAsState(initial = emptyList())
    val customDares by repository.allDares.collectAsState(initial = emptyList())
    var gameSettings by remember { mutableStateOf(GameSettings()) }
    var showResetDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Carregar configurações ao iniciar
    LaunchedEffect(Unit) {
        gameSettings = repository.getSettingsOnce()
        repository.getBottleImageUri()?.let { uriString ->
            bottleImageUri = Uri.parse(uriString)
        }
        // Inicializar perguntas padrão se necessário
        repository.initializeDefaultQuestions()
    }

    // Sincronizar settings com AudioManager
    LaunchedEffect(gameSettings) {
        audioManager.soundEnabled = gameSettings.soundEnabled
        audioManager.musicEnabled = gameSettings.musicEnabled
        audioManager.updateSoundVolume(gameSettings.soundVolume)
        audioManager.updateMusicVolume(gameSettings.musicVolume)
        repository.saveSettings(gameSettings)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (gameState) {
            GameState.MAIN_MENU -> MainMenuScreen(
                onPlay = { gameState = GameState.SETUP },
                onOptions = { gameState = GameState.OPTIONS },
                onReset = { showResetDialog = true }
            )

            GameState.OPTIONS -> OptionsScreen(
                bottleImageUri = bottleImageUri,
                onBottleImageChanged = { uri ->
                    bottleImageUri = uri
                    coroutineScope.launch {
                        repository.saveBottleImageUri(uri?.toString())
                    }
                },
                customTruths = customTruths,
                customDares = customDares,
                onAddTruth = { truth ->
                    coroutineScope.launch {
                        repository.addTruth(truth)
                    }
                },
                onUpdateTruth = { entity ->
                    coroutineScope.launch {
                        repository.updateTruth(entity)
                    }
                },
                onDeleteTruth = { entity ->
                    coroutineScope.launch {
                        repository.deleteTruth(entity)
                    }
                },
                onAddDare = { dare ->
                    coroutineScope.launch {
                        repository.addDare(dare)
                    }
                },
                onUpdateDare = { entity ->
                    coroutineScope.launch {
                        repository.updateDare(entity)
                    }
                },
                onDeleteDare = { entity ->
                    coroutineScope.launch {
                        repository.deleteDare(entity)
                    }
                },
                gameSettings = gameSettings,
                onSettingsChanged = { newSettings ->
                    gameSettings = newSettings
                    coroutineScope.launch {
                        repository.saveSettings(newSettings)
                    }
                },
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
                        if (customTruths.isNotEmpty()) {
                            customTruths.random().question
                        } else {
                            "Sem perguntas disponíveis"
                        }
                    } else {
                        if (customDares.isNotEmpty()) {
                            customDares.random().question
                        } else {
                            "Sem desafios disponíveis"
                        }
                    }
                    gameState = GameState.RESULT
                },
                onBack = { gameState = GameState.CHOOSE }
            )

            GameState.RESULT -> ResultScreen(
                challenged = challenged,
                option = selectedOption ?: "",
                question = currentQuestion,
                onComplete = { completed ->
                    coroutineScope.launch {
                        val score = playerScores.find { it.name == challenged }
                            ?: PlayerScore(name = challenged)

                        val updatedScore = if (completed) {
                            if (selectedOption == "Verdade") {
                                score.copy(
                                    points = score.points + ScoreRules.COMPLETE_TRUTH,
                                    truthsCompleted = score.truthsCompleted + 1
                                )
                            } else {
                                score.copy(
                                    points = score.points + ScoreRules.COMPLETE_DARE,
                                    challengesCompleted = score.challengesCompleted + 1
                                )
                            }
                        } else {
                            score.copy(
                                points = score.points + ScoreRules.REFUSE_CHALLENGE,
                                refusals = score.refusals + 1
                            )
                        }

                        repository.saveScore(updatedScore)

                        repository.addGameHistory(
                            challenger = challenger,
                            challenged = challenged,
                            questionType = selectedOption ?: "",
                            question = currentQuestion,
                            completed = completed
                        )
                    }
                },
                onTakePhoto = {
                    gameState = GameState.PHOTO_CAPTURE
                },
                onNext = {
                    selectedOption = null
                    currentQuestion = ""
                    gameState = GameState.SCOREBOARD
                },
                onBackToMenu = {
                    selectedOption = null
                    currentQuestion = ""
                    gameState = GameState.SETUP
                },
                allowPhotos = gameSettings.allowSavePhotos
            )

            GameState.PHOTO_CAPTURE -> PhotoCaptureScreen(
                challengedPlayer = challenged,
                onPhotoSelected = { uri ->
                    uri?.let {
                        coroutineScope.launch {
                            repository.addPhoto(
                                photoUri = it.toString(),
                                players = listOf(challenger, challenged),
                                challengeType = selectedOption ?: "",
                                description = ""
                            )
                        }
                    }
                    gameState = GameState.RESULT
                },
                onSkip = {
                    gameState = GameState.RESULT
                }
            )

            GameState.SCOREBOARD -> ScoreboardScreen(
                playerScores = playerScores,
                onBack = { gameState = GameState.SETUP },
                onContinue = { gameState = GameState.SPINNING }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                        text = "Resetar Pontuações?",
                        fontWeight = FontWeight.Black,
                        color = NeonRed
                    )
                },
                text = {
                    Text(
                        text = "Isso irá zerar todas as pontuações dos jogadores. Esta ação não pode ser desfeita.",
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                repository.resetAllScores()
                            }
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed)
                    ) {
                        Text("RESETAR")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("CANCELAR", color = Color.Gray)
                    }
                },
                containerColor = DarkCard
            )
        }
    }
}