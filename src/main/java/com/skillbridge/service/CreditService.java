package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.dao.UserSkillDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkill;

import java.util.List;

public class CreditService {

    private final UserDAO userDAO = new UserDAO();
    private final UserSkillDAO userSkillDAO = new UserSkillDAO();

    /**
     * Faculty-defined credit costs based on skill level.
     * Beginner = 5, Intermediate = 7, Advanced = 10
     */
    private int getCostByLevel(String skillLevel) {
        if ("Advanced".equalsIgnoreCase(skillLevel)) return 10;
        if ("Intermediate".equalsIgnoreCase(skillLevel)) return 7;
        return 5; // Beginner (default)
    }

    /**
     * Processes credit transaction before scheduling a session.
     * - One Way (Direct): Learner pays teacher based on skill level
     * - Two Way (Swap): No deduction - both exchange skills free
     */
    public boolean processTransaction(int teacherId, int learnerId, int teacherSkillId, boolean isSwap) {
        User teacher = userDAO.getUserById(teacherId);
        User learner = userDAO.getUserById(learnerId);

        if (teacher == null || learner == null) {
            System.out.println("Transaction Failed: Teacher or Learner not found.");
            return false;
        }

        // Get teacher's skill level for this specific skill
        String tLevel = getLevelForSkill(teacherId, teacherSkillId);
        int teacherCost = getCostByLevel(tLevel);

        if (!isSwap) {
            // --- ONE WAY LEARNING (Direct) ---
            if (learner.getCredits() < teacherCost) {
                System.out.println("Transaction Failed!");
                System.out.println("Required Credits: " + teacherCost);
                System.out.println("Your Balance: " + learner.getCredits());
                System.out.println("You need " + (teacherCost - learner.getCredits()) + " more credits.");
                return false;
            }

            // Deduct from learner, add to teacher
            userDAO.updateUserCredits(learnerId, learner.getCredits() - teacherCost);
            userDAO.updateUserCredits(teacherId, teacher.getCredits() + teacherCost);

            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║       CREDIT TRANSACTION SUCCESS         ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Type: One-Way Learning                   ║");
            System.out.println("║ Skill Level: " + tLevel);
            System.out.println("║ Credits Paid: " + teacherCost + " to Teacher");
            System.out.println("║ Your New Balance: " + (learner.getCredits() - teacherCost));
            System.out.println("╚══════════════════════════════════════════╝\n");

            return true;

        } else {
            // --- TWO WAY SWAP EXCHANGE ---
            // No credit deduction - both parties exchange skills freely
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║       SWAP EXCHANGE APPROVED             ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Type: Two-Way Swap Exchange              ║");
            System.out.println("║ Credits Deducted: 0 (Both parties)       ║");
            System.out.println("║ Both users exchange skills for free!     ║");
            System.out.println("╚══════════════════════════════════════════╝\n");

            return true;
        }
    }

    /**
     * Get skill level for a specific teaching skill of user.
     */
    private String getLevelForSkill(int userId, int skillId) {
        List<UserSkill> skills = userSkillDAO.getSkillsByUserId(userId);
        for (UserSkill s : skills) {
            if (s.getSkillId() == skillId && s.getSkillType().equalsIgnoreCase("Teaching")) {
                return s.getSkillLevel();
            }
        }
        return "Beginner"; // Default
    }

    /**
     * Check user's current credit balance
     */
    public int getUserCredits(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getCredits() : 0;
    }
}