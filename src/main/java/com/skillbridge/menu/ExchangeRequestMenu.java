package com.skillbridge.menu;

import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.service.ExchangeRequestService;
import com.skillbridge.util.SessionManager;

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
            System.out.println("1. Send New Exchange Request");
            System.out.println("2. View My Requests");
            System.out.println("3. Accept a Request");
            System.out.println("4. Reject a Request");
            System.out.println("5. Cancel a Request");
            System.out.println("6. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleSendRequest();
                    break;
                case "2":
                    handleViewRequests();
                    break;
                case "3":
                    handleAcceptRequest();
                    break;
                case "4":
                    handleRejectRequest();
                    break;
                case "5":
                    handleCancelRequest();
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void handleSendRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SEND NEW REQUEST ---");

        System.out.print("Enter Receiver's User ID: ");
        int receiverId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Requested Skill ID: ");
        int skillId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Exchange Type (e.g., Direct, Swap): ");
        String type = scanner.nextLine();

        System.out.print("Enter Message: ");
        String message = scanner.nextLine();

        boolean success = requestService.sendExchangeRequest(currentUserId, receiverId, skillId, type, message);
        if (success) {
            System.out.println("Request sent successfully!");
        }
    }

    private void handleViewRequests() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- MY REQUESTS ---");
        List<ExchangeRequest> requests = requestService.getUserRequestHistory(currentUserId);

        if (requests.isEmpty()) {
            System.out.println("You have no requests.");
        } else {
            for (ExchangeRequest req : requests) {
                System.out.println("Request ID: " + req.getRequestId() +
                        " | Sender: " + req.getSenderId() +
                        " | Receiver: " + req.getReceiverId() +
                        " | Skill ID: " + req.getRequestedSkillId() +
                        " | Type: " + req.getExchangeType() +
                        " | Status: " + req.getStatus());
            }
        }
    }

    private void handleAcceptRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- ACCEPT REQUEST ---");
        System.out.print("Enter Request ID to Accept: ");
        try {
            int requestId = Integer.parseInt(scanner.nextLine());
            if (requestService.acceptExchangeRequest(requestId, currentUserId)) {
                System.out.println("Request successfully accepted!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Request ID.");
        }
    }

    private void handleRejectRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- REJECT REQUEST ---");
        System.out.print("Enter Request ID to Reject: ");
        try {
            int requestId = Integer.parseInt(scanner.nextLine());
            if (requestService.rejectExchangeRequest(requestId, currentUserId)) {
                System.out.println("Request successfully rejected!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Request ID.");
        }
    }

    private void handleCancelRequest() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- CANCEL REQUEST ---");
        System.out.print("Enter Request ID to Cancel: ");
        try {
            int requestId = Integer.parseInt(scanner.nextLine());
            if (requestService.cancelExchangeRequest(requestId, currentUserId)) {
                System.out.println("Request cancelled successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid Request ID.");
        }
    }
}