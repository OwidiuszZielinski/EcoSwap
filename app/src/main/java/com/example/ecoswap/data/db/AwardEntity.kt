package com.example.ecoswap.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "awards")
data class AwardEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val winnerName: String,
    val winnerId: String,
    val date: Date,
    val pointsEarned: Int
) 