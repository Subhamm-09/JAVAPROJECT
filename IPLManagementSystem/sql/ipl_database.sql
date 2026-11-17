-- ============================================================
-- IPL Cricket League Management System — Database Schema
-- ============================================================
-- SQL Concepts Used:
--   CREATE DATABASE, CREATE TABLE, PRIMARY KEY, FOREIGN KEY,
--   NOT NULL, AUTO_INCREMENT, INSERT INTO, SELECT
-- ============================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS ipl_management;

-- Step 2: Select (use) the database
USE ipl_management;

-- ============================================================
-- Table 1: admin
-- Purpose: Stores admin login credentials for authentication
-- ============================================================
CREATE TABLE IF NOT EXISTS admin (
    admin_id    INT             AUTO_INCREMENT PRIMARY KEY,  -- AUTO_INCREMENT: MySQL auto-generates IDs
    username    VARCHAR(50)     NOT NULL UNIQUE,              -- NOT NULL: This field cannot be empty
    password    VARCHAR(100)    NOT NULL,                     -- Stores the admin's password
    full_name   VARCHAR(100)    NOT NULL,                     -- Admin's full name
    email       VARCHAR(100)                                  -- Optional email field
);

-- ============================================================
-- Table 2: teams
-- Purpose: Stores IPL team information
-- ============================================================
CREATE TABLE IF NOT EXISTS teams (
    team_id       INT           AUTO_INCREMENT PRIMARY KEY,
    team_name     VARCHAR(100)  NOT NULL UNIQUE,             -- Each team name must be unique
    city          VARCHAR(50)   NOT NULL,                     -- Home city of the team
    coach         VARCHAR(100),                               -- Coach name (optional)
    captain       VARCHAR(100),                               -- Captain name (optional)
    founded_year  INT                                         -- Year the franchise was founded
);

-- ============================================================
-- Table 3: players
-- Purpose: Stores player details linked to teams
-- FOREIGN KEY: player.team_id references teams.team_id
--   This is REFERENTIAL INTEGRITY — you cannot add a player
--   for a team_id that doesn't exist in the teams table.
-- ============================================================
CREATE TABLE IF NOT EXISTS players (
    player_id       INT             AUTO_INCREMENT PRIMARY KEY,
    player_name     VARCHAR(100)    NOT NULL,
    team_id         INT,                                      -- Which team this player belongs to
    role            VARCHAR(50),                               -- Batsman, Bowler, All-rounder, Wicketkeeper
    age             INT,
    matches_played  INT             DEFAULT 0,                -- DEFAULT: starts at 0 if not specified
    runs_scored     INT             DEFAULT 0,
    wickets_taken   INT             DEFAULT 0,
    batting_avg     DOUBLE          DEFAULT 0.0,
    bowling_avg     DOUBLE          DEFAULT 0.0,
    FOREIGN KEY (team_id) REFERENCES teams(team_id)           -- FOREIGN KEY constraint
        ON DELETE SET NULL                                     -- If a team is deleted, set player's team_id to NULL
);

-- ============================================================
-- Table 4: matches
-- Purpose: Stores match schedule and results
-- Has TWO foreign keys to teams (team1 and team2)
-- ============================================================
CREATE TABLE IF NOT EXISTS matches (
    match_id        INT             AUTO_INCREMENT PRIMARY KEY,
    team1_id        INT             NOT NULL,
    team2_id        INT             NOT NULL,
    match_date      DATE            NOT NULL,                 -- DATE type stores YYYY-MM-DD
    venue           VARCHAR(150)    NOT NULL,
    status          VARCHAR(30)     DEFAULT 'Scheduled',      -- Scheduled, Live, Completed
    winner_team_id  INT,                                      -- NULL until match is completed
    FOREIGN KEY (team1_id) REFERENCES teams(team_id),
    FOREIGN KEY (team2_id) REFERENCES teams(team_id),
    FOREIGN KEY (winner_team_id) REFERENCES teams(team_id)
);

-- ============================================================
-- Table 5: scores
-- Purpose: Stores scorecard for each team in each match
-- ============================================================
CREATE TABLE IF NOT EXISTS scores (
    score_id    INT         AUTO_INCREMENT PRIMARY KEY,
    match_id    INT         NOT NULL,
    team_id     INT         NOT NULL,
    runs        INT         DEFAULT 0,
    wickets     INT         DEFAULT 0,
    overs       DOUBLE      DEFAULT 0.0,                      -- e.g., 19.4 overs
    extras      INT         DEFAULT 0,
    FOREIGN KEY (match_id) REFERENCES matches(match_id)
        ON DELETE CASCADE,                                    -- If match deleted, scores deleted too
    FOREIGN KEY (team_id) REFERENCES teams(team_id)
);

-- ============================================================
-- Table 6: points_table
-- Purpose: Stores league standings for each team
-- ============================================================
CREATE TABLE IF NOT EXISTS points_table (
    points_id       INT     AUTO_INCREMENT PRIMARY KEY,
    team_id         INT     NOT NULL UNIQUE,                  -- One entry per team
    matches_played  INT     DEFAULT 0,
    wins            INT     DEFAULT 0,
    losses          INT     DEFAULT 0,
    draws           INT     DEFAULT 0,
    points          INT     DEFAULT 0,                        -- Typically: Win=2, Draw=1, Loss=0
    net_run_rate    DOUBLE  DEFAULT 0.00,
    FOREIGN KEY (team_id) REFERENCES teams(team_id)
        ON DELETE CASCADE
);


-- ============================================================
-- Insert default admin account
-- Username: admin  |  Password: admin123
-- ============================================================
INSERT INTO admin (username, password, full_name, email)
VALUES ('admin', 'admin123', 'IPL Administrator', 'admin@ipl.com');

-- ============================================================
-- Insert sample IPL teams
-- ============================================================
INSERT INTO teams (team_name, city, coach, captain, founded_year) VALUES
('Mumbai Indians',            'Mumbai',     'Mark Boucher',    'Hardik Pandya',    2008),
('Chennai Super Kings',       'Chennai',    'Stephen Fleming', 'MS Dhoni',         2008),
('Royal Challengers Bangalore','Bangalore', 'Andy Flower',    'Faf du Plessis',   2008),
('Kolkata Knight Riders',     'Kolkata',    'Chandrakant Pandit','Shreyas Iyer',   2008),
('Delhi Capitals',            'Delhi',      'Ricky Ponting',   'David Warner',     2008),
('Rajasthan Royals',          'Jaipur',     'Kumar Sangakkara','Sanju Samson',     2008),
('Sunrisers Hyderabad',       'Hyderabad',  'Brian Lara',      'Aiden Markram',    2013),
('Punjab Kings',              'Mohali',     'Trevor Bayliss',  'Shikhar Dhawan',   2008),
('Gujarat Titans',            'Ahmedabad',  'Ashish Nehra',    'Shubman Gill',     2022),
('Lucknow Super Giants',      'Lucknow',    'Justin Langer',   'KL Rahul',         2022);

-- ============================================================
-- Insert sample players
-- ============================================================
INSERT INTO players (player_name, team_id, role, age, matches_played, runs_scored, wickets_taken, batting_avg, bowling_avg) VALUES
('Rohit Sharma',    1, 'Batsman',      37, 250, 6500, 15, 30.50, 45.00),
('Jasprit Bumrah',  1, 'Bowler',       30, 130, 100,  160, 5.50, 24.30),
('Suryakumar Yadav',1, 'Batsman',      33, 120, 3200, 0,  35.00, 0.00),
('MS Dhoni',        2, 'Wicketkeeper', 42, 260, 5100, 0,  38.00, 0.00),
('Ravindra Jadeja', 2, 'All-rounder',  35, 220, 2800, 150, 25.00, 30.50),
('Virat Kohli',     3, 'Batsman',      35, 240, 7200, 4,  37.50, 95.00),
('Glenn Maxwell',   3, 'All-rounder',  35, 110, 2200, 30, 22.00, 35.00),
('Andre Russell',   4, 'All-rounder',  36, 100, 2100, 80, 28.00, 25.00),
('Sunil Narine',    4, 'All-rounder',  35, 170, 1200, 180, 10.00, 22.50),
('David Warner',    5, 'Batsman',      37, 180, 6200, 0,  42.00, 0.00);

-- ============================================================
-- Insert points_table entries for all teams (starting values)
-- ============================================================
INSERT INTO points_table (team_id, matches_played, wins, losses, draws, points, net_run_rate) VALUES
(1, 0, 0, 0, 0, 0, 0.00),
(2, 0, 0, 0, 0, 0, 0.00),
(3, 0, 0, 0, 0, 0, 0.00),
(4, 0, 0, 0, 0, 0, 0.00),
(5, 0, 0, 0, 0, 0, 0.00),
(6, 0, 0, 0, 0, 0, 0.00),
(7, 0, 0, 0, 0, 0, 0.00),
(8, 0, 0, 0, 0, 0, 0.00),
(9, 0, 0, 0, 0, 0, 0.00),
(10, 0, 0, 0, 0, 0, 0.00);

-- ============================================================
-- Useful queries you can test in MySQL Workbench:
-- ============================================================
-- SELECT * FROM admin;
-- SELECT * FROM teams;
-- SELECT * FROM players WHERE team_id = 1;
-- SELECT t.team_name, COUNT(p.player_id) AS player_count
--   FROM teams t LEFT JOIN players p ON t.team_id = p.team_id
--   GROUP BY t.team_name;
-- SELECT * FROM points_table ORDER BY points DESC, net_run_rate DESC;
