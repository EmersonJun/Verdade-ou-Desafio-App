package com.cafeteria.verdadeoudesafio.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.cafeteria.verdadeoudesafio.models.PowerCard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    val players: String,
    val challengeType: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoUri: String,
    val challenger: String,
    val challenged: String,
    val challengeType: String,
    val question: String = "",
    val duration: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_settings")
data class GameSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
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
    val consecutiveChallenges: Int = 0,
    val cards: String = "[]", // JSON das cartas
    val lastPlayed: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_history")
data class GameHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val challenger: String,
    val challenged: String,
    val questionType: String,
    val question: String,
    val completed: Boolean,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromCardsList(cards: List<PowerCard>): String {
        return gson.toJson(cards)
    }

    @TypeConverter
    fun toCardsList(cardsString: String): List<PowerCard> {
        val type = object : TypeToken<List<PowerCard>>() {}.type
        return gson.fromJson(cardsString, type) ?: emptyList()
    }
}