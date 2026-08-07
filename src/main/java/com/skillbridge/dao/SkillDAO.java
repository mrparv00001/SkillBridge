package com.skillbridge.dao;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.Skill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        // Using skill_name to match the database
        String query = "SELECT * FROM Skills ORDER BY skill_name ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Re-added the description field!
                Skill skill = new Skill(
                        rs.getInt("skill_id"),
                        rs.getString("skill_name"),
                        rs.getString("category"),
                        rs.getString("description")
                );
                skills.add(skill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching skills: " + e.getMessage(), e);
        }
        return skills;
    }

    public boolean addSkill(Skill skill) {
        // Re-added description to the INSERT statement
        String query = "INSERT INTO Skills (skill_name, category, description) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, skill.getSkillName());
            pstmt.setString(2, skill.getCategory());
            pstmt.setString(3, skill.getDescription()); // Setting the description

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getTrendingSkills(int limit) {
        List<String> trending = new ArrayList<>();
        String sql = "SELECT s.skill_name, s.category, COUNT(ls.session_id) AS session_count " +
                "FROM Skills s " +
                "JOIN LearningSessions ls ON s.skill_id = ls.skill_id " +
                "GROUP BY s.skill_id, s.skill_name, s.category " +
                "ORDER BY session_count DESC " +
                "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            int rank = 1;
            while (rs.next()) {
                trending.add(String.format("%-6d %-25s %-10d %-15s",
                        rank,
                        rs.getString("skill_name"),
                        rs.getInt("session_count"),
                        rs.getString("category")));
                rank++;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching trending skills: " + e.getMessage());
        }
        return trending;
    }
}