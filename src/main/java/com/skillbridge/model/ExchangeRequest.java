package com.skillbridge.model;

import java.sql.Timestamp;

public class ExchangeRequest {
    private int requestId;
    private int senderId;
    private int receiverId;
    private int requestedSkillId;
    private String exchangeType; // e.g., "Direct", "Swap"
    private String message;
    private String status; // Pending, Accepted, Rejected, Cancelled
    private Timestamp requestDate;

    public ExchangeRequest() {}

    public ExchangeRequest(int requestId, int senderId, int receiverId, int requestedSkillId,
                           String exchangeType, String message, String status, Timestamp requestDate) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.requestedSkillId = requestedSkillId;
        this.exchangeType = exchangeType;
        this.message = message;
        this.status = status;
        this.requestDate = requestDate;
    }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public int getRequestedSkillId() { return requestedSkillId; }
    public void setRequestedSkillId(int requestedSkillId) { this.requestedSkillId = requestedSkillId; }

    public String getExchangeType() { return exchangeType; }
    public void setExchangeType(String exchangeType) { this.exchangeType = exchangeType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }
}