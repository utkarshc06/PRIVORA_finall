package com.myprivora.session;

import com.myprivora.model.User;

public final class SessionManager {

    private static User currentUser;

    private SessionManager() {
    }

    // =========================================================
    // SET CURRENT USER
    // =========================================================

    public static synchronized void setCurrentUser(User user) {
        currentUser = user;
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    public static synchronized User getCurrentUser() {
        return currentUser;
    }

    // =========================================================
    // GET USER ID
    // =========================================================

    public static synchronized String getUserId() {

        if (currentUser == null) {
            return null;
        }

        return currentUser.getUserId();
    }

    // =========================================================
    // GET NAME
    // =========================================================

    public static synchronized String getName() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getName();
    }

    // =========================================================
    // GET EMAIL
    // =========================================================

    public static synchronized String getEmail() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getEmail();
    }

    // =========================================================
    // GET ROLE
    // =========================================================

    public static synchronized String getRole() {

        if (currentUser == null) {
            return "";
        }

        return currentUser.getRole();
    }

    // =========================================================
    // CHECK LOGIN
    // =========================================================

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    public static synchronized void clear() {
        currentUser = null;
    }
}