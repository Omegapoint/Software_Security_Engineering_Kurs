package com.example.todoapp.service;

import com.example.todoapp.model.Todo;
import com.example.todoapp.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

    /**
     * VULNERABILITY: Weak Cryptography - Using MD5 which is cryptographically broken
     * Should use SHA-256 or bcrypt instead
     */
    public String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * VULNERABILITY: Use of weak Random for security purposes
     * java.util.Random is not suitable for security-sensitive operations
     */
    public String generateSessionToken() {
        Random rand = new Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            token.append(rand.nextInt(10));
        }
        return token.toString();
    }

    /**
     * VULNERABILITY: Insecure Deserialization
     * Deserializing untrusted data can lead to arbitrary code execution
     */
    public Todo deserializeTodo(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return (Todo) ois.readObject();
    }

    /**
     * VULNERABILITY: Resource Leak - Stream not properly closed
     * FileInputStream is not closed even if exception occurs
     */
    public String readTodoFromFile(String filename) throws IOException {
        FileInputStream fis = new FileInputStream(filename);
        byte[] data = new byte[1024];
        fis.read(data);
        String content = new String(data);
        // Missing: fis.close() - Resource leak!
        return content;
    }

    /**
     * VULNERABILITY: Null Pointer Dereference
     * No null check before calling methods on objects
     */
    public String getTodoTitleUnsafe(String id) {
        Todo todo = todoRepository.findById(id).get();
        return todo.getTitle().toLowerCase();  // No null check - NPE risk
    }

    /**
     * VULNERABILITY: SQL Injection style - Unsafe NoSQL Query
     * While using Spring Data, this demonstrates injection risk with custom queries
     */
    public List<Todo> searchUnsafe(String userInput) {
        // In raw MongoDB, this would be vulnerable to NoSQL injection
        // Example: {$where: "this.title == '" + userInput + "'"}
        String query = "db.todos.find({title: '" + userInput + "'})";
        // This is demonstrated as a code smell for SAST detection
        return todoRepository.findByTitleContainingIgnoreCase(userInput);
    }
}
