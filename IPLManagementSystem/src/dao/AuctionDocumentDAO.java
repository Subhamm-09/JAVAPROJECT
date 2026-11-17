/*
 * ============================================================
 * AuctionDocumentDAO.java — Storing & Fetching PDFs from MySQL
 * Package: dao
 * ============================================================
 *
 * JAVA CONCEPTS DEMONSTRATED:
 * 1. JDBC BLOB Handling: PreparedStatement.setBytes() / setBinaryStream()
 * 2. File Handling: Storing actual PDF files in MySQL database
 * 3. Exception Handling: try-catch with SQL error logging
 */

package dao;

import connection.DBConnection;
import model.AuctionDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuctionDocumentDAO {

    // ── Save PDF file directly into MySQL database (LONGBLOB) ──
    public boolean uploadPDF(int auctionYear, File file) {
        String sql = "INSERT INTO auction_documents (auction_year, file_name, file_size_kb, pdf_data) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             FileInputStream fis = new FileInputStream(file)) {

            double sizeKb = file.length() / 1024.0;

            pstmt.setInt(1, auctionYear);
            pstmt.setString(2, file.getName());
            pstmt.setDouble(3, sizeKb);
            // setBinaryStream streams the raw PDF binary directly into the database!
            pstmt.setBinaryStream(4, fis, (int) file.length());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException | IOException e) {
            System.err.println("❌ Failed to store PDF in MySQL: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Get all uploaded PDF records (metadata without heavy blob) ──
    public List<AuctionDocument> getAllDocuments() {
        List<AuctionDocument> docs = new ArrayList<>();
        String sql = "SELECT doc_id, auction_year, file_name, file_size_kb, upload_date FROM auction_documents ORDER BY auction_year DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                docs.add(new AuctionDocument(
                    rs.getInt("doc_id"),
                    rs.getInt("auction_year"),
                    rs.getString("file_name"),
                    rs.getDouble("file_size_kb"),
                    rs.getTimestamp("upload_date"),
                    null // don't load binary into memory here
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return docs;
    }

    // ── Download/Export stored PDF from MySQL to a local file ──
    public boolean downloadPDF(int docId, File destinationFile) {
        String sql = "SELECT pdf_data FROM auction_documents WHERE doc_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, docId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] bytes = rs.getBytes("pdf_data");
                try (FileOutputStream fos = new FileOutputStream(destinationFile)) {
                    fos.write(bytes);
                }
                return true;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ── Delete PDF from MySQL ──
    public boolean deleteDocument(int docId) {
        String sql = "DELETE FROM auction_documents WHERE doc_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, docId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
