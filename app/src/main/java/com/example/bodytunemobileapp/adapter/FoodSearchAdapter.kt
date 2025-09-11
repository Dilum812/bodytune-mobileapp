package com.example.bodytunemobileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bodytunemobileapp.databinding.ItemFoodSearchBinding
import com.example.bodytunemobileapp.model.FoodItem

class FoodSearchAdapter(
    private val onFoodClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodSearchAdapter.FoodViewHolder>() {

    private var foods = listOf<FoodItem>()

    fun updateFoods(newFoods: List<FoodItem>) {
        foods = newFoods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foods[position])
    }

    override fun getItemCount(): Int = foods.size

    inner class FoodViewHolder(
        private val binding: ItemFoodSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: FoodItem) {
            binding.tvFoodName.text = foodItem.name
            binding.tvCalories.text = "${foodItem.caloriesPer100g} kcal per 100g"
            binding.ivFoodImage.setImageResource(foodItem.imageRes)
            
            binding.root.setOnClickListener {
                onFoodClick(foodItem)
            }
        }
    }
}
