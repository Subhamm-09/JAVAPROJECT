/*
 * ============================================================
 * PointsTablePanel.java — League Points Table Display
 * Package: gui
 * ============================================================
 *
 * SWING: JTable, JScrollPane, JButton
 * SQL: SELECT with ORDER BY points DESC, net_run_rate DESC
 * Shows league standings sorted by points and NRR
 */

package gui;

import dao.PointsTableDAO;
import dao.TeamDAO;
import model.PointsTable;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PointsTablePanel extends JPanel {

    private JTable pointsTable;
    private DefaultTableModel tableModel;
    private PointsTableDAO pointsTableDAO;
    private TeamDAO teamDAO;

    public PointsTablePanel() {
        pointsTableDAO = new PointsTableDAO();
        teamDAO = new TeamDAO();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ── Title + Refresh ───────────────────────────────────
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("📋 IPL Points Table", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(44, 62, 80));
        titlePanel.add(title, BorderLayout.WEST);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadPointsTable());
        titlePanel.add(refreshBtn, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────
        String[] columns = {"#", "Team", "P", "W", "L", "D", "Pts", "NRR"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        pointsTable = new JTable(tableModel);
        pointsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pointsTable.setRowHeight(32);
        pointsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        pointsTable.getTableHeader().setBackground(new Color(44, 62, 80));
        pointsTable.getTableHeader().setForeground(Color.WHITE);

        // Center-align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < columns.length; i++) {
            pointsTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        pointsTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // #
        pointsTable.getColumnModel().getColumn(1).setPreferredWidth(200);  // Team
        pointsTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // NRR

        JScrollPane scrollPane = new JScrollPane(pointsTable);
        add(scrollPane, BorderLayout.CENTER);

        loadPointsTable();
    }

    private void loadPointsTable() {
        tableModel.setRowCount(0);
        List<PointsTable> standings = pointsTableDAO.getAll();  // Already sorted by points DESC
        int rank = 1;
        for (PointsTable pt : standings) {
            Team team = teamDAO.getById(pt.getTeamId());
            String teamName = (team != null) ? team.getTeamName() : "Team " + pt.getTeamId();
            tableModel.addRow(new Object[]{
                rank++,
                teamName,
                pt.getMatchesPlayed(),
                pt.getWins(),
                pt.getLosses(),
                pt.getDraws(),
                pt.getPoints(),
                String.format("%+.2f", pt.getNetRunRate())
            });
        }
    }
}
