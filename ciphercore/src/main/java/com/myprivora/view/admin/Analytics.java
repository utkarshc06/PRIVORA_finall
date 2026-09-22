package com.myprivora.view.admin;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.myprivora.config.DatabaseConfig;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.myprivora.view.theme.ClayTheme;

public class Analytics {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;
    private final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private final String CARD_HOVER = ClayTheme.OBSIDIAN_SURFACE;
    private final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private final String LIGHT_PURPLE = ClayTheme.CYAN_LIGHT;

    private final String TEXT = ClayTheme.FROST_WHITE;
    private final String SECONDARY = ClayTheme.FROST_MUTED;


    // =========================================================
    // FIRESTORE
    // =========================================================

    private final com.google.cloud.firestore.Firestore db =
            DatabaseConfig.getFirestore();


    // =========================================================
    // REAL-TIME LISTENERS
    // =========================================================

    private ListenerRegistration usersListener;
    private ListenerRegistration centresListener;
    private ListenerRegistration documentsListener;
    private ListenerRegistration requestsListener;

    private boolean listenersStarted = false;


    // =========================================================
    // REAL-TIME DATA
    // =========================================================

    private final Map<String, DocumentSnapshot> users =
            new java.util.concurrent.ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> centres =
            new java.util.concurrent.ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> documents =
            new java.util.concurrent.ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> requests =
            new java.util.concurrent.ConcurrentHashMap<>();


    // =========================================================
    // UI REFERENCES
    // =========================================================

    private Label usersValue;
    private Label centresValue;
    private Label documentsValue;
    private Label requestsValue;

    private Label usersChange;
    private Label centresChange;
    private Label documentsChange;
    private Label requestsChange;

    private LineChart<String, Number> requestTrendChart;
    private BarChart<String, Number> documentStatusChart;
    private PieChart requestStatusChart;
    private BarChart<String, Number> roleChart;


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox root =
                new VBox(22);

        root.setPadding(
                new Insets(
                        30,
                        38,
                        40,
                        38
                )
        );

        root.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title =
                new Label(
                        "Analytics"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 32px;"
                        + "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Real-time insights across users, centres, documents and print requests."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 14px;"
        );


        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );


        // =====================================================
        // STAT CARDS
        // =====================================================

        GridPane stats =
                new GridPane();

        stats.setHgap(15);
        stats.setVgap(15);


        VBox usersCard =
                createStatCard(
                        "REGISTERED USERS",
                        "0",
                        "Live"
                );

        usersValue =
                (Label) usersCard.getProperties()
                        .get("valueLabel");

        usersChange =
                (Label) usersCard.getProperties()
                        .get("changeLabel");


        VBox centresCard =
                createStatCard(
                        "XEROX CENTRES",
                        "0",
                        "Live"
                );

        centresValue =
                (Label) centresCard.getProperties()
                        .get("valueLabel");

        centresChange =
                (Label) centresCard.getProperties()
                        .get("changeLabel");


        VBox documentsCard =
                createStatCard(
                        "TOTAL DOCUMENTS",
                        "0",
                        "Live"
                );

        documentsValue =
                (Label) documentsCard.getProperties()
                        .get("valueLabel");

        documentsChange =
                (Label) documentsCard.getProperties()
                        .get("changeLabel");


        VBox requestsCard =
                createStatCard(
                        "PENDING REQUESTS",
                        "0",
                        "Live"
                );

        requestsValue =
                (Label) requestsCard.getProperties()
                        .get("valueLabel");

        requestsChange =
                (Label) requestsCard.getProperties()
                        .get("changeLabel");


        stats.add(
                usersCard,
                0,
                0
        );

        stats.add(
                centresCard,
                1,
                0
        );

        stats.add(
                documentsCard,
                0,
                1
        );

        stats.add(
                requestsCard,
                1,
                1
        );


        GridPane.setHgrow(
                usersCard,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                centresCard,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                documentsCard,
                Priority.ALWAYS
        );

        GridPane.setHgrow(
                requestsCard,
                Priority.ALWAYS
        );


        // =====================================================
        // PRINT REQUEST TREND
        // =====================================================

        VBox requestTrendCard =
                createChartCard(
                        "Print request trend",
                        "Requests created during the last 7 days."
                );


        CategoryAxis requestXAxis =
                new CategoryAxis();

        NumberAxis requestYAxis =
                new NumberAxis();


        requestXAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );

        requestYAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );


        requestTrendChart =
                new LineChart<>(
                        requestXAxis,
                        requestYAxis
                );


        requestTrendChart.setLegendVisible(
                false
        );

        requestTrendChart.setAnimated(
                false
        );

        requestTrendChart.setCreateSymbols(
                true
        );

        requestTrendChart.setPrefHeight(
                300
        );

        requestTrendChart.setStyle(
                "-fx-background-color: transparent;"
        );


        requestTrendCard.getChildren().add(
                requestTrendChart
        );


        // =====================================================
        // DOCUMENT STATUS
        // =====================================================

        VBox documentStatusCard =
                createChartCard(
                        "Documents by status",
                        "Current document status across PRIVORA."
                );


        CategoryAxis documentXAxis =
                new CategoryAxis();

        NumberAxis documentYAxis =
                new NumberAxis();


        documentXAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );

        documentYAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );


        documentStatusChart =
                new BarChart<>(
                        documentXAxis,
                        documentYAxis
                );


        documentStatusChart.setLegendVisible(
                false
        );

        documentStatusChart.setAnimated(
                false
        );

        documentStatusChart.setPrefHeight(
                300
        );


        documentStatusCard.getChildren().add(
                documentStatusChart
        );


        // =====================================================
        // PRINT REQUEST STATUS
        // =====================================================

        VBox requestStatusCard =
                createChartCard(
                        "Print request status",
                        "Distribution of current print request states."
                );


        requestStatusChart =
                new PieChart();


        requestStatusChart.setLegendVisible(
                true
        );

        requestStatusChart.setLabelsVisible(
                true
        );

        requestStatusChart.setAnimated(
                false
        );

        requestStatusChart.setPrefHeight(
                320
        );


        requestStatusCard.getChildren().add(
                requestStatusChart
        );


        // =====================================================
        // USERS BY ROLE
        // =====================================================

        VBox roleCard =
                createChartCard(
                        "Users by role",
                        "Current distribution of PRIVORA accounts."
                );


        CategoryAxis roleXAxis =
                new CategoryAxis();

        NumberAxis roleYAxis =
                new NumberAxis();


        roleXAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );

        roleYAxis.setTickLabelFill(
                Color.web("#9E94AD")
        );


        roleChart =
                new BarChart<>(
                        roleXAxis,
                        roleYAxis
                );


        roleChart.setLegendVisible(
                false
        );

        roleChart.setAnimated(
                false
        );

        roleChart.setPrefHeight(
                320
        );


        roleCard.getChildren().add(
                roleChart
        );


        // =====================================================
        // ADD TO ROOT
        // =====================================================

        root.getChildren().addAll(
                heading,
                stats,
                requestTrendCard,
                documentStatusCard,
                requestStatusCard,
                roleCard
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        root
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

        scrollPane.setPannable(
                true
        );

        scrollPane.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
                        + "-fx-background: "
                        + BACKGROUND
                        + ";"
                        + "-fx-border-color: transparent;"
        );


        // =====================================================
        // START FIRESTORE LISTENERS
        // =====================================================

        startRealtimeListeners();


        return scrollPane;
    }


    // =========================================================
    // START REAL-TIME LISTENERS
    // =========================================================

    private void startRealtimeListeners() {

        if (listenersStarted) {
            return;
        }

        listenersStarted = true;


        // =====================================================
        // USERS
        // =====================================================

        usersListener =
                db.collection("Users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {
                                        System.out.println(
                                                "Analytics Users Listener Error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }


                                    users.clear();


                                    for (DocumentSnapshot document :
                                            snapshot.getDocuments()) {

                                        users.put(
                                                document.getId(),
                                                document
                                        );
                                    }


                                    refreshAnalytics();
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
                                                "Analytics Centres Listener Error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }


                                    centres.clear();


                                    for (DocumentSnapshot document :
                                            snapshot.getDocuments()) {

                                        centres.put(
                                                document.getId(),
                                                document
                                        );
                                    }


                                    refreshAnalytics();
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
                                                "Analytics Documents Listener Error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }


                                    documents.clear();


                                    for (DocumentSnapshot document :
                                            snapshot.getDocuments()) {

                                        documents.put(
                                                document.getId(),
                                                document
                                        );
                                    }


                                    refreshAnalytics();
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
                                                "Analytics Requests Listener Error:"
                                        );

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }


                                    requests.clear();


                                    for (DocumentSnapshot document :
                                            snapshot.getDocuments()) {

                                        requests.put(
                                                document.getId(),
                                                document
                                        );
                                    }


                                    refreshAnalytics();
                                }
                        );
    }


    // =========================================================
    // REFRESH EVERYTHING
    // =========================================================

    private void refreshAnalytics() {

        Platform.runLater(
                () -> {

                    updateStatCards();

                    updateRequestTrend();

                    updateDocumentStatus();

                    updateRequestStatus();

                    updateRoleChart();
                }
        );
    }


    // =========================================================
    // UPDATE STAT CARDS
    // =========================================================

    private void updateStatCards() {

        int totalUsers =
                users.size();


        int totalCentres =
                centres.size();


        int totalDocuments =
                documents.size();


        int pendingRequests =
                0;


        for (DocumentSnapshot request :
                requests.values()) {

            String status =
                    getString(
                            request,
                            "status"
                    );


            if ("PENDING".equalsIgnoreCase(
                    status
            )) {

                pendingRequests++;
            }
        }


        usersValue.setText(
                String.valueOf(
                        totalUsers
                )
        );


        centresValue.setText(
                String.valueOf(
                        totalCentres
                )
        );


        documentsValue.setText(
                String.valueOf(
                        totalDocuments
                )
        );


        requestsValue.setText(
                String.valueOf(
                        pendingRequests
                )
        );


        usersChange.setText(
                "Live Firestore data"
        );

        centresChange.setText(
                "Live Firestore data"
        );

        documentsChange.setText(
                "Live Firestore data"
        );

        requestsChange.setText(
                "Currently pending"
        );
    }


    // =========================================================
    // PRINT REQUEST TREND
    // =========================================================

    private void updateRequestTrend() {

        if (requestTrendChart == null) {
            return;
        }


        requestTrendChart.getData().clear();


        XYChart.Series<String, Number> series =
                new XYChart.Series<>();


        LocalDate today =
                LocalDate.now();


        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM",
                        Locale.ENGLISH
                );


        Map<LocalDate, Integer> dailyCounts =
                new HashMap<>();


        for (int i = 6; i >= 0; i--) {

            LocalDate date =
                    today.minusDays(i);

            dailyCounts.put(
                    date,
                    0
            );
        }


        for (DocumentSnapshot request :
                requests.values()) {

            Object requestedAt =
                    request.get("requestedAt");


            LocalDate date =
                    convertToLocalDate(
                            requestedAt
                    );


            if (date == null) {
                continue;
            }


            if (dailyCounts.containsKey(
                    date
            )) {

                dailyCounts.put(
                        date,
                        dailyCounts.get(date) + 1
                );
            }
        }


        for (int i = 6; i >= 0; i--) {

            LocalDate date =
                    today.minusDays(i);


            series.getData().add(
                    new XYChart.Data<>(
                            date.format(
                                    formatter
                            ),
                            dailyCounts.get(
                                    date
                            )
                    )
            );
        }


        requestTrendChart.getData().add(
                series
        );
    }


    // =========================================================
    // DOCUMENT STATUS CHART
    // =========================================================

    private void updateDocumentStatus() {

        if (documentStatusChart == null) {
            return;
        }


        documentStatusChart.getData().clear();


        Map<String, Integer> statusCounts =
                new HashMap<>();


        for (DocumentSnapshot document :
                documents.values()) {

            String status =
                    getString(
                            document,
                            "status"
                    );


            if (status == null ||
                    status.isBlank()) {

                status = "ACTIVE";
            }


            status =
                    status.toUpperCase();


            statusCounts.put(
                    status,
                    statusCounts.getOrDefault(
                            status,
                            0
                    ) + 1
            );
        }


        XYChart.Series<String, Number> series =
                new XYChart.Series<>();


        for (Map.Entry<String, Integer> entry :
                statusCounts.entrySet()) {

            series.getData().add(
                    new XYChart.Data<>(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }


        documentStatusChart.getData().add(
                series
        );
    }


    // =========================================================
    // REQUEST STATUS PIE CHART
    // =========================================================

    private void updateRequestStatus() {

        if (requestStatusChart == null) {
            return;
        }


        requestStatusChart.getData().clear();


        Map<String, Integer> statusCounts =
                new HashMap<>();


        for (DocumentSnapshot request :
                requests.values()) {

            String status =
                    getString(
                            request,
                            "status"
                    );


            if (status == null ||
                    status.isBlank()) {

                status = "UNKNOWN";
            }


            status =
                    status.toUpperCase();


            statusCounts.put(
                    status,
                    statusCounts.getOrDefault(
                            status,
                            0
                    ) + 1
            );
        }


        for (Map.Entry<String, Integer> entry :
                statusCounts.entrySet()) {

            requestStatusChart.getData().add(
                    new PieChart.Data(
                            entry.getKey()
                                    + " "
                                    + entry.getValue(),
                            entry.getValue()
                    )
            );
        }
    }


    // =========================================================
    // USER ROLE CHART
    // =========================================================

    private void updateRoleChart() {

        if (roleChart == null) {
            return;
        }


        roleChart.getData().clear();


        Map<String, Integer> roleCounts =
                new HashMap<>();


        for (DocumentSnapshot user :
                users.values()) {

            String role =
                    getString(
                            user,
                            "role"
                    );


            if (role == null ||
                    role.isBlank()) {

                role = "UNKNOWN";
            }


            role =
                    role.toUpperCase();


            roleCounts.put(
                    role,
                    roleCounts.getOrDefault(
                            role,
                            0
                    ) + 1
            );
        }


        XYChart.Series<String, Number> series =
                new XYChart.Series<>();


        for (Map.Entry<String, Integer> entry :
                roleCounts.entrySet()) {

            series.getData().add(
                    new XYChart.Data<>(
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }


        roleChart.getData().add(
                series
        );
    }


    // =========================================================
    // SAFE STRING VALUE
    // =========================================================

    private String getString(
            DocumentSnapshot document,
            String field
    ) {

        Object value =
                document.get(field);


        if (value == null) {
            return null;
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // CONVERT FIRESTORE TIME
    // =========================================================

    private LocalDate convertToLocalDate(
            Object value
    ) {

        if (value == null) {
            return null;
        }


        try {

            // -------------------------------------------------
            // Firestore Timestamp
            // -------------------------------------------------

            if (value instanceof Timestamp) {

                Timestamp timestamp =
                        (Timestamp) value;

                return timestamp
                        .toDate()
                        .toInstant()
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toLocalDate();
            }


            // -------------------------------------------------
            // Java Date
            // -------------------------------------------------

            if (value instanceof java.util.Date) {

                java.util.Date date =
                        (java.util.Date) value;

                return date.toInstant()
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toLocalDate();
            }


            // -------------------------------------------------
            // Instant
            // -------------------------------------------------

            if (value instanceof Instant) {

                return ((Instant) value)
                        .atZone(
                                ZoneId.systemDefault()
                        )
                        .toLocalDate();
            }


            // -------------------------------------------------
            // String
            // -------------------------------------------------

            if (value instanceof String) {

                String text =
                        ((String) value).trim();


                // ISO timestamp
                try {

                    return Instant
                            .parse(text)
                            .atZone(
                                    ZoneId.systemDefault()
                            )
                            .toLocalDate();

                } catch (Exception ignored) {
                }


                // LocalDate
                try {

                    return LocalDate.parse(
                            text
                    );

                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to convert timestamp: "
                            + value
            );
        }


        return null;
    }


    // =========================================================
    // STAT CARD
    // =========================================================

    private VBox createStatCard(
            String title,
            String value,
            String change
    ) {

        VBox card =
                new VBox(7);


        card.setPadding(
                new Insets(20)
        );


        card.setPrefHeight(
                115
        );


        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-background-radius: 20;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 20;"
        );


        Label titleLabel =
                new Label(
                        title
                );


        titleLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
        );


        Label valueLabel =
                new Label(
                        value
                );


        valueLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 27px;"
                        + "-fx-font-weight: bold;"
        );


        Label changeLabel =
                new Label(
                        change
                );


        changeLabel.setStyle(
                "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        + "-fx-font-size: 10px;"
                        + "-fx-font-weight: bold;"
        );


        card.getProperties().put(
                "valueLabel",
                valueLabel
        );


        card.getProperties().put(
                "changeLabel",
                changeLabel
        );


        HBox.setHgrow(
                card,
                Priority.ALWAYS
        );


        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                changeLabel
        );


        // =====================================================
        // HOVER
        // =====================================================

        card.setOnMouseEntered(
                e ->
                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD_HOVER
                                        + ";"
                                        + "-fx-background-radius: 20;"
                                        + "-fx-border-color: "
                                        + PURPLE
                                        + ";"
                                        + "-fx-border-radius: 20;"
                        )
        );


        card.setOnMouseExited(
                e ->
                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD
                                        + ";"
                                        + "-fx-background-radius: 20;"
                                        + "-fx-border-color: "
                                        + BORDER
                                        + ";"
                                        + "-fx-border-radius: 20;"
                        )
        );


        return card;
    }


    // =========================================================
    // CHART CARD
    // =========================================================

    private VBox createChartCard(
            String title,
            String description
    ) {

        VBox card =
                new VBox();


        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-background-radius: 20;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 20;"
        );


        Label titleLabel =
                new Label(
                        title
                );


        titleLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
        );


        Label descriptionLabel =
                new Label(
                        description
                );


        descriptionLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 11px;"
        );


        VBox titleBox =
                new VBox(
                        4,
                        titleLabel,
                        descriptionLabel
                );


        HBox header =
                new HBox(
                        titleBox
                );


        header.setAlignment(
                Pos.CENTER_LEFT
        );


        header.setPadding(
                new Insets(
                        18,
                        20,
                        16,
                        20
                )
        );


        header.setStyle(
                "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-width: 0 0 1 0;"
        );


        card.getChildren().add(
                header
        );


        return card;
    }


    // =========================================================
    // STOP REAL-TIME LISTENERS
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


        listenersStarted = false;


        System.out.println(
                "Analytics real-time listeners stopped."
        );
    }
}