package com.example.bodytunemobileapp.models

data class RunningSession(
    val sessionId: String = "",
    val userId: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val duration: Long = 0L,
    val distance: Double = 0.0,
    val averagePace: String = "",
    val calories: Int = 0,
    val route: List<RoutePoint> = emptyList(),
    val stats: RunningStats = RunningStats()
)

data class RoutePoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L,
    val elevation: Double = 0.0
)

data class RunningStats(
    val maxSpeed: Double = 0.0,
    val averageSpeed: Double = 0.0,
    val elevationGain: Double = 0.0,
    val heartRate: List<Int> = emptyList()
)
