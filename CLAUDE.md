# CLAUDE.md - AI Assistant Guide for BasicFrameworkProject

**Last Updated:** 2026-01-10
**Project Version:** 1.0
**Target SDK:** Android 10 (API 29)

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

**BasicFrameworkProject** is a demonstration Android application showcasing Clean Architecture principles with MVP pattern. The app displays currency exchange rates using the rates API.

### Key Characteristics
- **Package Name:** `io.github.wawakaka.basicframeworkproject`
- **Language:** Kotlin 1.3.72
- **Build System:** Gradle 7.0.0
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 29 (Android 10)
- **Primary Feature:** Currency exchange rate display

### Project Purpose
This is a framework/template project demonstrating professional Android development practices with:
- Clean Architecture separation
- Reactive programming patterns
- Dependency injection
- Modern Android components

---

## Architecture & Design Patterns

### Clean Architecture Layers

The project follows a strict layered architecture:

```
┌─────────────────────────────────────────────────┐
│  Presentation Layer (app module)                │
│  - Activities, Fragments, Presenters            │
│  - UI Components, Adapters                      │
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

### MVP Pattern Implementation

Each feature implements the MVP pattern with clear contracts:

**Contract Definition (`*Contract.kt`):**
```kotlin
interface FeatureContract {
    interface View : BaseContract.View {
        fun onGetDataSuccess(data: List<T>)
        fun onGetDataFailed(throwable: Throwable)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onButtonClickedEvent()
    }
}
```

**Key Principles:**
- **View:** Passive, only updates UI (Fragment/Activity)
- **Presenter:** Handles business logic, lifecycle-aware
- **Contract:** Explicit interface between View and Presenter
- **BaseContract:** Common view/presenter interfaces in `app/src/main/java/io/github/wawakaka/basicframeworkproject/base/BaseContract.kt`

### Dependency Injection (Koin)

**Module Organization:**
- Each feature has its own Koin module (`*Module.kt`)
- Modules are combined in `Modules.kt` → `applicationModules` list
- Scoped injection: `factory`, `single`, `scope<Activity>`

**Example Module Structure:**
```kotlin
val featureModule = module {
    scope<FeatureActivity> {
        scoped<FeatureContract.Presenter> { FeaturePresenter(get()) }
    }
}
```

**Initialization:** Koin is started in `App.kt:onCreate()`

### Reactive Programming Flow

All async operations use RxJava 2:

```
User Action → Presenter
    ↓
UseCase (domain layer)
    ↓ subscribeOn(Schedulers.io())
Repository → API Call
    ↓ observeOn(AndroidSchedulers.mainThread())
    ↓
Presenter → View (UI update)
```

**Key Components:**
- **CompositeDisposable:** Used in presenters for lifecycle management
- **Schedulers:** IO for network, mainThread for UI
- **Observable chains:** Transformations with `map`, `flatMap`

---

## Module Structure

### 1. `app` Module (Presentation Layer)

**Location:** `/app/src/main/java/io/github/wawakaka/basicframeworkproject/`

**Key Directories:**
```
app/
├── App.kt                          # Application class, Koin initialization
├── Modules.kt                      # Combined Koin modules
├── base/                           # Base classes for MVP
│   ├── BaseActivity.kt             # Base with RxPermissions
│   ├── BaseFragment.kt             # Base with callbacks
│   ├── BaseContract.kt             # MVP base interfaces
│   └── FragmentActivityCallbacks.kt
├── presentation/                   # Main screen (permission check)
│   ├── MainActivity.kt
│   ├── MainPresenter.kt
│   ├── MainContract.kt
│   ├── MainModule.kt
│   └── content/                    # Currency feature
│       ├── CurrencyFragment.kt     # RecyclerView display
│       ├── CurrencyPresenter.kt    # Business logic
│       ├── CurrencyContract.kt
│       ├── CurrencyModule.kt
│       └── adapter/
│           ├── CurrencyListAdapter.kt
│           └── CurrencyListViewHolder.kt
├── domain/
│   └── domainModules.kt            # Domain layer DI setup
└── utilities/
    └── ViewExtensions.kt           # UI helper extensions
```

**Dependencies:**
- `domain` module
- AndroidX libraries (AppCompat, Navigation, Material)
- Koin for Android
- RxAndroid, RxPermissions, RxBinding

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
    fun execute(base: String): Observable<List<Pair<String, Double>>> {
        return repository.getLatestCurrencyRates(base)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { /* transform data */ }
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
    fun getLatestWithBase(@Query("base") base: String): Observable<CurrencyRatesResponse>
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
- **Adapters:** RxJava2 call adapter

---

## Technology Stack

### Core Technologies

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Kotlin | 1.3.72 | Primary development language |
| **Build** | Gradle | 7.0.0 | Build automation |
| **UI Framework** | Android SDK | API 29 | Application framework |

### Key Libraries

#### Reactive Programming
- **RxJava 2:** `2.2.10` - Reactive streams
- **RxKotlin:** `2.3.0` - Kotlin extensions for RxJava
- **RxAndroid:** `2.1.1` - Android schedulers
- **RxBinding:** `3.1.0` - UI event bindings
- **RxPermissions:** `0.10.2` - Permission handling

#### Networking
- **Retrofit:** `2.9.0` - REST API client
- **OkHttp:** `4.7.2` - HTTP client
- **Gson:** `2.8.6` - JSON serialization
- **Chuck:** `1.1.0` - Network debugging

#### Dependency Injection
- **Koin:** `2.0.1` - Lightweight DI framework
- **Koin Android Scope:** `2.0.1` - Android lifecycle scopes

#### UI Components
- **AndroidX AppCompat:** `1.1.0`
- **Material Design:** `1.1.0`
- **ConstraintLayout:** `1.1.3`
- **Navigation Component:** `2.3.0`
- **Anko:** `0.10.8` - Kotlin helpers

#### Other
- **MultiDex:** `2.0.1` - Support for large apps

### Repository Information
- **Maven Repositories:** Google, JCenter, JitPack
- **Dependency Management:** Centralized in `dependencies.gradle`

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
- `dependencies.gradle` - Centralized dependency versions
- `settings.gradle` - Module inclusions
- `gradle.properties` - Build properties
- `dep/build-lib.gradle` - Shared library module template

**Version Variables:** Defined in `dependencies.gradle` with `ext` block

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
| **Contract Interface** | `*Contract` | `CurrencyContract` |
| **Presenter** | `*Presenter` | `CurrencyPresenter` |
| **Activity** | `*Activity` | `MainActivity` |
| **Fragment** | `*Fragment` | `CurrencyFragment` |
| **API Interface** | `*Api` | `CurrencyRatesApi` |
| **Repository** | `*Repository` | `CurrencyRatesRepository` |
| **UseCase** | `*Usecase` | `GetLatestRatesUsecase` |
| **Response Model** | `*Response` | `CurrencyRatesResponse` |
| **Koin Module** | `*Module` | `CurrencyModule` |
| **Adapter** | `*Adapter` | `CurrencyListAdapter` |
| **ViewHolder** | `*ViewHolder` | `CurrencyListViewHolder` |

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

### RxJava Patterns

#### Resource Management
Always use `CompositeDisposable` in presenters:

```kotlin
class MyPresenter : BasePresenter<MyContract.View>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onViewDetached() {
        compositeDisposable.clear()
    }
}
```

#### Threading
Standard pattern for API calls:

```kotlin
repository.getData()
    .subscribeOn(Schedulers.io())           // Network on IO thread
    .observeOn(AndroidSchedulers.mainThread()) // Results on main thread
    .subscribe({ data ->
        // Success on main thread
    }, { error ->
        // Error on main thread
    })
```

### MVP Lifecycle

**Fragment/Activity:**
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.onViewAttached(this)  // Attach view
}

override fun onDestroyView() {
    presenter.onViewDetached()       // Detach view, cleanup
    super.onDestroyView()
}
```

**Presenter:**
```kotlin
override fun onViewAttached(view: View) {
    this.view = view
    // Initialize subscriptions
}

override fun onViewDetached() {
    compositeDisposable.clear()      // Clear RxJava subscriptions
    this.view = null
}
```

### Error Handling

**In Presenters:**
```kotlin
.subscribe({ data ->
    view?.onGetDataSuccess(data)
}, { throwable ->
    view?.onGetDataFailed(throwable)
})
```

**In Views:**
```kotlin
override fun onGetDataFailed(throwable: Throwable) {
    Log.e(TAG, "Error: ${throwable.message}")
    // Show error message to user
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

| File | Purpose |
|------|---------|
| `BaseActivity.kt` | Base activity with RxPermissions |
| `BaseFragment.kt` | Base fragment with activity callbacks |
| `BaseContract.kt` | MVP base interfaces |
| `FragmentActivityCallbacks.kt` | Fragment-Activity communication |

### Koin Modules

| File | Purpose |
|------|---------|
| `Modules.kt` | Application module composition |
| `MainModule.kt` | Main screen dependencies |
| `CurrencyModule.kt` | Currency feature dependencies |
| `domainModules.kt` | Domain layer dependencies |

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

### Adding a New Feature

1. **Create MVP Contract** (`*Contract.kt`):
```kotlin
interface FeatureContract {
    interface View : BaseContract.View {
        fun onDataLoaded(data: DataType)
        fun onError(error: Throwable)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun loadData()
    }
}
```

2. **Create Presenter** (`*Presenter.kt`):
```kotlin
class FeaturePresenter(
    private val useCase: FeatureUseCase
) : BasePresenter<FeatureContract.View>(), FeatureContract.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun loadData() {
        useCase.execute()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ data ->
                view?.onDataLoaded(data)
            }, { error ->
                view?.onError(error)
            })
            .addTo(compositeDisposable)
    }

    override fun onViewDetached() {
        compositeDisposable.clear()
    }
}
```

3. **Create Fragment/Activity** implementing `FeatureContract.View`

4. **Create Koin Module** (`*Module.kt`):
```kotlin
val featureModule = module {
    scope<FeatureActivity> {
        scoped<FeatureContract.Presenter> {
            FeaturePresenter(get())
        }
    }
}
```

5. **Add to `Modules.kt`:**
```kotlin
val applicationModules = listOf(
    // existing modules...
    featureModule
)
```

### Adding a New API Endpoint

1. **Define API interface** in `repository` module:
```kotlin
interface FeatureApi {
    @GET("endpoint")
    fun getData(@Query("param") param: String): Observable<ResponseModel>
}
```

2. **Create Response Model:**
```kotlin
data class ResponseModel(
    @SerializedName("field") val field: String?
)
```

3. **Create Repository:**
```kotlin
class FeatureRepository(private val api: FeatureApi) {
    fun getData(param: String): Observable<ResponseModel> {
        return api.getData(param)
    }
}
```

4. **Create Repository factory** in `Repository.kt`

5. **Create UseCase** in `domain` module

6. **Wire up in Koin modules**

### Adding Dependencies

1. **Add version variable** to `dependencies.gradle`:
```kotlin
ext {
    newlib = "1.0.0"
}
```

2. **Add library definition** to `lib` map:
```kotlin
lib = [
    newlib: "com.example:newlib:$newlib"
]
```

3. **Add to module's `build.gradle`:**
```kotlin
dependencies {
    implementation lib.newlib
}
```

### Handling Permissions

Use RxPermissions in Activity/Fragment:

```kotlin
rxPermissions.request(Manifest.permission.CAMERA)
    .subscribe { granted ->
        if (granted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }
```

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

### Current State
- **Unit Tests:** Minimal setup with JUnit 4
- **Instrumented Tests:** Basic AndroidJUnit4 runner configured
- **Test Location:** `app/src/test/` (unit), `app/src/androidTest/` (instrumented)

### Test Configuration

```kotlin
// build.gradle
testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

dependencies {
    testImplementation 'junit:junit:4.13'
}
```

### Recommended Testing Approach

#### Unit Tests (Domain Layer)
- Test UseCases in isolation
- Mock repositories using RxJava `TestObserver`
- Test data transformations

#### Presenter Tests
- Test presenter logic with mocked views
- Verify view method calls
- Test error handling

#### Integration Tests
- Test repository with real API (or mock server)
- Test end-to-end data flow

---

## Important Notes for AI Assistants

### ⚠️ Critical Guidelines

#### 1. Module Dependencies
**NEVER violate the dependency hierarchy:**
- `app` → `domain` → `repository` → `restapi`
- Lower layers must not depend on higher layers
- Keep `domain` module pure Kotlin (no Android imports)

#### 2. Architecture Patterns
**Always follow MVP pattern:**
- Create `*Contract.kt` interfaces first
- Keep Views passive (UI updates only)
- Put business logic in Presenters
- Keep UseCases focused on single operations

#### 3. RxJava Resource Management
**ALWAYS dispose subscriptions:**
- Use `CompositeDisposable` in presenters
- Clear in `onViewDetached()`
- Use `.addTo(compositeDisposable)` or manual disposal

#### 4. Threading
**Follow standard threading pattern:**
```kotlin
.subscribeOn(Schedulers.io())           // For network/disk
.observeOn(AndroidSchedulers.mainThread()) // For UI updates
```

#### 5. Dependency Injection
**Use Koin properly:**
- Create feature-specific modules
- Use appropriate scopes (`factory`, `single`, `scope<Activity>`)
- Add modules to `applicationModules` list in `Modules.kt`

#### 6. View Binding
**Note:** Project uses Kotlin Synthetics (deprecated)
- When modernizing, migrate to ViewBinding or Jetpack Compose
- Don't mix binding approaches

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

### 📝 When Adding Features

1. **Start with Contract** - Define View and Presenter interfaces
2. **Create UseCase** - Implement business logic in domain layer
3. **Add Repository** (if needed) - For new data sources
4. **Implement Presenter** - Wire UseCase to View
5. **Create View** - Fragment/Activity implementing Contract.View
6. **Setup Koin** - Create module and add to composition
7. **Test** - Verify build and runtime behavior

### 🚫 What to Avoid

- **Don't** add Android dependencies to `domain` module
- **Don't** create cyclic dependencies between modules
- **Don't** forget to dispose RxJava subscriptions
- **Don't** put business logic in Views (Fragments/Activities)
- **Don't** access UI from background threads
- **Don't** skip Contract definitions for new features
- **Don't** mix different architectural patterns
- **Don't** create god objects or god classes
- **Don't** add features without corresponding Koin modules

### 🔧 When Refactoring

1. **Maintain architecture** - Don't break Clean Architecture
2. **Keep MVP pattern** - Preserve View/Presenter separation
3. **Update all layers** - If changing data models, update all layers
4. **Update Koin modules** - Adjust DI configuration as needed
5. **Run tests** - Verify nothing broke

### 📚 Common Pitfalls

#### Memory Leaks
- Not clearing CompositeDisposable
- Holding View references after detach
- Static references to Activities/Fragments

#### Threading Issues
- Accessing UI from background threads
- Not using proper schedulers
- Blocking operations on main thread

#### Dependency Issues
- Circular dependencies in Koin
- Wrong dependency directions between modules
- Missing Koin module registrations

### 🎯 Best Practices Summary

1. **Separation of Concerns** - Each class has one responsibility
2. **Interface Segregation** - Use contracts for decoupling
3. **Dependency Inversion** - Depend on abstractions (interfaces)
4. **Single Responsibility** - Each UseCase = one operation
5. **Reactive Patterns** - Use RxJava consistently
6. **Lifecycle Awareness** - Respect Android lifecycle
7. **Resource Cleanup** - Dispose, clear, nullify appropriately
8. **Error Handling** - Handle all error cases
9. **Testability** - Write testable, mockable code
10. **Consistency** - Follow established patterns

---

## Additional Resources

### External Dependencies Documentation

- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [RxJava 2](https://github.com/ReactiveX/RxJava)
- [Koin](https://insert-koin.io/)
- [Retrofit](https://square.github.io/retrofit/)
- [Android Developers](https://developer.android.com/)

### Project-Specific

- **README.md** - Project overview
- **API Documentation** - https://api.ratesapi.io/

---

## Change Log

### 2026-01-10
- Initial CLAUDE.md creation
- Documented current architecture state
- Added comprehensive guidelines for AI assistants
- Included common tasks and workflows

---

**For questions or clarifications about this codebase, use the Task tool with subagent_type=Explore to investigate specific areas in depth.**
