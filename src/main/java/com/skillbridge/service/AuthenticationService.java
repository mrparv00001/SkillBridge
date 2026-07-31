package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationService {

    private UserService userService;
    private UserDAO userDAO;

    public AuthenticationService() {
        this.userService = new UserService();
        this.userDAO = new UserDAO();
    }

    public boolean register(User newUser) {
        // MILLIMETER UPGRADE: Standardize email format (Lowercase & Trim)
        if (newUser.getEmail() != null) {
            newUser.setEmail(newUser.getEmail().toLowerCase().trim());
        }

        if (!Validator.isValidEmail(newUser.getEmail())) {
            System.out.println("Registration failed: Invalid email format.");
            return false;
        }

        if (!Validator.isValidPassword(newUser.getPasswordHash())) {
            System.out.println("Registration failed: Password must be at least 6 characters long.");
            return false;
        }

        String encryptedPassword = hashPassword(newUser.getPasswordHash());
        newUser.setPasswordHash(encryptedPassword);

        return userService.registerNewUser(newUser);
    }

    public boolean login(String email, String password) {
        if (Validator.isEmptyText(email) || Validator.isEmptyText(password)) {
            System.out.println("Login failed: Email and Password cannot be empty.");
            return false;
        }

        // MILLIMETER UPGRADE: Standardize input email before querying database
        email = email.toLowerCase().trim();
        User user = userDAO.getUserByEmail(email);

        if (user == null) {
            System.out.println("Login failed: No account found with that email.");
            return false;
        }

        String encryptedInputPassword = hashPassword(password);
        if (!user.getPasswordHash().equals(encryptedInputPassword)) {
            System.out.println("Login failed: Incorrect password.");
            return false;
        }

        if (!user.isActive()) {
            System.out.println("Login failed: Your account has been deactivated.");
            return false;
        }

        SessionManager.login(user);
        System.out.println("Login successful! Welcome, " + user.getFullName() + ".");
        return true;
    }

    public void logout() {
        SessionManager.logout();
        System.out.println("You have been successfully logged out.");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: Hashing algorithm not found.", e);
        }
    }
}