/*
 * ============================================================
 * Team.java — Team model class
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. OOP             → Class and Object — Team is a blueprint for team objects
 * 2. Encapsulation   → All fields are 'private', accessed through getters/setters
 * 3. Constructor Overloading (Polymorphism) → Two constructors with different parameters
 *
 * WHAT IS A MODEL CLASS?
 * ----------------------
 * A model class represents a TABLE from the database as a Java object.
 * Each field in this class corresponds to a COLUMN in the 'teams' table.
 * This is part of the MVC (Model-View-Controller) architecture.
 *
 * Database Table:                  Java Class:
 * ┌─────────────┐                 ┌─────────────┐
 * │ teams       │                 │ Team.java   │
 * ├─────────────┤                 ├─────────────┤
 * │ team_id     │  ←──────────→   │ teamId      │
 * │ team_name   │  ←──────────→   │ teamName    │
 * │ city        │  ←──────────→   │ city        │
 * │ coach       │  ←──────────→   │ coach       │
 * │ captain     │  ←──────────→   │ captain     │
 * │ founded_year│  ←──────────→   │ foundedYear │
 * └─────────────┘                 └─────────────┘
 */

package model;

public class Team {

    // ── Private fields (mapped to database columns) ───────────
    private int teamId;
    private String teamName;
    private String city;
    private String coach;
    private String captain;
    private int foundedYear;

    // ── Default Constructor ───────────────────────────────────
    // Constructor Overloading: Same name "Team", no parameters
    public Team() {
    }

    // ── Parameterised Constructor (without ID) ────────────────
    // Used when INSERTING a new team (database generates the ID via AUTO_INCREMENT)
    public Team(String teamName, String city, String coach, String captain, int foundedYear) {
        this.teamName = teamName;
        this.city = city;
        this.coach = coach;
        this.captain = captain;
        this.foundedYear = foundedYear;
    }

    // ── Full Constructor (with ID) ────────────────────────────
    // Constructor Overloading: Same name "Team", different parameters
    // Used when READING from database (we already have the ID)
    public Team(int teamId, String teamName, String city, String coach, String captain, int foundedYear) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.city = city;
        this.coach = coach;
        this.captain = captain;
        this.foundedYear = foundedYear;
    }

    // ── Getters and Setters ───────────────────────────────────
    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getCaptain() {
        return captain;
    }

    public void setCaptain(String captain) {
        this.captain = captain;
    }

    public int getFoundedYear() {
        return foundedYear;
    }

    public void setFoundedYear(int foundedYear) {
        this.foundedYear = foundedYear;
    }

    // ── toString() ────────────────────────────────────────────
    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", teamName='" + teamName + '\'' +
                ", city='" + city + '\'' +
                ", captain='" + captain + '\'' +
                '}';
    }
}
