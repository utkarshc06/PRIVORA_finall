package com.myprivora.view.xerox;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import com.myprivora.view.theme.ClayTheme;

public class Profile {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;
    private final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private final String PURPLE_LIGHT = ClayTheme.CYAN_LIGHT;

    private final String TEXT = ClayTheme.FROST_WHITE;
    private final String SECONDARY = ClayTheme.FROST_MUTED;

    private final String INPUT = ClayTheme.OBSIDIAN_SURFACE;


    // =========================================================
    // FIRESTORE LISTENER
    // =========================================================

    private ListenerRegistration profileListener;


    // =========================================================
    // PROFILE HEADER REFERENCES
    // =========================================================

    private Label profileNameLabel;

    private Label profileEmailLabel;

    private Label initialsLabel;


    // =========================================================
    // INFORMATION REFERENCES
    // =========================================================

    private TextField nameField;

    private TextField emailField;

    private TextField mobileField;

    private TextField cityField;


    private Label roleValueLabel;

    private Label xeroxIdValueLabel;


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox content =
                new VBox(25);

        content.setPadding(
                new Insets(
                        35,
                        40,
                        50,
                        40
                )
        );

        content.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        // =====================================================
        // PAGE TITLE
        // =====================================================

        Label title =
                new Label(
                        "My profile"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Manage your Xerox centre information and security."
                );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 16px;"
        );


        VBox heading =
                new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // PROFILE CARD
        // =====================================================

        VBox profileCard =
                createProfileCard();


        // =====================================================
        // PERSONAL INFORMATION
        // =====================================================

        VBox personalCard =
                createPersonalInformation();


        // =====================================================
        // SECURITY
        // =====================================================

        VBox securityCard =
                createSecurityCard();


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

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(
                content
        );

        scrollPane.setFitToWidth(
                true
        );

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

        BorderPane root =
                new BorderPane();

        root.setCenter(
                scrollPane
        );

        root.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        VBox finalContent =
                new VBox();

        finalContent.getChildren().add(
                root
        );


        VBox.setVgrow(
                root,
                Priority.ALWAYS
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

        String userId =
                SessionManager.getUserId();


        // =====================================================
        // CHECK LOGIN
        // =====================================================

        if (userId == null
                || userId.trim().isEmpty()) {

            System.out.println(
                    "[Xerox Profile] "
                            + "No logged-in Xerox user found."
            );

            return;
        }


        System.out.println(
                "[Xerox Profile] "
                        + "Starting real-time listener for UID: "
                        + userId
        );


        try {

            // =================================================
            // PREVENT DUPLICATE LISTENER
            // =================================================

            stopRealtimeProfile();


            // =================================================
            // USERS/{UID}
            // =================================================

            profileListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("Users")
                            .document(userId)
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        // =====================
                                        // ERROR
                                        // =====================

                                        if (error != null) {

                                            System.err.println(
                                                    "[Xerox Profile] "
                                                            + "Firestore listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        // =====================
                                        // DOCUMENT NOT FOUND
                                        // =====================

                                        if (snapshot == null
                                                || !snapshot.exists()) {

                                            System.out.println(
                                                    "[Xerox Profile] "
                                                            + "Xerox user document not found."
                                            );

                                            return;
                                        }


                                        // =====================
                                        // READ SAME DATA
                                        // AS XDashboard
                                        // =====================

                                        String name =
                                                snapshot.getString(
                                                        "name"
                                                );


                                        String email =
                                                snapshot.getString(
                                                        "email"
                                                );


                                        String mobile =
                                                snapshot.getString(
                                                        "mobile"
                                                );


                                        String city =
                                                snapshot.getString(
                                                        "city"
                                                );


                                        String role =
                                                snapshot.getString(
                                                        "role"
                                                );


                                        // =====================
                                        // DEFAULT VALUES
                                        // =====================

                                        if (name == null
                                                || name.trim().isEmpty()) {

                                            name =
                                                    "Xerox Centre";
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


                                        if (role == null
                                                || role.trim().isEmpty()) {

                                            role =
                                                    "XEROX";
                                        }


                                        // =====================
                                        // FINAL VALUES
                                        // =====================

                                        final String finalName =
                                                name.trim();

                                        final String finalEmail =
                                                email.trim();

                                        final String finalMobile =
                                                mobile.trim();

                                        final String finalCity =
                                                city.trim();

                                        final String finalRole =
                                                role.trim();

                                        final String finalUserId =
                                                userId;


                                        // =====================
                                        // UPDATE JAVAFX UI
                                        // =====================

                                        Platform.runLater(() -> {

                                            // -----------------
                                            // PROFILE HEADER
                                            // -----------------

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


                                            // -----------------
                                            // INITIALS
                                            // -----------------

                                            if (initialsLabel != null) {

                                                initialsLabel.setText(
                                                        getInitials(
                                                                finalName
                                                        )
                                                );
                                            }


                                            // -----------------
                                            // PERSONAL INFO
                                            // -----------------

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


                                            // -----------------
                                            // ROLE
                                            // -----------------

                                            if (roleValueLabel != null) {

                                                roleValueLabel.setText(
                                                        finalRole
                                                );
                                            }


                                            // -----------------
                                            // XEROX ID
                                            // -----------------

                                            if (xeroxIdValueLabel != null) {

                                                xeroxIdValueLabel.setText(
                                                        finalUserId
                                                );
                                            }


                                            System.out.println(
                                                    "[Xerox Profile] "
                                                            + "Real-time profile updated."
                                            );
                                        });
                                    }
                            );


        } catch (Exception e) {

            System.err.println(
                    "[Xerox Profile] "
                            + "Could not start Firestore listener."
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
                    "[Xerox Profile] "
                            + "Firestore listener stopped."
            );
        }
    }


    // =========================================================
    // GET INITIALS
    // =========================================================

    private String getInitials(
            String name) {

        if (name == null
                || name.trim().isEmpty()) {

            return "X";
        }


        String[] parts =
                name.trim()
                        .split("\\s+");


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
                        +
                parts[
                        parts.length - 1
                ].substring(0, 1)
        ).toUpperCase();
    }


    // =========================================================
    // PROFILE CARD
    // =========================================================

    private VBox createProfileCard() {

        VBox card =
                new VBox(15);

        card.setPadding(
                new Insets(28)
        );

        card.setAlignment(
                Pos.CENTER
        );

        card.setStyle("-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;");


        // =====================================================
        // PROFILE CIRCLE
        // =====================================================

        Circle profileCircle =
                new Circle(58);

        profileCircle.setFill(
                Color.web(
                        PURPLE
                )
        );


        // =====================================================
        // INITIALS
        // =====================================================

        initialsLabel =
                new Label("X");

        initialsLabel.setStyle("-fx-text-fill: #080C16;" +
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;");


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
        // NAME
        // =====================================================

        profileNameLabel =
                new Label(
                        "Xerox Centre"
                );

        profileNameLabel.setStyle("-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;");


        // =====================================================
        // EMAIL
        // =====================================================

        profileEmailLabel =
                new Label(
                        "Loading..."
                );

        profileEmailLabel.setStyle("-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;");


        // =====================================================
        // STATUS
        // =====================================================

        Label status =
                new Label(
                        "Active"
                );

        status.setStyle("-fx-background-color: rgba(0, 240, 255, 0.15);" +
                "-fx-border-color: rgba(0, 240, 255, 0.3);" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: " + PURPLE + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 15 6 15;");


        // =====================================================
        // CHANGE PICTURE
        // =====================================================

        Button changePicture =
                new Button(
                        "Change picture"
                );

        changePicture.setMaxWidth(
                Double.MAX_VALUE
        );

        changePicture.setPrefHeight(
                38
        );

        changePicture.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;");
        ClayTheme.applyButtonHover(changePicture, false);


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

        VBox card =
                createCard();


        Label title =
                new Label(
                        "Xerox centre information"
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
                        "Centre name"
                );


        nameField =
                createTextField(
                        ""
                );


        // =====================================================
        // EMAIL
        // =====================================================

        Label emailLabel =
                createFieldLabel(
                        "Email"
                );


        emailField =
                createTextField(
                        ""
                );


        emailField.setEditable(
                false
        );


        // =====================================================
        // MOBILE
        // =====================================================

        Label mobileLabel =
                createFieldLabel(
                        "Mobile"
                );


        mobileField =
                createTextField(
                        ""
                );


        // =====================================================
        // CITY
        // =====================================================

        Label cityLabel =
                createFieldLabel(
                        "City"
                );


        cityField =
                createTextField(
                        ""
                );


        // =====================================================
        // NAME BOX
        // =====================================================

        VBox nameBox =
                new VBox(7);

        nameBox.getChildren().addAll(
                nameLabel,
                nameField
        );


        // =====================================================
        // EMAIL BOX
        // =====================================================

        VBox emailBox =
                new VBox(7);

        emailBox.getChildren().addAll(
                emailLabel,
                emailField
        );


        // =====================================================
        // MOBILE BOX
        // =====================================================

        VBox mobileBox =
                new VBox(7);

        mobileBox.getChildren().addAll(
                mobileLabel,
                mobileField
        );


        // =====================================================
        // CITY BOX
        // =====================================================

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
        // ROLE
        // =====================================================

        Label roleLabel =
                createFieldLabel(
                        "Role"
                );


        roleValueLabel =
                new Label(
                        "XEROX"
                );

        roleValueLabel.setStyle("""
                -fx-text-fill: #A78BFA;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                """);


        // =====================================================
        // XEROX ID
        // =====================================================

        Label xeroxIdLabel =
                createFieldLabel(
                        "Xerox ID"
                );


        xeroxIdValueLabel =
                new Label(
                        SessionManager.getUserId()
                );

        xeroxIdValueLabel.setWrapText(
                true
        );

        xeroxIdValueLabel.setStyle("""
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 12px;
                -fx-font-weight: bold;
                """);


        // =====================================================
        // EXTRA INFORMATION
        // =====================================================

        HBox roleRow =
                new HBox();

        roleRow.setAlignment(
                Pos.CENTER_LEFT
        );

        roleRow.getChildren().addAll(
                roleLabel
        );


        HBox roleValueRow =
                new HBox();

        roleValueRow.setAlignment(
                Pos.CENTER_LEFT
        );

        roleValueRow.getChildren().add(
                roleValueLabel
        );


        HBox xeroxIdRow =
                new HBox();

        xeroxIdRow.setAlignment(
                Pos.CENTER_LEFT
        );

        xeroxIdRow.getChildren().addAll(
                xeroxIdLabel
        );


        HBox xeroxIdValueRow =
                new HBox();

        xeroxIdValueRow.setAlignment(
                Pos.CENTER_LEFT
        );

        xeroxIdValueRow.getChildren().add(
                xeroxIdValueLabel
        );


        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        Button updateButton =
                createPurpleButton(
                        "Update profile"
                );


        updateButton.setOnAction(
                e -> updateProfile()
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        card.getChildren().addAll(
                title,
                grid,
                roleRow,
                roleValueRow,
                xeroxIdRow,
                xeroxIdValueRow,
                updateButton
        );


        return card;
    }


    // =========================================================
    // UPDATE PROFILE
    // =========================================================

    private void updateProfile() {

        String userId =
                SessionManager.getUserId();


        if (userId == null
                || userId.trim().isEmpty()) {

            System.out.println(
                    "[Xerox Profile] "
                            + "No logged-in Xerox user."
            );

            return;
        }


        String name =
                nameField.getText()
                        .trim();


        String mobile =
                mobileField.getText()
                        .trim();


        String city =
                cityField.getText()
                        .trim();


        if (name.isEmpty()) {

            System.out.println(
                    "[Xerox Profile] "
                            + "Centre name cannot be empty."
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
                    "[Xerox Profile] "
                            + "Profile updated successfully."
            );


        } catch (Exception e) {

            System.err.println(
                    "[Xerox Profile] "
                            + "Error updating profile."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // SECURITY CARD
    // =========================================================

    private VBox createSecurityCard() {

        VBox card =
                createCard();


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
        // CHANGE PASSWORD
        // =====================================================

        Button changePassword =
                new Button(
                        "Change password"
                );

        changePassword.setPrefHeight(
                42
        );

        changePassword.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        ClayTheme.applyButtonHover(changePassword, false);


        changePassword.setOnAction(e -> {

            if (!newPassword.getText().equals(
                    confirmPassword.getText()
            )) {

                System.out.println(
                        "Passwords do not match."
                );

                return;
            }


            if (newPassword.getText()
                    .trim()
                    .isEmpty()) {

                System.out.println(
                        "New password is required."
                );

                return;
            }


            System.out.println(
                    "Password change functionality "
                            + "will be connected separately."
            );
        });


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

        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;"
        );


        return card;
    }


    // =========================================================
    // FIELD LABEL
    // =========================================================

    private Label createFieldLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        return label;
    }


    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField createTextField(
            String value) {

        TextField field =
                new TextField(
                        value
                );

        field.setPrefHeight(
                48
        );

        field.setStyle(
                "-fx-background-color: " + INPUT + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 0 15 0 15;"
        );


        return field;
    }


    // =========================================================
    // PASSWORD FIELD
    // =========================================================

    private PasswordField createPasswordField() {

        PasswordField field =
                new PasswordField();

        field.setPrefHeight(
                48
        );

        field.setStyle(
                "-fx-background-color: " + INPUT + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 0 15 0 15;"
        );


        return field;
    }


    // =========================================================
    // PURPLE BUTTON
    // =========================================================

    private Button createPurpleButton(
            String text) {

        Button button =
                new Button(text);

        button.setPrefHeight(
                45
        );

        button.setPrefWidth(
                145
        );

        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #00F0FF, #00B4D8);" +
                "-fx-background-radius: 13;" +
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        ClayTheme.applyButtonHover(button, true);


        return button;
    }
}