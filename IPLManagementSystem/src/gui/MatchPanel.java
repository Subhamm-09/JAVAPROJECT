/*
 * ============================================================
 * MatchPanel.java — Match Management Panel
 * Package: gui
 * ============================================================
 *
 * SWING: JPanel, JTable, JComboBox (team selection), JTextField
 * FEATURES: Schedule matches, update status, set winner
 */

package gui;

import dao.MatchDAO;
import dao.TeamDAO;
import model.Match;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MatchPanel extends JPanel {

    private JTable matchTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> team1Combo, team2Combo, statusCombo, winnerCombo;
    private JTextField dateField, venueField;
    private MatchDAO matchDAO;
    private TeamDAO teamDAO;
    private List<Team> teamList;

    public MatchPanel() {
        matchDAO = new MatchDAO();
        teamDAO = new TeamDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("🏟️ Match Management", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // ── Form Panel ────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Match Details"));
        formPanel.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        teamList = teamDAO.getAll();

        int row = 0;

        // Team 1
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Team 1:"), gbc);
        gbc.gridx = 1;
        team1Combo = new JComboBox<>();
        for (Team t : teamList) team1Combo.addItem(t.getTeamId() + " - " + t.getTeamName());
        formPanel.add(team1Combo, gbc);
        row++;

        // Team 2
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Team 2:"), gbc);
        gbc.gridx = 1;
        team2Combo = new JComboBox<>();
        for (Team t : teamList) team2Combo.addItem(t.getTeamId() + " - " + t.getTeamName());
        formPanel.add(team2Combo, gbc);
        row++;

        // Date
        dateField = addFormField(formPanel, gbc, "Date (YYYY-MM-DD):", row++);

        // Venue
        venueField = addFormField(formPanel, gbc, "Venue:", row++);

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        statusCombo = new JComboBox<>(new String[]{"Scheduled", "Live", "Completed"});
        formPanel.add(statusCombo, gbc);
        row++;

        // Winner
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Winner:"), gbc);
        gbc.gridx = 1;
        winnerCombo = new JComboBox<>();
        winnerCombo.addItem("-- Not decided --");
        for (Team t : teamList) winnerCombo.addItem(t.getTeamId() + " - " + t.getTeamName());
        formPanel.add(winnerCombo, gbc);
        row++;

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);
        JButton addBtn = createButton("➕ Schedule", new Color(46, 204, 113));
        JButton updateBtn = createButton("✏️ Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("🗑️ Delete", new Color(231, 76, 60));
        JButton clearBtn = createButton("🔄 Clear", new Color(149, 165, 166));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 5, 8);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // ── Table ─────────────────────────────────────────────
        String[] columns = {"ID", "Team 1 ID", "Team 2 ID", "Date", "Venue", "Status", "Winner ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        matchTable = new JTable(tableModel);
        matchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        matchTable.setRowHeight(26);
        matchTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = matchTable.getSelectedRow();
                if (r >= 0) {
                    selectComboByTeamId(team1Combo, (int) tableModel.getValueAt(r, 1));
                    selectComboByTeamId(team2Combo, (int) tableModel.getValueAt(r, 2));
                    dateField.setText(tableModel.getValueAt(r, 3).toString());
                    venueField.setText(tableModel.getValueAt(r, 4).toString());
                    statusCombo.setSelectedItem(tableModel.getValueAt(r, 5).toString());
                    int winnerId = (int) tableModel.getValueAt(r, 6);
                    if (winnerId > 0) selectComboByTeamId(winnerCombo, winnerId);
                    else winnerCombo.setSelectedIndex(0);
                }
            }
        });

        add(new JScrollPane(matchTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> addMatch());
        updateBtn.addActionListener(e -> updateMatch());
        deleteBtn.addActionListener(e -> deleteMatch());
        clearBtn.addActionListener(e -> clearFields());

        loadMatches();
    }

    private void selectComboByTeamId(JComboBox<String> combo, int teamId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).startsWith(teamId + " - ")) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private int getTeamIdFromCombo(JComboBox<String> combo) {
        if (combo.getSelectedItem() == null) return 0;
        String s = combo.getSelectedItem().toString();
        if (s.startsWith("--")) return 0;
        return Integer.parseInt(s.split(" - ")[0]);
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(14);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(field, gbc);
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadMatches() {
        tableModel.setRowCount(0);
        List<Match> matches = matchDAO.getAll();
        for (Match m : matches) {
            tableModel.addRow(new Object[]{
                m.getMatchId(), m.getTeam1Id(), m.getTeam2Id(),
                m.getMatchDate(), m.getVenue(), m.getStatus(), m.getWinnerTeamId()
            });
        }
    }

    private void addMatch() {
        if (dateField.getText().trim().isEmpty() || venueField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date and venue are required!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Match m = new Match(getTeamIdFromCombo(team1Combo), getTeamIdFromCombo(team2Combo),
                dateField.getText().trim(), venueField.getText().trim());
        m.setStatus(statusCombo.getSelectedItem().toString());
        if (matchDAO.add(m)) {
            JOptionPane.showMessageDialog(this, "Match scheduled!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMatches();
            clearFields();
        }
    }

    private void updateMatch() {
        int row = matchTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a match!"); return; }
        int matchId = (int) tableModel.getValueAt(row, 0);
        Match m = new Match(matchId, getTeamIdFromCombo(team1Combo), getTeamIdFromCombo(team2Combo),
                dateField.getText().trim(), venueField.getText().trim(),
                statusCombo.getSelectedItem().toString(), getTeamIdFromCombo(winnerCombo));
        if (matchDAO.update(m)) {
            JOptionPane.showMessageDialog(this, "Match updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMatches();
        }
    }

    private void deleteMatch() {
        int row = matchTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a match!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this match?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (matchDAO.delete((int) tableModel.getValueAt(row, 0))) {
                JOptionPane.showMessageDialog(this, "Match deleted!");
                loadMatches();
            }
        }
    }

    private void clearFields() {
        dateField.setText(""); venueField.setText("");
        statusCombo.setSelectedIndex(0);
        winnerCombo.setSelectedIndex(0);
        matchTable.clearSelection();
    }
}
