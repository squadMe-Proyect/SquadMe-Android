package com.example.squadme.data.repository

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Match
import com.example.squadme.data.db.Match.MatchDBRepository
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val dbRepository: MatchDBRepository
) {
    suspend fun getAllMatchesByCoachId(coachId: String): LiveData<List<Match>> {
        return dbRepository.getAllMatchesByCoachId(coachId)
    }

    suspend fun insertMatch(match: Match) {
        dbRepository.insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        dbRepository.updateMatch(match)
    }

    suspend fun deleteMatch(matchId: String) {
        dbRepository.deleteMatch(matchId)
    }

}