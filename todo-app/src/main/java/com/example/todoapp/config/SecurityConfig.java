package com.example.todoapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * VULNERABILITY SHOWCASE: Configuration class with multiple security vulnerabilities
 * for SAST scanning education
 */
@Configuration
public class SecurityConfig {

    /**
     * VULNERABILITY: Weak CORS Configuration
     * Allows all origins and all methods without restrictions
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Collections.singletonList("*"));  // Allow all origins - vulnerable!
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Allow all methods
        configuration.setAllowedHeaders(java.util.Collections.singletonList("*"));  // Allow all headers
        configuration.setAllowCredentials(true);  // VULNERABILITY: Allows credentials with * origins - combined vulnerability!
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * VULNERABILITY: Using NoOpPasswordEncoder - passwords are not encoded at all!
     * This completely disables password encryption
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();  // Passwords stored in plain text!
    }

    /**
     * VULNERABILITY: Hardcoded configuration values
     * API keys and secrets hardcoded in configuration class
     */
    public static final String ADMIN_PASSWORD = "admin";
    public static final String SECRET_KEY = "my-secret-key-12345";
    public static final String API_TOKEN = "token_abc123xyz";
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/todos";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "toor";

    /**
     * VULNERABILITY: Debug mode enabled with sensitive information exposure
     */
    public static final boolean DEBUG_MODE = true;
    public static final boolean ENABLE_DETAILED_ERRORS = true;
    public static final boolean LOG_REQUESTS = true;
    public static final boolean LOG_RESPONSES = true;

    /**
     * VULNERABILITY: Weak SSL/TLS configuration
     * This would disable certificate validation in a real scenario
     */
    public static void disableSslVerification() throws Exception {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);  // Accepts any hostname!
    }

    /**
     * VULNERABILITY: Empty or weak authentication method
     * This would be used with no actual authentication
     */
    public String getDefaultUsername() {
        return "admin";  // Default credentials hardcoded
    }

    public String getDefaultPassword() {
        return "password";  // Weak default password
    }
}
