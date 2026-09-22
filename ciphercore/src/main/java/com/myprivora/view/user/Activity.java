package com.myprivora.view.user;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.layout.VBox;


// =============================================================
// USER ACTIVITY
// =============================================================

public class Activity {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private static final String BACKGROUND = "#080C16";

    private static final String CARD = "rgba(13, 19, 34, 0.75)";

    private static final String CARD_HOVER = "rgba(18, 27, 48, 0.85)";

    private static final String BORDER = "rgba(255, 255, 255, 0.12)";

    private static final String PURPLE = "#00F0FF";

    private static final String PURPLE_DARK = "#00B4D8";

    private static final String TEXT = "#FFFFFF";

    private static final String SECONDARY = "rgba(255, 255, 255, 0.70)";

    private static final String GREEN = "#00F0FF";

    private static final String RED = "rgba(255, 255, 255, 0.85)";

    private static final String YELLOW = "#00B4D8";


    // =========================================================
    // FIRESTORE LISTENERS
    // =========================================================

    private ListenerRegistration documentsListener;

    private ListenerRegistration printRequestsListener;


    // =========================================================
    // ACTIVITY LIST
    // =========================================================

    private VBox timeline;


    // =========================================================
    // ACTIVITY DATA
    // =========================================================

    private final List<ActivityEvent> activityEvents =
            new ArrayList<>();


    // =========================================================
    // DATE FORMATTER
    // =========================================================

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a")
                    .withZone(
                            ZoneId.systemDefault()
                    );


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
                    .withZone(
                            ZoneId.systemDefault()
                    );


    // =========================================================
    // CONTENT
    // =========================================================

    public VBox getContent() {

        // =====================================================
        // MAIN CONTAINER
        // =====================================================

        VBox main =
                new VBox(25);

        main.setPadding(
                new Insets(
                        35,
                        40,
                        40,
                        40
                )
        );

        main.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title =
                new Label(
                        "Activity timeline"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "A real-time record of your document and printing activity."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY + ";" +
                "-fx-font-size: 16px;"
        );


        VBox heading =
                new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // TIMELINE CARD
        // =====================================================

        VBox timelineCard =
                new VBox();

        timelineCard.setPadding(
                new Insets(
                        25,
                        30,
                        25,
                        30
                )
        );

        timelineCard.setMaxWidth(
                Double.MAX_VALUE
        );

        timelineCard.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
        );


        // =====================================================
        // REAL-TIME TIMELINE
        // =====================================================

        timeline =
                new VBox();

        timeline.setFillWidth(
                true
        );


        Label loadingLabel =
                new Label(
                        "Loading activity..."
                );

        loadingLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 20 0 20 0;"
        );


        timeline.getChildren().add(
                loadingLabel
        );


        timelineCard.getChildren().add(
                timeline
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        timelineCard
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
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );


        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                heading,
                scrollPane
        );


        // =====================================================
        // START REAL-TIME LISTENERS
        // =====================================================

        loadRealtimeDocuments();

        loadRealtimePrintRequests();


        return main;
    }


    // =========================================================
    // REAL-TIME DOCUMENT LISTENER
    // =========================================================

    private void loadRealtimeDocuments() {

        String userId =
                SessionManager.getUserId();


        if (
                userId == null
                || userId.trim().isEmpty()
        ) {

            showNoUser();

            return;
        }


        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (
                    documentsListener != null
            ) {

                documentsListener.remove();

                documentsListener = null;
            }


            System.out.println(
                    "[Activity] Starting Documents listener..."
            );


            // -------------------------------------------------
            // LISTEN TO USER DOCUMENTS
            // -------------------------------------------------

            documentsListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("Documents")
                            .whereEqualTo(
                                    "ownerId",
                                    userId
                            )
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        if (
                                                error != null
                                        ) {

                                            System.err.println(
                                                    "[Activity] "
                                                            + "Documents listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        if (
                                                snapshot == null
                                        ) {

                                            return;
                                        }


                                        synchronized (
                                                activityEvents
                                        ) {

                                            // -------------------------------------
                                            // REMOVE OLD DOCUMENT EVENTS
                                            // -------------------------------------

                                            activityEvents.removeIf(
                                                    event ->
                                                            "DOCUMENT"
                                                                    .equals(
                                                                            event.getSource()
                                                                    )
                                            );


                                            // -------------------------------------
                                            // ADD CURRENT DOCUMENT EVENTS
                                            // -------------------------------------

                                            for (
                                                    DocumentSnapshot document
                                                    : snapshot.getDocuments()
                                            ) {

                                                String documentId =
                                                        document.getId();


                                                String documentName =
                                                        safeString(
                                                                document.getString(
                                                                        "fileName"
                                                                ),
                                                                "Document"
                                                        );


                                                String uploadedAt =
                                                        document.getString(
                                                                "uploadedAt"
                                                        );


                                                Instant eventTime =
                                                        parseInstant(
                                                                uploadedAt
                                                        );


                                                if (
                                                        eventTime == null
                                                ) {

                                                    continue;
                                                }


                                                ActivityEvent event =
                                                        new ActivityEvent(
                                                                documentId,
                                                                "DOCUMENT",
                                                                "Uploaded",
                                                                documentName,
                                                                "Document uploaded",
                                                                eventTime,
                                                                PURPLE
                                                        );


                                                activityEvents.add(
                                                        event
                                                );
                                            }
                                        }


                                        refreshTimeline();
                                    }
                            );

        } catch (
                Exception e
        ) {

            System.err.println(
                    "[Activity] "
                            + "Unable to start Documents listener."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // REAL-TIME PRINT REQUEST LISTENER
    // =========================================================

    private void loadRealtimePrintRequests() {

        String userId =
                SessionManager.getUserId();


        if (
                userId == null
                || userId.trim().isEmpty()
        ) {

            showNoUser();

            return;
        }


        try {

            // -------------------------------------------------
            // REMOVE OLD LISTENER
            // -------------------------------------------------

            if (
                    printRequestsListener != null
            ) {

                printRequestsListener.remove();

                printRequestsListener = null;
            }


            System.out.println(
                    "[Activity] Starting PrintRequests listener..."
            );


            // -------------------------------------------------
            // LISTEN TO USER REQUESTS
            // -------------------------------------------------

            printRequestsListener =
                    DatabaseConfig
                            .getFirestore()
                            .collection("PrintRequests")
                            .whereEqualTo(
                                    "userId",
                                    userId
                            )
                            .addSnapshotListener(

                                    (
                                            snapshot,
                                            error
                                    ) -> {

                                        if (
                                                error != null
                                        ) {

                                            System.err.println(
                                                    "[Activity] "
                                                            + "PrintRequests listener error:"
                                            );

                                            error.printStackTrace();

                                            return;
                                        }


                                        if (
                                                snapshot == null
                                        ) {

                                            return;
                                        }


                                        synchronized (
                                                activityEvents
                                        ) {

                                            // -------------------------------------
                                            // REMOVE OLD REQUEST EVENTS
                                            // -------------------------------------

                                            activityEvents.removeIf(
                                                    event ->
                                                            "REQUEST"
                                                                    .equals(
                                                                            event.getSource()
                                                                    )
                                            );


                                            // -------------------------------------
                                            // CREATE CURRENT REQUEST EVENTS
                                            // -------------------------------------

                                            for (
                                                    DocumentSnapshot request
                                                    : snapshot.getDocuments()
                                            ) {

                                                String requestId =
                                                        request.getId();


                                                String documentName =
                                                        safeString(
                                                                request.getString(
                                                                        "documentName"
                                                                ),
                                                                "Document"
                                                        );


                                                String requestedAt =
                                                        request.getString(
                                                                "requestedAt"
                                                        );


                                                Instant eventTime =
                                                        parseInstant(
                                                                requestedAt
                                                        );


                                                if (
                                                        eventTime == null
                                                ) {

                                                    continue;
                                                }


                                                String status =
                                                        safeString(
                                                                request.getString(
                                                                        "status"
                                                                ),
                                                                "UNKNOWN"
                                                        )
                                                                .toUpperCase();


                                                String xeroxName =
                                                        safeString(
                                                                request.getString(
                                                                        "xeroxName"
                                                                ),
                                                                "Xerox Centre"
                                                        );


                                                // ---------------------------------
                                                // REQUEST CREATED
                                                // ---------------------------------

                                                ActivityEvent requestEvent =
                                                        new ActivityEvent(
                                                                requestId,
                                                                "REQUEST",
                                                                "Print Request",
                                                                documentName,
                                                                "Sent to "
                                                                        + xeroxName,
                                                                eventTime,
                                                                PURPLE
                                                        );


                                                activityEvents.add(
                                                        requestEvent
                                                );


                                                // ---------------------------------
                                                // CURRENT STATUS EVENT
                                                // ---------------------------------

                                                String statusTitle =
                                                        getStatusTitle(
                                                                status
                                                        );


                                                String statusDescription =
                                                        getStatusDescription(
                                                                status
                                                        );


                                                String statusColor =
                                                        getStatusColor(
                                                                status
                                                        );


                                                ActivityEvent statusEvent =
                                                        new ActivityEvent(
                                                                requestId
                                                                        + "_STATUS",
                                                                "REQUEST",
                                                                statusTitle,
                                                                documentName,
                                                                statusDescription,
                                                                eventTime,
                                                                statusColor
                                                        );


                                                activityEvents.add(
                                                        statusEvent
                                                );
                                            }
                                        }


                                        refreshTimeline();
                                    }
                            );

        } catch (
                Exception e
        ) {

            System.err.println(
                    "[Activity] "
                            + "Unable to start PrintRequests listener."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // REFRESH TIMELINE
    // =========================================================

    private void refreshTimeline() {

        List<ActivityEvent> events;


        synchronized (
                activityEvents
        ) {

            events =
                    new ArrayList<>(
                            activityEvents
                    );
        }


        // =====================================================
        // SORT LATEST FIRST
        // =====================================================

        events.sort(
                Comparator.comparing(
                        ActivityEvent::getTime,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        // =====================================================
        // SHOW MAXIMUM 20 EVENTS
        // =====================================================

        if (
                events.size() > 20
        ) {

            events =
                    new ArrayList<>(
                            events.subList(
                                    0,
                                    20
                            )
                    );
        }


        final List<ActivityEvent> finalEvents =
                events;


        // =====================================================
        // UPDATE JAVAFX THREAD
        // =====================================================

        Platform.runLater(
                () -> {

                    if (
                            timeline == null
                    ) {

                        return;
                    }


                    timeline.getChildren().clear();


                    // -----------------------------------------
                    // EMPTY STATE
                    // -----------------------------------------

                    if (
                            finalEvents.isEmpty()
                    ) {

                        Label emptyLabel =
                                new Label(
                                        "No activity yet"
                                );

                        emptyLabel.setStyle(
                                "-fx-text-fill: "
                                        + SECONDARY + ";" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 20 0 20 0;"
                        );


                        timeline.getChildren().add(
                                emptyLabel
                        );


                        return;
                    }


                    // -----------------------------------------
                    // CREATE TIMELINE
                    // -----------------------------------------

                    for (
                            ActivityEvent event
                            : finalEvents
                    ) {

                        timeline.getChildren().add(
                                createTimelineItem(
                                        event
                                )
                        );
                    }
                }
        );
    }


    // =========================================================
    // CREATE TIMELINE ITEM
    // =========================================================

    private HBox createTimelineItem(
            ActivityEvent event
    ) {

        HBox item =
                new HBox(18);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setMinHeight(
                75
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setPadding(
                new Insets(
                        8,
                        0,
                        8,
                        0
                )
        );


        // =====================================================
        // LEFT ICON
        // =====================================================

        VBox timelineIconBox =
                new VBox();

        timelineIconBox.setAlignment(
                Pos.TOP_CENTER
        );

        timelineIconBox.setPrefWidth(
                32
        );

        timelineIconBox.setMinWidth(
                32
        );


        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel =
                new Label(
                        getEventIcon(
                                event.getTitle()
                        )
                );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                32,
                32
        );

        iconLabel.setMinSize(
                32,
                32
        );

        iconLabel.setStyle(
                "-fx-background-color: "
                        + event.getColor() + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        timelineIconBox.getChildren().add(
                iconLabel
        );


        // =====================================================
        // INFORMATION
        // =====================================================

        VBox information =
                new VBox(3);


        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );


        Label titleLabel =
                new Label(
                        event.getTitle()
                );

        titleLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label descriptionLabel =
                new Label(
                        event.getDescription()
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY + ";" +
                "-fx-font-size: 14px;"
        );


        Label documentLabel =
                new Label(
                        event.getDocumentName()
                );

        documentLabel.setStyle(
                "-fx-text-fill: "
                        + event.getColor() + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        information.getChildren().addAll(
                titleLabel,
                documentLabel,
                descriptionLabel
        );


        // =====================================================
        // TIME
        // =====================================================

        String formattedTime =
                formatDisplayTime(
                        event.getTime()
                );


        Label timeLabel =
                new Label(
                        formattedTime
                );

        timeLabel.setMinWidth(
                115
        );

        timeLabel.setAlignment(
                Pos.CENTER_RIGHT
        );

        timeLabel.setStyle(
                "-fx-text-fill: #A8A0B8;" +
                "-fx-font-size: 12px;"
        );


        // =====================================================
        // ADD CONTENT
        // =====================================================

        item.getChildren().addAll(
                timelineIconBox,
                information,
                timeLabel
        );


        // =====================================================
        // HOVER
        // =====================================================

        item.setOnMouseEntered(
                e -> {

                    item.setStyle(
                            "-fx-background-color: "
                                    + CARD_HOVER + ";" +
                            "-fx-background-radius: 12;" +
                            "-fx-cursor: hand;"
                    );

                    item.setPadding(
                            new Insets(
                                    8,
                                    10,
                                    8,
                                    10
                            )
                    );
                }
        );


        // =====================================================
        // MOUSE EXIT
        // =====================================================

        item.setOnMouseExited(
                e -> {

                    item.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-background-radius: 12;"
                    );

                    item.setPadding(
                            new Insets(
                                    8,
                                    0,
                                    8,
                                    0
                            )
                    );
                }
        );


        return item;
    }


    // =========================================================
    // GET EVENT ICON
    // =========================================================

    private String getEventIcon(
            String title
    ) {

        if (
                title == null
        ) {

            return "•";
        }


        switch (
                title.toUpperCase()
        ) {

            case "UPLOADED":

                return "⇧";


            case "PRINT REQUEST":

                return "◈";


            case "ACCEPTED":

                return "✓";


            case "APPROVED":

                return "✓";


            case "PRINTING":

                return "▣";


            case "COMPLETED":

                return "✓";


            case "REJECTED":

                return "×";


            case "EXPIRED":

                return "×";


            default:

                return "•";
        }
    }


    // =========================================================
    // STATUS TITLE
    // =========================================================

    private String getStatusTitle(
            String status
    ) {

        switch (
                status
        ) {

            case "PENDING":

                return "Pending";


            case "ACCEPTED":

                return "Accepted";


            case "APPROVED":

                return "Approved";


            case "PRINTING":

                return "Printing";


            case "COMPLETED":

                return "Completed";


            case "REJECTED":

                return "Rejected";


            case "EXPIRED":

                return "Expired";


            default:

                return "Request Updated";
        }
    }


    // =========================================================
    // STATUS DESCRIPTION
    // =========================================================

    private String getStatusDescription(
            String status
    ) {

        switch (
                status
        ) {

            case "PENDING":

                return "Waiting for Xerox centre action";


            case "ACCEPTED":

                return "Xerox centre accepted the request";


            case "APPROVED":

                return "Request approved for printing";


            case "PRINTING":

                return "Document is currently being printed";


            case "COMPLETED":

                return "Printing completed successfully";


            case "REJECTED":

                return "Xerox centre rejected the request";


            case "EXPIRED":

                return "Print request has expired";


            default:

                return "Print request status updated";
        }
    }


    // =========================================================
    // STATUS COLOR
    // =========================================================

    private String getStatusColor(
            String status
    ) {

        switch (
                status
        ) {

            case "COMPLETED":

                return GREEN;


            case "REJECTED":

                return RED;


            case "EXPIRED":

                return RED;


            case "PENDING":

                return YELLOW;


            case "ACCEPTED":

                return PURPLE;


            case "APPROVED":

                return PURPLE;


            case "PRINTING":

                return GREEN;


            default:

                return PURPLE;
        }
    }


    // =========================================================
    // FORMAT DISPLAY TIME
    // =========================================================

    private String formatDisplayTime(
            Instant instant
    ) {

        if (
                instant == null
        ) {

            return "";
        }


        long seconds =
                Math.max(
                        0,
                        Instant.now()
                                .getEpochSecond()
                                - instant.getEpochSecond()
                );


        // -----------------------------------------------------
        // JUST NOW
        // -----------------------------------------------------

        if (
                seconds < 60
        ) {

            return "Just now";
        }


        // -----------------------------------------------------
        // MINUTES
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // HOURS
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // DAYS
        // -----------------------------------------------------

        long days =
                hours / 24;


        if (
                days < 7
        ) {

            return days
                    + (
                            days == 1
                                    ? " day ago"
                                    : " days ago"
                    );
        }


        // -----------------------------------------------------
        // OLDER EVENTS
        // -----------------------------------------------------

        return DATE_FORMATTER.format(
                instant
        );
    }


    // =========================================================
    // PARSE INSTANT
    // =========================================================

    private Instant parseInstant(
            String timestamp
    ) {

        if (
                timestamp == null
                || timestamp.trim().isEmpty()
        ) {

            return null;
        }


        try {

            return Instant.parse(
                    timestamp.trim()
            );

        } catch (
                DateTimeParseException e
        ) {

            return null;
        }
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safeString(
            String value,
            String defaultValue
    ) {

        if (
                value == null
                || value.trim().isEmpty()
        ) {

            return defaultValue;
        }


        return value.trim();
    }


    // =========================================================
    // NO USER
    // =========================================================

    private void showNoUser() {

        Platform.runLater(
                () -> {

                    if (
                            timeline == null
                    ) {

                        return;
                    }


                    timeline.getChildren().clear();


                    Label label =
                            new Label(
                                    "No logged-in user found."
                            );

                    label.setStyle(
                            "-fx-text-fill: "
                                    + SECONDARY + ";" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 20 0 20 0;"
                    );


                    timeline.getChildren().add(
                            label
                    );
                }
        );
    }


    // =========================================================
    // STOP REAL-TIME LISTENERS
    // =========================================================

    public void dispose() {

        // -----------------------------------------------------
        // DOCUMENTS
        // -----------------------------------------------------

        if (
                documentsListener != null
        ) {

            documentsListener.remove();

            documentsListener = null;
        }


        // -----------------------------------------------------
        // PRINT REQUESTS
        // -----------------------------------------------------

        if (
                printRequestsListener != null
        ) {

            printRequestsListener.remove();

            printRequestsListener = null;
        }


        synchronized (
                activityEvents
        ) {

            activityEvents.clear();
        }


        System.out.println(
                "[Activity] "
                        + "All real-time listeners removed."
        );
    }


    // =========================================================
    // ACTIVITY EVENT MODEL
    // =========================================================

    private static class ActivityEvent {

        private final String id;

        private final String source;

        private final String title;

        private final String documentName;

        private final String description;

        private final Instant time;

        private final String color;


        // -----------------------------------------------------
        // CONSTRUCTOR
        // -----------------------------------------------------

        ActivityEvent(
                String id,
                String source,
                String title,
                String documentName,
                String description,
                Instant time,
                String color
        ) {

            this.id =
                    id;

            this.source =
                    source;

            this.title =
                    title;

            this.documentName =
                    documentName;

            this.description =
                    description;

            this.time =
                    time;

            this.color =
                    color;
        }


        // -----------------------------------------------------
        // GETTERS
        // -----------------------------------------------------

        public String getId() {

            return id;
        }


        public String getSource() {

            return source;
        }


        public String getTitle() {

            return title;
        }


        public String getDocumentName() {

            return documentName;
        }


        public String getDescription() {

            return description;
        }


        public Instant getTime() {

            return time;
        }


        public String getColor() {

            return color;
        }
    }
}