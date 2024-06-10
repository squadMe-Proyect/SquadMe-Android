package com.example.squadme.data.repository

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Player
import com.example.squadme.data.db.Player.PlayerDBRepository
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val dbRepository: PlayerDBRepository
) {

    suspend fun getAllPlayersByCoachId(coachId: String): LiveData<List<Player>> {
        return dbRepository.getAllPlayersByCoachId(coachId)
    }

    suspend fun insertPlayer(player: Player) {
        dbRepository.insertPlayer(player)
    }

    suspend fun updatePlayer(player: Player) {
        dbRepository.updatePlayer(player)
    }

    suspend fun deletePlayer(playerId: String) {
        dbRepository.deletePlayer(playerId)
    }
}