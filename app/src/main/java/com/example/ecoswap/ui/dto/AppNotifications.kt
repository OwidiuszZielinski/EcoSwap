package com.example.ecoswap.ui.dto

object AppNotifications {
    private val notifications = mutableListOf<NotificationItem>()
    private var unreadCount = 0

    fun addNotification(notification: NotificationItem) {
        notifications.add(0, notification) // najnowsze na górze
        unreadCount++
    }

    fun getNotifications(): List<NotificationItem> = notifications

    fun getUnreadCount(): Int = unreadCount

    fun markAllAsRead() {
        unreadCount = 0
    }

    fun hasUnreadNotifications(): Boolean = unreadCount > 0
} 