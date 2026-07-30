package com.skillbridge.service;

import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.model.LearningSession;

import java.util.ArrayList;
import java.util.List;

public class SessionService {

    private final LearningSessionDAO sessionDAO;

    public SessionService() {
        this.sessionDAO = new LearningSessionDAO();
    }

    /**
     * Schedules a new learning session for an accepted exchange request.
     */
    public boolean scheduleSession(LearningSession session) {
        // 1. Basic Validation
        if (session.getRequestId() <= 0 || session.getTeacherId() <= 0 || session.getLearnerId() <= 0) {
            System.err.println("❌ Validation Error: Missing critical session IDs.");
            return false;
        }

        if (session.getSessionDate() == null || session.getStartTime() == null) {
            System.err.println("❌ Validation Error: Session date and start time are required.");
            return false;
        }

        // 2. Save to database using the DAO's exact method
        boolean isScheduled = sessionDAO.scheduleSession(session);

        if (isScheduled) {
            System.out.println("✅ Session successfully scheduled for " + session.getSessionDate() + "!");
        } else {
            System.err.println("❌ Failed to schedule the session.");
        }

        return isScheduled;
    }

    /**
     * Marks an existing session as completed.
     */
    public boolean completeSession(int sessionId) {
        if (sessionId <= 0) {
            System.err.println("❌ Validation Error: Invalid session ID.");
            return false;
        }

        // Uses the specific markSessionCompleted method from your DAO
        boolean isUpdated = sessionDAO.markSessionCompleted(sessionId);

        if (isUpdated) {
            System.out.println("✅ Session " + sessionId + " marked as Completed. Great job!");
        } else {
            System.err.println("❌ Failed to update session status.");
        }

        return isUpdated;
    }

    /**
     * Cancels an existing session.
     */
    public boolean cancelSession(int sessionId) {
        if (sessionId <= 0) {
            System.err.println("❌ Validation Error: Invalid session ID.");
            return false;
        }

        // Uses the general updateSessionStatus method to set status to 'Cancelled'
        boolean isCancelled = sessionDAO.updateSessionStatus(sessionId, "Cancelled");

        if (isCancelled) {
            System.out.println("✅ Session " + sessionId + " has been successfully cancelled.");
        } else {
            System.err.println("❌ Failed to cancel the session.");
        }

        return isCancelled;
    }

    /**
     * Retrieves all upcoming scheduled sessions for a specific user (whether teaching or learning).
     */
    public List<LearningSession> getUpcomingSessionsForUser(int userId) {
        // Uses the getSessionsByUser method from your DAO
        List<LearningSession> allSessions = sessionDAO.getSessionsByUser(userId);
        List<LearningSession> upcomingSessions = new ArrayList<>();

        // Business Logic: Filter down to just the "Scheduled" sessions
        for (LearningSession session : allSessions) {
            if ("Scheduled".equalsIgnoreCase(session.getStatus())) {
                upcomingSessions.add(session);
            }
        }

        if (upcomingSessions.isEmpty()) {
            System.out.println("ℹ️ You have no upcoming sessions scheduled.");
        } else {
            System.out.println("📅 You have " + upcomingSessions.size() + " upcoming session(s).");
        }

        return upcomingSessions;
    }
}