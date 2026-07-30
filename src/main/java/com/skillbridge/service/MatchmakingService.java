package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.dao.UserSkillDAO;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkill;

import java.util.ArrayList;
import java.util.List;

public class MatchmakingService {

    private final UserSkillDAO userSkillDAO;
    private final UserDAO userDAO;

    public MatchmakingService() {
        this.userSkillDAO = new UserSkillDAO();
        this.userDAO = new UserDAO();
    }

    /**
     * Finds active users who are registered to TEACH a specific skill.
     * Prevents the current user from being matched with themselves.
     *
     * @param learnerId The ID of the user requesting the match.
     * @param skillId The ID of the skill they want to learn.
     * @return A list of User objects who can teach the skill.
     */
    public List<User> findTeachersForSkill(int learnerId, int skillId) {
        List<User> matchingTeachers = new ArrayList<>();

        // Fetch all UserSkill records where the skill matches and the type is 'TEACHING'
        // (Ensure your UserSkillDAO has a method similar to this)
        List<UserSkill> teachingSkills = userSkillDAO.findUsersBySkill(skillId, "TEACHING");

        for (UserSkill us : teachingSkills) {
            // Ensure users don't get matched with themselves
            if (us.getUserId() != learnerId) {
                User teacher = userDAO.getUserById(us.getUserId());

                // Only add the teacher if their account is currently active
                if (teacher != null && teacher.isActive()) {
                    matchingTeachers.add(teacher);
                }
            }
        }

        if (matchingTeachers.isEmpty()) {
            System.out.println("ℹ️ No active teachers found for this skill at the moment.");
        } else {
            System.out.println("✅ Found " + matchingTeachers.size() + " teacher(s) ready to help!");
        }

        return matchingTeachers;
    }
}