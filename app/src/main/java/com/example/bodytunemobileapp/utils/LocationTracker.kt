package com.example.bodytunemobileapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.bodytunemobileapp.models.RoutePoint

class LocationTracker(private val context: Context) {
    
    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationListener: LocationListener? = null
    private var isTracking = false
    
    interface LocationUpdateListener {
        fun onLocationUpdate(location: Location)
        fun onLocationError(error: String)
    }
    
    fun startTracking(listener: LocationUpdateListener) {
        if (!hasLocationPermission()) {
            listener.onLocationError("Location permission not granted")
            return
        }
        
        if (isTracking) return
        
        isTracking = true
        
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                try {
                    listener.onLocationUpdate(location)
                } catch (e: Exception) {
                    listener.onLocationError("Error processing location update: ${e.message}")
                }
            }
            
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {
                listener.onLocationError("Location provider disabled")
            }
        }
        
        try {
            // Try GPS first, fallback to network
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L, // 5 seconds
                    5f, // 5 meters
                    locationListener!!
                )
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    5f,
                    locationListener!!
                )
            } else {
                listener.onLocationError("No location providers available")
            }
        } catch (e: SecurityException) {
            listener.onLocationError("Location permission denied: ${e.message}")
        } catch (e: Exception) {
            listener.onLocationError("Error starting location tracking: ${e.message}")
        }
    }
    
    fun stopTracking() {
        try {
            locationListener?.let {
                locationManager.removeUpdates(it)
            }
        } catch (e: Exception) {
            // Ignore errors when stopping tracking
        } finally {
            isTracking = false
            locationListener = null
        }
    }
    
    fun getCurrentLocation(callback: (Location?) -> Unit) {
        if (!hasLocationPermission()) {
            callback(null)
            return
        }
        
        try {
            // Try to get last known location from GPS first, then network
            var lastLocation: Location? = null
            
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
            
            if (lastLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            
            callback(lastLocation)
        } catch (e: SecurityException) {
            callback(null)
        }
    }
    
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    companion object {
        fun calculateDistance(point1: RoutePoint, point2: RoutePoint): Double {
            val results = FloatArray(1)
            Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
            )
            return results[0].toDouble() / 1000.0 // Convert to kilometers
        }
        
        fun calculatePace(distance: Double, timeInMillis: Long): String {
            return try {
                if (distance <= 0 || timeInMillis <= 0) return "0'00\""
                
                val timeInMinutes = timeInMillis / 60000.0
                val paceInMinutes = timeInMinutes / distance
                val minutes = paceInMinutes.toInt()
                val seconds = ((paceInMinutes - minutes) * 60).toInt()
                
                "${minutes}'${seconds.toString().padStart(2, '0')}\""
            } catch (e: Exception) {
                "0'00\""
            }
        }
        
        fun formatDuration(milliseconds: Long): String {
            val totalSeconds = milliseconds / 1000
            val minutes = (totalSeconds / 60).toInt()
            val seconds = (totalSeconds % 60).toInt()
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
        
        fun calculateCalories(distance: Double, weightKg: Double = 70.0): Int {
            // Approximate calories burned: 0.75 * weight * distance
            return (0.75 * weightKg * distance).toInt()
        }
    }
}
