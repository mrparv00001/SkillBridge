package com.skillbridge.menu;

import com.skillbridge.model.*;
import com.skillbridge.service.LeaderboardService;
import com.skillbridge.service.ManagerService;

import java.util.List;
import java.util.Scanner;

public class ManagerMenu {

    private Scanner scanner;
    private ManagerService managerService;
    private LeaderboardService leaderboardService;

    public ManagerMenu() {
        this.scanner = new Scanner(System.in);
        this.managerService = new ManagerService();
        this.leaderboardService = new LeaderboardService();
    }

    public void start() {
        System.out.println("\n=================================");
        System.out.println("   MANAGER PORTAL LOGIN");
        System.out.println("=================================");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Manager manager = managerService.loginManager(username, password);

        if (manager == null) {
            System.out.println("\nManager login failed. Returning to main menu.\n");
            return;
        }

        showManagerDashboard(manager);
    }

    private void showManagerDashboard(Manager manager) {
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n=====================================");
            System.out.println("   MANAGER DASHBOARD - " + manager.getFullName().toUpperCase());
            System.out.println("=====================================");
            System.out.println("1. View All Students");
            System.out.println("2. View All Skills");
            System.out.println("3. View All Exchange Requests");
            System.out.println("4. View All Sessions");
            System.out.println("5. View All Feedback");
            System.out.println("6. View Leaderboard");
            System.out.println("7. View Cancellation History");
            System.out.println("8. Activate / Deactivate User");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewAllStudents(); break;
                case "2": viewAllSkills(); break;
                case "3": viewAllRequests(); break;
                case "4": viewAllSessions(); break;
                case "5": viewAllFeedback(); break;
                case "6": leaderboardService.displayLeaderboard(); break;
                case "7": viewCancellationHistory(); break;
                case "8": deactivateUser(); break;
                case "9": loggedIn = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllStudents() {
        System.out.println("\n--- ALL STUDENTS ---");
        List<User> users = managerService.getAllUsers();

        if (users == null || users.isEmpty()) {
            System.out.println("No students found.");
            return;
        }

        System.out.println("Total Students: " + users.size());
        System.out.printf("%-4s %-22s %-12s %-18s %-4s %-8s %-8s%n",
                "ID", "Name", "Enrollment", "Department", "Sem", "Credits", "Status");
        System.out.println("------------------------------------------------------------------------------------");

        for (User u : users) {
            System.out.printf("%-4d %-22s %-12s %-18s %-4d %-8d %-8s%n",
                    u.getUserId(),
                    u.getFullName(),
                    u.getEnrollmentNo(),
                    u.getDepartment(),
                    u.getSemester(),
                    u.getCredits(),
                    (u.isActive() ? "Active" : "Suspended"));
        }
        System.out.println("------------------------------------------------------------------------------------");
    }

    private void viewAllSkills() {
        System.out.println("\n--- ALL SKILLS ---");
        List<Skill> skills = managerService.getAllSkills();

        if (skills == null || skills.isEmpty()) {
            System.out.println("No skills found.");
            return;
        }

        System.out.println("Total Skills: " + skills.size());
        System.out.printf("%-4s %-22s %-15s %-30s%n",
                "ID", "Skill Name", "Category", "Description");
        System.out.println("------------------------------------------------------------------------");

        for (Skill s : skills) {
            System.out.printf("%-4d %-22s %-15s %-30s%n",
                    s.getSkillId(),
                    s.getSkillName(),
                    s.getCategory(),
                    s.getDescription());
        }
        System.out.println("------------------------------------------------------------------------");
    }

    private void viewAllRequests() {
        System.out.println("\n--- ALL EXCHANGE REQUESTS ---");
        List<ExchangeRequest> requests = managerService.getAllRequests();

        if (requests == null || requests.isEmpty()) {
            System.out.println("No requests found.");
            return;
        }

        System.out.println("Total Requests: " + requests.size());
        System.out.printf("%-6s %-8s %-10s %-10s %-8s %-12s %-20s%n",
                "ReqID", "Sender", "Receiver", "SkillID", "Type", "Status", "Date");
        System.out.println("------------------------------------------------------------------------------------");

        for (ExchangeRequest r : requests) {
            System.out.printf("%-6d %-8d %-10d %-10d %-8s %-12s %-20s%n",
                    r.getRequestId(),
                    r.getSenderId(),
                    r.getReceiverId(),
                    r.getRequestedSkillId(),
                    r.getExchangeType(),
                    r.getStatus(),
                    r.getRequestDate());
        }
        System.out.println("------------------------------------------------------------------------------------");
    }

    private void viewAllSessions() {
        System.out.println("\n--- ALL LEARNING SESSIONS ---");
        List<LearningSession> sessions = managerService.getAllSessions();

        if (sessions == null || sessions.isEmpty()) {
            System.out.println("No sessions found.");
            return;
        }

        System.out.println("Total Sessions: " + sessions.size());
        System.out.printf("%-6s %-10s %-10s %-10s %-12s %-8s %-8s %-12s%n",
                "SesID", "Teacher", "Learner", "SkillID", "Date", "Start", "End", "Status");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (LearningSession s : sessions) {
            System.out.printf("%-6d %-10d %-10d %-10d %-12s %-8s %-8s %-12s%n",
                    s.getSessionId(),
                    s.getTeacherId(),
                    s.getLearnerId(),
                    s.getSkillId(),
                    s.getSessionDate(),
                    s.getStartTime(),
                    s.getEndTime(),
                    s.getStatus());
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    private void viewAllFeedback() {
        System.out.println("\n--- ALL FEEDBACK ---");
        List<Feedback> feedbackList = managerService.getAllFeedback();

        if (feedbackList == null || feedbackList.isEmpty()) {
            System.out.println("No feedback found.");
            return;
        }

        System.out.println("Total Feedback: " + feedbackList.size());
        System.out.printf("%-6s %-10s %-10s %-10s %-8s %-25s %-20s%n",
                "ID", "Session", "Reviewer", "Reviewed", "Rating", "Comment", "Date");
        System.out.println("------------------------------------------------------------------------------------------------------");

        for (Feedback f : feedbackList) {
            String comment = (f.getComment() != null && f.getComment().length() > 22)
                    ? f.getComment().substring(0, 22) + "..." : f.getComment();
            System.out.printf("%-6d %-10d %-10d %-10d %-8s %-25s %-20s%n",
                    f.getFeedbackId(),
                    f.getSessionId(),
                    f.getReviewerId(),
                    f.getReviewedUserId(),
                    f.getRating() + "/5",
                    comment != null ? comment : "No comment",
                    f.getFeedbackDate());
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    private void viewCancellationHistory() {
        System.out.println("\n--- USER CANCELLATION HISTORY ---");
        List<User> users = managerService.getAllUsers();

        if (users == null || users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        System.out.println("=================================================================");
        System.out.printf("%-4s %-25s %-15s %-15s %-10s%n",
                "ID", "Name", "Cancellations", "Credits", "Status");
        System.out.println("=================================================================");

        for (User u : users) {
            int cancelCount = managerService.getCancellationCount(u.getUserId());
            String status = u.isActive() ? "Active" : "Suspended";

            System.out.printf("%-4d %-25s %-15d %-15d %-10s%n",
                    u.getUserId(),
                    u.getFullName(),
                    cancelCount,
                    u.getCredits(),
                    status);
        }
        System.out.println("=================================================================");
        System.out.println("\nTIP: Use Option 8/9 to take action on users with high cancellations.");
    }

    private void deactivateUser() {
        System.out.println("\n--- ACTIVATE / DEACTIVATE USER ---");
        System.out.println("1. Activate User");
        System.out.println("2. Deactivate User");
        System.out.print("Choose (1 or 2): ");

        int actionChoice = com.skillbridge.util.Validator.safeParseInt(scanner.nextLine());

        if (actionChoice != 1 && actionChoice != 2) {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter User ID: ");
        int userId = com.skillbridge.util.Validator.safeParseInt(scanner.nextLine());
        if (userId == -1) {
            System.out.println("Invalid User ID.");
            return;
        }

        List<User> users = managerService.getAllUsers();
        User targetUser = null;
        for (User u : users) {
            if (u.getUserId() == userId) {
                targetUser = u;
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        int cancelCount = managerService.getCancellationCount(userId);

        System.out.println("\n===========================================");
        System.out.println("|          USER DETAILS                   |");
        System.out.println("===========================================");
        System.out.println("User ID         : " + targetUser.getUserId());
        System.out.println("Name            : " + targetUser.getFullName());
        System.out.println("Enrollment No   : " + targetUser.getEnrollmentNo());
        System.out.println("Email           : " + targetUser.getEmail());
        System.out.println("Department      : " + targetUser.getDepartment());
        System.out.println("Semester        : " + targetUser.getSemester());
        System.out.println("Phone           : " + targetUser.getPhone());
        System.out.println("Credits         : " + targetUser.getCredits());
        System.out.println("Cancellations   : " + cancelCount);
        System.out.println("Current Status  : " + (targetUser.isActive() ? "Active" : "Suspended"));
        System.out.println("===========================================");

        if (actionChoice == 1) {
            if (targetUser.isActive()) {
                System.out.println("\nUser is already Active. No change needed.");
                return;
            }

            System.out.println("\nConfirm activation?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Choose (1 or 2): ");
            if (com.skillbridge.util.Validator.safeParseInt(scanner.nextLine()) == 1) {
                if (managerService.activateUser(userId)) {
                    System.out.println("User has been activated. They can now login again.");
                } else {
                    System.out.println("Failed to activate user.");
                }
            } else {
                System.out.println("Activation cancelled.");
            }

        } else {
            if (!targetUser.isActive()) {
                System.out.println("\nUser is already Suspended. No change needed.");
                return;
            }

            System.out.println("\nConfirm deactivation?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            System.out.print("Choose (1 or 2): ");
            if (com.skillbridge.util.Validator.safeParseInt(scanner.nextLine()) == 1) {
                if (managerService.deactivateUser(userId)) {
                    System.out.println("User has been deactivated. They can no longer login.");
                } else {
                    System.out.println("Failed to deactivate user.");
                }
            } else {
                System.out.println("Deactivation cancelled.");
            }
        }
    }

    private void deductUserCredits() {
        System.out.println("\n--- DEDUCT USER CREDITS ---");
        System.out.print("Enter User ID: ");
        int userId = com.skillbridge.util.Validator.safeParseInt(scanner.nextLine());
        if (userId == -1) {
            System.out.println("Invalid User ID.");
            return;
        }

        // Show user info
        List<User> users = managerService.getAllUsers();
        User targetUser = null;
        for (User u : users) {
            if (u.getUserId() == userId) {
                targetUser = u;
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }

        int cancelCount = managerService.getCancellationCount(userId);

        System.out.println("\n--- USER DETAILS ---");
        System.out.println("Name           : " + targetUser.getFullName());
        System.out.println("Current Credits: " + targetUser.getCredits());
        System.out.println("Cancellations  : " + cancelCount);

        System.out.print("\nEnter credits to deduct: ");
        int amount = com.skillbridge.util.Validator.safeParseInt(scanner.nextLine());
        if (amount <= 0) {
            System.out.println("Amount must be positive.");
            return;
        }

        System.out.println("Confirm deduction of " + amount + " credits?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Choose (1 or 2): ");
        if (com.skillbridge.util.Validator.safeParseInt(scanner.nextLine()) == 1) {
            if (managerService.deductCredits(userId, amount)) {
                System.out.println("Deducted " + amount + " credits from " + targetUser.getFullName());
                System.out.println("New balance: " + Math.max(0, targetUser.getCredits() - amount));
            } else {
                System.out.println("Failed to deduct credits.");
            }
        } else {
            System.out.println("Deduction cancelled.");
        }
    }

    // NEW: Called from MainMenu smart login (no separate login needed)
    public void showManagerDashboardDirect(Manager manager) {
        showManagerDashboard(manager);
    }
}