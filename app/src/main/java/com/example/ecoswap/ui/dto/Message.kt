package com.example.ecoswap.ui.dto

import java.time.Instant

data class Message(
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: Instant? = null,
    val read: Boolean = false
)