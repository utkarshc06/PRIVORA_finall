package com.myprivora.view.admin;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.myprivora.config.DatabaseConfig;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.myprivora.view.theme.ClayTheme;

public class Documents {

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

    private final Firestore db =
            DatabaseConfig.getFirestore();


    // =========================================================
    // REAL-TIME LISTENERS
    // =========================================================

    private ListenerRegistration documentsListener;
    private ListenerRegistration usersListener;
    private ListenerRegistration centresListener;
    private ListenerRegistration printRequestsListener;


    // =========================================================
    // LISTENER CONTROL
    // =========================================================

    private volatile boolean listenersStarted = false;


    // =========================================================
    // REAL-TIME DATA
    //
    // IMPORTANT:
    // ConcurrentHashMap is used because Firestore listeners
    // update these maps from background threads while JavaFX
    // reads them from the JavaFX Application Thread.
    // =========================================================

    private final Map<String, DocumentData> documents =
            new ConcurrentHashMap<>();

    private final Map<String, String> users =
            new ConcurrentHashMap<>();

    private final Map<String, String> centres =
            new ConcurrentHashMap<>();

    private final Map<String, PrintRequestData> printRequests =
            new ConcurrentHashMap<>();


    // =========================================================
    // UI REFERENCES
    // =========================================================

    private VBox documentList;

    private TextField searchField;

    private String selectedFilter = "All";


    // =========================================================
    // STAT LABEL REFERENCES
    // =========================================================

    private Label totalDocumentsValue;
    private Label activeDocumentsValue;
    private Label printedDocumentsValue;
    private Label expiredDocumentsValue;


    // =========================================================
    // DATE FORMAT
    // =========================================================

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, hh:mm a"
            );


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        // =====================================================
        // MAIN
        // =====================================================

        VBox main =
                new VBox(24);

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
                        + BACKGROUND
                        + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        Label title =
                new Label(
                        "Document management"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        +
                        "-fx-font-size: 32px;"
                        +
                        "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Monitor documents, sessions and privacy status across PRIVORA."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        +
                        "-fx-font-size: 16px;"
        );


        VBox heading =
                new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // STATISTICS
        // =====================================================

        HBox stats =
                new HBox(15);


        totalDocumentsValue =
                new Label("0");

        activeDocumentsValue =
                new Label("0");

        printedDocumentsValue =
                new Label("0");

        expiredDocumentsValue =
                new Label("0");


        VBox totalDocuments =
                createStatCard(
                        totalDocumentsValue,
                        "Total Documents",
                        "Uploaded documents"
                );


        VBox activeDocuments =
                createStatCard(
                        activeDocumentsValue,
                        "Active",
                        "Currently available"
                );


        VBox printedDocuments =
                createStatCard(
                        printedDocumentsValue,
                        "Printed",
                        "Completed prints"
                );


        VBox expiredDocuments =
                createStatCard(
                        expiredDocumentsValue,
                        "Expired",
                        "Sessions expired"
                );


        HBox.setHgrow(
                totalDocuments,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                activeDocuments,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                printedDocuments,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                expiredDocuments,
                Priority.ALWAYS
        );


        stats.getChildren().addAll(
                totalDocuments,
                activeDocuments,
                printedDocuments,
                expiredDocuments
        );


        // =====================================================
        // DOCUMENT CARD
        // =====================================================

        VBox documentCard =
                new VBox(18);

        documentCard.setPadding(
                new Insets(25)
        );

        documentCard.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        +
                        "-fx-border-color: "
                        + BORDER
                        + ";"
                        +
                        "-fx-border-width: 1;"
                        +
                        "-fx-border-radius: 20;"
                        +
                        "-fx-background-radius: 20;"
        );


        // =====================================================
        // SEARCH ROW
        // =====================================================

        HBox searchRow =
                new HBox(12);

        searchRow.setAlignment(
                Pos.CENTER_LEFT
        );


        searchField =
                new TextField();

        searchField.setPromptText(
                "Search documents by name or owner..."
        );

        searchField.setStyle(
                "-fx-background-color: #171D2B;"
                        +
                        "-fx-background-radius: 22;"
                        +
                        "-fx-border-color: #302A43;"
                        +
                        "-fx-border-radius: 22;"
                        +
                        "-fx-text-fill: "
                        + TEXT
                        + ";"
                        +
                        "-fx-prompt-text-fill: #777084;"
                        +
                        "-fx-font-size: 13px;"
                        +
                        "-fx-padding: 11 18 11 18;"
        );


        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );


        // =====================================================
        // LIVE SEARCH
        // =====================================================

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    renderDocuments();
                }
        );


        // =====================================================
        // FILTER BUTTON
        // =====================================================

        Button filterButton =
                new Button(
                        "☰   Filter"
                );

        filterButton.setStyle(
                "-fx-background-color: #17131F;"
                        +
                        "-fx-border-color: #39304D;"
                        +
                        "-fx-border-radius: 20;"
                        +
                        "-fx-background-radius: 20;"
                        +
                        "-fx-text-fill: "
                        + TEXT
                        + ";"
                        +
                        "-fx-font-size: 12px;"
                        +
                        "-fx-padding: 10 18;"
                        +
                        "-fx-cursor: hand;"
        );


        filterButton.setOnMouseEntered(
                e -> {

                    filterButton.setStyle(
                            "-fx-background-color: #241A32;"
                                    +
                                    "-fx-border-color: "
                                    + PURPLE
                                    + ";"
                                    +
                                    "-fx-border-radius: 20;"
                                    +
                                    "-fx-background-radius: 20;"
                                    +
                                    "-fx-text-fill: "
                                    + LIGHT_PURPLE
                                    + ";"
                                    +
                                    "-fx-font-size: 12px;"
                                    +
                                    "-fx-padding: 10 18;"
                                    +
                                    "-fx-cursor: hand;"
                    );
                }
        );


        filterButton.setOnMouseExited(
                e -> {

                    filterButton.setStyle(
                            "-fx-background-color: #17131F;"
                                    +
                                    "-fx-border-color: #39304D;"
                                    +
                                    "-fx-border-radius: 20;"
                                    +
                                    "-fx-background-radius: 20;"
                                    +
                                    "-fx-text-fill: "
                                    + TEXT
                                    + ";"
                                    +
                                    "-fx-font-size: 12px;"
                                    +
                                    "-fx-padding: 10 18;"
                                    +
                                    "-fx-cursor: hand;"
                    );
                }
        );


        // =====================================================
        // FILTER ACTION
        // =====================================================

        filterButton.setOnAction(
                event -> {

                    ChoiceDialog<String> dialog =
                            new ChoiceDialog<>(
                                    selectedFilter,
                                    FXCollections.observableArrayList(
                                            "All",
                                            "Active",
                                            "Printed",
                                            "Expired",
                                            "Pending"
                                    )
                            );


                    dialog.setTitle(
                            "Document Filter"
                    );

                    dialog.setHeaderText(
                            "Filter documents"
                    );

                    dialog.setContentText(
                            "Select status:"
                    );


                    dialog.showAndWait()
                            .ifPresent(
                                    selected -> {

                                        selectedFilter =
                                                selected;

                                        filterButton.setText(
                                                "☰   "
                                                        + selectedFilter
                                        );

                                        renderDocuments();
                                    }
                            );
                }
        );


        searchRow.getChildren().addAll(
                searchField,
                filterButton
        );


        // =====================================================
        // COLUMN HEADERS
        // =====================================================

        HBox headerRow =
                new HBox();


        headerRow.setPadding(
                new Insets(
                        8,
                        15,
                        8,
                        15
                )
        );


        Label documentHeader =
                createHeader("DOCUMENT");

        Label ownerHeader =
                createHeader("OWNER");

        Label centreHeader =
                createHeader("CENTRE");

        Label statusHeader =
                createHeader("STATUS");

        Label uploadedHeader =
                createHeader("UPLOADED");

        Label actionsHeader =
                createHeader("ACTIONS");


        documentHeader.setPrefWidth(220);
        ownerHeader.setPrefWidth(150);
        centreHeader.setPrefWidth(160);
        statusHeader.setPrefWidth(120);
        uploadedHeader.setPrefWidth(130);
        actionsHeader.setPrefWidth(100);


        headerRow.getChildren().addAll(
                documentHeader,
                ownerHeader,
                centreHeader,
                statusHeader,
                uploadedHeader,
                actionsHeader
        );


        // =====================================================
        // SEPARATOR
        // =====================================================

        HBox separator =
                new HBox();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: "
                        + BORDER
                        + ";"
        );


        // =====================================================
        // DOCUMENT LIST
        // =====================================================

        documentList =
                new VBox();

        documentList.setSpacing(0);


        // =====================================================
        // CARD CONTENT
        // =====================================================

        documentCard.getChildren().addAll(
                searchRow,
                headerRow,
                separator,
                documentList
        );


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        main.getChildren().addAll(
                heading,
                stats,
                documentCard
        );


        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(main);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        +
                        "-fx-background: transparent;"
                        +
                        "-fx-border-color: transparent;"
        );


        // =====================================================
        // START LISTENERS ONLY ONCE
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

        listenersStarted = true;


        // =====================================================
        // DOCUMENTS
        // =====================================================

        documentsListener =
                db.collection("Documents")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        error.printStackTrace();

                                        Platform.runLater(
                                                () ->
                                                        showError(
                                                                "Unable to load documents."
                                                        )
                                        );

                                        return;
                                    }


                                    if (snapshot == null) {
                                        return;
                                    }


                                    /*
                                     * ConcurrentHashMap allows the
                                     * JavaFX thread to safely iterate
                                     * while this listener updates data.
                                     */
                                    documents.clear();


                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        DocumentData data =
                                                convertDocument(
                                                        document
                                                );


                                        if (data != null) {

                                            documents.put(
                                                    data.documentId,
                                                    data
                                            );
                                        }
                                    }


                                    Platform.runLater(
                                            () -> {

                                                updateStatistics();

                                                renderDocuments();
                                            }
                                    );
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


                                    users.clear();


                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        String name =
                                                document.getString(
                                                        "name"
                                                );


                                        if (
                                                name == null
                                                        ||
                                                name.trim().isEmpty()
                                        ) {

                                            name =
                                                    document.getString(
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


                                        users.put(
                                                document.getId(),
                                                name
                                        );
                                    }


                                    Platform.runLater(
                                            this::renderDocuments
                                    );
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


                                    centres.clear();


                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        String name =
                                                document.getString(
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


                                        centres.put(
                                                document.getId(),
                                                name
                                        );
                                    }


                                    Platform.runLater(
                                            this::renderDocuments
                                    );
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


                                    printRequests.clear();


                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        PrintRequestData request =
                                                convertPrintRequest(
                                                        document
                                                );


                                        if (request != null) {

                                            printRequests.put(
                                                    request.requestId,
                                                    request
                                            );
                                        }
                                    }


                                    Platform.runLater(
                                            () -> {

                                                updateStatistics();

                                                renderDocuments();
                                            }
                                    );
                                }
                        );
    }


    // =========================================================
    // CONVERT DOCUMENT
    // =========================================================

    private DocumentData convertDocument(
            DocumentSnapshot document
    ) {

        try {

            String documentId =
                    document.getId();


            String ownerId =
                    document.getString("ownerId");


            String fileName =
                    document.getString("fileName");


            String filePath =
                    document.getString("filePath");


            String uploadedAt =
                    getTimestampAsString(
                            document.get("uploadedAt")
                    );


            String expiryTime =
                    getTimestampAsString(
                            document.get("expiryTime")
                    );


            String status =
                    document.getString("status");


            if (
                    fileName == null
                            ||
                    fileName.trim().isEmpty()
            ) {

                fileName =
                        "Unnamed Document";
            }


            if (ownerId == null) {
                ownerId = "";
            }


            if (
                    status == null
                            ||
                    status.trim().isEmpty()
            ) {

                /*
                 * Do not determine status here because
                 * printRequests may still be updating.
                 *
                 * determineDocumentStatus() is called later
                 * when rendering/statistics are calculated.
                 */
                status = "Active";
            }


            return new DocumentData(
                    documentId,
                    ownerId,
                    fileName,
                    filePath,
                    uploadedAt,
                    expiryTime,
                    status
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    // =========================================================
    // CONVERT PRINT REQUEST
    // =========================================================

    private PrintRequestData convertPrintRequest(
            DocumentSnapshot document
    ) {

        try {

            String requestId =
                    document.getId();


            String documentId =
                    document.getString("documentId");


            String documentName =
                    document.getString("documentName");


            String xeroxId =
                    document.getString("xeroxId");


            String xeroxName =
                    document.getString("xeroxName");


            String status =
                    document.getString("status");


            String requestedAt =
                    getTimestampAsString(
                            document.get("requestedAt")
                    );


            String expiresAt =
                    getTimestampAsString(
                            document.get("expiresAt")
                    );


            if (status == null) {
                status = "";
            }


            return new PrintRequestData(
                    requestId,
                    documentId,
                    documentName,
                    xeroxId,
                    xeroxName,
                    status,
                    requestedAt,
                    expiresAt
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    // =========================================================
    // DOCUMENT STATUS
    // =========================================================

    private String determineDocumentStatus(
            String documentId,
            String expiryTime
    ) {

        if (
                documentId == null
        ) {

            return "Active";
        }


        // =====================================================
        // CHECK PRINT REQUESTS
        // =====================================================

        for (
                PrintRequestData request
                : printRequests.values()
        ) {

            if (
                    request.documentId == null
                            ||
                    !documentId.equals(
                            request.documentId
                    )
            ) {

                continue;
            }


            String requestStatus =
                    normaliseStatus(
                            request.status
                    );


            if (
                    requestStatus.equalsIgnoreCase(
                            "Expired"
                    )
            ) {

                return "Expired";
            }


            if (
                    requestStatus.equalsIgnoreCase(
                            "Printed"
                    )
            ) {

                return "Printed";
            }


            if (
                    requestStatus.equalsIgnoreCase(
                            "Pending"
                    )
                            ||
                    requestStatus.equalsIgnoreCase(
                            "Active"
                    )
            ) {

                return "Active";
            }
        }


        // =====================================================
        // CHECK DOCUMENT EXPIRY
        // =====================================================

        if (
                expiryTime != null
                        &&
                !expiryTime.trim().isEmpty()
        ) {

            try {

                Instant expiry =
                        Instant.parse(
                                expiryTime
                        );


                if (
                        expiry.isBefore(
                                Instant.now()
                        )
                ) {

                    return "Expired";
                }

            } catch (Exception ignored) {
            }
        }


        return "Active";
    }


    // =========================================================
    // UPDATE STATISTICS
    // =========================================================

    private void updateStatistics() {

        if (
                totalDocumentsValue == null
        ) {

            return;
        }


        int total =
                documents.size();


        int active =
                0;


        int expired =
                0;


        /*
         * ConcurrentHashMap makes this iteration safe even
         * when Firestore listeners are updating the map.
         */
        for (
                DocumentData document
                : documents.values()
        ) {

            String status =
                    determineDocumentStatus(
                            document.documentId,
                            document.expiryTime
                    );


            if (
                    status.equalsIgnoreCase(
                            "Expired"
                    )
            ) {

                expired++;

            } else {

                active++;
            }
        }


        int printed =
                0;


        for (
                PrintRequestData request
                : printRequests.values()
        ) {

            if (
                    request.documentId == null
            ) {

                continue;
            }


            if (
                    request.status != null
                            &&
                    (
                            request.status.equalsIgnoreCase(
                                    "COMPLETED"
                            )
                                    ||
                            request.status.equalsIgnoreCase(
                                    "PRINTED"
                            )
                    )
            ) {

                printed++;
            }
        }


        totalDocumentsValue.setText(
                String.valueOf(total)
        );


        activeDocumentsValue.setText(
                String.valueOf(active)
        );


        printedDocumentsValue.setText(
                String.valueOf(printed)
        );


        expiredDocumentsValue.setText(
                String.valueOf(expired)
        );
    }


    // =========================================================
    // RENDER DOCUMENTS
    // =========================================================

    private void renderDocuments() {

        if (
                documentList == null
        ) {

            return;
        }


        documentList.getChildren().clear();


        String search =
                searchField == null
                        ? ""
                        : searchField
                                .getText()
                                .trim()
                                .toLowerCase();


        List<DocumentData> filtered =
                new ArrayList<>();


        /*
         * Copy the current values into an ArrayList.
         *
         * This gives rendering a stable collection to work with
         * even while Firestore listeners continue updating the
         * ConcurrentHashMaps.
         */
        List<DocumentData> currentDocuments =
                new ArrayList<>(
                        documents.values()
                );


        for (
                DocumentData document
                : currentDocuments
        ) {

            String owner =
                    getOwnerName(
                            document.ownerId
                    );


            String centre =
                    getCentreName(
                            document.documentId
                    );


            String status =
                    determineDocumentStatus(
                            document.documentId,
                            document.expiryTime
                    );


            // =================================================
            // SEARCH
            // =================================================

            boolean searchMatches =
                    search.isEmpty()
                            ||
                    document.fileName
                            .toLowerCase()
                            .contains(search)
                            ||
                    owner
                            .toLowerCase()
                            .contains(search)
                            ||
                    centre
                            .toLowerCase()
                            .contains(search);


            if (!searchMatches) {
                continue;
            }


            // =================================================
            // FILTER
            // =================================================

            boolean filterMatches =
                    selectedFilter.equalsIgnoreCase(
                            "All"
                    )
                            ||
                    status.equalsIgnoreCase(
                            selectedFilter
                    );


            if (!filterMatches) {
                continue;
            }


            filtered.add(
                    document
            );
        }


        // =====================================================
        // SORT
        // =====================================================

        filtered.sort(
                Comparator.comparing(
                        document ->
                                getUploadedInstant(
                                        document.uploadedAt
                                ),
                        Comparator.reverseOrder()
                )
        );


        // =====================================================
        // EMPTY
        // =====================================================

        if (
                filtered.isEmpty()
        ) {

            Label empty =
                    new Label(
                            documents.isEmpty()
                                    ? "Loading documents..."
                                    : "No documents found."
                    );


            empty.setPadding(
                    new Insets(30)
            );


            empty.setStyle(
                    "-fx-text-fill: "
                            + SECONDARY
                            + ";"
                            +
                            "-fx-font-size: 14px;"
            );


            documentList.getChildren().add(
                    empty
            );


            return;
        }


        // =====================================================
        // ROWS
        // =====================================================

        for (
                DocumentData document
                : filtered
        ) {

            documentList.getChildren().add(
                    createDocumentRow(
                            document
                    )
            );
        }
    }


    // =========================================================
    // GET OWNER NAME
    // =========================================================

    private String getOwnerName(
            String ownerId
    ) {

        if (
                ownerId == null
                        ||
                ownerId.trim().isEmpty()
        ) {

            return "Unknown Owner";
        }


        String name =
                users.get(
                        ownerId
                );


        if (
                name == null
                        ||
                name.trim().isEmpty()
        ) {

            return ownerId;
        }


        return name;
    }


    // =========================================================
    // GET CENTRE NAME
    // =========================================================

    private String getCentreName(
            String documentId
    ) {

        if (
                documentId == null
        ) {

            return "Not assigned";
        }


        PrintRequestData latestRequest =
                null;


        Instant latestTime =
                Instant.MIN;


        /*
         * ConcurrentHashMap allows safe iteration here.
         */
        List<PrintRequestData> currentRequests =
                new ArrayList<>(
                        printRequests.values()
                );


        for (
                PrintRequestData request
                : currentRequests
        ) {

            if (
                    request.documentId == null
                            ||
                    !request.documentId.equals(
                            documentId
                    )
            ) {

                continue;
            }


            Instant requestedTime =
                    getUploadedInstant(
                            request.requestedAt
                    );


            if (
                    requestedTime.isAfter(
                            latestTime
                    )
            ) {

                latestTime =
                        requestedTime;

                latestRequest =
                        request;
            }
        }


        if (
                latestRequest != null
        ) {

            if (
                    latestRequest.xeroxName != null
                            &&
                    !latestRequest.xeroxName
                            .trim()
                            .isEmpty()
            ) {

                return latestRequest.xeroxName;
            }


            if (
                    latestRequest.xeroxId != null
                            &&
                    !latestRequest.xeroxId
                            .trim()
                            .isEmpty()
            ) {

                String centre =
                        centres.get(
                                latestRequest.xeroxId
                        );


                if (
                        centre != null
                                &&
                        !centre.trim().isEmpty()
                ) {

                    return centre;
                }
            }
        }


        return "Not assigned";
    }


    // =========================================================
    // DOCUMENT ROW
    // =========================================================

    private HBox createDocumentRow(
            DocumentData document
    ) {

        HBox row =
                new HBox();


        row.setMinHeight(78);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        10,
                        15,
                        10,
                        15
                )
        );

        row.setStyle(
                "-fx-background-color: transparent;"
        );


        // =====================================================
        // DOCUMENT
        // =====================================================

        Label iconLabel =
                new Label("▤");


        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setMinSize(
                38,
                38
        );

        iconLabel.setMaxSize(
                38,
                38
        );

        iconLabel.setStyle(
                "-fx-background-color: #2B2040;"
                        +
                        "-fx-background-radius: 11;"
                        +
                        "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        +
                        "-fx-font-size: 15px;"
        );


        Label documentLabel =
                new Label(
                        document.fileName
                );


        documentLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        +
                        "-fx-font-size: 12px;"
                        +
                        "-fx-font-weight: bold;"
        );


        documentLabel.setWrapText(true);


        VBox documentInfo =
                new VBox(
                        documentLabel
                );


        HBox documentBox =
                new HBox(
                        12,
                        iconLabel,
                        documentInfo
                );


        documentBox.setAlignment(
                Pos.CENTER_LEFT
        );


        documentBox.setPrefWidth(
                220
        );


        // =====================================================
        // OWNER
        // =====================================================

        String owner =
                getOwnerName(
                        document.ownerId
                );


        Label ownerLabel =
                new Label(
                        owner
                );


        ownerLabel.setStyle(
                "-fx-text-fill: #C5BDCE;"
                        +
                        "-fx-font-size: 11px;"
        );


        ownerLabel.setWrapText(true);

        ownerLabel.setPrefWidth(150);


        // =====================================================
        // CENTRE
        // =====================================================

        String centre =
                getCentreName(
                        document.documentId
                );


        Label centreLabel =
                new Label(
                        centre
                );


        centreLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        +
                        "-fx-font-size: 11px;"
        );


        centreLabel.setWrapText(true);

        centreLabel.setPrefWidth(160);


        // =====================================================
        // STATUS
        // =====================================================

        String status =
                determineDocumentStatus(
                        document.documentId,
                        document.expiryTime
                );


        Label statusLabel =
                createStatus(
                        status
                );


        HBox statusBox =
                new HBox(
                        statusLabel
                );


        statusBox.setPrefWidth(120);

        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // UPLOADED
        // =====================================================

        Label uploadedLabel =
                new Label(
                        formatDate(
                                document.uploadedAt
                        )
                );


        uploadedLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        +
                        "-fx-font-size: 10px;"
        );


        uploadedLabel.setWrapText(true);

        uploadedLabel.setPrefWidth(130);


        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Button viewButton =
                new Button("◉");


        Button deleteButton =
                new Button("⌫");


        styleActionButton(
                viewButton,
                LIGHT_PURPLE
        );


        styleActionButton(
                deleteButton,
                "#EF6B73"
        );


        viewButton.setOnAction(
                e ->
                        showDocumentDetails(
                                document
                        )
        );


        deleteButton.setOnAction(
                e ->
                        deleteDocument(
                                document
                        )
        );


        HBox actions =
                new HBox(
                        8,
                        viewButton,
                        deleteButton
                );


        actions.setPrefWidth(100);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // ADD ROW
        // =====================================================

        row.getChildren().addAll(
                documentBox,
                ownerLabel,
                centreLabel,
                statusBox,
                uploadedLabel,
                actions
        );


        // =====================================================
        // HOVER
        // =====================================================

        row.setOnMouseEntered(
                e -> {

                    row.setStyle(
                            "-fx-background-color: "
                                    + CARD_HOVER
                                    + ";"
                                    +
                                    "-fx-background-radius: 12;"
                    );
                }
        );


        row.setOnMouseExited(
                e -> {

                    row.setStyle(
                            "-fx-background-color: transparent;"
                    );
                }
        );


        return row;
    }


    // =========================================================
    // DOCUMENT DETAILS
    // =========================================================

    private void showDocumentDetails(
            DocumentData document
    ) {

        String owner =
                getOwnerName(
                        document.ownerId
                );


        String centre =
                getCentreName(
                        document.documentId
                );


        String status =
                determineDocumentStatus(
                        document.documentId,
                        document.expiryTime
                );


        String message =
                "Document: "
                        + document.fileName
                        + "\n\n"
                        +
                        "Owner: "
                        + owner
                        + "\n\n"
                        +
                        "Centre: "
                        + centre
                        + "\n\n"
                        +
                        "Status: "
                        + status
                        + "\n\n"
                        +
                        "Uploaded: "
                        + formatDate(
                                document.uploadedAt
                        )
                        + "\n\n"
                        +
                        "Document ID: "
                        + document.documentId;


        showMessage(
                "Document Details",
                message
        );
    }


    // =========================================================
    // DELETE DOCUMENT
    // =========================================================

    private void deleteDocument(
            DocumentData document
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Document"
        );


        confirmation.setHeaderText(
                document.fileName
        );


        confirmation.setContentText(
                "Are you sure you want to delete this document from Firestore?"
        );


        confirmation.showAndWait()
                .ifPresent(
                        result -> {

                            if (
                                    result != ButtonType.OK
                            ) {

                                return;
                            }


                            db.collection(
                                    "Documents"
                            )
                            .document(
                                    document.documentId
                            )
                            .delete()
                            .addListener(
                                    () -> {

                                        Platform.runLater(
                                                () ->
                                                        showMessage(
                                                                "Document Deleted",
                                                                "Document deleted successfully."
                                                        )
                                        );
                                    },
                                    Runnable::run
                            );
                        }
                );
    }


    // =========================================================
    // CREATE STAT CARD
    // =========================================================

    private VBox createStatCard(
            Label valueLabel,
            String title,
            String description
    ) {

        VBox card =
                new VBox(7);


        card.setPadding(
                new Insets(20)
        );


        card.setMinHeight(
                110
        );


        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        +
                        "-fx-border-color: "
                        + BORDER
                        + ";"
                        +
                        "-fx-border-radius: 18;"
                        +
                        "-fx-background-radius: 18;"
        );


        valueLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        +
                        "-fx-font-size: 25px;"
                        +
                        "-fx-font-weight: bold;"
        );


        Label titleLabel =
                new Label(
                        title
                );


        titleLabel.setStyle(
                "-fx-text-fill: #D7D0E1;"
                        +
                        "-fx-font-size: 12px;"
                        +
                        "-fx-font-weight: bold;"
        );


        Label descriptionLabel =
                new Label(
                        description
                );


        descriptionLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        +
                        "-fx-font-size: 10px;"
        );


        card.getChildren().addAll(
                valueLabel,
                titleLabel,
                descriptionLabel
        );


        return card;
    }


    // =========================================================
    // STATUS LABEL
    // =========================================================

    private Label createStatus(
            String status
    ) {

        String display =
                normaliseStatus(
                        status
                );


        Label label =
                new Label(
                        display
                );


        if (
                display.equalsIgnoreCase(
                        "Active"
                )
        ) {

            label.setStyle(
                    "-fx-background-color: #17352F;"
                            +
                            "-fx-background-radius: 14;"
                            +
                            "-fx-text-fill: #4ADE80;"
                            +
                            "-fx-font-size: 10px;"
                            +
                            "-fx-font-weight: bold;"
                            +
                            "-fx-padding: 6 12;"
            );


        } else if (
                display.equalsIgnoreCase(
                        "Printed"
                )
        ) {

            label.setStyle(
                    "-fx-background-color: #29213B;"
                            +
                            "-fx-background-radius: 14;"
                            +
                            "-fx-text-fill: "
                            + LIGHT_PURPLE
                            + ";"
                            +
                            "-fx-font-size: 10px;"
                            +
                            "-fx-font-weight: bold;"
                            +
                            "-fx-padding: 6 12;"
            );


        } else if (
                display.equalsIgnoreCase(
                        "Pending"
                )
        ) {

            label.setStyle(
                    "-fx-background-color: rgba(245,158,11,0.15);"
                            +
                            "-fx-background-radius: 14;"
                            +
                            "-fx-text-fill: #F5B942;"
                            +
                            "-fx-font-size: 10px;"
                            +
                            "-fx-font-weight: bold;"
                            +
                            "-fx-padding: 6 12;"
            );


        } else {

            label.setStyle(
                    "-fx-background-color: #292735;"
                            +
                            "-fx-background-radius: 14;"
                            +
                            "-fx-text-fill: #F87171;"
                            +
                            "-fx-font-size: 10px;"
                            +
                            "-fx-font-weight: bold;"
                            +
                            "-fx-padding: 6 12;"
            );
        }


        return label;
    }


    // =========================================================
    // NORMALISE STATUS
    // =========================================================

    private String normaliseStatus(
            String status
    ) {

        if (
                status == null
                        ||
                status.trim().isEmpty()
        ) {

            return "Active";
        }


        switch (
                status.trim().toUpperCase()
        ) {

            case "ACTIVE":
                return "Active";

            case "PENDING":
                return "Pending";

            case "PRINTED":
                return "Printed";

            case "COMPLETED":
                return "Printed";

            case "EXPIRED":
                return "Expired";

            case "REJECTED":
                return "Expired";

            case "APPROVED":
                return "Active";

            case "ACCEPTED":
                return "Active";

            case "PRINTING":
                return "Active";

            default:
                return status;
        }
    }


    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(
            String value
    ) {

        if (
                value == null
                        ||
                value.trim().isEmpty()
        ) {

            return "Not available";
        }


        try {

            Instant instant =
                    Instant.parse(
                            value
                    );


            return dateFormatter.format(
                    instant.atZone(
                            ZoneId.systemDefault()
                    )
            );

        } catch (Exception e) {

            return value;
        }
    }


    // =========================================================
    // GET INSTANT
    // =========================================================

    private Instant getUploadedInstant(
            String value
    ) {

        if (
                value == null
                        ||
                value.trim().isEmpty()
        ) {

            return Instant.MIN;
        }


        try {

            return Instant.parse(
                    value
            );

        } catch (Exception e) {

            return Instant.MIN;
        }
    }


    // =========================================================
    // FIRESTORE TIMESTAMP / STRING CONVERSION
    // =========================================================

    private String getTimestampAsString(
            Object value
    ) {

        if (value == null) {
            return "";
        }


        // Firestore Timestamp
        if (
                value instanceof Timestamp
        ) {

            Timestamp timestamp =
                    (Timestamp) value;


            return timestamp
                    .toDate()
                    .toInstant()
                    .toString();
        }


        // String timestamp
        if (
                value instanceof String
        ) {

            return (String) value;
        }


        return String.valueOf(
                value
        );
    }


    // =========================================================
    // HEADER
    // =========================================================

    private Label createHeader(
            String text
    ) {

        Label label =
                new Label(
                        text
                );


        label.setStyle(
                "-fx-text-fill: #81758F;"
                        +
                        "-fx-font-size: 10px;"
                        +
                        "-fx-font-weight: bold;"
        );


        return label;
    }


    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private void styleActionButton(
            Button button,
            String textColor
    ) {

        button.setMinSize(
                32,
                32
        );


        button.setStyle(
                "-fx-background-color: transparent;"
                        +
                        "-fx-text-fill: "
                        + textColor
                        + ";"
                        +
                        "-fx-font-size: 16px;"
                        +
                        "-fx-padding: 4;"
                        +
                        "-fx-cursor: hand;"
        );


        button.setOnMouseEntered(
                e -> {

                    button.setStyle(
                            "-fx-background-color: #241A32;"
                                    +
                                    "-fx-background-radius: 10;"
                                    +
                                    "-fx-text-fill: "
                                    + textColor
                                    + ";"
                                    +
                                    "-fx-font-size: 16px;"
                                    +
                                    "-fx-padding: 4;"
                                    +
                                    "-fx-cursor: hand;"
                    );
                }
        );


        button.setOnMouseExited(
                e -> {

                    button.setStyle(
                            "-fx-background-color: transparent;"
                                    +
                                    "-fx-text-fill: "
                                    + textColor
                                    + ";"
                                    +
                                    "-fx-font-size: 16px;"
                                    +
                                    "-fx-padding: 4;"
                                    +
                                    "-fx-cursor: hand;"
                    );
                }
        );
    }


    // =========================================================
    // SHOW MESSAGE
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


    // =========================================================
    // SHOW ERROR
    // =========================================================

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "PRIVORA"
        );


        alert.setHeaderText(
                "Database Error"
        );


        alert.setContentText(
                message
        );


        alert.show();
    }


    // =========================================================
    // STOP REAL-TIME LISTENERS
    // =========================================================

    public void stopRealtimeListeners() {

        if (
                documentsListener != null
        ) {

            documentsListener.remove();

            documentsListener = null;
        }


        if (
                usersListener != null
        ) {

            usersListener.remove();

            usersListener = null;
        }


        if (
                centresListener != null
        ) {

            centresListener.remove();

            centresListener = null;
        }


        if (
                printRequestsListener != null
        ) {

            printRequestsListener.remove();

            printRequestsListener = null;
        }


        listenersStarted = false;
    }


    // =========================================================
    // DOCUMENT DATA
    // =========================================================

    private static class DocumentData {

        private final String documentId;
        private final String ownerId;
        private final String fileName;
        private final String filePath;
        private final String uploadedAt;
        private final String expiryTime;
        private final String status;


        private DocumentData(
                String documentId,
                String ownerId,
                String fileName,
                String filePath,
                String uploadedAt,
                String expiryTime,
                String status
        ) {

            this.documentId =
                    documentId;

            this.ownerId =
                    ownerId;

            this.fileName =
                    fileName;

            this.filePath =
                    filePath;

            this.uploadedAt =
                    uploadedAt;

            this.expiryTime =
                    expiryTime;

            this.status =
                    status;
        }
    }


    // =========================================================
    // PRINT REQUEST DATA
    // =========================================================

    private static class PrintRequestData {

        private final String requestId;
        private final String documentId;
        private final String documentName;
        private final String xeroxId;
        private final String xeroxName;
        private final String status;
        private final String requestedAt;
        private final String expiresAt;


        private PrintRequestData(
                String requestId,
                String documentId,
                String documentName,
                String xeroxId,
                String xeroxName,
                String status,
                String requestedAt,
                String expiresAt
        ) {

            this.requestId =
                    requestId;

            this.documentId =
                    documentId;

            this.documentName =
                    documentName;

            this.xeroxId =
                    xeroxId;

            this.xeroxName =
                    xeroxName;

            this.status =
                    status;

            this.requestedAt =
                    requestedAt;

            this.expiresAt =
                    expiresAt;
        }
    }
}