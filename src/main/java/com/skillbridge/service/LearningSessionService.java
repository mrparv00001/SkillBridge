package com.skillbridge.service;

import com.skillbridge.dao.ExchangeRequestDAO;
import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.dao.UserDAO;
import com.skillbridge.dao.UserSkillDAO;
import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.model.LearningSession;
import com.skillbridge.model.User;
import com.skillbridge.model.UserSkill;
import com.skillbridge.util.Validator;

import java.util.LinkedList;
import java.util.List;

public class LearningSessionService {

    private LearningSessionDAO sessionDAO;
    private ExchangeRequestDAO requestDAO;
    private CreditService creditService;
    private UserDAO userDAO;
    private UserSkillDAO userSkillDAO;

    public LearningSessionService() {
        this.sessionDAO = new LearningSessionDAO();
        this.requestDAO = new ExchangeRequestDAO();
        this.creditService = new CreditService();
        this.userDAO = new UserDAO();
        this.userSkillDAO = new UserSkillDAO();
    }

    public boolean scheduleSession(LearningSession session) {
        if (!Validator.isFutureDate(session.getSessionDate())) {
            System.out.println("Schedule failed: Date must be today or in the future.");
            return false;
        }

        if (!Validator.isValidTimeRange(session.getStartTime(), session.getEndTime())) {
            System.out.println("Schedule failed: End time must be after start time.");
            return false;
        }

        if (Validator.isEmptyText(session.getMode())) {
            System.out.println("Schedule failed: Please specify Online or Offline.");
            return false;
        }

        // LinkedList Data Structure - Time clash check for teacher
        LinkedList<LearningSession> teacherSessions = new LinkedList<>(sessionDAO.getSessionsByUser(session.getTeacherId()));
        for (LearningSession existing : teacherSessions) {
            if (existing.getStatus().equalsIgnoreCase("Scheduled") &&
                    existing.getSessionDate().equals(session.getSessionDate())) {
                boolean overlap = session.getStartTime().isBefore(existing.getEndTime()) &&
                        session.getEndTime().isAfter(existing.getStartTime());
                if (overlap) {
                    System.out.println("Teacher is already booked during this time!");
                    return false;
                }
            }
        }

        // LinkedList Data Structure - Time clash check for learner
        LinkedList<LearningSession> learnerSessions = new LinkedList<>(sessionDAO.getSessionsByUser(session.getLearnerId()));
        for (LearningSession existing : learnerSessions) {
            if (existing.getStatus().equalsIgnoreCase("Scheduled") &&
                    existing.getSessionDate().equals(session.getSessionDate())) {
                boolean overlap = session.getStartTime().isBefore(existing.getEndTime()) &&
                        session.getEndTime().isAfter(existing.getStartTime());
                if (overlap) {
                    System.out.println("Learner is already booked during this time!");
                    return false;
                }
            }
        }

        // Credit System - Process transaction
        ExchangeRequest request = requestDAO.getRequestById(session.getRequestId());
        boolean isSwap = request != null && "Swap".equalsIgnoreCase(request.getExchangeType());

        boolean transactionSuccess = creditService.processTransaction(
                session.getTeacherId(),
                session.getLearnerId(),
                session.getSkillId(),
                isSwap
        );

        if (!transactionSuccess) {
            return false;
        }

        boolean success = sessionDAO.scheduleSession(session);

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n===========================================");
            System.out.println("|      SESSION SCHEDULED SUCCESSFULLY!    |");
            System.out.println("===========================================");
            System.out.println("Date         : " + session.getSessionDate());
            System.out.println("Time         : " + session.getStartTime() + " to " + session.getEndTime());
            System.out.println("Mode         : " + session.getMode());
            System.out.println("Notification sent to both users.");
            System.out.println("===========================================\n");
        }

        return success;
    }

    public List<LearningSession> getUserSessionHistory(int userId) {
        return new LinkedList<>(sessionDAO.getSessionsByUser(userId));
    }

    public boolean completeSession(int sessionId, int currentUserId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("Session not found.");
            return false;
        }

        if (session.getTeacherId() != currentUserId && session.getLearnerId() != currentUserId) {
            System.out.println("Unauthorized: Only session participants can mark it complete.");
            return false;
        }

        if (session.getStatus().equalsIgnoreCase("Completed")) {
            System.out.println("Session is already completed!");
            return false;
        }

        if (session.getStatus().equalsIgnoreCase("Cancelled")) {
            System.out.println("Cannot complete a cancelled session.");
            return false;
        }

        boolean success = sessionDAO.markSessionCompleted(sessionId);

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n===========================================");
            System.out.println("|      SESSION COMPLETED SUCCESSFULLY!    |");
            System.out.println("===========================================");
            System.out.println("Session ID  : " + sessionId);
            System.out.println("Please submit your feedback for teacher.");
            System.out.println("===========================================\n");
        }

        return success;
    }

    public boolean cancelSession(int sessionId, int currentUserId) {
        LearningSession session = sessionDAO.getSessionById(sessionId);

        if (session == null) {
            System.out.println("Session not found.");
            return false;
        }

        if (session.getTeacherId() != currentUserId && session.getLearnerId() != currentUserId) {
            System.out.println("Unauthorized: Only session participants can cancel it.");
            return false;
        }

        if (!session.getStatus().equalsIgnoreCase("Scheduled")) {
            System.out.println("Only Scheduled sessions can be cancelled.");
            return false;
        }

        // CANCELLATION POLICY - 2 HOUR RULE
        java.time.LocalDateTime sessionDateTime = java.time.LocalDateTime.of(
                session.getSessionDate(),
                session.getStartTime()
        );
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        long hoursUntilSession = java.time.Duration.between(now, sessionDateTime).toHours();

        if (hoursUntilSession < 0) {
            System.out.println("Cannot cancel: This session has already started or completed.");
            return false;
        }

        ExchangeRequest request = requestDAO.getRequestById(session.getRequestId());
        boolean wasCharged = request != null && "Direct".equalsIgnoreCase(request.getExchangeType());
        boolean eligibleForRefund = hoursUntilSession >= 2;

        // Show cancellation policy
        System.out.println("\n===========================================");
        System.out.println("|    CANCELLATION POLICY NOTICE            |");
        System.out.println("===========================================");
        System.out.println("Time until session: " + hoursUntilSession + " hours");

        if (wasCharged) {
            String tLevel = getTeacherSkillLevel(session.getTeacherId(), session.getSkillId());
            int totalCost = getCostByLevel(tLevel);

            if (eligibleForRefund) {
                System.out.println("Refund: " + totalCost + " credits (Full Refund - 2+ hours before)");
            } else {
                System.out.println("Refund: 0 credits (No refund - within 2 hours)");
                System.out.println("Reason: Teacher's time is valuable and cannot be recovered.");
            }
        }
        System.out.println("===========================================");

        // Confirm cancellation
        System.out.print("\nDo you still want to cancel? (yes/no): ");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes") && !confirm.equalsIgnoreCase("y")) {
            System.out.println("Cancellation aborted.");
            return false;
        }

        // Update database (Trigger will auto-fire!)
        boolean success = sessionDAO.updateSessionStatus(sessionId, "Cancelled");

        // Apply refund only if eligible
        if (success && wasCharged && eligibleForRefund) {
            String tLevel = getTeacherSkillLevel(session.getTeacherId(), session.getSkillId());
            int refundAmount = getCostByLevel(tLevel);

            User teacher = userDAO.getUserById(session.getTeacherId());
            User learner = userDAO.getUserById(session.getLearnerId());

            if (teacher != null && learner != null) {
                userDAO.updateUserCredits(learner.getUserId(), learner.getCredits() + refundAmount);
                userDAO.updateUserCredits(teacher.getUserId(), teacher.getCredits() - refundAmount);
                System.out.println("Full refund processed: " + refundAmount + " credits returned to learner.");
            }
        } else if (success && wasCharged && !eligibleForRefund) {
            System.out.println("No refund issued. Teacher keeps credits as compensation.");
        }

        // Success message (Trigger handles notification automatically!)
        if (success) {
            System.out.println("\n===========================================");
            System.out.println("|      SESSION CANCELLED SUCCESSFULLY!    |");
            System.out.println("===========================================");
            System.out.println("Session ID  : " + sessionId);
            System.out.println("Both users notified via database trigger.");
            System.out.println("===========================================\n");
        }

        return success;
    }

    // Helper method to get credit cost based on skill level
    private int getCostByLevel(String skillLevel) {
        if ("Advanced".equalsIgnoreCase(skillLevel)) return 10;
        if ("Intermediate".equalsIgnoreCase(skillLevel)) return 7;
        return 5;
    }

    // Helper method to get teacher's skill level for a specific skill
    private String getTeacherSkillLevel(int teacherId, int skillId) {
        List<UserSkill> skills = userSkillDAO.getSkillsByUserId(teacherId);
        for (UserSkill s : skills) {
            if (s.getSkillId() == skillId && s.getSkillType().equalsIgnoreCase("Teaching")) {
                return s.getSkillLevel();
            }
        }
        return "Beginner";
    }
}