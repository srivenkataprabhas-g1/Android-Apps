# View Interoperability in Compose - Advanced FAQs & Troubleshooting

## Frequently Asked Questions (FAQ)

---

## 1. GENERAL QUESTIONS

### Q: What is the difference between AndroidView and ComposeView?

**A:**
| Aspect | AndroidView | ComposeView |
|--------|------------|-----------|
| **Purpose** | Embed traditional Views IN Compose | Embed Compose IN traditional Views |
| **Direction** | Views → Compose | Compose → Views |
| **Used in** | Compose-first apps | View-first/legacy apps |
| **Example** | `AndroidView(factory = { Spinner(it) })` | `binding.composeView.setContent { }` |

**When to use AndroidView:**
- You're building a Compose app
- You need a View component that doesn't exist in Compose
- You're gradually migrating to Compose

**When to use ComposeView:**
- You're in a View-based Activity/Fragment
- You want to add new Compose UI features
- You're doing a phased migration

---

### Q: Should I always use AndroidView instead of building a Compose equivalent?

**A:** No. Use this decision matrix:

```
Does Compose have an equivalent?
├─ YES
│  ├─ Use Compose version (better performance, animations, integration)
│  └─ Except: You already have proven custom View logic
└─ NO
   └─ Use AndroidView wrapping
```

**Examples:**
- ❌ DON'T use `AndroidView(Spinner)` → Use `ExposedDropdownMenuBox`
- ✅ DO use `AndroidView(RatingBar)` → No Compose equivalent exists
- ✅ DO use `AndroidView(AdView)` → No Compose equivalent exists
- ❌ DON'T use `AndroidView(Button)` → Use Compose `Button`

---

### Q: What are the performance implications of AndroidView?

**A:** 
- **Cons:**
  - Android View system has overhead (layout passes, drawing)
  - Bridge cost between Compose and View hierarchies
  - May skip some Compose optimizations (Skiko rendering, recomposition scope)
  - More memory if not properly disposed

- **Pros:**
  - Negligible if only a few AndroidViews
  - Better than complete rewrite to Compose
  - Gradual migration allows phased optimization

**Best practice:** Use AndroidView for necessary components, not as a crutch.

---

## 2. STATE MANAGEMENT & UPDATES

### Q: Why does the View not update when Compose state changes?

**A:** 99% of the time: **You're setting properties in factory instead of update.**

```kotlin
// ❌ This doesn't work
AndroidView(
    factory = { context ->
        MyView(context).apply {
            value = myState  // Only runs once!
        }
    }
)

// ✅ This works
AndroidView(
    factory = { context -> MyView(context) },
    update = { view -> view.value = myState }  // Runs on every recomposition
)
```

**Why?**
- `factory` is called once during composition creation
- It captures the state value at that moment
- When state changes, factory doesn't run again
- Only `update` runs on recomposition

---

### Q: How do I prevent unnecessary updates in the update lambda?

**A:** Check before updating:

```kotlin
update = { view ->
    // ✅ Good: Only update if necessary
    if (view.currentValue != newValue) {
        view.currentValue = newValue
    }
}

// OR use a mutable state that tracks previous value
update = { view ->
    val previous = remember(newValue) { newValue }
    if (view.value != previous) {
        view.value = previous
    }
}
```

**Why:**
- Prevents unnecessary View redraws
- Avoids triggering View listeners again (preventing state loops)
- Better performance

---

### Q: How do I access current state in a callback/listener set in factory?

**A:** Use `rememberUpdatedState`:

```kotlin
@Composable
fun MyComponent(state: String) {
    val currentState by rememberUpdatedState(state)
    
    AndroidView(
        factory = { context ->
            MyView(context).apply {
                setOnEventListener {
                    // ✅ Always has latest state
                    println("Current: ${currentState.value}")
                    // ❌ vs just using state captures old value
                }
            }
        }
    )
}
```

**Why rememberUpdatedState is needed:**
- Callback is created once in factory
- It captures variable at that time
- Without rememberUpdatedState, it would always use the original value
- rememberUpdatedState returns a State object that updates without recreating callbacks

---

### Q: How do I handle two-way binding (View updates Compose, Compose updates View)?

**A:** Implement both directions:

```kotlin
@Composable
fun TwoWayView(
    value: String,
    onValueChange: (String) -> Unit
) {
    AndroidView(
        factory = { context ->
            MyView(context).apply {
                // Direction 1: View → Compose
                setOnChangeListener { newValue ->
                    onValueChange(newValue)
                }
            }
        },
        update = { view ->
            // Direction 2: Compose → View
            if (view.getValue() != value) {
                view.setValue(value)
            }
        }
    )
}
```

**Usage:**
```kotlin
var selectedValue by remember { mutableStateOf("A") }

TwoWayView(
    value = selectedValue,
    onValueChange = { newValue ->
        selectedValue = newValue  // Update Compose state when View changes
    }
)
```

---

## 3. LIFECYCLE & COMPOSITION MANAGEMENT

### Q: What's the difference between the ViewCompositionStrategy options?

**A:**

| Strategy | Disposes When | Use Case | Risk |
|----------|---------------|----------|------|
| **DisposeOnDetachedFromWindow** | View detached | Simple standalone Views | Deprecated |
| **DisposeOnDetachedFromWindowOrReleasedFromPool** | Detached OR pool released | RecyclerView items | Default, safest for reusable Views |
| **DisposeOnLifecycleDestroyed** | Specific Lifecycle destroyed | Activity/Fragment with known Lifecycle | Manual Lifecycle management |
| **DisposeOnViewTreeLifecycleDestroyed** | Window's ViewTree Lifecycle destroyed | Fragment (auto-discovers Lifecycle) | Most appropriate for Fragments |

**Decision tree:**
```
Are you using ComposeView in a Fragment?
├─ YES → Use DisposeOnViewTreeLifecycleDestroyed
└─ NO
   └─ Is it in a RecyclerView?
      ├─ YES → Use DisposeOnDetachedFromWindowOrReleasedFromPool (default)
      └─ NO
         └─ Do you have explicit Lifecycle reference?
            ├─ YES → Use DisposeOnLifecycleDestroyed(yourLifecycle)
            └─ NO → Use DisposeOnViewTreeLifecycleDestroyed
```

---

### Q: Why does my View's state disappear after navigation?

**A:** Likely causes:

1. **Wrong ViewCompositionStrategy**
   ```kotlin
   // ❌ Wrong for Fragment
   setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
   
   // ✅ Right for Fragment
   setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
   ```

2. **View not preserving state across Lifecycle**
   ```kotlin
   // If your View has state, save it
   override fun onSaveInstanceState(): Parcelable {
       return Bundle().apply {
           putString("myState", value)
           putParcelable("parent", super.onSaveInstanceState())
       }
   }
   ```

3. **Recreating the View in factory on every recomposition**
   ```kotlin
   // ❌ Factory gets called multiple times
   AndroidView(factory = { MyView(it) })
   
   // ✅ View is reused
   var viewInstance: MyView? = null
   AndroidView(
       factory = { context ->
           if (viewInstance == null) viewInstance = MyView(context)
           viewInstance!!
       }
   )
   ```

---

### Q: Do I need to handle lifecycle events for AndroidView?

**A:** Only if the underlying View requires it (MapView, MediaView, etc.):

```kotlin
@Composable
fun LifecycleAwareMap() {
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                // Register for lifecycle events
                lifecycleOwner.lifecycle.addObserver(
                    object : DefaultLifecycleObserver {
                        override fun onCreate(owner: LifecycleOwner) {
                            this@apply.onCreate(Bundle())
                        }
                        override fun onResume(owner: LifecycleOwner) {
                            this@apply.onResume()
                        }
                        override fun onPause(owner: LifecycleOwner) {
                            this@apply.onPause()
                        }
                        override fun onDestroy(owner: LifecycleOwner) {
                            this@apply.onDestroy()
                        }
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
```

**Views that typically need lifecycle management:**
- MapView (Google Maps)
- MediaPlayer / ExoPlayer views
- WebView
- Custom Views with resource management

**Views that don't:**
- Spinner, EditText, Button (standard Views)
- RatingBar, ProgressBar
- AdView (Google Ads handles it)

---

## 4. LISTENERS & EVENT HANDLING

### Q: How do I set up listeners correctly in AndroidView?

**A:** Best practice:

```kotlin
@Composable
fun ViewWithListener(onEvent: (String) -> Unit) {
    // Remember the callback to maintain reference
    val currentCallback by rememberUpdatedState(onEvent)
    
    AndroidView(
        factory = { context ->
            MyView(context).apply {
                // Set listener in factory - it's created once
                setMyListener { event ->
                    // Always call current callback
                    currentCallback.value(event)
                }
            }
        }
    )
}
```

**Why this pattern:**
- Listener created once (efficient)
- Always calls current callback (has latest state)
- No listener recreation on recomposition

---

### Q: My listener callback doesn't have access to current state!

**A:** Use `rememberUpdatedState`:

```kotlin
// ❌ Old state captured
@Composable
fun BadListener(clickCount: Int) {
    AndroidView(
        factory = { context ->
            Button(context).apply {
                setOnClickListener {
                    // clickCount here is always initial value!
                    println(clickCount)
                }
            }
        }
    )
}

// ✅ Always latest state
@Composable
fun GoodListener(clickCount: Int) {
    val currentCount by rememberUpdatedState(clickCount)
    
    AndroidView(
        factory = { context ->
            Button(context).apply {
                setOnClickListener {
                    // currentCount.value is always latest
                    println(currentCount.value)
                }
            }
        }
    )
}
```

---

## 5. SIZE & LAYOUT

### Q: My AndroidView is not visible / doesn't have the right size

**A:** Most common issue: **Not setting explicit size**

```kotlin
// ❌ View might not be visible
AndroidView(factory = { Spinner(it) })

// ✅ Explicit size
AndroidView(
    factory = { Spinner(it) },
    modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
)

// ✅ Or wrap in Box with size
Box(modifier = Modifier.fillMaxWidth().height(50.dp)) {
    AndroidView(
        factory = { Spinner(it) },
        modifier = Modifier.fillMaxSize()  // Fill the Box
    )
}
```

**Why:**
- Compose needs explicit sizing
- Views with wrap_content might not display properly
- Parent layout must have size for child to render

---

### Q: How do I control padding and margins of AndroidView?

**A:** Use standard Modifier functions:

```kotlin
AndroidView(
    factory = { /* ... */ },
    modifier = Modifier
        .padding(16.dp)           // Internal padding
        .fillMaxWidth()           // Width
        .height(100.dp)          // Height
        .clip(RoundedCornerShape(8.dp))  // Border radius
        .border(1.dp, Color.Gray)  // Border
)
```

**Compose Modifiers work with AndroidView:**
- `padding()` - Internal spacing
- `size()`, `width()`, `height()` - Dimensions
- `fillMaxWidth()`, `fillMaxSize()` - Stretch to parent
- `clip()`, `border()` - Visual styling
- `alpha()` - Transparency
- `scale()`, `rotate()` - Transforms

---

## 6. COMPOSE IN VIEWS (ComposeView)

### Q: How do I properly set up ComposeView in a Fragment?

**A:** Complete example:

```kotlin
class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        
        binding.composeView.apply {
            // ✅ Must set strategy BEFORE setContent
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            
            // Now set Compose content
            setContent {
                MaterialTheme {
                    MyComposableScreen()
                }
            }
        }
        
        return binding.root
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Clean up binding
    }
}
```

**XML layout (fragment_my.xml):**
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/compose_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</LinearLayout>
```

---

### Q: How do I add Compose to a View-based Activity?

**A:** Two approaches:

**Approach 1: Full Compose Activity (Recommended for new Activities)**
```kotlin
class MyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MyComposableScreen()
            }
        }
    }
}
```

**Approach 2: Compose in existing View hierarchy**
```kotlin
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        
        // Add traditional View
        layout.addView(
            TextView(this).apply { text = "Traditional View" }
        )
        
        // Add Compose view
        layout.addView(
            ComposeView(this).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
                )
                setContent {
                    Text("Compose View")
                }
            }
        )
        
        setContentView(layout)
    }
}
```

---

## 7. MIGRATION & GRADUAL ADOPTION

### Q: I have a View-based app. How do I migrate to Compose?

**A:** Four-phase approach:

**Phase 1: Add Compose to existing screens**
```kotlin
// Fragment with View-based UI + Compose section
binding.composeViewNewFeature.apply {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent { NewComposeFeature() }
}
```

**Phase 2: Create new screens in Compose**
```kotlin
// New Activity entirely in Compose
class NewFeatureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NewFeatureScreen() }
    }
}
```

**Phase 3: Convert existing screens**
```kotlin
// Gradually convert Views to Composables
// Use AndroidView where Compose equivalents don't exist
@Composable
fun ConvertedScreen() {
    Column {
        // Compose UI for parts you've converted
        Text("New Compose Title")
        
        // Legacy Views still used
        AndroidView(factory = { LegacyCustomView(it) })
    }
}
```

**Phase 4: Complete migration**
- All screens in Compose
- AndroidView only for necessary Views
- Deprecate ComposeView usage

---

### Q: How do I share state during migration?

**A:** Use shared ViewModels:

```kotlin
// Fragment with traditional View
class MyFragment : Fragment() {
    private val viewModel: SharedViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // View observes ViewModel
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
        
        // Compose section also uses same ViewModel
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ComposePart(viewModel)
            }
        }
    }
}

@Composable
fun ComposePart(viewModel: SharedViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    // Use shared state
}
```

---

## 8. TESTING

### Q: How do I test AndroidView in Compose?

**A:** Use `createComposeRule()`:

```kotlin
class MyAndroidViewTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun androidViewDisplaysCorrectly() {
        composeTestRule.setContent {
            MyAndroidViewComponent(
                value = "Test Value",
                onValueChange = {}
            )
        }
        
        // Find the View
        composeTestRule.onRoot().printToLog("MyTest")
        
        // Alternative: Use onNodeWithText if View renders text
        composeTestRule.onNodeWithText("Test Value").assertIsDisplayed()
    }
    
    @Test
    fun androidViewCallsCallback() {
        var callbackValue = ""
        
        composeTestRule.setContent {
            MyAndroidViewComponent(
                value = "Initial",
                onValueChange = { callbackValue = it }
            )
        }
        
        // Simulate View change
        composeTestRule.runOnIdle {
            // Interact with View programmatically
        }
        
        assertEquals("Expected Value", callbackValue)
    }
}
```

---

## 9. COMMON MISTAKES & SOLUTIONS

### Mistake 1: Setting state in factory
```kotlin
// ❌ WRONG
AndroidView(
    factory = { context ->
        Spinner(context).apply {
            setSelection(selectedIndex)  // Only happens once!
        }
    }
)

// ✅ RIGHT
AndroidView(
    factory = { context -> Spinner(context) },
    update = { spinner ->
        if (spinner.selectedItemPosition != selectedIndex) {
            spinner.setSelection(selectedIndex)
        }
    }
)
```

### Mistake 2: Not setting size
```kotlin
// ❌ WRONG - View might not be visible
AndroidView(factory = { Spinner(it) })

// ✅ RIGHT
AndroidView(
    factory = { Spinner(it) },
    modifier = Modifier.fillMaxWidth().height(50.dp)
)
```

### Mistake 3: Wrong ViewCompositionStrategy
```kotlin
// ❌ WRONG for Fragment
binding.composeView.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnDetachedFromWindow
)

// ✅ RIGHT for Fragment
binding.composeView.setViewCompositionStrategy(
    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
)
```

### Mistake 4: Listener doesn't have current state
```kotlin
// ❌ WRONG - clickCount is captured at factory time
var clickCount by remember { mutableIntStateOf(0) }
AndroidView(
    factory = { Button(it).apply {
        setOnClickListener { println(clickCount) }  // Always 0!
    }}
)

// ✅ RIGHT
var clickCount by remember { mutableIntStateOf(0) }
val currentCount by rememberUpdatedState(clickCount)
AndroidView(
    factory = { Button(it).apply {
        setOnClickListener { println(currentCount.value) }  // Always current
    }}
)
```

### Mistake 5: Not cleaning up resources
```kotlin
// ❌ Resource leaks
AndroidView(
    factory = { context ->
        MyExpensiveView(context).apply {
            startListening()  // Never stops!
        }
    }
)

// ✅ Proper cleanup
AndroidView(
    factory = { context ->
        MyExpensiveView(context).apply {
            startListening()
        }
    }
)
DisposableEffect(Unit) {
    onDispose {
        // Cleanup - though View instance might be inaccessible here
    }
}
```

---

## 10. ADVANCED TOPICS

### Q: How do I reuse Views in a Lazy list?

**A:** AndroidView supports view reuse:

```kotlin
@Composable
fun LazyListWithAndroidViews(items: List<Item>) {
    var viewHolder: MyCustomView? = null
    
    LazyColumn {
        items(items) { item ->
            AndroidView(
                factory = { context ->
                    if (viewHolder == null) {
                        viewHolder = MyCustomView(context)
                    }
                    viewHolder!!
                },
                update = { view ->
                    view.bind(item)  // Update view for new item
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}
```

### Q: How do I measure View size from Compose?

**A:** Use `onSizeChanged` modifier:

```kotlin
AndroidView(
    factory = { Spinner(it) },
    modifier = Modifier
        .fillMaxWidth()
        .onSizeChanged { size ->
            println("View size: ${size.width}x${size.height}")
        }
)
```

### Q: How do I theme AndroidView within Compose?

**A:** Pass theme context:

```kotlin
@Composable
fun ThemedAndroidView() {
    val context = LocalContext.current
    val themedContext = ContextThemeWrapper(context, R.style.AppTheme)
    
    AndroidView(
        factory = {
            Spinner(themedContext)  // Uses app theme
        }
    )
}
```

---

## Quick Decision Flow

```
Need to display something in Compose?
│
├─ Is it a traditional View (Spinner, RatingBar, etc.)?
│  ├─ No Compose equivalent AND no custom implementation?
│  │  └─ Use AndroidView(factory = { ... })
│  └─ Compose has equivalent (Button, Text, etc.)?
│     └─ Use Compose composable
│
└─ Need to add Compose to existing View-based UI?
   └─ Use ComposeView + setContent() + appropriate ViewCompositionStrategy
```

---

*Study these FAQs thoroughly, as they cover the most common questions asked in interviews and the issues encountered in production.*