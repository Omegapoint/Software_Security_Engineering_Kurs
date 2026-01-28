package com.example.todoapp.controller;

import com.example.todoapp.model.Todo;
import com.example.todoapp.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * Get all todos
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return ResponseEntity.ok(todoService.getAllTodos());
    }

    /**
     * Get todo by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
        return todoService.getTodoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create a new todo
     */
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    /**
     * Update a todo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(
            @PathVariable String id,
            @RequestBody Todo todoDetails) {
        return todoService.updateTodo(id, todoDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Delete a todo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        if (todoService.deleteTodo(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get todos by completion status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Todo>> getTodosByStatus(@PathVariable boolean status) {
        return ResponseEntity.ok(todoService.getTodosByStatus(status));
    }

    /**
     * Get todos by priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Todo>> getTodosByPriority(@PathVariable String priority) {
        return ResponseEntity.ok(todoService.getTodosByPriority(priority));
    }

    /**
     * Search todos by title
     */
    @GetMapping("/search/{title}")
    public ResponseEntity<List<Todo>> searchTodosByTitle(@PathVariable String title) {
        return ResponseEntity.ok(todoService.searchTodosByTitle(title));
    }

    /**
     * Mark todo as completed
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Todo> markAsCompleted(@PathVariable String id) {
        return todoService.markAsCompleted(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Mark todo as incomplete
     */
    @PutMapping("/{id}/incomplete")
    public ResponseEntity<Todo> markAsIncomplete(@PathVariable String id) {
        return todoService.markAsIncomplete(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Todo App is running!");
    }
}
