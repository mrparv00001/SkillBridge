package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.util.Validator;

public class UserService {

    // The service needs the DAO to talk to the database
    private UserDAO userDAO;

    // Constructor: When we create the service, we also create its DAO tool
    public UserService() {
        this.userDAO = new UserDAO();
    }

    // 1. Check if an email is already in the database
    public boolean isEmailTaken(String email) {
        User existingUser = userDAO.getUserByEmail(email);
        if (existingUser != null) {
            return true; // Someone is already using this email
        }
        return false; // Email is free to use
    }

    // 2. Check if an enrollment number is already in the database
    public boolean isEnrollmentNoTaken(String enrollmentNo) {
        User existingUser = userDAO.getUserByEnrollmentNo(enrollmentNo);
        if (existingUser != null) {
            return true; // Someone already registered with this number
        }
        return false; // Number is free to use
    }

    // 3. Register a brand new user safely
    public boolean registerNewUser(User user) {
        // Double check just in case the menu forgot to check
        if (isEmailTaken(user.getEmail())) {
            System.out.println("Registration failed: Email is already registered.");
            return false;
        }

        if (isEnrollmentNoTaken(user.getEnrollmentNo())) {
            System.out.println("Registration failed: Enrollment number is already registered.");
            return false;
        }

        // If everything is okay, tell the DAO to save the user to MySQL
        return userDAO.createUser(user);
    }

    // 4. Get a user's full profile
    public User getUserProfile(int userId) {
        return userDAO.getUserById(userId);
    }

    // 5. Update a user's profile safely
    public boolean updateUserProfile(User user) {
        // Check if the new phone number is correct before saving
        if (!Validator.isValidPhone(user.getPhone())) {
            System.out.println("Update failed: Phone number must be exactly 10 digits.");
            return false;
        }

        // Check if they left their name blank
        if (Validator.isEmptyText(user.getFullName())) {
            System.out.println("Update failed: Name cannot be empty.");
            return false;
        }

        // If rules pass, tell the DAO to update the database
        boolean success = userDAO.updateUser(user);

        if (success) {
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile in the database.");
        }

        return success;
    }
}