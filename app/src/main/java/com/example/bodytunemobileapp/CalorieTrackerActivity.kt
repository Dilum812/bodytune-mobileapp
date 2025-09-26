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
import com.example.bodytunemobileapp.utils.CalendarManager
import com.example.bodytunemobileapp.utils.SmartAnimations
import android.widget.Toast
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.Date

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
    
    // Date selector views
    private lateinit var day1Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day2Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day3Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day4Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day5Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day6Container: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var day7Container: androidx.constraintlayout.widget.ConstraintLayout
    
    private lateinit var tvDay1Name: TextView
    private lateinit var tvDay1Number: TextView
    private lateinit var tvDay2Name: TextView
    private lateinit var tvDay2Number: TextView
    private lateinit var tvDay3Name: TextView
    private lateinit var tvDay3Number: TextView
    private lateinit var tvDay4Name: TextView
    private lateinit var tvDay4Number: TextView
    private lateinit var tvDay5Name: TextView
    private lateinit var tvDay5Number: TextView
    private lateinit var tvDay6Name: TextView
    private lateinit var tvDay6Number: TextView
    private lateinit var tvDay7Name: TextView
    private lateinit var tvDay7Number: TextView
    
    private lateinit var calorieTracker: CalorieTracker
    private var dailyNutrition: DailyNutrition? = null
    private var goalCalories = 2200.0
    private var selectedDate: Date = CalendarManager.getCurrentDate()
    private var weekDates: List<Date> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_calorie_tracker)

            initializeViews()
            setupFullScreen()
            setupDateSelector()
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
            
            // Initialize date selector views
            day1Container = findViewById(R.id.day1Container)
            day2Container = findViewById(R.id.day2Container)
            day3Container = findViewById(R.id.day3Container)
            day4Container = findViewById(R.id.day4Container)
            day5Container = findViewById(R.id.day5Container)
            day6Container = findViewById(R.id.day6Container)
            day7Container = findViewById(R.id.day7Container)
            
            tvDay1Name = findViewById(R.id.tvDay1Name)
            tvDay1Number = findViewById(R.id.tvDay1Number)
            tvDay2Name = findViewById(R.id.tvDay2Name)
            tvDay2Number = findViewById(R.id.tvDay2Number)
            tvDay3Name = findViewById(R.id.tvDay3Name)
            tvDay3Number = findViewById(R.id.tvDay3Number)
            tvDay4Name = findViewById(R.id.tvDay4Name)
            tvDay4Number = findViewById(R.id.tvDay4Number)
            tvDay5Name = findViewById(R.id.tvDay5Name)
            tvDay5Number = findViewById(R.id.tvDay5Number)
            tvDay6Name = findViewById(R.id.tvDay6Name)
            tvDay6Number = findViewById(R.id.tvDay6Number)
            tvDay7Name = findViewById(R.id.tvDay7Name)
            tvDay7Number = findViewById(R.id.tvDay7Number)
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
            SmartAnimations.animateButtonPress(ivBack) {
                finish()
            }
        }
        
        btnAddMeal.setOnClickListener {
            SmartAnimations.animateButtonPress(btnAddMeal) {
                val intent = Intent(this, AddMealActivity::class.java)
                intent.putExtra("selected_date", CalendarManager.formatDateForStorage(selectedDate))
                startActivity(intent)
            }
        }
        
        cardBreakfast.setOnClickListener {
            SmartAnimations.animateCardPress(cardBreakfast) {
                openAddMeal(MealType.BREAKFAST)
            }
        }
        
        cardLunch.setOnClickListener {
            SmartAnimations.animateCardPress(cardLunch) {
                openAddMeal(MealType.LUNCH)
            }
        }
        
        cardDinner.setOnClickListener {
            SmartAnimations.animateCardPress(cardDinner) {
                openAddMeal(MealType.DINNER)
            }
        }
        
        cardSnacks.setOnClickListener {
            SmartAnimations.animateCardPress(cardSnacks) {
                openAddMeal(MealType.SNACKS)
            }
        }
        
        // Date selector click listeners
        day1Container.setOnClickListener { selectDate(0) }
        day2Container.setOnClickListener { selectDate(1) }
        day3Container.setOnClickListener { selectDate(2) }
        day4Container.setOnClickListener { selectDate(3) }
        day5Container.setOnClickListener { selectDate(4) }
        day6Container.setOnClickListener { selectDate(5) }
        day7Container.setOnClickListener { selectDate(6) }
        
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
        intent.putExtra("selected_date", CalendarManager.formatDateForStorage(selectedDate))
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
    
    private fun animateUIEntrance() {
        // Enhanced UI entrance with premium animations
        val uiElements = listOf(
            findViewById<View>(R.id.headerLayout),
            findViewById<View>(R.id.progressContainer),
            findViewById<View>(R.id.dateSelector),
            cardBreakfast,
            cardLunch,
            cardDinner,
            cardSnacks,
            btnAddMeal
        )
        
        // Animate UI elements with stagger
        uiElements.forEachIndexed { index, view ->
            SmartAnimations.animateViewEntrance(view, delay = (index * 100L))
        }
        
        // Special animation for profile picture
        ivProfile.postDelayed({
            SmartAnimations.animateBounce(ivProfile)
        }, 600L)
    }

    private fun loadCalorieData() {
        try {
            // Load goal calories first
            calorieTracker.getUserGoalCalories(
                onSuccess = { goal ->
                    goalCalories = goal
                    loadMealsForSelectedDate()
                },
                onError = {
                    goalCalories = 2200.0 // Default
                    loadMealsForSelectedDate()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Show default empty state
            goalCalories = 2200.0
            updateUI()
        }
    }

    private fun loadMealsForSelectedDate() {
        try {
            val dateString = CalendarManager.formatDateForStorage(selectedDate)
            calorieTracker.getMealsForDate(
                dateString,
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
            
            SmartAnimations.animateProgressBar(circularProgress, progressPercentage)
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
        val newText = "${consumedCalories.toInt()}/${goalCalories.toInt()} kcal"
        SmartAnimations.animateTextChange(caloriesTextView, newText)
        
        val progress = ((consumedCalories / goalCalories) * 100).toInt().coerceIn(0, 100)
        SmartAnimations.animateProgressBar(progressBar, progress, duration = SmartAnimations.DURATION_MEDIUM)
    }

    private fun setupDateSelector() {
        weekDates = CalendarManager.getLast7Days()
        updateDateSelector()
        
        // Select today by default
        val todayIndex = weekDates.indexOfFirst { CalendarManager.isToday(it) }
        if (todayIndex != -1) {
            selectedDate = weekDates[todayIndex]
        }
    }
    
    private fun updateDateSelector() {
        val dayContainers = listOf(day1Container, day2Container, day3Container, day4Container, day5Container, day6Container, day7Container)
        val dayNames = listOf(tvDay1Name, tvDay2Name, tvDay3Name, tvDay4Name, tvDay5Name, tvDay6Name, tvDay7Name)
        val dayNumbers = listOf(tvDay1Number, tvDay2Number, tvDay3Number, tvDay4Number, tvDay5Number, tvDay6Number, tvDay7Number)
        
        weekDates.forEachIndexed { index, date ->
            if (index < dayNames.size) {
                dayNames[index].text = CalendarManager.getDayName(date)
                dayNumbers[index].text = CalendarManager.getDayNumber(date)
                
                // Update appearance based on selection and date type
                updateDayAppearance(dayContainers[index], dayNames[index], dayNumbers[index], date)
            }
        }
    }
    
    private fun updateDayAppearance(
        container: androidx.constraintlayout.widget.ConstraintLayout,
        nameView: TextView,
        numberView: TextView,
        date: Date
    ) {
        val isSelected = CalendarManager.formatDateForStorage(date) == CalendarManager.formatDateForStorage(selectedDate)
        val isToday = CalendarManager.isToday(date)
        val isFuture = CalendarManager.isFutureDate(date)
        
        when {
            isSelected -> {
                container.setBackgroundResource(R.drawable.selected_day_background)
                nameView.setTextColor(ContextCompat.getColor(this, R.color.white))
                numberView.setTextColor(ContextCompat.getColor(this, R.color.white))
                container.isEnabled = true
            }
            isFuture -> {
                container.setBackgroundResource(0)
                nameView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
                numberView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
                container.isEnabled = false
            }
            else -> {
                container.setBackgroundResource(0)
                nameView.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
                numberView.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
                container.isEnabled = true
            }
        }
    }
    
    private fun selectDate(dayIndex: Int) {
        if (dayIndex < weekDates.size) {
            val newDate = weekDates[dayIndex]
            
            // Don't allow future dates
            if (CalendarManager.isFutureDate(newDate)) {
                Toast.makeText(this, "Cannot view future dates", Toast.LENGTH_SHORT).show()
                return
            }
            
            selectedDate = newDate
            updateDateSelector()
            
            // Show feedback for non-today dates
            if (!CalendarManager.isToday(selectedDate)) {
                val description = CalendarManager.getDateDescription(selectedDate)
                Toast.makeText(this, "Viewing $description", Toast.LENGTH_SHORT).show()
            }
            
            // Load data for selected date
            loadCalorieData()
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadCalorieData() // Refresh data when returning to screen
    }
}
