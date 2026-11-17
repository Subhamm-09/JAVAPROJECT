/*
 * ============================================================
 * LiveAuctionDAO.java — Real-time Bidding & Purse Management
 * Package: dao
 * ============================================================
 *
 * FEATURES:
 * 1. Place live bids with team purse validation
 * 2. Deduct winning amount from team's purse upon 'SOLD'
 * 3. Update player status in the database
 */

package dao;

import connection.DBConnection;
import model.LiveAuction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LiveAuctionDAO {

    // ── Add player to Live Auction pool ──
    public boolean addPlayerToAuction(LiveAuction item) {
        String sql = "INSERT INTO live_auctions (player_name, role, country, base_price, historical_avg, calculated_bid, current_bid, highest_bidder, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getPlayerName());
            pstmt.setString(2, item.getRole());
            pstmt.setString(3, item.getCountry());
            pstmt.setDouble(4, item.getBasePrice());
            pstmt.setDouble(5, item.getHistoricalAvg());
            pstmt.setDouble(6, item.getCalculatedBid());
            pstmt.setDouble(7, item.getCurrentBid());
            pstmt.setString(8, item.getHighestBidder());
            pstmt.setString(9, item.getStatus());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Get all players in live auction ──
    public List<LiveAuction> getAllLiveAuctions() {
        List<LiveAuction> list = new ArrayList<>();
        String sql = "SELECT * FROM live_auctions ORDER BY auction_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new LiveAuction(
                    rs.getInt("auction_id"),
                    rs.getString("player_name"),
                    rs.getString("role"),
                    rs.getString("country"),
                    rs.getDouble("base_price"),
                    rs.getDouble("historical_avg"),
                    rs.getDouble("calculated_bid"),
                    rs.getDouble("current_bid"),
                    rs.getString("highest_bidder"),
                    rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Place a Bid (with team purse check) ──
    public boolean placeBid(int auctionId, String teamName, double newBidAmount) {
        // First verify the team has enough purse remaining
        String checkPurseSql = "SELECT purse_remaining FROM teams WHERE team_name = ?";
        String updateBidSql = "UPDATE live_auctions SET current_bid = ?, highest_bidder = ?, status = 'Live' WHERE auction_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkPurseSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateBidSql)) {

            checkStmt.setString(1, teamName);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                double purse = rs.getDouble("purse_remaining");
                if (purse < newBidAmount) {
                    System.err.println("❌ Team " + teamName + " does not have enough purse! (Has: ₹" + purse + "L, Needs: ₹" + newBidAmount + "L)");
                    return false;
                }
            }

            // Update highest bid
            updateStmt.setDouble(1, newBidAmount);
            updateStmt.setString(2, teamName);
            updateStmt.setInt(3, auctionId);
            return updateStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Mark Player as SOLD! ──
    // Deducts final bid from winning team's purse
    public boolean markSold(int auctionId, String teamName, double finalPrice) {
        String updateAuctionSql = "UPDATE live_auctions SET status = 'Sold', highest_bidder = ?, current_bid = ? WHERE auction_id = ?";
        String deductPurseSql = "UPDATE teams SET purse_remaining = purse_remaining - ? WHERE team_name = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaction

            try (PreparedStatement p1 = conn.prepareStatement(updateAuctionSql);
                 PreparedStatement p2 = conn.prepareStatement(deductPurseSql)) {

                p1.setString(1, teamName);
                p1.setDouble(2, finalPrice);
                p1.setInt(3, auctionId);
                p1.executeUpdate();

                p2.setDouble(1, finalPrice);
                p2.setString(2, teamName);
                p2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Mark Player as UNSOLD ──
    public boolean markUnsold(int auctionId) {
        String sql = "UPDATE live_auctions SET status = 'Unsold', highest_bidder = 'None' WHERE auction_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, auctionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
