package com.cafeteria.verdadeoudesafio.repository

import com.cafeteria.verdadeoudesafio.database.*
import com.cafeteria.verdadeoudesafio.models.GameSettings
import com.cafeteria.verdadeoudesafio.models.PlayerScore
import com.cafeteria.verdadeoudesafio.models.PowerCard
import com.cafeteria.verdadeoudesafio.models.dareQuestions
import com.cafeteria.verdadeoudesafio.models.truthQuestions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.lang.reflect.Type

class GameRepository(private val database: AppDatabase) {

    private val gson = Gson()

    suspend fun hasTruthsInitialized(): Boolean {
        return try {
            allTruths.first().isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun initializeDefaultQuestions() {
        if (hasTruthsInitialized()) {
            return
        }

        truthQuestions.forEach { question ->
            database.customTruthDao().insertTruth(
                CustomTruthEntity(question = question, createdAt = 0)
            )
        }

        dareQuestions.forEach { question ->
            database.customDareDao().insertDare(
                CustomDareEntity(question = question, createdAt = 0)
            )
        }
    }

    // Custom Truths
    val allTruths: Flow<List<CustomTruthEntity>> = database.customTruthDao().getAllTruths()

    suspend fun addTruth(question: String) {
        database.customTruthDao().insertTruth(
            CustomTruthEntity(
                question = question,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateTruth(entity: CustomTruthEntity) {
        database.customTruthDao().insertTruth(entity)
    }

    suspend fun deleteTruth(entity: CustomTruthEntity) {
        database.customTruthDao().deleteTruth(entity)
    }

    // Custom Dares
    val allDares: Flow<List<CustomDareEntity>> = database.customDareDao().getAllDares()

    suspend fun addDare(question: String) {
        database.customDareDao().insertDare(
            CustomDareEntity(
                question = question,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateDare(entity: CustomDareEntity) {
        database.customDareDao().insertDare(entity)
    }

    suspend fun deleteDare(entity: CustomDareEntity) {
        database.customDareDao().deleteDare(entity)
    }

    // Photos
    val allPhotos: Flow<List<PhotoEntity>> = database.photoDao().getAllPhotos()

    suspend fun addPhoto(photoUri: String, players: List<String>, challengeType: String, description: String = "") {
        val photo = PhotoEntity(
            photoUri = photoUri,
            players = gson.toJson(players),
            challengeType = challengeType,
            description = description
        )
        database.photoDao().insertPhoto(photo)
    }

    suspend fun deletePhoto(photoId: Long) {
        database.photoDao().deletePhotoById(photoId)
    }

    val allVideos: Flow<List<VideoEntity>> = database.videoDao().getAllVideos()

    suspend fun addVideo(
        videoUri: String,
        challenger: String,
        challenged: String,
        challengeType: String,
        question: String = "",
        duration: Long = 0
    ) {
        val video = VideoEntity(
            videoUri = videoUri,
            challenger = challenger,
            challenged = challenged,
            challengeType = challengeType,
            question = question,
            duration = duration
        )
        database.videoDao().insertVideo(video)
    }

    suspend fun deleteVideo(video: VideoEntity) {
        database.videoDao().deleteVideo(video)
    }

    suspend fun deleteAllVideos() {
        database.videoDao().deleteAllVideos()
    }

    val settings: Flow<GameSettings?> = database.gameSettingsDao().getSettings()
        .map { entity ->
            entity?.let {
                GameSettings(
                    soundEnabled = it.soundEnabled,
                    musicEnabled = it.musicEnabled,
                    soundVolume = it.soundVolume,
                    musicVolume = it.musicVolume,
                    allowSavePhotos = it.allowSavePhotos,
                    hapticEnabled = it.hapticEnabled
                )
            }
        }

    suspend fun getSettingsOnce(): GameSettings {
        val entity = database.gameSettingsDao().getSettingsOnce()
        return entity?.let {
            GameSettings(
                soundEnabled = it.soundEnabled,
                musicEnabled = it.musicEnabled,
                soundVolume = it.soundVolume,
                musicVolume = it.musicVolume,
                allowSavePhotos = it.allowSavePhotos,
                hapticEnabled = it.hapticEnabled
            )
        } ?: GameSettings()
    }

    suspend fun saveSettings(settings: GameSettings, bottleImageUri: String? = null) {
        val current = database.gameSettingsDao().getSettingsOnce()
        val entity = GameSettingsEntity(
            id = 1,
            soundEnabled = settings.soundEnabled,
            musicEnabled = settings.musicEnabled,
            soundVolume = settings.soundVolume,
            musicVolume = settings.musicVolume,
            allowSavePhotos = settings.allowSavePhotos,
            hapticEnabled = settings.hapticEnabled,
            bottleImageUri = bottleImageUri ?: current?.bottleImageUri
        )
        database.gameSettingsDao().insertSettings(entity)
    }

    suspend fun getBottleImageUri(): String? {
        return database.gameSettingsDao().getSettingsOnce()?.bottleImageUri
    }

    suspend fun saveBottleImageUri(uri: String?) {
        val current = database.gameSettingsDao().getSettingsOnce() ?: GameSettingsEntity()
        database.gameSettingsDao().insertSettings(current.copy(bottleImageUri = uri))
    }

    val allScores: Flow<List<PlayerScore>> = database.playerScoreDao().getAllScores()
        .map { entities ->
            entities.map { entity ->
                val cardsJson = entity.cards
                val cards = try {
                    if (cardsJson.isNotEmpty() && cardsJson != "[]") {
                        val type = object : TypeToken<List<PowerCard>>() {}.type
                        gson.fromJson<List<PowerCard>>(cardsJson, type) ?: emptyList()
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }

                PlayerScore(
                    name = entity.name,
                    points = entity.points,
                    challengesCompleted = entity.challengesCompleted,
                    truthsCompleted = entity.truthsCompleted,
                    refusals = entity.refusals,
                    consecutiveChallenges = entity.consecutiveChallenges,
                    cards = cards.toMutableList()
                )
            }
        }

    suspend fun getScoreByName(name: String): PlayerScore? {
        val entity = database.playerScoreDao().getScoreByName(name)
        return entity?.let { it ->
            val cardsJson = it.cards
            val cards = try {
                if (cardsJson.isNotEmpty() && cardsJson != "[]") {
                    val type = object : TypeToken<List<PowerCard>>() {}.type
                    gson.fromJson<List<PowerCard>>(cardsJson, type) ?: emptyList()
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                emptyList()
            }

            PlayerScore(
                name = it.name,
                points = it.points,
                challengesCompleted = it.challengesCompleted,
                truthsCompleted = it.truthsCompleted,
                refusals = it.refusals,
                consecutiveChallenges = it.consecutiveChallenges,
                cards = cards.toMutableList()
            )
        }
    }

    suspend fun saveScore(score: PlayerScore) {
        val entity = PlayerScoreEntity(
            name = score.name,
            points = score.points,
            challengesCompleted = score.challengesCompleted,
            truthsCompleted = score.truthsCompleted,
            refusals = score.refusals,
            consecutiveChallenges = score.consecutiveChallenges,
            cards = gson.toJson(score.cards)
        )
        database.playerScoreDao().insertScore(entity)
    }

    suspend fun saveScores(scores: List<PlayerScore>) {
        scores.forEach { saveScore(it) }
    }

    suspend fun resetAllScores() {
        database.playerScoreDao().resetAllScores()
    }

    // Game History
    val gameHistory: Flow<List<GameHistoryEntity>> = database.gameHistoryDao().getAllHistory()

    suspend fun addGameHistory(
        challenger: String,
        challenged: String,
        questionType: String,
        question: String,
        completed: Boolean,
        photoUri: String? = null
    ) {
        val history = GameHistoryEntity(
            challenger = challenger,
            challenged = challenged,
            questionType = questionType,
            question = question,
            completed = completed,
            photoUri = photoUri
        )
        database.gameHistoryDao().insertHistory(history)
    }

    suspend fun deleteAllHistory() {
        database.gameHistoryDao().deleteAllHistory()
    }

    suspend fun getHistoryCount(): Int {
        return database.gameHistoryDao().getHistoryCount()
    }
}