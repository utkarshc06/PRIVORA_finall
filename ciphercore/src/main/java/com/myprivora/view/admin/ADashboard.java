
package com.myprivora.view.admin;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.myprivora.config.DatabaseConfig;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Admin Dashboard
 *
 * All important dashboard values are loaded directly from Firestore.
 *
 * Collections used:
 *      Users
 *      XeroxCentres
 *      Documents
 *      PrintRequests
 *
 * Firestore listeners are used so the dashboard updates automatically
 * when data changes.
 */
import com.myprivora.view.theme.ClayTheme;

public class ADashboard {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;
    private final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private final String CARD_BORDER = ClayTheme.CARD_BORDER_COLOR;

    private final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private final String VIOLET = ClayTheme.CYAN_LIGHT;
    private final String DEEP_PURPLE = ClayTheme.CYAN_ACCENT;

    private final String TEXT = ClayTheme.FROST_WHITE;
    private final String SECONDARY_TEXT = ClayTheme.FROST_MUTED;
    private final String MUTED_TEXT = ClayTheme.WHITE_DIM;


    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore db =
            DatabaseConfig.getFirestore();


    // =========================================================
    // FIRESTORE LISTENERS
    // =========================================================

    private ListenerRegistration usersListener;
    private ListenerRegistration centresListener;
    private ListenerRegistration documentsListener;
    private ListenerRegistration requestsListener;


    // =========================================================
    // REALTIME DATA
    // =========================================================

    private QuerySnapshot usersSnapshot;
    private QuerySnapshot centresSnapshot;
    private QuerySnapshot documentsSnapshot;
    private QuerySnapshot requestsSnapshot;


    // =========================================================
    // UI LABELS
    // =========================================================

    private Label totalUsersLabel;
    private Label usersBottomLabel;

    private Label totalCentresLabel;
    private Label centresBottomLabel;

    private Label documentsTodayLabel;
    private Label documentsBottomLabel;

    private Label activeSessionsLabel;
    private Label sessionsBottomLabel;

    private VBox activityList;
    private VBox overviewList;


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox main = new VBox();

        main.setSpacing(20);

        main.setPadding(
                new Insets(5, 5, 40, 5)
        );

        main.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox headingBox = new VBox(4);


        Label welcome = new Label(
                "Welcome back, Admin 👋"
        );

        welcome.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle = new Label(
                "Here's what's happening across PRIVORA today."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        headingBox.getChildren().addAll(
                welcome,
                subtitle
        );


        HBox.setHgrow(
                headingBox,
                Priority.ALWAYS
        );


        // =====================================================
        // SYSTEM STATUS
        // =====================================================

        HBox statusBox = new HBox(8);

        statusBox.setAlignment(
                Pos.CENTER
        );


        StackPane statusCircle =
                new StackPane();

        statusCircle.setPrefSize(
                10,
                10
        );

        statusCircle.setMaxSize(
                10,
                10
        );

        statusCircle.setStyle(
                "-fx-background-color: #22D3A5;" +
                "-fx-background-radius: 50%;"
        );


        Label statusText =
                new Label("System Online");

        statusText.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        statusBox.getChildren().addAll(
                statusCircle,
                statusText
        );


        header.getChildren().addAll(
                headingBox,
                statusBox
        );


        // =====================================================
        // STATISTICS ROW 1
        // =====================================================

        HBox statisticsRow1 =
                new HBox(14);


        VBox usersCard =
                createStatCard(
                        "TOTAL USERS",
                        "0",
                        "Loading..."
                );

        totalUsersLabel =
                (Label) usersCard.getProperties()
                        .get("numberLabel");

        usersBottomLabel =
                (Label) usersCard.getProperties()
                        .get("bottomLabel");


        VBox centresCard =
                createStatCard(
                        "PRINT CENTRES",
                        "0",
                        "Loading..."
                );

        totalCentresLabel =
                (Label) centresCard.getProperties()
                        .get("numberLabel");

        centresBottomLabel =
                (Label) centresCard.getProperties()
                        .get("bottomLabel");


        statisticsRow1.getChildren().addAll(
                usersCard,
                centresCard
        );


        HBox.setHgrow(
                usersCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                centresCard,
                Priority.ALWAYS
        );


        // =====================================================
        // STATISTICS ROW 2
        // =====================================================

        HBox statisticsRow2 =
                new HBox(14);


        VBox documentsCard =
                createStatCard(
                        "DOCUMENTS TODAY",
                        "0",
                        "Loading..."
                );

        documentsTodayLabel =
                (Label) documentsCard.getProperties()
                        .get("numberLabel");

        documentsBottomLabel =
                (Label) documentsCard.getProperties()
                        .get("bottomLabel");


        VBox sessionsCard =
                createStatCard(
                        "ACTIVE SESSIONS",
                        "0",
                        "Loading..."
                );

        activeSessionsLabel =
                (Label) sessionsCard.getProperties()
                        .get("numberLabel");

        sessionsBottomLabel =
                (Label) sessionsCard.getProperties()
                        .get("bottomLabel");


        statisticsRow2.getChildren().addAll(
                documentsCard,
                sessionsCard
        );


        HBox.setHgrow(
                documentsCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                sessionsCard,
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


        // -----------------------------------------------------
        // ACTIVITY HEADER
        // -----------------------------------------------------

        VBox activityHeader =
                new VBox(4);

        activityHeader.setPadding(
                new Insets(18, 22, 14, 22)
        );


        Label activityTitle =
                new Label("Recent activity");

        activityTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label activitySubtitle =
                new Label(
                        "Latest events across the PRIVORA platform"
                );

        activitySubtitle.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 12px;"
        );


        activityHeader.getChildren().addAll(
                activityTitle,
                activitySubtitle
        );


        // -----------------------------------------------------
        // SEPARATOR
        // -----------------------------------------------------

        HBox separator =
                new HBox();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: " + CARD_BORDER + ";"
        );


        // -----------------------------------------------------
        // ACTIVITY LIST
        // -----------------------------------------------------

        activityList =
                new VBox(3);

        activityList.setPadding(
                new Insets(12, 22, 16, 22)
        );


        recentActivity.getChildren().addAll(
                activityHeader,
                separator,
                activityList
        );


        // =====================================================
        // SYSTEM OVERVIEW
        // =====================================================

        VBox systemOverview =
                new VBox();

        systemOverview.setSpacing(0);

        systemOverview.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );


        Label overviewTitle =
                new Label("System overview");

        overviewTitle.setPadding(
                new Insets(18, 22, 14, 22)
        );

        overviewTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        HBox overviewSeparator =
                new HBox();

        overviewSeparator.setPrefHeight(1);

        overviewSeparator.setStyle(
                "-fx-background-color: " + CARD_BORDER + ";"
        );


        overviewList =
                new VBox(3);

        overviewList.setPadding(
                new Insets(12, 22, 16, 22)
        );


        systemOverview.getChildren().addAll(
                overviewTitle,
                overviewSeparator,
                overviewList
        );


        // =====================================================
        // ADMIN TIP
        // =====================================================

        VBox adminTip =
                new VBox(7);

        adminTip.setPadding(
                new Insets(18, 22, 18, 22)
        );

        adminTip.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + "rgba(8, 12, 22, 0.95), rgba(0, 240, 255, 0.10));" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(0, 240, 255, 0.35);" +
                "-fx-border-radius: 18;"
        );


        Label tipIcon =
                new Label("✧");

        tipIcon.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 20px;"
        );


        Label tipTitle =
                new Label("Admin Tip");

        tipTitle.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label tipText =
                new Label(
                        "Monitor users, print centres, documents and "
                        + "print requests from the Admin panel."
                );

        tipText.setWrapText(true);

        tipText.setStyle(
                "-fx-text-fill: #D8D0EA;" +
                "-fx-font-size: 12px;"
        );


        adminTip.getChildren().addAll(
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

                systemOverview,

                adminTip
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
                "-fx-background-color: " + BACKGROUND + ";" +
                "-fx-border-color: transparent;"
        );


        // =====================================================
        // START REALTIME LISTENERS
        // =====================================================

        startRealtimeListeners();


        return scrollPane;
    }


    // =========================================================
    // START REALTIME FIRESTORE LISTENERS
    // =========================================================

    private void startRealtimeListeners() {

        stopRealtimeListeners();


        // =====================================================
        // USERS
        // =====================================================

        usersListener =
                db.collection("Users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[ADashboard] "
                                                + "Users listener error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    usersSnapshot =
                                            snapshot;

                                    refreshDashboard();
                                }
                        );


        // =====================================================
        // XEROX CENTRES
        // =====================================================

        centresListener =
                db.collection("XeroxCentres")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[ADashboard] "
                                                + "Centres listener error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    centresSnapshot =
                                            snapshot;

                                    refreshDashboard();
                                }
                        );


        // =====================================================
        // DOCUMENTS
        // =====================================================

        documentsListener =
                db.collection("Documents")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[ADashboard] "
                                                + "Documents listener error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    documentsSnapshot =
                                            snapshot;

                                    refreshDashboard();
                                }
                        );


        // =====================================================
        // PRINT REQUESTS
        // =====================================================

        requestsListener =
                db.collection("PrintRequests")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[ADashboard] "
                                                + "Requests listener error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    requestsSnapshot =
                                            snapshot;

                                    refreshDashboard();
                                }
                        );
    }


    // =========================================================
    // REFRESH DASHBOARD
    // =========================================================

    private void refreshDashboard() {

        Platform.runLater(() -> {

            updateStatistics();

            updateSystemOverview();

            updateRecentActivity();
        });
    }


    // =========================================================
    // UPDATE STATISTICS
    // =========================================================

    private void updateStatistics() {

        // -----------------------------------------------------
        // USERS
        // -----------------------------------------------------

        int totalUsers = 0;

        if (usersSnapshot != null) {

            totalUsers =
                    usersSnapshot.size();
        }


        // -----------------------------------------------------
        // CENTRES
        // -----------------------------------------------------

        int totalCentres = 0;
        int pendingCentres = 0;

        if (centresSnapshot != null) {

            totalCentres =
                    centresSnapshot.size();

            for (DocumentSnapshot document :
                    centresSnapshot.getDocuments()) {

                String status =
                        document.getString("status");

                if (status != null
                        && status.equalsIgnoreCase("PENDING")) {

                    pendingCentres++;
                }
            }
        }


        // -----------------------------------------------------
        // DOCUMENTS TODAY
        // -----------------------------------------------------

        int documentsToday = 0;

        if (documentsSnapshot != null) {

            LocalDate today =
                    LocalDate.now();

            for (DocumentSnapshot document :
                    documentsSnapshot.getDocuments()) {

                String uploadedAt =
                        document.getString("uploadedAt");

                if (isToday(uploadedAt, today)) {

                    documentsToday++;
                }
            }
        }


        // -----------------------------------------------------
        // ACTIVE SESSIONS
        // -----------------------------------------------------

        int activeSessions = 0;

        if (requestsSnapshot != null) {

            for (DocumentSnapshot document :
                    requestsSnapshot.getDocuments()) {

                String status =
                        document.getString("status");

                if (isActiveSession(status)) {

                    activeSessions++;
                }
            }
        }


        // -----------------------------------------------------
        // UPDATE UI
        // -----------------------------------------------------

        if (totalUsersLabel != null) {

            totalUsersLabel.setText(
                    String.valueOf(totalUsers)
            );
        }


        if (usersBottomLabel != null) {

            usersBottomLabel.setText(
                    "Registered PRIVORA users"
            );
        }


        if (totalCentresLabel != null) {

            totalCentresLabel.setText(
                    String.valueOf(totalCentres)
            );
        }


        if (centresBottomLabel != null) {

            centresBottomLabel.setText(
                    pendingCentres
                            + " pending approval"
            );
        }


        if (documentsTodayLabel != null) {

            documentsTodayLabel.setText(
                    String.valueOf(documentsToday)
            );
        }


        if (documentsBottomLabel != null) {

            documentsBottomLabel.setText(
                    "Uploaded today"
            );
        }


        if (activeSessionsLabel != null) {

            activeSessionsLabel.setText(
                    String.valueOf(activeSessions)
            );
        }


        if (sessionsBottomLabel != null) {

            sessionsBottomLabel.setText(
                    "Currently active"
            );
        }
    }


    // =========================================================
    // ACTIVE SESSION CHECK
    // =========================================================

    private boolean isActiveSession(
            String status) {

        if (status == null) {
            return false;
        }

        return status.equalsIgnoreCase("PENDING")
                || status.equalsIgnoreCase("ACCEPTED")
                || status.equalsIgnoreCase("APPROVED")
                || status.equalsIgnoreCase("PRINTING");
    }


    // =========================================================
    // CHECK TODAY
    // =========================================================

    private boolean isToday(
            String timestamp,
            LocalDate today) {

        if (timestamp == null
                || timestamp.isBlank()) {

            return false;
        }

        try {

            Instant instant =
                    Instant.parse(timestamp);

            LocalDate date =
                    instant
                            .atZone(
                                    ZoneId.systemDefault()
                            )
                            .toLocalDate();

            return date.equals(today);

        } catch (DateTimeParseException e) {

            return false;
        }
    }


    // =========================================================
    // UPDATE SYSTEM OVERVIEW
    // =========================================================

    private void updateSystemOverview() {

        if (overviewList == null) {
            return;
        }


        int totalUsers =
                usersSnapshot == null
                        ? 0
                        : usersSnapshot.size();


        int totalCentres =
                centresSnapshot == null
                        ? 0
                        : centresSnapshot.size();


        int pendingCentres = 0;

        if (centresSnapshot != null) {

            for (DocumentSnapshot document :
                    centresSnapshot.getDocuments()) {

                String status =
                        document.getString("status");

                if (status != null
                        && status.equalsIgnoreCase("PENDING")) {

                    pendingCentres++;
                }
            }
        }


        int activeSessions = 0;

        if (requestsSnapshot != null) {

            for (DocumentSnapshot document :
                    requestsSnapshot.getDocuments()) {

                if (isActiveSession(
                        document.getString("status"))) {

                    activeSessions++;
                }
            }
        }


        int privacyScansToday = 0;

        if (documentsSnapshot != null) {

            LocalDate today =
                    LocalDate.now();

            for (DocumentSnapshot document :
                    documentsSnapshot.getDocuments()) {

                String uploadedAt =
                        document.getString("uploadedAt");

                if (isToday(uploadedAt, today)) {

                    privacyScansToday++;
                }
            }
        }


        overviewList.getChildren().clear();


        overviewList.getChildren().addAll(

                createOverviewRow(
                        "User accounts",
                        String.valueOf(totalUsers),
                        "Healthy"
                ),

                createOverviewRow(
                        "Registered centres",
                        String.valueOf(totalCentres),
                        pendingCentres
                                + " pending"
                ),

                createOverviewRow(
                        "Active document sessions",
                        String.valueOf(activeSessions),
                        activeSessions > 0
                                ? "Active"
                                : "Normal"
                ),

                createOverviewRow(
                        "Documents uploaded today",
                        String.valueOf(
                                privacyScansToday
                        ),
                        "Secure"
                )
        );
    }


    // =========================================================
    // REALTIME RECENT ACTIVITY
    // =========================================================

    private void updateRecentActivity() {

        if (activityList == null) {
            return;
        }


        List<ActivityItem> activities =
                new ArrayList<>();


        // =====================================================
        // USERS
        // =====================================================

        if (usersSnapshot != null) {

            for (DocumentSnapshot document :
                    usersSnapshot.getDocuments()) {

                String name =
                        getString(
                                document,
                                "name"
                        );

                if (name == null
                        || name.isBlank()) {

                    name =
                            getString(
                                    document,
                                    "email"
                            );
                }


                String time =
                        getString(
                                document,
                                "createdAt"
                        );

                activities.add(
                        new ActivityItem(
                                "♙",
                                "New user registered",
                                name == null
                                        ? "User"
                                        : name,
                                time
                        )
                );
            }
        }


        // =====================================================
        // XEROX CENTRES
        // =====================================================

        if (centresSnapshot != null) {

            for (DocumentSnapshot document :
                    centresSnapshot.getDocuments()) {

                String name =
                        getString(
                                document,
                                "name"
                        );

                String status =
                        getString(
                                document,
                                "status"
                        );

                String time =
                        getString(
                                document,
                                "createdAt"
                        );

                String action =
                        "Xerox centre registered";

                if (status != null
                        && status.equalsIgnoreCase("ACTIVE")) {

                    action =
                            "Centre active";
                }


                activities.add(
                        new ActivityItem(
                                "▥",
                                action,
                                name == null
                                        ? "Print Centre"
                                        : name,
                                time
                        )
                );
            }
        }


        // =====================================================
        // DOCUMENTS
        // =====================================================

        if (documentsSnapshot != null) {

            for (DocumentSnapshot document :
                    documentsSnapshot.getDocuments()) {

                String fileName =
                        getString(
                                document,
                                "fileName"
                        );

                String time =
                        getString(
                                document,
                                "uploadedAt"
                        );

                activities.add(
                        new ActivityItem(
                                "▤",
                                "Document uploaded",
                                fileName == null
                                        ? "Document"
                                        : fileName,
                                time
                        )
                );
            }
        }


        // =====================================================
        // PRINT REQUESTS
        // =====================================================

        if (requestsSnapshot != null) {

            for (DocumentSnapshot document :
                    requestsSnapshot.getDocuments()) {

                String documentName =
                        getString(
                                document,
                                "documentName"
                        );

                String status =
                        getString(
                                document,
                                "status"
                        );

                String time =
                        getString(
                                document,
                                "requestedAt"
                        );


                String action =
                        "Print request created";


                if (status != null) {

                    if (status.equalsIgnoreCase(
                            "COMPLETED")) {

                        action =
                                "Print session completed";

                    } else if (status.equalsIgnoreCase(
                            "PRINTING")) {

                        action =
                                "Print session active";

                    } else if (status.equalsIgnoreCase(
                            "REJECTED")) {

                        action =
                                "Print request rejected";

                    } else if (status.equalsIgnoreCase(
                            "EXPIRED")) {

                        action =
                                "Print request expired";
                    }
                }


                activities.add(
                        new ActivityItem(
                                "▣",
                                action,
                                documentName == null
                                        ? "Print request"
                                        : documentName,
                                time
                        )
                );
            }
        }


        // =====================================================
        // SORT NEWEST FIRST
        // =====================================================

        activities.sort(
                Comparator.comparing(
                        ActivityItem::getInstant,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        // =====================================================
        // SHOW LATEST 8
        // =====================================================

        activityList.getChildren().clear();


        int count =
                Math.min(
                        activities.size(),
                        8
                );


        for (int i = 0; i < count; i++) {

            ActivityItem activity =
                    activities.get(i);


            activityList.getChildren().add(
                    createActivity(
                            activity.icon,
                            activity.action,
                            activity.description,
                            formatTime(
                                    activity.timestamp
                            )
                    )
            );
        }


        // -----------------------------------------------------
        // NO ACTIVITY
        // -----------------------------------------------------

        if (count == 0) {

            Label empty =
                    new Label(
                            "No recent activity"
                    );

            empty.setStyle(
                    "-fx-text-fill: "
                            + MUTED_TEXT + ";" +
                    "-fx-font-size: 12px;"
            );

            activityList.getChildren().add(
                    empty
            );
        }
    }


    // =========================================================
    // GET STRING
    // =========================================================

    private String getString(
            DocumentSnapshot document,
            String field) {

        Object value =
                document.get(field);

        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }


    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatTime(
            String timestamp) {

        if (timestamp == null
                || timestamp.isBlank()) {

            return "Recently";
        }


        try {

            Instant time =
                    Instant.parse(timestamp);

            long seconds =
                    java.time.Duration
                            .between(
                                    time,
                                    Instant.now()
                            )
                            .getSeconds();


            if (seconds < 60) {

                return "Just now";
            }


            long minutes =
                    seconds / 60;


            if (minutes < 60) {

                return minutes
                        + (minutes == 1
                                ? " min ago"
                                : " mins ago");
            }


            long hours =
                    minutes / 60;


            if (hours < 24) {

                return hours
                        + (hours == 1
                                ? " hr ago"
                                : " hrs ago");
            }


            long days =
                    hours / 24;


            return days
                    + (days == 1
                            ? " day ago"
                            : " days ago");

        } catch (Exception e) {

            return "Recently";
        }
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
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + CARD_BORDER + ";" +
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

        numberLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;"
        );


        Label bottomLabel =
                new Label(bottomText);

        bottomLabel.setStyle(
                "-fx-text-fill: " + VIOLET + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );


        card.getChildren().addAll(
                titleLabel,
                numberLabel,
                bottomLabel
        );


        /*
         * Store references to the labels so that
         * Firestore listeners can update them later.
         */
        card.getProperties().put(
                "numberLabel",
                numberLabel
        );

        card.getProperties().put(
                "bottomLabel",
                bottomLabel
        );


        return card;
    }


    // =========================================================
    // ACTIVITY ROW
    // =========================================================

    private HBox createActivity(
            String icon,
            String action,
            String person,
            String time) {


        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(7, 0, 7, 0)
        );


        // -----------------------------------------------------
        // ICON
        // -----------------------------------------------------

        StackPane iconBox =
                new StackPane();

        iconBox.setPrefSize(
                30,
                30
        );

        iconBox.setStyle(
                "-fx-background-color: #211B32;" +
                "-fx-background-radius: 8;"
        );


        Label iconLabel =
                new Label(icon);

        iconLabel.setStyle(
                "-fx-text-fill: " + VIOLET + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        iconBox.getChildren().add(
                iconLabel
        );


        // -----------------------------------------------------
        // TEXT
        // -----------------------------------------------------

        VBox textBox =
                new VBox(2);


        Label actionLabel =
                new Label(action);

        actionLabel.setStyle(
                "-fx-text-fill: #E9E5F2;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        Label personLabel =
                new Label(person);

        personLabel.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 11px;"
        );


        textBox.getChildren().addAll(
                actionLabel,
                personLabel
        );


        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        // -----------------------------------------------------
        // TIME
        // -----------------------------------------------------

        Label timeLabel =
                new Label(time);

        timeLabel.setStyle(
                "-fx-text-fill: " + MUTED_TEXT + ";" +
                "-fx-font-size: 11px;"
        );


        row.getChildren().addAll(
                iconBox,
                textBox,
                timeLabel
        );


        return row;
    }


    // =========================================================
    // SYSTEM OVERVIEW ROW
    // =========================================================

    private HBox createOverviewRow(
            String title,
            String value,
            String status) {


        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(8, 0, 8, 0)
        );


        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: #E9E5F2;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        HBox.setHgrow(
                titleLabel,
                Priority.ALWAYS
        );


        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        Label statusLabel =
                new Label(status);

        statusLabel.setStyle(
                "-fx-text-fill: " + VIOLET + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );


        row.getChildren().addAll(
                titleLabel,
                valueLabel,
                statusLabel
        );


        return row;
    }


    // =========================================================
    // STOP REALTIME LISTENERS
    // =========================================================

    public void stopRealtimeListeners() {

        if (usersListener != null) {

            usersListener.remove();

            usersListener = null;
        }


        if (centresListener != null) {

            centresListener.remove();

            centresListener = null;
        }


        if (documentsListener != null) {

            documentsListener.remove();

            documentsListener = null;
        }


        if (requestsListener != null) {

            requestsListener.remove();

            requestsListener = null;
        }
    }


    // =========================================================
    // ACTIVITY ITEM
    // =========================================================

    private static class ActivityItem {

        private final String icon;
        private final String action;
        private final String description;
        private final String timestamp;


        ActivityItem(
                String icon,
                String action,
                String description,
                String timestamp) {

            this.icon = icon;
            this.action = action;
            this.description = description;
            this.timestamp = timestamp;
        }


        private Instant getInstant() {

            if (timestamp == null
                    || timestamp.isBlank()) {

                return null;
            }


            try {

                return Instant.parse(
                        timestamp
                );

            } catch (Exception e) {

                return null;
            }
        }
    }
}

