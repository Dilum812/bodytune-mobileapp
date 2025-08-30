# BodyTune Mobile App

A fitness tracking mobile application built with Android Studio and Kotlin.

## Onboarding Screens

The app features 7 onboarding screens that introduce users to the app's features and collect user information:

### 1. Welcome Screen
- **Content**: "Welcome to Your Productive Journey" with "Track fitness in one simple app."
- **Design**: Man with athletic wear and water bottle, app logo, title, and description
- **Navigation**: Next and Skip buttons

### 2. Feature Screen 1 - Smarter Tracking
- **Content**: "Smarter Tracking, Better Results" with "Log meals, runs, and BMI easily."
- **Design**: Smiling man with crossed arms, app logo, title, and description
- **Navigation**: Next and Skip buttons

### 3. Feature Screen 2 - Smarter Tracking
- **Content**: "Smarter Tracking, Better Results" with "Log meals, runs, and BMI easily."
- **Design**: Woman with earbuds and sweat, app logo, title, and description
- **Navigation**: Next and Skip buttons

### 4. Sign In Screen
- **Content**: "Welcome Back" with "Log in now to continue your BodyTune fitness journey."
- **Design**: Email and password input fields, Google sign-in option
- **Navigation**: Sign In button and "Don't have an account? Sign Up" link

### 5. Sign Up Screen
- **Content**: "Create Account" with "Log in now to continue your BodyTune fitness journey."
- **Design**: Email, password, and confirm password input fields, Google sign-up option
- **Navigation**: Sign Up button

### 6. Profile Setup Screen
- **Content**: "Tell Us About Yourself" with "Your info helps us create the perfect plan for you."
- **Design**: Gender selection, height, weight, and age input fields
- **Navigation**: Continue and Skip for now buttons

### 7. Ready to Start Screen
- **Content**: "Ready to Start?" with "Join thousands of users who have transformed their fitness journey with BodyTune."
- **Design**: Confident person with motivational elements, app logo, title, and description
- **Navigation**: "Get Started" and Skip buttons

## Features

- **ViewPager2**: Smooth horizontal swiping between screens
- **TabLayout**: Page indicators showing current position
- **Skip Functionality**: Users can skip to the main app at any time
- **Immersive Design**: Full-screen experience with hidden system bars
- **Responsive Layout**: Adapts to different screen sizes
- **Dark Theme**: Consistent black background with white text and blue accents
- **Multiple Screen Types**: Onboarding, authentication, and profile setup screens
- **Form Validation**: Input fields with proper validation and styling

## Technical Implementation

- **Activity**: `OnboardingActivity.kt`
- **Adapter**: `OnboardingAdapter.kt` with support for multiple view types
- **Model**: `OnboardingItem.kt` data class with `ScreenType` enum
- **Layouts**: 
  - `activity_onboarding.xml` - Main activity layout
  - `item_onboarding.xml` - Regular onboarding item layout
  - `item_sign_in.xml` - Sign in screen layout
  - `item_sign_up.xml` - Sign up screen layout
  - `item_profile_setup.xml` - Profile setup screen layout

## Navigation

- Users can swipe between screens or use the Next button
- Skip button allows users to jump directly to the main app
- On the final screen, "Next" becomes "Get Started"
- After completing onboarding, users are taken to `MainActivity`

## UI Components

- **Buttons**: Material Design buttons with blue primary color
- **Typography**: Bold titles with descriptive subtitles
- **Images**: Vector drawables representing fitness scenarios
- **Logo**: Custom TB logo design in blue and white
- **Indicators**: Dot indicators showing progress through onboarding
- **Input Fields**: Styled text input fields with icons
- **Gender Selection**: Interactive buttons for gender selection
- **Google Integration**: Google sign-in/sign-up buttons

## Screen Flow

1. **Welcome Screen** → Next/Skip buttons
2. **Feature Screen 1** → Next/Skip buttons  
3. **Feature Screen 2** → Next/Skip buttons
4. **Sign In Screen** → Sign In/Google Sign In
5. **Sign Up Screen** → Sign Up/Google Sign Up
6. **Profile Setup Screen** → Continue/Skip for now
7. **Ready to Start Screen** → "Get Started"/Skip buttons
8. **Main App** → `MainActivity`

## Build & Run

```bash
# Build the project
./gradlew build

# Install on device/emulator
./gradlew installDebug

# Run the app
./gradlew runDebug
```

The onboarding screens will automatically start when users launch the app, providing a comprehensive introduction to your fitness tracking features and collecting necessary user information.
