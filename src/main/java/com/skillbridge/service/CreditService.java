package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.dao.UserSkillDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkill;

import java.util.List;

public class CreditService {

    private final UserDAO userDAO = new UserDAO();
    private final UserSkillDAO userSkillDAO = new UserSkillDAO();

    // Define the cost framework based on proficiency level
    private int getCostByLevel(String skillLevel) {
        if ("Advanced".equalsIgnoreCase(skillLevel)) return 30;
        if ("Intermediate".equalsIgnoreCase(skillLevel)) return 20;
        return 10; // Beginner
    }

    /**
     * Processes the transaction logic before scheduling a session.
     */
    public boolean processTransaction(int teacherId, int learnerId, int teacherSkillId, boolean isSwap) {
        User teacher = userDAO.getUserById(teacherId);
        User learner = userDAO.getUserById(learnerId);

        if (teacher == null || learner == null) return false;

        // 1. Determine Teacher's Cost
        String tLevel = getLevelForSkill(teacherId, teacherSkillId);
        int teacherCost = getCostByLevel(tLevel);

        if (!isSwap) {
            // --- DIRECT LEARNING ---
            if (learner.getCredits() < teacherCost) {
                System.out.println("❌ Transaction Failed: You need " + teacherCost + " credits, but you only have " + learner.getCredits() + ".");
                return false;
            }

            userDAO.updateUserCredits(learnerId, learner.getCredits() - teacherCost);
            userDAO.updateUserCredits(teacherId, teacher.getCredits() + teacherCost);
            System.out.println("💳 Paid " + teacherCost + " credits to the teacher for a " + tLevel + " session.");
            return true;

        } else {
            // --- SWAP EXCHANGE ---
            String lLevel = getHighestTeachingLevel(learnerId);
            int learnerCost = getCostByLevel(lLevel);

            if (teacherCost == learnerCost) {
                System.out.println("⚖️ Equal Skill Level Swap (Both " + tLevel + "): Zero transaction cost applied.");
                return true;
            } else if (teacherCost > learnerCost) {
                int diff = teacherCost - learnerCost;
                if (learner.getCredits() < diff) {
                    System.out.println("❌ Swap Failed: You need " + diff + " credits to cover the skill level difference.");
                    return false;
                }
                userDAO.updateUserCredits(learnerId, learner.getCredits() - diff);
                userDAO.updateUserCredits(teacherId, teacher.getCredits() + diff);
                System.out.println("⚖️ Swap Difference: Paid " + diff + " credits to the teacher (Higher Level).");
                return true;
            } else {
                int diff = learnerCost - teacherCost;
                if (teacher.getCredits() < diff) {
                    System.out.println("❌ Swap Failed: Teacher does not have enough credits to cover the difference.");
                    return false;
                }
                userDAO.updateUserCredits(teacherId, teacher.getCredits() - diff);
                userDAO.updateUserCredits(learnerId, learner.getCredits() + diff);
                System.out.println("⚖️ Swap Difference: You earned " + diff + " credits from the teacher (You have a Higher Level skill).");
                return true;
            }
        }
    }

    private String getLevelForSkill(int userId, int skillId) {
        List<UserSkill> skills = userSkillDAO.getSkillsByUserId(userId);
        for (UserSkill s : skills) {
            if (s.getSkillId() == skillId && s.getSkillType().equalsIgnoreCase("Teaching")) {
                return s.getSkillLevel();
            }
        }
        return "Beginner";
    }

    // Automatically finds the highest teaching level the learner has to use as swap currency
    private String getHighestTeachingLevel(int userId) {
        List<UserSkill> skills = userSkillDAO.getSkillsByUserId(userId);
        boolean hasIntermediate = false;

        for (UserSkill s : skills) {
            if (s.getSkillType().equalsIgnoreCase("Teaching")) {
                if ("Advanced".equalsIgnoreCase(s.getSkillLevel())) return "Advanced";
                if ("Intermediate".equalsIgnoreCase(s.getSkillLevel())) hasIntermediate = true;
            }
        }
        return hasIntermediate ? "Intermediate" : "Beginner";
    }
}