package com.example.bodytunemobileapp.data

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val duration: Int, // in minutes
    val imageResource: Int,
    val category: ExerciseCategory,
    val targetAudience: String = "Women & Men",
    val instructions: String
)

enum class ExerciseCategory(val displayName: String, val exerciseCount: Int) {
    CARDIO_ENDURANCE("Cardio / Endurance", 8),
    STRENGTH_TRAINING("Strength Training", 6),
    CORE_ABS("Core & Abs", 7),
    FLEXIBILITY_MOBILITY("Flexibility & Mobility", 8),
    HIIT("HIIT", 5),
    YOGA("Yoga", 9),
    PILATES("Pilates", 6)
}
