package com.example.truthordare.viewmodels

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.truthordare.models.*

class GameViewModel : ViewModel() {

    var gameData by mutableStateOf(GameData())
        private set

    // Navigation
    fun navigateToState(state: GameState) {
        gameData = gameData.copy(gameState = state)
    }

    fun navigateToMainMenu() {
        navigateToState(GameState.MAIN_MENU)
    }

    fun navigateToOptions() {
        navigateToState(GameState.OPTIONS)
    }

    fun navigateToSetup() {
        navigateToState(GameState.SETUP)
    }

    fun navigateToSpinning() {
        if (gameData.players.size >= 2) {
            navigateToState(GameState.SPINNING)
        }
    }

    // Player Management
    fun addPlayer(name: String) {
        if (name.isNotBlank()) {
            gameData = gameData.copy(
                players = gameData.players + name
            )
        }
    }

    fun removePlayer(name: String) {
        gameData = gameData.copy(
            players = gameData.players.filter { it != name }
        )
    }

    fun setPlayers(players: List<String>) {
        gameData = gameData.copy(players = players)
    }

    // Spinning Logic
    fun completeSpin() {
        val challenger = gameData.players.random()
        var challenged = gameData.players.random()

        while (challenged == challenger && gameData.players.size > 1) {
            challenged = gameData.players.random()
        }

        gameData = gameData.copy(
            currentChallenger = challenger,
            currentChallenged = challenged,
            gameState = GameState.PLAYERS_REVEAL
        )
    }

    // Choice Selection
    fun selectOption(option: String) {
        gameData = gameData.copy(
            selectedOption = option,
            gameState = GameState.QUESTION_TYPE
        )
    }

    // Question Management
    fun selectCustomQuestion() {
        gameData = gameData.copy(
            currentQuestion = "O desafiador irá falar pessoalmente",
            gameState = GameState.RESULT
        )
    }

    fun selectRandomQuestion() {
        val question = if (gameData.selectedOption == "Verdade") {
            gameData.settings.customTruths.random()
        } else {
            gameData.settings.customDares.random()
        }

        gameData = gameData.copy(
            currentQuestion = question,
            gameState = GameState.RESULT
        )
    }

    fun nextRound() {
        gameData = gameData.copy(
            currentChallenger = "",
            currentChallenged = "",
            selectedOption = null,
            currentQuestion = "",
            gameState = GameState.SPINNING
        )
    }

    // Settings Management
    fun updateBottleImage(uri: Uri?) {
        gameData = gameData.copy(
            settings = gameData.settings.copy(bottleImageUri = uri)
        )
    }

    fun addTruth(truth: String) {
        if (truth.isNotBlank()) {
            val updatedTruths = gameData.settings.customTruths.toMutableList()
            updatedTruths.add(truth)
            gameData = gameData.copy(
                settings = gameData.settings.copy(customTruths = updatedTruths)
            )
        }
    }

    fun removeTruth(index: Int) {
        val updatedTruths = gameData.settings.customTruths.toMutableList()
        if (index in updatedTruths.indices) {
            updatedTruths.removeAt(index)
            gameData = gameData.copy(
                settings = gameData.settings.copy(customTruths = updatedTruths)
            )
        }
    }

    fun addDare(dare: String) {
        if (dare.isNotBlank()) {
            val updatedDares = gameData.settings.customDares.toMutableList()
            updatedDares.add(dare)
            gameData = gameData.copy(
                settings = gameData.settings.copy(customDares = updatedDares)
            )
        }
    }

    fun removeDare(index: Int) {
        val updatedDares = gameData.settings.customDares.toMutableList()
        if (index in updatedDares.indices) {
            updatedDares.removeAt(index)
            gameData = gameData.copy(
                settings = gameData.settings.copy(customDares = updatedDares)
            )
        }
    }

    fun resetGame() {
        gameData = gameData.copy(
            currentChallenger = "",
            currentChallenged = "",
            selectedOption = null,
            currentQuestion = ""
        )
    }
}
