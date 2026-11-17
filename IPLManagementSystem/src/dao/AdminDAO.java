/*
 * ============================================================
 * AdminDAO.java — Data Access Object for Admin authentication
 * Package: dao
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. JDBC            → Connection, PreparedStatement, ResultSet
 * 2. Exception Handling → try-catch-finally for database errors
 * 3. Packages        → import connection.DBConnection, import model.Admin
 * 4. DAO Pattern     → Separates database logic from GUI logic
 *
 * WHAT IS PreparedStatement?
 * ──────────────────────────
 * PreparedStatement is SAFER than Statement because it prevents SQL Injection.
 * Instead of: "SELECT * FROM admin WHERE username='" + user + "'"  (DANGEROUS!)
 * We use:     "SELECT * FROM admin WHERE username=? AND password=?"
 * The '?' are placeholders filled in with setString(1, value)
 *
 * WHAT IS ResultSet?
 * ──────────────────
 * ResultSet is like a cursor that points to rows returned by a SELECT query.
 * rs.next() moves to the next row. rs.getString("column_name") gets a value.
 */

package dao;

import connection.DBConnection;
import model.Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    // ── Login Authentication ──────────────────────────────────
    // Returns an Admin object if credentials are correct, null otherwise
    public Admin login(String username, String password) {
        // The SQL query with '?' placeholders (PreparedStatement)
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";

        try {
            // Step 1: Get database connection
            Connection conn = DBConnection.getConnection();

            // Step 2: Create PreparedStatement from the SQL
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Step 3: Fill in the '?' placeholders
            // setString(position, value) — positions start at 1, not 0
            pstmt.setString(1, username);   // First '?' → username
            pstmt.setString(2, password);   // Second '?' → password

            // Step 4: Execute the query — returns a ResultSet
            ResultSet rs = pstmt.executeQuery();

            // Step 5: Check if any row was returned
            if (rs.next()) {
                // Credentials are correct! Create Admin object from the result
                Admin admin = new Admin();
                admin.setAdminId(rs.getInt("admin_id"));         // Get integer column
                admin.setUsername(rs.getString("username"));      // Get string column
                admin.setPassword(rs.getString("password"));
                admin.setFullName(rs.getString("full_name"));
                admin.setEmail(rs.getString("email"));
                return admin;   // Login successful
            }

        } catch (SQLException e) {
            System.err.println("❌ Login query failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;   // Login failed — wrong username or password
    }

    // ── Update Admin Password ─────────────────────────────────
    public boolean updatePassword(int adminId, String newPassword) {
        String sql = "UPDATE admin SET password = ? WHERE admin_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setInt(2, adminId);

            // executeUpdate() returns the NUMBER of rows affected
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;   // true if at least one row was updated

        } catch (SQLException e) {
            System.err.println("❌ Password update failed: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
