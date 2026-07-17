package com.skillbridge.util;

import com.skillbridge.model.User;

public class SessionManager {

    // This variable remembers the currently logged-in user.
    // 'static' means there is only ONE memory for the whole application.
    private static User currentUser = null;

    // We make the constructor private so nobody can create an object of SessionManager.
    // We only want to use its methods directly.
    private SessionManager() {
    }

    // Called when a user successfully logs in
    public static void login(User user) {
        currentUser = user;
    }

    // Called when the user chooses to log out
    public static void logout() {
        currentUser = null;
    }

    // Used to get the details of the user who is currently logged in
    public static User getCurrentUser() {
        return currentUser;
    }

    // A quick way to check if ANY user is currently logged in
    public static boolean isLoggedIn() {
        if (currentUser != null) {
            return true; // Someone is logged in
        } else {
            return false; // No one is logged in
        }
    }
}