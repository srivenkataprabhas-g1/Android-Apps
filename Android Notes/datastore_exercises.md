# DataStore Practice Exercises & Solutions
## Hands-On Learning with Real-World Scenarios

---

## BEGINNER EXERCISES

### Exercise 1: Basic Setup and Hello World
**Objective**: Set up DataStore and save/read your first value

**Task**:
1. Create a new Android project
2. Add DataStore dependency
3. Create a DataStore instance with name "my_store"
4. Define a String key for storing a greeting message
5. Write code to save "Hello DataStore!" to the key
6. Read and log the value

**Solution**:
```kotlin
// Step 1: Add to build.gradle
dependencies {
    implementation "androidx.datastore:datastore-preferences:1.0.0"
}

// Step 2: Create DataStore instance (in separate file or Activity)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "my_store"
)

// Step 3: Define key
val GREETING_KEY = stringPreferencesKey("greeting")

// Step 4-5: Save value
dataStore.edit { preferences ->
    preferences[GREETING_KEY] = "Hello DataStore!"
}

// Step 6: Read and log
lifecycleScope.launch {
    dataStore.data
        .map { preferences -> preferences[GREETING_KEY] ?: "No greeting" }
        .collect { greeting ->
            Log.d("DataStore", "Greeting: $greeting")
        }
}
```

---

### Exercise 2: Multiple Data Types
**Objective**: Work with different key types

**Task**: Create keys and operations for:
- User's name (String)
- User's age (Int)
- Premium membership status (Boolean)
- Account balance (Double)

Save and retrieve all values, then log them in a formatted string.

**Solution**:
```kotlin
// Define keys
object UserKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_AGE = intPreferencesKey("user_age")
    val IS_PREMIUM = booleanPreferencesKey("is_premium")
    val ACCOUNT_BALANCE = doublePreferencesKey("balance")
}

// Create repository
class UserRepository(private val dataStore: DataStore<Preferences>) {
    
    // Read operations
    fun getUserDataFlow(): Flow<UserData> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { preferences ->
            UserData(
                name = preferences[UserKeys.USER_NAME] ?: "Unknown",
                age = preferences[UserKeys.USER_AGE] ?: 0,
                isPremium = preferences[UserKeys.IS_PREMIUM] ?: false,
                balance = preferences[UserKeys.ACCOUNT_BALANCE] ?: 0.0
            )
        }
    
    // Write all at once
    suspend fun saveUserData(
        name: String,
        age: Int,
        isPremium: Boolean,
        balance: Double
    ) {
        dataStore.edit { preferences ->
            preferences[UserKeys.USER_NAME] = name
            preferences[UserKeys.USER_AGE] = age
            preferences[UserKeys.IS_PREMIUM] = isPremium
            preferences[UserKeys.ACCOUNT_BALANCE] = balance
        }
    }
}

data class UserData(
    val name: String,
    val age: Int,
    val isPremium: Boolean,
    val balance: Double
) {
    override fun toString(): String =
        "User: $name, Age: $age, Premium: $isPremium, Balance: $$balance"
}

// Usage
lifecycleScope.launch {
    repository.saveUserData("John Doe", 28, true, 1500.50)
    
    repository.getUserDataFlow().collect { userData ->
        Log.d("UserData", userData.toString())
        // Output: User: John Doe, Age: 28, Premium: true, Balance: $1500.5
    }
}
```

---

### Exercise 3: Simple Theme Toggle
**Objective**: Implement dark mode preference that persists

**Task**:
1. Create a Boolean key for dark mode
2. Create a repository with methods to:
   - Get dark mode status as Flow
   - Toggle dark mode (save opposite value)
3. Use it in an Activity with a Switch

**Solution**:
```kotlin
// Define key
val IS_DARK_MODE = booleanPreferencesKey("dark_mode")

// Repository
class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    
    fun isDarkModeFlow(): Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[IS_DARK_MODE] ?: false }
    
    suspend fun toggleDarkMode(currentValue: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = !currentValue
        }
    }
}

// In Activity
class SettingsActivity : AppCompatActivity() {
    private val repository by lazy {
        ThemeRepository(dataStore)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        val darkModeSwitch = findViewById<Switch>(R.id.dark_mode_switch)
        
        // Observe and update switch
        lifecycleScope.launch {
            repository.isDarkModeFlow().collect { isDark ->
                darkModeSwitch.isChecked = isDark
            }
        }
        
        // Listen to switch changes
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                repository.toggleDarkMode(!isChecked)  // Toggle will do opposite
            }
        }
    }
}
```

---

## INTERMEDIATE EXERCISES

### Exercise 4: Error Handling
**Objective**: Implement proper error handling

**Task**:
1. Create a method that reads a value with error handling
2. Create a method that writes a value with error handling
3. Log different error types appropriately

**Solution**:
```kotlin
class RobustRepository(private val dataStore: DataStore<Preferences>) {
    
    // Read with proper error handling
    fun getSafeValueFlow(): Flow<String> = dataStore.data
        .catch { exception ->
            Log.e("DataStore", "Error reading data", exception)
            when (exception) {
                is IOException -> {
                    Log.w("DataStore", "Disk I/O error, using default value")
                    emit(emptyPreferences())
                }
                else -> {
                    Log.e("DataStore", "Unexpected error: $exception")
                    throw exception
                }
            }
        }
        .map { preferences ->
            preferences[stringPreferencesKey("safe_key")] ?: "default"
        }
    
    // Write with proper error handling
    suspend fun saveSafeValue(value: String): Result<Unit> = 
        try {
            dataStore.edit { preferences ->
                preferences[stringPreferencesKey("safe_key")] = value
            }
            Result.success(Unit)
        } catch (ioException: IOException) {
            Log.e("DataStore", "Failed to save value: $ioException")
            Result.failure(ioException)
        } catch (exception: Exception) {
            Log.e("DataStore", "Unexpected error while saving: $exception")
            Result.failure(exception)
        }
}

// Usage with Result handling
lifecycleScope.launch {
    val result = repository.saveSafeValue("important data")
    result.onSuccess {
        Log.d("DataStore", "Save successful")
    }
    result.onFailure { error ->
        Log.e("DataStore", "Save failed: ${error.message}")
        showErrorDialog("Failed to save data")
    }
}
```

---

### Exercise 5: ViewModel Integration
**Objective**: Properly integrate DataStore with ViewModel and MutableState

**Task**:
1. Create a ViewModel that exposes user data as StateFlow
2. Create methods to update individual fields
3. Handle loading state
4. Use in Fragment with proper lifecycle awareness

**Solution**:
```kotlin
// ViewModel
class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    
    // Expose user data as StateFlow
    val userProfile: StateFlow<UserProfile?> = repository.getUserFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    
    // Mutable state for UI feedback
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    fun updateName(newName: String) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                repository.saveUserName(newName)
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = "Failed to save name: ${e.message}"
                Log.e("ProfileViewModel", "Error updating name", e)
            } finally {
                _loadingState.value = false
            }
        }
    }
    
    fun updateAge(newAge: Int) {
        viewModelScope.launch {
            try {
                _loadingState.value = true
                repository.saveUserAge(newAge)
                _errorMessage.value = null
            } catch (e: IOException) {
                _errorMessage.value = "Failed to save age: ${e.message}"
            } finally {
                _loadingState.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

data class UserProfile(
    val name: String,
    val age: Int,
    val email: String
)

// In Fragment
class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Observe user data
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userProfile.collect { profile ->
                    if (profile != null) {
                        nameEditText.setText(profile.name)
                        ageEditText.setText(profile.age.toString())
                        emailEditText.setText(profile.email)
                    }
                }
            }
        }
        
        // Observe loading state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadingState.collect { isLoading ->
                    saveButton.isEnabled = !isLoading
                    progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
        
        // Observe errors
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    if (message != null) {
                        showErrorSnackbar(message)
                    }
                }
            }
        }
        
        // Save button
        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString()
            val newAge = ageEditText.text.toString().toIntOrNull() ?: return@setOnClickListener
            viewModel.updateName(newName)
            viewModel.updateAge(newAge)
        }
    }
}
```

---

### Exercise 6: Data Migration
**Objective**: Migrate from SharedPreferences to DataStore

**Task**:
1. Create sample SharedPreferences data
2. Set up DataStore with migration
3. Verify data is migrated correctly

**Solution**:
```kotlin
// Step 1: Setup old SharedPreferences
fun setupOldSharedPreferences(context: Context) {
    val sharedPref = context.getSharedPreferences("old_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().apply {
        putString("user_name", "John Doe")
        putInt("user_age", 28)
        putBoolean("is_premium", true)
        apply()
    }
}

// Step 2: Define matching keys with same names
object MigrationKeys {
    val USER_NAME = stringPreferencesKey("user_name")  // Same name!
    val USER_AGE = intPreferencesKey("user_age")        // Same name!
    val IS_PREMIUM = booleanPreferencesKey("is_premium") // Same name!
}

// Step 3: Create DataStore with migration
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "new_datastore",
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context = context,
                sharedPreferencesName = "old_prefs"
            )
        )
    }
)

// Step 4: Verify migration
class MigrationRepository(private val dataStore: DataStore<Preferences>) {
    
    fun getMigratedDataFlow(): Flow<MigratedData> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            MigratedData(
                name = preferences[MigrationKeys.USER_NAME] ?: "Not found",
                age = preferences[MigrationKeys.USER_AGE] ?: 0,
                isPremium = preferences[MigrationKeys.IS_PREMIUM] ?: false
            )
        }
}

data class MigratedData(val name: String, val age: Int, val isPremium: Boolean)

// Usage
// First call setupOldSharedPreferences(context)
// Then access:
lifecycleScope.launch {
    repository.getMigratedDataFlow().collect { data ->
        Log.d("Migration", data.toString())
        // Output: MigratedData(name=John Doe, age=28, isPremium=true)
        // ✓ Data successfully migrated!
    }
}
```

---

## ADVANCED EXERCISES

### Exercise 7: Complex State Management
**Objective**: Manage a shopping cart preference store

**Task**:
Create a system to store:
- Cart items (as JSON string)
- Cart total price
- Last updated timestamp
- Checkout status

**Solution**:
```kotlin
// Keys
object CartKeys {
    val CART_ITEMS = stringPreferencesKey("cart_items")  // JSON
    val CART_TOTAL = doublePreferencesKey("cart_total")
    val LAST_UPDATED = longPreferencesKey("last_updated")
    val IS_CHECKED_OUT = booleanPreferencesKey("is_checked_out")
}

// Data classes
data class CartItem(val id: String, val name: String, val price: Double, val quantity: Int)

data class CartState(
    val items: List<CartItem>,
    val totalPrice: Double,
    val lastUpdated: Long,
    val isCheckedOut: Boolean
)

// Repository
class CartRepository(private val dataStore: DataStore<Preferences>) {
    
    fun getCartFlow(): Flow<CartState> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val itemsJson = preferences[CartKeys.CART_ITEMS] ?: "[]"
            val items = parseCartItems(itemsJson)  // Use Gson or Kotlinx Serialization
            
            CartState(
                items = items,
                totalPrice = preferences[CartKeys.CART_TOTAL] ?: 0.0,
                lastUpdated = preferences[CartKeys.LAST_UPDATED] ?: 0L,
                isCheckedOut = preferences[CartKeys.IS_CHECKED_OUT] ?: false
            )
        }
    
    suspend fun addToCart(item: CartItem) {
        dataStore.edit { preferences ->
            val currentJson = preferences[CartKeys.CART_ITEMS] ?: "[]"
            val items = parseCartItems(currentJson).toMutableList()
            items.add(item)
            
            val newTotal = items.sumOf { it.price * it.quantity }
            
            preferences[CartKeys.CART_ITEMS] = serializeCartItems(items)
            preferences[CartKeys.CART_TOTAL] = newTotal
            preferences[CartKeys.LAST_UPDATED] = System.currentTimeMillis()
        }
    }
    
    suspend fun updateQuantity(itemId: String, newQuantity: Int) {
        dataStore.edit { preferences ->
            val currentJson = preferences[CartKeys.CART_ITEMS] ?: "[]"
            val items = parseCartItems(currentJson).map { item ->
                if (item.id == itemId) item.copy(quantity = newQuantity) else item
            }
            
            val newTotal = items.sumOf { it.price * it.quantity }
            
            preferences[CartKeys.CART_ITEMS] = serializeCartItems(items)
            preferences[CartKeys.CART_TOTAL] = newTotal
            preferences[CartKeys.LAST_UPDATED] = System.currentTimeMillis()
        }
    }
    
    suspend fun checkout(): Boolean = try {
        dataStore.edit { preferences ->
            preferences[CartKeys.IS_CHECKED_OUT] = true
            preferences[CartKeys.LAST_UPDATED] = System.currentTimeMillis()
        }
        true
    } catch (e: IOException) {
        false
    }
    
    suspend fun clearCart() {
        dataStore.edit { preferences ->
            preferences[CartKeys.CART_ITEMS] = "[]"
            preferences[CartKeys.CART_TOTAL] = 0.0
            preferences[CartKeys.IS_CHECKED_OUT] = false
            preferences[CartKeys.LAST_UPDATED] = System.currentTimeMillis()
        }
    }
    
    // Helper functions (implement with your serialization library)
    private fun parseCartItems(json: String): List<CartItem> {
        // Implement using Gson: Gson().fromJson(json, object : TypeToken<List<CartItem>>() {}.type)
        return emptyList()
    }
    
    private fun serializeCartItems(items: List<CartItem>): String {
        // Implement using Gson: Gson().toJson(items)
        return "[]"
    }
}

// ViewModel
class CartViewModel(private val repository: CartRepository) : ViewModel() {
    
    val cartState: StateFlow<CartState> = repository.getCartFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CartState(emptyList(), 0.0, 0L, false)
        )
    
    fun addItem(item: CartItem) {
        viewModelScope.launch {
            repository.addToCart(item)
        }
    }
    
    fun checkout() {
        viewModelScope.launch {
            if (repository.checkout()) {
                Log.d("Cart", "Checkout successful!")
            }
        }
    }
}
```

---

## PRACTICE CHALLENGES

### Challenge 1: User Preferences Score
Create a system that tracks user activity:
- Stores current score (Int)
- Stores high score (Int)
- Stores total games played (Int)
- Automatically updates high score if current score is higher
- Increment games played on save

### Challenge 2: Settings Sync
Implement settings backup:
- Save user preferences with timestamp
- Allow restoring previous state
- Show history of last 5 saves
- Enable rollback to previous state

### Challenge 3: Encrypted Storage
Extend the system to:
- Encrypt sensitive data before saving
- Decrypt on read
- Handle encryption/decryption errors
- Support migration from unencrypted to encrypted

---

## SELF-ASSESSMENT CHECKLIST

After completing all exercises, verify you understand:

- [ ] DataStore setup and configuration
- [ ] Creating and managing keys of all types
- [ ] Reading single and multiple values
- [ ] Writing atomic transactions
- [ ] Error handling with IOException
- [ ] Using Flow, StateFlow, and collect
- [ ] ViewModel integration
- [ ] Lifecycle-aware collection
- [ ] Repository pattern
- [ ] Data migration
- [ ] Testing DataStore operations
- [ ] Performance optimization
- [ ] Complex state management
- [ ] Real-world application patterns

---

## SOLUTION VERIFICATION GUIDE

When you complete exercises, verify:

1. **Compilation**: Code compiles without errors
2. **Runtime**: App runs without crashes
3. **Functionality**: Data persists after app restart
4. **Error Handling**: App handles network/disk errors gracefully
5. **Performance**: No UI freezing or ANR
6. **Testing**: Unit tests pass for repository methods
7. **Best Practices**: Code follows architecture patterns
8. **Documentation**: Code is well-commented

---

**Practice Guide Version**: 1.0
**Difficulty Progression**: Beginner → Intermediate → Advanced
**Estimated Completion Time**: 8-12 hours

