package com.example.bodytunemobileapp.utils

import com.example.bodytunemobileapp.models.DailyNutrition
import com.example.bodytunemobileapp.models.Food
import com.example.bodytunemobileapp.models.MealEntry
import com.example.bodytunemobileapp.models.MealType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class CalorieTracker {
    
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun addMealEntry(
        food: Food,
        quantity: Double,
        mealType: MealType,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val currentUser = auth.currentUser
            if (currentUser == null) {
                onError("Please sign in to add meals")
                return
            }
            
            val userId = currentUser.uid
            val today = dateFormat.format(Date())
            val mealId = database.reference.push().key
            
            if (mealId == null) {
                onError("Failed to generate meal ID")
                return
            }
            
            // Calculate nutrition values based on quantity
            val multiplier = quantity / 100.0
            val calories = food.caloriesPer100g * multiplier
            val protein = food.proteinPer100g * multiplier
            val carbs = food.carbsPer100g * multiplier
            val fat = food.fatPer100g * multiplier
            
            val mealEntry = MealEntry(
                id = mealId,
                userId = userId,
                foodId = food.id,
                foodName = food.name,
                quantity = quantity,
                mealType = mealType,
                date = today,
                timestamp = System.currentTimeMillis(),
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )
            
            // Convert to map to ensure proper serialization
            val mealMap = mapOf(
                "id" to mealEntry.id,
                "userId" to mealEntry.userId,
                "foodId" to mealEntry.foodId,
                "foodName" to mealEntry.foodName,
                "quantity" to mealEntry.quantity,
                "mealType" to mealEntry.mealType.name,
                "date" to mealEntry.date,
                "timestamp" to mealEntry.timestamp,
                "calories" to mealEntry.calories,
                "protein" to mealEntry.protein,
                "carbs" to mealEntry.carbs,
                "fat" to mealEntry.fat
            )
            
            database.reference
                .child("meal_entries")
                .child(userId)
                .child(today)
                .child(mealId)
                .setValue(mealMap)
                .addOnSuccessListener { 
                    onSuccess() 
                }
                .addOnFailureListener { exception ->
                    val errorMessage = when {
                        exception.message?.contains("permission", true) == true -> "Database permission denied. Please check your account."
                        exception.message?.contains("network", true) == true -> "Network error. Please check your internet connection."
                        exception.message?.contains("auth", true) == true -> "Authentication error. Please sign in again."
                        else -> "Failed to save meal: ${exception.message}"
                    }
                    onError(errorMessage)
                }
        } catch (e: Exception) {
            onError("Unexpected error: ${e.message}")
        }
    }
    
    fun getTodaysMeals(
        onSuccess: (List<MealEntry>) -> Unit,
        onError: (String) -> Unit
    ) {
        val today = dateFormat.format(Date())
        getMealsForDate(today, onSuccess, onError)
    }
    
    fun getMealsForDate(
        date: String,
        onSuccess: (List<MealEntry>) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("User not authenticated")
            return
        }
        
        database.reference
            .child("meal_entries")
            .child(userId)
            .child(date)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val meals = mutableListOf<MealEntry>()
                    for (mealSnapshot in snapshot.children) {
                        val meal = mealSnapshot.getValue(MealEntry::class.java)
                        meal?.let { meals.add(it) }
                    }
                    onSuccess(meals)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }
    
    fun getMealsByType(
        mealType: MealType,
        onSuccess: (List<MealEntry>) -> Unit,
        onError: (String) -> Unit
    ) {
        getTodaysMeals({ meals ->
            val filteredMeals = meals.filter { it.mealType == mealType }
            onSuccess(filteredMeals)
        }, onError)
    }
    
    fun calculateDailyNutrition(
        meals: List<MealEntry>,
        goalCalories: Double = 2200.0
    ): DailyNutrition {
        val today = dateFormat.format(Date())
        
        val totalCalories = meals.sumOf { it.calories }
        val totalProtein = meals.sumOf { it.protein }
        val totalCarbs = meals.sumOf { it.carbs }
        val totalFat = meals.sumOf { it.fat }
        
        val breakfastCalories = meals.filter { it.mealType == MealType.BREAKFAST }.sumOf { it.calories }
        val lunchCalories = meals.filter { it.mealType == MealType.LUNCH }.sumOf { it.calories }
        val dinnerCalories = meals.filter { it.mealType == MealType.DINNER }.sumOf { it.calories }
        val snacksCalories = meals.filter { it.mealType == MealType.SNACKS }.sumOf { it.calories }
        
        return DailyNutrition(
            date = today,
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat,
            goalCalories = goalCalories,
            breakfastCalories = breakfastCalories,
            lunchCalories = lunchCalories,
            dinnerCalories = dinnerCalories,
            snacksCalories = snacksCalories
        )
    }
    
    fun getDailyNutritionForDate(
        date: String,
        onSuccess: (DailyNutrition) -> Unit,
        onError: (String) -> Unit
    ) {
        getUserGoalCalories(
            onSuccess = { goalCalories ->
                getMealsForDate(date,
                    onSuccess = { meals ->
                        val nutrition = DailyNutrition(
                            date = date,
                            totalCalories = meals.sumOf { it.calories },
                            totalProtein = meals.sumOf { it.protein },
                            totalCarbs = meals.sumOf { it.carbs },
                            totalFat = meals.sumOf { it.fat },
                            goalCalories = goalCalories,
                            breakfastCalories = meals.filter { it.mealType == MealType.BREAKFAST }.sumOf { it.calories },
                            lunchCalories = meals.filter { it.mealType == MealType.LUNCH }.sumOf { it.calories },
                            dinnerCalories = meals.filter { it.mealType == MealType.DINNER }.sumOf { it.calories },
                            snacksCalories = meals.filter { it.mealType == MealType.SNACKS }.sumOf { it.calories }
                        )
                        onSuccess(nutrition)
                    },
                    onError = onError
                )
            },
            onError = onError
        )
    }
    
    fun deleteMealEntry(
        mealId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("User not authenticated")
            return
        }
        
        val today = dateFormat.format(Date())
        
        database.reference
            .child("meal_entries")
            .child(userId)
            .child(today)
            .child(mealId)
            .removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to delete meal") }
    }
    
    fun getUserGoalCalories(
        onSuccess: (Double) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("User not authenticated")
            return
        }
        
        database.reference
            .child("users")
            .child(userId)
            .child("goalCalories")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val goalCalories = snapshot.getValue(Double::class.java) ?: 2200.0
                    onSuccess(goalCalories)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }
    
    fun updateGoalCalories(
        goalCalories: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError("User not authenticated")
            return
        }
        
        database.reference
            .child("users")
            .child(userId)
            .child("goalCalories")
            .setValue(goalCalories)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Failed to update goal") }
    }
}
