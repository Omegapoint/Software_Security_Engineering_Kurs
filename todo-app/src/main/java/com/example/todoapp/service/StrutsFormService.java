package com.example.todoapp.service;

import org.springframework.stereotype.Service;

/**
 * Service demonstrating usage of Struts 2.3.15.1 (CVE-2017-5638).
 * 
 * CVE-2017-5638: Remote Code Execution via OGNL injection
 * VULNERABILITY: Dynamic OGNL evaluation of user input
 * 
 * 
 */
@Service
public class StrutsFormService {

    /**
     * 
     * @param userName User input 
     * @param email User input
     * @return Processed result
     */
    public String processFormData(String userName, String email) {
        String processedName = sanitizeInput(userName);
        String processedEmail = sanitizeInput(email);
        
        return String.format("User: %s, Email: %s", processedName, processedEmail);
    }

    /**
     * 
     * @param input User input
     * @return Sanitized input
     */
    private String sanitizeInput(String input) {
        if (input == null || input.isEmpty()) {
            return "N/A";
        }
        return input.trim().substring(0, Math.min(input.length(), 100));
    }

    /**
     * 
     * @return Static result
     */
    public String getStaticConfiguration() {
        String appName = "TodoApp";
        String version = "1.0.0";
        return String.format("%s v%s", appName, version);
    }
}
