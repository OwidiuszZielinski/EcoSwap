package com.example.ecoswap.ui

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ActivityScenario
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
import android.widget.EditText
import android.widget.Button
import androidx.test.platform.app.InstrumentationRegistry

@RunWith(AndroidJUnit4::class)
class ChatActivityTest {

    private lateinit var context: Context
    private lateinit var apiService: ApiService
    private val testReceiverId = "testReceiver"
    private val testUserName = "Test User"

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
    fun `test chat activity loads messages correctly`() = runBlocking {
        // Given
        val messages = listOf(
            Message(
                id = "1",
                senderId = UserManager.ownerId,
                receiverId = testReceiverId,
                content = "Hello",
                timestamp = Instant.now(),
                read = true
            ),
            Message(
                id = "2",
                receiverId = UserManager.ownerId,
                senderId = testReceiverId,
                content = "Hi there",
                timestamp = Instant.now(),
                read = false
            )
        )
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(messages)
        whenever(apiService.getSentMessages(UserManager.ownerId)).thenReturn(emptyList())

        // When
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("receiverId", testReceiverId)
            putExtra("userName", testUserName)
        }
        val scenario = ActivityScenario.launch<ChatActivity>(intent)

        // Then
        scenario.onActivity { activity ->
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rvMessages)
            val adapter = recyclerView.adapter as MessagesAdapter
            assertEquals(2, adapter.itemCount)
            // Verify messages are sorted by timestamp
            val firstMessage = adapter.getMessages()[0]
            val secondMessage = adapter.getMessages()[1]
            assertTrue(firstMessage.timestamp!!.isBefore(secondMessage.timestamp!!))
        }
    }

    @Test
    fun `test sending message updates chat`() = runBlocking {
        // Given
        val newMessage = Message(
            id = "3",
            senderId = UserManager.ownerId,
            receiverId = testReceiverId,
            content = "New message",
            timestamp = Instant.now(),
            read = false
        )
        whenever(apiService.sendMessage(any())).thenReturn(newMessage)
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(emptyList())
        whenever(apiService.getSentMessages(UserManager.ownerId)).thenReturn(listOf(newMessage))

        // When
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("receiverId", testReceiverId)
            putExtra("userName", testUserName)
        }
        val scenario = ActivityScenario.launch<ChatActivity>(intent)

        // Then
        scenario.onActivity { activity ->
            val messageInput = activity.findViewById<EditText>(R.id.etMessage)
            val sendButton = activity.findViewById<Button>(R.id.btnSend)
            
            // Type and send message
            messageInput.setText("New message")
            sendButton.performClick()

            // Verify message was sent
            verify(apiService).sendMessage(any())
            
            // Verify RecyclerView was updated
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rvMessages)
            val adapter = recyclerView.adapter as MessagesAdapter
            assertEquals(1, adapter.itemCount)
            assertEquals("New message", adapter.getMessages()[0].content)
        }
    }

    @Test
    fun `test empty message is not sent`() = runBlocking {
        // Given
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(emptyList())
        whenever(apiService.getSentMessages(UserManager.ownerId)).thenReturn(emptyList())

        // When
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("receiverId", testReceiverId)
            putExtra("userName", testUserName)
        }
        val scenario = ActivityScenario.launch<ChatActivity>(intent)

        // Then
        scenario.onActivity { activity ->
            val messageInput = activity.findViewById<EditText>(R.id.etMessage)
            val sendButton = activity.findViewById<Button>(R.id.btnSend)
            
            // Try to send empty message
            messageInput.setText("")
            sendButton.performClick()

            // Verify no message was sent
            verify(apiService, never()).sendMessage(any())
            
            // Verify RecyclerView is still empty
            val recyclerView = activity.findViewById<RecyclerView>(R.id.rvMessages)
            val adapter = recyclerView.adapter as MessagesAdapter
            assertEquals(0, adapter.itemCount)
        }
    }

    @Test
    fun `test error handling when loading messages`() = runBlocking {
        // Given
        whenever(apiService.getReceivedMessages(UserManager.ownerId))
            .thenThrow(RuntimeException("Network error"))
        whenever(apiService.getSentMessages(UserManager.ownerId))
            .thenReturn(emptyList())

        // When
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("receiverId", testReceiverId)
            putExtra("userName", testUserName)
        }
        val scenario = ActivityScenario.launch<ChatActivity>(intent)

        // Then
        scenario.onActivity { activity ->
            // Verify error toast was shown
            // This would typically be done by checking Toast messages
            // For now, we just verify the activity is still running
            assertFalse(activity.isFinishing)
        }
    }

    @Test
    fun `test error handling when sending message`() = runBlocking {
        // Given
        whenever(apiService.getReceivedMessages(UserManager.ownerId)).thenReturn(emptyList())
        whenever(apiService.getSentMessages(UserManager.ownerId)).thenReturn(emptyList())
        whenever(apiService.sendMessage(any())).thenThrow(RuntimeException("Network error"))

        // When
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra("receiverId", testReceiverId)
            putExtra("userName", testUserName)
        }
        val scenario = ActivityScenario.launch<ChatActivity>(intent)

        // Then
        scenario.onActivity { activity ->
            val messageInput = activity.findViewById<EditText>(R.id.etMessage)
            val sendButton = activity.findViewById<Button>(R.id.btnSend)
            
            // Try to send message
            messageInput.setText("Test message")
            sendButton.performClick()

            // Verify error toast was shown
            // This would typically be done by checking Toast messages
            // For now, we just verify the activity is still running
            assertFalse(activity.isFinishing)
        }
    }
}

// Extension function to get messages from adapter
private fun MessagesAdapter.getMessages(): List<Message> {
    return this::class.java.getDeclaredField("messages")
        .apply { isAccessible = true }
        .get(this) as List<Message>
} 