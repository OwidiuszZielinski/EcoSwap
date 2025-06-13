package com.example.ecoswap.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AwardDao {
    @Query("SELECT * FROM awards ORDER BY date DESC")
    fun getAllAwards(): Flow<List<AwardEntity>>

    @Query("SELECT * FROM awards WHERE winnerId = :userId ORDER BY date DESC")
    fun getUserAwards(userId: String): Flow<List<AwardEntity>>

    @Query("SELECT * FROM awards ORDER BY date DESC LIMIT :limit")
    fun getRecentAwards(limit: Int = 10): Flow<List<AwardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAward(award: AwardEntity)

    @Delete
    suspend fun deleteAward(award: AwardEntity)

    @Query("DELETE FROM awards")
    suspend fun deleteAllAwards()
} 