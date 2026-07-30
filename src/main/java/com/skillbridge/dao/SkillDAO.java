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
}