/*
 * ============================================================
 * DashboardFrame.java — Main Navigation Dashboard
 * Package: gui
 * ============================================================
 *
 * SWING COMPONENTS USED:
 * ──────────────────────
 * JFrame     → Main application window
 * JPanel     → Sidebar navigation + content area
 * JButton    → Navigation buttons (Teams, Players, Matches, etc.)
 * CardLayout → Switches between different panels WITHOUT opening new windows
 *
 * WHAT IS CardLayout?
 * ───────────────────
 * CardLayout is like a stack of cards — only ONE card (panel) is visible at a time.
 * When you click "Teams", it shows the TeamPanel.
 * When you click "Players", it hides TeamPanel and shows PlayerPanel.
 * All panels are pre-loaded; CardLayout just controls which one is visible.
 */

package gui;

import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class DashboardFrame extends JFrame {

    private JPanel contentPanel;       // The area where different panels are shown
    private CardLayout cardLayout;     // Controls which panel is visible
    private Admin currentAdmin;        // The logged-in admin

    // Panel name constants (used as "card" names)
    private static final String PANEL_TEAMS    = "TEAMS";
    private static final String PANEL_PLAYERS  = "PLAYERS";
    private static final String PANEL_MATCHES  = "MATCHES";
    private static final String PANEL_SCORES   = "SCORES";
    private static final String PANEL_POINTS   = "POINTS";
    private static final String PANEL_STATS    = "STATS";
    private static final String PANEL_AUCTION  = "AUCTION";

    public DashboardFrame(Admin admin) {
        this.currentAdmin = admin;

        // ── JFrame Configuration ──────────────────────────────
        setTitle("🏏 IPL Management System — Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());  // BorderLayout: North, South, East, West, Center

        // ── Top Header Panel ──────────────────────────────────
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // ── Left Sidebar Panel (Navigation) ───────────────────
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // ── Center Content Panel (CardLayout) ─────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(236, 240, 241));

        // Add all module panels as "cards"
        contentPanel.add(new TeamPanel(), PANEL_TEAMS);
        contentPanel.add(new PlayerPanel(), PANEL_PLAYERS);
        contentPanel.add(new MatchPanel(), PANEL_MATCHES);
        contentPanel.add(new ScorePanel(), PANEL_SCORES);
        contentPanel.add(new PointsTablePanel(), PANEL_POINTS);
        contentPanel.add(new PlayerStatsPanel(), PANEL_STATS);
        contentPanel.add(new AuctionPanel(), PANEL_AUCTION);

        add(contentPanel, BorderLayout.CENTER);

        // Show Teams panel by default
        cardLayout.show(contentPanel, PANEL_TEAMS);

        setVisible(true);
    }

    // ── Create Header Panel ───────────────────────────────────
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(23, 32, 42));
        header.setPreferredSize(new Dimension(0, 60));

        // Title
        JLabel title = new JLabel("  🏏 IPL Cricket League Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(46, 204, 113));
        header.add(title, BorderLayout.WEST);

        // Welcome message + Logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentAdmin.getFullName() + "  ");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(Color.WHITE);
        rightPanel.add(welcomeLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrame();
            }
        });
        rightPanel.add(logoutButton);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // ── Create Sidebar Panel ──────────────────────────────────
    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Navigation buttons
        String[][] navItems = {
            {"🏢 Teams",       PANEL_TEAMS},
            {"👤 Players",     PANEL_PLAYERS},
            {"🏟️ Matches",    PANEL_MATCHES},
            {"📊 Scores",      PANEL_SCORES},
            {"📋 Points Table",PANEL_POINTS},
            {"📈 Statistics",  PANEL_STATS},
            {"🔨 Auction",     PANEL_AUCTION}
        };

        for (String[] item : navItems) {
            JButton btn = createNavButton(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return sidebar;
    }

    // ── Create a navigation button ────────────────────────────
    private JButton createNavButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(46, 204, 113));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });

        // Switch to the corresponding panel when clicked
        button.addActionListener(e -> cardLayout.show(contentPanel, panelName));

        return button;
    }
}
