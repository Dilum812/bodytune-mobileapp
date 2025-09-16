package com.example.bodytunemobileapp

import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class WorkoutTimerActivity : AppCompatActivity() {

    private lateinit var ivClose: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var ivExerciseImage: ImageView
    private lateinit var tvExerciseName: TextView
    private lateinit var tvTimeRemaining: TextView
    private lateinit var tvTimeLeft: TextView
    private lateinit var btnRestart: ImageView
    private lateinit var btnPause: ImageView
    private lateinit var btnStop: ImageView
    private lateinit var btnExit: Button
    
    private var countDownTimer: CountDownTimer? = null
    private var totalTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var isTimerRunning = false
    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_timer)

        // Get exercise data from intent
        val exerciseName = intent.getStringExtra("exercise_name") ?: "Exercise"
        val exerciseDuration = intent.getIntExtra("exercise_duration", 15)
        val exerciseImage = intent.getIntExtra("exercise_image", R.drawable.exercise_crunch)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up UI
        setupUI(exerciseName, exerciseDuration, exerciseImage)

        // Set up click listeners
        setupClickListeners()

        // Start timer
        startTimer()
    }

    private fun initializeViews() {
        ivClose = findViewById(R.id.ivClose)
        ivProfile = findViewById(R.id.ivProfile)
        ivExerciseImage = findViewById(R.id.ivExerciseImage)
        tvExerciseName = findViewById(R.id.tvExerciseName)
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining)
        tvTimeLeft = findViewById(R.id.tvTimeLeft)
        btnRestart = findViewById(R.id.btnRestart)
        btnPause = findViewById(R.id.btnPause)
        btnStop = findViewById(R.id.btnStop)
        btnExit = findViewById(R.id.btnExit)
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

    private fun setupUI(exerciseName: String, exerciseDuration: Int, exerciseImage: Int) {
        tvExerciseName.text = exerciseName
        ivExerciseImage.setImageResource(exerciseImage)
        
        // Convert minutes to milliseconds
        totalTimeInMillis = (exerciseDuration * 60 * 1000).toLong()
        timeLeftInMillis = totalTimeInMillis
        
        updateTimerDisplay()
    }

    private fun setupClickListeners() {
        ivClose.setOnClickListener {
            stopTimer()
            finish()
        }

        ivProfile.setOnClickListener {
            // TODO: Navigate to profile screen
        }

        btnRestart.setOnClickListener {
            restartTimer()
        }

        btnPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                resumeTimer()
            }
        }

        btnStop.setOnClickListener {
            stopTimer()
        }

        btnExit.setOnClickListener {
            stopTimer()
            finish()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerDisplay()
                updateProgress()
            }

            override fun onFinish() {
                timeLeftInMillis = 0
                updateTimerDisplay()
                updateProgress()
                isTimerRunning = false
                // TODO: Show workout completion dialog
            }
        }.start()
        
        isTimerRunning = true
        isPaused = false
        updatePauseButton()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isPaused = true
        updatePauseButton()
    }

    private fun resumeTimer() {
        startTimer()
    }

    private fun restartTimer() {
        countDownTimer?.cancel()
        timeLeftInMillis = totalTimeInMillis
        updateTimerDisplay()
        updateProgress()
        startTimer()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        isPaused = false
    }

    private fun updateTimerDisplay() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        
        tvTimeRemaining.text = String.format("%02d:%02d Min", minutes, seconds)
        
        val totalMinutes = (totalTimeInMillis / 1000) / 60
        val remainingMinutes = (timeLeftInMillis / 1000) / 60
        tvTimeLeft.text = "$remainingMinutes min left"
    }

    private fun updateProgress() {
        // Calculate progress percentage
        val progress = ((totalTimeInMillis - timeLeftInMillis).toFloat() / totalTimeInMillis.toFloat()) * 100
        // TODO: Update circular progress bar if implemented
    }

    private fun updatePauseButton() {
        if (isPaused || !isTimerRunning) {
            btnPause.setImageResource(R.drawable.ic_play)
        } else {
            btnPause.setImageResource(R.drawable.ic_pause)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
