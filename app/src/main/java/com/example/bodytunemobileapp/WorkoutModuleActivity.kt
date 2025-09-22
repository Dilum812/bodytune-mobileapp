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
import com.example.bodytunemobileapp.utils.SmartAnimations

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
        
        // Animate UI entrance
        animateUIEntrance()
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
            SmartAnimations.animateButtonPress(ivBack) {
                finish()
            }
        }

        ivProfile.setOnClickListener {
            SmartAnimations.animateBounce(ivProfile)
            // TODO: Navigate to profile screen
        }

        // Workout category click listeners with animations
        cardCardio.setOnClickListener {
            SmartAnimations.animateCardPress(cardCardio) {
                navigateToExerciseList(ExerciseCategory.CARDIO_ENDURANCE)
            }
        }

        cardStrength.setOnClickListener {
            SmartAnimations.animateCardPress(cardStrength) {
                navigateToExerciseList(ExerciseCategory.STRENGTH_TRAINING)
            }
        }

        cardCore.setOnClickListener {
            SmartAnimations.animateCardPress(cardCore) {
                navigateToExerciseList(ExerciseCategory.CORE_ABS)
            }
        }

        cardFlexibility.setOnClickListener {
            SmartAnimations.animateCardPress(cardFlexibility) {
                navigateToExerciseList(ExerciseCategory.FLEXIBILITY_MOBILITY)
            }
        }

        cardHIIT.setOnClickListener {
            SmartAnimations.animateCardPress(cardHIIT) {
                navigateToExerciseList(ExerciseCategory.HIIT)
            }
        }

        cardYoga.setOnClickListener {
            SmartAnimations.animateCardPress(cardYoga) {
                navigateToExerciseList(ExerciseCategory.YOGA)
            }
        }

        cardPilates.setOnClickListener {
            SmartAnimations.animateCardPress(cardPilates) {
                navigateToExerciseList(ExerciseCategory.PILATES)
            }
        }
    }
    
    private fun animateUIEntrance() {
        // Animate header
        SmartAnimations.animateViewEntrance(findViewById(R.id.headerLayout), delay = 0L)
        
        // Animate workout cards with stagger
        SmartAnimations.animateViewEntrance(cardCardio, delay = 100L)
        SmartAnimations.animateViewEntrance(cardStrength, delay = 200L)
        SmartAnimations.animateViewEntrance(cardCore, delay = 300L)
        SmartAnimations.animateViewEntrance(cardFlexibility, delay = 400L)
        SmartAnimations.animateViewEntrance(cardHIIT, delay = 500L)
        SmartAnimations.animateViewEntrance(cardYoga, delay = 600L)
        SmartAnimations.animateViewEntrance(cardPilates, delay = 700L)
    }

    private fun navigateToExerciseList(category: ExerciseCategory) {
        try {
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
            intent.putExtra("categoryDisplayName", categoryName)
            startActivity(intent)
            
            // Add smooth transition
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            
        } catch (e: Exception) {
            ModernNotification.showError(this, "Unable to load workout category. Please try again.")
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
