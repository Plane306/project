package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

// Income class (Represents family income transactions)
// Core entity for F105 Income Management feature
public class Income implements Serializable {
    
    private String incomeId;
    private String familyId;
    private String userId; // Who recorded this income
    private String categoryId;
    private double amount;
    private String description;
    private Date incomeDate; // When the income was received
    private Date createdDate; // When the record was created
    private Date lastModifiedDate;
    private boolean isRecurring; // For salary, allowance, etc.
    private boolean isActive;
    private String source; // Company name, client name, etc.
    
    // Constructor for creating new income
    public Income(String familyId, String userId, String categoryId, double amount, String description, Date incomeDate) {
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isRecurring = false;
    }
    
    // Constructor with current date as income date
    public Income(String familyId, String userId, String categoryId, double amount, String description) {
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isRecurring = false;
    }
    
    // Constructor with recurring flag
    public Income(String familyId, String userId, String categoryId, double amount, String description, 
                  Date incomeDate, boolean isRecurring) {
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
        this.isRecurring = isRecurring;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
    }

    // Full constructor (for database retrieval)
    public Income(String incomeId, String familyId, String userId, String categoryId, double amount, 
                  String description, Date incomeDate, Date createdDate, Date lastModifiedDate, 
                  boolean isRecurring, boolean isActive, String source) {
        this.incomeId = incomeId;
        this.familyId = familyId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.incomeDate = incomeDate;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isRecurring = isRecurring;
        this.isActive = isActive;
        this.source = source;
    }
    
    // Default constructor
    public Income() {
        this.incomeDate = new Date();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isRecurring = false;
        this.amount = 0.0;
    }
    
    // Getters and Setters
    public String getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(String incomeId) {
        this.incomeId = incomeId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        this.lastModifiedDate = new Date();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.lastModifiedDate = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        this.lastModifiedDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModifiedDate = new Date();
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
        this.lastModifiedDate = new Date();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
        this.lastModifiedDate = new Date();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.lastModifiedDate = new Date();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        this.lastModifiedDate = new Date();
    }
    
    // Business logic methods
    
    // Check if income amount is valid
    public boolean hasValidAmount() {
        return amount > 0;
    }
    
    // Check if income has required fields
    public boolean hasRequiredFields() {
        return familyId != null && !familyId.trim().isEmpty() &&
               userId != null && !userId.trim().isEmpty() &&
               categoryId != null && !categoryId.trim().isEmpty() &&
               hasValidAmount() &&
               incomeDate != null;
    }
    
    // Get formatted amount as currency string
    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    
    // Get short description for display (max 30 chars)
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "No description";
        }
        String trimmed = description.trim();
        return trimmed.length() <= 30 ? trimmed : trimmed.substring(0, 27) + "...";
    }
    
    // Check if income is from this month
    public boolean isFromThisMonth() {
        if (incomeDate == null) return false;
        
        Calendar incomeCal = Calendar.getInstance();
        incomeCal.setTime(incomeDate);
        
        Calendar now = Calendar.getInstance();
        
        return incomeCal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            incomeCal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }

    // Check if income is from today
    public boolean isFromToday() {
        if (incomeDate == null) return false;
        
        Calendar incomeCal = Calendar.getInstance();
        incomeCal.setTime(incomeDate);
        
        Calendar now = Calendar.getInstance();
        
        return incomeCal.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH) &&
            incomeCal.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
            incomeCal.get(Calendar.YEAR) == now.get(Calendar.YEAR);
    }
    
    // Get income age in days
    public long getIncomeAgeInDays() {
        if (incomeDate == null) return 0;
        
        Date now = new Date();
        long diffInMillies = Math.abs(now.getTime() - incomeDate.getTime());
        return diffInMillies / (24 * 60 * 60 * 1000);
    }
    
    // Check if income is recent (within last 7 days)
    public boolean isRecent() {
        return getIncomeAgeInDays() <= 7;
    }
    
    // Get transaction type for unified transaction history
    public String getTransactionType() {
        return "Income";
    }
    
    // Get display title combining amount and description
    public String getDisplayTitle() {
        String desc = getShortDescription();
        String recurringIndicator = isRecurring ? " (Recurring)" : "";
        return getFormattedAmount() + " - " + desc + recurringIndicator;
    }
    
    // Get recurring status display
    public String getRecurringStatusDisplay() {
        return isRecurring ? "Recurring Income" : "One-time Income";
    }
    
    // Get source display (fallback to description if no source)
    public String getSourceDisplay() {
        if (source != null && !source.trim().isEmpty()) {
            return source;
        }
        return getShortDescription();
    }
    
    // Calculate monthly income estimate (for recurring income)
    public double getMonthlyIncomeEstimate() {
        return isRecurring ? amount : 0.0;
    }
    
    // Validate income data
    public boolean isValid() {
        return hasRequiredFields() && 
               (description == null || description.length() <= 255) &&
               (source == null || source.length() <= 100);
    }
    
    // Get income type icon
    public String getIncomeTypeIcon() {
        if (isRecurring) {
            return "🔄"; // Recurring income
        } else {
            return "💰"; // One-time income
        }
    }
    
    @Override
    public String toString() {
        return "Income{" +
                "incomeId='" + incomeId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", userId='" + userId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", incomeDate=" + incomeDate +
                ", isRecurring=" + isRecurring +
                ", isActive=" + isActive +
                '}';
    }
}