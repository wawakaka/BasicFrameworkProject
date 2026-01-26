# Architecture

This project follows Clean Architecture (`app` → `domain` → `data` → `data-remote`) with feature
modules inside the presentation layer (e.g. `:feature-currency-exchange`).

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

### Concurrency policy

- When `state.isLoading == true`, **ignore any new action** (`execute` returns early).
- This keeps action handling deterministic and prevents overlapping network calls.

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
