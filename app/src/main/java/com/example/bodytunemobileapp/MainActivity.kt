package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.BMIRecord
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.example.bodytunemobileapp.utils.CalorieTracker
import com.example.bodytunemobileapp.auth.GoogleSignInHelper
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    private lateinit var btnStartTracking: Button
    private lateinit var ivProfile: ImageView
    private lateinit var navHome: LinearLayout
    private lateinit var navMeals: LinearLayout
    private lateinit var navRun: LinearLayout
    private lateinit var navTrain: LinearLayout
    private lateinit var navBMI: LinearLayout
    private lateinit var workoutsSection: LinearLayout
    
    // BMI Calculator views
    private lateinit var bmiCard: CardView
    private lateinit var tvBMIValue: TextView
    private lateinit var tvBMICategory: TextView
    private lateinit var bmiProgressBar: ProgressBar
    
    // Calorie Tracker views
    private lateinit var cardDailyGoal: CardView
    private lateinit var progressDaily: ProgressBar
    private lateinit var tvCaloriesConsumed: TextView
    private lateinit var tvCaloriesGoal: TextView
    
    // Current BMI data
    private var currentHeight: Double = 175.0
    private var currentWeight: Double = 68.0
    private var currentBMI: Double = 0.0
    
    // Calorie tracker
    private lateinit var calorieTracker: CalorieTracker

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
        
        // Initialize BMI calculator
        initializeBMICalculator()
        
        // Initialize calorie tracker
        calorieTracker = CalorieTracker()
        loadCalorieData()
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
        
        // BMI views
        bmiCard = findViewById(R.id.bmiCard)
        tvBMIValue = findViewById(R.id.tvBMIValue)
        tvBMICategory = findViewById(R.id.tvBMICategory)
        bmiProgressBar = findViewById(R.id.bmiProgressBar)
        
        // Calorie tracker views
        cardDailyGoal = findViewById(R.id.cardDailyGoal)
        progressDaily = findViewById(R.id.progressDaily)
        tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed)
        tvCaloriesGoal = findViewById(R.id.tvCaloriesGoal)
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
        // Quick Run button click
        btnStartTracking.setOnClickListener {
            val intent = Intent(this, RunningTrackerFreeActivity::class.java)
            startActivity(intent)
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
            val intent = Intent(this, CalorieTrackerActivity::class.java)
            startActivity(intent)
        }

        navRun.setOnClickListener {
            val intent = Intent(this, RunningTrackerFreeActivity::class.java)
            startActivity(intent)
        }

        navTrain.setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }

        navBMI.setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }
        
        // BMI card click
        bmiCard.setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }
        
        // Daily Goal card click
        cardDailyGoal.setOnClickListener {
            val intent = Intent(this, CalorieTrackerActivity::class.java)
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

    private fun initializeBMICalculator() {
        // Load saved BMI data or calculate with default values
        loadLatestBMIRecord()
    }
    
    private fun loadLatestBMIRecord() {
        val userId = FirebaseHelper.getCurrentUserId()
        if (userId != null) {
            FirebaseHelper.getLatestBMIRecord(userId) { bmiRecord, error ->
                if (bmiRecord != null) {
                    currentHeight = bmiRecord.height
                    currentWeight = bmiRecord.weight
                    currentBMI = bmiRecord.bmiValue
                    updateBMIDisplay(currentBMI, bmiRecord.bmiCategory)
                } else {
                    // Use default values and calculate
                    calculateAndUpdateBMI()
                }
            }
        } else {
            // User not logged in, use default values
            calculateAndUpdateBMI()
        }
    }
    
    private fun calculateAndUpdateBMI() {
        val heightInMeters = currentHeight / 100
        currentBMI = currentWeight / (heightInMeters.pow(2))
        val category = getBMICategory(currentBMI)
        updateBMIDisplay(currentBMI, category)
    }
    
    private fun updateBMIDisplay(bmi: Double, category: String) {
        val bmiRounded = String.format("%.1f", bmi)
        tvBMIValue.text = bmiRounded
        tvBMICategory.text = category
        
        // Update color based on category using the new BMI color system
        val (color, progress) = when (category) {
            "Underweight" -> Pair(getColor(R.color.bmi_warning_orange), 25)
            "Normal Weight" -> Pair(getColor(R.color.bmi_good_green), 60)
            "Overweight" -> Pair(getColor(R.color.bmi_warning_orange), 80)
            "Obese" -> Pair(getColor(R.color.bmi_danger_red), 95)
            else -> Pair(getColor(R.color.bmi_good_green), 60)
        }
        
        tvBMICategory.setTextColor(color)
        bmiProgressBar.progress = progress
    }
    
    private fun getBMICategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal Weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload BMI data when returning from BMI Calculator
        loadLatestBMIRecord()
        // Reload calorie data when returning from calorie tracker
        loadCalorieData()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }

    private fun loadCalorieData() {
        calorieTracker.getUserGoalCalories(
            onSuccess = { goalCalories ->
                calorieTracker.getTodaysMeals(
                    onSuccess = { meals ->
                        val dailyNutrition = calorieTracker.calculateDailyNutrition(meals, goalCalories)
                        updateCalorieDisplay(dailyNutrition.totalCalories, goalCalories)
                    },
                    onError = {
                        // Show default values on error
                        updateCalorieDisplay(0.0, 2200.0)
                    }
                )
            },
            onError = {
                // Show default values on error
                updateCalorieDisplay(0.0, 2200.0)
            }
        )
    }

    private fun updateCalorieDisplay(consumedCalories: Double, goalCalories: Double) {
        val remainingCalories = goalCalories - consumedCalories
        val progressPercentage = ((consumedCalories / goalCalories) * 100).toInt().coerceIn(0, 100)
        
        tvCaloriesConsumed.text = String.format("%.0f", consumedCalories)
        tvCaloriesGoal.text = "/ ${goalCalories.toInt()}"
        progressDaily.progress = progressPercentage
    }
}
