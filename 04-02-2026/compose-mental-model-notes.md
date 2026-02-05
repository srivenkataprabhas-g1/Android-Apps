# Jetpack Compose: Mental Model - Complete Study Notes

## Table of Contents
1. [Core Concepts](#core-concepts)
2. [Declarative Programming Paradigm](#declarative-programming-paradigm)
3. [Composable Functions](#composable-functions)
4. [Recomposition](#recomposition)
5. [Key Principles](#key-principles)
6. [Examples & Code Patterns](#examples--code-patterns)
7. [FAQs](#faqs)
8. [Common Pitfalls](#common-pitfalls)

---

## Core Concepts

### What is Jetpack Compose?

**Definition:** Jetpack Compose is a modern declarative UI toolkit for Android that simplifies writing and maintaining app UI by providing a declarative API that lets you render your app UI without imperatively mutating frontend views.

**Key Difference from Traditional Android:**
- **Traditional (Imperative):** You manually tell the UI what to do (e.g., `button.setText()`, `container.addChild()`)
- **Compose (Declarative):** You describe what the UI should look like, and Compose handles the updates

### Why Declarative UI?

**Problems with Imperative Approach:**
- Manually walking the UI tree using `findViewById()` is error-prone
- If data is rendered in multiple places, you might forget to update one view
- Leads to illegal states when two updates conflict (e.g., trying to set a value on a removed node)
- Maintenance complexity grows with the number of views requiring updates

**Advantages of Declarative Approach:**
- Conceptually regenerates the entire screen from scratch
- Applies only necessary changes
- Avoids manual state management complexity
- More maintainable and predictable code

---

## Declarative Programming Paradigm

### Historical Context

Historically, Android UI used an **imperative object-oriented approach:**
1. You instantiate a tree of widgets
2. Each widget maintains its own internal state
3. You access widgets through getter and setter methods
4. Updates happen by calling methods on individual widgets

### The Paradigm Shift: Imperative → Declarative

| Aspect | Imperative (Traditional) | Declarative (Compose) |
|--------|--------------------------|----------------------|
| **Approach** | Manually mutate UI widgets | Describe desired UI state |
| **State** | Stored in widgets | Stored in app logic |
| **Updates** | Call setter methods | Re-call function with new data |
| **Architecture** | Widget-centric | Data-centric |
| **Reuse** | Limited by widget structure | High, through composition |

### How Declarative Works

```
App Logic (Data) 
    ↓
Top-level Composable Function
    ↓
Child Composable Functions (with data passed down)
    ↓
UI Elements Rendered on Screen
```

When user interacts with UI:
```
User Interaction
    ↓
Event (e.g., onClick)
    ↓
App Logic Updates State
    ↓
Composable Functions Called Again with New Data
    ↓
UI Redrawn (Recomposition)
```

---

## Composable Functions

### Definition and Structure

A **Composable Function** is a Kotlin function that:
- Takes data as input
- Emits UI elements as output
- Is annotated with `@Composable`
- Does not return anything (describes state instead)

### Essential Properties of Composable Functions

All composable functions MUST follow these three rules:

#### 1. **Idempotent**
- Returns the same result when called multiple times with the same arguments
- No reliance on global state or random values

#### 2. **Side-Effect Free**
- Does not modify properties or global variables
- Does not perform I/O operations
- Does not have hidden dependencies

#### 3. **Fast**
- Executes quickly to avoid UI jank
- No expensive operations (file reads, network calls, database queries)

### Example: Basic Composable Function

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello $name")
}
```

**Breaking it down:**

| Component | Explanation |
|-----------|-------------|
| `@Composable` | Annotation telling Compose compiler this converts data to UI |
| `name: String` | Data input parameter |
| `Text("Hello $name")` | Calls another composable function to emit UI |
| No return statement | Describes state, doesn't construct objects |

### Key Characteristics

1. **Annotation Required:** `@Composable` must be present
2. **Data Input:** Accepts parameters to describe UI
3. **UI Emission:** Calls other composables to emit UI hierarchy
4. **No Return Value:** Returns Unit (nothing)
5. **Pure Function:** Behavior depends only on inputs

### Dynamic Content in Composables

Composable functions are written in Kotlin, so they can use any Kotlin construct:

```kotlin
@Composable
fun Greeting(names: List<String>) {
    for (name in names) {
        Text("Hello $name")
    }
}
```

**Possible dynamic constructs:**
- `if` statements (conditional rendering)
- `for` loops (iterating over lists)
- `when` expressions
- Helper function calls
- Full language flexibility

---

## Recomposition

### Definition

**Recomposition** is the process of calling composable functions again when their inputs change, allowing Compose to intelligently update only the parts of the UI that changed.

### Why Recomposition Matters

Regenerating the entire screen every time state changes would be expensive in terms of:
- Time
- Computing power
- Battery usage

**Solution:** Compose intelligently chooses which parts need redrawing.

### How Recomposition Works

```
Input Changes
    ↓
Compose Identifies Affected Composables
    ↓
Only Those Functions Re-execute
    ↓
UI Updates (if necessary)
```

### Example: Button Click Counter

```kotlin
@Composable
fun ClickCounter(clicks: Int, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("I've been clicked $clicks times")
    }
}
```

**Process:**
1. Button is clicked
2. `clicks` value increases
3. `ClickCounter()` is called again with new `clicks` value
4. `Text()` is recomposed to show new value
5. Other composables not depending on `clicks` are skipped

### Recomposition Skips as Much as Possible

Compose recomposes only the functions and lambdas affected by changed parameters.

**Example: List with Header**

```kotlin
@Composable
fun NamePicker(
    header: String,
    names: List<String>,
    onNameClicked: (String) -> Unit
) {
    Column {
        // Recomposes when header changes, NOT when names changes
        Text(header, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider()

        LazyColumn {
            items(names) { name ->
                // Recomposes when individual name updates
                // Does NOT recompose when header changes
                NamePickerItem(name, onNameClicked)
            }
        }
    }
}

@Composable
private fun NamePickerItem(name: String, onClicked: (String) -> Unit) {
    Text(name, Modifier.clickable(onClick = { onClicked(name) }))
}
```

**Recomposition Behavior:**
- When `header` changes: Only `Text(header)` recomposes; `LazyColumn` is skipped
- When `names` changes: Only affected `NamePickerItem()` instances recompose
- `header` changes don't trigger `NamePickerItem` recomposition

### Recomposition is Optimistic

**What does "optimistic" mean?**
- Compose starts recomposition when parameters might have changed
- Assumes recomposition will finish before parameters change again
- If a parameter changes before recomposition finishes, Compose **cancels** and restarts

**Important Consequence:**
If you have side-effects dependent on UI being displayed, they execute even if composition is canceled, leading to **inconsistent app state**.

**Example of Problem:**
```kotlin
@Composable
fun BadExample(value: Int) {
    // Dangerous! Side-effect that might execute even if composition is canceled
    LaunchedEffect(Unit) {
        updateSharedPreferences(value)  // ← Wrong place!
    }
    Text("Value: $value")
}
```

**Correct Approach:**
```kotlin
@Composable
fun GoodExample(value: Int, onValueChanged: (Int) -> Unit) {
    Button(onClick = { onValueChanged(value + 1) }) {
        Text("Value: $value")
    }
}
```

---

## Key Principles for Composable Design

### 1. Composable Functions Might Run Quite Frequently

**Frequency:** Often runs every frame of a UI animation

**Impact:** Expensive operations cause UI jank and performance issues

**Anti-pattern:**
```kotlin
@Composable
fun BadPerformance() {
    // Reading from storage hundreds of times per second! ← WRONG
    val settings = readFromDeviceStorage()
    Text(settings.value)
}
```

**Correct Pattern:**
```kotlin
@Composable
fun GoodPerformance(value: Boolean) {
    // Data passed in as parameter
    // Expensive work done elsewhere (ViewModel, background thread)
    Checkbox(checked = value, onCheckedChange = { /* callback */ })
}
```

### 2. Never Depend on Side-Effects from Composable Execution

**Why?** Recomposition may be skipped, causing unpredictable behavior

**Side-Effects to Avoid:**
- Writing to shared object properties
- Updating observables in ViewModel
- Updating SharedPreferences
- Writing to files
- Making network calls
- Modifying global variables

**Proper Pattern:**
```kotlin
@Composable
fun SharedPrefsToggle(
    text: String,
    value: Boolean,
    onValueChanged: (Boolean) -> Unit
) {
    Row {
        Text(text)
        Checkbox(checked = value, onCheckedChange = onValueChanged)
    }
}
// Side-effects (reading/writing prefs) handled in ViewModel or callback
```

### 3. Composable Functions Could Run in Parallel

**Current Reality:** Composable functions currently run on the main thread sequentially

**Future:** Compose may execute them in parallel to leverage multiple CPU cores

**Important:** Write composables as if they could be multithreaded

**Anti-pattern (Not thread-safe):**
```kotlin
@Composable
fun ListWithBug(myList: List<String>) {
    var items = 0  // ← Shared mutable state

    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            for (item in myList) {
                Card {
                    Text("Item: $item")
                    items++  // ← Dangerous! Side-effect and race condition risk
                }
            }
        }
        Text("Count: $items")  // ← May show wrong count
    }
}
```

**Correct Pattern:**
```kotlin
@Composable
fun ListCorrect(myList: List<String>) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            for (item in myList) {
                Card {
                    Text("Item: $item")
                }
            }
        }
        Text("Count: ${myList.size}")  // ← Derived from input, not mutation
    }
}
```

### 4. Composable Functions Can Execute in Any Order

**Important:** Do not assume execution order based on code appearance

**Example:**
```kotlin
@Composable
fun ButtonRow() {
    MyFancyNavigation {
        StartScreen()      // Might execute 3rd
        MiddleScreen()     // Might execute 1st
        EndScreen()        // Might execute 2nd
    }
}
```

**Why?** Compose may prioritize rendering certain UI elements first

**Implication:** Each composable must be self-contained and independent

**Anti-pattern:**
```kotlin
@Composable
fun BadOrder() {
    var counter = 0
    
    if (condition) {
        counter = 5  // Set in first function
    }
    
    Text("Count: $counter")  // Depends on first function execution
}
```

**Correct Pattern:** Pass data through parameters, don't rely on execution order

---

## Examples & Code Patterns

### Pattern 1: Data Flow (Unidirectional)

```kotlin
// State lives in parent/ViewModel
class MyViewModel {
    var count = mutableStateOf(0)
}

// Parent passes data down
@Composable
fun MyScreen(viewModel: MyViewModel) {
    ClickCounter(
        clicks = viewModel.count.value,
        onClick = { viewModel.count.value++ }
    )
}

// Child receives data and callbacks
@Composable
fun ClickCounter(clicks: Int, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Clicked $clicks times")
    }
}
```

### Pattern 2: Handling Expensive Operations

```kotlin
// WRONG: Reading in composable
@Composable
fun BadPrefs() {
    val savedValue = readFromSharedPreferences()  // ← Every recomposition!
    Text(savedValue)
}

// CORRECT: Read in ViewModel, pass to composable
@Composable
fun GoodPrefs(value: String) {
    Text(value)  // ← Already loaded, fast execution
}

// ViewModel handles expensive operation
class MyViewModel {
    val savedValue = mutableStateOf("")
    
    init {
        viewModelScope.launch(Dispatchers.Default) {
            savedValue.value = readFromSharedPreferences()
        }
    }
}
```

### Pattern 3: List Rendering with Efficient Recomposition

```kotlin
@Composable
fun UserList(
    users: List<User>,
    onUserSelected: (User) -> Unit
) {
    LazyColumn {
        items(users) { user ->
            // Each item recomposes independently
            UserCard(user = user, onClick = { onUserSelected(user) })
        }
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    Card(modifier = Modifier.clickable(onClick = onClick)) {
        Text(user.name)
        Text(user.email)
    }
}
```

### Pattern 4: Conditional UI Based on State

```kotlin
@Composable
fun LoginScreen(isLoggedIn: Boolean, userName: String) {
    if (isLoggedIn) {
        Text("Welcome, $userName")
    } else {
        Button(onClick = { /* navigate to login */ }) {
            Text("Login")
        }
    }
}
```

### Pattern 5: Correct Side-Effect Handling

```kotlin
@Composable
fun DataDisplay(dataId: Int) {
    val data = remember { mutableStateOf("") }
    
    // Correct: Side-effect in LaunchedEffect, not in composable body
    LaunchedEffect(dataId) {
        data.value = fetchDataFromServer(dataId)
    }
    
    Text(data.value)
}
```

---

## FAQs

### Q1: Why can't I use `var` to track state in a composable?

**A:** Because composables can recompose at any time (every frame during animation). Local mutable variables don't persist across recompositions. They reset on each recomposition, making your UI behave incorrectly. Use state management APIs like `remember` and `mutableStateOf` instead.

```kotlin
// ❌ WRONG
@Composable
fun Counter() {
    var count = 0
    Button(onClick = { count++ }) {
        Text("Clicked $count times")  // Always shows 0
    }
}

// ✅ CORRECT
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Button(onClick = { count++ }) {
        Text("Clicked $count times")  // Correctly updates
    }
}
```

### Q2: When should I move logic out of a composable?

**A:** Move any expensive operation outside the composable:
- File I/O
- Network requests
- Database queries
- Calculations on large datasets
- SharedPreferences reading/writing
- Sensor data collection

Instead, do these in a ViewModel or coroutine and pass the result to the composable.

### Q3: What's the difference between `@Composable` and regular functions?

**A:** Regular functions return objects; composable functions emit UI and return nothing. Composables:
- Must be annotated with `@Composable`
- Can only be called from other composables
- Cannot call regular functions that expect composables
- Create a special function type that Compose can track and optimize

### Q4: Can composable functions be called multiple times?

**A:** Yes, and they are! Whenever their parameters change, Compose calls them again. However, Compose intelligently skips calling composables whose parameters haven't changed. This is the core optimization mechanism.

### Q5: What happens if I modify a global variable inside a composable?

**A:** You'll experience unpredictable behavior:
- The modification might execute, or might be skipped during recomposition
- Multiple instances might modify it concurrently if Compose becomes multithreaded
- Other composables reading that variable won't recompose when it changes
- Your app state becomes inconsistent

### Q6: Why is idempotency important?

**A:** If a composable isn't idempotent (same input → different output), Compose can't know whether it needs to recompose. This breaks the framework's optimization. For example:

```kotlin
// ❌ NOT idempotent
@Composable
fun BadIdempotent() {
    Text(getCurrentTimeMillis())  // Different each call!
}

// ✅ Idempotent
@Composable
fun GoodIdempotent(timeMs: Long) {
    Text(formatTime(timeMs))  // Same input = same output
}
```

### Q7: What's the relationship between recomposition and animation performance?

**A:** During animations, composables recompose every frame (60+ times per second). If your composable does expensive work, it causes UI jank (stuttering). This is why keeping composables fast is critical:
- No I/O operations
- No complex calculations
- No expensive iterations on large collections

### Q8: Can I rely on execution order of composables?

**A:** No! Compose may execute child composables in any order for optimization. Don't write code like:

```kotlin
// ❌ WRONG - Relies on execution order
@Composable
fun OrderDependent() {
    var setup = false
    Button(onClick = { setup = true })  // Might execute second
    if (setup) {
        Text("Ready")  // Might execute first
    }
}
```

### Q9: What's the difference between composition and recomposition?

**A:** 
- **Composition:** Initial creation of the UI tree from composable functions
- **Recomposition:** Re-executing composable functions when inputs change to update the UI tree

### Q10: How does Compose handle the "expensive operations" problem?

**A:** Three strategies:
1. **Move to background thread:** Use ViewModelScope.launch(Dispatchers.Default) or similar
2. **Pass as parameter:** Load in ViewModel, pass result to composable
3. **Use LaunchedEffect:** Trigger side-effects only when specific inputs change

---

## Common Pitfalls

### Pitfall 1: Using Random or Global State

```kotlin
// ❌ WRONG - Not idempotent
@Composable
fun RandomText() {
    Text(Random.nextInt().toString())
}

// ✅ CORRECT - Pass random value as parameter
@Composable
fun RandomText(randomValue: Int) {
    Text(randomValue.toString())
}
```

### Pitfall 2: Directly Reading from Device Storage

```kotlin
// ❌ WRONG - Executes every recomposition
@Composable
fun SettingsDisplay() {
    val setting = readSharedPreferences("key")  // Hundreds of times per second!
    Text(setting)
}

// ✅ CORRECT - Read once in ViewModel
@Composable
fun SettingsDisplay(setting: String) {
    Text(setting)
}
```

### Pitfall 3: Modifying Mutable Variables

```kotlin
// ❌ WRONG - Mutates local variable on every recomposition
@Composable
fun ItemList(items: List<String>) {
    var itemCount = 0
    Column {
        items.forEach { item ->
            itemCount++  // Resets to 0 each recomposition!
            Text("$item ($itemCount)")
        }
    }
}

// ✅ CORRECT - Use list size directly
@Composable
fun ItemList(items: List<String>) {
    Column {
        items.forEachIndexed { index, item ->
            Text("$item (${index + 1})")  // Index comes from iteration
        }
    }
}
```

### Pitfall 4: Not Using Keys in Lists

```kotlin
// ❌ Without keys - Items recompose on reorder
LazyColumn {
    items(users) { user ->
        UserCard(user)
    }
}

// ✅ With keys - Items maintain identity
LazyColumn {
    items(users, key = { user -> user.id }) { user ->
        UserCard(user)
    }
}
```

### Pitfall 5: Assuming Composables Execute Sequentially

```kotlin
// ❌ WRONG - Assumes StartScreen executes before MiddleScreen
@Composable
fun TabScreen() {
    var initialized = false
    
    StartScreen()  // Might set initialized
    if (initialized) {
        MiddleScreen()  // Might expect initialized = true
    }
}

// ✅ CORRECT - Each screen is self-contained
@Composable
fun TabScreen(isInitialized: Boolean) {
    StartScreen(isInitialized)
    if (isInitialized) {
        MiddleScreen()
    }
}
```

### Pitfall 6: Performing Side-Effects in Composable Body

```kotlin
// ❌ WRONG - Side-effect in composable body
@Composable
fun WrongSideEffect() {
    analytics.logScreenView()  // Fires every recomposition!
    Text("Screen")
}

// ✅ CORRECT - Side-effect in LaunchedEffect
@Composable
fun CorrectSideEffect() {
    LaunchedEffect(Unit) {
        analytics.logScreenView()  // Fires once
    }
    Text("Screen")
}
```

### Pitfall 7: Not Memoizing Expensive Computations

```kotlin
// ❌ WRONG - Recomputed every recomposition
@Composable
fun ExpensiveList(items: List<String>) {
    val sorted = items.sortedBy { it.length }  // Recalculated every time!
    LazyColumn {
        items(sorted) { item -> Text(item) }
    }
}

// ✅ CORRECT - Memoized using remember
@Composable
fun ExpensiveList(items: List<String>) {
    val sorted = remember(items) {
        items.sortedBy { it.length }  // Only recalculated when items changes
    }
    LazyColumn {
        items(sorted) { item -> Text(item) }
    }
}
```

### Pitfall 8: Creating Objects in Composable Body

```kotlin
// ❌ WRONG - New object created each recomposition
@Composable
fun BadObject() {
    val modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    Text("Hello", modifier = modifier)
}

// ✅ CORRECT - Object remembered
@Composable
fun GoodObject() {
    val modifier = remember {
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    }
    Text("Hello", modifier = modifier)
}
```

---

## Summary Table: Composable Best Practices

| Principle | Do ✅ | Don't ❌ |
|-----------|------|---------|
| **State** | Use `remember`, `mutableStateOf` | Mutable local variables |
| **Operations** | Pass data as parameters | Read files/DB in composable |
| **Side-Effects** | Use `LaunchedEffect` | Execute in composable body |
| **Modification** | Accept callbacks | Modify global/shared objects |
| **Purity** | Idempotent, side-effect free | Depend on global state |
| **Order** | Self-contained composables | Assume execution order |
| **Performance** | Fast execution | Expensive operations |
| **Mutability** | Immutable parameters | Mutable parameters |
| **Threading** | UI thread callbacks | Modify state from other threads |

---

## Key Takeaways

1. **Declarative > Imperative:** Compose describes UI state, not imperative mutations
2. **Composables are pure functions:** Idempotent, side-effect free, fast
3. **Recomposition is selective:** Only affected composables re-execute
4. **Never do expensive work in composables:** Move to background threads or ViewModel
5. **State lives outside composables:** Pass through parameters, not stored in composables
6. **Callbacks trigger updates:** Events flow up, data flows down
7. **Every composable is independent:** Don't assume order or execution
8. **Think parallel:** Write code as if composables could run multithreaded

