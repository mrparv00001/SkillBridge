package org.skillbridge.model;

import java.sql.Timestamp;

public class ExchangeRequest {
    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED
    }
    private int requestId;
    private int senderId;
    private int receiverId;
    private int requestedSkillId;
    private String exchangeType;
    private String message;

    private Status status;
    private Timestamp requestDate;

    public ExchangeRequest(int requestId, int senderId, int receiverId, int requestedSkillId, String exchangeType, String message, Status status, Timestamp requestDate) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.requestedSkillId = requestedSkillId;
        this.exchangeType = exchangeType;
        this.message = message;
        this.status = status;
        this.requestDate = requestDate;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getRequestedSkillId() {
        return requestedSkillId;
    }

    public void setRequestedSkillId(int requestedSkillId) {
        this.requestedSkillId = requestedSkillId;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }
}
