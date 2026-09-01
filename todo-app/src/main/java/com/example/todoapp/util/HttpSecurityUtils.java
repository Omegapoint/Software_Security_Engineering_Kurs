package com.example.todoapp.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * VULNERABILITY SHOWCASE: HTTP Response and Authentication utilities
 * with multiple security vulnerabilities for SAST education
 */
public class HttpSecurityUtils {

    /**
     * VULNERABILITY: Missing Security Headers
     * Response doesn't include security headers like X-Frame-Options, CSP, etc.
     */
    public static ResponseEntity<String> createResponseWithoutSecurityHeaders(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "text/html");
        // Missing: X-Frame-Options, X-Content-Type-Options, Strict-Transport-Security,
        // Content-Security-Policy, X-XSS-Protection, etc.
        return ResponseEntity.ok().headers(headers).body(content);
    }

    /**
     * VULNERABILITY: Weak authentication - comparing strings with ==
     * String comparison with == is object reference comparison, not value comparison
     */
    public static boolean authenticateUserWeak(String username, String password) {
        String storedUsername = "admin";
        String storedPassword = "password";
        return username == storedUsername && password == storedPassword;  // Wrong comparison method!
    }

    /**
     * VULNERABILITY: Password stored in plain text in memory
     * Password variable stays in memory and logs
     */
    public static void loginWithPlaintextPassword(String username, String password) {
        String logMessage = "Login attempt for user: " + username + " with password: " + password;
        System.out.println(logMessage);  // Password logged in plain text!
        
        // Password variable not cleared after use
        if (authenticateUserWeak(username, password)) {
            System.out.println("Login successful");
        }
    }

    /**
     * VULNERABILITY: Weak session ID generation
     * Using timestamp + random number (predictable)
     */
    public static String generateSessionId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return timestamp + "-" + random;  // Predictable!
    }

    /**
     * VULNERABILITY: Missing input validation on JWT
     * JWT signature not verified
     */
    public static String parseJwtWithoutValidation(String token) {
        // Splits JWT without verifying signature
        String[] parts = token.split("\\.");
        if (parts.length == 3) {
            try {
                String payload = new String(java.util.Base64.getDecoder().decode(parts[1]));
                return payload;  // No signature verification!
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * VULNERABILITY: Open Redirect vulnerability
     * User-controlled URL parameter used in Location header
     */
    public static ResponseEntity<Void> redirectToUserUrl(String redirectUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location", redirectUrl);  // No validation - open redirect!
        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * VULNERABILITY: HTTP Response Splitting
     * User input used in HTTP headers without validation
     */
    public static ResponseEntity<String> setCustomHeader(String headerName, String headerValue) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(headerName, headerValue);  // No validation - CRLF injection possible!
        return ResponseEntity.ok().headers(headers).body("OK");
    }

    /**
     * VULNERABILITY: Information Disclosure via Stack Trace
     * Full stack trace returned to client
     */
    public static ResponseEntity<String> handleErrorUnsafe(Exception e) {
        StringBuilder errorResponse = new StringBuilder();
        errorResponse.append("Error occurred: ").append(e.getMessage()).append("\\n");
        for (StackTraceElement element : e.getStackTrace()) {
            errorResponse.append(element.toString()).append("\\n");  // Full stack trace exposed!
        }
        return ResponseEntity.status(500).body(errorResponse.toString());
    }

    /**
     * VULNERABILITY: Sensitive data in URL parameters
     * Passwords and tokens passed in URL instead of request body
     */
    public static String buildLoginUrl(String username, String password) {
        return "/login?username=" + username + "&password=" + password;  // Credentials in URL!
    }

    /**
     * VULNERABILITY: Weak token validation
     * Token only checked for non-null, not for validity
     */
    public static boolean isTokenValid(String token) {
        return token != null && token.length() > 0;  // Insufficient validation!
    }

    /**
     * VULNERABILITY: Hardcoded secret key visible in code
     * Used for HMAC operations
     */
    public static String computeHmac(String message) throws Exception {
        String secretKey = "this_is_a_weak_secret_key_12345";  // Hardcoded!
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        javax.crypto.spec.SecretKeySpec secretKeySpec = 
            new javax.crypto.spec.SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(message.getBytes());
        return javax.xml.bind.DatatypeConverter.printHexBinary(hmacBytes);
    }

    /**
     * VULNERABILITY: Broken access control - no authorization check
     * User can access any resource by guessing IDs
     */
    public static ResponseEntity<String> getUserData(String userId) {
        // No check if current user is authorized to view this userId
        String userData = "SELECT * FROM users WHERE id = '" + userId + "'";  // Also SQL injection!
        return ResponseEntity.ok(userData);
    }

    /**
     * VULNERABILITY: Weak randomness for generating tokens
     */
    public static String generateWeakToken() {
        java.util.Random random = new java.util.Random();
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            token.append(String.format("%02x", random.nextInt(256)));
        }
        return token.toString();  // java.util.Random is not cryptographically secure!
    }

    /**
     * VULNERABILITY: Code comment containing password
     * Developer left password in comment (code smell)
     */
    // TODO: Use database credentials: username=admin, password=SuperSecret123!
    public static void connectToDatabase() {
        // Connection code here
    }

    /**
     * VULNERABILITY: Unsafe file upload handling
     * No validation of file type or content
     */
    public static void saveUploadedFile(String filename, byte[] content) throws Exception {
        String uploadDir = "/var/uploads/";
        String filePath = uploadDir + filename;  // No sanitization - can traverse directories!
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), content);
    }
}
