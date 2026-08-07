package com.skillbridge.menu;

import com.skillbridge.model.LearningSession;
import com.skillbridge.service.LearningSessionService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;
import com.skillbridge.model.ExchangeRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class SessionMenu {

    private Scanner scanner;
    private LearningSessionService sessionService;

    public SessionMenu() {
        this.scanner = new Scanner(System.in);
        this.sessionService = new LearningSessionService();
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   LEARNING SESSIONS");
            System.out.println("=================================");
            System.out.println("1. Schedule New Session");
            System.out.println("2. View Session History");
            System.out.println("3. Mark Session as Completed");
            System.out.println("4. Cancel a Session");
            System.out.println("5. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleScheduleSession(); break;
                case "2": handleViewSessionHistory(); break;
                case "3": handleCompleteSession(); break;
                case "4": handleCancelSession(); break;
                case "5": running = false; break;
                default: System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void handleScheduleSession() {
        System.out.println("\n--- SCHEDULE SESSION ---");
        int myId = SessionManager.getCurrentUser().getUserId();

        // Show accepted requests that can be scheduled
        System.out.println("\n--- Your Accepted Requests ---");
        com.skillbridge.dao.ExchangeRequestDAO reqDAO = new com.skillbridge.dao.ExchangeRequestDAO();
        List<com.skillbridge.model.ExchangeRequest> allRequests = reqDAO.getRequestsByUserId(myId);

        boolean hasAccepted = false;
        for (com.skillbridge.model.ExchangeRequest req : allRequests) {
            if ("Accepted".equalsIgnoreCase(req.getStatus())) {
                if (!hasAccepted) {
                    System.out.println("Req ID | Partner | Skill ID | Type");
                    System.out.println("-------|---------|----------|-----");
                    hasAccepted = true;
                }
                int partnerId = (req.getSenderId() == myId) ? req.getReceiverId() : req.getSenderId();
                com.skillbridge.dao.UserDAO userDAO = new com.skillbridge.dao.UserDAO();
                com.skillbridge.model.User partner = userDAO.getUserById(partnerId);
                String partnerName = partner != null ? partner.getFullName() : "Unknown";
                System.out.println(req.getRequestId() + "      | " + partnerName + " (ID:" + partnerId + ") | " + req.getRequestedSkillId() + "        | " + req.getExchangeType());
            }
        }

        if (!hasAccepted) {
            System.out.println("No accepted requests found. Get a request accepted first!");
            return;
        }

        System.out.print("\nSelect Request ID from above: ");
        int requestId = Validator.safeParseInt(scanner.nextLine());
        if (requestId == -1) { System.out.println("Invalid Request ID."); return; }

        // Auto-fill partner and skill from request
        com.skillbridge.model.ExchangeRequest selectedReq = reqDAO.getRequestById(requestId);
        if (selectedReq == null || !"Accepted".equalsIgnoreCase(selectedReq.getStatus())) {
            System.out.println("Invalid or not accepted request.");
            return;
        }

        int partnerId = (selectedReq.getSenderId() == myId) ? selectedReq.getReceiverId() : selectedReq.getSenderId();
        int skillId = selectedReq.getRequestedSkillId();

        com.skillbridge.dao.UserDAO userDAO = new com.skillbridge.dao.UserDAO();
        com.skillbridge.model.User partner = userDAO.getUserById(partnerId);
        System.out.println("\nPartner: " + (partner != null ? partner.getFullName() : "Unknown"));
        System.out.println("Skill ID: " + skillId);

        System.out.print("Enter Date (YYYY-MM-DD): ");
        LocalDate date = Validator.safeParseDate(scanner.nextLine());
        if (date == null) { System.out.println("Invalid date."); return; }

        System.out.print("Enter Start Time (HH:MM): ");
        LocalTime startTime = Validator.safeParseTime(scanner.nextLine());
        if (startTime == null) { System.out.println("Invalid time."); return; }

        System.out.print("Enter End Time (HH:MM): ");
        LocalTime endTime = Validator.safeParseTime(scanner.nextLine());
        if (endTime == null) { System.out.println("Invalid time."); return; }

        System.out.println("\nSelect Mode:");
        System.out.println("  1. Online");
        System.out.println("  2. Offline");
        System.out.print("Choose (1 or 2): ");
        int modeChoice = Validator.safeParseInt(scanner.nextLine());

        String mode;
        if (modeChoice == 1) mode = "Online";
        else if (modeChoice == 2) mode = "Offline";
        else { System.out.println("Invalid choice."); return; }

        String link = "";
        String location = "";
        if (modeChoice == 1) {
            System.out.print("Enter Meeting Link: ");
            link = scanner.nextLine();
        } else {
            System.out.print("Enter Location: ");
            location = scanner.nextLine();
        }

        LearningSession session = new LearningSession(requestId, myId, partnerId, skillId, date, startTime, endTime, mode, link, location);

        if (sessionService.scheduleSession(session)) {
            System.out.println("Session scheduled successfully!");
        }
    }

    private void handleViewSessionHistory() {
        System.out.println("\n--- MY SESSION HISTORY ---");
        int myId = SessionManager.getCurrentUser().getUserId();
        List<LearningSession> sessions = sessionService.getUserSessionHistory(myId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("No sessions yet.");
        } else {
            System.out.printf("%-6s %-12s %-8s %-8s %-10s %-12s%n",
                    "SesID", "Date", "Start", "End", "Mode", "Status");
            System.out.println("--------------------------------------------------------------");
            for (LearningSession session : sessions) {
                System.out.printf("%-6d %-12s %-8s %-8s %-10s %-12s%n",
                        session.getSessionId(),
                        session.getSessionDate(),
                        session.getStartTime(),
                        session.getEndTime(),
                        session.getMode(),
                        session.getStatus());
            }
            System.out.println("--------------------------------------------------------------");
        }
    }

    private void handleCompleteSession() {
        System.out.println("\n--- COMPLETE SESSION ---");
        System.out.print("Enter Session ID: ");
        int sessionId = Validator.safeParseInt(scanner.nextLine());
        if (sessionId == -1) { System.out.println("❌ Invalid Session ID."); return; }

        int currentUserId = SessionManager.getCurrentUser().getUserId();
        if (sessionService.completeSession(sessionId, currentUserId)) {
            System.out.println("✅ Session marked completed!");
        }
    }

    private void handleCancelSession() {
        System.out.println("\n--- CANCEL SESSION ---");
        System.out.print("Enter Session ID: ");
        int sessionId = Validator.safeParseInt(scanner.nextLine());
        if (sessionId == -1) { System.out.println("❌ Invalid Session ID."); return; }

        int currentUserId = SessionManager.getCurrentUser().getUserId();
        if (sessionService.cancelSession(sessionId, currentUserId)) {
            System.out.println("✅ Session cancelled!");
        }
    }
}