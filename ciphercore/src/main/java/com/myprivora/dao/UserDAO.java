package com.myprivora.dao;

import java.util.ArrayList;
import java.util.List;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

public class UserDAO {

    private final Firestore db = DatabaseConfig.getFirestore();

    // =========================================================
    // CREATE USER
    // =========================================================
    public boolean saveUser(User user) {

        try {

            db.collection("Users")
                    .document(user.getUserId())
                    .create(user)
                    .get();

            System.out.println("User registered successfully.");

            return true;

        } catch (Exception e) {

            System.out.println("Error while registering user.");
            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // GET USER BY EMAIL
    // =========================================================
    public User getUserByEmail(String email) {

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("Users")
                            .whereEqualTo("email", email)
                            .get();

            QuerySnapshot snapshot = future.get();

            if (!snapshot.isEmpty()) {

                DocumentSnapshot document =
                        snapshot.getDocuments().get(0);

                User user =
                        document.toObject(User.class);

                if (user != null) {

                    // Firestore document ID = Firebase UID
                    user.setUserId(document.getId());
                }

                return user;
            }

        } catch (Exception e) {

            System.out.println(
                    "Error while finding user by email."
            );

            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // GET USER BY ID
    // =========================================================
    public User getUserById(String userId) {

        try {

            DocumentSnapshot document =
                    db.collection("Users")
                            .document(userId)
                            .get()
                            .get();

            if (document.exists()) {

                User user =
                        document.toObject(User.class);

                if (user != null) {

                    // IMPORTANT:
                    // Always use Firestore document ID
                    // as the user's UID.
                    user.setUserId(document.getId());
                }

                return user;
            }

        } catch (Exception e) {

            System.out.println(
                    "Error while finding user by ID."
            );

            e.printStackTrace();
        }

        return null;
    }

    // =========================================================
    // UPDATE USER
    // =========================================================
    public boolean updateUser(User user) {

        try {

            db.collection("Users")
                    .document(user.getUserId())
                    .set(user)
                    .get();

            System.out.println(
                    "User updated successfully."
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Error while updating user."
            );

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // DELETE USER
    // =========================================================
    public boolean deleteUser(String userId) {

        try {

            db.collection("Users")
                    .document(userId)
                    .delete()
                    .get();

            System.out.println(
                    "User deleted successfully."
            );

            return true;

        } catch (Exception e) {

            System.out.println(
                    "Error while deleting user."
            );

            e.printStackTrace();

            return false;
        }
    }

    // =========================================================
    // GET ALL USERS
    // =========================================================
    public List<User> getAllUsers() {

        List<User> users =
                new ArrayList<>();

        try {

            ApiFuture<QuerySnapshot> future =
                    db.collection("Users")
                            .get();

            QuerySnapshot snapshot =
                    future.get();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                User user =
                        document.toObject(User.class);

                if (user != null) {

                    // IMPORTANT:
                    // Restore UID from document ID.
                    user.setUserId(
                            document.getId()
                    );

                    users.add(user);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error while fetching users."
            );

            e.printStackTrace();
        }

        return users;
    }
}