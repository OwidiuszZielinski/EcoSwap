package com.example.ecoswap.ui.dto

data class MobileLoginRequest(
    val email: String,
    val password: String
)

data class MobileLoginResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenType: String? = null,
    val expiresIn: Long? = null,
    val user: MobileUserInfo? = null,
    val error: String? = null
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenType: String? = null,
    val expiresIn: Long? = null,
    val error: String? = null
)

data class TokenValidationResponse(
    val valid: Boolean,
    val user: MobileUserInfo? = null,
    val expiresIn: Long? = null,
    val error: String? = null
)

data class LogoutResponse(
    val message: String
)

data class MobileUserInfo(
    val id: String?,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val points: Int
)
