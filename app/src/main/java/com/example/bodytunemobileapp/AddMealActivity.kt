package com.example.bodytunemobileapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bodytunemobileapp.adapter.FoodSearchAdapter
import com.example.bodytunemobileapp.databinding.ActivityAddMealBinding
import com.example.bodytunemobileapp.model.FoodItem

class AddMealActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddMealBinding
    private lateinit var foodAdapter: FoodSearchAdapter
    private var selectedFood: FoodItem? = null
    private var selectedMealType = "Breakfast"
    private var quantity = 100 // grams
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMealBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        selectedMealType = intent.getStringExtra("meal_type") ?: "Breakfast"
        
        setupUI()
        setupRecyclerView()
        setupClickListeners()
        setupSearch()
        updateMealTypeSelection()
    }
    
    private fun setupUI() {
        // Hide system bars for immersive experience
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        binding.etQuantity.setText(quantity.toString())
    }
    
    private fun setupRecyclerView() {
        foodAdapter = FoodSearchAdapter { foodItem ->
            selectedFood = foodItem
            showFoodDetails(foodItem)
        }
        binding.rvFoodSearch.layoutManager = LinearLayoutManager(this)
        binding.rvFoodSearch.adapter = foodAdapter
    }
    
    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }
        
        binding.btnDone.setOnClickListener {
            // Save meal and finish
            saveMeal()
            finish()
        }
        
        binding.btnDecrease.setOnClickListener {
            if (quantity > 10) {
                quantity -= 10
                updateQuantity()
            }
        }
        
        binding.btnIncrease.setOnClickListener {
            quantity += 10
            updateQuantity()
        }
        
        // Meal type buttons
        binding.btnBreakfast.setOnClickListener {
            selectedMealType = "Breakfast"
            updateMealTypeSelection()
        }
        
        binding.btnLunch.setOnClickListener {
            selectedMealType = "Lunch"
            updateMealTypeSelection()
        }
        
        binding.btnDinner.setOnClickListener {
            selectedMealType = "Dinner"
            updateMealTypeSelection()
        }
        
        binding.btnSnacks.setOnClickListener {
            selectedMealType = "Snacks"
            updateMealTypeSelection()
        }
    }
    
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.length >= 2) {
                    searchFood(query)
                } else {
                    foodAdapter.updateFoods(emptyList())
                    binding.foodDetailsContainer.visibility = View.GONE
                }
            }
            
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun searchFood(query: String) {
        // Sample food database - in real app this would be from API or local database
        val foodDatabase = getFoodDatabase()
        val results = foodDatabase.filter { 
            it.name.contains(query, ignoreCase = true) 
        }.take(10)
        
        foodAdapter.updateFoods(results)
    }
    
    private fun showFoodDetails(foodItem: FoodItem) {
        binding.foodDetailsContainer.visibility = View.VISIBLE
        binding.ivFoodImage.setImageResource(foodItem.imageRes)
        binding.tvFoodName.text = foodItem.name
        updateNutritionInfo(foodItem)
    }
    
    private fun updateQuantity() {
        binding.etQuantity.setText(quantity.toString())
        selectedFood?.let { updateNutritionInfo(it) }
    }
    
    private fun updateNutritionInfo(foodItem: FoodItem) {
        val multiplier = quantity / 100f
        val calories = (foodItem.caloriesPer100g * multiplier).toInt()
        val protein = (foodItem.proteinPer100g * multiplier)
        val carbs = (foodItem.carbsPer100g * multiplier)
        val fat = (foodItem.fatPer100g * multiplier)
        
        binding.tvCalories.text = "$calories kcal"
        binding.tvProtein.text = "${String.format("%.1f", protein)}g"
        binding.tvCarbs.text = "${String.format("%.1f", carbs)}g"
        binding.tvFat.text = "${String.format("%.1f", fat)}g"
        
        // Update progress bars (assuming max values for visualization)
        binding.progressProtein.progress = ((protein / 50) * 100).toInt().coerceAtMost(100)
        binding.progressCarbs.progress = ((carbs / 50) * 100).toInt().coerceAtMost(100)
        binding.progressFat.progress = ((fat / 30) * 100).toInt().coerceAtMost(100)
    }
    
    private fun updateMealTypeSelection() {
        // Reset all buttons
        binding.btnBreakfast.setBackgroundColor(getColor(android.R.color.transparent))
        binding.btnLunch.setBackgroundColor(getColor(android.R.color.transparent))
        binding.btnDinner.setBackgroundColor(getColor(android.R.color.transparent))
        binding.btnSnacks.setBackgroundColor(getColor(android.R.color.transparent))
        
        // Highlight selected button
        when (selectedMealType) {
            "Breakfast" -> binding.btnBreakfast.setBackgroundColor(getColor(R.color.blue_primary))
            "Lunch" -> binding.btnLunch.setBackgroundColor(getColor(R.color.blue_primary))
            "Dinner" -> binding.btnDinner.setBackgroundColor(getColor(R.color.blue_primary))
            "Snacks" -> binding.btnSnacks.setBackgroundColor(getColor(R.color.blue_primary))
        }
    }
    
    private fun saveMeal() {
        // In real app, save to database
        selectedFood?.let { food ->
            val multiplier = quantity / 100f
            val calories = (food.caloriesPer100g * multiplier).toInt()
            // Save meal with calories, meal type, etc.
        }
    }
    
    private fun getFoodDatabase(): List<FoodItem> {
        return listOf(
            FoodItem(
                name = "Grilled Chicken Breast",
                caloriesPer100g = 165,
                proteinPer100g = 31.0f,
                carbsPer100g = 0f,
                fatPer100g = 3.6f,
                imageRes = R.drawable.food_chicken_breast
            ),
            FoodItem(
                name = "Brown Rice",
                caloriesPer100g = 111,
                proteinPer100g = 2.6f,
                carbsPer100g = 23f,
                fatPer100g = 0.9f,
                imageRes = R.drawable.food_brown_rice
            ),
            FoodItem(
                name = "Salmon Fillet",
                caloriesPer100g = 208,
                proteinPer100g = 25.4f,
                carbsPer100g = 0f,
                fatPer100g = 12.4f,
                imageRes = R.drawable.food_salmon
            ),
            FoodItem(
                name = "Broccoli",
                caloriesPer100g = 34,
                proteinPer100g = 2.8f,
                carbsPer100g = 7f,
                fatPer100g = 0.4f,
                imageRes = R.drawable.food_broccoli
            ),
            FoodItem(
                name = "Banana",
                caloriesPer100g = 89,
                proteinPer100g = 1.1f,
                carbsPer100g = 23f,
                fatPer100g = 0.3f,
                imageRes = R.drawable.food_banana
            ),
            FoodItem(
                name = "Greek Yogurt",
                caloriesPer100g = 59,
                proteinPer100g = 10f,
                carbsPer100g = 3.6f,
                fatPer100g = 0.4f,
                imageRes = R.drawable.food_yogurt
            ),
            FoodItem(
                name = "Almonds",
                caloriesPer100g = 579,
                proteinPer100g = 21.2f,
                carbsPer100g = 21.6f,
                fatPer100g = 49.9f,
                imageRes = R.drawable.food_almonds
            ),
            FoodItem(
                name = "Sweet Potato",
                caloriesPer100g = 86,
                proteinPer100g = 1.6f,
                carbsPer100g = 20.1f,
                fatPer100g = 0.1f,
                imageRes = R.drawable.food_sweet_potato
            )
        )
    }
}
