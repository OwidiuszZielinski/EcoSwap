package com.example.ecoswap.ui.dto

data class NotificationItem(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) 