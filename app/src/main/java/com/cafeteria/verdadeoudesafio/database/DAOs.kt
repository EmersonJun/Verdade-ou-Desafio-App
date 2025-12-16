package com.cafeteria.verdadeoudesafio.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomTruthDao {
    @Query("SELECT * FROM custom_truths ORDER BY createdAt DESC")
    fun getAllTruths(): Flow<List<CustomTruthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruth(truth: CustomTruthEntity)

    @Delete
    suspend fun deleteTruth(truth: CustomTruthEntity)

    @Query("DELETE FROM custom_truths WHERE id = :id")
    suspend fun deleteTruthById(id: Long)

    @Query("DELETE FROM custom_truths")
    suspend fun deleteAllTruths()
}

@Dao
interface CustomDareDao {
    @Query("SELECT * FROM custom_dares ORDER BY createdAt DESC")
    fun getAllDares(): Flow<List<CustomDareEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDare(dare: CustomDareEntity)

    @Delete
    suspend fun deleteDare(dare: CustomDareEntity)

    @Query("DELETE FROM custom_dares WHERE id = :id")
    suspend fun deleteDareById(id: Long)

    @Query("DELETE FROM custom_dares")
    suspend fun deleteAllDares()
}

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY timestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentPhotos(limit: Int = 20): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhotoById(id: Long)

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()
}

@Dao
interface GameSettingsDao {
    @Query("SELECT * FROM game_settings WHERE id = 1")
    fun getSettings(): Flow<GameSettingsEntity?>

    @Query("SELECT * FROM game_settings WHERE id = 1")
    suspend fun getSettingsOnce(): GameSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: GameSettingsEntity)

    @Update
    suspend fun updateSettings(settings: GameSettingsEntity)
}

@Dao
interface PlayerScoreDao {
    @Query("SELECT * FROM player_scores ORDER BY points DESC")
    fun getAllScores(): Flow<List<PlayerScoreEntity>>

    @Query("SELECT * FROM player_scores WHERE name = :name")
    suspend fun getScoreByName(name: String): PlayerScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: PlayerScoreEntity)

    @Update
    suspend fun updateScore(score: PlayerScoreEntity)

    @Query("UPDATE player_scores SET points = 0, challengesCompleted = 0, truthsCompleted = 0, refusals = 0")
    suspend fun resetAllScores()

    @Delete
    suspend fun deleteScore(score: PlayerScoreEntity)
}

@Dao
interface GameHistoryDao {
    @Query("SELECT * FROM game_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<GameHistoryEntity>>

    @Query("SELECT * FROM game_history ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 50): Flow<List<GameHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: GameHistoryEntity)

    @Delete
    suspend fun deleteHistory(history: GameHistoryEntity)

    @Query("DELETE FROM game_history")
    suspend fun deleteAllHistory()

    @Query("SELECT COUNT(*) FROM game_history")
    suspend fun getHistoryCount(): Int
}
