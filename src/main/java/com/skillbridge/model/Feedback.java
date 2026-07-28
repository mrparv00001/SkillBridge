package com.skillbridge.model;

import java.sql.Timestamp;

public class Feedback {
    private int feedbackId;
    private int sessionId;
    private int reviewerId;
    private int reviewedUserId;
    private int rating; // 1 to 5
    private String comment;
    private Timestamp feedbackDate;

    public Feedback() {}

    public Feedback(int feedbackId, int sessionId, int reviewerId, int reviewedUserId,
                    int rating, String comment, Timestamp feedbackDate) {
        this.feedbackId = feedbackId;
        this.sessionId = sessionId;
        this.reviewerId = reviewerId;
        this.reviewedUserId = reviewedUserId;
        this.rating = rating;
        this.comment = comment;
        this.feedbackDate = feedbackDate;
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }

    public int getReviewedUserId() { return reviewedUserId; }
    public void setReviewedUserId(int reviewedUserId) { this.reviewedUserId = reviewedUserId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getFeedbackDate() { return feedbackDate; }
    public void setFeedbackDate(Timestamp feedbackDate) { this.feedbackDate = feedbackDate; }
}