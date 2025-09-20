package com.example.bodytunemobileapp.models

data class Food(
    val id: String = "",
    val name: String = "",
    val caloriesPer100g: Double = 0.0,
    val proteinPer100g: Double = 0.0,
    val carbsPer100g: Double = 0.0,
    val fatPer100g: Double = 0.0,
    val category: String = ""
)

data class MealEntry(
    val id: String = "",
    val userId: String = "",
    val foodId: String = "",
    val foodName: String = "",
    val quantity: Double = 100.0, // in grams
    val mealType: MealType = MealType.BREAKFAST,
    val date: String = "", // YYYY-MM-DD format
    val timestamp: Long = System.currentTimeMillis(),
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0
)

enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACKS("Snacks")
}

data class DailyNutrition(
    val date: String = "",
    val totalCalories: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val goalCalories: Double = 2200.0,
    val breakfastCalories: Double = 0.0,
    val lunchCalories: Double = 0.0,
    val dinnerCalories: Double = 0.0,
    val snacksCalories: Double = 0.0
)
