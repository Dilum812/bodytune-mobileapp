package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

// This class is deprecated - use RunningTrackerFreeActivity instead
class RunningTrackerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Redirect to the free version
        Toast.makeText(this, "Redirecting to free running tracker...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, RunningTrackerFreeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
