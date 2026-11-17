-- ============================================================
-- IPL Auction & 10-Year Historical PDF Storage Schema
-- Database: ipl_management
-- ============================================================

USE ipl_management;

-- 1. Table to store the raw PDF files directly in MySQL (LONGBLOB)
CREATE TABLE IF NOT EXISTS auction_documents (
    doc_id          INT             AUTO_INCREMENT PRIMARY KEY,
    auction_year    INT             NOT NULL,               -- e.g., 2015, 2016 ... 2024, 2025
    file_name       VARCHAR(255)    NOT NULL,               -- e.g., 'TATA_IPL_2025_Auction.pdf'
    file_size_kb    DOUBLE          DEFAULT 0.0,
    upload_date     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    pdf_data        LONGBLOB        NOT NULL                -- Full PDF binary stored in database
);

-- 2. Table to store extracted 10-year player price records from those PDFs
CREATE TABLE IF NOT EXISTS auction_history (
    history_id      INT             AUTO_INCREMENT PRIMARY KEY,
    player_name     VARCHAR(100)    NOT NULL,
    auction_year    INT             NOT NULL,
    role            VARCHAR(50),                            -- Batter, Bowler, All-Rounder, Wicketkeeper
    country         VARCHAR(50)     DEFAULT 'India',
    base_price      DOUBLE          DEFAULT 0.0,            -- in Lakhs
    sold_price      DOUBLE          DEFAULT 0.0,            -- in Lakhs
    bought_by_team  VARCHAR(100)    DEFAULT 'Unsold',       -- Team name or 'Unsold'
    status          VARCHAR(20)     DEFAULT 'Sold'          -- 'Sold' or 'Unsold'
);

-- 3. Table for Live Auctions with dynamically calculated starting bids
CREATE TABLE IF NOT EXISTS live_auctions (
    auction_id       INT             AUTO_INCREMENT PRIMARY KEY,
    player_name      VARCHAR(100)    NOT NULL,
    role             VARCHAR(50),
    country          VARCHAR(50)     DEFAULT 'India',
    base_price       DOUBLE          NOT NULL,              -- Reserve price in Lakhs
    historical_avg   DOUBLE          DEFAULT 0.0,           -- Average sold price over previous years
    calculated_bid   DOUBLE          DEFAULT 0.0,           -- Dynamic starting bid based on popularity
    current_bid      DOUBLE          DEFAULT 0.0,           -- Highest live bid
    highest_bidder   VARCHAR(100)    DEFAULT 'None',        -- Team with highest bid
    status           VARCHAR(20)     DEFAULT 'Upcoming'     -- 'Upcoming', 'Live', 'Sold', 'Unsold'
);

-- Ensure teams table has purse tracking
ALTER TABLE teams ADD COLUMN IF NOT EXISTS purse_remaining DOUBLE DEFAULT 10000.0; -- 100 Crore in Lakhs
