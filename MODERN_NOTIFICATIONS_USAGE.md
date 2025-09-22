# Modern Notifications Usage Guide

## Overview
The BodyTune app now includes a modern notification system that replaces traditional Toast messages with elegant, animated notifications.

## Features
- ✨ **Modern Design**: Gradient backgrounds with smooth animations
- 🎨 **Multiple Types**: Success, Error, and Info notifications
- 🔄 **Smooth Animations**: Slide-in from top with fade effects
- ⏱️ **Auto Dismiss**: Configurable duration with manual close option
- 📱 **Responsive**: Works across all screen sizes

## Usage Examples

### Basic Usage
```kotlin
// Success notification
ModernNotification.showSuccess(this, "Data saved successfully!")

// Error notification
ModernNotification.showError(this, "Failed to save data")

// Info notification
ModernNotification.showInfo(this, "Please check your internet connection")
```

### Advanced Usage
```kotlin
// Custom duration and title
ModernNotification.show(
    activity = this,
    title = "Custom Title",
    message = "Your custom message here",
    type = ModernNotification.NotificationType.SUCCESS,
    duration = 5000L // 5 seconds
)
```

### Manual Dismiss
```kotlin
// Dismiss current notification programmatically
ModernNotification.dismiss()
```

## Notification Types

### 1. Success Notifications
- **Color**: Green gradient (#4CAF50 → #45A049)
- **Icon**: Check circle
- **Usage**: Data saved, operations completed successfully
- **Default Duration**: 3 seconds

### 2. Error Notifications
- **Color**: Red gradient (#F44336 → #E53935)
- **Icon**: Error circle
- **Usage**: Validation errors, save failures, network errors
- **Default Duration**: 4 seconds

### 3. Info Notifications
- **Color**: Blue gradient (#2196F3 → #1976D2)
- **Icon**: Info circle
- **Usage**: General information, tips, status updates
- **Default Duration**: 3 seconds

## Implementation Examples

### Profile Setup Activity
```kotlin
// Validation error
if (height.isEmpty()) {
    ModernNotification.showError(this, "Please enter your height")
    return
}

// Success after saving
ModernNotification.showSuccess(this, "Profile saved successfully!")
```

### BMI Calculator Activity
```kotlin
// Success after calculation
ModernNotification.showSuccess(this, "BMI record saved successfully!")

// Error handling
ModernNotification.showError(this, "Failed to save BMI record: $error")
```

### Workout Activities
```kotlin
// Workout completed
ModernNotification.showSuccess(this, "Workout completed! Great job!")

// Rest time info
ModernNotification.showInfo(this, "Take a 30-second rest")

// Form validation
ModernNotification.showError(this, "Please select at least one exercise")
```

## Customization

### Colors
Modify colors in `colors.xml`:
```xml
<color name="notification_success">#4CAF50</color>
<color name="notification_error">#F44336</color>
<color name="notification_info">#2196F3</color>
```

### Animation Duration
Modify in `ModernNotification.kt`:
```kotlin
translateY.duration = 300 // Slide animation
alpha.duration = 300      // Fade animation
```

### Layout
Customize the notification layout in `custom_notification.xml`:
- Change card corner radius
- Modify padding and margins
- Add additional UI elements

## Best Practices

1. **Use Appropriate Types**:
   - Success: For completed actions
   - Error: For failures and validation
   - Info: For general information

2. **Keep Messages Concise**:
   - Use clear, actionable language
   - Avoid technical jargon
   - Maximum 2 lines of text

3. **Don't Overuse**:
   - Only show notifications for important events
   - Avoid showing multiple notifications rapidly

4. **Test on Different Screens**:
   - Ensure notifications look good on all device sizes
   - Test with different system fonts and accessibility settings

## Migration from Toast

### Before (Toast)
```kotlin
Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show()
```

### After (Modern Notification)
```kotlin
ModernNotification.showSuccess(this, "Data saved successfully!")
```

## Troubleshooting

### Notification Not Showing
- Ensure the activity context is valid
- Check if another notification is currently displayed
- Verify the root view is accessible

### Animation Issues
- Check if hardware acceleration is enabled
- Ensure the activity is not finishing during animation
- Verify view hierarchy is not being modified during animation

### Memory Leaks
- The notification system automatically cleans up references
- No manual cleanup required
- Notifications are dismissed when activities are destroyed
