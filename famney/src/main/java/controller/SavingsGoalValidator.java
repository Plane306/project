package controller;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Validates savings goal input for F107.
 * Server-side validation for all goal operations.
 */
public class SavingsGoalValidator implements Serializable {

    private String amountPattern = "^\\d+(\\.\\d{1,2})?$";
    private String descriptionPattern = "^.{0,500}$";

    private boolean validate(String pattern, String input) {
        Pattern regEx = Pattern.compile(pattern);
        Matcher match = regEx.matcher(input);
        return match.matches();
    }

    public boolean validateGoalName(String name) {
        return name != null && !name.trim().isEmpty() &&
                name.trim().length() >= 1 && name.trim().length() <= 100;
    }

    public boolean validateTargetAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        if (!validate(amountPattern, amount)) {
            return false;
        }
        try {
            double value = Double.parseDouble(amount);
            return value > 0 && value <= 9999999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return true;
        }
        return validate(descriptionPattern, description);
    }

    public boolean validateTargetDate(Date targetDate) {
        if (targetDate == null)
            return false;
        return targetDate.after(new Date());
    }

    public boolean validateTargetDateString(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateString);
            return validateTargetDate(date);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validateContribution(String amount) {
        if (amount == null || amount.trim().isEmpty()) {
            return false;
        }
        try {
            double value = Double.parseDouble(amount);
            return value > 0 && value <= 999999.99;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateSavingsGoal(String goalName, String targetAmount, String targetDate) {
        return validateGoalName(goalName) &&
                validateTargetAmount(targetAmount) &&
                validateTargetDateString(targetDate);
    }
}
