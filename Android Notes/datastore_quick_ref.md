# DataStore Quick Reference & Visual Guides
## One-Page Cheat Sheet & Architecture Diagrams

---

## QUICK REFERENCE GUIDE

### 1. DEPENDENCY SETUP
```gradle
implementation "androidx.datastore:datastore-preferences:1.0.0"
```

### 2. CREATE DATASTORE INSTANCE
```kotlin
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_settings"
)
```

### 3. DEFINE KEYS (All Types)
```kotlin
object MyKeys {
    val STRING_KEY = stringPreferencesKey("key_name")
    val INT_KEY = intPreferencesKey("key_name")
    val BOOLEAN_KEY = booleanPreferencesKey("key_name")
    val FLOAT_KEY = floatPreferencesKey("key_name")
    val DOUBLE_KEY = doublePreferencesKey("key_name")
    val LONG_KEY = longPreferencesKey("key_name")
    val STRING_SET_KEY = stringSetPreferencesKey("key_name")
}
```

### 4. READ SINGLE VALUE (Returns Flow)
```kotlin
fun getValueFlow(): Flow<String> = dataStore.data
    .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
    .map { preferences -> preferences[KEY] ?: "default" }
```

### 5. READ WITH first() (Single Value)
```kotlin
val currentValue = dataStore.data
    .map { preferences -> preferences[KEY] ?: "default" }
    .first()  // Get first emission, cancel flow
```

### 6. WRITE VALUE (Transactional)
```kotlin
dataStore.edit { preferences ->
    preferences[KEY] = "new value"
}
```

### 7. DELETE VALUE
```kotlin
dataStore.edit { preferences ->
    preferences.remove(KEY)
}
```

### 8. IN VIEWMODEL (Recommended)
```kotlin
val value: StateFlow<String> = repository.getValueFlow()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = "Loading..."
    )
```

### 9. COLLECT IN UI
```kotlin
lifecycleScope.launch {
    viewModel.value.collect { newValue ->
        // Update UI with newValue
    }
}
```

### 10. ERROR HANDLING
```kotlin
try {
    dataStore.edit { preferences ->
        preferences[KEY] = value
    }
} catch (ioException: IOException) {
    Log.e("DataStore", "Error writing", ioException)
}
```

---

## ARCHITECTURE DIAGRAM

```
┌─────────────────────────────────────────────────────────────┐
│                   ANDROID APPLICATION                       │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────────────────────────────────────────┐   │
│  │               UI LAYER (Activities/Fragments)        │   │
│  │  - Observes StateFlow                                │   │
│  │  - Calls ViewModel methods                           │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │ Observes                             │
│  ┌────────────────────▼─────────────────────────────────┐   │
│  │            VIEWMODEL LAYER                           │   │
│  │  - Exposes StateFlow<Data>                           │   │
│  │  - Calls Repository methods in viewModelScope        │   │
│  │  - Handles business logic                            │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │ Calls suspend functions              │
│  ┌────────────────────▼─────────────────────────────────┐   │
│  │          REPOSITORY LAYER                            │   │
│  │  - Exposes Flow<Data>                                │   │
│  │  - Read operations return Flow                        │   │
│  │  - Write operations are suspend functions            │   │
│  │  - Error handling with catch/try-catch               │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │ Reads/Writes                         │
│  ┌────────────────────▼─────────────────────────────────┐   │
│  │      PREFERENCES DATASTORE (Singleton)               │   │
│  │  - dataStore.data: Flow<Preferences>                 │   │
│  │  - edit(): Update data transactionally               │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │ I/O Operations (IO Dispatcher)       │
│  ┌────────────────────▼─────────────────────────────────┐   │
│  │     XML FILE IN APP INTERNAL STORAGE                 │   │
│  │  Path: /data/data/package/files/datastore/*.pb       │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

---

## DATA FLOW DIAGRAM (Read Operation)

```
                    UI Layer
                       ▲
                       │ Collects from StateFlow
                       │
                   ┌───┴────────────────────┐
                   │                        │
          ViewModel StateFlow          Activity/Fragment
                   │
                   │ Created from Flow
                   │
          ┌────────▼──────────────────┐
          │  Repository.valueFlow()   │
          │  Returns: Flow<String>    │
          └────────┬──────────────────┘
                   │
          ┌────────▼──────────────────────────┐
          │  dataStore.data.map { prefs ->   │
          │    prefs[KEY] ?: "default"        │
          │  }                                 │
          │  Returns: Flow<String>             │
          └────────┬──────────────────────────┘
                   │
          ┌────────▼──────────────────┐
          │  I/O Dispatcher            │
          │  (Automatic)               │
          └────────┬──────────────────┘
                   │
          ┌────────▼──────────────────┐
          │  Read from Disk            │
          │  (XML File)                │
          └────────────────────────────┘
```

---

## DATA FLOW DIAGRAM (Write Operation)

```
                    UI Layer
                       │
                       │ Calls viewModel.updateValue()
                       │
          ┌────────────▼─────────────┐
          │   ViewModel Method        │
          │  Fun updateValue(v) {     │
          │    viewModelScope.launch {}│
          │  }                        │
          └────────────┬──────────────┘
                       │
          ┌────────────▼──────────────────┐
          │  Repository.saveValue()       │
          │  suspend fun                  │
          └────────────┬──────────────────┘
                       │
          ┌────────────▼──────────────────┐
          │  dataStore.edit { prefs ->   │
          │    prefs[KEY] = value         │
          │  }                            │
          │  Transactional Update         │
          └────────────┬──────────────────┘
                       │
          ┌────────────▼──────────────────┐
          │  I/O Dispatcher               │
          │  (Automatic)                  │
          └────────────┬──────────────────┘
                       │
          ┌────────────▼──────────────────┐
          │  Write to Disk                │
          │  (XML File - Atomic)          │
          └────────────┬──────────────────┘
                       │
          ┌────────────▼──────────────────┐
          │  Emit new value to Flow       │
          │  (Triggers UI update)         │
          └───────────────────────────────┘
```

---

## KEY TYPES REFERENCE TABLE

| Key Type | Kotlin Type | Function | Example |
|----------|------------|----------|---------|
| String | String | `stringPreferencesKey()` | `stringPreferencesKey("name")` |
| Integer | Int | `intPreferencesKey()` | `intPreferencesKey("age")` |
| Boolean | Boolean | `booleanPreferencesKey()` | `booleanPreferencesKey("enabled")` |
| Float | Float | `floatPreferencesKey()` | `floatPreferencesKey("rating")` |
| Double | Double | `doublePreferencesKey()` | `doublePreferencesKey("price")` |
| Long | Long | `longPreferencesKey()` | `longPreferencesKey("timestamp")` |
| String Set | Set<String> | `stringSetPreferencesKey()` | `stringSetPreferencesKey("tags")` |

---

## OPERATION COMPARISON

### READ vs WRITE

```
┌────────────────────────────────────────────────────────────┐
│                        READ OPERATION                       │
├────────────────────────────────────────────────────────────┤
│ Type:        Non-blocking, asynchronous                   │
│ Returns:     Flow<T>                                        │
│ Usage:       .map(), .collect(), .first()                 │
│ Threading:   Automatic IO dispatcher                       │
│ Error Type:  IOException (via catch operator)             │
│ Thread-safe: Yes                                           │
│ Blocks UI:   No                                            │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│                       WRITE OPERATION                       │
├────────────────────────────────────────────────────────────┤
│ Type:        Suspend function (asynchronous)              │
│ Returns:     Unit                                          │
│ Usage:       dataStore.edit { ... }                        │
│ Threading:   Automatic IO dispatcher                       │
│ Error Type:  IOException (catch with try-catch)          │
│ Thread-safe: Yes (Serialized writes)                       │
│ Blocks UI:   No (uses coroutines)                         │
│ Transaction: Yes (Atomic read-modify-write)               │
└────────────────────────────────────────────────────────────┘
```

---

## ERROR HANDLING FLOWCHART

```
                    ┌─────────────────┐
                    │  Start Operation │
                    └────────┬──────────┘
                             │
                    ┌────────▼──────────┐
                    │  Is it a READ?    │
                    └────┬───────────┬──┘
                    YES  │           │  NO (WRITE)
            ┌────────────┘           └──────────────┐
            │                                        │
    ┌───────▼────────────┐             ┌────────────▼────────┐
    │  Use .catch()      │             │  Use try-catch      │
    │  operator on Flow  │             │  block              │
    └───────┬────────────┘             └────────┬────────────┘
            │                                   │
    ┌───────▼──────────────┐          ┌────────▼──────────────┐
    │ Is exception         │          │ Is exception         │
    │ IOException?         │          │ IOException?         │
    └───┬────────────────┬─┘          └────┬──────────────┬──┘
    YES │                │ NO         YES  │              │ NO
        │                │                 │              │
    ┌───▼───┐      ┌─────▼──┐      ┌──────▼──┐       ┌───▼──┐
    │ Emit  │      │ Re-throw    │Log error │      │ Throw │
    │Default│      │ exception   │ & handle │      │ again │
    │Value  │      │            │         │       │       │
    └───────┘      └────────────┘      └──────────┘       └───┘
```

---

## COMMON OPERATIONS SNIPPETS

### Read String Value
```kotlin
stringPreferencesKey("user_name").let { key ->
    dataStore.data.map { it[key] ?: "Guest" }
}
```

### Write String Value
```kotlin
stringPreferencesKey("user_name").let { key ->
    dataStore.edit { it[key] = "John" }
}
```

### Update Integer with Condition
```kotlin
intPreferencesKey("counter").let { key ->
    dataStore.edit { prefs ->
        val current = prefs[key] ?: 0
        if (current < 100) prefs[key] = current + 1
    }
}
```

### Read Multiple Keys
```kotlin
dataStore.data.map { prefs ->
    Triple(
        prefs[stringPreferencesKey("name")] ?: "",
        prefs[intPreferencesKey("age")] ?: 0,
        prefs[booleanPreferencesKey("enabled")] ?: false
    )
}
```

### Clear All Data
```kotlin
dataStore.edit { it.clear() }
```

### Delete Single Key
```kotlin
stringPreferencesKey("temp_key").let { key ->
    dataStore.edit { it.remove(key) }
}
```

---

## LIFECYCLE-AWARE COLLECTION IN ACTIVITY

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: MyViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Lifecycle-aware, automatic cleanup
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collect { name ->
                    nameTextView.text = name
                }
            }
        }
    }
}
```

---

## COMMON MISTAKES & HOW TO AVOID

| Mistake | Wrong | Correct |
|---------|-------|---------|
| Creating multiple instances | `val store1 = createStore(); val store2 = createStore()` | `val Context.dataStore by preferencesDataStore(...)` |
| Synchronous read on UI thread | `val v = runBlocking { dataStore.data.first() }` | `lifecycleScope.launch { dataStore.data.collect {...} }` |
| Not handling IOException | `dataStore.data.map { ... }` | `dataStore.data.catch {...}.map {...}` |
| Blocking on IO | Creating coroutines without withContext | Using viewModelScope which handles dispatchers |
| Not using repository | Reading directly from Activity | Create Repository exposing Flow |
| Missing error handling for writes | Not catching exceptions | Using try-catch with IOException |
| Reading in main thread | `Thread { runBlocking { ... } }` | Always use coroutines with viewModelScope |

---

## PERFORMANCE TIPS

1. **Cache in Memory**: Use StateFlow to avoid re-reading
2. **Batch Updates**: Combine multiple edits in single transaction
3. **Use distinctUntilChanged()**: Reduce unnecessary emissions
4. **Lazy Initialize**: Create DataStore when first needed
5. **Preload Data**: Load on app startup to avoid UI delays

```kotlin
// Pre-load in Application.onCreate()
override fun onCreate() {
    super.onCreate()
    lifecycleScope.launch {
        dataStore.data.first()  // Pre-load data
    }
}
```

---

## TESTING PATTERNS

```kotlin
@Test
fun testReadValue() = runTest {
    val testDataStore = DataStoreTestUtils.createTestDataStore()
    val repository = MyRepository(testDataStore)
    
    // Initial value
    val initial = repository.valueFlow().first()
    assertEquals("default", initial)
}

@Test
fun testWriteValue() = runTest {
    val testDataStore = DataStoreTestUtils.createTestDataStore()
    val repository = MyRepository(testDataStore)
    
    // Write value
    repository.saveValue("test")
    
    // Verify
    val saved = repository.valueFlow().first()
    assertEquals("test", saved)
}
```

---

## DECISION TREE

```
Need to store data in Android?
│
├─ Simple key-value pairs? → Preferences DataStore ✓
├─ Complex typed objects? → Proto DataStore
├─ Relational data? → Room Database
├─ Large files? → File-based storage
├─ Temporary data? → Memory cache
└─ Encrypted sensitive data? → EncryptedSharedPreferences

Preferences DataStore chosen?
│
├─ Define keys in object
├─ Create singleton instance
├─ Implement Repository pattern
├─ Use ViewModel for state management
├─ Collect with lifecycle awareness
├─ Handle IOException properly
└─ Test with test DataStore ✓
```

---

## SUMMARY TABLE

| Component | Purpose | Returns | Async |
|-----------|---------|---------|-------|
| Key | Identifier for value | Preferences.Key<T> | N/A |
| dataStore.data | Read current values | Flow<Preferences> | Yes |
| dataStore.edit() | Modify values | Unit (suspend) | Yes |
| .map() | Transform Flow values | Flow<T> | Yes |
| .catch() | Handle Flow errors | Flow<T> | Yes |
| .first() | Get single value | T | Suspend |
| .collect() | Observe Flow values | Unit | Yes |
| stateIn() | Convert to StateFlow | StateFlow<T> | Yes |
| collectAsState() | Compose observation | State<T> | - |

---

**Quick Lookup Guide Version**: 1.0
**Ideal For**: Quick reference during coding
**Print Format**: A3 or A2 recommended for wall reference

