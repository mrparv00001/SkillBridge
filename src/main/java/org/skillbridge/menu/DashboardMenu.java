package com.skillbridge.menu;

import com.skillbridge.model.LearningSession;
import com.skillbridge.model.User;
import com.skillbridge.service.AuthenticationService;
import com.skillbridge.service.LearningSessionService;
import com.skillbridge.service.UserService;
import com.skillbridge.util.SessionManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class DashboardMenu {

    private Scanner scanner;
    private UserService userService;
    private LearningSessionService sessionService;
    private AuthenticationService authService;

    public DashboardMenu() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.sessionService = new LearningSessionService();
        this.authService = new AuthenticationService();
    }

    // 1. The Main Dashboard Loop
    public void showDashboard() {
        boolean loggedIn = true;

        while (loggedIn && SessionManager.isLoggedIn()) {
            User currentUser = SessionManager.getCurrentUser();
            System.out.println("\n=================================");
            System.out.println("   DASHBOARD - " + currentUser.getFullName().toUpperCase());
            System.out.println("=================================");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Schedule Learning Session");
            System.out.println("4. View Session History");
            System.out.println("5. Mark Session as Completed");
            System.out.println("6. Cancel a Session");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewProfile();
                    break;
                case "2":
                    updateProfile();
                    break;
                case "3":
                    scheduleSession();
                    break;
                case "4":
                    viewSessionHistory();
                    break;
                case "5":
                    completeSession();
                    break;
                case "6":
                    cancelSession();
                    break;
                case "7":
                    authService.logout();
                    loggedIn = false; // Breaks the loop and sends them back to Main Menu
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // 2. View Profile
    private void viewProfile() {
        System.out.println("\n--- MY PROFILE ---");
        // Get the latest profile from the database just in case it changed
        User user = userService.getUserProfile(SessionManager.getCurrentUser().getUserId());
        System.out.println(user.toString());
    }

    // 3. Update Profile
    private void updateProfile() {
        System.out.println("\n--- UPDATE PROFILE ---");
        User currentUser = SessionManager.getCurrentUser();

        System.out.print("Enter New Full Name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) currentUser.setFullName(name);

        System.out.print("Enter New Department: ");
        String dept = scanner.nextLine();
        if (!dept.trim().isEmpty()) currentUser.setDepartment(dept);

        System.out.print("Enter New Semester: ");
        String semInput = scanner.nextLine();
        if (!semInput.trim().isEmpty()) currentUser.setSemester(Integer.parseInt(semInput));

        System.out.print("Enter New Phone (10 digits): ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) currentUser.setPhone(phone);

        System.out.print("Enter New Bio: ");
        String bio = scanner.nextLine();
        if (!bio.trim().isEmpty()) currentUser.setBio(bio);

        userService.updateUserProfile(currentUser);
    }

    // 4. Schedule a Session
    private void scheduleSession() {
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

            System.out.print("Meeting Link or Location: ");
            String location = scanner.nextLine();

            // Assuming the current user is the teacher for this example
            int myId = SessionManager.getCurrentUser().getUserId();

            LearningSession session = new LearningSession(requestId, myId, partnerId, skillId, date, startTime, endTime, mode, location, location);

            if (sessionService.scheduleSession(session)) {
                System.out.println("Session scheduled successfully!");
            }
        } catch (Exception e) {
            System.out.println("Invalid input format. Please try again carefully.");
        }
    }

    // 5. View Session History
    private void viewSessionHistory() {
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

    // 6. Complete a Session
    private void completeSession() {
        System.out.println("\n--- COMPLETE SESSION ---");
        System.out.print("Enter Session ID to mark as completed: ");
        try {
            int sessionId = Integer.parseInt(scanner.nextLine());
            if (sessionService.completeSession(sessionId)) {
                System.out.println("Session successfully marked as completed!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Session ID. Must be a number.");
        }
    }

    // 7. Cancel a Session
    private void cancelSession() {
        System.out.println("\n--- CANCEL SESSION ---");
        System.out.print("Enter Session ID to cancel: ");
        try {
            int sessionId = Integer.parseInt(scanner.nextLine());
            if (sessionService.cancelSession(sessionId)) {
                System.out.println("Session successfully cancelled.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Session ID. Must be a number.");
        }
    }
}