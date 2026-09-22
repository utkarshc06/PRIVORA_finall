package com.myprivora.view.user;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.PrintRequest;
import com.myprivora.session.SessionManager;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RequestStatus {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private static final String BACKGROUND = "#080C16";
    private static final String CARD = "rgba(13, 19, 34, 0.75)";
    private static final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private static final String GREEN = "#00F0FF";
    private static final String YELLOW = "#00B4D8";
    private static final String RED = "rgba(255, 255, 255, 0.85)";

    private static final String TEXT = "#FFFFFF";
    private static final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";
    private static final String MUTED_TEXT = "rgba(255, 255, 255, 0.45)";

    private static final String INACTIVE = "rgba(255, 255, 255, 0.12)";


    // =========================================================
    // FIRESTORE LISTENER
    // =========================================================

    private ListenerRegistration requestListener;


    // =========================================================
    // CURRENT REQUEST
    // =========================================================

    private PrintRequest currentRequest;


    // =========================================================
    // UI
    // =========================================================

    private VBox trackerContainer;

    private Label liveStatusLabel;

    private Label documentNameLabel;

    private Label requestIdLabel;

    private Label copiesLabel;


    // =========================================================
    // TIME FORMAT
    // =========================================================

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a")
                    .withZone(ZoneId.systemDefault());


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox root =
                new VBox(20);

        root.setPadding(
                new Insets(25)
        );

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        HBox header =
                new HBox(15);

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        VBox headerText =
                new VBox(4);


        Label title =
                new Label(
                        "Request status"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Live progress of your active session."
                );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        headerText.getChildren().addAll(
                title,
                subtitle
        );


        javafx.scene.image.ImageView radarAnim =
                com.myprivora.view.theme.ClayTheme.createImageViewSafe("/assets/animations/pulse_radar.gif", 28, 28);

        liveStatusLabel =
                new Label(
                        "● Live"
                );

        liveStatusLabel.setStyle(
                "-fx-background-color: rgba(0, 240, 255, 0.15);" +
                "-fx-border-color: #00F0FF;" +
                "-fx-border-radius: 20;" +
                "-fx-text-fill: #00F0FF;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 7 14;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        HBox.setMargin(
                liveStatusLabel,
                new Insets(0, 0, 0, 10)
        );


        header.getChildren().addAll(
                headerText,
                radarAnim,
                liveStatusLabel
        );


        // =====================================================
        // INFORMATION CARD
        // =====================================================

        VBox informationCard =
                new VBox(10);

        informationCard.setPadding(
                new Insets(18)
        );

        informationCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 16;"
        );


        documentNameLabel =
                new Label(
                        "Document: Waiting..."
                );

        documentNameLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        requestIdLabel =
                new Label(
                        "Request ID: Waiting..."
                );

        requestIdLabel.setStyle(
                "-fx-text-fill: " + MUTED_TEXT + ";" +
                "-fx-font-size: 11px;"
        );


        copiesLabel =
                new Label(
                        "Copies: --"
                );

        copiesLabel.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 12px;"
        );


        informationCard.getChildren().addAll(
                documentNameLabel,
                requestIdLabel,
                copiesLabel
        );


        // =====================================================
        // HORIZONTAL TRACKER CARD
        // =====================================================

        VBox trackerCard =
                new VBox();

        trackerCard.setPadding(
                new Insets(28, 20, 28, 20)
        );

        trackerCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 18;"
        );


        trackerContainer =
                new VBox();


        trackerCard.getChildren().add(
                trackerContainer
        );


        // =====================================================
        // ROOT
        // =====================================================

        root.getChildren().addAll(
                header,
                informationCard,
                trackerCard
        );


        // =====================================================
        // START REAL-TIME LISTENER
        // =====================================================

        startRealtimeListener();


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

        scrollPane.setStyle(
                "-fx-background: " + BACKGROUND + ";" +
                "-fx-background-color: " + BACKGROUND + ";"
        );


        return scrollPane;
    }


    // =========================================================
    // REAL-TIME LISTENER
    // =========================================================

    private void startRealtimeListener() {

        stopRealtimeListener();


        String userId =
                SessionManager.getUserId();


        if (userId == null ||
                userId.isBlank()) {

            System.out.println(
                    "[RequestStatus] User is not logged in."
            );


            Platform.runLater(
                    () -> showNoRequest()
            );


            return;
        }


        System.out.println(
                "[RequestStatus] Starting real-time listener."
        );


        System.out.println(
                "[RequestStatus] User ID = "
                        + userId
        );


        requestListener =
                DatabaseConfig.getFirestore()
                        .collection("PrintRequests")
                        .whereEqualTo(
                                "userId",
                                userId
                        )
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[RequestStatus] "
                                                        + "Firestore listener error."
                                        );

                                        error.printStackTrace();

                                        return;
                                    }


                                    if (snapshot == null) {

                                        return;
                                    }


                                    processRealtimeSnapshot(
                                            snapshot
                                    );
                                }
                        );
    }


    // =========================================================
    // PROCESS REAL-TIME SNAPSHOT
    // =========================================================

    private void processRealtimeSnapshot(
            QuerySnapshot snapshot
    ) {

        List<PrintRequest> requests =
                new ArrayList<>();


        for (DocumentSnapshot document :
                snapshot.getDocuments()) {

            try {

                PrintRequest request =
                        document.toObject(
                                PrintRequest.class
                        );


                if (request != null) {

                    requests.add(
                            request
                    );
                }


            } catch (Exception e) {

                System.out.println(
                        "[RequestStatus] "
                                + "Unable to convert Firestore request."
                );

                e.printStackTrace();
            }
        }


        if (requests.isEmpty()) {

            Platform.runLater(
                    () -> showNoRequest()
            );

            return;
        }


        // =====================================================
        // MOST RECENT REQUEST
        // =====================================================

        requests.sort(
                Comparator.comparing(
                        PrintRequest::getRequestedAt,
                        Comparator.nullsFirst(
                                String::compareTo
                        )
                ).reversed()
        );


        PrintRequest latestRequest =
                requests.get(0);


        System.out.println(
                "[REAL-TIME] Request update received."
        );


        System.out.println(
                "[REAL-TIME] Request ID = "
                        + latestRequest.getRequestId()
        );


        System.out.println(
                "[REAL-TIME] Status = "
                        + latestRequest.getStatus()
        );


        System.out.println(
                "[REAL-TIME] Printed Count = "
                        + latestRequest.getPrintedCount()
        );


        Platform.runLater(
                () -> updateUI(
                        latestRequest
                )
        );
    }


    // =========================================================
    // UPDATE UI
    // =========================================================

    private void updateUI(
            PrintRequest request
    ) {

        currentRequest =
                request;


        documentNameLabel.setText(
                "Document: "
                        + safeValue(
                                request.getDocumentName()
                        )
        );


        requestIdLabel.setText(
                "Request ID: "
                        + safeValue(
                                request.getRequestId()
                        )
        );


        copiesLabel.setText(
                "Copies: "
                        + request.getPrintedCount()
                        + " / "
                        + request.getPrintCopies()
        );


        String status =
                safeValue(
                        request.getStatus()
                ).toUpperCase();


        updateLiveBadge(
                status
        );


        rebuildTracker(
                request
        );
    }


    // =========================================================
    // LIVE BADGE
    // =========================================================

    private void updateLiveBadge(
            String status
    ) {

        if (status.equals("COMPLETED")) {

            liveStatusLabel.setText(
                    "● Completed"
            );

            liveStatusLabel.setStyle(
                    "-fx-background-color: rgba(34,211,165,0.12);" +
                    "-fx-text-fill: " + GREEN + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 7 12;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;"
            );


        } else if (status.equals("REJECTED")) {

            liveStatusLabel.setText(
                    "● Rejected"
            );

            liveStatusLabel.setStyle(
                    "-fx-background-color: rgba(248,113,113,0.12);" +
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 7 12;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;"
            );


        } else if (status.equals("EXPIRED")) {

            liveStatusLabel.setText(
                    "● Expired"
            );

            liveStatusLabel.setStyle(
                    "-fx-background-color: rgba(117,106,134,0.15);" +
                    "-fx-text-fill: " + MUTED_TEXT + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 7 12;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;"
            );


        } else {

            liveStatusLabel.setText(
                    "● Live"
            );

            liveStatusLabel.setStyle(
                    "-fx-background-color: rgba(250,204,21,0.12);" +
                    "-fx-text-fill: " + YELLOW + ";" +
                    "-fx-background-radius: 20;" +
                    "-fx-padding: 7 12;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;"
            );
        }
    }


    // =========================================================
    // HORIZONTAL TRACKER
    // =========================================================

    private void rebuildTracker(
            PrintRequest request
    ) {

        trackerContainer
                .getChildren()
                .clear();


        String status =
                safeValue(
                        request.getStatus()
                ).toUpperCase();


        int printedCount =
                request.getPrintedCount();


        int totalCopies =
                request.getPrintCopies();


        // =====================================================
        // MAIN HORIZONTAL TRACKER
        // =====================================================

        HBox tracker =
                new HBox();


        tracker.setAlignment(
                Pos.TOP_CENTER
        );


        tracker.setSpacing(
                0
        );


        // =====================================================
        // STEP 1
        // =====================================================

        boolean uploadedComplete =
                true;


        tracker.getChildren().add(
                createHorizontalStep(
                        "1",
                        "Uploaded",
                        "Document uploaded",
                        GREEN,
                        uploadedComplete
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        GREEN
                )
        );


        // =====================================================
        // STEP 2
        // =====================================================

        boolean centreComplete =
                status.equals("ACCEPTED") ||
                status.equals("APPROVED") ||
                status.equals("PRINTING") ||
                status.equals("COMPLETED");


        boolean centreCurrent =
                status.equals("PENDING");


        String centreColor;


        if (centreComplete) {

            centreColor =
                    GREEN;

        } else if (status.equals("REJECTED")) {

            centreColor =
                    RED;

        } else {

            centreColor =
                    YELLOW;
        }


        tracker.getChildren().add(
                createHorizontalStep(
                        "2",
                        "Centre Accepted",
                        centreComplete
                                ? "Accepted"
                                : centreCurrent
                                ? "Waiting"
                                : "Rejected",
                        centreColor,
                        centreComplete
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        centreComplete
                                ? GREEN
                                : INACTIVE
                )
        );


        // =====================================================
        // STEP 3
        // =====================================================

        boolean approvalComplete =
                status.equals("APPROVED") ||
                status.equals("PRINTING") ||
                status.equals("COMPLETED");


        String approvalColor;


        if (approvalComplete) {

            approvalColor =
                    GREEN;

        } else if (status.equals("REJECTED")) {

            approvalColor =
                    RED;

        } else {

            approvalColor =
                    YELLOW;
        }


        String approvalText;


        if (approvalComplete) {

            approvalText =
                    "PIN approved";

        } else if (status.equals("ACCEPTED")) {

            approvalText =
                    "Waiting for PIN";

        } else if (status.equals("REJECTED")) {

            approvalText =
                    "Rejected";

        } else {

            approvalText =
                    "Pending";
        }


        tracker.getChildren().add(
                createHorizontalStep(
                        "3",
                        "Approval",
                        approvalText,
                        approvalColor,
                        approvalComplete
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        approvalComplete
                                ? GREEN
                                : INACTIVE
                )
        );


        // =====================================================
        // STEP 4
        // =====================================================

        boolean printingComplete =
                status.equals("COMPLETED");


        boolean printingStarted =
                status.equals("PRINTING") ||
                status.equals("COMPLETED") ||
                printedCount > 0;


        String printingColor;


        if (printingComplete) {

            printingColor =
                    GREEN;

        } else if (printingStarted) {

            printingColor =
                    YELLOW;

        } else if (status.equals("REJECTED")) {

            printingColor =
                    RED;

        } else {

            printingColor =
                    INACTIVE;
        }


        String printingText;


        if (printingComplete) {

            printingText =
                    printedCount
                            + " / "
                            + totalCopies
                            + " printed";

        } else if (printingStarted) {

            printingText =
                    printedCount
                            + " / "
                            + totalCopies
                            + " printing";

        } else if (status.equals("APPROVED")) {

            printingText =
                    "Ready to print";

        } else {

            printingText =
                    "Pending";
        }


        tracker.getChildren().add(
                createHorizontalStep(
                        "4",
                        "Printing",
                        printingText,
                        printingColor,
                        printingComplete
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        printingComplete
                                ? GREEN
                                : INACTIVE
                )
        );


        // =====================================================
        // STEP 5
        // =====================================================

        String completedColor;


        if (status.equals("COMPLETED")) {

            completedColor =
                    GREEN;

        } else if (status.equals("REJECTED") ||
                status.equals("EXPIRED")) {

            completedColor =
                    RED;

        } else {

            completedColor =
                    INACTIVE;
        }


        String completedText;


        if (status.equals("COMPLETED")) {

            completedText =
                    "All copies printed";

        } else if (status.equals("REJECTED")) {

            completedText =
                    "Request rejected";

        } else if (status.equals("EXPIRED")) {

            completedText =
                    "Request expired";

        } else {

            completedText =
                    "Pending";
        }


        tracker.getChildren().add(
                createHorizontalStep(
                        "5",
                        "Completed",
                        completedText,
                        completedColor,
                        status.equals("COMPLETED")
                )
        );


        // =====================================================
        // ADD TRACKER
        // =====================================================

        trackerContainer
                .getChildren()
                .add(
                        tracker
                );
    }


    // =========================================================
    // HORIZONTAL STEP
    // =========================================================

    private VBox createHorizontalStep(
            String number,
            String title,
            String description,
            String color,
            boolean completed
    ) {

        VBox step =
                new VBox(8);


        step.setAlignment(
                Pos.TOP_CENTER
        );


        step.setPrefWidth(
                125
        );


        // =====================================================
        // CIRCLE
        // =====================================================

        Circle circle =
                new Circle(
                        22,
                        Color.web(
                                color
                        )
                );


        Label numberLabel =
                new Label(
                        number
                );


        numberLabel.setStyle(
                "-fx-text-fill: #0B0A0F;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        HBox circleBox =
                new HBox();


        circleBox.setAlignment(
                Pos.CENTER
        );


        circleBox.setMinSize(
                44,
                44
        );


        circleBox.setMaxSize(
                44,
                44
        );


        circleBox.getChildren().add(
                circle
        );


        circleBox.getChildren().add(
                numberLabel
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel =
                new Label(
                        title
                );


        titleLabel.setStyle(
                "-fx-text-fill: " +
                        (
                                completed
                                        ? TEXT
                                        : color.equals(RED)
                                        ? RED
                                        : color.equals(YELLOW)
                                        ? YELLOW
                                        : MUTED_TEXT
                        ) +
                        ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );


        titleLabel.setWrapText(
                true
        );


        titleLabel.setAlignment(
                Pos.CENTER
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel =
                new Label(
                        description
                );


        descriptionLabel.setStyle(
                "-fx-text-fill: " +
                        (
                                color.equals(RED)
                                        ? RED
                                        : color.equals(YELLOW)
                                        ? YELLOW
                                        : SECONDARY_TEXT
                        ) +
                        ";" +
                "-fx-font-size: 10px;"
        );


        descriptionLabel.setWrapText(
                true
        );


        descriptionLabel.setAlignment(
                Pos.CENTER
        );


        descriptionLabel.setMaxWidth(
                120
        );


        step.getChildren().addAll(
                circleBox,
                titleLabel,
                descriptionLabel
        );


        return step;
    }


    // =========================================================
    // HORIZONTAL CONNECTOR
    // =========================================================

    private HBox createHorizontalConnector(
            String color
    ) {

        HBox connector =
                new HBox();


        connector.setAlignment(
                Pos.CENTER
        );


        connector.setPrefWidth(
                45
        );


        connector.setPadding(
                new Insets(
                        0,
                        0,
                        0,
                        0
                )
        );


        javafx.scene.layout.Region line =
                new javafx.scene.layout.Region();

        line.setPrefWidth(28);
        line.setPrefHeight(2);
        line.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");

        connector.getChildren().add(
                line
        );


        return connector;
    }


    // =========================================================
    // NO REQUEST
    // =========================================================

    private void showNoRequest() {

        currentRequest =
                null;


        documentNameLabel.setText(
                "Document: No active request"
        );


        requestIdLabel.setText(
                "Request ID: --"
        );


        copiesLabel.setText(
                "Copies: --"
        );


        liveStatusLabel.setText(
                "● Waiting"
        );


        liveStatusLabel.setStyle(
                "-fx-background-color: rgba(117,106,134,0.15);" +
                "-fx-text-fill: " + MUTED_TEXT + ";" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 7 12;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        trackerContainer
                .getChildren()
                .clear();


        HBox tracker =
                new HBox();


        tracker.setAlignment(
                Pos.TOP_CENTER
        );


        tracker.setSpacing(
                0
        );


        tracker.getChildren().add(
                createHorizontalStep(
                        "1",
                        "Uploaded",
                        "No request",
                        INACTIVE,
                        false
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        INACTIVE
                )
        );


        tracker.getChildren().add(
                createHorizontalStep(
                        "2",
                        "Centre Accepted",
                        "Pending",
                        INACTIVE,
                        false
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        INACTIVE
                )
        );


        tracker.getChildren().add(
                createHorizontalStep(
                        "3",
                        "Approval",
                        "Pending",
                        INACTIVE,
                        false
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        INACTIVE
                )
        );


        tracker.getChildren().add(
                createHorizontalStep(
                        "4",
                        "Printing",
                        "Pending",
                        INACTIVE,
                        false
                )
        );


        tracker.getChildren().add(
                createHorizontalConnector(
                        INACTIVE
                )
        );


        tracker.getChildren().add(
                createHorizontalStep(
                        "5",
                        "Completed",
                        "Pending",
                        INACTIVE,
                        false
                )
        );


        trackerContainer
                .getChildren()
                .add(
                        tracker
                );
    }


    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatTime(
            String timestamp
    ) {

        if (timestamp == null ||
                timestamp.isBlank()) {

            return "--";
        }


        try {

            Instant instant =
                    Instant.parse(
                            timestamp
                    );


            return TIME_FORMATTER.format(
                    instant
            );


        } catch (Exception e) {

            return timestamp;
        }
    }


    // =========================================================
    // SAFE VALUE
    // =========================================================

    private String safeValue(
            String value
    ) {

        if (value == null ||
                value.isBlank()) {

            return "--";
        }


        return value;
    }


    // =========================================================
    // STOP REAL-TIME LISTENER
    // =========================================================

    public void stopRealtimeListener() {

        if (requestListener != null) {

            requestListener.remove();

            requestListener = null;


            System.out.println(
                    "[RequestStatus] "
                            + "Real-time listener stopped."
            );
        }
    }
}