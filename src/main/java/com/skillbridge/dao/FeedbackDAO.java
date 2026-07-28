package com.skillbridge.dao;

import com.skillbridge.model.Feedback;
import com.skillbridge.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDAO {

    public boolean submitFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedback (session_id, reviewer_id, reviewed_user_id, rating, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, feedback.getSessionId());
            stmt.setInt(2, feedback.getReviewerId());
            stmt.setInt(3, feedback.getReviewedUserId());
            stmt.setInt(4, feedback.getRating());
            stmt.setString(5, feedback.getComment());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Feedback> getFeedbackByReviewedUser(int reviewedUserId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM Feedback WHERE reviewed_user_id = ? ORDER BY feedback_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reviewedUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    /**
     * Skill-wise Feedback Query (Note 1)
     * Retrieves feedback details along with skill details via SQL Joins.
     */
    public List<String> getSkillWiseFeedbackForUser(int userId) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT s.skill_name, f.rating, f.comment, f.feedback_date " +
                "FROM Feedback f " +
                "JOIN LearningSessions ls ON f.session_id = ls.session_id " +
                "JOIN Skills s ON ls.skill_id = s.skill_id " +
                "WHERE f.reviewed_user_id = ? " +
                "ORDER BY s.skill_name, f.feedback_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String line = String.format("Skill: %s | Rating: %d/5 | Comment: %s | Date: %s",
                        rs.getString("skill_name"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("feedback_date").toString());
                results.add(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        return new Feedback(
                rs.getInt("feedback_id"),
                rs.getInt("session_id"),
                rs.getInt("reviewer_id"),
                rs.getInt("reviewed_user_id"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getTimestamp("feedback_date")
        );
    }
}