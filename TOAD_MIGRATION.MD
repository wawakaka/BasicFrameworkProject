# TOAD Migration Plan (Medium-style)

This document tracks the migration of the Currency feature from the older "State/Event/Effect +
handleEvent" approach to **Medium-style TOAD (Typed Object Action Dispatch)**.

Reference:
`https://medium.com/@aumaidkh/toad-a-kotlin-first-architecture-pattern-that-finally-made-my-viewmodels-boring-b615a9ab6c30`

## Goals

- Make `CurrencyViewModel` boring (single action entry point).
- Move screen logic into typed **action objects**.
- Use **single immutable state** data class.
- Use **events** for one-time effects.
- Enforce a simple concurrency policy: ignore any action while loading.

## Before → After

### Public API

- Before: `viewModel.handleEvent(CurrencyUiEvent.*)`
- After: `viewModel.runAction(CurrencyAction.*)`

### State model

- Before: `sealed class CurrencyUiState` (Idle/Loading/Success/Error)
- After: `data class CurrencyState(isLoading, rates, timestamp, errorMessage)`

### One-time side effects

- Before: `CurrencyUiEffect` via `effect: Flow<CurrencyUiEffect>`
- After: `CurrencyEvent` via `events: Flow<CurrencyEvent>`

### Business logic location

- Before: inside `CurrencyViewModel.loadRates()`
- After: inside `CurrencyAction.execute(dependencies, scope)`

## Migration checklist

### 1) Add TOAD core

Add generic contracts under:

- `lib-toad/src/main/java/io/github/wawakaka/toad/`

Includes:

- `ViewState`, `ViewEvent`, `ActionDependencies`, `ViewAction`, `ActionScope`, `ToadViewModel`

### 2) Add `TimeProvider`

Add timestamp abstraction for deterministic tests:

- `app/src/main/java/io/github/wawakaka/basicframeworkproject/utilities/TimeProvider.kt`

### 3) Split Currency contracts

Split old `CurrencyState.kt` (legacy UiState/UiEvent/UiEffect) into:

- `CurrencyState.kt` — state data class
- `CurrencyEvent.kt` — one-time events
- `CurrencyDependencies.kt` — action dependencies bundle
- `CurrencyAction.kt` — typed action objects

### 4) Refactor CurrencyViewModel

- Extend `ToadViewModel<CurrencyState, CurrencyEvent>`
- Expose only:
    - `fun runAction(action: CurrencyAction)`

### 5) Update Compose UI

- UI dispatches actions (no more `UiEvent`)
- UI collects `viewModel.events`

### 6) Update tests

- Unit tests updated to new state/event/action types.
- Compose tests updated to render `CurrencyScreenContent` from `CurrencyState`.

## Concurrency policy

- Any `CurrencyAction.execute(...)` must return immediately if:
    - `scope.currentState.isLoading == true`

This prevents overlapping calls (e.g., rapid taps on Refresh).

## Event policy

- `CurrencyEvent.ShowError` exists for future dialog support.
- **Do not emit `ShowError` yet** until a dialog UI is implemented.

## Verification

Suggested commands:

- Unit tests: `./gradlew test`
- Instrumented tests: `./gradlew connectedAndroidTest`
