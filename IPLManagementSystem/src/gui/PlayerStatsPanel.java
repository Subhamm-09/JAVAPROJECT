/*
 * ============================================================
 * PlayerStatsPanel.java — Player Statistics & Report Generation
 * Package: gui
 * ============================================================
 *
 * FEATURES:
 * - View top run scorers and wicket takers
 * - Generate match reports (FILE HANDLING demo)
 * - Uses ArrayList, sorting, aggregate data
 */

package gui;

import dao.PlayerDAO;
import dao.MatchDAO;
import model.Player;
import model.Match;
import filehandling.ReportGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlayerStatsPanel extends JPanel {

    private JTable statsTable;
    private DefaultTableModel tableModel;
    private PlayerDAO playerDAO;
    private MatchDAO matchDAO;

    public PlayerStatsPanel() {
        playerDAO = new PlayerDAO();
        matchDAO = new MatchDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title ─────────────────────────────────────────────
        JLabel title = new JLabel("📈 Player Statistics & Reports", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // ── Button Panel (Top Buttons) ────────────────────────
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setOpaque(false);

        JButton topRunsBtn = createButton("🏏 Top Run Scorers", new Color(46, 204, 113));
        JButton topWicketsBtn = createButton("🎯 Top Wicket Takers", new Color(155, 89, 182));
        JButton allPlayersBtn = createButton("👥 All Players", new Color(52, 152, 219));
        JButton genReportBtn = createButton("📄 Generate Match Report", new Color(230, 126, 34));
        JButton genSummaryBtn = createButton("📋 Generate Summary", new Color(231, 76, 60));

        topPanel.add(topRunsBtn);
        topPanel.add(topWicketsBtn);
        topPanel.add(allPlayersBtn);
        topPanel.add(genReportBtn);
        topPanel.add(genSummaryBtn);

        // Wrap title + buttons
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────
        String[] columns = {"#", "Player", "Team ID", "Role", "Matches", "Runs", "Wickets", "Bat Avg", "Bowl Avg"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        statsTable = new JTable(tableModel);
        statsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsTable.setRowHeight(28);
        statsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        // ── Button Actions ────────────────────────────────────
        topRunsBtn.addActionListener(e -> loadTopRunScorers());
        topWicketsBtn.addActionListener(e -> loadTopWicketTakers());
        allPlayersBtn.addActionListener(e -> loadAllPlayers());
        genReportBtn.addActionListener(e -> generateMatchReport());
        genSummaryBtn.addActionListener(e -> generateSummaryReport());

        // Default view
        loadAllPlayers();
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadAllPlayers() {
        loadPlayersIntoTable(playerDAO.getAll());
    }

    private void loadTopRunScorers() {
        loadPlayersIntoTable(playerDAO.getTopRunScorers(10));
    }

    private void loadTopWicketTakers() {
        loadPlayersIntoTable(playerDAO.getTopWicketTakers(10));
    }

    private void loadPlayersIntoTable(List<Player> players) {
        tableModel.setRowCount(0);
        int rank = 1;
        for (Player p : players) {
            tableModel.addRow(new Object[]{
                rank++, p.getPlayerName(), p.getTeamId(), p.getRole(),
                p.getMatchesPlayed(), p.getRunsScored(), p.getWicketsTaken(),
                p.getBattingAvg(), p.getBowlingAvg()
            });
        }
    }

    // ── FILE HANDLING: Generate Match Report ──────────────────
    private void generateMatchReport() {
        String input = JOptionPane.showInputDialog(this, "Enter Match ID to generate report:");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int matchId = Integer.parseInt(input.trim());
                ReportGenerator generator = new ReportGenerator();
                String filePath = generator.generateMatchReport(matchId);

                if (filePath != null) {
                    JOptionPane.showMessageDialog(this,
                            "Report generated successfully!\nSaved to: " + filePath,
                            "Report Generated", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Match not found with ID: " + matchId,
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid match ID!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // ── FILE HANDLING: Generate Summary Report ────────────────
    private void generateSummaryReport() {
        ReportGenerator generator = new ReportGenerator();
        String filePath = generator.generateAllMatchesReport();

        if (filePath != null) {
            JOptionPane.showMessageDialog(this,
                    "Summary report generated!\nSaved to: " + filePath,
                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to generate report.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
