package com.example.ecoswap.auth

/**
 * Helper class for testing authentication with the provided test users
 * This is for development/testing purposes only
 */
object AuthTestHelper {
    
    val testUsers = listOf(
        TestUser("jdoe@example.com", "jdoe123", "jdoe", "John", "Doe", 120),
        TestUser("asmith@example.com", "asmith123", "asmith", "Anna", "Smith", 80),
        TestUser("mmeyer@example.com", "mmeyer123", "mmeyer", "Maria", "Meyer", 50),
        TestUser("rnowak@example.com", "rnowak123", "rnowak", "Robert", "Nowak", 200),
        TestUser("klee@example.com", "klee123", "klee", "Karl", "Lee", 30)
    )
    
    data class TestUser(
        val email: String,
        val password: String,
        val username: String,
        val firstName: String,
        val lastName: String,
        val points: Int
    )
    
    fun getRandomTestUser(): TestUser {
        return testUsers.random()
    }
    
    fun getTestUserByEmail(email: String): TestUser? {
        return testUsers.find { it.email == email }
    }
}
