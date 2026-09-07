# X Board Sinhala Keyboard - Complete Google Play Store Deployment Guide

This guide details how to build and upload your customized Sinhala & English Keyboard to Google Play Store using Android Studio.

## Requirements
- Android Studio Ladybug or newer
- JDK 17
- Android Device or Emulator running Android 7.0+ (API 24+)
- Google Play Developer Account ($25 one-time registration)

## Steps
1. **Extract and Open in Android Studio**:
   - Unzip this archive.
   - Launch Android Studio and choose "Open" -> select the extracted directory.
   - Wait for Gradle sync to complete.

2. **Test Locally**:
   - Connect your Android device with USB debugging enabled.
   - Click "Run" (Green Play button).
   - Follow on-screen instructions in the app to enable the keyboard in Android Settings.

3. **Generate Signed Bundle**:
   - Menu: `Build` > `Generate Signed Bundle / APK`
   - Select `Android App Bundle`
   - Create a new KeyStore (save password safely for future updates)
   - Choose `release` build variant and generate `.aab`.

4. **Upload to Google Play Console**:
   - Create App with package name `com.xboard.sinhalakeyboard`.
   - Complete Data Safety (No personal data collected).
   - Add App Icon (512x512) and Feature Graphic (1024x500).
   - Upload the `.aab` in Production track and submit for review.
