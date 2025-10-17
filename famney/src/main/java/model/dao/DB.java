package model.dao;

import java.sql.Connection;

// Base class for database configuration
// Stores connection info for SQLite database
public abstract class DB {
    
    // Database file path - update this to match your project location
    protected String URL = "jdbc:sqlite:C:/Users/flyin/Famney/famney/database/famney.db";
    
    // SQLite JDBC driver
    protected String driver = "org.sqlite.JDBC";
    
    // Database connection object
    protected Connection conn;
}