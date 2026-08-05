package com.skillbridge.dao;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManagerDAO {

    public Manager getManagerByUsername(String username) {
        String sql = "SELECT * FROM Managers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                return new Manager(
                        rs.getInt("manager_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        ts != null ? ts.toLocalDateTime() : null
                );
            }
        } catch (SQLException e) {
            System.err.println("Error finding manager: " + e.getMessage());
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("created_at");
                User u = new User(
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
                        ts != null ? ts.toLocalDateTime() : null,
                        rs.getInt("credits")
                );
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM Skills ORDER BY skill_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                skills.add(new Skill(
                        rs.getInt("skill_id"),
                        rs.getString("skill_name"),
                        rs.getString("category"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching skills: " + e.getMessage());
        }
        return skills;
    }

    public List<ExchangeRequest> getAllRequests() {
        List<ExchangeRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM ExchangeRequests ORDER BY request_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                requests.add(new ExchangeRequest(
                        rs.getInt("request_id"),
                        rs.getInt("sender_id"),
                        rs.getInt("receiver_id"),
                        rs.getInt("requested_skill_id"),
                        rs.getString("exchange_type"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getTimestamp("request_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching requests: " + e.getMessage());
        }
        return requests;
    }

    public List<LearningSession> getAllSessions() {
        List<LearningSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM LearningSessions ORDER BY session_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("completed_at");
                sessions.add(new LearningSession(
                        rs.getInt("session_id"),
                        rs.getInt("request_id"),
                        rs.getInt("teacher_id"),
                        rs.getInt("learner_id"),
                        rs.getInt("skill_id"),
                        rs.getDate("session_date").toLocalDate(),
                        rs.getTime("start_time").toLocalTime(),
                        rs.getTime("end_time").toLocalTime(),
                        rs.getString("mode"),
                        rs.getString("meeting_link"),
                        rs.getString("location"),
                        rs.getString("status"),
                        ts != null ? ts.toLocalDateTime() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sessions: " + e.getMessage());
        }
        return sessions;
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback ORDER BY feedback_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                feedbackList.add(new Feedback(
                        rs.getInt("feedback_id"),
                        rs.getInt("session_id"),
                        rs.getInt("reviewer_id"),
                        rs.getInt("reviewed_user_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("feedback_date")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching feedback: " + e.getMessage());
        }
        return feedbackList;
    }

    // Add these 3 methods in ManagerDAO.java

    public int getCancellationCountByUser(int userId) {
        int count = 0;
        String sql = "SELECT COUNT(*) as cancel_count FROM LearningSessions " +
                "WHERE (teacher_id = ? OR learner_id = ?) AND status = 'Cancelled'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("cancel_count");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cancellation count: " + e.getMessage());
        }
        return count;
    }

    public boolean deactivateUser(int userId) {
        String sql = "UPDATE Users SET is_active = false WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deductCredits(int userId, int amount) {
        String sql = "UPDATE Users SET credits = GREATEST(0, credits - ?) WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deducting credits: " + e.getMessage());
            return false;
        }
    }
}