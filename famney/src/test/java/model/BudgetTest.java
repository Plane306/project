package model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BudgetTest {

    @Test
    public void testBudgetCreation() {
        Budget budget = new Budget("fam1", "Monthly Budget", 9, 2025, 1000.0, "user1");
        assertEquals("fam1", budget.getFamilyId());
        assertEquals("Monthly Budget", budget.getBudgetName());
        assertEquals(9, budget.getMonth());
        assertEquals(2025, budget.getYear());
        assertEquals(1000.0, budget.getTotalAmount(), 0.001);
        assertEquals("user1", budget.getCreatedBy());
        assertTrue(budget.isActive());
        assertNotNull(budget.getCreatedDate());
    }

    @Test
    public void testSettersAndGetters() {
        Budget budget = new Budget();
        budget.setFamilyId("fam2");
        budget.setBudgetName("Yearly Budget");
        budget.setMonth(10);
        budget.setYear(2026);
        budget.setTotalAmount(2000.0);
        budget.setCreatedBy("user2");
        budget.setDescription("Test Desc");
        assertEquals("fam2", budget.getFamilyId());
        assertEquals("Yearly Budget", budget.getBudgetName());
        assertEquals(10, budget.getMonth());
        assertEquals(2026, budget.getYear());
        assertEquals(2000.0, budget.getTotalAmount(), 0.001);
        assertEquals("user2", budget.getCreatedBy());
        assertEquals("Test Desc", budget.getDescription());
    }

    @Test
    public void testIsValid() {
        Budget budget = new Budget("fam3", "Valid Budget", 9, 2025, 500.0, "user3");
        assertTrue(budget.isValid());
        budget.setTotalAmount(0.0);
        assertFalse(budget.isValid());
    }
}
