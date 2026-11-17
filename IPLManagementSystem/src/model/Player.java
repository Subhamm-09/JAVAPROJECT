/*
 * ============================================================
 * Player.java — Player model class
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. OOP & Encapsulation → Private fields with getters/setters
 * 2. Constructor Overloading (Polymorphism) → Multiple constructors
 * 3. Foreign Key mapping → teamId links Player to Team
 *
 * This model maps to the 'players' table in the database.
 * The teamId field represents the FOREIGN KEY relationship:
 *   players.team_id → teams.team_id
 */

package model;

public class Player {

    private int playerId;
    private String playerName;
    private int teamId;              // FOREIGN KEY → references teams.team_id
    private String role;             // Batsman, Bowler, All-rounder, Wicketkeeper
    private int age;
    private int matchesPlayed;
    private int runsScored;
    private int wicketsTaken;
    private double battingAvg;
    private double bowlingAvg;

    // ── Default Constructor ───────────────────────────────────
    public Player() {
    }

    // ── Constructor without ID (for INSERT) ───────────────────
    public Player(String playerName, int teamId, String role, int age,
                  int matchesPlayed, int runsScored, int wicketsTaken,
                  double battingAvg, double bowlingAvg) {
        this.playerName = playerName;
        this.teamId = teamId;
        this.role = role;
        this.age = age;
        this.matchesPlayed = matchesPlayed;
        this.runsScored = runsScored;
        this.wicketsTaken = wicketsTaken;
        this.battingAvg = battingAvg;
        this.bowlingAvg = bowlingAvg;
    }

    // ── Full Constructor (for SELECT) ─────────────────────────
    public Player(int playerId, String playerName, int teamId, String role, int age,
                  int matchesPlayed, int runsScored, int wicketsTaken,
                  double battingAvg, double bowlingAvg) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.teamId = teamId;
        this.role = role;
        this.age = age;
        this.matchesPlayed = matchesPlayed;
        this.runsScored = runsScored;
        this.wicketsTaken = wicketsTaken;
        this.battingAvg = battingAvg;
        this.bowlingAvg = bowlingAvg;
    }

    // ── Getters and Setters ───────────────────────────────────
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getRunsScored() { return runsScored; }
    public void setRunsScored(int runsScored) { this.runsScored = runsScored; }

    public int getWicketsTaken() { return wicketsTaken; }
    public void setWicketsTaken(int wicketsTaken) { this.wicketsTaken = wicketsTaken; }

    public double getBattingAvg() { return battingAvg; }
    public void setBattingAvg(double battingAvg) { this.battingAvg = battingAvg; }

    public double getBowlingAvg() { return bowlingAvg; }
    public void setBowlingAvg(double bowlingAvg) { this.bowlingAvg = bowlingAvg; }

    @Override
    public String toString() {
        return "Player{" +
                "playerId=" + playerId +
                ", playerName='" + playerName + '\'' +
                ", teamId=" + teamId +
                ", role='" + role + '\'' +
                ", runs=" + runsScored +
                ", wickets=" + wicketsTaken +
                '}';
    }
}
