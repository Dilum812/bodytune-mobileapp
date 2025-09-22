package com.example.bodytunemobileapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.bodytunemobileapp.models.RoutePoint
import com.example.bodytunemobileapp.models.RunningSession
import com.example.bodytunemobileapp.utils.ModernNotification
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.*

class QuickRunActivity : AppCompatActivity(), OnMapReadyCallback {

    // Map components
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var routePolyline: Polyline? = null
    
    // Location components
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    
    // UI Components
    private lateinit var ivBack: ImageView
    private lateinit var ivProfile: ImageView
    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvPace: TextView
    private lateinit var tvCalories: TextView
    private lateinit var btnPlayPause: ImageView
    private lateinit var btnStop: ImageView
    private lateinit var btnLap: ImageView
    
    // Running session data
    private val routePoints = mutableListOf<RoutePoint>()
    private var isRunning = false
    private var isPaused = false
    private var startTime = 0L
    private var pausedTime = 0L
    private var totalDistance = 0.0
    private var lastLocation: Location? = null
    
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
    
    // Permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                initializeLocationTracking()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                initializeLocationTracking()
            }
            else -> {
                ModernNotification.showError(this, "Location permission is required for running tracker")
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_run)
        
        initializeViews()
        setupFullScreen()
        setupClickListeners()
        loadProfilePicture()
        
        // Initialize map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        
        // Initialize UI with default values
        resetStats()
        
        // Check permissions
        checkLocationPermissions()
    }

    private fun initializeViews() {
        mapView = findViewById(R.id.mapView)
        ivBack = findViewById(R.id.ivBack)
        ivProfile = findViewById(R.id.ivProfile)
        tvTime = findViewById(R.id.tvTime)
        tvDistance = findViewById(R.id.tvDistance)
        tvPace = findViewById(R.id.tvPace)
        tvCalories = findViewById(R.id.tvCalories)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnStop = findViewById(R.id.btnStop)
        btnLap = findViewById(R.id.btnLap)
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

        btnPlayPause.setOnClickListener {
            toggleRunning()
        }

        btnStop.setOnClickListener {
            stopRunning()
        }

        btnLap.setOnClickListener {
            recordLap()
        }
    }

    private fun loadProfilePicture() {
        try {
            ProfilePictureLoader.loadProfilePicture(this, ivProfile)
        } catch (e: Exception) {
            e.printStackTrace()
            ivProfile.setImageResource(R.drawable.profile_placeholder)
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                initializeLocationTracking()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initializeLocationTracking() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    onLocationUpdate(location)
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        
        // Configure map settings
        googleMap?.apply {
            mapType = GoogleMap.MAP_TYPE_NORMAL
            uiSettings.isZoomControlsEnabled = false
            uiSettings.isCompassEnabled = false
            uiSettings.isMyLocationButtonEnabled = false
            
            // Enable location layer if permission granted
            if (ContextCompat.checkSelfPermission(this@QuickRunActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                isMyLocationEnabled = true
            }
        }
    }


    private fun onLocationUpdate(location: Location) {
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
            val segmentDistance = calculateDistance(previousPoint, currentPoint)
            totalDistance += segmentDistance
        }
        
        // Update map camera to follow user
        val currentLatLng = LatLng(location.latitude, location.longitude)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
        
        // Draw route on map
        drawRouteOnMap()
        
        lastLocation = location
    }


    private fun calculateDistance(point1: RoutePoint, point2: RoutePoint): Double {
        val earthRadius = 6371000.0 // Earth radius in meters
        
        val lat1Rad = Math.toRadians(point1.latitude)
        val lat2Rad = Math.toRadians(point2.latitude)
        val deltaLatRad = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLonRad = Math.toRadians(point2.longitude - point1.longitude)
        
        val a = sin(deltaLatRad / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c / 1000.0 // Return distance in kilometers
    }

    private fun drawRouteOnMap() {
        if (routePoints.size > 1 && googleMap != null) {
            // Remove existing polyline
            routePolyline?.remove()
            
            // Create new polyline from route points
            val polylineOptions = PolylineOptions()
                .width(8f)
                .color(android.graphics.Color.BLUE)
                .geodesic(true)
            
            // Add all route points to polyline
            routePoints.forEach { point ->
                polylineOptions.add(LatLng(point.latitude, point.longitude))
            }
            
            // Draw polyline on map
            routePolyline = googleMap?.addPolyline(polylineOptions)
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

    @SuppressLint("MissingPermission")
    private fun startRunning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ModernNotification.showError(this, "Location permission required")
            return
        }
        
        isRunning = true
        isPaused = false
        startTime = System.currentTimeMillis()
        
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        
        // Start location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        
        // Start stats updates
        updateHandler.post(updateRunnable)
        
        ModernNotification.showSuccess(this, "Quick Run started!")
    }

    private fun pauseRunning() {
        isPaused = true
        pausedTime = System.currentTimeMillis()
        
        btnPlayPause.setImageResource(R.drawable.ic_play)
        
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
        
        ModernNotification.showInfo(this, "Quick Run paused")
    }

    private fun resumeRunning() {
        isPaused = false
        
        // Adjust start time to account for pause duration
        val pauseDuration = System.currentTimeMillis() - pausedTime
        startTime += pauseDuration
        
        btnPlayPause.setImageResource(R.drawable.ic_pause)
        
        // Resume location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
        
        // Resume stats updates
        updateHandler.post(updateRunnable)
        
        ModernNotification.showSuccess(this, "Quick Run resumed")
    }

    private fun stopRunning() {
        isRunning = false
        isPaused = false
        
        btnPlayPause.setImageResource(R.drawable.ic_play)
        
        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
        
        // Stop stats updates
        updateHandler.removeCallbacks(updateRunnable)
        
        // Save session data
        saveRunningSession()
        
        ModernNotification.showSuccess(this, "Quick Run completed! Session saved.")
        
        // Reset for new session
        resetSession()
    }

    private fun recordLap() {
        if (isRunning && !isPaused) {
            ModernNotification.showInfo(this, "Lap recorded: ${String.format("%.2f", totalDistance)} km")
        }
    }

    private fun updateStats() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        
        // Update time
        tvTime.text = formatDuration(elapsedTime)
        
        // Update distance
        tvDistance.text = String.format("%.2f km", totalDistance)
        
        // Update pace (minutes per kilometer)
        val pace = if (totalDistance > 0) {
            val paceMinutes = (elapsedTime / 1000.0 / 60.0) / totalDistance
            String.format("%.0f'%02d\"", paceMinutes.toInt().toDouble(), ((paceMinutes % 1) * 60).toInt())
        } else {
            "0'00\""
        }
        tvPace.text = pace
        
        // Update calories (approximate: 60 calories per km)
        val calories = (totalDistance * 60).toInt()
        tvCalories.text = calories.toString()
    }

    private fun formatDuration(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = millis / (1000 * 60 * 60)
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun saveRunningSession() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            ModernNotification.showError(this, "Please sign in to save your session")
            return
        }
        
        val session = RunningSession(
            sessionId = System.currentTimeMillis().toString(),
            userId = user.uid,
            startTime = startTime,
            endTime = System.currentTimeMillis(),
            duration = System.currentTimeMillis() - startTime,
            distance = totalDistance,
            averagePace = if (totalDistance > 0) {
                ((System.currentTimeMillis() - startTime) / 1000.0 / 60.0) / totalDistance
            } else 0.0,
            calories = (totalDistance * 60).toInt(),
            route = routePoints
        )
        
        val database = FirebaseDatabase.getInstance()
        database.reference
            .child("running_sessions")
            .child(user.uid)
            .child(session.sessionId)
            .setValue(session)
            .addOnSuccessListener {
                ModernNotification.showSuccess(this, "Session saved successfully!")
            }
            .addOnFailureListener { exception ->
                ModernNotification.showError(this, "Failed to save session: ${exception.message}")
            }
    }

    private fun resetSession() {
        routePoints.clear()
        totalDistance = 0.0
        startTime = 0L
        pausedTime = 0L
        lastLocation = null
        
        // Clear route from map
        routePolyline?.remove()
        routePolyline = null
        
        // Reset UI
        resetStats()
    }

    private fun resetStats() {
        tvTime.text = "00:00"
        tvDistance.text = "0.00 km"
        tvPace.text = "0'00\""
        tvCalories.text = "0"
        btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun showStopRunningDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Stop Quick Run?")
            .setMessage("Are you sure you want to stop your current running session?")
            .setPositiveButton("Stop") { _, _ ->
                stopRunning()
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    // MapView lifecycle methods
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        updateHandler.removeCallbacks(updateRunnable)
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
