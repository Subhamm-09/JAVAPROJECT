/*
 * ============================================================
 * ReportGenerator.java — File Handling Demo
 * Package: filehandling
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. File Handling    → FileWriter, BufferedWriter for writing files
 * 2. File Handling    → FileReader, BufferedReader for reading files
 * 3. Exception Handling → try-catch-finally, try-with-resources
 * 4. Collections      → Uses List<Match>, List<Score> to generate reports
 *
 * WHAT IS FILE HANDLING?
 * ──────────────────────
 * File handling lets Java programs READ from and WRITE to files on disk.
 *
 * Writing to a file:
 *   FileWriter fw = new FileWriter("file.txt");   // Opens the file
 *   BufferedWriter bw = new BufferedWriter(fw);     // Buffers for efficiency
 *   bw.write("Hello");                              // Writes text
 *   bw.close();                                     // MUST close to flush & save
 *
 * Reading from a file:
 *   FileReader fr = new FileReader("file.txt");
 *   BufferedReader br = new BufferedReader(fr);
 *   String line = br.readLine();                    // Reads one line at a time
 *   br.close();
 *
 * TRY-WITH-RESOURCES (Java 7+):
 *   try (BufferedWriter bw = new BufferedWriter(new FileWriter("file.txt"))) {
 *       bw.write("Hello");
 *   }  // Automatically closes the writer — no need for finally block!
 */

package filehandling;

import dao.MatchDAO;
import dao.ScoreDAO;
import dao.TeamDAO;
import model.Match;
import model.Score;
import model.Team;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportGenerator {

    // Directory where reports are saved
    private static final String REPORTS_DIR = "reports/";

    // ── Generate Match Report ─────────────────────────────────
    // Creates a text file with all match details and scores
    public String generateMatchReport(int matchId) {
        MatchDAO matchDAO = new MatchDAO();
        ScoreDAO scoreDAO = new ScoreDAO();
        TeamDAO teamDAO = new TeamDAO();

        Match match = matchDAO.getById(matchId);
        if (match == null) {
            return null;
        }

        // Create filename with timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = REPORTS_DIR + "match_report_" + matchId + "_" + timestamp + ".txt";

        // Try-with-resources: automatically closes the writer
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // Get team names
            Team team1 = teamDAO.getById(match.getTeam1Id());
            Team team2 = teamDAO.getById(match.getTeam2Id());
            String team1Name = (team1 != null) ? team1.getTeamName() : "Team " + match.getTeam1Id();
            String team2Name = (team2 != null) ? team2.getTeamName() : "Team " + match.getTeam2Id();

            // Write report header
            writer.write("╔══════════════════════════════════════════════════════════╗");
            writer.newLine();
            writer.write("║           IPL MATCH REPORT                               ║");
            writer.newLine();
            writer.write("╚══════════════════════════════════════════════════════════╝");
            writer.newLine();
            writer.newLine();

            writer.write("Match ID    : " + match.getMatchId());
            writer.newLine();
            writer.write("Match       : " + team1Name + " vs " + team2Name);
            writer.newLine();
            writer.write("Date        : " + match.getMatchDate());
            writer.newLine();
            writer.write("Venue       : " + match.getVenue());
            writer.newLine();
            writer.write("Status      : " + match.getStatus());
            writer.newLine();

            // Winner
            if (match.getWinnerTeamId() > 0) {
                Team winner = teamDAO.getById(match.getWinnerTeamId());
                writer.write("Winner      : " + (winner != null ? winner.getTeamName() : "Unknown"));
                writer.newLine();
            }

            writer.newLine();
            writer.write("── SCORECARD ─────────────────────────────────────────────");
            writer.newLine();

            // Get scores for this match
            List<Score> scores = scoreDAO.getByMatchId(matchId);
            if (scores.isEmpty()) {
                writer.write("  No scores recorded yet.");
                writer.newLine();
            } else {
                for (Score score : scores) {
                    Team team = teamDAO.getById(score.getTeamId());
                    String teamName = (team != null) ? team.getTeamName() : "Team " + score.getTeamId();
                    writer.write("  " + teamName + ": " + score.getRuns() + "/" + score.getWickets()
                                 + " (" + score.getOvers() + " overs) | Extras: " + score.getExtras());
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.write("── Report generated on: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            writer.newLine();
            writer.write("══════════════════════════════════════════════════════════");
            writer.newLine();

            System.out.println("📄 Report saved: " + fileName);
            return fileName;

        } catch (IOException e) {
            System.err.println("❌ Failed to generate report: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ── Generate All Matches Summary Report ───────────────────
    public String generateAllMatchesReport() {
        MatchDAO matchDAO = new MatchDAO();
        TeamDAO teamDAO = new TeamDAO();

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = REPORTS_DIR + "all_matches_" + timestamp + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            writer.write("╔══════════════════════════════════════════════════════════╗");
            writer.newLine();
            writer.write("║           IPL MATCHES SUMMARY                            ║");
            writer.newLine();
            writer.write("╚══════════════════════════════════════════════════════════╝");
            writer.newLine();
            writer.newLine();

            List<Match> matches = matchDAO.getAll();

            if (matches.isEmpty()) {
                writer.write("  No matches scheduled.");
                writer.newLine();
            } else {
                writer.write(String.format("  %-5s %-25s %-25s %-12s %-12s", "ID", "Team 1", "Team 2", "Date", "Status"));
                writer.newLine();
                writer.write("  " + "-".repeat(80));
                writer.newLine();

                for (Match m : matches) {
                    Team t1 = teamDAO.getById(m.getTeam1Id());
                    Team t2 = teamDAO.getById(m.getTeam2Id());
                    writer.write(String.format("  %-5d %-25s %-25s %-12s %-12s",
                            m.getMatchId(),
                            (t1 != null ? t1.getTeamName() : "?"),
                            (t2 != null ? t2.getTeamName() : "?"),
                            m.getMatchDate(),
                            m.getStatus()));
                    writer.newLine();
                }
            }

            writer.newLine();
            writer.write("── Report generated on: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
            writer.newLine();

            System.out.println("📄 Summary report saved: " + fileName);
            return fileName;

        } catch (IOException e) {
            System.err.println("❌ Failed to generate summary: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // ── Read a report file back (FILE READING demo) ───────────
    public String readReport(String fileName) {
        StringBuilder content = new StringBuilder();

        // FileReader + BufferedReader for reading text files
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            // readLine() returns null when end of file is reached
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println("❌ File not found: " + fileName);
            return "File not found: " + fileName;
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
            return "Error reading file.";
        }

        return content.toString();
    }
}
