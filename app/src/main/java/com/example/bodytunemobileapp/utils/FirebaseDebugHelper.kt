package com.example.bodytunemobileapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseDebugHelper {
    
    companion object {
        private const val TAG = "FirebaseDebug"
        
        fun checkFirebaseStatus(context: Context) {
            val auth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance()
            
            Log.d(TAG, "=== Firebase Debug Status ===")
            
            // Check Authentication
            val currentUser = auth.currentUser
            if (currentUser != null) {
                Log.d(TAG, "✅ User authenticated")
                Log.d(TAG, "User ID: ${currentUser.uid}")
                Log.d(TAG, "User Email: ${currentUser.email}")
                Log.d(TAG, "User Display Name: ${currentUser.displayName}")
                Log.d(TAG, "Email Verified: ${currentUser.isEmailVerified}")
                
                Toast.makeText(context, "User: ${currentUser.email}", Toast.LENGTH_LONG).show()
            } else {
                Log.d(TAG, "❌ User NOT authenticated")
                Toast.makeText(context, "ERROR: User not signed in!", Toast.LENGTH_LONG).show()
                return
            }
            
            // Check Database Connection
            Log.d(TAG, "Database URL: ${database.reference.toString()}")
            
            // Test simple write operation
            testDatabaseWrite(context, currentUser.uid)
        }
        
        private fun testDatabaseWrite(context: Context, userId: String) {
            val database = FirebaseDatabase.getInstance()
            val testData = mapOf(
                "test" to "connection_test",
                "timestamp" to System.currentTimeMillis(),
                "userId" to userId
            )
            
            database.reference
                .child("debug_test")
                .child(userId)
                .setValue(testData)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Database write successful")
                    Toast.makeText(context, "✅ Firebase connection working!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Database write failed: ${exception.message}")
                    Log.e(TAG, "Exception type: ${exception.javaClass.simpleName}")
                    
                    val errorMessage = when {
                        exception.message?.contains("permission", true) == true -> 
                            "❌ PERMISSION DENIED - Update Firebase rules!"
                        exception.message?.contains("network", true) == true -> 
                            "❌ NETWORK ERROR - Check internet connection"
                        exception.message?.contains("auth", true) == true -> 
                            "❌ AUTH ERROR - Sign in again"
                        else -> "❌ DATABASE ERROR: ${exception.message}"
                    }
                    
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
        }
        
        fun testMealEntryWrite(context: Context) {
            val auth = FirebaseAuth.getInstance()
            val database = FirebaseDatabase.getInstance()
            val currentUser = auth.currentUser
            
            if (currentUser == null) {
                Toast.makeText(context, "❌ Not signed in!", Toast.LENGTH_LONG).show()
                return
            }
            
            val userId = currentUser.uid
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            val mealId = database.reference.push().key ?: "test_meal_id"
            
            val testMeal = mapOf(
                "id" to mealId,
                "userId" to userId,
                "foodId" to "test_food",
                "foodName" to "Test Food",
                "quantity" to 100.0,
                "mealType" to "BREAKFAST",
                "date" to today,
                "timestamp" to System.currentTimeMillis(),
                "calories" to 100.0,
                "protein" to 10.0,
                "carbs" to 15.0,
                "fat" to 5.0
            )
            
            Log.d(TAG, "Testing meal entry write to: meal_entries/$userId/$today/$mealId")
            
            database.reference
                .child("meal_entries")
                .child(userId)
                .child(today)
                .child(mealId)
                .setValue(testMeal)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Meal entry write successful")
                    Toast.makeText(context, "✅ Meal entry test successful!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Meal entry write failed: ${exception.message}")
                    
                    val errorMessage = when {
                        exception.message?.contains("permission", true) == true -> 
                            "❌ MEAL PERMISSION DENIED - Update Firebase rules for meal_entries!"
                        else -> "❌ MEAL WRITE ERROR: ${exception.message}"
                    }
                    
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
        }
    }
}
