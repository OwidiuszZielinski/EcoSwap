package com.example.ecoswap.ui.dto

data class ItemResponse(
    val id: String,
    val title: String,
    val price: Double,
    val photoDataUrl: String,
    val ownerId: String,
    val description: String,
    val category: String
) 