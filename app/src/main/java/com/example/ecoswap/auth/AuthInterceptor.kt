package com.example.ecoswap.auth

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip auth for login and refresh endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/mobile/login") || url.contains("/refresh")) {
            return chain.proceed(originalRequest)
        }
        
        val authHeader = AuthManager.getAuthHeader()
        if (authHeader != null) {
            val newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", authHeader)
                .build()
            return chain.proceed(newRequest)
        }
        
        return chain.proceed(originalRequest)
    }
}
