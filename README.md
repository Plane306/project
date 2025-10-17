# Famney - Family Financial Management System

## R1 Release - Backend Integration & Database

This is Release 1 (R1) of the Famney project which adds database integration and backend functionality to the existing front-end prototype from R0.

---

## What's New in R1

R1 adds the following features to the R0 prototype:
- SQLite database integration
- DAO (Data Access Object) pattern implementation
- Backend servlet controllers for business logic
- Password hashing with SHA-256
- JUnit testing for DAO classes
- Server-side validation
- Full CRUD operations for all features (F101-F108)

---

## Required Extensions

Make sure you've got these VS Code extensions installed:

- Extension Pack for Java - for Java development support
- JSP Language Support - for JSP syntax highlighting
- Maven for Java - for Maven project management
- Community Server Connector - for Tomcat server integration
- SQLite - for viewing and managing the database

---

## Environment Setup

### 1. Java JDK Setup
- Install Java JDK 11 or higher
- Set up JAVA_HOME environment variable pointing to your JDK installation
- Add Java bin folder to your system PATH

### 2. Maven Setup
- Download Apache Maven
- Set up MAVEN_HOME environment variable
- Add Maven bin folder to your system PATH
- Verify installation by running `mvn --version` in terminal

Reference: Check out this guide for detailed setup instructions:
https://www.qamadness.com/knowledge-base/how-to-install-maven-and-configure-environment-variables/

### 3. Apache Tomcat Setup
- Download Apache Tomcat 11.0.0-M6
- Extract it to a location on your computer
- Remember the path as you'll need it when creating the server

---

## Project Structure

```
famney/
├── database/
│   ├── famney.db              # SQLite database file
│   └── queries/
│       ├── create_tables.sql  # Database schema
│       └── table_data.sql     # Sample data (optional)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/    # Servlets and utilities
│   │   │   └── model/         # Entity classes and DAO
│   │   └── webapp/
│   │       ├── WEB-INF/       # web.xml configuration
│   │       └── *.jsp          # All JSP view files
│   └── test/
│       └── java/              # JUnit test classes
├── pom.xml                     # Maven dependencies
└── README.md
```

---

## Database Setup

### Initial Database Creation

The database file `famney.db` should already exist in the `famney/database/` folder. If not, you'll need to create it:

1. Open VS Code and make sure the SQLite extension is installed
2. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac) and type "SQLite: Open Database"
3. Navigate to `famney/database/` and create a new database called `famney.db`
4. Open `database/queries/create_tables.sql` in VS Code
5. Right-click the SQL file and select "Run Query" to create all tables

### Updating Database Path

Important: You need to update the database path in `DB.java` to match your local setup.

1. Open `src/main/java/model/dao/DB.java`
2. Find the line with the database URL
3. Change it to your actual project location

Example:
```java
protected String URL = "jdbc:sqlite:C:/Users/YourName/Documents/UTS/Famney/famney/database/famney.db";
```

### Viewing the Database

To check your database contents:
1. Right-click on `famney.db` in VS Code
2. Select "Open Database"
3. The SQLite Explorer will appear in the sidebar
4. You can now browse tables and run SQL queries

---

## Maven Dependencies

The project uses these main dependencies (defined in pom.xml):

- SQLite JDBC (3.49.1.0) - for database connectivity
- Jakarta Servlet API (6.1.0) - for servlet development
- JUnit Jupiter (5.11.3) - for unit testing
- Commons Codec (1.15) - for password hashing

All dependencies are automatically downloaded when you run Maven commands.

---

## How to Deploy

### First-Time Setup

1. Create Tomcat Server
   - In VS Code, look for the "SERVERS" panel (usually bottom-left)
   - Right-click on "Community Server Connector"
   - Select "Create New Server"
   - Click "Yes" at the top prompt
   - Choose "apache-tomcat-11.0.0-M6" from the list
   - Browse to your Tomcat installation folder

2. Update Database Path
   - Before building, make sure you've updated the database path in `DB.java` (see Database Setup section above)

3. Build the Project
   - Open terminal in VS Code
   - Navigate to the famney directory: `cd famney`
   - Run the Maven build command:
     ```bash
     mvn clean compile package
     ```
   - This creates a `target` folder with a `famney` folder inside containing the compiled application

4. Deploy to Tomcat
   - In the Explorer panel, navigate to `target/famney` folder
   - Right-click on the `famney` folder
   - Select "Run on Server"
   - Choose the Apache Tomcat server you created

5. Access the Application
   - Open your browser and go to: http://localhost:8080/famney/index.jsp
   - You should see the Famney homepage

### Sample Login Credentials

After initialising the database with sample data, you can use:
- Email: john.smith@email.com
- Password: password123

(These are set up in the sample data - check `database/queries/table_data.sql`)

---

## After Making Changes

Whenever you edit or add new files (Java classes, JSP files, etc.), you need to rebuild and restart:

1. Rebuild the Project
   ```bash
   cd famney
   mvn clean compile package
   ```

2. Restart Tomcat Server
   - In the SERVERS panel, right-click your Tomcat server
   - Select "Restart Server"
   - Wait for it to finish restarting
   - Refresh your browser to see the changes

Tip: For JSP-only changes, sometimes just a browser refresh works, but rebuilding is safer to ensure everything updates properly.

---

## Running Tests

The project includes JUnit tests for DAO classes. To run tests:

```bash
cd famney
mvn test
```

This will run all test classes in the `src/test/java` folder and show you the results in the terminal.

Example test classes:
- UserManagerTest.java - tests User DAO operations
- CategoryManagerTest.java - tests Category DAO operations
- FamilyManagerTest.java - tests Family DAO operations

---

## Key Features Implemented

### F101: User Authentication & Family Management
Core feature that provides the foundation for the entire system. Handles family account creation with unique family code generation, member registration using family codes, role-based authentication (Family Head, Adult, Teen, Kid), and user profile management.

### F102: Category Management
Classification foundation for all financial transactions. Allows families to create custom expense and income categories, edit and delete categories, with default categories auto-initialised. Category names must be unique within each family.

### F103: Budget Management
Planning layer for financial control. Enables creation and management of monthly or weekly family budgets, setting budget amounts per category, and tracking budget allocations. Each budget period is unique per family.

### F104: Expense Tracking
Daily spending recording and monitoring. Records family expenses with category assignment, attaches expenses to specific family members, and links expenses to budget periods for comparison and analysis.

### F105: Income Management
Tracks family earnings from various sources. Records income with categories, supports recurring income entries, and assigns income to specific family members. Feeds data to dashboard and transaction history.

### F106: Financial Dashboard
Comprehensive financial overview and analytics. Displays overall financial health, compares budget vs actual spending, visualises income vs expense trends, and shows monthly summaries with category-wise breakdowns.

### F107: Savings Goals
Tracks progress towards financial targets. Create savings goals with target amounts, track current progress, set target dates, and mark goals as completed when reached. Independent feature that integrates with dashboard display.

### F108: Transaction History
Unified view of all financial activities. Provides combined chronological view of income and expenses, with filtering by date range, category, and transaction type. Shows both income and expenses in one unified timeline.

---

## Common Issues & Solutions

### Issue: Database connection error
Solution: Check that your database path in `DB.java` is correct and points to an existing `famney.db` file.

### Issue: Server won't start
Solution: Make sure port 8080 isn't being used by another application. You can check this and kill the process if needed, or configure Tomcat to use a different port.

### Issue: Changes not showing up
Solution: Make sure you've run `mvn clean compile package` and restarted the Tomcat server. Sometimes you might need to clear your browser cache too.

### Issue: Maven build fails
Solution: Check that your `JAVA_HOME` and `MAVEN_HOME` environment variables are set correctly. Run `mvn --version` to verify.

### Issue: SQLite extension can't find database
Solution: The database file must exist before you can open it. If it doesn't exist, create it using the SQLite extension, then run `create_tables.sql` from the `database/queries/` folder to set up the schema.

---

## Development Notes

### DAO Pattern
The project uses the DAO (Data Access Object) pattern to separate database operations from business logic. Each entity has its own Manager class (e.g., UserManager, CategoryManager) that handles database operations.

### Servlet Controllers
Servlets handle HTTP requests and coordinate between the JSP views and DAO classes. The main servlet is `ConnServlet` which initialises all DAO managers and makes them available to JSP pages.

### Security
- Passwords are hashed using SHA-256 before storing in the database
- Session management handles user authentication
- Input validation prevents SQL injection
- All database queries use prepared statements

---

## Team Members & Features

- F101 (User Authentication & Family Management): Muhammad Naufal Farhan Mudofi
- F102 (Category Management): Muhammad Naufal Farhan Mudofi
- F103 (Budget Management): Sachin Bhat
- F104 (Expense Tracking): Sachin Bhat
- F105 (Income Management): Jason Dang
- F106 (Financial Dashboard): Jason Dang
- F107 (Savings Goals): Haoxuan Huang
- F108 (Transaction History): Haoxuan Huang

---

## Additional Resources

- Maven Documentation: https://maven.apache.org/guides/
- Jakarta Servlet Spec: https://jakarta.ee/specifications/servlet/
- SQLite Documentation: https://www.sqlite.org/docs.html
- JUnit 5 User Guide: https://junit.org/junit5/docs/current/user-guide/

---

## Project Version

- Version: 1.4
- Release: R1 (Backend Integration)
- Last Updated: September 2025

---