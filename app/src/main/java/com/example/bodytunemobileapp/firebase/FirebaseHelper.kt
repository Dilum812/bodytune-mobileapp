package com.example.bodytunemobileapp.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.example.bodytunemobileapp.models.User
import com.example.bodytunemobileapp.models.WorkoutSession
import com.example.bodytunemobileapp.models.BMIRecord
import com.example.bodytunemobileapp.models.DailyData

class FirebaseHelper {
    
    companion object {
        private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        
        // Database references
        private val usersRef: DatabaseReference = database.getReference("users")
        private val workoutsRef: DatabaseReference = database.getReference("workouts")
        private val bmiRecordsRef: DatabaseReference = database.getReference("bmi_records")
        private val dailyDataRef: DatabaseReference = database.getReference("daily_data")
        
        // Authentication methods
        fun getCurrentUser(): FirebaseUser? = auth.currentUser
        
        fun getCurrentUserId(): String? = auth.currentUser?.uid
        
        fun signOut() = auth.signOut()
        
        // User data methods
        fun saveUserProfile(user: User, callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                usersRef.child(userId).setValue(user)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { exception ->
                        callback(false, exception.message)
                    }
            } else {
                callback(false, "User not authenticated")
            }
        }
        
        fun getUserProfile(callback: (User?, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        callback(user, null)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error.message)
                    }
                })
            } else {
                callback(null, "User not authenticated")
            }
        }
        
        // Workout session methods
        fun saveWorkoutSession(workoutSession: WorkoutSession, callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                val sessionId = workoutsRef.child(userId).push().key
                if (sessionId != null) {
                    workoutsRef.child(userId).child(sessionId).setValue(workoutSession)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message)
                        }
                } else {
                    callback(false, "Failed to generate session ID")
                }
            } else {
                callback(false, "User not authenticated")
            }
        }
        
        fun getWorkoutSessions(callback: (List<WorkoutSession>?, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                workoutsRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val workoutSessions = mutableListOf<WorkoutSession>()
                        for (sessionSnapshot in snapshot.children) {
                            val session = sessionSnapshot.getValue(WorkoutSession::class.java)
                            session?.let { workoutSessions.add(it) }
                        }
                        callback(workoutSessions, null)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error.message)
                    }
                })
            } else {
                callback(null, "User not authenticated")
            }
        }
        
        // BMI record methods
        fun saveBMIRecord(bmiRecord: BMIRecord, callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                val recordId = bmiRecordsRef.child(userId).push().key
                if (recordId != null) {
                    bmiRecordsRef.child(userId).child(recordId).setValue(bmiRecord)
                        .addOnSuccessListener {
                            callback(true, null)
                        }
                        .addOnFailureListener { exception ->
                            callback(false, exception.message)
                        }
                } else {
                    callback(false, "Failed to generate record ID")
                }
            } else {
                callback(false, "User not authenticated")
            }
        }
        
        fun getBMIRecords(callback: (List<BMIRecord>?, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                bmiRecordsRef.child(userId).orderByChild("timestamp")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val bmiRecords = mutableListOf<BMIRecord>()
                            for (recordSnapshot in snapshot.children) {
                                val record = recordSnapshot.getValue(BMIRecord::class.java)
                                record?.let { bmiRecords.add(it) }
                            }
                            callback(bmiRecords, null)
                        }
                        
                        override fun onCancelled(error: DatabaseError) {
                            callback(null, error.message)
                        }
                    })
            } else {
                callback(null, "User not authenticated")
            }
        }
        
        fun getLatestBMIRecord(userId: String, callback: (BMIRecord?, String?) -> Unit) {
            bmiRecordsRef.child(userId)
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var latestRecord: BMIRecord? = null
                        for (recordSnapshot in snapshot.children) {
                            latestRecord = recordSnapshot.getValue(BMIRecord::class.java)
                            break // Get the first (and only) record
                        }
                        callback(latestRecord, null)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        callback(null, error.message)
                    }
                })
        }
        
        // Real-time listeners
        fun addWorkoutSessionListener(callback: (List<WorkoutSession>) -> Unit): DatabaseReference? {
            val userId = getCurrentUserId()
            return if (userId != null) {
                val listener = workoutsRef.child(userId)
                listener.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val workoutSessions = mutableListOf<WorkoutSession>()
                        for (sessionSnapshot in snapshot.children) {
                            val session = sessionSnapshot.getValue(WorkoutSession::class.java)
                            session?.let { workoutSessions.add(it) }
                        }
                        callback(workoutSessions)
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        // Handle error if needed
                    }
                })
                listener
            } else {
                null
            }
        }
        
        fun removeListener(reference: DatabaseReference, listener: ValueEventListener) {
            reference.removeEventListener(listener)
        }
        
        // Daily data methods
        fun saveDailyData(dailyData: DailyData, callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                dailyDataRef.child(userId).child(dailyData.date).setValue(dailyData)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { exception ->
                        callback(false, exception.message)
                    }
            } else {
                callback(false, "User not authenticated")
            }
        }
        
        fun getDailyData(date: String, callback: (DailyData?, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                dailyDataRef.child(userId).child(date)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val dailyData = snapshot.getValue(DailyData::class.java)
                            callback(dailyData, null)
                        }
                        
                        override fun onCancelled(error: DatabaseError) {
                            callback(null, error.message)
                        }
                    })
            } else {
                callback(null, "User not authenticated")
            }
        }
        
        fun getDailyDataForDateRange(startDate: String, endDate: String, callback: (List<DailyData>?, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                dailyDataRef.child(userId)
                    .orderByKey()
                    .startAt(startDate)
                    .endAt(endDate)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val dailyDataList = mutableListOf<DailyData>()
                            for (dataSnapshot in snapshot.children) {
                                val dailyData = dataSnapshot.getValue(DailyData::class.java)
                                dailyData?.let { dailyDataList.add(it) }
                            }
                            callback(dailyDataList, null)
                        }
                        
                        override fun onCancelled(error: DatabaseError) {
                            callback(null, error.message)
                        }
                    })
            } else {
                callback(null, "User not authenticated")
            }
        }
        
        fun updateDailyDataField(date: String, field: String, value: Any, callback: (Boolean, String?) -> Unit) {
            val userId = getCurrentUserId()
            if (userId != null) {
                val updates = mapOf(
                    field to value,
                    "updatedAt" to System.currentTimeMillis()
                )
                
                dailyDataRef.child(userId).child(date).updateChildren(updates)
                    .addOnSuccessListener {
                        callback(true, null)
                    }
                    .addOnFailureListener { exception ->
                        callback(false, exception.message)
                    }
            } else {
                callback(false, "User not authenticated")
            }
        }
        
        fun getOrCreateDailyData(date: String, callback: (DailyData?, String?) -> Unit) {
            getDailyData(date) { existingData, error ->
                if (existingData != null) {
                    callback(existingData, null)
                } else if (error == null) {
                    // No data exists, create default
                    val userId = getCurrentUserId()
                    if (userId != null) {
                        val defaultData = DailyData.createDefault(date, userId)
                        saveDailyData(defaultData) { success, saveError ->
                            if (success) {
                                callback(defaultData, null)
                            } else {
                                callback(null, saveError)
                            }
                        }
                    } else {
                        callback(null, "User not authenticated")
                    }
                } else {
                    callback(null, error)
                }
            }
        }
        
        fun updateUserProfile(
            gender: String,
            height: Double,
            weight: Double,
            age: Int,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            val userId = getCurrentUserId()
            if (userId != null) {
                // First get current user data to ensure we don't overwrite existing fields
                usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentUser = snapshot.getValue(User::class.java)
                        
                        val updates = mutableMapOf<String, Any>(
                            "gender" to gender,
                            "height" to height,
                            "weight" to weight,
                            "age" to age,
                            "updatedAt" to System.currentTimeMillis()
                        )
                        
                        // Ensure required fields are present
                        if (currentUser != null) {
                            updates["uid"] = currentUser.uid
                            updates["email"] = currentUser.email
                        } else {
                            // If no existing user data, add required fields
                            updates["uid"] = userId
                            updates["email"] = auth.currentUser?.email ?: ""
                            updates["createdAt"] = System.currentTimeMillis()
                        }
                        
                        android.util.Log.d("FirebaseHelper", "Updating user profile for userId: $userId")
                        android.util.Log.d("FirebaseHelper", "Updates: $updates")
                        
                        usersRef.child(userId).updateChildren(updates)
                            .addOnSuccessListener {
                                android.util.Log.d("FirebaseHelper", "Profile update successful")
                                onSuccess()
                            }
                            .addOnFailureListener { exception ->
                                android.util.Log.e("FirebaseHelper", "Profile update failed: ${exception.message}")
                                onError(exception.message ?: "Failed to update profile")
                            }
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        android.util.Log.e("FirebaseHelper", "Failed to read current user data: ${error.message}")
                        onError("Failed to read current user data: ${error.message}")
                    }
                })
            } else {
                android.util.Log.e("FirebaseHelper", "User not authenticated")
                onError("User not authenticated")
            }
        }
    }
}
