package com.example.bodytunemobileapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class CircularProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var progress = 0f
    private var strokeWidth = 16f
    
    private val backgroundPaint = Paint().apply {
        color = Color.parseColor("#333333")
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }
    
    private val progressPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.blue_primary)
        style = Paint.Style.STROKE
        strokeWidth = this@CircularProgressView.strokeWidth
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }
    
    private val rect = RectF()

    fun setProgress(progress: Float) {
        this.progress = progress.coerceIn(0f, 100f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (minOf(width, height) / 2f) - strokeWidth / 2f
        
        rect.set(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )
        
        // Draw background circle
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Draw progress arc
        val sweepAngle = (progress / 100f) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, progressPaint)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = minOf(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(size, size)
    }
}
