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
    private NotificationService notificationService;

    public LearningSessionService() {
        this.sessionDAO = new LearningSessionDAO();
        this.requestDAO = new ExchangeRequestDAO();
        this.creditService = new CreditService();
        this.userDAO = new UserDAO();
        this.userSkillDAO = new UserSkillDAO();
        this.notificationService = new NotificationService();
    }

    public boolean scheduleSession(LearningSession session) {
        if (!Validator.isFutureDate(session.getSessionDate())) {
            System.out.println("Schedule failed: Date must be today or in the future.");
            return false;
        }

        if (session.getSessionDate().equals(java.time.LocalDate.now())) {
            if (session.getStartTime().isBefore(java.time.LocalTime.now())) {
                System.out.println("Schedule failed: Start time has already passed today.");
                System.out.println("Current time: " + java.time.LocalTime.now().withSecond(0).withNano(0));
                System.out.println("Your start time: " + session.getStartTime());
                return false;
            }
        }

        if (!Validator.isValidTimeRange(session.getStartTime(), session.getEndTime())) {
            System.out.println("Schedule failed: End time must be after start time.");
            return false;
        }

        if (Validator.isEmptyText(session.getMode())) {
            System.out.println("Schedule failed: Please specify Online or Offline.");
            return false;
        }

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

        if (success) {
            // Real Notifications to both users
            notificationService.sendNotification(session.getTeacherId(),
                    "New session scheduled on " + session.getSessionDate() + " at " + session.getStartTime(),
                    "SESSION_SCHEDULED");
            notificationService.sendNotification(session.getLearnerId(),
                    "Your session confirmed on " + session.getSessionDate() + " at " + session.getStartTime(),
                    "SESSION_SCHEDULED");

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

        if (success) {
            // Real Notifications to both users
            notificationService.sendNotification(session.getTeacherId(),
                    "Session " + sessionId + " completed. Please submit feedback!",
                    "SESSION_COMPLETED");
            notificationService.sendNotification(session.getLearnerId(),
                    "Session " + sessionId + " completed. Please submit feedback!",
                    "SESSION_COMPLETED");

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

        System.out.println("\nDo you still want to cancel?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Choose (1 or 2): ");
        java.util.Scanner sc = new java.util.Scanner(System.in);
        int confirm = com.skillbridge.util.Validator.safeParseInt(sc.nextLine());

        if (confirm != 1) {
            System.out.println("Cancellation aborted.");
            return false;
        }

        boolean success = sessionDAO.updateSessionStatus(sessionId, "Cancelled");

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

        if (success) {
            // Real Notifications to both users
            notificationService.sendNotification(session.getTeacherId(),
                    "Session " + sessionId + " has been cancelled. Date: " + session.getSessionDate(),
                    "SESSION_CANCELLED");
            notificationService.sendNotification(session.getLearnerId(),
                    "Session " + sessionId + " has been cancelled. Date: " + session.getSessionDate(),
                    "SESSION_CANCELLED");

            System.out.println("\n===========================================");
            System.out.println("|      SESSION CANCELLED SUCCESSFULLY!    |");
            System.out.println("===========================================");
            System.out.println("Session ID  : " + sessionId);
            System.out.println("Both users notified.");
            System.out.println("===========================================\n");
        }

        return success;
    }

    private int getCostByLevel(String skillLevel) {
        if ("Advanced".equalsIgnoreCase(skillLevel)) return 10;
        if ("Intermediate".equalsIgnoreCase(skillLevel)) return 7;
        return 5;
    }

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