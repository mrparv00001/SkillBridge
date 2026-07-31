package com.skillbridge.menu;

import com.skillbridge.model.Skill;
import com.skillbridge.model.User;
import com.skillbridge.service.RecommendationService;
import com.skillbridge.service.SearchService;
import com.skillbridge.service.SkillService;
import com.skillbridge.service.UserSkillService;
import com.skillbridge.util.SessionManager;

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
            System.out.println("1. View All Platform Skills");
            System.out.println("2. Create a Brand New Skill");
            System.out.println("3. Add Skill to My Profile");
            System.out.println("4. Search Students by Department");
            System.out.println("5. Search Students by Semester");
            System.out.println("6. Get Smart Teacher Recommendations"); // UPGRADED: Added feature
            System.out.println("7. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            // UPGRADED: Prevents app crash on bad input
            try {
                switch (choice) {
                    case "1": handleViewAllSkills(); break;
                    case "2": handleCreateNewSkill(); break;
                    case "3": handleAddSkillToProfile(); break;
                    case "4": handleSearchByDepartment(); break;
                    case "5": handleSearchBySemester(); break;
                    case "6": handleGetRecommendations(); break;
                    case "7": running = false; break;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input detected. Please enter numbers where required.");
            }
        }
    }

    private void handleViewAllSkills() {
        System.out.println("\n--- ALL PLATFORM SKILLS ---");
        List<Skill> skills = skillService.getAllSkills();
        if (skills == null || skills.isEmpty()) {
            System.out.println("No skills available yet. You should create one!");
        } else {
            for (Skill skill : skills) {
                System.out.println("ID: " + skill.getSkillId() + " | Name: " + skill.getSkillName() + " | Category: " + skill.getCategory());
            }
        }
    }

    private void handleCreateNewSkill() {
        System.out.println("\n--- CREATE A NEW SKILL ---");
        System.out.print("Enter Skill Name (e.g., Python): ");
        String name = scanner.nextLine();

        System.out.print("Enter Category (e.g., Programming): ");
        String category = scanner.nextLine();

        System.out.print("Enter short Description: ");
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

        System.out.print("Enter Skill ID you want to add: ");
        int skillId = Integer.parseInt(scanner.nextLine());

        System.out.print("Skill Type (Teaching / Learning): ");
        String type = scanner.nextLine();

        System.out.print("Skill Level (Beginner / Intermediate / Advanced): ");
        String level = scanner.nextLine();

        boolean success = userSkillService.addSkillToUser(currentUserId, skillId, type, level);
        if (success) {
            System.out.println("Skill added to your profile successfully!");
        } else {
            System.out.println("Failed to add skill. Maybe you already added it?");
        }
    }

    private void handleSearchByDepartment() {
        System.out.println("\n--- SEARCH BY DEPARTMENT ---");
        System.out.print("Enter Skill ID you want to learn: ");
        int skillId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Department Name (e.g., Computer Science): ");
        String dept = scanner.nextLine();

        List<User> results = searchService.searchStudentsByDepartment(dept, skillId);
        printSearchResults(results);
    }

    private void handleSearchBySemester() {
        System.out.println("\n--- SEARCH BY SEMESTER ---");
        System.out.print("Enter Skill ID you want to learn: ");
        int skillId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter Semester Number (e.g., 3): ");
        int sem = Integer.parseInt(scanner.nextLine());

        List<User> results = searchService.searchStudentsBySemester(sem, skillId);
        printSearchResults(results);
    }

    // UPGRADED: Calling Member 2's Recommendation Engine
    private void handleGetRecommendations() {
        int currentUserId = SessionManager.getCurrentUser().getUserId();
        System.out.println("\n--- SMART TEACHER RECOMMENDATIONS ---");
        System.out.print("Enter the Skill ID you want to learn: ");
        int skillId = Integer.parseInt(scanner.nextLine());

        System.out.println("Analyzing ratings, semesters, and departments...");
        List<User> recommendations = recommendationService.getRecommendedTeachers(currentUserId, skillId);

        if (recommendations != null && !recommendations.isEmpty()) {
            System.out.println("\n--- TOP MATCHES ---");
            for (User teacher : recommendations) {
                System.out.println("User ID: " + teacher.getUserId() + " | Name: " + teacher.getFullName() +
                        " | Dept: " + teacher.getDepartment() + " | Sem: " + teacher.getSemester());
            }
        }
    }

    private void printSearchResults(List<User> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("No active students found teaching this skill.");
        } else {
            System.out.println("\n--- TEACHERS FOUND ---");
            for (User teacher : results) {
                System.out.println("User ID: " + teacher.getUserId() + " | Name: " + teacher.getFullName() +
                        " | Dept: " + teacher.getDepartment() + " | Sem: " + teacher.getSemester());
            }
        }
    }
}