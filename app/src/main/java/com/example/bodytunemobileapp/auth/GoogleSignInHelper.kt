package com.example.bodytunemobileapp.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.bodytunemobileapp.R

class GoogleSignInHelper(private val context: Context) {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient
    
    companion object {
        const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleSignInHelper"
    }
    
    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .requestProfile()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }
    
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    fun handleSignInResult(data: Intent?, callback: (Boolean, String?, GoogleSignInAccount?) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google sign in successful: ${account.email}")
            firebaseAuthWithGoogle(account, callback)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
            callback(false, "Google sign in failed: ${e.message}", null)
        }
    }
    
    private fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount,
        callback: (Boolean, String?, GoogleSignInAccount?) -> Unit
    ) {
        Log.d(TAG, "firebaseAuthWithGoogle: ${account.id}")
        
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    callback(true, null, account)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    callback(false, "Authentication failed: ${task.exception?.message}", null)
                }
            }
    }
    
    fun signOut(callback: () -> Unit) {
        // Firebase sign out
        auth.signOut()
        
        // Google sign out
        googleSignInClient.signOut().addOnCompleteListener {
            callback()
        }
    }
    
    fun getCurrentGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }
    
    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }
}
