package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        
        adapter = OnboardingAdapter(
            onNextClick = { position ->
                if (position < 2) {
                    viewPager.currentItem = position + 1
                } else {
                    // Last screen - navigate to main app
                    navigateToMainApp()
                }
            },
            onSkipClick = {
                navigateToMainApp()
            }
        )

        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = true // Allow swiping
    }

    private fun navigateToMainApp() {
        // Navigate to Sign In screen after onboarding
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}
