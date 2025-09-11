package com.example.bodytunemobileapp.model

data class FoodItem(
    val name: String,
    val caloriesPer100g: Int,
    val proteinPer100g: Float,
    val carbsPer100g: Float,
    val fatPer100g: Float,
    val imageRes: Int
)
