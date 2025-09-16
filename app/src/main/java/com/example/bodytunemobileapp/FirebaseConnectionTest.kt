package com.example.bodytunemobileapp

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseConnectionTest(private val context: Context) {
    
    private val TAG = "FirebaseTest"
    
    fun runAllTests(callback: (Boolean, String) -> Unit) {
        Log.d(TAG, "Starting Firebase connection tests...")
        
        // Test 1: Check Firebase App initialization
        if (!testFirebaseAppInitialization()) {
            callback(false, "Firebase App not initialized")
            return
        }
        
        // Test 2: Check Database connection
        testDatabaseConnection { dbConnected, dbMessage ->
            if (!dbConnected) {
                callback(false, "Database connection failed: $dbMessage")
                return@testDatabaseConnection
            }
            
            // Test 3: Test Authentication
            testAuthentication { authWorking, authMessage ->
                val overallResult = dbConnected && authWorking
                val finalMessage = if (overallResult) {
                    "✅ All Firebase tests passed!\n• App initialized\n• Database connected\n• Authentication ready"
                } else {
                    "❌ Some tests failed:\n• Database: ${if (dbConnected) "✅" else "❌"}\n• Auth: ${if (authWorking) "✅" else "❌"}\n$authMessage"
                }
                callback(overallResult, finalMessage)
            }
        }
    }
    
    private fun testFirebaseAppInitialization(): Boolean {
        return try {
            val apps = FirebaseApp.getApps(context)
            val hasDefaultApp = apps.any { it.name == FirebaseApp.DEFAULT_APP_NAME }
            Log.d(TAG, "Firebase apps found: ${apps.size}, Default app: $hasDefaultApp")
            hasDefaultApp
        } catch (e: Exception) {
            Log.e(TAG, "Firebase app initialization test failed", e)
            false
        }
    }
    
    private fun testDatabaseConnection(callback: (Boolean, String) -> Unit) {
        try {
            val database = FirebaseDatabase.getInstance()
            val testRef = database.getReference(".info/connected")
            
            testRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    Log.d(TAG, "Database connection status: $connected")
                    
                    if (connected) {
                        // Test writing to database
                        testDatabaseWrite(callback)
                    } else {
                        callback(false, "Database not connected")
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Database connection test cancelled", error.toException())
                    callback(false, "Database connection test failed: ${error.message}")
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Database connection test failed", e)
            callback(false, "Database connection error: ${e.message}")
        }
    }
    
    private fun testDatabaseWrite(callback: (Boolean, String) -> Unit) {
        try {
            val database = FirebaseDatabase.getInstance()
            val testRef = database.getReference("test_connection")
            val testData = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "message" to "Firebase connection test",
                "app" to "BodyTune"
            )
            
            testRef.setValue(testData)
                .addOnSuccessListener {
                    Log.d(TAG, "Database write test successful")
                    
                    // Clean up test data
                    testRef.removeValue()
                    callback(true, "Database read/write successful")
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Database write test failed", exception)
                    callback(false, "Database write failed: ${exception.message}")
                }
        } catch (e: Exception) {
            Log.e(TAG, "Database write test error", e)
            callback(false, "Database write error: ${e.message}")
        }
    }
    
    private fun testAuthentication(callback: (Boolean, String) -> Unit) {
        try {
            val auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Auth instance created successfully")
            
            // Check if auth is properly configured
            val currentUser = auth.currentUser
            Log.d(TAG, "Current user: ${currentUser?.email ?: "None"}")
            
            callback(true, "Authentication service ready")
        } catch (e: Exception) {
            Log.e(TAG, "Authentication test failed", e)
            callback(false, "Authentication error: ${e.message}")
        }
    }
    
    fun showTestResults(success: Boolean, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.i(TAG, "Test Results: $message")
    }
    
    companion object {
        fun quickTest(context: Context) {
            val tester = FirebaseConnectionTest(context)
            tester.runAllTests { success, message ->
                tester.showTestResults(success, message)
            }
        }
    }
}
