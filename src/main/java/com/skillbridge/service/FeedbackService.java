package com.skillbridge.service;

import com.skillbridge.dao.FeedbackDAO;
import com.skillbridge.model.Feedback;

import java.util.List;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO = new FeedbackDAO();

    public boolean submitFeedback(int sessionId, int reviewerId, int reviewedUserId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return false;
        }
        if (reviewerId == reviewedUserId) {
            System.out.println("Reviewer and reviewed user cannot be the same person.");
            return false;
        }

        Feedback feedback = new Feedback();
        feedback.setSessionId(sessionId);
        feedback.setReviewerId(reviewerId);
        feedback.setReviewedUserId(reviewedUserId);
        feedback.setRating(rating);
        feedback.setComment(comment);

        return feedbackDAO.submitFeedback(feedback);
    }

    public List<Feedback> getFeedbackForUser(int userId) {
        return feedbackDAO.getFeedbackByReviewedUser(userId);
    }

    public List<String> getSkillWiseFeedbackSummary(int userId) {
        return feedbackDAO.getSkillWiseFeedbackForUser(userId);
    }
}