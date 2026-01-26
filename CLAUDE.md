# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Build Commands

```bash
./gradlew clean                    # Clean build
./gradlew assembleDebug            # Build debug APK
./gradlew installDebug             # Install to device
./gradlew test                     # Run unit tests
./gradlew connectedAndroidTest     # Run instrumented tests
./gradlew :app:testDebugUnitTest   # Run single module tests
```

## Architecture

**Clean Architecture** with **Medium-style TOAD** (Typed Object Action Dispatch).

### Module Dependency Graph
```
app (host shell)
├── feature-currency-exchange (presentation feature)
│   └── domain (business logic, pure Kotlin)
│       └── data (repositories)
│           └── data-remote (Retrofit/OkHttp)
├── lib-toad (TOAD base classes)
└── ui (shared Compose components)
```

**Critical:** Lower layers must not depend on higher layers. The `domain` module must remain pure
Kotlin (no Android imports).

### TOAD Pattern

Each feature has these components (see `ARCHITECTURE.MD` for full details):

| Component       | Purpose                            | Example                    |
|-----------------|------------------------------------|----------------------------|
| `*State`        | Immutable UI state                 | `CurrencyState`            |
| `*Event`        | One-time outputs (toast/nav)       | `CurrencyEvent`            |
| `*Action`       | Typed commands with business logic | `CurrencyAction.LoadRates` |
| `*Dependencies` | UseCase/provider bundle            | `CurrencyDependencies`     |
| `*ViewModel`    | State holder, action dispatcher    | extends `ToadViewModel`    |

**Key rules:**

- ViewModels stay boring - no business logic, just dispatch actions
- State updates via `scope.setState { copy(...) }`
- Side effects via `scope.sendEvent(...)`
- UI dispatches actions: `viewModel.runAction(SomeAction)`

### Compose UI Pattern

Split into Route (stateful) and Content (stateless):

- `*Route` - collects state/events, wires callbacks to actions
- `*ScreenContent` - pure rendering from state

## Technology Stack

- **Language:** Kotlin 2.0.21
- **Build:** Gradle 8.5, AGP 8.2.2, JDK 21
- **UI:** Jetpack Compose with Material 3 (BOM 2024.02.00)
- **Async:** Kotlin Coroutines + StateFlow
- **DI:** Koin 3.5.3 (use `viewModel { }` for ViewModels)
- **Network:** Retrofit 2.9.0, OkHttp 4.12.0
- **Dependencies:** Gradle Version Catalog (`gradle/libs.versions.toml`)

## Adding a New Feature

1. Create `*State.kt`, `*Event.kt`, `*Action.kt`, `*Dependencies.kt`
2. Create ViewModel extending `ToadViewModel<State, Event>`
3. Create Compose UI: `*Route` + `*ScreenContent`
4. Add Koin module with `viewModel { }` definition
5. Register module in `Modules.kt`

## Testing

- **Action tests:** Execute `Action.execute(deps, scope)` with fakes (fast, isolated)
- **Compose tests:** Render `*ScreenContent` with state directly
- Use `testTag` for stable Compose assertions

## API Configuration

Set in `local.properties`:

```properties
API_KEY=your_api_key
BASE_URL=https://api.example.com/  # optional
```
