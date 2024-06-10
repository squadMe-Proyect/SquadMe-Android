package com.example.squadme.data.db.Player

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.squadme.data.Models.Player
import java.io.Serializable


@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String,
    val coachId: String,
    val picture: String,
    val email: String,
    val name: String,
    val surname: String,
    val teamName: String,
    val nation: String,
    val numbers: Int,
    val position: String,
    val goal: Int,
    val assists: Int,
    val yellowCards: Int,
    val redCards: Int,
    val role: String
)

fun List<PlayerEntity>.asPlayerList(): List<Player> {
    return this.map {
        Player(
            it.id,
            it.coachId,
            it.picture,
            it.email,
            it.name,
            it.surname,
            it.teamName,
            it.nation,
            it.numbers,
            it.position,
            it.goal,
            it.assists,
            it.yellowCards,
            it.redCards,
            it.role
        )
    }
}
