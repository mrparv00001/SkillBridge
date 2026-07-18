package com.skillbridge.dao;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.UserSkill; // Make sure this matches your model's exact package and name

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserSkillDAO {

    // Method to link a skill to a specific user's profile
    public boolean addUserSkill(UserSkill userSkill) {
        // The SQL matches the table we just built: user_id, skill_id, type, and level
        String sql = "INSERT INTO UserSkills (user_id, skill_id, skill_type, skill_level) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Bind the Java object data to the SQL placeholders
            stmt.setInt(1, userSkill.getUserId());
            stmt.setInt(2, userSkill.getSkillId());
            stmt.setString(3, userSkill.getSkillType());   // e.g., "TEACHING" or "LEARNING"
            stmt.setString(4, userSkill.getSkillLevel());  // e.g., "BEGINNER", "INTERMEDIATE", "ADVANCED"

            // Execute the update and confirm if the row was successfully saved
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error adding skill to user profile: " + e.getMessage());
            return false;
        }
    }

    // Method to find users offering or looking for a specific skill (The Matchmaker)
    public List<UserSkill> findUsersBySkill(int skillId, String skillType) {
        List<UserSkill> matchedUsers = new ArrayList<>();
        String sql = "SELECT * FROM UserSkills WHERE skill_id = ? AND skill_type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, skillId);
            stmt.setString(2, skillType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserSkill userSkill = new UserSkill(
                        rs.getInt("user_skill_id"),
                        rs.getInt("skill_id"), // Swapped order to match model
                        rs.getInt("user_id"),
                        UserSkill.SkillType.valueOf(rs.getString("skill_type").toUpperCase()),
                        UserSkill.SkillLevel.valueOf(rs.getString("skill_level").toUpperCase()),
                        true
                );
                matchedUsers.add(userSkill);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return matchedUsers;
    }
}