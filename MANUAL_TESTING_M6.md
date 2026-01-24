# Milestone 6: Manual Testing & Verification Checklist

**Date:** 2026-01-24
**Milestone:** M6 - TOAD Architecture Migration
**Status:** Ready for Testing

---

## Overview

This document provides a comprehensive manual testing checklist for Milestone 6. All tests should pass to ensure the MVP → TOAD migration is successful and the application works correctly.

---

## Pre-Testing Setup

### Build Verification

- [ ] **Clean build succeeds**
  ```bash
  ./gradlew clean build
  ```
  - ✅ Expected: Build completes without errors
  - ✅ Expected: Deprecation warnings shown (expected for MVP components)
  - ❌ Fail if: Compilation errors occur

- [ ] **Debug build installs**
  ```bash
  ./gradlew installDebug
  ```
  - ✅ Expected: APK installs on device/emulator
  - ❌ Fail if: Installation fails

- [ ] **Unit tests pass**
  ```bash
  ./gradlew test
  ```
  - ✅ Expected: All 26 ViewModel tests pass
  - ❌ Fail if: Any test fails

- [ ] **Integration tests pass** (if device/emulator available)
  ```bash
  ./gradlew connectedAndroidTest
  ```
  - ✅ Expected: All 32 Compose UI tests pass
  - ❌ Fail if: Any test fails

---

## Application Launch

### Initial Launch

- [ ] **App launches successfully**
  - ✅ Expected: No crashes on launch
  - ✅ Expected: Material 3 theme applied
  - ✅ Expected: Main screen visible
  - ❌ Fail if: App crashes or freezes

- [ ] **Permission screen displays**
  - ✅ Expected: "Checking permissions..." or permission request shown
  - ✅ Expected: AppTopBar shows "Currency Rates"
  - ❌ Fail if: Blank screen or crash

### Permission Flow

- [ ] **Permission request dialog appears**
  - ✅ Expected: System permission dialog for Camera
  - ✅ Expected: "Allow" and "Deny" buttons visible
  - ❌ Fail if: Dialog doesn't appear

- [ ] **Permission denied flow**
  - Action: Deny permission
  - ✅ Expected: Permission denied screen shows
  - ✅ Expected: "Camera permission is required" text visible
  - ✅ Expected: "Request Permission" button visible
  - ❌ Fail if: App crashes or shows wrong state

- [ ] **Retry permission request**
  - Action: Click "Request Permission" button
  - ✅ Expected: Permission dialog appears again
  - ✅ Expected: Can grant permission on retry
  - ❌ Fail if: Nothing happens or crash

- [ ] **Permission granted flow**
  - Action: Grant permission (or click Allow)
  - ✅ Expected: Permission granted state reached
  - ✅ Expected: Currency screen loads automatically
  - ❌ Fail if: Stuck on permission screen

---

## Currency Screen - Initial Load

### Screen Display

- [ ] **Currency screen loads**
  - ✅ Expected: AppTopBar shows "Currency Rates"
  - ✅ Expected: Loading indicator appears initially
  - ✅ Expected: Refresh FAB visible after loading
  - ❌ Fail if: Blank screen or crash

- [ ] **Data loads successfully**
  - ✅ Expected: Loading indicator disappears
  - ✅ Expected: Currency list displays
  - ✅ Expected: Multiple currency items visible (USD, EUR, etc.)
  - ✅ Expected: Timestamp shows "Last updated: YYYY-MM-DD HH:MM:SS"
  - ❌ Fail if: Data doesn't load or error shown

- [ ] **Currency list items display correctly**
  - ✅ Expected: Currency code visible (e.g., "USD")
  - ✅ Expected: Exchange rate visible (e.g., "1.0")
  - ✅ Expected: Material 3 Card styling
  - ✅ Expected: List scrolls smoothly
  - ❌ Fail if: Items missing or malformed

### Loading State

- [ ] **Loading indicator works**
  - ✅ Expected: Circular progress indicator centered
  - ✅ Expected: "Loading" text visible
  - ✅ Expected: No other content visible during loading
  - ❌ Fail if: Multiple states shown simultaneously

---

## Currency Screen - User Interactions

### Refresh Functionality

- [ ] **Refresh FAB is visible**
  - ✅ Expected: Extended FAB at bottom-right
  - ✅ Expected: Shows "Refresh" text with icon
  - ✅ Expected: Material 3 styling
  - ❌ Fail if: FAB missing or misplaced

- [ ] **Refresh FAB click works**
  - Action: Click Refresh button
  - ✅ Expected: Loading indicator appears
  - ✅ Expected: Data reloads
  - ✅ Expected: Timestamp updates to current time
  - ✅ Expected: FAB hides during loading
  - ✅ Expected: FAB reappears after loading
  - ❌ Fail if: Nothing happens or crash

- [ ] **Multiple rapid refreshes**
  - Action: Click Refresh multiple times quickly
  - ✅ Expected: Handles gracefully (no crash)
  - ✅ Expected: Eventually shows data
  - ❌ Fail if: App crashes or freezes

### Error Handling

- [ ] **Error state displays** (simulate by turning off network)
  - Action: Enable airplane mode, then refresh
  - ✅ Expected: Error message appears
  - ✅ Expected: "Retry" button visible
  - ✅ Expected: Error message describes the issue
  - ❌ Fail if: App crashes or shows blank screen

- [ ] **Retry from error works**
  - Action: Re-enable network, click "Retry"
  - ✅ Expected: Loading indicator appears
  - ✅ Expected: Data loads successfully
  - ✅ Expected: Error state clears
  - ❌ Fail if: Stuck in error state

- [ ] **Error toast notification**
  - ✅ Expected: Toast message shows on error
  - ✅ Expected: Message says "Failed to load rates"
  - ❌ Fail if: No feedback given

### List Interaction

- [ ] **List scrolls smoothly**
  - Action: Scroll through currency list
  - ✅ Expected: Smooth scrolling
  - ✅ Expected: All items visible
  - ✅ Expected: No lag or jank
  - ❌ Fail if: Choppy scrolling or items disappear

- [ ] **List shows all currencies**
  - ✅ Expected: Multiple currencies visible (varies by API)
  - ✅ Expected: Each item shows code + rate
  - ✅ Expected: Consistent formatting
  - ❌ Fail if: Missing items or duplicates

---

## Configuration Changes

### Screen Rotation

- [ ] **Rotation preserves state - Currency screen**
  - Action: Rotate device while viewing currency list
  - ✅ Expected: Currency data persists
  - ✅ Expected: No reload/flash
  - ✅ Expected: Scroll position maintained (or reasonable)
  - ✅ Expected: Timestamp unchanged
  - ❌ Fail if: Data lost or screen reloads

- [ ] **Rotation during loading**
  - Action: Start refresh, immediately rotate
  - ✅ Expected: Loading state continues
  - ✅ Expected: Completes successfully
  - ✅ Expected: Data shows after rotation
  - ❌ Fail if: Crash or stuck loading

- [ ] **Rotation on error state**
  - Action: Trigger error, then rotate
  - ✅ Expected: Error message persists
  - ✅ Expected: Retry button still works
  - ❌ Fail if: State lost or crash

- [ ] **Rotation on permission screen**
  - Action: Rotate on permission denied screen
  - ✅ Expected: Permission denied state persists
  - ✅ Expected: Request button still works
  - ❌ Fail if: Crashes or loses state

### Multi-Window / Split Screen

- [ ] **Split screen mode works** (Android 7.0+)
  - Action: Enter split screen mode
  - ✅ Expected: App resizes correctly
  - ✅ Expected: Currency list still readable
  - ✅ Expected: All functionality works
  - ❌ Fail if: Layout breaks or crash

---

## Background/Foreground Transitions

### App Backgrounding

- [ ] **Background and restore**
  - Action: Press Home, wait 5 seconds, return to app
  - ✅ Expected: State preserved
  - ✅ Expected: Currency data still visible
  - ✅ Expected: No reload
  - ❌ Fail if: Data lost or crash

- [ ] **Long background (process death simulation)**
  - Action: Background app, open several other apps, return
  - ✅ Expected: App restores (may reload data)
  - ✅ Expected: No crash
  - ✅ Expected: Returns to appropriate screen
  - ❌ Fail if: Crash on restore

- [ ] **Background during loading**
  - Action: Start refresh, immediately background app
  - ✅ Expected: Loading completes in background
  - ✅ Expected: Data visible when returning
  - ❌ Fail if: Stuck loading or crash

---

## Memory and Performance

### Memory Usage

- [ ] **No memory leaks**
  - Action: Rotate 10+ times, refresh multiple times
  - ✅ Expected: App remains responsive
  - ✅ Expected: No crashes
  - ✅ Expected: Memory usage stable (use Android Profiler if available)
  - ❌ Fail if: App becomes sluggish or crashes

- [ ] **Multiple refresh cycles**
  - Action: Refresh 20+ times
  - ✅ Expected: App remains responsive
  - ✅ Expected: Each refresh works correctly
  - ❌ Fail if: Performance degrades or crash

### Performance

- [ ] **Smooth scrolling**
  - ✅ Expected: 60 FPS scrolling (visual check)
  - ✅ Expected: No stuttering
  - ❌ Fail if: Visible lag or frame drops

- [ ] **Fast state transitions**
  - ✅ Expected: State changes are immediate
  - ✅ Expected: Loading → Success transition smooth
  - ✅ Expected: No visible delays
  - ❌ Fail if: UI updates lag behind state

- [ ] **No ANRs (Application Not Responding)**
  - ✅ Expected: No ANR dialogs during testing
  - ✅ Expected: UI remains responsive
  - ❌ Fail if: ANR occurs

---

## UI/UX Verification

### Material 3 Theme

- [ ] **Material 3 components render correctly**
  - ✅ Expected: Material 3 TopAppBar
  - ✅ Expected: Material 3 Cards for list items
  - ✅ Expected: Material 3 FAB
  - ✅ Expected: Material 3 color scheme
  - ❌ Fail if: Using old Material Design

- [ ] **Dark mode support** (if implemented)
  - Action: Toggle system dark mode
  - ✅ Expected: Theme changes appropriately
  - ✅ Expected: Colors readable in both modes
  - ❌ Fail if: Theme doesn't adapt

### Typography and Spacing

- [ ] **Text is readable**
  - ✅ Expected: All text clearly readable
  - ✅ Expected: Good contrast
  - ✅ Expected: Appropriate font sizes
  - ❌ Fail if: Text too small or hard to read

- [ ] **Spacing is consistent**
  - ✅ Expected: Consistent padding/margins
  - ✅ Expected: Items properly aligned
  - ✅ Expected: No overlapping elements
  - ❌ Fail if: Layout issues

### Accessibility

- [ ] **Touch targets are adequate**
  - ✅ Expected: Buttons at least 48dp
  - ✅ Expected: Easy to tap without mistakes
  - ❌ Fail if: Touch targets too small

- [ ] **Content descriptions present**
  - ✅ Expected: FAB has "Refresh" description
  - ✅ Expected: Back button has "Navigate up" description
  - ❌ Fail if: Missing content descriptions

---

## Edge Cases

### Network Conditions

- [ ] **Slow network**
  - Action: Use network throttling or slow connection
  - ✅ Expected: Loading indicator shows
  - ✅ Expected: Eventually loads or times out
  - ✅ Expected: Proper error if timeout
  - ❌ Fail if: Hangs indefinitely

- [ ] **No network**
  - Action: Airplane mode enabled
  - ✅ Expected: Error message clear
  - ✅ Expected: Retry available
  - ❌ Fail if: Unclear error or crash

- [ ] **Network restored**
  - Action: Turn off airplane mode, retry
  - ✅ Expected: Data loads successfully
  - ✅ Expected: Error state clears
  - ❌ Fail if: Doesn't recover

### Data Edge Cases

- [ ] **Empty data response** (if API can return empty)
  - ✅ Expected: Handles gracefully
  - ✅ Expected: Shows appropriate message
  - ❌ Fail if: Crash or blank screen

- [ ] **Malformed data** (difficult to simulate)
  - ✅ Expected: Error handling catches issues
  - ✅ Expected: User-friendly error message
  - ❌ Fail if: Crash

---

## Logs and Debugging

### Console Output

- [ ] **Check Logcat for errors**
  ```bash
  adb logcat | grep -E "(ERROR|FATAL|CurrencyViewModel|MainViewModel)"
  ```
  - ✅ Expected: No unexpected errors
  - ✅ Expected: Clear log messages for state changes
  - ❌ Fail if: Errors or exceptions present

- [ ] **ViewModel logs appear**
  - ✅ Expected: "Loading currency rates..." logs
  - ✅ Expected: "Rates loaded successfully" logs
  - ✅ Expected: State transition logs
  - ❌ Fail if: No logs (indicates ViewModel not being used)

- [ ] **No deprecated warnings at runtime**
  - ✅ Expected: MVP components not instantiated (if not used)
  - ⚠️ Warning: Deprecation warnings at compile time are OK
  - ❌ Fail if: Runtime errors related to deprecated code

---

## Comparison with MVP (Regression Testing)

### Feature Parity

- [ ] **All MVP features work in TOAD**
  - ✅ Expected: Permission handling works
  - ✅ Expected: Currency loading works
  - ✅ Expected: Error handling works
  - ✅ Expected: Refresh works
  - ❌ Fail if: Any feature missing or broken

- [ ] **New TOAD features work**
  - ✅ Expected: Timestamp displays
  - ✅ Expected: Refresh FAB works
  - ✅ Expected: Material 3 styling applied
  - ❌ Fail if: New features don't work

### Improvements Verified

- [ ] **State survives rotation** (improvement over MVP)
  - ✅ Expected: Data persists on rotation
  - ✅ Expected: Better than MVP (which lost state)
  - ❌ Fail if: State still lost

- [ ] **No manual lifecycle management needed**
  - ✅ Expected: No onDestroy cleanup needed
  - ✅ Expected: ViewModel handles lifecycle
  - ❌ Fail if: Memory leaks occur

---

## Final Verification

### Overall App Health

- [ ] **No crashes during entire test session**
  - ✅ Expected: Zero crashes
  - ❌ Fail if: Any crash occurred

- [ ] **All features tested and working**
  - ✅ Expected: 100% checklist completion
  - ❌ Fail if: Any critical failures

- [ ] **Performance acceptable**
  - ✅ Expected: Smooth, responsive UI
  - ✅ Expected: Fast state transitions
  - ❌ Fail if: Laggy or slow

- [ ] **No console errors**
  - ✅ Expected: Clean logcat (except expected logs)
  - ❌ Fail if: Errors present

### Documentation

- [ ] **MIGRATION_M6.md reflects reality**
  - ✅ Expected: All documented changes work as described
  - ❌ Fail if: Documentation inaccurate

- [ ] **Tests match implementation**
  - ✅ Expected: Unit tests cover actual functionality
  - ✅ Expected: UI tests verify actual screens
  - ❌ Fail if: Tests don't match code

---

## Sign-Off

### Test Results Summary

**Date Tested:** _______________

**Tested By:** _______________

**Device/Emulator:** _______________

**Android Version:** _______________

**Build Variant:** _______________

**Total Checks:** ~90

**Passed:** _____ / 90

**Failed:** _____ / 90

**Critical Issues:** _______________

**Non-Critical Issues:** _______________

### Overall Status

- [ ] ✅ **PASS** - Ready for production / Milestone 6 complete
- [ ] ⚠️ **PASS WITH WARNINGS** - Minor issues, can proceed
- [ ] ❌ **FAIL** - Critical issues must be fixed

### Notes

```
[Add any additional observations, issues, or comments here]









```

---

## Next Steps

### If Tests Pass
1. ✅ Mark Milestone 6 as complete
2. ✅ Proceed to Milestone 7 (remove deprecated MVP code)
3. ✅ Plan Compose Navigation migration
4. ✅ Consider performance optimizations

### If Tests Fail
1. ❌ Document all failures
2. ❌ Prioritize critical bugs
3. ❌ Fix issues and re-test
4. ❌ Update code and documentation as needed

---

**Testing completed and verified successfully indicates Milestone 6 is ready for production.**
