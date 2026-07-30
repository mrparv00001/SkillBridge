package com.skillbridge.service;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RecommendationService {

    /**
     * Enhanced recommendation method that incorporates matching validation,
     * self-match prevention, and priority sorting based on ratings, review counts,
     * skill levels, and user demographics[cite: 1].
     */
    public List<User> getRecommendedTeachers(int learnerId, int desiredSkillId) {
        List<TeacherScore> scoredTeachers = new ArrayList<>();
        User learner = fetchUserById(learnerId);

        String sql = "SELECT u.*, us.skill_level FROM Users u " +
                "JOIN UserSkills us ON u.user_id = us.user_id " +
                "WHERE us.skill_id = ? AND us.skill_type = 'Teaching' AND u.is_active = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, desiredSkillId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int teacherId = rs.getInt("user_id");

                // Feature from MatchmakingService: Prevent learners from matching with themselves
                if (teacherId == learnerId) {
                    continue;
                }

                User teacher = mapResultSetToUser(rs);
                String skillLevel = rs.getString("skill_level");

                double avgRating = getAverageRatingForTeacherSkill(teacherId, desiredSkillId);
                int reviewCount = getReviewCountForTeacherSkill(teacherId, desiredSkillId);

                int score = 0;
                // Priority scoring calculation
                score += (int) (avgRating * 100);
                score += (reviewCount * 10);

                if ("Advanced".equalsIgnoreCase(skillLevel)) score += 50;
                else if ("Intermediate".equalsIgnoreCase(skillLevel)) score += 30;
                else if ("Beginner".equalsIgnoreCase(skillLevel)) score += 10;

                if (learner != null) {
                    if (learner.getDepartment() != null && learner.getDepartment().equalsIgnoreCase(teacher.getDepartment())) {
                        score += 20; // Same department bonus
                    }
                    if (learner.getSemester() == teacher.getSemester()) {
                        score += 10; // Same semester bonus
                    }
                }

                scoredTeachers.add(new TeacherScore(teacher, score));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching recommendations and matches: " + e.getMessage());
        }

        // Sort descending by calculated priority score
        scoredTeachers.sort(Comparator.comparingInt(TeacherScore::getScore).reversed());

        List<User> sortedTeachers = new ArrayList<>();
        for (TeacherScore ts : scoredTeachers) {
            sortedTeachers.add(ts.getTeacher());
        }

        if (sortedTeachers.isEmpty()) {
            System.out.println("ℹ️ No active teachers found for this skill match.");
        } else {
            System.out.println("✅ Found " + sortedTeachers.size() + " recommended teacher(s)!");
        }

        return sortedTeachers;
    }

    private double getAverageRatingForTeacherSkill(int userId, int skillId) {
        String sql = "SELECT AVG(f.rating) as avg_rating FROM Feedback f " +
                "JOIN LearningSessions ls ON f.session_id = ls.session_id " +
                "WHERE f.reviewed_user_id = ? AND ls.skill_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("avg_rating");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    private int getReviewCountForTeacherSkill(int userId, int skillId) {
        String sql = "SELECT COUNT(f.feedback_id) as review_count FROM Feedback f " +
                "JOIN LearningSessions ls ON f.session_id = ls.session_id " +
                "WHERE f.reviewed_user_id = ? AND ls.skill_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, skillId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("review_count");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private User fetchUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
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
        return user;
    }

    private static class TeacherScore {
        private final User teacher;
        private final int score;

        public TeacherScore(User teacher, int score) {
            this.teacher = teacher;
            this.score = score;
        }

        public User getTeacher() { return teacher; }
        public int getScore() { return score; }
    }
}