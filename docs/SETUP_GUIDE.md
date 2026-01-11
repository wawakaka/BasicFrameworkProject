# Developer Setup & Installation Guide

**Last Updated:** 2026-01-11
**Milestone:** 5 Complete
**Status:** Production Ready

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [Project Installation](#project-installation)
4. [First Build](#first-build)
5. [IDE Configuration](#ide-configuration)
6. [Gradle Configuration](#gradle-configuration)
7. [Running on Device](#running-on-device)
8. [Troubleshooting](#troubleshooting)
9. [Development Workflow](#development-workflow)
10. [Performance Tips](#performance-tips)

---

## Prerequisites

### System Requirements

| Requirement | Minimum | Recommended |
|-----------|---------|-------------|
| **OS** | macOS 10.15, Ubuntu 18.04, Windows 10 | macOS 12+, Ubuntu 20.04, Windows 11 |
| **RAM** | 8 GB | 16 GB |
| **Disk Space** | 30 GB | 50+ GB |
| **CPU** | Intel i5 / AMD Ryzen 5 | Intel i7 / AMD Ryzen 7 |

### Software Requirements

| Software | Minimum Version | Recommended |
|----------|-----------------|-------------|
| **JDK** | 17 LTS | 21 LTS |
| **Android Studio** | Flamingo (2022.2.1) | Latest Stable |
| **Gradle** | 8.1 | 8.5 |
| **Android SDK** | API 26 (Android 8.0) | API 34 (Android 14) |
| **Target SDK** | API 34 (Android 14) | API 34 (Android 14) |

### Android SDK Components

Required SDK components to install:

```
SDK Platforms:
├── Android API 34 (Android 14)
├── Android API 33 (Android 13)
├── Android API 32 (Android 12)
└── Android API 26 (Android 8.0)

SDK Tools:
├── Android SDK Build-Tools 34
├── Android SDK Platform-Tools (latest)
├── Android Emulator
├── Android SDK Tools (latest)
└── NDK (optional, for native code)

Support Libraries:
├── Google Repository
├── Android Support Repository
└── Google Play Services
```

---

## Environment Setup

### macOS

#### 1. Install JDK 21

```bash
# Using Homebrew
brew install openjdk@21

# Add to shell profile (~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH

# Verify installation
java -version
javac -version
```

#### 2. Install Android Studio

```bash
# Using Homebrew
brew install android-studio

# Or download from: https://developer.android.com/studio
```

#### 3. Configure Android SDK

```bash
# Open Android Studio
# Go to: Android Studio → Preferences → Appearance & Behavior → System Settings → Android SDK

# Install required SDK:
# - SDK Platforms: API 26, 32, 33, 34
# - SDK Tools: Latest versions
# - Emulator
```

#### 4. Set Environment Variables

```bash
# Add to ~/.zshrc or ~/.bash_profile
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$PATH

# Source the profile
source ~/.zshrc  # or ~/.bash_profile
```

### Windows

#### 1. Install JDK 21

```powershell
# Download from: https://www.oracle.com/java/technologies/downloads/#java21

# Or use Chocolatey
choco install openjdk21

# Add to system environment variables:
# JAVA_HOME=C:\Program Files\Java\jdk-21
# Update PATH to include: %JAVA_HOME%\bin

# Verify in Command Prompt
java -version
```

#### 2. Install Android Studio

```powershell
# Download from: https://developer.android.com/studio
# Run installer and follow prompts
```

#### 3. Configure Android SDK

```
- Open Android Studio
- File → Settings → Appearance & Behavior → System Settings → Android SDK
- Install required SDK platforms and tools
```

#### 4. Set Environment Variables

```
New User Variables:
- ANDROID_HOME = C:\Users\{username}\AppData\Local\Android\sdk

Update PATH:
- Add: %ANDROID_HOME%\emulator
- Add: %ANDROID_HOME%\tools
- Add: %ANDROID_HOME%\platform-tools
```

### Linux (Ubuntu/Debian)

#### 1. Install JDK 21

```bash
# Using apt
sudo apt update
sudo apt install openjdk-21-jdk

# Verify
java -version
```

#### 2. Install Android Studio

```bash
# Download from: https://developer.android.com/studio
# Extract and run
./android-studio/bin/studio.sh
```

#### 3. Configure Android SDK

```bash
# Set environment variables in ~/.bashrc
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/emulator:$ANDROID_HOME/tools:$PATH

# Source the profile
source ~/.bashrc
```

---

## Project Installation

### 1. Clone Repository

```bash
# Clone with HTTPS
git clone https://github.com/wawakaka/BasicFrameworkProject.git

# Or clone with SSH (requires SSH key setup)
git clone git@github.com:wawakaka/BasicFrameworkProject.git

# Navigate to project
cd BasicFrameworkProject
```

### 2. Verify Git Configuration

```bash
# Check git status
git status

# Expected output: On branch master (or your current branch)

# View remote
git remote -v

# Expected output:
# origin  https://github.com/wawakaka/BasicFrameworkProject.git (fetch)
# origin  https://github.com/wawakaka/BasicFrameworkProject.git (push)
```

### 3. Verify Gradle Wrapper

```bash
# Check Gradle wrapper files exist
ls -la gradle/wrapper/

# Expected files:
# gradle-wrapper.jar
# gradle-wrapper.properties

# Check gradle permissions (Unix/Linux/macOS)
ls -la gradlew
chmod +x gradlew  # Make executable if needed
```

### 4. Create Local Properties

```bash
# Create local.properties file
cat > local.properties << EOF
sdk.dir=$ANDROID_HOME
ndk.dir=$ANDROID_HOME/ndk/{version}
org.gradle.java.home=$JAVA_HOME
EOF

# Verify file created
cat local.properties
```

---

## First Build

### Build from Command Line

#### 1. Clean Build

```bash
# Full clean build (removes build artifacts)
./gradlew clean build

# Expected output:
# BUILD SUCCESSFUL in Xs
# 335 actionable tasks: 15 executed, 320 up-to-date
```

#### 2. Check Build Status

```bash
# Verify build files were created
ls -la app/build/outputs/apk/

# Expected structure:
# debug/
# ├── app-debug.apk
# └── app-debug-unaligned.apk
```

#### 3. Debug Build

```bash
# Build debug variant quickly
./gradlew assembleDebug

# Output location: app/build/outputs/apk/debug/app-debug.apk
```

#### 4. Release Build

```bash
# Build release variant (requires signing configuration)
./gradlew assembleRelease

# Output location: app/build/outputs/apk/release/app-release-unsigned.apk
```

### Build from Android Studio

#### 1. Open Project

```
- File → Open
- Navigate to BasicFrameworkProject folder
- Click "Open"
- Wait for Gradle sync to complete
```

#### 2. Sync Gradle

```
- Build → Clean Project
- Build → Rebuild Project

# Or use keyboard shortcut: Ctrl+Shift+A (search for "Sync Now")
```

#### 3. Run Build

```
- Build → Build Bundle(s) / APK(s) → Build APK(s)
- Or press: Ctrl+Shift+B

# Watch output in Build panel
```

#### 4. View Build Artifacts

```
- Build → Analyze APK
- Select app/build/outputs/apk/debug/app-debug.apk
- View APK structure and content
```

---

## IDE Configuration

### Android Studio Setup

#### 1. Code Style

```
File → Settings → Editor → Code Style → Kotlin

Configure:
- Indentation: 4 spaces
- Line length: 120 characters (custom limit: 120)
- Right margin: 120

Tabs:
- Use spaces (not tabs)
- Tab size: 4
- Indent: 4
```

#### 2. Project Structure

```
File → Project Structure

SDK:
- JDK: Select 21 LTS
- Android SDK: Select installed API 34
- Gradle: Select 8.5

Modules:
- Verify app, domain, repository, restapi modules listed
- Check each module has correct SDK versions
```

#### 3. Plugins

Recommended plugins to install:

```
File → Settings → Plugins → Marketplace

Recommended:
- Kotlin (usually pre-installed)
- Jetpack Compose IDE Support (pre-installed)
- Material Theme UI (optional, for better UI)
- Markdown (for documentation editing)
- Git Toolbox (for better Git integration)

Search and install:
- Click "Install" button
- Restart IDE when prompted
```

#### 4. Run Configurations

Create run configuration:

```
Run → Edit Configurations

Select "Android App"
- Name: app-debug
- Module: app
- Installation options: Default APK
- Launch options: Default Activity
- Launch: Default
- Debugger: Default

Click "Apply" and "OK"
```

### Editor Tips

#### 1. Enable Compose Preview

```
In any Composable file with @Preview:
- Right side of editor shows "Preview" tab
- Shows live preview of composables
- Click "Refresh" to update preview
```

#### 2. Enable Gradle Build Cache

```
File → Settings → Build, Execution, Deployment → Gradle

Gradle:
- Check "Offline work" (optional)
- Check "Build cache" (recommended)
- VM options: -Xmx2g (adjust for your RAM)
```

#### 3. Enable Code Inspections

```
File → Settings → Editor → Inspections

Enable:
- Android
- Kotlin
- Java
- General

This will highlight potential issues in code
```

---

## Gradle Configuration

### Gradle Wrapper

The project uses Gradle Wrapper for consistent builds:

```bash
# Location
gradle/wrapper/
├── gradle-wrapper.jar       # Wrapper executable
└── gradle-wrapper.properties # Gradle version specification
```

### Version Catalog

Dependencies are managed in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "1.9.22"
compose = "2024.09.00"
agp = "8.2.2"
material3 = "1.2.1"

[libraries]
compose-ui = { group = "androidx.compose.ui", name = "ui", version.ref = "compose" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
```

### Modifying Dependencies

To add a new dependency:

1. **Add version to `libs.versions.toml`:**
```toml
[versions]
newlib = "1.0.0"
```

2. **Add library definition:**
```toml
[libraries]
newlib = { group = "com.example", name = "newlib", version.ref = "newlib" }
```

3. **Add to module's `build.gradle`:**
```kotlin
dependencies {
    implementation libs.newlib
}
```

4. **Sync Gradle:**
```bash
./gradlew clean build
```

---

## Running on Device

### Physical Device Setup

#### 1. Enable Developer Mode

**Android 4.1+:**
- Settings → About Phone
- Tap "Build Number" 7 times
- Back to Settings → Developer options → Enable

#### 2. Enable USB Debugging

```
Settings → Developer options → USB Debugging
- Toggle: ON
```

#### 3. Trust Computer

```
Connect device to computer via USB
- Android will prompt: "Allow USB debugging?"
- Tap "Always allow"
```

#### 4. Install APK

```bash
# List connected devices
./gradlew devices

# Install debug APK
./gradlew installDebug

# Or from Android Studio:
# Run → Run 'app' (or press Shift+F10)

# Monitor logs
./gradlew logcat
```

### Android Emulator Setup

#### 1. Create Virtual Device

```
Android Studio → Tools → Device Manager → Create device

Select device:
- Phone (any modern device)
- API Level: 34
- Target: "Android 14"
- RAM: 2-4 GB
- Heap: 512 MB
- SD Card: 512 MB
- Keyboard: Check "Enable keyboard input"

Finish creating AVD
```

#### 2. Launch Emulator

```bash
# List available emulators
emulator -list-avds

# Launch emulator (from Android Studio)
Tools → Device Manager → Play button

# Or from command line
$ANDROID_HOME/emulator/emulator -avd {AVD_NAME}
```

#### 3. Run App on Emulator

```bash
# Build and install
./gradlew installDebug

# Or from Android Studio:
# Select emulator in device selector
# Click "Run" button (Shift+F10)
```

---

## Troubleshooting

### Common Issues

#### Issue: "Cannot find SDK"

```
Error: Could not find an installed version of Gradle being distributed

Solution:
1. Check ANDROID_HOME environment variable:
   echo $ANDROID_HOME  # macOS/Linux
   echo %ANDROID_HOME%  # Windows

2. If not set, set it:
   export ANDROID_HOME=$HOME/Android/Sdk  # macOS/Linux

3. Verify SDK folder exists:
   ls $ANDROID_HOME/platforms/android-34

4. Create local.properties:
   echo "sdk.dir=$ANDROID_HOME" > local.properties
```

#### Issue: "Unsupported class-file format"

```
Error: Unsupported class-file format
Exception in thread "main" java.lang.UnsupportedClassFileFormatException

Solution:
1. Verify JDK version is 17+:
   java -version

2. Update JAVA_HOME:
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)

3. Configure Android Studio:
   File → Project Structure → JDK → Select 21 LTS

4. Clear gradle cache:
   ./gradlew clean
```

#### Issue: "Gradle sync failed"

```
Error: Unable to get content length for resource

Solution:
1. Check internet connection
2. Clear Gradle cache:
   rm -rf ~/.gradle/caches
   ./gradlew clean

3. Refresh Gradle:
   File → Sync Now
   Or: Ctrl+Shift+A → "Sync Now"

4. If still failing, try offline mode:
   ./gradlew build --offline
```

#### Issue: "Compose Preview not showing"

```
Solution:
1. Add @Preview annotation to composable:
   @Preview
   @Composable
   fun MyComponentPreview() { }

2. Click "Refresh" in preview panel
3. If still not showing, restart IDE:
   File → Invalidate Caches / Restart

4. Check Compose version in build.gradle
```

#### Issue: "Device not recognized"

```
For macOS:
1. Install Android File Transfer:
   brew install --cask android-file-transfer

2. Reconnect device

3. Restart adb:
   adb kill-server
   adb start-server

For Linux:
1. Install libusb:
   sudo apt install libusb-dev

2. Create udev rule:
   echo 'SUBSYSTEM=="usb", ATTR{idVendor}=="0fce", MODE="0666"' | sudo tee /etc/udev/rules.d/51-android.rules
   sudo udevadm control --reload-rules

3. Reconnect device
```

---

## Development Workflow

### Daily Development

```bash
# Morning: Update dependencies and sync
./gradlew clean build

# Make changes to code
# (Use IDE for editing and debugging)

# Run tests after changes
./gradlew test

# Before committing
./gradlew clean build test

# Run on device/emulator
./gradlew installDebug  # or use IDE Run button
```

### Commit Workflow

```bash
# Check status before committing
git status

# Make meaningful commits
git add {files}
git commit -m "Brief description of change"

# Push to remote
git push origin {branch}

# Create pull request on GitHub
```

### Debug Mode

```bash
# In Android Studio:
1. Set breakpoint (click line number margin)
2. Run → Debug 'app' (Shift+F9)
3. Step through code:
   - Step Over: F10
   - Step Into: F11
   - Step Out: Shift+F11
   - Resume: F9
```

---

## Performance Tips

### Build Performance

#### 1. Parallel Builds

```gradle
# Add to gradle.properties
org.gradle.parallel=true
org.gradle.workers.max=4
```

#### 2. Build Cache

```gradle
# Add to gradle.properties
org.gradle.build.cache=true
```

#### 3. Incremental Annotation Processing

```gradle
# Add to gradle.properties
org.gradle.incremental.annotation.processing=true
```

#### 4. Kotlin Incremental Compilation

```gradle
# Add to gradle.properties
kotlin.incremental=true
kotlin.incremental.js=true
```

#### 5. Daemon JVM Args

```gradle
# Add to gradle.properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxPermSize=512m
```

### IDE Performance

#### 1. Disable Unnecessary Inspections

```
File → Settings → Editor → Inspections
- Uncheck items you don't need
- Reduces IDE overhead
```

#### 2. Increase IDE Memory

```
Help → Change Memory Settings
- Set to 2048 MB or higher (based on RAM)
- Restart IDE
```

#### 3. Enable Power Saving Mode

```
File → Power Saving Mode
- Toggle on when focused on editing
- Disables real-time inspections temporarily
```

---

## Useful Commands Reference

### Build Commands

```bash
./gradlew clean            # Clean build artifacts
./gradlew build            # Full build
./gradlew assembleDebug    # Debug APK only
./gradlew assembleRelease  # Release APK only
./gradlew test             # Run unit tests
./gradlew connectedAndroidTest  # Run instrumented tests
```

### Development Commands

```bash
./gradlew tasks            # List all tasks
./gradlew properties       # Show project properties
./gradlew dependencies     # Show dependency tree
./gradlew dependencyInsight --dependency {lib}  # Show dependency details
./gradlew lint             # Run lint checks
```

### Device Commands

```bash
adb devices                # List connected devices
adb install app-debug.apk  # Install APK
adb uninstall {package}    # Uninstall app
adb logcat                 # View device logs
adb push {local} {remote}  # Copy file to device
adb pull {remote} {local}  # Copy file from device
```

---

**Last Updated:** 2026-01-11
**Status:** Complete & Production Ready
**Ready for Use:** ✅ YES
