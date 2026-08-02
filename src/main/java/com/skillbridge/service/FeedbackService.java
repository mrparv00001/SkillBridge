package com.skillbridge.service;

import com.skillbridge.dao.FeedbackDAO;
import com.skillbridge.dao.LearningSessionDAO;
import com.skillbridge.model.Feedback;
import com.skillbridge.model.LearningSession;

import java.util.List;

public class FeedbackService {

    private final FeedbackDAO feedbackDAO;
    private final LearningSessionDAO sessionDAO;

    public FeedbackService() {
        this.feedbackDAO = new FeedbackDAO();
        this.sessionDAO = new LearningSessionDAO();
    }

    public boolean leaveFeedback(int sessionId, int reviewerId, int reviewedUserId, int rating, String comment) {
        if (sessionId <= 0 || reviewerId <= 0 || reviewedUserId <= 0) {
            System.err.println("❌ Validation Error: Invalid IDs provided.");
            return false;
        }

        if (rating < 1 || rating > 5) {
            System.err.println("❌ Validation Error: Rating must be between 1 and 5.");
            return false;
        }

        if (reviewerId == reviewedUserId) {
            System.err.println("❌ You cannot review yourself.");
            return false;
        }

        // FIX: Check if session exists and is Completed
        LearningSession session = sessionDAO.getSessionById(sessionId);
        if (session == null) {
            System.err.println("❌ Session not found.");
            return false;
        }

        if (!"Completed".equalsIgnoreCase(session.getStatus())) {
            System.err.println("❌ You can only submit feedback for Completed sessions.");
            return false;
        }

        // FIX: Reviewer must be part of the session (teacher OR learner)
        if (session.getTeacherId() != reviewerId && session.getLearnerId() != reviewerId) {
            System.err.println("❌ Unauthorized: You were not part of this session.");
            return false;
        }

        // FIX: Reviewed user must be the OTHER person in the session
        if (session.getTeacherId() != reviewedUserId && session.getLearnerId() != reviewedUserId) {
            System.err.println("❌ Invalid: Reviewed user is not part of this session.");
            return false;
        }

        Feedback feedback = new Feedback();
        feedback.setSessionId(sessionId);
        feedback.setReviewerId(reviewerId);
        feedback.setReviewedUserId(reviewedUserId);
        feedback.setRating(rating);
        feedback.setComment(comment);

        boolean isSubmitted = feedbackDAO.submitFeedback(feedback);

        if (isSubmitted) {
            System.out.println("✅ Feedback successfully submitted. Thank you!");
        } else {
            System.err.println("❌ Failed to submit feedback. You may have already reviewed this session.");
        }

        return isSubmitted;
    }

    public List<Feedback> getFeedbackForUser(int userId) {
        List<Feedback> feedbackList = feedbackDAO.getFeedbackByReviewedUser(userId);
        if (feedbackList == null || feedbackList.isEmpty()) {
            System.out.println("ℹ️ No feedback found for this user yet.");
        }
        return feedbackList;
    }

    public List<String> getSkillWiseFeedbackSummary(int userId) {
        return feedbackDAO.getSkillWiseFeedbackForUser(userId);
    }
}