/*
 * ============================================================
 * TeamDAO.java — Full CRUD operations for Teams
 * Package: dao
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Interface Implementation → "implements ManagementOperations<Team>"
 * 2. JDBC CRUD               → INSERT, SELECT, UPDATE, DELETE
 * 3. Collections              → ArrayList to store multiple Team objects
 * 4. PreparedStatement        → Safe parameterised queries
 * 5. Exception Handling       → try-catch for database errors
 *
 * CRUD OPERATIONS EXPLAINED:
 * ──────────────────────────
 * C = Create → INSERT INTO teams (...) VALUES (...)
 * R = Read   → SELECT * FROM teams
 * U = Update → UPDATE teams SET ... WHERE team_id = ?
 * D = Delete → DELETE FROM teams WHERE team_id = ?
 *
 * This class IMPLEMENTS the ManagementOperations interface,
 * so it MUST provide bodies for: add(), getAll(), update(), delete(), getById()
 */

package dao;

import connection.DBConnection;
import model.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO implements ManagementOperations<Team> {

    // ── CREATE: Add a new team ────────────────────────────────
    @Override
    public boolean add(Team team) {
        String sql = "INSERT INTO teams (team_name, city, coach, captain, founded_year) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set each '?' with the Team object's values
            pstmt.setString(1, team.getTeamName());
            pstmt.setString(2, team.getCity());
            pstmt.setString(3, team.getCoach());
            pstmt.setString(4, team.getCaptain());
            pstmt.setInt(5, team.getFoundedYear());

            int rowsAffected = pstmt.executeUpdate();  // executeUpdate for INSERT/UPDATE/DELETE
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to add team: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ALL: Get all teams ───────────────────────────────
    @Override
    public List<Team> getAll() {
        // ArrayList is a Collection class — dynamic array that can grow/shrink
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM teams ORDER BY team_name";  // ORDER BY sorts alphabetically

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();       // Simple Statement (no parameters)
            ResultSet rs = stmt.executeQuery(sql);         // executeQuery for SELECT

            // rs.next() moves the cursor to the next row
            // Loop continues until there are no more rows
            while (rs.next()) {
                Team team = new Team(
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    rs.getString("city"),
                    rs.getString("coach"),
                    rs.getString("captain"),
                    rs.getInt("founded_year")
                );
                teams.add(team);   // Add each team to the ArrayList
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch teams: " + e.getMessage());
            e.printStackTrace();
        }
        return teams;
    }

    // ── READ BY ID: Get a single team ─────────────────────────
    @Override
    public Team getById(int id) {
        String sql = "SELECT * FROM teams WHERE team_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Team(
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    rs.getString("city"),
                    rs.getString("coach"),
                    rs.getString("captain"),
                    rs.getInt("founded_year")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch team by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── UPDATE: Modify an existing team ───────────────────────
    @Override
    public boolean update(Team team) {
        String sql = "UPDATE teams SET team_name=?, city=?, coach=?, captain=?, founded_year=? WHERE team_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, team.getTeamName());
            pstmt.setString(2, team.getCity());
            pstmt.setString(3, team.getCoach());
            pstmt.setString(4, team.getCaptain());
            pstmt.setInt(5, team.getFoundedYear());
            pstmt.setInt(6, team.getTeamId());   // WHERE clause — which team to update

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update team: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE: Remove a team ─────────────────────────────────
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM teams WHERE team_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to delete team: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── EXTRA: Get team name by ID (for display purposes) ─────
    public String getTeamNameById(int teamId) {
        Team team = getById(teamId);
        return (team != null) ? team.getTeamName() : "Unknown";
    }
}
