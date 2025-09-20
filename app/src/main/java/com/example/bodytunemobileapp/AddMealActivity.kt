package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.models.Food
import com.example.bodytunemobileapp.models.MealType
import com.example.bodytunemobileapp.utils.CalorieTracker
import com.example.bodytunemobileapp.utils.FoodDatabase
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.example.bodytunemobileapp.utils.FirebaseDebugHelper
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class AddMealActivity : AppCompatActivity() {

    private lateinit var ivClose: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var etSearch: EditText
    private lateinit var foodDetailsCard: CardView
    private lateinit var mealTypeContainer: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var btnDone: Button
    
    private lateinit var tvFoodName: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvProteinValue: TextView
    private lateinit var tvCarbsValue: TextView
    private lateinit var tvFatValue: TextView
    private lateinit var progressProtein: ProgressBar
    private lateinit var progressCarbs: ProgressBar
    private lateinit var progressFat: ProgressBar
    
    private lateinit var btnMinus: ImageView
    private lateinit var btnPlus: ImageView
    private lateinit var tvQuantity: TextView
    private lateinit var spinnerUnit: Spinner
    
    private lateinit var btnBreakfast: Button
    private lateinit var btnLunch: Button
    private lateinit var btnDinner: Button
    private lateinit var btnSnacks: Button
    
    private var selectedFood: Food? = null
    private var selectedMealType: MealType = MealType.BREAKFAST
    private var quantity: Double = 100.0
    private lateinit var calorieTracker: CalorieTracker
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meal)

        initializeViews()
        setupFullScreen()
        setupClickListeners()
        loadProfilePicture()
        
        calorieTracker = CalorieTracker()
        auth = FirebaseAuth.getInstance()
        
        // Check authentication
        checkAuthentication()
        
        // Check if meal type was passed from previous screen
        val mealTypeExtra = intent.getStringExtra("meal_type")
        if (mealTypeExtra != null) {
            selectedMealType = MealType.valueOf(mealTypeExtra)
            updateMealTypeSelection()
        }
        
        setupSearch()
    }

    private fun initializeViews() {
        ivClose = findViewById(R.id.ivClose)
        ivProfile = findViewById(R.id.ivProfile)
        etSearch = findViewById(R.id.etSearch)
        foodDetailsCard = findViewById(R.id.foodDetailsCard)
        mealTypeContainer = findViewById(R.id.mealTypeContainer)
        btnDone = findViewById(R.id.btnDone)
        
        tvFoodName = findViewById(R.id.tvFoodName)
        tvCalories = findViewById(R.id.tvCalories)
        tvProteinValue = findViewById(R.id.tvProteinValue)
        tvCarbsValue = findViewById(R.id.tvCarbsValue)
        tvFatValue = findViewById(R.id.tvFatValue)
        progressProtein = findViewById(R.id.progressProtein)
        progressCarbs = findViewById(R.id.progressCarbs)
        progressFat = findViewById(R.id.progressFat)
        
        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        tvQuantity = findViewById(R.id.tvQuantity)
        spinnerUnit = findViewById(R.id.spinnerUnit)
        
        btnBreakfast = findViewById(R.id.btnBreakfast)
        btnLunch = findViewById(R.id.btnLunch)
        btnDinner = findViewById(R.id.btnDinner)
        btnSnacks = findViewById(R.id.btnSnacks)
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
        ivProfile.setOnClickListener {
            // Debug: Test Firebase connection
            FirebaseDebugHelper.checkFirebaseStatus(this)
        }
        
        ivClose.setOnClickListener {
            finish()
        }
        
        btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity -= 1
                updateQuantityDisplay()
                updateNutritionValues()
            }
        }
        
        btnPlus.setOnClickListener {
            quantity += 1
            updateQuantityDisplay()
            updateNutritionValues()
        }
        
        btnBreakfast.setOnClickListener { selectMealType(MealType.BREAKFAST) }
        btnLunch.setOnClickListener { selectMealType(MealType.LUNCH) }
        btnDinner.setOnClickListener { selectMealType(MealType.DINNER) }
        btnSnacks.setOnClickListener { selectMealType(MealType.SNACKS) }
        
        btnDone.setOnClickListener {
            addMealToTracker()
        }
        
        // Debug: Add long press to test meal entry
        btnDone.setOnLongClickListener {
            FirebaseDebugHelper.testMealEntryWrite(this)
            true
        }
        
        // Bottom navigation
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navHome).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navMeals).setOnClickListener {
            val intent = Intent(this, CalorieTrackerActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navRun).setOnClickListener {
            val intent = Intent(this, RunningTrackerFreeActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navTrain).setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navBMI).setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchFood(query)
                } else {
                    hideFoodDetails()
                }
            }
        })
    }

    private fun searchFood(query: String) {
        val foods = FoodDatabase.searchFoods(query)
        if (foods.isNotEmpty()) {
            // Show first matching food
            selectedFood = foods.first()
            showFoodDetails(selectedFood!!)
        } else {
            hideFoodDetails()
        }
    }

    private fun showFoodDetails(food: Food) {
        selectedFood = food
        
        tvFoodName.text = food.name
        updateNutritionValues()
        
        foodDetailsCard.visibility = View.VISIBLE
        mealTypeContainer.visibility = View.VISIBLE
        btnDone.visibility = View.VISIBLE
    }

    private fun hideFoodDetails() {
        foodDetailsCard.visibility = View.GONE
        mealTypeContainer.visibility = View.GONE
        btnDone.visibility = View.GONE
        selectedFood = null
    }

    private fun updateNutritionValues() {
        val food = selectedFood ?: return
        
        val multiplier = quantity / 100.0
        val calories = food.caloriesPer100g * multiplier
        val protein = food.proteinPer100g * multiplier
        val carbs = food.carbsPer100g * multiplier
        val fat = food.fatPer100g * multiplier
        
        tvCalories.text = "${calories.toInt()} kcal"
        tvProteinValue.text = "${String.format("%.1f", protein)}g"
        tvCarbsValue.text = "${String.format("%.1f", carbs)}g"
        tvFatValue.text = "${String.format("%.1f", fat)}g"
        
        // Update progress bars (relative to typical daily values)
        progressProtein.progress = ((protein / 50.0) * 100).toInt().coerceAtMost(100)
        progressCarbs.progress = ((carbs / 300.0) * 100).toInt().coerceAtMost(100)
        progressFat.progress = ((fat / 65.0) * 100).toInt().coerceAtMost(100)
    }

    private fun updateQuantityDisplay() {
        tvQuantity.text = quantity.toInt().toString()
    }

    private fun selectMealType(mealType: MealType) {
        selectedMealType = mealType
        updateMealTypeSelection()
    }

    private fun updateMealTypeSelection() {
        // Reset all buttons
        btnBreakfast.isSelected = false
        btnLunch.isSelected = false
        btnDinner.isSelected = false
        btnSnacks.isSelected = false
        
        // Select current meal type
        when (selectedMealType) {
            MealType.BREAKFAST -> btnBreakfast.isSelected = true
            MealType.LUNCH -> btnLunch.isSelected = true
            MealType.DINNER -> btnDinner.isSelected = true
            MealType.SNACKS -> btnSnacks.isSelected = true
        }
    }

    private fun checkAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("AddMeal", "User not authenticated")
            Toast.makeText(this, "Please sign in to add meals", Toast.LENGTH_LONG).show()
            // Redirect to sign in
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            Log.d("AddMeal", "User authenticated: ${currentUser.email} (${currentUser.uid})")
            // Test Firebase connection on startup
            FirebaseDebugHelper.checkFirebaseStatus(this)
        }
    }
    
    private fun addMealToTracker() {
        val food = selectedFood
        if (food == null) {
            Toast.makeText(this, "Please select a food item first", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Check authentication again before saving
        if (auth.currentUser == null) {
            Toast.makeText(this, "Authentication expired. Please sign in again.", Toast.LENGTH_LONG).show()
            checkAuthentication()
            return
        }
        
        // Disable the button to prevent multiple submissions
        btnDone.isEnabled = false
        btnDone.text = "Adding..."
        
        calorieTracker.addMealEntry(
            food = food,
            quantity = quantity,
            mealType = selectedMealType,
            onSuccess = {
                runOnUiThread {
                    Toast.makeText(this, "Meal added successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            },
            onError = { error ->
                runOnUiThread {
                    btnDone.isEnabled = true
                    btnDone.text = "Done"
                    
                    // Show more user-friendly error messages
                    val userFriendlyMessage = when {
                        error.contains("sign in", true) -> "Please sign in to add meals"
                        error.contains("permission", true) -> "Database access denied. Please contact support."
                        error.contains("network", true) -> "No internet connection. Please try again."
                        error.contains("auth", true) -> "Session expired. Please sign in again."
                        else -> "Failed to add meal. Please try again."
                    }
                    
                    Toast.makeText(this, userFriendlyMessage, Toast.LENGTH_LONG).show()
                    
                    // If authentication error, redirect to sign in
                    if (error.contains("sign in", true) || error.contains("auth", true)) {
                        checkAuthentication()
                    }
                }
            }
        )
    }

    private fun loadProfilePicture() {
        ProfilePictureLoader.loadProfilePicture(this, ivProfile)
    }
}
