# Firebase Setup Guide for BodyTune App

This guide will walk you through setting up Firebase for the BodyTune mobile application.

## Prerequisites

- Android Studio installed
- Google account
- BodyTune project already configured with Firebase dependencies

## Step 1: Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Create a project" or "Add project"
3. Enter project name: `BodyTune` or `bodytune-app`
4. Choose whether to enable Google Analytics (recommended)
5. Select or create a Google Analytics account
6. Click "Create project"

## Step 2: Add Android App to Firebase Project

1. In Firebase Console, click "Add app" and select Android
2. Enter the following details:
   - **Android package name**: `com.example.bodytunemobileapp`
   - **App nickname**: `BodyTune Mobile App`
   - **Debug signing certificate SHA-1**: (Optional, but recommended for Google Sign-In)

### Getting SHA-1 Certificate (Optional)

Run this command in your project terminal:
```bash
./gradlew signingReport
```
Copy the SHA-1 from the debug keystore.

3. Click "Register app"

## Step 3: Download Configuration File

1. Download the `google-services.json` file
2. Move it to your app module directory: `app/google-services.json`
3. **Replace** the existing `google-services.json.template` file

## Step 4: Enable Firebase Services

### Enable Authentication
1. In Firebase Console → Authentication
2. Click "Get started"
3. Go to "Sign-in method" tab
4. Enable "Email/Password" provider
5. Click "Save"

### Enable Realtime Database
1. In Firebase Console → Realtime Database
2. Click "Create Database"
3. Choose location (preferably closest to your users)
4. Start in **test mode** for development
5. Click "Enable"

### Set Database Rules
1. Go to Realtime Database → Rules tab
2. Replace the default rules with the content from `firebase-database-rules.json`
3. Click "Publish"

## Step 5: Build and Test

1. Clean and rebuild your project:
```bash
./gradlew clean
./gradlew build
```

2. Run the app and test:
   - Sign up with a new account
   - Sign in with existing account
   - Calculate BMI (should save to Firebase)
   - Check Firebase Console to see data

## Firebase Integration Features

### ✅ **Authentication**
- Email/Password sign-up and sign-in
- User session management
- Automatic sign-in for returning users

### ✅ **Realtime Database**
- User profiles storage
- BMI calculation history
- Workout session tracking
- Real-time data synchronization

### ✅ **Data Models**
- **User**: Profile information, fitness goals
- **BMIRecord**: BMI calculations with timestamps
- **WorkoutSession**: Exercise tracking with duration and calories

## Usage Examples

### Save User Profile
```kotlin
val user = User(
    uid = FirebaseHelper.getCurrentUserId()!!,
    email = "user@example.com",
    name = "John Doe",
    age = 25,
    height = 175.0,
    weight = 70.0
)

FirebaseHelper.saveUserProfile(user) { success, error ->
    if (success) {
        // Profile saved successfully
    }
}
```

### Track Workout Session
```kotlin
val workoutTracker = WorkoutTracker(this)
workoutTracker.startWorkout("Cardio", "Running")

// Later...
workoutTracker.endWorkout(caloriesBurned = 300, notes = "Great session!")
```

### Get BMI History
```kotlin
FirebaseHelper.getBMIRecords { records, error ->
    records?.let { bmiList ->
        // Display BMI history
    }
}
```

## Security Rules

The database rules ensure:
- Users can only access their own data
- Data validation for all fields
- Authentication required for all operations
- Proper data structure enforcement

## Troubleshooting

### Common Issues

1. **Build fails with "google-services.json not found"**
   - Ensure `google-services.json` is in the `app/` directory
   - Check that the file is not named `google-services.json.template`

2. **Authentication fails**
   - Verify Email/Password is enabled in Firebase Console
   - Check internet connectivity
   - Ensure Firebase project is correctly configured

3. **Database permission denied**
   - Check that database rules are properly set
   - Verify user is authenticated before database operations
   - Ensure rules match the data structure

4. **App crashes on startup**
   - Check that `google-services.json` has correct package name
   - Verify Firebase dependencies are correctly added
   - Check logs for specific error messages

### Debug Commands

```bash
# Check Firebase configuration
./gradlew app:dependencies | grep firebase

# View detailed build logs
./gradlew build --info

# Clean project
./gradlew clean
```

## Production Considerations

Before releasing to production:

1. **Update Database Rules**: Change from test mode to production rules
2. **Enable App Check**: Add additional security layer
3. **Set up Analytics**: Configure conversion tracking
4. **Add Crashlytics**: Monitor app stability
5. **Configure Backup**: Set up automated database backups

## Support

For additional help:
- [Firebase Documentation](https://firebase.google.com/docs)
- [Android Firebase Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication Guide](https://firebase.google.com/docs/auth/android/start)
- [Realtime Database Guide](https://firebase.google.com/docs/database/android/start)

---

**Note**: This setup guide assumes you're using the latest Firebase SDK versions as specified in the project's `libs.versions.toml` file.
