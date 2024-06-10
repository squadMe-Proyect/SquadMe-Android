package com.example.squadme.data.db.Player

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.squadme.data.Models.Player

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE coachId = :coachId")
    fun getAllPlayersByCoachId(coachId: String): LiveData<List<Player>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlayer(player: Player)

    @Update
    fun updatePlayer(player: Player)

    @Delete
    fun deletePlayer(playerId: String)
}