package com.example.bodytunemobileapp.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth

class ProfileDiagnostics {
    
    companion object {
        private const val TAG = "ProfileDiagnostics"
        
        fun diagnoseProfilePictureIssues(context: Context) {
            Log.d(TAG, "=== Profile Picture Diagnostics ===")
            
            val currentUser = FirebaseAuth.getInstance().currentUser
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            
            // Check Firebase Auth
            Log.d(TAG, "Firebase Auth Status:")
            if (currentUser != null) {
                Log.d(TAG, "  ✓ User is signed in")
                Log.d(TAG, "  Email: ${currentUser.email}")
                Log.d(TAG, "  Display Name: ${currentUser.displayName}")
                Log.d(TAG, "  Photo URL: ${currentUser.photoUrl}")
                Log.d(TAG, "  Provider: ${currentUser.providerId}")
                Log.d(TAG, "  Providers: ${currentUser.providerData.map { it.providerId }}")
            } else {
                Log.w(TAG, "  ✗ No Firebase user signed in")
            }
            
            // Check Google Sign-In
            Log.d(TAG, "Google Sign-In Status:")
            if (googleAccount != null) {
                Log.d(TAG, "  ✓ Google account found")
                Log.d(TAG, "  Email: ${googleAccount.email}")
                Log.d(TAG, "  Display Name: ${googleAccount.displayName}")
                Log.d(TAG, "  Photo URL: ${googleAccount.photoUrl}")
                Log.d(TAG, "  ID: ${googleAccount.id}")
                Log.d(TAG, "  Server Auth Code: ${googleAccount.serverAuthCode}")
                
                // Analyze photo URL
                googleAccount.photoUrl?.let { uri ->
                    val url = uri.toString()
                    Log.d(TAG, "  Photo URL Analysis:")
                    Log.d(TAG, "    Full URL: $url")
                    Log.d(TAG, "    Contains googleusercontent: ${url.contains("googleusercontent.com")}")
                    Log.d(TAG, "    Contains s96-c: ${url.contains("s96-c")}")
                    Log.d(TAG, "    Contains =s96-c: ${url.contains("=s96-c")}")
                    
                    // Suggest enhanced URL
                    val enhancedUrl = when {
                        url.contains("s96-c") -> url.replace("s96-c", "s400-c")
                        url.contains("=s96-c") -> url.replace("=s96-c", "=s400-c")
                        else -> url
                    }
                    Log.d(TAG, "    Enhanced URL: $enhancedUrl")
                }
            } else {
                Log.w(TAG, "  ✗ No Google account found")
            }
            
            // Check permissions and network
            Log.d(TAG, "System Status:")
            Log.d(TAG, "  Internet Permission: ${checkInternetPermission(context)}")
            Log.d(TAG, "  Network State Permission: ${checkNetworkStatePermission(context)}")
            
            Log.d(TAG, "=== End Diagnostics ===")
        }
        
        private fun checkInternetPermission(context: Context): Boolean {
            return try {
                val pm = context.packageManager
                val permission = android.Manifest.permission.INTERNET
                pm.checkPermission(permission, context.packageName) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        }
        
        private fun checkNetworkStatePermission(context: Context): Boolean {
            return try {
                val pm = context.packageManager
                val permission = android.Manifest.permission.ACCESS_NETWORK_STATE
                pm.checkPermission(permission, context.packageName) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } catch (e: Exception) {
                false
            }
        }
    }
}
