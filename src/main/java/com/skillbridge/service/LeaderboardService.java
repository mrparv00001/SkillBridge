package com.skillbridge.service;

import com.skillbridge.database.DBConnection;
import com.skillbridge.model.LeaderboardEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class LeaderboardService {

    private static final String FILE_PATH = "leaderboard.txt";

    /**
     * Generates leaderboard using QUEUE Data Structure.
     * Rank users by credits (highest first), fetches all 10 required fields.
     */
    public Queue<LeaderboardEntry> buildLeaderboardQueue() {
        Queue<LeaderboardEntry> leaderboardQueue = new LinkedList<>();

        String sql = "SELECT u.user_id, u.full_name, u.department, u.semester, u.credits, u.is_active, " +
                "(SELECT COUNT(*) FROM LearningSessions ls WHERE ls.teacher_id = u.user_id) AS teaching_count, " +
                "(SELECT COUNT(*) FROM LearningSessions ls WHERE ls.learner_id = u.user_id) AS learning_count, " +
                "(SELECT COUNT(*) FROM LearningSessions ls WHERE (ls.teacher_id = u.user_id OR ls.learner_id = u.user_id) AND ls.status = 'Completed') AS completed_count, " +
                "(SELECT COALESCE(AVG(f.rating), 0) FROM Feedback f WHERE f.reviewed_user_id = u.user_id) AS avg_rating " +
                "FROM Users u WHERE u.is_active = true " +
                "ORDER BY u.credits DESC LIMIT 20";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                        rank++,
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("department"),
                        rs.getInt("semester"),
                        rs.getInt("credits"),
                        rs.getInt("teaching_count"),
                        rs.getInt("learning_count"),
                        rs.getInt("completed_count"),
                        rs.getDouble("avg_rating"),
                        rs.getBoolean("is_active")
                );
                leaderboardQueue.offer(entry); // Add to Queue
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leaderboard: " + e.getMessage());
        }

        return leaderboardQueue;
    }

    /**
     * Write leaderboard to TXT file using Queue.
     * Poll from queue (FIFO) and write to file.
     */
    public void generateLeaderboardFile() {
        Queue<LeaderboardEntry> queue = buildLeaderboardQueue();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("========================================\n");
            writer.write("       SKILLBRIDGE LEADERBOARD\n");
            writer.write("       (Top Peer Learners)\n");
            writer.write("========================================\n\n");

            if (queue.isEmpty()) {
                writer.write("No active users found.\n");
                return;
            }

            // Process Queue (FIFO)
            while (!queue.isEmpty()) {
                LeaderboardEntry entry = queue.poll();
                writer.write(entry.toFormattedString() + "\n");
                writer.write("----------------------------------------\n");
            }

            writer.write("\nGenerated using Queue Data Structure\n");

        } catch (IOException e) {
            System.err.println("File I/O Error writing leaderboard: " + e.getMessage());
        }
    }

    /**
     * Display leaderboard in console + also update file
     */
    public void displayLeaderboard() {
        generateLeaderboardFile();

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║        SKILLBRIDGE OVERALL STUDENTS RANKING             ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading leaderboard file: " + e.getMessage());
        }

        System.out.println("\nFile saved at: " + FILE_PATH);
    }
}