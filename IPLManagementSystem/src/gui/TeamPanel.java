/*
 * ============================================================
 * TeamPanel.java — Team Management CRUD Panel
 * Package: gui
 * ============================================================
 *
 * SWING: JPanel, JTable, JScrollPane, JTextField, JButton, JOptionPane
 * CRUD: Add, Update, Delete, View teams
 * DAO: Uses TeamDAO for all database operations
 */

package gui;

import dao.TeamDAO;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeamPanel extends JPanel {

    private JTable teamTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, cityField, coachField, captainField, yearField;
    private TeamDAO teamDAO;

    public TeamPanel() {
        teamDAO = new TeamDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title ─────────────────────────────────────────────
        JLabel title = new JLabel("🏢 Team Management", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        add(title, BorderLayout.NORTH);

        // ── Form Panel (Left) ─────────────────────────────────
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Team Details"));
        formPanel.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        nameField = addFormField(formPanel, gbc, "Team Name:", 0);
        cityField = addFormField(formPanel, gbc, "City:", 1);
        coachField = addFormField(formPanel, gbc, "Coach:", 2);
        captainField = addFormField(formPanel, gbc, "Captain:", 3);
        yearField = addFormField(formPanel, gbc, "Founded Year:", 4);

        // ── Buttons ───────────────────────────────────────────
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

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // ── Table (Center) ────────────────────────────────────
        String[] columns = {"ID", "Team Name", "City", "Coach", "Captain", "Founded"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table read-only
            }
        };
        teamTable = new JTable(tableModel);
        teamTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teamTable.setRowHeight(28);
        teamTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When a row is clicked, populate the form fields
        teamTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = teamTable.getSelectedRow();
                if (row >= 0) {
                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    cityField.setText(tableModel.getValueAt(row, 2).toString());
                    coachField.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
                    captainField.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
                    yearField.setText(tableModel.getValueAt(row, 5).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(teamTable);
        add(scrollPane, BorderLayout.CENTER);

        // ── Button Actions ────────────────────────────────────
        addBtn.addActionListener(e -> addTeam());
        updateBtn.addActionListener(e -> updateTeam());
        deleteBtn.addActionListener(e -> deleteTeam());
        clearBtn.addActionListener(e -> clearFields());

        // ── Load initial data ─────────────────────────────────
        loadTeams();
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String label, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gbc);

        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── CRUD Operations ───────────────────────────────────────
    private void loadTeams() {
        tableModel.setRowCount(0);  // Clear existing rows
        List<Team> teams = teamDAO.getAll();
        for (Team t : teams) {
            tableModel.addRow(new Object[]{
                t.getTeamId(), t.getTeamName(), t.getCity(),
                t.getCoach(), t.getCaptain(), t.getFoundedYear()
            });
        }
    }

    private void addTeam() {
        if (nameField.getText().trim().isEmpty() || cityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Team name and city are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Team team = new Team(
                nameField.getText().trim(),
                cityField.getText().trim(),
                coachField.getText().trim(),
                captainField.getText().trim(),
                Integer.parseInt(yearField.getText().trim())
            );

            if (teamDAO.add(team)) {
                JOptionPane.showMessageDialog(this, "Team added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeams();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add team.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Founded year must be a number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTeam() {
        int row = teamTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a team to update!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int teamId = (int) tableModel.getValueAt(row, 0);
            Team team = new Team(
                teamId,
                nameField.getText().trim(),
                cityField.getText().trim(),
                coachField.getText().trim(),
                captainField.getText().trim(),
                Integer.parseInt(yearField.getText().trim())
            );

            if (teamDAO.update(team)) {
                JOptionPane.showMessageDialog(this, "Team updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeams();
                clearFields();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Founded year must be a number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTeam() {
        int row = teamTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a team to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this team?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int teamId = (int) tableModel.getValueAt(row, 0);
            if (teamDAO.delete(teamId)) {
                JOptionPane.showMessageDialog(this, "Team deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeams();
                clearFields();
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        cityField.setText("");
        coachField.setText("");
        captainField.setText("");
        yearField.setText("");
        teamTable.clearSelection();
    }
}
