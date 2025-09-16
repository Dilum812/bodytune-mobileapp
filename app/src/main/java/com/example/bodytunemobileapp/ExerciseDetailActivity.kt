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
import com.example.bodytunemobileapp.data.ExerciseRepository

class ExerciseDetailActivity : AppCompatActivity() {

    private lateinit var ivClose: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var ivExerciseImage: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvWorkoutType: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvTargetAudience: TextView
    private lateinit var tvInstructions: TextView
    private lateinit var btnStartWorkout: Button
    private lateinit var exercise: Exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_detail)

        // Get exercise from intent
        val exerciseId = intent.getIntExtra("exercise_id", -1)
        exercise = findExerciseById(exerciseId) ?: return

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up UI
        setupUI()

        // Set up click listeners
        setupClickListeners()
    }

    private fun findExerciseById(id: Int): Exercise? {
        // Search through all categories to find the exercise
        for (category in com.example.bodytunemobileapp.data.ExerciseCategory.values()) {
            val exercises = ExerciseRepository.getExercisesByCategory(category)
            exercises.find { it.id == id }?.let { return it }
        }
        return null
    }

    private fun initializeViews() {
        ivClose = findViewById(R.id.ivClose)
        ivProfile = findViewById(R.id.ivProfile)
        ivExerciseImage = findViewById(R.id.ivExerciseImage)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvWorkoutType = findViewById(R.id.tvWorkoutType)
        tvDuration = findViewById(R.id.tvDuration)
        tvTargetAudience = findViewById(R.id.tvTargetAudience)
        tvInstructions = findViewById(R.id.tvInstructions)
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

    private fun setupUI() {
        ivExerciseImage.setImageResource(exercise.imageResource)
        tvExerciseName.text = exercise.name
        tvWorkoutType.text = "${exercise.category.displayName} Workout"
        tvDuration.text = "${exercise.duration} Min"
        tvTargetAudience.text = exercise.targetAudience
        tvInstructions.text = exercise.instructions
    }

    private fun setupClickListeners() {
        ivClose.setOnClickListener {
            finish()
        }

        ivProfile.setOnClickListener {
            // TODO: Navigate to profile screen
        }

        btnStartWorkout.setOnClickListener {
            startWorkout()
        }
    }

    private fun startWorkout() {
        val intent = Intent(this, WorkoutTimerActivity::class.java)
        intent.putExtra("exercise_id", exercise.id)
        intent.putExtra("exercise_name", exercise.name)
        intent.putExtra("exercise_duration", exercise.duration)
        intent.putExtra("exercise_image", exercise.imageResource)
        startActivity(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
