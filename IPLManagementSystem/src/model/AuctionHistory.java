/*
 * ============================================================
 * AuctionHistory.java — 10-Year Historical Player Price Model
 * Package: model
 * ============================================================
 */

package model;

public class AuctionHistory {

    private int historyId;
    private String playerName;
    private int auctionYear;
    private String role;
    private String country;
    private double basePrice;       // in Lakhs
    private double soldPrice;       // in Lakhs
    private String boughtByTeam;    // e.g. 'CSK', 'MI', or 'Unsold'
    private String status;          // 'Sold' or 'Unsold'

    public AuctionHistory() {}

    public AuctionHistory(String playerName, int auctionYear, String role, String country,
                          double basePrice, double soldPrice, String boughtByTeam, String status) {
        this.playerName = playerName;
        this.auctionYear = auctionYear;
        this.role = role;
        this.country = country;
        this.basePrice = basePrice;
        this.soldPrice = soldPrice;
        this.boughtByTeam = boughtByTeam;
        this.status = status;
    }

    public AuctionHistory(int historyId, String playerName, int auctionYear, String role, String country,
                          double basePrice, double soldPrice, String boughtByTeam, String status) {
        this.historyId = historyId;
        this.playerName = playerName;
        this.auctionYear = auctionYear;
        this.role = role;
        this.country = country;
        this.basePrice = basePrice;
        this.soldPrice = soldPrice;
        this.boughtByTeam = boughtByTeam;
        this.status = status;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getAuctionYear() { return auctionYear; }
    public void setAuctionYear(int auctionYear) { this.auctionYear = auctionYear; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public double getSoldPrice() { return soldPrice; }
    public void setSoldPrice(double soldPrice) { this.soldPrice = soldPrice; }

    public String getBoughtByTeam() { return boughtByTeam; }
    public void setBoughtByTeam(String boughtByTeam) { this.boughtByTeam = boughtByTeam; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
