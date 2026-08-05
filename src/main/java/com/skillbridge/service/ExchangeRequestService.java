package com.skillbridge.service;

import com.skillbridge.dao.ExchangeRequestDAO;
import com.skillbridge.model.ExchangeRequest;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class ExchangeRequestService {

    private final ExchangeRequestDAO exchangeRequestDAO = new ExchangeRequestDAO();

    public boolean sendExchangeRequest(int senderId, int receiverId, int requestedSkillId, String exchangeType, String message) {
        if (senderId == receiverId) {
            System.out.println("You cannot send a request to yourself.");
            return false;
        }

        // Check for duplicate pending request
        List<ExchangeRequest> existingRequests = exchangeRequestDAO.getRequestsByUserId(senderId);
        for (ExchangeRequest req : existingRequests) {
            if (req.getSenderId() == senderId &&
                    req.getReceiverId() == receiverId &&
                    req.getRequestedSkillId() == requestedSkillId &&
                    "Pending".equalsIgnoreCase(req.getStatus())) {
                System.out.println("You already have a pending request for this skill with this user.");
                return false;
            }
        }

        ExchangeRequest request = new ExchangeRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setRequestedSkillId(requestedSkillId);
        request.setExchangeType(exchangeType);
        request.setMessage(message);

        boolean success = exchangeRequestDAO.createRequest(request);

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   EXCHANGE REQUEST SENT!                 ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Sent to User ID: " + receiverId);
            System.out.println("║ Skill ID: " + requestedSkillId);
            System.out.println("║ Type: " + exchangeType);
            System.out.println("║ Notification sent to receiver.           ║");
            System.out.println("╚══════════════════════════════════════════╝\n");
        }

        return success;
    }

    public boolean acceptExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getReceiverId() != currentUserId) {
            System.out.println("Unauthorized: Only the receiver can accept.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Request is not pending.");
            return false;
        }

        boolean success = exchangeRequestDAO.updateStatus(requestId, "Accepted");

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   REQUEST ACCEPTED SUCCESSFULLY!         ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Request ID: " + requestId);
            System.out.println("║ You can now schedule the session.        ║");
            System.out.println("║ Notification sent to sender.             ║");
            System.out.println("╚══════════════════════════════════════════╝\n");
        }

        return success;
    }

    public boolean rejectExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getReceiverId() != currentUserId) {
            System.out.println("Unauthorized: Only the receiver can reject.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Request is not pending.");
            return false;
        }

        boolean success = exchangeRequestDAO.updateStatus(requestId, "Rejected");

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   REQUEST REJECTED                       ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Request ID: " + requestId);
            System.out.println("║ Notification sent to sender.             ║");
            System.out.println("╚══════════════════════════════════════════╝\n");
        }

        return success;
    }

    public boolean cancelExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getSenderId() != currentUserId) {
            System.out.println("Unauthorized: Only the sender can cancel.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Only pending requests can be cancelled.");
            return false;
        }

        boolean success = exchangeRequestDAO.updateStatus(requestId, "Cancelled");

        // POP-UP NOTIFICATION
        if (success) {
            System.out.println("\n╔══════════════════════════════════════════╗");
            System.out.println("║   REQUEST CANCELLED                      ║");
            System.out.println("╠══════════════════════════════════════════╣");
            System.out.println("║ Request ID: " + requestId);
            System.out.println("╚══════════════════════════════════════════╝\n");
        }

        return success;
    }

    // Return as LinkedList (Data Structure requirement)
    public List<ExchangeRequest> getUserRequestHistory(int userId) {
        return new LinkedList<>(exchangeRequestDAO.getRequestsByUserId(userId));
    }

    /**
     * QUEUE Data Structure - Get pending requests in FIFO order
     * (First received request should be accepted first)
     */
    public Queue<ExchangeRequest> getPendingRequestsQueue(int receiverId) {
        Queue<ExchangeRequest> pendingQueue = new LinkedList<>();
        List<ExchangeRequest> allRequests = exchangeRequestDAO.getRequestsByUserId(receiverId);

        // Sort by date (oldest first) and add to queue
        allRequests.sort((r1, r2) -> r1.getRequestDate().compareTo(r2.getRequestDate()));

        for (ExchangeRequest req : allRequests) {
            if (req.getReceiverId() == receiverId && "Pending".equalsIgnoreCase(req.getStatus())) {
                pendingQueue.offer(req); // FIFO
            }
        }

        return pendingQueue;
    }

    /**
     * STACK Data Structure - Get notifications history (LIFO - Latest first)
     */
    public Stack<ExchangeRequest> getRequestNotificationsStack(int userId) {
        Stack<ExchangeRequest> notificationStack = new Stack<>();
        List<ExchangeRequest> allRequests = exchangeRequestDAO.getRequestsByUserId(userId);

        // Sort by date (oldest first) then push to stack (latest goes on top)
        allRequests.sort((r1, r2) -> r1.getRequestDate().compareTo(r2.getRequestDate()));

        for (ExchangeRequest req : allRequests) {
            notificationStack.push(req); // LIFO - Latest on top
        }

        return notificationStack;
    }
}