package com.example.ecoswap.ui.apis

import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.ItemResponse
import com.example.ecoswap.ui.dto.Message
import com.example.ecoswap.ui.dto.User
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import com.google.gson.GsonBuilder
import java.lang.reflect.Type
import com.google.gson.JsonSerializer
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonDeserializationContext

class ApiServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        
        val gson = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
            .setLenient()
            .create()

        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getBestDeals returns list of deals`() = runBlocking {
        // Given
        val mockResponse = """
            [
                {
                    "id": "1",
                    "title": "Test Deal",
                    "price": 10.0,
                    "photoDataUrl": "base64string",
                    "ownerId": "user1",
                    "description": "Test description",
                    "category": "ELECTRONICS",
                    "condition": "NEW"
                }
            ]
        """.trimIndent()
        
        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        // When
        val deals = apiService.getBestDeals()

        // Then
        assertEquals(1, deals.size)
        assertEquals("1", deals[0].id)
        assertEquals("Test Deal", deals[0].title)
        assertEquals(10.0, deals[0].price, 0.0)
        assertEquals("user1", deals[0].ownerId)
    }

    @Test
    fun `test createItem returns ItemResponse`() = runBlocking {
        // Given
        val mockResponse = """
            {
                "id": "1",
                "title": "New Deal",
                "price": 20.0,
                "photoDataUrl": "base64string",
                "ownerId": "user1",
                "description": "New description",
                "category": "ELECTRONICS"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        val newDeal = Deal(
            title = "New Deal",
            price = 20.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "New description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When
        val response = apiService.createItem(newDeal)

        // Then
        assertNotNull(response)
        assertEquals("1", response.id)
        assertEquals("New Deal", response.title)
        assertEquals(20.0, response.price, 0.0)
        assertEquals("user1", response.ownerId)
    }

    @Test
    fun `test sendMessage returns Message`() = runBlocking {
        // Given
        val mockResponse = """
            {
                "id": "1",
                "senderId": "user1",
                "receiverId": "user2",
                "content": "Test message",
                "timestamp": "2024-03-20T10:00:00Z",
                "read": false
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        val message = Message(
            senderId = "user1",
            receiverId = "user2",
            content = "Test message"
        )

        // When
        val response = apiService.sendMessage(message)

        // Then
        assertNotNull(response)
        assertEquals("1", response.id)
        assertEquals("user1", response.senderId)
        assertEquals("user2", response.receiverId)
        assertEquals("Test message", response.content)
        assertEquals(false, response.read)
    }

    @Test
    fun `test getUserById returns User`() = runBlocking {
        // Given
        val mockResponse = """
            {
                "id": "user1",
                "username": "testuser",
                "email": "test@example.com",
                "points": 100
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        // When
        val user = apiService.getUserById("user1")

        // Then
        assertNotNull(user)
        assertEquals("user1", user.id)
        assertEquals("testuser", user.username)
        assertEquals("test@example.com", user.email)
        assertEquals(100, user.points)
    }

    @Test
    fun `test getReceivedMessages returns list of messages`() = runBlocking {
        // Given
        val mockResponse = """
            [
                {
                    "id": "1",
                    "senderId": "user2",
                    "receiverId": "user1",
                    "content": "Hello",
                    "timestamp": "2024-03-20T10:00:00Z",
                    "read": false
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        // When
        val messages = apiService.getReceivedMessages("user1")

        // Then
        assertEquals(1, messages.size)
        assertEquals("1", messages[0].id)
        assertEquals("user2", messages[0].senderId)
        assertEquals("user1", messages[0].receiverId)
        assertEquals("Hello", messages[0].content)
        assertEquals(false, messages[0].read)
    }

    @Test
    fun `test getByOwnerId returns owner's deals`() = runBlocking {
        // Given
        val ownerId = "user1"
        val mockResponse = """
            [
                {
                    "id": "1",
                    "title": "Owner's Deal",
                    "price": 15.0,
                    "photoDataUrl": "base64string",
                    "ownerId": "$ownerId",
                    "description": "Owner's description",
                    "category": "ELECTRONICS",
                    "condition": "NEW"
                }
            ]
        """.trimIndent()
        
        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        // When
        val deals = apiService.getByOwnerId(ownerId)

        // Then
        assertEquals(1, deals.size)
        assertEquals(ownerId, deals[0].ownerId)
        assertEquals("Owner's Deal", deals[0].title)
    }

    @Test
    fun `test deleteItem returns success response`() = runBlocking {
        // Given
        val itemId = "1"
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        // When
        val response = apiService.deleteItem(itemId)

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(204, response.code())
    }

    @Test
    fun `test updateItem returns updated item`() = runBlocking {
        // Given
        val itemId = "1"
        val mockResponse = """
            {
                "id": "$itemId",
                "title": "Updated Deal",
                "price": 25.0,
                "photoDataUrl": "base64string",
                "ownerId": "user1",
                "description": "Updated description",
                "category": "ELECTRONICS"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        val updatedDeal = Deal(
            id = itemId,
            title = "Updated Deal",
            price = 25.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "Updated description",
            category = Category.ELECTRONICS,
            condition = Condition.GOOD
        )

        // When
        val response = apiService.updateItem(itemId, updatedDeal)

        // Then
        assertEquals(itemId, response.id)
        assertEquals("Updated Deal", response.title)
        assertEquals(25.0, response.price, 0.0)
    }

    @Test
    fun `test getSentMessages returns sent messages`() = runBlocking {
        // Given
        val userId = "user1"
        val mockResponse = """
            [
                {
                    "id": "1",
                    "senderId": "$userId",
                    "receiverId": "user2",
                    "content": "Sent message",
                    "timestamp": "2024-03-20T10:00:00Z",
                    "read": true
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setBody(mockResponse))

        // When
        val messages = apiService.getSentMessages(userId)

        // Then
        assertEquals(1, messages.size)
        assertEquals(userId, messages[0].senderId)
        assertEquals("Sent message", messages[0].content)
        assertTrue(messages[0].read)
    }

    @Test
    fun `test markMessageAsRead returns success response`() = runBlocking {
        // Given
        val messageId = "1"
        mockWebServer.enqueue(MockResponse().setResponseCode(204))

        // When
        val response = apiService.markMessageAsRead(messageId)

        // Then
        assertTrue(response.isSuccessful)
        assertEquals(204, response.code())
    }

    @Test
    fun `test getBestDeals handles empty response`() = runBlocking {
        // Given
        mockWebServer.enqueue(MockResponse().setBody("[]"))

        // When
        val deals = apiService.getBestDeals()

        // Then
        assertTrue(deals.isEmpty())
    }

    @Test
    fun `test createItem handles server error`() = runBlocking {
        // Given
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val newDeal = Deal(
            title = "New Deal",
            price = 20.0,
            photoDataUrl = "base64string",
            ownerId = "user1",
            description = "New description",
            category = Category.ELECTRONICS,
            condition = Condition.NEW
        )

        // When/Then
        try {
            apiService.createItem(newDeal)
            fail("Expected exception was not thrown")
        } catch (e: Exception) {
            assertTrue(e is retrofit2.HttpException)
            assertEquals(500, (e as retrofit2.HttpException).code())
        }
    }

    @Test
    fun `test getUserById handles not found error`() = runBlocking {
        // Given
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        // When/Then
        try {
            apiService.getUserById("nonexistent")
            fail("Expected exception was not thrown")
        } catch (e: Exception) {
            assertTrue(e is retrofit2.HttpException)
            assertEquals(404, (e as retrofit2.HttpException).code())
        }
    }

    @Test
    fun `test sendMessage handles invalid message`() = runBlocking {
        // Given
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val invalidMessage = Message(
            senderId = "",
            receiverId = "",
            content = ""
        )

        // When/Then
        try {
            apiService.sendMessage(invalidMessage)
            fail("Expected exception was not thrown")
        } catch (e: Exception) {
            assertTrue(e is retrofit2.HttpException)
            assertEquals(400, (e as retrofit2.HttpException).code())
        }
    }
}

// Helper class for Instant serialization/deserialization in tests
class InstantTypeAdapter : JsonSerializer<Instant>, JsonDeserializer<Instant> {
    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toString())
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Instant? {
        return try {
            json?.asString?.let { Instant.parse(it) }
        } catch (e: Exception) {
            null
        }
    }
} 