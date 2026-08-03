package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean isEmailTaken(String email) {
        return userDAO.getUserByEmail(email) != null;
    }

    public boolean isEnrollmentNoTaken(String enrollmentNo) {
        return userDAO.getUserByEnrollmentNo(enrollmentNo) != null;
    }

    public boolean registerNewUser(User user) {
        if (isEmailTaken(user.getEmail())) {
            System.out.println("❌ Email already registered.");
            return false;
        }

        if (isEnrollmentNoTaken(user.getEnrollmentNo())) {
            System.out.println("❌ Enrollment number already registered.");
            return false;
        }

        return userDAO.createUser(user);
    }

    public User getUserProfile(int userId) {
        return userDAO.getUserById(userId);
    }

    public boolean updateUserProfile(User user) {
        if (!Validator.isValidPhone(user.getPhone())) {
            System.out.println("❌ Phone must be exactly 10 digits.");
            return false;
        }

        if (Validator.isEmptyText(user.getFullName())) {
            System.out.println("❌ Name cannot be empty.");
            return false;
        }

        boolean success = userDAO.updateUser(user);

        if (success) {
            System.out.println("✅ Profile updated successfully!");
            // FIX: Refresh SessionManager with new data
            User updatedUser = userDAO.getUserById(user.getUserId());
            if (updatedUser != null) {
                SessionManager.login(updatedUser);
            }
        } else {
            System.out.println("❌ Failed to update profile.");
        }

        return success;
    }
}