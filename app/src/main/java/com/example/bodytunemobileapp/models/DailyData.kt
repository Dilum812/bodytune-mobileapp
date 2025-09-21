package com.example.bodytunemobileapp.models

import java.util.*

/**
 * Model class representing daily fitness data for a specific date
 * Contains all the information displayed on the main screen for each day
 */
data class DailyData(
    val date: String = "", // Format: yyyy-MM-dd
    val userId: String = "",
    
    // Calorie tracking data
    val caloriesConsumed: Double = 0.0,
    val caloriesGoal: Double = 2200.0,
    // Workout data
    val workoutsCompleted: Int = 0,
    val workoutMinutes: Int = 0,
    val plannedWorkout: String = "Full Body Workout",
    val plannedWorkoutDuration: String = "45 min",
    val plannedWorkoutExercises: String = "12 exercises",
    
    // Running data
    val runningDistance: Double = 0.0, // in km
    val runningDuration: Int = 0, // in minutes
    val runningCalories: Int = 0,
    val hasRunToday: Boolean = false,
    
    // BMI data (latest for the day)
    val bmiValue: Double = 0.0,
    val bmiCategory: String = "",
    val height: Double = 175.0, // in cm
    val weight: Double = 68.0, // in kg
    
    // Achievement progress
    val streakDays: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val totalWorkouts: Int = 0,
    
    // Timestamps
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    
    /**
     * Calculate calories remaining for the day
     */
    fun getCaloriesRemaining(): Double {
        return (caloriesGoal - caloriesConsumed).coerceAtLeast(0.0)
    }
    
    /**
     * Calculate daily goal progress percentage
     */
    fun getDailyGoalProgress(): Int {
        return if (caloriesGoal > 0) {
            ((caloriesConsumed / caloriesGoal) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }
    
    /**
     * Get workout summary text
     */
    fun getWorkoutSummary(): String {
        return if (workoutsCompleted > 0) {
            "$workoutsCompleted workouts completed"
        } else {
            "$plannedWorkout"
        }
    }
    
    /**
     * Get running summary text
     */
    fun getRunningSummary(): String {
        return if (hasRunToday) {
            "${String.format("%.1f", runningDistance)}km in ${runningDuration}min"
        } else {
            "No run today"
        }
    }
    
    /**
     * Check if this is a complete fitness day (has data in all categories)
     */
    fun isCompleteFitnessDay(): Boolean {
        return caloriesConsumed > 0 && (workoutsCompleted > 0 || hasRunToday)
    }
    
    /**
     * Get overall daily score (0-100) based on goal completion
     */
    fun getDailyScore(): Int {
        var score = 0
        
        // Calorie goal (40 points max)
        score += (getDailyGoalProgress() * 0.4).toInt()
        
        // Workout completion (30 points max)
        if (workoutsCompleted > 0) score += 30
        
        // Running activity (30 points max)
        if (hasRunToday) score += 30
        
        return score.coerceIn(0, 100)
    }
    
    companion object {
        /**
         * Create default daily data for a specific date
         */
        fun createDefault(date: String, userId: String): DailyData {
            return DailyData(
                date = date,
                userId = userId,
                caloriesGoal = 2200.0,
                plannedWorkout = "Full Body Workout",
                plannedWorkoutDuration = "45 min",
                plannedWorkoutExercises = "12 exercises"
            )
        }
        
        /**
         * Create daily data for today
         */
        fun createForToday(userId: String): DailyData {
            val today = com.example.bodytunemobileapp.utils.CalendarManager.formatDateForStorage(Date())
            return createDefault(today, userId)
        }
    }
}
