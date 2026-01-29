# Architecture

This project follows Clean Architecture (`app` → `domain` → `data` → `data-remote` / `data-local`)
with feature modules inside the presentation layer (e.g. `:feature-currency-exchange`).

## Data Layer

The data layer consists of:

- **`:data`** - Repository implementations with caching logic
- **`:data-remote`** - Retrofit/OkHttp network layer
- **`:data-local`** - Room database for local caching

### Caching Strategy

API responses are cached locally using Room database with a 24-hour validity period:

- If cached data exists and is less than 24 hours old, return cached data
- If cache is expired or missing, fetch from API and update cache
- Cache key is based on the base currency (e.g., "EUR", "USD")

The `CacheTimeProvider` interface enables testable time-based logic.

For the **presentation layer**, we use **Medium-style TOAD** (Typed Object Action Dispatch),
inspired by the article:
`https://medium.com/@aumaidkh/toad-a-kotlin-first-architecture-pattern-that-finally-made-my-viewmodels-boring-b615a9ab6c30`

## Presentation: Medium-style TOAD

### Core idea

- **State** is a single immutable `data class` per feature.
- **Events** are one-time outputs (toast, navigation, etc.).
- **Actions** are typed command objects that encapsulate business logic.
- **ViewModel** is intentionally boring: it exposes `StateFlow` + `events` Flow and dispatches
  actions.

### Naming conventions

For a feature named `Currency`:

- `CurrencyState` — implements `ViewState`
- `CurrencyEvent` — implements `ViewEvent`
- `CurrencyAction` — implements `ViewAction<CurrencyDependencies, CurrencyState, CurrencyEvent>`
- `CurrencyDependencies` — extends `ActionDependencies`
- `CurrencyViewModel` — extends `ToadViewModel<CurrencyState, CurrencyEvent>`

### Contracts (TOAD core)

Located in module `:lib-toad` under package `io.github.wawakaka.toad` (
`lib-toad/src/main/java/io/github/wawakaka/toad/`):

- `ViewState` — marker interface for states
- `ViewEvent` — marker interface for one-time events
- `ActionDependencies` — dependency bundle created once per ViewModel
- `ViewAction` — command interface (`execute(dependencies, scope)`)
- `ActionScope` — controlled access to `setState { copy(...) }` and `sendEvent(...)`
- `ToadViewModel` — owns `StateFlow` + `events` Flow, dispatches actions

### Rules

- **Do not add new public methods to ViewModels** for user actions.
    - Add a new `*Action` instead.
- **State updates must use reducers:** `scope.setState { copy(...) }`
- **Side effects must use events:** `scope.sendEvent(CurrencyEvent.ShowToast(...))`
- **UI dispatches actions:** `viewModel.runAction(CurrencyAction.LoadRates)` (or route-level
  callbacks that forward to actions)

### Time / timestamps

- Use `TimeProvider` from the feature module for consistent time formatting:
  `feature-currency-exchange/src/main/java/io/github/wawakaka/feature/currencyexchange/util/TimeProvider.kt`.
- In tests, use a fake `TimeProvider` to avoid flaky timestamp assertions.

### Testing

Preferred testing approach:

- **Action tests** (fast, isolated): execute `CurrencyAction.execute(deps, scope)` with fake
  deps/scope.
- **ViewModel tests** (minimal): verify wiring and state/event propagation.
- **Compose tests**: render `*ScreenContent` from `*State` directly.

Notes:

- Prefer **stable semantics** (e.g., `Modifier.testTag("loading_indicator")`) over asserting
  transient text like "Loading".

## Data Layer Components

### data-local module

Located in `:data-local` under package `io.github.wawakaka.data.local`:

| Component                   | Purpose                                      |
|-----------------------------|----------------------------------------------|
| `CachedCurrencyRatesEntity` | Room entity for cached API responses         |
| `CurrencyRatesDao`          | DAO with insert, query, delete operations    |
| `CurrencyRatesDatabase`     | Room database with singleton pattern         |
| `RatesTypeConverter`        | Gson-based converter for Map<String, String> |
| `LocalDataSource`           | Factory object (matches RestApi pattern)     |

### data module

The `:data` module contains:

| Component                 | Purpose                                     |
|---------------------------|---------------------------------------------|
| `CurrencyRatesRepository` | Coordinates cache and API, 24h TTL logic    |
| `CacheTimeProvider`       | Interface for testable time (+ System impl) |
| `Repository`              | Factory object wiring API + DAO             |
