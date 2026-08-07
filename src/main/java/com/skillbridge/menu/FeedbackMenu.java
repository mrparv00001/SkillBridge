package com.skillbridge.menu;

import com.skillbridge.model.Feedback;
import com.skillbridge.service.FeedbackService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import java.util.List;
import java.util.Scanner;

public class FeedbackMenu {

    private Scanner scanner;
    private FeedbackService feedbackService;

    public FeedbackMenu() {
        this.scanner = new Scanner(System.in);
        this.feedbackService = new FeedbackService();
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   FEEDBACK & RATINGS");
            System.out.println("=================================");
            System.out.println("1. Submit Feedback");
            System.out.println("2. View All Feedback Received");
            System.out.println("3. View Skill-wise Feedback");
            System.out.println("4. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleSubmitFeedback(); break;
                case "2": handleViewFeedback(); break;
                case "3": handleSkillWiseSummary(); break;
                case "4": running = false; break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void handleSubmitFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SUBMIT FEEDBACK ---");

        // Show completed sessions
        System.out.println("\n--- Your Completed Sessions ---");
        com.skillbridge.dao.LearningSessionDAO sessionDAO = new com.skillbridge.dao.LearningSessionDAO();
        List<com.skillbridge.model.LearningSession> sessions = sessionDAO.getSessionsByUser(currentUserId);

        boolean hasCompleted = false;
        for (com.skillbridge.model.LearningSession s : sessions) {
            if ("Completed".equalsIgnoreCase(s.getStatus())) {
                if (!hasCompleted) {
                    System.out.println("Session ID | Date       | Partner");
                    System.out.println("-----------|------------|--------");
                    hasCompleted = true;
                }
                int partnerId = (s.getTeacherId() == currentUserId) ? s.getLearnerId() : s.getTeacherId();
                com.skillbridge.dao.UserDAO userDAO = new com.skillbridge.dao.UserDAO();
                com.skillbridge.model.User partner = userDAO.getUserById(partnerId);
                String partnerName = partner != null ? partner.getFullName() : "Unknown";
                System.out.println(s.getSessionId() + "          | " + s.getSessionDate() + " | " + partnerName + " (ID:" + partnerId + ")");
            }
        }

        if (!hasCompleted) {
            System.out.println("No completed sessions found.");
            return;
        }

        System.out.print("\nSelect Session ID from above: ");
        int sessionId = Validator.safeParseInt(scanner.nextLine());
        if (sessionId == -1) { System.out.println("Invalid Session ID."); return; }

        // Auto-detect reviewed user
        com.skillbridge.model.LearningSession selectedSession = sessionDAO.getSessionById(sessionId);
        if (selectedSession == null || !"Completed".equalsIgnoreCase(selectedSession.getStatus())) {
            System.out.println("Invalid session.");
            return;
        }

        int reviewedId = (selectedSession.getTeacherId() == currentUserId) ? selectedSession.getLearnerId() : selectedSession.getTeacherId();
        com.skillbridge.dao.UserDAO userDAO = new com.skillbridge.dao.UserDAO();
        com.skillbridge.model.User reviewedUser = userDAO.getUserById(reviewedId);
        System.out.println("Reviewing: " + (reviewedUser != null ? reviewedUser.getFullName() : "Unknown"));

        System.out.print("Enter Rating (1-5): ");
        int rating = Validator.safeParseInt(scanner.nextLine());
        if (!Validator.isValidRating(rating)) { System.out.println("Rating must be 1-5."); return; }

        System.out.print("Enter Comment: ");
        String comment = scanner.nextLine();

        if (feedbackService.leaveFeedback(sessionId, currentUserId, reviewedId, rating, comment)) {
            System.out.println("Feedback submitted!");
        }
    }

    private void handleViewFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- FEEDBACK RECEIVED ---");
        List<Feedback> feedbackList = feedbackService.getFeedbackForUser(currentUserId);

        if (feedbackList != null && !feedbackList.isEmpty()) {
            System.out.printf("%-10s %-8s %-25s %-20s%n",
                    "Session", "Rating", "Comment", "Date");
            System.out.println("---------------------------------------------------------------------");
            for (Feedback fb : feedbackList) {
                String comment = (fb.getComment() != null && !fb.getComment().trim().isEmpty())
                        ? fb.getComment() : "No comment";
                if (comment.length() > 22) comment = comment.substring(0, 22) + "...";
                System.out.printf("%-10d %-8s %-25s %-20s%n",
                        fb.getSessionId(),
                        fb.getRating() + "/5",
                        comment,
                        fb.getFeedbackDate());
            }
            System.out.println("---------------------------------------------------------------------");
        }
    }

    private void handleSkillWiseSummary() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SKILL-WISE FEEDBACK SUMMARY ---");
        List<String> summary = feedbackService.getSkillWiseFeedbackSummary(currentUserId);

        if (summary == null || summary.isEmpty()) {
            System.out.println("No skill-wise feedback yet.");
        } else {
            for (String line : summary) {
                System.out.println(line);
            }
        }
    }
}