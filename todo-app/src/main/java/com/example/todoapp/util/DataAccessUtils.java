package com.example.todoapp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * VULNERABILITY SHOWCASE: Data access utilities with injection vulnerabilities
 * This class demonstrates SQL injection and NoSQL injection patterns for SAST education
 */
public class DataAccessUtils {

    private static Connection connection;

    /**
     * VULNERABILITY: SQL Injection via string concatenation
     * User input is directly concatenated into SQL query
     */
    public static ResultSet getUserByName(String username) throws Exception {
        String query = "SELECT * FROM users WHERE username = '" + username + "'";  // SQL Injection!
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * VULNERABILITY: SQL Injection with multiple parameters
     * Multiple user inputs concatenated without sanitization
     */
    public static ResultSet searchUsers(String firstName, String lastName, String email) throws Exception {
        String query = "SELECT * FROM users WHERE first_name = '" + firstName + 
                      "' AND last_name = '" + lastName + 
                      "' AND email = '" + email + "'";  // Multiple injection points!
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * VULNERABILITY: Dynamic query construction without parameterization
     * Column names taken from user input
     */
    public static ResultSet sortByColumn(String columnName) throws Exception {
        String query = "SELECT * FROM todos ORDER BY " + columnName;  // Column name injection!
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(query);
    }

    /**
     * VULNERABILITY: LDAP Injection
     * User input directly used in LDAP filter
     */
    public static String buildLdapFilter(String username) {
        return "(&(objectClass=user)(sAMAccountName=" + username + "))";  // LDAP Injection!
    }

    /**
     * VULNERABILITY: Command injection via string concatenation
     * User input directly used in system command
     */
    public static void generateReport(String filename) throws Exception {
        String cmd = "generate_report.sh " + filename;  // Command injection!
        Runtime.getRuntime().exec(cmd);
    }

    /**
     * VULNERABILITY: Path traversal with insufficient sanitization
     * Only removes single "../" occurrence
     */
    public static String sanitizePathWeakly(String userPath) {
        return userPath.replace("../", "");  // Insufficient - can be bypassed with "....//\"
    }

    /**
     * VULNERABILITY: Expression Language (EL) Injection
     * User input used in EL expression evaluation
     */
    public static String evaluateExpression(String userExpression) throws Exception {
        javax.el.ExpressionFactory factory = javax.el.ExpressionFactory.newInstance();
        javax.el.ELContext context = new org.apache.el.lang.EvaluationContext(factory, null);
        // User input directly evaluated as EL expression - injection vulnerability!
        javax.el.ValueExpression expr = factory.createValueExpression(context, userExpression, String.class);
        return (String) expr.getValue(context);
    }

    /**
     * VULNERABILITY: Template Injection
     * User input directly used in template
     */
    public static String renderTemplate(String templateContent, String userInput) throws Exception {
        // Using freemarker or similar template engine
        // User input injected into template - can execute arbitrary code
        String template = "<#assign x=userInput>" + templateContent;
        return template;  // This would be rendered by template engine
    }

    /**
     * VULNERABILITY: NoSQL Injection in query building
     * For MongoDB: building query string with concatenation
     */
    public static String buildMongoQuery(String userId) {
        // This demonstrates the pattern, though it uses Spring Data in the actual app
        String query = "{$where: \"this.user_id == '" + userId + "'\"}";  // NoSQL Injection!
        return query;
    }

    /**
     * VULNERABILITY: Type confusion / Insecure direct object reference
     * No validation of ID before using it to query
     */
    public static String getObjectByIdUnsafe(String id) {
        // User could pass any string value including object IDs they don't own
        String query = "SELECT data FROM objects WHERE id = '" + id + "'";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getString("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * VULNERABILITY: Unchecked return value
     * Query result not verified, could lead to incorrect behavior
     */
    public static void updateUserPassword(String userId, String newPassword) throws Exception {
        String query = "UPDATE users SET password = '" + newPassword + "' WHERE id = '" + userId + "'";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(query);  // Return value not checked - could fail silently
    }

    /**
     * VULNERABILITY: Race condition - TOCTOU (Time-of-check-time-of-use)
     * User exists check and update are separate operations
     */
    public static boolean createUserIfNotExists(String username) throws Exception {
        String checkQuery = "SELECT COUNT(*) as count FROM users WHERE username = '" + username + "'";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(checkQuery);
        
        // Between the check and the insert, another thread could create the same user
        rs.next();
        if (rs.getInt("count") == 0) {
            String insertQuery = "INSERT INTO users (username) VALUES ('" + username + "')";
            stmt.executeUpdate(insertQuery);
            return true;
        }
        return false;
    }
}
