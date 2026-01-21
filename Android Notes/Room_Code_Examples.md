# Room Database: Practical Code Examples

## Complete App Example: Todo List Application

This section provides a complete, production-ready example of a Todo app using Room with modern Android architecture.

---

## 1. Entity (Data Model)

```kotlin
// entities/TodoEntity.kt

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.time.LocalDateTime

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todo_id")
    val id: Int = 0,
    
    @ColumnInfo(name = "todo_title")
    val title: String,
    
    @ColumnInfo(name = "todo_description")
    val description: String = "",
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    @ColumnInfo(name = "priority")
    val priority: Int = 1,  // 1=Low, 2=Medium, 3=High
    
    @ColumnInfo(name = "due_date")
    val dueDate: String = "",  // Format: "yyyy-MM-dd HH:mm"
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)

// Projection: Return only essential fields
data class TodoPreview(
    @ColumnInfo(name = "todo_id")
    val id: Int,
    
    @ColumnInfo(name = "todo_title")
    val title: String,
    
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean
)
```

---

## 2. Data Access Object (DAO)

```kotlin
// dao/TodoDao.kt

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    
    // ============ INSERT ============
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long
    
    @Insert
    suspend fun insertMultipleTodos(todos: List<TodoEntity>)
    
    
    // ============ READ ============
    
    // Get all todos with Flow for reactive updates
    @Query("SELECT * FROM todos ORDER BY priority DESC, created_at DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>
    
    // Get all incomplete todos
    @Query("""
        SELECT * FROM todos 
        WHERE is_completed = 0 
        ORDER BY priority DESC, due_date ASC
    """)
    fun getIncompleteTodos(): Flow<List<TodoEntity>>
    
    // Get todos by priority
    @Query("""
        SELECT * FROM todos 
        WHERE priority = :priority 
        ORDER BY due_date ASC
    """)
    fun getTodosByPriority(priority: Int): Flow<List<TodoEntity>>
    
    // Get single todo
    @Query("SELECT * FROM todos WHERE todo_id = :todoId")
    suspend fun getTodoById(todoId: Int): TodoEntity?
    
    // Search todos
    @Query("""
        SELECT * FROM todos 
        WHERE todo_title LIKE :query OR todo_description LIKE :query
        ORDER BY created_at DESC
    """)
    fun searchTodos(query: String): Flow<List<TodoEntity>>
    
    // Get only essential fields (projection)
    @Query("SELECT todo_id, todo_title, is_completed FROM todos ORDER BY created_at DESC")
    fun getTodoPreviews(): Flow<List<TodoPreview>>
    
    // Count todos
    @Query("SELECT COUNT(*) FROM todos")
    fun getTodoCount(): Flow<Int>
    
    // Get completed count
    @Query("SELECT COUNT(*) FROM todos WHERE is_completed = 1")
    suspend fun getCompletedCount(): Int
    
    
    // ============ UPDATE ============
    
    @Update
    suspend fun updateTodo(todo: TodoEntity)
    
    @Query("""
        UPDATE todos 
        SET is_completed = :isCompleted, updated_at = :timestamp
        WHERE todo_id = :todoId
    """)
    suspend fun updateTodoCompletion(
        todoId: Int,
        isCompleted: Boolean,
        timestamp: Long = System.currentTimeMillis()
    )
    
    @Query("""
        UPDATE todos 
        SET todo_title = :title, 
            todo_description = :description,
            updated_at = :timestamp
        WHERE todo_id = :todoId
    """)
    suspend fun updateTodoContent(
        todoId: Int,
        title: String,
        description: String,
        timestamp: Long = System.currentTimeMillis()
    )
    
    
    // ============ DELETE ============
    
    @Delete
    suspend fun deleteTodo(todo: TodoEntity)
    
    @Query("DELETE FROM todos WHERE todo_id = :todoId")
    suspend fun deleteTodoById(todoId: Int)
    
    @Query("DELETE FROM todos WHERE is_completed = 1")
    suspend fun deleteCompletedTodos()
    
    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()
}
```

---

## 3. Database Class

```kotlin
// database/AppDatabase.kt

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TodoEntity::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun todoDao(): TodoDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        // Singleton pattern
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(dbCallback)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        // Migration from version 1 to 2: Add priority field
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE todos ADD COLUMN priority INTEGER DEFAULT 1 NOT NULL"
                )
                database.execSQL(
                    "ALTER TABLE todos ADD COLUMN updated_at INTEGER DEFAULT ${System.currentTimeMillis()} NOT NULL"
                )
            }
        }
        
        // Pre-populate database with sample data
        private val dbCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Pre-populate if needed
            }
        }
    }
}
```

---

## 4. Repository

```kotlin
// repository/TodoRepository.kt

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository(private val todoDao: TodoDao) {
    
    // Expose flows for UI observation
    val allTodos: Flow<List<TodoEntity>> = todoDao.getAllTodos()
    
    val incompleteTodos: Flow<List<TodoEntity>> = todoDao.getIncompleteTodos()
    
    val todoCount: Flow<Int> = todoDao.getTodoCount()
    
    // Get todos (suspend function for one-shot fetch)
    suspend fun getTodoById(id: Int): TodoEntity? = withContext(Dispatchers.IO) {
        todoDao.getTodoById(id)
    }
    
    // Insert todo
    suspend fun insertTodo(todo: TodoEntity): Long = withContext(Dispatchers.IO) {
        todoDao.insertTodo(todo)
    }
    
    // Update todo
    suspend fun updateTodo(todo: TodoEntity) = withContext(Dispatchers.IO) {
        todoDao.updateTodo(todo)
    }
    
    // Toggle completion
    suspend fun toggleTodoCompletion(todoId: Int, isCompleted: Boolean) {
        withContext(Dispatchers.IO) {
            todoDao.updateTodoCompletion(todoId, isCompleted)
        }
    }
    
    // Delete todo
    suspend fun deleteTodo(todo: TodoEntity) = withContext(Dispatchers.IO) {
        todoDao.deleteTodo(todo)
    }
    
    // Delete by ID
    suspend fun deleteTodoById(todoId: Int) = withContext(Dispatchers.IO) {
        todoDao.deleteTodoById(todoId)
    }
    
    // Clear completed
    suspend fun clearCompleted() = withContext(Dispatchers.IO) {
        todoDao.deleteCompletedTodos()
    }
    
    // Search
    fun searchTodos(query: String): Flow<List<TodoEntity>> {
        return todoDao.searchTodos("%$query%")
    }
    
    // Get by priority
    fun getTodosByPriority(priority: Int): Flow<List<TodoEntity>> {
        return todoDao.getTodosByPriority(priority)
    }
}
```

---

## 5. ViewModel

```kotlin
// viewmodel/TodoViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // Todos as StateFlow for UI
    val allTodos = repository.allTodos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val incompleteTodos = repository.incompleteTodos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val todoCount = repository.todoCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    // Add new todo
    fun addTodo(title: String, description: String = "", priority: Int = 1) {
        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading
                
                val newTodo = TodoEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    createdAt = System.currentTimeMillis()
                )
                
                repository.insertTodo(newTodo)
                _uiState.value = UiState.Success("Todo added")
                
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Update todo
    fun updateTodo(todo: TodoEntity) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo)
                _uiState.value = UiState.Success("Todo updated")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Toggle completion status
    fun toggleTodoCompletion(todoId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                repository.toggleTodoCompletion(todoId, isCompleted)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Delete todo
    fun deleteTodo(todo: TodoEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                _uiState.value = UiState.Success("Todo deleted")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Delete completed todos
    fun clearCompleted() {
        viewModelScope.launch {
            try {
                repository.clearCompleted()
                _uiState.value = UiState.Success("Completed todos cleared")
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    // Search todos
    fun searchTodos(query: String) = repository.searchTodos(query)
    
    // Get todos by priority
    fun getTodosByPriority(priority: Int) = repository.getTodosByPriority(priority)
}

// UI State
sealed class UiState {
    object Loading : UiState()
    data class Success(val message: String) : UiState()
    data class Error(val message: String) : UiState()
}

// ViewModelProvider Factory
class TodoViewModelFactory(private val repository: TodoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

---

## 6. Fragment/UI Layer Example

```kotlin
// ui/TodoFragment.kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.launch

class TodoFragment : Fragment() {
    
    private val viewModel: TodoViewModel by viewModels {
        val db = AppDatabase.getDatabase(requireContext())
        val repository = TodoRepository(db.todoDao())
        TodoViewModelFactory(repository)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Collect todos
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTodos.collect { todos ->
                    updateUI(todos)
                }
            }
        }
        
        // Observe UI state
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> showLoading()
                        is UiState.Success -> showMessage(state.message)
                        is UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }
    
    private fun updateUI(todos: List<TodoEntity>) {
        // Update adapter with todos
    }
    
    private fun showLoading() {
        // Show loading indicator
    }
    
    private fun showMessage(message: String) {
        // Show success message
    }
    
    private fun showError(message: String) {
        // Show error message
    }
}
```

---

## 7. Unit Tests

```kotlin
// test/TodoDaoTest.kt

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {
    
    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        todoDao = db.todoDao()
    }
    
    @After
    fun closeDb() {
        db.close()
    }
    
    @Test
    fun insertAndRetrieveTodo() = runBlocking {
        // Arrange
        val todo = TodoEntity(
            title = "Test Todo",
            description = "Test Description",
            priority = 1
        )
        
        // Act
        val id = todoDao.insertTodo(todo)
        val retrieved = todoDao.getTodoById(id.toInt())
        
        // Assert
        assertNotNull(retrieved)
        assertEquals(retrieved?.title, "Test Todo")
        assertEquals(retrieved?.priority, 1)
    }
    
    @Test
    fun updateTodoCompletion() = runBlocking {
        // Arrange
        val todo = TodoEntity(title = "Test", priority = 1)
        val id = todoDao.insertTodo(todo).toInt()
        
        // Act
        todoDao.updateTodoCompletion(id, true)
        val updated = todoDao.getTodoById(id)
        
        // Assert
        assertTrue(updated?.isCompleted ?: false)
    }
    
    @Test
    fun searchTodosByQuery() = runBlocking {
        // Arrange
        todoDao.insertMultipleTodos(listOf(
            TodoEntity(title = "Buy milk", priority = 1),
            TodoEntity(title = "Study Kotlin", priority = 2),
            TodoEntity(title = "Buy bread", priority = 1)
        ))
        
        // Act & Assert
        todoDao.searchTodos("%Buy%").collect { results ->
            assertEquals(results.size, 2)
            assertTrue(results.all { it.title.contains("Buy") })
        }
    }
    
    @Test
    fun deleteCompletedTodos() = runBlocking {
        // Arrange
        val todo1 = TodoEntity(title = "Todo 1", priority = 1)
        val todo2 = TodoEntity(title = "Todo 2", isCompleted = true, priority = 1)
        todoDao.insertMultipleTodos(listOf(todo1, todo2))
        
        // Act
        todoDao.deleteCompletedTodos()
        
        // Assert
        todoDao.getAllTodos().collect { results ->
            assertEquals(results.size, 1)
            assertEquals(results[0].title, "Todo 1")
        }
    }
}
```

---

## 8. Type Converter Example

```kotlin
// database/DateConverter.kt

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateConverter {
    
    private val formatter = DateTimeFormatter.ISO_DATE_TIME
    
    @TypeConverter
    fun fromString(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, formatter)
        }
    }
    
    @TypeConverter
    fun dateToString(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}
```

---

## 9. Dependency Injection Setup (Hilt)

```kotlin
// di/DatabaseModule.kt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = AppDatabase.getDatabase(context)
    
    @Singleton
    @Provides
    fun provideTodoDao(database: AppDatabase): TodoDao = database.todoDao()
    
    @Singleton
    @Provides
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepository(todoDao)
    }
}

// Usage in ViewModel
@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    // ...
}
```

---

## Quick Reference: Common Patterns

### Pattern 1: Insert & Get ID

```kotlin
@Dao
interface MyDao {
    @Insert
    suspend fun insertItem(item: Item): Long
}

// Usage
viewModelScope.launch {
    val newId = dao.insertItem(item)
    Log.d("Room", "Inserted with ID: $newId")
}
```

### Pattern 2: Observe & Update

```kotlin
@Dao
interface MyDao {
    @Query("SELECT * FROM items")
    fun getItems(): Flow<List<Item>>
    
    @Update
    suspend fun updateItem(item: Item)
}

// Usage
viewModel.items.collect { items ->
    updateUI(items)
}
```

### Pattern 3: Transaction

```kotlin
@Dao
interface MyDao {
    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserWithOrders(userId: Int): UserWithOrders
}
```

### Pattern 4: Batch Operations

```kotlin
@Dao
interface MyDao {
    @Insert
    suspend fun insertAll(items: List<Item>)
    
    @Update
    suspend fun updateAll(items: List<Item>)
    
    @Delete
    suspend fun deleteAll(items: List<Item>)
}
```

---

This comprehensive guide provides production-ready code that you can adapt to your specific needs!

