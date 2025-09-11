package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.databinding.ActivityCalorieTrackerBinding

class CalorieTrackerActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCalorieTrackerBinding
    
    // Sample data - in real app this would come from database
    private var totalCaloriesConsumed = 1467
    private var dailyCalorieGoal = 2000
    private var breakfastCalories = 320
    private var breakfastGoal = 500
    private var lunchCalories = 450
    private var lunchGoal = 600
    private var dinnerCalories = 380
    private var dinnerGoal = 700
    private var snacksCalories = 200
    private var snacksGoal = 300
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalorieTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupClickListeners()
        updateCalorieData()
    }
    
    private fun setupUI() {
        // Hide system bars for immersive experience
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.btnAddMeal.setOnClickListener {
            startActivity(Intent(this, AddMealActivity::class.java))
        }
        
        binding.cardBreakfast.setOnClickListener {
            openAddMeal("Breakfast")
        }
        
        binding.cardLunch.setOnClickListener {
            openAddMeal("Lunch")
        }
        
        binding.cardDinner.setOnClickListener {
            openAddMeal("Dinner")
        }
        
        binding.cardSnacks.setOnClickListener {
            openAddMeal("Snacks")
        }
    }
    
    private fun openAddMeal(mealType: String) {
        val intent = Intent(this, AddMealActivity::class.java)
        intent.putExtra("meal_type", mealType)
        startActivity(intent)
    }
    
    private fun updateCalorieData() {
        // Update main calorie display
        binding.tvCaloriesLeft.text = (dailyCalorieGoal - totalCaloriesConsumed).toString()
        
        // Update meal cards
        binding.tvBreakfastCalories.text = "$breakfastCalories/$breakfastGoal kcal"
        binding.progressBreakfast.progress = ((breakfastCalories.toFloat() / breakfastGoal) * 100).toInt()
        
        binding.tvLunchCalories.text = "$lunchCalories/$lunchGoal kcal"
        binding.progressLunch.progress = ((lunchCalories.toFloat() / lunchGoal) * 100).toInt()
        
        binding.tvDinnerCalories.text = "$dinnerCalories/$dinnerGoal kcal"
        binding.progressDinner.progress = ((dinnerCalories.toFloat() / dinnerGoal) * 100).toInt()
        
        binding.tvSnacksCalories.text = "$snacksCalories/$snacksGoal kcal"
        binding.progressSnacks.progress = ((snacksCalories.toFloat() / snacksGoal) * 100).toInt()
        
        // Update circular progress
        val overallProgress = ((totalCaloriesConsumed.toFloat() / dailyCalorieGoal) * 100).toInt()
        binding.circularProgress.progress = overallProgress
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh data when returning from add meal screen
        updateCalorieData()
    }
}
