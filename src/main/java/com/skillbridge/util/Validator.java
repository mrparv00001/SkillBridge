package com.skillbridge.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class Validator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";

    private Validator() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 6) {
            System.out.println("❌ Password must be at least 6 characters long.");
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpperCase = true;
            else if (Character.isLowerCase(ch)) hasLowerCase = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if (specialChars.indexOf(ch) >= 0) hasSpecialChar = true;
        }

        if (!hasUpperCase) {
            System.out.println("❌ Password must contain at least one UPPERCASE letter.");
            return false;
        }
        if (!hasLowerCase) {
            System.out.println("❌ Password must contain at least one lowercase letter.");
            return false;
        }
        if (!hasDigit) {
            System.out.println("❌ Password must contain at least one number.");
            return false;
        }
        if (!hasSpecialChar) {
            System.out.println("❌ Password must contain at least one special character (!@#$% etc).");
            return false;
        }

        return true;
    }

    public static boolean isValidPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) return false;
        return phoneNumber.matches(PHONE_PATTERN);
    }

    public static boolean isValidEnrollmentNo(String enrollmentNumber) {
        if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) return false;
        return enrollmentNumber.length() >= 5;
    }

    public static boolean isFutureDate(LocalDate sessionDate) {
        if (sessionDate == null) return false;
        return !sessionDate.isBefore(LocalDate.now());
    }

    public static boolean isValidTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) return false;
        return startTime.isBefore(endTime);
    }

    public static boolean isEmptyText(String text) {
        return text == null || text.trim().isEmpty();
    }

    public static int safeParseInt(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static LocalDate safeParseDate(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(input.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static LocalTime safeParseTime(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return LocalTime.parse(input.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}