package com.example.bodytunemobileapp.models

data class BMIRecord(
    val recordId: String = "",
    val userId: String = "",
    val height: Double = 0.0, // in cm
    val weight: Double = 0.0, // in kg
    val bmiValue: Double = 0.0,
    val bmiCategory: String = "", // Underweight, Normal, Overweight, Obese
    val timestamp: Long = System.currentTimeMillis()
) {
    // No-argument constructor required by Firebase
    constructor() : this("", "", 0.0, 0.0, 0.0, "", 0L)
}
