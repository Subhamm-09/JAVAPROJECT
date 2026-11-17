/*
 * ============================================================
 * MatchDAO.java — CRUD for Matches
 * Package: dao
 * ============================================================
 *
 * SQL: INSERT, SELECT with JOIN, UPDATE, DELETE, ORDER BY
 */

package dao;

import connection.DBConnection;
import model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchDAO implements ManagementOperations<Match> {

    @Override
    public boolean add(Match match) {
        String sql = "INSERT INTO matches (team1_id, team2_id, match_date, venue, status) VALUES (?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, match.getTeam1Id());
            pstmt.setInt(2, match.getTeam2Id());
            pstmt.setString(3, match.getMatchDate());
            pstmt.setString(4, match.getVenue());
            pstmt.setString(5, match.getStatus() != null ? match.getStatus() : "Scheduled");

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to add match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Match> getAll() {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches ORDER BY match_date DESC";

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                matches.add(extractMatch(rs));
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch matches: " + e.getMessage());
            e.printStackTrace();
        }
        return matches;
    }

    @Override
    public Match getById(int id) {
        String sql = "SELECT * FROM matches WHERE match_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractMatch(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch match: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(Match match) {
        String sql = "UPDATE matches SET team1_id=?, team2_id=?, match_date=?, venue=?, status=?, winner_team_id=? WHERE match_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, match.getTeam1Id());
            pstmt.setInt(2, match.getTeam2Id());
            pstmt.setString(3, match.getMatchDate());
            pstmt.setString(4, match.getVenue());
            pstmt.setString(5, match.getStatus());
            if (match.getWinnerTeamId() > 0) {
                pstmt.setInt(6, match.getWinnerTeamId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setInt(7, match.getMatchId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to update match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM matches WHERE match_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to delete match: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ── Get matches by status ─────────────────────────────────
    public List<Match> getByStatus(String status) {
        List<Match> matches = new ArrayList<>();
        String sql = "SELECT * FROM matches WHERE status = ? ORDER BY match_date";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                matches.add(extractMatch(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matches;
    }

    // ── Helper: Extract Match from ResultSet ──────────────────
    private Match extractMatch(ResultSet rs) throws SQLException {
        return new Match(
            rs.getInt("match_id"),
            rs.getInt("team1_id"),
            rs.getInt("team2_id"),
            rs.getString("match_date"),
            rs.getString("venue"),
            rs.getString("status"),
            rs.getInt("winner_team_id")
        );
    }
}
