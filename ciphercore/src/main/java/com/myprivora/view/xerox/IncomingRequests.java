package com.myprivora.view.xerox;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

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
import javafx.util.Duration;

public class IncomingRequests {

    // =====================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =====================================================

    private static final String BG_COLOR = "#080C16";
    private static final String CARD_COLOR = "rgba(13, 19, 34, 0.75)";
    private static final String CARD_HOVER = "rgba(18, 27, 48, 0.85)";
    private static final String BORDER_COLOR = "rgba(255, 255, 255, 0.12)";
    private static final String PURPLE = "#00F0FF";
    private static final String PURPLE_LIGHT = "#33F3FF";
    private static final String WHITE = "#FFFFFF";
    private static final String TEXT_SECONDARY = "rgba(255, 255, 255, 0.70)";

    private static final String GREEN = "#00F0FF";
    private static final String YELLOW = "#00B4D8";
    private static final String RED = "rgba(255, 255, 255, 0.85)";
    private static final String GRAY = "rgba(255, 255, 255, 0.12)";

    // =====================================================
    // DAO
    // =====================================================

    private final PrintRequestDAO printRequestDAO =
            new PrintRequestDAO();

    // =====================================================
    // UI
    // =====================================================

    private VBox requestContainer;

    private Label pendingCountLabel;
    private Label todayCountLabel;
    private Label acceptedCountLabel;

    private ScrollPane scrollPane;

    // =====================================================
    // CALLBACK
    // =====================================================

    private Consumer<PrintRequest> openRequestCallback;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public IncomingRequests() {
    }

    public IncomingRequests(
            Consumer<PrintRequest> openRequestCallback) {

        this.openRequestCallback =
                openRequestCallback;
    }

    // =====================================================
    // MAIN CONTENT
    // =====================================================

    public VBox getContent() {

        VBox root =
                new VBox(22);

        root.setPadding(
                new Insets(28)
        );

        root.setStyle(
                "-fx-background-color: " + BG_COLOR + ";"
        );

        // =================================================
        // HEADER
        // =================================================

        HBox header =
                new HBox(15);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(5);

        Label title =
                new Label(
                        "Incoming Requests"
                );

        title.setStyle(
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "Manage and process incoming print requests"
                );

        subtitle.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;"
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

        // =================================================
        // REFRESH
        // =================================================

        Button refreshButton =
                createActionButton(
                        "↻  Refresh"
                );

        refreshButton.setOnAction(
                e -> refreshRequests()
        );

        // =================================================
        // CLEAR
        // =================================================

        Button clearButton =
                createClearButton(
                        "Clear"
                );

        clearButton.setOnAction(
                e -> clearRequestsFromUI()
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                refreshButton,
                clearButton
        );

        // =================================================
        // SUMMARY
        // =================================================

        HBox summaryBox =
                new HBox(16);

        VBox pendingCard =
                createSummaryCard(
                        "PENDING",
                        "0",
                        YELLOW
                );

        pendingCountLabel =
                (Label) pendingCard
                        .getProperties()
                        .get("valueLabel");

        VBox todayCard =
                createSummaryCard(
                        "TODAY",
                        "0",
                        PURPLE_LIGHT
                );

        todayCountLabel =
                (Label) todayCard
                        .getProperties()
                        .get("valueLabel");

        VBox acceptedCard =
                createSummaryCard(
                        "ACCEPTED",
                        "0",
                        GREEN
                );

        acceptedCountLabel =
                (Label) acceptedCard
                        .getProperties()
                        .get("valueLabel");

        HBox.setHgrow(
                pendingCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                todayCard,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                acceptedCard,
                Priority.ALWAYS
        );

        summaryBox.getChildren().addAll(
                pendingCard,
                todayCard,
                acceptedCard
        );

        // =================================================
        // REQUEST CONTAINER
        // =================================================

        requestContainer =
                new VBox(14);

        requestContainer.setPadding(
                new Insets(5)
        );

        // =================================================
        // SCROLL PANE
        // =================================================

        scrollPane =
                new ScrollPane();

        scrollPane.setContent(
                requestContainer
        );

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setPannable(true);

        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        // =================================================
        // ADD
        // =================================================

        root.getChildren().addAll(
                header,
                summaryBox,
                scrollPane
        );

        // =================================================
        // LOAD
        // =================================================

        loadRequests();

        return root;
    }

    // =====================================================
    // SESSION
    // =====================================================

    private String getCurrentXeroxId() {

        return SessionManager.getUserId();
    }

    // =====================================================
    // REFRESH
    // =====================================================

    private void refreshRequests() {

        loadRequests();
    }

    // =====================================================
    // LOAD REQUESTS
    // =====================================================

    private void loadRequests() {

        if (requestContainer == null) {
            return;
        }

        requestContainer
                .getChildren()
                .clear();

        String xeroxId =
                getCurrentXeroxId();

        if (xeroxId == null
                || xeroxId.isBlank()) {

            showError(
                    "Xerox centre session not found."
            );

            return;
        }

        try {

            List<PrintRequest> requests =
                    printRequestDAO
                            .getRequestsByXerox(
                                    xeroxId
                            );

            if (requests == null
                    || requests.isEmpty()) {

                updateSummary(
                        List.of()
                );

                requestContainer
                        .getChildren()
                        .add(
                                createEmptyState()
                        );

                return;
            }

            // =================================================
            // AUTOMATIC EXPIRY
            // =================================================

            for (PrintRequest request : requests) {

                checkAndExpireRequest(
                        request
                );
            }

            // =================================================
            // DISPLAY REQUESTS
            // =================================================

            for (PrintRequest request : requests) {

                String status =
                        safe(
                                request.getStatus()
                        );

                if ("PENDING".equalsIgnoreCase(status)
                        || "ACCEPTED".equalsIgnoreCase(status)
                        || "EXPIRED".equalsIgnoreCase(status)) {

                    requestContainer
                            .getChildren()
                            .add(
                                    createRequestCard(
                                            request
                                    )
                            );
                }
            }

            if (requestContainer
                    .getChildren()
                    .isEmpty()) {

                requestContainer
                        .getChildren()
                        .add(
                                createEmptyState()
                        );
            }

            updateSummary(
                    requests
            );

        } catch (Exception e) {

            e.printStackTrace();

            showError(
                    "Unable to load incoming requests."
            );
        }
    }

    // =====================================================
    // AUTOMATIC EXPIRY
    // =====================================================

    private void checkAndExpireRequest(
            PrintRequest request) {

        if (request == null) {
            return;
        }

        String status =
                safe(
                        request.getStatus()
                );

        if (!"PENDING".equalsIgnoreCase(status)) {
            return;
        }

        if (!isRequestExpired(request)) {
            return;
        }

        try {

            boolean updated =
                    printRequestDAO.updateStatus(
                            request.getRequestId(),
                            "EXPIRED"
                    );

            if (updated) {

                request.setStatus(
                        "EXPIRED"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // EXPIRY CHECK
    // =====================================================

    private boolean isRequestExpired(
            PrintRequest request) {

        if (request == null) {
            return false;
        }

        String expiresAt =
                request.getExpiresAt();

        if (expiresAt == null
                || expiresAt.isBlank()) {

            return false;
        }

        try {

            Instant expiry =
                    Instant.parse(
                            expiresAt
                    );

            return Instant.now()
                    .isAfter(expiry);

        } catch (Exception e) {

            return false;
        }
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearRequestsFromUI() {

        if (requestContainer == null) {
            return;
        }

        requestContainer
                .getChildren()
                .clear();

        requestContainer
                .getChildren()
                .add(
                        createClearedState()
                );

        if (pendingCountLabel != null) {
            pendingCountLabel.setText("0");
        }

        if (todayCountLabel != null) {
            todayCountLabel.setText("0");
        }

        if (acceptedCountLabel != null) {
            acceptedCountLabel.setText("0");
        }
    }

    // =====================================================
    // CLEARED STATE
    // =====================================================

    private VBox createClearedState() {

        VBox box =
                new VBox(10);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(50)
        );

        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-text-fill: " + GREEN + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );

        Label text =
                new Label(
                        "Requests cleared from view"
                );

        text.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;"
        );

        box.getChildren().addAll(
                icon,
                text
        );

        return box;
    }

    // =====================================================
    // EMPTY STATE
    // =====================================================

    private VBox createEmptyState() {

        VBox box =
                new VBox(10);

        box.setAlignment(
                Pos.CENTER
        );

        box.setPadding(
                new Insets(50)
        );

        Label icon =
                new Label("✓");

        icon.setStyle(
                "-fx-text-fill: " + GREEN + ";" +
                "-fx-font-size: 34px;" +
                "-fx-font-weight: bold;"
        );

        Label text =
                new Label(
                        "No incoming requests"
                );

        text.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 15px;"
        );

        box.getChildren().addAll(
                icon,
                text
        );

        return box;
    }

    // =====================================================
    // REQUEST CARD
    // =====================================================

    private HBox createRequestCard(
            PrintRequest request) {

        HBox card =
                new HBox(18);

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPadding(
                new Insets(18)
        );

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 14;"
        );

        boolean expired =
                isRequestExpired(request);

        String status =
                safe(
                        request.getStatus()
                );

        if (expired
                && "PENDING".equalsIgnoreCase(status)) {

            status = "EXPIRED";
        }

        boolean viewAllowed =
                !expired
                && !"EXPIRED".equalsIgnoreCase(status)
                && !"REJECTED".equalsIgnoreCase(status)
                && !"COMPLETED".equalsIgnoreCase(status);

        // =================================================
        // ICON
        // =================================================

        Label documentIcon =
                new Label("▣");

        documentIcon.setMinWidth(42);
        documentIcon.setMinHeight(42);

        documentIcon.setAlignment(
                Pos.CENTER
        );

        documentIcon.setStyle(
                "-fx-background-color: #24213F;" +
                "-fx-background-radius: 10;" +
                "-fx-text-fill: " + PURPLE_LIGHT + ";" +
                "-fx-font-size: 20px;"
        );

        // =================================================
        // INFORMATION
        // =================================================

        VBox infoBox =
                new VBox(6);

        Label documentName =
                new Label(
                        safeDocumentName(
                                request
                        )
                );

        documentName.setStyle(
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        Label requestId =
                new Label(
                        "Request ID: "
                                + safe(
                                        request.getRequestId()
                                )
                );

        requestId.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 11px;"
        );

        Label copies =
                new Label(
                        "Copies: "
                                + request.getPrintCopies()
                );

        copies.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 12px;"
        );

        infoBox.getChildren().addAll(
                documentName,
                requestId,
                copies
        );

        HBox.setHgrow(
                infoBox,
                Priority.ALWAYS
        );

        // =================================================
        // STATUS
        // =================================================

        Label statusLabel =
                new Label(
                        status
                );

        applyStatusStyle(
                statusLabel,
                status
        );

        // =================================================
        // EXPIRY
        // =================================================

        Label expiryLabel =
                new Label(
                        formatExpiry(
                                request
                        )
                );

        expiryLabel.setMinWidth(
                110
        );

        expiryLabel.setAlignment(
                Pos.CENTER
        );

        expiryLabel.setStyle(
                "-fx-text-fill: "
                        + (expired ? RED : TEXT_SECONDARY)
                        + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        // =================================================
        // VIEW
        // =================================================

        Button viewButton =
                createViewButton(
                        viewAllowed
                );

        if (viewAllowed) {

            viewButton.setOnAction(
                    e -> openRequest(
                            request
                    )
            );
        }

        card.getChildren().addAll(
                documentIcon,
                infoBox,
                statusLabel,
                expiryLabel,
                viewButton
        );

        // =================================================
        // HOVER
        // =================================================

        if (viewAllowed) {

            card.setOnMouseEntered(
                    e -> {

                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD_HOVER + ";" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: "
                                        + PURPLE + ";" +
                                "-fx-border-radius: 14;"
                        );

                        ScaleTransition scale =
                                new ScaleTransition(
                                        Duration.millis(120),
                                        card
                                );

                        scale.setToX(1.01);
                        scale.setToY(1.01);
                        scale.play();
                    }
            );

            card.setOnMouseExited(
                    e -> {

                        card.setStyle(
                                "-fx-background-color: "
                                        + CARD_COLOR + ";" +
                                "-fx-background-radius: 14;" +
                                "-fx-border-color: "
                                        + BORDER_COLOR + ";" +
                                "-fx-border-radius: 14;"
                        );

                        ScaleTransition scale =
                                new ScaleTransition(
                                        Duration.millis(120),
                                        card
                                );

                        scale.setToX(1);
                        scale.setToY(1);
                        scale.play();
                    }
            );
        }

        return card;
    }

    // =====================================================
    // STATUS STYLE
    // =====================================================

    private void applyStatusStyle(
            Label label,
            String status) {

        if ("PENDING".equalsIgnoreCase(status)) {

            label.setStyle(
                    "-fx-background-color: #3A3214;" +
                    "-fx-text-fill: " + YELLOW + ";" +
                    "-fx-padding: 6 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );

        } else if ("ACCEPTED".equalsIgnoreCase(status)) {

            label.setStyle(
                    "-fx-background-color: #123A32;" +
                    "-fx-text-fill: " + GREEN + ";" +
                    "-fx-padding: 6 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );

        } else if ("EXPIRED".equalsIgnoreCase(status)) {

            label.setStyle(
                    "-fx-background-color: #3A1D24;" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-padding: 6 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );

        } else {

            label.setStyle(
                    "-fx-background-color: " + GRAY + ";" +
                    "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                    "-fx-padding: 6 12;" +
                    "-fx-background-radius: 20;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;"
            );
        }
    }

    // =====================================================
    // VIEW BUTTON
    // =====================================================

    private Button createViewButton(
            boolean enabled) {

        Button button =
                new Button(
                        enabled
                                ? "View"
                                : "Expired"
                );

        button.setPrefWidth(82);
        button.setPrefHeight(34);

        button.setCursor(
                enabled
                        ? Cursor.HAND
                        : Cursor.DEFAULT
        );

        if (enabled) {

            button.setStyle(
                    "-fx-background-color: " + PURPLE + ";" +
                    "-fx-text-fill: " + WHITE + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 9;"
            );

            button.setOnMouseEntered(
                    e -> button.setStyle(
                            "-fx-background-color: #8B5CF6;" +
                            "-fx-text-fill: " + WHITE + ";" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 9;"
                    )
            );

            button.setOnMouseExited(
                    e -> button.setStyle(
                            "-fx-background-color: " + PURPLE + ";" +
                            "-fx-text-fill: " + WHITE + ";" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 9;"
                    )
            );

        } else {

            button.setDisable(true);

            button.setStyle(
                    "-fx-background-color: #292B3A;" +
                    "-fx-text-fill: #777A8F;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 9;"
            );
        }

        return button;
    }

    // =====================================================
    // ACTION BUTTON
    // =====================================================

    private Button createActionButton(
            String text) {

        Button button =
                new Button(text);

        button.setPrefWidth(105);
        button.setPrefHeight(36);

        button.setCursor(
                Cursor.HAND
        );

        button.setStyle(
                "-fx-background-color: " + PURPLE + ";" +
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;"
        );

        button.setOnMouseEntered(
                e -> button.setStyle(
                        "-fx-background-color: #8B5CF6;" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;"
                )
        );

        button.setOnMouseExited(
                e -> button.setStyle(
                        "-fx-background-color: " + PURPLE + ";" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;"
                )
        );

        return button;
    }

    // =====================================================
    // CLEAR BUTTON
    // =====================================================

    private Button createClearButton(
            String text) {

        Button button =
                new Button(text);

        button.setPrefWidth(90);
        button.setPrefHeight(36);

        button.setCursor(
                Cursor.HAND
        );

        button.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                "-fx-text-fill: " + WHITE + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 10;"
        );

        button.setOnMouseEntered(
                e -> button.setStyle(
                        "-fx-background-color: #25283F;" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + PURPLE + ";" +
                        "-fx-border-radius: 10;"
                )
        );

        button.setOnMouseExited(
                e -> button.setStyle(
                        "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-text-fill: " + WHITE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-radius: 10;"
                )
        );

        return button;
    }

    // =====================================================
    // OPEN REQUEST
    // =====================================================

    private void openRequest(
            PrintRequest request) {

        if (isRequestExpired(request)) {

            try {

                printRequestDAO.updateStatus(
                        request.getRequestId(),
                        "EXPIRED"
                );

                request.setStatus(
                        "EXPIRED"
                );

            } catch (Exception e) {

                e.printStackTrace();
            }

            showExpiredAlert();

            refreshRequests();

            return;
        }

        if ("EXPIRED".equalsIgnoreCase(
                safe(request.getStatus())
        )) {

            showExpiredAlert();

            return;
        }

        // =================================================
        // SEND THE ACTUAL REQUEST TO XEROX DASHBOARD
        // =================================================

        if (openRequestCallback != null) {

            openRequestCallback.accept(
                    request
            );
        }
    }

    // =====================================================
    // EXPIRED ALERT
    // =====================================================

    private void showExpiredAlert() {

        Alert alert =
                new Alert(
                        Alert.AlertType.WARNING
                );

        alert.setTitle(
                "Request Expired"
        );

        alert.setHeaderText(
                "This print request has expired."
        );

        alert.setContentText(
                "The request can no longer be viewed or processed."
        );

        alert.showAndWait();
    }

    // =====================================================
    // SUMMARY
    // =====================================================

    private void updateSummary(
            List<PrintRequest> requests) {

        int pending = 0;
        int today = 0;
        int accepted = 0;

        Instant todayStart =
                java.time.LocalDate
                        .now()
                        .atStartOfDay(
                                ZoneId.systemDefault()
                        )
                        .toInstant();

        for (PrintRequest request : requests) {

            if (request == null) {
                continue;
            }

            String status =
                    safe(
                            request.getStatus()
                    );

            if ("PENDING".equalsIgnoreCase(status)
                    && !isRequestExpired(request)) {

                pending++;
            }

            if ("ACCEPTED".equalsIgnoreCase(status)) {

                accepted++;
            }

            String requestedAt =
                    request.getRequestedAt();

            if (requestedAt != null
                    && !requestedAt.isBlank()) {

                try {

                    Instant requested =
                            Instant.parse(
                                    requestedAt
                            );

                    if (!requested.isBefore(
                            todayStart
                    )) {

                        today++;
                    }

                } catch (Exception ignored) {
                }
            }
        }

        if (pendingCountLabel != null) {

            pendingCountLabel.setText(
                    String.valueOf(pending)
            );
        }

        if (todayCountLabel != null) {

            todayCountLabel.setText(
                    String.valueOf(today)
            );
        }

        if (acceptedCountLabel != null) {

            acceptedCountLabel.setText(
                    String.valueOf(accepted)
            );
        }
    }

    // =====================================================
    // SUMMARY CARD
    // =====================================================

    private VBox createSummaryCard(
            String title,
            String value,
            String accentColor) {

        VBox card =
                new VBox(8);

        card.setPadding(
                new Insets(18)
        );

        card.setMinHeight(95);

        card.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: " + BORDER_COLOR + ";" +
                "-fx-border-radius: 14;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setStyle(
                "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setStyle(
                "-fx-text-fill: " + accentColor + ";" +
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;"
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

    // =====================================================
    // DOCUMENT NAME
    // =====================================================

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

    // =====================================================
    // SAFE
    // =====================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    // =====================================================
    // FORMAT DATE TIME
    // =====================================================

    private String formatDateTime(
            String value) {

        if (value == null
                || value.isBlank()) {

            return "Unknown";
        }

        try {

            Instant instant =
                    Instant.parse(value);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM yyyy, hh:mm a"
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

    // =====================================================
    // FORMAT EXPIRY
    // =====================================================

    private String formatExpiry(
            PrintRequest request) {

        if (request == null) {
            return "Expiry: Unknown";
        }

        if (isRequestExpired(request)) {
            return "Expired";
        }

        String expiresAt =
                request.getExpiresAt();

        if (expiresAt == null
                || expiresAt.isBlank()) {

            return "Expiry: None";
        }

        try {

            Instant expiry =
                    Instant.parse(
                            expiresAt
                    );

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern(
                            "dd MMM, hh:mm a"
                    ).withZone(
                            ZoneId.systemDefault()
                    );

            return "Expires: "
                    + formatter.format(
                            expiry
                    );

        } catch (Exception e) {

            return "Expiry: Unknown";
        }
    }

    // =====================================================
    // ERROR
    // =====================================================

    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );

        alert.setTitle(
                "Error"
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