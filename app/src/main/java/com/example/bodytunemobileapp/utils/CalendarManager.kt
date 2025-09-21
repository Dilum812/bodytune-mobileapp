package com.example.bodytunemobileapp.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for managing calendar operations in BodyTune app
 * Handles date calculations, formatting, and navigation logic
 */
class CalendarManager {
    
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        private val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        private val dayNameFormat = SimpleDateFormat("EEE", Locale.getDefault())
        
        /**
         * Get the current date
         */
        fun getCurrentDate(): Date {
            return Date()
        }
        
        /**
         * Get a formatted date string for database storage
         */
        fun formatDateForStorage(date: Date): String {
            return dateFormat.format(date)
        }
        
        /**
         * Parse date from storage format
         */
        fun parseDateFromStorage(dateString: String): Date? {
            return try {
                dateFormat.parse(dateString)
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Get the last 7 days including today
         * Returns list of dates from 6 days ago to today
         */
        fun getLast7Days(): List<Date> {
            val calendar = Calendar.getInstance()
            val dates = mutableListOf<Date>()
            
            // Start from 6 days ago
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            
            // Add 7 days (6 days ago + today)
            for (i in 0..6) {
                dates.add(calendar.time)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            return dates
        }
        
        /**
         * Get day number (01-31) for display
         */
        fun getDayNumber(date: Date): String {
            return dayFormat.format(date)
        }
        
        /**
         * Get day name (Mon, Tue, etc.) for display
         */
        fun getDayName(date: Date): String {
            return dayNameFormat.format(date)
        }
        
        /**
         * Check if a date is today
         */
        fun isToday(date: Date): Boolean {
            val today = Calendar.getInstance()
            val checkDate = Calendar.getInstance()
            checkDate.time = date
            
            return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
                   today.get(Calendar.DAY_OF_YEAR) == checkDate.get(Calendar.DAY_OF_YEAR)
        }
        
        /**
         * Check if a date is in the future (should be disabled)
         */
        fun isFutureDate(date: Date): Boolean {
            val today = Calendar.getInstance()
            val checkDate = Calendar.getInstance()
            checkDate.time = date
            
            return checkDate.after(today)
        }
        
        /**
         * Get days difference from today (negative for past, 0 for today, positive for future)
         */
        fun getDaysFromToday(date: Date): Int {
            val today = Calendar.getInstance()
            val checkDate = Calendar.getInstance()
            checkDate.time = date
            
            val todayStart = Calendar.getInstance()
            todayStart.set(Calendar.HOUR_OF_DAY, 0)
            todayStart.set(Calendar.MINUTE, 0)
            todayStart.set(Calendar.SECOND, 0)
            todayStart.set(Calendar.MILLISECOND, 0)
            
            val checkDateStart = Calendar.getInstance()
            checkDateStart.time = date
            checkDateStart.set(Calendar.HOUR_OF_DAY, 0)
            checkDateStart.set(Calendar.MINUTE, 0)
            checkDateStart.set(Calendar.SECOND, 0)
            checkDateStart.set(Calendar.MILLISECOND, 0)
            
            val diffInMillis = checkDateStart.timeInMillis - todayStart.timeInMillis
            return (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
        }
        
        /**
         * Get a user-friendly date description
         */
        fun getDateDescription(date: Date): String {
            return when {
                isToday(date) -> "Today"
                getDaysFromToday(date) == -1 -> "Yesterday"
                getDaysFromToday(date) < -1 -> "${-getDaysFromToday(date)} days ago"
                else -> "Future date"
            }
        }
    }
}
