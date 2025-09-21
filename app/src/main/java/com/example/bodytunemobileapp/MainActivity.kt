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
import com.example.bodytunemobileapp.utils.CalendarManager
import com.example.bodytunemobileapp.models.DailyData
import com.example.bodytunemobileapp.auth.GoogleSignInHelper
import kotlin.math.pow
import java.util.*

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
    
    // Calendar functionality
    private var selectedDate: Date = CalendarManager.getCurrentDate()
    private var currentDailyData: DailyData? = null
    private val calendarDays = mutableListOf<Date>()
    private val calendarViews = mutableListOf<LinearLayout>()

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
        
        // Initialize calendar
        initializeCalendar()
        
        // Load data for selected date
        loadDataForSelectedDate()
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
        // Reload data for selected date
        loadDataForSelectedDate()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }

    private fun initializeCalendar() {
        // Get the last 7 days including today
        calendarDays.clear()
        calendarDays.addAll(CalendarManager.getLast7Days())
        
        // ALWAYS select today's date by default
        selectedDate = CalendarManager.getCurrentDate()
        
        // Find calendar layout and get all day views
        val calendarLayout = findViewById<LinearLayout>(R.id.calendarLayout)
        calendarViews.clear()
        
        // Get all day LinearLayouts from the calendar
        for (i in 0 until calendarLayout.childCount) {
            val dayView = calendarLayout.getChildAt(i) as LinearLayout
            calendarViews.add(dayView)
        }
        
        // Update calendar with real dates
        updateCalendarDisplay()
        
        // Set up click listeners for calendar days
        setupCalendarClickListeners()
        
        // Show current date info to user
        showSelectedDateInfo()
    }
    
    private fun updateCalendarDisplay() {
        for (i in calendarDays.indices) {
            if (i < calendarViews.size) {
                val dayView = calendarViews[i]
                val date = calendarDays[i]
                
                // Get day name and number TextViews
                val dayNameView = dayView.getChildAt(0) as TextView
                val dayNumberView = dayView.getChildAt(1) as TextView
                
                // Update with real date
                dayNameView.text = CalendarManager.getDayName(date)
                dayNumberView.text = CalendarManager.getDayNumber(date)
                
                // Update appearance based on date status
                updateDayViewAppearance(dayView, date)
            }
        }
    }
    
    private fun updateDayViewAppearance(dayView: LinearLayout, date: Date) {
        val dayNameView = dayView.getChildAt(0) as TextView
        val dayNumberView = dayView.getChildAt(1) as TextView
        
        when {
            CalendarManager.isToday(date) && date == selectedDate -> {
                // Today and selected - highlight with blue background
                dayView.setBackgroundResource(R.drawable.selected_day_background)
                dayNameView.setTextColor(getColor(android.R.color.white))
                dayNumberView.setTextColor(getColor(android.R.color.white))
                dayNumberView.setTypeface(null, android.graphics.Typeface.BOLD)
                
                // Add "TODAY" indicator
                dayNameView.text = "TODAY"
            }
            date == selectedDate -> {
                // Selected but not today
                dayView.setBackgroundResource(R.drawable.selected_day_background)
                dayNameView.setTextColor(getColor(android.R.color.white))
                dayNumberView.setTextColor(getColor(android.R.color.white))
                dayNumberView.setTypeface(null, android.graphics.Typeface.BOLD)
            }
            CalendarManager.isToday(date) -> {
                // Today but not selected - show with emphasis
                dayView.background = null
                dayNameView.setTextColor(getColor(R.color.blue_primary))
                dayNumberView.setTextColor(getColor(R.color.blue_primary))
                dayNumberView.setTypeface(null, android.graphics.Typeface.BOLD)
                dayView.isClickable = true
                
                // Add "TODAY" indicator
                dayNameView.text = "TODAY"
            }
            CalendarManager.isFutureDate(date) -> {
                // Future date - disabled
                dayView.background = null
                dayNameView.setTextColor(getColor(R.color.disabled_text))
                dayNumberView.setTextColor(getColor(R.color.disabled_text))
                dayNumberView.setTypeface(null, android.graphics.Typeface.NORMAL)
                dayView.isClickable = false
            }
            else -> {
                // Past date - available
                dayView.background = null
                dayNameView.setTextColor(getColor(R.color.calendar_text))
                dayNumberView.setTextColor(getColor(R.color.calendar_text))
                dayNumberView.setTypeface(null, android.graphics.Typeface.NORMAL)
                dayView.isClickable = true
            }
        }
        
        // Load progress indicator for this date (async)
        loadProgressIndicatorForDate(dayView, date)
    }
    
    private fun loadProgressIndicatorForDate(dayView: LinearLayout, date: Date) {
        // Only show progress for past dates and today
        if (!CalendarManager.isFutureDate(date)) {
            val dateString = CalendarManager.formatDateForStorage(date)
            
            FirebaseHelper.getDailyData(dateString) { dailyData, error ->
                if (dailyData != null) {
                    // Add subtle progress indicator
                    val dayNumberView = dayView.getChildAt(1) as TextView
                    
                    if (dailyData.isCompleteFitnessDay()) {
                        // Show completion indicator (green dot or checkmark)
                        dayNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_check_small)
                    } else if (dailyData.getDailyGoalProgress() > 0) {
                        // Show partial progress indicator
                        dayNumberView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_progress_dot)
                    }
                }
            }
        }
    }
    
    private fun setupCalendarClickListeners() {
        for (i in calendarDays.indices) {
            if (i < calendarViews.size) {
                val dayView = calendarViews[i]
                val date = calendarDays[i]
                
                dayView.setOnClickListener {
                    if (!CalendarManager.isFutureDate(date)) {
                        selectDate(date)
                    }
                }
            }
        }
    }
    
    private fun selectDate(date: Date) {
        selectedDate = date
        updateCalendarDisplay()
        showSelectedDateInfo()
        loadDataForSelectedDate()
    }
    
    private fun showSelectedDateInfo() {
        val dateDescription = CalendarManager.getDateDescription(selectedDate)
        val dateString = CalendarManager.formatDateForStorage(selectedDate)
        
        // You can add a Toast or update a TextView to show selected date
        // For now, we'll use a subtle toast for user feedback
        if (!CalendarManager.isToday(selectedDate)) {
            Toast.makeText(this, "Viewing data for $dateDescription", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadDataForSelectedDate() {
        val dateString = CalendarManager.formatDateForStorage(selectedDate)
        
        FirebaseHelper.getOrCreateDailyData(dateString) { dailyData, error ->
            if (dailyData != null) {
                currentDailyData = dailyData
                updateUIWithDailyData(dailyData)
            } else {
                // Handle error or show default data
                showDefaultData()
            }
        }
    }
    
    private fun updateUIWithDailyData(dailyData: DailyData) {
        // Update calorie display with progress
        updateCalorieDisplay(dailyData.caloriesConsumed, dailyData.caloriesGoal)
        
        // Update BMI display if available
        if (dailyData.bmiValue > 0) {
            updateBMIDisplay(dailyData.bmiValue, dailyData.bmiCategory)
        } else {
            // Load latest BMI record as fallback
            loadLatestBMIRecord()
        }
        
        // Update workout plan display with progress indicators
        updateWorkoutPlanDisplay(dailyData)
        
        // Update progress indicators
        updateProgressIndicators(dailyData)
    }
    
    private fun updateProgressIndicators(dailyData: DailyData) {
        // Calculate daily progress score
        val progressScore = dailyData.getDailyScore()
        
        // Update daily goal progress bar
        val goalProgress = dailyData.getDailyGoalProgress()
        progressDaily.progress = goalProgress
        
        // Show progress feedback based on selected date
        if (CalendarManager.isToday(selectedDate)) {
            // Today's progress
            if (goalProgress >= 100) {
                // Goal achieved today
                tvCaloriesConsumed.setTextColor(getColor(R.color.bmi_good_green))
            } else if (goalProgress >= 75) {
                // Good progress
                tvCaloriesConsumed.setTextColor(getColor(R.color.bmi_warning_orange))
            } else {
                // Normal progress
                tvCaloriesConsumed.setTextColor(getColor(android.R.color.white))
            }
        } else {
            // Historical data - show completion status
            if (dailyData.isCompleteFitnessDay()) {
                tvCaloriesConsumed.setTextColor(getColor(R.color.bmi_good_green))
            } else {
                tvCaloriesConsumed.setTextColor(getColor(R.color.calendar_text))
            }
        }
    }
    
    private fun updateWorkoutPlanDisplay(dailyData: DailyData) {
        // This can be expanded to show different workout plans based on the day
        // For now, we'll keep the existing display
    }
    
    private fun showDefaultData() {
        // Show default values when no data is available
        updateCalorieDisplay(0.0, 2200.0)
        loadLatestBMIRecord()
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
