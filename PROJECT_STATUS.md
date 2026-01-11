# Project Status & Next Steps

**Last Updated:** 2026-01-11

## Current State Analysis

### ✅ What's Actually Completed

| Milestone | Status | Details |
|-----------|--------|---------|
| **M1** | ✅ | Build modernization (Gradle 8.5, Kotlin 2.0.21, JDK 21) |
| **M3** | ✅ | Kotlin Coroutines & Flow (async operations use coroutines, not RxJava) |
| **M4** | ✅ | Permission modernization (ActivityResultContracts API) |

### ⚠️ What's Incomplete

| Milestone | Status | Current State | Issue |
|-----------|--------|---------------|-------|
| **M2 (Compose)** | 🔴 Incomplete | Documentation claims done, but code still uses XML layouts + ViewBinding | No actual Compose UI code yet |
| **M6 (Architecture)** | 🔴 Not Started | Still using MVP pattern | TOAD/MVVM/MVI evaluation not done |

## Code Reality vs Documentation

### Current Architecture
```
app/ (Activities/Fragments with XML layouts)
 ├── MainActivity.kt (uses ViewBinding with activity_main.xml)
 ├── CurrencyFragment.kt (uses ViewBinding with fragment_currency.xml)
 ├── Presenter layer (MainPresenter, CurrencyPresenter)
 ├── Contract layer (MainContract, CurrencyContract)
 └── RecyclerView (CurrencyListAdapter, CurrencyListViewHolder)
```

### What Needs to Happen

**Phase 1: Compose Migration (Milestone 5)**
- Remove all XML layout files
- Convert Activities/Fragments to Compose
- Replace RecyclerView with LazyColumn
- Update Koin modules for Compose-based DI
- Add Compose Previews

**Phase 2: Architecture Evolution (Milestone 6)**
- Evaluate TOAD vs MVVM vs MVI
- Replace Presenter layer with ViewModel
- Implement UiState/UiEvent pattern
- Update Compose to consume new architecture
- Refactor error handling with sealed types

## Recommended Next Actions

### Short Term (Immediate)
1. **Clarify M2 Status** - Create a new branch to implement actual Compose migration
2. **Update Documentation** - CLAUDE.md is correct conceptually but ahead of code reality
3. **Create Implementation Plan** - Detailed tasks for Compose migration

### Path Forward

**Option A: Start M5 (Compose Migration)**
- Begin with MainActivity Compose migration
- Create Compose previews
- Build LazyColumn for currency list
- Then move to M6 (Architecture)

**Option B: Plan M6 Architecture First**
- Design new architecture pattern (TOAD/MVVM/MVI)
- Create examples with new pattern
- Then migrate UI to Compose with new architecture

**Recommended:** Option A then B
- Get Compose working first
- Then evolve architecture with Compose's better fit
- Less risky than doing both simultaneously

## Key Decisions Needed

1. **Architecture Choice for M6**
   - [ ] TOAD (recommended - combines state + events)
   - [ ] MVVM (simpler, lighter weight)
   - [ ] MVI (more complex, unidirectional)
   - [ ] Other?

2. **Compose Migration Scope**
   - [ ] Full rewrite (all layouts at once)
   - [ ] Incremental (one screen at a time)
   - Recommend: Incremental per screen

3. **Testing Strategy**
   - [ ] Compose testing framework
   - [ ] What about Presenter unit tests during transition?
   - [ ] Keep old tests during migration?

## Files That Need Updates

### Documentation
- [ ] CLAUDE.md - Already updated with roadmap
- [ ] Add MILESTONE_5_PLAN.md (Compose migration detailed tasks)
- [ ] Add MILESTONE_6_PLAN.md (Architecture evolution options)

### Code
- [ ] app/build.gradle - Add Compose dependencies
- [ ] All Activities/Fragments - Convert to Compose
- [ ] All XML layouts - Remove completely
- [ ] CurrencyListAdapter/ViewHolder - Remove, use Compose state
- [ ] Presenter/Contract layer - Later in M6
- [ ] Koin modules - Update for Compose DI

---

## Architecture Comparison Matrix

### Current: MVP + Coroutines

```
Pros:
✅ Clear separation (View, Presenter, Contract)
✅ Testable presenters (isolated from UI framework)
✅ Coroutines handle async properly

Cons:
❌ Presenter holds view reference (lifecycle issues)
❌ No built-in state management (Presenter is stateful)
❌ Not ideal with Compose recomposition model
❌ Two layers (Presenter + ViewModel) overhead
```

### Option 1: TOAD (The Opinionated Android Design)

```
Screen Structure:
┌─ UiState (sealed class or data class)
├─ UiEvent (sealed class)
├─ UiEffect (sealed class)
└─ ViewModel
    ├─ StateFlow<UiState>
    ├─ fun handleEvent(event: UiEvent)
    └─ Effect channel for side effects

Pros:
✅ Explicit state modeling
✅ Type-safe events
✅ Natural fit with Compose
✅ Testable reducers
✅ Clear side effects

Cons:
❌ More boilerplate (UiState + UiEvent)
❌ Steeper learning curve
```

**Example:**
```kotlin
sealed class CurrencyUiState {
    object Loading : CurrencyUiState()
    data class Success(val rates: List<Pair<String, Double>>) : CurrencyUiState()
    data class Error(val message: String) : CurrencyUiState()
}

sealed class CurrencyUiEvent {
    object LoadRates : CurrencyUiEvent()
    object Retry : CurrencyUiEvent()
}

class CurrencyViewModel(
    private val getRatesUseCase: GetLatestRatesUsecase
) : ViewModel() {
    private val _state = MutableStateFlow<CurrencyUiState>(CurrencyUiState.Loading)
    val state: StateFlow<CurrencyUiState> = _state.asStateFlow()

    fun handleEvent(event: CurrencyUiEvent) {
        when (event) {
            is CurrencyUiEvent.LoadRates -> loadRates()
            is CurrencyUiEvent.Retry -> loadRates()
        }
    }

    private fun loadRates() {
        viewModelScope.launch {
            try {
                val rates = getRatesUseCase.getLatestCurrencyRates()
                _state.value = CurrencyUiState.Success(rates)
            } catch (e: Exception) {
                _state.value = CurrencyUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

### Option 2: MVVM (Modern + Compose)

```
Screen Structure:
┌─ ViewModel (state holder)
├─ StateFlow<Data> or LiveData<Data>
└─ Side effects via Event channel

Pros:
✅ Lighter than TOAD
✅ Less boilerplate
✅ Still good separation
✅ Easy migration from MVP
✅ Compose naturally consumes StateFlow

Cons:
❌ Less explicit about states/events
❌ Side effects handling less clear
```

### Option 3: MVI (Most Complex)

```
Screen Structure:
┌─ Intent (user action)
├─ Model (state reducer)
└─ View (Compose consuming state)

Pros:
✅ Unidirectional data flow
✅ Pure functions
✅ Highly testable
✅ Predictable

Cons:
❌ Most boilerplate
❌ Steeper learning curve
❌ Overkill for simple screens
```

---

## Recommended Next Steps

### Immediate (Next Session)
1. Create MILESTONE_5_PLAN.md with detailed Compose migration tasks
2. Decide on M6 architecture (recommend TOAD)
3. Create MILESTONE_6_PLAN.md with architecture transition plan
4. Start M5 implementation (start with MainActivity Compose)

### First Task for M5
```
Create new Compose-based MainActivity:
1. Add Compose dependencies to build.gradle
2. Create MainScreen composable
3. Create CurrencyListScreen composable with LazyColumn
4. Keep MVP presenter (will replace in M6)
5. Wire Compose UI to existing presenters via callbacks
6. Remove activity_main.xml and fragment_currency.xml
```

---

**Ready to proceed with Milestone 5?** Let me know which architecture pattern you prefer for M6, and I'll create detailed implementation plans for both milestones.
