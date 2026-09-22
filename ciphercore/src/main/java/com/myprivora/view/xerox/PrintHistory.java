package com.myprivora.view.xerox;

import com.myprivora.view.theme.ClayTheme;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.myprivora.dao.PrintRequestDAO;
import com.myprivora.model.PrintRequest;
import com.myprivora.session.SessionManager;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class PrintHistory {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private static final String BACKGROUND =
            ClayTheme.OBSIDIAN_DEEP;

    private static final String CARD =
            ClayTheme.OBSIDIAN_GLASS;

    private static final String CARD_HOVER =
            ClayTheme.OBSIDIAN_SURFACE;

    private static final String BORDER =
            ClayTheme.CARD_BORDER_COLOR;

    private static final String PURPLE =
            ClayTheme.CYAN_PRIMARY;

    private static final String PURPLE_LIGHT =
            ClayTheme.CYAN_LIGHT;

    private static final String WHITE =
            ClayTheme.FROST_WHITE;

    private static final String SECONDARY =
            ClayTheme.FROST_MUTED;

    private static final String GREEN =
            ClayTheme.CYAN_PRIMARY;

    private static final String RED =
            ClayTheme.FROST_MUTED;

    private static final String GRAY =
            ClayTheme.OBSIDIAN_SURFACE;

    // =========================================================
    // DAO
    // =========================================================

    private final PrintRequestDAO printRequestDAO =
            new PrintRequestDAO();

    // =========================================================
    // CONTENT
    // =========================================================

    private VBox historyContainer;

    private ScrollPane scrollPane;

    private Label totalLabel;

    private Label completedLabel;

    private Label todayLabel;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox root =
                new VBox(22);

        root.setPadding(
                new Insets(30, 35, 35, 35)
        );

        root.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox(15);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(5);

        Label title =
                new Label(
                        "Print History"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        28
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        Label subtitle =
                new Label(
                        "View successfully processed print requests."
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        subtitle.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        // =====================================================
        // REFRESH
        // =====================================================

        Button refreshButton =
                createRefreshButton();

        refreshButton.setOnAction(
                e -> loadHistory()
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                refreshButton
        );

        // =====================================================
        // SUMMARY
        // =====================================================

        HBox summaryBox =
                new HBox(15);

        VBox totalCard =
                createSummaryCard(
                        "TOTAL REQUESTS",
                        "0",
                        PURPLE_LIGHT
                );

        totalLabel =
                getValueLabel(
                        totalCard
                );

        VBox completedCard =
                createSummaryCard(
                        "COMPLETED",
                        "0",
                        GREEN
                );

        completedLabel =
                getValueLabel(
                        completedCard
                );

        VBox todayCard =
                createSummaryCard(
                        "TODAY",
                        "0",
                        PURPLE_LIGHT
                );

        todayLabel =
                getValueLabel(
                        todayCard
                );

        HBox.setHgrow(
                totalCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                completedCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                todayCard,
                Priority.ALWAYS
        );

        summaryBox.getChildren().addAll(
                totalCard,
                completedCard,
                todayCard
        );

        // =====================================================
        // HISTORY CONTAINER
        // =====================================================

        historyContainer =
                new VBox(14);

        historyContainer.setPadding(
                new Insets(5)
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        scrollPane =
                new ScrollPane(
                        historyContainer
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setFitToHeight(
                false
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
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        // =====================================================
        // ADD
        // =====================================================

        root.getChildren().addAll(
                header,
                summaryBox,
                scrollPane
        );

        // =====================================================
        // LOAD HISTORY
        // =====================================================

        loadHistory();

        return root;
    }

    // =========================================================
    // LOAD HISTORY
    // =========================================================

    private void loadHistory() {

        if (historyContainer == null) {
            return;
        }

        historyContainer
                .getChildren()
                .clear();

        String xeroxId =
                SessionManager.getUserId();

        if (xeroxId == null
                || xeroxId.isBlank()) {

            showError(
                    "Xerox centre session not found."
            );

            return;
        }

        try {

            List<PrintRequest> allRequests =
                    printRequestDAO
                            .getRequestsByXerox(
                                    xeroxId
                            );

            List<PrintRequest> completedRequests =
                    new ArrayList<>();

            if (allRequests != null) {

                for (PrintRequest request :
                        allRequests) {

                    if (request == null) {
                        continue;
                    }

                    if ("COMPLETED"
                            .equalsIgnoreCase(
                                    safe(
                                            request.getStatus()
                                    )
                            )) {

                        completedRequests.add(
                                request
                        );
                    }
                }
            }

            // =================================================
            // SORT NEWEST FIRST
            // =================================================

            completedRequests.sort(
                    Comparator.comparing(
                            this::getRequestedInstant,
                            Comparator.nullsLast(
                                    Comparator.reverseOrder()
                            )
                    )
            );

            // =================================================
            // SUMMARY
            // =================================================

            updateSummary(
                    completedRequests
            );

            // =================================================
            // EMPTY
            // =================================================

            if (completedRequests.isEmpty()) {

                historyContainer
                        .getChildren()
                        .add(
                                createEmptyState()
                        );

                return;
            }

            // =================================================
            // CARDS
            // =================================================

            for (PrintRequest request :
                    completedRequests) {

                historyContainer
                        .getChildren()
                        .add(
                                createHistoryCard(
                                        request
                                )
                        );
            }

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to load print history."
            );
        }
    }

    // =========================================================
    // HISTORY CARD
    // =========================================================

    private HBox createHistoryCard(
            PrintRequest request) {

        HBox card =
                new HBox(18);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(20)
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 15;"
        );

        // =====================================================
        // SUCCESS ICON
        // =====================================================

        Circle circle =
                new Circle(23);

        circle.setFill(
                Color.web(
                        "#123A32"
                )
        );

        Label check =
                new Label(
                        "✓"
                );

        check.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        check.setTextFill(
                Color.web(
                        GREEN
                )
        );

        javafx.scene.layout.StackPane
                iconBox =
                new javafx.scene.layout.StackPane(
                        circle,
                        check
                );

        iconBox.setPrefSize(
                48,
                48
        );

        // =====================================================
        // DOCUMENT INFORMATION
        // =====================================================

        VBox info =
                new VBox(6);

        Label documentName =
                new Label(
                        safeDocumentName(
                                request
                        )
                );

        documentName.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        16
                )
        );

        documentName.setTextFill(
                Color.WHITE
        );

        Label requestId =
                new Label(
                        "Request ID: "
                                + safe(
                                        request.getRequestId()
                                )
                );

        requestId.setFont(
                Font.font(
                        "Arial",
                        11
                )
        );

        requestId.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        Label xeroxName =
                new Label(
                        "Centre: "
                                + safe(
                                        request.getXeroxName()
                                )
                );

        xeroxName.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        xeroxName.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        info.getChildren().addAll(
                documentName,
                requestId,
                xeroxName
        );

        HBox.setHgrow(
                info,
                Priority.ALWAYS
        );

        // =====================================================
        // COPY INFORMATION
        // =====================================================

        VBox copiesBox =
                new VBox(5);

        copiesBox.setAlignment(
                Pos.CENTER
        );

        Label copiesTitle =
                new Label(
                        "COPIES"
                );

        copiesTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        copiesTitle.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        Label copiesValue =
                new Label(
                        String.valueOf(
                                request.getPrintCopies()
                        )
                );

        copiesValue.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        copiesValue.setTextFill(
                Color.WHITE
        );

        copiesBox.getChildren().addAll(
                copiesTitle,
                copiesValue
        );

        // =====================================================
        // DATE
        // =====================================================

        VBox dateBox =
                new VBox(5);

        dateBox.setAlignment(
                Pos.CENTER_RIGHT
        );

        Label dateTitle =
                new Label(
                        "PRINTED"
                );

        dateTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        dateTitle.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        Label date =
                new Label(
                        formatDateTime(
                                request.getRequestedAt()
                        )
                );

        date.setFont(
                Font.font(
                        "Arial",
                        12
                )
        );

        date.setTextFill(
                Color.WHITE
        );

        dateBox.getChildren().addAll(
                dateTitle,
                date
        );

        // =====================================================
        // STATUS
        // =====================================================

        Label status =
                new Label(
                        "COMPLETED"
                );

        status.setPadding(
                new Insets(
                        7,
                        13,
                        7,
                        13
                )
        );

        status.setStyle(
                "-fx-background-color: #123A32;" +
                "-fx-background-radius: 20;" +
                "-fx-text-fill: " + GREEN + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                iconBox,
                info,
                copiesBox,
                dateBox,
                status
        );

        // =====================================================
        // HOVER
        // =====================================================

        card.setOnMouseEntered(
                e -> {

                    card.setStyle(
                            "-fx-background-color: "
                                    + CARD_HOVER
                                    + ";" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: "
                                    + PURPLE
                                    + ";" +
                            "-fx-border-radius: 15;"
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    card
                            );

                    scale.setToX(
                            1.01
                    );

                    scale.setToY(
                            1.01
                    );

                    scale.play();
                }
        );

        card.setOnMouseExited(
                e -> {

                    card.setStyle(
                            "-fx-background-color: "
                                    + CARD
                                    + ";" +
                            "-fx-background-radius: 15;" +
                            "-fx-border-color: "
                                    + BORDER
                                    + ";" +
                            "-fx-border-radius: 15;"
                    );

                    ScaleTransition scale =
                            new ScaleTransition(
                                    Duration.millis(120),
                                    card
                            );

                    scale.setToX(
                            1.0
                    );

                    scale.setToY(
                            1.0
                    );

                    scale.play();
                }
        );

        return card;
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private VBox createEmptyState() {

        VBox box =
                new VBox(14);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(70)
        );

        javafx.scene.Node icon =
                ClayTheme.createLottieAnimation("laser_scan.gif", 56, 56);

        Label title =
                new Label(
                        "No Print History"
                );

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        title.setTextFill(
                Color.WHITE
        );

        Label message =
                new Label(
                        "Completed print requests will appear here."
                );

        message.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        message.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        box.getChildren().addAll(
                icon,
                title,
                message
        );

        return box;
    }

    // =========================================================
    // SUMMARY
    // =========================================================

    private void updateSummary(
            List<PrintRequest> requests) {

        int total =
                requests.size();

        int completed =
                0;

        int today =
                0;

        Instant todayStart =
                java.time.LocalDate
                        .now()
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        )
                        .toInstant();

        for (PrintRequest request :
                requests) {

            if ("COMPLETED"
                    .equalsIgnoreCase(
                            safe(
                                    request.getStatus()
                            )
                    )) {

                completed++;
            }

            Instant requested =
                    getRequestedInstant(
                            request
                    );

            if (requested != null
                    && !requested.isBefore(
                            todayStart
                    )) {

                today++;
            }
        }

        if (totalLabel != null) {

            totalLabel.setText(
                    String.valueOf(
                            total
                    )
            );
        }

        if (completedLabel != null) {

            completedLabel.setText(
                    String.valueOf(
                            completed
                    )
            );
        }

        if (todayLabel != null) {

            todayLabel.setText(
                    String.valueOf(
                            today
                    )
            );
        }
    }

    // =========================================================
    // SUMMARY CARD
    // =========================================================

    private VBox createSummaryCard(
            String title,
            String value,
            String accent) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setMinHeight(
                95
        );

        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-radius: 14;"
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        10
                )
        );

        titleLabel.setTextFill(
                Color.web(
                        SECONDARY
                )
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        25
                )
        );

        valueLabel.setTextFill(
                Color.web(
                        accent
                )
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        card.getProperties().put(
                "valueLabel",
                valueLabel
        );

        return card;
    }

    // =========================================================
    // GET VALUE LABEL
    // =========================================================

    private Label getValueLabel(
            VBox card) {

        return (Label) card
                .getProperties()
                .get(
                        "valueLabel"
                );
    }

    // =========================================================
    // REFRESH BUTTON
    // =========================================================

    private Button createRefreshButton() {

        Button button =
                new Button(
                        "↻  Refresh"
                );

        button.setPrefHeight(
                38
        );

        button.setPrefWidth(
                105
        );

        button.setCursor(
                Cursor.HAND
        );

        button.setStyle(
                "-fx-background-color: linear-gradient(" +
                        "to right, #5B3CC4, #2563EB" +
                        ");" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(
                e -> {

                    button.setStyle(
                            "-fx-background-color: linear-gradient(" +
                                    "to right, #6D4DE0, #3474F0" +
                                    ");" +
                            "-fx-background-radius: 10;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
                    );
                }
        );

        button.setOnMouseExited(
                e -> {

                    button.setStyle(
                            "-fx-background-color: linear-gradient(" +
                                    "to right, #5B3CC4, #2563EB" +
                                    ");" +
                            "-fx-background-radius: 10;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
                    );
                }
        );

        return button;
    }

    // =========================================================
    // DATE
    // =========================================================

    private String formatDateTime(
            String value) {

        if (value == null
                || value.isBlank()) {

            return "Unknown";
        }

        try {

            Instant instant =
                    Instant.parse(
                            value
                    );

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy\nhh:mm a"
                    ).withZone(
                            ZoneId.systemDefault()
                    );

            return formatter.format(
                    instant
            );

        } catch (Exception e) {

            return value;
        }
    }

    // =========================================================
    // INSTANT
    // =========================================================

    private Instant getRequestedInstant(
            PrintRequest request) {

        if (request == null
                || request.getRequestedAt() == null
                || request.getRequestedAt().isBlank()) {

            return null;
        }

        try {

            return Instant.parse(
                    request.getRequestedAt()
            );

        } catch (Exception e) {

            return null;
        }
    }

    // =========================================================
    // DOCUMENT NAME
    // =========================================================

    private String safeDocumentName(
            PrintRequest request) {

        if (request == null) {

            return "Unknown Document";
        }

        String name =
                request.getDocumentName();

        if (name == null
                || name.isBlank()) {

            return "Unknown Document";
        }

        return name;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    // =========================================================
    // ERROR
    // =========================================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "PRIVORA"
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