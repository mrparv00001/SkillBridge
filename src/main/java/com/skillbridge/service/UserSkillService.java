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
            UserSkill userSkill = new UserSkill(
                    0,
                    skillId,
                    userId,
                    UserSkill.SkillType.valueOf(skillType.toUpperCase()),
                    UserSkill.SkillLevel.valueOf(skillLevel.toUpperCase()),
                    true
            );

            // Check if skill already exists for this user
            List<UserSkill> existingSkills = userSkillDAO.getSkillsByUserId(userId);
            for (UserSkill existing : existingSkills) {
                if (existing.getSkillId() == skillId && existing.getSkillType().equalsIgnoreCase(skillType)) {
                    // Skill already exists — Update level instead
                    if (existing.getSkillLevel().equalsIgnoreCase(skillLevel)) {
                        System.out.println("Already at " + skillLevel + " level. No change needed.");
                        return false;
                    }
                    boolean updated = userSkillDAO.updateSkillLevel(userId, skillId, skillType, skillLevel);
                    if (updated) {
                        System.out.println("Skill level updated: " + existing.getSkillLevel() + " -> " + skillLevel);
                    }
                    return updated;
                }
            }

            // New skill — Add normally
            return userSkillDAO.addUserSkill(userSkill);

        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Skill Type or Level.");
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