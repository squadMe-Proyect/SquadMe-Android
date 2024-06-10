package com.example.squadme.data.db.Training

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Match
import com.example.squadme.data.Models.Training
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingDBRepository @Inject constructor(private val trainingDao: TrainingDao) {

    suspend fun getAllTrainingsByCoachId(coachId: String): LiveData<List<Training>> {
        return trainingDao.getAllTrainingsByCoachId(coachId)
    }

    suspend fun insertTraining(match: Training) {
        trainingDao.insertTraining(match)
    }

    suspend fun updateTraining(training: Training) {
        trainingDao.updateTraining(training)
    }

    suspend fun deleteTraining(trainingId: String) {
        trainingDao.deleteTraining(trainingId)
    }
}