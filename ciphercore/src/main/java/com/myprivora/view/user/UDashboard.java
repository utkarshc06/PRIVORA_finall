package com.myprivora.view.user;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.session.SessionManager;

import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class UDashboard {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00F0FF";
    private final String VIOLET = "#33F3FF";
    private final String DEEP_PURPLE = "#00B4D8";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";

    // =========================================================
    // REFERENCES
    // =========================================================

    private Button newUploadButton;

    private HBox uploadDocumentAction;

    private Label welcomeLabel;

    // =========================================================
    // REAL-TIME LISTENERS
    // =========================================================

    private ListenerRegistration userListener;

    private ListenerRegistration documentListener;

    // =========================================================
    // TOTAL DOCUMENT LABEL
    // =========================================================

    private Label totalDocumentsNumberLabel;

    private javafx.scene.layout.StackPane dashboardRootPane;
    private javafx.scene.control.ProgressIndicator dashboardLoader;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public StackPane getContent() {

        VBox main = new VBox();

        main.setSpacing(20);

        main.setPadding(
                new Insets(
                        5,
                        5,
                        40,
                        5
                )
        );

        main.setStyle(
                "-fx-background-color: #080C16;"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox headingBox =
                new VBox(4);

        // =====================================================
        // REAL USER NAME
        // =====================================================

        welcomeLabel =
                new Label(
                        "Welcome back, User 👋"
                );

        welcomeLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "Here's what's happening with your documents today."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );

        headingBox.getChildren().addAll(
                welcomeLabel,
                subtitle
        );

        // =====================================================
        // NEW UPLOAD BUTTON
        // =====================================================

        newUploadButton =
                new Button(
                        "⇧   New Upload"
                );

        newUploadButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", "
                        + DEEP_PURPLE + ");" +
                "-fx-background-radius: 22;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-cursor: hand;"
        );

        newUploadButton.setOnMouseEntered(
                e -> {

                    newUploadButton.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + VIOLET + ", "
                                    + PURPLE + ");" +
                            "-fx-background-radius: 22;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 18 10 18;" +
                            "-fx-cursor: hand;"
                    );

                    newUploadButton.setScaleX(1.03);
                    newUploadButton.setScaleY(1.03);
                }
        );

        newUploadButton.setOnMouseExited(
                e -> {

                    newUploadButton.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + PURPLE + ", "
                                    + DEEP_PURPLE + ");" +
                            "-fx-background-radius: 22;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 18 10 18;" +
                            "-fx-cursor: hand;"
                    );

                    newUploadButton.setScaleX(1);
                    newUploadButton.setScaleY(1);
                }
        );

        HBox.setHgrow(
                headingBox,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                headingBox,
                newUploadButton
        );

        // =====================================================
        // STATISTICS ROW 1
        // =====================================================

        HBox statisticsRow1 =
                new HBox(14);

        VBox totalDocuments =
                createStatCard(
                        "TOTAL DOCUMENTS",
                        "0",
                        "No document data yet"
                );

        VBox activeSessions =
                createStatCard(
                        "ACTIVE SESSIONS",
                        "0",
                        "No active sessions"
                );

        statisticsRow1.getChildren().addAll(
                totalDocuments,
                activeSessions
        );

        HBox.setHgrow(
                totalDocuments,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activeSessions,
                Priority.ALWAYS
        );

        // =====================================================
        // STATISTICS ROW 2
        // =====================================================

        HBox statisticsRow2 =
                new HBox(14);

        VBox completed =
                createStatCard(
                        "COMPLETED",
                        "0",
                        "No completed jobs"
                );

        VBox privacyScore =
                createStatCard(
                        "PRIVACY SCORE",
                        "0/100",
                        "Waiting for data"
                );

        statisticsRow2.getChildren().addAll(
                completed,
                privacyScore
        );

        HBox.setHgrow(
                completed,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                privacyScore,
                Priority.ALWAYS
        );

        // =====================================================
        // RECENT ACTIVITY
        // =====================================================

        VBox recentActivity =
                new VBox();

        recentActivity.setSpacing(0);

        recentActivity.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        VBox activityHeader =
                new VBox(4);

        activityHeader.setPadding(
                new Insets(
                        18,
                        22,
                        14,
                        22
                )
        );

        Label activityTitle =
                new Label(
                        "Recent activity"
                );

        activityTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        Label activitySubtitle =
                new Label(
                        "Latest events across your documents"
                );

        activitySubtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 12px;"
        );

        activityHeader.getChildren().addAll(
                activityTitle,
                activitySubtitle
        );

        HBox separator =
                new HBox();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: "
                        + CARD_BORDER + ";"
        );

        VBox activityList =
                new VBox(3);

        activityList.setPadding(
                new Insets(
                        12,
                        22,
                        16,
                        22
                )
        );

        Label noActivity =
                new Label(
                        "No recent activity"
                );

        noActivity.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 0 10 0;"
        );

        activityList.getChildren().add(
                noActivity
        );

        recentActivity.getChildren().addAll(
                activityHeader,
                separator,
                activityList
        );

        // =====================================================
        // QUICK ACTIONS
        // =====================================================

        VBox quickActions =
                new VBox();

        quickActions.setSpacing(0);

        quickActions.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        Label quickTitle =
                new Label(
                        "Quick actions"
                );

        quickTitle.setPadding(
                new Insets(
                        18,
                        22,
                        14,
                        22
                )
        );

        quickTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        HBox quickSeparator =
                new HBox();

        quickSeparator.setPrefHeight(1);

        quickSeparator.setStyle(
                "-fx-background-color: "
                        + CARD_BORDER + ";"
        );

        VBox quickList =
                new VBox(10);

        quickList.setPadding(
                new Insets(
                        15,
                        22,
                        20,
                        22
                )
        );

        // =====================================================
        // UPLOAD DOCUMENT
        // =====================================================

        uploadDocumentAction =
                createQuickAction(
                        "⇧",
                        "Upload Document"
                );

        // =====================================================
        // VIEW HISTORY
        // =====================================================

        HBox viewHistory =
                createQuickAction(
                        "〽",
                        "View History"
                );

        quickList.getChildren().addAll(
                uploadDocumentAction,
                viewHistory
        );

        quickActions.getChildren().addAll(
                quickTitle,
                quickSeparator,
                quickList
        );

        // =====================================================
        // PRO TIP
        // =====================================================

        VBox proTip =
                new VBox(7);

        proTip.setPadding(
                new Insets(
                        18,
                        22,
                        18,
                        22
                )
        );

        proTip.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + "#24134A, #32165E, #173C38);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #4C3575;" +
                "-fx-border-radius: 18;"
        );

        Label tipIcon =
                new Label("✧");

        tipIcon.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 20px;"
        );

        Label tipTitle =
                new Label(
                        "Pro Tip"
                );

        tipTitle.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        Label tipText =
                new Label(
                        "Keep your documents protected and "
                                + "use privacy controls before sharing."
                );

        tipText.setWrapText(true);

        tipText.setStyle(
                "-fx-text-fill: #D8D0EA;" +
                "-fx-font-size: 12px;"
        );

        proTip.getChildren().addAll(
                tipIcon,
                tipTitle,
                tipText
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                header,
                statisticsRow1,
                statisticsRow2,
                recentActivity,
                quickActions,
                proTip
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(main);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: #09080F;" +
                "-fx-border-color: transparent;"
        );

        scrollPane.setVisible(false);

        dashboardRootPane = new StackPane();
        dashboardRootPane.setStyle("-fx-background-color: #080C16;");
        dashboardLoader = com.myprivora.view.theme.ClayTheme.createLoader();
        dashboardRootPane.getChildren().addAll(dashboardLoader, scrollPane);

        // =====================================================
        // START REAL-TIME LISTENERS
        // =====================================================

        loadRealtimeUser();

        loadRealtimeDocumentCount();

        return dashboardRootPane;
    }

    // =========================================================
    // REAL-TIME USER DATA
    // =========================================================

    private void loadRealtimeUser() {

        String userId =
                SessionManager.getUserId();

        if (userId == null
                || userId.trim().isEmpty()) {

            Platform.runLater(() -> {

                if (welcomeLabel != null) {

                    welcomeLabel.setText(
                            "Welcome back, User 👋"
                    );
                }
            });

            return;
        }

        try {

            // Remove old listener
            if (userListener != null) {

                userListener.remove();

                userListener = null;
            }

            System.out.println(
                    "[UDashboard] Starting user listener..."
            );

            userListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("Users")
                            .document(userId)
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        if (error != null) {

                                            System.err.println(
                                                    "[UDashboard] "
                                                            + "User listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }

                                        if (snapshot == null
                                                || !snapshot.exists()) {

                                            return;
                                        }

                                        String name =
                                                snapshot.getString(
                                                        "name"
                                                );

                                        if (name == null
                                                || name.trim()
                                                        .isEmpty()) {

                                            name = "User";
                                        }

                                        final String finalName =
                                                name.trim();

                                        Platform.runLater(() -> {

                                            if (welcomeLabel != null) {

                                                welcomeLabel.setText(
                                                        "Welcome back,\n"
                                                                + finalName
                                                                + " 👋"
                                                );
                                            }

                                            if (dashboardRootPane != null && dashboardRootPane.getChildren().contains(dashboardLoader)) {
                                                dashboardRootPane.getChildren().remove(dashboardLoader);
                                                if (dashboardRootPane.getChildren().size() > 0) {
                                                    dashboardRootPane.getChildren().get(0).setVisible(true);
                                                }
                                            }

                                        });
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[UDashboard] "
                            + "Unable to start user listener."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // REAL-TIME DOCUMENT COUNT
    // =========================================================

    private void loadRealtimeDocumentCount() {

        String userId =
                SessionManager.getUserId();

        if (userId == null
                || userId.trim().isEmpty()) {

            System.out.println(
                    "[UDashboard] "
                            + "No logged-in user for document count."
            );

            return;
        }

        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (documentListener != null) {

                documentListener.remove();

                documentListener = null;
            }

            System.out.println(
                    "[UDashboard] Starting document count listener..."
            );

            System.out.println(
                    "[UDashboard] Document ownerId: "
                            + userId
            );

            // -------------------------------------------------
            // FIRESTORE REAL-TIME QUERY
            // -------------------------------------------------

            documentListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("Documents")
                            .whereEqualTo(
                                    "ownerId",
                                    userId
                            )
                            .addSnapshotListener(

                                    (
                                            QuerySnapshot snapshot,
                                            FirestoreException error
                                    ) -> {

                                        // -------------------------
                                        // ERROR
                                        // -------------------------

                                        if (error != null) {

                                            System.err.println(
                                                    "[UDashboard] "
                                                            + "Document count listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }

                                        // -------------------------
                                        // NO SNAPSHOT
                                        // -------------------------

                                        if (snapshot == null) {

                                            return;
                                        }

                                        // -------------------------
                                        // COUNT DOCUMENTS
                                        // -------------------------

                                        int count =
                                                snapshot.size();

                                        System.out.println(
                                                "[UDashboard] "
                                                        + "Total documents = "
                                                        + count
                                        );

                                        // -------------------------
                                        // UPDATE UI
                                        // -------------------------

                                        Platform.runLater(() -> {

                                            if (totalDocumentsNumberLabel
                                                    != null) {

                                                totalDocumentsNumberLabel
                                                        .setText(
                                                                String.valueOf(
                                                                        count
                                                                )
                                                        );
                                            }
                                        });
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[UDashboard] "
                            + "Unable to start document count listener."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // REMOVE REAL-TIME LISTENERS
    // =========================================================

    public void dispose() {

        if (userListener != null) {

            userListener.remove();

            userListener = null;

            System.out.println(
                    "[UDashboard] User listener removed."
            );
        }

        if (documentListener != null) {

            documentListener.remove();

            documentListener = null;

            System.out.println(
                    "[UDashboard] Document listener removed."
            );
        }
    }

    // =========================================================
    // GET NEW UPLOAD BUTTON
    // =========================================================

    public Button getNewUploadButton() {

        return newUploadButton;
    }

    // =========================================================
    // GET UPLOAD DOCUMENT ACTION
    // =========================================================

    public HBox getUploadDocumentAction() {

        return uploadDocumentAction;
    }

    // =========================================================
    // STATISTICS CARD
    // =========================================================

    private VBox createStatCard(
            String title,
            String number,
            String bottomText) {

        VBox card =
                new VBox();

        card.setSpacing(4);

        card.setPadding(
                new Insets(16)
        );

        card.setMinHeight(105);

        card.setPrefHeight(105);

        card.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + CARD_BORDER + ";" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: #9E96B1;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;"
        );

        Label numberLabel =
                new Label(number);

        // =====================================================
        // SAVE TOTAL DOCUMENT LABEL
        // =====================================================

        if ("TOTAL DOCUMENTS".equalsIgnoreCase(title)) {

            totalDocumentsNumberLabel =
                    numberLabel;
        }

        numberLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;"
        );

        Label bottomLabel =
                new Label(bottomText);

        bottomLabel.setStyle(
                "-fx-text-fill: "
                        + VIOLET + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                titleLabel,
                numberLabel,
                bottomLabel
        );

        return card;
    }

    // =========================================================
    // QUICK ACTION
    // =========================================================

    private HBox createQuickAction(
            String icon,
            String text) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        10,
                        14,
                        10,
                        14
                )
        );

        row.setStyle(
                "-fx-background-color: #11101A;" +
                "-fx-border-color: #29243A;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-cursor: hand;"
        );

        StackPane iconBox =
                new StackPane();

        iconBox.setPrefSize(
                34,
                34
        );

        iconBox.setStyle(
                "-fx-background-color: #211B32;" +
                "-fx-background-radius: 9;"
        );

        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: "
                        + VIOLET + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        iconBox.getChildren().add(
                iconLabel
        );

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: #E9E5F2;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        Label arrow =
                new Label("›");

        arrow.setStyle(
                "-fx-text-fill: #958DA8;" +
                "-fx-font-size: 22px;"
        );

        HBox.setHgrow(
                textLabel,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                iconBox,
                textLabel,
                arrow
        );

        // =====================================================
        // HOVER
        // =====================================================

        row.setOnMouseEntered(
                e -> {

                    row.setStyle(
                            "-fx-background-color: #1C1830;" +
                            "-fx-border-color: "
                                    + PURPLE + ";" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-cursor: hand;"
                    );

                    row.setScaleX(1.01);
                    row.setScaleY(1.01);
                }
        );

        row.setOnMouseExited(
                e -> {

                    row.setStyle(
                            "-fx-background-color: #11101A;" +
                            "-fx-border-color: #29243A;" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-cursor: hand;"
                    );

                    row.setScaleX(1);
                    row.setScaleY(1);
                }
        );

        return row;
    }
}