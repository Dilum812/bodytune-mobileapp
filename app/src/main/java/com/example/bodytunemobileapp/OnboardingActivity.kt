package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bodytunemobileapp.adapter.OnboardingAdapter
import com.example.bodytunemobileapp.databinding.ActivityOnboardingBinding
import com.example.bodytunemobileapp.model.OnboardingItem
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingAdapter: OnboardingAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set up mobile screen handling
        setupMobileScreen()
        
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupOnboardingItems()
        setupViewPager()
        setupButtons()
    }
    
    private fun setupMobileScreen() {
        // Use modern WindowInsets API
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        // Hide system bars for immersive experience
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    
    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(
                R.drawable.onboarding_welcome,
                getString(R.string.welcome_to_bodytune),
                getString(R.string.onboarding_description)
            ),
            OnboardingItem(
                R.drawable.onboarding_workout,
                getString(R.string.personalized_workouts),
                getString(R.string.workout_description)
            ),
            OnboardingItem(
                R.drawable.onboarding_progress,
                getString(R.string.track_your_progress),
                getString(R.string.progress_description)
            ),
            OnboardingItem(
                R.drawable.onboarding_start,
                getString(R.string.ready_to_start),
                getString(R.string.start_description)
            )
        )
        
        onboardingAdapter = OnboardingAdapter(onboardingItems)
    }
    
    private fun setupViewPager() {
        binding.viewPager.adapter = onboardingAdapter
        
        // Connect ViewPager2 with TabLayout
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ ->
            // This lambda is called for each tab, but we don't need to do anything here
            // as we're using custom tab backgrounds
        }.attach()
        
        // Update button text based on current page
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position)
            }
        })
    }
    
    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            navigateToMainActivity()
        }
        
        binding.btnNext.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < onboardingAdapter.itemCount - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                navigateToMainActivity()
            }
        }
    }
    
    private fun updateButtonText(position: Int) {
        if (position == onboardingAdapter.itemCount - 1) {
            binding.btnNext.text = getString(R.string.get_started)
        } else {
            binding.btnNext.text = getString(R.string.next)
        }
    }
    
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-hide system bars when app gains focus
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
