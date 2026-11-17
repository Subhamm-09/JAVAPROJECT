/*
 * ============================================================
 * LiveAuction.java — Live Bidding Item with Dynamic Starting Bid
 * Package: model
 * ============================================================
 */

package model;

public class LiveAuction {

    private int auctionId;
    private String playerName;
    private String role;
    private String country;
    private double basePrice;        // Base reserve price in Lakhs
    private double historicalAvg;     // 10-year historical average sold price in Lakhs
    private double calculatedBid;     // Smart starting bid based on past popularity
    private double currentBid;        // Current highest bid
    private String highestBidder;     // Team name
    private String status;            // 'Upcoming', 'Live', 'Sold', 'Unsold'

    public LiveAuction() {}

    public LiveAuction(String playerName, String role, String country, double basePrice,
                       double historicalAvg, double calculatedBid) {
        this.playerName = playerName;
        this.role = role;
        this.country = country;
        this.basePrice = basePrice;
        this.historicalAvg = historicalAvg;
        this.calculatedBid = calculatedBid;
        this.currentBid = calculatedBid > 0 ? calculatedBid : basePrice;
        this.highestBidder = "None";
        this.status = "Upcoming";
    }

    public LiveAuction(int auctionId, String playerName, String role, String country, double basePrice,
                       double historicalAvg, double calculatedBid, double currentBid,
                       String highestBidder, String status) {
        this.auctionId = auctionId;
        this.playerName = playerName;
        this.role = role;
        this.country = country;
        this.basePrice = basePrice;
        this.historicalAvg = historicalAvg;
        this.calculatedBid = calculatedBid;
        this.currentBid = currentBid;
        this.highestBidder = highestBidder;
        this.status = status;
    }

    public int getAuctionId() { return auctionId; }
    public void setAuctionId(int auctionId) { this.auctionId = auctionId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getHistoricalAvg() { return historicalAvg; }
    public void setHistoricalAvg(double historicalAvg) { this.historicalAvg = historicalAvg; }

    public double getCalculatedBid() { return calculatedBid; }
    public void setCalculatedBid(double calculatedBid) { this.calculatedBid = calculatedBid; }

    public double getCurrentBid() { return currentBid; }
    public void setCurrentBid(double currentBid) { this.currentBid = currentBid; }

    public String getHighestBidder() { return highestBidder; }
    public void setHighestBidder(String highestBidder) { this.highestBidder = highestBidder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
