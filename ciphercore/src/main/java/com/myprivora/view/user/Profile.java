package com.myprivora.view.user;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.session.SessionManager;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Profile {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String BACKGROUND = "#080C16";
    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00B4D8";
    private final String PURPLE_LIGHT = "#00F0FF";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY = "rgba(255, 255, 255, 0.70)";

    private final String INPUT = "rgba(14, 22, 40, 0.80)";

    // =========================================================
    // FIRESTORE LISTENER
    // =========================================================

    private ListenerRegistration profileListener;

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private Label profileNameLabel;
    private Label profileEmailLabel;
    private Label initialsLabel;

    private TextField nameField;
    private TextField emailField;
    private TextField mobileField;
    private TextField cityField;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox content = new VBox(25);

        content.setPadding(
                new Insets(35, 40, 50, 40)
        );

        content.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        // =====================================================
        // PAGE TITLE
        // =====================================================

        Label title = new Label(
                "My profile"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Manage your personal information and security."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 16px;"
        );

        VBox heading = new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );

        // =====================================================
        // PROFILE CARD
        // =====================================================

        VBox profileCard = createProfileCard();

        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personalCard = createPersonalInformation();

        // =====================================================
        // SECURITY
        // =====================================================

        VBox securityCard = createSecurityCard();

        // =====================================================
        // ADD ALL
        // =====================================================

        content.getChildren().addAll(
                heading,
                profileCard,
                personalCard,
                securityCard
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane = new ScrollPane();

        scrollPane.setContent(content);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle("""
                -fx-background-color: #0B0A0F;
                -fx-background: #0B0A0F;
                -fx-border-color: transparent;
                """);

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setCenter(scrollPane);

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );

        VBox finalContent = new VBox();

        finalContent.getChildren().add(root);

        VBox.setVgrow(
                root,
                javafx.scene.layout.Priority.ALWAYS
        );

        // =====================================================
        // START REAL-TIME FIRESTORE
        // =====================================================

        startRealtimeProfile();

        return finalContent;
    }

    // =========================================================
    // REAL-TIME PROFILE
    // =========================================================

    private void startRealtimeProfile() {

        String userId = SessionManager.getUserId();

        if (userId == null || userId.trim().isEmpty()) {

            System.out.println(
                    "[Profile] No logged-in user found."
            );

            return;
        }

        System.out.println(
                "[Profile] Starting real-time listener for UID: "
                        + userId
        );

        try {

            // Prevent duplicate listener
            stopRealtimeProfile();

            profileListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("Users")
                            .document(userId)
                            .addSnapshotListener(
                                    (snapshot, error) -> {

                                        // =================================================
                                        // FIRESTORE ERROR
                                        // =================================================

                                        if (error != null) {

                                            System.err.println(
                                                    "[Profile] Firestore listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }

                                        // =================================================
                                        // DOCUMENT NOT FOUND
                                        // =================================================

                                        if (snapshot == null
                                                || !snapshot.exists()) {

                                            System.out.println(
                                                    "[Profile] User document not found."
                                            );

                                            return;
                                        }

                                        // =================================================
                                        // READ FIRESTORE DATA
                                        // =================================================

                                        String name =
                                                snapshot.getString("name");

                                        String email =
                                                snapshot.getString("email");

                                        String mobile =
                                                snapshot.getString("mobile");

                                        String city =
                                                snapshot.getString("city");

                                        if (name == null
                                                || name.trim().isEmpty()) {

                                            name = "User";
                                        }

                                        if (email == null) {
                                            email = "";
                                        }

                                        if (mobile == null) {
                                            mobile = "";
                                        }

                                        if (city == null) {
                                            city = "";
                                        }

                                        final String finalName = name;
                                        final String finalEmail = email;
                                        final String finalMobile = mobile;
                                        final String finalCity = city;

                                        // =================================================
                                        // UPDATE JAVAFX UI
                                        // =================================================

                                        Platform.runLater(() -> {

                                            // Profile header
                                            if (profileNameLabel != null) {

                                                profileNameLabel.setText(
                                                        finalName
                                                );
                                            }

                                            if (profileEmailLabel != null) {

                                                profileEmailLabel.setText(
                                                        finalEmail
                                                );
                                            }

                                            // Initials
                                            if (initialsLabel != null) {

                                                initialsLabel.setText(
                                                        getInitials(
                                                                finalName
                                                        )
                                                );
                                            }

                                            // Personal information
                                            if (nameField != null) {

                                                nameField.setText(
                                                        finalName
                                                );
                                            }

                                            if (emailField != null) {

                                                emailField.setText(
                                                        finalEmail
                                                );
                                            }

                                            if (mobileField != null) {

                                                mobileField.setText(
                                                        finalMobile
                                                );
                                            }

                                            if (cityField != null) {

                                                cityField.setText(
                                                        finalCity
                                                );
                                            }

                                            System.out.println(
                                                    "[Profile] Real-time profile updated."
                                            );
                                        });
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[Profile] Could not start Firestore listener."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // STOP LISTENER
    // =========================================================

    public void stopRealtimeProfile() {

        if (profileListener != null) {

            profileListener.remove();

            profileListener = null;

            System.out.println(
                    "[Profile] Firestore listener stopped."
            );
        }
    }

    // =========================================================
    // GET INITIALS
    // =========================================================

    private String getInitials(String name) {

        if (name == null || name.trim().isEmpty()) {

            return "U";
        }

        String[] parts =
                name.trim().split("\\s+");

        if (parts.length == 1) {

            return parts[0]
                    .substring(
                            0,
                            1
                    )
                    .toUpperCase();
        }

        return (
                parts[0].substring(0, 1)
                        + parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    // =========================================================
    // PROFILE CARD
    // =========================================================

    private VBox createProfileCard() {

        VBox card = new VBox(15);

        card.setPadding(
                new Insets(28)
        );

        card.setAlignment(
                Pos.CENTER
        );

        card.setStyle("""
                -fx-background-color: #111722;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 22;
                -fx-background-radius: 22;
                """);

        // =====================================================
        // PROFILE CIRCLE
        // =====================================================

        Circle profileCircle =
                new Circle(58);

        profileCircle.setFill(
                Color.web("#6D28D9")
        );

        // =====================================================
        // DYNAMIC INITIALS
        // =====================================================

        initialsLabel =
                new Label("U");

        initialsLabel.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 30px;
                -fx-font-weight: bold;
                """);

        StackPane avatar =
                new StackPane();

        avatar.setPrefSize(
                116,
                116
        );

        avatar.getChildren().addAll(
                profileCircle,
                initialsLabel
        );

        // =====================================================
        // DYNAMIC NAME
        // =====================================================

        profileNameLabel =
                new Label("User");

        profileNameLabel.setStyle("""
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 22px;
                -fx-font-weight: bold;
                """);

        // =====================================================
        // DYNAMIC EMAIL
        // =====================================================

        profileEmailLabel =
                new Label("");

        profileEmailLabel.setStyle("""
                -fx-text-fill: #8E98A8;
                -fx-font-size: 14px;
                """);

        // =====================================================
        // STATUS
        // =====================================================

        Label status =
                new Label("●  Active");

        status.setStyle("""
                -fx-background-color: rgba(0, 240, 255, 0.15);
                -fx-border-color: #00F0FF;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
                -fx-text-fill: #00F0FF;
                -fx-font-size: 13px;
                -fx-font-weight: bold;
                -fx-padding: 6 14 6 14;
                """);

        // =====================================================
        // CHANGE PICTURE
        // =====================================================

        Button changePicture =
                new Button("Change picture");

        changePicture.setMaxWidth(
                Double.MAX_VALUE
        );

        changePicture.setPrefHeight(38);

        changePicture.setStyle("""
                -fx-background-color: #080B12;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                -fx-text-fill: #D8D2E2;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        card.getChildren().addAll(
                avatar,
                profileNameLabel,
                profileEmailLabel,
                status,
                changePicture
        );

        return card;
    }

    // =========================================================
    // PERSONAL INFORMATION
    // =========================================================

    private VBox createPersonalInformation() {

        VBox card = createCard();

        Label title =
                new Label(
                        "Personal information"
                );

        title.setStyle("""
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 19px;
                -fx-font-weight: bold;
                """);

        GridPane grid =
                new GridPane();

        grid.setHgap(20);
        grid.setVgap(10);

        // =====================================================
        // NAME
        // =====================================================

        Label nameLabel =
                createFieldLabel(
                        "Full name"
                );

        nameField =
                createTextField("");

        // =====================================================
        // EMAIL
        // =====================================================

        Label emailLabel =
                createFieldLabel(
                        "Email"
                );

        emailField =
                createTextField("");

        // Email normally should not be edited here
        emailField.setEditable(false);

        // =====================================================
        // MOBILE
        // =====================================================

        Label mobileLabel =
                createFieldLabel(
                        "Mobile"
                );

        mobileField =
                createTextField("");

        // =====================================================
        // CITY
        // =====================================================

        Label cityLabel =
                createFieldLabel(
                        "City"
                );

        cityField =
                createTextField("");

        // =====================================================
        // BOXES
        // =====================================================

        VBox nameBox =
                new VBox(7);

        nameBox.getChildren().addAll(
                nameLabel,
                nameField
        );

        VBox emailBox =
                new VBox(7);

        emailBox.getChildren().addAll(
                emailLabel,
                emailField
        );

        VBox mobileBox =
                new VBox(7);

        mobileBox.getChildren().addAll(
                mobileLabel,
                mobileField
        );

        VBox cityBox =
                new VBox(7);

        cityBox.getChildren().addAll(
                cityLabel,
                cityField
        );

        // =====================================================
        // GRID
        // =====================================================

        grid.add(
                nameBox,
                0,
                0
        );

        grid.add(
                emailBox,
                1,
                0
        );

        grid.add(
                mobileBox,
                0,
                1
        );

        grid.add(
                cityBox,
                1,
                1
        );

        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        Button updateButton =
                createPurpleButton(
                        "Update profile"
                );

        updateButton.setOnAction(e ->
                updateProfile()
        );

        // =====================================================
        // ADD
        // =====================================================

        card.getChildren().addAll(
                title,
                grid,
                updateButton
        );

        return card;
    }

    // =========================================================
    // UPDATE PROFILE IN FIRESTORE
    // =========================================================

    private void updateProfile() {

        String userId =
                SessionManager.getUserId();

        if (userId == null
                || userId.trim().isEmpty()) {

            System.out.println(
                    "[Profile] No logged-in user."
            );

            return;
        }

        String name =
                nameField.getText().trim();

        String mobile =
                mobileField.getText().trim();

        String city =
                cityField.getText().trim();

        if (name.isEmpty()) {

            System.out.println(
                    "[Profile] Name cannot be empty."
            );

            return;
        }

        try {

            DatabaseConfig
                    .getFirestore()
                    .collection("Users")
                    .document(userId)
                    .update(
                            "name",
                            name,
                            "mobile",
                            mobile,
                            "city",
                            city
                    )
                    .get();

            System.out.println(
                    "[Profile] Profile updated successfully."
            );

        } catch (Exception e) {

            System.err.println(
                    "[Profile] Error updating profile."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // SECURITY CARD
    // =========================================================

    private VBox createSecurityCard() {

        VBox card = createCard();

        Label title =
                new Label(
                        "Security"
                );

        title.setStyle("""
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 19px;
                -fx-font-weight: bold;
                """);

        // =====================================================
        // CURRENT PASSWORD
        // =====================================================

        Label currentLabel =
                createFieldLabel(
                        "Current password"
                );

        PasswordField currentPassword =
                createPasswordField();

        // IMPORTANT:
        // Never hardcode the real password.
        currentPassword.setPromptText(
                "Enter current password"
        );

        // =====================================================
        // NEW PASSWORD
        // =====================================================

        Label newLabel =
                createFieldLabel(
                        "New password"
                );

        PasswordField newPassword =
                createPasswordField();

        // =====================================================
        // CONFIRM PASSWORD
        // =====================================================

        Label confirmLabel =
                createFieldLabel(
                        "Confirm new password"
                );

        PasswordField confirmPassword =
                createPasswordField();

        // =====================================================
        // BOXES
        // =====================================================

        VBox currentBox =
                new VBox(7);

        currentBox.getChildren().addAll(
                currentLabel,
                currentPassword
        );

        VBox newBox =
                new VBox(7);

        newBox.getChildren().addAll(
                newLabel,
                newPassword
        );

        VBox confirmBox =
                new VBox(7);

        confirmBox.getChildren().addAll(
                confirmLabel,
                confirmPassword
        );

        // =====================================================
        // PASSWORD GRID
        // =====================================================

        GridPane passwordGrid =
                new GridPane();

        passwordGrid.setHgap(20);
        passwordGrid.setVgap(20);

        passwordGrid.add(
                currentBox,
                0,
                0,
                2,
                1
        );

        passwordGrid.add(
                newBox,
                0,
                1
        );

        passwordGrid.add(
                confirmBox,
                1,
                1
        );

        // =====================================================
        // CHANGE PASSWORD BUTTON
        // =====================================================

        Button changePassword =
                new Button(
                        "Change password"
                );

        changePassword.setPrefHeight(42);

        changePassword.setStyle("""
                -fx-background-color: #080B12;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 12;
                -fx-background-radius: 12;
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        changePassword.setOnAction(e -> {

            if (!newPassword.getText().equals(
                    confirmPassword.getText()
            )) {

                System.out.println(
                        "Passwords do not match."
                );

                return;
            }

            if (newPassword.getText().trim().isEmpty()) {

                System.out.println(
                        "New password is required."
                );

                return;
            }

            System.out.println(
                    "Password change functionality will be connected separately."
            );
        });

        // =====================================================
        // ADD
        // =====================================================

        card.getChildren().addAll(
                title,
                passwordGrid,
                changePassword
        );

        return card;
    }

    // =========================================================
    // CREATE CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox(20);

        card.setPadding(
                new Insets(
                        25,
                        28,
                        28,
                        28
                )
        );

        card.setStyle("""
                -fx-background-color: #111722;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 22;
                -fx-background-radius: 22;
                """);

        return card;
    }

    // =========================================================
    // FIELD LABEL
    // =========================================================

    private Label createFieldLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle("""
                -fx-text-fill: #D8D2E2;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                """);

        return label;
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField createTextField(
            String value) {

        TextField field =
                new TextField(value);

        field.setPrefHeight(48);

        field.setStyle("""
                -fx-background-color: #101620;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 14;
                -fx-background-radius: 14;
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 14px;
                -fx-padding: 0 15 0 15;
                """);

        return field;
    }

    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    private PasswordField createPasswordField() {

        PasswordField field =
                new PasswordField();

        field.setPrefHeight(48);

        field.setStyle("""
                -fx-background-color: #101620;
                -fx-border-color: #252B3A;
                -fx-border-width: 1;
                -fx-border-radius: 14;
                -fx-background-radius: 14;
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 14px;
                -fx-padding: 0 15 0 15;
                """);

        return field;
    }

    // =========================================================
    // PURPLE BUTTON
    // =========================================================

    private Button createPurpleButton(
            String text) {

        Button button =
                new Button(text);

        button.setPrefHeight(45);

        button.setPrefWidth(145);

        button.setStyle("""
                -fx-background-color:
                    linear-gradient(
                        to right,
                        #6D28D9,
                        #8B5CF6
                    );
                -fx-background-radius: 13;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-cursor: hand;
                """);

        return button;
    }
}