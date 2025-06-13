package com.example.ecoswap.ui.awards

import com.example.ecoswap.data.db.AwardEntity
import com.example.ecoswap.data.repository.AwardRepository
import com.example.ecoswap.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
class AwardsViewModelTest {

    @Mock
    private lateinit var repository: AwardRepository

    private lateinit var viewModel: AwardsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AwardsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial loyalty points value`() = runTest {
        // Then
        assertEquals(200, viewModel.loyaltyPoints.value)
    }

    @Test
    fun `test openRewardBox with sufficient points returns reward`() = runTest {
        // Given
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "Test Award",
                winnerName = UserManager.ownerId,
                winnerId = UserManager.ownerId,
                date = Date(),
                pointsEarned = 0
            )
        )
        `when`(repository.getRecentAwards()).thenReturn(flowOf(mockAwards))

        // When
        val reward = viewModel.openRewardBox()

        // Then
        assertNotNull(reward)
        assertEquals(100, viewModel.loyaltyPoints.value) // 200 - 100 points spent
        verify(repository).addAward(
            title = anyString(),
            winnerName = eq(UserManager.ownerId),
            winnerId = eq(UserManager.ownerId),
            pointsEarned = anyInt()
        )
    }

    @Test
    fun `test openRewardBox with insufficient points returns null`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        repeat(3) { viewModel.openRewardBox() } // Spend 300 points

        // When
        val reward = viewModel.openRewardBox()

        // Then
        assertNull(reward)
        assertEquals(0, viewModel.loyaltyPoints.value)
        verify(repository, times(3)).addAward(anyString(), anyString(), anyString(), anyInt())
    }

    @Test
    fun `test openRewardBox with bonus points reward`() = runTest {
        // Given
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "50 bonus points",
                winnerName = UserManager.ownerId,
                winnerId = UserManager.ownerId,
                date = Date(),
                pointsEarned = 50
            )
        )
        `when`(repository.getRecentAwards()).thenReturn(flowOf(mockAwards))

        // When
        val reward = viewModel.openRewardBox()

        // Then
        assertNotNull(reward)
        assert(reward!!.contains("50 bonus points"))
        assertEquals(150, viewModel.loyaltyPoints.value) // 200 - 100 + 50 bonus points
        verify(repository).addAward(
            title = eq("50 bonus points"),
            winnerName = eq(UserManager.ownerId),
            winnerId = eq(UserManager.ownerId),
            pointsEarned = eq(50)
        )
    }

    @Test
    fun `test loadRecentAwards updates recentAwards state`() = runTest {
        // Given
        val mockAwards = listOf(
            AwardEntity(
                id = "1",
                title = "Test Award",
                winnerName = "User1",
                winnerId = "user1",
                date = Date(),
                pointsEarned = 100
            )
        )
        `when`(repository.getRecentAwards()).thenReturn(flowOf(mockAwards))

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.recentAwards.value.size)
        assertEquals("Test Award", viewModel.recentAwards.value[0].title)
        assertEquals("User1", viewModel.recentAwards.value[0].winnerName)
    }

    @Test
    fun `test openRewardBox with maximum points`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        val maxPoints = 1000
        viewModel._loyaltyPoints.value = maxPoints

        // When
        repeat(10) { viewModel.openRewardBox() }

        // Then
        assertEquals(maxPoints - 1000, viewModel.loyaltyPoints.value)
        verify(repository, times(10)).addAward(anyString(), anyString(), anyString(), anyInt())
    }

    @Test
    fun `test openRewardBox with exact points needed`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        viewModel._loyaltyPoints.value = 100

        // When
        val reward = viewModel.openRewardBox()

        // Then
        assertNotNull(reward)
        assertEquals(0, viewModel.loyaltyPoints.value)
        verify(repository).addAward(anyString(), anyString(), anyString(), anyInt())
    }

    @Test
    fun `test openRewardBox with different reward types`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        val rewards = mutableSetOf<String>()
        
        // When
        repeat(20) {
            viewModel._loyaltyPoints.value = 200 // Reset points
            val reward = viewModel.openRewardBox()
            if (reward != null) {
                rewards.add(reward)
            }
        }

        // Then
        assertTrue(rewards.size > 1) // Should get different types of rewards
        assertTrue(rewards.any { it.contains("discount") })
        assertTrue(rewards.any { it.contains("delivery") })
        assertTrue(rewards.any { it.contains("membership") })
        assertTrue(rewards.any { it.contains("points") })
        assertTrue(rewards.any { it.contains("badge") })
    }

    @Test
    fun `test loadRecentAwards with empty list`() = runTest {
        // Given
        `when`(repository.getRecentAwards()).thenReturn(flowOf(emptyList()))

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertTrue(viewModel.recentAwards.value.isEmpty())
    }

    @Test
    fun `test loadRecentAwards with multiple awards`() = runTest {
        // Given
        val mockAwards = listOf(
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
            ),
            AwardEntity(
                id = "3",
                title = "Award 3",
                winnerName = "User3",
                winnerId = "user3",
                date = Date(),
                pointsEarned = 300
            )
        )
        `when`(repository.getRecentAwards()).thenReturn(flowOf(mockAwards))

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(3, viewModel.recentAwards.value.size)
        assertEquals("Award 1", viewModel.recentAwards.value[0].title)
        assertEquals("Award 2", viewModel.recentAwards.value[1].title)
        assertEquals("Award 3", viewModel.recentAwards.value[2].title)
    }

    @Test
    fun `test openRewardBox updates recentAwards immediately`() = runTest {
        // Given
        val mockAwards = mutableListOf<AwardEntity>()
        `when`(repository.getRecentAwards()).thenReturn(flowOf(mockAwards))
        `when`(repository.addAward(anyString(), anyString(), anyString(), anyInt()))
            .thenAnswer { invocation ->
                val award = AwardEntity(
                    id = UUID.randomUUID().toString(),
                    title = invocation.getArgument(0),
                    winnerName = invocation.getArgument(1),
                    winnerId = invocation.getArgument(2),
                    date = Date(),
                    pointsEarned = invocation.getArgument(3)
                )
                mockAwards.add(0, award)
                Unit
            }

        // When
        val reward = viewModel.openRewardBox()

        // Then
        assertNotNull(reward)
        assertEquals(1, viewModel.recentAwards.value.size)
        assertEquals(reward, viewModel.recentAwards.value[0].title)
    }

    @Test
    fun `test openRewardBox with bonus points updates points correctly`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        var bonusPointsAwarded = false
        `when`(repository.addAward(anyString(), anyString(), anyString(), anyInt()))
            .thenAnswer { invocation ->
                if (invocation.getArgument<String>(0).contains("50 bonus points")) {
                    bonusPointsAwarded = true
                }
                Unit
            }

        // When
        var reward: String? = null
        while (!bonusPointsAwarded && viewModel.loyaltyPoints.value >= 100) {
            reward = viewModel.openRewardBox()
        }

        // Then
        assertNotNull(reward)
        assertTrue(reward!!.contains("50 bonus points"))
        assertEquals(150, viewModel.loyaltyPoints.value) // 200 - 100 + 50
    }

    @Test
    fun `test openRewardBox maintains points consistency`() = runTest {
        // Given
        viewModel = AwardsViewModel(repository)
        val initialPoints = viewModel.loyaltyPoints.value

        // When
        repeat(5) {
            if (viewModel.loyaltyPoints.value >= 100) {
                viewModel.openRewardBox()
            }
        }

        // Then
        val expectedPoints = initialPoints - (viewModel.loyaltyPoints.value / 100) * 100
        assertEquals(expectedPoints, viewModel.loyaltyPoints.value)
    }
} 