package com.example.ecoswap.data.repository

import com.example.ecoswap.data.db.AwardDao
import com.example.ecoswap.data.db.AwardEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class AwardRepositoryTest {

    @Mock
    private lateinit var awardDao: AwardDao

    private lateinit var repository: AwardRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = AwardRepository(awardDao)
    }

    @Test
    fun `test getRecentAwards returns limited awards`() = runBlocking {
        // Given
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "Test Award 1",
                winnerName = "User1",
                winnerId = "user1",
                date = Date(),
                pointsEarned = 100
            ),
            AwardEntity(
                id = "2",
                title = "Test Award 2",
                winnerName = "User2",
                winnerId = "user2",
                date = Date(),
                pointsEarned = 200
            )
        )
        `when`(awardDao.getRecentAwards(2)).thenReturn(flowOf(mockAwards))

        // When
        val result = repository.getRecentAwards(2).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Test Award 1", result[0].title)
        assertEquals("Test Award 2", result[1].title)
        verify(awardDao).getRecentAwards(2)
    }

    @Test
    fun `test getUserAwards returns user specific awards`() = runBlocking {
        // Given
        val userId = "user1"
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "User Award",
                winnerName = "User1",
                winnerId = userId,
                date = Date(),
                pointsEarned = 100
            )
        )
        `when`(awardDao.getUserAwards(userId)).thenReturn(flowOf(mockAwards))

        // When
        val result = repository.getUserAwards(userId).first()

        // Then
        assertEquals(1, result.size)
        assertEquals(userId, result[0].winnerId)
        assertEquals("User Award", result[0].title)
        verify(awardDao).getUserAwards(userId)
    }

    @Test
    fun `test addAward inserts award correctly`() = runBlocking {
        // Given
        val title = "New Award"
        val winnerName = "Test User"
        val winnerId = "testUser"
        val pointsEarned = 150

        // When
        repository.addAward(title, winnerName, winnerId, pointsEarned)

        // Then
        verify(awardDao).insertAward(argThat { award ->
            award.title == title &&
            award.winnerName == winnerName &&
            award.winnerId == winnerId &&
            award.pointsEarned == pointsEarned
        })
    }

    @Test
    fun `test deleteAward removes award correctly`() = runBlocking {
        // Given
        val award = AwardEntity(
            id = "1",
            title = "Test Award",
            winnerName = "User1",
            winnerId = "user1",
            date = Date(),
            pointsEarned = 100
        )

        // When
        repository.deleteAward(award)

        // Then
        verify(awardDao).deleteAward(award)
    }

    @Test
    fun `test clearAllAwards removes all awards`() = runBlocking {
        // When
        repository.clearAllAwards()

        // Then
        verify(awardDao).deleteAllAwards()
    }

    @Test
    fun `test getRecentAwards with zero limit returns empty list`() = runBlocking {
        // Given
        `when`(awardDao.getRecentAwards(0)).thenReturn(flowOf(emptyList()))

        // When
        val result = repository.getRecentAwards(0).first()

        // Then
        assertTrue(result.isEmpty())
        verify(awardDao).getRecentAwards(0)
    }

    @Test
    fun `test getRecentAwards with negative limit returns empty list`() = runBlocking {
        // Given
        `when`(awardDao.getRecentAwards(-1)).thenReturn(flowOf(emptyList()))

        // When
        val result = repository.getRecentAwards(-1).first()

        // Then
        assertTrue(result.isEmpty())
        verify(awardDao).getRecentAwards(-1)
    }

    @Test
    fun `test getUserAwards with non-existent user returns empty list`() = runBlocking {
        // Given
        val nonExistentUserId = "nonexistent"
        `when`(awardDao.getUserAwards(nonExistentUserId)).thenReturn(flowOf(emptyList()))

        // When
        val result = repository.getUserAwards(nonExistentUserId).first()

        // Then
        assertTrue(result.isEmpty())
        verify(awardDao).getUserAwards(nonExistentUserId)
    }

    @Test
    fun `test addAward with zero points`() = runBlocking {
        // Given
        val title = "Zero Points Award"
        val winnerName = "Test User"
        val winnerId = "testUser"
        val pointsEarned = 0

        // When
        repository.addAward(title, winnerName, winnerId, pointsEarned)

        // Then
        verify(awardDao).insertAward(argThat { award ->
            award.title == title &&
            award.winnerName == winnerName &&
            award.winnerId == winnerId &&
            award.pointsEarned == pointsEarned
        })
    }

    @Test
    fun `test addAward with negative points`() = runBlocking {
        // Given
        val title = "Negative Points Award"
        val winnerName = "Test User"
        val winnerId = "testUser"
        val pointsEarned = -50

        // When
        repository.addAward(title, winnerName, winnerId, pointsEarned)

        // Then
        verify(awardDao).insertAward(argThat { award ->
            award.title == title &&
            award.winnerName == winnerName &&
            award.winnerId == winnerId &&
            award.pointsEarned == pointsEarned
        })
    }

    @Test
    fun `test addAward with empty strings`() = runBlocking {
        // Given
        val title = ""
        val winnerName = ""
        val winnerId = ""
        val pointsEarned = 100

        // When
        repository.addAward(title, winnerName, winnerId, pointsEarned)

        // Then
        verify(awardDao).insertAward(argThat { award ->
            award.title == title &&
            award.winnerName == winnerName &&
            award.winnerId == winnerId &&
            award.pointsEarned == pointsEarned
        })
    }

    @Test
    fun `test deleteAward with non-existent award does nothing`() = runBlocking {
        // Given
        val nonExistentAward = AwardEntity(
            id = "nonexistent",
            title = "Non-existent Award",
            winnerName = "Test User",
            winnerId = "testUser",
            date = Date(),
            pointsEarned = 100
        )

        // When
        repository.deleteAward(nonExistentAward)

        // Then
        verify(awardDao).deleteAward(nonExistentAward)
    }

    @Test
    fun `test clearAllAwards after adding multiple awards`() = runBlocking {
        // Given
        val awards = listOf(
            AwardEntity(
                id = "1",
                title = "Award 1",
                winnerName = "User1",
                winnerId = "user1",
                date = Date(),
                pointsEarned = 100
            ),
            AwardEntity(
                id = "2",
                title = "Award 2",
                winnerName = "User2",
                winnerId = "user2",
                date = Date(),
                pointsEarned = 200
            )
        )

        // When
        awards.forEach { repository.addAward(it.title, it.winnerName, it.winnerId, it.pointsEarned) }
        repository.clearAllAwards()

        // Then
        verify(awardDao, times(awards.size)).insertAward(any())
        verify(awardDao).deleteAllAwards()
    }

    @Test
    fun `test getRecentAwards orders by date descending`() = runBlocking {
        // Given
        val olderDate = Date(System.currentTimeMillis() - 1000)
        val newerDate = Date()
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "Older Award",
                winnerName = "User1",
                winnerId = "user1",
                date = olderDate,
                pointsEarned = 100
            ),
            AwardEntity(
                id = "2",
                title = "Newer Award",
                winnerName = "User2",
                winnerId = "user2",
                date = newerDate,
                pointsEarned = 200
            )
        )
        `when`(awardDao.getRecentAwards(2)).thenReturn(flowOf(mockAwards))

        // When
        val result = repository.getRecentAwards(2).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Newer Award", result[0].title)
        assertEquals("Older Award", result[1].title)
        assertTrue(result[0].date.after(result[1].date))
    }
} 