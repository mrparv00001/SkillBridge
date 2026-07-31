package com.skillbridge.menu;

import com.skillbridge.model.LearningSession;
import com.skillbridge.service.LearningSessionService;
import com.skillbridge.util.SessionManager;

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
                default: System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleScheduleSession() {
        System.out.println("\n--- SCHEDULE SESSION ---");
        try {
            System.out.print("Enter Exchange Request ID: ");
            int requestId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Partner's User ID: ");
            int partnerId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Skill ID being taught/learned: ");
            int skillId = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter Date (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());

            System.out.print("Enter Start Time (HH:MM) 24-hour format: ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine());

            System.out.print("Enter End Time (HH:MM) 24-hour format: ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine());

            System.out.print("Mode (Online/Offline): ");
            String mode = scanner.nextLine();

            // UPGRADED: Fixed the missing Meeting Link bug
            System.out.print("Meeting Link (leave blank if Offline): ");
            String link = scanner.nextLine();

            System.out.print("Location (leave blank if Online): ");
            String location = scanner.nextLine();

            int myId = SessionManager.getCurrentUser().getUserId();
            LearningSession session = new LearningSession(requestId, myId, partnerId, skillId, date, startTime, endTime, mode, link, location);

            if (sessionService.scheduleSession(session)) {
                System.out.println("Session scheduled successfully!");
            }
        } catch (Exception e) {
            // UPGRADED: Prevents the app from crashing if they type text instead of numbers
            System.out.println("Invalid input format. Please check dates (YYYY-MM-DD) and IDs.");
        }
    }

    private void handleViewSessionHistory() {
        System.out.println("\n--- MY SESSION HISTORY ---");
        int myId = SessionManager.getCurrentUser().getUserId();
        List<LearningSession> sessions = sessionService.getUserSessionHistory(myId);

        if (sessions.isEmpty()) {
            System.out.println("You have no learning sessions yet.");
        } else {
            for (LearningSession session : sessions) {
                System.out.println(session.toString());
            }
        }
    }

    private void handleCompleteSession() {
        System.out.println("\n--- COMPLETE SESSION ---");
        System.out.print("Enter Session ID to mark as completed: ");
        try {
            int sessionId = Integer.parseInt(scanner.nextLine());
            if (sessionService.completeSession(sessionId)) {
                System.out.println("Session successfully marked as completed!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Session ID.");
        }
    }

    private void handleCancelSession() {
        System.out.println("\n--- CANCEL SESSION ---");
        System.out.print("Enter Session ID to cancel: ");
        try {
            int sessionId = Integer.parseInt(scanner.nextLine());
            if (sessionService.cancelSession(sessionId)) {
                System.out.println("Session successfully cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Session ID.");
        }
    }
}