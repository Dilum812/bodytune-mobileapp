package com.example.bodytunemobileapp.models

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val age: Int = 0,
    val height: Double = 0.0, // in cm
    val weight: Double = 0.0, // in kg
    val fitnessGoal: String = "",
    val activityLevel: String = "",
    val profileImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", 0, 0.0, 0.0, "", "", "", 0L, 0L)
}
