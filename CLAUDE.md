# CLAUDE.md - AI Assistant Guide for BasicFrameworkProject

**Last Updated:** 2026-01-24 (Milestone 6 Complete)
**Project Version:** 1.0
**Target SDK:** Android 14 (API 34)

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Design Patterns](#architecture--design-patterns)
3. [Module Structure](#module-structure)
4. [Technology Stack](#technology-stack)
5. [Development Workflows](#development-workflows)
6. [Code Conventions & Best Practices](#code-conventions--best-practices)
7. [Key Files & Locations](#key-files--locations)
8. [Common Tasks](#common-tasks)
9. [Testing Strategy](#testing-strategy)
10. [Important Notes for AI Assistants](#important-notes-for-ai-assistants)

---

## Project Overview

**BasicFrameworkProject** is a demonstration Android application showcasing Clean Architecture principles with TOAD (The Opinionated Android Design) pattern. The app displays currency exchange rates using the rates API.

### Key Characteristics
- **Package Name:** `io.github.wawakaka.basicframeworkproject`
- **Language:** Kotlin 2.0.21
- **Build System:** Gradle 8.5
- **AGP:** 8.2.2
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Compile SDK:** 34
- **JDK:** 21 (recommended, 17+ required)
- **Primary Feature:** Currency exchange rate display
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture:** TOAD with ViewModel + StateFlow

### Project Purpose
This is a framework/template project demonstrating professional Android development practices with:
- Clean Architecture separation
- TOAD architecture pattern (State/Event/Effect)
- Jetpack Compose UI
- Reactive programming with Kotlin Coroutines & StateFlow
- Dependency injection with Koin
- Modern Android components

---

## Architecture & Design Patterns

### Clean Architecture Layers

The project follows a strict layered architecture:

```
┌─────────────────────────────────────────────────┐
│  Presentation Layer (app module)                │
│  - Jetpack Compose UI (Activities, Fragments)   │
│  - ViewModels (TOAD Pattern)                    │
│  - State/Event/Effect Models                    │
│  - Koin DI Configuration                        │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│  Domain Layer (domain module)                   │
│  - Use Cases (Business Logic)                   │
│  - Pure Kotlin (no Android dependencies)        │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│  Data Layer (repository module)                 │
│  - Repositories                                 │
│  - API Interfaces                               │
│  - Data Models                                  │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│  Network Layer (restapi module)                 │
│  - Retrofit Configuration                       │
│  - OkHttp Client Setup                          │
│  - Interceptors                                 │
└─────────────────────────────────────────────────┘
```

### TOAD Pattern Implementation (Current - Milestone 6)

Each feature implements the TOAD pattern with State/Event/Effect modeling:

**State Model (`*State.kt`):**
```kotlin
sealed class FeatureUiState {
    object Idle : FeatureUiState()
    object Loading : FeatureUiState()
    data class Success(val data: List<T>) : FeatureUiState()
    data class Error(val message: String) : FeatureUiState()
}

sealed class FeatureUiEvent {
    object OnLoadData : FeatureUiEvent()
    object OnRetry : FeatureUiEvent()
    object OnRefresh : FeatureUiEvent()
}

sealed class FeatureUiEffect {
    data class ShowToast(val message: String) : FeatureUiEffect()
    data class NavigateBack : FeatureUiEffect()
}
```

**ViewModel Implementation:**
```kotlin
class FeatureViewModel(
    private val useCase: FeatureUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<FeatureUiState>(FeatureUiState.Idle)
    val state: StateFlow<FeatureUiState> = _state.asStateFlow()

    private val _effect = Channel<FeatureUiEffect>()
    val effect: Flow<FeatureUiEffect> = _effect.receiveAsFlow()

    fun handleEvent(event: FeatureUiEvent) {
        when (event) {
            is FeatureUiEvent.OnLoadData -> loadData()
            is FeatureUiEvent.OnRetry -> loadData()
            is FeatureUiEvent.OnRefresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = FeatureUiState.Loading
            try {
                val data = useCase.execute()
                _state.value = FeatureUiState.Success(data)
            } catch (e: Exception) {
                _state.value = FeatureUiState.Error(e.message ?: "Unknown error")
                _effect.send(FeatureUiEffect.ShowToast("Failed to load data"))
            }
        }
    }
}
```

**Compose UI Integration:**
```kotlin
@Composable
fun FeatureScreen(viewModel: FeatureViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FeatureUiEffect.ShowToast -> { /* show toast */ }
                is FeatureUiEffect.NavigateBack -> { /* navigate */ }
            }
        }
    }

    when (state) {
        is FeatureUiState.Loading -> LoadingIndicator()
        is FeatureUiState.Success -> SuccessContent((state as FeatureUiState.Success).data)
        is FeatureUiState.Error -> ErrorMessage((state as FeatureUiState.Error).message)
        is FeatureUiState.Idle -> EmptyState()
    }
}
```

**Key Principles:**
- **UiState:** Immutable data class representing screen state
- **UiEvent:** User interactions or screen events
- **UiEffect:** One-time side effects (toasts, navigation)
- **ViewModel:** State holder with viewModelScope for lifecycle management
- **StateFlow:** Reactive state observation in Compose
- **Channel:** One-time effect delivery
- **Unidirectional Data Flow:** Events → ViewModel → State → UI

**Benefits:**
- ✅ Survives configuration changes automatically
- ✅ Type-safe state modeling with sealed classes
- ✅ Better separation of concerns
- ✅ Easier to test (no View mocking needed)
- ✅ Perfect integration with Jetpack Compose
- ✅ Built-in lifecycle management

### Dependency Injection (Koin)

**Module Organization:**
- Each feature has its own Koin module (`*Module.kt`)
- Modules are combined in `Modules.kt` → `applicationModules` list
- Use `viewModel { }` for ViewModels, `single { }` for singletons, `factory { }` for new instances

**Example Module Structure (TOAD):**
```kotlin
val featureModule = module {
    // ViewModel injection
    viewModel { FeatureViewModel(getRatesUseCase = get()) }

    // UseCase injection
    factory { GetDataUseCase(repository = get()) }

    // Repository injection
    single { FeatureRepository(api = get()) }
}
```

**ViewModel Injection in Fragment/Activity:**
```kotlin
class FeatureFragment : Fragment() {
    private val viewModel: FeatureViewModel by viewModel()  // Koin extension

    override fun onCreateView(...): View = ComposeView(requireContext()).apply {
        setContent {
            BasicFrameworkTheme {
                FeatureScreen(viewModel = viewModel)
            }
        }
    }
}
```

**ViewModel Injection in Composable (Alternative):**
```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel = org.koin.androidx.compose.koinViewModel()
) {
    // Use viewModel
}
```

**Initialization:** Koin is started in `App.kt:onCreate()`

### Async Operations Flow (TOAD with Kotlin Coroutines)

All async operations use Kotlin Coroutines with ViewModel:

```
User Action (Compose UI)
    ↓
UiEvent dispatched to ViewModel.handleEvent()
    ↓
viewModelScope.launch { ... }
    ↓
UseCase (suspend function)
    ↓
Repository → API Call (suspend function)
    ↓
ViewModel updates _state.value
    ↓
StateFlow emits new state
    ↓
Compose UI recomposes automatically
```

**Key Components:**
- **ViewModel:** State holder with viewModelScope
- **StateFlow:** Reactive state observation
- **Channel:** One-time side effects
- **Suspend functions:** Used throughout domain and data layers
- **viewModelScope:** Automatically cancelled when ViewModel is cleared
- **collectAsStateWithLifecycle:** Lifecycle-aware state collection in Compose

---

## Module Structure

### 1. `app` Module (Presentation Layer)

**Location:** `/app/src/main/java/io/github/wawakaka/basicframeworkproject/`

**Key Directories:**
```
app/
├── App.kt                          # Application class, Koin initialization
├── Modules.kt                      # Combined Koin modules
├── base/                           # Base classes
│   └── BaseActivity.kt             # Base activity
├── presentation/                   # Main screen (permission check)
│   ├── MainActivity.kt             # Main activity with Compose
│   ├── MainViewModel.kt            # TOAD ViewModel
│   ├── MainModule.kt               # Koin module
│   └── content/                    # Currency feature
│       ├── CurrencyFragment.kt     # Compose UI host
│       ├── CurrencyViewModel.kt    # TOAD ViewModel
│       ├── CurrencyState.kt        # State/Event/Effect models
│       └── CurrencyModule.kt       # Koin module
├── presentation/ui/                # Compose UI components
│   ├── theme/                      # Material 3 theme
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── components/                 # Reusable components
│   │   ├── AppTopBar.kt
│   │   ├── LoadingIndicator.kt
│   │   ├── ErrorMessage.kt
│   │   └── CurrencyListItem.kt
│   └── screens/                    # Screen composables
│       └── CurrencyScreen.kt       # Currency screen UI
├── domain/
│   └── domainModules.kt            # Domain layer DI setup
└── utilities/
    └── ViewExtensions.kt           # UI helper extensions
```

**Dependencies:**
- `domain` module
- AndroidX libraries (Lifecycle, ViewModel, Compose)
- Jetpack Compose BOM 2024.02.00
- Material 3 Compose
- Koin for Android + Compose
- Kotlin Coroutines

### 2. `domain` Module (Business Logic)

**Location:** `/domain/src/main/java/io/github/wawakaka/domain/`

**Structure:**
```
domain/
├── Usecase.kt                      # Factory object for use cases
└── usecase/
    └── GetLatestRatesUsecase.kt    # Currency rates business logic
```

**Key Characteristics:**
- **Pure Kotlin:** No Android framework dependencies
- **UseCase Pattern:** Each use case = one business operation
- **RxJava Transformations:** Threading and data mapping
- **Dependencies:** `repository` module for data access

**Example UseCase:**
```kotlin
class GetLatestRatesUsecase(private val repository: CurrencyRatesRepository) {
    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        val response = repository.getLatestCurrencyRates()
        return response.rates?.toList() ?: emptyList()
    }
}
```

### 3. `repository` Module (Data Layer)

**Location:** `/repository/src/main/java/io/github/wawakaka/repository/`

**Structure:**
```
repository/
├── Repository.kt                   # Factory for repositories
└── currencyrates/
    ├── CurrencyRatesRepository.kt  # Data access abstraction
    ├── CurrencyRatesApi.kt         # Retrofit interface
    └── model/
        └── response/
            └── CurrencyRatesResponse.kt
```

**Key Responsibilities:**
- Abstracts data sources from domain layer
- Implements repository pattern
- Defines Retrofit API interfaces
- Contains data models with JSON mapping

**API Interface Example:**
```kotlin
interface CurrencyRatesApi {
    @GET("latest")
    suspend fun getLatestWithBase(@Query("base") base: String): CurrencyRatesResponse
}
```

**Dependencies:** `restapi` module for network client

### 4. `restapi` Module (Network Layer)

**Location:** `/restapi/src/main/java/io/github/wawakaka/restapi/`

**Structure:**
```
restapi/
├── RestApi.kt                      # Retrofit/OkHttp factory
└── HeaderInterceptor.kt            # Custom HTTP interceptor
```

**Configuration:**
- **Base URL:** `https://api.ratesapi.io/api/`
- **Timeouts:** 60s connect/read/write
- **Interceptors:** Headers, logging, Chuck (network inspection)
- **Converters:** Gson with identity field naming
- **Note:** Uses suspend functions natively (Retrofit 2.6+)

---

## Technology Stack

### Core Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Kotlin | 2.0.21 | Primary development language |
| **Build** | Gradle | 8.5 | Build automation |
| **AGP** | Android Gradle Plugin | 8.2.2 | Android build system |
| **JDK** | Java Development Kit | 21 | Build target (17+ required) |
| **UI Framework** | Android SDK | API 34 | Application framework |

### Key Libraries

#### Kotlin Coroutines
- **Kotlin Coroutines Core:** `1.7.3` - Lightweight concurrency library
- **Kotlin Coroutines Android:** `1.7.3` - Android-specific coroutine support
- **AndroidX Lifecycle Runtime KTX:** `2.7.0` - Lifecycle-aware coroutine scopes
- **AndroidX Lifecycle ViewModel KTX:** `2.7.0` - ViewModel coroutine support

#### Android Permissions & Activities
- **AndroidX Activity KTX:** `1.8.1` - ActivityResultContracts for permission handling

#### Networking
- **Retrofit:** `2.9.0` - REST API client
- **OkHttp:** `4.12.0` - HTTP client
- **Gson:** `2.10.1` - JSON serialization
- **Chuck:** `1.1.0` - Network debugging

#### Dependency Injection
- **Koin:** `3.5.3` - Lightweight DI framework
- **Koin Compose:** `3.5.3` - ViewModel injection for Compose
- **⚠️ Breaking Change:** Koin 3.x has API changes from 2.x

#### Jetpack Compose (Milestone 5+)
- **Compose BOM:** `2024.02.00` - Bill of Materials for version alignment
- **Compose UI:** Core composable framework
- **Compose Material 3:** Material Design 3 components
- **Compose UI Tooling:** Preview and debugging tools
- **Compose Navigation:** `2.7.6` - Navigation for Compose
- **Compose Activity:** `1.8.1` - Activity integration
- **Compose Lifecycle:** ViewModel and Runtime Compose integration

#### AndroidX Lifecycle (TOAD Pattern)
- **Lifecycle ViewModel Compose:** ViewModel integration for Compose
- **Lifecycle Runtime Compose:** collectAsStateWithLifecycle support

#### UI Components (Legacy - mostly deprecated)
- **AndroidX Core KTX:** `1.12.0`
- **AndroidX AppCompat:** `1.6.1`
- **Material Design:** `1.11.0` - Used for Activities (deprecated for fragments)
- **ConstraintLayout:** `2.1.4` - Not used (Compose uses Box/Column/Row)
- **Navigation Component:** `2.7.6` - Fragment navigation (legacy)
- **Anko:** `0.10.8` - Kotlin helpers (deprecated, consider removing)

#### Other
- **MultiDex:** `2.0.1` - Support for large apps

#### Testing
- **JUnit:** `4.13.2` - Unit testing framework
- **Mockito Kotlin:** `5.1.0` - Mocking framework for Kotlin
- **Coroutines Test:** `1.7.3` - Testing coroutines
- **Arch Core Testing:** `2.2.0` - Testing LiveData and ViewModel
- **Compose UI Test:** Compose UI testing framework
- **AndroidX Test Runner:** `1.5.2` - Instrumented test runner
- **Espresso Core:** `1.5.1` - UI testing

### Repository Information
- **Maven Repositories:** Google, Maven Central, JitPack
- **⚠️ Note:** JCenter removed (deprecated)
- **Dependency Management:** Gradle Version Catalog (`gradle/libs.versions.toml`)

---

## Development Workflows

### Build Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build to device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Build all modules
./gradlew build
```

### Build Variants

#### Debug Build
- **Package Suffix:** `.dev`
- **Version Suffix:** `dev`
- **Debuggable:** Yes
- **Minification:** Disabled
- **MultiDex:** Enabled

#### Release Build
- **Debuggable:** No
- **Minification:** Disabled (can enable with ProGuard)
- **ProGuard:** Configuration available in `proguard-rules.pro`

### Gradle Configuration

**Key Files:**
- `build.gradle` (root) - Project-level configuration
- `gradle/libs.versions.toml` - **Gradle Version Catalog** (centralized dependency management)
- `settings.gradle` - Module inclusions
- `gradle.properties` - Build properties with modern Gradle features
- `dep/build-lib.gradle` - Shared library module template

**Dependency Management:**
- Uses **Gradle Version Catalog** for type-safe dependency accessors
- Access dependencies via `libs.*` in build.gradle files
- All versions centralized in `gradle/libs.versions.toml`
- Supports IDE auto-completion for dependencies

**Version Catalog Structure:**
```toml
[versions]
kotlin = "1.9.22"
retrofit = "2.9.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
```

### Module Dependencies

```
app
 ├── domain
 │    └── repository
 │         └── restapi
```

**Important:** Respect dependency direction. Lower layers should never depend on higher layers.

---

## Code Conventions & Best Practices

### Package Structure

```
io.github.wawakaka.<module>.<layer>.<feature>
```

**Examples:**
- `io.github.wawakaka.basicframeworkproject.presentation.content`
- `io.github.wawakaka.domain.usecase`
- `io.github.wawakaka.repository.currencyrates`

### Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| **ViewModel** | `*ViewModel` | `CurrencyViewModel` |
| **UiState** | `*UiState` | `CurrencyUiState` |
| **UiEvent** | `*UiEvent` | `CurrencyUiEvent` |
| **UiEffect** | `*UiEffect` | `CurrencyUiEffect` |
| **State File** | `*State.kt` | `CurrencyState.kt` |
| **Composable Screen** | `*Screen` | `CurrencyScreen` |
| **Composable Component** | descriptive name | `AppTopBar`, `LoadingIndicator` |
| **Activity** | `*Activity` | `MainActivity` |
| **Fragment** | `*Fragment` | `CurrencyFragment` |
| **API Interface** | `*Api` | `CurrencyRatesApi` |
| **Repository** | `*Repository` | `CurrencyRatesRepository` |
| **UseCase** | `*Usecase` | `GetLatestRatesUsecase` |
| **Response Model** | `*Response` | `CurrencyRatesResponse` |
| **Koin Module** | `*Module` | `CurrencyModule` |
| **Contract** (deprecated) | `*Contract` | `CurrencyContract` |
| **Presenter** (deprecated) | `*Presenter` | `CurrencyPresenter` |

### Kotlin Best Practices

#### Extension Functions
Create extensions for common UI operations:

```kotlin
// utilities/ViewExtensions.kt
fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}
```

#### Data Classes
Use for models with JSON annotations:

```kotlin
data class CurrencyRatesResponse(
    @SerializedName("base") val base: String?,
    @SerializedName("date") val date: String?,
    @SerializedName("rates") val rates: Rates?
)
```

#### Object Singletons
Use for factory objects:

```kotlin
object Repository {
    fun provideCurrencyRatesRepository(api: CurrencyRatesApi) =
        CurrencyRatesRepository(api)
}
```

#### Property Delegation
Koin injection with `by inject()` or `by currentScope.inject()`:

```kotlin
class MyPresenter : BasePresenter<MyContract.View>() {
    private val useCase: MyUseCase by inject()
}
```

### Kotlin Coroutines Patterns (TOAD)

#### ViewModel with viewModelScope
ViewModels use `viewModelScope` for lifecycle-aware coroutines:

```kotlin
class MyViewModel(
    private val usecase: MyUsecase
) : ViewModel() {

    private val _state = MutableStateFlow<MyUiState>(MyUiState.Idle)
    val state: StateFlow<MyUiState> = _state.asStateFlow()

    private val _effect = Channel<MyUiEffect>()
    val effect: Flow<MyUiEffect> = _effect.receiveAsFlow()

    fun handleEvent(event: MyUiEvent) {
        when (event) {
            is MyUiEvent.OnLoadData -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = MyUiState.Loading
            try {
                val data = usecase.getData()
                _state.value = MyUiState.Success(data)
            } catch (e: Exception) {
                _state.value = MyUiState.Error(e.message ?: "Unknown error")
                _effect.send(MyUiEffect.ShowToast("Failed to load data"))
            }
        }
    }
    // viewModelScope automatically cancels when ViewModel is cleared
}
```

#### Compose UI with State Collection
Use `collectAsStateWithLifecycle()` for lifecycle-aware state observation:

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(MyUiEvent.OnLoadData)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MyUiEffect.ShowToast -> { /* show toast */ }
            }
        }
    }

    when (state) {
        is MyUiState.Loading -> LoadingIndicator()
        is MyUiState.Success -> SuccessContent((state as MyUiState.Success).data)
        is MyUiState.Error -> ErrorMessage((state as MyUiState.Error).message)
        is MyUiState.Idle -> EmptyState()
    }
}
```

#### Fragment Hosting Compose (Bridge Pattern)
Fragments can host Compose UI with ViewModel injection:

```kotlin
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            BasicFrameworkTheme {
                MyScreen(viewModel = viewModel)
            }
        }
    }
}
```

#### Suspend Functions in Domain/Repository
Replace Observable<T> with suspend functions:

```kotlin
// Repository
suspend fun getLatestCurrencyRates(): CurrencyRatesResponse {
    return currencyRatesApi.getLatestWithBase()
}

// UseCase
suspend fun getLatestRates(): List<Pair<String, Double>> {
    val response = repository.getLatestCurrencyRates()
    return response.rates?.toList() ?: emptyList()
}
```

### TOAD Lifecycle

**ViewModel Lifecycle:**
- ViewModel is created when first requested
- Survives configuration changes (rotation, etc.)
- Cleared when Activity/Fragment is permanently destroyed
- `viewModelScope` automatically cancels all coroutines on clear

**Fragment Lifecycle with ViewModel:**
```kotlin
class MyFragment : Fragment() {
    private val viewModel: MyViewModel by viewModel()  // Koin injection

    override fun onCreateView(...): View = ComposeView(requireContext()).apply {
        setContent {
            BasicFrameworkTheme {
                MyScreen(viewModel = viewModel)
            }
        }
    }
    // ViewModel persists across configuration changes
    // No manual cleanup needed
}
```

**Compose State Collection Lifecycle:**
```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel) {
    // collectAsStateWithLifecycle handles lifecycle automatically
    val state by viewModel.state.collectAsStateWithLifecycle()

    // LaunchedEffect runs when composition starts
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            // Handle one-time effects
        }
    }
    // Collection stops when Composable leaves composition
}
```

### Error Handling (TOAD Pattern)

**In ViewModel:**
```kotlin
private fun loadData() {
    viewModelScope.launch {
        _state.value = MyUiState.Loading
        try {
            val data = usecase.getData()
            _state.value = MyUiState.Success(data)
        } catch (e: Exception) {
            _state.value = MyUiState.Error(e.message ?: "Unknown error")
            _effect.send(MyUiEffect.ShowToast("Failed to load data"))
        }
    }
}
```

**In Compose UI:**
```kotlin
when (state) {
    is MyUiState.Error -> {
        ErrorMessage(
            message = (state as MyUiState.Error).message,
            onRetry = { viewModel.handleEvent(MyUiEvent.OnRetry) }
        )
    }
    // ... other states
}

// Handle effects
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is MyUiEffect.ShowToast -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

---

## Key Files & Locations

### Application Entry Points

| File | Location | Purpose |
|------|----------|---------|
| `App.kt` | `app/src/main/java/.../App.kt` | Application class, Koin initialization |
| `MainActivity.kt` | `app/src/main/java/.../presentation/MainActivity.kt` | Entry activity, permission checks |
| `AndroidManifest.xml` | `app/src/main/AndroidManifest.xml` | App configuration, permissions |

### Configuration Files

| File | Location | Purpose |
|------|----------|---------|
| `dependencies.gradle` | `/dependencies.gradle` | Centralized dependency versions |
| `build.gradle` (root) | `/build.gradle` | Root project configuration |
| `build.gradle` (app) | `/app/build.gradle` | App module configuration |
| `settings.gradle` | `/settings.gradle` | Module inclusions |
| `gradle.properties` | `/gradle.properties` | Build properties |

### Base Classes

| File | Purpose | Status |
|------|---------|--------|
| `BaseActivity.kt` | Base activity | Active |

**Note:** All MVP pattern files were removed in Milestone 7. The project now uses TOAD pattern exclusively with ViewModels.

### Koin Modules

| File | Purpose | Contains |
|------|---------|----------|
| `Modules.kt` | Application module composition | All modules combined |
| `MainModule.kt` | Main screen dependencies | MainViewModel |
| `CurrencyModule.kt` | Currency feature dependencies | CurrencyViewModel |
| `domainModules.kt` | Domain layer dependencies | UseCases, Repositories, APIs |

### Navigation

| File | Location | Purpose |
|------|----------|---------|
| `navigation_graph.xml` | `app/src/main/res/navigation/` | Navigation component graph |

### Network Configuration

| File | Purpose |
|------|---------|
| `RestApi.kt` | Retrofit/OkHttp factory |
| `HeaderInterceptor.kt` | Custom HTTP headers |
| `network_security_config.xml` | Network security policy |

---

## Common Tasks

### Adding a New Feature (TOAD Pattern)

1. **Create State/Event/Effect Models** (`*State.kt`):
```kotlin
sealed class FeatureUiState {
    object Idle : FeatureUiState()
    object Loading : FeatureUiState()
    data class Success(val data: List<DataType>) : FeatureUiState()
    data class Error(val message: String) : FeatureUiState()
}

sealed class FeatureUiEvent {
    object OnLoadData : FeatureUiEvent()
    object OnRetry : FeatureUiEvent()
    object OnRefresh : FeatureUiEvent()
}

sealed class FeatureUiEffect {
    data class ShowToast(val message: String) : FeatureUiEffect()
    object NavigateBack : FeatureUiEffect()
}
```

2. **Create ViewModel** (`*ViewModel.kt`):
```kotlin
class FeatureViewModel(
    private val useCase: FeatureUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<FeatureUiState>(FeatureUiState.Idle)
    val state: StateFlow<FeatureUiState> = _state.asStateFlow()

    private val _effect = Channel<FeatureUiEffect>()
    val effect: Flow<FeatureUiEffect> = _effect.receiveAsFlow()

    fun handleEvent(event: FeatureUiEvent) {
        when (event) {
            is FeatureUiEvent.OnLoadData -> loadData()
            is FeatureUiEvent.OnRetry -> loadData()
            is FeatureUiEvent.OnRefresh -> loadData()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = FeatureUiState.Loading
            try {
                val data = useCase.execute()
                _state.value = FeatureUiState.Success(data)
            } catch (e: Exception) {
                _state.value = FeatureUiState.Error(e.message ?: "Unknown error")
                _effect.send(FeatureUiEffect.ShowToast("Failed to load data"))
            }
        }
    }
}
```

3. **Create Compose Screen** (`*Screen.kt`):
```kotlin
@Composable
fun FeatureScreen(
    viewModel: FeatureViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.handleEvent(FeatureUiEvent.OnLoadData)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is FeatureUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is FeatureUiEffect.NavigateBack -> { /* navigate */ }
            }
        }
    }

    Scaffold(
        topBar = { AppTopBar(title = "Feature Title") }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is FeatureUiState.Idle -> EmptyState()
                is FeatureUiState.Loading -> LoadingIndicator()
                is FeatureUiState.Success -> {
                    val data = (state as FeatureUiState.Success).data
                    // Display success content
                }
                is FeatureUiState.Error -> {
                    val message = (state as FeatureUiState.Error).message
                    ErrorMessage(
                        message = message,
                        onRetry = { viewModel.handleEvent(FeatureUiEvent.OnRetry) }
                    )
                }
            }
        }
    }
}
```

4. **Create Fragment** (if needed to host Compose):
```kotlin
class FeatureFragment : Fragment() {
    private val viewModel: FeatureViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            BasicFrameworkTheme {
                FeatureScreen(viewModel = viewModel)
            }
        }
    }
}
```

5. **Create Koin Module** (`*Module.kt`):
```kotlin
val featureModule = module {
    viewModel { FeatureViewModel(useCase = get()) }
    factory { FeatureUseCase(repository = get()) }
    single { FeatureRepository(api = get()) }
}
```

6. **Add to `Modules.kt`:**
```kotlin
val applicationModules = listOf(
    // existing modules...
    featureModule
)
```

7. **Write Tests:**
   - Unit tests for ViewModel (state transitions, event handling)
   - Compose UI tests for screen rendering
   - See MIGRATION_M6.md for testing examples

### Adding a New API Endpoint

1. **Define API interface** in `repository` module (with suspend functions):
```kotlin
interface FeatureApi {
    @GET("endpoint")
    suspend fun getData(@Query("param") param: String): ResponseModel
}
```

2. **Create Response Model:**
```kotlin
data class ResponseModel(
    @SerializedName("field") val field: String?
)
```

3. **Create Repository** (with suspend functions):
```kotlin
class FeatureRepository(private val api: FeatureApi) {
    suspend fun getData(param: String): ResponseModel {
        return api.getData(param)
    }
}
```

4. **Create UseCase** in `domain` module:
```kotlin
class GetDataUseCase(private val repository: FeatureRepository) {
    suspend fun execute(param: String): ProcessedData {
        val response = repository.getData(param)
        return processResponse(response)
    }
}
```

5. **Wire up in Koin modules**

### Adding Dependencies

**Using Gradle Version Catalog (Current Approach):**

1. **Add version** to `gradle/libs.versions.toml` in `[versions]` section:
```toml
[versions]
newlib = "1.0.0"
```

2. **Add library definition** to `[libraries]` section:
```toml
[libraries]
newlib = { group = "com.example", name = "newlib", version.ref = "newlib" }
```

3. **Add to module's `build.gradle`:**
```kotlin
dependencies {
    implementation libs.newlib  // Note: libs (not lib)
}
```

**Benefits:**
- Type-safe accessors with IDE auto-completion
- Centralized version management
- Automatic dependency conflict resolution
- No manual string concatenation

### Handling Permissions with ActivityResultContracts

Modern Android permission handling using `ActivityResultContracts` (recommended since M4):

**In Activity:**
```kotlin
class PermissionActivity : AppCompatActivity(), PermissionContract.View {

    // Register permission launcher (must be before onCreate returns)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        presenter.onPermissionResult(isGranted)
    }

    override fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                presenter.onPermissionResult(granted = true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show rationale dialog
                showPermissionRationaleDialog()
            }

            else -> {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        // Show Material 3 AlertDialog explaining why permission is needed
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
```

**In Presenter:**
```kotlin
class PermissionPresenter : BasePresenter<PermissionContract.View>(), PermissionContract.Presenter {
    override fun checkPermission() {
        // Delegate to View (Activity)
        view?.requestCameraPermission()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            view?.onPermissionGranted()
        } else {
            view?.onPermissionDenied()
        }
    }
}
```

**Benefits:**
- ✅ No RxJava dependency overhead
- ✅ Type-safe contract API
- ✅ Automatic lifecycle management
- ✅ Modern Android best practice
- ✅ Easier to test and maintain

### Implementing RecyclerView

1. **Create Adapter:**
```kotlin
class MyAdapter(private val onItemClick: (Item) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {

    private val items = mutableListOf<Item>()

    fun setData(newItems: List<Item>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
```

2. **Create ViewHolder:**
```kotlin
class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item, onClick: (Item) -> Unit) {
        // Bind data
    }
}
```

---

## Testing Strategy

### Current State (Milestone 6)
- **Unit Tests:** ViewModel testing with Mockito Kotlin
- **Integration Tests:** Compose UI testing
- **Test Coverage:** 26 unit tests + 32 integration tests = 58 total tests
- **Test Location:** `app/src/test/` (unit), `app/src/androidTest/` (instrumented)

### Test Configuration

```kotlin
// build.gradle
testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

dependencies {
    // Unit testing
    testImplementation libs.junit
    testImplementation libs.kotlinx.coroutines.test
    testImplementation libs.mockito.kotlin
    testImplementation libs.androidx.arch.core.testing

    // Compose UI testing
    androidTestImplementation platform(libs.compose.bom)
    androidTestImplementation libs.compose.ui.test.junit4
    debugImplementation libs.compose.ui.test.manifest
    androidTestImplementation libs.androidx.test.runner
    androidTestImplementation libs.androidx.test.espresso.core
}
```

### Testing Approach (TOAD Pattern)

#### Unit Tests (ViewModel Layer)
Test ViewModel state transitions and event handling:

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class FeatureViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    lateinit var useCase: FeatureUseCase

    private lateinit var viewModel: FeatureViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = FeatureViewModel(useCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `handleEvent OnLoadData success should emit Success state`() = runTest {
        // Arrange
        val mockData = listOf("data1", "data2")
        whenever(useCase.execute()).thenReturn(mockData)

        // Act
        viewModel.handleEvent(FeatureUiEvent.OnLoadData)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is FeatureUiState.Success)
        assertEquals(mockData, (finalState as FeatureUiState.Success).data)
    }

    @Test
    fun `handleEvent OnLoadData failure should emit Error state`() = runTest {
        // Arrange
        val errorMessage = "Network error"
        whenever(useCase.execute()).thenThrow(RuntimeException(errorMessage))

        // Act
        viewModel.handleEvent(FeatureUiEvent.OnLoadData)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.state.value
        assertTrue(finalState is FeatureUiState.Error)
        assertEquals(errorMessage, (finalState as FeatureUiState.Error).message)
    }
}
```

**Benefits:**
- ✅ No View mocking needed
- ✅ Direct state assertions
- ✅ Test state transitions
- ✅ Test effect emissions

#### Compose UI Integration Tests
Test UI rendering and interactions:

```kotlin
@RunWith(AndroidJUnit4::class)
class FeatureScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun successState_shouldShowContent() {
        // Arrange
        val mockData = listOf("Item 1", "Item 2")

        composeTestRule.setContent {
            BasicFrameworkTheme {
                FeatureScreenContent(
                    state = FeatureUiState.Success(mockData),
                    onEvent = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Item 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Item 2").assertIsDisplayed()
    }

    @Test
    fun loadingState_shouldShowLoadingIndicator() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                FeatureScreenContent(
                    state = FeatureUiState.Loading,
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }

    @Test
    fun errorState_shouldShowErrorWithRetryButton() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                FeatureScreenContent(
                    state = FeatureUiState.Error("Network error"),
                    onEvent = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
```

#### Unit Tests (Domain Layer)
- Test UseCases in isolation
- Mock repositories with suspend functions
- Test data transformations and business logic

#### Integration Tests (Repository Layer)
- Test repository with mock API
- Test end-to-end data flow
- Use MockWebServer for API testing

### Test Coverage Goals
- **ViewModels:** 90%+ coverage (state transitions, events, effects, error handling)
- **Compose UI:** 80%+ coverage (rendering, interactions, edge cases)
- **UseCases:** 95%+ coverage (business logic)
- **Repositories:** 80%+ coverage (data access)

---

## Important Notes for AI Assistants

### ⚠️ Critical Guidelines

#### 1. Module Dependencies
**NEVER violate the dependency hierarchy:**
- `app` → `domain` → `repository` → `restapi`
- Lower layers must not depend on higher layers
- Keep `domain` module pure Kotlin (no Android imports)

#### 2. Architecture Patterns
**Always follow TOAD pattern (Milestone 6+):**
- Create `*State.kt` with UiState/UiEvent/UiEffect sealed classes first
- Create ViewModel extending `androidx.lifecycle.ViewModel`
- Use StateFlow for state, Channel for effects
- Keep UI layer passive (Compose observes state)
- Put business logic in ViewModels
- Keep UseCases focused on single operations

**⚠️ MVP Pattern is Deprecated:**
- Do NOT create new Presenters or Contracts
- Use TOAD pattern for all new features
- See MIGRATION_M6.md for migration guide

#### 3. Coroutine Lifecycle Management (TOAD)
**ViewModel handles lifecycle automatically:**
- Use `viewModelScope` in ViewModels (automatically provided)
- ViewModels survive configuration changes
- `viewModelScope` cancels when ViewModel is cleared
- Use `collectAsStateWithLifecycle()` in Compose for state observation
- No manual scope management needed

#### 4. Threading
**Follow standard threading pattern:**
```kotlin
.subscribeOn(Schedulers.io())           // For network/disk
.observeOn(AndroidSchedulers.mainThread()) // For UI updates
```

#### 5. Dependency Injection
**Use Koin 3.x with ViewModels:**
- **⚠️ Breaking Change:** Project uses Koin 3.5.3 (migrated from 2.0.1)
- Create feature-specific modules
- Use `viewModel { }` for ViewModels
- Use `factory { }` for UseCases (new instance each time)
- Use `single { }` for Repositories (singleton)
- Add koin-androidx-compose for Compose support
- Add modules to `applicationModules` list in `Modules.kt`
- Inject ViewModels with `by viewModel()` in Fragment/Activity
- **Note:** Some Koin APIs changed in 3.x - check imports and method calls

#### 6. UI Framework
**Current State:** 100% Jetpack Compose (Milestone 5+)
- **✅ Completed:** All UI migrated to Jetpack Compose with Material 3
- **✅ Zero XML layouts:** All layouts deleted
- **✅ No ViewBinding:** Skipped entirely, went directly to Compose
- **✅ No Kotlin Synthetics:** Completely removed
- **Always use Compose** for all UI code
- Use Material 3 components (Scaffold, TopAppBar, Card, etc.)
- Follow Compose best practices (stateless composables, state hoisting)

#### 7. Error Handling
**Implement proper error callbacks:**
- Always provide `onError` in Observable subscriptions
- Pass errors to View layer for user feedback
- Log errors appropriately

### 🔍 Before Making Changes

1. **Read existing code first** - Use Read tool before editing
2. **Check module boundaries** - Respect Clean Architecture layers
3. **Review naming conventions** - Follow established patterns
4. **Test compilation** - Run `./gradlew build` after changes
5. **Check Koin modules** - Ensure new dependencies are wired correctly

### 📝 When Adding Features (TOAD Pattern)

1. **Define State Models** - Create `*State.kt` with UiState/UiEvent/UiEffect
2. **Create ViewModel** - Implement state management with StateFlow and Channel
3. **Create UseCase** - Implement business logic in domain layer
4. **Add Repository** (if needed) - For new data sources
5. **Create Compose UI** - Build screen composable with state observation
6. **Create Fragment** (if needed) - Host Compose UI with ComposeView
7. **Setup Koin** - Create module with `viewModel { }` and add to composition
8. **Write Tests** - ViewModel unit tests + Compose UI integration tests
9. **Test** - Verify build and runtime behavior

### 🚫 What to Avoid

- **Don't** add Android dependencies to `domain` module
- **Don't** create cyclic dependencies between modules
- **Don't** create new Presenters or Contracts (MVP is deprecated)
- **Don't** put business logic in Composables (use ViewModel)
- **Don't** mutate state directly (use MutableStateFlow.value = ...)
- **Don't** collect state without lifecycle awareness (use collectAsStateWithLifecycle)
- **Don't** skip State/Event/Effect definitions for new features
- **Don't** mix MVP and TOAD patterns in the same feature
- **Don't** create god objects or god classes
- **Don't** add features without corresponding Koin modules
- **Don't** use XML layouts (use Jetpack Compose)

### 🔧 When Refactoring

1. **Maintain architecture** - Don't break Clean Architecture
2. **Follow TOAD pattern** - Use ViewModel/State/Event/Effect
3. **Migrate from MVP** - See MIGRATION_M6.md for guidance
4. **Update all layers** - If changing data models, update all layers
5. **Update Koin modules** - Adjust DI configuration as needed (use `viewModel { }`)
6. **Update tests** - ViewModel tests + Compose UI tests
7. **Run tests** - Verify nothing broke

### 📚 Common Pitfalls

#### State Management Issues
- Directly accessing `_state` instead of exposing `state.asStateFlow()`
- Forgetting to use `collectAsStateWithLifecycle()` (causes memory leaks)
- Mutating state from Composables instead of ViewModel
- Not handling all state branches in `when` expression
- Using `mutableStateOf` in ViewModel (use StateFlow instead)

#### Effect Handling Issues
- Using StateFlow for one-time effects (use Channel instead)
- Not collecting effects in LaunchedEffect
- Forgetting to handle all effect types

#### Threading Issues
- Accessing UI from background threads
- Blocking operations on main thread
- Not using suspend functions with proper context switching
- Forgetting `viewModelScope.launch` in ViewModel

#### Dependency Issues
- Circular dependencies in Koin
- Wrong dependency directions between modules
- Missing Koin module registrations
- Using `scope<Activity>` instead of `viewModel { }` for ViewModels

#### Compose UI Issues
- Not using `remember` for expensive computations
- Side effects in Composable body (use LaunchedEffect)
- Recomposition loops from unstable parameters
- Not using `derivedStateOf` for computed state

### 🎯 Best Practices Summary

1. **Separation of Concerns** - Each class has one responsibility
2. **TOAD Pattern** - State/Event/Effect for clear data flow
3. **Immutable State** - Use sealed classes and data classes
4. **Unidirectional Data Flow** - Events → ViewModel → State → UI
5. **Single Responsibility** - Each UseCase = one operation
6. **Coroutine Patterns** - Use Kotlin Coroutines consistently (suspend functions)
7. **Lifecycle Awareness** - Use viewModelScope, collectAsStateWithLifecycle
8. **Resource Cleanup** - ViewModel clears automatically, no manual cleanup
9. **Error Handling** - Sealed Error states, effect channels for toasts
10. **Testability** - Write testable code (ViewModels easy to test)
11. **Compose Best Practices** - Stateless composables, state hoisting
12. **Material 3** - Use Material 3 components consistently
13. **Consistency** - Follow established patterns (TOAD, Compose, Koin ViewModels)

---

## Additional Resources

### External Dependencies Documentation

- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [ActivityResultContracts](https://developer.android.com/training/basics/intents/result)
- [Koin](https://insert-koin.io/)
- [Retrofit](https://square.github.io/retrofit/)
- [Android Developers](https://developer.android.com/)

### Project-Specific

- **README.md** - Project overview
- **MIGRATION_M1.md** - Milestone 1 migration guide (Gradle 8.5, AGP 8.2.2, Version Catalog)
- **MIGRATION_M6.md** - Milestone 6 migration guide (MVP → TOAD pattern)
- **MILESTONE_5_SUMMARY.md** - Jetpack Compose migration summary
- **MANUAL_TESTING_M6.md** - Manual testing checklist for M6
- **API Documentation** - https://api.ratesapi.io/
- **Gradle Version Catalog** - https://docs.gradle.org/current/userguide/platforms.html
- **Jetpack Compose** - https://developer.android.com/jetpack/compose
- **Material 3** - https://m3.material.io/

---

## Change Log

### 2026-01-24 (Milestone 7 Complete - MVP Code Cleanup)
- **Milestone 7: Remove Deprecated MVP Code**
  - Deleted all deprecated MVP components (8 files):
    * BasePresenter.kt, BaseContract.kt (base classes)
    * MainPresenter.kt, MainContract.kt (main feature)
    * CurrencyPresenter.kt, CurrencyContract.kt (currency feature)
    * BaseFragment.kt, FragmentActivityCallbacks.kt (legacy support)
  - Cleaned up Koin modules (removed deprecated scopes):
    * MainModule.kt - removed MVP presenter scope
    * CurrencyModule.kt - removed MVP presenter scope + unused scopedOf import
  - Updated active code files:
    * MainActivity.kt - removed FragmentActivityCallbacks interface and setToolbar() method
    * CurrencyFragment.kt - removed commented MVP implementation code
  - Updated CLAUDE.md documentation:
    * Removed MVP Pattern section
    * Updated Base Classes table (only BaseActivity.kt remains)
    * Updated Koin Modules descriptions
    * Updated module structure diagrams
    * Marked M7 as complete in roadmap
  - Codebase now 100% TOAD pattern (no MVP remnants)
  - All 58 tests remain passing (verified by code inspection)
  - Zero functional impact - all active code uses TOAD pattern
- **Next:** Milestone 8 - Multi-module feature architecture

### 2026-01-24 (Milestone 6 Complete - TOAD Architecture Migration)
- **Milestone 6: MVP → TOAD Architecture Migration**
  - Added koin-androidx-compose 3.5.3 for ViewModel injection in Compose
  - Created TOAD pattern State/Event/Effect models:
    * CurrencyState.kt (CurrencyUiState, CurrencyUiEvent, CurrencyUiEffect)
    * Inline State/Event/Effect in MainViewModel.kt
  - Created ViewModels with StateFlow + Channel:
    * CurrencyViewModel.kt (state management for currency feature)
    * MainViewModel.kt (permission flow management)
  - Updated Koin modules for ViewModel injection:
    * CurrencyModule.kt - added `viewModel { CurrencyViewModel(...) }`
    * MainModule.kt - added `viewModel { MainViewModel() }`
  - Refactored Compose UI to use ViewModels:
    * CurrencyScreen.kt - collectAsStateWithLifecycle, handleEvent, effect collection
    * CurrencyFragment.kt - inject ViewModel, pass to Compose
    * MainActivity.kt - MainScreenContent with ViewModel, permission flow
  - Deprecated MVP components with @Deprecated annotations:
    * CurrencyPresenter.kt, CurrencyContract.kt
    * MainPresenter.kt, MainContract.kt
    * BasePresenter.kt, BaseContract.kt
  - Created comprehensive documentation:
    * MIGRATION_M6.md (814 lines) - complete migration guide
    * MANUAL_TESTING_M6.md (530 lines) - manual testing checklist
  - Added comprehensive testing:
    * CurrencyViewModelTest.kt (12 tests) - state, events, effects
    * MainViewModelTest.kt (14 tests) - permission flow
    * CurrencyScreenTest.kt (13 tests) - Compose UI integration
    * ComponentsTest.kt (19 tests) - component testing
    * Total: 58 tests (26 unit + 32 integration)
  - Updated CLAUDE.md with TOAD patterns and best practices
  - **Architecture Benefits:**
    * ✅ Configuration change survival (ViewModel persists)
    * ✅ Type-safe state with sealed classes
    * ✅ Better separation of concerns
    * ✅ Easier testing (no View mocking)
    * ✅ Perfect Compose integration
- **Next:** Milestone 7 - Remove deprecated MVP code (completed same day)

### 2026-01-24 (Milestone 5 Complete - Jetpack Compose Migration)
- **Milestone 5: Jetpack Compose UI Migration with Material 3**
  - Added Jetpack Compose BOM 2024.02.00 and all Compose dependencies
  - Applied Kotlin Compose Compiler Plugin (compatible with Kotlin 2.0.21)
  - Disabled ViewBinding completely (no longer needed)
  - Created Material 3 theme system (Color.kt, Type.kt, Theme.kt)
  - Created reusable Compose components:
    * AppTopBar (Material 3 TopAppBar with optional back button)
    * LoadingIndicator (centered progress indicator)
    * ErrorMessage (error display with retry button)
    * CurrencyListItem (Material 3 Card for currency display)
  - Created CurrencyScreen composable with state-based rendering
  - Migrated MainActivity from XML + ViewBinding to `setContent` + Compose
  - Migrated CurrencyFragment from XML + RecyclerView to ComposeView + LazyColumn
  - Replaced RecyclerView with Compose LazyColumn
  - Deleted all XML layout files (activity_main.xml, fragment_currency.xml, layout_currency_item.xml)
  - Deleted RecyclerView adapter and viewholder (CurrencyListAdapter.kt, CurrencyListViewHolder.kt)
  - Removed all Kotlin synthetic imports (kotlinx.android.synthetic)
  - Updated CurrencyFragment to use `mutableStateOf` for UI state management
  - Kept MVP pattern (Presenter/Contract) - will migrate to TOAD in M6
  - Added Compose Previews for all components
  - All UI is now 100% Jetpack Compose (zero XML layouts)
- **Next:** Milestone 6 - Architecture Evolution (MVP → TOAD with ViewModel + StateFlow)

### 2026-01-11 (Milestone 4 Complete - Permission Modernization)
- **Milestone 4: Migrate RxPermissions to ActivityResultContracts**
  - Removed RxPermissions 0.12 dependency (last remaining RxJava dependency)
  - Added AndroidX Activity KTX 1.8.1 for ActivityResultContracts
  - Migrated permission handling from RxJava 2 to ActivityResultContracts API
  - Updated MainPresenter to extend BasePresenter (no more CompositeDisposable)
  - Simplified MainPresenter to delegation pattern (requests permission from View)
  - Updated MainActivity to implement ActivityResultLauncher
  - Added permission pre-check (already granted) and rationale handling
  - Improved MainContract interfaces (removed error callbacks)
  - Updated MainModule with explicit type specification for Presenter
  - Simplified BaseActivity (removed RxPermissions property)
  - Updated CLAUDE.md with ActivityResultContracts examples
  - Updated testing strategy for coroutine-based approach
  - Updated best practices and common pitfalls documentation
  - All async operations now use Kotlin Coroutines (no RxJava anywhere)
  - Build is now completely RxJava-free
- **Verified:** No RxJava transitive dependencies remain in project

### 2026-01-10 (Milestone 1 Complete - Enhanced)
- **Milestone 1: Modernization Complete**
  - Upgraded Gradle 6.1.1 → 8.5
  - Upgraded AGP 7.0.0 → 8.2.2
  - Upgraded Kotlin 1.3.72 → 2.0.21 (Kotlin 2.x with K2 compiler)
  - Upgraded JDK target 17 → 21 (LTS)
  - Implemented Gradle Version Catalog for dependency management
  - Updated all AndroidX libraries to latest versions
  - Updated Koin 2.0.1 → 3.5.3 with breaking changes fixed
  - Removed deprecated jcenter() repository
  - Removed deprecated kotlin-android-extensions plugin
  - Enabled ViewBinding (migration deferred to Milestone 2 - Compose)
  - Added namespace declarations to all modules
  - Updated targetSdk 29 → 34
  - Fixed Koin 3.x breaking changes (scope management)
  - Implemented manual scope creation with createScope() for Activities/Fragments
  - Proper scope lifecycle management (lazy init, close on destroy)
- Updated Technology Stack section with Kotlin 2.0.21 and JDK 21
- Updated Gradle Configuration section for Version Catalog
- Updated Common Tasks section with new dependency management approach
- Added Koin 3.x migration examples with scope management patterns
- Documented JDK 21 compatibility

### 2026-01-10 (Initial)
- Initial CLAUDE.md creation
- Documented current architecture state
- Added comprehensive guidelines for AI assistants
- Included common tasks and workflows

---

## Project Roadmap

### Completed Milestones ✅

| Milestone | Status | Completed | Focus |
|-----------|--------|-----------|-------|
| **M1** | ✅ | 2026-01-10 | Build modernization (Gradle 8.5, Kotlin 2.0.21, JDK 21) |
| **M3** | ✅ | 2026-01-10 | Kotlin Coroutines & Flow (replace RxJava) |
| **M4** | ✅ | 2026-01-11 | Permission modernization (ActivityResultContracts) |
| **M5** | ✅ | 2026-01-24 | Jetpack Compose UI migration with Material 3 |
| **M6** | ✅ | 2026-01-24 | Architecture modernization (MVP → TOAD) |
| **M7** | ✅ | 2026-01-24 | Code cleanup (removed deprecated MVP code) |

### Upcoming Milestones 🚀

| Milestone | Status | Target | Focus |
|-----------|--------|--------|-------|
| **M8** | 📋 Planned | TBD | Multi-module feature architecture |
| **M9** | 📋 Planned | TBD | Comprehensive E2E testing |

### Milestone 5: Jetpack Compose UI Migration ✅ COMPLETE

**Scope:** (All completed 2026-01-24)
- ✅ Migrated all Activities/Fragments from legacy views to Compose
- ✅ Skipped ViewBinding entirely (not needed with Compose)
- ✅ Removed Kotlin Synthetics completely
- ✅ Adopted Compose Previews for UI development
- ⏳ Compose Navigation (deferred to M6)

**Key Tasks Completed:**
1. ✅ Created Compose UI for MainActivity (`setContent`)
2. ✅ Created Compose UI for CurrencyFragment (ComposeView)
3. ✅ Migrated RecyclerView to LazyColumn
4. ✅ Removed CurrencyListAdapter/ViewHolder (replaced with Compose)
5. ✅ Removed all legacy layout XMLs
6. ✅ Created Material 3 theme system
7. ✅ Built reusable Compose components

**Benefits Achieved:**
- ✅ Single language for UI (Kotlin, no XML)
- ✅ Better composability and reusability
- ✅ Built-in state management with `mutableStateOf`
- ✅ Compose Previews enabled
- ✅ Modern Material 3 integration

**See:** MILESTONE_5_SUMMARY.md for complete details

### Milestone 6: Architecture Evolution (MVP → TOAD) ✅ COMPLETE

**Status:** Completed 2026-01-24

**What Was Done:**
- ✅ Created State/Event/Effect models (CurrencyState.kt)
- ✅ Implemented ViewModels with StateFlow + Channel
- ✅ Updated all Compose UI to use ViewModels
- ✅ Refactored Koin modules for ViewModel injection
- ✅ Deprecated all MVP components (@Deprecated annotations)
- ✅ Wrote 58 comprehensive tests (26 unit + 32 integration)
- ✅ Created migration documentation (MIGRATION_M6.md)
- ✅ Created manual testing checklist (MANUAL_TESTING_M6.md)
- ✅ Updated CLAUDE.md with TOAD patterns

**Pattern Selected: TOAD (The Opinionated Android Design)**
- **UiState:** Sealed class for screen state (Idle, Loading, Success, Error)
- **UiEvent:** Sealed class for user interactions
- **UiEffect:** Channel for one-time side effects (toasts, navigation)
- **ViewModel:** State holder with viewModelScope
- **StateFlow:** Reactive state observation
- **Unidirectional Data Flow:** Event → ViewModel → State → UI

**Key Changes Made:**
- ✅ Removed Presenter layer (ViewModel replaces it)
- ✅ Introduced UiState/UiEvent/UiEffect pattern
- ✅ Refactored Koin modules: `viewModel { }` instead of `scope<Activity>`
- ✅ Updated Compose UI: `collectAsStateWithLifecycle()` for state observation
- ✅ Simplified error handling: Sealed Error state + effect channel
- ✅ Parallel migration strategy (MVP deprecated, not deleted yet)

**Benefits Achieved:**
- ✅ Configuration changes: State survives rotation automatically
- ✅ Separation of concerns: UI, ViewModel, UseCase clearly separated
- ✅ Easier state management: StateFlow handles complexity
- ✅ Improved testability: No View mocking, direct state assertions
- ✅ Modern Android standards: Follows official Google recommendations
- ✅ Better Compose integration: Perfect fit for declarative UI

**Testing Coverage:**
- **ViewModel Unit Tests:** 26 tests (CurrencyViewModel: 12, MainViewModel: 14)
- **Compose UI Integration Tests:** 32 tests (CurrencyScreen: 13, Components: 19)
- **Total:** 58 tests with high coverage

**See:** MIGRATION_M6.md for complete migration guide and examples

---

**For questions or clarifications about this codebase, use the Task tool with subagent_type=Explore to investigate specific areas in depth.**
