package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.data.Exercise
import com.example.bodytunemobileapp.data.ExerciseCategory
import com.example.bodytunemobileapp.data.ExerciseRepository
import com.example.bodytunemobileapp.utils.ModernNotification
import com.example.bodytunemobileapp.utils.SmartAnimations

class ExerciseDetailsActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var ivExerciseImage: ImageView
    private lateinit var tvWorkoutTitle: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTargetAudience: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnStartWorkout: Button
    
    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_details)

        // Get exercise data from intent
        val exerciseId = intent.getIntExtra("exercise_id", 1)
        val categoryName = intent.getStringExtra("category") ?: ""
        
        if (categoryName.isEmpty()) {
            ModernNotification.showError(this, "Invalid exercise data")
            finish()
            return
        }

        val category = ExerciseCategory.valueOf(categoryName)
        val exercises = ExerciseRepository.getExercisesByCategory(category)
        exercise = exercises.find { it.id == exerciseId } ?: exercises.first()

        // Initialize views
        initializeViews()
        
        // Set up full screen
        setupFullScreen()
        
        // Load exercise data
        loadExerciseData()
        
        // Set up click listeners
        setupClickListeners()
        
        // Animate UI entrance
        animateUIEntrance()
    }

    private fun initializeViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfile = findViewById(R.id.ivProfile)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        ivExerciseImage = findViewById(R.id.ivExerciseImage)
        tvWorkoutTitle = findViewById(R.id.tvWorkoutTitle)
        tvDuration = findViewById(R.id.tvDuration)
        tvTargetAudience = findViewById(R.id.tvTargetAudience)
        tvDescription = findViewById(R.id.tvDescription)
        btnStartWorkout = findViewById(R.id.btnStartWorkout)
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

    private fun loadExerciseData() {
        tvExerciseName.text = exercise.name
        tvWorkoutTitle.text = "${exercise.name} Workout"
        tvDuration.text = "45 Min"
        tvTargetAudience.text = "👥 Women & Men"
        tvDescription.text = exercise.instructions ?: "This exercise helps strengthen and tone your muscles while improving overall fitness and endurance."
        
        // Set exercise image
        try {
            ivExerciseImage.setImageResource(exercise.imageResource)
        } catch (e: Exception) {
            ivExerciseImage.setImageResource(R.drawable.icon)
        }
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            SmartAnimations.animateButtonPress(ivBack) {
                finish()
            }
        }

        ivProfile.setOnClickListener {
            // TODO: Navigate to profile
        }

        btnStartWorkout.setOnClickListener {
            SmartAnimations.animateButtonPress(btnStartWorkout) {
                startWorkoutTimer()
            }
        }
    }

    private fun startWorkoutTimer() {
        val intent = Intent(this, WorkoutTimerActivity::class.java)
        intent.putExtra("exercise_id", exercise.id)
        intent.putExtra("exercise_name", exercise.name)
        intent.putExtra("exercise_image", exercise.imageResource)
        startActivity(intent)
    }

    private fun animateUIEntrance() {
        SmartAnimations.animateViewEntrance(findViewById(R.id.headerLayout), delay = 0L)
        SmartAnimations.animateViewEntrance(ivExerciseImage, delay = 100L)
        SmartAnimations.animateViewEntrance(findViewById(R.id.contentLayout), delay = 200L)
        SmartAnimations.animateViewEntrance(btnStartWorkout, delay = 300L)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
