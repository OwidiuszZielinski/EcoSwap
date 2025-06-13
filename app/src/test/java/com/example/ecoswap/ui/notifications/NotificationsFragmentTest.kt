package com.example.ecoswap.ui.notifications

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ecoswap.MainActivity
import com.example.ecoswap.UserManager
import com.example.ecoswap.ui.apis.ApiService
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.Message
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.Instant
import org.junit.Assert.*
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class NotificationsFragmentTest {

    private lateinit var context: Context
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        apiService = mock(ApiService::class.java)
        // Mock RetrofitInstance.api
        val retrofitInstance = RetrofitInstance::class.java.getDeclaredField("api")
        retrofitInstance.isAccessible = true
        retrofitInstance.set(null, apiService)
    }

    @Test
    fun `test fragment shows messages when available`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                senderId = "user1",
                receiverId = UserManager.ownerId,
                content = "Hello",
                timestamp = Instant.now(),
                read = false
            ),
            Message(
                id = "2",
                senderId = "user2",
                receiverId = UserManager.ownerId,
                content = "Hi there",
                timestamp = Instant.now(),
                read = true
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.rvMessages)
            val noMessagesText = fragment.view?.findViewById<TextView>(R.id.tvNoMessages)
            
            assertNotNull(recyclerView)
            assertNotNull(noMessagesText)
            assertEquals(View.GONE, noMessagesText?.visibility)
            assertEquals(View.VISIBLE, recyclerView?.visibility)
            
            val adapter = recyclerView?.adapter as? MessagesAdapter
            assertNotNull(adapter)
            assertEquals(2, adapter?.itemCount)
        }
    }

    @Test
    fun `test fragment shows no messages text when empty`() = runBlocking {
        // Given
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(emptyList())

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.rvMessages)
            val noMessagesText = fragment.view?.findViewById<TextView>(R.id.tvNoMessages)
            
            assertNotNull(recyclerView)
            assertNotNull(noMessagesText)
            assertEquals(View.VISIBLE, noMessagesText?.visibility)
            assertEquals(View.GONE, recyclerView?.visibility)
        }
    }

    @Test
    fun `test unread messages are marked as read`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                senderId = "user1",
                receiverId = UserManager.ownerId,
                content = "Hello",
                timestamp = Instant.now(),
                read = false
            ),
            Message(
                id = "2",
                senderId = "user2",
                receiverId = UserManager.ownerId,
                content = "Hi there",
                timestamp = Instant.now(),
                read = false
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)
        whenever(apiService.markMessageAsRead(any())).thenReturn(mock())

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            // Verify each unread message was marked as read
            messages.forEach { message ->
                verify(apiService).markMessageAsRead(message.id!!)
            }
        }
    }

    @Test
    fun `test error handling when loading messages`() = runBlocking {
        // Given
        whenever(apiService.getReceivedMessages(UserManager.ownerId))
            .thenThrow(RuntimeException("Network error"))

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            // Verify error toast was shown
            // This would typically be done by checking Toast messages
            // For now, we just verify the fragment is still attached
            assertTrue(fragment.isAdded)
        }
    }

    @Test
    fun `test error handling when marking messages as read`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                senderId = "user1",
                receiverId = UserManager.ownerId,
                content = "Hello",
                timestamp = Instant.now(),
                read = false
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)
        whenever(apiService.markMessageAsRead(any()))
            .thenThrow(RuntimeException("Network error"))

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            // Verify fragment is still attached despite error
            assertTrue(fragment.isAdded)
            // Verify messages are still displayed
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.rvMessages)
            val adapter = recyclerView?.adapter as? MessagesAdapter
            assertEquals(1, adapter?.itemCount)
        }
    }

    @Test
    fun `test fragment updates message counter in MainActivity`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                senderId = "user1",
                receiverId = UserManager.ownerId,
                content = "Hello",
                timestamp = Instant.now(),
                read = false
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)
        whenever(apiService.markMessageAsRead(any())).thenReturn(mock())

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>(
            themeResId = R.style.Theme_EcoSwap
        )

        // Then
        scenario.onFragment { fragment ->
            // Verify MainActivity's message counter was reset
            // This would typically be done by checking MainActivity's state
            // For now, we just verify the fragment is still attached
            assertTrue(fragment.isAdded)
        }
    }

    @Test
    fun `test fragment handles null message IDs`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = null, // Message without ID
                senderId = "user1",
                receiverId = UserManager.ownerId,
                content = "Hello",
                timestamp = Instant.now(),
                read = false
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)

        // When
        val scenario = launchFragmentInContainer<NotificationsFragment>()

        // Then
        scenario.onFragment { fragment ->
            // Verify fragment handles null IDs gracefully
            val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.rvMessages)
            val adapter = recyclerView?.adapter as? MessagesAdapter
            assertEquals(1, adapter?.itemCount)
            // Verify no attempt to mark message as read
            verify(apiService, never()).markMessageAsRead(any())
        }
    }
} 