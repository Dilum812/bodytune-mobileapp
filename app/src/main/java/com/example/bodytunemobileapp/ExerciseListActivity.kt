package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bodytunemobileapp.adapters.ExerciseAdapter
import com.example.bodytunemobileapp.data.Exercise
import com.example.bodytunemobileapp.data.ExerciseCategory
import com.example.bodytunemobileapp.data.ExerciseRepository
import com.example.bodytunemobileapp.utils.ModernNotification

class ExerciseListActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var tvCategoryTitle: TextView
    private lateinit var rvExercises: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter
    private lateinit var category: ExerciseCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)

        // Get category from intent
        val categoryName = intent.getStringExtra("category") ?: ""
        category = ExerciseCategory.valueOf(categoryName)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up UI
        setupUI()

        // Set up RecyclerView
        setupRecyclerView()

        // Set up click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfile = findViewById(R.id.ivProfile)
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle)
        rvExercises = findViewById(R.id.rvExercises)
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
    }

    private fun setupRecyclerView() {
        exerciseAdapter = ExerciseAdapter { exercise ->
            navigateToExerciseDetail(exercise)
        }
        
        rvExercises.apply {
            layoutManager = GridLayoutManager(this@ExerciseListActivity, 2)
            adapter = exerciseAdapter
        }
        
        // Load exercises for this category
        val exercises = ExerciseRepository.getExercisesByCategory(category)
        exerciseAdapter.submitList(exercises)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        ivProfile.setOnClickListener {
            // TODO: Navigate to profile screen
        }
    }

    private fun navigateToExerciseDetail(exercise: Exercise) {
        val intent = Intent(this, ExerciseDetailActivity::class.java)
        intent.putExtra("exercise_id", exercise.id)
        startActivity(intent)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
