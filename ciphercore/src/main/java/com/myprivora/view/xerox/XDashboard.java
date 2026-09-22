package com.myprivora.view.xerox;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.session.SessionManager;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


// =============================================================
// XEROX DASHBOARD
// =============================================================

public class XDashboard {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private static final String BACKGROUND = "#080C16";

    private static final String CARD = "rgba(13, 19, 34, 0.75)";

    private static final String CARD_HOVER = "rgba(18, 27, 48, 0.85)";

    private static final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private static final String PURPLE = "#00F0FF";

    private static final String VIOLET = "#33F3FF";

    private static final String DEEP_PURPLE = "#00B4D8";

    private static final String TEXT = "#FFFFFF";

    private static final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";


    // =========================================================
    // REFERENCES
    // =========================================================

    private Label welcomeLabel;

    private Label xeroxNameLabel;

    private Label xeroxEmailLabel;


    // =========================================================
    // REAL-TIME LISTENERS
    // =========================================================

    private ListenerRegistration xeroxProfileListener;

    private ListenerRegistration xeroxCentreListener;

    private ListenerRegistration requestsListener;


    // =========================================================
    // REAL-TIME STATISTICS LABELS
    // =========================================================

    private Label pendingRequestsNumberLabel;

    private Label activeRequestsNumberLabel;

    private Label completedNumberLabel;

    private Label printerStatusNumberLabel;


    // =========================================================
    // RECENT ACTIVITY
    // =========================================================

    private VBox activityList;


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        // =====================================================
        // MAIN CONTAINER
        // =====================================================

        VBox main =
                new VBox();

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
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox headingBox =
                new VBox(4);


        // =====================================================
        // WELCOME LABEL
        // =====================================================

        welcomeLabel =
                new Label(
                        "Welcome back, Xerox Centre 👋"
                );

        welcomeLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // HEADER SUBTITLE
        // =====================================================

        Label subtitle =
                new Label(
                        "Here's what's happening with your printing requests today."
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


        header.getChildren().add(
                headingBox
        );


        // =====================================================
        // STATISTICS ROW 1
        // =====================================================

        HBox statisticsRow1 =
                new HBox(14);


        VBox pendingRequests =
                createStatCard(
                        "PENDING REQUESTS",
                        "Loading...",
                        "Requests waiting for action"
                );


        VBox activeRequests =
                createStatCard(
                        "ACTIVE JOBS",
                        "Loading...",
                        "Currently processing"
                );


        statisticsRow1.getChildren().addAll(
                pendingRequests,
                activeRequests
        );


        HBox.setHgrow(
                pendingRequests,
                Priority.ALWAYS
        );


        HBox.setHgrow(
                activeRequests,
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
                        "Loading...",
                        "Completed print jobs"
                );


        VBox printerStatus =
                createStatCard(
                        "PRINTER STATUS",
                        "Loading...",
                        "Checking centre availability"
                );


        statisticsRow2.getChildren().addAll(
                completed,
                printerStatus
        );


        HBox.setHgrow(
                completed,
                Priority.ALWAYS
        );


        HBox.setHgrow(
                printerStatus,
                Priority.ALWAYS
        );


        // =====================================================
        // XEROX INFORMATION
        // =====================================================

        VBox centreInformation =
                new VBox();

        centreInformation.setSpacing(0);

        centreInformation.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + CARD_BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );


        // =====================================================
        // INFORMATION HEADER
        // =====================================================

        VBox informationHeader =
                new VBox(4);

        informationHeader.setPadding(
                new Insets(
                        18,
                        22,
                        14,
                        22
                )
        );


        Label informationTitle =
                new Label(
                        "Xerox centre information"
                );

        informationTitle.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label informationSubtitle =
                new Label(
                        "Currently logged-in Xerox centre"
                );

        informationSubtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 12px;"
        );


        informationHeader.getChildren().addAll(
                informationTitle,
                informationSubtitle
        );


        // =====================================================
        // SEPARATOR
        // =====================================================

        HBox separator =
                new HBox();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: "
                        + CARD_BORDER + ";"
        );


        // =====================================================
        // INFORMATION LIST
        // =====================================================

        VBox informationList =
                new VBox(12);

        informationList.setPadding(
                new Insets(
                        16,
                        22,
                        20,
                        22
                )
        );


        HBox nameRow =
                createInformationRow(
                        "Name",
                        "Loading..."
                );


        HBox emailRow =
                createInformationRow(
                        "Email",
                        "Loading..."
                );


        HBox roleRow =
                createInformationRow(
                        "Role",
                        "XEROX"
                );


        String currentUserId =
                SessionManager.getUserId();


        HBox uidRow =
                createInformationRow(
                        "Xerox ID",
                        currentUserId
                );


        informationList.getChildren().addAll(
                nameRow,
                emailRow,
                roleRow,
                uidRow
        );


        centreInformation.getChildren().addAll(
                informationHeader,
                separator,
                informationList
        );


        // =====================================================
        // SAVE PROFILE REFERENCES
        // =====================================================

        xeroxNameLabel =
                (Label) nameRow
                        .getProperties()
                        .get("valueLabel");


        xeroxEmailLabel =
                (Label) emailRow
                        .getProperties()
                        .get("valueLabel");


        // =====================================================
        // RECENT ACTIVITY
        // =====================================================

        VBox recentActivity =
                new VBox();

        recentActivity.setSpacing(0);

        recentActivity.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + CARD_BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );


        // =====================================================
        // ACTIVITY HEADER
        // =====================================================

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
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label activitySubtitle =
                new Label(
                        "Latest events across your printing requests"
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


        // =====================================================
        // ACTIVITY SEPARATOR
        // =====================================================

        HBox activitySeparator =
                new HBox();

        activitySeparator.setPrefHeight(1);

        activitySeparator.setStyle(
                "-fx-background-color: "
                        + CARD_BORDER + ";"
        );


        // =====================================================
        // REAL-TIME ACTIVITY LIST
        // =====================================================

        activityList =
                new VBox(3);

        activityList.setPadding(
                new Insets(
                        12,
                        22,
                        16,
                        22
                )
        );


        Label loadingActivity =
                new Label(
                        "Loading activity..."
                );

        loadingActivity.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 10 0 10 0;"
        );


        activityList.getChildren().add(
                loadingActivity
        );


        recentActivity.getChildren().addAll(
                activityHeader,
                activitySeparator,
                activityList
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
                        + "#24134A"
                        + ", "
                        + "#32165E"
                        + ", "
                        + "#171426"
                        + ");" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #4C3575;" +
                "-fx-border-radius: 18;"
        );


        Label tipIcon =
                new Label(
                        "✧"
                );

        tipIcon.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 20px;"
        );


        Label tipTitle =
                new Label(
                        "Secure Printing"
                );

        tipTitle.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label tipText =
                new Label(
                        "Only process documents after verifying "
                                + "the request, privacy PIN and expiry rules."
                );

        tipText.setWrapText(
                true
        );

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
                centreInformation,
                recentActivity,
                proTip
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(
                main
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

        scrollPane.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";" +
                "-fx-border-color: transparent;"
        );


        // =====================================================
        // START REAL-TIME LISTENERS
        // =====================================================

        loadRealtimeXerox();

        loadRealtimeRequests();

        loadRealtimeCentreStatus();


        return scrollPane;
    }


    // =========================================================
    // REAL-TIME XEROX PROFILE
    // =========================================================

    private void loadRealtimeXerox() {

        String userId =
                SessionManager.getUserId();


        if (
                userId == null
                || userId.trim().isEmpty()
        ) {

            System.out.println(
                    "[XDashboard] No logged-in Xerox UID."
            );

            return;
        }


        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (xeroxProfileListener != null) {

                xeroxProfileListener.remove();

                xeroxProfileListener = null;
            }


            System.out.println(
                    "[XDashboard] Starting Xerox profile listener..."
            );


            // -------------------------------------------------
            // USERS/{UID}
            // -------------------------------------------------

            xeroxProfileListener =
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
                                                    "[XDashboard] "
                                                            + "Profile listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        if (
                                                snapshot == null
                                                || !snapshot.exists()
                                        ) {

                                            System.out.println(
                                                    "[XDashboard] "
                                                            + "Xerox user document not found."
                                            );

                                            return;
                                        }


                                        // -----------------------------------------
                                        // READ REAL DATA
                                        // -----------------------------------------

                                        String name =
                                                snapshot.getString(
                                                        "name"
                                                );


                                        String email =
                                                snapshot.getString(
                                                        "email"
                                                );


                                        String role =
                                                snapshot.getString(
                                                        "role"
                                                );


                                        if (
                                                name == null
                                                || name.trim().isEmpty()
                                        ) {

                                            name =
                                                    "Xerox Centre";
                                        }


                                        if (email == null) {

                                            email =
                                                    "";
                                        }


                                        if (
                                                role == null
                                                || role.trim().isEmpty()
                                        ) {

                                            role =
                                                    "XEROX";
                                        }


                                        final String finalName =
                                                name.trim();


                                        final String finalEmail =
                                                email.trim();


                                        final String finalRole =
                                                role.trim();


                                        // -----------------------------------------
                                        // UPDATE JAVAFX THREAD
                                        // -----------------------------------------

                                        Platform.runLater(
                                                () -> {

                                                    if (
                                                            welcomeLabel
                                                                    != null
                                                    ) {

                                                        welcomeLabel.setText(
                                                                "Welcome back, "
                                                                        + finalName
                                                                        + " 👋"
                                                        );
                                                    }


                                                    if (
                                                            xeroxNameLabel
                                                                    != null
                                                    ) {

                                                        xeroxNameLabel.setText(
                                                                finalName
                                                        );
                                                    }


                                                    if (
                                                            xeroxEmailLabel
                                                                    != null
                                                    ) {

                                                        xeroxEmailLabel.setText(
                                                                finalEmail
                                                        );
                                                    }


                                                    System.out.println(
                                                            "[XDashboard] "
                                                                    + "Profile updated: "
                                                                    + finalName
                                                                    + " / "
                                                                    + finalRole
                                                    );
                                                }
                                        );
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[XDashboard] "
                            + "Unable to start profile listener."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // REAL-TIME PRINT REQUESTS
    // =========================================================

    private void loadRealtimeRequests() {

        String xeroxId =
                SessionManager.getUserId();


        if (
                xeroxId == null
                || xeroxId.trim().isEmpty()
        ) {

            System.out.println(
                    "[XDashboard] "
                            + "Cannot load requests. Xerox UID missing."
            );

            return;
        }


        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (requestsListener != null) {

                requestsListener.remove();

                requestsListener = null;
            }


            System.out.println(
                    "[XDashboard] "
                            + "Starting PrintRequests listener..."
            );


            // -------------------------------------------------
            // PRINT REQUESTS
            // -------------------------------------------------

            requestsListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("PrintRequests")
                            .whereEqualTo(
                                    "xeroxId",
                                    xeroxId
                            )
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        if (error != null) {

                                            System.err.println(
                                                    "[XDashboard] "
                                                            + "PrintRequests listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        if (snapshot == null) {

                                            return;
                                        }


                                        // -----------------------------------------
                                        // COUNTERS
                                        // -----------------------------------------

                                        int pendingCount = 0;

                                        int activeCount = 0;

                                        int completedCount = 0;


                                        // -----------------------------------------
                                        // COPY DOCUMENTS
                                        // -----------------------------------------

                                        List<DocumentSnapshot>
                                                requestDocuments =
                                                new ArrayList<>(
                                                        snapshot.getDocuments()
                                                );


                                        // -----------------------------------------
                                        // COUNT STATUSES
                                        // -----------------------------------------

                                        for (
                                                DocumentSnapshot request
                                                : requestDocuments
                                        ) {

                                            String status =
                                                    request.getString(
                                                            "status"
                                                    );


                                            if (
                                                    status == null
                                                    || status.trim().isEmpty()
                                            ) {

                                                continue;
                                            }


                                            status =
                                                    status.trim()
                                                            .toUpperCase();


                                            // -------------------------------------
                                            // PENDING
                                            // -------------------------------------

                                            if (
                                                    "PENDING"
                                                            .equals(status)
                                            ) {

                                                pendingCount++;
                                            }


                                            // -------------------------------------
                                            // ACTIVE
                                            // -------------------------------------

                                            if (
                                                    "ACCEPTED"
                                                            .equals(status)
                                                    ||
                                                    "APPROVED"
                                                            .equals(status)
                                                    ||
                                                    "PRINTING"
                                                            .equals(status)
                                            ) {

                                                activeCount++;
                                            }


                                            // -------------------------------------
                                            // COMPLETED
                                            // -------------------------------------

                                            if (
                                                    "COMPLETED"
                                                            .equals(status)
                                            ) {

                                                completedCount++;
                                            }
                                        }


                                        // -----------------------------------------
                                        // SORT LATEST FIRST
                                        // -----------------------------------------

                                        requestDocuments.sort(
                                                Comparator.comparing(
                                                        this::getRequestedAtInstant,
                                                        Comparator.nullsLast(
                                                                Comparator.reverseOrder()
                                                        )
                                                )
                                        );


                                        // -----------------------------------------
                                        // SHOW ONLY FIVE
                                        // -----------------------------------------

                                        List<DocumentSnapshot>
                                                latestRequests;


                                        if (
                                                requestDocuments.size() > 5
                                        ) {

                                            latestRequests =
                                                    new ArrayList<>(
                                                            requestDocuments.subList(
                                                                    0,
                                                                    5
                                                            )
                                                    );

                                        } else {

                                            latestRequests =
                                                    new ArrayList<>(
                                                            requestDocuments
                                                    );
                                        }


                                        final int finalPendingCount =
                                                pendingCount;


                                        final int finalActiveCount =
                                                activeCount;


                                        final int finalCompletedCount =
                                                completedCount;


                                        // -----------------------------------------
                                        // JAVAFX THREAD
                                        // -----------------------------------------

                                        Platform.runLater(
                                                () -> {

                                                    updateStatistics(
                                                            finalPendingCount,
                                                            finalActiveCount,
                                                            finalCompletedCount
                                                    );


                                                    updateRecentActivity(
                                                            latestRequests
                                                    );
                                                }
                                        );
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[XDashboard] "
                            + "Unable to start PrintRequests listener."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // REAL-TIME XEROX CENTRE STATUS
    // =========================================================

    private void loadRealtimeCentreStatus() {

        String xeroxId =
                SessionManager.getUserId();


        if (
                xeroxId == null
                || xeroxId.trim().isEmpty()
        ) {

            return;
        }


        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (xeroxCentreListener != null) {

                xeroxCentreListener.remove();

                xeroxCentreListener = null;
            }


            // -------------------------------------------------
            // XEROXCENTRES/{UID}
            // -------------------------------------------------

            xeroxCentreListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("XeroxCentres")
                            .document(xeroxId)
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        if (error != null) {

                                            System.err.println(
                                                    "[XDashboard] "
                                                            + "Centre status listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        if (
                                                snapshot == null
                                                || !snapshot.exists()
                                        ) {

                                            updatePrinterStatus(
                                                    "OFFLINE"
                                            );

                                            return;
                                        }


                                        // -----------------------------------------
                                        // AVAILABLE
                                        // -----------------------------------------

                                        Boolean available =
                                                snapshot.getBoolean(
                                                        "available"
                                                );


                                        // -----------------------------------------
                                        // STATUS
                                        // -----------------------------------------

                                        String status =
                                                snapshot.getString(
                                                        "status"
                                                );


                                        // -----------------------------------------
                                        // DETERMINE REAL STATUS
                                        // -----------------------------------------

                                        if (
                                                available != null
                                                && available
                                                && status != null
                                                && status.equalsIgnoreCase(
                                                        "ACTIVE"
                                                )
                                        ) {

                                            updatePrinterStatus(
                                                    "READY"
                                            );

                                        } else if (
                                                available != null
                                                && available
                                        ) {

                                            updatePrinterStatus(
                                                    "AVAILABLE"
                                            );

                                        } else if (
                                                status != null
                                                && (
                                                        status.equalsIgnoreCase(
                                                                "BLOCKED"
                                                        )
                                                        ||
                                                        status.equalsIgnoreCase(
                                                                "REJECTED"
                                                        )
                                                )
                                        ) {

                                            updatePrinterStatus(
                                                    "OFFLINE"
                                            );

                                        } else {

                                            updatePrinterStatus(
                                                    "UNAVAILABLE"
                                            );
                                        }
                                    }
                            );

        } catch (Exception e) {

            System.err.println(
                    "[XDashboard] "
                            + "Unable to start centre status listener."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // UPDATE STATISTICS
    // =========================================================

    private void updateStatistics(
            int pending,
            int active,
            int completed
    ) {

        if (
                pendingRequestsNumberLabel != null
        ) {

            pendingRequestsNumberLabel.setText(
                    String.valueOf(
                            pending
                    )
            );
        }


        if (
                activeRequestsNumberLabel != null
        ) {

            activeRequestsNumberLabel.setText(
                    String.valueOf(
                            active
                    )
            );
        }


        if (
                completedNumberLabel != null
        ) {

            completedNumberLabel.setText(
                    String.valueOf(
                            completed
                    )
            );
        }
    }


    // =========================================================
    // UPDATE PRINTER STATUS
    // =========================================================

    private void updatePrinterStatus(
            String status
    ) {

        Platform.runLater(
                () -> {

                    if (
                            printerStatusNumberLabel != null
                    ) {

                        printerStatusNumberLabel.setText(
                                status
                        );
                    }
                }
        );
    }


    // =========================================================
    // UPDATE RECENT ACTIVITY
    // =========================================================

    private void updateRecentActivity(
            List<DocumentSnapshot> requests
    ) {

        if (
                activityList == null
        ) {

            return;
        }


        activityList.getChildren().clear();


        // =====================================================
        // NO ACTIVITY
        // =====================================================

        if (
                requests == null
                || requests.isEmpty()
        ) {

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

            return;
        }


        // =====================================================
        // ADD LATEST REQUESTS
        // =====================================================

        for (
                DocumentSnapshot request
                : requests
        ) {

            activityList.getChildren().add(
                    createActivityRow(
                            request
                    )
            );
        }
    }


    // =========================================================
    // CREATE ACTIVITY ROW
    // =========================================================

    private HBox createActivityRow(
            DocumentSnapshot request
    ) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        9,
                        12,
                        9,
                        12
                )
        );

        row.setStyle(
                "-fx-background-color: "
                        + CARD_HOVER + ";" +
                "-fx-background-radius: 12;"
        );


        // =====================================================
        // ICON
        // =====================================================

        StackPane iconBox =
                new StackPane();

        iconBox.setPrefSize(
                32,
                32
        );

        iconBox.setMinSize(
                32,
                32
        );

        iconBox.setMaxSize(
                32,
                32
        );

        iconBox.setStyle(
                "-fx-background-color: rgba(139,92,246,0.15);" +
                "-fx-background-radius: 9;"
        );


        Label icon =
                new Label(
                        "▣"
                );

        icon.setStyle(
                "-fx-text-fill: "
                        + VIOLET + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        iconBox.getChildren().add(
                icon
        );


        // =====================================================
        // TEXT BOX
        // =====================================================

        VBox textBox =
                new VBox(2);


        String documentName =
                request.getString(
                        "documentName"
                );


        if (
                documentName == null
                || documentName.trim().isEmpty()
        ) {

            documentName =
                    "Document";
        }


        String status =
                request.getString(
                        "status"
                );


        if (
                status == null
                || status.trim().isEmpty()
        ) {

            status =
                    "UNKNOWN";
        }


        Label documentLabel =
                new Label(
                        documentName
                );

        documentLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        documentLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        Label statusLabel =
                new Label(
                        formatStatus(
                                status
                        )
                );

        statusLabel.setStyle(
                "-fx-text-fill: "
                        + VIOLET + ";" +
                "-fx-font-size: 11px;"
        );


        textBox.getChildren().addAll(
                documentLabel,
                statusLabel
        );


        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        // =====================================================
        // TIME
        // =====================================================

        String requestedAt =
                request.getString(
                        "requestedAt"
                );


        Label timeLabel =
                new Label(
                        formatTime(
                                requestedAt
                        )
                );

        timeLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 10px;"
        );


        row.getChildren().addAll(
                iconBox,
                textBox,
                timeLabel
        );


        return row;
    }


    // =========================================================
    // FORMAT STATUS
    // =========================================================

    private String formatStatus(
            String status
    ) {

        if (
                status == null
                || status.trim().isEmpty()
        ) {

            return "Unknown";
        }


        String upper =
                status.trim()
                        .toUpperCase();


        switch (upper) {

            case "PENDING":

                return "Pending request";


            case "ACCEPTED":

                return "Request accepted";


            case "APPROVED":

                return "Approved for printing";


            case "PRINTING":

                return "Currently printing";


            case "COMPLETED":

                return "Print completed";


            case "REJECTED":

                return "Request rejected";


            case "EXPIRED":

                return "Request expired";


            default:

                return status;
        }
    }


    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatTime(
            String timestamp
    ) {

        if (
                timestamp == null
                || timestamp.trim().isEmpty()
        ) {

            return "";
        }


        try {

            Instant instant =
                    Instant.parse(
                            timestamp
                    );


            long seconds =
                    Math.max(
                            0,
                            Instant.now()
                                    .getEpochSecond()
                                    - instant.getEpochSecond()
                    );


            if (
                    seconds < 60
            ) {

                return "Just now";
            }


            long minutes =
                    seconds / 60;


            if (
                    minutes < 60
            ) {

                return minutes
                        + (
                                minutes == 1
                                        ? " min ago"
                                        : " mins ago"
                        );
            }


            long hours =
                    minutes / 60;


            if (
                    hours < 24
            ) {

                return hours
                        + (
                                hours == 1
                                        ? " hour ago"
                                        : " hours ago"
                        );
            }


            long days =
                    hours / 24;


            return days
                    + (
                            days == 1
                                    ? " day ago"
                                    : " days ago"
                    );

        } catch (
                DateTimeParseException e
        ) {

            return "";
        }
    }


    // =========================================================
    // GET REQUESTED AT
    // =========================================================

    private Instant getRequestedAtInstant(
            DocumentSnapshot request
    ) {

        String requestedAt =
                request.getString(
                        "requestedAt"
                );


        if (
                requestedAt == null
                || requestedAt.trim().isEmpty()
        ) {

            return null;
        }


        try {

            return Instant.parse(
                    requestedAt
            );

        } catch (
                DateTimeParseException e
        ) {

            return null;
        }
    }


    // =========================================================
    // STOP ALL REAL-TIME LISTENERS
    // =========================================================

    public void dispose() {

        // -----------------------------------------------------
        // PROFILE LISTENER
        // -----------------------------------------------------

        if (
                xeroxProfileListener != null
        ) {

            xeroxProfileListener.remove();

            xeroxProfileListener = null;
        }


        // -----------------------------------------------------
        // CENTRE LISTENER
        // -----------------------------------------------------

        if (
                xeroxCentreListener != null
        ) {

            xeroxCentreListener.remove();

            xeroxCentreListener = null;
        }


        // -----------------------------------------------------
        // REQUEST LISTENER
        // -----------------------------------------------------

        if (
                requestsListener != null
        ) {

            requestsListener.remove();

            requestsListener = null;
        }


        System.out.println(
                "[XDashboard] "
                        + "All real-time listeners removed."
        );
    }


    // =========================================================
    // STATISTICS CARD
    // =========================================================

    private VBox createStatCard(
            String title,
            String number,
            String bottomText
    ) {

        VBox card =
                new VBox();

        card.setSpacing(4);

        card.setPadding(
                new Insets(
                        16
                )
        );

        card.setMinHeight(
                105
        );

        card.setPrefHeight(
                105
        );

        card.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + CARD_BORDER + ";" +
                "-fx-border-radius: 16;" +
                "-fx-background-radius: 16;"
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // NUMBER
        // =====================================================

        Label numberLabel =
                new Label(
                        number
                );

        numberLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // SAVE REFERENCES
        // =====================================================

        if (
                "PENDING REQUESTS"
                        .equalsIgnoreCase(
                                title
                        )
        ) {

            pendingRequestsNumberLabel =
                    numberLabel;
        }


        if (
                "ACTIVE JOBS"
                        .equalsIgnoreCase(
                                title
                        )
        ) {

            activeRequestsNumberLabel =
                    numberLabel;
        }


        if (
                "COMPLETED"
                        .equalsIgnoreCase(
                                title
                        )
        ) {

            completedNumberLabel =
                    numberLabel;
        }


        if (
                "PRINTER STATUS"
                        .equalsIgnoreCase(
                                title
                        )
        ) {

            printerStatusNumberLabel =
                    numberLabel;


            numberLabel.setStyle(
                    "-fx-text-fill: "
                            + VIOLET + ";" +
                    "-fx-font-size: 22px;" +
                    "-fx-font-weight: bold;"
            );
        }


        // =====================================================
        // BOTTOM TEXT
        // =====================================================

        Label bottomLabel =
                new Label(
                        bottomText
                );

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
    // INFORMATION ROW
    // =========================================================

    private HBox createInformationRow(
            String title,
            String value
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );


        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        Label valueLabel =
                new Label(
                        value == null
                                ? ""
                                : value
                );

        valueLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        HBox spacer =
                new HBox();


        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        row.getChildren().addAll(
                titleLabel,
                spacer,
                valueLabel
        );


        row.getProperties().put(
                "valueLabel",
                valueLabel
        );


        return row;
    }
}