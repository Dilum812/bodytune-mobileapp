package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.example.bodytunemobileapp.auth.GoogleSignInHelper

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartTracking: Button
    private lateinit var ivProfile: ImageView
    private lateinit var navHome: LinearLayout
    private lateinit var navMeals: LinearLayout
    private lateinit var navRun: LinearLayout
    private lateinit var navTrain: LinearLayout
    private lateinit var navBMI: LinearLayout
    private lateinit var workoutsSection: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up click listeners
        setupClickListeners()
        
        // Load profile picture
        loadProfilePicture()
    }

    private fun initializeViews() {
        btnStartTracking = findViewById(R.id.btnStartTracking)
        ivProfile = findViewById(R.id.ivProfile)
        navHome = findViewById(R.id.navHome)
        navMeals = findViewById(R.id.navMeals)
        navRun = findViewById(R.id.navRun)
        navTrain = findViewById(R.id.navTrain)
        navBMI = findViewById(R.id.navBMI)
        workoutsSection = findViewById(R.id.workoutsSection)
    }

    private fun setupFullScreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    private fun setupClickListeners() {
        // Start tracking button - Test Firebase connection
        btnStartTracking.setOnClickListener {
            FirebaseConnectionTest.quickTest(this)
        }

        // Profile image click
        ivProfile.setOnClickListener {
            showProfileMenu()
        }

        // Bottom navigation clicks
        navHome.setOnClickListener {
            // Already on home screen
        }

        navMeals.setOnClickListener {
            // TODO: Navigate to meals screen
        }

        navRun.setOnClickListener {
            // TODO: Navigate to run screen
        }

        navTrain.setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }

        navBMI.setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }

        // Workouts section click
        workoutsSection.setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProfilePicture() {
        ProfilePictureLoader.loadProfilePicture(this, ivProfile)
    }

    private fun showProfileMenu() {
        val userName = ProfilePictureLoader.getUserDisplayName(this)
        val userEmail = ProfilePictureLoader.getUserEmail(this)

        AlertDialog.Builder(this)
            .setTitle("Profile Menu")
            .setMessage("$userName\n$userEmail")
            .setPositiveButton("Sign Out") { _, _ ->
                signOut()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun signOut() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                // Sign out from both Firebase and Google
                val googleSignInHelper = GoogleSignInHelper(this)
                googleSignInHelper.signOut {
                    FirebaseHelper.signOut()
                    Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    navigateToSignIn()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
