package com.skillbridge.menu;

import com.skillbridge.model.Skill;
import com.skillbridge.model.User;
import com.skillbridge.service.RecommendationService;
import com.skillbridge.service.SearchService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserSkillService;
import com.skillbridge.util.SessionManager;
import com.skillbridge.util.Validator;

import java.util.List;
import java.util.Scanner;

public class SkillMenu {

    private Scanner scanner;
    private SkillService skillService;
    private UserSkillService userSkillService;
    private SearchService searchService;
    private RecommendationService recommendationService;

    public SkillMenu() {
        this.scanner = new Scanner(System.in);
        this.skillService = new SkillService();
        this.userSkillService = new UserSkillService();
        this.searchService = new SearchService();
        this.recommendationService = new RecommendationService();
    }

    public void showMenu() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   SKILL MANAGEMENT & SEARCH");
            System.out.println("=================================");
            System.out.println("1. View All Skills");
            System.out.println("2. Create New Skill");
            System.out.println("3. Add Skill to Profile");
            System.out.println("4. Search by Department");
            System.out.println("5. Search by Semester");
            System.out.println("6. Get Smart Recommendations");
            System.out.println("7. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleViewAllSkills(); break;
                case "2": handleCreateNewSkill(); break;
                case "3": handleAddSkillToProfile(); break;
                case "4": handleSearchByDepartment(); break;
                case "5": handleSearchBySemester(); break;
                case "6": handleGetRecommendations(); break;
                case "7": running = false; break;
                default: System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void handleViewAllSkills() {
        System.out.println("\n--- ALL PLATFORM SKILLS ---");
        List<Skill> skills = skillService.getAllSkills();
        if (skills != null && !skills.isEmpty()) {
            for (Skill skill : skills) {
                System.out.println("ID: " + skill.getSkillId() + " | Name: " + skill.getSkillName() + " | Category: " + skill.getCategory());
            }
        }
    }

    private void handleCreateNewSkill() {
        System.out.println("\n--- CREATE A NEW SKILL ---");
        System.out.print("Enter Skill Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Category: ");
        String category = scanner.nextLine();

        System.out.print("Enter Description: ");
        String desc = scanner.nextLine();

        Skill newSkill = new Skill();
        newSkill.setSkillName(name);
        newSkill.setCategory(category);
        newSkill.setDescription(desc);

        skillService.addNewSkill(newSkill);
    }

    private void handleAddSkillToProfile() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- ADD SKILL TO PROFILE ---");

        System.out.print("Enter Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("Invalid Skill ID."); return; }

        // Skill Type Selection (Number-based)
        System.out.println("\nSelect Skill Type:");
        System.out.println("  1. Teaching");
        System.out.println("  2. Learning");
        System.out.print("Choose (1 or 2): ");
        int typeChoice = Validator.safeParseInt(scanner.nextLine());

        String type;
        if (typeChoice == 1) {
            type = "Teaching";
        } else if (typeChoice == 2) {
            type = "Learning";
        } else {
            System.out.println("Invalid choice. Please select 1 or 2.");
            return;
        }

        // Skill Level Selection (Number-based)
        System.out.println("\nSelect Skill Level:");
        System.out.println("  1. Beginner");
        System.out.println("  2. Intermediate");
        System.out.println("  3. Advanced");
        System.out.print("Choose (1, 2, or 3): ");
        int levelChoice = Validator.safeParseInt(scanner.nextLine());

        String level;
        if (levelChoice == 1) {
            level = "Beginner";
        } else if (levelChoice == 2) {
            level = "Intermediate";
        } else if (levelChoice == 3) {
            level = "Advanced";
        } else {
            System.out.println("Invalid choice. Please select 1, 2, or 3.");
            return;
        }

        if (userSkillService.addSkillToUser(currentUserId, skillId, type, level)) {
            System.out.println("Skill added to your profile!");
        }
    }

    private void handleSearchByDepartment() {
        System.out.println("\n--- SEARCH BY DEPARTMENT ---");
        System.out.print("Enter Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("❌ Invalid Skill ID."); return; }

        System.out.print("Enter Department: ");
        String dept = scanner.nextLine();

        List<User> results = searchService.searchStudentsByDepartment(dept, skillId);
        printSearchResults(results);
    }

    private void handleSearchBySemester() {
        System.out.println("\n--- SEARCH BY SEMESTER ---");
        System.out.print("Enter Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("❌ Invalid Skill ID."); return; }

        System.out.print("Enter Semester (1-8): ");
        int sem = Validator.safeParseInt(scanner.nextLine());
        if (!Validator.isValidSemester(sem)) {
            System.out.println("❌ Invalid Semester. Must be between 1 and 8.");
            return;
        }

        List<User> results = searchService.searchStudentsBySemester(sem, skillId);
        printSearchResults(results);
    }

    private void handleGetRecommendations() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SMART RECOMMENDATIONS ---");
        System.out.print("Enter Skill ID: ");
        int skillId = Validator.safeParseInt(scanner.nextLine());
        if (skillId == -1) { System.out.println("❌ Invalid Skill ID."); return; }

        List<User> recommendations = recommendationService.getRecommendedTeachers(currentUserId, skillId);
        if (recommendations != null && !recommendations.isEmpty()) {
            System.out.println("\n--- TOP MATCHES ---");
            for (User teacher : recommendations) {
                System.out.println("User ID: " + teacher.getUserId() + " | " + teacher.getFullName() + " | " + teacher.getDepartment() + " | Sem: " + teacher.getSemester());
            }
        }
    }

    private void printSearchResults(List<User> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("ℹ️ No teachers found.");
        } else {
            for (User teacher : results) {
                System.out.println("User ID: " + teacher.getUserId() + " | " + teacher.getFullName() + " | " + teacher.getDepartment() + " | Sem: " + teacher.getSemester());
            }
        }
    }
}