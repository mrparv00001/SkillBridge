package com.skillbridge.service;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.model.Skill;

import java.util.List;

public class SkillService {

    private final SkillDAO skillDAO;

    // Constructor initializes the DAO
    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    /**
     * Retrieves the entire catalog of skills available on the platform.
     */
    public List<Skill> getAllSkills() {
        List<Skill> skills = skillDAO.getAllSkills();

        if (skills.isEmpty()) {
            System.out.println("ℹ️ The skill catalog is currently empty.");
        }

        return skills;
    }

    /**
     * Adds a new skill to the platform after validating the input.
     */
    public boolean addNewSkill(Skill newSkill) {
        // 1. Validate essential input fields
        if (newSkill.getSkillName() == null || newSkill.getSkillName().trim().isEmpty()) {
            System.err.println("❌ Validation Error: Skill name cannot be empty.");
            return false;
        }

        if (newSkill.getCategory() == null || newSkill.getCategory().trim().isEmpty()) {
            System.err.println("❌ Validation Error: Skill category cannot be empty.");
            return false;
        }

        // 2. Prevent exact duplicates from being added to the catalog
        List<Skill> existingSkills = skillDAO.getAllSkills();
        for (Skill existing : existingSkills) {
            if (existing.getSkillName().equalsIgnoreCase(newSkill.getSkillName())) {
                System.err.println("❌ Validation Error: The skill '" + newSkill.getSkillName() + "' already exists in the catalog.");
                return false;
            }
        }

        // 3. Save to database via DAO
        boolean isAdded = skillDAO.addSkill(newSkill);
        if (isAdded) {
            System.out.println("✅ Successfully added '" + newSkill.getSkillName() + "' to the catalog!");
        } else {
            System.err.println("❌ Failed to add skill due to a database error.");
        }

        return isAdded;
    }
}