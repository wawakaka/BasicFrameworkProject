# Jetpack Compose Best Practices & Patterns Guide

**Last Updated:** 2026-01-11
**Milestone:** 5 Complete
**Framework:** Jetpack Compose 2024.09.00 BOM + Material 3 1.2.1

---

## Table of Contents

1. [Overview](#overview)
2. [Core Principles](#core-principles)
3. [Component Structure](#component-structure)
4. [State Management](#state-management)
5. [Reusable Components](#reusable-components)
6. [Screen Implementation](#screen-implementation)
7. [Theme Integration](#theme-integration)
8. [Navigation](#navigation)
9. [Performance Optimization](#performance-optimization)
10. [Testing](#testing)
11. [Common Patterns](#common-patterns)
12. [Anti-Patterns to Avoid](#anti-patterns-to-avoid)

---

## Overview

Jetpack Compose is a modern, declarative UI framework for building Android applications. It replaces XML-based layouts with Kotlin code, providing type safety, better code reuse, and improved developer experience.

### Benefits of Compose

- **Declarative:** Describe UI state, Compose handles updates
- **Type-Safe:** Full Kotlin type safety for UI code
- **Hot Reload:** Instant previews during development
- **Concise:** Write less code than XML + Kotlin
- **Reactive:** UI automatically updates with state changes
- **Testable:** UI logic is testable Kotlin code

### Project Integration

This project uses:
- **Jetpack Compose BOM 2024.09.00:** Coordinated dependency versions
- **Material 3 1.2.1:** Latest Material Design system
- **Compose Navigation 2.7.7:** Type-safe screen routing
- **Koin 3.5.3:** Dependency injection with `koinInject()`
- **Kotlin Coroutines:** Async operations in composables

---

## Core Principles

### 1. Composables are Functions

A composable is a regular Kotlin function marked with `@Composable`:

```kotlin
@Composable
fun MyComponent(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title)
        Button(onClick = onClick) { Text("Click Me") }
    }
}
```

**Key Points:**
- Functions can be called from other composables
- Parameters are immutable inputs
- No side effects in the function body
- Use `modifier: Modifier = Modifier` as last parameter

### 2. Recomposition

Composables recompose when state changes:

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Count: $count")  // Recomposes when count changes
    }
}
```

**Principles:**
- Only compose functions that depend on changed state recompose
- Recomposition should be frequent and fast
- Avoid expensive operations in compose bodies
- Use `remember` to preserve state across recompositions

### 3. Single Responsibility

Each composable should have one clear purpose:

```kotlin
// ✅ Good - Single responsibility
@Composable
fun UserCard(user: User) {
    Card { UserCardContent(user) }
}

@Composable
fun UserCardContent(user: User) {
    Column { Text(user.name) }
}

// ❌ Bad - Too many responsibilities
@Composable
fun ComplexUserScreen(
    users: List<User>,
    onUserClick: (User) -> Unit,
    onDeleteClick: (User) -> Unit,
    // ... more parameters
) {
    // ... complex logic
}
```

### 4. State Hoisting

Move state to the caller for reusability:

```kotlin
// ✅ Good - State hoisted
@Composable
fun TextInputScreen() {
    var text by remember { mutableStateOf("") }

    Column {
        TextInput(
            value = text,
            onValueChange = { text = it }
        )
        DisplayText(text)
    }
}

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(value = value, onValueChange = onValueChange)
}

// ❌ Bad - State tied to component
@Composable
fun TextInputWithState() {
    var text by remember { mutableStateOf("") }
    TextField(value = text, onValueChange = { text = it })
}
```

---

## Component Structure

### Basic Component Template

```kotlin
@Composable
fun MyComponent(
    // Required parameters
    title: String,
    onAction: () -> Unit,
    // Optional parameters with defaults
    subtitle: String? = null,
    isEnabled: Boolean = true,
    // Always last - for layout customization
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onAction,
                enabled = isEnabled,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Action")
            }
        }
    }
}

// Preview for Android Studio
@Preview(showBackground = true)
@Composable
fun MyComponentPreview() {
    BasicFrameworkTheme {
        MyComponent(
            title = "Preview Title",
            subtitle = "Subtitle",
            onAction = {}
        )
    }
}
```

**Best Practices:**
- Put `modifier` last in parameter list
- Use meaningful parameter names
- Provide default values where appropriate
- Include preview function for development
- Use Material 3 theme resources (colors, typography, shapes)

### Component Types

#### 1. Stateless (Presentational) Components

```kotlin
@Composable
fun CurrencyListItem(
    currencyCode: String,
    rate: Double,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier,
        headlineContent = { Text(currencyCode) },
        supportingContent = { Text("Rate: $rate") }
    )
}
```

**Characteristics:**
- No internal state
- Takes data as parameters
- Callback functions for user interaction
- Easy to test and reuse

#### 2. Stateful (Container) Components

```kotlin
@Composable
fun CurrencySelector(
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf("EUR") }

    Column(modifier = modifier) {
        Button(onClick = { expanded = !expanded }) {
            Text(selectedCurrency)
        }

        if (expanded) {
            CurrencyDropdown(
                onSelect = { currency ->
                    selectedCurrency = currency
                    onCurrencySelected(currency)
                    expanded = false
                }
            )
        }
    }
}
```

**Characteristics:**
- Manages internal state
- Calls callbacks for external communication
- Can contain other stateful components
- Good for self-contained features

---

## State Management

### 1. Using remember

`remember` preserves state across recompositions:

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    Button(onClick = { count++ }) {
        Text("Count: $count")
    }
}
```

**Key Points:**
- Creates value on first composition
- Returns same value on subsequent recompositions
- NOT persisted across process death (use SavedStateHandle for that)

### 2. LaunchedEffect for Side Effects

```kotlin
@Composable
fun DataScreen(
    presenter: DataPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var data by remember { mutableStateOf<List<Item>>(emptyList()) }

    // Execute once when composition enters
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            data = presenter.loadData()
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        LoadingIndicator(modifier = modifier)
    } else {
        DataList(data = data, modifier = modifier)
    }
}
```

**When to Use:**
- Initialize with data
- Subscribe to observable streams
- Trigger presenter actions
- Perform one-time setup

### 3. DisposableEffect for Cleanup

```kotlin
@Composable
fun PresenterScreen(
    presenter: MyPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(MyState()) }

    val view = object : MyContract.View {
        override fun updateState(newState: MyState) {
            state = newState
        }
    }

    // Attach on enter, detach on exit
    DisposableEffect(Unit) {
        presenter.attach(view)
        onDispose { presenter.detach() }
    }

    MyContent(state = state, modifier = modifier)
}
```

**When to Use:**
- Attach/detach listeners
- Initialize/cleanup resources
- Presenter attachment/detachment (MVP pattern)
- RegisterBroadcastReceiver/unregister patterns

### 4. rememberUpdatedState

```kotlin
@Composable
fun DebounceSearch(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf("") }
    val onSearchUpdated = rememberUpdatedState(onSearch)

    LaunchedEffect(searchText) {
        delay(500)  // Debounce delay
        onSearchUpdated.value(searchText)
    }

    TextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = modifier
    )
}
```

**When to Use:**
- Callback references in LaunchedEffect that might change
- Avoid recreating LaunchedEffect unnecessarily

---

## Reusable Components

### Complete Component Examples

#### Loading Indicator

```kotlin
@Composable
fun LoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingIndicatorPreview() {
    BasicFrameworkTheme {
        LoadingIndicator()
    }
}
```

#### Error Message

```kotlin
@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error Occurred",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorMessagePreview() {
    BasicFrameworkTheme {
        ErrorMessage(message = "Failed to load data", onRetry = {})
    }
}
```

#### List Item

```kotlin
@Composable
fun ItemListItem(
    item: Item,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) },
        color = MaterialTheme.colorScheme.surface
    ) {
        ListItem(
            headlineContent = { Text(item.title) },
            supportingContent = { Text(item.description) },
            leadingContent = {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = item.title.take(1),
                        modifier = Modifier.wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ItemListItemPreview() {
    BasicFrameworkTheme {
        ItemListItem(
            item = Item("Title", "Description"),
            onItemClick = {}
        )
    }
}
```

---

## Screen Implementation

### MVP-Adapted Screen Pattern

```kotlin
@Composable
fun MyScreen(
    presenter: MyPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    // 1. State management
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var data by remember { mutableStateOf<List<Item>>(emptyList()) }

    // 2. View adapter - implements contract.View
    val view = object : MyContract.View {
        override fun showLoading() {
            isLoading = true
            errorMessage = null
        }

        override fun hideLoading() {
            isLoading = false
        }

        override fun onDataSuccess(items: List<Item>) {
            data = items
            errorMessage = null
        }

        override fun onDataError(throwable: Throwable) {
            errorMessage = throwable.message ?: "Unknown error"
            isLoading = false
        }
    }

    // 3. Lifecycle management
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.loadData()
    }

    DisposableEffect(Unit) {
        onDispose { presenter.detach() }
    }

    // 4. Render UI based on state
    when {
        isLoading -> LoadingIndicator(modifier = modifier)
        errorMessage != null -> ErrorMessage(
            message = errorMessage!!,
            onRetry = { presenter.loadData() },
            modifier = modifier
        )
        else -> MyScreenContent(
            data = data,
            modifier = modifier
        )
    }
}

@Composable
fun MyScreenContent(
    data: List<Item>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(data) { item ->
            ItemListItem(item = item, onItemClick = {})
        }
    }
}
```

**Key Steps:**
1. Create state variables with `remember { mutableStateOf() }`
2. Create view adapter object implementing Contract.View
3. Use `LaunchedEffect` for initialization (presenter.attach, loadData)
4. Use `DisposableEffect` for cleanup (presenter.detach)
5. Render UI conditionally based on state

---

## Theme Integration

### Using Material 3 Theme

```kotlin
@Composable
fun MyComponent() {
    Column {
        // Use theme colors
        Text(
            text = "Primary Color",
            color = MaterialTheme.colorScheme.primary
        )

        // Use theme typography
        Text(
            text = "Title Text",
            style = MaterialTheme.typography.titleMedium
        )

        // Use theme shapes
        Surface(shape = MaterialTheme.shapes.medium) {
            Text("Rounded Surface")
        }
    }
}
```

### Available Resources

#### Colors
- `primary`, `onPrimary` - Primary color and text on primary
- `secondary`, `onSecondary` - Secondary color and text
- `tertiary`, `onTertiary` - Tertiary color and text
- `background`, `onBackground` - Background and foreground
- `surface`, `onSurface` - Surface and text on surface
- `error`, `onError` - Error and text on error
- `errorContainer`, `onErrorContainer` - Error container variants

#### Typography Styles
- `displayLarge`, `displayMedium`, `displaySmall` - Large display text
- `headlineLarge`, `headlineMedium`, `headlineSmall` - Heading text
- `titleLarge`, `titleMedium`, `titleSmall` - Title text
- `bodyLarge`, `bodyMedium`, `bodySmall` - Body text
- `labelLarge`, `labelMedium`, `labelSmall` - Label text

#### Shapes
- `extraSmall`, `small`, `medium` (default), `large`, `extraLarge`
- All shapes are `RoundedCornerShape` with appropriate corner radii

### Light/Dark Theme

```kotlin
@Composable
fun BasicFrameworkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
```

**Features:**
- Automatic dark/light theme detection
- Full color scheme adaptation
- No manual theme switching needed
- Works on Android 5.0+ (API 21+)

---

## Navigation

### Type-Safe Navigation with Compose

```kotlin
@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "currency",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("currency") {
            CurrencyScreen(
                onNavigateToDetails = { currencyCode ->
                    navController.navigate("details/$currencyCode")
                }
            )
        }

        composable("details/{currencyCode}") { backStackEntry ->
            val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: ""
            DetailsScreen(
                currencyCode = currencyCode,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
```

**Best Practices:**
- Define routes as constants
- Use string interpolation for parameters
- Retrieve parameters from `backStackEntry.arguments`
- Use `navController.popBackStack()` for back navigation
- Use `navController.navigate()` for forward navigation

---

## Performance Optimization

### 1. Composition Skipping

```kotlin
// ✅ Good - Stable parameter types (skips recomposition if unchanged)
@Composable
fun Item(name: String, count: Int) {
    Text(name)
}

// ❌ Bad - Unstable lambda (always recomposes)
@Composable
fun Item(name: String, onDelete: (String) -> Unit) {
    Button(onClick = { onDelete(name) }) { Text("Delete") }
}

// ✅ Better - Stable parameter with remember
@Composable
fun Item(name: String, onDelete: (String) -> Unit) {
    val onDeleteMemoized = remember(name) { { onDelete(name) } }
    Button(onClick = onDeleteMemoized) { Text("Delete") }
}
```

### 2. LazyColumn for Lists

```kotlin
// ✅ Good - Only composites visible items
@Composable
fun ItemList(items: List<Item>) {
    LazyColumn {
        items(items) { item ->
            ItemCard(item)
        }
    }
}

// ❌ Bad - Composes all items (slow for large lists)
@Composable
fun ItemList(items: List<Item>) {
    Column {
        items.forEach { item ->
            ItemCard(item)
        }
    }
}
```

### 3. Key for List Stability

```kotlin
// ✅ Good - Stable keys preserve component state
@Composable
fun ItemList(items: List<Item>) {
    LazyColumn {
        items(items, key = { it.id }) { item ->
            ItemCard(item)  // State preserved if list reorders
        }
    }
}
```

### 4. Avoid State in Large Compositions

```kotlin
// ✅ Good - Extract small composable with state
@Composable
fun OptimizedScreen() {
    var data by remember { mutableStateOf(listOf<Item>()) }

    LazyColumn {
        items(data) { item ->
            StatelessItemCard(item)  // No state here
        }
    }
}

// ❌ Bad - State at root causes full recomposition
@Composable
fun UnoptimizedScreen() {
    var data by remember { mutableStateOf(listOf<Item>()) }
    var selectedId by remember { mutableStateOf(0) }

    LazyColumn {
        items(data) { item ->
            ItemCardWithState(item, selectedId)  // Full recompose on selection
        }
    }
}
```

---

## Testing

### Unit Testing Composables

```kotlin
@RunWith(AndroidJUnit4::class)
class MyComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun componentDisplaysTitle() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                MyComponent(title = "Test Title", onAction = {})
            }
        }

        composeTestRule.onNodeWithText("Test Title").assertIsDisplayed()
    }

    @Test
    fun buttonClickTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            BasicFrameworkTheme {
                MyComponent(
                    title = "Test",
                    onAction = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Action").performClick()
        assertTrue(clicked)
    }
}
```

### Preview Testing

```kotlin
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MyComponentPreview() {
    BasicFrameworkTheme {
        MyComponent(title = "Preview", onAction = {})
    }
}
```

---

## Common Patterns

### 1. State Wrapper Pattern

```kotlin
data class ScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: List<Item> = emptyList()
)

@Composable
fun ScreenWithState() {
    var state by remember { mutableStateOf(ScreenState()) }

    // Single state object easier to manage
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorMessage(state.error!!)
        else -> ContentScreen(state.data)
    }
}
```

### 2. View Model with Compose

```kotlin
// In ViewModel
class MyViewModel : ViewModel() {
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()
}

// In Composable
@Composable
fun MyScreen(viewModel: MyViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    // Use state
}
```

### 3. Conditional Content

```kotlin
@Composable
fun ConditionalContent(isVisible: Boolean) {
    AnimatedVisibility(visible = isVisible) {
        Text("Visible content")
    }
}
```

---

## Anti-Patterns to Avoid

### ❌ Don't Place State in Composable Parameters

```kotlin
// ❌ Bad
@Composable
fun BadComponent(value: MutableState<String>) {
    // Direct state mutation from outside
    TextField(value = value.value)
}

// ✅ Good
@Composable
fun GoodComponent(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(value = value, onValueChange = onValueChange)
}
```

### ❌ Don't Create Objects in Composable Body

```kotlin
// ❌ Bad - recreates on every recomposition
@Composable
fun BadComponent() {
    val list = mutableListOf(1, 2, 3)  // New instance each time
}

// ✅ Good - created once
@Composable
fun GoodComponent() {
    val list = remember { mutableListOf(1, 2, 3) }
}
```

### ❌ Don't Perform Side Effects Outside Effect Blocks

```kotlin
// ❌ Bad - side effect in composition
@Composable
fun BadComponent() {
    Log.d("TAG", "Composing")  // Logs on every recomposition
}

// ✅ Good - side effect in LaunchedEffect
@Composable
fun GoodComponent() {
    LaunchedEffect(Unit) {
        Log.d("TAG", "Initialized")
    }
}
```

### ❌ Don't Use Complex Calculations in Render

```kotlin
// ❌ Bad - expensive calculation on every recomposition
@Composable
fun BadComponent(items: List<Int>) {
    val sum = items.sumOf { it * it * it }
    Text("Sum: $sum")
}

// ✅ Good - memoized calculation
@Composable
fun GoodComponent(items: List<Int>) {
    val sum = remember(items) {
        items.sumOf { it * it * it }
    }
    Text("Sum: $sum")
}
```

---

## Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design System](https://m3.material.io/)
- [Compose Best Practices](https://developer.android.com/jetpack/compose/performance)
- [Testing Compose](https://developer.android.com/jetpack/compose/testing)

---

**Last Updated:** 2026-01-11
**Status:** Complete
**Ready for Use:** ✅ YES
