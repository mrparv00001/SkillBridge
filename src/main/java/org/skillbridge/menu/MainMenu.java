package com.skillbridge.menu;

import com.skillbridge.model.User;
import com.skillbridge.service.AuthenticationService;

import java.util.Scanner;

public class MainMenu {

    private Scanner scanner;
    private AuthenticationService authService;
    private DashboardMenu dashboardMenu; // The next screen

    public MainMenu() {
        this.scanner = new Scanner(System.in);
        this.authService = new AuthenticationService();
        this.dashboardMenu = new DashboardMenu();
    }

    // 1. The main loop that keeps the menu running
    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=================================");
            System.out.println("   WELCOME TO SKILLBRIDGE");
            System.out.println("=================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit Application");
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
                    System.out.println("Thank you for using SkillBridge. Goodbye!");
                    running = false; // Breaks the loop and closes the app
                    break;
                default:
                    System.out.println("Invalid choice. Please type 1, 2, or 3.");
            }
        }
    }

    // 2. Collects email and password, then tries to log in
    private void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        // Ask the service to check the credentials
        boolean isSuccess = authService.login(email, password);

        if (isSuccess) {
            // If login works, send them to the Dashboard screen!
            dashboardMenu.showDashboard();
        }
    }

    // 3. Collects all details to create a new account
    private void handleRegister() {
        System.out.println("\n--- REGISTER NEW ACCOUNT ---");

        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Enrollment Number: ");
        String enrollmentNo = scanner.nextLine();

        System.out.print("Enter Department (e.g., Computer Science): ");
        String dept = scanner.nextLine();

        System.out.print("Enter Semester (e.g., 3): ");
        int semester = 0;
        try {
            semester = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid semester. Setting to 1 by default.");
            semester = 1;
        }

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Password (min 6 characters): ");
        String password = scanner.nextLine();

        System.out.print("Enter Phone Number (10 digits): ");
        String phone = scanner.nextLine();

        System.out.print("Write a short Bio about yourself: ");
        String bio = scanner.nextLine();

        // Pack all answers into a new User form
        User newUser = new User(name, enrollmentNo, dept, semester, email, password, phone, bio);

        // Ask the service to save the new user
        boolean isSuccess = authService.register(newUser);

        if (isSuccess) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }
}