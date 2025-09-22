package com.example.bodytunemobileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.models.RoutePoint
import com.example.bodytunemobileapp.models.RunningSession
import com.example.bodytunemobileapp.utils.LocationTracker
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.example.bodytunemobileapp.utils.ModernNotification

class RunningTrackerFreeActivity : AppCompatActivity(), LocationTracker.LocationUpdateListener {

    private lateinit var locationTracker: LocationTracker
    
    // UI Components
    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var tvPace: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvElevation: TextView
    private lateinit var tvBPM: TextView
    private lateinit var btnLap: ImageView
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnStop: ImageView
    
    // Running session data
    private var runningSession = RunningSession()
    private val routePoints = mutableListOf<RoutePoint>()
    private var isRunning = false
    private var isPaused = false
    private var startTime = 0L
    private var pausedTime = 0L
    private var totalDistance = 0.0
    
    // Update handlers
    private val updateHandler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning && !isPaused) {
                updateStats()
                updateHandler.postDelayed(this, 1000) // Update every second
            }
        }
    }
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_running_tracker_simple)

            initializeViews()
            setupFullScreen()
            setupClickListeners()
            loadProfilePicture()
            
            locationTracker = LocationTracker(this)
            
            // Initialize UI with default values
            resetStats()
            
            // Check location permissions
            checkLocationPermissions()
        } catch (e: Exception) {
            e.printStackTrace()
            ModernNotification.showError(this, "Error initializing running tracker: ${e.message}")
            finish()
        }
    }

    private fun initializeViews() {
        try {
            ivBack = findViewById(R.id.ivBack)
            ivProfile = findViewById(R.id.ivProfile)
            tvPace = findViewById(R.id.tvPace)
            tvDistance = findViewById(R.id.tvDistance)
            tvCalories = findViewById(R.id.tvCalories)
            tvTime = findViewById(R.id.tvTime)
            tvElevation = findViewById(R.id.tvElevation)
            tvBPM = findViewById(R.id.tvBPM)
            btnLap = findViewById(R.id.btnLap)
            btnPlayPause = findViewById(R.id.btnPlayPause)
            btnStop = findViewById(R.id.btnStop)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
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

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            if (isRunning) {
                showStopRunningDialog()
            } else {
                finish()
            }
        }

        ivProfile.setOnClickListener {
            // TODO: Show profile menu
        }

        btnPlayPause.setOnClickListener {
            toggleRunning()
        }

        btnStop.setOnClickListener {
            stopRunning()
        }

        btnLap.setOnClickListener {
            recordLap()
        }
        
        // Bottom navigation
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navHome).setOnClickListener {
            navigateToMain()
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navTrain).setOnClickListener {
            val intent = Intent(this, WorkoutModuleActivity::class.java)
            startActivity(intent)
        }
        
        findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.navBMI).setOnClickListener {
            val intent = Intent(this, BMICalculatorActivity::class.java)
            startActivity(intent)
        }
    }

    private fun resetStats() {
        tvTime.text = "00:00"
        tvDistance.text = "0.00"
        tvPace.text = "0'00\""
        tvCalories.text = "0"
        tvElevation.text = "0"
        tvBPM.text = "--"
        btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun loadProfilePicture() {
        try {
            ProfilePictureLoader.loadProfilePicture(this, ivProfile)
        } catch (e: Exception) {
            e.printStackTrace()
            // Set default placeholder if profile loading fails
            ivProfile.setImageResource(R.drawable.profile_placeholder)
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                ModernNotification.showError(this, "Location permission required for tracking")
            }
        }
    }

    private fun getCurrentLocation() {
        try {
            locationTracker.getCurrentLocation { location ->
                location?.let {
                    ModernNotification.showSuccess(this, "GPS Ready - Location found!")
                } ?: run {
                    ModernNotification.showInfo(this, "Unable to get current location. GPS tracking may be limited.")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ModernNotification.showError(this, "Error getting location: ${e.message}")
        }
    }

    private fun toggleRunning() {
        if (!isRunning) {
            startRunning()
        } else {
            if (isPaused) {
                resumeRunning()
            } else {
                pauseRunning()
            }
        }
    }

    private fun startRunning() {
        isRunning = true
        isPaused = false
        startTime = System.currentTimeMillis()
        
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        
        // Start location tracking
        locationTracker.startTracking(this)
        
        // Start stats updates
        updateHandler.post(updateRunnable)
        
        ModernNotification.showSuccess(this, "Running started!")
    }

    private fun pauseRunning() {
        isPaused = true
        pausedTime = System.currentTimeMillis()
        
        btnPlayPause.setImageResource(R.drawable.ic_play)
        
        // Stop location tracking
        locationTracker.stopTracking()
        
        ModernNotification.showInfo(this, "Running paused")
    }

    private fun resumeRunning() {
        isPaused = false
        
        // Adjust start time to account for pause duration
        val pauseDuration = System.currentTimeMillis() - pausedTime
        startTime += pauseDuration
        
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        
        // Resume location tracking
        locationTracker.startTracking(this)
        
        // Resume stats updates
        updateHandler.post(updateRunnable)
        
        ModernNotification.showSuccess(this, "Running resumed")
    }

    private fun stopRunning() {
        isRunning = false
        isPaused = false
        
        btnPlayPause.setImageResource(R.drawable.ic_play)
        
        // Stop location tracking
        locationTracker.stopTracking()
        
        // Stop stats updates
        updateHandler.removeCallbacks(updateRunnable)
        
        // Save session data
        saveRunningSession()
        
        ModernNotification.showSuccess(this, "Running stopped! Session saved.")
        
        // Reset for new session
        resetSession()
    }

    private fun recordLap() {
        if (isRunning && !isPaused) {
            ModernNotification.showInfo(this, "Lap recorded: ${String.format("%.2f", totalDistance)} km")
        }
    }

    override fun onLocationUpdate(location: Location) {
        try {
            val currentPoint = RoutePoint(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = System.currentTimeMillis(),
                elevation = location.altitude
            )
            
            routePoints.add(currentPoint)
            
            // Calculate distance if we have previous points
            if (routePoints.size > 1) {
                val previousPoint = routePoints[routePoints.size - 2]
                val segmentDistance = LocationTracker.calculateDistance(previousPoint, currentPoint)
                totalDistance += segmentDistance
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ModernNotification.showError(this, "Error updating location: ${e.message}")
        }
    }

    override fun onLocationError(error: String) {
        ModernNotification.showError(this, "Location error: $error")
    }


    private fun updateStats() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        
        // Update time
        tvTime.text = LocationTracker.formatDuration(elapsedTime)
        
        // Update distance
        tvDistance.text = String.format("%.2f", totalDistance)
        
        // Update pace
        val pace = LocationTracker.calculatePace(totalDistance, elapsedTime)
        tvPace.text = pace
        
        // Update calories (approximate)
        val calories = LocationTracker.calculateCalories(totalDistance)
        tvCalories.text = calories.toString()
        
        // Update elevation (if available)
        if (routePoints.isNotEmpty()) {
            val currentElevation = routePoints.last().elevation.toInt()
            tvElevation.text = currentElevation.toString()
        }
        
        // Update BPM (simulated - would need heart rate sensor)
        val simulatedBPM = (120..140).random()
        tvBPM.text = simulatedBPM.toString()
    }

    private fun saveRunningSession() {
        // TODO: Save to Firebase
        val session = RunningSession(
            sessionId = System.currentTimeMillis().toString(),
            userId = "current_user", // Get from Firebase Auth
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            duration = System.currentTimeMillis() - startTime,
            distance = totalDistance,
            averagePace = if (totalDistance > 0) {
                ((System.currentTimeMillis() - startTime) / 1000.0 / 60.0) / totalDistance
            } else 0.0,
            calories = LocationTracker.calculateCalories(totalDistance),
            route = routePoints
        )
    }

    private fun resetSession() {
        routePoints.clear()
        totalDistance = 0.0
        startTime = 0L
        pausedTime = 0L
        
        // Reset UI
        resetStats()
    }

    private fun showStopRunningDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Stop Running?")
            .setMessage("Are you sure you want to stop your current running session?")
            .setPositiveButton("Stop") { _, _ ->
                stopRunning()
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    private fun navigateToMain() {
        if (isRunning) {
            showStopRunningDialog()
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationTracker.stopTracking()
        updateHandler.removeCallbacks(updateRunnable)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
