-- Famney Sample Data for Testing (R1)
-- The hash below is SHA-256("password123" + "famney_salt")

-- Clear existing data if any
DELETE FROM Categories;
DELETE FROM Users;
DELETE FROM Families;
DELETE FROM BudgetCategories;
DELETE FROM Budgets;

-- F101: Sample Family Data
-- The Smith Family with family code FAMNEY-A1B2
INSERT INTO Families (familyId, familyCode, familyName, familyHead, memberCount, createdDate, lastModifiedDate, isActive) 
VALUES (
    'F0001',
    'FAMNEY-A1B2',
    'The Smith Family',
    'U0001',
    4,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- F101: Sample Users (4 family members with different roles)
-- Password for all users: "password123"
-- Hash generated using: PasswordUtil.hashPassword("password123")
-- Run PasswordUtil.main() to verify the hash value

-- User 1: John Smith (Family Head)
INSERT INTO Users (userId, email, password, fullName, role, familyId, joinDate, createdDate, lastModifiedDate, isActive)
VALUES (
    'U0001',
    'john@smith.com',
    '7fb287e06294e9f3ab31527c53b804e50409a7a6ac13f51d58b927eb69a5e053',
    'John Smith',
    'Family Head',
    'F0001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- User 2: Jane Smith (Adult)
INSERT INTO Users (userId, email, password, fullName, role, familyId, joinDate, createdDate, lastModifiedDate, isActive)
VALUES (
    'U0002',
    'jane@smith.com',
    '7fb287e06294e9f3ab31527c53b804e50409a7a6ac13f51d58b927eb69a5e053',
    'Jane Smith',
    'Adult',
    'F0001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- User 3: Mike Smith (Teen)
INSERT INTO Users (userId, email, password, fullName, role, familyId, joinDate, createdDate, lastModifiedDate, isActive)
VALUES (
    'U0003',
    'mike@smith.com',
    '7fb287e06294e9f3ab31527c53b804e50409a7a6ac13f51d58b927eb69a5e053',
    'Mike Smith',
    'Teen',
    'F0001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- User 4: Lucy Smith (Kid)
INSERT INTO Users (userId, email, password, fullName, role, familyId, joinDate, createdDate, lastModifiedDate, isActive)
VALUES (
    'U0004',
    'lucy@smith.com',
    '7fb287e06294e9f3ab31527c53b804e50409a7a6ac13f51d58b927eb69a5e053',
    'Lucy Smith',
    'Kid',
    'F0001',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1
);

-- F102: Default Expense Categories (6 categories)
INSERT INTO Categories (categoryId, familyId, categoryName, categoryType, isDefault, description, createdDate, lastModifiedDate, isActive)
VALUES
    ('C0001', 'F0001', 'Food & Dining', 'Expense', 1, 'Groceries, restaurants, takeaways', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0002', 'F0001', 'Transportation', 'Expense', 1, 'Petrol, public transport, car maintenance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0003', 'F0001', 'Utilities', 'Expense', 1, 'Electricity, water, gas, internet', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0004', 'F0001', 'Entertainment', 'Expense', 1, 'Movies, games, hobbies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0005', 'F0001', 'Healthcare', 'Expense', 1, 'Medical expenses, insurance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0006', 'F0001', 'Shopping', 'Expense', 1, 'Clothes, electronics, household items', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- F102: Default Income Categories (4 categories)
INSERT INTO Categories (categoryId, familyId, categoryName, categoryType, isDefault, description, createdDate, lastModifiedDate, isActive)
VALUES
    ('C0007', 'F0001', 'Salary', 'Income', 1, 'Monthly salary from employment', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0008', 'F0001', 'Freelance', 'Income', 1, 'Freelance work and contracts', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0009', 'F0001', 'Allowance', 'Income', 1, 'Pocket money and allowances', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
    ('C0010', 'F0001', 'Investment', 'Income', 1, 'Dividends, interest, capital gains', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);

-- F107: Default Saving Goals
INSERT INTO SavingsGoals (
        goalId,
        familyId,
        goalName,
        description,
        targetAmount,
        currentAmount,
        targetDate,
        createdDate,
        lastModifiedDate,
        isActive,
        isCompleted,
        createdBy
    )
VALUES (
        'G1',
        'F1',
        'Vacation Fund',
        'Save for family trip',
        5000,
        1000,
        '2025-12-31',
        DATE('now'),
        DATE('now'),
        1,
        0,
        'admin'
    ),
    (
        'G2',
        'F1',
        'Emergency Fund',
        'Backup savings',
        10000,
        2500,
        '2026-06-30',
        DATE('now'),
        DATE('now'),
        1,
        0,
        'admin'
    );