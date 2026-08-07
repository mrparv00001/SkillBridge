package com.skillbridge.menu;

import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.service.ExchangeRequestService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import java.util.List;
import java.util.Scanner;

public class ExchangeRequestMenu {

    private Scanner scanner;
    private ExchangeRequestService requestService;

    public ExchangeRequestMenu() {
        this.scanner = new Scanner(System.in);
        this.requestService = new ExchangeRequestService();
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   EXCHANGE REQUESTS");
            System.out.println("=================================");
            System.out.println("1. Send New Request");
            System.out.println("2. View My Requests");
            System.out.println("3. Accept Request");
            System.out.println("4. Reject Request");
            System.out.println("5. Cancel Request");
            System.out.println("6. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleSendRequest(); break;
                case "2": handleViewRequests(); break;
                case "3": handleAcceptRequest(); break;
                case "4": handleRejectRequest(); break;
                case "5": handleCancelRequest(); break;
                case "6": running = false; break;
                default: System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void handleSendRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SEND NEW REQUEST ---");

        // Show available skills first
        System.out.println("\n--- Available Skills ---");
        com.skillbridge.service.SkillService skillService = new com.skillbridge.service.SkillService();
        List<com.skillbridge.model.Skill> skills = skillService.getAllSkills();
        if (skills == null || skills.isEmpty()) {
            System.out.println("No skills available.");
            return;
        }
        for (com.skillbridge.model.Skill skill : skills) {
            System.out.println(skill.getSkillId() + ". " + skill.getSkillName() + " (" + skill.getCategory() + ")");
        }

        System.out.print("\nSelect Skill Number: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("Invalid Skill."); return; }

        // Show teachers for that skill
        System.out.println("\n--- Teachers for this Skill ---");
        com.skillbridge.service.SearchService searchService = new com.skillbridge.service.SearchService();
        // Get all teachers by searching all departments
        com.skillbridge.dao.UserSkillDAO userSkillDAO = new com.skillbridge.dao.UserSkillDAO();
        List<com.skillbridge.model.UserSkill> teachers = userSkillDAO.findUsersBySkill(skillId, "Teaching");

        if (teachers == null || teachers.isEmpty()) {
            System.out.println("No teachers found for this skill.");
            return;
        }

        com.skillbridge.dao.UserDAO userDAO = new com.skillbridge.dao.UserDAO();
        for (com.skillbridge.model.UserSkill us : teachers) {
            if (us.getUserId() != currentUserId) {
                com.skillbridge.model.User teacher = userDAO.getUserById(us.getUserId());
                if (teacher != null && teacher.isActive()) {
                    System.out.println("ID: " + teacher.getUserId() + " | " + teacher.getFullName() + " | " + teacher.getDepartment() + " | Level: " + us.getSkillLevel());
                }
            }
        }

        System.out.print("\nSelect Teacher User ID from above: ");
        int receiverId = Validator.safeParseInt(scanner.nextLine());
        if (receiverId == -1) { System.out.println("Invalid ID."); return; }

        System.out.println("\nSelect Exchange Type:");
        System.out.println("  1. Direct (One-way learning - Credits deducted)");
        System.out.println("  2. Swap (Two-way exchange - No credits deducted)");
        System.out.print("Choose (1 or 2): ");
        int typeChoice = Validator.safeParseInt(scanner.nextLine());

        String type;
        if (typeChoice == 1) type = "Direct";
        else if (typeChoice == 2) type = "Swap";
        else { System.out.println("Invalid choice."); return; }

        System.out.print("Enter Message: ");
        String message = scanner.nextLine();

        if (requestService.sendExchangeRequest(currentUserId, receiverId, skillId, type, message)) {
            System.out.println("Request sent successfully!");
        }
    }

    private void handleViewRequests() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- MY REQUESTS ---");

        java.util.Queue<ExchangeRequest> pendingQueue = requestService.getPendingRequestsQueue(currentUserId);

        if (!pendingQueue.isEmpty()) {
            System.out.println("\n### PENDING REQUESTS (First Come First Serve) ###");
            System.out.printf("%-6s %-10s %-10s %-8s %-20s%n",
                    "ReqID", "From User", "Skill ID", "Type", "Received");
            System.out.println("--------------------------------------------------------");
            int priority = 1;
            while (!pendingQueue.isEmpty()) {
                ExchangeRequest req = pendingQueue.poll();
                System.out.printf("%-6d %-10d %-10d %-8s %-20s%n",
                        req.getRequestId(),
                        req.getSenderId(),
                        req.getRequestedSkillId(),
                        req.getExchangeType(),
                        req.getRequestDate());
                priority++;
            }
            System.out.println("--------------------------------------------------------\n");
        }

        List<ExchangeRequest> allRequests = requestService.getUserRequestHistory(currentUserId);
        boolean hasHistory = false;

        for (ExchangeRequest req : allRequests) {
            if (!"Pending".equalsIgnoreCase(req.getStatus())) {
                if (!hasHistory) {
                    System.out.println("### REQUEST HISTORY ###");
                    System.out.printf("%-6s %-8s %-10s %-10s %-8s %-12s%n",
                            "ReqID", "Sender", "Receiver", "Skill ID", "Type", "Status");
                    System.out.println("------------------------------------------------------------");
                    hasHistory = true;
                }
                System.out.printf("%-6d %-8d %-10d %-10d %-8s %-12s%n",
                        req.getRequestId(),
                        req.getSenderId(),
                        req.getReceiverId(),
                        req.getRequestedSkillId(),
                        req.getExchangeType(),
                        req.getStatus());
            }
        }

        if (hasHistory) {
            System.out.println("------------------------------------------------------------");
        }

        if (pendingQueue.isEmpty() && !hasHistory) {
            System.out.println("No requests yet.");
        }
    }

    private void handleAcceptRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.print("\nEnter Request ID to Accept: ");
        int requestId = Validator.safeParseInt(scanner.nextLine());
        if (requestId == -1) { System.out.println("❌ Invalid Request ID."); return; }

        if (requestService.acceptExchangeRequest(requestId, currentUserId)) {
            System.out.println("✅ Request accepted!");
        }
    }

    private void handleRejectRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.print("\nEnter Request ID to Reject: ");
        int requestId = Validator.safeParseInt(scanner.nextLine());
        if (requestId == -1) { System.out.println("❌ Invalid Request ID."); return; }

        if (requestService.rejectExchangeRequest(requestId, currentUserId)) {
            System.out.println("✅ Request rejected!");
        }
    }

    private void handleCancelRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.print("\nEnter Request ID to Cancel: ");
        int requestId = Validator.safeParseInt(scanner.nextLine());
        if (requestId == -1) { System.out.println("❌ Invalid Request ID."); return; }

        if (requestService.cancelExchangeRequest(requestId, currentUserId)) {
            System.out.println("✅ Request cancelled!");
        }
    }
}