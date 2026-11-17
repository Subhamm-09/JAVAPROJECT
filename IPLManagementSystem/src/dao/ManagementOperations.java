/*
 * ============================================================
 * ManagementOperations.java — Interface (ABSTRACTION demo)
 * Package: dao
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Abstraction    → An interface defines WHAT to do, not HOW to do it
 * 2. Interface      → Uses 'interface' keyword instead of 'class'
 * 3. Polymorphism   → Multiple DAO classes implement this differently
 *
 * WHAT IS AN INTERFACE?
 * ---------------------
 * An interface is a CONTRACT. It says:
 *   "Any class that 'implements' me MUST provide these methods."
 *
 * - All methods in an interface are abstract by default (no body)
 * - Classes use 'implements' keyword to implement an interface
 * - A class CAN implement multiple interfaces (unlike extends, which allows only one)
 *
 * WHY USE AN INTERFACE?
 * ---------------------
 * TeamDAO, PlayerDAO, MatchDAO all need similar CRUD operations.
 * The interface ensures they ALL have add(), update(), delete(), getAll() methods.
 * This is the DAO (Data Access Object) pattern — standardised database operations.
 *
 * <T> is a GENERIC TYPE — it's replaced with Team, Player, Match, etc.
 * when a class implements this interface.
 */

package dao;

import java.util.List;

public interface ManagementOperations<T> {

    // C — CREATE: Add a new record to the database
    boolean add(T item);

    // R — READ: Get all records from the database
    List<T> getAll();

    // U — UPDATE: Modify an existing record
    boolean update(T item);

    // D — DELETE: Remove a record by its ID
    boolean delete(int id);

    // READ by ID: Get a single record
    T getById(int id);
}
