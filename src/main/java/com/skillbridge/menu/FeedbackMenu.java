package com.skillbridge.menu;

import com.skillbridge.model.Feedback;
import com.skillbridge.service.FeedbackService;
import com.skillbridge.util.SessionManager;

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
            System.out.println("1. Submit Feedback for a Completed Session");
            System.out.println("2. View All Feedback Received");
            System.out.println("3. View Skill-wise Feedback Summary");
            System.out.println("4. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleSubmitFeedback();
                    break;
                case "2":
                    handleViewFeedback();
                    break;
                case "3":
                    handleSkillWiseSummary();
                    break;
                case "4":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void handleSubmitFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SUBMIT FEEDBACK ---");

        System.out.print("Enter Session ID: ");
        int sessionId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Reviewed User's ID: ");
        int reviewedId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Rating (1 to 5): ");
        int rating = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter your Comment: ");
        String comment = scanner.nextLine();

        boolean success = feedbackService.submitFeedback(sessionId, currentUserId, reviewedId, rating, comment);
        if (success) {
            System.out.println("Thank you! Feedback submitted successfully.");
        }
    }

    private void handleViewFeedback() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- FEEDBACK RECEIVED ---");
        List<Feedback> feedbackList = feedbackService.getFeedbackForUser(currentUserId);

        if (feedbackList.isEmpty()) {
            System.out.println("No feedback found for your profile yet.");
        } else {
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

        if (summary.isEmpty()) {
            System.out.println("No skill-wise feedback available yet.");
        } else {
            for (String line : summary) {
                System.out.println(line);
            }
        }
    }
}