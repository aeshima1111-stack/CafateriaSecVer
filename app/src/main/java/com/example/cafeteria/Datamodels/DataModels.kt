package com.example.cafeteria.Datamodels

data class DessertItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = ""
)

data class AdminFoodItem(
    val id: String = "",
    val category: String = "",
    val name: String = "",
    val price: String = "",
    val imageUrl: String = ""
)