package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("TodoService Unit Tests")
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        todoService = new TodoService(todoRepository);
    }

    // ==================== Create Tests ====================

    @Test
    @DisplayName("should create a new todo with default values")
    void testCreateTodo() {
        // Arrange
        Todo inputTodo = Todo.builder()
                .title("Test Todo")
                .description("Test Description")
                .priority("HIGH")
                .build();

        Todo savedTodo = Todo.builder()
                .id("1")
                .title("Test Todo")
                .description("Test Description")
                .priority("HIGH")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.save(any(Todo.class))).thenReturn(savedTodo);

        // Act
        Todo result = todoService.createTodo(inputTodo);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getTitle()).isEqualTo("Test Todo");
        assertThat(result.isCompleted()).isFalse();
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("should set timestamps when creating a todo")
    void testCreateTodoSetsTimestamps() {
        // Arrange
        Todo inputTodo = Todo.builder()
                .title("Test Todo")
                .build();

        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> {
            Todo todo = invocation.getArgument(0);
            assertThat(todo.getCreatedAt()).isNotNull();
            assertThat(todo.getUpdatedAt()).isNotNull();
            todo.setId("1");
            return todo;
        });

        // Act
        Todo result = todoService.createTodo(inputTodo);

        // Assert
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    // ==================== Read Tests ====================

    @Test
    @DisplayName("should return all todos")
    void testGetAllTodos() {
        // Arrange
        List<Todo> todos = Arrays.asList(
                Todo.builder().id("1").title("Todo 1").build(),
                Todo.builder().id("2").title("Todo 2").build(),
                Todo.builder().id("3").title("Todo 3").build()
        );
        when(todoRepository.findAll()).thenReturn(todos);

        // Act
        List<Todo> result = todoService.getAllTodos();

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(todos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should return empty list when no todos exist")
    void testGetAllTodosEmpty() {
        // Arrange
        when(todoRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Todo> result = todoService.getAllTodos();

        // Assert
        assertThat(result).isEmpty();
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("should get todo by ID successfully")
    void testGetTodoById() {
        // Arrange
        Todo todo = Todo.builder()
                .id("1")
                .title("Test Todo")
                .build();
        when(todoRepository.findById("1")).thenReturn(Optional.of(todo));

        // Act
        Optional<Todo> result = todoService.getTodoById("1");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("1");
        assertThat(result.get().getTitle()).isEqualTo("Test Todo");
        verify(todoRepository, times(1)).findById("1");
    }

    @Test
    @DisplayName("should return empty Optional when todo not found")
    void testGetTodoByIdNotFound() {
        // Arrange
        when(todoRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.getTodoById("999");

        // Assert
        assertThat(result).isEmpty();
        verify(todoRepository, times(1)).findById("999");
    }

    // ==================== Update Tests ====================

    @Test
    @DisplayName("should update todo successfully")
    void testUpdateTodo() {
        // Arrange
        String todoId = "1";
        Todo existingTodo = Todo.builder()
                .id(todoId)
                .title("Old Title")
                .description("Old Description")
                .priority("LOW")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Todo updateDetails = Todo.builder()
                .title("New Title")
                .description("New Description")
                .priority("HIGH")
                .completed(true)
                .build();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.updateTodo(todoId, updateDetails);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("New Title");
        assertThat(result.get().getDescription()).isEqualTo("New Description");
        assertThat(result.get().getPriority()).isEqualTo("HIGH");
        assertThat(result.get().isCompleted()).isTrue();
        assertThat(result.get().getUpdatedAt()).isNotNull();
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("should update only non-null fields")
    void testUpdateTodoPartial() {
        // Arrange
        String todoId = "1";
        Todo existingTodo = Todo.builder()
                .id(todoId)
                .title("Original Title")
                .description("Original Description")
                .priority("LOW")
                .build();

        Todo updateDetails = Todo.builder()
                .title("Updated Title")
                .description(null)
                .priority(null)
                .build();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.updateTodo(todoId, updateDetails);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Updated Title");
        assertThat(result.get().getDescription()).isEqualTo("Original Description");
        assertThat(result.get().getPriority()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("should return empty Optional when updating non-existent todo")
    void testUpdateTodoNotFound() {
        // Arrange
        when(todoRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Todo> result = todoService.updateTodo("999", new Todo());

        // Assert
        assertThat(result).isEmpty();
        verify(todoRepository, times(1)).findById("999");
        verify(todoRepository, never()).save(any());
    }

    // ==================== Delete Tests ====================

    @Test
    @DisplayName("should delete todo successfully")
    void testDeleteTodo() {
        // Arrange
        String todoId = "1";
        when(todoRepository.existsById(todoId)).thenReturn(true);
        doNothing().when(todoRepository).deleteById(todoId);

        // Act
        boolean result = todoService.deleteTodo(todoId);

        // Assert
        assertThat(result).isTrue();
        verify(todoRepository, times(1)).existsById(todoId);
        verify(todoRepository, times(1)).deleteById(todoId);
    }

    @Test
    @DisplayName("should return false when deleting non-existent todo")
    void testDeleteTodoNotFound() {
        // Arrange
        String todoId = "999";
        when(todoRepository.existsById(todoId)).thenReturn(false);

        // Act
        boolean result = todoService.deleteTodo(todoId);

        // Assert
        assertThat(result).isFalse();
        verify(todoRepository, times(1)).existsById(todoId);
        verify(todoRepository, never()).deleteById(any());
    }

    // ==================== Filter Tests ====================

    @Test
    @DisplayName("should get todos by completion status")
    void testGetTodosByStatus() {
        // Arrange
        List<Todo> completedTodos = Arrays.asList(
                Todo.builder().id("1").title("Todo 1").completed(true).build(),
                Todo.builder().id("2").title("Todo 2").completed(true).build()
        );
        when(todoRepository.findByCompleted(true)).thenReturn(completedTodos);

        // Act
        List<Todo> result = todoService.getTodosByStatus(true);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Todo::isCompleted);
        verify(todoRepository, times(1)).findByCompleted(true);
    }

    @Test
    @DisplayName("should get todos by priority")
    void testGetTodosByPriority() {
        // Arrange
        List<Todo> highPriorityTodos = Arrays.asList(
                Todo.builder().id("1").title("Todo 1").priority("HIGH").build(),
                Todo.builder().id("2").title("Todo 2").priority("HIGH").build()
        );
        when(todoRepository.findByPriority("HIGH")).thenReturn(highPriorityTodos);

        // Act
        List<Todo> result = todoService.getTodosByPriority("HIGH");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(todo -> "HIGH".equals(todo.getPriority()));
        verify(todoRepository, times(1)).findByPriority("HIGH");
    }

    @Test
    @DisplayName("should search todos by title")
    void testSearchTodosByTitle() {
        // Arrange
        List<Todo> searchResults = Arrays.asList(
                Todo.builder().id("1").title("Buy Groceries").build(),
                Todo.builder().id("2").title("Buy Books").build()
        );
        when(todoRepository.findByTitleContainingIgnoreCase("Buy")).thenReturn(searchResults);

        // Act
        List<Todo> result = todoService.searchTodosByTitle("Buy");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(todo -> todo.getTitle().contains("Buy"));
        verify(todoRepository, times(1)).findByTitleContainingIgnoreCase("Buy");
    }

    // ==================== Status Change Tests ====================

    @Test
    @DisplayName("should mark todo as completed")
    void testMarkAsCompleted() {
        // Arrange
        String todoId = "1";
        Todo todo = Todo.builder()
                .id(todoId)
                .title("Test Todo")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.markAsCompleted(todoId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().isCompleted()).isTrue();
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("should mark todo as incomplete")
    void testMarkAsIncomplete() {
        // Arrange
        String todoId = "1";
        Todo todo = Todo.builder()
                .id(todoId)
                .title("Test Todo")
                .completed(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.markAsIncomplete(todoId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().isCompleted()).isFalse();
        verify(todoRepository, times(1)).findById(todoId);
        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    @DisplayName("should update timestamp when marking as completed")
    void testMarkAsCompletedUpdatesTimestamp() {
        // Arrange
        String todoId = "1";
        LocalDateTime originalTime = LocalDateTime.now().minusHours(1);
        Todo todo = Todo.builder()
                .id(todoId)
                .title("Test Todo")
                .completed(false)
                .createdAt(originalTime)
                .updatedAt(originalTime)
                .build();

        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Todo> result = todoService.markAsCompleted(todoId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUpdatedAt()).isAfter(originalTime);
    }
}
