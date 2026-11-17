/*
 * ============================================================
 * ScorePanel.java — Score Management Panel
 * Package: gui
 * ============================================================
 *
 * SWING: JComboBox to select match & team, JTextField for scores
 * FEATURES: Add/update scores, view scorecard per match
 * MULTITHREADING: Auto-refresh thread for live score updates
 */

package gui;

import dao.MatchDAO;
import dao.ScoreDAO;
import dao.TeamDAO;
import model.Match;
import model.Score;
import model.Team;
import threads.ScoreRefreshThread;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScorePanel extends JPanel {

    private JTable scoreTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> matchCombo, teamCombo;
    private JTextField runsField, wicketsField, oversField, extrasField;
    private ScoreDAO scoreDAO;
    private MatchDAO matchDAO;
    private TeamDAO teamDAO;
    private ScoreRefreshThread refreshThread;

    public ScorePanel() {
        scoreDAO = new ScoreDAO();
        matchDAO = new MatchDAO();
        teamDAO = new TeamDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title with auto-refresh indicator ─────────────────
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("📊 Score Management", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        titlePanel.add(title, BorderLayout.WEST);

        // Auto-refresh toggle button (MULTITHREADING)
        JButton autoRefreshBtn = new JButton("🔄 Auto-Refresh: OFF");
        autoRefreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        autoRefreshBtn.setBackground(new Color(149, 165, 166));
        autoRefreshBtn.setForeground(Color.WHITE);
        autoRefreshBtn.setFocusPainted(false);
        autoRefreshBtn.addActionListener(e -> toggleAutoRefresh(autoRefreshBtn));
        titlePanel.add(autoRefreshBtn, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // ── Form Panel ────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Score Entry"));
        formPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Match combo
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Match:"), gbc);
        gbc.gridx = 1;
        matchCombo = new JComboBox<>();
        loadMatchCombo();
        formPanel.add(matchCombo, gbc);
        row++;

        // Team combo
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1;
        teamCombo = new JComboBox<>();
        loadTeamCombo();
        formPanel.add(teamCombo, gbc);
        row++;

        runsField = addFormField(formPanel, gbc, "Runs:", row++);
        wicketsField = addFormField(formPanel, gbc, "Wickets:", row++);
        oversField = addFormField(formPanel, gbc, "Overs:", row++);
        extrasField = addFormField(formPanel, gbc, "Extras:", row++);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);
        JButton addBtn = createButton("➕ Add Score", new Color(46, 204, 113));
        JButton updateBtn = createButton("✏️ Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("🗑️ Delete", new Color(231, 76, 60));
        JButton refreshBtn = createButton("🔄 Refresh", new Color(155, 89, 182));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // ── Table ─────────────────────────────────────────────
        String[] columns = {"Score ID", "Match ID", "Team ID", "Runs", "Wickets", "Overs", "Extras"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        scoreTable.setRowHeight(26);
        scoreTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        scoreTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = scoreTable.getSelectedRow();
                if (r >= 0) {
                    runsField.setText(tableModel.getValueAt(r, 3).toString());
                    wicketsField.setText(tableModel.getValueAt(r, 4).toString());
                    oversField.setText(tableModel.getValueAt(r, 5).toString());
                    extrasField.setText(tableModel.getValueAt(r, 6).toString());
                }
            }
        });

        add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addScore());
        updateBtn.addActionListener(e -> updateScore());
        deleteBtn.addActionListener(e -> deleteScore());
        refreshBtn.addActionListener(e -> loadScores());

        loadScores();
    }

    // ── MULTITHREADING: Toggle auto-refresh ───────────────────
    private void toggleAutoRefresh(JButton btn) {
        if (refreshThread == null || !refreshThread.isRunning()) {
            // Start auto-refresh thread
            refreshThread = new ScoreRefreshThread(this::loadScores, 5000);
            new Thread(refreshThread).start();
            btn.setText("🔄 Auto-Refresh: ON");
            btn.setBackground(new Color(46, 204, 113));
        } else {
            // Stop auto-refresh thread
            refreshThread.stop();
            btn.setText("🔄 Auto-Refresh: OFF");
            btn.setBackground(new Color(149, 165, 166));
        }
    }

    private void loadMatchCombo() {
        matchCombo.removeAllItems();
        List<Match> matches = matchDAO.getAll();
        for (Match m : matches) {
            matchCombo.addItem(m.getMatchId() + " - " + m.getMatchDate() + " (" + m.getStatus() + ")");
        }
    }

    private void loadTeamCombo() {
        teamCombo.removeAllItems();
        List<Team> teams = teamDAO.getAll();
        for (Team t : teams) {
            teamCombo.addItem(t.getTeamId() + " - " + t.getTeamName());
        }
    }

    private int getIdFromCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return 0;
        return Integer.parseInt(combo.getSelectedItem().toString().split(" - ")[0]);
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(12);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(field, gbc);
        return field;
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

    private void loadScores() {
        tableModel.setRowCount(0);
        List<Score> scores = scoreDAO.getAll();
        for (Score s : scores) {
            tableModel.addRow(new Object[]{
                s.getScoreId(), s.getMatchId(), s.getTeamId(),
                s.getRuns(), s.getWickets(), s.getOvers(), s.getExtras()
            });
        }
    }

    private void addScore() {
        try {
            Score s = new Score(getIdFromCombo(matchCombo), getIdFromCombo(teamCombo),
                    Integer.parseInt(runsField.getText().trim()),
                    Integer.parseInt(wicketsField.getText().trim()),
                    Double.parseDouble(oversField.getText().trim()),
                    Integer.parseInt(extrasField.getText().trim()));
            if (scoreDAO.add(s)) {
                JOptionPane.showMessageDialog(this, "Score added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadScores();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateScore() {
        int row = scoreTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a score!"); return; }
        try {
            int scoreId = (int) tableModel.getValueAt(row, 0);
            Score s = new Score(scoreId, getIdFromCombo(matchCombo), getIdFromCombo(teamCombo),
                    Integer.parseInt(runsField.getText().trim()),
                    Integer.parseInt(wicketsField.getText().trim()),
                    Double.parseDouble(oversField.getText().trim()),
                    Integer.parseInt(extrasField.getText().trim()));
            if (scoreDAO.update(s)) {
                JOptionPane.showMessageDialog(this, "Score updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadScores();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numbers!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteScore() {
        int row = scoreTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a score!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (scoreDAO.delete((int) tableModel.getValueAt(row, 0))) {
                JOptionPane.showMessageDialog(this, "Score deleted!");
                loadScores();
            }
        }
    }
}
