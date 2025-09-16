package com.example.bodytunemobileapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bodytunemobileapp.R
import com.example.bodytunemobileapp.data.Exercise

class ExerciseAdapter(
    private val onExerciseClick: (Exercise) -> Unit
) : ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(ExerciseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivExerciseImage: ImageView = itemView.findViewById(R.id.ivExerciseImage)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvExerciseName: TextView = itemView.findViewById(R.id.tvExerciseName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val btnPlay: TextView = itemView.findViewById(R.id.btnPlay)
        private val ivInfo: ImageView = itemView.findViewById(R.id.ivInfo)

        fun bind(exercise: Exercise) {
            tvDuration.text = "${exercise.duration} min"
            tvExerciseName.text = exercise.name
            tvDescription.text = exercise.description
            ivExerciseImage.setImageResource(exercise.imageResource)
            
            btnPlay.setOnClickListener {
                onExerciseClick(exercise)
            }
            
            ivInfo.setOnClickListener {
                onExerciseClick(exercise)
            }
            
            itemView.setOnClickListener {
                onExerciseClick(exercise)
            }
        }
    }

    private class ExerciseDiffCallback : DiffUtil.ItemCallback<Exercise>() {
        override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
            return oldItem == newItem
        }
    }
}
