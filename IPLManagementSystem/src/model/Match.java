/*
 * ============================================================
 * Match.java — Match model class
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS: OOP, Encapsulation, Constructor Overloading
 *
 * Maps to the 'matches' table.
 * Has TWO foreign keys to teams (team1 and team2).
 * 'status' can be: "Scheduled", "Live", "Completed"
 */

package model;

public class Match {

    private int matchId;
    private int team1Id;             // FK → teams.team_id (first team)
    private int team2Id;             // FK → teams.team_id (second team)
    private String matchDate;        // Stored as String for simplicity (YYYY-MM-DD)
    private String venue;
    private String status;           // Scheduled / Live / Completed
    private int winnerTeamId;        // FK → teams.team_id (winner, 0 if not decided)

    // ── Constructors ──────────────────────────────────────────
    public Match() {
    }

    // For scheduling a new match (no winner yet)
    public Match(int team1Id, int team2Id, String matchDate, String venue) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.matchDate = matchDate;
        this.venue = venue;
        this.status = "Scheduled";    // Default status
        this.winnerTeamId = 0;
    }

    // Full constructor (reading from database)
    public Match(int matchId, int team1Id, int team2Id, String matchDate,
                 String venue, String status, int winnerTeamId) {
        this.matchId = matchId;
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.matchDate = matchDate;
        this.venue = venue;
        this.status = status;
        this.winnerTeamId = winnerTeamId;
    }

    // ── Getters and Setters ───────────────────────────────────
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getTeam1Id() { return team1Id; }
    public void setTeam1Id(int team1Id) { this.team1Id = team1Id; }

    public int getTeam2Id() { return team2Id; }
    public void setTeam2Id(int team2Id) { this.team2Id = team2Id; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getWinnerTeamId() { return winnerTeamId; }
    public void setWinnerTeamId(int winnerTeamId) { this.winnerTeamId = winnerTeamId; }

    @Override
    public String toString() {
        return "Match{" +
                "matchId=" + matchId +
                ", team1Id=" + team1Id +
                ", team2Id=" + team2Id +
                ", date='" + matchDate + '\'' +
                ", venue='" + venue + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
