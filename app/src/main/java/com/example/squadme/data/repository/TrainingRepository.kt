package com.example.squadme.data.repository

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Training
import com.example.squadme.data.db.Training.TrainingDBRepository
import javax.inject.Inject

class TrainingRepository @Inject constructor(
    private val dbRepository: TrainingDBRepository
) {
    suspend fun getAllTrainingsByCoachId(coachId: String): LiveData<List<Training>> {
        return dbRepository.getAllTrainingsByCoachId(coachId)
    }

    suspend fun insertTraining(training: Training) {
        dbRepository.insertTraining(training)
    }

    suspend fun updateTraining(training: Training) {
        dbRepository.updateTraining(training)
    }

    suspend fun deleteTraining(trainingId: String) {
        dbRepository.deleteTraining(trainingId)
    }
}