package com.example.ecoswap.data.repository

import com.example.ecoswap.data.db.AwardDao
import com.example.ecoswap.data.db.AwardEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

class AwardRepository(private val awardDao: AwardDao) {

    fun getRecentAwards(limit: Int = 10): Flow<List<AwardEntity>> {
        return awardDao.getRecentAwards(limit)
    }

    fun getUserAwards(userId: String): Flow<List<AwardEntity>> {
        return awardDao.getUserAwards(userId)
    }

    suspend fun addAward(title: String, winnerName: String, winnerId: String, pointsEarned: Int) {
        val award = AwardEntity(
            title = title,
            winnerName = winnerName,
            winnerId = winnerId,
            date = Date(),
            pointsEarned = pointsEarned
        )
        awardDao.insertAward(award)
    }

    suspend fun deleteAward(award: AwardEntity) {
        awardDao.deleteAward(award)
    }

    suspend fun clearAllAwards() {
        awardDao.deleteAllAwards()
    }
} 