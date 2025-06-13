package com.example.ecoswap.ui.awards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecoswap.data.db.AwardEntity
import com.example.ecoswap.data.repository.AwardRepository
import com.example.ecoswap.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class AwardsViewModel(private val repository: AwardRepository) : ViewModel() {

    private val _loyaltyPoints = MutableStateFlow(200)
    val loyaltyPoints: StateFlow<Int> = _loyaltyPoints

    private val _recentAwards = MutableStateFlow<List<RecentAward>>(emptyList())
    val recentAwards: StateFlow<List<RecentAward>> = _recentAwards

    private val possibleRewards = listOf(
        "10% discount on next rental",
        "Free delivery",
        "24h premium membership",
        "50 bonus points",
        "Special badge"
    )

    init {
        loadRecentAwards()
    }

    private fun loadRecentAwards() {
        viewModelScope.launch {
            repository.getRecentAwards().map { entities ->
                entities.map { it.toRecentAward() }
            }.collect { awards ->
                _recentAwards.value = awards
            }
        }
    }

    fun openRewardBox(): String? {
        if (_loyaltyPoints.value >= 100) {
            _loyaltyPoints.value -= 100
            val reward = possibleRewards.random()
            var message = "Congratulations! You won: $reward"
            var pointsEarned = 0
            if (reward == "50 bonus points") {
                _loyaltyPoints.value += 50
                message = "Congratulations! You won 50 bonus points!"
                pointsEarned = 50
            }
            viewModelScope.launch {
                repository.addAward(
                    title = reward,
                    winnerName = UserManager.ownerId,
                    winnerId = UserManager.ownerId,
                    pointsEarned = pointsEarned
                )
            }
            return message
        }
        return null
    }

    private fun AwardEntity.toRecentAward() = RecentAward(
        id = id,
        title = title,
        winnerName = winnerName,
        date = date
    )

    class Factory(private val repository: AwardRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AwardsViewModel::class.java)) {
                return AwardsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 