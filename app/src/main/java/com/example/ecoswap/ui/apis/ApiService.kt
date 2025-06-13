package com.example.ecoswap.ui.apis

import com.example.ecoswap.ui.dto.Deal
import com.example.ecoswap.ui.dto.ItemResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.Response
import retrofit2.http.DELETE

interface ApiService {
    @GET("items/deals")
    suspend fun getBestDeals(): List<Deal>

    @GET("items/owner/{ownerId}")
    suspend fun getByOwnerId(@Path("ownerId") ownerId: String): List<Deal>

    @POST("items")
    suspend fun createItem(@Body item: Deal): ItemResponse

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<Unit>
}

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: ApiService = retrofit.create(ApiService::class.java)
}
