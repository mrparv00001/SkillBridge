package com.skillbridge.service;

import com.skillbridge.dao.UserDAO;
import com.skillbridge.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LeaderboardService {

    private static final String FILE_PATH = "leaderboard.txt";
    private final UserDAO userDAO = new UserDAO();

    public void generateLeaderboardFile() {
        List<User> topUsers = userDAO.getTopUsersByCredits(10);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("🏆 SKILLBRIDGE LEADERBOARD (Top 10 Earners) 🏆\n");
            writer.write("-------------------------------------------------\n");

            int rank = 1;
            for (User user : topUsers) {
                String line = String.format("%d. %s - %s (%d Credits)\n",
                        rank, user.getFullName(), user.getDepartment(), user.getCredits());
                writer.write(line);
                rank++;
            }
        } catch (IOException e) {
            System.err.println("❌ File I/O Error writing leaderboard: " + e.getMessage());
        }
    }

    public void displayLeaderboard() {
        generateLeaderboardFile();

        System.out.println("\n=================================");
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("❌ File I/O Error reading leaderboard: " + e.getMessage());
        }
        System.out.println("=================================\n");
    }
}