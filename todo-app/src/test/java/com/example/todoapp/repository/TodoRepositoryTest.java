package com.example.todoapp.repository;

import com.example.todoapp.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
@DisplayName("TodoRepository Integration Tests")
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    private Todo todo1;
    private Todo todo2;
    private Todo todo3;

    @BeforeEach
    void setUp() {
        // Clear the repository before each test
        todoRepository.deleteAll();

        // Create test data
        todo1 = Todo.builder()
                .title("Complete Project")
                .description("Finish Spring Boot project")
                .priority("HIGH")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        todo2 = Todo.builder()
                .title("Review Code")
                .description("Code review for team members")
                .priority("MEDIUM")
                .completed(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        todo3 = Todo.builder()
                .title("Buy Groceries")
                .description("Milk, eggs, bread")
                .priority("LOW")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== Standard CRUD Tests ====================

    @Test
    @DisplayName("should save a todo")
    void testSave() {
        // Act
        Todo savedTodo = todoRepository.save(todo1);

        // Assert
        assertThat(savedTodo).isNotNull();
        assertThat(savedTodo.getId()).isNotNull();
        assertThat(savedTodo.getTitle()).isEqualTo("Complete Project");
    }

    @Test
    @DisplayName("should find a todo by ID")
    void testFindById() {
        // Arrange
        Todo savedTodo = todoRepository.save(todo1);

        // Act
        Optional<Todo> foundTodo = todoRepository.findById(savedTodo.getId());

        // Assert
        assertThat(foundTodo).isPresent();
        assertThat(foundTodo.get().getTitle()).isEqualTo("Complete Project");
    }

    @Test
    @DisplayName("should return empty Optional for non-existent ID")
    void testFindByIdNotFound() {
        // Act
        Optional<Todo> foundTodo = todoRepository.findById("nonexistent-id");

        // Assert
        assertThat(foundTodo).isEmpty();
    }

    @Test
    @DisplayName("should find all todos")
    void testFindAll() {
        // Arrange
        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // Act
        List<Todo> allTodos = todoRepository.findAll();

        // Assert
        assertThat(allTodos).hasSize(3);
        assertThat(allTodos).containsExactlyInAnyOrder(todo1, todo2, todo3);
    }

    @Test
    @DisplayName("should return empty list when no todos exist")
    void testFindAllEmpty() {
        // Act
        List<Todo> allTodos = todoRepository.findAll();

        // Assert
        assertThat(allTodos).isEmpty();
    }

    @Test
    @DisplayName("should update a todo")
    void testUpdate() {
        // Arrange
        Todo savedTodo = todoRepository.save(todo1);
        savedTodo.setTitle("Updated Title");
        savedTodo.setCompleted(true);

        // Act
        Todo updatedTodo = todoRepository.save(savedTodo);

        // Assert
        assertThat(updatedTodo.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedTodo.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("should delete a todo by ID")
    void testDeleteById() {
        // Arrange
        Todo savedTodo = todoRepository.save(todo1);

        // Act
        todoRepository.deleteById(savedTodo.getId());

        // Assert
        Optional<Todo> deletedTodo = todoRepository.findById(savedTodo.getId());
        assertThat(deletedTodo).isEmpty();
    }

    @Test
    @DisplayName("should delete all todos")
    void testDeleteAll() {
        // Arrange
        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // Act
        todoRepository.deleteAll();

        // Assert
        assertThat(todoRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("should check if todo exists by ID")
    void testExistsById() {
        // Arrange
        Todo savedTodo = todoRepository.save(todo1);

        // Act & Assert
        assertThat(todoRepository.existsById(savedTodo.getId())).isTrue();
        assertThat(todoRepository.existsById("nonexistent-id")).isFalse();
    }

    // ==================== Custom Query Tests ====================

    @Test
    @DisplayName("should find todos by completion status - completed true")
    void testFindByCompletedTrue() {
        // Arrange
        todoRepository.save(todo1); // false
        todoRepository.save(todo2); // true
        todoRepository.save(todo3); // false

        // Act
        List<Todo> completedTodos = todoRepository.findByCompleted(true);

        // Assert
        assertThat(completedTodos).hasSize(1);
        assertThat(completedTodos).containsExactly(todo2);
    }

    @Test
    @DisplayName("should find todos by completion status - completed false")
    void testFindByCompletedFalse() {
        // Arrange
        todoRepository.save(todo1); // false
        todoRepository.save(todo2); // true
        todoRepository.save(todo3); // false

        // Act
        List<Todo> incompleteTodos = todoRepository.findByCompleted(false);

        // Assert
        assertThat(incompleteTodos).hasSize(2);
        assertThat(incompleteTodos).contains(todo1, todo3);
    }

    @Test
    @DisplayName("should return empty list when no completed todos exist")
    void testFindByCompletedEmpty() {
        // Arrange
        todoRepository.save(todo1); // false
        todoRepository.save(todo3); // false

        // Act
        List<Todo> completedTodos = todoRepository.findByCompleted(true);

        // Assert
        assertThat(completedTodos).isEmpty();
    }

    @Test
    @DisplayName("should find todos by priority - HIGH")
    void testFindByPriorityHigh() {
        // Arrange
        todoRepository.save(todo1); // HIGH
        todoRepository.save(todo2); // MEDIUM
        todoRepository.save(todo3); // LOW

        // Act
        List<Todo> highPriorityTodos = todoRepository.findByPriority("HIGH");

        // Assert
        assertThat(highPriorityTodos).hasSize(1);
        assertThat(highPriorityTodos).containsExactly(todo1);
    }

    @Test
    @DisplayName("should find todos by priority - MEDIUM")
    void testFindByPriorityMedium() {
        // Arrange
        todoRepository.save(todo1); // HIGH
        todoRepository.save(todo2); // MEDIUM
        todoRepository.save(todo3); // LOW

        // Act
        List<Todo> mediumPriorityTodos = todoRepository.findByPriority("MEDIUM");

        // Assert
        assertThat(mediumPriorityTodos).hasSize(1);
        assertThat(mediumPriorityTodos).containsExactly(todo2);
    }

    @Test
    @DisplayName("should find todos by priority - LOW")
    void testFindByPriorityLow() {
        // Arrange
        todoRepository.save(todo1); // HIGH
        todoRepository.save(todo2); // MEDIUM
        todoRepository.save(todo3); // LOW

        // Act
        List<Todo> lowPriorityTodos = todoRepository.findByPriority("LOW");

        // Assert
        assertThat(lowPriorityTodos).hasSize(1);
        assertThat(lowPriorityTodos).containsExactly(todo3);
    }

    @Test
    @DisplayName("should return empty list for non-existent priority")
    void testFindByPriorityNotFound() {
        // Arrange
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // Act
        List<Todo> todosWithUrgentPriority = todoRepository.findByPriority("URGENT");

        // Assert
        assertThat(todosWithUrgentPriority).isEmpty();
    }

    @Test
    @DisplayName("should find todos by title containing text - case insensitive")
    void testFindByTitleContainingIgnoreCase() {
        // Arrange
        todoRepository.save(todo1); // "Complete Project"
        todoRepository.save(todo2); // "Review Code"
        todoRepository.save(todo3); // "Buy Groceries"

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("project");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results).containsExactly(todo1);
    }

    @Test
    @DisplayName("should find todos by title containing - case insensitive uppercase")
    void testFindByTitleContainingIgnoreCaseUppercase() {
        // Arrange
        todoRepository.save(todo1); // "Complete Project"
        todoRepository.save(todo2); // "Review Code"
        todoRepository.save(todo3); // "Buy Groceries"

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("PROJECT");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results).containsExactly(todo1);
    }

    @Test
    @DisplayName("should find todos by title containing partial match")
    void testFindByTitleContainingPartialMatch() {
        // Arrange
        todo1.setTitle("Buy Milk");
        todo2.setTitle("Buy Eggs");
        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3); // "Buy Groceries"

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("Buy");

        // Assert
        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("should return empty list when title search has no matches")
    void testFindByTitleContainingNoMatches() {
        // Arrange
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("Delete");

        // Assert
        assertThat(results).isEmpty();
    }

    // ==================== Combined Filter Tests ====================

    @Test
    @DisplayName("should combine filters - find completed HIGH priority todos")
    void testCombinedFilterCompletedAndPriority() {
        // Arrange
        todo1.setCompleted(true);
        todo2.setCompleted(true);
        todoRepository.save(todo1); // completed, HIGH
        todoRepository.save(todo2); // completed, MEDIUM
        todoRepository.save(todo3); // not completed, LOW

        // Act
        List<Todo> completed = todoRepository.findByCompleted(true);
        List<Todo> highPriority = completed.stream()
                .filter(t -> "HIGH".equals(t.getPriority()))
                .toList();

        // Assert
        assertThat(highPriority).hasSize(1);
        assertThat(highPriority).containsExactly(todo1);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("should handle special characters in title search")
    void testSearchWithSpecialCharacters() {
        // Arrange
        todo1.setTitle("Buy @ #1 Urgent Task!");
        todoRepository.save(todo1);

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("@");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results).containsExactly(todo1);
    }

    @Test
    @DisplayName("should handle Unicode characters in title")
    void testSearchWithUnicodeCharacters() {
        // Arrange
        todo1.setTitle("Learn Français español");
        todoRepository.save(todo1);

        // Act
        List<Todo> results = todoRepository.findByTitleContainingIgnoreCase("français");

        // Assert
        assertThat(results).hasSize(1);
    }

    @Test
    @DisplayName("should preserve timestamps when saving")
    void testTimestampsPreserved() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        todo1.setCreatedAt(now);
        todo1.setUpdatedAt(now);

        // Act
        Todo savedTodo = todoRepository.save(todo1);
        Optional<Todo> foundTodo = todoRepository.findById(savedTodo.getId());

        // Assert
        assertThat(foundTodo).isPresent();
        assertThat(foundTodo.get().getCreatedAt()).isNotNull();
        assertThat(foundTodo.get().getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("should count todos")
    void testCount() {
        // Arrange
        todoRepository.save(todo1);
        todoRepository.save(todo2);
        todoRepository.save(todo3);

        // Act
        long count = todoRepository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }
}
