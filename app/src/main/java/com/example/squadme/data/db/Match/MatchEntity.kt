package com.example.squadme.data.db.Match

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.squadme.data.Models.LineUp
import java.io.Serializable

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey(autoGenerate = false)
    var id: String,
    val coachId: String,
    val date: String,
    val opponent: String,
    val result: String,
    val squad: LineUp,
    val finished: Boolean = false
) : Serializable
