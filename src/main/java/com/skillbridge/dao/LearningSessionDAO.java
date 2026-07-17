package com.skillbridge.dao;

import com.skillbridge.model.LearningSession;
import com.skillbridge.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LearningSessionDAO {

    // 1. Save a new session to the database
    public boolean scheduleSession(LearningSession session) {
        String sql = "INSERT INTO LearningSessions (request_id, teacher_id, learner_id, skill_id, " +
                "session_date, start_time, end_time, mode, meeting_link, location, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Scheduled')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, session.getRequestId());
            stmt.setInt(2, session.getTeacherId());
            stmt.setInt(3, session.getLearnerId());
            stmt.setInt(4, session.getSkillId());

            // Convert Java Dates/Times to SQL Dates/Times
            stmt.setDate(5, Date.valueOf(session.getSessionDate()));
            stmt.setTime(6, Time.valueOf(session.getStartTime()));
            stmt.setTime(7, Time.valueOf(session.getEndTime()));

            stmt.setString(8, session.getMode());
            stmt.setString(9, session.getMeetingLink());
            stmt.setString(10, session.getLocation());

            int rowsSaved = stmt.executeUpdate();
            return rowsSaved > 0;

        } catch (SQLException e) {
            System.out.println("Error scheduling session in DB: " + e.getMessage());
            return false;
        }
    }

    // 2. Fetch all sessions for a user (whether they taught or learned)
    public List<LearningSession> getSessionsByUser(int userId) {
        List<LearningSession> sessions = new ArrayList<>();
        // Query looks for sessions where user is either teacher OR learner, sorted newest first
        String sql = "SELECT * FROM LearningSessions WHERE teacher_id = ? OR learner_id = ? ORDER BY session_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            // Loop through all results and add them to our list
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user sessions: " + e.getMessage());
        }
        return sessions;
    }

    // 3. Find a single session by its ID
    public LearningSession getSessionById(int sessionId) {
        String sql = "SELECT * FROM LearningSessions WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error finding session by ID: " + e.getMessage());
        }
        return null;
    }

    // 4. Mark a session as 'Completed' and record the current time
    public boolean markSessionCompleted(int sessionId) {
        // CURRENT_TIMESTAMP is a MySQL command to get the exact current time
        String sql = "UPDATE LearningSessions SET status = 'Completed', completed_at = CURRENT_TIMESTAMP WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sessionId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error completing session: " + e.getMessage());
            return false;
        }
    }

    // 5. Change the status of a session (e.g., to 'Cancelled')
    public boolean updateSessionStatus(int sessionId, String newStatus) {
        String sql = "UPDATE LearningSessions SET status = ? WHERE session_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, sessionId);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error updating session status: " + e.getMessage());
            return false;
        }
    }

    // --- Helper Method ---
    // Converts a database row into a Java LearningSession object
    private LearningSession mapResultSetToSession(ResultSet rs) throws SQLException {

        // Safely check if completed_at is null before converting
        Timestamp dbTimestamp = rs.getTimestamp("completed_at");
        java.time.LocalDateTime completedAt = (dbTimestamp != null) ? dbTimestamp.toLocalDateTime() : null;

        return new LearningSession(
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
                completedAt
        );
    }
}