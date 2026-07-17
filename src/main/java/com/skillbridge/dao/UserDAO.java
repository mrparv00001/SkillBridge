package com.skillbridge.dao;

import com.skillbridge.model.User;
import com.skillbridge.database.DBConnection; // We will use this to connect to the database

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserDAO {

    // 1. Method to save a brand new user to the database
    public boolean createUser(User user) {
        // The SQL query to insert data. '?' are placeholders we will fill in.
        String sql = "INSERT INTO Users (full_name, enrollment_no, department, semester, email, password_hash, phone, bio) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Try-with-resources: This automatically closes the connection when done
        try (Connection con = DBConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Fill in the '?' placeholders with actual user data
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEnrollmentNo());
            stmt.setString(3, user.getDepartment());
            stmt.setInt(4, user.getSemester());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPasswordHash());
            stmt.setString(7, user.getPhone());
            stmt.setString(8, user.getBio());

            // Run the query. executeUpdate() returns the number of rows saved.
            int rowsSaved = stmt.executeUpdate();
            return rowsSaved > 0; // Return true if at least 1 row was saved

        } catch (SQLException e) {
            System.out.println("Error saving user to database: " + e.getMessage());
            return false;
        }
    }

    // 2. Method to find a user by their email (Used for Login)
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery(); // Run the query and get results

            // If we find a row in the database, pack it into a User object
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by email: " + e.getMessage());
        }
        return null; // Return null if no user is found
    }

    // 3. Method to find a user by their enrollment number (To check uniqueness)
    public User getUserByEnrollmentNo(String enrollmentNo) {
        String sql = "SELECT * FROM Users WHERE enrollment_no = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, enrollmentNo);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by enrollment number: " + e.getMessage());
        }
        return null;
    }

    // 4. Method to get user by their ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding user by ID: " + e.getMessage());
        }
        return null;
    }

    // 5. Method to update a user's profile details
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

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user profile: " + e.getMessage());
            return false;
        }
    }

    // --- Helper Method ---
    // Instead of writing this code 4 times above, we write it once here.
    // This takes a row from the database and turns it into a Java User object.
    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        // Convert MySQL DATETIME to Java LocalDateTime safely
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
                createdAt
        );
    }
}