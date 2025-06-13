package com.example.ecoswap.ui.dto

import com.example.ecoswap.ui.dto.Deal

object FavoritesManager {
    val favoriteDeals = mutableListOf<Deal>()

    fun isFavorite(deal: Deal) = favoriteDeals.any { it.id == deal.id }

    fun addFavorite(deal: Deal) {
        if (!isFavorite(deal)) favoriteDeals.add(deal)
    }

    fun removeFavorite(deal: Deal) {
        favoriteDeals.removeAll { it.id == deal.id }
    }
} 