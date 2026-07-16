package org.skillbridge.dao;

import org.skillbridge.database.DBConnection;
import org.skillbridge.model.Skill;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {

  public List<Skill> getAllSkills()
  {
        List<Skill> skills = new ArrayList<>();
      String query = "SELECT * FROM Skills ORDER BY name ASC";
      try {
          Connection conn = DBConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(query);
          while (rs.next()) {
              Skill skill = new Skill(
                      rs.getInt("skill_id"),
                      rs.getString("name"),
                      rs.getString("category"),
                      rs.getString("description"));
              skills.add(skill);
          }
      }
      catch (SQLException e) {
          throw new RuntimeException(e);
      }
      return skills;
  }

    public boolean addSkill(Skill skill) {
      String query = "INSERT INTO Skills (name, category, description) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, skill.getSkillName());
            pstmt.setString(2, skill.getCategory());
            pstmt.setString(3, skill.getDescription());

            // Execute the update and check if any rows were affected
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}