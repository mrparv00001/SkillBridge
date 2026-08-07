package com.skillbridge.service;

import com.skillbridge.dao.ManagerDAO;
import com.skillbridge.model.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ManagerService {

    private final ManagerDAO managerDAO = new ManagerDAO();

    // Manager login
    public Manager loginManager(String email, String password) {
        if (email == null || password == null || email.trim().isEmpty()) {
            return null;
        }

        Manager manager = managerDAO.getManagerByEmail(email.toLowerCase().trim());
        if (manager == null) {
            return null;
        }

        String hashedInput = hashPassword(password);
        if (!manager.getPasswordHash().equals(hashedInput)) {
            return null;
        }

        return manager;
    }

    // Get all users
    public List<User> getAllUsers() {
        return managerDAO.getAllUsers();
    }

    // Get all skills
    public List<Skill> getAllSkills() {
        return managerDAO.getAllSkills();
    }

    // Get all exchange requests
    public List<ExchangeRequest> getAllRequests() {
        return managerDAO.getAllRequests();
    }

    // Get all sessions
    public List<LearningSession> getAllSessions() {
        return managerDAO.getAllSessions();
    }

    // Get all feedback
    public List<Feedback> getAllFeedback() {
        return managerDAO.getAllFeedback();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error", e);
        }
    }

    // Add these 3 methods in ManagerService.java

    public int getCancellationCount(int userId) {
        return managerDAO.getCancellationCountByUser(userId);
    }

    public boolean deactivateUser(int userId) {
        return managerDAO.deactivateUser(userId);
    }

    public boolean deductCredits(int userId, int amount) {
        return managerDAO.deductCredits(userId, amount);
    }

    public boolean activateUser(int userId) {
        return managerDAO.activateUser(userId);
    }
}