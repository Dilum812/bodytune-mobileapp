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

class WorkoutSessionActivity : AppCompatActivity() {

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
    private lateinit var progressWorkout: ProgressBar
    private lateinit var tvWorkoutProgress: TextView
    
    // Control Buttons
    private lateinit var btnPrevious: Button
    private lateinit var btnPlayPause: Button
    private lateinit var btnNext: Button
    private lateinit var btnSkip: Button
    private lateinit var btnFinish: Button
    
    // Workout Data
    private lateinit var category: ExerciseCategory
    private lateinit var exercises: List<Exercise>
    private var currentExerciseIndex = 0
    private var currentExercise: Exercise? = null
    
    // Timer Variables
    private var countDownTimer: CountDownTimer? = null
    private val exerciseDurationMs: Long = 3 * 60 * 1000L // 3 minutes in milliseconds
    private var timeLeftInMillis: Long = exerciseDurationMs
    private var isTimerRunning = false
    private var isPaused = false
    
    // Workout Session Tracking
    private var workoutStartTime: Long = 0
    private var totalExercisesCompleted = 0
    private var workoutSession: WorkoutSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_session)

        // Get category from intent
        val categoryName = intent.getStringExtra("category") ?: ""
        category = ExerciseCategory.valueOf(categoryName)
        exercises = ExerciseRepository.getExercisesByCategory(category)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up UI
        setupUI()

        // Set up click listeners
        setupClickListeners()

        // Start workout session
        startWorkoutSession()

        // Load first exercise
        loadCurrentExercise()
        
        // Animate UI entrance
        animateUIEntrance()
    }

    private fun initializeViews() {
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
        progressWorkout = findViewById(R.id.progressWorkout)
        tvWorkoutProgress = findViewById(R.id.tvWorkoutProgress)
        
        btnPrevious = findViewById(R.id.btnPrevious)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnNext = findViewById(R.id.btnNext)
        btnSkip = findViewById(R.id.btnSkip)
        btnFinish = findViewById(R.id.btnFinish)
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
        updateWorkoutProgress()
        updateExerciseCounter()
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            SmartAnimations.animateButtonPress(ivBack) {
                showExitConfirmation()
            }
        }

        btnPrevious.setOnClickListener {
            SmartAnimations.animateButtonPress(btnPrevious) {
                goToPreviousExercise()
            }
        }

        btnPlayPause.setOnClickListener {
            SmartAnimations.animateButtonPress(btnPlayPause) {
                toggleTimer()
            }
        }

        btnNext.setOnClickListener {
            SmartAnimations.animateButtonPress(btnNext) {
                goToNextExercise()
            }
        }

        btnSkip.setOnClickListener {
            SmartAnimations.animateButtonPress(btnSkip) {
                skipCurrentExercise()
            }
        }

        btnFinish.setOnClickListener {
            SmartAnimations.animateButtonPress(btnFinish) {
                finishWorkout()
            }
        }
    }

    private fun startWorkoutSession() {
        workoutStartTime = System.currentTimeMillis()
        
        // Create workout session for tracking
        workoutSession = WorkoutSession(
            sessionId = "",
            userId = FirebaseHelper.getCurrentUserId() ?: "",
            workoutType = category.displayName,
            exerciseName = "",
            startTime = workoutStartTime,
            endTime = 0L,
            duration = 0L,
            caloriesBurned = 0,
            notes = ""
        )
    }

    private fun loadCurrentExercise() {
        if (currentExerciseIndex >= exercises.size) {
            completeWorkout()
            return
        }

        currentExercise = exercises[currentExerciseIndex]
        currentExercise?.let { exercise ->
            // Update UI
            tvExerciseName.text = exercise.name
            tvExerciseDescription.text = exercise.description
            tvInstructions.text = exercise.instructions
            ivExerciseImage.setImageResource(exercise.imageResource)
            
            // Reset timer to 3 minutes
            timeLeftInMillis = exerciseDurationMs
            updateTimerDisplay()
            updateTimerProgress()
            
            // Update navigation buttons
            updateNavigationButtons()
            
            // Update counters
            updateExerciseCounter()
            updateWorkoutProgress()
            
            // Auto-start timer
            startTimer()
            
            // Show exercise info
            ModernNotification.showInfo(this, "Starting: ${exercise.name}")
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
                
                // Exercise completed - auto move to next
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

    private fun completeCurrentExercise() {
        totalExercisesCompleted++
        
        // Show completion message
        ModernNotification.showSuccess(this, "Exercise completed! 🎉")
        
        // Auto move to next exercise after a short delay
        SmartAnimations.animateViewExit(findViewById(R.id.exerciseContainer), onComplete = {
            currentExerciseIndex++
            loadCurrentExercise()
        })
    }

    private fun goToNextExercise() {
        if (currentExerciseIndex < exercises.size - 1) {
            stopTimer()
            currentExerciseIndex++
            loadCurrentExercise()
        } else {
            completeWorkout()
        }
    }

    private fun goToPreviousExercise() {
        if (currentExerciseIndex > 0) {
            stopTimer()
            currentExerciseIndex--
            loadCurrentExercise()
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

    private fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isPaused = false
        updatePlayPauseButton()
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

    private fun updateWorkoutProgress() {
        val progress = ((currentExerciseIndex.toFloat() / exercises.size.toFloat()) * 100).toInt()
        SmartAnimations.animateProgressBar(progressWorkout, progress)
        tvWorkoutProgress.text = "${currentExerciseIndex}/${exercises.size} exercises"
    }

    private fun updateExerciseCounter() {
        tvExerciseCounter.text = "${currentExerciseIndex + 1} of ${exercises.size}"
    }

    private fun updateNavigationButtons() {
        btnPrevious.isEnabled = currentExerciseIndex > 0
        btnNext.isEnabled = currentExerciseIndex < exercises.size - 1
        
        // Update button colors based on state
        btnPrevious.alpha = if (currentExerciseIndex > 0) 1.0f else 0.5f
        btnNext.alpha = if (currentExerciseIndex < exercises.size - 1) 1.0f else 0.5f
    }

    private fun updatePlayPauseButton() {
        if (isPaused || !isTimerRunning) {
            btnPlayPause.text = "▶"
            btnPlayPause.setBackgroundColor(ContextCompat.getColor(this, R.color.bmi_good_green))
        } else {
            btnPlayPause.text = "⏸"
            btnPlayPause.setBackgroundColor(ContextCompat.getColor(this, R.color.bmi_warning_orange))
        }
    }

    private fun completeWorkout() {
        val endTime = System.currentTimeMillis()
        val duration = ((endTime - workoutStartTime) / 1000).toInt() // in seconds
        val estimatedCalories = calculateCaloriesBurned(duration, totalExercisesCompleted)
        
        // Update workout session
        workoutSession?.let { session ->
            val completedSession = session.copy(
                endTime = endTime,
                duration = duration.toLong(),
                caloriesBurned = estimatedCalories,
                exerciseName = "${category.displayName} Workout",
                notes = "Completed $totalExercisesCompleted/${exercises.size} exercises"
            )
            
            // Save to Firebase
            FirebaseHelper.saveWorkoutSession(completedSession) { success, error ->
                if (success) {
                    ModernNotification.showSuccess(this, "Workout saved successfully!")
                } else {
                    ModernNotification.showError(this, "Failed to save workout: $error")
                }
            }
        }
        
        // Show completion dialog
        showWorkoutCompletionDialog(duration, totalExercisesCompleted, estimatedCalories)
    }

    private fun calculateCaloriesBurned(durationSeconds: Int, exercisesCompleted: Int): Int {
        // Simple estimation: 5 calories per minute + 10 calories per exercise completed
        val minutes = durationSeconds / 60
        return (minutes * 5) + (exercisesCompleted * 10)
    }

    private fun showWorkoutCompletionDialog(duration: Int, exercisesCompleted: Int, calories: Int) {
        val minutes = duration / 60
        val seconds = duration % 60
        
        AlertDialog.Builder(this)
            .setTitle("🎉 Workout Complete!")
            .setMessage("""
                Great job! Here's your workout summary:
                
                ⏱️ Duration: ${minutes}m ${seconds}s
                💪 Exercises: $exercisesCompleted/${exercises.size}
                🔥 Calories: ~$calories kcal
                📈 Category: ${category.displayName}
                
                Keep up the great work!
            """.trimIndent())
            .setPositiveButton("Finish") { _, _ ->
                finish()
            }
            .setNegativeButton("New Workout") { _, _ ->
                // Navigate back to workout selection
                val intent = Intent(this, WorkoutModuleActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun finishWorkout() {
        AlertDialog.Builder(this)
            .setTitle("Finish Workout")
            .setMessage("Are you sure you want to finish this workout session?")
            .setPositiveButton("Finish") { _, _ ->
                completeWorkout()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    private fun showExitConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Exit Workout")
            .setMessage("Are you sure you want to exit? Your progress will be lost.")
            .setPositiveButton("Exit") { _, _ ->
                stopTimer()
                finish()
            }
            .setNegativeButton("Stay", null)
            .show()
    }

    private fun animateUIEntrance() {
        SmartAnimations.animateViewEntrance(findViewById(R.id.headerLayout), delay = 0L)
        SmartAnimations.animateViewEntrance(findViewById(R.id.exerciseContainer), delay = 100L)
        SmartAnimations.animateViewEntrance(tvTimeRemaining, delay = 200L)
        SmartAnimations.animateViewEntrance(findViewById(R.id.controlsContainer), delay = 300L)
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
