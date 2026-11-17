/*
 * ============================================================
 * DBConnection.java — Database Connection (Singleton Pattern)
 * Package: connection
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Packages        → "package connection;" organises this file into the connection package
 * 2. Import          → We import java.sql classes needed for JDBC
 * 3. JDBC            → DriverManager.getConnection() connects Java to MySQL
 * 4. Exception Handling → try-catch handles connection errors gracefully
 * 5. Encapsulation   → private constructor (Singleton pattern) — only ONE connection exists
 * 6. Static methods  → getConnection() can be called without creating an object
 *
 * HOW JDBC WORKS (4 Steps):
 * -------------------------
 * Step 1: Load the MySQL JDBC Driver        → Class.forName("com.mysql.cj.jdbc.Driver")
 * Step 2: Define the connection URL          → "jdbc:mysql://localhost:3306/ipl_management"
 * Step 3: Open a connection                  → DriverManager.getConnection(url, user, password)
 * Step 4: Use the connection in DAO classes  → Pass it to Statement/PreparedStatement
 *
 * WHAT IS SINGLETON PATTERN?
 * --------------------------
 * We only want ONE database connection shared across the entire application.
 * The "instance" variable holds that single connection.
 * If it's null or closed, we create a new one; otherwise, we reuse it.
 */

package connection;

// java.sql package contains all JDBC classes
import java.sql.Connection;          // Represents a connection to the database
import java.sql.DriverManager;       // Manages JDBC drivers and creates connections
import java.sql.SQLException;        // Exception class for database errors

public class DBConnection {

    // ── Connection Configuration ──────────────────────────────
    // IMPORTANT: Change these values to match YOUR MySQL setup!
    private static final String URL      = "jdbc:mysql://localhost:3306/ipl_management";
    private static final String USER     = "root";        // Your MySQL username
    private static final String PASSWORD = "0928";        // Your MySQL password (change this!)

    // ── Singleton Instance ────────────────────────────────────
    // 'static' means this variable belongs to the CLASS, not to any object
    // 'private' means only this class can access it (Encapsulation)
    private static Connection instance = null;

    // ── Private Constructor (Singleton) ───────────────────────
    // 'private' constructor prevents other classes from doing "new DBConnection()"
    // This ensures only ONE connection is ever created
    private DBConnection() {
        // Cannot instantiate from outside
    }

    // ── Get the Connection ────────────────────────────────────
    // 'public static' means any class can call DBConnection.getConnection()
    // without creating a DBConnection object
    public static Connection getConnection() {
        try {
            // Check if connection doesn't exist OR is closed
            if (instance == null || instance.isClosed()) {

                // Step 1: Load the MySQL JDBC Driver
                // Class.forName() loads the driver class into memory
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Step 2 & 3: Create the connection using DriverManager
                // DriverManager.getConnection(URL, username, password) returns a Connection object
                instance = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            // This happens if mysql-connector-j JAR is not in the classpath
            System.err.println("❌ MySQL JDBC Driver not found!");
            System.err.println("   Make sure mysql-connector-j-x.x.x.jar is in your lib/ folder");
            System.err.println("   and added to your project's classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            // This happens if MySQL server is not running or credentials are wrong
            System.err.println("❌ Database connection failed!");
            System.err.println("   Check: 1) MySQL is running  2) Username/password correct  3) Database exists");
            e.printStackTrace();
        }
        return instance;
    }

    // ── Close the Connection ──────────────────────────────────
    // Always close the connection when the application exits
    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                System.out.println("🔒 Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
