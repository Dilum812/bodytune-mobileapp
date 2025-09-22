package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.utils.ModernNotification

class ProfileSetupActivity : AppCompatActivity() {

    // UI Elements
    private lateinit var llMale: LinearLayout
    private lateinit var llFemale: LinearLayout
    private lateinit var llOther: LinearLayout
    private lateinit var etHeight: EditText
    private lateinit var etWeight: EditText
    private lateinit var etAge: EditText
    private lateinit var btnContinue: Button
    private lateinit var tvSkip: TextView

    // Selected gender
    private var selectedGender: String = "Male" // Default to Male as shown in design

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        // Set up immersive full-screen experience
        setupFullScreen()

        // Initialize views
        initializeViews()

        // Set up click listeners
        setupClickListeners()
    }

    private fun setupFullScreen() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                )
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }

    private fun initializeViews() {
        llMale = findViewById(R.id.llMale)
        llFemale = findViewById(R.id.llFemale)
        llOther = findViewById(R.id.llOther)
        etHeight = findViewById(R.id.etHeight)
        etWeight = findViewById(R.id.etWeight)
        etAge = findViewById(R.id.etAge)
        btnContinue = findViewById(R.id.btnContinue)
        tvSkip = findViewById(R.id.tvSkip)

        // Set Male as selected by default (as shown in design)
        updateGenderSelection("Male")
    }

    private fun setupClickListeners() {
        llMale.setOnClickListener {
            updateGenderSelection("Male")
        }

        llFemale.setOnClickListener {
            updateGenderSelection("Female")
        }

        llOther.setOnClickListener {
            updateGenderSelection("Other")
        }

        btnContinue.setOnClickListener {
            handleContinue()
        }

        tvSkip.setOnClickListener {
            handleSkip()
        }
    }

    private fun updateGenderSelection(gender: String) {
        selectedGender = gender

        // Reset all to unselected state
        resetGenderButtons()

        // Set selected state for chosen gender
        when (gender) {
            "Male" -> {
                llMale.setBackgroundResource(R.drawable.gender_selected_background)
                updateGenderButtonText(llMale, "Male", true)
            }
            "Female" -> {
                llFemale.setBackgroundResource(R.drawable.gender_selected_background)
                updateGenderButtonText(llFemale, "Female", true)
            }
            "Other" -> {
                llOther.setBackgroundResource(R.drawable.gender_selected_background)
                updateGenderButtonText(llOther, "Other", true)
            }
        }
    }

    private fun resetGenderButtons() {
        llMale.setBackgroundResource(R.drawable.gender_unselected_background)
        llFemale.setBackgroundResource(R.drawable.gender_unselected_background)
        llOther.setBackgroundResource(R.drawable.gender_unselected_background)

        updateGenderButtonText(llMale, "Male", false)
        updateGenderButtonText(llFemale, "Female", false)
        updateGenderButtonText(llOther, "Other", false)
    }

    private fun updateGenderButtonText(layout: LinearLayout, text: String, isSelected: Boolean) {
        val textView = layout.getChildAt(1) as TextView
        val imageView = layout.getChildAt(0) as android.widget.ImageView

        if (isSelected) {
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white))
            textView.setTypeface(null, android.graphics.Typeface.BOLD)
            imageView.setColorFilter(ContextCompat.getColor(this, android.R.color.white))
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.calendar_text))
            textView.setTypeface(null, android.graphics.Typeface.NORMAL)
            imageView.setColorFilter(ContextCompat.getColor(this, R.color.calendar_text))
        }
    }

    private fun handleContinue() {
        // Validate input
        val height = etHeight.text.toString().trim()
        val weight = etWeight.text.toString().trim()
        val age = etAge.text.toString().trim()

        if (height.isEmpty() || weight.isEmpty() || age.isEmpty()) {
            ModernNotification.showError(this, "Please fill in all fields")
            return
        }

        // Validate ranges
        val heightValue = height.toDoubleOrNull()
        val weightValue = weight.toDoubleOrNull()
        val ageValue = age.toIntOrNull()

        if (heightValue == null || heightValue < 100 || heightValue > 250) {
            ModernNotification.showError(this, "Please enter a valid height (100-250 cm)")
            return
        }

        if (weightValue == null || weightValue < 30 || weightValue > 300) {
            ModernNotification.showError(this, "Please enter a valid weight (30-300 kg)")
            return
        }

        if (ageValue == null || ageValue < 13 || ageValue > 100) {
            ModernNotification.showError(this, "Please enter a valid age (13-100 years)")
            return
        }

        // Save user profile data
        saveUserProfile(selectedGender, heightValue, weightValue, ageValue)
    }

    private fun saveUserProfile(gender: String, height: Double, weight: Double, age: Int) {
        // Show loading state
        btnContinue.isEnabled = false
        btnContinue.text = "Saving..."

        // Log the data being saved for debugging
        android.util.Log.d("ProfileSetup", "Saving profile data: gender=$gender, height=$height, weight=$weight, age=$age")

        // Save to Firebase
        FirebaseHelper.updateUserProfile(
            gender = gender,
            height = height,
            weight = weight,
            age = age,
            onSuccess = {
                android.util.Log.d("ProfileSetup", "Profile saved successfully")
                ModernNotification.showSuccess(this, "Profile saved successfully!")
                
                // Navigate to MainActivity after a short delay to show the notification
                btnContinue.postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }, 1500)
            },
            onError = { error ->
                android.util.Log.e("ProfileSetup", "Error saving profile: $error")
                ModernNotification.showError(this, "Error saving profile: $error")
                btnContinue.isEnabled = true
                btnContinue.text = "Continue"
            }
        )
    }

    private fun handleSkip() {
        // Navigate to MainActivity without saving profile data
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
