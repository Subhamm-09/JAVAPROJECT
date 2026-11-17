# 🏏 IPL Cricket League Management System

## 📂 Project Overview
Complete Java Mini Project developed with **Core Java, Swing GUI, JDBC, and MySQL**.
Implements **MVC Architecture & DAO Pattern** with all viva syllabus concepts (OOP, Inheritance, Polymorphism, Abstraction, Multithreading, File Handling).

---

## 🚀 Quick Start (Running via Command Line)

### Step 1: Set Up MySQL Database
1. Open **MySQL Workbench** or command line client.
2. Open and run the SQL script:
   `sql/ipl_database.sql`
3. This creates:
   - Database: `ipl_management`
   - All 6 tables (`admin`, `teams`, `players`, `matches`, `scores`, `points_table`)
   - Default admin credentials:
     - **Username:** `admin`
     - **Password:** `admin123`
   - Sample teams & player records!

### Step 2: Configure Database Password (if needed)
Open [DBConnection.java](file:///c:/Users/bandi/Downloads/java%20mini/IPLManagementSystem/src/connection/DBConnection.java) and set your MySQL password:
```java
private static final String URL      = "jdbc:mysql://localhost:3306/ipl_management";
private static final String USER     = "root";
private static final String PASSWORD = "your_mysql_password"; // <--- change here
```

### Step 3: MySQL Connector/J Driver
Download `mysql-connector-j-8.x.x.jar` (or copy your existing one) into the `lib/` directory:
```
IPLManagementSystem/lib/mysql-connector-j-8.x.x.jar
```

### Step 4: Compile & Run
- Double-click **`build.bat`** to compile all Java source files.
- Double-click **`run.bat`** to start the application!

---

## 💻 Running in IntelliJ IDEA / NetBeans

### IntelliJ IDEA:
1. Open IntelliJ → `File` → `Open` → Select `c:\Users\bandi\Downloads\java mini\IPLManagementSystem`.
2. Mark `src` as **Sources Root** (Right click `src` → `Mark Directory as` → `Sources Root`).
3. Add MySQL Connector JAR:
   `File` → `Project Structure` → `Libraries` → `+` → Select `mysql-connector-j.jar`.
4. Right-click [Main.java](file:///c:/Users/bandi/Downloads/java%20mini/IPLManagementSystem/src/Main.java) → `Run 'Main.main()'`.

### NetBeans:
1. Create a `Java Application with Existing Sources`.
2. Point source folder to `src/`.
3. In Project Properties → `Libraries` → Add `mysql-connector-j.jar`.
4. Set Main Class to `Main`.
5. Click **Run**.
