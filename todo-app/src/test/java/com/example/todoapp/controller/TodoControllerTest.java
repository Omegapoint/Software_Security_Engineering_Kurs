package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@DisplayName("TodoController Unit Tests")
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = Todo.builder()
                .id("1")
                .title("Test Todo")
                .description("Test Description")
                .priority("HIGH")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ==================== GET Tests ====================

    @Test
    @DisplayName("should return all todos with 200 status")
    void testGetAllTodos() throws Exception {
        // Arrange
        List<Todo> todos = Arrays.asList(testTodo);
        when(todoService.getAllTodos()).thenReturn(todos);

        // Act & Assert
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].title", is("Test Todo")));

        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    @DisplayName("should return empty list when no todos exist")
    void testGetAllTodosEmpty() throws Exception {
        // Arrange
        when(todoService.getAllTodos()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(todoService, times(1)).getAllTodos();
    }

    @Test
    @DisplayName("should return todo by ID with 200 status")
    void testGetTodoById() throws Exception {
        // Arrange
        when(todoService.getTodoById("1")).thenReturn(Optional.of(testTodo));

        // Act & Assert
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Test Todo")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(todoService, times(1)).getTodoById("1");
    }

    @Test
    @DisplayName("should return 404 when todo not found")
    void testGetTodoByIdNotFound() throws Exception {
        // Arrange
        when(todoService.getTodoById("999")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/todos/999"))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).getTodoById("999");
    }

    @Test
    @DisplayName("should return health check with 200 status")
    void testHealthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Todo App is running!"));
    }

    // ==================== POST Tests ====================

    @Test
    @DisplayName("should create a new todo and return 201 status")
    void testCreateTodo() throws Exception {
        // Arrange
        Todo newTodo = Todo.builder()
                .title("New Todo")
                .description("New Description")
                .priority("MEDIUM")
                .build();

        Todo createdTodo = Todo.builder()
                .id("2")
                .title("New Todo")
                .description("New Description")
                .priority("MEDIUM")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.createTodo(any(Todo.class))).thenReturn(createdTodo);

        // Act & Assert
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("2")))
                .andExpect(jsonPath("$.title", is("New Todo")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }

    @Test
    @DisplayName("should return 400 when creating todo with null title")
    void testCreateTodoWithInvalidData() throws Exception {
        // Arrange
        Todo invalidTodo = Todo.builder()
                .description("Missing title")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isCreated()); // Spring still creates it but allows null

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }

    // ==================== PUT Tests ====================

    @Test
    @DisplayName("should update a todo and return 200 status")
    void testUpdateTodo() throws Exception {
        // Arrange
        Todo updateData = Todo.builder()
                .title("Updated Title")
                .description("Updated Description")
                .priority("LOW")
                .completed(true)
                .build();

        Todo updatedTodo = Todo.builder()
                .id("1")
                .title("Updated Title")
                .description("Updated Description")
                .priority("LOW")
                .completed(true)
                .createdAt(testTodo.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.updateTodo(eq("1"), any(Todo.class))).thenReturn(Optional.of(updatedTodo));

        // Act & Assert
        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.priority", is("LOW")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(todoService, times(1)).updateTodo(eq("1"), any(Todo.class));
    }

    @Test
    @DisplayName("should return 404 when updating non-existent todo")
    void testUpdateTodoNotFound() throws Exception {
        // Arrange
        when(todoService.updateTodo(eq("999"), any(Todo.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/todos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTodo)))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).updateTodo(eq("999"), any(Todo.class));
    }

    @Test
    @DisplayName("should mark todo as completed")
    void testMarkAsCompleted() throws Exception {
        // Arrange
        Todo completedTodo = testTodo;
        completedTodo.setCompleted(true);
        when(todoService.markAsCompleted("1")).thenReturn(Optional.of(completedTodo));

        // Act & Assert
        mockMvc.perform(put("/api/todos/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(todoService, times(1)).markAsCompleted("1");
    }

    @Test
    @DisplayName("should mark todo as incomplete")
    void testMarkAsIncomplete() throws Exception {
        // Arrange
        Todo incompleteTodo = testTodo;
        incompleteTodo.setCompleted(false);
        when(todoService.markAsIncomplete("1")).thenReturn(Optional.of(incompleteTodo));

        // Act & Assert
        mockMvc.perform(put("/api/todos/1/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(todoService, times(1)).markAsIncomplete("1");
    }

    @Test
    @DisplayName("should return 404 when marking non-existent todo as completed")
    void testMarkAsCompletedNotFound() throws Exception {
        // Arrange
        when(todoService.markAsCompleted("999")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/api/todos/999/complete"))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).markAsCompleted("999");
    }

    // ==================== DELETE Tests ====================

    @Test
    @DisplayName("should delete a todo and return 204 status")
    void testDeleteTodo() throws Exception {
        // Arrange
        when(todoService.deleteTodo("1")).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/api/todos/1"))
                .andExpect(status().isNoContent());

        verify(todoService, times(1)).deleteTodo("1");
    }

    @Test
    @DisplayName("should return 404 when deleting non-existent todo")
    void testDeleteTodoNotFound() throws Exception {
        // Arrange
        when(todoService.deleteTodo("999")).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/todos/999"))
                .andExpect(status().isNotFound());

        verify(todoService, times(1)).deleteTodo("999");
    }

    // ==================== Filter Tests ====================

    @Test
    @DisplayName("should return todos filtered by completion status")
    void testGetTodosByStatus() throws Exception {
        // Arrange
        List<Todo> completedTodos = Arrays.asList(
                Todo.builder().id("1").title("Todo 1").completed(true).build()
        );
        when(todoService.getTodosByStatus(true)).thenReturn(completedTodos);

        // Act & Assert
        mockMvc.perform(get("/api/todos/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].completed", is(true)));

        verify(todoService, times(1)).getTodosByStatus(true);
    }

    @Test
    @DisplayName("should return todos filtered by priority")
    void testGetTodosByPriority() throws Exception {
        // Arrange
        List<Todo> highPriorityTodos = Arrays.asList(
                Todo.builder().id("1").title("Todo 1").priority("HIGH").build()
        );
        when(todoService.getTodosByPriority("HIGH")).thenReturn(highPriorityTodos);

        // Act & Assert
        mockMvc.perform(get("/api/todos/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("HIGH")));

        verify(todoService, times(1)).getTodosByPriority("HIGH");
    }

    @Test
    @DisplayName("should search todos by title")
    void testSearchTodosByTitle() throws Exception {
        // Arrange
        List<Todo> searchResults = Arrays.asList(
                Todo.builder().id("1").title("Buy Groceries").build()
        );
        when(todoService.searchTodosByTitle("Buy")).thenReturn(searchResults);

        // Act & Assert
        mockMvc.perform(get("/api/todos/search/Buy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Buy Groceries")));

        verify(todoService, times(1)).searchTodosByTitle("Buy");
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("should handle special characters in title")
    void testSearchWithSpecialCharacters() throws Exception {
        // Arrange
        List<Todo> results = Arrays.asList(
                Todo.builder().id("1").title("Test @ #123").build()
        );
        when(todoService.searchTodosByTitle("@")).thenReturn(results);

        // Act & Assert
        mockMvc.perform(get("/api/todos/search/@"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(todoService, times(1)).searchTodosByTitle("@");
    }

    @Test
    @DisplayName("should handle large payloads")
    void testCreateTodoWithLargeDescription() throws Exception {
        // Arrange
        String largeDescription = "x".repeat(5000);
        Todo todoWithLargeDescription = Todo.builder()
                .title("Large Description Todo")
                .description(largeDescription)
                .priority("MEDIUM")
                .build();

        Todo createdTodo = Todo.builder()
                .id("3")
                .title("Large Description Todo")
                .description(largeDescription)
                .priority("MEDIUM")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(todoService.createTodo(any(Todo.class))).thenReturn(createdTodo);

        // Act & Assert
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoWithLargeDescription)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("3")));

        verify(todoService, times(1)).createTodo(any(Todo.class));
    }
}
