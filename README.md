BasicFrameworkProject
=====================

![Android CI](https://github.com/wawakaka/BasicFrameworkProject/workflows/Android%20CI/badge.svg)

Modern Android application demonstrating **Clean Architecture** with **TOAD pattern** (The Opinionated Android Design), built with Jetpack Compose and Material 3.

## Features

- **Architecture:** Clean Architecture with TOAD pattern (State/Event/Effect)
- **UI:** 100% Jetpack Compose with Material 3
- **Language:** Kotlin 2.0.21
- **Async:** Kotlin Coroutines & StateFlow
- **DI:** Koin 3.5.3
- **Networking:** Retrofit + OkHttp
- **Demo Feature:** Currency exchange rates display

## Tech Stack

- **Build System:** Gradle 8.5, AGP 8.2.2, JDK 21
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34 (Android 14)
- **Architecture Pattern:** TOAD (core in `:lib-toad` module)
- **UI Framework:** Jetpack Compose (BOM 2024.02.00)
- **Testing:** JUnit, Mockito Kotlin, Compose UI Test (58 tests)

## API

This demo app uses [ExchangeRatesAPI](https://exchangeratesapi.io/) to display live currency
exchange rates.

### API Key setup

Add your API key to the project root `local.properties`:

```properties
API_KEY=your_api_key_here
```

(Optional) You can also override the base URL via `local.properties`:

```properties
BASE_URL=http://api.exchangeratesapi.io/v1/
```

## Documentation

- [ARCHITECTURE.MD](ARCHITECTURE.MD) - Presentation + Clean Architecture guidelines (TOAD)
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
