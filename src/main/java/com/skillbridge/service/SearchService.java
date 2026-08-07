package com.skillbridge.service;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    // ==========================================
    // BST NODE (Inner Class) - Data Structure
    // ==========================================
    private static class BSTNode {
        User user;
        BSTNode left, right;

        BSTNode(User user) {
            this.user = user;
        }
    }

    // BST Root
    private BSTNode root;

    /**
     * Insert user into BST (sorted by user_id).
     */
    private BSTNode insertBST(BSTNode node, User user) {
        if (node == null) return new BSTNode(user);

        if (user.getUserId() < node.user.getUserId()) {
            node.left = insertBST(node.left, user);
        } else if (user.getUserId() > node.user.getUserId()) {
            node.right = insertBST(node.right, user);
        }
        return node;
    }

    /**
     * In-order traversal to get sorted list from BST.
     */
    private void inOrderTraversal(BSTNode node, List<User> result) {
        if (node == null) return;
        inOrderTraversal(node.left, result);
        result.add(node.user);
        inOrderTraversal(node.right, result);
    }

    /**
     * Search Students by Department using BST.
     * Users are fetched from DB, inserted into BST, then traversed sorted.
     */
    public List<User> searchStudentsByDepartment(String department) {
        root = null;
        List<User> sortedUsers = new ArrayList<>();

        String sql = "SELECT DISTINCT u.* FROM Users u " +
                "JOIN UserSkills us ON u.user_id = us.user_id " +
                "WHERE u.department LIKE CONCAT('%', ?, '%') AND us.skill_type = 'Teaching' AND u.is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                root = insertBST(root, user);
            }

            inOrderTraversal(root, sortedUsers);

        } catch (SQLException e) {
            System.err.println("Error searching by department: " + e.getMessage());
        }

        return sortedUsers;
    }

    public List<User> searchStudentsBySemester(int semester) {
        root = null;
        List<User> sortedUsers = new ArrayList<>();

        String sql = "SELECT DISTINCT u.* FROM Users u " +
                "JOIN UserSkills us ON u.user_id = us.user_id " +
                "WHERE u.semester = ? AND us.skill_type = 'Teaching' AND u.is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, semester);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = mapResultSetToUser(rs);
                root = insertBST(root, user);
            }

            inOrderTraversal(root, sortedUsers);

        } catch (SQLException e) {
            System.err.println("Error searching by semester: " + e.getMessage());
        }

        return sortedUsers;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEnrollmentNo(rs.getString("enrollment_no"));
        user.setDepartment(rs.getString("department"));
        user.setSemester(rs.getInt("semester"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setBio(rs.getString("bio"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCredits(rs.getInt("credits"));
        return user;
    }
}