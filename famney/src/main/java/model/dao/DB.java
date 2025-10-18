package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URL;
import java.nio.file.Paths;

public abstract class DB {

    protected String URL;
    protected String driver = "org.sqlite.JDBC";
    protected Connection conn;

    public DB() {
        try {
            // Load the SQLite JDBC driver
            Class.forName(driver);

            // Step 3: get the database path from resources
            // Assumes the database is at src/main/resources/database/famney.db
            URL dbResource = getClass().getClassLoader().getResource("database/famney.db");
            if (dbResource == null) {
                throw new RuntimeException("Database file not found in resources!");
            }

            // Convert URL to absolute file path
            URL = "jdbc:sqlite:" + Paths.get(dbResource.toURI()).toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to open connection
    protected void connect() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
        }
    }

    // Method to close connection
    protected void disconnect() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
