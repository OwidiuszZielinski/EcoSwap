package com.example.ecoswap.ui.model

data class Item(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val condition: String,
    val photoDataUrl: String,
    val available: Boolean,
    val createdAt: String
)