package com.delivery.dao;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:delivery.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            throw new RuntimeException("DB connection failed", e);
        }
    }

    public static void init() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    address TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS drivers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    phone TEXT NOT NULL,
                    license_plate TEXT NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS packages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    description TEXT NOT NULL,
                    weight REAL NOT NULL,
                    address TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    driver_id INTEGER,
                    customer_id INTEGER,
                    created_at TEXT NOT NULL DEFAULT (datetime('now')),
                    delivered_at TEXT,
                    FOREIGN KEY (driver_id) REFERENCES drivers(id),
                    FOREIGN KEY (customer_id) REFERENCES customers(id)
                )
            """);
        } catch (Exception e) {
            throw new RuntimeException("DB init failed", e);
        }
    }
}
