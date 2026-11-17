/*
 * ============================================================
 * AuctionPanel.java — Multi-Year PDF & Live Auction GUI
 * Package: gui
 * ============================================================
 *
 * SWING TABS:
 * 1. 📂 PDF Document Archive: Upload & store multi-year PDFs in MySQL (LONGBLOB)
 * 2. 📊 10-Year Price History: Search any player's past sales records
 * 3. 🔨 Live Auction Arena: Dynamic bids, popularity factors, and live bidding!
 */

package gui;

import dao.AuctionDocumentDAO;
import dao.AuctionHistoryDAO;
import dao.LiveAuctionDAO;
import dao.TeamDAO;
import model.AuctionDocument;
import model.AuctionHistory;
import model.LiveAuction;
import model.Team;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AuctionPanel extends JPanel {

    private JTabbedPane tabbedPane;

    // DAOs
    private AuctionDocumentDAO docDAO;
    private AuctionHistoryDAO historyDAO;
    private LiveAuctionDAO liveDAO;
    private TeamDAO teamDAO;

    // Components - Tab 1 (PDF Manager)
    private JTable docTable;
    private DefaultTableModel docTableModel;
    private JComboBox<Integer> yearCombo;

    // Components - Tab 2 (History Search)
    private JTextField playerSearchField;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;

    // Components - Tab 3 (Live Auction Arena)
    private JTable liveTable;
    private DefaultTableModel liveTableModel;
    private JComboBox<String> biddingTeamCombo;
    private JLabel currentBidDisplay;
    private JLabel activePlayerDisplay;
    private JLabel calculatedBidDisplay;
    private LiveAuction selectedAuctionItem = null;

    public AuctionPanel() {
        docDAO = new AuctionDocumentDAO();
        historyDAO = new AuctionHistoryDAO();
        liveDAO = new LiveAuctionDAO();
        teamDAO = new TeamDAO();

        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241));

        // ── Top Title ──
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(23, 32, 42));
        titlePanel.setPreferredSize(new Dimension(0, 50));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("🔨 IPL Mega Auction & 10-Year Historical Analytics");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(241, 196, 15)); // Gold
        titlePanel.add(title, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // ── Tabbed Pane ──
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("📂 10-Year PDF Archive", createPdfManagerPanel());
        tabbedPane.addTab("📊 10-Year Player Price History", createHistoryPanel());
        tabbedPane.addTab("🔨 Live Auction & Bidding Arena", createLiveAuctionPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ============================================================
    // TAB 1: PDF Document Archive (Upload & Store PDFs in DB)
    // ============================================================
    private JPanel createPdfManagerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        // Top bar: Select Year & Upload PDF button
        JPanel uploadBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        uploadBar.setBackground(Color.WHITE);
        uploadBar.setBorder(BorderFactory.createTitledBorder("Upload Official IPL Auction List PDF"));

        uploadBar.add(new JLabel("Auction Year:"));
        yearCombo = new JComboBox<>(new Integer[]{2025, 2024, 2023, 2022, 2021, 2020, 2019, 2018, 2017, 2016, 2015});
        uploadBar.add(yearCombo);

        JButton uploadBtn = new JButton("📁 Choose & Store PDF to Database");
        uploadBtn.setBackground(new Color(46, 204, 113));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        uploadBtn.setFocusPainted(false);
        uploadBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        uploadBtn.addActionListener(e -> handleUploadPDF());
        uploadBar.add(uploadBtn);

        JButton refreshBtn = new JButton("🔄 Refresh List");
        refreshBtn.setBackground(new Color(52, 152, 219));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> loadUploadedPdfs());
        uploadBar.add(refreshBtn);

        JButton downloadBtn = new JButton("💾 Download Selected PDF");
        downloadBtn.setBackground(new Color(155, 89, 182));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.addActionListener(e -> handleDownloadPDF());
        uploadBar.add(downloadBtn);

        panel.add(uploadBar, BorderLayout.NORTH);

        // Table showing stored PDFs in MySQL
        String[] cols = {"Doc ID", "Year", "File Name", "Size (KB)", "Upload Timestamp"};
        docTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        docTable = new JTable(docTableModel);
        docTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        docTable.setRowHeight(28);
        docTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(new JScrollPane(docTable), BorderLayout.CENTER);

        loadUploadedPdfs();
        return panel;
    }

    private void handleUploadPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select IPL Auction PDF");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                JOptionPane.showMessageDialog(this, "Please select a valid PDF file!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int year = (Integer) yearCombo.getSelectedItem();
            boolean ok = docDAO.uploadPDF(year, file);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "✅ PDF successfully uploaded and stored in MySQL database!\n" +
                    "File: " + file.getName() + " (" + (file.length() / 1024) + " KB)",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUploadedPdfs();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to upload PDF into database.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDownloadPDF() {
        int r = docTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Please select a PDF document from the table!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int docId = (int) docTableModel.getValueAt(r, 0);
        String name = docTableModel.getValueAt(r, 2).toString();

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(name));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            if (docDAO.downloadPDF(docId, chooser.getSelectedFile())) {
                JOptionPane.showMessageDialog(this, "PDF downloaded successfully from MySQL database!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void loadUploadedPdfs() {
        docTableModel.setRowCount(0);
        List<AuctionDocument> docs = docDAO.getAllDocuments();
        for (AuctionDocument d : docs) {
            docTableModel.addRow(new Object[]{
                d.getDocId(), d.getAuctionYear(), d.getFileName(),
                String.format("%.1f", d.getFileSizeKb()),
                d.getUploadDate()
            });
        }
    }

    // ============================================================
    // TAB 2: 10-Year Player Price History
    // ============================================================
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        // Search Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(Color.WHITE);
        searchBar.setBorder(BorderFactory.createTitledBorder("Search 10-Year Player Track Record"));

        searchBar.add(new JLabel("Player Name:"));
        playerSearchField = new JTextField(18);
        searchBar.add(playerSearchField);

        JButton searchBtn = new JButton("🔍 Search History");
        searchBtn.setBackground(new Color(52, 152, 219));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.addActionListener(e -> searchPlayerHistory());
        searchBar.add(searchBtn);

        JButton viewYearBtn = new JButton("📅 View by Year (2024)");
        viewYearBtn.setBackground(new Color(149, 165, 166));
        viewYearBtn.setForeground(Color.WHITE);
        viewYearBtn.addActionListener(e -> loadHistoryByYear(2024));
        searchBar.add(viewYearBtn);

        panel.add(searchBar, BorderLayout.NORTH);

        // History Table
        String[] cols = {"Year", "Player Name", "Role", "Country", "Base (₹ Lakhs)", "Sold (₹ Lakhs)", "Team", "Status"};
        historyTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        historyTable.setRowHeight(28);
        historyTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        panel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        searchPlayerHistory();
        return panel;
    }

    private void searchPlayerHistory() {
        historyTableModel.setRowCount(0);
        String name = playerSearchField.getText().trim();
        List<AuctionHistory> list = historyDAO.getPlayerHistory(name);
        for (AuctionHistory h : list) {
            historyTableModel.addRow(new Object[]{
                h.getAuctionYear(), h.getPlayerName(), h.getRole(), h.getCountry(),
                h.getBasePrice(), h.getSoldPrice(), h.getBoughtByTeam(), h.getStatus()
            });
        }
    }

    private void loadHistoryByYear(int yr) {
        historyTableModel.setRowCount(0);
        List<AuctionHistory> list = historyDAO.getByYear(yr);
        for (AuctionHistory h : list) {
            historyTableModel.addRow(new Object[]{
                h.getAuctionYear(), h.getPlayerName(), h.getRole(), h.getCountry(),
                h.getBasePrice(), h.getSoldPrice(), h.getBoughtByTeam(), h.getStatus()
            });
        }
    }

    // ============================================================
    // TAB 3: Live Auction Arena with Smart Dynamic Starting Bids
    // ============================================================
    private JPanel createLiveAuctionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(236, 240, 241));

        // ── Left: Interactive Bidding Control Console ──
        JPanel console = new JPanel(new GridBagLayout());
        console.setPreferredSize(new Dimension(340, 0));
        console.setBackground(Color.WHITE);
        console.setBorder(BorderFactory.createTitledBorder("Live Bidding Room"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        activePlayerDisplay = new JLabel("Select a Player Below", SwingConstants.CENTER);
        activePlayerDisplay.setFont(new Font("Segoe UI", Font.BOLD, 16));
        activePlayerDisplay.setForeground(new Color(41, 128, 185));
        console.add(activePlayerDisplay, gbc);

        gbc.gridy = row++;
        calculatedBidDisplay = new JLabel("Smart Starting Bid: ₹0 Lakhs", SwingConstants.CENTER);
        calculatedBidDisplay.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        calculatedBidDisplay.setForeground(new Color(39, 174, 96));
        console.add(calculatedBidDisplay, gbc);

        gbc.gridy = row++;
        currentBidDisplay = new JLabel("Current Bid: ₹0 Lakhs", SwingConstants.CENTER);
        currentBidDisplay.setFont(new Font("Segoe UI", Font.BOLD, 18));
        currentBidDisplay.setForeground(new Color(192, 57, 43));
        console.add(currentBidDisplay, gbc);

        // Bidding Team
        gbc.gridy = row++; gbc.gridwidth = 1;
        console.add(new JLabel("Bidding Team:"), gbc);
        gbc.gridx = 1;
        biddingTeamCombo = new JComboBox<>();
        loadTeamsIntoCombo();
        console.add(biddingTeamCombo, gbc);

        // Bid Increment Buttons
        JPanel incPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        incPanel.setOpaque(false);
        JButton b20 = new JButton("+ ₹20 L");
        JButton b50 = new JButton("+ ₹50 L");
        JButton b100 = new JButton("+ ₹1 Cr");
        b20.setBackground(new Color(52, 152, 219)); b20.setForeground(Color.WHITE);
        b50.setBackground(new Color(41, 128, 185)); b50.setForeground(Color.WHITE);
        b100.setBackground(new Color(142, 68, 173)); b100.setForeground(Color.WHITE);

        b20.addActionListener(e -> incrementBid(20.0));
        b50.addActionListener(e -> incrementBid(50.0));
        b100.addActionListener(e -> incrementBid(100.0));

        incPanel.add(b20); incPanel.add(b50); incPanel.add(b100);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        console.add(incPanel, gbc);

        // Action Buttons: SOLD and UNSOLD
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        actionPanel.setOpaque(false);
        JButton soldBtn = new JButton("🔨 SOLD!");
        soldBtn.setBackground(new Color(39, 174, 96));
        soldBtn.setForeground(Color.WHITE);
        soldBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        soldBtn.addActionListener(e -> handleSold());

        JButton unsoldBtn = new JButton("❌ UNSOLD");
        unsoldBtn.setBackground(new Color(192, 57, 43));
        unsoldBtn.setForeground(Color.WHITE);
        unsoldBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        unsoldBtn.addActionListener(e -> handleUnsold());

        actionPanel.add(soldBtn);
        actionPanel.add(unsoldBtn);

        gbc.gridy = row++;
        console.add(actionPanel, gbc);

        panel.add(console, BorderLayout.WEST);

        // ── Right: Live Players Pool Table ──
        String[] cols = {"ID", "Player Name", "Role", "Base (₹L)", "10-Yr Avg (₹L)", "Smart Start Bid (₹L)", "Current Bid (₹L)", "Highest Bidder", "Status"};
        liveTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        liveTable = new JTable(liveTableModel);
        liveTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        liveTable.setRowHeight(28);
        liveTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        liveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        liveTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int r = liveTable.getSelectedRow();
                if (r >= 0) {
                    selectedAuctionItem = new LiveAuction(
                        (int) liveTableModel.getValueAt(r, 0),
                        liveTableModel.getValueAt(r, 1).toString(),
                        liveTableModel.getValueAt(r, 2).toString(),
                        "India",
                        (double) liveTableModel.getValueAt(r, 3),
                        (double) liveTableModel.getValueAt(r, 4),
                        (double) liveTableModel.getValueAt(r, 5),
                        (double) liveTableModel.getValueAt(r, 6),
                        liveTableModel.getValueAt(r, 7).toString(),
                        liveTableModel.getValueAt(r, 8).toString()
                    );
                    activePlayerDisplay.setText(selectedAuctionItem.getPlayerName() + " (" + selectedAuctionItem.getRole() + ")");
                    calculatedBidDisplay.setText("Smart Starting Bid: ₹" + selectedAuctionItem.getCalculatedBid() + " L");
                    currentBidDisplay.setText("Current Bid: ₹" + selectedAuctionItem.getCurrentBid() + " L (" + selectedAuctionItem.getHighestBidder() + ")");
                }
            }
        });

        panel.add(new JScrollPane(liveTable), BorderLayout.CENTER);

        loadLiveAuctions();
        return panel;
    }

    private void loadTeamsIntoCombo() {
        biddingTeamCombo.removeAllItems();
        List<Team> teams = teamDAO.getAll();
        for (Team t : teams) {
            biddingTeamCombo.addItem(t.getTeamName());
        }
    }

    private void loadLiveAuctions() {
        liveTableModel.setRowCount(0);
        List<LiveAuction> items = liveDAO.getAllLiveAuctions();
        for (LiveAuction a : items) {
            liveTableModel.addRow(new Object[]{
                a.getAuctionId(), a.getPlayerName(), a.getRole(),
                a.getBasePrice(), a.getHistoricalAvg(), a.getCalculatedBid(),
                a.getCurrentBid(), a.getHighestBidder(), a.getStatus()
            });
        }
    }

    private void incrementBid(double amountLakhs) {
        if (selectedAuctionItem == null) {
            JOptionPane.showMessageDialog(this, "Select a player from the table to place a bid!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String team = (String) biddingTeamCombo.getSelectedItem();
        double newBid = selectedAuctionItem.getCurrentBid() + amountLakhs;

        boolean ok = liveDAO.placeBid(selectedAuctionItem.getAuctionId(), team, newBid);
        if (ok) {
            selectedAuctionItem.setCurrentBid(newBid);
            selectedAuctionItem.setHighestBidder(team);
            currentBidDisplay.setText("Current Bid: ₹" + newBid + " L (" + team + ")");
            loadLiveAuctions();
        } else {
            JOptionPane.showMessageDialog(this, "Team " + team + " does not have sufficient purse budget for this bid!", "Purse Limit Exceeded", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSold() {
        if (selectedAuctionItem == null) return;
        String team = selectedAuctionItem.getHighestBidder();
        if (team.equals("None")) {
            JOptionPane.showMessageDialog(this, "Place at least one bid before hammering SOLD!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "🔨 SOLD!\n\nPlayer: " + selectedAuctionItem.getPlayerName() +
            "\nSold to: " + team +
            "\nFinal Price: ₹" + selectedAuctionItem.getCurrentBid() + " Lakhs",
            "Confirm Sale", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            liveDAO.markSold(selectedAuctionItem.getAuctionId(), team, selectedAuctionItem.getCurrentBid());
            JOptionPane.showMessageDialog(this, "Player successfully assigned to " + team + " and purse deducted!", "Done", JOptionPane.INFORMATION_MESSAGE);
            loadLiveAuctions();
        }
    }

    private void handleUnsold() {
        if (selectedAuctionItem == null) return;
        liveDAO.markUnsold(selectedAuctionItem.getAuctionId());
        JOptionPane.showMessageDialog(this, selectedAuctionItem.getPlayerName() + " marked UNSOLD.", "Unsold", JOptionPane.INFORMATION_MESSAGE);
        loadLiveAuctions();
    }
}
