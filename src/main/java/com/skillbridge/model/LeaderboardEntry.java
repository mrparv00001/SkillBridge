package com.skillbridge.model;

public class LeaderboardEntry {
    private int rank;
    private int userId;
    private String name;
    private String department;
    private int semester;
    private int credits;
    private int teachingSessions;
    private int learningSessions;
    private int completedSessions;
    private double averageRating;
    private boolean isActive;

    public LeaderboardEntry() {}

    public LeaderboardEntry(int rank, int userId, String name, String department, int semester,
                            int credits, int teachingSessions, int learningSessions,
                            int completedSessions, double averageRating, boolean isActive) {
        this.rank = rank;
        this.userId = userId;
        this.name = name;
        this.department = department;
        this.semester = semester;
        this.credits = credits;
        this.teachingSessions = teachingSessions;
        this.learningSessions = learningSessions;
        this.completedSessions = completedSessions;
        this.averageRating = averageRating;
        this.isActive = isActive;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getTeachingSessions() { return teachingSessions; }
    public void setTeachingSessions(int teachingSessions) { this.teachingSessions = teachingSessions; }

    public int getLearningSessions() { return learningSessions; }
    public void setLearningSessions(int learningSessions) { this.learningSessions = learningSessions; }

    public int getCompletedSessions() { return completedSessions; }
    public void setCompletedSessions(int completedSessions) { this.completedSessions = completedSessions; }

    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String toFormattedString() {
        return String.format(
                "Rank: %d | %s | Dept: %s | Sem: %d | Credits: %d | Teaching: %d | Learning: %d | Completed: %d | Avg Rating: %.2f | Active: %s",
                rank, name, department, semester, credits, teachingSessions, learningSessions,
                completedSessions, averageRating, (isActive ? "Yes" : "No")
        );
    }
}