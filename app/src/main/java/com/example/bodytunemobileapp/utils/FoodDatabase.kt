package com.example.bodytunemobileapp.utils

import com.example.bodytunemobileapp.models.Food
import com.example.bodytunemobileapp.R

object FoodDatabase {

    private val foods = mutableListOf<Food>()

    init {
        initializeFoodDatabase()
    }

    private fun initializeFoodDatabase() {
        foods.addAll(
            listOf(
                // Grains & Cereals
                Food(
                    "1",
                    "Rice (White, Cooked)",
                    130.0,
                    2.7,
                    28.0,
                    0.3,
                    "Grains",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "2",
                    "Rice (Brown, Cooked)",
                    112.0,
                    2.6,
                    22.0,
                    0.9,
                    "Grains",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "3",
                    "Bread (White)",
                    265.0,
                    9.0,
                    49.0,
                    3.2,
                    "Grains",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "4",
                    "Bread (Whole Wheat)",
                    247.0,
                    13.0,
                    41.0,
                    4.2,
                    "Grains",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "5",
                    "Pasta (Cooked)",
                    131.0,
                    5.0,
                    25.0,
                    1.1,
                    "Grains",
                    R.drawable.ic_food_generic
                ),
                Food("6", "Oats", 389.0, 16.9, 66.3, 6.9, "Grains", R.drawable.ic_food_generic),
                Food(
                    "7",
                    "Quinoa (Cooked)",
                    120.0,
                    4.4,
                    22.0,
                    1.9,
                    "Grains",
                    R.drawable.ic_food_generic
                ),

                // Proteins
                Food(
                    "8",
                    "Chicken Breast (Grilled)",
                    165.0,
                    31.0,
                    0.0,
                    3.6,
                    "Protein",
                    R.drawable.ic_food_chicken
                ),
                Food(
                    "9",
                    "Chicken Thigh",
                    209.0,
                    26.0,
                    0.0,
                    11.0,
                    "Protein",
                    R.drawable.ic_food_chicken
                ),
                Food(
                    "10",
                    "Beef (Lean)",
                    250.0,
                    26.0,
                    0.0,
                    15.0,
                    "Protein",
                    R.drawable.ic_food_chicken
                ),
                Food("11", "Salmon", 208.0, 20.0, 0.0, 13.0, "Protein", R.drawable.ic_food_chicken),
                Food("12", "Tuna", 144.0, 30.0, 0.0, 1.0, "Protein", R.drawable.ic_food_chicken),
                Food("13", "Eggs", 155.0, 13.0, 1.1, 11.0, "Protein", R.drawable.ic_food_chicken),
                Food("14", "Tofu", 76.0, 8.0, 1.9, 4.8, "Protein", R.drawable.ic_food_chicken),
                Food(
                    "15",
                    "Lentils (Cooked)",
                    116.0,
                    9.0,
                    20.0,
                    0.4,
                    "Protein",
                    R.drawable.ic_food_chicken
                ),

                // Vegetables
                Food(
                    "16",
                    "Broccoli",
                    34.0,
                    2.8,
                    7.0,
                    0.4,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "17",
                    "Spinach",
                    23.0,
                    2.9,
                    3.6,
                    0.4,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "18",
                    "Carrot",
                    41.0,
                    0.9,
                    10.0,
                    0.2,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "19",
                    "Tomato",
                    18.0,
                    0.9,
                    3.9,
                    0.2,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "20",
                    "Bell Pepper",
                    31.0,
                    1.0,
                    7.0,
                    0.3,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "21",
                    "Cucumber",
                    16.0,
                    0.7,
                    4.0,
                    0.1,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "22",
                    "Onion",
                    40.0,
                    1.1,
                    9.3,
                    0.1,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),

                // Fruits
                Food("23", "Apple", 52.0, 0.3, 14.0, 0.2, "Fruits", R.drawable.ic_food_fruit),
                Food("24", "Banana", 89.0, 1.1, 23.0, 0.3, "Fruits", R.drawable.ic_food_fruit),
                Food("25", "Orange", 47.0, 0.9, 12.0, 0.1, "Fruits", R.drawable.ic_food_fruit),
                Food("26", "Grapes", 62.0, 0.6, 16.0, 0.2, "Fruits", R.drawable.ic_food_fruit),
                Food("27", "Strawberry", 32.0, 0.7, 8.0, 0.3, "Fruits", R.drawable.ic_food_fruit),
                Food("28", "Mango", 60.0, 0.8, 15.0, 0.4, "Fruits", R.drawable.ic_food_fruit),

                // Dairy
                Food(
                    "29",
                    "Milk (Whole)",
                    61.0,
                    3.2,
                    4.8,
                    3.3,
                    "Dairy",
                    R.drawable.ic_food_generic
                ),
                Food("30", "Milk (Skim)", 34.0, 3.4, 5.0, 0.1, "Dairy", R.drawable.ic_food_generic),
                Food(
                    "31",
                    "Yogurt (Plain)",
                    59.0,
                    10.0,
                    3.6,
                    0.4,
                    "Dairy",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "32",
                    "Cheese (Cheddar)",
                    402.0,
                    25.0,
                    1.3,
                    33.0,
                    "Dairy",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "33",
                    "Cottage Cheese",
                    98.0,
                    11.0,
                    3.4,
                    4.3,
                    "Dairy",
                    R.drawable.ic_food_generic
                ),

                // Nuts & Seeds
                Food("34", "Almonds", 579.0, 21.0, 22.0, 50.0, "Nuts", R.drawable.ic_food_generic),
                Food("35", "Walnuts", 654.0, 15.0, 14.0, 65.0, "Nuts", R.drawable.ic_food_generic),
                Food("36", "Peanuts", 567.0, 26.0, 16.0, 49.0, "Nuts", R.drawable.ic_food_generic),
                Food(
                    "37",
                    "Sunflower Seeds",
                    584.0,
                    21.0,
                    20.0,
                    51.0,
                    "Nuts",
                    R.drawable.ic_food_generic
                ),

                // Oils & Fats
                Food("38", "Olive Oil", 884.0, 0.0, 0.0, 100.0, "Fats", R.drawable.ic_food_generic),
                Food("39", "Butter", 717.0, 0.9, 0.1, 81.0, "Fats", R.drawable.ic_food_generic),
                Food("40", "Avocado", 160.0, 2.0, 9.0, 15.0, "Fats", R.drawable.ic_food_fruit),

                // Snacks & Others
                Food(
                    "41",
                    "Potato (Baked)",
                    93.0,
                    2.1,
                    21.0,
                    0.1,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "42",
                    "Sweet Potato",
                    86.0,
                    1.6,
                    20.0,
                    0.1,
                    "Vegetables",
                    R.drawable.ic_food_vegetable
                ),
                Food(
                    "43",
                    "Dark Chocolate (70%)",
                    546.0,
                    7.8,
                    46.0,
                    31.0,
                    "Snacks",
                    R.drawable.ic_food_generic
                ),
                Food("44", "Honey", 304.0, 0.3, 82.0, 0.0, "Snacks", R.drawable.ic_food_generic),
                Food(
                    "45",
                    "Granola",
                    471.0,
                    13.0,
                    61.0,
                    20.0,
                    "Snacks",
                    R.drawable.ic_food_generic
                ),

                // Sri Lankan Meals
                // Rice and Curry
                Food(
                    "46",
                    "White Rice with Chicken Curry",
                    320.0,
                    15.0,
                    45.0,
                    8.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "47",
                    "Red Rice with Fish Curry",
                    280.0,
                    18.0,
                    40.0,
                    5.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "48",
                    "Yellow Rice with Chicken",
                    350.0,
                    16.0,
                    50.0,
                    9.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),

                // String Hoppers & Hoppers
                Food(
                    "49",
                    "String Hoppers (10pcs)",
                    180.0,
                    3.5,
                    40.0,
                    0.5,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "50",
                    "Plain Hoppers (2pcs)",
                    200.0,
                    4.0,
                    42.0,
                    1.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "51",
                    "Egg Hoppers (2pcs)",
                    320.0,
                    14.0,
                    35.0,
                    14.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),

                // Kottu
                Food(
                    "52",
                    "Chicken Kottu Roti",
                    650.0,
                    25.0,
                    75.0,
                    25.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "53",
                    "Cheese Kottu Roti",
                    720.0,
                    20.0,
                    80.0,
                    35.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),

                // Short Eats
                Food(
                    "54",
                    "Chicken Roti (1pc)",
                    280.0,
                    12.0,
                    30.0,
                    12.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "55",
                    "Vegetable Roti (1pc)",
                    220.0,
                    5.0,
                    35.0,
                    6.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),

                // Traditional Breakfast
                Food(
                    "56",
                    "Kiri Bath (1 cup)",
                    350.0,
                    5.0,
                    45.0,
                    17.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "57",
                    "Pittu (2 pieces)",
                    220.0,
                    5.0,
                    45.0,
                    2.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),

                // More Sri Lankan Dishes
                Food(
                    "58",
                    "Lamprais (1 packet)",
                    580.0,
                    22.0,
                    65.0,
                    25.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "59",
                    "Kottu with Egg & Cheese",
                    720.0,
                    24.0,
                    85.0,
                    30.0,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                ),
                Food(
                    "60",
                    "Indi Appa (2 pieces)",
                    180.0,
                    4.0,
                    38.0,
                    1.5,
                    "Sri Lankan",
                    R.drawable.ic_food_generic
                )
            )
        )
    }
    
    fun getAllFoods(): List<Food> = foods.toList()
    
    fun searchFoods(query: String): List<Food> {
        if (query.isBlank()) return foods.toList()
        return foods.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.category.contains(query, ignoreCase = true) 
        }
    }
    
    fun getFoodById(id: String): Food? {
        return foods.find { it.id == id }
    }
    
    fun getFoodsByCategory(category: String): List<Food> {
        return foods.filter { it.category.equals(category, ignoreCase = true) }
    }
    
    fun getCategories(): List<String> {
        return foods.map { it.category }.distinct().sorted()
    }
    
    fun addFood(food: Food) {
        foods.add(food)
    }
}
