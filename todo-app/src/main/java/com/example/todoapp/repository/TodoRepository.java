package com.example.todoapp.repository;

import com.example.todoapp.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends MongoRepository<Todo, String> {
    List<Todo> findByCompleted(boolean completed);

    List<Todo> findByPriority(String priority);

    List<Todo> findByTitleContainingIgnoreCase(String title);
}
