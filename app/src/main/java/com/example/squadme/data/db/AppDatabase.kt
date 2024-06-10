package com.example.squadme.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.squadme.data.db.Match.MatchDao
import com.example.squadme.data.db.Match.MatchEntity
import com.example.squadme.data.db.Player.PlayerDao
import com.example.squadme.data.db.Player.PlayerEntity
import com.example.squadme.data.db.Training.TrainingDao
import com.example.squadme.data.db.Training.TrainingEntity

/*
@Database(entities = [PlayerEntity::class, MatchEntity::class, TrainingEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun trainingDao(): TrainingDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
        }
    }
}

 */