package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.data.Exercise
import com.example.bodytunemobileapp.data.ExerciseCategory
import com.example.bodytunemobileapp.data.ExerciseRepository
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.WorkoutSession
import com.example.bodytunemobileapp.utils.ModernNotification
import com.example.bodytunemobileapp.utils.SmartAnimations

class SingleExerciseTimerActivity : AppCompatActivity() {

    // UI Components
    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var tvExerciseCounter: TextView
    private lateinit var ivExerciseImage: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvExerciseDescription: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var tvTimeRemaining: TextView
    private lateinit var progressTimer: ProgressBar
    
    // Control Buttons
    private lateinit var btnPlayPause: Button
    private lateinit var btnRestart: Button
    private lateinit var btnSkip: Button
    private lateinit var btnNext: Button
    
    // Exercise Data
    private lateinit var category: ExerciseCategory
    private lateinit var allExercises: List<Exercise>
    private var currentExercise: Exercise? = null
    private var currentExerciseIndex = 0
    private var categoryExerciseIds: IntArray = intArrayOf()
    
    // Timer Variables
    private var countDownTimer: CountDownTimer? = null
    private val exerciseDurationMs: Long = 3 * 60 * 1000L // 3 minutes in milliseconds
    private var timeLeftInMillis: Long = exerciseDurationMs
    private var isTimerRunning = false
    private var isPaused = false
    
    // Workout Tracking
    private var workoutStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_single_exercise_timer)

            // Get data from intent
            val exerciseId = intent.getIntExtra("exercise_id", 1)
            val categoryName = intent.getStringExtra("category") ?: ""
            
            if (categoryName.isEmpty()) {
                ModernNotification.showError(this, "Invalid category")
                finish()
                return
            }
            
            category = ExerciseCategory.valueOf(categoryName)
            categoryExerciseIds = intent.getIntArrayExtra("category_exercises") ?: intArrayOf()
            
            // Get all exercises for this category
            allExercises = ExerciseRepository.getExercisesByCategory(category)
            
            if (allExercises.isEmpty()) {
                ModernNotification.showError(this, "No exercises found for this category")
                finish()
                return
            }
            
            // Find current exercise index
            currentExerciseIndex = allExercises.indexOfFirst { it.id == exerciseId }.coerceAtLeast(0)
            currentExercise = allExercises.getOrNull(currentExerciseIndex)

            // Initialize views
            initializeViews()

            // Set up immersive full-screen experience
            setupFullScreen()

            // Set up UI
            setupUI()

            // Set up click listeners
            setupClickListeners()

            // Load current exercise
            loadCurrentExercise()
            
            // Start workout tracking
            workoutStartTime = System.currentTimeMillis()
            
            // Animate UI entrance
            animateUIEntrance()
            
        } catch (e: Exception) {
            ModernNotification.showError(this, "Error loading exercise: ${e.message}")
            finish()
        }
    }

    private fun initializeViews() {
        try {
            ivBack = findViewById(R.id.ivBack)
            ivProfile = findViewById(R.id.ivProfile)
            tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
            tvExerciseCounter = findViewById(R.id.tvExerciseCounter)
            ivExerciseImage = findViewById(R.id.ivExerciseImage)
            tvExerciseName = findViewById(R.id.tvExerciseName)
            tvExerciseDescription = findViewById(R.id.tvExerciseDescription)
            tvInstructions = findViewById(R.id.tvInstructions)
            tvTimeRemaining = findViewById(R.id.tvTimeRemaining)
            progressTimer = findViewById(R.id.progressTimer)
            
            btnPlayPause = findViewById(R.id.btnPlayPause)
            btnRestart = findViewById(R.id.btnRestart)
            btnSkip = findViewById(R.id.btnSkip)
            btnNext = findViewById(R.id.btnNext)
        } catch (e: Exception) {
            ModernNotification.showError(this, "Error initializing views: ${e.message}")
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

    private fun setupUI() {
        tvCategoryTitle.text = category.displayName
        updateExerciseCounter()
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            SmartAnimations.animateButtonPress(ivBack) {
                showExitConfirmation()
            }
        }

        btnPlayPause.setOnClickListener {
            SmartAnimations.animateButtonPress(btnPlayPause) {
                toggleTimer()
            }
        }

        btnRestart.setOnClickListener {
            SmartAnimations.animateButtonPress(btnRestart) {
                restartTimer()
            }
        }

        btnSkip.setOnClickListener {
            SmartAnimations.animateButtonPress(btnSkip) {
                skipCurrentExercise()
            }
        }

        btnNext.setOnClickListener {
            SmartAnimations.animateButtonPress(btnNext) {
                goToNextExercise()
            }
        }
    }

    private fun loadCurrentExercise() {
        try {
            currentExercise?.let { exercise ->
                // Update UI
                tvExerciseName.text = exercise.name ?: "Exercise"
                tvExerciseDescription.text = exercise.description ?: "Exercise description"
                
                // Always ensure instructions are visible and populated
                val instructions = if (exercise.instructions.isNullOrBlank()) {
                    "Perform this exercise with proper form and technique. Focus on controlled movements and maintain good posture throughout the exercise."
                } else {
                    exercise.instructions
                }
                tvInstructions.text = instructions
                
                // Ensure instructions card is always visible
                findViewById<androidx.cardview.widget.CardView>(R.id.instructionsCard)?.let { card ->
                    card.visibility = android.view.View.VISIBLE
                }
                
                // Safely set image resource
                try {
                    ivExerciseImage.setImageResource(exercise.imageResource)
                } catch (e: Exception) {
                    ivExerciseImage.setImageResource(R.drawable.icon) // Fallback image
                }
                
                // Reset timer to 3 minutes
                timeLeftInMillis = exerciseDurationMs
                updateTimerDisplay()
                updateTimerProgress()
                
                // Update navigation buttons
                updateNavigationButtons()
                
                // Update counter
                updateExerciseCounter()
                
                // Show exercise info
                ModernNotification.showInfo(this, "Ready: ${exercise.name}")
            } ?: run {
                ModernNotification.showError(this, "Exercise not found")
                finish()
            }
        } catch (e: Exception) {
            ModernNotification.showError(this, "Error loading exercise: ${e.message}")
            finish()
        }
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
                updateTimerProgress()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerDisplay()
                updateTimerProgress()
                isTimerRunning = false
                isPaused = false
                
                // Exercise completed
                completeCurrentExercise()
            }
        }.start()
        
        isTimerRunning = true
        isPaused = false
        updatePlayPauseButton()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isPaused = true
        updatePlayPauseButton()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun toggleTimer() {
        if (isTimerRunning) {
            pauseTimer()
        } else {
            resumeTimer()
        }
    }

    private fun restartTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = exerciseDurationMs
        updateTimerDisplay()
        updateTimerProgress()
        isTimerRunning = false
        isPaused = false
        updatePlayPauseButton()
        ModernNotification.showInfo(this, "Timer reset to 3 minutes")
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isPaused = false
        updatePlayPauseButton()
    }

    private fun completeCurrentExercise() {
        // Save workout session
        saveWorkoutSession()
        
        // Show modern completion modal
        showExerciseCompleteModal()
    }

    private fun goToNextExercise() {
        if (currentExerciseIndex < allExercises.size - 1) {
            stopTimer()
            currentExerciseIndex++
            currentExercise = allExercises[currentExerciseIndex]
            loadCurrentExercise()
        } else {
            // No more exercises, show completion
            showWorkoutCompletionDialog()
        }
    }

    private fun skipCurrentExercise() {
        AlertDialog.Builder(this)
            .setTitle("Skip Exercise")
            .setMessage("Are you sure you want to skip this exercise?")
            .setPositiveButton("Skip") { _, _ ->
                stopTimer()
                goToNextExercise()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        tvTimeRemaining.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateTimerProgress() {
        val progress = ((exerciseDurationMs - timeLeftInMillis).toFloat() / exerciseDurationMs.toFloat() * 100).toInt()
        SmartAnimations.animateProgressBar(progressTimer, progress)
    }

    private fun updateExerciseCounter() {
        tvExerciseCounter.text = "${currentExerciseIndex + 1} of ${allExercises.size}"
    }

    private fun updateNavigationButtons() {
        btnNext.isEnabled = currentExerciseIndex < allExercises.size - 1
        btnNext.alpha = if (currentExerciseIndex < allExercises.size - 1) 1.0f else 0.5f
        
        // Update next button text
        if (currentExerciseIndex < allExercises.size - 1) {
            btnNext.text = "Next Exercise"
        } else {
            btnNext.text = "Finish"
        }
    }

    private fun updatePlayPauseButton() {
        // Always keep white text color and blue background
        btnPlayPause.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnPlayPause.setBackgroundResource(R.drawable.play_pause_button_background)
        
        if (isPaused || !isTimerRunning) {
            btnPlayPause.text = "▶"
        } else {
            btnPlayPause.text = "⏸"
        }
    }

    private fun saveWorkoutSession() {
        val endTime = System.currentTimeMillis()
        val duration = ((endTime - workoutStartTime) / 1000).toLong() // in seconds
        val estimatedCalories = ((duration / 60) * 5).toInt() // 5 calories per minute
        
        val workoutSession = WorkoutSession(
            sessionId = "",
            userId = FirebaseHelper.getCurrentUserId() ?: "",
            workoutType = category.displayName,
            exerciseName = currentExercise?.name ?: "",
            startTime = workoutStartTime,
            endTime = endTime,
            duration = duration,
            caloriesBurned = estimatedCalories,
            notes = "Single exercise workout"
        )
        
        // Save to Firebase
        FirebaseHelper.saveWorkoutSession(workoutSession) { success, error ->
            if (success) {
                ModernNotification.showSuccess(this, "Workout saved!")
            }
        }
    }

    private fun showExerciseCompletionDialog() {
        val duration = ((System.currentTimeMillis() - workoutStartTime) / 1000).toInt()
        val minutes = duration / 60
        val seconds = duration % 60
        
        // Show modern completion modal
        showExerciseCompleteModal()
    }
    
    private fun showExerciseCompleteModal() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_exercise_complete, null)
        
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        
        // Make dialog background transparent for rounded corners
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        // Calculate stats
        val completionTimeMs = exerciseDurationMs - timeLeftInMillis
        val minutes = (completionTimeMs / 60000).toInt()
        val seconds = ((completionTimeMs % 60000) / 1000).toInt()
        val caloriesBurned = ((completionTimeMs / 60000) * 5).toInt() // 5 calories per minute
        
        // Update modal content
        dialogView.findViewById<TextView>(R.id.tvCompletionTime)?.text = "${minutes}m ${seconds}s"
        dialogView.findViewById<TextView>(R.id.tvCaloriesBurned)?.text = "~${caloriesBurned} kcal"
        
        // Set button listeners
        dialogView.findViewById<Button>(R.id.btnRestart)?.setOnClickListener {
            dialog.dismiss()
            restartTimer()
        }
        
        dialogView.findViewById<Button>(R.id.btnFinish)?.setOnClickListener {
            dialog.dismiss()
            saveWorkoutSession()
            finish()
        }
        
        dialogView.findViewById<Button>(R.id.btnNextExercise)?.setOnClickListener {
            dialog.dismiss()
            goToNextExercise()
        }
        
        dialog.show()
    }

    private fun showWorkoutCompletionDialog() {
        AlertDialog.Builder(this)
            .setTitle("")
            .setMessage("You've completed all exercises in ${category.displayName}!\n\nGreat work! ")
            .setPositiveButton("New Workout") { _, _ ->
                val intent = Intent(this, WorkoutModuleActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Finish") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Exercise")
            .setMessage("Are you sure you want to exit this exercise?")
            .setPositiveButton("Exit") { _, _ ->
                stopTimer()
                finish()
            }
            .setNegativeButton("Stay", null)
            .show()
    }

    private fun animateUIEntrance() {
        try {
            SmartAnimations.animateViewEntrance(findViewById(R.id.headerLayout), delay = 0L)
            SmartAnimations.animateViewEntrance(findViewById(R.id.exerciseContainer), delay = 100L)
            SmartAnimations.animateViewEntrance(tvTimeRemaining, delay = 200L)
            SmartAnimations.animateViewEntrance(findViewById(R.id.controlsContainer), delay = 300L)
        } catch (e: Exception) {
            // Continue without animations if there's an error
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }

    override fun onBackPressed() {
        showExitConfirmation()
    }
}
