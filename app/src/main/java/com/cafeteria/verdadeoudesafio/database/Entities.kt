package com.cafeteria.verdadeoudesafio.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_truths")
data class CustomTruthEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_dares")
data class CustomDareEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val question: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val photoUri: String,
    val players: String, // JSON list
    val challengeType: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_settings")
data class GameSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Sempre 1, só uma linha de configurações
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val soundVolume: Float = 0.7f,
    val musicVolume: Float = 0.5f,
    val allowSavePhotos: Boolean = true,
    val hapticEnabled: Boolean = true,
    val bottleImageUri: String? = null
)

@Entity(tableName = "player_scores")
data class PlayerScoreEntity(
    @PrimaryKey
    val name: String,
    val points: Int = 0,
    val challengesCompleted: Int = 0,
    val truthsCompleted: Int = 0,
    val refusals: Int = 0,
    val lastPlayed: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val challenger: String,
    val challenged: String,
    val questionType: String, // "Verdade" ou "Desafio"
    val question: String,
    val completed: Boolean,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)