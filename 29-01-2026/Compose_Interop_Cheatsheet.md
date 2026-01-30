# Views in Compose - Quick Reference & Revision Notes

## Core Concepts at a Glance

### What is View Interop?
Seamlessly integrate traditional Android Views into Jetpack Compose applications and vice versa. Necessary because not all UI components have Compose equivalents (AdView, RatingBar, custom Views, etc.).

---

## The AndroidView Composable

### Signature
```kotlin
@Composable
fun <T : View> AndroidView(
    factory: (Context) -> T,
    modifier: Modifier = Modifier,
    update: (T) -> Unit = NoOpUpdate
)
```

### Parameters Comparison

| Parameter | Called | Purpose | Key Points |
|-----------|--------|---------|------------|
| **factory** | Once at creation | Create & initialize View | Static setup, no state |
| **update** | Every recomposition | Update View with Compose state | Called repeatedly |
| **modifier** | Standard | Size, padding, alignment | Works like any Composable |

### The Golden Rule
```
❌ WRONG: Set state in factory
factory = { MyView(it).apply { value = state } }  // Only happens once!

✅ RIGHT: Update state in update block
update = { view -> view.value = state }  // Happens on every recomposition
```

---

## Factory vs. Update Pattern (CRITICAL)

### The Problem
```kotlin
@Composable
fun BadExample() {
    var rating by remember { mutableFloatStateOf(3f) }
    
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                this.rating = rating  // ❌ Only runs ONCE
            }
        }
    )
    // When rating changes in Compose, the View doesn't update!
}
```

### The Solution
```kotlin
@Composable
fun GoodExample() {
    var rating by remember { mutableFloatStateOf(3f) }
    
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                // ✅ Just create it
            }
        },
        update = { ratingBar ->
            // ✅ Update runs every recomposition
            if (ratingBar.rating != rating) {
                ratingBar.rating = rating
            }
        }
    )
}
```

---

## Two-Way Communication Pattern

### View → Compose (User interaction)
```kotlin
setOnClickListener { 
    composeState = newValue  // Send data to Compose
}
```

### Compose → View (State updates)
```kotlin
update = { view ->
    view.property = composeState  // Send state to View
}
```

### Complete Example
```kotlin
@Composable
fun Spinner(selected: String, onSelect: (String) -> Unit) {
    val items = listOf("A", "B", "C")
    
    AndroidView(
        factory = { context ->
            Spinner(context).apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, items)
                // View → Compose
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        onSelect(items[position])
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        },
        update = { spinner ->
            // Compose → View
            val index = items.indexOf(selected)
            if (spinner.selectedItemPosition != index && index >= 0) {
                spinner.setSelection(index, false)
            }
        }
    )
}
```

---

## Common APIs Overview

| API | Use Case | Example |
|-----|----------|---------|
| **AndroidView** | Wrap traditional View | `AndroidView(factory = { Spinner(it) })` |
| **ComposeView** | Add Compose to View-based layout | `setContent { MyComposable() }` |
| **AndroidViewBinding** | Use XML layout with binding | `AndroidViewBinding(MyLayoutBinding::inflate)` |
| **AndroidFragment** | Add Fragment to Compose | `AndroidFragment()` |

---

## ViewCompositionStrategy (For ComposeView)

### When to Use Which Strategy?

| Strategy | Used For | When to Choose |
|----------|----------|---|
| **DisposeOnDetachedFromWindowOrReleasedFromPool** | RecyclerView, standalone | Default. View can be reused. |
| **DisposeOnLifecycleDestroyed** | Activity/Fragment with known Lifecycle | When you have explicit Lifecycle reference |
| **DisposeOnViewTreeLifecycleDestroyed** | Fragment (recommended) | Most common. Auto-discovers Lifecycle from window. |

### Why DisposeOnViewTreeLifecycleDestroyed for Fragments?
Fragments have TWO lifecycles:
- Fragment's lifecycle
- Fragment's View's lifecycle

The View can be destroyed before the Fragment (e.g., navigation). Using ViewTree strategy ensures cleanup when the View is destroyed.

```kotlin
composeView.apply {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent { MyComposable() }
}
```

---

## Handling State in Callbacks

### Problem: Stale State in Listeners
```kotlin
var counter by remember { mutableIntStateOf(0) }

AndroidView(
    factory = { context ->
        Button(context).apply {
            setOnClickListener {
                // ❌ counter here is captured at factory time!
                println(counter)  // Always prints 0
            }
        }
    }
)
```

### Solution: rememberUpdatedState
```kotlin
var counter by remember { mutableIntStateOf(0) }
val currentCounter by rememberUpdatedState(counter)

AndroidView(
    factory = { context ->
        Button(context).apply {
            setOnClickListener {
                // ✅ Always has latest value
                println(currentCounter.value)  // Always current
            }
        }
    }
)
```

---

## Real-World Examples Quick Reference

### RatingBar
```kotlin
@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    AndroidView(
        factory = { context ->
            RatingBar(context).apply {
                numStars = 5
                setOnRatingBarChangeListener { _, r, fromUser ->
                    if (fromUser) onRatingChanged(r)
                }
            }
        },
        update = { view ->
            if (view.rating != rating) view.rating = rating
        }
    )
}
```

### Spinner
```kotlin
@Composable
fun ColorSpinner(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("Red", "Green", "Blue")
    
    AndroidView(
        factory = { context ->
            Spinner(context).apply {
                adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                        onSelect(options[pos])
                    }
                    override fun onNothingSelected(p: AdapterView<*>?) {}
                }
            }
        },
        update = { spinner ->
            val idx = options.indexOf(selected)
            if (spinner.selectedItemPosition != idx && idx >= 0) {
                spinner.setSelection(idx, false)
            }
        }
    )
}
```

### AdView (Google Ads)
```kotlin
@Composable
fun GoogleAdBanner() {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-3940256099942544/6300978111"  // Test ID
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = Modifier.fillMaxWidth().height(50.dp)
    )
}
```

---

## Debugging Checklist

**View not showing?**
- [ ] Did you set explicit size? (modifier = Modifier.width().height())
- [ ] Is the View created in factory?
- [ ] Is parent layout visible?

**State not updating?**
- [ ] Are you setting state in factory (❌) instead of update (✅)?
- [ ] Did you add the update lambda?
- [ ] Are you checking for changes before updating?

**Listener not working?**
- [ ] Is the listener set in factory?
- [ ] Do you need rememberUpdatedState for current state access?
- [ ] Is the callback function signature correct?

**Lifecycle issues?**
- [ ] Did you choose correct ViewCompositionStrategy?
- [ ] Does the View need explicit lifecycle management (MapView)?
- [ ] Test rotation and navigation!

---

## Key Takeaways for Exam/Interview

1. **View Interop = Bridge between Views and Compose**
   - Use when Compose doesn't have equivalent (AdView, RatingBar)
   - Use for gradual migration strategies

2. **AndroidView has three parameters**
   - factory: Create once
   - update: Run every recomposition
   - modifier: Size and position

3. **Factory is called ONCE**
   - Don't set state-dependent properties there
   - Use update block for all state updates
   - CRITICAL: This is the #1 mistake developers make

4. **Two-way communication**
   - View → Compose: Via callbacks in factory (onClickListener, etc.)
   - Compose → View: Via update block

5. **ViewCompositionStrategy matters**
   - Use DisposeOnViewTreeLifecycleDestroyed for Fragments
   - Use DisposeOnDetachedFromWindowOrReleasedFromPool for RecyclerView
   - Prevents memory leaks and resource issues

6. **State management requires rememberUpdatedState**
   - When callbacks need access to current state
   - Without it, they capture old values

---

## Code Template for Copy-Paste

```kotlin
@Composable
fun MyAndroidViewWrapper(
    state: String,
    onStateChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            // ✅ Create and setup - called ONCE
            MyView(context).apply {
                setListener { newValue ->
                    // View → Compose
                    onStateChange(newValue)
                }
            }
        },
        update = { view ->
            // ✅ Update based on state - called EVERY RECOMPOSITION
            if (view.state != state) {
                view.state = state  // Compose → View
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(16.dp)
    )
}
```

---

## Study Tips

**To master View Interop:**

1. **Build the Juice Tracker app** following the codelab
   - Implement RatingBar (AndroidView wrapper)
   - Implement Color Spinner (state management)
   - Integrate AdView (no update needed)

2. **Practice the factory/update pattern**
   - Write 5 different AndroidView implementations
   - Make common mistakes intentionally, then fix them
   - Understand WHY factory is called once

3. **Test lifecycle behavior**
   - Rotate device with View Interop
   - Navigate between screens
   - Watch for memory leaks

4. **Create your own wrapper composables**
   - MapView
   - Custom Analytics View
   - Barcode Scanner
   
5. **Review state management**
   - rememberUpdatedState
   - DisposableEffect
   - Effect timing

---

**Last Updated:** January 2026
**Android Version:** Compose 1.5+
**Target Audience:** Android Developers in mid-stage Compose adoption