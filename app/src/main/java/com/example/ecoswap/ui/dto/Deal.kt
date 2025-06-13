package com.example.ecoswap.ui.dto

import java.io.Serializable

data class Deal(
    val id: String = "",
    val title: String,
    val price: Double,
    val photoDataUrl: String,
    val photoUrl: String? = null,
    val ownerId: String,
    val description: String,
    val category: Category,
    val condition: Condition
) : Serializable
