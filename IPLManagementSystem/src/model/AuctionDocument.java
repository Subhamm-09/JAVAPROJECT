/*
 * ============================================================
 * AuctionDocument.java — Model for Storing PDF Files in DB
 * Package: model
 * ============================================================
 *
 * JAVA CONCEPTS:
 * 1. Encapsulation: Private fields, public getters and setters
 * 2. BLOB Data: byte[] stores raw binary PDF files in MySQL
 */

package model;

import java.sql.Timestamp;

public class AuctionDocument {

    private int docId;
    private int auctionYear;
    private String fileName;
    private double fileSizeKb;
    private Timestamp uploadDate;
    private byte[] pdfData;       // Stores the raw PDF binary in memory / database

    public AuctionDocument() {}

    public AuctionDocument(int auctionYear, String fileName, double fileSizeKb, byte[] pdfData) {
        this.auctionYear = auctionYear;
        this.fileName = fileName;
        this.fileSizeKb = fileSizeKb;
        this.pdfData = pdfData;
    }

    public AuctionDocument(int docId, int auctionYear, String fileName, double fileSizeKb, Timestamp uploadDate, byte[] pdfData) {
        this.docId = docId;
        this.auctionYear = auctionYear;
        this.fileName = fileName;
        this.fileSizeKb = fileSizeKb;
        this.uploadDate = uploadDate;
        this.pdfData = pdfData;
    }

    public int getDocId() { return docId; }
    public void setDocId(int docId) { this.docId = docId; }

    public int getAuctionYear() { return auctionYear; }
    public void setAuctionYear(int auctionYear) { this.auctionYear = auctionYear; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public double getFileSizeKb() { return fileSizeKb; }
    public void setFileSizeKb(double fileSizeKb) { this.fileSizeKb = fileSizeKb; }

    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }

    public byte[] getPdfData() { return pdfData; }
    public void setPdfData(byte[] pdfData) { this.pdfData = pdfData; }
}
