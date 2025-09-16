package com.example.bodytunemobileapp

import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.BMIRecord
import kotlin.math.pow

class BMICalculatorActivity : AppCompatActivity() {

    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var tvBMIValue: TextView
    private lateinit var tvBMICategory: TextView
    private lateinit var tvBMIDescription: TextView
    private lateinit var btnRecalculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_calculator)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up click listeners
        setupClickListeners()

        // Set default values
        setDefaultValues()
    }

    private fun initializeViews() {
        ivBack = findViewById(R.id.ivBack)
        ivProfile = findViewById(R.id.ivProfile)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        tvBMIValue = findViewById(R.id.tvBMIValue)
        tvBMICategory = findViewById(R.id.tvBMICategory)
        tvBMIDescription = findViewById(R.id.tvBMIDescription)
        btnRecalculate = findViewById(R.id.btnRecalculate)
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
        // Back button
        ivBack.setOnClickListener {
            finish()
        }

        // Profile button
        ivProfile.setOnClickListener {
            // TODO: Navigate to profile screen
        }

        // Recalculate button
        btnRecalculate.setOnClickListener {
            calculateBMI()
        }
    }

    private fun setDefaultValues() {
        etHeight.setText("175")
        etWeight.setText("68")
        calculateBMI()
    }

    private fun calculateBMI() {
        val heightText = etHeight.text.toString().trim()
        val weightText = etWeight.text.toString().trim()

        if (heightText.isEmpty() || weightText.isEmpty()) {
            return
        }

        try {
            val height = heightText.toDouble() / 100 // Convert cm to meters
            val weight = weightText.toDouble()

            if (height <= 0 || weight <= 0) {
                return
            }

            val bmi = weight / (height.pow(2))
            val bmiRounded = String.format("%.1f", bmi)

            tvBMIValue.text = bmiRounded

            // Determine BMI category and description
            val (category, description, color) = getBMICategory(bmi)
            tvBMICategory.text = category
            tvBMIDescription.text = description
            tvBMICategory.setTextColor(getColor(color))

            // Save BMI record to Firebase
            saveBMIRecord(heightText.toDouble(), weight, bmi, category)

        } catch (e: NumberFormatException) {
            // Handle invalid input
        }
    }

    private fun getBMICategory(bmi: Double): Triple<String, String, Int> {
        return when {
            bmi < 18.5 -> Triple(
                "Underweight",
                "Your BMI indicates you're underweight. Consider consulting a healthcare provider for guidance on healthy weight gain.",
                android.R.color.holo_orange_light
            )
            bmi < 25.0 -> Triple(
                "Normal Weight",
                "Your BMI indicates you're at a healthy weight. Maintain a balanced diet and regular exercise.",
                R.color.healthy_green
            )
            bmi < 30.0 -> Triple(
                "Overweight",
                "Your BMI indicates you're overweight. Consider a balanced diet and increased physical activity.",
                android.R.color.holo_orange_light
            )
            else -> Triple(
                "Obese",
                "Your BMI indicates obesity. Consider consulting a healthcare provider for personalized guidance.",
                android.R.color.holo_red_light
            )
        }
    }

    private fun saveBMIRecord(height: Double, weight: Double, bmi: Double, category: String) {
        // Only save if user is authenticated
        if (FirebaseHelper.getCurrentUserId() != null) {
            val bmiRecord = BMIRecord(
                userId = FirebaseHelper.getCurrentUserId()!!,
                height = height,
                weight = weight,
                bmiValue = bmi,
                bmiCategory = category,
                timestamp = System.currentTimeMillis()
            )

            FirebaseHelper.saveBMIRecord(bmiRecord) { success, error ->
                if (success) {
                    Toast.makeText(this, "BMI record saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to save BMI record: $error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
