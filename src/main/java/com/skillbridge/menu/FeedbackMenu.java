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
                default: System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void handleSubmitFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SUBMIT FEEDBACK ---");

        System.out.print("Enter Session ID: ");
        int sessionId = Validator.safeParseInt(scanner.nextLine());
        if (sessionId == -1) { System.out.println("❌ Invalid Session ID."); return; }

        System.out.print("Enter Reviewed User's ID: ");
        int reviewedId = Validator.safeParseInt(scanner.nextLine());
        if (reviewedId == -1) { System.out.println("❌ Invalid User ID."); return; }

        System.out.print("Enter Rating (1-5): ");
        int rating = Validator.safeParseInt(scanner.nextLine());
        if (!Validator.isValidRating(rating)) { System.out.println("❌ Rating must be 1-5."); return; }

        System.out.print("Enter Comment: ");
        String comment = scanner.nextLine();

        if (feedbackService.leaveFeedback(sessionId, currentUserId, reviewedId, rating, comment)) {
            System.out.println("✅ Feedback submitted!");
        }
    }

    private void handleViewFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- FEEDBACK RECEIVED ---");
        List<Feedback> feedbackList = feedbackService.getFeedbackForUser(currentUserId);

        if (feedbackList != null && !feedbackList.isEmpty()) {
            for (Feedback fb : feedbackList) {
                System.out.println("Session ID: " + fb.getSessionId() +
                        " | Rating: " + fb.getRating() + "/5" +
                        " | Comment: " + fb.getComment() +
                        " | Date: " + fb.getFeedbackDate());
            }
        }
    }

    private void handleSkillWiseSummary() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SKILL-WISE FEEDBACK SUMMARY ---");
        List<String> summary = feedbackService.getSkillWiseFeedbackSummary(currentUserId);

        if (summary == null || summary.isEmpty()) {
            System.out.println("ℹ️ No skill-wise feedback yet.");
        } else {
            for (String line : summary) {
                System.out.println(line);
            }
        }
    }
}