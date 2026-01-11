# Documentation Index

**Last Updated:** 2026-01-11
**Project Status:** M1-M4 Complete | M5-M6 Planned

---

## Quick Navigation

### 🚀 For Getting Started
- **Start here:** [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Executive overview
- **Current state:** [PROJECT_STATUS.md](PROJECT_STATUS.md) - What's actually implemented
- **Next steps:** See M5 or M6 sections below

### 📖 Comprehensive Guides
- **Architecture & patterns:** [CLAUDE.md](CLAUDE.md) - 1000+ lines of architecture guide
- **Build system:** [MIGRATION_M1.md](MIGRATION_M1.md) - Gradle 8.5, Kotlin 2.0.21, JDK 21
- **Roadmap history:** [MILESTONE_1_SUMMARY.md](MILESTONE_1_SUMMARY.md) - M1 completion details

---

## Documentation by Purpose

### Project Managers
**Need:** Timeline, effort, risks, resources
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Timeline, effort estimates, risk assessment
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Current state, completeness analysis
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - M5 effort breakdown (20-30 hours)
- [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) - M6 effort breakdown (15-20 hours)

**Key Metrics:**
- M5 Duration: ~20-30 hours (1-2 weeks, part-time)
- M6 Duration: ~15-20 hours (1 week, part-time)
- Risk Level: Low (M5), Medium (M6)
- Effort: 35-50 total hours for M5+M6

### Developers
**Need:** Implementation steps, code patterns, architecture details

#### For M5 (Compose UI Migration)
1. Read: [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - 5 phases, detailed tasks
2. Reference: [CLAUDE.md](CLAUDE.md) § Code Conventions
3. Implement: Follow Phase 1-5 in order
4. Test: Use checklist in MILESTONE_5_PLAN.md

#### For M6 (TOAD Architecture)
1. Read: [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) - 6 steps with code examples
2. Reference: [CLAUDE.md](CLAUDE.md) § Coroutine Patterns
3. Implement: Follow Step 1-6 in order
4. Test: Use unit test examples in MILESTONE_6_PLAN.md

#### For Current Work (M1-M4)
1. Architecture: [CLAUDE.md](CLAUDE.md) § Architecture & Design Patterns
2. Build System: [CLAUDE.md](CLAUDE.md) § Development Workflows
3. Code Style: [CLAUDE.md](CLAUDE.md) § Code Conventions & Best Practices
4. Common Tasks: [CLAUDE.md](CLAUDE.md) § Common Tasks
5. Pitfalls: [CLAUDE.md](CLAUDE.md) § Common Pitfalls

### Code Reviewers
**Need:** What changed, why it changed, testing done

- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Understand the larger context
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) § Deliverables & PR Description
- [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) § Deliverables & PR Description
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Compare before/after state

### AI Assistants & Future Developers
**Need:** Complete architecture understanding, guidelines, patterns

1. Start: [CLAUDE.md](CLAUDE.md) - Comprehensive 1000+ line guide
   - Project overview
   - Architecture patterns
   - Technology stack
   - Code conventions
   - Common tasks
   - Important notes for AI

2. Current State: [PROJECT_STATUS.md](PROJECT_STATUS.md) - Reality check
3. Roadmap: [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Where we're headed
4. For M5: [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - Next steps
5. For M6: [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) - After M5

---

## Document Details

### CLAUDE.md (1000+ lines)
**Purpose:** Comprehensive AI assistant guide for the project
**Contents:**
- Project overview and architecture
- Module structure (app, domain, repository, restapi)
- Technology stack with all versions
- Development workflows and commands
- Code conventions and naming standards
- Design patterns (MVP, Koin, Coroutines)
- Common tasks (add feature, add API, add dependencies)
- Testing strategy
- Important notes for AI assistants
- Best practices summary

**When to use:** Daily reference for architecture and patterns
**Read time:** 30-45 minutes

### PROJECT_STATUS.md (250+ lines)
**Purpose:** Clarify what's actually implemented vs documented
**Contents:**
- Current state analysis (reality check)
- Code reality vs documentation gaps
- Architecture comparison matrix (TOAD, MVVM, MVI)
- Recommended next steps with code examples
- Key decisions needed
- Files needing updates

**When to use:** Before starting M5/M6 to understand current state
**Read time:** 15-20 minutes

### ROADMAP_SUMMARY.md (380+ lines)
**Purpose:** Executive overview of entire modernization plan
**Contents:**
- Executive summary (what's done, what's planned)
- Current code state vs planned improvements
- Effort estimates for M5 and M6 (hours breakdown)
- Risk assessment matrix with mitigation
- Resource requirements and timeline
- Key decisions and rationale
- Success criteria for each milestone
- Communication plan
- Quick start guide for M5

**When to use:** Planning and decision-making
**Read time:** 20-30 minutes

### MILESTONE_5_PLAN.md (1000+ lines)
**Purpose:** Detailed implementation plan for Compose UI migration
**Contents:**
- 5 implementation phases:
  1. Setup & Infrastructure (dependencies, theme, utilities)
  2. Component Layer (toolbar, loading, error, list items)
  3. Screen Layer (main activity, currency fragment)
  4. Navigation & Integration (update layout files, build config)
  5. Testing & Cleanup (unit tests, runtime tests, documentation)
- Code examples for each file
- Testing checklist
- Deliverables list
- PR description template
- Migration notes and limitations

**When to use:** Implementing Milestone 5
**Read time:** 60-90 minutes (skim) or 120+ minutes (detailed)

### MILESTONE_6_PLAN.md (1500+ lines)
**Purpose:** Detailed implementation plan for TOAD architecture
**Contents:**
- TOAD pattern explanation with diagrams
- State-Event-Effect pattern examples
- 6 migration steps:
  1. Dependency updates
  2. Create TOAD components (state/events/effects)
  3. Create ViewModel implementations
  4. Refactor Koin modules
  5. Update Compose UI to use TOAD
  6. Replace/deprecate MVP
- Unit test examples
- Common pitfalls and solutions
- Delivery checklist
- PR description template
- Architecture benefits comparison

**When to use:** Implementing Milestone 6 (after M5)
**Read time:** 60-90 minutes (skim) or 120+ minutes (detailed)

### MIGRATION_M1.md
**Purpose:** Detailed guide for M1 (Gradle 8.5, Kotlin 2.0.21, JDK 21) migration
**Status:** Historical reference for completed migration
**When to use:** Understanding how M1 migration was done (for future M7+ migrations)

### MILESTONE_1_SUMMARY.md
**Purpose:** Summary of completed M1 work
**Status:** Historical reference
**When to use:** Understanding M1 achievements and decisions

---

## Architecture Decision Records (ADR)

### ADR-001: Skip ViewBinding Migration
**Decision:** Don't implement ViewBinding; go directly to Compose
**Rationale:** ViewBinding is intermediate step; Compose is final destination
**Status:** ✅ Decided and documented in ROADMAP_SUMMARY.md
**Implementation:** M5 plan skips ViewBinding entirely

### ADR-002: Choose TOAD Architecture
**Decision:** Replace MVP with TOAD pattern (not MVVM or MVI)
**Rationale:** TOAD combines state/events/effects explicitly, ideal for Compose
**Status:** ✅ Decided and documented in PROJECT_STATUS.md
**Implementation:** M6 plan uses TOAD with detailed examples

### ADR-003: Gradual Migration (not Big Bang)
**Decision:** Migrate M5 first (UI), then M6 (Architecture)
**Rationale:** Reduces risk, allows testing between phases
**Status:** ✅ Decided and documented in ROADMAP_SUMMARY.md
**Implementation:** Separate milestone plans with test checkpoints

---

## Dependency Map

```
Documentation Dependency Graph:

Start → ROADMAP_SUMMARY.md (executive overview)
  ├→ CLAUDE.md (architecture details)
  ├→ PROJECT_STATUS.md (current state)
  └→ MILESTONE_5_PLAN.md (next steps)
       └→ MILESTONE_6_PLAN.md (after M5)
            └→ (M7, M8 planning - future)

For Implementation:
  MILESTONE_5_PLAN.md (read fully before starting)
    ├→ CLAUDE.md (reference for patterns)
    ├→ gradle/libs.versions.toml (dependency additions)
    └→ app/build.gradle (configuration updates)

After M5, before M6:
  MILESTONE_6_PLAN.md (read fully)
    ├→ CLAUDE.md (coroutine patterns)
    └→ MILESTONE_5_PLAN.md (for UI structure context)
```

---

## Reading Recommendations by Role

### 👔 Project Manager
**Essential (1-2 hours):**
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Complete overview
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Current state

**Optional (if needed):**
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) § Deliverables
- [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) § Deliverables

### 👨‍💻 Developer (Starting M5)
**Essential (2-3 hours):**
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Context
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - Complete implementation guide
- [CLAUDE.md](CLAUDE.md) § Kotlin Best Practices - Code style

**Reference (as needed):**
- [CLAUDE.md](CLAUDE.md) § Code Conventions
- [CLAUDE.md](CLAUDE.md) § Common Tasks

### 👨‍💻 Developer (Starting M6)
**Essential (2-3 hours):**
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Context
- [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) - Complete implementation guide
- [CLAUDE.md](CLAUDE.md) § Kotlin Coroutines Patterns

**Reference (as needed):**
- [CLAUDE.md](CLAUDE.md) § Code Conventions
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - Understanding UI structure

### 🔍 Code Reviewer
**Essential (1-2 hours):**
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Understand context
- Relevant MILESTONE_X_PLAN.md § Testing Checklist
- Relevant MILESTONE_X_PLAN.md § Deliverables

**Reference (if needed):**
- [CLAUDE.md](CLAUDE.md) - For style questions
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - For architecture questions

### 🤖 AI Assistant (Future)
**Essential (4-5 hours):**
- [CLAUDE.md](CLAUDE.md) - Comprehensive guide (read all)
- [PROJECT_STATUS.md](PROJECT_STATUS.md) - Understand current state
- [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) - Understand direction

**Reference (as assigned tasks):**
- [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) - For M5 tasks
- [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) - For M6 tasks

---

## Document Maintenance

### Update Frequency
| Document | Update Frequency | Who | Notes |
|----------|-----------------|-----|-------|
| CLAUDE.md | After each milestone | Dev lead | Major architecture changes |
| PROJECT_STATUS.md | During implementation | As needed | Track actual progress |
| ROADMAP_SUMMARY.md | When priorities change | PM | Timeline and resource updates |
| MILESTONE_X_PLAN.md | Before/after implementation | Dev lead | Task completion, lessons learned |
| MIGRATION_Mx.md | After milestone completion | Dev lead | Document what happened |

### Version Control
- All docs checked into git
- Changes tracked in commits
- MRs reviewed like code
- Significant changes in separate commit

---

## FAQ

**Q: Which document should I read first?**
A: Start with ROADMAP_SUMMARY.md (20-30 min), then PROJECT_STATUS.md (15-20 min)

**Q: How do I implement M5?**
A: Read MILESTONE_5_PLAN.md completely, follow 5 phases in order with code examples

**Q: How do I implement M6?**
A: Read MILESTONE_6_PLAN.md completely, follow 6 steps with TOAD pattern examples

**Q: What's the current state of the code?**
A: See PROJECT_STATUS.md - M1-M4 complete, M5-M6 planned, code still uses MVP + XML

**Q: How much effort for M5 and M6?**
A: M5 = 20-30 hours, M6 = 15-20 hours = ~35-50 total hours

**Q: Why TOAD over MVVM?**
A: TOAD is more explicit about state/events/effects, better Compose integration, see PROJECT_STATUS.md architecture comparison

**Q: Can I do M5 and M6 in parallel?**
A: Not recommended - M6 depends on M5 (needs Compose UI), do M5 first then M6

**Q: What about ViewBinding?**
A: Skip it - go directly to Compose. M5 removes XML layouts completely.

---

## Contributing

### When Adding Documentation
1. Maintain structure and links in this index
2. Update table of contents in docs
3. Add to appropriate section in this index
4. Create entry with purpose, contents, when to use

### When Updating Documentation
1. Update last modified date
2. Include what changed in commit message
3. Update version numbers if applicable
4. Check links still work

### Documentation Standards
- Use Markdown formatting
- Include code examples where relevant
- Add table of contents for long docs (>500 lines)
- Include quick navigation links
- Keep sentences concise and clear
- Number lists for sequential tasks
- Use checkboxes ☐ for task lists

---

## Quick Links

### Immediate Actions
- Start M5? → [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md)
- Understand current state? → [PROJECT_STATUS.md](PROJECT_STATUS.md)
- Need architecture guide? → [CLAUDE.md](CLAUDE.md)

### Planning & Management
- Executive overview → [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md)
- Resource estimation → [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) § Resource Requirements
- Risk assessment → [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) § Risk Assessment

### Architecture Decisions
- TOAD pattern → [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) § TOAD Architecture Pattern
- Skip ViewBinding → [ROADMAP_SUMMARY.md](ROADMAP_SUMMARY.md) § Key Decisions Made
- MVP limitations → [PROJECT_STATUS.md](PROJECT_STATUS.md) § Architecture Comparison

### Implementation Help
- Compose setup? → [MILESTONE_5_PLAN.md](MILESTONE_5_PLAN.md) § Phase 1
- ViewModel creation? → [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) § Step 2
- State management? → [MILESTONE_6_PLAN.md](MILESTONE_6_PLAN.md) § TOAD Architecture
- Code patterns? → [CLAUDE.md](CLAUDE.md) § Code Conventions

---

**Last Updated:** 2026-01-11
**Next Review:** After M5 completion
**Maintainer:** Development Lead
