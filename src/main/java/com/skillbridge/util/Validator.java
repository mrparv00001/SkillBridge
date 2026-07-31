package com.skillbridge.util;

import java.time.LocalDate;
import java.time.LocalTime;

public class Validator {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";

    private Validator() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 6) {
            return false;
        }
        return true;
    }

    public static boolean isValidPhone(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return phoneNumber.matches(PHONE_PATTERN);
    }

    public static boolean isValidEnrollmentNo(String enrollmentNumber) {
        if (enrollmentNumber == null || enrollmentNumber.trim().isEmpty()) {
            return false;
        }
        if (enrollmentNumber.length() < 5) {
            return false;
        }
        return true;
    }

    // UPGRADED: Now allows scheduling sessions for TODAY
    public static boolean isFutureDate(LocalDate sessionDate) {
        if (sessionDate == null) {
            return false;
        }
        return !sessionDate.isBefore(LocalDate.now());
    }

    public static boolean isValidTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return startTime.isBefore(endTime);
    }

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