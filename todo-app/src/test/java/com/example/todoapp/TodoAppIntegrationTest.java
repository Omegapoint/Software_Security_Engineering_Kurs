package com.example.todoapp;

import com.example.todoapp.model.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("TodoApp Integration Tests")
class TodoAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clear all todos before each test by getting all and deleting them
        // This is optional and depends on your test data setup
    }

    // ==================== Full Flow Tests ====================

    @Test
    @DisplayName("should complete full CRUD workflow")
    void testFullCrudWorkflow() throws Exception {
        // Step 1: Create a new todo
        Todo newTodo = Todo.builder()
                .title("Full Flow Test")
                .description("Testing complete workflow")
                .priority("HIGH")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTodo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Full Flow Test")))
                .andReturn();

        // Extract the created todo ID
        String responseBody = createResult.getResponse().getContentAsString();
        Todo createdTodo = objectMapper.readValue(responseBody, Todo.class);
        String todoId = createdTodo.getId();

        assertThat(todoId).isNotNull();

        // Step 2: Retrieve the created todo
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(todoId)))
                .andExpect(jsonPath("$.title", is("Full Flow Test")))
                .andExpect(jsonPath("$.completed", is(false)));

        // Step 3: Update the todo
        Todo updateData = Todo.builder()
                .title("Updated Full Flow Test")
                .description("Updated description")
                .priority("MEDIUM")
                .completed(false)
                .build();

        mockMvc.perform(put("/api/todos/" + todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Full Flow Test")))
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        // Step 4: Mark as completed
        mockMvc.perform(put("/api/todos/" + todoId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        // Step 5: Delete the todo
        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isNoContent());

        // Step 6: Verify deletion
        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should create and filter todos by status")
    void testCreateAndFilterByStatus() throws Exception {
        // Create multiple todos with different completion statuses
        Todo incompleteTodo = Todo.builder()
                .title("Incomplete Task")
                .description("This is not completed")
                .priority("HIGH")
                .build();

        Todo completedTodo = Todo.builder()
                .title("Completed Task")
                .description("This is completed")
                .priority("MEDIUM")
                .build();

        // Create both todos
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteTodo)))
                .andExpect(status().isCreated());

        MvcResult completedResult = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completedTodo)))
                .andExpect(status().isCreated())
                .andReturn();

        String completedTodoBody = completedResult.getResponse().getContentAsString();
        Todo savedCompletedTodo = objectMapper.readValue(completedTodoBody, Todo.class);

        // Mark the second todo as completed
        mockMvc.perform(put("/api/todos/" + savedCompletedTodo.getId() + "/complete"))
                .andExpect(status().isOk());

        // Filter by completed status
        mockMvc.perform(get("/api/todos/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].completed", hasSize(1)));

        // Filter by incomplete status
        mockMvc.perform(get("/api/todos/status/false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("should create and filter todos by priority")
    void testCreateAndFilterByPriority() throws Exception {
        // Create todos with different priorities
        Todo highPriorityTodo = Todo.builder()
                .title("High Priority Task")
                .priority("HIGH")
                .build();

        Todo mediumPriorityTodo = Todo.builder()
                .title("Medium Priority Task")
                .priority("MEDIUM")
                .build();

        Todo lowPriorityTodo = Todo.builder()
                .title("Low Priority Task")
                .priority("LOW")
                .build();

        // Create all todos
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(highPriorityTodo)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mediumPriorityTodo)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowPriorityTodo)))
                .andExpect(status().isCreated());

        // Filter by priority
        mockMvc.perform(get("/api/todos/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("HIGH")));

        mockMvc.perform(get("/api/todos/priority/MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("MEDIUM")));

        mockMvc.perform(get("/api/todos/priority/LOW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].priority", is("LOW")));
    }

    @Test
    @DisplayName("should create and search todos by title")
    void testCreateAndSearchByTitle() throws Exception {
        // Create todos with searchable titles
        Todo todo1 = Todo.builder()
                .title("Buy Groceries")
                .priority("MEDIUM")
                .build();

        Todo todo2 = Todo.builder()
                .title("Buy Books")
                .priority("MEDIUM")
                .build();

        Todo todo3 = Todo.builder()
                .title("Write Documentation")
                .priority("LOW")
                .build();

        // Create all todos
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo3)))
                .andExpect(status().isCreated());

        // Search for todos containing "Buy"
        mockMvc.perform(get("/api/todos/search/Buy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Search for todos containing "Write"
        mockMvc.perform(get("/api/todos/search/Write"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Write Documentation")));

        // Search for non-existent todo
        mockMvc.perform(get("/api/todos/search/NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("should handle concurrent operations")
    void testConcurrentOperations() throws Exception {
        // Create first todo
        Todo todo1 = Todo.builder()
                .title("Todo 1")
                .priority("HIGH")
                .build();

        MvcResult result1 = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo1)))
                .andExpect(status().isCreated())
                .andReturn();

        String body1 = result1.getResponse().getContentAsString();
        Todo savedTodo1 = objectMapper.readValue(body1, Todo.class);

        // Create second todo
        Todo todo2 = Todo.builder()
                .title("Todo 2")
                .priority("LOW")
                .build();

        MvcResult result2 = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo2)))
                .andExpect(status().isCreated())
                .andReturn();

        String body2 = result2.getResponse().getContentAsString();
        Todo savedTodo2 = objectMapper.readValue(body2, Todo.class);

        // Verify both todos exist
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Update todo1
        Todo updateTodo1 = Todo.builder()
                .title("Updated Todo 1")
                .priority("MEDIUM")
                .completed(false)
                .build();

        mockMvc.perform(put("/api/todos/" + savedTodo1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTodo1)))
                .andExpect(status().isOk());

        // Mark todo2 as completed
        mockMvc.perform(put("/api/todos/" + savedTodo2.getId() + "/complete"))
                .andExpect(status().isOk());

        // Verify both operations
        mockMvc.perform(get("/api/todos/" + savedTodo1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Todo 1")))
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        mockMvc.perform(get("/api/todos/" + savedTodo2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    @DisplayName("should validate health check endpoint")
    void testHealthCheckEndpoint() throws Exception {
        mockMvc.perform(get("/api/todos/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Todo App is running!"));
    }
}
