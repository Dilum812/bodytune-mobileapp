package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.example.bodytunemobileapp.firebase.FirebaseHelper
import com.example.bodytunemobileapp.models.User
import com.example.bodytunemobileapp.auth.GoogleSignInHelper

class SignUpActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignUp: Button
    private lateinit var btnGoogleSignUp: Button
    private lateinit var tvSignInLink: TextView
    
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Initialize Google Sign-In
        googleSignInHelper = GoogleSignInHelper(this)

        // Initialize views
        initializeViews()

        // Set up immersive full-screen experience
        setupFullScreen()

        // Set up click listeners
        setupClickListeners()
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

    private fun initializeViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnGoogleSignUp = findViewById(R.id.btnGoogleSignUp)
        tvSignInLink = findViewById(R.id.tvSignInLink)
    }

    private fun setupClickListeners() {
        // Sign Up button click
        btnSignUp.setOnClickListener {
            handleSignUp()
        }

        // Google Sign Up button click
        btnGoogleSignUp.setOnClickListener {
            handleGoogleSignUp()
        }

        // Sign In link click
        tvSignInLink.setOnClickListener {
            navigateToSignIn()
        }
    }

    private fun handleSignUp() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        // Basic validation
        when {
            email.isEmpty() -> {
                showToast("Please enter your email")
                return
            }
            !isValidEmail(email) -> {
                showToast("Please enter a valid email address")
                return
            }
            password.isEmpty() -> {
                showToast("Please create a password")
                return
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters")
                return
            }
            confirmPassword.isEmpty() -> {
                showToast("Please confirm your password")
                return
            }
            password != confirmPassword -> {
                showToast("Passwords do not match")
                return
            }
        }

        // Show loading state
        setLoadingState(true)

        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, create user profile
                    createUserProfile(email)
                } else {
                    setLoadingState(false)
                    showToast("Registration failed: ${task.exception?.message}")
                }
            }
    }

    private fun handleGoogleSignUp() {
        setLoadingState(true)
        val signInIntent = googleSignInHelper.getSignInIntent()
        startActivityForResult(signInIntent, GoogleSignInHelper.RC_SIGN_IN)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == GoogleSignInHelper.RC_SIGN_IN) {
            googleSignInHelper.handleSignInResult(data) { success, error, account ->
                setLoadingState(false)
                if (success && account != null) {
                    // Create user profile with Google account info
                    createGoogleUserProfile(account.email ?: "", account.displayName ?: "")
                } else {
                    showToast("Google Sign-Up failed: $error")
                }
            }
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        // After successful sign-up, always go to ProfileSetupActivity
        val intent = Intent(this, ProfileSetupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun createUserProfile(email: String) {
        val user = User(
            uid = auth.currentUser?.uid ?: "",
            email = email,
            name = "", // Will be filled in profile setup
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        FirebaseHelper.saveUserProfile(user) { success, error ->
            setLoadingState(false)
            if (success) {
                showToast("Account created successfully!")
                navigateToMainActivity()
            } else {
                showToast("Failed to create profile: $error")
            }
        }
    }

    private fun createGoogleUserProfile(email: String, displayName: String) {
        val user = User(
            uid = auth.currentUser?.uid ?: "",
            email = email,
            name = displayName,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        FirebaseHelper.saveUserProfile(user) { success, error ->
            if (success) {
                showToast("Google account linked successfully!")
                navigateToMainActivity()
            } else {
                showToast("Failed to create profile: $error")
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSignUp.isEnabled = !isLoading
        btnGoogleSignUp.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            setupFullScreen()
        }
    }
}
