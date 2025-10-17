package controller;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.sql.Timestamp;

// Utility class for date formatting in Famney system
// Handles conversion between Java Date objects and SQLite-compatible date strings
public class DateUtil {
    
    // Standard date-time format for database storage: YYYY-MM-DD HH:MM:SS
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // Date only format for display: YYYY-MM-DD
    private static final SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Get current date-time as formatted string for database insertion
    // Returns format: "2025-10-01 16:38:54"
    public static String getCurrentDateTime() {
        return dateTimeFormat.format(new Date());
    }
    
    // Get current date only as formatted string
    // Returns format: "2025-10-01"
    public static String getCurrentDate() {
        return dateOnlyFormat.format(new Date());
    }
    
    // Format a Date object to database-compatible string
    // Used when updating lastModifiedDate fields
    public static String formatDateTime(Date date) {
        if (date == null) {
            return getCurrentDateTime();
        }
        return dateTimeFormat.format(date);
    }
    
    // Format a Timestamp object to database-compatible string
    // Used for compatibility with existing Timestamp fields
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) {
            return getCurrentDateTime();
        }
        return dateTimeFormat.format(timestamp);
    }
    
    // Parse database date string back to Date object
    // Used when reading dates from database for display
    public static Date parseDateTime(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return dateTimeFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Parse database date string back to Timestamp object
    // Used for setting Date fields in model objects
    public static Timestamp parseToTimestamp(String dateString) {
        Date date = parseDateTime(dateString);
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }
}