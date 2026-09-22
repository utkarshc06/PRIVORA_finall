package com.myprivora.view.admin;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.myprivora.config.DatabaseConfig;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import com.myprivora.view.theme.ClayTheme;

public class ActivityLogs {

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
    private ListenerRegistration documentsListener;
    private ListenerRegistration centresListener;
    private ListenerRegistration requestsListener;

    private boolean listenersStarted = false;


    // =========================================================
    // REAL-TIME DATA
    // =========================================================

    private final Map<String, DocumentSnapshot> users =
            new ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> documents =
            new ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> centres =
            new ConcurrentHashMap<>();

    private final Map<String, DocumentSnapshot> requests =
            new ConcurrentHashMap<>();


    // =========================================================
    // UI REFERENCES
    // =========================================================

    private VBox activityCard;

    private Label countLabel;

    private Button allButton;
    private Button userButton;
    private Button documentButton;
    private Button securityButton;


    // =========================================================
    // CURRENT FILTER
    // =========================================================

    private String currentFilter =
            "ALL";


    // =========================================================
    // DATE FORMAT
    // =========================================================

    private final DateTimeFormatter timeFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a",
                    Locale.ENGLISH
            );


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox content =
                new VBox(22);

        content.setPadding(
                new Insets(
                        30
                )
        );

        content.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title =
                new Label(
                        "Activity Logs"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 30px;"
                        + "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Monitor real-time activities across the PRIVORA platform."
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
        // FILTER BAR
        // =====================================================

        HBox filterBar =
                new HBox(
                        12
                );

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        filterBar.setPadding(
                new Insets(
                        16
                )
        );

        filterBar.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 16;"
        );


        allButton =
                createFilterButton(
                        "All Activities",
                        true
                );


        userButton =
                createFilterButton(
                        "Users",
                        false
                );


        documentButton =
                createFilterButton(
                        "Documents",
                        false
                );


        securityButton =
                createFilterButton(
                        "Security",
                        false
                );


        allButton.setOnAction(
                e -> {

                    currentFilter =
                            "ALL";

                    updateFilterButtons();

                    refreshActivityLogs();
                }
        );


        userButton.setOnAction(
                e -> {

                    currentFilter =
                            "USERS";

                    updateFilterButtons();

                    refreshActivityLogs();
                }
        );


        documentButton.setOnAction(
                e -> {

                    currentFilter =
                            "DOCUMENTS";

                    updateFilterButtons();

                    refreshActivityLogs();
                }
        );


        securityButton.setOnAction(
                e -> {

                    currentFilter =
                            "SECURITY";

                    updateFilterButtons();

                    refreshActivityLogs();
                }
        );


        filterBar.getChildren().addAll(
                allButton,
                userButton,
                documentButton,
                securityButton
        );


        // =====================================================
        // ACTIVITY CARD
        // =====================================================

        activityCard =
                new VBox();


        activityCard.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-background-radius: 18;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 18;"
        );


        // =====================================================
        // CARD HEADER
        // =====================================================

        HBox cardHeader =
                new HBox();


        cardHeader.setPadding(
                new Insets(
                        20
                )
        );

        cardHeader.setAlignment(
                Pos.CENTER_LEFT
        );


        Label activityTitle =
                new Label(
                        "Recent Activity"
                );

        activityTitle.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 17px;"
                        + "-fx-font-weight: bold;"
        );


        HBox.setHgrow(
                activityTitle,
                Priority.ALWAYS
        );


        countLabel =
                new Label(
                        "0 events"
                );

        countLabel.setStyle(
                "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );


        cardHeader.getChildren().addAll(
                activityTitle,
                countLabel
        );


        activityCard.getChildren().add(
                cardHeader
        );


        // =====================================================
        // SECURITY INFORMATION
        // =====================================================

        HBox securityBox =
                createSecurityInformation();


        // =====================================================
        // ADD CONTENT
        // =====================================================

        content.getChildren().addAll(
                heading,
                filterBar,
                activityCard,
                securityBox
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
        // START REAL-TIME LISTENERS
        // =====================================================

        startRealtimeListeners();


        return scrollPane;
    }


    // =========================================================
    // REAL-TIME LISTENERS
    // =========================================================

    private void startRealtimeListeners() {

        if (listenersStarted) {
            return;
        }


        listenersStarted =
                true;


        // =====================================================
        // USERS
        // =====================================================

        usersListener =
                db.collection("Users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "Activity Logs Users Listener Error"
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


                                    refreshActivityLogs();
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
                                                "Activity Logs Documents Listener Error"
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


                                    refreshActivityLogs();
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
                                                "Activity Logs Centres Listener Error"
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


                                    refreshActivityLogs();
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
                                                "Activity Logs Requests Listener Error"
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


                                    refreshActivityLogs();
                                }
                        );
    }


    // =========================================================
    // REFRESH ACTIVITY LOGS
    // =========================================================

    private void refreshActivityLogs() {

        Platform.runLater(
                () -> {

                    List<ActivityEvent> events =
                            buildActivityEvents();


                    events.sort(
                            Comparator.comparing(
                                    ActivityEvent::getDateTime,
                                    Comparator.nullsLast(
                                            Comparator.reverseOrder()
                                    )
                            )
                    );


                    List<ActivityEvent> filteredEvents =
                            filterEvents(
                                    events
                            );


                    activityCard
                            .getChildren()
                            .removeIf(
                                    node ->
                                            node.getProperties()
                                                    .containsKey(
                                                            "activityRow"
                                                    )
                            );


                    int maxEvents =
                            Math.min(
                                    filteredEvents.size(),
                                    50
                            );


                    for (int i = 0;
                         i < maxEvents;
                         i++) {

                        ActivityEvent event =
                                filteredEvents.get(i);


                        VBox row =
                                createLog(
                                        event
                                );


                        row.getProperties().put(
                                "activityRow",
                                true
                        );


                        activityCard.getChildren().add(
                                row
                        );
                    }


                    countLabel.setText(
                            filteredEvents.size()
                                    + " events"
                    );
                }
        );
    }


    // =========================================================
    // BUILD EVENTS FROM FIRESTORE
    // =========================================================

    private List<ActivityEvent> buildActivityEvents() {

        List<ActivityEvent> events =
                new ArrayList<>();


        // =====================================================
        // USER EVENTS
        // =====================================================

        for (DocumentSnapshot user :
                users.values()) {

            String name =
                    getString(
                            user,
                            "name"
                    );


            String email =
                    getString(
                            user,
                            "email"
                    );


            String role =
                    getString(
                            user,
                            "role"
                    );


            Object createdAt =
                    user.get("createdAt");


            LocalDateTime dateTime =
                    convertToDateTime(
                            createdAt
                    );


            if (dateTime == null) {

                dateTime =
                        getFallbackDocumentTime(
                                user
                        );
            }


            String displayName =
                    !isEmpty(name)
                            ? name
                            : email;


            if (isEmpty(displayName)) {
                displayName =
                        "User "
                                + user.getId();
            }


            String roleText =
                    !isEmpty(role)
                            ? role
                            : "User";


            events.add(
                    new ActivityEvent(
                            "U",
                            "User account registered",
                            displayName
                                    + " registered a "
                                    + roleText
                                    + " account.",
                            "Users",
                            dateTime,
                            "#8B5CF6",
                            "USERS"
                    )
            );
        }


        // =====================================================
        // DOCUMENT EVENTS
        // =====================================================

        for (DocumentSnapshot document :
                documents.values()) {

            String fileName =
                    getString(
                            document,
                            "fileName"
                    );


            if (isEmpty(fileName)) {

                fileName =
                        "Document "
                                + document.getId();
            }


            String ownerId =
                    getString(
                            document,
                            "ownerId"
                    );


            String ownerName =
                    findUserName(
                            ownerId
                    );


            Object uploadedAt =
                    document.get("uploadedAt");


            LocalDateTime dateTime =
                    convertToDateTime(
                            uploadedAt
                    );


            if (dateTime == null) {

                dateTime =
                        getFallbackDocumentTime(
                                document
                        );
            }


            String description;


            if (!isEmpty(ownerName)) {

                description =
                        fileName
                                + " was uploaded by "
                                + ownerName
                                + ".";

            } else {

                description =
                        fileName
                                + " was uploaded to PRIVORA.";
            }


            events.add(
                    new ActivityEvent(
                            "D",
                            "Document uploaded",
                            description,
                            "Documents",
                            dateTime,
                            "#A78BFA",
                            "DOCUMENTS"
                    )
            );
        }


        // =====================================================
        // XEROX CENTRE EVENTS
        // =====================================================

        for (DocumentSnapshot centre :
                centres.values()) {

            String name =
                    getString(
                            centre,
                            "name"
                    );


            if (isEmpty(name)) {

                name =
                        "Xerox Centre "
                                + centre.getId();
            }


            String status =
                    getString(
                            centre,
                            "status"
                    );


            if (isEmpty(status)) {
                status =
                        "ACTIVE";
            }


            Object createdAt =
                    centre.get("createdAt");


            LocalDateTime dateTime =
                    convertToDateTime(
                            createdAt
                    );


            if (dateTime == null) {

                dateTime =
                        getFallbackDocumentTime(
                                centre
                        );
            }


            String normalizedStatus =
                    status.toUpperCase();


            if ("ACTIVE".equals(
                    normalizedStatus
            )) {

                events.add(
                        new ActivityEvent(
                                "C",
                                "Xerox centre active",
                                name
                                        + " is currently active and available.",
                                "Centre Management",
                                dateTime,
                                "#22C55E",
                                "ALL"
                        )
                );

            } else if ("PENDING".equals(
                    normalizedStatus
            )) {

                events.add(
                        new ActivityEvent(
                                "C",
                                "Centre verification pending",
                                name
                                        + " is waiting for administrator verification.",
                                "Centre Management",
                                dateTime,
                                "#F59E0B",
                                "SECURITY"
                        )
                );

            } else if ("REJECTED".equals(
                    normalizedStatus
            )) {

                events.add(
                        new ActivityEvent(
                                "X",
                                "Centre registration rejected",
                                name
                                        + " has been rejected.",
                                "Centre Management",
                                dateTime,
                                "#EF4444",
                                "SECURITY"
                        )
                );

            } else if ("BLOCKED".equals(
                    normalizedStatus
            )) {

                events.add(
                        new ActivityEvent(
                                "X",
                                "Xerox centre blocked",
                                name
                                        + " is currently blocked.",
                                "Centre Management",
                                dateTime,
                                "#EF4444",
                                "SECURITY"
                        )
                );
            }
        }


        // =====================================================
        // PRINT REQUEST EVENTS
        // =====================================================

        for (DocumentSnapshot request :
                requests.values()) {

            String documentName =
                    getString(
                            request,
                            "documentName"
                    );


            if (isEmpty(documentName)) {

                documentName =
                        "Document";
            }


            String status =
                    getString(
                            request,
                            "status"
                    );


            if (isEmpty(status)) {

                status =
                        "UNKNOWN";
            }


            status =
                    status.toUpperCase();


            String userId =
                    getString(
                            request,
                            "userId"
                    );


            String userName =
                    findUserName(
                            userId
                    );


            Object requestedAt =
                    request.get(
                            "requestedAt"
                    );


            LocalDateTime dateTime =
                    convertToDateTime(
                            requestedAt
                    );


            if (dateTime == null) {

                dateTime =
                        getFallbackDocumentTime(
                                request
                        );
            }


            String actorText =
                    !isEmpty(userName)
                            ? userName
                            : "A user";


            String title;
            String description;
            String icon;
            String iconColor;
            String category;
            String filter;


            switch (status) {

                case "PENDING":

                    title =
                            "Print request created";

                    description =
                            actorText
                                    + " requested printing for "
                                    + documentName
                                    + ".";

                    icon =
                            "P";

                    iconColor =
                            "#8B5CF6";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;


                case "ACCEPTED":

                    title =
                            "Print request accepted";

                    description =
                            "A Xerox centre accepted the request for "
                                    + documentName
                                    + ".";

                    icon =
                            "A";

                    iconColor =
                            "#22C55E";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;


                case "APPROVED":

                    title =
                            "Print request approved";

                    description =
                            "The print request for "
                                    + documentName
                                    + " was approved.";

                    icon =
                            "✓";

                    iconColor =
                            "#22C55E";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;


                case "PRINTING":

                    title =
                            "Document printing";

                    description =
                            documentName
                                    + " is currently being printed.";

                    icon =
                            "P";

                    iconColor =
                            "#A78BFA";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;


                case "COMPLETED":

                    title =
                            "Print session completed";

                    description =
                            "Printing for "
                                    + documentName
                                    + " was completed.";

                    icon =
                            "✓";

                    iconColor =
                            "#22C55E";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;


                case "REJECTED":

                    title =
                            "Print request rejected";

                    description =
                            "The print request for "
                                    + documentName
                                    + " was rejected.";

                    icon =
                            "X";

                    iconColor =
                            "#EF4444";

                    category =
                            "Security";

                    filter =
                            "SECURITY";

                    break;


                case "EXPIRED":

                    title =
                            "Print request expired";

                    description =
                            "The request for "
                                    + documentName
                                    + " expired.";

                    icon =
                            "!";

                    iconColor =
                            "#F59E0B";

                    category =
                            "Security";

                    filter =
                            "SECURITY";

                    break;


                default:

                    title =
                            "Print request updated";

                    description =
                            "The request for "
                                    + documentName
                                    + " changed to "
                                    + status
                                    + ".";

                    icon =
                            "R";

                    iconColor =
                            "#A78BFA";

                    category =
                            "Printing";

                    filter =
                            "ALL";

                    break;
            }


            events.add(
                    new ActivityEvent(
                            icon,
                            title,
                            description,
                            category,
                            dateTime,
                            iconColor,
                            filter
                    )
            );
        }


        return events;
    }


    // =========================================================
    // FILTER EVENTS
    // =========================================================

    private List<ActivityEvent> filterEvents(
            List<ActivityEvent> events
    ) {

        List<ActivityEvent> filtered =
                new ArrayList<>();


        for (ActivityEvent event :
                events) {

            if ("ALL".equals(
                    currentFilter
            )) {

                filtered.add(
                        event
                );

                continue;
            }


            if ("USERS".equals(
                    currentFilter
            )) {

                if ("USERS".equals(
                        event.getFilter()
                )) {

                    filtered.add(
                            event
                    );
                }

                continue;
            }


            if ("DOCUMENTS".equals(
                    currentFilter
            )) {

                if ("DOCUMENTS".equals(
                        event.getFilter()
                )) {

                    filtered.add(
                            event
                    );
                }

                continue;
            }


            if ("SECURITY".equals(
                    currentFilter
            )) {

                if ("SECURITY".equals(
                        event.getFilter()
                )) {

                    filtered.add(
                            event
                    );
                }
            }
        }


        return filtered;
    }


    // =========================================================
    // CREATE LOG ROW
    // =========================================================

    private VBox createLog(
            ActivityEvent event
    ) {

        VBox container =
                new VBox();


        container.setPadding(
                new Insets(
                        17,
                        20,
                        17,
                        20
                )
        );


        container.setSpacing(
                10
        );


        container.setStyle(
                "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-width: 1 0 0 0;"
        );


        HBox row =
                new HBox(
                        15
                );


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // ICON
        // =====================================================

        VBox iconBox =
                new VBox();


        iconBox.setAlignment(
                Pos.CENTER
        );


        iconBox.setMinWidth(
                42
        );

        iconBox.setMaxWidth(
                42
        );

        iconBox.setMinHeight(
                42
        );

        iconBox.setMaxHeight(
                42
        );


        iconBox.setStyle(
                "-fx-background-color: "
                        + event.getIconColor()
                        + ";"
                        + "-fx-background-radius: 50%;"
        );


        Label icon =
                new Label(
                        event.getIcon()
                );


        icon.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );


        iconBox.getChildren().add(
                icon
        );


        // =====================================================
        // TEXT
        // =====================================================

        VBox textBox =
                new VBox(
                        4
                );


        Label titleLabel =
                new Label(
                        event.getTitle()
                );


        titleLabel.setStyle(
                "-fx-text-fill: #E9EEF5;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );


        Label descriptionLabel =
                new Label(
                        event.getDescription()
                );


        descriptionLabel.setWrapText(
                true
        );


        descriptionLabel.setStyle(
                "-fx-text-fill: #7E8999;"
                        + "-fx-font-size: 11px;"
        );


        textBox.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );


        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        // =====================================================
        // RIGHT SIDE
        // =====================================================

        VBox rightBox =
                new VBox(
                        5
                );


        rightBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        Label categoryLabel =
                new Label(
                        event.getCategory()
                );


        categoryLabel.setStyle(
                "-fx-background-color: #181329;"
                        + "-fx-background-radius: 10;"
                        + "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        + "-fx-font-size: 9px;"
                        + "-fx-padding: 5 9;"
        );


        Label timeLabel =
                new Label(
                        formatTime(
                                event.getDateTime()
                        )
                );


        timeLabel.setStyle(
                "-fx-text-fill: #657285;"
                        + "-fx-font-size: 9px;"
        );


        rightBox.getChildren().addAll(
                categoryLabel,
                timeLabel
        );


        row.getChildren().addAll(
                iconBox,
                textBox,
                rightBox
        );


        container.getChildren().add(
                row
        );


        // =====================================================
        // HOVER
        // =====================================================

        container.setOnMouseEntered(
                e ->
                        container.setStyle(
                                "-fx-background-color: "
                                        + CARD_HOVER
                                        + ";"
                                        + "-fx-border-color: "
                                        + BORDER
                                        + ";"
                                        + "-fx-border-width: 1 0 0 0;"
                        )
        );


        container.setOnMouseExited(
                e ->
                        container.setStyle(
                                "-fx-background-color: transparent;"
                                        + "-fx-border-color: "
                                        + BORDER
                                        + ";"
                                        + "-fx-border-width: 1 0 0 0;"
                        )
        );


        return container;
    }


    // =========================================================
    // SECURITY INFORMATION
    // =========================================================

    private HBox createSecurityInformation() {

        HBox securityBox =
                new HBox(
                        15
                );


        securityBox.setAlignment(
                Pos.CENTER_LEFT
        );


        securityBox.setPadding(
                new Insets(
                        18
                )
        );


        securityBox.setStyle(
                "-fx-background-color: #101722;"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-color: #262137;"
                        + "-fx-border-radius: 16;"
        );


        VBox securityIconBox =
                new VBox();


        securityIconBox.setAlignment(
                Pos.CENTER
        );


        securityIconBox.setPrefSize(
                36,
                36
        );


        securityIconBox.setStyle(
                "-fx-background-color: #21163A;"
                        + "-fx-background-radius: 50%;"
        );


        Label securityIcon =
                new Label(
                        "✓"
                );


        securityIcon.setStyle(
                "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        + "-fx-font-size: 14px;"
                        + "-fx-font-weight: bold;"
        );


        securityIconBox.getChildren().add(
                securityIcon
        );


        VBox securityText =
                new VBox(
                        3
                );


        Label securityTitle =
                new Label(
                        "Real-time monitoring active"
                );


        securityTitle.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );


        Label securityDescription =
                new Label(
                        "Activity data is synchronized from PRIVORA Firestore collections."
                );


        securityDescription.setWrapText(
                true
        );


        securityDescription.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 11px;"
        );


        securityText.getChildren().addAll(
                securityTitle,
                securityDescription
        );


        HBox.setHgrow(
                securityText,
                Priority.ALWAYS
        );


        securityBox.getChildren().addAll(
                securityIconBox,
                securityText
        );


        return securityBox;
    }


    // =========================================================
    // FILTER BUTTON
    // =========================================================

    private Button createFilterButton(
            String text,
            boolean selected
    ) {

        Button button =
                new Button(
                        text
                );


        button.setPadding(
                new Insets(
                        9,
                        16,
                        9,
                        16
                )
        );


        if (selected) {

            button.setStyle(
                    "-fx-background-color: "
                            + PURPLE
                            + ";"
                            + "-fx-background-radius: 10;"
                            + "-fx-text-fill: white;"
                            + "-fx-font-size: 11px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: #14111D;"
                            + "-fx-background-radius: 10;"
                            + "-fx-text-fill: #8C829D;"
                            + "-fx-font-size: 11px;"
                            + "-fx-cursor: hand;"
            );
        }


        return button;
    }


    // =========================================================
    // UPDATE FILTER BUTTONS
    // =========================================================

    private void updateFilterButtons() {

        setFilterButtonStyle(
                allButton,
                "ALL".equals(
                        currentFilter
                )
        );


        setFilterButtonStyle(
                userButton,
                "USERS".equals(
                        currentFilter
                )
        );


        setFilterButtonStyle(
                documentButton,
                "DOCUMENTS".equals(
                        currentFilter
                )
        );


        setFilterButtonStyle(
                securityButton,
                "SECURITY".equals(
                        currentFilter
                )
        );
    }


    // =========================================================
    // FILTER BUTTON STYLE
    // =========================================================

    private void setFilterButtonStyle(
            Button button,
            boolean selected
    ) {

        if (button == null) {
            return;
        }


        if (selected) {

            button.setStyle(
                    "-fx-background-color: "
                            + PURPLE
                            + ";"
                            + "-fx-background-radius: 10;"
                            + "-fx-text-fill: white;"
                            + "-fx-font-size: 11px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: #14111D;"
                            + "-fx-background-radius: 10;"
                            + "-fx-text-fill: #8C829D;"
                            + "-fx-font-size: 11px;"
                            + "-fx-cursor: hand;"
            );
        }
    }


    // =========================================================
    // FIND USER NAME
    // =========================================================

    private String findUserName(
            String userId
    ) {

        if (isEmpty(userId)) {
            return null;
        }


        DocumentSnapshot user =
                users.get(
                        userId
                );


        if (user == null) {
            return null;
        }


        String name =
                getString(
                        user,
                        "name"
                );


        if (!isEmpty(name)) {
            return name;
        }


        return getString(
                user,
                "email"
        );
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String getString(
            DocumentSnapshot document,
            String field
    ) {

        Object value =
                document.get(
                        field
                );


        if (value == null) {
            return null;
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // EMPTY CHECK
    // =========================================================

    private boolean isEmpty(
            String value
    ) {

        return value == null
                || value.trim().isEmpty();
    }


    // =========================================================
    // CONVERT FIRESTORE DATE
    // =========================================================

    private LocalDateTime convertToDateTime(
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
                                ZoneIdHolder.ZONE
                        )
                        .toLocalDateTime();
            }


            // -------------------------------------------------
            // Java Date
            // -------------------------------------------------

            if (value instanceof java.util.Date) {

                java.util.Date date =
                        (java.util.Date) value;


                return date
                        .toInstant()
                        .atZone(
                                ZoneIdHolder.ZONE
                        )
                        .toLocalDateTime();
            }


            // -------------------------------------------------
            // Instant
            // -------------------------------------------------

            if (value instanceof Instant) {

                return ((Instant) value)
                        .atZone(
                                ZoneIdHolder.ZONE
                        )
                        .toLocalDateTime();
            }


            // -------------------------------------------------
            // String
            // -------------------------------------------------

            if (value instanceof String) {

                String text =
                        value.toString()
                                .trim();


                // ISO timestamp
                try {

                    return Instant
                            .parse(
                                    text
                            )
                            .atZone(
                                    ZoneIdHolder.ZONE
                            )
                            .toLocalDateTime();

                } catch (Exception ignored) {
                }


                // LocalDateTime
                try {

                    return LocalDateTime.parse(
                            text
                    );

                } catch (Exception ignored) {
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Activity Logs date conversion error: "
                            + value
            );
        }


        return null;
    }


    // =========================================================
    // FALLBACK FIRESTORE DOCUMENT TIME
    // =========================================================

    private LocalDateTime getFallbackDocumentTime(
            DocumentSnapshot document
    ) {

        if (document == null) {
            return null;
        }


        try {

            if (document.getCreateTime() != null) {

                return document
                        .getCreateTime()
                        .toDate()
                        .toInstant()
                        .atZone(
                                ZoneIdHolder.ZONE
                        )
                        .toLocalDateTime();
            }

        } catch (Exception ignored) {
        }


        return null;
    }


    // =========================================================
    // FORMAT TIME
    // =========================================================

    private String formatTime(
            LocalDateTime dateTime
    ) {

        if (dateTime == null) {

            return "Time unavailable";
        }


        LocalDateTime now =
                LocalDateTime.now();


        long minutes =
                ChronoUnit.MINUTES.between(
                        dateTime,
                        now
                );


        if (minutes >= 0
                && minutes < 1) {

            return "Just now";
        }


        if (minutes >= 1
                && minutes < 60) {

            return minutes
                    + " minute"
                    + (minutes == 1
                            ? ""
                            : "s")
                    + " ago";
        }


        long hours =
                ChronoUnit.HOURS.between(
                        dateTime,
                        now
                );


        if (hours >= 1
                && hours < 24) {

            return hours
                    + " hour"
                    + (hours == 1
                            ? ""
                            : "s")
                    + " ago";
        }


        long days =
                ChronoUnit.DAYS.between(
                        dateTime,
                        now
                );


        if (days >= 1
                && days < 7) {

            return days
                    + " day"
                    + (days == 1
                            ? ""
                            : "s")
                    + " ago";
        }


        return dateTime.format(
                timeFormatter
        );
    }


    // =========================================================
    // STOP LISTENERS
    // =========================================================

    public void stopRealtimeListeners() {

        if (usersListener != null) {

            usersListener.remove();

            usersListener = null;
        }


        if (documentsListener != null) {

            documentsListener.remove();

            documentsListener = null;
        }


        if (centresListener != null) {

            centresListener.remove();

            centresListener = null;
        }


        if (requestsListener != null) {

            requestsListener.remove();

            requestsListener = null;
        }


        listenersStarted =
                false;


        System.out.println(
                "Activity Logs real-time listeners stopped."
        );
    }


    // =========================================================
    // ACTIVITY EVENT MODEL
    // =========================================================

    private static class ActivityEvent {

        private final String icon;
        private final String title;
        private final String description;
        private final String category;
        private final LocalDateTime dateTime;
        private final String iconColor;
        private final String filter;


        ActivityEvent(
                String icon,
                String title,
                String description,
                String category,
                LocalDateTime dateTime,
                String iconColor,
                String filter
        ) {

            this.icon =
                    icon;

            this.title =
                    title;

            this.description =
                    description;

            this.category =
                    category;

            this.dateTime =
                    dateTime;

            this.iconColor =
                    iconColor;

            this.filter =
                    filter;
        }


        public String getIcon() {
            return icon;
        }


        public String getTitle() {
            return title;
        }


        public String getDescription() {
            return description;
        }


        public String getCategory() {
            return category;
        }


        public LocalDateTime getDateTime() {
            return dateTime;
        }


        public String getIconColor() {
            return iconColor;
        }


        public String getFilter() {
            return filter;
        }
    }


    // =========================================================
    // TIMEZONE HOLDER
    // =========================================================

    private static class ZoneIdHolder {

        private static final java.time.ZoneId ZONE =
                java.time.ZoneId.systemDefault();
    }
}