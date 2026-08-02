package com.skillbridge.service;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.model.Skill;

import java.util.List;

public class SkillService {

    private final SkillDAO skillDAO;

    public SkillService() {
        this.skillDAO = new SkillDAO();
    }

    public List<Skill> getAllSkills() {
        List<Skill> skills = skillDAO.getAllSkills();
        if (skills == null || skills.isEmpty()) {
            System.out.println("ℹ️ The skill catalog is currently empty.");
        }
        return skills;
    }

    public boolean addNewSkill(Skill newSkill) {
        if (newSkill.getSkillName() == null || newSkill.getSkillName().trim().isEmpty()) {
            System.err.println("❌ Skill name cannot be empty.");
            return false;
        }

        if (newSkill.getCategory() == null || newSkill.getCategory().trim().isEmpty()) {
            System.err.println("❌ Category cannot be empty.");
            return false;
        }

        if (newSkill.getDescription() == null || newSkill.getDescription().trim().isEmpty()) {
            System.err.println("❌ Description cannot be empty.");
            return false;
        }

        // FIX: Normalize skill name (Title Case) to prevent duplicates
        String normalizedName = newSkill.getSkillName().trim();
        newSkill.setSkillName(normalizedName);

        // FIX: Case-insensitive duplicate check
        List<Skill> existingSkills = skillDAO.getAllSkills();
        for (Skill existing : existingSkills) {
            if (existing.getSkillName().equalsIgnoreCase(normalizedName)) {
                System.err.println("❌ Skill '" + normalizedName + "' already exists.");
                return false;
            }
        }

        boolean isAdded = skillDAO.addSkill(newSkill);
        if (isAdded) {
            System.out.println("✅ Successfully added '" + normalizedName + "' to the catalog!");
        } else {
            System.err.println("❌ Failed to add skill.");
        }
        return isAdded;
    }
}