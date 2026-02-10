BasicFrameworkProject
=====================

![Android CI](https://github.com/wawakaka/BasicFrameworkProject/workflows/Android%20CI/badge.svg)

Modern Android application demonstrating **Clean Architecture** with **Medium-style TOAD** (Typed
Object Action Dispatch), built with Jetpack Compose and Material 3.

## Features

- **Architecture:** Clean Architecture (`:app` host + feature modules, with strict layer boundaries)
- **Presentation Pattern:** Medium-style TOAD (state + events + typed actions, core in `:lib-toad`)
- **UI:** 100% Jetpack Compose with Material 3
- **Async:** Kotlin Coroutines + StateFlow
- **DI:** Koin 4.1.1
- **Networking:** Retrofit + OkHttp (+ Chucker in debug)
- **Local Storage:** Room database with 24-hour caching (in `:data-local`)
- **Demo Feature:** Currency exchange rates (in `:feature-currency-exchange`)

## Tech Stack

- **Language:** Kotlin 2.3.0
- **Build System:** Gradle 9.1.0, AGP 9.0.0, JDK 21
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 36 (Android 16)
- **Compose:** BOM 2026.01.00
- **Local Database:** Room 2.7.1
- **Testing:** JUnit, Mockito Kotlin, Compose UI Test (unit + instrumented)

## API

This demo app can be configured to use a currency rates API via `local.properties`.

### API Key setup

Add your API key to the project root `local.properties`:

```properties
API_KEY=your_api_key_here
```

### Base URL override (optional)

You can override the base URL via `local.properties`:

```properties
BASE_URL=https://api.exchangeratesapi.io/v1/
```

Note: The actual base URL and endpoint paths are controlled by DI / Retrofit configuration (see
`:data-remote` + `:data`). API responses are cached locally for 24 hours (see `:data-local`).

## Documentation

- [ARCHITECTURE.MD](ARCHITECTURE.MD) - Architecture + TOAD guidelines
- [CLAUDE.md](CLAUDE.md) - Comprehensive project documentation (workflows, conventions, testing,
  migrations)

## Build & Run

```bash
# Clean build
./gradlew clean build

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Install debug APK
./gradlew installDebug
```

## License

This is a demonstration project for educational purposes.
