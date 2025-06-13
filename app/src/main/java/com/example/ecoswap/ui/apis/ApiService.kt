package com.example.ecoswap.ui.apis

import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.ItemResponse
import com.example.ecoswap.ui.dto.Message
import com.example.ecoswap.ui.dto.User
import com.google.gson.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter

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

interface ApiService {
    @GET("items/deals")
    suspend fun getBestDeals(): List<Deal>

    @GET("items/owner/{ownerId}")
    suspend fun getByOwnerId(@Path("ownerId") ownerId: String): List<Deal>

    @POST("items")
    suspend fun createItem(@Body item: Deal): ItemResponse

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<Unit>

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body item: Deal): ItemResponse

    @POST("messages")
    suspend fun sendMessage(@Body message: Message): Message

    @GET("messages/received/{userId}")
    suspend fun getReceivedMessages(@Path("userId") userId: String): List<Message>

    @GET("messages/sent/{userId}")
    suspend fun getSentMessages(@Path("userId") userId: String): List<Message>

    @PUT("messages/{messageId}/read")
    suspend fun markMessageAsRead(@Path("messageId") messageId: String): Response<Unit>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): User
}

object RetrofitInstance {
    private val gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api: ApiService = retrofit.create(ApiService::class.java)
}
