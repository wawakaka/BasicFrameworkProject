# BasicFrameworkProject

A modern Android application demonstrating professional development practices with **Clean Architecture**, **MVP Pattern**, and **Jetpack Compose** UI framework. The application displays currency exchange rates using a REST API.

**Status:** Milestone 5 Complete (Jetpack Compose Migration)  
**Build:** [![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()  
**Language:** Kotlin 1.9.22 | **Target SDK:** Android 14 (API 34) | **Min SDK:** Android 8.0 (API 26)

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Architecture](#architecture)
4. [Technology Stack](#technology-stack)
5. [Project Structure](#project-structure)
6. [Getting Started](#getting-started)
7. [Development Guide](#development-guide)
8. [Building & Testing](#building--testing)
9. [Contributing](#contributing)
10. [Documentation](#documentation)

---

## Overview

**BasicFrameworkProject** is a framework/template project demonstrating professional Android development using modern architecture patterns and libraries. It serves as a reference for building scalable, maintainable Android applications.

### Key Highlights

✅ **Modern UI Framework:** Jetpack Compose with Material 3 design system  
✅ **Clean Architecture:** Strict separation of concerns  
✅ **MVP Pattern:** Testable View-Presenter-Model separation  
✅ **Dependency Injection:** Koin for lightweight DI  
✅ **Reactive:** Kotlin Coroutines for async operations  
✅ **Type-Safe:** Full Kotlin with null safety  
✅ **Well-Tested:** Comprehensive test coverage  
✅ **Well-Documented:** Extensive guides and examples  

---

## Features

✅ **Currency Exchange Rates** - Real-time rate conversion  
✅ **Jetpack Compose UI** - Modern declarative UI framework  
✅ **Material 3 Design** - Latest Material Design system  
✅ **Dark Mode Support** - Automatic light/dark theme detection  
✅ **Modern Permissions** - ActivityResultContracts API  
✅ **REST API Integration** - Retrofit with OkHttp  
✅ **Dependency Injection** - Koin DI framework  
✅ **Kotlin Coroutines** - Async operations and lifecycle management  

---

## Architecture

### Layer Structure

```
┌─────────────────────────────────────────────┐
│  Presentation Layer (app)                   │
│  - MainActivity, Screens, Components        │
│  - Material 3 Theme, Compose UI             │
└─────────────────┬───────────────────────────┘
                  │ depends on
┌─────────────────▼───────────────────────────┐
│  Domain Layer (domain)                      │
│  - Use Cases, Business Logic                │
│  - Pure Kotlin (no Android deps)            │
└─────────────────┬───────────────────────────┘
                  │ depends on
┌─────────────────▼───────────────────────────┐
│  Data Layer (repository)                    │
│  - Repositories, API Interfaces, Models     │
└─────────────────┬───────────────────────────┘
                  │ depends on
┌─────────────────▼───────────────────────────┐
│  Network Layer (restapi)                    │
│  - Retrofit, OkHttp, Interceptors           │
└─────────────────────────────────────────────┘
```

### MVP Pattern

```kotlin
// Contract defines interface
interface CurrencyContract {
    interface View {
        fun showLoading()
        fun onSuccess(data: List<Pair<String, Double>>)
        fun onError(error: String)
    }
    interface Presenter {
        fun loadData()
    }
}

// Screen (View layer)
@Composable
fun CurrencyScreen(presenter: CurrencyPresenter = koinInject()) {
    // UI implementation
}

// Presenter (Business logic)
class CurrencyPresenter(usecase: GetLatestRatesUsecase) : Presenter {
    override fun loadData() {
        presenterScope.launch {
            try {
                val data = usecase.getLatestCurrencyRates()
                view?.onSuccess(data)
            } catch (e: Exception) {
                view?.onError(e.message ?: "Error")
            }
        }
    }
}
```

---

## Technology Stack

### Core Libraries

| Library | Version | Purpose |
|---------|---------|---------|
| **Kotlin** | 1.9.22 | Language |
| **Gradle** | 8.5 | Build system |
| **AGP** | 8.2.2 | Android build |
| **Target SDK** | 34 | API level |

### UI & Compose

| Library | Version | Purpose |
|---------|---------|---------|
| Jetpack Compose | 2024.09.00 | Declarative UI |
| Material 3 | 1.2.1 | Design system |
| Compose Navigation | 2.7.7 | Navigation |
| Activity Compose | 1.8.1 | Compose in Activity |

### Architecture & DI

| Library | Version | Purpose |
|---------|---------|---------|
| Koin | 3.5.3 | DI framework |
| Android Navigation | 2.7.6 | Navigation |

### Async & Networking

| Library | Version | Purpose |
|---------|---------|---------|
| Kotlin Coroutines | 1.7.3 | Async operations |
| Retrofit | 2.9.0 | REST client |
| OkHttp | 4.12.0 | HTTP client |
| Gson | 2.10.1 | JSON serialization |

---

## Project Structure

```
BasicFrameworkProject/
├── app/                          # Application (Presentation layer)
│   ├── src/main/java/io/github/wawakaka/basicframeworkproject/
│   │   ├── presentation/         # MVP Presenters, Contracts
│   │   ├── components/           # Compose components
│   │   ├── screens/              # Compose screens
│   │   ├── theme/                # Material 3 theme
│   │   └── utilities/            # Extensions, helpers
│   └── src/main/res/             # Resources
│
├── domain/                       # Domain (Business logic)
│   └── usecase/                  # Use cases
│
├── repository/                   # Data (Repositories)
│   └── currencyrates/            # Currency feature
│
├── restapi/                      # Network (Retrofit)
│   ├── RestApi.kt                # Configuration
│   └── HeaderInterceptor.kt      # Interceptors
│
├── gradle/
│   └── libs.versions.toml        # Centralized versions
│
├── CLAUDE.md                     # AI assistant guide
├── README.md                     # This file
└── docs/                         # Additional docs
```

---

## Getting Started

### Prerequisites

- **JDK:** 17+ (21 recommended)
- **Android Studio:** Latest version
- **Android SDK:** 34
- **Gradle:** 8.5+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/wawakaka/BasicFrameworkProject.git
cd BasicFrameworkProject
```

2. Build the project:
```bash
./gradlew clean build
```

3. Run on device/emulator:
```bash
./gradlew installDebug
```

### First Build

```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

---

## Development Guide

### Package Structure

```
io.github.wawakaka.<module>.<layer>.<feature>

Examples:
- io.github.wawakaka.basicframeworkproject.presentation.content
- io.github.wawakaka.domain.usecase
- io.github.wawakaka.repository.currencyrates
```

### Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Contract | `*Contract` | `CurrencyContract` |
| Presenter | `*Presenter` | `CurrencyPresenter` |
| Screen | `*Screen` | `CurrencyScreen` |
| Component | `*Component` | `CurrencyListItem` |
| API | `*Api` | `CurrencyRatesApi` |
| Repository | `*Repository` | `CurrencyRatesRepository` |
| Use Case | `*Usecase` | `GetLatestRatesUsecase` |
| Module | `*Module` | `CurrencyModule` |

### Creating a Feature

1. **Create Contract:**
```kotlin
interface MyContract {
    interface View {
        fun onSuccess(data: Data)
        fun onError(error: String)
    }
    interface Presenter {
        fun loadData()
    }
}
```

2. **Create Use Case:**
```kotlin
class MyUsecase(private val repo: MyRepository) {
    suspend fun execute(): Data = repo.getData()
}
```

3. **Create Presenter:**
```kotlin
class MyPresenter(usecase: MyUsecase) : 
    BasePresenter<MyContract.View>(), MyContract.Presenter {
    
    override fun loadData() {
        presenterScope.launch {
            try {
                val data = usecase.execute()
                view?.onSuccess(data)
            } catch (e: Exception) {
                view?.onError(e.message ?: "Error")
            }
        }
    }
}
```

4. **Create Screen:**
```kotlin
@Composable
fun MyScreen(presenter: MyPresenter = koinInject()) {
    var state by remember { mutableStateOf(MyState()) }
    
    val view = object : MyContract.View {
        override fun onSuccess(data: Data) {
            state = state.copy(data = data)
        }
        override fun onError(error: String) {
            state = state.copy(error = error)
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.loadData()
    }
    
    DisposableEffect(Unit) {
        onDispose { presenter.detach() }
    }
    
    MyScreenContent(state = state)
}
```

5. **Create Koin Module:**
```kotlin
val myModule = module {
    factory { MyUsecase(get()) }
    factory { MyPresenter(get()) }
}
```

6. **Add to Modules.kt:**
```kotlin
val applicationModules = listOf(
    myModule,
    // ... other modules
)
```

### Compose Best Practices

```kotlin
@Composable
fun MyComponent(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Button(onClick) { Text("Click") }
        }
    }
}

@Preview
@Composable
fun MyComponentPreview() {
    BasicFrameworkTheme {
        MyComponent(title = "Preview", onClick = {})
    }
}
```

### Material 3 Resources

```kotlin
// Colors
val primary = MaterialTheme.colorScheme.primary
val error = MaterialTheme.colorScheme.error

// Typography
val title = MaterialTheme.typography.titleMedium
val body = MaterialTheme.typography.bodyMedium

// Shapes
val shape = MaterialTheme.shapes.medium
```

---

## Building & Testing

### Build Commands

```bash
# Clean build
./gradlew clean build

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# Quick build (skip tests)
./gradlew build -x test
```

### Build Variants

**Debug:** Development build with debugging enabled  
**Release:** Optimized production build  

Build time: ~6-15 seconds (incremental), ~30-45 seconds (clean)

---

## Contributing

### Code Style

- Follow [Kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use 4 spaces for indentation
- Keep lines under 120 characters
- Write clear, meaningful names
- Comment complex logic

### Making Changes

1. Create feature branch:
```bash
git checkout -b feature/your-feature
```

2. Make changes following conventions

3. Test your changes:
```bash
./gradlew build
```

4. Commit with clear message:
```bash
git commit -m "Add meaningful description"
```

5. Push and create Pull Request

---

## Documentation

Comprehensive documentation included:

- **[CLAUDE.md](CLAUDE.md)** - Implementation patterns and guides
- **[MIGRATION_GUIDE.md](docs/MIGRATION_GUIDE.md)** - Views to Compose
- **[COMPOSE_GUIDE.md](docs/COMPOSE_GUIDE.md)** - Compose best practices
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Architecture details

---

## Roadmap

### ✅ Completed
- **Milestone 1:** Build system modernization
- **Milestone 2:** XML to Compose migration
- **Milestone 3:** Kotlin Coroutines integration
- **Milestone 4:** Permission handling modernization
- **Milestone 5:** Compose finalization & documentation

### 📋 Upcoming
- **Milestone 6:** TOAD Architecture Evolution
  - UiState/UiEvent/UiEffect pattern
  - ViewModel integration
  - Enhanced state management

---

## License

MIT License - See LICENSE file for details

---

## Support

- **Issues:** [GitHub Issues](https://github.com/wawakaka/BasicFrameworkProject/issues)
- **Documentation:** [CLAUDE.md](CLAUDE.md)
- **Examples:** See `docs/` directory

---

**Last Updated:** 2026-01-11  
**Version:** 1.0  
**Status:** Milestone 5 Complete
