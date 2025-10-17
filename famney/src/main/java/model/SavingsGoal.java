package model;

import java.io.Serializable;
import java.util.Date;

// SavingsGoal class (Represents family savings objectives and progress tracking)
// Core entity for F107 Savings Goals feature
public class SavingsGoal implements Serializable {

    private String goalId;
    private String familyId;
    private String goalName;
    private String description;
    private double targetAmount;
    private double currentAmount;
    private Date targetDate;
    private Date createdDate;
    private Date lastModifiedDate;
    private boolean isActive;
    private boolean isCompleted;
    private String createdBy; // userId who created this goal

    // Constructor for creating new savings goal
    public SavingsGoal(String familyId, String goalName, double targetAmount, Date targetDate, String createdBy) {
        this.familyId = familyId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.createdBy = createdBy;
        this.currentAmount = 0.0;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isCompleted = false;
    }

    // Constructor with description
    public SavingsGoal(String familyId, String goalName, String description, double targetAmount,
            Date targetDate, String createdBy) {
        this.familyId = familyId;
        this.goalName = goalName;
        this.description = description;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.createdBy = createdBy;
        this.currentAmount = 0.0;
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isCompleted = false;
    }

    // Full constructor (for database retrieval)
    public SavingsGoal(String goalId, String familyId, String goalName, String description,
            double targetAmount, double currentAmount, Date targetDate, Date createdDate,
            Date lastModifiedDate, boolean isActive, boolean isCompleted, String createdBy) {
        this.goalId = goalId;
        this.familyId = familyId;
        this.goalName = goalName;
        this.description = description;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.isActive = isActive;
        this.isCompleted = isCompleted;
        this.createdBy = createdBy;
    }

    // Default constructor
    public SavingsGoal() {
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
        this.isActive = true;
        this.isCompleted = false;
        this.currentAmount = 0.0;
        this.targetAmount = 0.0;
    }

    // Getters and Setters
    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
        this.lastModifiedDate = new Date();
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
        this.lastModifiedDate = new Date();
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
        this.lastModifiedDate = new Date();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModifiedDate = new Date();
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
        this.lastModifiedDate = new Date();
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
        this.lastModifiedDate = new Date();

        // Auto-complete if target is reached
        if (this.currentAmount >= this.targetAmount && !this.isCompleted) {
            this.isCompleted = true;
        }
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.lastModifiedDate = new Date();
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
        this.lastModifiedDate = new Date();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        this.lastModifiedDate = new Date();
    }

    // Business logic methods

    // Add amount to current savings
    public void addToSavings(double amount) {
        if (amount > 0) {
            this.currentAmount += amount;
            this.lastModifiedDate = new Date();

            // Auto-complete if target is reached
            if (this.currentAmount >= this.targetAmount && !this.isCompleted) {
                this.isCompleted = true;
            }
        }
    }

    // Get progress percentage (0-100)
    public double getProgressPercentage() {
        if (targetAmount <= 0)
            return 0.0;
        double percentage = (currentAmount / targetAmount) * 100.0;
        return Math.min(percentage, 100.0); // Cap at 100%
    }

    // Get remaining amount to reach goal
    public double getRemainingAmount() {
        double remaining = targetAmount - currentAmount;
        return Math.max(remaining, 0.0); // Don't return negative
    }

    // Get formatted target amount
    public String getFormattedTargetAmount() {
        return String.format("$%.2f", targetAmount);
    }

    // Get formatted current amount
    public String getFormattedCurrentAmount() {
        return String.format("$%.2f", currentAmount);
    }

    // Get formatted remaining amount
    public String getFormattedRemainingAmount() {
        return String.format("$%.2f", getRemainingAmount());
    }

    // Check if goal is overdue
    public boolean isOverdue() {
        if (targetDate == null || isCompleted)
            return false;
        return new Date().after(targetDate);
    }

    // Get days remaining until target date
    public long getDaysRemaining() {
        if (targetDate == null || isCompleted)
            return 0;

        Date now = new Date();
        if (now.after(targetDate))
            return 0; // Overdue

        long diffInMillies = targetDate.getTime() - now.getTime();
        return diffInMillies / (24 * 60 * 60 * 1000);
    }

    // Get goal status display
    public String getStatusDisplay() {
        if (isCompleted) {
            return "🎉 Completed";
        } else if (isOverdue()) {
            return "⏰ Overdue";
        } else if (getDaysRemaining() <= 30) {
            return "🔔 Due Soon";
        } else {
            return "📈 In Progress";
        }
    }

    // Get goal priority based on time remaining
    public String getPriority() {
        if (isCompleted)
            return "Completed";
        if (isOverdue())
            return "Overdue";

        long days = getDaysRemaining();
        if (days <= 7)
            return "High";
        if (days <= 30)
            return "Medium";
        return "Low";
    }

    // Calculate recommended monthly savings
    public double getRecommendedMonthlySavings() {
        if (targetDate == null || isCompleted)
            return 0.0;

        long daysRemaining = getDaysRemaining();
        if (daysRemaining <= 0)
            return getRemainingAmount();

        double monthsRemaining = daysRemaining / 30.0;
        if (monthsRemaining < 1)
            monthsRemaining = 1; // At least 1 month

        return getRemainingAmount() / monthsRemaining;
    }

    // Get formatted recommended monthly savings
    public String getFormattedRecommendedMonthlySavings() {
        return String.format("$%.2f", getRecommendedMonthlySavings());
    }

    // Validate savings goal data
    public boolean isValid() {
        return goalName != null && !goalName.trim().isEmpty() &&
                familyId != null && !familyId.trim().isEmpty() &&
                targetAmount > 0 &&
                currentAmount >= 0 &&
                targetDate != null &&
                createdBy != null && !createdBy.trim().isEmpty();
    }

    // Check if goal has required fields
    public boolean hasRequiredFields() {
        return isValid();
    }

    // Get short description for display
    public String getShortDescription() {
        if (description == null || description.trim().isEmpty()) {
            return "No description";
        }
        String trimmed = description.trim();
        return trimmed.length() <= 50 ? trimmed : trimmed.substring(0, 47) + "...";
    }

    // Get goal icon based on name/description
    public String getGoalIcon() {
        if (goalName == null)
            return "💰";

        String name = goalName.toLowerCase();
        if (name.contains("vacation") || name.contains("travel") || name.contains("trip"))
            return "✈️";
        if (name.contains("car") || name.contains("vehicle"))
            return "🚗";
        if (name.contains("house") || name.contains("home"))
            return "🏠";
        if (name.contains("education") || name.contains("school") || name.contains("college"))
            return "🎓";
        if (name.contains("emergency"))
            return "🆘";
        if (name.contains("wedding"))
            return "💒";
        if (name.contains("baby") || name.contains("child"))
            return "👶";
        if (name.contains("retirement"))
            return "👴";

        return "💰"; // Default savings icon
    }

    @Override
    public String toString() {
        return "SavingsGoal{" +
                "goalId='" + goalId + '\'' +
                ", familyId='" + familyId + '\'' +
                ", goalName='" + goalName + '\'' +
                ", targetAmount=" + targetAmount +
                ", currentAmount=" + currentAmount +
                ", targetDate=" + targetDate +
                ", isCompleted=" + isCompleted +
                ", isActive=" + isActive +
                '}';
    }
}