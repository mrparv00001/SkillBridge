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
}