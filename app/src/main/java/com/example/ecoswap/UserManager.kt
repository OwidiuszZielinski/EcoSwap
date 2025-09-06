package com.example.ecoswap

import com.example.ecoswap.auth.AuthManager
import com.example.ecoswap.ui.dto.MobileUserInfo

object UserManager {
    var ownerId: String = "rnowak"
    
    fun getCurrentUserId(): String? {
        return AuthManager.getCurrentUser()?.id ?: ownerId
    }
    
    fun getCurrentUser(): MobileUserInfo? {
        return AuthManager.getCurrentUser()
    }
    
    fun isAuthenticated(): Boolean {
        return AuthManager.isLoggedIn()
    }
} 