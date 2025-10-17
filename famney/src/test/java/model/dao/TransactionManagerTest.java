package model.dao;

import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionManagerTest {

    private Connection connection;
    private TransactionManager transactionManager;
    private String testFamilyId = "FAM12345";
    private String testUserId = "U001";
    private String testCategoryId = "C001";

    @BeforeAll
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        transactionManager = new TransactionManager(connection);
        createTestSchema();
        insertTestData();
    }

    @AfterEach
    void cleanUpTransactions() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM Income");
        stmt.execute("DELETE FROM Expenses");
        stmt.close();
    }

    @AfterAll
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTestSchema() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("CREATE TABLE Families (" +
                "familyId VARCHAR(8) PRIMARY KEY, " +
                "familyCode VARCHAR(15), familyName VARCHAR(100), " +
                "familyHead VARCHAR(8), memberCount INT DEFAULT 1, " +
                "createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN DEFAULT TRUE)");

        stmt.execute("CREATE TABLE Users (" +
                "userId VARCHAR(8) PRIMARY KEY, email VARCHAR(100), " +
                "password VARCHAR(255), fullName VARCHAR(100), " +
                "role VARCHAR(20), familyId VARCHAR(8), " +
                "joinDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN DEFAULT TRUE)");

        stmt.execute("CREATE TABLE Categories (" +
                "categoryId VARCHAR(8) PRIMARY KEY, familyId VARCHAR(8), " +
                "categoryName VARCHAR(50), categoryType VARCHAR(10), " +
                "isDefault BOOLEAN DEFAULT FALSE, description VARCHAR(200), " +
                "createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN DEFAULT TRUE)");

        stmt.execute("CREATE TABLE Income (" +
                "incomeId VARCHAR(8) PRIMARY KEY, familyId VARCHAR(8), " +
                "userId VARCHAR(8), categoryId VARCHAR(8), " +
                "amount DECIMAL(10,2), description VARCHAR(200), " +
                "incomeDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isRecurring BOOLEAN DEFAULT FALSE, " +
                "createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN DEFAULT TRUE)");

        stmt.execute("CREATE TABLE Expenses (" +
                "expenseId VARCHAR(8) PRIMARY KEY, familyId VARCHAR(8), " +
                "userId VARCHAR(8), categoryId VARCHAR(8), " +
                "amount DECIMAL(10,2), description VARCHAR(200), " +
                "expenseDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "lastModifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "isActive BOOLEAN DEFAULT TRUE)");

        stmt.close();
    }

    private void insertTestData() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("INSERT INTO Families VALUES ('" + testFamilyId + "', 'FAM-TEST', 'Test Family', '" + testUserId
                + "', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Users VALUES ('" + testUserId + "', 'test@test.com', 'hash', 'Test User', 'Family Head', '"
                        + testFamilyId + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute("INSERT INTO Categories VALUES ('" + testCategoryId + "', '" + testFamilyId
                + "', 'Test Category', 'Expense', 0, 'Test', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");

        stmt.close();
    }

    @Test
    void testGetAllTransactions() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
                "INSERT INTO Income VALUES ('I001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 1000.00, 'Salary', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Expenses VALUES ('E001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 500.00, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.close();

        List<Map<String, Object>> transactions = transactionManager.getFilteredTransactions(
                testFamilyId, null, null, null, null, null, null, 1, 10);

        assertEquals(2, transactions.size());
    }

    @Test
    void testFilterByType_IncomeOnly() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
                "INSERT INTO Income VALUES ('I001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 1000.00, 'Salary', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Expenses VALUES ('E001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 500.00, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.close();

        List<Map<String, Object>> transactions = transactionManager.getFilteredTransactions(
                testFamilyId, null, "Income", null, null, null, null, 1, 10);

        assertEquals(1, transactions.size());
        assertEquals("Income", transactions.get(0).get("type"));
    }

    @Test
    void testFilterByType_ExpenseOnly() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
                "INSERT INTO Income VALUES ('I001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 1000.00, 'Salary', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Expenses VALUES ('E001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 500.00, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.close();

        List<Map<String, Object>> transactions = transactionManager.getFilteredTransactions(
                testFamilyId, null, "Expense", null, null, null, null, 1, 10);

        assertEquals(1, transactions.size());
        assertEquals("Expense", transactions.get(0).get("type"));
    }

    @Test
    void testSearchDescription() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("INSERT INTO Income VALUES ('I001', '" + testFamilyId + "', '" + testUserId + "', '"
                + testCategoryId
                + "', 1000.00, 'Monthly Salary', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Expenses VALUES ('E001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 500.00, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.close();

        List<Map<String, Object>> transactions = transactionManager.getFilteredTransactions(
                testFamilyId, null, null, null, null, null, "Salary", 1, 10);

        assertEquals(1, transactions.size());
        assertTrue(((String) transactions.get(0).get("description")).contains("Salary"));
    }

    @Test
    void testPagination() throws SQLException {
        Statement stmt = connection.createStatement();
        for (int i = 1; i <= 25; i++) {
            stmt.execute("INSERT INTO Expenses VALUES ('E" + String.format("%03d", i) + "', '" + testFamilyId + "', '"
                    + testUserId + "', '" + testCategoryId + "', 100.00, 'Expense " + i
                    + "', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        }
        stmt.close();

        List<Map<String, Object>> page1 = transactionManager.getFilteredTransactions(
                testFamilyId, null, null, null, null, null, null, 1, 10);
        List<Map<String, Object>> page2 = transactionManager.getFilteredTransactions(
                testFamilyId, null, null, null, null, null, null, 2, 10);

        assertEquals(10, page1.size());
        assertEquals(10, page2.size());
    }

    @Test
    void testGetTotalCount() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute(
                "INSERT INTO Income VALUES ('I001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 1000.00, 'Salary', CURRENT_TIMESTAMP, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.execute(
                "INSERT INTO Expenses VALUES ('E001', '" + testFamilyId + "', '" + testUserId + "', '" + testCategoryId
                        + "', 500.00, 'Groceries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1)");
        stmt.close();

        int count = transactionManager.getTotalTransactionCount(testFamilyId);

        assertEquals(2, count);
    }
}