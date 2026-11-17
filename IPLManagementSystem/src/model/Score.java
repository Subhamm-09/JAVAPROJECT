/*
 * ============================================================
 * Score.java — Score model class
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS: OOP, Encapsulation
 *
 * Maps to the 'scores' table.
 * Each row = one team's score in one match.
 * So each match will have TWO score entries (one per team).
 */

package model;

public class Score {

    private int scoreId;
    private int matchId;    // FK → matches.match_id
    private int teamId;     // FK → teams.team_id
    private int runs;
    private int wickets;
    private double overs;   // e.g., 19.4 means 19 overs and 4 balls
    private int extras;

    // ── Constructors ──────────────────────────────────────────
    public Score() {
    }

    public Score(int matchId, int teamId, int runs, int wickets, double overs, int extras) {
        this.matchId = matchId;
        this.teamId = teamId;
        this.runs = runs;
        this.wickets = wickets;
        this.overs = overs;
        this.extras = extras;
    }

    public Score(int scoreId, int matchId, int teamId, int runs, int wickets, double overs, int extras) {
        this.scoreId = scoreId;
        this.matchId = matchId;
        this.teamId = teamId;
        this.runs = runs;
        this.wickets = wickets;
        this.overs = overs;
        this.extras = extras;
    }

    // ── Getters and Setters ───────────────────────────────────
    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }

    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }

    public double getOvers() { return overs; }
    public void setOvers(double overs) { this.overs = overs; }

    public int getExtras() { return extras; }
    public void setExtras(int extras) { this.extras = extras; }

    @Override
    public String toString() {
        return "Score{" +
                "matchId=" + matchId +
                ", teamId=" + teamId +
                ", runs=" + runs + "/" + wickets +
                " (" + overs + " ov)" +
                '}';
    }
}
