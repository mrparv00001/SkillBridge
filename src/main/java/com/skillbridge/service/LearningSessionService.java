package com.skillbridge.service;

import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.model.LearningSession;
import com.skillbridge.util.Validator;

import java.util.List;

public class LearningSessionService {

    private LearningSessionDAO sessionDAO;

    public LearningSessionService() {
        this.sessionDAO = new LearningSessionDAO();
    }

    public boolean scheduleSession(LearningSession session) {

        if (!Validator.isFutureDate(session.getSessionDate())) {
            System.out.println("❌ Schedule failed: You can only schedule sessions for future dates.");
            return false;
        }

        if (!Validator.isValidTimeRange(session.getStartTime(), session.getEndTime())) {
            System.out.println("❌ Schedule failed: The end time must be after the start time.");
            return false;
        }

        if (Validator.isEmptyText(session.getMode())) {
            System.out.println("❌ Schedule failed: Please specify if it is Online or Offline.");
            return false;
        }

        // MILLIMETER UPGRADE: Prevent Double Booking (Time Clash Check)
        List<LearningSession> teacherExistingSessions = sessionDAO.getSessionsByUser(session.getTeacherId());
        for (LearningSession existing : teacherExistingSessions) {
            // Check if it's on the same day and status is Scheduled
            if (existing.getStatus().equalsIgnoreCase("Scheduled") &&
                    existing.getSessionDate().equals(session.getSessionDate())) {

                // Check if the new time overlaps with the existing time
                boolean isOverlapping =
                        (session.getStartTime().isBefore(existing.getEndTime()) &&
                                session.getEndTime().isAfter(existing.getStartTime()));

                if (isOverlapping) {
                    System.out.println("❌ Schedule failed: The Teacher is already booked for a session during this time!");
                    return false;
                }
            }
        }

        return sessionDAO.scheduleSession(session);
    }

    public List<LearningSession> getUserSessionHistory(int userId) {
        return sessionDAO.getSessionsByUser(userId);
    }

    public boolean completeSession(int sessionId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("Error: Session ID not found.");
            return false;
        }
        if (session.getStatus().equalsIgnoreCase("Completed")) {
            System.out.println("Error: This session is already completed!");
            return false;
        }
        if (session.getStatus().equalsIgnoreCase("Cancelled")) {
            System.out.println("Error: Cannot complete a cancelled session.");
            return false;
        }

        return sessionDAO.markSessionCompleted(sessionId);
    }

    public boolean cancelSession(int sessionId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("Error: Session ID not found.");
            return false;
        }
        if (!session.getStatus().equalsIgnoreCase("Scheduled")) {
            System.out.println("Error: You can only cancel sessions that are 'Scheduled'.");
            return false;
        }

        return sessionDAO.updateSessionStatus(sessionId, "Cancelled");
    }
}