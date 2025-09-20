package com.example.bodytunemobileapp.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.example.bodytunemobileapp.R

class ProfilePictureLoader {
    
    companion object {
        fun loadProfilePicture(context: Context, imageView: ImageView) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            
            var profilePhotoUrl: String? = null
            
            // Try to get profile photo from Google account first
            googleAccount?.photoUrl?.let { uri ->
                profilePhotoUrl = uri.toString()
                // Get higher resolution image by modifying the URL
                if (profilePhotoUrl?.contains("s96-c") == true) {
                    profilePhotoUrl = profilePhotoUrl?.replace("s96-c", "s400-c")
                }
            }
            
            // Fallback to Firebase user photo URL
            if (profilePhotoUrl == null) {
                currentUser?.photoUrl?.let { uri ->
                    profilePhotoUrl = uri.toString()
                }
            }
            
            // Load the image using Glide
            if (profilePhotoUrl != null) {
                Glide.with(context)
                    .load(profilePhotoUrl)
                    .apply(RequestOptions()
                        .transform(CircleCrop())
                        .placeholder(R.drawable.profile_placeholder)
                        .error(R.drawable.profile_placeholder)
                    )
                    .into(imageView)
            } else {
                // Load default placeholder
                Glide.with(context)
                    .load(R.drawable.profile_placeholder)
                    .apply(RequestOptions().transform(CircleCrop()))
                    .into(imageView)
            }
        }
        
        fun getUserDisplayName(context: Context): String {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            
            // Try Google account display name first
            googleAccount?.displayName?.let { name ->
                if (name.isNotBlank()) return name
            }
            
            // Fallback to Firebase user display name
            currentUser?.displayName?.let { name ->
                if (name.isNotBlank()) return name
            }
            
            // Fallback to email
            currentUser?.email?.let { email ->
                return email.substringBefore("@")
            }
            
            return "User"
        }
        
        fun getUserEmail(context: Context): String {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            
            // Try Google account email first
            googleAccount?.email?.let { email ->
                if (email.isNotBlank()) return email
            }
            
            // Fallback to Firebase user email
            currentUser?.email?.let { email ->
                return email
            }
            
            return "No email"
        }
        
        fun hasProfilePicture(context: Context): Boolean {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val googleAccount = GoogleSignIn.getLastSignedInAccount(context)
            
            return googleAccount?.photoUrl != null || currentUser?.photoUrl != null
        }
    }
}
