package com.skillbridge.dao;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.UserSkill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserSkillDAO {

    public boolean addUserSkill(UserSkill userSkill) {
        String sql = "INSERT INTO UserSkills (user_id, skill_id, skill_type, skill_level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userSkill.getUserId());
            stmt.setInt(2, userSkill.getSkillId());
            // Since Member 2's getters return Strings, we can use them directly
            stmt.setString(3, userSkill.getSkillType());
            stmt.setString(4, userSkill.getSkillLevel());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding skill to user profile: " + e.getMessage());
            return false;
        }
    }

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
                        rs.getInt("skill_id"),
                        rs.getInt("user_id"),
                        UserSkill.SkillType.valueOf(rs.getString("skill_type").toUpperCase()),
                        UserSkill.SkillLevel.valueOf(rs.getString("skill_level").toUpperCase()),
                        rs.getBoolean("is_active") // Member 2 had this hardcoded to true, fixed it to pull from DB
                );
                matchedUsers.add(userSkill);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return matchedUsers;
    }

    // --- MISSING METHOD 1 ADDED ---
    public List<UserSkill> getSkillsByUserId(int userId) {
        List<UserSkill> userSkills = new ArrayList<>();
        String sql = "SELECT * FROM UserSkills WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UserSkill userSkill = new UserSkill(
                        rs.getInt("user_skill_id"),
                        rs.getInt("skill_id"),
                        rs.getInt("user_id"),
                        UserSkill.SkillType.valueOf(rs.getString("skill_type").toUpperCase()),
                        UserSkill.SkillLevel.valueOf(rs.getString("skill_level").toUpperCase()),
                        rs.getBoolean("is_active")
                );
                userSkills.add(userSkill);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user skills: " + e.getMessage());
        }
        return userSkills;
    }

    // --- MISSING METHOD 2 ADDED ---
    public boolean deleteUserSkill(int userSkillId) {
        String sql = "DELETE FROM UserSkills WHERE user_skill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userSkillId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user skill: " + e.getMessage());
            return false;
        }
    }
}