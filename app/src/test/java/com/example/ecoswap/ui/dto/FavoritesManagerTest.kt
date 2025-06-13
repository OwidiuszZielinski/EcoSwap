package com.example.ecoswap.ui.dto

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FavoritesManagerTest {

    @Before
    fun setup() {
        FavoritesManager.favoriteDeals.clear()
    }

    @Test
    fun `test isFavorite returns false for empty favorites`() {
        // Given
        val deal = Deal(
            id = "1",
            title = "Test Deal",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When
        val result = FavoritesManager.isFavorite(deal)

        // Then
        assertFalse(result)
    }

    @Test
    fun `test addFavorite adds deal to favorites`() {
        // Given
        val deal = Deal(
            id = "1",
            title = "Test Deal",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When
        FavoritesManager.addFavorite(deal)

        // Then
        assertTrue(FavoritesManager.isFavorite(deal))
        assertEquals(1, FavoritesManager.favoriteDeals.size)
    }

    @Test
    fun `test addFavorite does not add duplicate deals`() {
        // Given
        val deal = Deal(
            id = "1",
            title = "Test Deal",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When
        FavoritesManager.addFavorite(deal)
        FavoritesManager.addFavorite(deal)

        // Then
        assertTrue(FavoritesManager.isFavorite(deal))
        assertEquals(1, FavoritesManager.favoriteDeals.size)
    }

    @Test
    fun `test removeFavorite removes deal from favorites`() {
        // Given
        val deal = Deal(
            id = "1",
            title = "Test Deal",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )
        FavoritesManager.addFavorite(deal)

        // When
        FavoritesManager.removeFavorite(deal)

        // Then
        assertFalse(FavoritesManager.isFavorite(deal))
        assertEquals(0, FavoritesManager.favoriteDeals.size)
    }

    @Test
    fun `test removeFavorite does nothing for non-existent deal`() {
        // Given
        val deal = Deal(
            id = "1",
            title = "Test Deal",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When
        FavoritesManager.removeFavorite(deal)

        // Then
        assertFalse(FavoritesManager.isFavorite(deal))
        assertEquals(0, FavoritesManager.favoriteDeals.size)
    }

    @Test
    fun `test multiple deals in favorites`() {
        // Given
        val deal1 = Deal(
            id = "1",
            title = "Test Deal 1",
            price = 10.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Test description 1",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )
        val deal2 = Deal(
            id = "2",
            title = "Test Deal 2",
            price = 20.0,
            photoDataUrl = "base64string2",
            ownerId = "user2",
            description = "Test description 2",
            category = Category.SPORT,
            condition = Condition.GOOD
        )

        // When
        FavoritesManager.addFavorite(deal1)
        FavoritesManager.addFavorite(deal2)

        // Then
        assertTrue(FavoritesManager.isFavorite(deal1))
        assertTrue(FavoritesManager.isFavorite(deal2))
        assertEquals(2, FavoritesManager.favoriteDeals.size)

        // When removing one deal
        FavoritesManager.removeFavorite(deal1)

        // Then
        assertFalse(FavoritesManager.isFavorite(deal1))
        assertTrue(FavoritesManager.isFavorite(deal2))
        assertEquals(1, FavoritesManager.favoriteDeals.size)
    }
} 