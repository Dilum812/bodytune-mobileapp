package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.BMIRecord
import com.example.bodytunemobileapp.models.DailyData
import com.example.bodytunemobileapp.utils.AchievementModal
import com.example.bodytunemobileapp.utils.CalendarManager
import com.example.bodytunemobileapp.utils.CalorieTracker
import com.example.bodytunemobileapp.utils.ModernNotification
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.example.bodytunemobileapp.utils.SmartAnimations
import kotlin.math.pow
import java.util.*
import android.util.Log

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
    
    // Achievement UI components
    private lateinit var achievementCard1: CardView
    private lateinit var achievementCard2: CardView
    private lateinit var achievementCard3: CardView
    private lateinit var achievementIcon1: ImageView
    private lateinit var achievementIcon2: ImageView
    private lateinit var achievementIcon3: ImageView
    private lateinit var achievementTitle1: TextView
    private lateinit var achievementTitle2: TextView
    private lateinit var achievementTitle3: TextView
    private lateinit var achievementProgress1: TextView
    private lateinit var achievementProgress2: TextView
    private lateinit var achievementProgress3: TextView
    
    // Calorie Tracker views
    private lateinit var cardDailyGoal: CardView
    private lateinit var progressDaily: ProgressBar
    private lateinit var tvCaloriesConsumed: TextView
    private lateinit var tvCaloriesGoal: TextView
    
    // Current BMI data
    private var currentHeight: Double = 175.0
    private var currentWeight: Double = 68.0
    private var currentBMI: Double = 0.0
    
    // Current selected date
    private var selectedDate: String = CalendarManager.formatDateForStorage(Date())
    private var currentDailyData: DailyData? = null
    private val calendarDays = mutableListOf<Date>()
    private val calendarViews = mutableListOf<LinearLayout>()
    
    // CalorieTracker instance
    private val calorieTracker = CalorieTracker()
    
    // Achievement modal
    private lateinit var achievementModal: AchievementModal

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
        
        // Initialize achievement modal
        achievementModal = AchievementModal(this)
        
        // Initialize calorie tracking (handled by FirebaseHelper)
        
        // Initialize calendar
        initializeCalendar()
        
        // Load data for selected date
        loadDataForSelectedDate()
        
        // Animate UI entrance
        animateUIEntrance()
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
        
        // Achievement views
        achievementCard1 = findViewById(R.id.achievementCard1)
        achievementCard2 = findViewById(R.id.achievementCard2)
        achievementCard3 = findViewById(R.id.achievementCard3)
        achievementIcon1 = findViewById(R.id.achievementIcon1)
        achievementIcon2 = findViewById(R.id.achievementIcon2)
        achievementIcon3 = findViewById(R.id.achievementIcon3)
        achievementTitle1 = findViewById(R.id.achievementTitle1)
        achievementTitle2 = findViewById(R.id.achievementTitle2)
        achievementTitle3 = findViewById(R.id.achievementTitle3)
        achievementProgress1 = findViewById(R.id.achievementProgress1)
        achievementProgress2 = findViewById(R.id.achievementProgress2)
        achievementProgress3 = findViewById(R.id.achievementProgress3)
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
        // Quick Run button with animation
        btnStartTracking.setOnClickListener {
            SmartAnimations.animateButtonPress(btnStartTracking) {
                val intent = Intent(this, QuickRunActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        // Profile image click with animation
        ivProfile.setOnClickListener {
            SmartAnimations.animateBounce(ivProfile)
            showProfileMenu()
        }

        // Bottom navigation clicks with animations
        navHome.setOnClickListener {
            SmartAnimations.animateButtonPress(navHome)
            // Already on home screen
        }

        navMeals.setOnClickListener {
            SmartAnimations.animateButtonPress(navMeals) {
                val intent = Intent(this, CalorieTrackerActivity::class.java)
                // Pass the current selected date to CalorieTracker
                intent.putExtra("selected_date", selectedDate)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        navRun.setOnClickListener {
            SmartAnimations.animateButtonPress(navRun) {
                val intent = Intent(this, RunningTrackerFreeActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        navTrain.setOnClickListener {
            SmartAnimations.animateButtonPress(navTrain) {
                val intent = Intent(this, WorkoutModuleActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        navBMI.setOnClickListener {
            SmartAnimations.animateButtonPress(navBMI) {
                val intent = Intent(this, BMICalculatorActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        
        // BMI card click with animation
        bmiCard.setOnClickListener {
            SmartAnimations.animateCardPress(bmiCard) {
                val intent = Intent(this, BMICalculatorActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        
        // Daily Goal card click with animation
        cardDailyGoal.setOnClickListener {
            SmartAnimations.animateCardPress(cardDailyGoal) {
                val intent = Intent(this, CalorieTrackerActivity::class.java)
                // Pass the current selected date to CalorieTracker
                intent.putExtra("selected_date", selectedDate)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        
        // Workouts section click with animation
        workoutsSection.setOnClickListener {
            SmartAnimations.animateButtonPress(workoutsSection) {
                val intent = Intent(this, WorkoutModuleActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }
    }

    private fun loadProfilePicture() {
        ProfilePictureLoader.loadProfilePicture(this, ivProfile)
    }
    
    private fun refreshProfilePicture() {
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
                // Sign out from Firebase
                FirebaseHelper.signOut()
                ModernNotification.showSuccess(this, "Signed out successfully")
                
                // Navigate to sign-in screen
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
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
        
        // Animate BMI value and category changes
        SmartAnimations.animateTextChange(tvBMIValue, bmiRounded)
        SmartAnimations.animateTextChange(tvBMICategory, category)
        
        // Update color based on category using the new BMI color system
        val (color, progress) = when (category) {
            "Underweight" -> Pair(ContextCompat.getColor(this, R.color.bmi_warning_orange), 25)
            "Normal Weight" -> Pair(ContextCompat.getColor(this, R.color.bmi_good_green), 60)
            "Overweight" -> Pair(ContextCompat.getColor(this, R.color.bmi_warning_orange), 80)
            "Obese" -> Pair(ContextCompat.getColor(this, R.color.bmi_danger_red), 95)
            else -> Pair(ContextCompat.getColor(this, R.color.bmi_good_green), 60)
        }
        
        tvBMICategory.setTextColor(color)
        
        // Animate progress bar
        SmartAnimations.animateProgressBar(bmiProgressBar, progress)
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
        Log.d("MainActivity", "onResume called - current selectedDate: $selectedDate")
        
        // Refresh profile picture in case user updated their Google account
        refreshProfilePicture()
        
        // Reload data for selected date
        loadDataForSelectedDate()
        
        // Also refresh today's data if we're viewing today (common case when returning from CalorieTracker)
        val todayString = CalendarManager.formatDateForStorage(Date())
        if (selectedDate == todayString) {
            Log.d("MainActivity", "Refreshing today's data on resume")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
    
    private fun animateUIEntrance() {
        // Enhanced UI entrance with smoother animations
        val views = listOf(
            findViewById<View>(R.id.headerLayout),
            findViewById<View>(R.id.calendarLayout),
            findViewById<View>(R.id.statsRow1),
            findViewById<View>(R.id.statsRow2),
            findViewById<View>(R.id.workoutsSection),
            findViewById<View>(R.id.achievementsSection),
            findViewById<View>(R.id.bottomNavigation)
        )
        
        // Animate views with stagger
        views.forEachIndexed { index, view ->
            SmartAnimations.animateViewEntrance(view, delay = (index * 120L))
        }
        
        // Special animation for profile picture
        ivProfile.postDelayed({
            SmartAnimations.animateBounce(ivProfile)
        }, 900L)
    }

    private fun initializeCalendar() {
        // Get the last 7 days including today
        calendarDays.clear()
        calendarDays.addAll(CalendarManager.getLast7Days())
        
        // ALWAYS select today's date by default
        selectedDate = CalendarManager.formatDateForStorage(CalendarManager.getCurrentDate())
        
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
        
        // Always show the actual day name (Sun, Mon, Tue, etc.)
        dayNameView.text = CalendarManager.getDayName(date)
        
        when {
            CalendarManager.formatDateForStorage(date) == selectedDate -> {
                // Selected date - highlight with blue background
                dayView.setBackgroundResource(R.drawable.selected_day_background)
                dayNameView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                dayNumberView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
                dayNumberView.setTypeface(null, android.graphics.Typeface.BOLD)
                dayView.isClickable = true
            }
            CalendarManager.isFutureDate(date) -> {
                // Future date - disabled
                dayView.background = null
                dayNameView.setTextColor(ContextCompat.getColor(this, R.color.disabled_text))
                dayNumberView.setTextColor(ContextCompat.getColor(this, R.color.disabled_text))
                dayNumberView.setTypeface(null, android.graphics.Typeface.NORMAL)
                dayView.isClickable = false
            }
            else -> {
                // Past date or today (not selected) - available
                dayView.background = null
                dayNameView.setTextColor(ContextCompat.getColor(this, R.color.calendar_text))
                dayNumberView.setTextColor(ContextCompat.getColor(this, R.color.calendar_text))
                dayNumberView.setTypeface(null, android.graphics.Typeface.NORMAL)
                dayView.isClickable = true
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
        val newSelectedDate = CalendarManager.formatDateForStorage(date)
        Log.d("MainActivity", "Selecting new date: $newSelectedDate (was: $selectedDate)")
        selectedDate = newSelectedDate
        updateCalendarDisplay()
        showSelectedDateInfo()
        loadDataForSelectedDate()
    }
    
    private fun showSelectedDateInfo() {
        // Find the matching date from calendarDays
        val selectedDateObj = calendarDays.find { 
            CalendarManager.formatDateForStorage(it) == selectedDate 
        } ?: Date()
        val dateDescription = CalendarManager.getDateDescription(selectedDateObj)
        
        // You can add a Toast or update a TextView to show selected date
        // For now, we'll use a subtle toast for user feedback
        if (!CalendarManager.isToday(selectedDateObj)) {
            ModernNotification.showInfo(this, "Viewing data for $dateDescription")
        }
    }
    
    private fun loadDataForSelectedDate() {
        val dateString = selectedDate
        
        // Load calorie data from meal entries for the selected date
        Log.d("MainActivity", "Loading data for selected date: $dateString")
        calorieTracker.getDailyNutritionForDate(dateString,
            onSuccess = { nutrition ->
                Log.d("MainActivity", "Received nutrition data: ${nutrition.totalCalories}/${nutrition.goalCalories}")
                // Update UI immediately with real calorie data
                updateCalorieDisplay(nutrition.totalCalories, nutrition.goalCalories)
                
                // Also sync with DailyData for persistence
                FirebaseHelper.getOrCreateDailyData(dateString) { dailyData, error ->
                    if (error == null && dailyData != null) {
                        // Update daily data with real calorie information
                        val updatedDailyData = dailyData.copy(
                            caloriesConsumed = nutrition.totalCalories,
                            caloriesGoal = nutrition.goalCalories
                        )
                        
                        // Save the updated data back to Firebase
                        FirebaseHelper.saveDailyData(updatedDailyData) { success, saveError ->
                            currentDailyData = updatedDailyData
                            // Update other UI components (BMI, achievements, etc.)
                            updateWorkoutPlanDisplay(updatedDailyData)
                            updateProgressIndicators(updatedDailyData)
                        }
                    } else {
                        // Create new daily data with calorie information
                        val userId = FirebaseHelper.getCurrentUserId() ?: ""
                        val newDailyData = DailyData.createDefault(dateString, userId).copy(
                            caloriesConsumed = nutrition.totalCalories,
                            caloriesGoal = nutrition.goalCalories
                        )
                        
                        FirebaseHelper.saveDailyData(newDailyData) { success, saveError ->
                            currentDailyData = newDailyData
                            updateWorkoutPlanDisplay(newDailyData)
                            updateProgressIndicators(newDailyData)
                        }
                    }
                }
                
                // Load BMI data separately
                loadLatestBMIRecord()
            },
            onError = { error ->
                Log.e("MainActivity", "Error loading calorie data for $dateString: $error")
                // If calorie data fails to load, show default calorie display
                updateCalorieDisplay(0.0, 2200.0)
                
                // Try to load daily data without calorie sync
                FirebaseHelper.getOrCreateDailyData(dateString) { dailyData, error ->
                    if (error == null && dailyData != null) {
                        currentDailyData = dailyData
                        updateWorkoutPlanDisplay(dailyData)
                        updateProgressIndicators(dailyData)
                    } else {
                        showDefaultData()
                    }
                }
                
                // Load BMI data separately
                loadLatestBMIRecord()
            }
        )
    }
    
    private fun updateUI(dailyData: DailyData) {
        // Update calorie display with progress
        updateCalorieDisplay(dailyData.caloriesConsumed, dailyData.caloriesGoal)
        
        // Update BMI display - always load latest BMI record for consistency
        loadLatestBMIRecord()
        
        // Update workout plan display with progress indicators
        updateWorkoutPlanDisplay(dailyData)
        
        // Update progress indicators
        updateProgressIndicators(dailyData)
    }
    
    private fun updateProgressIndicators(dailyData: DailyData) {
        // Update daily goal progress bar
        val goalProgress = dailyData.getDailyGoalProgress()
        progressDaily.progress = goalProgress
    }
    
    private fun updateWorkoutPlanDisplay(dailyData: DailyData) {
        // This can be expanded to show different workout plans based on the day
        // For now, we'll keep the existing display
        
        // Check for achievements and show notifications
        checkForAchievements(dailyData)
        
        // Update achievements with simple static content
        updateSimpleAchievements(dailyData)
    }
    
    private fun checkForAchievements(dailyData: DailyData) {
        val selectedDateObj = calendarDays.find { 
            CalendarManager.formatDateForStorage(it) == selectedDate 
        } ?: Date()
        
        // Check for calorie goal achievement
        if (dailyData.caloriesConsumed >= dailyData.caloriesGoal && dailyData.caloriesConsumed > 0) {
            if (CalendarManager.isToday(selectedDateObj)) {
                ModernNotification.showSuccess(this, "🎉 Daily calorie goal achieved!")
            }
        }
        
        // Check for workout achievement
        if (dailyData.workoutsCompleted > 0 && CalendarManager.isToday(selectedDateObj)) {
            ModernNotification.showSuccess(this, "💪 Great job completing your workout!")
        }
        
        // Check for running achievement
        if (dailyData.hasRunToday && dailyData.runningDistance > 0 && CalendarManager.isToday(selectedDateObj)) {
            ModernNotification.showSuccess(this, "🏃‍♂️ Running session completed!")
        }
    }
    
    private fun updateSimpleAchievements(dailyData: DailyData) {
        // Update achievement card 1 - Workout Streak
        val workoutStreak = dailyData.workoutsCompleted
        achievementTitle1.text = "Workout Streak"
        achievementProgress1.text = "$workoutStreak/7"
        achievementIcon1.setColorFilter(if (workoutStreak >= 7) 
            ContextCompat.getColor(this, R.color.bmi_good_green) else 
            ContextCompat.getColor(this, R.color.blue_primary))
        
        // Update achievement card 2 - Calorie Goal
        val calorieProgress = if (dailyData.caloriesGoal > 0) {
            ((dailyData.caloriesConsumed / dailyData.caloriesGoal) * 7).toInt().coerceIn(0, 7)
        } else 0
        achievementTitle2.text = "Calorie Master"
        achievementProgress2.text = "$calorieProgress/7"
        achievementIcon2.setColorFilter(if (calorieProgress >= 7) 
            ContextCompat.getColor(this, R.color.bmi_good_green) else 
            ContextCompat.getColor(this, R.color.bmi_warning_orange))
        
        // Update achievement card 3 - Total Workouts
        val totalWorkouts = dailyData.workoutsCompleted
        achievementTitle3.text = "Fitness Pro"
        achievementProgress3.text = "$totalWorkouts/10"
        achievementIcon3.setColorFilter(if (totalWorkouts >= 10) 
            ContextCompat.getColor(this, R.color.bmi_good_green) else 
            ContextCompat.getColor(this, R.color.purple))
        
        // Add click listeners for achievement details
        setupAchievementClickListeners()
    }
    
    private fun setupAchievementClickListeners() {
        achievementCard1.setOnClickListener {
            SmartAnimations.animateCardPress(achievementCard1) {
                showAchievementModal(
                    title = "Workout Streak",
                    description = "Complete workouts for 7 consecutive days to unlock new workout plans and advanced training routines.",
                    reward = "Unlock new workout plans",
                    currentValue = currentDailyData?.workoutsCompleted ?: 3,
                    targetValue = 7,
                    iconResource = R.drawable.ic_medal,
                    iconColor = ContextCompat.getColor(this, R.color.blue_primary),
                    progressColor = R.color.blue_primary
                )
            }
        }
        
        achievementCard2.setOnClickListener {
            SmartAnimations.animateCardPress(achievementCard2) {
                val calorieProgress = if (currentDailyData?.caloriesGoal ?: 0.0 > 0) {
                    ((currentDailyData?.caloriesConsumed ?: 0.0) / (currentDailyData?.caloriesGoal ?: 2200.0) * 7).toInt().coerceIn(0, 7)
                } else 5
                
                showAchievementModal(
                    title = "Calorie Master",
                    description = "Meet your daily calorie goal for 7 consecutive days to unlock personalized meal recommendations and nutrition insights.",
                    reward = "Unlock meal recommendations",
                    currentValue = calorieProgress,
                    targetValue = 7,
                    iconResource = R.drawable.ic_fire,
                    iconColor = ContextCompat.getColor(this, R.color.bmi_warning_orange),
                    progressColor = R.color.bmi_warning_orange
                )
            }
        }
        
        achievementCard3.setOnClickListener {
            SmartAnimations.animateCardPress(achievementCard3) {
                showAchievementModal(
                    title = "Fitness Pro",
                    description = "Complete 10 total workouts to unlock advanced exercises, specialized training programs, and expert fitness tips.",
                    reward = "Unlock advanced exercises",
                    currentValue = currentDailyData?.workoutsCompleted ?: 3,
                    targetValue = 10,
                    iconResource = R.drawable.ic_trophy,
                    iconColor = ContextCompat.getColor(this, R.color.purple),
                    progressColor = R.color.purple
                )
            }
        }
    }
    
    private fun showAchievementModal(
        title: String,
        description: String,
        reward: String,
        currentValue: Int,
        targetValue: Int,
        iconResource: Int,
        iconColor: Int,
        progressColor: Int
    ) {
        val achievementData = AchievementModal.AchievementData(
            title = title,
            description = description,
            reward = reward,
            currentValue = currentValue,
            targetValue = targetValue,
            iconResource = iconResource,
            iconColor = iconColor,
            progressColor = progressColor
        )
        
        achievementModal.show(achievementData)
    }
    
    private fun showDefaultData() {
        // Show default values when no data is available
        updateCalorieDisplay(0.0, 2200.0)
        loadLatestBMIRecord()
        
        // Initialize achievements with realistic default data based on selected date
        val userId = FirebaseHelper.getCurrentUserId() ?: ""
        val defaultDailyData = DailyData.createDefault(selectedDate, userId)
        
        // Save the default data for future reference
        FirebaseHelper.saveDailyData(defaultDailyData) { success, error ->
            // Continue regardless of save success
        }
        
        currentDailyData = defaultDailyData
        updateSimpleAchievements(defaultDailyData)
    }

    private fun updateCalorieDisplay(consumedCalories: Double, goalCalories: Double) {
        Log.d("MainActivity", "Updating calorie display: $consumedCalories/$goalCalories")
        val remainingCalories = goalCalories - consumedCalories
        val progressPercentage = ((consumedCalories / goalCalories) * 100).toInt().coerceIn(0, 100)
        
        // Animate calorie number changes
        SmartAnimations.animateTextChange(tvCaloriesConsumed, String.format("%.0f", consumedCalories))
        SmartAnimations.animateTextChange(tvCaloriesGoal, "/ ${goalCalories.toInt()}")
        
        // Animate progress bar
        SmartAnimations.animateProgressBar(progressDaily, progressPercentage)
        
        // Keep the progress bar blue to match original design
        tvCaloriesConsumed.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        progressDaily.progressTintList = ContextCompat.getColorStateList(this, R.color.blue_primary)
        
        Log.d("MainActivity", "Calorie display updated successfully")
    }
    
    override fun onBackPressed() {
        if (::achievementModal.isInitialized && achievementModal.isShowing()) {
            achievementModal.dismiss()
        } else {
            super.onBackPressed()
        }
    }
}
