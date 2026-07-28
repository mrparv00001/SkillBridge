package com.skillbridge.dao;

import com.skillbridge.model.ExchangeRequest;
import com.skillbridge.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRequestDAO {

    public boolean createRequest(ExchangeRequest request) {
        String sql = "INSERT INTO ExchangeRequests (sender_id, receiver_id, requested_skill_id, exchange_type, message, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getSenderId());
            stmt.setInt(2, request.getReceiverId());
            stmt.setInt(3, request.getRequestedSkillId());
            stmt.setString(4, request.getExchangeType());
            stmt.setString(5, request.getMessage());
            stmt.setString(6, "Pending");
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int requestId, String status) {
        String sql = "UPDATE ExchangeRequests SET status = ? WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, requestId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ExchangeRequest getRequestById(int requestId) {
        String sql = "SELECT * FROM ExchangeRequests WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToExchangeRequest(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ExchangeRequest> getRequestsByUserId(int userId) {
        List<ExchangeRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM ExchangeRequests WHERE sender_id = ? OR receiver_id = ? ORDER BY request_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(mapResultSetToExchangeRequest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    private ExchangeRequest mapResultSetToExchangeRequest(ResultSet rs) throws SQLException {
        return new ExchangeRequest(
                rs.getInt("request_id"),
                rs.getInt("sender_id"),
                rs.getInt("receiver_id"),
                rs.getInt("requested_skill_id"),
                rs.getString("exchange_type"),
                rs.getString("message"),
                rs.getString("status"),
                rs.getTimestamp("request_date")
        );
    }
}