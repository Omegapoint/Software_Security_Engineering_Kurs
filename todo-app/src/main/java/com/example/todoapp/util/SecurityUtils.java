package com.example.todoapp.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * VULNERABILITY SHOWCASE: This utility class contains multiple security vulnerabilities
 * intended for SAST scanning education purposes
 */
public class SecurityUtils {

    // VULNERABILITY: Hardcoded credentials
    private static final String DATABASE_PASSWORD = "admin123";
    private static final String API_KEY = "sk_test_4eC39HqLyjWDarhtT657G51C";
    private static final String ENCRYPTION_KEY = "0123456789abcdef";  // Hardcoded encryption key

    /**
     * VULNERABILITY: Weak Encryption using Base64 instead of real encryption
     * Base64 is encoding, not encryption - anyone can decode it
     */
    public static String encryptData(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * VULNERABILITY: Weak Encryption - Using simple XOR cipher
     * XOR is not a secure encryption method
     */
    public static String weakEncrypt(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            result.append((char) (input.charAt(i) ^ 'K'));  // Simple XOR encryption
        }
        return result.toString();
    }

    /**
     * VULNERABILITY: Command Injection
     * User input is directly concatenated and executed
     */
    public static String executeSystemCommand(String userCommand) throws Exception {
        String[] cmd = {"/bin/bash", "-c", "echo " + userCommand};  // Vulnerable to injection
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        return output.toString();
    }

    /**
     * VULNERABILITY: Path Traversal
     * File path is constructed from user input without validation
     */
    public static String readUserFile(String userPath) throws Exception {
        String baseDir = "/var/www/uploads/";
        String fullPath = baseDir + userPath;  // No sanitization - can traverse with ../
        return new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(fullPath)));
    }

    /**
     * VULNERABILITY: XXE (XML External Entity) Injection
     * Parsing XML without disabling external entity processing
     */
    public static void parseXmlUnsafe(String xmlContent) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory dbf = 
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        // XXE vulnerability - external entity processing is enabled
        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(
                new java.io.ByteArrayInputStream(xmlContent.getBytes()));
    }

    /**
     * VULNERABILITY: Unsafe Object Deserialization
     * Deserializing untrusted data without validation
     */
    @SuppressWarnings("unchecked")
    public static Object unsafeDeserialize(byte[] data) throws Exception {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
        return ois.readObject();  // Gadget chain attack possible
    }

    /**
     * VULNERABILITY: SQL Injection (String concatenation in query)
     * User input directly concatenated into SQL/query string
     */
    public static String buildQueryUnsafe(String userId) {
        String query = "SELECT * FROM users WHERE id = " + userId;  // Direct concatenation
        return query;
    }

    /**
     * VULNERABILITY: Use of weak randomness for security
     * java.util.Random is predictable and not cryptographically secure
     */
    public static String generateWeakSessionId() {
        java.util.Random random = new java.util.Random();
        return String.valueOf(System.currentTimeMillis()) + random.nextInt(10000);
    }

    /**
     * VULNERABILITY: Sensitive data in logs
     * Password and sensitive information is logged
     */
    public static void loginUser(String username, String password) {
        System.out.println("User logging in: " + username + " with password: " + password);  // Logs password!
        System.out.println("API Key: " + API_KEY);  // Logs sensitive API key!
    }

    /**
     * VULNERABILITY: Missing input validation
     * No validation before using user input
     */
    public static void processUserInput(String input) {
        if (input.length() > 0) {  // Only checks length, not content
            String[] parts = input.split("\\|");
            String command = parts[0];
            String arg = parts[1];
            // Executes command without any validation of content
            try {
                Runtime.getRuntime().exec(new String[]{command, arg});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * VULNERABILITY: Use of constant encryption key hardcoded
     * Should be loaded from secure configuration
     */
    public static byte[] getEncryptionKey() {
        return ENCRYPTION_KEY.getBytes();  // Hardcoded key
    }

    /**
     * VULNERABILITY: Possible null pointer dereference
     * No null check before using parameter
     */
    public static int processData(String data) {
        return data.length();  // Will throw NPE if data is null
    }

    /**
     * VULNERABILITY: Resource leak - Stream not closed
     */
    public static byte[] readFileUnsafe(String filePath) throws Exception {
        java.io.FileInputStream fis = new java.io.FileInputStream(filePath);
        byte[] data = new byte[1024];
        fis.read(data);
        // Missing fis.close() - Resource leak!
        return data;
    }
}
