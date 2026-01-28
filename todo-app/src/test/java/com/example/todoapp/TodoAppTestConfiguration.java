package com.example.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Test configuration for the Todo App
 */
@TestConfiguration
public class TodoAppTestConfiguration {

    /**
     * Main method to run tests (if needed)
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoAppApplication.class, args);
    }

}
