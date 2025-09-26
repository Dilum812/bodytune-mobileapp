package com.example.bodytunemobileapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.bodytunemobileapp.utils.ProfileDiagnostics
import com.example.bodytunemobileapp.utils.ProfilePictureLoader
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

class ProfilePictureTestActivity : AppCompatActivity() {
    
    private lateinit var ivProfileTest: ImageView
    private lateinit var tvDebugInfo: TextView
    private lateinit var btnLoadProfile: Button
    private lateinit var btnTestUrl: Button
    private lateinit var btnRunDiagnostics: Button
    private lateinit var btnClearCache: Button
    
    companion object {
        private const val TAG = "ProfilePictureTest"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_picture_test)
        
        initializeViews()
        setupClickListeners()
        
        // Run initial diagnostics
        runDiagnostics()
    }
    
    private fun initializeViews() {
        ivProfileTest = findViewById(R.id.ivProfileTest)
        tvDebugInfo = findViewById(R.id.tvDebugInfo)
        btnLoadProfile = findViewById(R.id.btnLoadProfile)
        btnTestUrl = findViewById(R.id.btnTestUrl)
        btnRunDiagnostics = findViewById(R.id.btnRunDiagnostics)
        btnClearCache = findViewById(R.id.btnClearCache)
    }
    
    private fun setupClickListeners() {
        btnLoadProfile.setOnClickListener {
            Log.d(TAG, "=== MANUAL PROFILE LOAD TEST ===")
            ProfilePictureLoader.loadProfilePicture(this, ivProfileTest)
        }
        
        btnTestUrl.setOnClickListener {
            testWithKnownUrls()
        }
        
        btnRunDiagnostics.setOnClickListener {
            runDiagnostics()
        }
        
        btnClearCache.setOnClickListener {
            clearCacheAndReload()
        }
    }
    
    private fun runDiagnostics() {
        Log.d(TAG, "=== RUNNING COMPREHENSIVE DIAGNOSTICS ===")
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
        
        val debugText = StringBuilder()
        debugText.append("=== PROFILE PICTURE DIAGNOSTICS ===\n\n")
        
        // Firebase Auth Status
        debugText.append("FIREBASE AUTH:\n")
        if (currentUser != null) {
            debugText.append("✓ User signed in\n")
            debugText.append("Email: ${currentUser.email}\n")
            debugText.append("Display Name: ${currentUser.displayName}\n")
            debugText.append("Photo URL: ${currentUser.photoUrl}\n")
            debugText.append("Provider: ${currentUser.providerId}\n")
        } else {
            debugText.append("✗ No user signed in\n")
        }
        
        debugText.append("\nGOOGLE SIGN-IN:\n")
        if (googleAccount != null) {
            debugText.append("✓ Google account found\n")
            debugText.append("Email: ${googleAccount.email}\n")
            debugText.append("Display Name: ${googleAccount.displayName}\n")
            debugText.append("Photo URL: ${googleAccount.photoUrl}\n")
            debugText.append("ID: ${googleAccount.id}\n")
            
            // Test URL enhancement
            googleAccount.photoUrl?.let { uri ->
                val originalUrl = uri.toString()
                val enhancedUrl = enhanceUrl(originalUrl)
                debugText.append("\nURL ANALYSIS:\n")
                debugText.append("Original: $originalUrl\n")
                debugText.append("Enhanced: $enhancedUrl\n")
                debugText.append("Changed: ${originalUrl != enhancedUrl}\n")
            }
        } else {
            debugText.append("✗ No Google account found\n")
        }
        
        // Network permissions
        debugText.append("\nPERMISSIONS:\n")
        debugText.append("Internet: ${checkPermission(android.Manifest.permission.INTERNET)}\n")
        debugText.append("Network State: ${checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)}\n")
        
        tvDebugInfo.text = debugText.toString()
        
        // Also run the utility diagnostics
        ProfileDiagnostics.diagnoseProfilePictureIssues(this)
    }
    
    private fun enhanceUrl(originalUrl: String): String {
        return when {
            originalUrl.contains("s96-c") -> originalUrl.replace("s96-c", "s400-c")
            originalUrl.contains("=s96-c") -> originalUrl.replace("=s96-c", "=s400-c")
            originalUrl.contains("s64-c") -> originalUrl.replace("s64-c", "s400-c")
            originalUrl.contains("=s64-c") -> originalUrl.replace("=s64-c", "=s400-c")
            else -> originalUrl
        }
    }
    
    private fun checkPermission(permission: String): Boolean {
        return try {
            val pm = packageManager
            pm.checkPermission(permission, packageName) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }
    
    private fun testWithKnownUrls() {
        Log.d(TAG, "=== TESTING WITH KNOWN URLS ===")
        
        val testUrls = listOf(
            "https://lh3.googleusercontent.com/a/default-user=s400-c",
            "https://via.placeholder.com/400x400/4285F4/FFFFFF?text=TEST",
            "https://picsum.photos/400/400"
        )
        
        var currentIndex = 0
        
        fun loadNextUrl() {
            if (currentIndex < testUrls.size) {
                val url = testUrls[currentIndex]
                Log.d(TAG, "Testing URL ${currentIndex + 1}/${testUrls.size}: $url")
                
                Glide.with(this@ProfilePictureTestActivity)
                    .load(url)
                    .apply(RequestOptions()
                        .transform(CircleCrop())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                        .timeout(5000)
                    )
                    .into(ivProfileTest)
                
                currentIndex++
                
                // Load next URL after 3 seconds
                ivProfileTest.postDelayed({
                    loadNextUrl()
                }, 3000)
            } else {
                Log.d(TAG, "Finished testing all URLs")
                // Load actual profile picture after tests
                ivProfileTest.postDelayed({
                    ProfilePictureLoader.loadProfilePicture(this@ProfilePictureTestActivity, ivProfileTest)
                }, 2000)
            }
        }
        
        loadNextUrl()
    }
    
    private fun clearCacheAndReload() {
        Log.d(TAG, "=== CLEARING CACHE AND RELOADING ===")
        
        // Clear Glide memory cache
        Glide.get(this).clearMemory()
        
        // Clear disk cache in background
        Thread {
            Glide.get(this).clearDiskCache()
            
            // Reload on main thread
            runOnUiThread {
                ProfilePictureLoader.loadProfilePicture(this, ivProfileTest)
            }
        }.start()
    }
}
