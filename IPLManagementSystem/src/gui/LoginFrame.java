/*
 * ============================================================
 * LoginFrame.java — Login Screen (FIRST screen the user sees)
 * Package: gui
 * ============================================================
 *
 * SWING COMPONENTS USED:
 * ──────────────────────
 * JFrame         → The main window
 * JPanel         → Container to organise components
 * JLabel         → Text labels ("Username:", "Password:")
 * JTextField     → Text input for username
 * JPasswordField → Password input (shows dots instead of text)
 * JButton        → "Login" button
 * JOptionPane    → Popup message dialogs (success/error)
 *
 * HOW SWING EVENT HANDLING WORKS:
 * ──────────────────────────────
 * 1. User clicks "Login" button
 * 2. Java fires an ActionEvent
 * 3. The ActionListener we attached to the button receives the event
 * 4. Inside actionPerformed(), we run our login logic
 *
 * This is the Observer/Event-Listener pattern.
 */

package gui;

import dao.AdminDAO;
import model.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    // ── Declare GUI components ────────────────────────────────
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel titleLabel, usernameLabel, passwordLabel;

    // ── Constructor: Build the Login Screen ───────────────────
    public LoginFrame() {
        // ── JFrame Configuration ──────────────────────────────
        setTitle("🏏 IPL Management System — Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close app when window closes
        setLocationRelativeTo(null);  // Center the window on screen
        setResizable(false);

        // ── Main Panel with Background Color ──────────────────
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);  // Absolute positioning (null layout)
        mainPanel.setBackground(new Color(23, 32, 42));  // Dark blue background

        // ── Title Label ───────────────────────────────────────
        titleLabel = new JLabel("🏏 IPL MANAGEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setBounds(50, 20, 350, 40);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(46, 204, 113));  // Green text
        mainPanel.add(titleLabel);

        // ── Subtitle ─────────────────────────────────────────
        JLabel subtitleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        subtitleLabel.setBounds(50, 60, 350, 25);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        mainPanel.add(subtitleLabel);

        // ── Username Label ────────────────────────────────────
        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(60, 110, 100, 25);
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameLabel.setForeground(Color.WHITE);
        mainPanel.add(usernameLabel);

        // ── Username Text Field (JTextField) ──────────────────
        usernameField = new JTextField();
        usernameField.setBounds(170, 110, 200, 30);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(usernameField);

        // ── Password Label ────────────────────────────────────
        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(60, 160, 100, 25);
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);

        // ── Password Field (JPasswordField) ───────────────────
        // JPasswordField hides the typed characters with dots for security
        passwordField = new JPasswordField();
        passwordField.setBounds(170, 160, 200, 30);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(passwordField);

        // ── Login Button ──────────────────────────────────────
        loginButton = new JButton("🔐 Login");
        loginButton.setBounds(170, 220, 200, 40);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(loginButton);

        // ── Default credentials hint ──────────────────────────
        JLabel hintLabel = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hintLabel.setBounds(50, 275, 350, 20);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel);

        // ── Add ActionListener to Login Button ────────────────
        // When the button is clicked, actionPerformed() is called
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // ── Allow Enter key to trigger login ──────────────────
        passwordField.addActionListener(e -> handleLogin());

        // ── Add panel to frame and make visible ───────────────
        add(mainPanel);
        setVisible(true);
    }

    // ── Login Logic ───────────────────────────────────────────
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // ── Input Validation ──────────────────────────────────
        if (username.isEmpty() || password.isEmpty()) {
            // JOptionPane: Shows a popup dialog
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password!",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── Database Authentication (JDBC) ────────────────────
        AdminDAO adminDAO = new AdminDAO();
        Admin admin = adminDAO.login(username, password);

        if (admin != null) {
            // Login successful!
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + admin.getFullName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // Close login window and open Dashboard
            this.dispose();                      // Close this JFrame
            new DashboardFrame(admin);           // Open Dashboard
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");           // Clear password field
        }
    }
}
