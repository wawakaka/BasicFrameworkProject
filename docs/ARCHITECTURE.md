# Architecture Guide

Comprehensive guide to the BasicFrameworkProject's architecture and design patterns.

**Last Updated:** 2026-01-11
**Milestone:** 5 Complete

---

## Table of Contents

1. [Overview](#overview)
2. [Clean Architecture](#clean-architecture)
3. [MVP Pattern](#mvp-pattern)
4. [Layer Responsibilities](#layer-responsibilities)
5. [Dependency Flow](#dependency-flow)
6. [Design Patterns](#design-patterns)
7. [Module Organization](#module-organization)
8. [Best Practices](#best-practices)

---

## Overview

The project follows **Clean Architecture** principles with an **MVP (Model-View-Presenter)** pattern. This ensures:

- **Separation of Concerns:** Each layer has a specific responsibility
- **Testability:** Business logic is isolated from Android dependencies
- **Maintainability:** Easy to understand and modify code
- **Scalability:** Easy to add new features without affecting existing code
- **Dependency Management:** Clear, one-way dependency flow

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│              (app module - Jetpack Compose)                 │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │   Screen     │  │  Components  │  │  Material 3    │  │
│  │ (Composable) │  │ (Reusable)   │  │  Theme/Style   │  │
│  └──────────────┘  └──────────────┘  └────────────────┘  │
│         │                │                    │             │
│         └────────────────┴────────────────────┘             │
│                         │                                   │
│              ┌──────────▼──────────┐                        │
│              │  MVP Presenter      │                        │
│              │  (Business Logic)   │                        │
│              └──────────┬──────────┘                        │
└─────────────────────────┼──────────────────────────────────┘
                          │ uses
┌─────────────────────────▼──────────────────────────────────┐
│                    Domain Layer                            │
│              (domain module - Pure Kotlin)                 │
│                                                            │
│         ┌──────────────────────────────────┐               │
│         │    Use Cases / Interactors       │               │
│         │  (Business Logic, No Android)    │               │
│         └──────────────┬───────────────────┘               │
│                        │                                   │
│            ┌───────────▼────────────┐                      │
│            │ Repository Interfaces  │                      │
│            │  (Contracts)           │                      │
│            └───────────┬────────────┘                      │
└──────────────────────────┼─────────────────────────────────┘
                           │ implements
┌──────────────────────────▼─────────────────────────────────┐
│                    Data Layer                              │
│            (repository module - Repositories)              │
│                                                            │
│         ┌────────────────────────────────┐                │
│         │   Repository Implementation    │                │
│         │   (Data Access, Mapping)       │                │
│         └────────────────┬───────────────┘                │
│                          │                                │
│         ┌────────────────▼───────────────┐                │
│         │   API Interfaces, Models       │                │
│         │   (Data Contracts)             │                │
│         └────────────────┬───────────────┘                │
└──────────────────────────┼─────────────────────────────────┘
                           │ uses
┌──────────────────────────▼─────────────────────────────────┐
│                   Network Layer                            │
│        (restapi module - Retrofit, OkHttp)                 │
│                                                            │
│         ┌────────────────────────────────┐                │
│         │   Retrofit API Implementation  │                │
│         │   HTTP Client Configuration   │                │
│         │   Interceptors                 │                │
│         └────────────────────────────────┘                │
└─────────────────────────────────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │  REST API   │
                    │  External   │
                    └─────────────┘
```

---

## Clean Architecture

### Principles

**Clean Architecture** organizes code into layers where:

1. **Inner layers** contain high-level business logic
2. **Outer layers** contain low-level implementation details
3. **Dependencies** always point inward
4. **Inner layers** know nothing about outer layers

### Benefits

- **Independence:** Libraries, frameworks, databases can be swapped
- **Testability:** Business logic can be tested without UI or database
- **Maintainability:** Changes in external layers don't affect business logic
- **Flexibility:** Easy to add new features or modify existing ones

### Layer Guidelines

**Presentation Layer:**
- Should only know how to display data
- Should not contain business logic
- Should handle user interactions
- Should be easily testable with mocks

**Domain Layer:**
- Pure Kotlin - no Android dependencies
- Contains business logic (Use Cases)
- Should be independent of frameworks
- Highly testable

**Data Layer:**
- Implements data access abstraction
- Handles data mapping and caching
- Should not expose internal implementation
- Can be easily mocked for testing

**Network Layer:**
- External service configuration
- HTTP client setup
- Interceptors and middleware
- Should be isolated from business logic

---

## MVP Pattern

### Overview

The **Model-View-Presenter (MVP)** pattern separates concerns into three components:

```
┌─────────────┐
│   View      │  Displays data, captures user input
│ (Composable)│  Implements View interface
└──────┬──────┘
       │ calls
       ▼
┌─────────────┐
│ Presenter   │  Contains business logic
│(Controller) │  Implements Presenter interface
└──────┬──────┘
       │ uses
       ▼
┌─────────────┐
│   Model     │  Data and business rules
│ (Use Cases) │  Independent of Presentation
└─────────────┘
```

### Components

**View (Composable Screen):**
- Displays UI based on state
- Captures user interactions
- Implements `Contract.View` interface
- Does not contain business logic
- Can be easily replaced with different UI

**Presenter:**
- Contains logic for feature
- Manages interaction between View and Model
- Implements `Contract.Presenter` interface
- Lifecycle-aware with proper cleanup
- Communicates with View through interface

**Model (Use Cases + Data):**
- Business logic and data access
- No Android framework dependencies
- Pure Kotlin code
- Can be tested independently

### Contract Definition

```kotlin
interface CurrencyContract {
    // View interface - Presenter calls these methods
    interface View {
        fun showLoading()
        fun hideLoading()
        fun onGetDataSuccess(data: List<Pair<String, Double>>)
        fun onGetDataFailed(throwable: Throwable)
    }

    // Presenter interface - View calls these methods
    interface Presenter {
        fun onButtonClickedEvent()
        fun onDestroy()
    }
}
```

### Implementation Example

**View (Composable):**
```kotlin
@Composable
fun CurrencyScreen(presenter: CurrencyPresenter = koinInject()) {
    var state by remember { mutableStateOf(CurrencyState()) }

    // Create view adapter for contract
    val view = object : CurrencyContract.View {
        override fun showLoading() {
            state = state.copy(isLoading = true)
        }
        override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
            state = state.copy(rates = data, isLoading = false)
        }
        // ... other methods
    }

    // Lifecycle management
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.onButtonClickedEvent()
    }
    DisposableEffect(Unit) {
        onDispose { presenter.detach() }
    }

    // Render UI
    CurrencyContent(state = state)
}
```

**Presenter:**
```kotlin
class CurrencyPresenter(
    private val usecase: GetLatestRatesUsecase
) : BasePresenter<CurrencyContract.View>(), CurrencyContract.Presenter {

    override fun onButtonClickedEvent() {
        presenterScope.launch {
            view?.showLoading()
            try {
                val data = usecase.getLatestCurrencyRates()
                view?.onGetDataSuccess(data)
            } catch (e: Exception) {
                view?.onGetDataFailed(e)
            } finally {
                view?.hideLoading()
            }
        }
    }
}
```

**Model (Use Case):**
```kotlin
class GetLatestRatesUsecase(
    private val repository: CurrencyRatesRepository
) {
    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        val response = repository.getLatestCurrencyRates()
        return response.rates?.toList() ?: emptyList()
    }
}
```

---

## Layer Responsibilities

### Presentation Layer (app module)

**Responsibilities:**
- Display UI using Compose
- Capture user interactions
- Update UI based on state changes
- Handle lifecycle events

**Contains:**
- Composable screens and components
- MVP presenters
- Contract interfaces
- Theme and styling
- Koin DI configuration

**Does NOT contain:**
- Network code
- Database access
- Business logic

### Domain Layer (domain module)

**Responsibilities:**
- Define business logic
- Define data contracts
- Execute use cases

**Contains:**
- Use cases (business operations)
- Data models (Domain models)
- Repository interfaces
- Entities

**Does NOT contain:**
- Android framework code
- UI components
- Database or network implementation

**Key Rule:** Pure Kotlin, no Android dependencies

### Data Layer (repository module)

**Responsibilities:**
- Implement data access
- Provide data mapping
- Implement repository contracts
- Handle data caching (if needed)

**Contains:**
- Repository implementations
- API interfaces (Retrofit)
- Data models (Response models)
- Data mappers

**Does NOT contain:**
- Business logic
- UI code
- Direct API calls (isolated in network layer)

### Network Layer (restapi module)

**Responsibilities:**
- Configure HTTP client
- Set up interceptors
- Implement Retrofit services
- Handle HTTP concerns

**Contains:**
- Retrofit configuration
- OkHttp client setup
- HTTP interceptors
- API service implementations

**Does NOT contain:**
- Business logic
- Data mapping (done in Data layer)
- Repository logic

---

## Dependency Flow

### Dependency Direction

```
Presentation Layer
    ↓ depends on
Domain Layer
    ↓ depends on
Data Layer
    ↓ depends on
Network Layer
```

**Critical Rule:** Dependencies flow INWARD only. Never have:
- Domain depending on Presentation
- Data depending on Domain
- Network depending on Data

### Dependency Injection

Koin manages all dependencies:

```kotlin
// Network Layer
val restApiModule = module {
    single { RestApi.provideRetrofit() }
}

// Data Layer
val dataModule = module {
    single { CurrencyRatesApi(get()) }
    single { CurrencyRatesRepository(get()) }
}

// Domain Layer
val domainModule = module {
    factory { GetLatestRatesUsecase(get()) }
}

// Presentation Layer
val presentationModule = module {
    factory { CurrencyPresenter(get()) }
}

// App initialization
val applicationModules = listOf(
    restApiModule,
    dataModule,
    domainModule,
    presentationModule
)

// In Application.onCreate()
startKoin {
    modules(applicationModules)
}
```

### Accessing Dependencies

In Composables:
```kotlin
@Composable
fun MyScreen(
    presenter: MyPresenter = koinInject(),  // Injected by Koin
    modifier: Modifier = Modifier
) {
    // presenter is ready to use
}
```

In Presenters:
```kotlin
class MyPresenter(
    private val usecase: MyUsecase  // Injected via constructor
) : BasePresenter<MyContract.View>() {
    // usecase ready in constructor
}
```

---

## Design Patterns

### Repository Pattern

```kotlin
// Contract (Domain layer)
interface CurrencyRatesRepository {
    suspend fun getLatestCurrencyRates(): CurrencyRatesResponse
}

// Implementation (Data layer)
class CurrencyRatesRepositoryImpl(
    private val api: CurrencyRatesApi
) : CurrencyRatesRepository {
    override suspend fun getLatestCurrencyRates(): CurrencyRatesResponse {
        return api.getLatestWithBase("EUR")
    }
}
```

**Benefits:**
- Decouples business logic from data sources
- Easy to mock for testing
- Can switch implementations without affecting business logic

### Use Case Pattern

```kotlin
class GetLatestRatesUsecase(
    private val repository: CurrencyRatesRepository
) {
    suspend fun getLatestCurrencyRates(): List<Pair<String, Double>> {
        val response = repository.getLatestCurrencyRates()
        return response.rates?.toList() ?: emptyList()
    }
}
```

**Benefits:**
- Single responsibility per use case
- Easy to test
- Reusable across different presenters
- Clear business logic

### Composition Root (Koin)

```kotlin
val currencyModule = module {
    // Factories for screens/presenters
    factory { CurrencyPresenter(get()) }

    // Singletons for services
    single { CurrencyRatesRepository(get()) }
    single { GetLatestRatesUsecase(get()) }
}
```

**Benefits:**
- Centralized dependency configuration
- Easy to change implementations
- Enables testing with mocks

### State Management (Compose)

```kotlin
@Composable
fun CurrencyScreen() {
    var state by remember { mutableStateOf(CurrencyState()) }

    // State updates trigger recomposition
    Button(onClick = {
        state = state.copy(isRefreshing = true)
    })
}
```

---

## Module Organization

### Module Dependency Graph

```
app module
  ├── depends on: domain
  ├── depends on: restapi (for Retrofit in DI)
  └── contains: Presentation layer

domain module
  ├── depends on: repository (for interfaces)
  └── contains: Domain layer

repository module
  ├── depends on: restapi (for API definitions)
  └── contains: Data layer

restapi module
  └── contains: Network layer (no dependencies)
```

### Build Isolation

Each module:
- Has independent `build.gradle`
- Declares its own dependencies
- Follows the `build-lib.gradle` template
- Is independently compilable

### Module Guidelines

**Presentation (app):**
- Android-specific code (Compose, Activity)
- UI components and screens
- Koin DI setup

**Domain:**
- Pure Kotlin (no Android imports)
- Business logic
- Use cases and entities

**Data:**
- Repository implementations
- API definitions
- Data models and mappers

**Network:**
- Retrofit and OkHttp setup
- HTTP interceptors
- API client configuration

---

## Best Practices

### Code Organization

1. **One Responsibility Per Class**
   - Presenter handles logic only
   - Repository handles data only
   - Use Case performs single operation

2. **Use Interfaces**
   - Define contracts before implementation
   - Mock easily for testing
   - Decouple implementations

3. **Dependency Injection**
   - Never create dependencies manually
   - Inject through constructor
   - Use Koin for management

### Testing Strategy

**Unit Tests:**
- Test Use Cases independently
- Test Presenters with mocked views
- Test Repositories with mocked APIs

**Integration Tests:**
- Test screen rendering
- Test presenter lifecycle
- Test data flow end-to-end

### Documentation

- Document public APIs
- Explain non-obvious business logic
- Keep CLAUDE.md updated
- Include code examples

### Performance

- Use LazyColumn for long lists
- Memoize expensive composables
- Cancel coroutines properly
- Avoid memory leaks with proper lifecycle

---

## Migration Guide

### From Views to Compose

**Before (XML + Views):**
```xml
<!-- activity_main.xml -->
<LinearLayout>
    <Button android:id="@+id/button_go" />
    <RecyclerView android:id="@+id/recycler" />
</LinearLayout>
```

**After (Compose):**
```kotlin
@Composable
fun CurrencyScreen() {
    Column {
        Button(onClick = { /* ... */ }) { Text("Go") }
        LazyColumn {
            items(rates) { rate ->
                CurrencyListItem(rate)
            }
        }
    }
}
```

**Benefits:**
- Type-safe UI code
- Better preview support
- Easier state management
- Better reusability

---

## Conclusion

This architecture provides:

✅ **Clear separation of concerns**
✅ **High testability**
✅ **Easy maintenance**
✅ **Scalable structure**
✅ **Type-safe dependencies**
✅ **Modern Compose UI**

Follow these principles when adding new features to maintain code quality and consistency.

---

**Related Documentation:**
- [CLAUDE.md](../CLAUDE.md) - Implementation patterns
- [COMPOSE_GUIDE.md](COMPOSE_GUIDE.md) - Compose best practices
- [README.md](../README.md) - Project overview
