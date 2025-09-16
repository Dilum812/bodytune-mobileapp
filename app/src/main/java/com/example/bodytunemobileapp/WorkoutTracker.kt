package com.example.bodytunemobileapp

import android.content.Context
import android.widget.Toast
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.WorkoutSession

class WorkoutTracker(private val context: Context) {
    
    private var currentSession: WorkoutSession? = null
    private var startTime: Long = 0
    
    fun startWorkout(workoutType: String, exerciseName: String) {
        startTime = System.currentTimeMillis()
        
        currentSession = WorkoutSession(
            userId = FirebaseHelper.getCurrentUserId() ?: "",
            workoutType = workoutType,
            exerciseName = exerciseName,
            startTime = startTime,
            createdAt = System.currentTimeMillis()
        )
        
        Toast.makeText(context, "Workout started: $exerciseName", Toast.LENGTH_SHORT).show()
    }
    
    fun endWorkout(caloriesBurned: Int = 0, notes: String = "") {
        currentSession?.let { session ->
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            
            val completedSession = session.copy(
                duration = duration,
                endTime = endTime,
                caloriesBurned = caloriesBurned,
                notes = notes
            )
            
            // Save to Firebase
            FirebaseHelper.saveWorkoutSession(completedSession) { success, error ->
                if (success) {
                    Toast.makeText(context, "Workout saved successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to save workout: $error", Toast.LENGTH_SHORT).show()
                }
            }
            
            currentSession = null
        }
    }
    
    fun getCurrentWorkoutDuration(): Long {
        return if (currentSession != null) {
            System.currentTimeMillis() - startTime
        } else {
            0L
        }
    }
    
    fun isWorkoutActive(): Boolean = currentSession != null
    
    fun getCurrentWorkout(): WorkoutSession? = currentSession
    
    fun formatDuration(durationMs: Long): String {
        val seconds = (durationMs / 1000) % 60
        val minutes = (durationMs / (1000 * 60)) % 60
        val hours = (durationMs / (1000 * 60 * 60))
        
        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    companion object {
        fun getWorkoutHistory(callback: (List<WorkoutSession>?, String?) -> Unit) {
            FirebaseHelper.getWorkoutSessions(callback)
        }
        
        fun calculateTotalCalories(sessions: List<WorkoutSession>): Int {
            return sessions.sumOf { it.caloriesBurned }
        }
        
        fun calculateTotalDuration(sessions: List<WorkoutSession>): Long {
            return sessions.sumOf { it.duration }
        }
        
        fun getWorkoutsByType(sessions: List<WorkoutSession>, type: String): List<WorkoutSession> {
            return sessions.filter { it.workoutType.equals(type, ignoreCase = true) }
        }
    }
}
