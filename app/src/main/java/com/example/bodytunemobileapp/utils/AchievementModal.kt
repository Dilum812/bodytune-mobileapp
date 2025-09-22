package com.example.bodytunemobileapp.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.bodytunemobileapp.R

class AchievementModal(private val activity: Activity) {
    
    private var modalView: View? = null
    private var isShowing = false
    
    data class AchievementData(
        val title: String,
        val description: String,
        val reward: String,
        val currentValue: Int,
        val targetValue: Int,
        val iconResource: Int,
        val iconColor: Int,
        val progressColor: Int
    )
    
    fun show(achievementData: AchievementData) {
        if (isShowing) return
        
        // Create modal view
        val inflater = LayoutInflater.from(activity)
        modalView = inflater.inflate(R.layout.modal_achievement_details, null)
        
        // Add to root view
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(modalView)
        
        // Setup modal content
        setupModalContent(achievementData)
        
        // Setup click listeners
        setupClickListeners()
        
        // Show with animation
        showWithAnimation()
        
        isShowing = true
    }
    
    private fun setupModalContent(data: AchievementData) {
        modalView?.let { modal ->
            // Set title
            modal.findViewById<TextView>(R.id.achievementTitle).text = data.title
            
            // Set description
            modal.findViewById<TextView>(R.id.achievementDescription).text = data.description
            
            // Set reward
            modal.findViewById<TextView>(R.id.achievementReward).text = data.reward
            
            // Set icon
            val iconView = modal.findViewById<ImageView>(R.id.achievementIcon)
            val iconContainer = modal.findViewById<CardView>(R.id.iconContainer)
            iconView.setImageResource(data.iconResource)
            iconView.setColorFilter(ContextCompat.getColor(activity, android.R.color.white))
            iconContainer.setCardBackgroundColor(data.iconColor)
            
            // Calculate progress
            val progressPercentage = if (data.targetValue > 0) {
                ((data.currentValue.toFloat() / data.targetValue.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else 0
            
            // Set progress bar
            val progressBar = modal.findViewById<ProgressBar>(R.id.achievementProgressBar)
            progressBar.progress = 0 // Start at 0 for animation
            progressBar.progressTintList = ContextCompat.getColorStateList(activity, data.progressColor)
            
            // Set progress text
            modal.findViewById<TextView>(R.id.progressText).text = "${data.currentValue} of ${data.targetValue} ${getProgressUnit(data.title)}"
            modal.findViewById<TextView>(R.id.progressPercentage).text = "$progressPercentage%"
            
            // Animate progress bar after modal is shown
            modal.postDelayed({
                animateProgressBar(progressBar, progressPercentage)
            }, 600)
            
            // Update button text based on completion
            val button = modal.findViewById<Button>(R.id.btnContinue)
            if (data.currentValue >= data.targetValue) {
                button.text = "🎉 Achievement Unlocked!"
                button.setBackgroundResource(R.drawable.rounded_background_blue)
            } else {
                button.text = "Keep Going!"
                button.setBackgroundResource(R.drawable.button_primary_background)
            }
        }
    }
    
    private fun getProgressUnit(title: String): String {
        return when {
            title.contains("Streak") -> "days"
            title.contains("Calorie") -> "days"
            title.contains("Workout") || title.contains("Fitness") -> "workouts"
            else -> "items"
        }
    }
    
    private fun setupClickListeners() {
        modalView?.let { modal ->
            // Close button
            modal.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
                dismiss()
            }
            
            // Continue button
            modal.findViewById<Button>(R.id.btnContinue).setOnClickListener {
                dismiss()
            }
            
            // Background click to dismiss
            modal.setOnClickListener {
                dismiss()
            }
            
            // Prevent modal container clicks from dismissing
            modal.findViewById<CardView>(R.id.modalContainer).setOnClickListener { 
                // Do nothing - prevent dismiss
            }
        }
    }
    
    private fun showWithAnimation() {
        modalView?.let { modal ->
            val container = modal.findViewById<CardView>(R.id.modalContainer)
            
            // Initial state
            modal.alpha = 0f
            container.scaleX = 0.7f
            container.scaleY = 0.7f
            container.translationY = 100f
            
            // Background fade in
            val backgroundFade = ObjectAnimator.ofFloat(modal, "alpha", 0f, 1f).apply {
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            // Container scale and slide animation
            val scaleX = ObjectAnimator.ofFloat(container, "scaleX", 0.7f, 1f)
            val scaleY = ObjectAnimator.ofFloat(container, "scaleY", 0.7f, 1f)
            val translateY = ObjectAnimator.ofFloat(container, "translationY", 100f, 0f)
            
            val containerAnimator = AnimatorSet().apply {
                playTogether(scaleX, scaleY, translateY)
                duration = 400
                interpolator = OvershootInterpolator(0.8f)
                startDelay = 100
            }
            
            // Icon bounce animation
            val iconContainer = modal.findViewById<CardView>(R.id.iconContainer)
            val iconBounce = ObjectAnimator.ofFloat(iconContainer, "scaleX", 1f, 1.2f, 1f).apply {
                duration = 600
                startDelay = 500
            }
            val iconBounceY = ObjectAnimator.ofFloat(iconContainer, "scaleY", 1f, 1.2f, 1f).apply {
                duration = 600
                startDelay = 500
            }
            
            // Play all animations
            val mainAnimator = AnimatorSet().apply {
                playTogether(backgroundFade, containerAnimator)
            }
            
            val iconAnimator = AnimatorSet().apply {
                playTogether(iconBounce, iconBounceY)
            }
            
            mainAnimator.start()
            iconAnimator.start()
        }
    }
    
    private fun animateProgressBar(progressBar: ProgressBar, targetProgress: Int) {
        val animator = ValueAnimator.ofInt(0, targetProgress).apply {
            duration = 1000
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { animation ->
                progressBar.progress = animation.animatedValue as Int
            }
        }
        animator.start()
    }
    
    fun dismiss() {
        if (!isShowing || modalView == null) return
        
        modalView?.let { modal ->
            val container = modal.findViewById<CardView>(R.id.modalContainer)
            
            // Background fade out
            val backgroundFade = ObjectAnimator.ofFloat(modal, "alpha", 1f, 0f).apply {
                duration = 250
                startDelay = 100
            }
            
            // Container scale and slide animation
            val scaleX = ObjectAnimator.ofFloat(container, "scaleX", 1f, 0.8f)
            val scaleY = ObjectAnimator.ofFloat(container, "scaleY", 1f, 0.8f)
            val translateY = ObjectAnimator.ofFloat(container, "translationY", 0f, 50f)
            
            val containerAnimator = AnimatorSet().apply {
                playTogether(scaleX, scaleY, translateY)
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
            }
            
            val dismissAnimator = AnimatorSet().apply {
                playTogether(backgroundFade, containerAnimator)
            }
            
            dismissAnimator.addListener(object : android.animation.Animator.AnimatorListener {
                override fun onAnimationStart(animation: android.animation.Animator) {}
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    // Remove from parent
                    val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
                    rootView.removeView(modal)
                    isShowing = false
                }
                override fun onAnimationCancel(animation: android.animation.Animator) {}
                override fun onAnimationRepeat(animation: android.animation.Animator) {}
            })
            
            dismissAnimator.start()
        }
    }
    
    fun isShowing(): Boolean = isShowing
}
