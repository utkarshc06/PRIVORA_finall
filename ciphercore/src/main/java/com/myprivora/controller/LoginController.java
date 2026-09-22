package com.myprivora.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import com.myprivora.dao.UserDAO;
import com.myprivora.model.Session;
import com.myprivora.model.User;
import com.myprivora.session.SessionManager;

public class LoginController {

    /*
     * Keep your existing Firebase Web API key here.
     *
     * Do NOT share the key publicly.
     */
    private final String API_KEY =
            "AIz";

    private final UserDAO userDAO =
            new UserDAO();


    // =========================================================
    // LOGIN
    // =========================================================

    public boolean login(
            String email,
            String password,
            String selectedRole) {

        // =====================================================
        // VALIDATION
        // =====================================================

        if (email == null
                || email.trim().isEmpty()
                || password == null
                || password.isEmpty()
                || selectedRole == null
                || selectedRole.trim().isEmpty()) {

            System.out.println(
                    "[LoginController] Email, password and role are required."
            );

            return false;
        }


        JSONObject payload =
                new JSONObject();


        payload.put(
                "email",
                email.trim()
        );


        payload.put(
                "password",
                password
        );


        payload.put(
                "returnSecureToken",
                true
        );


        try {

            // =================================================
            // FIREBASE AUTHENTICATION
            // =================================================

            HttpClient client =
                    HttpClient.newHttpClient();


            URI uri =
                    URI.create(
                            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
                                    + API_KEY
                    );


            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(uri)
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    payload.toString()
                                            )
                            )
                            .build();


            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );


            System.out.println(
                    "[LoginController] Authentication status = "
                            + response.statusCode()
            );


            // =================================================
            // AUTHENTICATION FAILED
            // =================================================

            if (response.statusCode() != 200) {

                System.out.println(
                        "[LoginController] Authentication failed."
                );

                System.out.println(
                        response.body()
                );

                return false;
            }


            // =================================================
            // GET FIREBASE UID
            // =================================================

            JSONObject result =
                    new JSONObject(
                            response.body()
                    );


            String uid =
                    result.getString(
                            "localId"
                    );


            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "[LoginController] FIREBASE LOGIN UID"
            );

            System.out.println(
                    "Firebase UID = "
                            + uid
            );


            // =================================================
            // GET USER FROM FIRESTORE
            // =================================================

            User user =
                    userDAO.getUserById(
                            uid
                    );


            if (user == null) {

                System.out.println(
                        "[LoginController] User data not found in Firestore."
                );

                return false;
            }


            // =================================================
            // DEBUG FIRESTORE USER
            // =================================================

            System.out.println(
                    "[LoginController] Firestore User ID = "
                            + user.getUserId()
            );

            System.out.println(
                    "[LoginController] Firestore User Name = "
                            + user.getName()
            );

            System.out.println(
                    "[LoginController] Firestore User Email = "
                            + user.getEmail()
            );

            System.out.println(
                    "[LoginController] Firestore User Role = "
                            + user.getRole()
            );

            System.out.println(
                    "[LoginController] Selected Login Role = "
                            + selectedRole
            );


            // =================================================
            // ROLE CHECK
            // =================================================

            if (user.getRole() == null
                    || !user.getRole()
                            .equalsIgnoreCase(
                                    selectedRole.trim()
                            )) {

                System.out.println(
                        "[LoginController] WRONG ROLE SELECTED."
                );

                System.out.println(
                        "Account Role = "
                                + user.getRole()
                );

                System.out.println(
                        "Selected Role = "
                                + selectedRole
                );

                return false;
            }


            // =================================================
            // CREATE SESSION
            // =================================================

            Session session =
                    new Session(
                            user.getUserId(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole()
                    );


            // =================================================
            // SAVE CURRENT USER
            // =================================================

            SessionManager.setCurrentUser(
                    user
            );


            // =================================================
            // VERIFY SESSION
            // =================================================

            System.out.println(
                    "--------------------------------------"
            );

            System.out.println(
                    "[LoginController] SESSION CREATED"
            );

            System.out.println(
                    "Session UID = "
                            + SessionManager.getUserId()
            );

            System.out.println(
                    "Session Name = "
                            + SessionManager.getName()
            );

            System.out.println(
                    "Session Email = "
                            + SessionManager.getEmail()
            );

            System.out.println(
                    "Session Role = "
                            + SessionManager.getRole()
            );

            System.out.println(
                    "======================================"
            );


            // =================================================
            // FINAL UID CHECK
            // =================================================

            if (!uid.equals(
                    SessionManager.getUserId()
            )) {

                System.err.println(
                        "[LoginController] WARNING: Firebase UID and Session UID differ!"
                );

                System.err.println(
                        "Firebase UID = "
                                + uid
                );

                System.err.println(
                        "Session UID = "
                                + SessionManager.getUserId()
                );

                return false;
            }


            System.out.println(
                    "[LoginController] LOGIN SUCCESSFUL"
            );


            return true;


        } catch (Exception e) {

            System.err.println(
                    "[LoginController] Login error:"
            );

            e.printStackTrace();

            return false;
        }
    }
}