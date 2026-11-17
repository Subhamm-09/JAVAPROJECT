/*
 * ============================================================
 * ScoreRefreshThread.java — Multithreading Demo
 * Package: threads
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * ----------------------------
 * 1. Multithreading     → implements Runnable interface
 * 2. Thread             → Runs code in a SEPARATE thread (parallel to the main GUI thread)
 * 3. Runnable interface → The run() method contains the code that runs in the background
 * 4. Thread.sleep()     → Pauses the thread for a specified time (milliseconds)
 * 5. volatile keyword   → Ensures the 'running' flag is read fresh from memory each time
 *
 * WHY MULTITHREADING?
 * -------------------
 * The GUI runs on the "Event Dispatch Thread" (EDT).
 * If we do database queries on the EDT, the GUI FREEZES.
 * So we use a SEPARATE background thread to refresh scores periodically.
 *
 * HOW IT WORKS:
 * 1. Create: ScoreRefreshThread srt = new ScoreRefreshThread(callback, 5000);
 * 2. Start:  new Thread(srt).start();
 * 3. Stop:   srt.stop();
 *
 * The callback is a Runnable that updates the GUI with fresh data.
 * Every 'intervalMs' milliseconds, the thread calls the callback.
 */

package threads;

public class ScoreRefreshThread implements Runnable {

    // 'volatile' ensures this variable is always read from main memory,
    // not from a thread's local cache. Important for thread safety.
    private volatile boolean running = true;

    // The code to execute on each refresh cycle
    private final Runnable refreshCallback;

    // How often to refresh (in milliseconds)
    private final int intervalMs;

    // ── Constructor ───────────────────────────────────────────
    // refreshCallback: The method to call when refreshing
    // intervalMs: Time between refreshes (e.g., 5000 = 5 seconds)
    public ScoreRefreshThread(Runnable refreshCallback, int intervalMs) {
        this.refreshCallback = refreshCallback;
        this.intervalMs = intervalMs;
    }

    // ── The run() method — this is what runs in the background thread ──
    @Override
    public void run() {
        System.out.println("🔄 Score refresh thread started (every " + (intervalMs / 1000) + " seconds)");

        while (running) {  // Keeps running until stop() is called
            try {
                // Call the refresh callback (updates the GUI table)
                if (refreshCallback != null) {
                    // SwingUtilities.invokeLater ensures GUI updates happen on the EDT
                    javax.swing.SwingUtilities.invokeLater(refreshCallback);
                }

                // Thread.sleep(milliseconds) — pauses this thread
                // The GUI thread keeps running normally during this pause
                Thread.sleep(intervalMs);

            } catch (InterruptedException e) {
                // InterruptedException is thrown if the thread is interrupted during sleep
                System.out.println("⚠️ Score refresh thread interrupted.");
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("🛑 Score refresh thread stopped.");
    }

    // ── Stop the thread gracefully ────────────────────────────
    // Sets running to false — the while loop will exit on next check
    public void stop() {
        this.running = false;
    }

    // ── Check if thread is running ────────────────────────────
    public boolean isRunning() {
        return running;
    }
}
