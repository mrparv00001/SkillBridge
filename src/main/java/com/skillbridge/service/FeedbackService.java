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
     * Validates and submits a new piece of feedback for a completed session.
     */
    public boolean leaveFeedback(Feedback feedback) {
        // 1. Basic Validation
        if (feedback.getSessionId() <= 0 || feedback.getReviewerId() <= 0 || feedback.getReviewedUserId() <= 0) {
            System.err.println("❌ Validation Error: Missing critical IDs for feedback.");
            return false;
        }

        // Enforce a strict 1 to 5 rating scale
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            System.err.println("❌ Validation Error: Rating must be between 1 and 5.");
            return false;
        }

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
     * Retrieves all general feedback left for a specific user.
     */
    public List<Feedback> getUserFeedback(int userId) {
        List<Feedback> feedbackList = feedbackDAO.getFeedbackByReviewedUser(userId);

        if (feedbackList.isEmpty()) {
            System.out.println("ℹ️ This user has not received any feedback yet.");
        } else {
            System.out.println("🌟 Found " + feedbackList.size() + " review(s) for this user.");
        }

        return feedbackList;
    }

    /**
     * Fetches and displays detailed skill-wise feedback for a user.
     * This utilizes the SQL JOIN query from the DAO to print formatted strings.
     */
    public void displaySkillWiseFeedback(int userId) {
        List<String> skillFeedback = feedbackDAO.getSkillWiseFeedbackForUser(userId);

        if (skillFeedback.isEmpty()) {
            System.out.println("ℹ️ No skill-specific feedback available for this user.");
        } else {
            System.out.println("📊 Skill-Wise Feedback Breakdown:");
            for (String record : skillFeedback) {
                System.out.println("  - " + record);
            }
        }
    }
}