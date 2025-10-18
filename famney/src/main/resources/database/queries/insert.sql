-- Insert default categories for family FAM001
INSERT INTO Categories (categoryId, familyId, categoryName, categoryType, isDefault, description, isActive)
VALUES
('CAT001', 'FAM001', 'Food & Dining', 'Expense', 1, 'Groceries, restaurants, takeaways', 1),
('CAT002', 'FAM001', 'Transportation', 'Expense', 1, 'Petrol, public transport, car maintenance', 1),
('CAT003', 'FAM001', 'Utilities', 'Expense', 1, 'Electricity, water, gas, internet', 1),
('CAT004', 'FAM001', 'Entertainment', 'Expense', 1, 'Movies, games, hobbies', 1),
('CAT005', 'FAM001', 'Healthcare', 'Expense', 1, 'Medical expenses, insurance', 1),
('CAT006', 'FAM001', 'Shopping', 'Expense', 1, 'Clothes, electronics, household items', 1),
('CAT007', 'FAM001', 'Salary', 'Income', 1, 'Monthly salary from employment', 1),
('CAT008', 'FAM001', 'Freelance', 'Income', 1, 'Freelance work and contracts', 1),
('CAT009', 'FAM001', 'Allowance', 'Income', 1, 'Pocket money and allowances', 1),
('CAT010', 'FAM001', 'Investment', 'Income', 1, 'Dividends, interest, capital gains', 1),
('CAT011', 'FAM001', 'Education', 'Expense', 1, 'School fees, books, courses', 1),
('CAT012', 'FAM001', 'Pet Care', 'Expense', 1, 'Pet food, vet bills, grooming', 1);
