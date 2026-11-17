/*
 * ============================================================
 * User.java — Base class for all users (INHERITANCE demo)
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Abstraction     → This class is NOT abstract itself, but serves as a BASE class
 * 2. Inheritance     → Admin class will EXTEND this class using "extends User"
 * 3. Encapsulation   → All fields are 'private' (data hiding)
 *                      Access only through 'getters' and 'setters' (public methods)
 * 4. OOP             → This is a "class" — a blueprint for creating User objects
 * 5. Constructor     → Special method called when "new User()" is used
 *
 * WHY USE A BASE CLASS?
 * ---------------------
 * Both Admin and (future) regular User share common fields like username and password.
 * Instead of repeating code, we put shared fields HERE and let subclasses INHERIT them.
 * This is the DRY principle: Don't Repeat Yourself.
 *
 * WHAT IS ENCAPSULATION?
 * ----------------------
 * - Fields are 'private' → No other class can directly access them
 * - We provide 'public' getter/setter methods → Controlled access
 * - Example: private String username; → Only accessible via getUsername() and setUsername()
 * - This PROTECTS the data from accidental or unauthorized changes
 */

package model;

public class User {

    // ── Private Fields (Encapsulation) ────────────────────────
    // 'private' keyword means these variables can ONLY be accessed
    // from inside this User class. Other classes MUST use getters/setters.
    private String username;
    private String password;
    private String fullName;

    // ── Default Constructor ───────────────────────────────────
    // Called when you write: User u = new User();
    // Creates an empty User object (fields will be null)
    public User() {
    }

    // ── Parameterised Constructor ─────────────────────────────
    // Called when you write: User u = new User("admin", "pass123", "Admin");
    // 'this' keyword refers to the CURRENT object's fields
    // It differentiates between the parameter "username" and the field "this.username"
    public User(String username, String password, String fullName) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }

    // ── Getters and Setters (Encapsulation) ───────────────────
    // Getter: Returns the value of a private field
    // Setter: Sets/changes the value of a private field

    public String getUsername() {
        return username;                // Returns the private username field
    }

    public void setUsername(String username) {
        this.username = username;       // Sets the private username field
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ── toString() Override (Polymorphism) ─────────────────────
    // Every class in Java inherits from Object class, which has a toString() method.
    // We OVERRIDE it to give a meaningful string representation.
    // @Override annotation tells the compiler we're intentionally replacing the parent method.
    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                '}';
    }
}
