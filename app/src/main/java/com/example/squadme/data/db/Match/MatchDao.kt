package com.example.squadme.data.db.Match

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.squadme.data.Models.Match

@Dao
interface MatchDao {
    @Query("SELECT * FROM matches WHERE coachId = :coachId")
    fun getAllMatchesByCoachId(coachId: String): LiveData<List<Match>>
    @Insert
    suspend fun insertMatch(player: Match)
    @Update
    suspend fun updateMatch(player: Match)
    @Delete
    suspend fun deleteMatch(matchId: String)
}