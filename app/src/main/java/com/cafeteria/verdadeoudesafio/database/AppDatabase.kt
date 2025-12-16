package com.cafeteria.verdadeoudesafio.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CustomTruthEntity::class,
        CustomDareEntity::class,
        PhotoEntity::class,
        VideoEntity::class,
        GameSettingsEntity::class,
        PlayerScoreEntity::class,
        GameHistoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customTruthDao(): CustomTruthDao
    abstract fun customDareDao(): CustomDareDao
    abstract fun photoDao(): PhotoDao
    abstract fun videoDao(): VideoDao
    abstract fun gameSettingsDao(): GameSettingsDao
    abstract fun playerScoreDao(): PlayerScoreDao
    abstract fun gameHistoryDao(): GameHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "verdade_desafio_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}