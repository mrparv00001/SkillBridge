package com.skillbridge.util;

import java.time.LocalDate;
import java.time.LocalTime;

public class Validator {

    // Simple rules for checking formats
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";

    // We make this private so no one can create an object of Validator.
    // We only want to use its methods directly.
    private Validator() {
    }

    // Checks if the email looks like a real email
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false; // Email is empty
        }
        // Uses Java's built-in string matcher
        return email.matches(EMAIL_PATTERN);
    }

    // Checks if the password has at least 6 characters
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false; // Password is too short
        }
        return true;
    }

    // Checks if phone number has exactly 10 digits
    public static boolean isValidPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return phoneNumber.matches(PHONE_PATTERN);
    }

    // Checks if the enrollment number is entered and is at least 5 characters long
    public static boolean isValidEnrollmentNo(String enrollmentNumber) {
        if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) {
            return false;
        }
        if (enrollmentNumber.length() < 5) {
            return false;
        }
        return true;
    }

    // Checks if the given date is in the future (not today or past)
    public static boolean isFutureDate(LocalDate sessionDate) {
        if (sessionDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();

        if (sessionDate.isAfter(today)) {
            return true;
        } else {
            return false; // Date is today or in the past
        }
    }

    // Checks if the start time comes before the end time
    public static boolean isValidTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }

        if (startTime.isBefore(endTime)) {
            return true;
        } else {
            return false; // Start time is after end time
        }
    }

    // A simple helper to check if any text is empty
    public static boolean isEmptyText(String text) {
        if (text == null) {
            return true;
        }
        if (text.trim().isEmpty()) {
            return true;
        }
        return false;
    }
}