package com.skillbridge.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LearningSession {

    private int sessionId;
    private int requestId;
    private int teacherId;
    private int learnerId;
    private int skillId;
    private LocalDate sessionDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String mode;
    private String meetingLink;
    private String location;
    private String status;
    private LocalDateTime completedAt;

    // 1. No-argument constructor
    public LearningSession() {
    }

    // 2. Constructor for Scheduling a new session (excluding auto-generated/default fields)
    public LearningSession(int requestId, int teacherId, int learnerId, int skillId,
                           LocalDate sessionDate, LocalTime startTime, LocalTime endTime,
                           String mode, String meetingLink, String location) {
        this.requestId = requestId;
        this.teacherId = teacherId;
        this.learnerId = learnerId;
        this.skillId = skillId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        // Status defaults to 'Scheduled' in DB/Service, completedAt is null initially
    }

    // 3. All-arguments constructor (used when fetching from Database)
    public LearningSession(int sessionId, int requestId, int teacherId, int learnerId,
                           int skillId, LocalDate sessionDate, LocalTime startTime,
                           LocalTime endTime, String mode, String meetingLink,
                           String location, String status, LocalDateTime completedAt) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.teacherId = teacherId;
        this.learnerId = learnerId;
        this.skillId = skillId;
        this.sessionDate = sessionDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mode = mode;
        this.meetingLink = meetingLink;
        this.location = location;
        this.status = status;
        this.completedAt = completedAt;
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public int getLearnerId() { return learnerId; }
    public void setLearnerId(int learnerId) { this.learnerId = learnerId; }

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getMeetingLink() { return meetingLink; }
    public void setMeetingLink(String meetingLink) { this.meetingLink = meetingLink; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    @Override
    public String toString() {
        return "LearningSession [" +
                "Session ID=" + sessionId +
                ", Date=" + sessionDate +
                ", Time=" + startTime + " to " + endTime +
                ", Mode='" + mode + '\'' +
                ", Status='" + status + '\'' +
                ']';
    }
}