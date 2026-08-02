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

        System.out.print("Enter Receiver's User ID: ");
        int receiverId = Validator.safeParseInt(scanner.nextLine());
        if (receiverId == -1) { System.out.println("❌ Invalid Receiver ID."); return; }

        System.out.print("Enter Requested Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("❌ Invalid Skill ID."); return; }

        System.out.print("Enter Exchange Type (Direct/Swap): ");
        String type = scanner.nextLine();

        System.out.print("Enter Message: ");
        String message = scanner.nextLine();

        if (requestService.sendExchangeRequest(currentUserId, receiverId, skillId, type, message)) {
            System.out.println("✅ Request sent successfully!");
        }
    }

    private void handleViewRequests() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- MY REQUESTS ---");
        List<ExchangeRequest> requests = requestService.getUserRequestHistory(currentUserId);

        if (requests == null || requests.isEmpty()) {
            System.out.println("ℹ️ No requests yet.");
        } else {
            for (ExchangeRequest req : requests) {
                System.out.println("Request ID: " + req.getRequestId() +
                        " | Sender: " + req.getSenderId() +
                        " | Receiver: " + req.getReceiverId() +
                        " | Skill: " + req.getRequestedSkillId() +
                        " | Type: " + req.getExchangeType() +
                        " | Status: " + req.getStatus());
            }
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