package com.skillbridge.service;

import com.skillbridge.dao.ExchangeRequestDAO;
import com.skillbridge.model.ExchangeRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestService {

    private final ExchangeRequestDAO exchangeRequestDAO;

    public RequestService() {
        this.exchangeRequestDAO = new ExchangeRequestDAO();
    }

    /**
     * Sends a new exchange request by building an ExchangeRequest object
     * and passing it to the DAO's createRequest method.
     */
    public boolean sendSkillRequest(int senderId, int receiverId, int requestedSkillId, String exchangeType, String message) {
        if (senderId == receiverId) {
            System.err.println("❌ Validation Error: You cannot send a request to yourself.");
            return false;
        }

        // Build the request object to match your DAO's createRequest signature
        ExchangeRequest newRequest = new ExchangeRequest();
        newRequest.setSenderId(senderId);
        newRequest.setReceiverId(receiverId);
        newRequest.setRequestedSkillId(requestedSkillId);
        newRequest.setExchangeType(exchangeType);
        newRequest.setMessage(message);
        // Note: Your DAO automatically sets "Pending" in the SQL, so we don't need to set it here.

        boolean isSent = exchangeRequestDAO.createRequest(newRequest);

        if (isSent) {
            System.out.println("✅ Request successfully sent!");
        } else {
            System.err.println("❌ Failed to send request. Please try again.");
        }
        return isSent;
    }

    /**
     * Fetches all requests for a user, then filters it down to only show
     * the 'Pending' requests where this user is the receiver.
     */
    public List<ExchangeRequest> viewMyPendingRequests(int userId) {
        // Fetch ALL requests from your DAO
        List<ExchangeRequest> allRequests = exchangeRequestDAO.getRequestsByUserId(userId);
        List<ExchangeRequest> pendingRequests = new ArrayList<>();

        // Business Logic: Filter out only the ones waiting for THIS user's response
        for (ExchangeRequest req : allRequests) {
            if (req.getReceiverId() == userId && "Pending".equalsIgnoreCase(req.getStatus())) {
                pendingRequests.add(req);
            }
        }

        if (pendingRequests.isEmpty()) {
            System.out.println("ℹ️ You have no pending requests at this time.");
        } else {
            System.out.println("📬 You have " + pendingRequests.size() + " new request(s) waiting for your response.");
        }

        return pendingRequests;
    }

    /**
     * Updates the status of a request using your DAO's updateStatus method.
     */
    public boolean respondToRequest(int requestId, boolean accept) {
        String newStatus = accept ? "Accepted" : "Rejected";

        boolean isUpdated = exchangeRequestDAO.updateStatus(requestId, newStatus);

        if (isUpdated) {
            System.out.println("✅ Request " + requestId + " has been marked as " + newStatus + ".");
        } else {
            System.err.println("❌ Failed to update the request status.");
        }

        return isUpdated;
    }
}