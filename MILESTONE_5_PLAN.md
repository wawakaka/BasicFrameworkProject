# Milestone 5: Jetpack Compose UI Migration

**Status:** 📋 Planned
**Target:** Migrate all legacy XML layouts to Jetpack Compose
**Architecture:** MVP (temporary) → TOAD (M6)

---

## Overview

Migrate the entire UI layer from traditional Activity/Fragment + XML layouts + ViewBinding to Jetpack Compose. This prepares the codebase for Milestone 6's architecture evolution (MVP → TOAD).

### Key Principles
- **Skip ViewBinding** - Not needed, we're going directly to Compose
- **Keep MVP for now** - Presenters stay until M6
- **One screen at a time** - Incremental migration reduces risk
- **Preserve business logic** - Only UI layer changes
- **Add Compose Previews** - Enable rapid UI iteration

---

## Dependencies to Add

### Step 1: Update gradle/libs.versions.toml

Add Compose version:

```toml
[versions]
# ... existing versions ...

# Jetpack Compose
compose = "1.6.0"
composeMaterial3 = "1.1.2"
composeActivity = "1.8.1"
composeNavigation = "2.7.6"

[libraries]
# ... existing libraries ...

# Jetpack Compose BOM
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose" }

# Compose Core
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "composeMaterial3" }
compose-foundation = { group = "androidx.compose.foundation", name = "foundation" }
compose-runtime = { group = "androidx.compose.runtime", name = "runtime" }

# Compose Activity Integration
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "composeActivity" }

# Compose Navigation
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "composeNavigation" }

# Compose Lifecycle
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "androidxLifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "androidxLifecycle" }
```

### Step 2: Update app/build.gradle

```kotlin
plugins {
    id "com.android.application"
    id "org.jetbrains.kotlin.android"
}

android {
    // ... existing config ...

    buildFeatures {
        compose = true
        viewBinding = false  // Disable ViewBinding, we're using Compose now
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"  // Matches Kotlin 2.0.21 compatible version
    }
}

dependencies {
    // Existing dependencies...

    // Compose BOM
    implementation platform(libs.compose.bom)

    // Compose UI
    implementation libs.compose.ui
    implementation libs.compose.ui.graphics
    implementation libs.compose.ui.tooling.preview
    implementation libs.compose.foundation
    implementation libs.compose.runtime

    // Compose Material 3
    implementation libs.compose.material3

    // Compose Activity Integration
    implementation libs.androidx.activity.compose

    // Compose Navigation
    implementation libs.androidx.navigation.compose

    // Compose Lifecycle
    implementation libs.androidx.lifecycle.viewmodel.compose
    implementation libs.androidx.lifecycle.runtime.compose

    // Debug tooling
    debugImplementation libs.compose.ui.tooling

    // Remove ViewBinding (no longer needed)
    // Remove: libs.androidx.constraintlayout (replaced by Compose layouts)
}
```

---

## Migration Tasks

### Phase 1: Setup & Infrastructure (1-2 hours)

#### Task 1.1: Create Compose UI Module Structure

**Create new package structure:**
```
app/src/main/java/io/github/wawakaka/basicframeworkproject/
├── presentation/
│   ├── ui/  (NEW - Compose composables)
│   │   ├── theme/
│   │   │   ├── Color.kt (NEW)
│   │   │   ├── Type.kt (NEW)
│   │   │   └── Theme.kt (NEW)
│   │   ├── screens/  (NEW - full screen composables)
│   │   │   ├── MainScreen.kt
│   │   │   └── CurrencyScreen.kt
│   │   └── components/  (NEW - reusable composables)
│   │       ├── CurrencyListItem.kt
│   │       ├── LoadingIndicator.kt
│   │       └── ErrorMessage.kt
│   └── ... (existing presenters, adapters)
```

**Checklist:**
- [ ] Create `/ui/theme` directory
- [ ] Create `/ui/screens` directory
- [ ] Create `/ui/components` directory

#### Task 1.2: Setup Compose Theme

**File: `app/src/main/java/.../ui/theme/Color.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF6200EE)
val PrimaryVariant = Color(0xFF3700B3)
val Secondary = Color(0xFF03DAC6)
val SecondaryVariant = Color(0xFF018786)
val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFFFFFFF)
val Error = Color(0xFFB00020)
val OnPrimary = Color.White
val OnSecondary = Color.Black
val OnBackground = Color.Black
val OnSurface = Color.Black
val OnError = Color.White
```

**File: `app/src/main/java/.../ui/theme/Type.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)
```

**File: `app/src/main/java/.../ui/theme/Theme.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    error = Error,
    onError = OnError
)

@Composable
fun BasicFrameworkTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
```

**Checklist:**
- [ ] Create Color.kt
- [ ] Create Type.kt
- [ ] Create Theme.kt
- [ ] Verify color scheme matches Material 3

#### Task 1.3: Create Compose Preview Utilities

**File: `app/src/main/java/.../ui/PreviewUtils.kt`** (optional, for better previews)
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class PreviewLight

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
annotation class PreviewPhone

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
annotation class PreviewLargePhone
```

**Checklist:**
- [ ] Create PreviewUtils.kt
- [ ] Ready for use in all composables

---

### Phase 2: Component Layer (2-3 hours)

Create reusable, composable UI components that match current design.

#### Task 2.1: Toolbar Component

**File: `app/src/main/java/.../ui/components/AppTopBar.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showUpButton: Boolean = false,
    onUpButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = if (showUpButton) {
            {
                IconButton(onClick = onUpButtonClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            null
        },
        modifier = modifier
    )
}
```

**Checklist:**
- [ ] Create AppTopBar composable
- [ ] Test with Compose Preview

#### Task 2.2: Loading & Error Components

**File: `app/src/main/java/.../ui/components/LoadingAndError.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message)
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
```

**Checklist:**
- [ ] Create LoadingIndicator
- [ ] Create ErrorMessage
- [ ] Add retry button functionality

#### Task 2.3: Currency List Item Component

**File: `app/src/main/java/.../ui/components/CurrencyListItem.kt`**
```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CurrencyListItem(
    currencyCode: String,
    rate: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currencyCode,
                fontSize = 16.sp
            )
            Text(
                text = String.format("%.4f", rate),
                fontSize = 16.sp
            )
        }
    }
}
```

**Checklist:**
- [ ] Create CurrencyListItem
- [ ] Verify layout matches existing design
- [ ] Add Compose Preview

---

### Phase 3: Screen Layer (3-4 hours)

#### Task 3.1: Create Compose Activity Setup

**File: `app/src/main/java/.../presentation/MainActivity.kt`** (REWRITE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope

class MainActivity : AppCompatActivity(), FragmentActivityCallbacks, MainContract.View {

    private val scope: Scope by lazy {
        GlobalContext.get().createScope("MainActivity-${this.hashCode()}")
    }
    private val presenter: MainPresenter by scope.inject()

    // Register permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        presenter.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BasicFrameworkTheme {
                MainScreen(
                    presenter = presenter,
                    view = this
                )
            }
        }

        presenter.attach(this)
        presenter.checkPermission()
    }

    override fun onDestroy() {
        presenter.detach()
        scope.close()
        super.onDestroy()
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        // Toolbar is now part of Compose hierarchy
        // Handled in MainScreen composable
    }

    override fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                presenter.onPermissionResult(granted = true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationaleDialog()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        // TODO: Material 3 AlertDialog for permission rationale
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onPermissionGranted() {
        Log.d(TAG, "Camera permission granted")
    }

    override fun onPermissionDenied() {
        Log.d(TAG, "Camera permission denied")
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}

@Composable
private fun MainScreen(
    presenter: MainPresenter,
    view: MainContract.View,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Currency Rates",
                showUpButton = false
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Navigation to CurrencyFragment happens here
            // For now, embed CurrencyScreen
            CurrencyListScreen(presenter)
        }
    }
}
```

**Checklist:**
- [ ] Rewrite MainActivity with setContent
- [ ] Remove ViewBinding completely
- [ ] Remove activity_main.xml
- [ ] Add MainScreen composable

#### Task 3.2: Create Currency List Screen

**File: `app/src/main/java/.../ui/screens/CurrencyScreen.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.wawakaka.basicframeworkproject.presentation.CurrencyContract
import io.github.wawakaka.basicframeworkproject.presentation.CurrencyPresenter
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.CurrencyListItem
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.ErrorMessage
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.LoadingIndicator

@Composable
fun CurrencyListScreen(
    presenter: CurrencyPresenter,
    modifier: Modifier = Modifier
) {
    // TODO: Connect to presenter state
    // For MVP transition period, presenter will call view callbacks

    Box(modifier = modifier.fillMaxSize()) {
        // Placeholder - will be updated in next task
        LoadingIndicator()
    }
}

@Composable
fun CurrencyListContent(
    currencies: List<Pair<String, Double>>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(currencies) { (code, rate) ->
            CurrencyListItem(
                currencyCode = code,
                rate = rate
            )
        }
    }
}
```

**Checklist:**
- [ ] Create CurrencyListScreen composable
- [ ] Create LazyColumn with items
- [ ] Remove fragment_currency.xml
- [ ] Remove CurrencyListAdapter/ViewHolder files (not needed)

#### Task 3.3: Update CurrencyFragment to Use Compose

**File: `app/src/main/java/.../presentation/content/CurrencyFragment.kt`** (REWRITE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyListContent
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyListScreen
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope

class CurrencyFragment : Fragment(), CurrencyContract.View {

    private val scope: Scope by lazy {
        GlobalContext.get().createScope("CurrencyFragment-${this.hashCode()}")
    }
    private val presenter: CurrencyPresenter by scope.inject()

    // State for UI updates from presenter
    private var uiState by mutableStateOf<CurrencyUiState>(CurrencyUiState.Loading)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setBackgroundColor(android.graphics.Color.WHITE)
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            BasicFrameworkTheme {
                when (val state = uiState) {
                    is CurrencyUiState.Loading -> {
                        LoadingIndicator()
                    }
                    is CurrencyUiState.Success -> {
                        CurrencyListContent(currencies = state.rates)
                    }
                    is CurrencyUiState.Error -> {
                        ErrorMessage(
                            message = state.message,
                            onRetry = { presenter.loadCurrencies() }
                        )
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        presenter.loadCurrencies()
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun onDestroy() {
        scope.close()
        super.onDestroy()
    }

    // CurrencyContract.View implementation
    override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
        uiState = CurrencyUiState.Success(data)
    }

    override fun onGetDataFailed(throwable: Throwable) {
        uiState = CurrencyUiState.Error(throwable.message ?: "Unknown error")
    }

    companion object {
        val TAG: String? = CurrencyFragment::class.java.canonicalName
    }
}

// Temporary state model (will be replaced by TOAD in M6)
sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    data class Success(val rates: List<Pair<String, Double>>) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}
```

**Checklist:**
- [ ] Convert Fragment to use ComposeView
- [ ] Implement mutableState for UI updates
- [ ] Keep presenter callbacks for MVP
- [ ] Remove fragment_currency.xml

---

### Phase 4: Navigation & Integration (2-3 hours)

#### Task 4.1: Update Navigation Graph

**File: `app/src/main/res/navigation/navigation_graph.xml`**

Replace or update navigation to work with Compose. For now, keep Fragment-based if using Compose in Fragments.

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/currencyFragment">

    <fragment
        android:id="@+id/currencyFragment"
        android:name="io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyFragment"
        android:label="Currency Rates" />

</navigation>
```

**Checklist:**
- [ ] Verify navigation compiles
- [ ] Test Fragment navigation works
- [ ] Plan M6 for Compose Navigation migration

#### Task 4.2: Update Layout Files to Remove ViewBinding References

**Changes needed:**
- [ ] Remove all `databinding { enabled = true }` from build.gradle
- [ ] Remove all ViewBinding imports from Activities/Fragments
- [ ] Delete all XML layout files:
  - [ ] activity_main.xml
  - [ ] fragment_currency.xml
  - [ ] Any other layout XMLs

#### Task 4.3: Update Build Configuration

**File: `app/build.gradle`**

```kotlin
android {
    // ... existing config ...

    buildFeatures {
        compose = true
        viewBinding = false  // Explicitly disable
        dataBinding = false  // Explicitly disable
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
}
```

**Checklist:**
- [ ] Update buildFeatures
- [ ] Update composeOptions
- [ ] Remove ViewBinding from dependencies
- [ ] Run `./gradlew clean build` to verify

---

### Phase 5: Testing & Cleanup (2-3 hours)

#### Task 5.1: Update Unit Tests

**Current state:** Some tests may reference ViewBinding or Presenter lifecycle

**Actions:**
- [ ] Update any tests that reference `ActivityMainBinding`
- [ ] Ensure Presenter tests still work (they should)
- [ ] No UI tests needed yet (Compose testing comes in M7)

#### Task 5.2: Verify App Runs

**Test plan:**
1. [ ] Build app: `./gradlew clean build`
2. [ ] Install to device: `./gradlew installDebug`
3. [ ] Test permission flow works
4. [ ] Test currency list loads and displays
5. [ ] Test error handling (load bad request)
6. [ ] Test retry button

#### Task 5.3: Code Cleanup

**Remove:**
- [ ] All XML layout files (activity_main.xml, fragment_currency.xml)
- [ ] CurrencyListAdapter.kt (no longer needed, using LazyColumn)
- [ ] CurrencyListViewHolder.kt (no longer needed)
- [ ] Any ViewBinding-related code

**Keep:**
- [ ] All Presenters (for now)
- [ ] All Contracts (for now)
- [ ] Domain layer (use cases)
- [ ] Repository layer

**Checklist:**
- [ ] Remove adapter/viewholder
- [ ] Delete layout XMLs
- [ ] Run `./gradlew build` one more time
- [ ] Verify no compilation errors

#### Task 5.4: Update Documentation

**Files to update:**
- [ ] CLAUDE.md - Update UI section to reference Compose
- [ ] Add Compose code examples to CLAUDE.md
- [ ] Create COMPOSE_PATTERNS.md (new file with Compose best practices)

---

## Testing Checklist

### Build Tests
```bash
# Clean build
./gradlew clean build

# Verify no warnings
./gradlew build --warning-mode all

# Run unit tests (if any)
./gradlew test
```

### Runtime Tests
- [ ] App launches without crashes
- [ ] Permission request dialog appears
- [ ] Grant permission → currency list loads
- [ ] Deny permission → app handles gracefully
- [ ] Currency list displays correctly
- [ ] Scroll through list works smoothly
- [ ] Error state displays error message
- [ ] Retry button works
- [ ] Configuration change preserves state

### Visual Tests
- [ ] Toolbar displays correctly
- [ ] Currency list items styled properly
- [ ] Loading spinner visible
- [ ] Error message readable
- [ ] Layout responsive on different screen sizes
- [ ] Material 3 colors applied correctly

---

## Deliverables

### Code Changes
- [x] New Compose files:
  - `/ui/theme/Color.kt`
  - `/ui/theme/Type.kt`
  - `/ui/theme/Theme.kt`
  - `/ui/components/AppTopBar.kt`
  - `/ui/components/LoadingAndError.kt`
  - `/ui/components/CurrencyListItem.kt`
  - `/ui/screens/CurrencyScreen.kt`

- [x] Modified Files:
  - `gradle/libs.versions.toml` (add Compose deps)
  - `app/build.gradle` (add Compose, disable ViewBinding)
  - `MainActivity.kt` (rewrite for Compose)
  - `CurrencyFragment.kt` (rewrite for Compose)

- [x] Deleted Files:
  - `activity_main.xml`
  - `fragment_currency.xml`
  - `CurrencyListAdapter.kt`
  - `CurrencyListViewHolder.kt`

### Documentation
- [x] Update CLAUDE.md with Compose examples
- [x] Create COMPOSE_PATTERNS.md
- [x] Create MILESTONE_5_SUMMARY.md (after completion)

### PR Description

```markdown
## Milestone 5: Jetpack Compose UI Migration

### Summary
Migrated entire UI layer from traditional Activities/Fragments + XML layouts to Jetpack Compose.

### What Changed
- ✅ All Activities/Fragments now use Compose
- ✅ Removed all XML layout files
- ✅ Removed ViewBinding (skipped intermediate step)
- ✅ Created reusable Compose components
- ✅ Added Material 3 theme with Compose
- ✅ Replaced RecyclerView with LazyColumn

### What Stayed
- ✅ MVP pattern (Presenter/Contract/View)
- ✅ Dependency injection (Koin)
- ✅ Coroutines for async operations
- ✅ Domain/Repository layers

### Next Milestone (M6)
Ready to replace MVP with TOAD architecture:
- ViewModel + StateFlow instead of Presenter
- UiState/UiEvent sealed classes
- Simplified state management
- Better Compose integration

### Testing
- ✅ App builds without errors
- ✅ All permissions work
- ✅ Currency list loads and displays
- ✅ Error handling works
- ✅ Navigation functions properly

Co-Authored-By: Claude Haiku 4.5 <noreply@anthropic.com>
```

---

## Migration Notes

### Known Limitations During M5

1. **State Management**: Still using Presenter callbacks
   - Will be replaced by StateFlow in M6
   - Recomposition happens via `mutableStateOf`
   - Not optimal but maintains MVP during transition

2. **Navigation**: Still using Fragment-based navigation
   - Will switch to Compose Navigation in M6
   - Current approach works but verbose

3. **Testing**: No Compose UI tests yet
   - Will add in M7 (comprehensive testing)
   - Presenter unit tests still valid

### Benefits of M5

1. **Modern UI Framework**
   - Single language (Kotlin, no XML)
   - Composable components
   - Better for Compose-first architecture

2. **Development Speed**
   - Compose Previews for rapid iteration
   - Less boilerplate than XML + ViewBinding
   - Easier to reason about UI

3. **Foundation for M6**
   - Compose integrates naturally with StateFlow/ViewModel
   - Easy transition to TOAD pattern
   - Cleaner separation from business logic

---

**Ready to start implementing Milestone 5?**

Next: Create detailed subtasks for each phase and begin with Phase 1 (Setup & Infrastructure).
