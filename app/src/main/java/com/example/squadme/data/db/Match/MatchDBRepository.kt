package com.example.squadme.data.db.Match

import androidx.lifecycle.LiveData
import com.example.squadme.data.Models.Match
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchDBRepository @Inject constructor(private val matchDao: MatchDao) {
    fun getAllMatchesByCoachId(coachId: String): LiveData<List<Match>> {
        return matchDao.getAllMatchesByCoachId(coachId)
    }

    suspend fun insertMatch(match: Match) {
        matchDao.insertMatch(match)
    }

    suspend fun updateMatch(match: Match) {
        matchDao.updateMatch(match)
    }

    suspend fun deleteMatch(matchId: String) {
        matchDao.deleteMatch(matchId)
    }
}