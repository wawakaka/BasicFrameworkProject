# Project Roadmap Summary

**Last Updated:** 2026-01-11
**Overall Status:** M1-M4 Complete, M5-M6 Planned

---

## Executive Summary

BasicFrameworkProject is undergoing a comprehensive modernization to adopt modern Android architecture patterns and UI frameworks.

### Completed ✅

| Milestone | Status | Key Achievements |
|-----------|--------|-----------------|
| **M1** | ✅ | Build system (Gradle 8.5, Kotlin 2.0.21, JDK 21) |
| **M2** | ✅ | Compose integration & Material 3 in dependencies |
| **M3** | ✅ | Kotlin Coroutines & Flow (RxJava removed) |
| **M4** | ✅ | Modern permissions (ActivityResultContracts) |

### In Progress 🔄

| Milestone | Status | Target | Duration |
|-----------|--------|--------|----------|
| **M5** | 📋 Planned | Compose UI Migration | ~20-30 hours |
| **M6** | 📋 Planned | Architecture (MVP → TOAD) | ~15-20 hours |

### Future 📅

| Milestone | Status | Focus |
|-----------|--------|-------|
| **M7** | 📋 Future | Comprehensive Testing |
| **M8** | 📋 Future | Multi-Module Architecture |

---

## What's Actually Needed

### Current Code State
- **UI Framework:** Traditional Activities/Fragments with XML layouts
- **UI Binding:** ViewBinding (not Compose)
- **Architecture:** MVP pattern (Presenter/Contract/View)
- **Async:** Kotlin Coroutines (RxJava removed ✅)
- **Permissions:** ActivityResultContracts ✅

### Plan Forward

#### **Milestone 5: Migrate UI to Compose** (Should do first)
**Goal:** Replace XML + ViewBinding with Jetpack Compose

**What changes:**
- Remove all XML layout files
- Convert Activities/Fragments to use Compose
- Replace RecyclerView with LazyColumn
- Remove CurrencyListAdapter/ViewHolder
- Keep MVP pattern (for now)
- Skip ViewBinding entirely

**Effort:** ~20-30 hours
**Risk:** Low (UI-only changes, logic unchanged)
**Files affected:** ~15-20 files

**Detailed Plan:** See `MILESTONE_5_PLAN.md`

#### **Milestone 6: Replace MVP with TOAD** (After M5)
**Goal:** Evolve from MVP to modern TOAD architecture

**What changes:**
- Replace Presenter with ViewModel
- Introduce UiState/UiEvent/UiEffect pattern
- Use StateFlow for reactive state
- Implement proper effect handling
- Update Compose UI to use TOAD
- Keep Domain/Repository layers

**Effort:** ~15-20 hours
**Risk:** Medium (refactoring existing logic)
**Files affected:** ~10-15 files

**Detailed Plan:** See `MILESTONE_6_PLAN.md`

---

## Recommended Approach

### Phase 1: Local Development
1. **Create M5 branch** from master
2. **Implement Compose migration** using MILESTONE_5_PLAN.md
3. **Test thoroughly** on device
4. **Create PR for M5** with test results
5. **Merge to master** after review

### Phase 2: Follow-up Architecture
1. **Create M6 branch** from master (after M5 merged)
2. **Implement TOAD pattern** using MILESTONE_6_PLAN.md
3. **Keep MVP temporarily** (marked @Deprecated)
4. **Test all screens** thoroughly
5. **Create PR for M6** with test results
6. **Merge to master** after review

### Phase 3: Cleanup (M7)
1. Remove deprecated MVP classes
2. Add comprehensive test suite
3. Setup CI/CD
4. Performance optimization

---

## Key Decisions Made

### ✅ Skip ViewBinding
- **Why:** Compose is the future direction
- **Benefit:** Fewer intermediate steps
- **Impact:** Go directly from XML to Compose

### ✅ Choose TOAD Architecture
- **Why:** Best fit for Compose + modern Android
- **Benefit:** Type-safe state management, easy testing
- **Comparison:**
  - TOAD: Explicit state/events/effects ✅
  - MVVM: Simpler but less explicit
  - MVI: More complex than needed

### ✅ Gradual Migration
- **Why:** Reduce risk of breaking changes
- **Benefit:** Can test incrementally
- **Strategy:** M5 (UI), then M6 (Architecture)

---

## Technical Details

### M5: Compose Dependencies to Add

```toml
[versions]
compose = "1.6.0"
composeMaterial3 = "1.1.2"

[libraries]
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3", version.ref = "composeMaterial3" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "composeActivity" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose" }
```

### M6: ViewModel Dependencies

Already available (Lifecycle 2.7.0):
- `androidx-lifecycle-viewmodel-ktx`
- `androidx-lifecycle-runtime-ktx`
- `androidx-lifecycle-runtime-compose`

Add:
- `koin-androidx-compose` (ViewModels with Koin)

---

## Architecture Evolution Timeline

```
M1-M4: Foundation
├── Modern build system ✅
├── Kotlin 2.0.21 ✅
├── Coroutines (RxJava removed) ✅
└── Modern permissions ✅

M5: UI Modernization (XML → Compose)
├── Create Compose components
├── Migrate Activities/Fragments
├── Remove RecyclerView
└── Keep MVP Presenters

M6: Architecture Evolution (MVP → TOAD)
├── Create ViewModels
├── Define UiState/UiEvent/Effect
├── Update Compose screens
└── Deprecate MVP pattern

M7: Testing & Quality
├── Unit tests for ViewModels
├── Compose UI tests
├── Integration tests
└── Performance testing

M8: Advanced Architecture
├── Feature-based modules
├── Dynamic feature delivery
└── Build performance optimization
```

---

## Success Criteria

### M5 Success
- ✅ All Activities/Fragments use Compose
- ✅ All XML layout files removed
- ✅ App builds without warnings
- ✅ All features work identically
- ✅ No performance degradation
- ✅ Code compiles cleanly

### M6 Success
- ✅ MVP Presenters replaced with ViewModels
- ✅ UiState/Event/Effect pattern implemented
- ✅ StateFlow used for reactive state
- ✅ Effects handled properly
- ✅ All tests pass
- ✅ Configuration changes handled correctly

---

## Risk Assessment

### M5 Risks
| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| Compose version incompatibility | Low | Medium | Use proven 1.6.0 version |
| Performance regression | Low | High | Benchmark before/after |
| Layout differences | Medium | Medium | Careful component recreation |
| Navigation issues | Low | Medium | Incremental testing |

### M6 Risks
| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|-----------|
| State management bugs | Medium | High | Comprehensive unit tests |
| Effect handling issues | Medium | Medium | Clear effect patterns |
| Presenter cleanup timing | Low | Low | Keep @Deprecated for safety |
| Performance impact | Low | Medium | Profile with Profiler tool |

---

## Resource Requirements

### Development Time
- **M5:** ~20-30 hours (1-2 weeks, part-time)
- **M6:** ~15-20 hours (1 week, part-time)
- **M7:** ~10-15 hours (testing & polish)

### Tools Required
- Android Studio (already have)
- Kotlin plugin (already have)
- Compose Preview (in AS)
- Android Profiler (in AS)

### External Resources
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose/documentation)
- [TOAD Architecture](https://github.com/dotanuki-okta/toad-android)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)

---

## Communication Plan

### For PR Reviews
1. Include before/after screenshots (M5)
2. Explain architecture benefits (M6)
3. Document breaking changes (if any)
4. Provide migration guide for future work
5. Include performance benchmarks

### For Stakeholders
- Update CLAUDE.md after each milestone
- Maintain PROJECT_STATUS.md
- Create MIGRATION_Mx.md files
- Document lessons learned

---

## Open Questions & Decisions

### Decision: Start with M5 or M6?
- **Recommended:** M5 first (UI), then M6 (Architecture)
- **Reason:** Reduces risk, allows testing before architecture change
- **Alternative:** Could do both in parallel (higher risk)

### Decision: Gradual vs Complete MVP Removal?
- **Recommended:** Gradual (keep @Deprecated for safety)
- **Reason:** Allows testing, rollback if needed
- **Alternative:** Complete rewrite (faster but riskier)

### Decision: Compose Navigation Strategy?
- **M5:** Keep Fragment-based navigation
- **M6:** Evaluate Compose Navigation
- **M7:** Might migrate to Compose Navigation
- **Current:** Works, no need to rush

---

## Maintenance & Support

### During M5 & M6
- Keep master branch stable
- Use feature branches for work
- Regular testing on devices
- Document blockers
- Update CLAUDE.md frequently

### After Milestones
- Regular dependency updates
- Monitor Compose/Kotlin updates
- Keep tests passing
- Performance monitoring
- Architecture reviews

---

## Links to Detailed Plans

- **MILESTONE_5_PLAN.md** - Compose UI Migration (5 phases, detailed tasks)
- **MILESTONE_6_PLAN.md** - TOAD Architecture (6 steps, code examples)
- **PROJECT_STATUS.md** - Current state analysis
- **CLAUDE.md** - Updated with roadmap section
- **MIGRATION_M1.md** - Historical migration guide

---

## Quick Start

To begin Milestone 5:

```bash
# 1. Create feature branch
git checkout -b feature/milestone-5-compose

# 2. Review M5 plan
cat MILESTONE_5_PLAN.md

# 3. Start with Phase 1 (Setup & Infrastructure)
# - Add Compose dependencies to gradle/libs.versions.toml
# - Update app/build.gradle with Compose config
# - Create theme files (Color.kt, Type.kt, Theme.kt)

# 4. Build to verify
./gradlew clean build

# 5. Continue with Phase 2 (Components)
# - Create AppTopBar composable
# - Create LoadingIndicator, ErrorMessage, CurrencyListItem

# 6. Test frequently
./gradlew installDebug
```

---

## Next Steps

1. **Review Plans** 📖
   - Read MILESTONE_5_PLAN.md
   - Read MILESTONE_6_PLAN.md
   - Understand TOAD pattern examples

2. **Prepare Development** 🔧
   - Create M5 feature branch
   - Set up Compose preview in AS
   - Review Compose documentation

3. **Start Implementation** ⚙️
   - Begin Phase 1 of M5 (Setup)
   - Add Compose dependencies
   - Create theme infrastructure

4. **Test & Iterate** ✅
   - Build frequently
   - Test on device
   - Fix issues as they arise

5. **Document & PR** 📝
   - Update CLAUDE.md
   - Create PR with detailed description
   - Reference MILESTONE_5_PLAN.md

---

**Questions?** Refer to the detailed milestone plans or PROJECT_STATUS.md

**Ready to start?** Create a feature branch and begin Phase 1 of Milestone 5!
