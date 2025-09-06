package com.example.ecoswap.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.ecoswap.ui.apis.RetrofitInstance
import com.example.ecoswap.ui.dto.MobileUserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_USERNAME = "user_username"
    private const val KEY_USER_FIRST_NAME = "user_first_name"
    private const val KEY_USER_LAST_NAME = "user_last_name"
    private const val KEY_USER_POINTS = "user_points"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_TOKEN_EXPIRY = "token_expiry"

    private lateinit var prefs: SharedPreferences
    private var currentUser: MobileUserInfo? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadUserFromPrefs()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !isTokenExpired()
    }

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getCurrentUser(): MobileUserInfo? {
        return currentUser
    }

    fun getAuthHeader(): String? {
        val token = getAccessToken()
        return if (token != null) "Bearer $token" else null
    }

    private fun isTokenExpired(): Boolean {
        val expiryTime = prefs.getLong(KEY_TOKEN_EXPIRY, 0)
        return System.currentTimeMillis() >= expiryTime
    }

    suspend fun login(email: String, password: String): Result<MobileUserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = com.example.ecoswap.ui.dto.MobileLoginRequest(email, password)
                val response = RetrofitInstance.api.mobileLogin(loginRequest)
                
                if (response.error != null) {
                    Result.failure(Exception(response.error))
                } else if (response.accessToken != null && response.user != null) {
                    saveAuthData(response)
                    Result.success(response.user)
                } else {
                    Result.failure(Exception("Invalid response from server"))
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Login failed", e)
                Result.failure(e)
            }
        }
    }

    suspend fun refreshToken(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val refreshToken = getRefreshToken()
                if (refreshToken == null) {
                    return@withContext Result.failure(Exception("No refresh token available"))
                }

                val refreshRequest = com.example.ecoswap.ui.dto.RefreshTokenRequest(refreshToken)
                val response = RetrofitInstance.api.refreshToken(refreshRequest)

                if (response.error != null) {
                    logout()
                    Result.failure(Exception(response.error))
                } else if (response.accessToken != null) {
                    saveRefreshData(response)
                    Result.success(true)
                } else {
                    Result.failure(Exception("Invalid refresh response"))
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Token refresh failed", e)
                logout()
                Result.failure(e)
            }
        }
    }

    suspend fun validateToken(): Result<MobileUserInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = getAuthHeader()
                if (authHeader == null) {
                    return@withContext Result.failure(Exception("No auth token available"))
                }

                val response = RetrofitInstance.api.validateToken(authHeader)

                if (!response.valid || response.user == null) {
                    logout()
                    Result.failure(Exception(response.error ?: "Token validation failed"))
                } else {
                    currentUser = response.user
                    saveUserToPrefs(response.user)
                    Result.success(response.user)
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Token validation failed", e)
                logout()
                Result.failure(e)
            }
        }
    }

    suspend fun logout(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = getAuthHeader()
                if (authHeader != null) {
                    RetrofitInstance.api.logout(authHeader)
                }
            } catch (e: Exception) {
                Log.e("AuthManager", "Logout API call failed", e)
            } finally {
                clearAuthData()
            }
            Result.success(true)
        }
    }

    private fun saveAuthData(response: com.example.ecoswap.ui.dto.MobileLoginResponse) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, response.accessToken)
        editor.putString(KEY_REFRESH_TOKEN, response.refreshToken)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        
        // Calculate token expiry time
        val expiryTime = System.currentTimeMillis() + (response.expiresIn ?: 3600000) * 1000
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        
        if (response.user != null) {
            currentUser = response.user
            saveUserToPrefs(response.user)
        }
        
        editor.apply()
    }

    private fun saveRefreshData(response: com.example.ecoswap.ui.dto.RefreshTokenResponse) {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, response.accessToken)
        if (response.refreshToken != null) {
            editor.putString(KEY_REFRESH_TOKEN, response.refreshToken)
        }
        
        // Calculate token expiry time
        val expiryTime = System.currentTimeMillis() + (response.expiresIn ?: 3600000) * 1000
        editor.putLong(KEY_TOKEN_EXPIRY, expiryTime)
        
        editor.apply()
    }

    private fun saveUserToPrefs(user: MobileUserInfo) {
        val editor = prefs.edit()
        editor.putString(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_USERNAME, user.username)
        editor.putString(KEY_USER_FIRST_NAME, user.firstName)
        editor.putString(KEY_USER_LAST_NAME, user.lastName)
        editor.putInt(KEY_USER_POINTS, user.points)
        editor.apply()
    }

    private fun loadUserFromPrefs() {
        val id = prefs.getString(KEY_USER_ID, null)
        val email = prefs.getString(KEY_USER_EMAIL, "")
        val username = prefs.getString(KEY_USER_USERNAME, "")
        val firstName = prefs.getString(KEY_USER_FIRST_NAME, "")
        val lastName = prefs.getString(KEY_USER_LAST_NAME, "")
        val points = prefs.getInt(KEY_USER_POINTS, 0)

        if (email != null && username != null && firstName != null && lastName != null) {
            currentUser = MobileUserInfo(id, email, username, firstName, lastName, points)
        }
    }

    private fun clearAuthData() {
        val editor = prefs.edit()
        editor.remove(KEY_ACCESS_TOKEN)
        editor.remove(KEY_REFRESH_TOKEN)
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_USER_EMAIL)
        editor.remove(KEY_USER_USERNAME)
        editor.remove(KEY_USER_FIRST_NAME)
        editor.remove(KEY_USER_LAST_NAME)
        editor.remove(KEY_USER_POINTS)
        editor.remove(KEY_IS_LOGGED_IN)
        editor.remove(KEY_TOKEN_EXPIRY)
        editor.apply()
        
        currentUser = null
    }
}
