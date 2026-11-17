/*
 * ============================================================
 * PlayerDAO.java — Full CRUD for Players with JOIN queries
 * Package: dao
 * ============================================================
 *
 * JAVA CONCEPTS: Interface implementation, JDBC, ArrayList,
 *                JOIN queries, FOREIGN KEY relationships
 *
 * SPECIAL SQL USED:
 * - JOIN: Combines players and teams tables to show team names
 * - WHERE: Filters players by team
 * - COUNT(), SUM(): Aggregate functions for statistics
 */

package dao;

import connection.DBConnection;
import model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO implements ManagementOperations<Player> {

    // ── CREATE ────────────────────────────────────────────────
    @Override
    public boolean add(Player player) {
        String sql = "INSERT INTO players (player_name, team_id, role, age, " +
                     "matches_played, runs_scored, wickets_taken, batting_avg, bowling_avg) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, player.getPlayerName());
            pstmt.setInt(2, player.getTeamId());
            pstmt.setString(3, player.getRole());
            pstmt.setInt(4, player.getAge());
            pstmt.setInt(5, player.getMatchesPlayed());
            pstmt.setInt(6, player.getRunsScored());
            pstmt.setInt(7, player.getWicketsTaken());
            pstmt.setDouble(8, player.getBattingAvg());
            pstmt.setDouble(9, player.getBowlingAvg());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to add player: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── READ ALL ──────────────────────────────────────────────
    @Override
    public List<Player> getAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY player_name";

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                players.add(extractPlayer(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch players: " + e.getMessage());
            e.printStackTrace();
        }
        return players;
    }

    // ── READ BY ID ────────────────────────────────────────────
    @Override
    public Player getById(int id) {
        String sql = "SELECT * FROM players WHERE player_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPlayer(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch player: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ── READ BY TEAM ID (uses WHERE clause) ───────────────────
    // Gets all players belonging to a specific team
    public List<Player> getByTeamId(int teamId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE team_id = ? ORDER BY player_name";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                players.add(extractPlayer(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch players by team: " + e.getMessage());
            e.printStackTrace();
        }
        return players;
    }

    // ── UPDATE ────────────────────────────────────────────────
    @Override
    public boolean update(Player player) {
        String sql = "UPDATE players SET player_name=?, team_id=?, role=?, age=?, " +
                     "matches_played=?, runs_scored=?, wickets_taken=?, batting_avg=?, bowling_avg=? " +
                     "WHERE player_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, player.getPlayerName());
            pstmt.setInt(2, player.getTeamId());
            pstmt.setString(3, player.getRole());
            pstmt.setInt(4, player.getAge());
            pstmt.setInt(5, player.getMatchesPlayed());
            pstmt.setInt(6, player.getRunsScored());
            pstmt.setInt(7, player.getWicketsTaken());
            pstmt.setDouble(8, player.getBattingAvg());
            pstmt.setDouble(9, player.getBowlingAvg());
            pstmt.setInt(10, player.getPlayerId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update player: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── DELETE ────────────────────────────────────────────────
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM players WHERE player_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to delete player: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── HELPER: Extract a Player from ResultSet ───────────────
    // Avoids code duplication — used by getAll(), getById(), getByTeamId()
    private Player extractPlayer(ResultSet rs) throws SQLException {
        return new Player(
            rs.getInt("player_id"),
            rs.getString("player_name"),
            rs.getInt("team_id"),
            rs.getString("role"),
            rs.getInt("age"),
            rs.getInt("matches_played"),
            rs.getInt("runs_scored"),
            rs.getInt("wickets_taken"),
            rs.getDouble("batting_avg"),
            rs.getDouble("bowling_avg")
        );
    }

    // ── STATISTICS: Get top run scorers (uses ORDER BY, LIMIT) ─
    public List<Player> getTopRunScorers(int limit) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY runs_scored DESC LIMIT ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                players.add(extractPlayer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }

    // ── STATISTICS: Get top wicket takers ──────────────────────
    public List<Player> getTopWicketTakers(int limit) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY wickets_taken DESC LIMIT ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                players.add(extractPlayer(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return players;
    }
}
