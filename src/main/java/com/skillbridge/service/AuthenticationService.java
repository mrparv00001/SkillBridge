package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

public class AuthenticationService {

    // We need our manager and worker to handle the database side
    private UserService userService;
    private UserDAO userDAO;

    // Constructor: Set up the tools when this service is created
    public AuthenticationService() {
        this.userService = new UserService();
        this.userDAO = new UserDAO();
    }

    // 1. Handle User Registration
    public boolean register(User newUser) {

        // Rule 1: Check if the email looks like a real email
        if (!Validator.isValidEmail(newUser.getEmail())) {
            System.out.println("Registration failed: Invalid email format.");
            return false;
        }

        // Rule 2: Check if password is secure (at least 6 characters)
        if (!Validator.isValidPassword(newUser.getPasswordHash())) {
            System.out.println("Registration failed: Password must be at least 6 characters long.");
            return false;
        }

        // If rules pass, hand it over to UserService to check for duplicate emails and save it
        return userService.registerNewUser(newUser);
    }

    // 2. Handle User Login
    public boolean login(String email, String password) {

        // Step 1: Check if they left the email or password blank
        if (Validator.isEmptyText(email) || Validator.isEmptyText(password)) {
            System.out.println("Login failed: Email and Password cannot be empty.");
            return false;
        }

        // Step 2: Ask the Worker (DAO) to find a user with this email
        User user = userDAO.getUserByEmail(email);

        // Step 3: Check if the user exists
        if (user == null) {
            System.out.println("Login failed: No account found with that email.");
            return false;
        }

        // Step 4: Check if the password matches
        // (Note: In a professional app, passwords are encrypted.
        // For this beginner-friendly version, we are comparing the exact text).
        if (!user.getPasswordHash().equals(password)) {
            System.out.println("Login failed: Incorrect password.");
            return false;
        }

        // Step 5: Check if the account is active
        if (!user.isActive()) {
            System.out.println("Login failed: Your account has been deactivated.");
            return false;
        }

        // Success! Save the user in our app's memory (SessionManager)
        SessionManager.login(user);
        System.out.println("Login successful! Welcome, " + user.getFullName() + ".");
        return true;
    }

    // 3. Handle User Logout
    public void logout() {
        // Clear the user from memory
        SessionManager.logout();
        System.out.println("You have been successfully logged out.");
    }
}