package com.skillbridge.service;

import com.skillbridge.dao.FeedbackDAO;
import com.skillbridge.model.Feedback;

import java.util.List;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
    }

    /**
     * Overloaded method to support direct parameters from FeedbackMenu.
     */
    public boolean leaveFeedback(int sessionId, int reviewerId, int reviewedUserId, int rating, String comment) {
        // 1. Basic Validation
        if (sessionId <= 0 || reviewerId <= 0 || reviewedUserId <= 0) {
            System.err.println("❌ Validation Error: Missing critical IDs for feedback.");
            return false;
        }

        // Enforce a strict 1 to 5 rating scale
        if (rating < 1 || rating > 5) {
            System.err.println("❌ Validation Error: Rating must be between 1 and 5.");
            return false;
        }

        // Create Feedback object to pass to DAO
        Feedback feedback = new Feedback();
        feedback.setSessionId(sessionId);
        feedback.setReviewerId(reviewerId);
        feedback.setReviewedUserId(reviewedUserId);
        feedback.setRating(rating);
        feedback.setComment(comment);

        // 2. Submit to the database via DAO
        boolean isSubmitted = feedbackDAO.submitFeedback(feedback);

        if (isSubmitted) {
            System.out.println("✅ Feedback successfully submitted. Thank you for your review!");
        } else {
            System.err.println("❌ Failed to submit feedback to the database.");
        }

        return isSubmitted;
    }

    /**
     * Retrieves all general feedback left for a specific user, matching FeedbackMenu call.
     */
    public List<Feedback> getFeedbackForUser(int userId) {
        List<Feedback> feedbackList = feedbackDAO.getFeedbackByReviewedUser(userId);

        if (feedbackList.isEmpty()) {
            System.out.println("ℹ️ This user has not received any feedback yet.");
        } else {
            System.out.println("🌟 Found " + feedbackList.size() + " review(s) for this user.");
        }

        return feedbackList;
    }

    /**
     * Fetches skill-wise feedback summary list, matching FeedbackMenu call.
     */
    public List<String> getSkillWiseFeedbackSummary(int userId) {
        return feedbackDAO.getSkillWiseFeedbackForUser(userId);
    }
}