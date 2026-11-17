/*
 * ============================================================
 * AuctionHistoryDAO.java — 10-Year Historical Price Analytics
 * Package: dao
 * ============================================================
 *
 * FEATURES:
 * 1. Stores and queries multi-year player auction records
 * 2. Implements the POPULARITY & DYNAMIC STARTING BID ALGORITHM:
 *    Calculated Bid = (Base Price * Popularity Factor) + (Historical Avg * 0.6)
 */

package dao;

import connection.DBConnection;
import model.AuctionHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuctionHistoryDAO {

    // ── Insert a historical auction record ──
    public boolean addHistory(AuctionHistory h) {
        String sql = "INSERT INTO auction_history (player_name, auction_year, role, country, base_price, sold_price, bought_by_team, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, h.getPlayerName());
            pstmt.setInt(2, h.getAuctionYear());
            pstmt.setString(3, h.getRole());
            pstmt.setString(4, h.getCountry());
            pstmt.setDouble(5, h.getBasePrice());
            pstmt.setDouble(6, h.getSoldPrice());
            pstmt.setString(7, h.getBoughtByTeam());
            pstmt.setString(8, h.getStatus());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Batch insert multiple records (optimized for PDF extraction) ──
    public int batchAddHistory(List<AuctionHistory> list) {
        String sql = "INSERT INTO auction_history (player_name, auction_year, role, country, base_price, sold_price, bought_by_team, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int count = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Transaction management
            for (AuctionHistory h : list) {
                pstmt.setString(1, h.getPlayerName());
                pstmt.setInt(2, h.getAuctionYear());
                pstmt.setString(3, h.getRole());
                pstmt.setString(4, h.getCountry());
                pstmt.setDouble(5, h.getBasePrice());
                pstmt.setDouble(6, h.getSoldPrice());
                pstmt.setString(7, h.getBoughtByTeam());
                pstmt.setString(8, h.getStatus());
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            conn.commit();
            count = results.length;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // ── Search player history across all 10 years ──
    public List<AuctionHistory> getPlayerHistory(String playerName) {
        List<AuctionHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM auction_history WHERE player_name LIKE ? ORDER BY auction_year DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + playerName + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new AuctionHistory(
                    rs.getInt("history_id"),
                    rs.getString("player_name"),
                    rs.getInt("auction_year"),
                    rs.getString("role"),
                    rs.getString("country"),
                    rs.getDouble("base_price"),
                    rs.getDouble("sold_price"),
                    rs.getString("bought_by_team"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Get all history for a specific auction year ──
    public List<AuctionHistory> getByYear(int year) {
        List<AuctionHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM auction_history WHERE auction_year = ? ORDER BY sold_price DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(new AuctionHistory(
                    rs.getInt("history_id"),
                    rs.getString("player_name"),
                    rs.getInt("auction_year"),
                    rs.getString("role"),
                    rs.getString("country"),
                    rs.getDouble("base_price"),
                    rs.getDouble("sold_price"),
                    rs.getString("bought_by_team"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── POPULARITY & SMART STARTING BID ALGORITHM ──
    // Calculates the recommended starting bid based on 10-year track record
    public double[] calculateSmartStartingBid(String playerName, double reservePrice) {
        String sql = "SELECT AVG(sold_price) AS avg_price, COUNT(*) AS times_sold, MAX(sold_price) AS max_price " +
                     "FROM auction_history WHERE player_name = ? AND status = 'Sold' AND sold_price > 0";

        double historicalAvg = 0.0;
        double calculatedBid = reservePrice;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("times_sold") > 0) {
                historicalAvg = rs.getDouble("avg_price");
                int timesSold = rs.getInt("times_sold");

                // Popularity factor: 1.0 + (0.1 * times sold in past auctions)
                double popularityFactor = Math.min(1.5, 1.0 + (0.1 * timesSold));

                // Smart Starting Bid formula:
                // Weighted blend of base reserve price + historical average popularity
                calculatedBid = (reservePrice * popularityFactor) + (historicalAvg * 0.4);
                // Round to nearest 10 Lakhs
                calculatedBid = Math.round(calculatedBid / 10.0) * 10.0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Returns: [0] = Historical Average, [1] = Smart Calculated Starting Bid
        return new double[]{historicalAvg, calculatedBid};
    }
}
