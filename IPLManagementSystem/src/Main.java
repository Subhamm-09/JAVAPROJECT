/*
 * ============================================================
 * Main.java — Application Entry Point
 * ============================================================
 *
 * This is the STARTING POINT of the entire application.
 * The JVM (Java Virtual Machine) looks for: public static void main(String[] args)
 *
 * FLOW:
 * 1. Main.java runs
 * 2. Sets the look-and-feel (visual theme)
 * 3. Opens LoginFrame
 * 4. After successful login → Dashboard opens
 * 5. Dashboard shows all module panels
 *
 * WHY SwingUtilities.invokeLater()?
 * ─────────────────────────────────
 * Swing is NOT thread-safe. All GUI operations MUST run on the
 * Event Dispatch Thread (EDT). invokeLater() schedules our code
 * to run on the EDT, ensuring thread safety.
 */

import gui.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        // ── Set Look and Feel (visual theme) ──────────────────
        try {
            // Use the system's native look and feel (Windows on Windows, Mac on Mac)
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If it fails, Swing uses its default "Metal" look
            System.out.println("⚠️ Could not set system look and feel. Using default.");
        }

        // ── Launch the application on the EDT ─────────────────
        // SwingUtilities.invokeLater() ensures GUI creation happens on the correct thread
        SwingUtilities.invokeLater(() -> {
            System.out.println("🏏 IPL Management System starting...");
            new LoginFrame();   // Open the Login screen
        });
    }
}
