package com.example.todoapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

// VULNERABILITY: Class is Serializable - enables deserialization attacks if used with untrusted data
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "todos")
public class Todo implements Serializable {
    // VULNERABILITY: serialVersionUID should be explicitly set for secure serialization
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String title;

    private String description;

    private boolean completed;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String priority; // LOW, MEDIUM, HIGH

    @Override
    public String toString() {
        return "Todo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", priority='" + priority + '\'' +
                '}';
    }
}
