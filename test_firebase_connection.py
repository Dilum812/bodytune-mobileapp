#!/usr/bin/env python3
"""
Simple Firebase connection test for BodyTune app
This script tests if your Firebase project is properly configured
"""

import json
import requests
import sys

def test_firebase_connection():
    print("🔥 Testing Firebase Connection for BodyTune App")
    print("=" * 50)
    
    # Read the google-services.json file
    try:
        with open('app/google-services.json', 'r') as f:
            config = json.load(f)
        print("✅ google-services.json file found and loaded")
    except FileNotFoundError:
        print("❌ google-services.json file not found in app/ directory")
        return False
    except json.JSONDecodeError:
        print("❌ Invalid JSON in google-services.json file")
        return False
    
    # Extract project info
    project_info = config.get('project_info', {})
    project_id = project_info.get('project_id')
    project_number = project_info.get('project_number')
    
    if not project_id:
        print("❌ No project_id found in configuration")
        return False
    
    print(f"📋 Project ID: {project_id}")
    print(f"📋 Project Number: {project_number}")
    
    # Test Firebase Realtime Database connection
    database_url = f"https://{project_id}-default-rtdb.firebaseio.com/.json"
    
    try:
        print(f"\n🔍 Testing database connection to: {database_url}")
        response = requests.get(database_url, timeout=10)
        
        if response.status_code == 200:
            print("✅ Database connection successful!")
            print(f"📊 Response: {response.status_code} - Database is accessible")
            return True
        elif response.status_code == 401:
            print("⚠️  Database connection successful but requires authentication")
            print("📊 This is expected - your database rules are working correctly")
            return True
        else:
            print(f"❌ Database connection failed: {response.status_code}")
            print(f"📊 Response: {response.text}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"❌ Network error connecting to database: {e}")
        return False
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        return False

def check_firebase_services():
    print("\n🛠️  Firebase Services Configuration Check")
    print("-" * 40)
    
    try:
        with open('app/google-services.json', 'r') as f:
            config = json.load(f)
        
        # Check for required client configuration
        clients = config.get('client', [])
        if not clients:
            print("❌ No client configurations found")
            return False
        
        # Find the correct client for our package
        target_package = "com.example.bodytunemobileapp"
        correct_client = None
        
        for client in clients:
            client_info = client.get('client_info', {})
            android_info = client_info.get('android_client_info', {})
            package_name = android_info.get('package_name')
            
            if package_name == target_package:
                correct_client = client
                break
        
        if not correct_client:
            print(f"❌ No client configuration found for package: {target_package}")
            print("Available packages:")
            for client in clients:
                client_info = client.get('client_info', {})
                android_info = client_info.get('android_client_info', {})
                package_name = android_info.get('package_name')
                print(f"   - {package_name}")
            return False
        
        print(f"✅ Client configuration found for: {target_package}")
        
        # Check API key
        api_keys = correct_client.get('api_key', [])
        if api_keys and api_keys[0].get('current_key'):
            print("✅ API key configured")
        else:
            print("❌ No API key found")
            return False
        
        # Check OAuth client
        oauth_clients = correct_client.get('oauth_client', [])
        if oauth_clients:
            print("✅ OAuth client configured")
        else:
            print("⚠️  No OAuth client configured (needed for Google Sign-In)")
        
        return True
        
    except Exception as e:
        print(f"❌ Error checking Firebase services: {e}")
        return False

def main():
    print("🚀 BodyTune Firebase Connection Test")
    print("=" * 50)
    
    # Test basic connection
    connection_ok = test_firebase_connection()
    
    # Check services configuration
    services_ok = check_firebase_services()
    
    print("\n" + "=" * 50)
    print("📋 SUMMARY")
    print("=" * 50)
    
    if connection_ok and services_ok:
        print("🎉 ALL TESTS PASSED!")
        print("✅ Firebase is properly configured and connected")
        print("✅ Your BodyTune app is ready to use Firebase")
        print("\n📱 Next steps:")
        print("   1. Enable Authentication in Firebase Console")
        print("   2. Enable Realtime Database in Firebase Console")
        print("   3. Set database security rules")
        print("   4. Run your Android app and test sign-up/sign-in")
        return True
    else:
        print("❌ SOME TESTS FAILED")
        print("🔧 Please check the issues above and fix them")
        return False

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)
