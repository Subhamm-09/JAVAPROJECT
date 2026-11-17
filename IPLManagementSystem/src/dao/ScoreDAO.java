/*
 * ============================================================
 * ScoreDAO.java — CRUD for Match Scores
 * Package: dao
 * ============================================================
 *
 * SQL: INSERT, SELECT, UPDATE, DELETE, SUM(), GROUP BY
 */

package dao;

import connection.DBConnection;
import model.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO implements ManagementOperations<Score> {

    @Override
    public boolean add(Score score) {
        String sql = "INSERT INTO scores (match_id, team_id, runs, wickets, overs, extras) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, score.getMatchId());
            pstmt.setInt(2, score.getTeamId());
            pstmt.setInt(3, score.getRuns());
            pstmt.setInt(4, score.getWickets());
            pstmt.setDouble(5, score.getOvers());
            pstmt.setInt(6, score.getExtras());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ Failed to add score: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Score> getAll() {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM scores ORDER BY match_id";

        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                scores.add(extractScore(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public Score getById(int id) {
        String sql = "SELECT * FROM scores WHERE score_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractScore(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── Get scores for a specific match ───────────────────────
    public List<Score> getByMatchId(int matchId) {
        List<Score> scores = new ArrayList<>();
        String sql = "SELECT * FROM scores WHERE match_id = ?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                scores.add(extractScore(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    @Override
    public boolean update(Score score) {
        String sql = "UPDATE scores SET runs=?, wickets=?, overs=?, extras=? WHERE score_id=?";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, score.getRuns());
            pstmt.setInt(2, score.getWickets());
            pstmt.setDouble(3, score.getOvers());
            pstmt.setInt(4, score.getExtras());
            pstmt.setInt(5, score.getScoreId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM scores WHERE score_id = ?";

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

    // ── Helper ────────────────────────────────────────────────
    private Score extractScore(ResultSet rs) throws SQLException {
        return new Score(
            rs.getInt("score_id"),
            rs.getInt("match_id"),
            rs.getInt("team_id"),
            rs.getInt("runs"),
            rs.getInt("wickets"),
            rs.getDouble("overs"),
            rs.getInt("extras")
        );
    }
}
