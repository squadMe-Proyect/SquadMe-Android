package com.example.squadme.data.db.Player

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Player
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerDBRepository @Inject constructor(private val playerDao: PlayerDao) {

    suspend fun getAllPlayersByCoachId(coachId: String): LiveData<List<Player>> {
        return playerDao.getAllPlayersByCoachId(coachId)
    }

    suspend fun insertPlayer(player: Player) {
        playerDao.insertPlayer(player)
    }

    suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player)
    }

    suspend fun deletePlayer(playerId: String) {
        playerDao.deletePlayer(playerId)
    }
}