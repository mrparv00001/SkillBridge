package com.skillbridge.menu;

import com.skillbridge.model.User;
import com.skillbridge.model.Notification;
import com.skillbridge.model.Skill;
import com.skillbridge.model.UserSkill;
import com.skillbridge.service.AuthenticationService;
import com.skillbridge.service.UserService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserSkillService;
import com.skillbridge.service.LeaderboardService;
import com.skillbridge.service.NotificationService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class DashboardMenu {

    private Scanner scanner;
    private UserService userService;
    private AuthenticationService authService;
    private LeaderboardService leaderboardService;
    private NotificationService notificationService;

    private SkillMenu skillMenu;
    private SessionMenu sessionMenu;
    private ExchangeRequestMenu requestMenu;
    private FeedbackMenu feedbackMenu;

    public DashboardMenu() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.authService = new AuthenticationService();
        this.leaderboardService = new LeaderboardService();
        this.notificationService = new NotificationService();

        this.skillMenu = new SkillMenu();
        this.sessionMenu = new SessionMenu();
        this.requestMenu = new ExchangeRequestMenu();
        this.feedbackMenu = new FeedbackMenu();
    }

    public void showDashboard() {
        boolean loggedIn = true;

        while (loggedIn && SessionManager.isLoggedIn()) {
            User currentUser = userService.getUserProfile(SessionManager.getCurrentUser().getUserId());
            SessionManager.login(currentUser);

            showNotificationPanel(currentUser.getUserId());

            int unreadCount = notificationService.getUnseenCount(currentUser.getUserId());
            String badge = unreadCount > 0 ? " | Notifications: " + unreadCount + " NEW" : "";

            System.out.println("\n=================================");
            System.out.println("   DASHBOARD - " + currentUser.getFullName().toUpperCase());
            System.out.println("   Credits: " + currentUser.getCredits() + badge);
            System.out.println("=================================");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Skill Management & Search");
            System.out.println("4. Exchange Requests");
            System.out.println("5. Learning Sessions");
            System.out.println("6. Feedback & Ratings");
            System.out.println("7. View Global Leaderboard");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": viewProfile(); break;
                case "2": updateProfile(); break;
                case "3": skillMenu.showMenu(); break;
                case "4": requestMenu.showMenu(); break;
                case "5": sessionMenu.showMenu(); break;
                case "6": feedbackMenu.showMenu(); break;
                case "7": leaderboardService.displayLeaderboard(); break;
                case "8":
                    authService.logout();
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void showNotificationPanel(int userId) {
        boolean viewing = true;

        while (viewing) {
            Stack<Notification> unseenStack = notificationService.getUnseenStack(userId);

            if (unseenStack.isEmpty()) {
                return;
            }

            List<Notification> displayList = new ArrayList<>();
            System.out.println("\n===============================================");
            System.out.println("   NOTIFICATIONS (" + unseenStack.size() + " unseen)");
            System.out.println("===============================================");

            int count = 1;
            while (!unseenStack.isEmpty()) {
                Notification notif = unseenStack.pop();
                displayList.add(notif);
                String timeAgo = getTimeAgo(notif.getCreatedAt());
                System.out.println(count + ". " + notif.getMessage() + " - " + timeAgo);
                System.out.println();
                count++;
            }

            System.out.println("===============================================");
            System.out.println("1. Mark specific as SEEN (Enter number)");
            System.out.println("2. Mark ALL as SEEN");
            System.out.println("3. Continue to Dashboard");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter notification number: ");
                    int num = Validator.safeParseInt(scanner.nextLine());
                    if (num >= 1 && num <= displayList.size()) {
                        Notification selected = displayList.get(num - 1);
                        if (notificationService.markAsSeen(selected.getNotificationId())) {
                            System.out.println("Notification #" + num + " removed.");
                        }
                    } else {
                        System.out.println("Invalid number.");
                    }
                    break;

                case "2":
                    notificationService.markAllAsSeen(userId);
                    System.out.println("All notifications cleared.");
                    viewing = false;
                    break;

                case "3":
                    viewing = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private String getTimeAgo(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "Unknown time";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        long minutes = java.time.Duration.between(dateTime, now).toMinutes();
        long hours = java.time.Duration.between(dateTime, now).toHours();
        long days = java.time.Duration.between(dateTime, now).toDays();

        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        if (hours < 24) return hours + " hours ago";
        if (days < 7) return days + " days ago";
        if (days < 30) return (days / 7) + " weeks ago";
        return (days / 30) + " months ago";
    }

    private void viewProfile() {
        System.out.println("\n--- MY PROFILE ---");
        User user = userService.getUserProfile(SessionManager.getCurrentUser().getUserId());
        System.out.println("User ID         : " + user.getUserId());
        System.out.println("Name            : " + user.getFullName());
        System.out.println("Enrollment No   : " + user.getEnrollmentNo());
        System.out.println("Department      : " + user.getDepartment());
        System.out.println("Semester        : " + user.getSemester());
        System.out.println("Email           : " + user.getEmail());
        System.out.println("Phone           : " + user.getPhone());
        System.out.println("Bio             : " + user.getBio());
        System.out.println("Credits         : " + user.getCredits());
        System.out.println("Status          : " + (user.isActive() ? "Active" : "Inactive"));

        System.out.println("\n--- MY SKILLS ---");
        UserSkillService userSkillService = new UserSkillService();
        SkillService skillService = new SkillService();
        List<UserSkill> mySkills = userSkillService.getUserSkills(user.getUserId());

        if (mySkills == null || mySkills.isEmpty()) {
            System.out.println("No skills added yet.");
        } else {
            List<Skill> allSkills = skillService.getAllSkills();
            System.out.println("Total Skills: " + mySkills.size());
            System.out.printf("%-4s %-22s %-12s %-12s%n",
                    "No", "Skill Name", "Type", "Level");
            System.out.println("--------------------------------------------------");
            int count = 1;
            for (UserSkill us : mySkills) {
                String skillName = "Unknown";
                if (allSkills != null) {
                    for (Skill s : allSkills) {
                        if (s.getSkillId() == us.getSkillId()) {
                            skillName = s.getSkillName();
                            break;
                        }
                    }
                }
                System.out.printf("%-4d %-22s %-12s %-12s%n",
                        count, skillName, us.getSkillType(), us.getSkillLevel());
                count++;
            }
            System.out.println("--------------------------------------------------");
        }
    }

    private void updateProfile() {
        System.out.println("\n--- UPDATE PROFILE ---");
        User currentUser = SessionManager.getCurrentUser();

        System.out.print("Enter New Full Name (or press Enter to keep current): ");
        String name = scanner.nextLine();
        if (!name.trim().isEmpty()) currentUser.setFullName(name);

        System.out.print("Enter New Department: ");
        String dept = scanner.nextLine();
        if (!dept.trim().isEmpty()) currentUser.setDepartment(dept);

        System.out.print("Enter New Semester (1-8): ");
        String semInput = scanner.nextLine();
        if (!semInput.trim().isEmpty()) {
            int sem = Validator.safeParseInt(semInput);
            if (Validator.isValidSemester(sem)) {
                currentUser.setSemester(sem);
            } else {
                System.out.println("Invalid semester. Skipping.");
            }
        }

        System.out.print("Enter New Phone (10 digits): ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) {
            if (Validator.isValidPhone(phone)) {
                currentUser.setPhone(phone);
            } else {
                System.out.println("Invalid phone. Skipping.");
            }
        }

        System.out.print("Enter New Bio: ");
        String bio = scanner.nextLine();
        if (!bio.trim().isEmpty()) currentUser.setBio(bio);

        userService.updateUserProfile(currentUser);
    }
}