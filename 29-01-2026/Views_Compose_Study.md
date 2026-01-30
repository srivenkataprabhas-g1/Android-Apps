# Views in Compose: Complete Study Guide
## View Interoperability in Jetpack Compose

---

## Table of Contents
1. [Introduction & Core Concepts](#introduction)
2. [AndroidView Composable](#androidview)
3. [Practical Examples](#examples)
4. [State Management](#state-management)
5. [Lifecycle Management](#lifecycle)
6. [FAQ & Best Practices](#faq)
7. [Common Use Cases](#use-cases)

---

## <a name="introduction"></a>1. INTRODUCTION & CORE CONCEPTS

### What is View Interoperability?

**Definition:** View Interoperability (View Interop) is the ability to seamlessly integrate traditional Android Views into modern Jetpack Compose applications, and vice versa.

### Why Do We Need It?

As of early 2024, not all Android UI components have Compose equivalents. Some scenarios where View Interop is essential:
- **AdView** (Google Ads/AdMob) - No native Compose component exists
- **RatingBar** - Not yet available as a Composable
- **Spinner** - Can use AndroidView for legacy implementations
- **Custom Views** - Reuse existing custom View implementations
- **Third-party Libraries** - MapView, MediaView, etc. that haven't been migrated to Compose
- **Legacy Codebases** - Gradual migration from View-based to Compose-based architecture

### Core Interoperability APIs

| API | Purpose | Use Case |
|-----|---------|----------|
| **AndroidView** | Embed traditional Views in Compose | Using legacy Views in Compose |
| **ComposeView** | Embed Compose in traditional Views | Adding Compose to View-based Activities/Fragments |
| **AndroidViewBinding** | Embed XML layouts with ViewBinding | Using pre-built XML layouts with data binding |
| **AndroidFragment** | Embed Fragments in Compose | Integrating Fragment-based components |

---

## <a name="androidview"></a>2. THE ANDROIDVIEW COMPOSABLE

### Basic Syntax

```kotlin
@Composable
fun <T : View> AndroidView(
    factory: (Context) -> T,           // Creates the View instance (called once)
    modifier: Modifier = Modifier,      // Standard Compose modifiers
    update: (T) -> Unit = NoOpUpdate   // Called whenever state changes
)
```

### Key Parameters Explained

#### **factory: (Context) -> T**
- **Called:** Only once, when the AndroidView composable first enters composition
- **Purpose:** Create and return the View instance
- **Scope:** Initialization logic only
- **Important:** Do NOT set state-dependent properties here; they won't update later

```kotlin
factory = { context ->
    // Create the view instance
    Spinner(context).apply {
        // Set up adapter and static properties
        adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
    }
}
```

#### **modifier: Modifier**
- Standard Compose modifier for sizing, padding, alignment, etc.
- Examples: `Modifier.fillMaxWidth()`, `Modifier.height(200.dp)`, `Modifier.padding(16.dp)`

```kotlin
AndroidView(
    factory = { /* ... */ },
    modifier = Modifier
        .fillMaxWidth()
        .height(100.dp)
        .padding(8.dp)
)
```

#### **update: (T) -> Unit**
- **Called:** Every time the composable recomposes
- **Purpose:** Update the View based on current Compose state
- **Critical for:** State synchronization between Compose and Views
- **Called with:** The same View instance created by factory

```kotlin
update = { spinner ->
    // This runs whenever selectedItem changes
    spinner.setSelection(selectedItem)
}
```

### Practical Implementation Pattern

The most important pattern to master:

```kotlin
@Composable
fun SpinnerExample() {
    val options = listOf("Option A", "Option B", "Option C")
    var selectedIndex by remember { mutableIntStateOf(0) }
    
    AndroidView(
        factory = { context ->
            // ✅ Create and setup - called once
            Spinner(context).apply {
                adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    options
                )
                // ❌ DON'T set selection here - won't update on state changes
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        selectedIndex = position  // Compose -> View communication
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        },
        update = { spinner ->
            // ✅ Update state - called every recomposition
            if (spinner.selectedItemPosition != selectedIndex) {
                spinner.setSelection(selectedIndex)  // View -> Compose communication
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
```

---

## <a name="examples"></a>3. PRACTICAL EXAMPLES

### Example 1: RatingBar Integration (Juice Tracker App)

**Problem:** Compose doesn't have a native RatingBar, but it's common in surveys/reviews.

```kotlin
@Composable
fun RatingBarView(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                numStars = 5
                stepSize = 0.5f
                setOnRatingBarChangeListener { ratingBar, r, fromUser ->
                    if (fromUser) {
                        onRatingChanged(r)  // Communicate rating back to Compose
                    }
                }
            }
        },
        update = { ratingBar ->
            if (ratingBar.rating != rating) {
                ratingBar.rating = rating  // Update View with Compose state
            }
        },
        modifier = modifier.fillMaxWidth().padding(16.dp)
    )
}

// Usage
@Composable
fun JuiceTrackerEntryDialog() {
    var rating by remember { mutableFloatStateOf(0f) }
    
    Column {
        Text("Rate this juice:")
        RatingBarView(
            rating = rating,
            onRatingChanged = { rating = it }
        )
        Text("Rating: $rating stars")
    }
}
```

### Example 2: Spinner for Color Selection

```kotlin
@Composable
fun ColorSpinner(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf("Red", "Green", "Blue", "Yellow", "Orange")
    
    AndroidView(
        factory = { context ->
            Spinner(context).apply {
                adapter = ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    colors
                )
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        onColorSelected(colors[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        },
        update = { spinner ->
            val currentSelection = colors.indexOf(selectedColor)
            if (spinner.selectedItemPosition != currentSelection && currentSelection >= 0) {
                spinner.setSelection(currentSelection)
            }
        },
        modifier = modifier.fillMaxWidth().padding(8.dp)
    )
}
```

### Example 3: Google AdMob Banner Ad Integration

**Prerequisites:**
```gradle
dependencies {
    implementation 'com.google.android.gms:play-services-ads:20.6.0'
}
```

**Implementation:**

```kotlin
@Composable
fun GoogleAdBanner() {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"  // Test Ad Unit
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

// Usage in your app
@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        GoogleAdBanner()
        
        // Rest of your content
        LazyColumn {
            items(juiceList) { juice ->
                JuiceListItem(juice)
            }
        }
    }
}
```

### Example 4: AndroidViewBinding with Pre-built Layouts

**Prerequisites:**
```gradle
dependencies {
    implementation 'androidx.compose.ui:ui-viewbinding:1.5.0'
}

android {
    buildFeatures {
        viewBinding true
    }
}
```

**XML Layout (res/layout/color_picker.xml):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">
    
    <Spinner
        android:id="@+id/colorSpinner"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</LinearLayout>
```

**Compose Integration:**
```kotlin
@Composable
fun ColorPickerWithBinding(onColorSelected: (String) -> Unit) {
    AndroidViewBinding(ColorPickerBinding::inflate) {
        val colors = arrayOf("Red", "Blue", "Green")
        val adapter = ArrayAdapter(root.context, android.R.layout.simple_spinner_item, colors)
        
        colorSpinner.adapter = adapter
        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onColorSelected(colors[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
```

---

## <a name="state-management"></a>4. STATE MANAGEMENT IN ANDROIDVIEW

### The Factory vs. Update Pattern (Critical Understanding)

**Problem:** Many developers set state-dependent properties in the `factory` lambda and wonder why they don't update.

**Root Cause:** `factory` is called **only once** during initialization. Any state read at that time is "captured" and never updated.

### Anti-Pattern (❌ DON'T DO THIS)

```kotlin
@Composable
fun BadRatingBar() {
    var rating by remember { mutableFloatStateOf(3f) }
    
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                // ❌ BUG: rating is read once here, never updates
                this.rating = rating
                
                setOnRatingBarChangeListener { ratingBar, r, fromUser ->
                    rating = r  // This works (View -> Compose)
                }
            }
        }
    )
}
```

When you change `rating` in Compose, the View won't reflect the change because the assignment in `factory` only happened once.

### Correct Pattern (✅ DO THIS)

```kotlin
@Composable
fun GoodRatingBar() {
    var rating by remember { mutableFloatStateOf(3f) }
    
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                setOnRatingBarChangeListener { ratingBar, r, fromUser ->
                    if (fromUser) rating = r  // View -> Compose
                }
            }
        },
        update = { ratingBar ->
            // ✅ CORRECT: Update called on every recomposition
            if (ratingBar.rating != rating) {
                ratingBar.rating = rating  // Compose -> View
            }
        }
    )
}
```

### Two-Way Communication Pattern

```kotlin
@Composable
fun TwoWaySpinner(
    selectedValue: String,
    onValueChanged: (String) -> Unit
) {
    val items = listOf("Item A", "Item B", "Item C")
    
    AndroidView(
        factory = { context ->
            Spinner(context).apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
                
                // View -> Compose: User selects item
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        onValueChanged(items[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        },
        update = { spinner ->
            // Compose -> View: External state changes
            val currentIndex = items.indexOf(selectedValue)
            if (spinner.selectedItemPosition != currentIndex && currentIndex >= 0) {
                spinner.setSelection(currentIndex, false)  // false = don't trigger listener
            }
        }
    )
}
```

### Using State in Callbacks Safely

When you need to access state inside a listener/callback:

```kotlin
@Composable
fun SafeStateAccess() {
    var data by remember { mutableStateOf("Initial") }
    
    // ✅ Use rememberUpdatedState to access latest state in callbacks
    val currentData by rememberUpdatedState(data)
    
    AndroidView(
        factory = { context ->
            Button(context).apply {
                text = "Click Me"
                setOnClickListener {
                    // Always uses latest value of data
                    println("Current data: ${currentData.value}")
                }
            }
        }
    )
}
```

---

## <a name="lifecycle"></a>5. LIFECYCLE MANAGEMENT

### ViewCompositionStrategy Explained

When integrating Compose into traditional Views (via ComposeView), you need to manage when the Composition is disposed.

```kotlin
composeView.setViewCompositionStrategy(strategy)
```

### Available Strategies

#### 1. **DisposeOnDetachedFromWindow** (Deprecated)
- ❌ No longer recommended
- Was used for simple cases where View is added/removed

#### 2. **DisposeOnDetachedFromWindowOrReleasedFromPool** (Default)
- Disposes when View is detached from window
- OR when released from a reusable pool (like RecyclerView)
- **Use when:** ComposeView in RecyclerView or standalone View hierarchy

```kotlin
composeView.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
)
```

#### 3. **DisposeOnLifecycleDestroyed**
- Disposes when a specific Lifecycle is destroyed
- Requires knowing the LifecycleOwner upfront
- **Use when:** ComposeView in an Activity/Fragment with known Lifecycle

```kotlin
val lifecycle = /* some LifecycleOwner */.lifecycle
composeView.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnLifecycleDestroyed(lifecycle)
)
```

#### 4. **DisposeOnViewTreeLifecycleDestroyed** (Most Common for Fragments)
- Automatically discovers the LifecycleOwner from the window's ViewTree
- Disposes when that Lifecycle is destroyed
- **Best for:** Fragment layouts where lifecycle is not explicitly known
- **Why not DisposeOnLifecycleDestroyed for Fragments?** 
  - Fragments have TWO lifecycles: the Fragment's and the Fragment's View's
  - The View's lifecycle may be destroyed before the Fragment's (e.g., navigation)
  - Using ViewTree strategy ensures proper cleanup when the View is destroyed

```kotlin
composeView.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
)
```

### Real-World Examples

#### In a Fragment (with ViewBinding)
```kotlin
class MyFragment : Fragment() {
    private var _binding: MyFragmentBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyFragmentBinding.inflate(inflater, container, false)
        
        binding.composeView.apply {
            // ✅ Use ViewTree strategy for fragments
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                MaterialTheme {
                    MyComposableContent()
                }
            }
        }
        
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

#### In a RecyclerView Adapter
```kotlin
class ComposableViewHolder(
    private val composeView: ComposeView
) : RecyclerView.ViewHolder(composeView) {
    
    fun bind(item: Item) {
        composeView.apply {
            // ✅ Use DetachedFromWindowOrReleasedFromPool for RecyclerView
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
            )
            setContent {
                MaterialTheme {
                    ItemCard(item)
                }
            }
        }
    }
}
```

### Lifecycle-Aware AndroidView

When using AndroidView, some legacy Views (like MapView) require explicit lifecycle management:

```kotlin
@Composable
fun LifecycleAwareMapView() {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                // Register for lifecycle events
                lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                    override fun onResume(owner: LifecycleOwner) {
                        this@apply.onResume()
                    }
                    override fun onPause(owner: LifecycleOwner) {
                        this@apply.onPause()
                    }
                    override fun onDestroy(owner: LifecycleOwner) {
                        this@apply.onDestroy()
                    }
                })
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

---

## <a name="faq"></a>6. FAQ & BEST PRACTICES

### Q1: When should I use AndroidView vs. creating a native Composable?

**A:** Use AndroidView when:
- ✅ No Compose equivalent exists (AdView, RatingBar, MapView)
- ✅ You have proven custom Views with complex logic
- ✅ Migration happens gradually over time

Prefer native Composables when:
- ✅ A Compose equivalent exists
- ✅ You're building new features
- ✅ You need better animation and state integration

### Q2: Why does my View not respond to state changes?

**A:** Most likely, you're setting properties in `factory` instead of `update`:

```kotlin
// ❌ Wrong - only happens once
factory = { context ->
    MyView(context).apply {
        this.selectedValue = state  // Only set once!
    }
}

// ✅ Correct - happens on every recomposition
update = { view ->
    view.selectedValue = state  // Updates every time
}
```

### Q3: How do I prevent unnecessary updates in the update lambda?

**A:** Check the current value before updating:

```kotlin
update = { view ->
    // ✅ Good: Only update if needed
    if (view.rating != rating) {
        view.rating = rating
    }
}
```

### Q4: My AndroidView is not visible. What's wrong?

**Common causes:**
1. **No size specified** → Add `modifier = Modifier.fillMaxWidth().height(100.dp)`
2. **Layout params incorrect** → Ensure `layoutParams` are set correctly
3. **Parent layout issue** → Wrap in a Box or Column with explicit sizing

```kotlin
// ✅ Correct sizing
Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
    AndroidView(
        factory = { /* ... */ },
        modifier = Modifier.fillMaxSize()
    )
}
```

### Q5: How do I handle listener callbacks that need access to Compose state?

**A:** Use `rememberUpdatedState`:

```kotlin
@Composable
fun ListenerWithState() {
    var counter by remember { mutableIntStateOf(0) }
    val currentCounter by rememberUpdatedState(counter)
    
    AndroidView(
        factory = { context ->
            Button(context).apply {
                setOnClickListener {
                    // ✅ Always has latest counter value
                    println("Counter is now: ${currentCounter.value}")
                }
            }
        }
    )
}
```

### Q6: Should I dispose resources in AndroidView?

**A:** Use `DisposableEffect` for cleanup:

```kotlin
@Composable
fun AndroidViewWithCleanup() {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            MyView(ctx).apply {
                // Initialize expensive resources
                startListening()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    
    DisposableEffect(Unit) {
        onDispose {
            // Cleanup when leaving composition
            // Note: View instance may not be accessible here
            // Better approach: manage cleanup inside the View or factory lambda
        }
    }
}
```

### Best Practice Checklist

- [ ] Use `factory` only for View creation and setup
- [ ] Use `update` for all state-dependent property updates
- [ ] Check for value changes before updating in `update` block
- [ ] Use `rememberUpdatedState` when passing state to callbacks
- [ ] Set explicit size via `modifier` (don't rely on wrap_content)
- [ ] Use appropriate `ViewCompositionStrategy` for ComposeView
- [ ] Test lifecycle behavior (rotation, navigation)
- [ ] Consider memory leaks from long-lived listeners

---

## <a name="use-cases"></a>7. COMMON USE CASES & SOLUTIONS

### Use Case 1: Implementing RatingBar in Juice Tracker App

**Context:** Users need to rate juice drinks with stars. Compose doesn't have RatingBar.

```kotlin
@Composable
fun RatingInput(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("Rate this juice", style = MaterialTheme.typography.bodyLarge)
        
        AndroidView(
            factory = { context ->
                RatingBar(context).apply {
                    numStars = 5
                    stepSize = 1f
                    setOnRatingBarChangeListener { _, r, fromUser ->
                        if (fromUser) {
                            onRatingChanged(r)
                        }
                    }
                }
            },
            update = { ratingBar ->
                if (ratingBar.rating != rating) {
                    ratingBar.rating = rating
                }
            },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .wrapContentSize(Alignment.Center)
        )
        
        Text("${rating.toInt()} stars")
    }
}

// In your entry dialog
@Composable
fun JuiceEntryDialog(onSave: (Juice) -> Unit) {
    var rating by remember { mutableFloatStateOf(0f) }
    
    AlertDialog(
        onDismissRequest = { /* ... */ },
        title = { Text("Add Juice") },
        text = {
            Column {
                // ... other fields
                RatingInput(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newJuice = Juice(rating = rating.toInt())
                onSave(newJuice)
            })
        }
    )
}
```

### Use Case 2: Color Spinner for Juice Tracking

**Context:** Users select juice color from a dropdown.

```kotlin
@Composable
fun ColorSpinnerField(
    label: String,
    selectedColor: JuiceColor,
    onColorSelected: (JuiceColor) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = JuiceColor.values().toList()
    val colorNames = colors.map { it.displayName }
    
    Column(modifier = modifier) {
        Text(label)
        
        AndroidView(
            factory = { context ->
                Spinner(context).apply {
                    adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_item,
                        colorNames
                    )
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            onColorSelected(colors[position])
                        }
                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            },
            update = { spinner ->
                val currentIndex = colors.indexOf(selectedColor)
                if (spinner.selectedItemPosition != currentIndex) {
                    spinner.setSelection(currentIndex, false)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    }
}

// In entry dialog
var selectedColor by remember { mutableStateOf(JuiceColor.ORANGE) }

ColorSpinnerField(
    label = "Juice Color",
    selectedColor = selectedColor,
    onColorSelected = { selectedColor = it }
)
```

### Use Case 3: Ad Banner at Top of List

**Context:** Show banner ads above the juice tracker list.

```kotlin
@Composable
fun JuiceTrackerList(juiceList: List<Juice>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Ad banner at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.LightGray)
        ) {
            GoogleAdBanner()
        }
        
        // List content
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(juiceList) { juice ->
                JuiceListItem(juice)
            }
        }
    }
}

@Composable
fun GoogleAdBanner() {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"
                
                val adRequest = AdRequest.Builder().build()
                loadAd(adRequest)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
    )
}
```

### Use Case 4: Custom View Reuse

**Context:** You have a complex custom View and want to use it in Compose.

```kotlin
@Composable
fun CustomChartView(data: List<ChartDataPoint>, modifier: Modifier = Modifier) {
    var dataPoints by remember { mutableStateOf(data) }
    
    AndroidView(
        factory = { context ->
            ChartView(context).apply {
                setChartDataPoints(dataPoints)
            }
        },
        update = { chartView ->
            chartView.setChartDataPoints(dataPoints)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

// Usage
val myData = remember { mutableStateOf(listOf(/* ... */)) }
CustomChartView(
    data = myData.value,
    modifier = Modifier.padding(16.dp)
)
```

### Use Case 5: Migration Strategy

**Context:** You're gradually migrating a View-based app to Compose.

```kotlin
// Phase 1: Wrap legacy screens
class LegacyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = inflater.inflate(R.layout.legacy_layout, null)
        
        // Add a Compose section
        val composeView = view.findViewById<ComposeView>(R.id.compose_section)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool)
            setContent {
                MyNewComposableFeature()
            }
        }
        
        setContentView(view)
    }
}

// Phase 2: Create Compose screens with AndroidView for legacy components
@Composable
fun NewScreen() {
    Column {
        // New Compose UI
        Text("Welcome to Compose!")
        
        // Still using legacy components
        AndroidView(
            factory = { LegacyCustomView(it) }
        )
    }
}

// Phase 3: Full Compose migration
class FullComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposableApp()
        }
    }
}
```

---

## Quick Reference: Syntax Cheatsheet

```kotlin
// Basic AndroidView
AndroidView(
    factory = { context -> MyView(context) },
    modifier = Modifier.fillMaxWidth().height(100.dp)
)

// With state management
AndroidView(
    factory = { context ->
        MyView(context).apply {
            setListener { value -> onStateChanged(value) }
        }
    },
    update = { view ->
        view.state = currentState
    }
)

// With binding
AndroidViewBinding(MyLayoutBinding::inflate) {
    myView.text = "Hello"
}

// ComposeView in Fragment
binding.composeView.apply {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        MyComposable()
    }
}

// Lifecycle-aware
val lifecycleOwner = LocalLifecycleOwner.current
// Use in DisposableEffect or effects...
```

---

## Summary

**Master these three concepts:**

1. **AndroidView composable** - Wraps traditional Views in Compose
2. **Factory vs. Update pattern** - Factory creates once, Update runs on recomposition
3. **ViewCompositionStrategy** - Manages lifecycle when Compose is embedded in Views

**Common pitfall:**
Setting state-dependent properties in `factory` instead of `update`

**Next steps for learning:**
- Build the Juice Tracker app following the codelab
- Experiment with RatingBar, Spinner, and AdView
- Practice the factory/update pattern until it's intuitive
- Test lifecycle behavior during rotation and navigation

---

*This guide covers Jetpack Compose as of January 2026. Always check the official Android Developers documentation for the latest APIs and best practices.*