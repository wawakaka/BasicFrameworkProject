# Milestone 6: Architecture Evolution (MVP → TOAD)

**Status:** 📋 Planned
**Target:** Replace MVP pattern with TOAD (The Opinionated Android Design)
**Dependencies:** Requires completion of Milestone 5 (Compose migration)

---

## Overview

Evolve from MVP (Presenter-based) architecture to TOAD pattern. TOAD combines modern Android patterns with clear state management, making it ideal for Compose-based UIs and complex screen logic.

### Why TOAD?

| Aspect | MVP | TOAD |
|--------|-----|------|
| **State Management** | Presenter (mutable) | StateFlow (immutable) |
| **View Updates** | Callbacks | Recomposition |
| **User Events** | Method calls | Sealed events |
| **Side Effects** | Optional callbacks | Explicit effects channel |
| **Testing** | Presenter tests | Reducer + state tests |
| **Compose Fit** | Requires workarounds | Natural fit |

---

## TOAD Architecture Pattern

### Core Components

```
┌─────────────────────────────────────┐
│  UI Layer (Compose)                 │
│  - Reads: State via StateFlow        │
│  - Emits: Events via callback        │
└────────────┬────────────────────────┘
             │ handleEvent(event)
             │
┌────────────▼────────────────────────┐
│  ViewModel (State Holder)            │
│  ├─ StateFlow<UiState>              │
│  ├─ fun handleEvent(UiEvent)        │
│  └─ Channel<UiEffect>               │
└────────────┬────────────────────────┘
             │ collectAsState/LaunchedEffect
             │
┌────────────▼────────────────────────┐
│  Domain Layer                        │
│  ├─ UseCases (suspend functions)    │
│  └─ Pure business logic             │
└────────────┬────────────────────────┘
             │
┌────────────▼────────────────────────┐
│  Data Layer                         │
│  ├─ Repository                      │
│  └─ Network/Database                │
└─────────────────────────────────────┘
```

### State-Event-Effect Pattern

#### UiState (Sealed Class)
Represents all possible UI states:

```kotlin
sealed class CurrencyUiState {
    object Idle : CurrencyUiState()
    object Loading : CurrencyUiState()
    data class Success(val rates: List<Pair<String, Double>>) : CurrencyUiState()
    data class Error(val message: String, val canRetry: Boolean = true) : CurrencyUiState()
}
```

#### UiEvent (Sealed Class)
Represents all user interactions:

```kotlin
sealed class CurrencyUiEvent {
    object OnLoadRates : CurrencyUiEvent()
    object OnRetry : CurrencyUiEvent()
}
```

#### UiEffect (Sealed Class)
Represents one-time side effects:

```kotlin
sealed class CurrencyUiEffect {
    data class ShowToast(val message: String) : CurrencyUiEffect()
    object NavigateToSettings : CurrencyUiEffect()
}
```

#### ViewModel
Manages state and handles events:

```kotlin
class CurrencyViewModel(
    private val getRatesUseCase: GetLatestRatesUsecase
) : ViewModel() {

    private val _state = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Idle)
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    private val _effect = Channel<CurrencyUiEffect>()
    val effect: Flow<CurrencyUiEffect> = _effect.receiveAsFlow()

    fun handleEvent(event: CurrencyUiEvent) {
        when (event) {
            is CurrencyUiEvent.OnLoadRates -> loadRates()
            is CurrencyUiEvent.OnRetry -> loadRates()
        }
    }

    private fun loadRates() {
        viewModelScope.launch {
            _state.value = CurrencyUiState.Loading
            try {
                val rates = getRatesUseCase.getLatestCurrencyRates()
                _state.value = CurrencyUiState.Success(rates)
            } catch (e: Exception) {
                _state.value = CurrencyUiState.Error(
                    message = e.message ?: "Unknown error",
                    canRetry = true
                )
                _effect.send(CurrencyUiEffect.ShowToast("Failed to load rates"))
            }
        }
    }
}
```

---

## Migration Path

### Step 1: Dependency Updates (30 minutes)

#### Update gradle/libs.versions.toml

Add ViewModel and StateFlow dependencies:

```toml
[libraries]
# Already have:
# androidx-lifecycle-viewmodel-ktx
# androidx-lifecycle-runtime-ktx
# androidx-lifecycle-runtime-compose

# Verify these are present:
androidx-lifecycle-viewmodel-ktx = { ... }
androidx-lifecycle-runtime-compose = { ... }
```

#### Update app/build.gradle

```kotlin
dependencies {
    // Already have from Compose:
    implementation libs.androidx.lifecycle.viewmodel.ktx
    implementation libs.androidx.lifecycle.runtime.compose

    // Koin ViewModels support
    implementation "io.insert-koin:koin-androidx-compose:3.5.3"
}
```

**Checklist:**
- [ ] Verify lifecycle libraries in gradle/libs.versions.toml
- [ ] Add koin-androidx-compose to build.gradle
- [ ] Build to verify no issues

### Step 2: Create TOAD Components (3-4 hours)

#### Task 2.1: Create State/Event/Effect Models

**File: `app/src/main/java/.../presentation/currency/CurrencyState.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.currency

// ============ UI STATE ============
sealed class CurrencyUiState {
    object Idle : CurrencyUiState()
    object Loading : CurrencyUiState()
    data class Success(
        val rates: List<Pair<String, Double>>,
        val timestamp: String = ""
    ) : CurrencyUiState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CurrencyUiState()
}

// ============ UI EVENTS ============
sealed class CurrencyUiEvent {
    object OnLoadRates : CurrencyUiEvent()
    object OnRetry : CurrencyUiEvent()
    object OnRefresh : CurrencyUiEvent()
}

// ============ UI EFFECTS ============
sealed class CurrencyUiEffect {
    data class ShowToast(val message: String) : CurrencyUiEffect()
    data class ShowError(val title: String, val message: String) : CurrencyUiEffect()
    object NavigateBack : CurrencyUiEffect()
}
```

**Checklist:**
- [ ] Create CurrencyState.kt
- [ ] Define all state variants
- [ ] Define all event types
- [ ] Define all effect types

#### Task 2.2: Create TOAD ViewModel

**File: `app/src/main/java/.../presentation/currency/CurrencyViewModel.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.currency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val getRatesUseCase: GetLatestRatesUsecase
) : ViewModel() {

    // ========== STATE ==========
    private val _state = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Idle)
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    // ========== EFFECTS ==========
    private val _effect = Channel<CurrencyUiEffect>()
    val effect: Flow<CurrencyUiEffect> = _effect.receiveAsFlow()

    // ========== EVENT HANDLER ==========
    fun handleEvent(event: CurrencyUiEvent) {
        when (event) {
            is CurrencyUiEvent.OnLoadRates -> loadRates()
            is CurrencyUiEvent.OnRetry -> loadRates()
            is CurrencyUiEvent.OnRefresh -> loadRates()
        }
    }

    // ========== PRIVATE HELPERS ==========
    private fun loadRates() {
        viewModelScope.launch {
            // Emit loading state
            _state.value = CurrencyUiState.Loading

            try {
                Log.d(TAG, "Loading rates...")

                // Call use case
                val rates = getRatesUseCase.getLatestCurrencyRates()

                // Emit success state
                _state.value = CurrencyUiState.Success(
                    rates = rates,
                    timestamp = java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date())
                )

                Log.d(TAG, "Rates loaded successfully: ${rates.size} entries")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load rates: ${e.message}", e)

                // Emit error state
                _state.value = CurrencyUiState.Error(
                    message = e.message ?: "Unknown error occurred",
                    canRetry = true
                )

                // Send effect for error notification
                _effect.send(
                    CurrencyUiEffect.ShowToast("Failed to load rates: ${e.message}")
                )
            }
        }
    }

    companion object {
        private const val TAG = "CurrencyViewModel"
    }
}
```

**Checklist:**
- [ ] Create CurrencyViewModel
- [ ] Implement state management
- [ ] Implement event handling
- [ ] Implement effect sending
- [ ] Add proper logging

#### Task 2.3: Create MainViewModel for Permissions

**File: `app/src/main/java/.../presentation/main/MainViewModel.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

// ========== STATES ==========
sealed class MainUiState {
    object CheckingPermission : MainUiState()
    object PermissionGranted : MainUiState()
    object PermissionDenied : MainUiState()
}

sealed class MainUiEvent {
    object OnCheckPermission : MainUiEvent()
    object OnPermissionResult : MainUiEvent()
}

sealed class MainUiEffect {
    object RequestPermission : MainUiEffect()
    object NavigateToCurrency : MainUiEffect()
}

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.CheckingPermission)
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    private val _effect = Channel<MainUiEffect>()
    val effect: Flow<MainUiEffect> = _effect.receiveAsFlow()

    fun handleEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnCheckPermission -> checkPermission()
            is MainUiEvent.OnPermissionResult -> handlePermissionResult()
        }
    }

    fun onPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            if (granted) {
                _state.value = MainUiState.PermissionGranted
                _effect.send(MainUiEffect.NavigateToCurrency)
            } else {
                _state.value = MainUiState.PermissionDenied
            }
        }
    }

    private fun checkPermission() {
        viewModelScope.launch {
            _state.value = MainUiState.CheckingPermission
            _effect.send(MainUiEffect.RequestPermission)
        }
    }

    private fun handlePermissionResult() {
        // Updated by onPermissionResult callback
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
```

**Checklist:**
- [ ] Create MainViewModel
- [ ] Manage permission state
- [ ] Handle permission events
- [ ] Send appropriate effects

---

### Step 3: Refactor Koin Modules (1-2 hours)

#### Task 3.1: Update Currency Module with ViewModel

**File: `app/src/main/java/.../presentation/currency/CurrencyModule.kt`** (UPDATE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.currency

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val currencyModule = module {
    // Old MVP setup (remove in M7)
    // scope<CurrencyFragment> {
    //     scoped<CurrencyContract.Presenter> { ... }
    // }

    // New TOAD setup
    viewModel<CurrencyViewModel> { (getRatesUseCase) ->
        CurrencyViewModel(getRatesUseCase = getRatesUseCase)
    }
}
```

**Note:** During M6, keep both old MVP and new TOAD in parallel for gradual migration.

**Checklist:**
- [ ] Add viewModel to Koin module
- [ ] Verify dependencies inject correctly
- [ ] Build to check for errors

#### Task 3.2: Update Main Module with ViewModel

**File: `app/src/main/java/.../presentation/MainModule.kt`** (UPDATE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    // New TOAD setup for MainViewModel
    viewModel<MainViewModel> { MainViewModel() }

    // Old MVP setup (remove in M7)
    // scope<MainActivity> { ... }
}
```

**Checklist:**
- [ ] Add MainViewModel to Koin
- [ ] Build and verify
- [ ] Test dependency injection

---

### Step 4: Update Compose UI to Use TOAD (3-4 hours)

#### Task 4.1: Create Currency Composable with TOAD

**File: `app/src/main/java/.../ui/screens/CurrencyScreen.kt`** (UPDATE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyUiEffect
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyUiEvent
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyUiState
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyViewModel
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.CurrencyListItem
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.ErrorMessage
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.LoadingIndicator

@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load initial data
    LaunchedEffect(Unit) {
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
    }

    // Handle effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CurrencyUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is CurrencyUiEffect.ShowError -> {
                    // Show error dialog (M7 - error dialog component)
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is CurrencyUiEffect.NavigateBack -> {
                    // Handle navigation (M6 - Compose Navigation)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Currency Rates")
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is CurrencyUiState.Idle -> {
                    // Initial state - show loading or empty
                    LoadingIndicator()
                }
                is CurrencyUiState.Loading -> {
                    LoadingIndicator()
                }
                is CurrencyUiState.Success -> {
                    val successState = state as CurrencyUiState.Success
                    CurrencyListContent(
                        currencies = successState.rates,
                        timestamp = successState.timestamp
                    )
                }
                is CurrencyUiState.Error -> {
                    val errorState = state as CurrencyUiState.Error
                    ErrorMessage(
                        message = errorState.message,
                        onRetry = {
                            viewModel.handleEvent(CurrencyUiEvent.OnRetry)
                        }
                    )
                }
            }

            // Refresh FAB
            if (state !is CurrencyUiState.Loading) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.handleEvent(CurrencyUiEvent.OnRefresh)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    icon = { Icon(Icons.Filled.Refresh, "Refresh") },
                    text = { Text("Refresh") }
                )
            }
        }
    }
}

@Composable
fun CurrencyListContent(
    currencies: List<Pair<String, Double>>,
    timestamp: String = "",
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (timestamp.isNotEmpty()) {
            item {
                Text(
                    text = "Updated: $timestamp",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
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
- [ ] Update CurrencyScreen to use ViewModel
- [ ] Implement collectAsStateWithLifecycle
- [ ] Handle effects with LaunchedEffect
- [ ] Add refresh FAB button
- [ ] Test state transitions

#### Task 4.2: Create Main Composable with TOAD

**File: `app/src/main/java/.../presentation/MainActivity.kt`** (UPDATE)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyScreen
import org.koin.androidx.viewmodel.ext.android.viewModel as koinViewmodel

class MainActivity : AppCompatActivity() {

    // Get MainViewModel from Koin
    private val mainViewModel: MainViewModel by koinViewmodel()

    // Register permission launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        mainViewModel.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BasicFrameworkTheme {
                MainScreenContent(
                    viewModel = mainViewModel,
                    onRequestPermission = {
                        requestPermission()
                    }
                )
            }
        }
    }

    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                mainViewModel.onPermissionResult(granted = true)
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
        // For now, proceed with request
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun MainScreenContent(
    viewModel: MainViewModel,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
    currencyViewModel: CurrencyViewModel = viewModel() // TODO: Get from Koin in M6.2
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle effects (permission requests, navigation)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainUiEffect.RequestPermission -> {
                    onRequestPermission()
                }
                is MainUiEffect.NavigateToCurrency -> {
                    Log.d("MainScreen", "Permission granted, ready to show currency")
                }
            }
        }
    }

    // Initialize permission check
    LaunchedEffect(Unit) {
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "BasicFramework")
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .androidx.compose.foundation.layout.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is MainUiState.CheckingPermission -> {
                    Text("Checking permissions...")
                }
                is MainUiState.PermissionGranted -> {
                    // Show currency screen
                    CurrencyScreen(viewModel = currencyViewModel)
                }
                is MainUiState.PermissionDenied -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Camera permission required")
                        Button(onClick = onRequestPermission) {
                            Text("Request Permission")
                        }
                    }
                }
            }
        }
    }
}
```

**Checklist:**
- [ ] Update MainActivity for TOAD
- [ ] Inject MainViewModel
- [ ] Handle effects properly
- [ ] Show appropriate UI for each state

---

### Step 5: Replace/Deprecate MVP Components (2 hours)

#### Task 5.1: Keep MVP for Compatibility (Gradual Migration)

During M6, we keep the old MVP components but mark them as deprecated:

**Update files:**
- [ ] Mark `Presenter` classes with `@Deprecated`
- [ ] Mark `*Contract` interfaces with `@Deprecated`
- [ ] Add migration notice in comments

Example:

```kotlin
@Deprecated("Migrate to TOAD ViewModel in M6")
class CurrencyPresenter(
    // ...
)
```

**Reason:** Allows gradual testing and migration without breaking everything at once.

#### Task 5.2: Create Migration Document

**File: `MIGRATION_M6.md`** (NEW)

Document exactly which classes were replaced:

```markdown
## Milestone 6: MVP → TOAD Migration

### Replaced Classes

| Old (MVP) | New (TOAD) | Status |
|-----------|-----------|--------|
| `CurrencyPresenter` | `CurrencyViewModel` | ✅ Use VM |
| `CurrencyContract` | `CurrencyUiState/Event/Effect` | ✅ Use TOAD |
| `MainPresenter` | `MainViewModel` | ✅ Use VM |
| `MainContract` | `MainUiState/Event/Effect` | ✅ Use TOAD |

### Deletion List (safe to remove)

After M6 completion and M7 testing:
- [ ] All `*Presenter.kt` files
- [ ] All `*Contract.kt` files
- [ ] All adapter/viewholder files (replaced by Compose)
```

**Checklist:**
- [ ] Create MIGRATION_M6.md
- [ ] Document all replacements
- [ ] Include deletion checklist

---

### Step 6: Testing & Verification (2-3 hours)

#### Task 6.1: Unit Tests for ViewModels

**File: `app/src/test/.../presentation/currency/CurrencyViewModelTest.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation.currency

import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    lateinit var getRatesUseCase: GetLatestRatesUsecase

    lateinit var viewModel: CurrencyViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = CurrencyViewModel(getRatesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadRatesSuccess() = runTest {
        // Arrange
        val mockRates = listOf("USD" to 1.0, "EUR" to 0.92)
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenReturn(mockRates)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)

        // Assert
        val state = viewModel.state.first()
        assert(state is CurrencyUiState.Success)
        assert((state as CurrencyUiState.Success).rates == mockRates)
    }

    @Test
    fun testLoadRatesError() = runTest {
        // Arrange
        val error = Exception("Network error")
        whenever(getRatesUseCase.getLatestCurrencyRates()).thenThrow(error)

        // Act
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)

        // Assert
        val state = viewModel.state.first { it is CurrencyUiState.Error }
        assert(state is CurrencyUiState.Error)
        assert((state as CurrencyUiState.Error).message.contains("Network"))
    }
}
```

**Checklist:**
- [ ] Create ViewModel unit tests
- [ ] Test success scenarios
- [ ] Test error scenarios
- [ ] Test event handling
- [ ] Run tests: `./gradlew test`

#### Task 6.2: Integration Tests

**File: `app/src/androidTest/.../presentation/CurrencyScreenTest.kt`** (NEW)

```kotlin
package io.github.wawakaka.basicframeworkproject.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyUiState
import io.github.wawakaka.basicframeworkproject.presentation.currency.CurrencyViewModel
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyScreen
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingState() {
        // TODO: Setup mock ViewModel with loading state
        composeTestRule.setContent {
            BasicFrameworkTheme {
                // CurrencyScreen(viewModel = mockViewModel)
            }
        }

        // Verify loading indicator shown
        composeTestRule.onNodeWithText("Loading").assertExists()
    }

    @Test
    fun testSuccessState() {
        // TODO: Setup mock ViewModel with success state
        composeTestRule.setContent {
            BasicFrameworkTheme {
                // CurrencyScreen(viewModel = mockViewModel)
            }
        }

        // Verify currency items displayed
        composeTestRule.onNodeWithText("USD").assertExists()
    }
}
```

**Checklist:**
- [ ] Create Compose UI tests
- [ ] Test all screen states
- [ ] Test user interactions
- [ ] Run tests: `./gradlew connectedAndroidTest`

#### Task 6.3: Manual Testing

**Test checklist:**
- [ ] App builds: `./gradlew clean build`
- [ ] Permission flow works
- [ ] Currency list loads
- [ ] Error handling works
- [ ] Retry button works
- [ ] Refresh FAB works
- [ ] Configuration changes preserve state
- [ ] No console errors

---

## Deliverables

### Code Changes

**New Files:**
- [ ] `presentation/currency/CurrencyState.kt` (state/event/effect)
- [ ] `presentation/currency/CurrencyViewModel.kt`
- [ ] `presentation/main/MainViewModel.kt`
- [ ] `presentation/currency/CurrencyModule.kt` (updated)
- [ ] `presentation/MainModule.kt` (updated)

**Modified Files:**
- [ ] `MainActivity.kt` (use MainViewModel)
- [ ] `ui/screens/CurrencyScreen.kt` (use CurrencyViewModel)
- [ ] `app/build.gradle` (add ViewModel deps)
- [ ] `gradle/libs.versions.toml` (add ViewModel deps)

**Deprecated (keep for now):**
- [ ] `CurrencyPresenter.kt` (mark @Deprecated)
- [ ] `CurrencyContract.kt` (mark @Deprecated)
- [ ] `MainPresenter.kt` (mark @Deprecated)
- [ ] `MainContract.kt` (mark @Deprecated)

### Documentation

- [ ] Update CLAUDE.md with TOAD examples
- [ ] Create MIGRATION_M6.md
- [ ] Create TOAD_PATTERNS.md (best practices)
- [ ] Create COMPOSE_TOAD_INTEGRATION.md

### PR Description

```markdown
## Milestone 6: Architecture Evolution (MVP → TOAD)

### Summary
Evolved from MVP (Presenter-based) to TOAD pattern with modern ViewModel + StateFlow + Compose integration.

### What Changed
- ✅ Replaced Presenter layer with ViewModel
- ✅ Introduced UiState/UiEvent/UiEffect pattern
- ✅ Implemented StateFlow for reactive state
- ✅ Updated Compose UI to use TOAD pattern
- ✅ Added effect handling for side effects
- ✅ Implemented proper error states

### What Stayed
- ✅ Domain layer (UseCases)
- ✅ Repository layer
- ✅ Coroutines for async
- ✅ Koin for DI (updated for ViewModels)

### Benefits
- ✅ Natural fit with Compose recomposition
- ✅ Immutable state management
- ✅ Type-safe events and effects
- ✅ Easier to test (state assertions)
- ✅ Better handling of configuration changes
- ✅ Clearer separation of concerns

### Testing
- ✅ ViewModel unit tests
- ✅ Effect handling tests
- ✅ Compose UI integration tests
- ✅ Manual testing on device

### Next Steps (M7)
- Full removal of MVP components
- Comprehensive Compose UI testing
- Setup CI/CD testing
- Performance optimization

Co-Authored-By: Claude Haiku 4.5 <noreply@anthropic.com>
```

---

## Architecture Benefits Achieved

### Before (MVP)
```
Problems:
❌ Presenter holds view reference (lifecycle issues)
❌ No immutable state (mutable presenter state)
❌ Callback-based UI updates (fragile)
❌ Side effects scattered (no clear pattern)
❌ Hard to test state transitions
```

### After (TOAD)
```
Benefits:
✅ ViewModel survives configuration changes
✅ Immutable StateFlow state
✅ Recomposition-based updates (Compose-native)
✅ Explicit effect channel for side effects
✅ Easy state assertions in tests
✅ Clear unidirectional data flow
✅ Type-safe events and states
```

---

## Common Pitfalls & Solutions

### Pitfall 1: Too Many States
**Problem:** Creating too many state variants
**Solution:** Group related states logically

```kotlin
// ❌ Bad: Too granular
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    object RequestInProgress : UiState()
    data class Success(val data: Data) : UiState()
}

// ✅ Good: Logical grouping
sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val data: Data) : UiState()
    data class Error(val message: String) : UiState()
}
```

### Pitfall 2: Blocking Operations in ViewModel
**Problem:** Long-running operations blocking recomposition
**Solution:** Use coroutines with proper dispatchers

```kotlin
// ❌ Bad: Blocks thread
private fun loadData() {
    val data = expensiveComputation()  // Blocks!
    _state.value = Success(data)
}

// ✅ Good: Async with coroutine
private fun loadData() {
    viewModelScope.launch {
        val data = withContext(Dispatchers.Default) {
            expensiveComputation()
        }
        _state.value = Success(data)
    }
}
```

### Pitfall 3: Missing Effect Cleanup
**Problem:** Effects sent but not collected, leaking coroutines
**Solution:** Always collect effects in Compose

```kotlin
// ✅ Always collect in LaunchedEffect
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            // Handle effects
        }
    }
}
```

---

**Ready for Milestone 6 implementation?**

Next steps:
1. Decide implementation approach (full rewrite vs gradual migration)
2. Create branch for M6 work
3. Start with Step 1 (Dependencies)
4. Gradually migrate one screen at a time (Currency first)
