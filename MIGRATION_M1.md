# Milestone 1: Library Updates & Version Catalog Migration

## Summary

This milestone modernizes the project's build configuration and dependencies:
- **Gradle:** 6.1.1 → 8.5
- **AGP:** 7.0.0 → 8.2.2
- **Kotlin:** 1.3.72 → 1.9.22
- **Introduced:** Gradle Version Catalog
- **Removed:** jcenter(), kotlin-android-extensions, dependencies.gradle
- **Updated:** All AndroidX and third-party libraries to latest compatible versions

---

## Changes Made

### 1. Version Catalog (`gradle/libs.versions.toml`)

Created a centralized dependency management file using Gradle Version Catalog:
- All dependency versions are now defined in `[versions]` section
- Libraries are declared in `[libraries]` section with proper grouping
- Plugins are declared in `[plugins]` section
- Accessible via `libs.*` in build.gradle files

**Benefits:**
- Type-safe dependency accessors
- IDE auto-completion support
- Centralized version management
- Better dependency conflict resolution

### 2. Gradle Wrapper Update

**File:** `gradle/wrapper/gradle-wrapper.properties`
- Updated distribution URL from `gradle-6.1.1-all.zip` to `gradle-8.5-all.zip`

### 3. Gradle Properties Enhancement

**File:** `gradle.properties`

**Removed:**
- `-XX:MaxPermSize=2G` (deprecated in Java 8+)

**Added:**
- `org.gradle.caching=true` - Enable build cache
- `kotlin.code.style=official` - Official Kotlin code style
- `android.nonTransitiveRClass=true` - Faster build times with R classes
- `org.gradle.configuration-cache=false` - Disable for compatibility during migration

### 4. Root Build Configuration

**File:** `build.gradle`

**Changes:**
- Removed `jcenter()` from repositories (deprecated)
- Added `mavenCentral()` as primary Maven repository
- Updated AGP classpath to 8.2.2
- Updated Kotlin Gradle Plugin to 1.9.22
- Removed `dependencies.gradle` dependency
- Cleaned up duplicate kotlin_version variables

### 5. App Module Modernization

**File:** `app/build.gradle`

**Major Changes:**
- Converted from `apply plugin` to `plugins {}` block
- Removed `kotlin-android-extensions` (deprecated)
- Added `viewBinding true` as replacement
- Added `namespace` declaration (required for AGP 8.x)
- Migrated all dependencies to use `libs.*` version catalog
- Added JDK 17 compatibility:
  - `sourceCompatibility JavaVersion.VERSION_17`
  - `targetCompatibility JavaVersion.VERSION_17`
  - `jvmTarget = '17'` in kotlinOptions
- Updated SDK versions:
  - `compileSdk 34`
  - `targetSdk 34`
  - `minSdk 26` (unchanged)

### 6. Library Modules Modernization

**Files:**
- `dep/build-lib.gradle` (shared configuration)
- `domain/build.gradle`
- `repository/build.gradle`
- `restapi/build.gradle`

**Changes:**
- Converted to `plugins {}` block syntax
- Removed `kotlin-android-extensions`
- Added namespace declarations:
  - domain: `io.github.wawakaka.domain`
  - repository: `io.github.wawakaka.repository`
  - restapi: `io.github.wawakaka.restapi`
- Migrated all dependencies to version catalog
- Added JDK 17 compatibility
- Removed references to deprecated variables (projectCompileSdkVersion, etc.)

### 7. AndroidManifest Updates

**Files:**
- `app/src/main/AndroidManifest.xml`
- `domain/src/main/AndroidManifest.xml`
- `repository/src/main/AndroidManifest.xml`
- `restapi/src/main/AndroidManifest.xml`

**Changes:**
- Removed `package` attribute (now defined as `namespace` in build.gradle)
- This is a requirement for AGP 8.x

---

## Dependency Version Updates

### Android/AndroidX
- **compileSdk & targetSdk:** 29 → 34
- **AndroidX Core KTX:** 1.3.0 → 1.12.0
- **AppCompat:** 1.1.0 → 1.6.1
- **ConstraintLayout:** 1.1.3 → 2.1.4
- **Navigation:** 2.3.0 → 2.7.6
- **Material Design:** 1.1.0 → 1.11.0

### Reactive Programming
- **RxJava:** 2.2.10 → 2.2.21 (final RxJava 2.x version)
- **RxKotlin:** 2.3.0 → 2.4.0
- **RxAndroid:** 2.1.1 → 2.1.1 (unchanged - no newer version)
- **RxBinding:** 3.1.0 → 3.1.0 (unchanged)
- **RxPermissions:** 0.10.2 → 0.12

### Networking
- **Retrofit:** 2.9.0 → 2.9.0 (unchanged - latest stable)
- **OkHttp:** 4.7.2 → 4.12.0
- **Gson:** 2.8.6 → 2.10.1

### Dependency Injection
- **Koin:** 2.0.1 → 3.5.3
  - **Breaking Changes:** API updates in Koin 3.x
  - Updated package names in imports (may need code adjustments)

### Testing
- **JUnit:** 4.13 → 4.13.2

---

## Breaking Changes & Migration Notes

### 1. Kotlin Synthetics Removal

**Old Way:**
```kotlin
import kotlinx.android.synthetic.main.fragment_currency.*

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    textView.text = "Hello" // Direct view access
}
```

**New Way (ViewBinding):**
```kotlin
private var _binding: FragmentCurrencyBinding? = null
private val binding get() = _binding!!

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    _binding = FragmentCurrencyBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.textView.text = "Hello"
}

override fun onDestroyView() {
    _binding = null
    super.onDestroyView()
}
```

### 2. Koin 3.x API Changes

**Old Way (Koin 2.x):**
```kotlin
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.currentScope
```

**New Way (Koin 3.x):**
```kotlin
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.createActivityScope
```

Module definition changes:
```kotlin
// Old (Koin 2.x)
val myModule = module {
    scope<MyActivity> {
        scoped { MyPresenter(get()) }
    }
}

// New (Koin 3.x) - mostly compatible, but check API docs for advanced usage
val myModule = module {
    scope<MyActivity> {
        scoped { MyPresenter(get()) }
    }
}
```

### 3. JDK 17 Requirement

AGP 8.2.2 requires **JDK 17** to build. Ensure your development environment has JDK 17 installed:

```bash
# Check Java version
java -version

# Should show: openjdk version "17.x.x" or higher
```

### 4. Namespace Declaration

All modules now require `namespace` in `build.gradle` instead of `package` in `AndroidManifest.xml`.

---

## Post-Migration Tasks

### Immediate (Required)

1. **Update Code for ViewBinding:**
   - Replace all Kotlin Synthetics imports
   - Implement ViewBinding pattern in all Activities and Fragments
   - Update affected files:
     - `MainActivity.kt`
     - `CurrencyFragment.kt`
     - `CurrencyListViewHolder.kt`

2. **Update Koin Usage:**
   - Check all Koin imports and update to Koin 3.x packages
   - Verify DI module definitions still work
   - Test app startup and injection

3. **Test Build:**
   - Run `./gradlew clean build` to verify compilation
   - Fix any remaining compilation errors
   - Run tests: `./gradlew test`

### Future Considerations

1. **RxJava to Coroutines Migration (Milestone 3)**
   - Current versions are final RxJava 2.x releases
   - Consider migrating to Kotlin Coroutines + Flow in next milestone

2. **ViewBinding to Compose (Milestone 2)**
   - ViewBinding is a stepping stone
   - Plan migration to Jetpack Compose for fully declarative UI

3. **Remove Deprecated Libraries:**
   - Anko (0.10.8) - No longer maintained, consider removing
   - Chuck (network inspector) - Consider migrating to Chucker

---

## Files Modified

### Created:
- `gradle/libs.versions.toml` - Version catalog

### Modified:
- `gradle/wrapper/gradle-wrapper.properties` - Gradle 8.5
- `gradle.properties` - Enhanced properties
- `build.gradle` - Root configuration
- `app/build.gradle` - App module configuration
- `dep/build-lib.gradle` - Shared library configuration
- `domain/build.gradle` - Domain module
- `repository/build.gradle` - Repository module
- `restapi/build.gradle` - REST API module
- `app/src/main/AndroidManifest.xml` - Removed package
- `domain/src/main/AndroidManifest.xml` - Removed package
- `repository/src/main/AndroidManifest.xml` - Removed package
- `restapi/src/main/AndroidManifest.xml` - Removed package

### To Be Removed (no longer needed):
- `dependencies.gradle` - Replaced by version catalog

---

## Verification Steps

1. **Clean build:**
   ```bash
   ./gradlew clean
   ```

2. **Build all modules:**
   ```bash
   ./gradlew build
   ```

3. **Run tests:**
   ```bash
   ./gradlew test
   ```

4. **Install debug build:**
   ```bash
   ./gradlew installDebug
   ```

5. **Verify app functionality:**
   - Launch app
   - Test currency rates feature
   - Verify permissions work correctly

---

## Rollback Plan

If issues arise, you can rollback by:

1. **Revert to previous commit:**
   ```bash
   git revert HEAD
   ```

2. **Or checkout previous version:**
   ```bash
   git checkout <previous-commit-hash>
   ```

3. **Known stable commit:** Before Milestone 1 migration

---

## Next Steps

Once Milestone 1 is complete and verified:

1. Update CLAUDE.md with new versions and build configuration
2. Begin Milestone 2: XML to Jetpack Compose migration
3. Plan Milestone 3: RxJava to Coroutines migration

---

**Migration Completed:** 2026-01-10
**Status:** Code changes complete, awaiting build verification
