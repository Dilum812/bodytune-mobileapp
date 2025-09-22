package com.example.bodytunemobileapp.utils

import android.animation.*
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

object SmartAnimations {
    
    // Standard animation durations
    const val DURATION_SHORT = 200L
    const val DURATION_MEDIUM = 300L
    const val DURATION_LONG = 500L
    const val DURATION_EXTRA_LONG = 800L
    
    // Standard interpolators
    val SMOOTH_INTERPOLATOR = FastOutSlowInInterpolator()
    val BOUNCE_INTERPOLATOR = OvershootInterpolator(0.8f)
    val ELASTIC_INTERPOLATOR = OvershootInterpolator(1.2f)
    
    /**
     * Animate view entrance with scale and fade
     */
    fun animateViewEntrance(view: View, delay: Long = 0L, duration: Long = DURATION_MEDIUM) {
        view.alpha = 0f
        view.scaleX = 0.8f
        view.scaleY = 0.8f
        view.translationY = 50f
        
        view.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .translationY(0f)
            .setDuration(duration)
            .setStartDelay(delay)
            .setInterpolator(BOUNCE_INTERPOLATOR)
            .start()
    }
    
    /**
     * Animate view exit with scale and fade
     */
    fun animateViewExit(view: View, onComplete: (() -> Unit)? = null, duration: Long = DURATION_SHORT) {
        view.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .translationY(-30f)
            .setDuration(duration)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .withEndAction { onComplete?.invoke() }
            .start()
    }
    
    /**
     * Animate card press with scale feedback
     */
    fun animateCardPress(card: CardView, onComplete: (() -> Unit)? = null) {
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(card, "scaleX", 1f, 0.95f),
                ObjectAnimator.ofFloat(card, "scaleY", 1f, 0.95f)
            )
            duration = 100
            interpolator = SMOOTH_INTERPOLATOR
        }
        
        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(card, "scaleX", 0.95f, 1f),
                ObjectAnimator.ofFloat(card, "scaleY", 0.95f, 1f)
            )
            duration = 150
            interpolator = BOUNCE_INTERPOLATOR
        }
        
        scaleDown.doOnEnd {
            scaleUp.start()
            onComplete?.invoke()
        }
        
        scaleDown.start()
    }
    
    /**
     * Animate button press with ripple effect
     */
    fun animateButtonPress(button: View, onComplete: (() -> Unit)? = null) {
        button.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(80)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .setInterpolator(BOUNCE_INTERPOLATOR)
                    .withEndAction { onComplete?.invoke() }
                    .start()
            }
            .start()
    }
    
    /**
     * Animate progress bar with smooth fill
     */
    fun animateProgressBar(progressBar: ProgressBar, targetProgress: Int, duration: Long = DURATION_LONG) {
        val animator = ValueAnimator.ofInt(0, targetProgress).apply {
            this.duration = duration
            interpolator = SMOOTH_INTERPOLATOR
            addUpdateListener { animation ->
                progressBar.progress = animation.animatedValue as Int
            }
        }
        animator.start()
    }
    
    /**
     * Animate text change with fade transition
     */
    fun animateTextChange(textView: TextView, newText: String, duration: Long = DURATION_SHORT) {
        textView.animate()
            .alpha(0f)
            .setDuration(duration / 2)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .withEndAction {
                textView.text = newText
                textView.animate()
                    .alpha(1f)
                    .setDuration(duration / 2)
                    .setInterpolator(SMOOTH_INTERPOLATOR)
                    .start()
            }
            .start()
    }
    
    /**
     * Animate icon change with rotation
     */
    fun animateIconChange(imageView: ImageView, newIconRes: Int, duration: Long = DURATION_MEDIUM) {
        imageView.animate()
            .rotationY(90f)
            .setDuration(duration / 2)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .withEndAction {
                imageView.setImageResource(newIconRes)
                imageView.animate()
                    .rotationY(0f)
                    .setDuration(duration / 2)
                    .setInterpolator(SMOOTH_INTERPOLATOR)
                    .start()
            }
            .start()
    }
    
    /**
     * Animate view group children with staggered entrance
     */
    fun animateChildrenEntrance(viewGroup: ViewGroup, staggerDelay: Long = 100L) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            animateViewEntrance(child, delay = i * staggerDelay)
        }
    }
    
    /**
     * Animate floating action with pulse effect
     */
    fun animateFloatingPulse(view: View, repeat: Boolean = true) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.8f, 1f)
        
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 1500
            interpolator = SMOOTH_INTERPOLATOR
            if (repeat) {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        animation.start()
                    }
                })
            }
        }
        
        animatorSet.start()
    }
    
    /**
     * Animate slide transition between views
     */
    fun animateSlideTransition(
        outView: View,
        inView: View,
        direction: SlideDirection = SlideDirection.LEFT_TO_RIGHT,
        duration: Long = DURATION_MEDIUM,
        onComplete: (() -> Unit)? = null
    ) {
        val screenWidth = outView.context.resources.displayMetrics.widthPixels.toFloat()
        
        val outTranslation = when (direction) {
            SlideDirection.LEFT_TO_RIGHT -> -screenWidth
            SlideDirection.RIGHT_TO_LEFT -> screenWidth
        }
        
        val inTranslation = when (direction) {
            SlideDirection.LEFT_TO_RIGHT -> screenWidth
            SlideDirection.RIGHT_TO_LEFT -> -screenWidth
        }
        
        // Setup incoming view
        inView.translationX = inTranslation
        inView.alpha = 0f
        inView.visibility = View.VISIBLE
        
        // Animate out
        outView.animate()
            .translationX(outTranslation)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .start()
        
        // Animate in
        inView.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(SMOOTH_INTERPOLATOR)
            .withEndAction {
                outView.visibility = View.GONE
                outView.translationX = 0f
                outView.alpha = 1f
                onComplete?.invoke()
            }
            .start()
    }
    
    /**
     * Animate number counter with smooth increment
     */
    fun animateNumberCounter(
        textView: TextView,
        startValue: Int,
        endValue: Int,
        duration: Long = DURATION_LONG,
        prefix: String = "",
        suffix: String = ""
    ) {
        val animator = ValueAnimator.ofInt(startValue, endValue).apply {
            this.duration = duration
            interpolator = SMOOTH_INTERPOLATOR
            addUpdateListener { animation ->
                val value = animation.animatedValue as Int
                textView.text = "$prefix$value$suffix"
            }
        }
        animator.start()
    }
    
    /**
     * Animate view shake for error feedback
     */
    fun animateShake(view: View, intensity: Float = 10f) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, intensity, -intensity, intensity, -intensity, 0f)
        shake.duration = 500
        shake.interpolator = AccelerateDecelerateInterpolator()
        shake.start()
    }
    
    /**
     * Animate view bounce for success feedback
     */
    fun animateBounce(view: View, scale: Float = 1.2f) {
        val bounceX = ObjectAnimator.ofFloat(view, "scaleX", 1f, scale, 1f)
        val bounceY = ObjectAnimator.ofFloat(view, "scaleY", 1f, scale, 1f)
        
        val animatorSet = AnimatorSet().apply {
            playTogether(bounceX, bounceY)
            duration = 400
            interpolator = BOUNCE_INTERPOLATOR
        }
        
        animatorSet.start()
    }
    
    /**
     * Create reveal animation for circular reveal effect
     */
    fun createCircularReveal(view: View, centerX: Int, centerY: Int, startRadius: Float, endRadius: Float): Animator? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius).apply {
                duration = DURATION_MEDIUM
                interpolator = SMOOTH_INTERPOLATOR
            }
        } else {
            // Fallback for older versions
            null
        }
    }
    
    enum class SlideDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }
}
