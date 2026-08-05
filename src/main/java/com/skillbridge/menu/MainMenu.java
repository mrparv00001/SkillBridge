package com.skillbridge.menu;

import com.skillbridge.model.User;
import com.skillbridge.service.AuthenticationService;
import com.skillbridge.util.Validator;

import java.util.Scanner;

public class MainMenu {

    private Scanner scanner;
    private AuthenticationService authService;
    private DashboardMenu dashboardMenu;
    private ManagerMenu managerMenu;

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthenticationService();
        this.dashboardMenu = new DashboardMenu();
        this.managerMenu = new ManagerMenu();
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   WELCOME TO SKILLBRIDGE");
            System.out.println("=================================");
            System.out.println("1. Login (Student)");
            System.out.println("2. Register (Student)");
            System.out.println("3. Manager Portal");
            System.out.println("4. Exit Application");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    managerMenu.start();
                    break;
                case "4":
                    System.out.println("==========================================");
                    System.out.println(" Thank you for using SkillBridge. Goodbye!");
                    System.out.println("==========================================");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please type 1, 2, 3, or 4.");
            }
        }
    }

    private void handleLogin() {
        System.out.println("\n--- STUDENT LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        boolean isSuccess = authService.login(email, password);

        if (isSuccess) {
            dashboardMenu.showDashboard();
        }
    }

    private void handleRegister() {
        System.out.println("\n--- REGISTER NEW STUDENT ACCOUNT ---");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Enrollment Number: ");
        String enrollmentNo = scanner.nextLine();

        System.out.print("Enter Department (e.g., Computer Science): ");
        String dept = scanner.nextLine();

        System.out.print("Enter Semester (1-8): ");
        int semester = Validator.safeParseInt(scanner.nextLine());
        if (!Validator.isValidSemester(semester)) {
            System.out.println("Invalid semester. Must be between 1 and 8.");
            return;
        }

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password (min 6 characters): ");
        String password = scanner.nextLine();

        System.out.print("Enter Phone Number (10 digits): ");
        String phone = scanner.nextLine();

        System.out.print("Write a short Bio about yourself: ");
        String bio = scanner.nextLine();

        User newUser = new User(name, enrollmentNo, dept, semester, email, password, phone, bio);

        boolean isSuccess = authService.register(newUser);

        if (isSuccess) {
            System.out.println("\nRegistration successful! You start with 50 credits.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }
}