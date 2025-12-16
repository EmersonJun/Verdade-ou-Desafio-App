package com.cafeteria.verdadeoudesafio

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cafeteria.verdadeoudesafio.database.AppDatabase
import com.cafeteria.verdadeoudesafio.managers.AudioManager
import com.cafeteria.verdadeoudesafio.models.*
import com.cafeteria.verdadeoudesafio.repository.GameRepository
import com.cafeteria.verdadeoudesafio.screens.*
import com.cafeteria.verdadeoudesafio.ui.theme.DarkBackground
import com.cafeteria.verdadeoudesafio.ui.theme.DarkCard
import com.cafeteria.verdadeoudesafio.ui.theme.NeonRed
import com.cafeteria.verdadeoudesafio.ui.theme.VerdadeOuDesafioTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var database: AppDatabase
    private lateinit var repository: GameRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "=== INICIANDO APLICATIVO ===")

            audioManager = AudioManager.getInstance(this)
            database = AppDatabase.getDatabase(this)
            repository = GameRepository(database)

            setContent {
                VerdadeOuDesafioTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = DarkBackground
                    ) {
                        GameScreen(
                            audioManager = audioManager,
                            repository = repository
                        )
                    }
                }
            }

            Log.d(TAG, "=== APLICATIVO INICIADO ===")
        } catch (e: Exception) {
            Log.e(TAG, "ERRO NO ONCREATE", e)
            e.printStackTrace()
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
fun GameScreen(
    audioManager: AudioManager,
    repository: GameRepository
) {
    var isLoading by remember { mutableStateOf(true) }
    var loadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Inicialização
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Inicializando...")

                // Inicializar perguntas apenas se necessário
                if (!repository.hasTruthsInitialized()) {
                    Log.d(TAG, "Inicializando perguntas padrão...")
                    repository.initializeDefaultQuestions()
                }

                Log.d(TAG, "Inicialização completa")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro na inicialização", e)
                withContext(Dispatchers.Main) {
                    loadError = e.message ?: "Erro desconhecido"
                    isLoading = false
                }
            }
        }
    }

    when {
        isLoading -> LoadingScreen()
        loadError != null -> ErrorScreen(error = loadError!!)
        else -> TruthOrDareGame(audioManager, repository)
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = NeonRed,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "CARREGANDO...",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed
            )
        }
    }
}

@Composable
fun ErrorScreen(error: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "❌",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "ERRO AO INICIAR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = NeonRed
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TruthOrDareGame(
    audioManager: AudioManager,
    repository: GameRepository
) {
    var gameState by rememberSaveable { mutableStateOf(GameState.MAIN_MENU) }
    var players by rememberSaveable { mutableStateOf(listOf<String>()) }

    val allScores by repository.allScores.collectAsState(initial = emptyList())
    val playerScores = remember(allScores, players) {
        allScores.filter { it.name in players }
    }

    var challenger by rememberSaveable { mutableStateOf("") }
    var challenged by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf<String?>(null) }
    var currentQuestion by rememberSaveable { mutableStateOf("") }

    var bottleImageUri by remember { mutableStateOf<Uri?>(null) }
    val customTruths by repository.allTruths.collectAsState(initial = emptyList())
    val customDares by repository.allDares.collectAsState(initial = emptyList())
    val allVideos by repository.allVideos.collectAsState(initial = emptyList())

    var gameSettings by remember { mutableStateOf(GameSettings()) }
    var showResetDialog by remember { mutableStateOf(false) }

    var receivedCard by remember { mutableStateOf<PowerCard?>(null) }
    var showingCardsFor by remember { mutableStateOf<String?>(null) }
    var activeCardForPlayer by remember { mutableStateOf<PowerCard?>(null) }
    var nextPlayerOverride by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Carregar configurações
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                gameSettings = repository.getSettingsOnce()
                repository.getBottleImageUri()?.let { uriString ->
                    withContext(Dispatchers.Main) {
                        bottleImageUri = Uri.parse(uriString)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar configurações", e)
            }
        }
    }

    // Atualizar audio
    LaunchedEffect(gameSettings) {
        audioManager.soundEnabled = gameSettings.soundEnabled
        audioManager.musicEnabled = gameSettings.musicEnabled
        audioManager.updateSoundVolume(gameSettings.soundVolume)
        audioManager.updateMusicVolume(gameSettings.musicVolume)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (gameState) {
            GameState.MAIN_MENU -> MainMenuScreen(
                onPlay = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            repository.resetAllScores()
                            withContext(Dispatchers.Main) {
                                players = emptyList()
                                gameState = GameState.SETUP
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Erro ao iniciar jogo", e)
                        }
                    }
                },
                onOptions = { gameState = GameState.OPTIONS },
                onReset = { showResetDialog = true }
            )

            GameState.OPTIONS -> OptionsScreen(
                bottleImageUri = bottleImageUri,
                onBottleImageChanged = { uri ->
                    bottleImageUri = uri
                    scope.launch(Dispatchers.IO) {
                        repository.saveBottleImageUri(uri?.toString())
                    }
                },
                customTruths = customTruths,
                customDares = customDares,
                videos = allVideos,
                onAddTruth = { truth ->
                    scope.launch(Dispatchers.IO) {
                        repository.addTruth(truth)
                    }
                },
                onUpdateTruth = { entity ->
                    scope.launch(Dispatchers.IO) {
                        repository.updateTruth(entity)
                    }
                },
                onDeleteTruth = { entity ->
                    scope.launch(Dispatchers.IO) {
                        repository.deleteTruth(entity)
                    }
                },
                onAddDare = { dare ->
                    scope.launch(Dispatchers.IO) {
                        repository.addDare(dare)
                    }
                },
                onUpdateDare = { entity ->
                    scope.launch(Dispatchers.IO) {
                        repository.updateDare(entity)
                    }
                },
                onDeleteDare = { entity ->
                    scope.launch(Dispatchers.IO) {
                        repository.deleteDare(entity)
                    }
                },
                onDeleteVideo = { video ->
                    scope.launch(Dispatchers.IO) {
                        repository.deleteVideo(video)
                    }
                },
                gameSettings = gameSettings,
                onSettingsChanged = { newSettings ->
                    gameSettings = newSettings
                    scope.launch(Dispatchers.IO) {
                        repository.saveSettings(newSettings)
                    }
                },
                onBack = { gameState = GameState.MAIN_MENU }
            )

            GameState.SETUP -> SetupScreen(
                players = players,
                onPlayersChanged = { newPlayers ->
                    players = newPlayers
                    scope.launch(Dispatchers.IO) {
                        newPlayers.forEach { playerName ->
                            if (allScores.none { it.name == playerName }) {
                                repository.saveScore(PlayerScore(name = playerName))
                            }
                        }
                    }
                },
                onStart = {
                    if (players.size >= 2) {
                        gameState = GameState.SPINNING
                    }
                },
                onBack = {
                    scope.launch(Dispatchers.IO) {
                        repository.resetAllScores()
                        withContext(Dispatchers.Main) {
                            players = emptyList()
                            gameState = GameState.MAIN_MENU
                        }
                    }
                }
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
                        customTruths.randomOrNull()?.question ?: "Sem perguntas"
                    } else {
                        customDares.randomOrNull()?.question ?: "Sem desafios"
                    }
                    gameState = GameState.RESULT
                },
                onBack = { gameState = GameState.CHOOSE }
            )

            GameState.RESULT -> {
                val currentPlayer = playerScores.find { it.name == challenged }

                ResultScreen(
                    challenged = challenged,
                    challenger = challenger,
                    option = selectedOption ?: "",
                    question = currentQuestion,
                    players = players,
                    onComplete = { completed, points ->
                        scope.launch(Dispatchers.IO) {
                            try {
                                val score = playerScores.find { it.name == challenged }
                                    ?: PlayerScore(name = challenged)

                                val updatedScore = if (completed) {
                                    if (selectedOption == "Verdade") {
                                        score.copy(
                                            points = score.points + points,
                                            truthsCompleted = score.truthsCompleted + 1
                                        )
                                    } else {
                                        score.copy(
                                            points = score.points + points,
                                            challengesCompleted = score.challengesCompleted + 1,
                                            consecutiveChallenges = score.consecutiveChallenges + 1
                                        )
                                    }
                                } else {
                                    score.copy(
                                        points = score.points + points,
                                        refusals = score.refusals + 1,
                                        consecutiveChallenges = 0
                                    )
                                }

                                repository.saveScore(updatedScore)

                                if (completed && selectedOption == "Desafio" && points > 0) {
                                    val shouldReceive = CardManager.shouldReceiveCard(updatedScore.consecutiveChallenges)
                                    if (shouldReceive) {
                                        val card = PowerCards.getRandomCard(updatedScore.consecutiveChallenges)
                                        if (card != null) {
                                            withContext(Dispatchers.Main) {
                                                receivedCard = card
                                                gameState = GameState.CARD_REVEAL
                                            }
                                            return@launch
                                        }
                                    }
                                }

                                withContext(Dispatchers.Main) {
                                    activeCardForPlayer = null
                                }

                                repository.addGameHistory(
                                    challenger = challenger,
                                    challenged = challenged,
                                    questionType = selectedOption ?: "",
                                    question = currentQuestion,
                                    completed = completed
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Erro ao completar", e)
                            }
                        }
                    },
                    onRecordVideo = { gameState = GameState.VIDEO_CAPTURE },
                    onNext = { extraTurnPlayer, _ ->
                        selectedOption = null
                        currentQuestion = ""
                        if (extraTurnPlayer != null) {
                            nextPlayerOverride = extraTurnPlayer
                        }
                        gameState = GameState.SCOREBOARD
                    },
                    onBackToMenu = {
                        selectedOption = null
                        currentQuestion = ""
                        activeCardForPlayer = null
                        gameState = GameState.SETUP
                    },
                    allowPhotos = gameSettings.allowSavePhotos,
                    playerCards = currentPlayer?.cards ?: emptyList(),
                    activeCard = activeCardForPlayer,
                    onViewCards = {
                        showingCardsFor = challenged
                        gameState = GameState.PLAYER_CARDS
                    },
                    onStealPoints = { from, to, points ->
                        scope.launch(Dispatchers.IO) {
                            val fromScore = playerScores.find { it.name == from }
                            val toScore = playerScores.find { it.name == to }
                            if (fromScore != null && toScore != null) {
                                repository.saveScore(fromScore.copy(points = maxOf(0, fromScore.points - points)))
                                repository.saveScore(toScore.copy(points = toScore.points + points))
                            }
                        }
                    },
                    onChooseNextPlayer = { selectedPlayers ->
                        if (selectedPlayers.size == 2) {
                            challenger = selectedPlayers[0]
                            challenged = selectedPlayers[1]
                        }
                    }
                )
            }

            GameState.VIDEO_CAPTURE -> VideoRecordScreen(
                challenger = challenger,
                challenged = challenged,
                challengeType = selectedOption ?: "",
                question = currentQuestion,
                onVideoRecorded = { uri, duration ->
                    scope.launch(Dispatchers.IO) {
                        repository.addVideo(
                            videoUri = uri.toString(),
                            challenger = challenger,
                            challenged = challenged,
                            challengeType = selectedOption ?: "",
                            question = currentQuestion,
                            duration = duration
                        )
                        withContext(Dispatchers.Main) {
                            gameState = GameState.RESULT
                        }
                    }
                },
                onSkip = { gameState = GameState.RESULT }
            )

            GameState.SCOREBOARD -> ScoreboardScreen(
                playerScores = playerScores,
                onBack = { gameState = GameState.SETUP },
                onContinue = {
                    if (nextPlayerOverride != null) {
                        challenged = nextPlayerOverride!!
                        nextPlayerOverride = null
                    }
                    gameState = GameState.SPINNING
                }
            )

            GameState.CARD_REVEAL -> {
                receivedCard?.let { card ->
                    CardRevealScreen(
                        card = card,
                        playerName = challenged,
                        onContinue = {
                            scope.launch(Dispatchers.IO) {
                                val score = playerScores.find { it.name == challenged }
                                if (score != null) {
                                    val updatedScore = score.copy(
                                        cards = (score.cards + card).toMutableList()
                                    )
                                    repository.saveScore(updatedScore)
                                }
                                withContext(Dispatchers.Main) {
                                    receivedCard = null
                                    activeCardForPlayer = null
                                    gameState = GameState.SCOREBOARD
                                }
                            }
                        }
                    )
                }
            }

            GameState.PLAYER_CARDS -> {
                showingCardsFor?.let { playerName ->
                    val player = playerScores.find { it.name == playerName }
                    PlayerCardsScreen(
                        playerName = playerName,
                        cards = player?.cards ?: emptyList(),
                        onCardSelected = { card ->
                            scope.launch(Dispatchers.IO) {
                                if (player != null) {
                                    val updatedCards = player.cards.toMutableList()
                                    updatedCards.remove(card)
                                    val updatedScore = player.copy(cards = updatedCards)
                                    repository.saveScore(updatedScore)
                                    withContext(Dispatchers.Main) {
                                        activeCardForPlayer = card
                                    }
                                }
                                withContext(Dispatchers.Main) {
                                    showingCardsFor = null
                                    gameState = GameState.RESULT
                                }
                            }
                        },
                        onBack = {
                            showingCardsFor = null
                            gameState = GameState.RESULT
                        }
                    )
                }
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = {
                    Text(
                        text = "Resetar Tudo?",
                        fontWeight = FontWeight.Black,
                        color = NeonRed
                    )
                },
                text = {
                    Column {
                        Text("Isso irá zerar:", color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Todas as pontuações", color = Color.White)
                        Text("• Todas as cartas", color = Color.White)
                        Text("• Lista de jogadores", color = Color.White)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                repository.resetAllScores()
                                withContext(Dispatchers.Main) {
                                    players = emptyList()
                                    showResetDialog = false
                                }
                            }
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