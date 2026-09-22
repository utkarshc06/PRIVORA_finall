package com.myprivora.view.admin;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.Document;
import com.myprivora.model.PrintRequest;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.myprivora.view.theme.ClayTheme;

public class Reports {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;
    private final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private final String CARD_HOVER = ClayTheme.OBSIDIAN_SURFACE;
    private final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private final String TEXT = ClayTheme.FROST_WHITE;
    private final String SECONDARY = ClayTheme.FROST_MUTED;

    private final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private final String LIGHT_PURPLE = ClayTheme.CYAN_LIGHT;
    private final String DARK_PURPLE = ClayTheme.CYAN_ACCENT;

    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore db =
            DatabaseConfig.getFirestore();

    private ListenerRegistration documentsListener;
    private ListenerRegistration printRequestsListener;
    private ListenerRegistration usersListener;
    private ListenerRegistration centresListener;

    // =========================================================
    // REAL-TIME DATA
    // =========================================================

    private final List<Document> documents =
            new ArrayList<>();

    private final List<PrintRequest> printRequests =
            new ArrayList<>();

    /*
     * userId -> role
     */
    private final Map<String, String> userRoles =
            new ConcurrentHashMap<>();

    /*
     * userId -> name
     */
    private final Map<String, String> userNames =
            new ConcurrentHashMap<>();

    /*
     * centreId -> centre name
     */
    private final Map<String, String> centreNames =
            new ConcurrentHashMap<>();

    private volatile int totalUsers = 0;
    private volatile int totalCentres = 0;

    // =========================================================
    // UI REFERENCES
    // =========================================================

    private Label dailyValueLabel;
    private Label weeklyValueLabel;
    private Label monthlyValueLabel;

    private Label dailyDescriptionLabel;
    private Label weeklyDescriptionLabel;
    private Label monthlyDescriptionLabel;

    private Label customResultLabel;

    // =========================================================
    // COMBO BOX REFERENCES
    // =========================================================

    private ComboBox<String> fromCombo;
    private ComboBox<String> toCombo;
    private ComboBox<String> segmentCombo;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox root =
                new VBox(25);

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
                        "Reports"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "Generate real-time reports from PRIVORA activity."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 15px;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        // =====================================================
        // REPORT CARDS
        // =====================================================

        HBox reportCards =
                new HBox(20);

        VBox daily =
                createReportCard(
                        "▣",
                        "Daily Report",
                        "Today's activity"
                );

        VBox weekly =
                createReportCard(
                        "▤",
                        "Weekly Report",
                        "Last 7 days activity"
                );

        VBox monthly =
                createReportCard(
                        "▥",
                        "Monthly Report",
                        "Last 30 days activity"
                );

        HBox.setHgrow(
                daily,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                weekly,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                monthly,
                Priority.ALWAYS
        );

        reportCards.getChildren().addAll(
                daily,
                weekly,
                monthly
        );

        // =====================================================
        // CUSTOM REPORT CARD
        // =====================================================

        VBox customCard =
                new VBox();

        customCard.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 22;"
        );

        // =====================================================
        // CUSTOM HEADER
        // =====================================================

        VBox customHeader =
                new VBox(5);

        customHeader.setPadding(
                new Insets(
                        22,
                        28,
                        20,
                        28
                )
        );

        Label customTitle =
                new Label(
                        "Custom report"
                );

        customTitle.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );

        Label customSubtitle =
                new Label(
                        "Build a report using live PRIVORA data."
                );

        customSubtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 13px;"
        );

        customHeader.getChildren().addAll(
                customTitle,
                customSubtitle
        );

        customHeader.setStyle(
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        // =====================================================
        // CUSTOM FORM
        // =====================================================

        HBox form =
                new HBox(20);

        form.setPadding(
                new Insets(
                        25,
                        28,
                        15,
                        28
                )
        );

        VBox fromBox =
                createDateComboBox(
                        "From"
                );

        VBox toBox =
                createDateComboBox(
                        "To"
                );

        VBox segmentBox =
                createSegmentCombo();

        Button generateButton =
                createGenerateButton();

        HBox.setHgrow(
                fromBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                toBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                segmentBox,
                Priority.ALWAYS
        );

        form.getChildren().addAll(
                fromBox,
                toBox,
                segmentBox,
                generateButton
        );

        // =====================================================
        // CUSTOM RESULT
        // =====================================================

        customResultLabel =
                new Label(
                        "Select a date range and segment to generate a report."
                );

        customResultLabel.setWrapText(
                true
        );

        customResultLabel.setPadding(
                new Insets(
                        0,
                        28,
                        25,
                        28
                )
        );

        customResultLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 13px;"
        );

        customCard.getChildren().addAll(
                customHeader,
                form,
                customResultLabel
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        root.getChildren().addAll(
                heading,
                reportCards,
                customCard
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(
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
                        + ";" +
                "-fx-background: "
                        + BACKGROUND
                        + ";" +
                "-fx-border-color: transparent;"
        );

        // =====================================================
        // START FIRESTORE LISTENERS
        // =====================================================

        startRealtimeListeners();

        return scrollPane;
    }

    // =========================================================
    // REPORT CARD
    // =========================================================

    private VBox createReportCard(
            String icon,
            String title,
            String description
    ) {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(25)
        );

        card.setPrefHeight(
                275
        );

        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 22;"
        );

        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel =
                new Label(
                        icon
                );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                55,
                55
        );

        iconLabel.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, "
                        + PURPLE
                        + ", "
                        + LIGHT_PURPLE
                        + ");" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
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
                        + TEXT
                        + ";" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 13px;"
        );

        // =====================================================
        // LIVE VALUE
        // =====================================================

        Label valueLabel =
                new Label(
                        "Loading..."
                );

        valueLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        HBox buttons =
                new HBox(10);

        Button pdfButton =
                createReportButton(
                        "▣  PDF",
                        false
                );

        Button excelButton =
                createReportButton(
                        "▦  Excel",
                        true
                );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        pdfButton.setOnAction(
                e ->
                        showMessage(
                                title,
                                buildReportSummary(title)
                        )
        );

        excelButton.setOnAction(
                e ->
                        showMessage(
                                title,
                                buildReportSummary(title)
                        )
        );

        buttons.getChildren().addAll(
                pdfButton,
                excelButton
        );

        // =====================================================
        // SAVE REFERENCES
        // =====================================================

        if (
                title.equals(
                        "Daily Report"
                )
        ) {

            dailyValueLabel =
                    valueLabel;

            dailyDescriptionLabel =
                    descriptionLabel;

        } else if (
                title.equals(
                        "Weekly Report"
                )
        ) {

            weeklyValueLabel =
                    valueLabel;

            weeklyDescriptionLabel =
                    descriptionLabel;

        } else if (
                title.equals(
                        "Monthly Report"
                )
        ) {

            monthlyValueLabel =
                    valueLabel;

            monthlyDescriptionLabel =
                    descriptionLabel;
        }

        // =====================================================
        // ADD CONTENT
        // =====================================================

        card.getChildren().addAll(
                iconLabel,
                titleLabel,
                descriptionLabel,
                valueLabel,
                buttons
        );

        addHover(
                card
        );

        return card;
    }

    // =========================================================
    // REPORT BUTTON
    // =========================================================

    private Button createReportButton(
            String text,
            boolean purpleButton
    ) {

        Button button =
                new Button(
                        text
                );

        button.setPrefHeight(
                40
        );

        if (purpleButton) {

            button.setStyle(
                    "-fx-background-color: "
                            + PURPLE
                            + ";" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
            );

            button.setOnMouseEntered(
                    e ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + LIGHT_PURPLE
                                            + ";" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-text-fill: #0B0A0F;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;"
                            )
            );

            button.setOnMouseExited(
                    e ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + PURPLE
                                            + ";" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;"
                            )
            );

        } else {

            button.setStyle(
                    "-fx-background-color: "
                            + BACKGROUND
                            + ";" +
                    "-fx-border-color: "
                            + BORDER
                            + ";" +
                    "-fx-border-radius: 14;" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: "
                            + TEXT
                            + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
            );

            button.setOnMouseEntered(
                    e ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + CARD_HOVER
                                            + ";" +
                                    "-fx-border-color: "
                                            + PURPLE
                                            + ";" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-text-fill: "
                                            + LIGHT_PURPLE
                                            + ";" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;"
                            )
            );

            button.setOnMouseExited(
                    e ->
                            button.setStyle(
                                    "-fx-background-color: "
                                            + BACKGROUND
                                            + ";" +
                                    "-fx-border-color: "
                                            + BORDER
                                            + ";" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-text-fill: "
                                            + TEXT
                                            + ";" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-cursor: hand;"
                            )
            );
        }

        return button;
    }

    // =========================================================
    // DATE COMBO BOX
    // =========================================================

    private VBox createDateComboBox(
            String title
    ) {

        VBox box =
                new VBox(7);

        Label label =
                new Label(
                        title
                );

        label.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        ComboBox<String> combo =
                new ComboBox<>();

        combo.getItems().addAll(
                "Today",
                "Yesterday",
                "Last 7 days",
                "Last 30 days"
        );

        if (
                title.equals(
                        "From"
                )
        ) {

            combo.setValue(
                    "Last 7 days"
            );

            fromCombo =
                    combo;

        } else {

            combo.setValue(
                    "Today"
            );

            toCombo =
                    combo;
        }

        combo.setPrefHeight(
                55
        );

        combo.setMaxWidth(
                Double.MAX_VALUE
        );

        combo.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #302A43;" +
                "-fx-border-radius: 18;" +
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 13px;"
        );

        box.getChildren().addAll(
                label,
                combo
        );

        return box;
    }

    // =========================================================
    // SEGMENT COMBO
    // =========================================================

    private VBox createSegmentCombo() {

        VBox box =
                new VBox(7);

        Label label =
                new Label(
                        "Segment"
                );

        label.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        segmentCombo =
                new ComboBox<>();

        segmentCombo.getItems().addAll(
                "All",
                "Users",
                "Centres",
                "Admins"
        );

        segmentCombo.setValue(
                "All"
        );

        segmentCombo.setPrefHeight(
                55
        );

        segmentCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        segmentCombo.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #302A43;" +
                "-fx-border-radius: 18;" +
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 13px;"
        );

        box.getChildren().addAll(
                label,
                segmentCombo
        );

        return box;
    }

    // =========================================================
    // GENERATE BUTTON
    // =========================================================

    private Button createGenerateButton() {

        Button button =
                new Button(
                        "↓   Generate"
                );

        button.setPrefHeight(
                55
        );

        button.setPrefWidth(
                175
        );

        button.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to right, "
                        + PURPLE
                        + ", "
                        + LIGHT_PURPLE
                        + ");" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(
                e ->
                        button.setStyle(
                                "-fx-background-color: "
                                        + LIGHT_PURPLE
                                        + ";" +
                                "-fx-background-radius: 18;" +
                                "-fx-text-fill: #0B0A0F;" +
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;"
                        )
        );

        button.setOnMouseExited(
                e ->
                        button.setStyle(
                                "-fx-background-color: linear-gradient("
                                        + "to right, "
                                        + PURPLE
                                        + ", "
                                        + LIGHT_PURPLE
                                        + ");" +
                                "-fx-background-radius: 18;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 15px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-cursor: hand;"
                        )
        );

        // =====================================================
        // GENERATE ACTION
        // =====================================================

        button.setOnAction(
                e ->
                        generateCustomReport()
        );

        return button;
    }

    // =========================================================
    // FIRESTORE REAL-TIME LISTENERS
    // =========================================================

    private void startRealtimeListeners() {

        stopRealtimeListeners();

        // =====================================================
        // DOCUMENTS
        // =====================================================

        documentsListener =
                db.collection("Documents")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    List<Document> newDocuments =
                                            new ArrayList<>();

                                    for (
                                            DocumentSnapshot snapshotDocument
                                            : snapshot.getDocuments()
                                    ) {

                                        Document document =
                                                snapshotDocument.toObject(
                                                        Document.class
                                                );

                                        if (document == null) {
                                            continue;
                                        }

                                        if (
                                                document.getDocumentId()
                                                        == null
                                        ) {

                                            document.setDocumentId(
                                                    snapshotDocument.getId()
                                            );
                                        }

                                        newDocuments.add(
                                                document
                                        );
                                    }

                                    synchronized (documents) {

                                        documents.clear();

                                        documents.addAll(
                                                newDocuments
                                        );
                                    }

                                    updateReportCards();
                                }
                        );

        // =====================================================
        // PRINT REQUESTS
        // =====================================================

        printRequestsListener =
                db.collection("PrintRequests")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    List<PrintRequest> newRequests =
                                            new ArrayList<>();

                                    for (
                                            DocumentSnapshot snapshotDocument
                                            : snapshot.getDocuments()
                                    ) {

                                        PrintRequest request =
                                                snapshotDocument.toObject(
                                                        PrintRequest.class
                                                );

                                        if (request == null) {
                                            continue;
                                        }

                                        if (
                                                request.getRequestId()
                                                        == null
                                        ) {

                                            request.setRequestId(
                                                    snapshotDocument.getId()
                                            );
                                        }

                                        newRequests.add(
                                                request
                                        );
                                    }

                                    synchronized (printRequests) {

                                        printRequests.clear();

                                        printRequests.addAll(
                                                newRequests
                                        );
                                    }

                                    updateReportCards();
                                }
                        );

        // =====================================================
        // USERS
        // =====================================================

        usersListener =
                db.collection("Users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    userRoles.clear();

                                    userNames.clear();

                                    for (
                                            DocumentSnapshot userDocument
                                            : snapshot.getDocuments()
                                    ) {

                                        String userId =
                                                userDocument.getId();

                                        String role =
                                                userDocument.getString(
                                                        "role"
                                                );

                                        String name =
                                                userDocument.getString(
                                                        "name"
                                                );

                                        if (
                                                role == null
                                        ) {

                                            role = "";
                                        }

                                        if (
                                                name == null
                                                        ||
                                                name.trim().isEmpty()
                                        ) {

                                            name =
                                                    userDocument.getString(
                                                            "email"
                                                    );
                                        }

                                        if (
                                                name == null
                                                        ||
                                                name.trim().isEmpty()
                                        ) {

                                            name =
                                                    "Unknown User";
                                        }

                                        userRoles.put(
                                                userId,
                                                role
                                        );

                                        userNames.put(
                                                userId,
                                                name
                                        );
                                    }

                                    totalUsers =
                                            snapshot.size();

                                    updateReportCards();
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

                                        error.printStackTrace();

                                        return;
                                    }

                                    if (snapshot == null) {
                                        return;
                                    }

                                    centreNames.clear();

                                    for (
                                            DocumentSnapshot centreDocument
                                            : snapshot.getDocuments()
                                    ) {

                                        String centreId =
                                                centreDocument.getId();

                                        String name =
                                                centreDocument.getString(
                                                        "name"
                                                );

                                        if (
                                                name == null
                                                        ||
                                                name.trim().isEmpty()
                                        ) {

                                            name =
                                                    "Unknown Centre";
                                        }

                                        centreNames.put(
                                                centreId,
                                                name
                                        );
                                    }

                                    totalCentres =
                                            snapshot.size();

                                    updateReportCards();
                                }
                        );
    }

    // =========================================================
    // UPDATE REPORT CARDS
    // =========================================================

    private void updateReportCards() {

        Platform.runLater(
                () -> {

                    LocalDate today =
                            LocalDate.now();

                    LocalDate sevenDaysAgo =
                            today.minusDays(6);

                    LocalDate thirtyDaysAgo =
                            today.minusDays(29);

                    int todayDocuments =
                            countDocumentsBetween(
                                    today,
                                    today
                            );

                    int weeklyDocuments =
                            countDocumentsBetween(
                                    sevenDaysAgo,
                                    today
                            );

                    int monthlyDocuments =
                            countDocumentsBetween(
                                    thirtyDaysAgo,
                                    today
                            );

                    int todayPrints =
                            countCompletedPrintsBetween(
                                    today,
                                    today
                            );

                    int weeklyPrints =
                            countCompletedPrintsBetween(
                                    sevenDaysAgo,
                                    today
                            );

                    int monthlyPrints =
                            countCompletedPrintsBetween(
                                    thirtyDaysAgo,
                                    today
                            );

                    int todayRequests =
                            countRequestsBetween(
                                    today,
                                    today
                            );

                    int weeklyRequests =
                            countRequestsBetween(
                                    sevenDaysAgo,
                                    today
                            );

                    int monthlyRequests =
                            countRequestsBetween(
                                    thirtyDaysAgo,
                                    today
                            );

                    // =========================================
                    // DAILY
                    // =========================================

                    if (
                            dailyValueLabel != null
                    ) {

                        dailyValueLabel.setText(
                                String.valueOf(
                                        todayDocuments
                                )
                        );
                    }

                    if (
                            dailyDescriptionLabel != null
                    ) {

                        dailyDescriptionLabel.setText(
                                todayDocuments +
                                " documents • " +
                                todayRequests +
                                " requests • " +
                                todayPrints +
                                " completed prints"
                        );
                    }

                    // =========================================
                    // WEEKLY
                    // =========================================

                    if (
                            weeklyValueLabel != null
                    ) {

                        weeklyValueLabel.setText(
                                String.valueOf(
                                        weeklyDocuments
                                )
                        );
                    }

                    if (
                            weeklyDescriptionLabel != null
                    ) {

                        weeklyDescriptionLabel.setText(
                                weeklyDocuments +
                                " documents • " +
                                weeklyRequests +
                                " requests • " +
                                weeklyPrints +
                                " completed prints"
                        );
                    }

                    // =========================================
                    // MONTHLY
                    // =========================================

                    if (
                            monthlyValueLabel != null
                    ) {

                        monthlyValueLabel.setText(
                                String.valueOf(
                                        monthlyDocuments
                                )
                        );
                    }

                    if (
                            monthlyDescriptionLabel != null
                    ) {

                        monthlyDescriptionLabel.setText(
                                monthlyDocuments +
                                " documents • " +
                                monthlyRequests +
                                " requests • " +
                                monthlyPrints +
                                " completed prints"
                        );
                    }
                }
        );
    }

    // =========================================================
    // DOCUMENT COUNT
    // =========================================================

    private int countDocumentsBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {

        int count = 0;

        synchronized (documents) {

            for (
                    Document document
                    : documents
            ) {

                LocalDate date =
                        parseDate(
                                document.getUploadedAt()
                        );

                if (
                        isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // COMPLETED PRINT COUNT
    // =========================================================

    private int countCompletedPrintsBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                if (
                        "COMPLETED".equalsIgnoreCase(
                                request.getStatus()
                        )
                ) {

                    count +=
                            Math.max(
                                    0,
                                    request.getPrintedCount()
                            );
                }
            }
        }

        return count;
    }

    // =========================================================
    // REQUEST COUNT
    // =========================================================

    private int countRequestsBetween(
            LocalDate startDate,
            LocalDate endDate
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // DATE PARSER
    // =========================================================

    private LocalDate parseDate(
            String value
    ) {

        if (
                value == null
                        ||
                value.trim().isEmpty()
        ) {

            return null;
        }

        // =====================================================
        // ISO DATE/TIME
        // =====================================================

        try {

            return Instant.parse(
                    value
            )
            .atZone(
                    ZoneId.systemDefault()
            )
            .toLocalDate();

        } catch (Exception ignored) {
        }

        // =====================================================
        // NORMAL LOCAL DATE
        // =====================================================

        try {

            return LocalDate.parse(
                    value
            );

        } catch (Exception ignored) {
        }

        return null;
    }

    // =========================================================
    // CUSTOM REPORT
    // =========================================================

    private void generateCustomReport() {

        if (
                fromCombo == null
                        ||
                toCombo == null
                        ||
                segmentCombo == null
        ) {

            showMessage(
                    "Report",
                    "Report controls are not ready yet."
            );

            return;
        }

        String from =
                fromCombo.getValue();

        String to =
                toCombo.getValue();

        String segment =
                segmentCombo.getValue();

        if (
                from == null
                        ||
                to == null
                        ||
                segment == null
        ) {

            showMessage(
                    "Report",
                    "Please select all report options."
            );

            return;
        }

        LocalDate today =
                LocalDate.now();

        LocalDate startDate;

        LocalDate endDate;

        // =====================================================
        // FROM DATE
        // =====================================================

        switch (from) {

            case "Today":

                startDate =
                        today;

                break;

            case "Yesterday":

                startDate =
                        today.minusDays(1);

                break;

            case "Last 7 days":

                startDate =
                        today.minusDays(6);

                break;

            case "Last 30 days":

                startDate =
                        today.minusDays(29);

                break;

            default:

                startDate =
                        today.minusDays(6);
        }

        // =====================================================
        // TO DATE
        // =====================================================

        switch (to) {

            case "Today":

                endDate =
                        today;

                break;

            case "Yesterday":

                endDate =
                        today.minusDays(1);

                break;

            case "Last 7 days":

                endDate =
                        today;

                break;

            case "Last 30 days":

                endDate =
                        today;

                break;

            default:

                endDate =
                        today;
        }

        // =====================================================
        // VALIDATE
        // =====================================================

        if (
                startDate.isAfter(
                        endDate
                )
        ) {

            showMessage(
                    "Invalid Date Range",
                    "The From date cannot be after the To date."
            );

            return;
        }

        // =====================================================
        // CALCULATE
        // =====================================================

        int documentCount =
                countDocumentsForSegment(
                        startDate,
                        endDate,
                        segment
                );

        int requestCount =
                countRequestsForSegment(
                        startDate,
                        endDate,
                        segment
                );

        int printCount =
                countPrintsForSegment(
                        startDate,
                        endDate,
                        segment
                );

        int completedRequests =
                countCompletedRequestsForSegment(
                        startDate,
                        endDate,
                        segment
                );

        int expiredRequests =
                countStatusRequestsForSegment(
                        startDate,
                        endDate,
                        segment,
                        "EXPIRED"
                );

        int rejectedRequests =
                countStatusRequestsForSegment(
                        startDate,
                        endDate,
                        segment,
                        "REJECTED"
                );

        int activeRequests =
                countActiveRequestsForSegment(
                        startDate,
                        endDate,
                        segment
                );

        // =====================================================
        // DISPLAY RESULT
        // =====================================================

        customResultLabel.setText(
                "Report generated from " +
                startDate +
                " to " +
                endDate +
                "  |  Segment: " +
                segment +

                "\n\n" +

                "Documents uploaded: " +
                documentCount +

                "    •    Print requests: " +
                requestCount +

                "    •    Completed requests: " +
                completedRequests +

                "\n" +

                "Printed copies: " +
                printCount +

                "    •    Active requests: " +
                activeRequests +

                "    •    Expired: " +
                expiredRequests +

                "    •    Rejected: " +
                rejectedRequests +

                "\n\n" +

                "Registered users: " +
                totalUsers +

                "    •    Registered centres: " +
                totalCentres
        );
    }

    // =========================================================
    // DOCUMENTS FOR SEGMENT
    // =========================================================

    private int countDocumentsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment
    ) {

        int count = 0;

        synchronized (documents) {

            for (
                    Document document
                    : documents
            ) {

                LocalDate date =
                        parseDate(
                                document.getUploadedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                if (
                        segmentMatchesUser(
                                document.getOwnerId(),
                                segment
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // REQUESTS FOR SEGMENT
    // =========================================================

    private int countRequestsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                if (
                        segmentMatchesRequest(
                                request,
                                segment
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // PRINTS FOR SEGMENT
    // =========================================================

    private int countPrintsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                if (
                        !"COMPLETED".equalsIgnoreCase(
                                request.getStatus()
                        )
                ) {

                    continue;
                }

                if (
                        !segmentMatchesRequest(
                                request,
                                segment
                        )
                ) {

                    continue;
                }

                count +=
                        Math.max(
                                0,
                                request.getPrintedCount()
                        );
            }
        }

        return count;
    }

    // =========================================================
    // COMPLETED REQUESTS
    // =========================================================

    private int countCompletedRequestsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment
    ) {

        return countStatusRequestsForSegment(
                startDate,
                endDate,
                segment,
                "COMPLETED"
        );
    }

    // =========================================================
    // STATUS REQUEST COUNT
    // =========================================================

    private int countStatusRequestsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment,
            String requiredStatus
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                if (
                        !requiredStatus.equalsIgnoreCase(
                                request.getStatus()
                        )
                ) {

                    continue;
                }

                if (
                        segmentMatchesRequest(
                                request,
                                segment
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // ACTIVE REQUEST COUNT
    // =========================================================

    private int countActiveRequestsForSegment(
            LocalDate startDate,
            LocalDate endDate,
            String segment
    ) {

        int count = 0;

        synchronized (printRequests) {

            for (
                    PrintRequest request
                    : printRequests
            ) {

                LocalDate date =
                        parseDate(
                                request.getRequestedAt()
                        );

                if (
                        !isWithin(
                                date,
                                startDate,
                                endDate
                        )
                ) {

                    continue;
                }

                String status =
                        request.getStatus();

                if (
                        status == null
                ) {

                    continue;
                }

                boolean active =
                        status.equalsIgnoreCase(
                                "PENDING"
                        )
                        ||
                        status.equalsIgnoreCase(
                                "ACCEPTED"
                        )
                        ||
                        status.equalsIgnoreCase(
                                "APPROVED"
                        )
                        ||
                        status.equalsIgnoreCase(
                                "PRINTING"
                        );

                if (!active) {
                    continue;
                }

                if (
                        segmentMatchesRequest(
                                request,
                                segment
                        )
                ) {

                    count++;
                }
            }
        }

        return count;
    }

    // =========================================================
    // USER SEGMENT MATCH
    // =========================================================

    private boolean segmentMatchesUser(
            String userId,
            String segment
    ) {

        if (
                "All".equalsIgnoreCase(
                        segment
                )
        ) {

            return true;
        }

        if (
                userId == null
                        ||
                userId.trim().isEmpty()
        ) {

            return false;
        }

        String role =
                userRoles.get(
                        userId
                );

        if (
                role == null
        ) {

            return false;
        }

        if (
                "Users".equalsIgnoreCase(
                        segment
                )
        ) {

            return role.equalsIgnoreCase(
                    "USER"
            );
        }

        if (
                "Admins".equalsIgnoreCase(
                        segment
                )
        ) {

            return role.equalsIgnoreCase(
                    "ADMIN"
            );
        }

        /*
         * Documents belong to users.
         * Therefore Centres do not own documents.
         */

        if (
                "Centres".equalsIgnoreCase(
                        segment
                )
        ) {

            return false;
        }

        return false;
    }

    // =========================================================
    // REQUEST SEGMENT MATCH
    // =========================================================

    private boolean segmentMatchesRequest(
            PrintRequest request,
            String segment
    ) {

        if (
                request == null
        ) {

            return false;
        }

        if (
                "All".equalsIgnoreCase(
                        segment
                )
        ) {

            return true;
        }

        // =====================================================
        // CENTRES
        // =====================================================

        if (
                "Centres".equalsIgnoreCase(
                        segment
                )
        ) {

            String xeroxId =
                    request.getXeroxId();

            if (
                    xeroxId == null
                            ||
                    xeroxId.trim().isEmpty()
            ) {

                return false;
            }

            return centreNames.containsKey(
                    xeroxId
            );
        }

        // =====================================================
        // USERS / ADMINS
        // =====================================================

        String userId =
                request.getUserId();

        if (
                userId == null
                        ||
                userId.trim().isEmpty()
        ) {

            return false;
        }

        String role =
                userRoles.get(
                        userId
                );

        if (
                role == null
        ) {

            return false;
        }

        if (
                "Users".equalsIgnoreCase(
                        segment
                )
        ) {

            return role.equalsIgnoreCase(
                    "USER"
            );
        }

        if (
                "Admins".equalsIgnoreCase(
                        segment
                )
        ) {

            return role.equalsIgnoreCase(
                    "ADMIN"
            );
        }

        return false;
    }

    // =========================================================
    // DATE CHECK
    // =========================================================

    private boolean isWithin(
            LocalDate date,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (
                date == null
        ) {

            return false;
        }

        return !date.isBefore(
                startDate
        )
        &&
        !date.isAfter(
                endDate
        );
    }

    // =========================================================
    // REPORT SUMMARY
    // =========================================================

    private String buildReportSummary(
            String reportType
    ) {

        LocalDate today =
                LocalDate.now();

        LocalDate startDate;

        if (
                "Daily Report".equals(
                        reportType
                )
        ) {

            startDate =
                    today;

        } else if (
                "Weekly Report".equals(
                        reportType
                )
        ) {

            startDate =
                    today.minusDays(6);

        } else {

            startDate =
                    today.minusDays(29);
        }

        int documentsCount =
                countDocumentsBetween(
                        startDate,
                        today
                );

        int requestsCount =
                countRequestsBetween(
                        startDate,
                        today
                );

        int printedCopies =
                countCompletedPrintsBetween(
                        startDate,
                        today
                );

        return
                "PRIVORA REPORT" +

                "\n\n" +

                "Report: " +
                reportType +

                "\n" +

                "Period: " +
                startDate +
                " → " +
                today +

                "\n\n" +

                "Documents uploaded: " +
                documentsCount +

                "\n" +

                "Print requests: " +
                requestsCount +

                "\n" +

                "Printed copies: " +
                printedCopies +

                "\n\n" +

                "Registered users: " +
                totalUsers +

                "\n" +

                "Registered centres: " +
                totalCentres;
    }

    // =========================================================
    // CARD HOVER
    // =========================================================

    private void addHover(
            VBox card
    ) {

        card.setOnMouseEntered(
                e ->
                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD_HOVER
                                        + ";" +
                                "-fx-background-radius: 22;" +
                                "-fx-border-color: "
                                        + PURPLE
                                        + ";" +
                                "-fx-border-radius: 22;"
                        )
        );

        card.setOnMouseExited(
                e ->
                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD
                                        + ";" +
                                "-fx-background-radius: 22;" +
                                "-fx-border-color: "
                                        + BORDER
                                        + ";" +
                                "-fx-border-radius: 22;"
                        )
        );
    }

    // =========================================================
    // STOP LISTENERS
    // =========================================================

    public void stopRealtimeListeners() {

        if (
                documentsListener != null
        ) {

            documentsListener.remove();

            documentsListener =
                    null;
        }

        if (
                printRequestsListener != null
        ) {

            printRequestsListener.remove();

            printRequestsListener =
                    null;
        }

        if (
                usersListener != null
        ) {

            usersListener.remove();

            usersListener =
                    null;
        }

        if (
                centresListener != null
        ) {

            centresListener.remove();

            centresListener =
                    null;
        }
    }

    // =========================================================
    // MESSAGE
    // =========================================================

    private void showMessage(
            String title,
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }
}