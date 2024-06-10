package com.example.squadme.data.db.Training

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Player
import com.example.squadme.data.Models.Training

@Dao
interface TrainingDao {
    @Query("SELECT * FROM trainings WHERE coachId = :coachId")
    fun getAllTrainingsByCoachId(coachId: String): LiveData<List<Training>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTraining(training: Training)

    @Update
    fun updateTraining(training: Training)

    @Delete
    fun deleteTraining(trainingId: String)
}