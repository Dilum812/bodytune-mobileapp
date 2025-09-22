package com.example.bodytunemobileapp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.bodytunemobileapp.R

class ModernNotification {
    
    enum class NotificationType {
        SUCCESS, ERROR, INFO
    }
    
    companion object {
        private var currentNotification: View? = null
        
        fun show(
            activity: Activity,
            title: String,
            message: String,
            type: NotificationType = NotificationType.SUCCESS,
            duration: Long = 3000L
        ) {
            // Remove any existing notification
            dismiss()
            
            // Get the root view
            val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            
            // Inflate the notification layout
            val notificationView = LayoutInflater.from(activity)
                .inflate(R.layout.custom_notification, rootView, false)
            
            // Configure the notification
            setupNotification(notificationView, title, message, type)
            
            // Add to root view
            rootView.addView(notificationView)
            currentNotification = notificationView
            
            // Set initial position (off-screen at top)
            notificationView.translationY = -200f
            notificationView.alpha = 0f
            
            // Animate in
            animateIn(notificationView)
            
            // Set up close button
            val closeButton = notificationView.findViewById<ImageView>(R.id.ivCloseNotification)
            closeButton.setOnClickListener {
                dismiss()
            }
            
            // Auto dismiss after duration
            notificationView.postDelayed({
                dismiss()
            }, duration)
        }
        
        private fun setupNotification(
            view: View,
            title: String,
            message: String,
            type: NotificationType
        ) {
            val card = view.findViewById<CardView>(R.id.notificationCard)
            val icon = view.findViewById<ImageView>(R.id.ivNotificationIcon)
            val titleView = view.findViewById<TextView>(R.id.tvNotificationTitle)
            val messageView = view.findViewById<TextView>(R.id.tvNotificationMessage)
            
            // Set text
            titleView.text = title
            messageView.text = message
            
            // Configure based on type
            when (type) {
                NotificationType.SUCCESS -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.notification_success))
                    icon.setImageResource(R.drawable.ic_check_circle)
                    icon.setColorFilter(ContextCompat.getColor(view.context, android.R.color.white))
                }
                NotificationType.ERROR -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.notification_error))
                    icon.setImageResource(R.drawable.ic_error_circle)
                    icon.setColorFilter(ContextCompat.getColor(view.context, android.R.color.white))
                }
                NotificationType.INFO -> {
                    card.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.notification_info))
                    icon.setImageResource(R.drawable.ic_info_circle)
                    icon.setColorFilter(ContextCompat.getColor(view.context, android.R.color.white))
                }
            }
        }
        
        private fun animateIn(view: View) {
            val translateY = ObjectAnimator.ofFloat(view, "translationY", -200f, 0f)
            val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            
            translateY.duration = 300
            alpha.duration = 300
            
            translateY.start()
            alpha.start()
        }
        
        private fun animateOut(view: View, onComplete: () -> Unit) {
            val translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, -200f)
            val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            
            translateY.duration = 250
            alpha.duration = 250
            
            alpha.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    onComplete()
                }
            })
            
            translateY.start()
            alpha.start()
        }
        
        fun dismiss() {
            currentNotification?.let { notification ->
                animateOut(notification) {
                    val parent = notification.parent as? ViewGroup
                    parent?.removeView(notification)
                    currentNotification = null
                }
            }
        }
        
        // Convenience methods
        fun showSuccess(activity: Activity, message: String, duration: Long = 3000L) {
            show(activity, "Success", message, NotificationType.SUCCESS, duration)
        }
        
        fun showError(activity: Activity, message: String, duration: Long = 4000L) {
            show(activity, "Error", message, NotificationType.ERROR, duration)
        }
        
        fun showInfo(activity: Activity, message: String, duration: Long = 3000L) {
            show(activity, "Info", message, NotificationType.INFO, duration)
        }
    }
}
