package com.skillbridge.menu;

import com.skillbridge.model.User;
import com.skillbridge.service.AuthenticationService;
import com.skillbridge.service.UserService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import com.skillbridge.model.Skill;
import com.skillbridge.model.UserSkill;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserSkillService;
import com.skillbridge.service.LeaderboardService;
import java.util.List;
import java.util.Scanner;

public class DashboardMenu {

    private Scanner scanner;
    private UserService userService;
    private AuthenticationService authService;
    private LeaderboardService leaderboardService;

    private SkillMenu skillMenu;
    private SessionMenu sessionMenu;
    private ExchangeRequestMenu requestMenu;
    private FeedbackMenu feedbackMenu;

    public DashboardMenu() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.authService = new AuthenticationService();
        this.leaderboardService = new LeaderboardService();

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

            System.out.println("\n=================================");
            System.out.println("   DASHBOARD - " + currentUser.getFullName().toUpperCase());
            System.out.println("   💳 Credits Available: " + currentUser.getCredits());
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
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void viewProfile() {
        System.out.println("\n--- MY PROFILE ---");
        User user = userService.getUserProfile(SessionManager.getCurrentUser().getUserId());
        System.out.println(user.toString());

        System.out.println("\n--- MY SKILLS ---");
        UserSkillService userSkillService = new UserSkillService();
        SkillService skillService = new SkillService();

        List<UserSkill> mySkills = userSkillService.getUserSkills(user.getUserId());

        if (mySkills == null || mySkills.isEmpty()) {
            System.out.println("ℹ️ No skills added yet. Go to Skill Menu to add skills!");
        } else {
            List<Skill> allSkills = skillService.getAllSkills();
            System.out.println("Total Skills: " + mySkills.size());
            System.out.println("---------------------------------");

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
                System.out.println("• " + skillName +
                        " | Type: " + us.getSkillType() +
                        " | Level: " + us.getSkillLevel());
            }
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
                System.out.println("❌ Invalid semester. Must be between 1 and 8. Skipping.");
            }
        }

        System.out.print("Enter New Phone (10 digits): ");
        String phone = scanner.nextLine();
        if (!phone.trim().isEmpty()) {
            if (Validator.isValidPhone(phone)) {
                currentUser.setPhone(phone);
            } else {
                System.out.println("❌ Invalid phone number. Skipping phone update.");
            }
        }

        System.out.print("Enter New Bio: ");
        String bio = scanner.nextLine();
        if (!bio.trim().isEmpty()) currentUser.setBio(bio);

        userService.updateUserProfile(currentUser);
    }
}