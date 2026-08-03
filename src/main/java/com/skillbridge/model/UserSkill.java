package com.skillbridge.model;

public class UserSkill
{
    // Defining the custom Enums
    public enum SkillType {
        TEACHING,
        LEARNING
    }

    public enum SkillLevel {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED
    }
    private int userSkillId;
    private int skillId;
    private int userId;
    private SkillType skillType;
    private SkillLevel skillLevel;
    private boolean isActive;

    public UserSkill(int userSkillId, int skillId, int userId, SkillType skillType, SkillLevel skillLevel, boolean isActive) {
        this.userSkillId = userSkillId;
        this.skillId = skillId;
        this.userId = userId;
        this.skillType = skillType;
        this.skillLevel = skillLevel;
        this.isActive = isActive;
    }

    public int getUserSkillId() {
        return userSkillId;
    }

    public void setUserSkillId(int userSkillId) {
        this.userSkillId = userSkillId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSkillType() {
        String s = skillType.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    public String getSkillLevel() {
        String s = skillLevel.name();
        return s.charAt(0) + s.substring(1).toLowerCase();
    }

    public void setSkillLevel(SkillLevel skillLevel) {
        this.skillLevel = skillLevel;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
