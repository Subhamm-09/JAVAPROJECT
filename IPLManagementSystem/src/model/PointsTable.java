/*
 * ============================================================
 * PointsTable.java — Points Table model class
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS: OOP, Encapsulation
 *
 * Maps to the 'points_table' database table.
 * Stores league standings: wins, losses, points, NRR.
 */

package model;

public class PointsTable {

    private int pointsId;
    private int teamId;          // FK → teams.team_id
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int draws;
    private int points;          // Win=2, Draw=1, Loss=0
    private double netRunRate;   // NRR = (total runs scored / overs faced) - (runs conceded / overs bowled)

    // ── Constructors ──────────────────────────────────────────
    public PointsTable() {
    }

    public PointsTable(int teamId, int matchesPlayed, int wins, int losses,
                       int draws, int points, double netRunRate) {
        this.teamId = teamId;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.points = points;
        this.netRunRate = netRunRate;
    }

    public PointsTable(int pointsId, int teamId, int matchesPlayed, int wins,
                       int losses, int draws, int points, double netRunRate) {
        this.pointsId = pointsId;
        this.teamId = teamId;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.points = points;
        this.netRunRate = netRunRate;
    }

    // ── Getters and Setters ───────────────────────────────────
    public int getPointsId() { return pointsId; }
    public void setPointsId(int pointsId) { this.pointsId = pointsId; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getMatchesPlayed() { return matchesPlayed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public double getNetRunRate() { return netRunRate; }
    public void setNetRunRate(double netRunRate) { this.netRunRate = netRunRate; }

    @Override
    public String toString() {
        return "PointsTable{" +
                "teamId=" + teamId +
                ", played=" + matchesPlayed +
                ", W=" + wins + " L=" + losses + " D=" + draws +
                ", pts=" + points +
                ", NRR=" + netRunRate +
                '}';
    }
}
