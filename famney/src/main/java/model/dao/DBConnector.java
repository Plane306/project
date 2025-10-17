package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Manages database connections for the application
// Opens and closes SQLite database connections
public class DBConnector extends DB {
    
    // Constructor loads SQLite driver and establishes connection
    // Throws exceptions if driver not found or connection fails
    public DBConnector() throws ClassNotFoundException, SQLException {
        // Load SQLite JDBC driver
        Class.forName(driver);
        
        // Establish connection to database
        conn = DriverManager.getConnection(URL);
    }
    
    // Returns the active database connection
    // Used by DAO managers to execute queries
    public Connection openConnection() {
        return this.conn;
    }
    
    // Closes the database connection
    // Should be called when application shuts down
    public void closeConnection() throws SQLException {
        this.conn.close();
    }
}