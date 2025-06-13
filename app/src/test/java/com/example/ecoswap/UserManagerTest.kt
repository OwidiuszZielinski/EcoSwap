package com.example.ecoswap

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserManagerTest {

    @Before
    fun setup() {
        // Reset UserManager to default state before each test
        UserManager.ownerId = "rnowak"
    }

    @Test
    fun `test default ownerId is rnowak`() {
        assertEquals("rnowak", UserManager.ownerId)
    }

    @Test
    fun `test ownerId can be changed`() {
        // Given
        val newOwnerId = "newuser123"

        // When
        UserManager.ownerId = newOwnerId

        // Then
        assertEquals(newOwnerId, UserManager.ownerId)
    }

    @Test
    fun `test ownerId persists after multiple changes`() {
        // Given
        val ownerIds = listOf("user1", "user2", "user3", "user4")

        // When
        ownerIds.forEach { id ->
            UserManager.ownerId = id
        }

        // Then
        assertEquals(ownerIds.last(), UserManager.ownerId)
    }

    @Test
    fun `test ownerId can be set to empty string`() {
        // Given
        val emptyId = ""

        // When
        UserManager.ownerId = emptyId

        // Then
        assertEquals(emptyId, UserManager.ownerId)
    }

    @Test
    fun `test ownerId can be set to same value multiple times`() {
        // Given
        val ownerId = "testuser"

        // When
        repeat(5) {
            UserManager.ownerId = ownerId
        }

        // Then
        assertEquals(ownerId, UserManager.ownerId)
    }
} 