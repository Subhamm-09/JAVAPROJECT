/*
 * ============================================================
 * Admin.java — Admin model class (INHERITANCE demo)
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Inheritance     → "extends User" — Admin INHERITS all fields/methods from User
 * 2. 'super' keyword → Calls the PARENT class (User) constructor
 * 3. Polymorphism    → We OVERRIDE toString() — same method name, different behavior
 * 4. Encapsulation   → adminId and email are 'private'
 * 5. OOP             → Admin IS-A User (inheritance relationship)
 *
 * WHAT IS INHERITANCE?
 * --------------------
 * "extends" keyword means Admin inherits from User.
 * Admin automatically gets: username, password, fullName, and their getters/setters.
 * Admin can ALSO have its own unique fields: adminId, email.
 *
 * WHAT IS 'super'?
 * ----------------
 * 'super(username, password, fullName)' calls the User class constructor.
 * This initializes the inherited fields. You MUST call super() first in the constructor.
 */

package model;

public class Admin extends User {       // 'extends' = INHERITANCE

    // ── Admin-specific fields ─────────────────────────────────
    // These fields exist ONLY in Admin, not in the parent User class
    private int adminId;
    private String email;

    // ── Default Constructor ───────────────────────────────────
    public Admin() {
        super();    // Calls User() — the parent's default constructor
    }

    // ── Parameterised Constructor ─────────────────────────────
    // Takes all fields: parent's fields (username, password, fullName) + Admin's own (adminId, email)
    public Admin(int adminId, String username, String password, String fullName, String email) {
        super(username, password, fullName);   // 'super' passes values to User's constructor
        this.adminId = adminId;                // Initialize Admin-specific fields
        this.email = email;
    }

    // ── Getters and Setters for Admin-specific fields ─────────
    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ── toString() Override (POLYMORPHISM) ─────────────────────
    // This OVERRIDES User's toString() method.
    // Polymorphism: Same method name "toString()" but DIFFERENT behavior in Admin vs User.
    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + getUsername() + '\'' +   // getUsername() inherited from User
                ", fullName='" + getFullName() + '\'' +   // getFullName() inherited from User
                ", email='" + email + '\'' +
                '}';
    }
}
