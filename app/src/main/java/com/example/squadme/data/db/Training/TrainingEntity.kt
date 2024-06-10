package com.example.squadme.data.db.Training

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.squadme.data.Models.Training
import com.google.firebase.Timestamp
import java.io.Serializable

@Entity(tableName = "trainings")
data class TrainingEntity(
    @PrimaryKey
    var id: String,
    val coachId: String,
    val date: Timestamp,
    val exercises: List<String> = listOf(),
    val completed: Boolean = false
)

fun List<TrainingEntity>.asTrainingList(): List<Training> {
    return this.map {
        Training(
            it.id,
            it.coachId,
            it.date,
            it.exercises,
            it.completed
        )
    }
}
