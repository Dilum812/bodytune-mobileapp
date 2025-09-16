package com.example.bodytunemobileapp.models

data class WorkoutSession(
    val sessionId: String = "",
    val userId: String = "",
    val workoutType: String = "", // Cardio, Strength, HIIT, etc.
    val exerciseName: String = "",
    val duration: Long = 0L, // in milliseconds
    val caloriesBurned: Int = 0,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", 0L, 0, 0L, 0L, "", 0L)
}
