package com.example.ecoswap.ui.apis

import com.example.ecoswap.ui.dto.Deal
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ApiService {
    @GET("items")
    suspend fun getBestDeals(): List<Deal>
}

object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: ApiService = retrofit.create(ApiService::class.java)
}
