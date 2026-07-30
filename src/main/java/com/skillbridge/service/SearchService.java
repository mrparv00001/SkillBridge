package com.skillbridge.service;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    /**
     * Searches for active users teaching a specific skill within a specific department.
     */
    public List<User> searchStudentsByDepartment(String department, int skillId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u " +
                "JOIN UserSkills us ON u.user_id = us.user_id " +
                "WHERE u.department = ? AND us.skill_id = ? AND us.skill_type = 'Teaching' AND u.is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, department);
            stmt.setInt(2, skillId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching students by department: " + e.getMessage());
        }
        return users;
    }

    /**
     * Searches for active users teaching a specific skill within a specific semester.
     */
    public List<User> searchStudentsBySemester(int semester, int skillId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM Users u " +
                "JOIN UserSkills us ON u.user_id = us.user_id " +
                "WHERE u.semester = ? AND us.skill_id = ? AND us.skill_type = 'Teaching' AND u.is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, semester);
            stmt.setInt(2, skillId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error searching students by semester: " + e.getMessage());
        }
        return users;
    }

    // Helper method to map result set to User object
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
        return user;
    }
}