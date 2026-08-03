package com.skillbridge.menu;

import com.skillbridge.model.LearningSession;
import com.skillbridge.service.LearningSessionService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

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

        System.out.print("Enter Exchange Request ID: ");
        int requestId = Validator.safeParseInt(scanner.nextLine());
        if (requestId == -1) { System.out.println("❌ Invalid Request ID."); return; }

        System.out.print("Enter Partner's User ID: ");
        int partnerId = Validator.safeParseInt(scanner.nextLine());
        if (partnerId == -1) { System.out.println("❌ Invalid Partner ID."); return; }

        System.out.print("Enter Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("❌ Invalid Skill ID."); return; }

        System.out.print("Enter Date (YYYY-MM-DD): ");
        LocalDate date = Validator.safeParseDate(scanner.nextLine());
        if (date == null) { System.out.println("❌ Invalid date. Use YYYY-MM-DD format."); return; }

        System.out.print("Enter Start Time (HH:MM): ");
        LocalTime startTime = Validator.safeParseTime(scanner.nextLine());
        if (startTime == null) { System.out.println("❌ Invalid time. Use HH:MM 24-hour format."); return; }

        System.out.print("Enter End Time (HH:MM): ");
        LocalTime endTime = Validator.safeParseTime(scanner.nextLine());
        if (endTime == null) { System.out.println("❌ Invalid time. Use HH:MM 24-hour format."); return; }

        System.out.print("Mode (Online/Offline): ");
        String mode = scanner.nextLine();

        System.out.print("Meeting Link (blank if Offline): ");
        String link = scanner.nextLine();

        System.out.print("Location (blank if Online): ");
        String location = scanner.nextLine();

        int myId = SessionManager.getCurrentUser().getUserId();
        LearningSession session = new LearningSession(requestId, myId, partnerId, skillId, date, startTime, endTime, mode, link, location);

        if (sessionService.scheduleSession(session)) {
            System.out.println("✅ Session scheduled successfully!");
        }
    }

    private void handleViewSessionHistory() {
        System.out.println("\n--- MY SESSION HISTORY ---");
        int myId = SessionManager.getCurrentUser().getUserId();
        List<LearningSession> sessions = sessionService.getUserSessionHistory(myId);

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("ℹ️ No sessions yet.");
        } else {
            for (LearningSession session : sessions) {
                System.out.println(session.toString());
            }
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