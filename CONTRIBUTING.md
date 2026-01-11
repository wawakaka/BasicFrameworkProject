# Contributing to BasicFrameworkProject

Thank you for your interest in contributing to BasicFrameworkProject! This document provides guidelines and instructions for contributing to this project.

**Last Updated:** 2026-01-11
**Status:** Open for Contributions

---

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Setup](#development-setup)
4. [Making Changes](#making-changes)
5. [Code Style Guidelines](#code-style-guidelines)
6. [Commit Guidelines](#commit-guidelines)
7. [Pull Request Process](#pull-request-process)
8. [Testing Guidelines](#testing-guidelines)
9. [Documentation](#documentation)
10. [Troubleshooting](#troubleshooting)

---

## Code of Conduct

### Our Commitment

We are committed to providing a welcoming and inspiring community for all. Please be respectful and constructive in all interactions.

### Expected Behavior

- Use welcoming and inclusive language
- Be respectful of differing opinions and experiences
- Focus on constructive feedback
- Show empathy towards other contributors
- Report inappropriate behavior to the maintainers

---

## Getting Started

### Prerequisites

- JDK 17+ (21 LTS recommended)
- Android Studio Flamingo (2022.2.1) or later
- Git and GitHub account
- Familiarity with Kotlin and Android development

### Fork & Clone

```bash
# 1. Fork the repository on GitHub
#    Click "Fork" button on https://github.com/wawakaka/BasicFrameworkProject

# 2. Clone your fork
git clone https://github.com/{your-username}/BasicFrameworkProject.git
cd BasicFrameworkProject

# 3. Add upstream remote
git remote add upstream https://github.com/wawakaka/BasicFrameworkProject.git

# 4. Verify remotes
git remote -v
# origin (your fork)
# upstream (original repo)
```

---

## Development Setup

### 1. Environment Setup

```bash
# Set ANDROID_HOME and JAVA_HOME
export ANDROID_HOME=$HOME/Android/Sdk
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home

# Add to ~/.bashrc or ~/.zshrc for persistence
```

### 2. Initial Build

```bash
# Clean build to ensure everything works
./gradlew clean build

# Expected output: BUILD SUCCESSFUL
```

### 3. Run Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "TestClassName"

# Run with coverage
./gradlew testDebugUnitTest --jacoco
```

### 4. Run on Device/Emulator

```bash
# Start emulator or connect device
adb devices

# Build and install
./gradlew installDebug

# Or use Android Studio Run button
```

---

## Making Changes

### 1. Create Feature Branch

```bash
# Update main branch first
git fetch upstream
git checkout master
git merge upstream/master

# Create feature branch
git checkout -b feature/your-feature-name

# Or for bug fixes
git checkout -b fix/bug-description

# Or for documentation
git checkout -b docs/documentation-name
```

**Branch Naming Conventions:**
- Features: `feature/description-in-kebab-case`
- Bug fixes: `fix/bug-description`
- Documentation: `docs/what-documented`
- Chores: `chore/description`

### 2. Make Your Changes

#### For Features

1. Start with the architecture documentation (docs/ARCHITECTURE.md)
2. Follow the module structure (see README.md)
3. Create appropriate classes/functions in correct modules
4. Write tests alongside code
5. Update documentation

#### For Bug Fixes

1. Add a test that reproduces the bug
2. Fix the bug
3. Ensure test passes
4. Verify no regressions

#### Code Organization

```
Feature in Presentation Layer (app module):
├── presentation/
│   ├── feature/
│   │   ├── FeatureScreen.kt          (Composable)
│   │   ├── FeaturePresenter.kt
│   │   ├── FeatureContract.kt
│   │   ├── FeatureModule.kt
│   │   └── components/
│   │       └── FeatureComponent.kt
│   └── Modules.kt                    (Add module here)

Feature in Domain Layer (domain module):
├── usecase/
│   └── FeatureUsecase.kt

Feature in Data Layer (repository module):
├── feature/
│   ├── FeatureRepository.kt
│   ├── FeatureApi.kt
│   └── model/
│       └── response/
│           └── FeatureResponse.kt
```

---

## Code Style Guidelines

### Kotlin Style

**Follow Kotlin Coding Conventions:**
https://kotlinlang.org/docs/coding-conventions.html

### Project Conventions

#### 1. Naming

```kotlin
// ✅ Correct
class UserPresenter         // Classes: PascalCase
fun loadUserData()          // Functions: camelCase
val userName: String        // Properties: camelCase
const val MAX_USERS = 100   // Constants: UPPER_SNAKE_CASE

// ❌ Avoid
class user_presenter
fun Load_User_Data()
var User_Name
val maxusers = 100
```

#### 2. Formatting

```kotlin
// Line length: Maximum 120 characters
// Indentation: 4 spaces (not tabs)
// Braces: Opening brace on same line

// ✅ Correct
@Composable
fun MyScreen() {
    Column {
        Text("Hello")
    }
}

// ❌ Avoid
@Composable
fun MyScreen()
{
    Column()
    {
        Text("Hello")
    }
}
```

#### 3. Comments

```kotlin
// ✅ Useful comments
// Retry with exponential backoff (max 3 attempts)
var retries = 0

// ❌ Avoid
// Increment retries
retries++

// Documentation comments
/**
 * Loads user data from repository
 * @param userId The user ID to load
 * @return User data or null if not found
 */
suspend fun loadUser(userId: String): User?
```

#### 4. Imports

```kotlin
// ✅ Organize imports
// 1. Android
import android.os.Bundle
import android.util.Log

// 2. AndroidX
import androidx.compose.material3.Button
import androidx.navigation.NavController

// 3. Third-party
import com.squareup.retrofit2.http.GET
import io.insert-koin.android.ext.android.getKoin

// 4. Project
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.github.wawakaka.repository.currencyrates.CurrencyRatesRepository
```

### Android/Compose Style

```kotlin
// Composable signature
@Composable
fun MyComponent(
    // Required parameters first
    title: String,
    onAction: () -> Unit,
    // Optional parameters
    subtitle: String? = null,
    isEnabled: Boolean = true,
    // Modifier last
    modifier: Modifier = Modifier
) {
    // Implementation
}

// Always provide preview
@Preview
@Composable
fun MyComponentPreview() {
    BasicFrameworkTheme {
        MyComponent(title = "Preview", onAction = {})
    }
}
```

### Architecture Rules

```kotlin
// ✅ Correct - Layers follow dependency direction
// Presentation → Domain → Data → Network

// ✅ Correct - Pure domain layer (no Android imports)
// domain/src/main/java/io/github/wawakaka/domain/usecase/MyUsecase.kt
class MyUsecase(private val repository: MyRepository) {
    suspend fun execute(): Data = repository.getData()
}

// ❌ Avoid - Domain importing Android classes
import android.content.Context  // ✗ Domain should not import Android

// ✅ Correct - MVP Contract pattern
interface MyContract {
    interface View {
        fun showLoading()
        fun onSuccess(data: Data)
        fun onError(error: String)
    }

    interface Presenter {
        fun loadData()
    }
}
```

---

## Commit Guidelines

### Commit Messages

**Format:**
```
[Type] Brief description (50 chars max)

Detailed explanation if needed (72 chars max per line)

Fixes #123
```

**Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation
- `test:` - Test addition/modification
- `refactor:` - Code refactoring
- `perf:` - Performance improvement
- `style:` - Code style changes
- `chore:` - Build/dependency changes

**Examples:**

```
feat: Add currency filtering by country

Added new CurrencyFilterUsecase to filter rates
by country. Integrates with existing MVP pattern.

Fixes #42

---

fix: Prevent memory leak in CurrencyPresenter

Ensure presenterScope is cancelled in detach()
to prevent coroutine leaks.

---

docs: Update COMPOSE_GUIDE with new patterns

Added documentation for state hoisting pattern
and testing composables.
```

### Making a Commit

```bash
# Stage specific files
git add file1.kt file2.kt

# Or stage all changes
git add .

# Commit with message
git commit -m "feat: Add new feature

Detailed explanation of what was changed
and why."

# View recent commits
git log --oneline -5
```

### Amending Commits

```bash
# If you need to fix the last commit
git add file.kt
git commit --amend --no-edit

# Or if you want to change the message
git commit --amend -m "New message"

# Force push (only for your branch before PR)
git push origin feature/branch-name --force
```

---

## Pull Request Process

### 1. Push Changes

```bash
# Push to your fork
git push origin feature/your-feature-name

# If force-push needed (only before PR)
git push origin feature/your-feature-name --force
```

### 2. Create Pull Request

**On GitHub:**
1. Go to your fork repository
2. Click "Pull requests" tab
3. Click "New pull request"
4. Select `wawakaka/BasicFrameworkProject` as base
5. Ensure your branch is the head
6. Click "Create pull request"

### 3. PR Description Template

```markdown
## Description
Brief description of what this PR does.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Related Issues
Fixes #123

## Changes Made
- Change 1
- Change 2
- Change 3

## How to Test
Steps to verify the changes work:
1. Build the project
2. Run tests
3. Test on device

## Testing
- [ ] Unit tests added/updated
- [ ] Manual testing completed
- [ ] No regression issues found

## Screenshots (if applicable)
Include before/after screenshots for UI changes

## Checklist
- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No new warnings generated
- [ ] Tests pass locally
```

### 4. Review Process

Reviewers will:
- Check code style and consistency
- Verify architecture principles followed
- Review test coverage
- Suggest improvements
- Request changes if needed

**Address Review Comments:**
```bash
# Make requested changes
# Commit with explanatory message
git add .
git commit -m "Address review comments"

# No need to force-push - maintainer will squash on merge
git push origin feature/your-feature-name
```

### 5. Merge

Once approved, a maintainer will merge your PR. Choose merge strategy:
- **Squash & Merge:** For multi-commit features
- **Rebase & Merge:** For linear history
- **Create Merge Commit:** For significant features

---

## Testing Guidelines

### Unit Testing

```kotlin
// Location: app/src/test/java/
@RunWith(AndroidJUnit4::class)
class PresenterTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockView: MyContract.View
    private lateinit var mockUsecase: MyUsecase
    private lateinit var presenter: MyPresenter

    @Before
    fun setup() {
        mockView = mockk(relaxed = true)
        mockUsecase = mockk()
        presenter = MyPresenter(mockUsecase)
        presenter.attach(mockView)
    }

    @Test
    fun loadData_showsLoadingThenData() = runTest {
        // Arrange
        val data = listOf("item1", "item2")
        coEvery { mockUsecase.getData() } returns data

        // Act
        presenter.loadData()

        // Assert
        verify { mockView.showLoading() }
        verify { mockView.onSuccess(data) }
        verify { mockView.hideLoading() }
    }

    @Test
    fun loadData_handlesError() = runTest {
        // Arrange
        coEvery { mockUsecase.getData() } throws Exception("Test error")

        // Act
        presenter.loadData()

        // Assert
        verify { mockView.showLoading() }
        verify { mockView.onError(any()) }
        verify { mockView.hideLoading() }
    }

    @After
    fun cleanup() {
        presenter.detach()
    }
}
```

### Composable Testing

```kotlin
@RunWith(AndroidJUnit4::class)
class MyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun screenDisplaysTitle() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                MyScreen()
            }
        }

        composeTestRule.onNodeWithText("Title")
            .assertIsDisplayed()
    }

    @Test
    fun buttonClickTriggersAction() {
        var clicked = false

        composeTestRule.setContent {
            BasicFrameworkTheme {
                MyScreen(onAction = { clicked = true })
            }
        }

        composeTestRule.onNodeWithText("Click Me")
            .performClick()

        assertTrue(clicked)
    }
}
```

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew testDebugUnitTest --jacoco

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "PackageName.ClassName"
```

---

## Documentation

### When to Document

- [ ] New public APIs/functions
- [ ] Architecture changes
- [ ] Complex logic or algorithms
- [ ] Configuration changes
- [ ] New build features

### Documentation Files

Update relevant documentation:
- **CLAUDE.md:** Implementation patterns
- **docs/ARCHITECTURE.md:** Architecture changes
- **docs/COMPOSE_GUIDE.md:** Compose patterns
- **docs/API_EXAMPLES.md:** API changes
- **README.md:** Getting started or build instructions

### Documentation Format

```kotlin
/**
 * Brief description of what this does.
 *
 * Longer explanation if needed. Explain the purpose,
 * any important notes, or common usage patterns.
 *
 * @param paramName Description of parameter
 * @return Description of return value
 * @throws ExceptionType When this exception might be thrown
 *
 * Example:
 * ```kotlin
 * val result = myFunction(param = "value")
 * ```
 */
fun myFunction(param: String): String {
    // Implementation
}
```

---

## Troubleshooting

### Build Issues

**Issue: "Gradle sync failed"**
```bash
./gradlew clean build --refresh-dependencies
```

**Issue: "Unsupported class-file format"**
```bash
# Verify JDK version
java -version
# Should be 17+, update JAVA_HOME if needed
```

### Test Issues

**Issue: "Test hangs indefinitely"**
```bash
# Add timeout to test
@Test(timeout = 5000)
fun myTest() { ... }
```

**Issue: "Compose preview not showing"**
```bash
# Invalidate caches and restart IDE
File → Invalidate Caches → Restart
```

### Git Issues

**Issue: "Merge conflicts"**
```bash
# View conflicts
git status

# Resolve conflicts in editor

# Mark as resolved
git add resolved-file.kt

# Continue merge
git commit -m "Resolve merge conflicts"
```

**Issue: "Lost commits"**
```bash
# Find lost commits
git reflog

# Restore branch
git reset --hard {commit-hash}
```

---

## Getting Help

- **Issues:** Open an issue on GitHub for bugs or features
- **Discussions:** Use GitHub Discussions for questions
- **Documentation:** Check docs/ folder and CLAUDE.md
- **Code Examples:** See existing code for patterns

---

## Acknowledgments

Thank you for contributing to BasicFrameworkProject! Your efforts help make this a better project for everyone.

---

**Last Updated:** 2026-01-11
**Status:** Open for Contributions
**Contributing Guide Version:** 1.0
