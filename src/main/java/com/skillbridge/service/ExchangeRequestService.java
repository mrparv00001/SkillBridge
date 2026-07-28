package com.skillbridge.service;

import com.skillbridge.dao.ExchangeRequestDAO;
import com.skillbridge.model.ExchangeRequest;

import java.util.List;

public class ExchangeRequestService {

    private final ExchangeRequestDAO exchangeRequestDAO = new ExchangeRequestDAO();

    public boolean sendExchangeRequest(int senderId, int receiverId, int requestedSkillId, String exchangeType, String message) {
        if (senderId == receiverId) {
            System.out.println("You cannot send an exchange request to yourself.");
            return false;
        }
        ExchangeRequest request = new ExchangeRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setRequestedSkillId(requestedSkillId);
        request.setExchangeType(exchangeType);
        request.setMessage(message);
        return exchangeRequestDAO.createRequest(request);
    }

    public boolean acceptExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getReceiverId() != currentUserId) {
            System.out.println("Unauthorized: Only the receiver can accept this request.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Request is not in a pending state.");
            return false;
        }
        return exchangeRequestDAO.updateStatus(requestId, "Accepted");
    }

    public boolean rejectExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getReceiverId() != currentUserId) {
            System.out.println("Unauthorized: Only the receiver can reject this request.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Request is not in a pending state.");
            return false;
        }
        return exchangeRequestDAO.updateStatus(requestId, "Rejected");
    }

    public boolean cancelExchangeRequest(int requestId, int currentUserId) {
        ExchangeRequest request = exchangeRequestDAO.getRequestById(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        if (request.getSenderId() != currentUserId) {
            System.out.println("Unauthorized: Only the sender can cancel this request.");
            return false;
        }
        if (!"Pending".equalsIgnoreCase(request.getStatus())) {
            System.out.println("Only pending requests can be cancelled.");
            return false;
        }
        return exchangeRequestDAO.updateStatus(requestId, "Cancelled");
    }

    public List<ExchangeRequest> getUserRequestHistory(int userId) {
        return exchangeRequestDAO.getRequestsByUserId(userId);
    }
}