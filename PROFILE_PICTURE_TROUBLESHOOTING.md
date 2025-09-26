# Profile Picture Troubleshooting Guide

## 🔍 **Step-by-Step Debugging Process**

### **Step 1: Check the Logs**
Run your app and look for these specific log messages in Android Studio Logcat:

```
Filter by: ProfilePictureLoader OR ProfileDiagnostics
```

**Expected Log Output:**
```
D/ProfileDiagnostics: === Profile Picture Diagnostics ===
D/ProfileDiagnostics: Firebase Auth Status:
D/ProfileDiagnostics:   ✓ User is signed in
D/ProfileDiagnostics:   Email: your-email@gmail.com
D/ProfilePictureLoader: === PROFILE PICTURE LOADING DEBUG ===
D/ProfilePictureLoader: Firebase user: your-email@gmail.com
D/ProfilePictureLoader: Google account: your-email@gmail.com
D/ProfilePictureLoader: Google account photo URL: https://lh3.googleusercontent.com/...
D/ProfilePictureLoader: ✓ Google photo URL found: https://lh3.googleusercontent.com/...
D/ProfilePictureLoader: ✓ Enhanced URL: https://lh3.googleusercontent.com/...=s400-c
D/ProfilePictureLoader: ✓ Loading profile picture from google: https://lh3.googleusercontent.com/...=s400-c
```

### **Step 2: Common Issues and Solutions**

#### **Issue 1: No Google Account Found**
**Log shows:** `Google account: null`

**Solutions:**
1. **Re-sign in with Google:**
   - Sign out from the app
   - Sign in again using Google Sign-In button
   - Make sure you select an account with a profile picture

2. **Check Google Services Configuration:**
   - Verify `google-services.json` exists in `/app/` directory
   - Check if web client ID is correct in `strings.xml`

#### **Issue 2: No Photo URL Found**
**Log shows:** `Google account photo URL: null`

**Solutions:**
1. **Check Google Account:**
   - Go to [myaccount.google.com](https://myaccount.google.com)
   - Verify you have a profile picture set
   - Make sure profile picture is public/visible

2. **Update Google Account:**
   - Add or change your Google profile picture
   - Wait a few minutes for changes to propagate
   - Re-sign in to the app

#### **Issue 3: URL Found but Image Not Loading**
**Log shows:** URL found but image still shows placeholder

**Solutions:**
1. **Test Manual Refresh:**
   - Click on profile picture → "Refresh Picture"
   - Check if image loads after refresh

2. **Check Network Connection:**
   - Ensure device has internet connection
   - Try on different network (WiFi vs Mobile data)

3. **Clear App Cache:**
   - Go to Android Settings → Apps → BodyTune → Storage → Clear Cache
   - Restart the app

### **Step 3: Manual Testing Steps**

#### **Test 1: Profile Menu Test**
1. Click on the profile picture (placeholder)
2. Profile menu should show:
   - Your name
   - Your email
   - "Refresh Picture" button
   - "Sign Out" button

#### **Test 2: Force Refresh Test**
1. Click "Refresh Picture" in profile menu
2. Watch for notification: "Refreshing profile picture..."
3. Check logs for new loading attempts

#### **Test 3: Sign Out/In Test**
1. Sign out from the app
2. Sign in again with Google
3. Check if profile picture loads on fresh sign-in

### **Step 4: Advanced Debugging**

#### **Check URL Accessibility**
Look for these logs to verify URL parsing:
```
D/ProfilePictureLoader: Testing URL accessibility: https://lh3.googleusercontent.com/...
D/ProfilePictureLoader: ✓ URL parsed successfully
D/ProfilePictureLoader:   - Scheme: https
D/ProfilePictureLoader:   - Host: lh3.googleusercontent.com
```

#### **Check Alternative URLs**
If primary URL fails, check for alternative attempts:
```
D/ProfilePictureLoader: Alternative URLs to try: [url1, url2, url3]
D/ProfilePictureLoader: Trying first alternative URL: https://lh3.googleusercontent.com/...=s200-c
```

### **Step 5: Common Root Causes**

#### **1. Google Services Setup Issues**
- Missing or incorrect `google-services.json`
- Wrong web client ID in Firebase Console
- SHA-1 certificate not added to Firebase project

#### **2. Account/Permission Issues**
- Google account doesn't have profile picture
- Profile picture privacy settings restrict access
- Account signed in with different provider (email/password vs Google)

#### **3. Network/Cache Issues**
- Poor internet connection
- Glide cache corruption
- Firewall blocking Google CDN

#### **4. App Configuration Issues**
- Missing internet permissions
- Incorrect Glide configuration
- Firebase Auth not properly initialized

### **Step 6: Quick Fixes to Try**

#### **Fix 1: Force Clear Everything**
```kotlin
// Add this to MainActivity for testing
private fun clearEverythingAndReload() {
    // Clear Glide cache
    Glide.get(this).clearMemory()
    Thread {
        Glide.get(this).clearDiskCache()
    }.start()
    
    // Force refresh profile picture
    ProfilePictureLoader.forceRefreshProfilePicture(this, ivProfile)
}
```

#### **Fix 2: Test with Direct URL**
```kotlin
// Test with a known working Google profile picture URL
private fun testWithKnownUrl() {
    val testUrl = "https://lh3.googleusercontent.com/a/default-user=s400-c"
    Glide.with(this)
        .load(testUrl)
        .apply(RequestOptions().transform(CircleCrop()))
        .into(ivProfile)
}
```

#### **Fix 3: Check Permissions**
Add to `AndroidManifest.xml` if missing:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### **Step 7: Expected Behavior**

#### **When Working Correctly:**
1. **App Launch:** Profile picture loads automatically
2. **Logs Show:** Clear progression from URL discovery to successful load
3. **Visual:** Circular profile picture instead of placeholder
4. **Refresh:** "Refresh Picture" button updates image immediately

#### **When Not Working:**
1. **App Launch:** Only placeholder shows
2. **Logs Show:** Either no URL found or loading failures
3. **Visual:** Generic placeholder icon remains
4. **Refresh:** No change after refresh attempts

### **Step 8: Contact Information**

If none of these steps resolve the issue, please provide:

1. **Complete log output** from ProfilePictureLoader and ProfileDiagnostics
2. **Google account status** (does it have a profile picture?)
3. **Network environment** (WiFi, mobile data, corporate network?)
4. **Device information** (Android version, device model)
5. **Firebase project setup** (is Google Sign-In enabled?)

---

## 🚀 **Quick Test Checklist**

- [ ] Check logs for ProfilePictureLoader messages
- [ ] Verify Google account has profile picture
- [ ] Test "Refresh Picture" button in profile menu
- [ ] Try signing out and back in
- [ ] Check internet connection
- [ ] Verify google-services.json exists
- [ ] Test on different network
- [ ] Clear app cache and retry

**Most Common Solution:** Re-sign in with Google account that has a profile picture set.
