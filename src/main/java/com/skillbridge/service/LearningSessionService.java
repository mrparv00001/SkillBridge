package com.skillbridge.service;

import com.skillbridge.dao.ExchangeRequestDAO;
import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.LearningSession;
import com.skillbridge.util.Validator;

import java.util.List;

public class LearningSessionService {

    private LearningSessionDAO sessionDAO;
    private ExchangeRequestDAO requestDAO;
    private CreditService creditService; // NEW

    public LearningSessionService() {
        this.sessionDAO = new LearningSessionDAO();
        this.requestDAO = new ExchangeRequestDAO();
        this.creditService = new CreditService();
    }

    public boolean scheduleSession(LearningSession session) {
        if (!Validator.isFutureDate(session.getSessionDate())) {
            System.out.println("❌ Schedule failed: Date must be today or in the future.");
            return false;
        }

        if (!Validator.isValidTimeRange(session.getStartTime(), session.getEndTime())) {
            System.out.println("❌ Schedule failed: End time must be after start time.");
            return false;
        }

        if (Validator.isEmptyText(session.getMode())) {
            System.out.println("❌ Schedule failed: Please specify Online or Offline.");
            return false;
        }

        // Check teacher's time clash
        List<LearningSession> teacherSessions = sessionDAO.getSessionsByUser(session.getTeacherId());
        for (LearningSession existing : teacherSessions) {
            if (existing.getStatus().equalsIgnoreCase("Scheduled") &&
                    existing.getSessionDate().equals(session.getSessionDate())) {
                boolean overlap = session.getStartTime().isBefore(existing.getEndTime()) &&
                        session.getEndTime().isAfter(existing.getStartTime());
                if (overlap) {
                    System.out.println("❌ Teacher is already booked during this time!");
                    return false;
                }
            }
        }

        // Check learner's time clash
        List<LearningSession> learnerSessions = sessionDAO.getSessionsByUser(session.getLearnerId());
        for (LearningSession existing : learnerSessions) {
            if (existing.getStatus().equalsIgnoreCase("Scheduled") &&
                    existing.getSessionDate().equals(session.getSessionDate())) {
                boolean overlap = session.getStartTime().isBefore(existing.getEndTime()) &&
                        session.getEndTime().isAfter(existing.getStartTime());
                if (overlap) {
                    System.out.println("❌ Learner is already booked during this time!");
                    return false;
                }
            }
        }

        // --- NEW: CREDIT SYSTEM TRANSACTION GATEWAY ---
        ExchangeRequest request = requestDAO.getRequestById(session.getRequestId());
        boolean isSwap = request != null && "Swap".equalsIgnoreCase(request.getExchangeType());

        System.out.println("\n--- Processing Transaction ---");
        boolean transactionSuccess = creditService.processTransaction(
                session.getTeacherId(),
                session.getLearnerId(),
                session.getSkillId(),
                isSwap
        );

        // If they don't have enough credits, completely block the scheduling
        if (!transactionSuccess) {
            return false;
        }
        System.out.println("------------------------------\n");

        return sessionDAO.scheduleSession(session);
    }

    public List<LearningSession> getUserSessionHistory(int userId) {
        return sessionDAO.getSessionsByUser(userId);
    }

    public boolean completeSession(int sessionId, int currentUserId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("❌ Session not found.");
            return false;
        }

        if (session.getTeacherId() != currentUserId && session.getLearnerId() != currentUserId) {
            System.out.println("❌ Unauthorized: Only session participants can mark it complete.");
            return false;
        }

        if (session.getStatus().equalsIgnoreCase("Completed")) {
            System.out.println("❌ Session is already completed!");
            return false;
        }

        if (session.getStatus().equalsIgnoreCase("Cancelled")) {
            System.out.println("❌ Cannot complete a cancelled session.");
            return false;
        }

        return sessionDAO.markSessionCompleted(sessionId);
    }

    public boolean cancelSession(int sessionId, int currentUserId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("❌ Session not found.");
            return false;
        }

        if (session.getTeacherId() != currentUserId && session.getLearnerId() != currentUserId) {
            System.out.println("❌ Unauthorized: Only session participants can cancel it.");
            return false;
        }

        if (!session.getStatus().equalsIgnoreCase("Scheduled")) {
            System.out.println("❌ Only Scheduled sessions can be cancelled.");
            return false;
        }

        // NOTE: In a robust economy, you may want to refund credits here!
        return sessionDAO.updateSessionStatus(sessionId, "Cancelled");
    }
}