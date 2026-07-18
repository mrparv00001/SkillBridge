package com.skillbridge.service;

import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.model.LearningSession;
import com.skillbridge.util.Validator;

import java.util.List;

public class LearningSessionService {

    // The Manager needs the Worker to talk to the database
    private LearningSessionDAO sessionDAO;

    // Constructor: Give the Manager its Worker when it is created
    public LearningSessionService() {
        this.sessionDAO = new LearningSessionDAO();
    }

    // 1. Schedule a new class safely
    public boolean scheduleSession(LearningSession session) {

        // Rule 1: The date must be in the future
        if (!Validator.isFutureDate(session.getSessionDate())) {
            System.out.println("Schedule failed: You can only schedule sessions for future dates.");
            return false;
        }

        // Rule 2: Start time must be before end time
        if (!Validator.isValidTimeRange(session.getStartTime(), session.getEndTime())) {
            System.out.println("Schedule failed: The end time must be after the start time.");
            return false;
        }

        // Rule 3: Must provide a meeting link or location
        if (Validator.isEmptyText(session.getMode())) {
            System.out.println("Schedule failed: Please specify if it is Online or Offline.");
            return false;
        }

        // All rules passed! Tell the DAO to save it to the database.
        return sessionDAO.scheduleSession(session);
    }

    // 2. Get the history of a user's classes
    public List<LearningSession> getUserSessionHistory(int userId) {
        // No special rules needed here, just ask the DAO to fetch the list
        return sessionDAO.getSessionsByUser(userId);
    }

    // 3. Mark a class as "Completed"
    public boolean completeSession(int sessionId) {
        // First, let's find the session to make sure it exists
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

        // If it's a valid Scheduled session, tell the DAO to mark it Completed
        return sessionDAO.markSessionCompleted(sessionId);
    }

    // 4. Cancel a scheduled session
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

        // Tell the DAO to update the status to 'Cancelled'
        return sessionDAO.updateSessionStatus(sessionId, "Cancelled");
    }
}