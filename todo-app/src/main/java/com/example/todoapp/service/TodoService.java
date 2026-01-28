package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    /**
     * Create a new todo
     */
    public Todo createTodo(Todo todo) {
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        todo.setCompleted(false);
        return todoRepository.save(todo);
    }

    /**
     * Get all todos
     */
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    /**
     * Get todo by ID
     */
    public Optional<Todo> getTodoById(String id) {
        return todoRepository.findById(id);
    }

    /**
     * Update a todo
     */
    public Optional<Todo> updateTodo(String id, Todo todoDetails) {
        return todoRepository.findById(id).map(todo -> {
            if (todoDetails.getTitle() != null) {
                todo.setTitle(todoDetails.getTitle());
            }
            if (todoDetails.getDescription() != null) {
                todo.setDescription(todoDetails.getDescription());
            }
            if (todoDetails.getPriority() != null) {
                todo.setPriority(todoDetails.getPriority());
            }
            todo.setCompleted(todoDetails.isCompleted());
            todo.setUpdatedAt(LocalDateTime.now());
            return todoRepository.save(todo);
        });
    }

    /**
     * Delete a todo
     */
    public boolean deleteTodo(String id) {
        if (todoRepository.existsById(id)) {
            todoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get todos by completion status
     */
    public List<Todo> getTodosByStatus(boolean completed) {
        return todoRepository.findByCompleted(completed);
    }

    /**
     * Get todos by priority
     */
    public List<Todo> getTodosByPriority(String priority) {
        return todoRepository.findByPriority(priority);
    }

    /**
     * Search todos by title
     */
    public List<Todo> searchTodosByTitle(String title) {
        return todoRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Mark todo as completed
     */
    public Optional<Todo> markAsCompleted(String id) {
        return todoRepository.findById(id).map(todo -> {
            todo.setCompleted(true);
            todo.setUpdatedAt(LocalDateTime.now());
            return todoRepository.save(todo);
        });
    }

    /**
     * Mark todo as incomplete
     */
    public Optional<Todo> markAsIncomplete(String id) {
        return todoRepository.findById(id).map(todo -> {
            todo.setCompleted(false);
            todo.setUpdatedAt(LocalDateTime.now());
            return todoRepository.save(todo);
        });
    }
}
