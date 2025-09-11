package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.bodytunemobileapp.adapter.OnboardingAdapter
import com.example.bodytunemobileapp.databinding.ActivityOnboardingBinding
import com.example.bodytunemobileapp.model.OnboardingItem
import com.example.bodytunemobileapp.model.ScreenType

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
        updateIndicator(0)
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
            // Screen 1 - Splash
            OnboardingItem(
                image = R.drawable.ic_launcher_foreground,
                logo = null,
                title = getString(R.string.app_title),
                description = getString(R.string.app_tagline),
                screenType = ScreenType.SPLASH
            ),
            // Screen 2 - Welcome
            OnboardingItem(
                image = R.drawable.onboarding_man_water,
                logo = R.drawable.tb_logo,
                title = getString(R.string.welcome_title),
                description = getString(R.string.welcome_description),
                screenType = ScreenType.ONBOARDING
            ),
            // Screen 3 - Smarter Tracking
            OnboardingItem(
                image = R.drawable.onboarding_woman_earbuds,
                logo = R.drawable.tb_logo,
                title = getString(R.string.smarter_tracking_title),
                description = getString(R.string.smarter_tracking_description),
                screenType = ScreenType.ONBOARDING
            ),
            // Screen 4 - Stay Motivated
            OnboardingItem(
                image = R.drawable.onboard3,
                logo = R.drawable.tb_logo,
                title = getString(R.string.stay_motivated_title),
                description = getString(R.string.stay_motivated_description),
                screenType = ScreenType.ONBOARDING
            ),
            // Screen 4 - Sign In
            OnboardingItem(
                image = R.drawable.onboarding_welcome,
                logo = R.drawable.tb_logo,
                title = getString(R.string.sign_in_title),
                description = getString(R.string.sign_in_subtitle),
                screenType = ScreenType.SIGN_IN
            ),
            // Screen 5 - Sign Up
            OnboardingItem(
                image = R.drawable.onboarding_welcome,
                logo = R.drawable.tb_logo,
                title = getString(R.string.sign_up_title),
                description = getString(R.string.sign_up_subtitle),
                screenType = ScreenType.SIGN_UP
            ),
            // Screen 6 - Profile Setup
            OnboardingItem(
                image = R.drawable.onboarding_welcome,
                logo = R.drawable.tb_logo,
                title = getString(R.string.tell_us_about_yourself),
                description = getString(R.string.profile_subtitle),
                screenType = ScreenType.PROFILE_SETUP
            ),
            // Screen 7 - Ready to Start
            OnboardingItem(
                image = R.drawable.onboarding_ready,
                logo = R.drawable.tb_logo,
                title = getString(R.string.ready_title),
                description = getString(R.string.ready_description),
                screenType = ScreenType.ONBOARDING
            )
        )
        
        onboardingAdapter = OnboardingAdapter(onboardingItems, { onSignUpClick() }, { onSignInClick() })
    }
    
    private fun onSignUpClick() {
        // Navigate to sign up screen (index 4)
        binding.viewPager.currentItem = 4
    }

    private fun onSignInClick() {
        // Navigate to sign in screen (index 3)
        binding.viewPager.currentItem = 3
    }
    
    private fun setupViewPager() {
        binding.viewPager.adapter = onboardingAdapter
        
        // Update button text and custom indicator based on current page
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateButtonText(position)
                updateButtonVisibility(position)
                updateIndicator(position)
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
    
    private fun updateButtonVisibility(position: Int) {
        // Hide buttons on Sign In (index 3) and Sign Up (index 4) pages
        if (position == 3 || position == 4) {
            binding.btnSkip.visibility = android.view.View.GONE
            binding.btnNext.visibility = android.view.View.GONE
        } else {
            binding.btnSkip.visibility = android.view.View.VISIBLE
            binding.btnNext.visibility = android.view.View.VISIBLE
        }
    }

    private fun updateIndicator(position: Int) {
        // Show indicator only on the first 3 onboarding pages
        val showIndicator = position <= 2
        binding.indicatorLayout.visibility = if (showIndicator) android.view.View.VISIBLE else android.view.View.GONE
        if (!showIndicator) return

        val segs = listOf(binding.seg1, binding.seg2, binding.seg3)
        segs.forEachIndexed { index, view ->
            if (index == position) {
                view.setBackgroundResource(R.drawable.progress_active)
                view.layoutParams = (view.layoutParams as ViewGroup.LayoutParams).apply {
                    width = resources.getDimensionPixelSize(R.dimen.progress_active_width)
                    height = resources.getDimensionPixelSize(R.dimen.progress_height)
                }
            } else {
                view.setBackgroundResource(R.drawable.progress_inactive)
                view.layoutParams = (view.layoutParams as ViewGroup.LayoutParams).apply {
                    width = resources.getDimensionPixelSize(R.dimen.progress_inactive_width)
                    height = resources.getDimensionPixelSize(R.dimen.progress_height)
                }
            }
            view.requestLayout()
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
