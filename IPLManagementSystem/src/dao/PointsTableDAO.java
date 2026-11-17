/*
 * ============================================================
 * PointsTableDAO.java — CRUD for League Points Table
 * Package: dao
 * ============================================================
 *
 * SQL: SELECT with ORDER BY, UPDATE, JOIN, GROUP BY
 * Demonstrates aggregate queries and sorting for league standings.
 */

package dao;

import connection.DBConnection;
import model.PointsTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointsTableDAO implements ManagementOperations<PointsTable> {

    @Override
    public boolean add(PointsTable pt) {
        String sql = "INSERT INTO points_table (team_id, matches_played, wins, losses, draws, points, net_run_rate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, pt.getTeamId());
            pstmt.setInt(2, pt.getMatchesPlayed());
            pstmt.setInt(3, pt.getWins());
            pstmt.setInt(4, pt.getLosses());
            pstmt.setInt(5, pt.getDraws());
            pstmt.setInt(6, pt.getPoints());
            pstmt.setDouble(7, pt.getNetRunRate());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Get standings sorted by points (DESC) then NRR (DESC) ─
    @Override
    public List<PointsTable> getAll() {
        List<PointsTable> standings = new ArrayList<>();
        // ORDER BY points DESC, net_run_rate DESC — highest points first, NRR breaks ties
        String sql = "SELECT * FROM points_table ORDER BY points DESC, net_run_rate DESC";

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                standings.add(extractPointsTable(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return standings;
    }

    @Override
    public PointsTable getById(int id) {
        String sql = "SELECT * FROM points_table WHERE points_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPointsTable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Get points entry by team ID ───────────────────────────
    public PointsTable getByTeamId(int teamId) {
        String sql = "SELECT * FROM points_table WHERE team_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, teamId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractPointsTable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean update(PointsTable pt) {
        String sql = "UPDATE points_table SET matches_played=?, wins=?, losses=?, draws=?, points=?, net_run_rate=? " +
                     "WHERE team_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, pt.getMatchesPlayed());
            pstmt.setInt(2, pt.getWins());
            pstmt.setInt(3, pt.getLosses());
            pstmt.setInt(4, pt.getDraws());
            pstmt.setInt(5, pt.getPoints());
            pstmt.setDouble(6, pt.getNetRunRate());
            pstmt.setInt(7, pt.getTeamId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM points_table WHERE points_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Update points when a match result is recorded ─────────
    // winnerTeamId gets +2 points, loserTeamId gets 0
    public void recordMatchResult(int winnerTeamId, int loserTeamId) {
        // Update winner
        PointsTable winner = getByTeamId(winnerTeamId);
        if (winner != null) {
            winner.setMatchesPlayed(winner.getMatchesPlayed() + 1);
            winner.setWins(winner.getWins() + 1);
            winner.setPoints(winner.getPoints() + 2);   // Win = 2 points
            update(winner);
        }

        // Update loser
        PointsTable loser = getByTeamId(loserTeamId);
        if (loser != null) {
            loser.setMatchesPlayed(loser.getMatchesPlayed() + 1);
            loser.setLosses(loser.getLosses() + 1);
            // Points stay the same (Loss = 0 points)
            update(loser);
        }
    }

    // ── Helper ────────────────────────────────────────────────
    private PointsTable extractPointsTable(ResultSet rs) throws SQLException {
        return new PointsTable(
            rs.getInt("points_id"),
            rs.getInt("team_id"),
            rs.getInt("matches_played"),
            rs.getInt("wins"),
            rs.getInt("losses"),
            rs.getInt("draws"),
            rs.getInt("points"),
            rs.getDouble("net_run_rate")
        );
    }
}
