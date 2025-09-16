package com.example.bodytunemobileapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class BodyTuneApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Enable Firebase Realtime Database offline persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
