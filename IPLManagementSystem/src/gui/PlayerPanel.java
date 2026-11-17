/*
 * ============================================================
 * PlayerPanel.java — Player Management CRUD Panel
 * Package: gui
 * ============================================================
 *
 * SWING: JPanel, JTable, JComboBox, JScrollPane, JButton
 * FEATURES: CRUD + filter players by team using JComboBox
 */

package gui;

import dao.PlayerDAO;
import dao.TeamDAO;
import model.Player;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlayerPanel extends JPanel {

    private JTable playerTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, ageField, matchesField, runsField, wicketsField, batAvgField, bowlAvgField;
    private JComboBox<String> teamCombo, roleCombo;
    private PlayerDAO playerDAO;
    private TeamDAO teamDAO;
    private List<Team> teamList;

    public PlayerPanel() {
        playerDAO = new PlayerDAO();
        teamDAO = new TeamDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title ─────────────────────────────────────────────
        JLabel title = new JLabel("👤 Player Management", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // ── Form Panel ────────────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Player Details"));
        formPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        nameField = addFormField(formPanel, gbc, "Player Name:", row++);

        // Team combo box
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Team:"), gbc);
        gbc.gridx = 1;
        teamCombo = new JComboBox<>();
        loadTeamCombo();
        formPanel.add(teamCombo, gbc);
        row++;

        // Role combo box
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"Batsman", "Bowler", "All-rounder", "Wicketkeeper"});
        formPanel.add(roleCombo, gbc);
        row++;

        ageField = addFormField(formPanel, gbc, "Age:", row++);
        matchesField = addFormField(formPanel, gbc, "Matches:", row++);
        runsField = addFormField(formPanel, gbc, "Runs:", row++);
        wicketsField = addFormField(formPanel, gbc, "Wickets:", row++);
        batAvgField = addFormField(formPanel, gbc, "Batting Avg:", row++);
        bowlAvgField = addFormField(formPanel, gbc, "Bowling Avg:", row++);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonPanel.setOpaque(false);

        JButton addBtn = createButton("➕ Add", new Color(46, 204, 113));
        JButton updateBtn = createButton("✏️ Update", new Color(52, 152, 219));
        JButton deleteBtn = createButton("🗑️ Delete", new Color(231, 76, 60));
        JButton clearBtn = createButton("🔄 Clear", new Color(149, 165, 166));

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 5, 8);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // ── Table ─────────────────────────────────────────────
        String[] columns = {"ID", "Name", "Team ID", "Role", "Age", "Matches", "Runs", "Wickets", "Bat Avg", "Bowl Avg"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        playerTable = new JTable(tableModel);
        playerTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        playerTable.setRowHeight(26);
        playerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        playerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = playerTable.getSelectedRow();
                if (r >= 0) {
                    nameField.setText(tableModel.getValueAt(r, 1).toString());
                    selectTeamInCombo((int) tableModel.getValueAt(r, 2));
                    roleCombo.setSelectedItem(tableModel.getValueAt(r, 3).toString());
                    ageField.setText(tableModel.getValueAt(r, 4).toString());
                    matchesField.setText(tableModel.getValueAt(r, 5).toString());
                    runsField.setText(tableModel.getValueAt(r, 6).toString());
                    wicketsField.setText(tableModel.getValueAt(r, 7).toString());
                    batAvgField.setText(tableModel.getValueAt(r, 8).toString());
                    bowlAvgField.setText(tableModel.getValueAt(r, 9).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(playerTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button actions
        addBtn.addActionListener(e -> addPlayer());
        updateBtn.addActionListener(e -> updatePlayer());
        deleteBtn.addActionListener(e -> deletePlayer());
        clearBtn.addActionListener(e -> clearFields());

        loadPlayers();
    }

    private void loadTeamCombo() {
        teamCombo.removeAllItems();
        teamList = teamDAO.getAll();
        for (Team t : teamList) {
            teamCombo.addItem(t.getTeamId() + " - " + t.getTeamName());
        }
    }

    private int getSelectedTeamId() {
        if (teamCombo.getSelectedItem() == null) return 0;
        String selected = teamCombo.getSelectedItem().toString();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void selectTeamInCombo(int teamId) {
        for (int i = 0; i < teamCombo.getItemCount(); i++) {
            if (teamCombo.getItemAt(i).startsWith(teamId + " - ")) {
                teamCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JTextField field = new JTextField(12);
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

    private void loadPlayers() {
        tableModel.setRowCount(0);
        List<Player> players = playerDAO.getAll();
        for (Player p : players) {
            tableModel.addRow(new Object[]{
                p.getPlayerId(), p.getPlayerName(), p.getTeamId(),
                p.getRole(), p.getAge(), p.getMatchesPlayed(),
                p.getRunsScored(), p.getWicketsTaken(),
                p.getBattingAvg(), p.getBowlingAvg()
            });
        }
    }

    private void addPlayer() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Player name is required!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Player p = new Player(
                nameField.getText().trim(), getSelectedTeamId(),
                roleCombo.getSelectedItem().toString(),
                Integer.parseInt(ageField.getText().trim()),
                Integer.parseInt(matchesField.getText().trim()),
                Integer.parseInt(runsField.getText().trim()),
                Integer.parseInt(wicketsField.getText().trim()),
                Double.parseDouble(batAvgField.getText().trim()),
                Double.parseDouble(bowlAvgField.getText().trim())
            );
            if (playerDAO.add(p)) {
                JOptionPane.showMessageDialog(this, "Player added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPlayers();
                clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for all numeric fields!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updatePlayer() {
        int row = playerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a player to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int playerId = (int) tableModel.getValueAt(row, 0);
            Player p = new Player(
                playerId, nameField.getText().trim(), getSelectedTeamId(),
                roleCombo.getSelectedItem().toString(),
                Integer.parseInt(ageField.getText().trim()),
                Integer.parseInt(matchesField.getText().trim()),
                Integer.parseInt(runsField.getText().trim()),
                Integer.parseInt(wicketsField.getText().trim()),
                Double.parseDouble(batAvgField.getText().trim()),
                Double.parseDouble(bowlAvgField.getText().trim())
            );
            if (playerDAO.update(p)) {
                JOptionPane.showMessageDialog(this, "Player updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPlayers();
                clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletePlayer() {
        int row = playerTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a player to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this player?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int playerId = (int) tableModel.getValueAt(row, 0);
            if (playerDAO.delete(playerId)) {
                JOptionPane.showMessageDialog(this, "Player deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPlayers();
                clearFields();
            }
        }
    }

    private void clearFields() {
        nameField.setText(""); ageField.setText(""); matchesField.setText("");
        runsField.setText(""); wicketsField.setText("");
        batAvgField.setText(""); bowlAvgField.setText("");
        playerTable.clearSelection();
    }
}
