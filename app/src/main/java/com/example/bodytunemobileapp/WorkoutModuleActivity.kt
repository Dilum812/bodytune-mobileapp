package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.data.ExerciseCategory
import com.example.bodytunemobileapp.utils.ModernNotification

class WorkoutModuleActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var cardCardio: CardView
    private lateinit var cardStrength: CardView
    private lateinit var cardCore: CardView
    private lateinit var cardFlexibility: CardView
    private lateinit var cardHIIT: CardView
    private lateinit var cardYoga: CardView
    private lateinit var cardPilates: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_module)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfile = findViewById(R.id.ivProfile)
        cardCardio = findViewById(R.id.cardCardio)
        cardStrength = findViewById(R.id.cardStrength)
        cardCore = findViewById(R.id.cardCore)
        cardFlexibility = findViewById(R.id.cardFlexibility)
        cardHIIT = findViewById(R.id.cardHIIT)
        cardYoga = findViewById(R.id.cardYoga)
        cardPilates = findViewById(R.id.cardPilates)
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

        ivProfile.setOnClickListener {
            // TODO: Navigate to profile screen
        }

        // Workout category click listeners
        cardCardio.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.CARDIO_ENDURANCE)
        }

        cardStrength.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.STRENGTH_TRAINING)
        }

        cardCore.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.CORE_ABS)
        }

        cardFlexibility.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.FLEXIBILITY_MOBILITY)
        }

        cardHIIT.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.HIIT)
        }

        cardYoga.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.YOGA)
        }

        cardPilates.setOnClickListener {
            navigateToExerciseList(ExerciseCategory.PILATES)
        }
    }

    private fun navigateToExerciseList(category: ExerciseCategory) {
        val categoryName = when(category) {
            ExerciseCategory.CARDIO_ENDURANCE -> "Cardio"
            ExerciseCategory.STRENGTH_TRAINING -> "Strength Training"
            ExerciseCategory.CORE_ABS -> "Core & Abs"
            ExerciseCategory.FLEXIBILITY_MOBILITY -> "Flexibility"
            ExerciseCategory.HIIT -> "HIIT"
            ExerciseCategory.YOGA -> "Yoga"
            ExerciseCategory.PILATES -> "Pilates"
        }
        ModernNotification.showInfo(this, "Starting $categoryName workout")
        val intent = Intent(this, ExerciseListActivity::class.java)
        intent.putExtra("category", category.name)
        startActivity(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
