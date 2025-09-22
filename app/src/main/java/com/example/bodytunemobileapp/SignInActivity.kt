package com.example.bodytunemobileapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.example.bodytunemobileapp.auth.GoogleSignInHelper

class SignInActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var tvSignUpLink: TextView
    private lateinit var progressBar: ProgressBar
    
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInHelper: GoogleSignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

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
        
        // Check if user is already signed in
        checkCurrentUser()
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
        btnSignIn = findViewById(R.id.btnSignIn)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvSignUpLink = findViewById(R.id.tvSignUpLink)
        // Note: Add progressBar to your layout if needed
        // progressBar = findViewById(R.id.progressBar)
    }

    private fun setupClickListeners() {
        // Sign In button click
        btnSignIn.setOnClickListener {
            handleSignIn()
        }

        // Google Sign In button click
        btnGoogleSignIn.setOnClickListener {
            handleGoogleSignIn()
        }

        // Sign Up link click
        tvSignUpLink.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun handleSignIn() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

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
                showToast("Please enter your password")
                return
            }
        }

        // Show loading state
        setLoadingState(true)

        // Sign in with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoadingState(false)
                if (task.isSuccessful) {
                    showToast("Sign in successful!")
                    navigateToMainActivity()
                } else {
                    showToast("Authentication failed: ${task.exception?.message}")
                }
            }
    }

    private fun handleGoogleSignIn() {
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
                    showToast("Google Sign-In successful!")
                    navigateToMainActivity()
                } else {
                    showToast("Google Sign-In failed: $error")
                }
            }
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToMainActivity() {
        // Check if user has completed profile setup
        checkProfileSetupStatus()
    }
    
    private fun checkProfileSetupStatus() {
        com.example.bodytunemobileapp.firebase.FirebaseHelper.getUserProfile { user, error ->
            if (user != null && user.height > 0 && user.weight > 0 && user.age > 0) {
                // Profile is complete, go to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Profile is incomplete or error occurred, go to ProfileSetupActivity
                val intent = Intent(this, ProfileSetupActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to main activity
            navigateToMainActivity()
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSignIn.isEnabled = !isLoading
        btnGoogleSignIn.isEnabled = !isLoading
        // Uncomment if you add progressBar to layout
        // progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
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
