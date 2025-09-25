package com.example.bodytunemobileapp.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.example.bodytunemobileapp.R
import kotlin.math.*

class BMIMeterView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var currentBMI = 22.0f
    private var animatedBMI = 22.0f
    
    // BMI ranges and colors
    private val bmiRanges = listOf(
        BMIRange("UNDERWEIGHT", 0f, 18.5f, ContextCompat.getColor(context, R.color.blue), "< 18.5"),
        BMIRange("NORMAL", 18.5f, 25f, ContextCompat.getColor(context, R.color.healthy_green), "18.5 - 24.9"),
        BMIRange("OVERWEIGHT", 25f, 30f, ContextCompat.getColor(context, R.color.warning_orange), "25.0 - 29.9"),
        BMIRange("OBESE", 30f, 40f, ContextCompat.getColor(context, R.color.danger_red), "30.0 - 39.9"),
        BMIRange("SEVERELY OBESE", 40f, 50f, ContextCompat.getColor(context, R.color.danger_red), "> 40.0")
    )
    
    private val minBMI = 15f
    private val maxBMI = 40f
    private val startAngle = 180f // Start from left
    private val sweepAngle = 180f // Half circle
    
    data class BMIRange(
        val label: String,
        val minValue: Float,
        val maxValue: Float,
        val color: Int,
        val displayRange: String
    )
    
    init {
        textPaint.apply {
            textAlign = Paint.Align.CENTER
            color = Color.WHITE
        }
        
        needlePaint.apply {
            color = Color.BLACK
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h * 0.75f // Position center for better layout fit
        radius = min(w.toFloat(), h * 1.2f) * 0.32f // Adjust radius for better proportions
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        drawBMIArc(canvas)
        drawLabels(canvas)
        drawNeedle(canvas)
        drawCenterCircle(canvas)
        drawBMIValue(canvas)
    }
    
    private fun drawBMIArc(canvas: Canvas) {
        val strokeWidth = radius * 0.15f
        paint.strokeWidth = strokeWidth
        paint.style = Paint.Style.STROKE
        
        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        
        var currentAngle = startAngle
        
        bmiRanges.forEach { range ->
            val rangeAngle = ((range.maxValue - range.minValue) / (maxBMI - minBMI)) * sweepAngle
            
            paint.color = range.color
            canvas.drawArc(rect, currentAngle, rangeAngle, false, paint)
            
            currentAngle += rangeAngle
        }
    }
    
    private fun drawLabels(canvas: Canvas) {
        textPaint.textSize = radius * 0.08f
        textPaint.color = Color.WHITE
        
        bmiRanges.forEach { range ->
            val midValue = (range.minValue + range.maxValue) / 2f
            val angle = startAngle + ((midValue - minBMI) / (maxBMI - minBMI)) * sweepAngle
            val labelRadius = radius * 1.25f
            
            val x = centerX + cos(Math.toRadians(angle.toDouble())).toFloat() * labelRadius
            val y = centerY + sin(Math.toRadians(angle.toDouble())).toFloat() * labelRadius
            
            // Draw label background
            val labelWidth = textPaint.measureText(range.label)
            val labelHeight = textPaint.textSize
            
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#CC000000") // Semi-transparent black
            canvas.drawRoundRect(
                x - labelWidth/2 - 8f,
                y - labelHeight/2 - 4f,
                x + labelWidth/2 + 8f,
                y + labelHeight/2 + 4f,
                8f, 8f, paint
            )
            
            // Draw label text
            canvas.drawText(range.label, x, y + textPaint.textSize/3, textPaint)
            
            // Draw range values
            textPaint.textSize = radius * 0.06f
            canvas.drawText(range.displayRange, x, y + textPaint.textSize * 1.5f, textPaint)
            textPaint.textSize = radius * 0.08f
        }
    }
    
    private fun drawNeedle(canvas: Canvas) {
        // Clamp BMI to visible range for needle positioning
        val clampedBMI = animatedBMI.coerceIn(minBMI, maxBMI)
        val bmiAngle = startAngle + ((clampedBMI - minBMI) / (maxBMI - minBMI)) * sweepAngle
        val needleLength = radius * 0.75f
        
        val needleEndX = centerX + cos(Math.toRadians(bmiAngle.toDouble())).toFloat() * needleLength
        val needleEndY = centerY + sin(Math.toRadians(bmiAngle.toDouble())).toFloat() * needleLength
        
        // Draw needle shadow
        needlePaint.color = Color.parseColor("#44000000")
        needlePaint.strokeWidth = 10f
        canvas.drawLine(centerX + 1f, centerY + 1f, needleEndX + 1f, needleEndY + 1f, needlePaint)
        
        // Draw needle
        needlePaint.color = Color.BLACK
        needlePaint.strokeWidth = 6f
        canvas.drawLine(centerX, centerY, needleEndX, needleEndY, needlePaint)
        
        // Draw needle tip
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK
        canvas.drawCircle(needleEndX, needleEndY, 4f, paint)
    }
    
    private fun drawCenterCircle(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        
        // Draw shadow circle
        paint.color = Color.parseColor("#33000000")
        canvas.drawCircle(centerX + 3f, centerY + 3f, radius * 0.08f, paint)
        
        // Draw center circle
        paint.color = Color.BLACK
        canvas.drawCircle(centerX, centerY, radius * 0.08f, paint)
        
        // Draw inner circle
        paint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, radius * 0.04f, paint)
    }
    
    private fun drawBMIValue(canvas: Canvas) {
        // Draw BMI value in the center bottom
        textPaint.textSize = radius * 0.25f
        textPaint.color = Color.WHITE
        textPaint.typeface = Typeface.DEFAULT_BOLD
        
        val bmiText = String.format("%.1f", animatedBMI)
        canvas.drawText(bmiText, centerX, centerY + radius * 0.4f, textPaint)
        
        // Draw "BMI" label
        textPaint.textSize = radius * 0.12f
        textPaint.typeface = Typeface.DEFAULT
        textPaint.color = Color.parseColor("#888888")
        canvas.drawText("BMI", centerX, centerY + radius * 0.6f, textPaint)
    }
    
    fun setBMI(bmi: Float, animate: Boolean = true) {
        val clampedBMI = bmi.coerceIn(minBMI, maxBMI)
        
        if (animate) {
            val animator = ValueAnimator.ofFloat(animatedBMI, clampedBMI).apply {
                duration = 1500
                interpolator = FastOutSlowInInterpolator()
                addUpdateListener { animation ->
                    animatedBMI = animation.animatedValue as Float
                    invalidate()
                }
            }
            animator.start()
        } else {
            animatedBMI = clampedBMI
            invalidate()
        }
        
        currentBMI = clampedBMI
    }
    
    fun getCurrentBMI(): Float = currentBMI
    
    fun getBMICategory(): String {
        return bmiRanges.find { 
            currentBMI >= it.minValue && currentBMI < it.maxValue 
        }?.label ?: "NORMAL"
    }
}
