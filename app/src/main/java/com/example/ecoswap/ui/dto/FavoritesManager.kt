package com.example.ecoswap.ui.dto

import android.content.Context
import com.example.ecoswap.ui.dto.Deal

object FavoritesManager {
    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorite_ids"
    private var favoriteDeals = mutableSetOf<String>()

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        favoriteDeals = prefs.getStringSet(KEY_FAVORITES, emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    fun isFavorite(deal: Deal) = favoriteDeals.contains(deal.id)

    fun addFavorite(context: Context, deal: Deal) {
        favoriteDeals.add(deal.id)
        save(context)
    }

    fun removeFavorite(context: Context, deal: Deal) {
        favoriteDeals.remove(deal.id)
        save(context)
    }

    private fun save(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_FAVORITES, favoriteDeals).apply()
    }

    fun getFavoriteIds(): Set<String> = favoriteDeals
} 