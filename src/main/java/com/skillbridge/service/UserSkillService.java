package com.skillbridge.service;

import com.skillbridge.dao.UserSkillDAO;
import com.skillbridge.model.UserSkill;

import java.util.List;

public class UserSkillService {

    private final UserSkillDAO userSkillDAO;

    public UserSkillService() {
        this.userSkillDAO = new UserSkillDAO();
    }

    public boolean addSkillToUser(int userId, int skillId, String skillType, String skillLevel) {
        try {
            // Using Member 2's constructor to convert Strings to their Enums safely
            // Passed 0 for ID because the database auto-increments it
            UserSkill userSkill = new UserSkill(
                    0,
                    skillId,
                    userId,
                    UserSkill.SkillType.valueOf(skillType.toUpperCase()),
                    UserSkill.SkillLevel.valueOf(skillLevel.toUpperCase()),
                    true
            );

            return userSkillDAO.addUserSkill(userSkill);

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid Skill Type or Level format. Please type exactly 'TEACHING' or 'LEARNING'.");
            return false;
        }
    }

    public List<UserSkill> getUserSkills(int userId) {
        return userSkillDAO.getSkillsByUserId(userId);
    }

    public boolean removeSkillFromUser(int userSkillId) {
        return userSkillDAO.deleteUserSkill(userSkillId);
    }
}