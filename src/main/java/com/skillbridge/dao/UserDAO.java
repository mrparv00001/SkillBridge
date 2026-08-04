package com.skillbridge.dao;

import com.skillbridge.model.User;
import com.skillbridge.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (full_name, enrollment_no, department, semester, email, password_hash, phone, bio, credits) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEnrollmentNo());
            stmt.setString(3, user.getDepartment());
            stmt.setInt(4, user.getSemester());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPasswordHash());
            stmt.setString(7, user.getPhone());
            stmt.setString(8, user.getBio());
            stmt.setInt(9, 50);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving user to database: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserCredits(int userId, int newBalance) {
        String sql = "UPDATE Users SET credits = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newBalance);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating credits: " + e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by email: " + e.getMessage());
        }
        return null;
    }

    public User getUserByEnrollmentNo(String enrollmentNo) {
        String sql = "SELECT * FROM Users WHERE enrollment_no = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, enrollmentNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by enrollment number: " + e.getMessage());
        }
        return null;
    }

    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET full_name = ?, department = ?, semester = ?, phone = ?, bio = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getDepartment());
            stmt.setInt(3, user.getSemester());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getBio());
            stmt.setInt(6, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    public List<User> getTopUsersByCredits(int limit) {
        List<User> topUsers = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE is_active = true ORDER BY credits DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    topUsers.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching leaderboard data: " + e.getMessage());
        }
        return topUsers;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        Timestamp dbTimestamp = rs.getTimestamp("created_at");
        java.time.LocalDateTime createdAt = (dbTimestamp != null) ? dbTimestamp.toLocalDateTime() : null;

        return new User(
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("enrollment_no"),
                rs.getString("department"),
                rs.getInt("semester"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getString("bio"),
                rs.getBoolean("is_active"),
                createdAt,
                rs.getInt("credits")
        );
    }
}