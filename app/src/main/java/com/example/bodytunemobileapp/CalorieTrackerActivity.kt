package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.models.DailyNutrition
import com.example.bodytunemobileapp.models.MealEntry
import com.example.bodytunemobileapp.models.MealType
import com.example.bodytunemobileapp.utils.CalorieTracker
import com.example.bodytunemobileapp.utils.ProfilePictureLoader

class CalorieTrackerActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var circularProgress: ProgressBar
    private lateinit var tvCaloriesConsumed: TextView
    private lateinit var tvCaloriesLeft: TextView
    
    private lateinit var cardBreakfast: CardView
    private lateinit var cardLunch: CardView
    private lateinit var cardDinner: CardView
    private lateinit var cardSnacks: CardView
    
    private lateinit var tvBreakfastCalories: TextView
    private lateinit var tvLunchCalories: TextView
    private lateinit var tvDinnerCalories: TextView
    private lateinit var tvSnacksCalories: TextView
    
    private lateinit var progressBreakfast: ProgressBar
    private lateinit var progressLunch: ProgressBar
    private lateinit var progressDinner: ProgressBar
    private lateinit var progressSnacks: ProgressBar
    
    private lateinit var btnAddMeal: Button
    
    private lateinit var calorieTracker: CalorieTracker
    private var dailyNutrition: DailyNutrition? = null
    private var goalCalories = 2200.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_calorie_tracker)

            initializeViews()
            setupFullScreen()
            setupClickListeners()
            loadProfilePicture()
            
            calorieTracker = CalorieTracker()
            loadCalorieData()
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
        }
    }

    private fun initializeViews() {
        try {
            ivBack = findViewById(R.id.ivBack)
            ivProfile = findViewById(R.id.ivProfile)
            circularProgress = findViewById(R.id.circularProgress)
            tvCaloriesConsumed = findViewById(R.id.tvCaloriesConsumed)
            tvCaloriesLeft = findViewById(R.id.tvCaloriesLeft)
            
            cardBreakfast = findViewById(R.id.cardBreakfast)
            cardLunch = findViewById(R.id.cardLunch)
            cardDinner = findViewById(R.id.cardDinner)
            cardSnacks = findViewById(R.id.cardSnacks)
            
            tvBreakfastCalories = findViewById(R.id.tvBreakfastCalories)
            tvLunchCalories = findViewById(R.id.tvLunchCalories)
            tvDinnerCalories = findViewById(R.id.tvDinnerCalories)
            tvSnacksCalories = findViewById(R.id.tvSnacksCalories)
            
            progressBreakfast = findViewById(R.id.progressBreakfast)
            progressLunch = findViewById(R.id.progressLunch)
            progressDinner = findViewById(R.id.progressDinner)
            progressSnacks = findViewById(R.id.progressSnacks)
            
            btnAddMeal = findViewById(R.id.btnAddMeal)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
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
        ivBack.setOnClickListener {
            finish()
        }
        
        btnAddMeal.setOnClickListener {
            val intent = Intent(this, AddMealActivity::class.java)
            startActivity(intent)
        }
        
        cardBreakfast.setOnClickListener {
            openAddMeal(MealType.BREAKFAST)
        }
        
        cardLunch.setOnClickListener {
            openAddMeal(MealType.LUNCH)
        }
        
        cardDinner.setOnClickListener {
            openAddMeal(MealType.DINNER)
        }
        
        cardSnacks.setOnClickListener {
            openAddMeal(MealType.SNACKS)
        }
        
        // Bottom navigation
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navMeals).setOnClickListener {
            // Already on meals screen
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navRun).setOnClickListener {
            val intent = Intent(this, RunningTrackerFreeActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navTrain).setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navBMI).setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openAddMeal(mealType: MealType) {
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("meal_type", mealType.name)
        startActivity(intent)
    }

    private fun loadProfilePicture() {
        try {
            ProfilePictureLoader.loadProfilePicture(this, ivProfile)
        } catch (e: Exception) {
            e.printStackTrace()
            // Set default placeholder if profile loading fails
            ivProfile.setImageResource(R.drawable.profile_placeholder)
        }
    }

    private fun loadCalorieData() {
        try {
            // Load goal calories first
            calorieTracker.getUserGoalCalories(
                onSuccess = { goal ->
                    goalCalories = goal
                    loadTodaysMeals()
                },
                onError = {
                    goalCalories = 2200.0 // Default
                    loadTodaysMeals()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Show default empty state
            goalCalories = 2200.0
            updateUI()
        }
    }

    private fun loadTodaysMeals() {
        try {
            calorieTracker.getTodaysMeals(
                onSuccess = { meals ->
                    dailyNutrition = calorieTracker.calculateDailyNutrition(meals, goalCalories)
                    updateUI()
                },
                onError = { error ->
                    // Show default empty state on error
                    dailyNutrition = DailyNutrition(goalCalories = goalCalories)
                    updateUI()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Show default empty state
            dailyNutrition = DailyNutrition(goalCalories = goalCalories)
            updateUI()
        }
    }

    private fun updateUI() {
        try {
            val nutrition = dailyNutrition ?: DailyNutrition(goalCalories = goalCalories)
            
            // Update circular progress
            val consumedCalories = nutrition.totalCalories
            val remainingCalories = nutrition.goalCalories - consumedCalories
            val progressPercentage = ((consumedCalories / nutrition.goalCalories) * 100).toInt().coerceIn(0, 100)
            
            circularProgress.progress = progressPercentage
            tvCaloriesConsumed.text = String.format("%.0f", remainingCalories.coerceAtLeast(0.0))
            tvCaloriesLeft.text = if (remainingCalories > 0) "kcal left" else "over goal"
            
            // Update meal cards
            updateMealCard(
                tvBreakfastCalories,
                progressBreakfast,
                nutrition.breakfastCalories,
                500.0 // Default breakfast goal
            )
            
            updateMealCard(
                tvLunchCalories,
                progressLunch,
                nutrition.lunchCalories,
                600.0 // Default lunch goal
            )
            
            updateMealCard(
                tvDinnerCalories,
                progressDinner,
                nutrition.dinnerCalories,
                700.0 // Default dinner goal
            )
            
            updateMealCard(
                tvSnacksCalories,
                progressSnacks,
                nutrition.snacksCalories,
                300.0 // Default snacks goal
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Set default values on error
            circularProgress.progress = 0
            tvCaloriesConsumed.text = "2200"
            tvCaloriesLeft.text = "kcal left"
        }
    }

    private fun updateMealCard(
        caloriesTextView: TextView,
        progressBar: ProgressBar,
        consumedCalories: Double,
        goalCalories: Double
    ) {
        caloriesTextView.text = "${consumedCalories.toInt()}/${goalCalories.toInt()} kcal"
        val progress = ((consumedCalories / goalCalories) * 100).toInt().coerceIn(0, 100)
        progressBar.progress = progress
    }

    override fun onResume() {
        super.onResume()
        loadCalorieData() // Refresh data when returning to screen
    }
}
