package com.skillbridge.service;

import com.skillbridge.dao.NotificationDAO;
import com.skillbridge.model.Notification;
import com.skillbridge.database.DBConnection;

import java.util.List;
import java.util.Stack;

public class NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    public void sendNotification(int userId, String message, String type) {
        if (DBConnection.isLocalhost()) {
            // XAMPP: Only send for events that have NO trigger
            if (type.equals("REQUEST_ACCEPTED") ||
                    type.equals("REQUEST_REJECTED") ||
                    type.equals("REQUEST_CANCELLED")) {
                notificationDAO.createNotification(userId, message, type);
            }
            // Session events → Trigger handles automatically
            // Request sent → Trigger handles automatically
        } else {
            // TiDB: No triggers — Java handles ALL
            notificationDAO.createNotification(userId, message, type);
        }
    }

    public Stack<Notification> getUnseenStack(int userId) {
        List<Notification> unreadList = notificationDAO.getUnreadNotifications(userId);
        Stack<Notification> unseenStack = new Stack<>();
        for (int i = unreadList.size() - 1; i >= 0; i--) {
            unseenStack.push(unreadList.get(i));
        }
        return unseenStack;
    }

    public Stack<Notification> getSeenStack(int userId) {
        List<Notification> readList = notificationDAO.getSeenNotifications(userId);
        Stack<Notification> seenStack = new Stack<>();
        for (int i = readList.size() - 1; i >= 0; i--) {
            seenStack.push(readList.get(i));
        }
        return seenStack;
    }

    public boolean markAsSeen(int notificationId) {
        return notificationDAO.markAsRead(notificationId);
    }

    public boolean markAllAsSeen(int userId) {
        return notificationDAO.markAllAsRead(userId);
    }

    public int getUnseenCount(int userId) {
        return notificationDAO.getUnreadCount(userId);
    }
}