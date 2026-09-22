package com.myprivora.view.admin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.myprivora.config.DatabaseConfig;
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

// =============================================================
// CENTRES
// =============================================================

public class Centres {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private static final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;

    private static final String CARD = ClayTheme.OBSIDIAN_GLASS;

    private static final String CARD_HOVER = ClayTheme.OBSIDIAN_SURFACE;

    private static final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private static final String PURPLE = ClayTheme.CYAN_PRIMARY;

    private static final String LIGHT_PURPLE = ClayTheme.CYAN_LIGHT;

    private static final String TEXT = ClayTheme.FROST_WHITE;

    private static final String SECONDARY = ClayTheme.FROST_MUTED;


    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore db =
            DatabaseConfig.getFirestore();


    // =========================================================
    // REAL-TIME LISTENER
    // =========================================================

    private ListenerRegistration centresListener;


    // =========================================================
    // DATA
    // =========================================================

    private final Map<String, CentreData> centres =
            new LinkedHashMap<>();


    // =========================================================
    // UI REFERENCES
    // =========================================================

    private VBox rows;

    private TextField searchField;

    private String selectedFilter = "All";


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        // =====================================================
        // MAIN PAGE CONTENT
        // =====================================================

        VBox root = new VBox(22);

        root.setPadding(
                new Insets(30, 38, 40, 38)
        );

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title = new Label(
                "Centre management"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle = new Label(
                "Approve, reject or block registered print centres."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 15px;"
        );


        VBox heading = new VBox(
                5,
                title,
                subtitle
        );


        // =====================================================
        // MAIN CARD
        // =====================================================

        VBox card = new VBox();

        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 22;"
        );


        // =====================================================
        // TOOLBAR
        // =====================================================

        HBox toolbar = new HBox(10);

        toolbar.setPadding(
                new Insets(25, 28, 20, 28)
        );


        // =====================================================
        // SEARCH
        // =====================================================

        searchField = new TextField();

        searchField.setPromptText(
                "⌕  Search centres by name or city..."
        );

        searchField.setPrefHeight(48);

        searchField.setStyle(
                "-fx-background-color: " + CARD_HOVER + ";" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: transparent;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 0 18;"
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

                    renderRows();
                }
        );


        // =====================================================
        // FILTER BUTTON
        // =====================================================

        Button filterButton = new Button(
                "▽  Filter"
        );

        filterButton.setPrefHeight(48);

        filterButton.setPrefWidth(120);

        filterButton.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // FILTER HOVER
        // =====================================================

        filterButton.setOnMouseEntered(
                event -> filterButton.setStyle(
                        "-fx-background-color: " + CARD_HOVER + ";" +
                        "-fx-border-color: " + PURPLE + ";" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );

        filterButton.setOnMouseExited(
                event -> filterButton.setStyle(
                        "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: " + TEXT + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        // =====================================================
        // FILTER ACTION
        // =====================================================

        filterButton.setOnAction(event -> {

            ChoiceDialog<String> dialog =
                    new ChoiceDialog<>(
                            selectedFilter,
                            FXCollections.observableArrayList(
                                    "All",
                                    "Pending",
                                    "Active",
                                    "Rejected",
                                    "Blocked"
                            )
                    );

            dialog.setTitle(
                    "Filter Centres"
            );

            dialog.setHeaderText(
                    "Select centre status"
            );

            dialog.setContentText(
                    "Status:"
            );

            dialog.showAndWait().ifPresent(
                    selected -> {

                        selectedFilter = selected;

                        filterButton.setText(
                                "▽  " + selectedFilter
                        );

                        renderRows();
                    }
            );
        });


        // =====================================================
        // ADD BUTTON
        // =====================================================

        Button addButton = new Button(
                "+  Add"
        );

        addButton.setPrefHeight(48);

        addButton.setPrefWidth(110);

        addButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", " + LIGHT_PURPLE + ");" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // ADD BUTTON HOVER
        // =====================================================

        addButton.setOnMouseEntered(
                event -> addButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + LIGHT_PURPLE + ", #80F6FF);" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: #080C16;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );

        addButton.setOnMouseExited(
                event -> addButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + PURPLE + ", " + LIGHT_PURPLE + ");" +
                        "-fx-background-radius: 18;" +
                        "-fx-text-fill: #080C16;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        // =====================================================
        // ADD ACTION
        // =====================================================

        addButton.setOnAction(event -> {

            Alert alert = new Alert(
                    Alert.AlertType.INFORMATION
            );

            alert.setTitle(
                    "Add Centre"
            );

            alert.setHeaderText(
                    "Centre registration"
            );

            alert.setContentText(
                    "New Xerox centres are registered through the PRIVORA registration process."
            );

            alert.showAndWait();
        });


        // =====================================================
        // TOOLBAR CHILDREN
        // =====================================================

        toolbar.getChildren().addAll(
                searchField,
                filterButton,
                addButton
        );


        // =====================================================
        // TABLE HEADER
        // =====================================================

        HBox tableHeader =
                createTableHeader();


        // =====================================================
        // CENTRE ROWS CONTAINER
        // =====================================================

        rows = new VBox();


        // =====================================================
        // CARD CONTENT
        // =====================================================

        card.getChildren().addAll(
                toolbar,
                tableHeader,
                rows
        );


        // =====================================================
        // ROOT CONTENT
        // =====================================================

        root.getChildren().addAll(
                heading,
                card
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(
                root
        );


        // =====================================================
        // SCROLL SETTINGS
        // =====================================================

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );


        // =====================================================
        // SCROLL STYLE
        // =====================================================

        scrollPane.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                "-fx-border-color: transparent;"
        );


        // =====================================================
        // START REAL-TIME LISTENER
        // =====================================================

        startRealtimeListener();


        // =====================================================
        // RETURN
        // =====================================================

        return scrollPane;
    }


    // =========================================================
    // REAL-TIME FIRESTORE LISTENER
    // =========================================================

    private void startRealtimeListener() {

        // -----------------------------------------------------
        // Remove previous listener if one exists
        // -----------------------------------------------------

        stopRealtimeListener();


        // -----------------------------------------------------
        // Listen to XeroxCentres collection
        // -----------------------------------------------------

        centresListener =
                db.collection("XeroxCentres")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        error.printStackTrace();

                                        Platform.runLater(() -> {

                                            showErrorRow(
                                                    "Unable to load centres."
                                            );
                                        });

                                        return;
                                    }


                                    if (snapshot == null) {

                                        return;
                                    }


                                    // -------------------------------------------------
                                    // Temporary map
                                    // -------------------------------------------------

                                    Map<String, CentreData>
                                            updatedCentres =
                                            new LinkedHashMap<>();


                                    // -------------------------------------------------
                                    // Read Firestore documents
                                    // -------------------------------------------------

                                    for (
                                            DocumentSnapshot document
                                            : snapshot.getDocuments()
                                    ) {

                                        CentreData centre =
                                                convertDocument(
                                                        document
                                                );


                                        if (centre != null) {

                                            updatedCentres.put(
                                                    centre.uid,
                                                    centre
                                            );
                                        }
                                    }


                                    // -------------------------------------------------
                                    // Update application data
                                    // -------------------------------------------------

                                    centres.clear();

                                    centres.putAll(
                                            updatedCentres
                                    );


                                    // -------------------------------------------------
                                    // Update JavaFX UI
                                    // -------------------------------------------------

                                    Platform.runLater(
                                            this::renderRows
                                    );
                                }
                        );
    }


    // =========================================================
    // CONVERT FIRESTORE DOCUMENT
    // =========================================================

    private CentreData convertDocument(
            DocumentSnapshot document
    ) {

        try {

            String uid =
                    document.getId();


            // -------------------------------------------------
            // Name
            // -------------------------------------------------

            String name =
                    document.getString("name");


            if (
                    name == null ||
                    name.trim().isEmpty()
            ) {

                name = "Unnamed Centre";
            }


            // -------------------------------------------------
            // Email
            // -------------------------------------------------

            String email =
                    document.getString("email");


            if (
                    email == null ||
                    email.trim().isEmpty()
            ) {

                email = "";
            }


            // -------------------------------------------------
            // Mobile
            // -------------------------------------------------

            String mobile =
                    document.getString("mobile");


            if (
                    mobile == null ||
                    mobile.trim().isEmpty()
            ) {

                mobile = "";
            }


            // -------------------------------------------------
            // Status
            // -------------------------------------------------

            String status =
                    document.getString("status");


            if (
                    status == null ||
                    status.trim().isEmpty()
            ) {

                status = "PENDING";
            }


            // -------------------------------------------------
            // Address
            // -------------------------------------------------

            String address =
                    document.getString("address");


            if (
                    address == null ||
                    address.trim().isEmpty()
            ) {

                address =
                        document.getString("city");
            }


            if (
                    address == null ||
                    address.trim().isEmpty()
            ) {

                address = "Address not available";
            }


            return new CentreData(
                    uid,
                    name,
                    email,
                    mobile,
                    address,
                    status
            );

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }


    // =========================================================
    // RENDER ROWS
    // =========================================================

    private void renderRows() {

        if (rows == null) {

            return;
        }


        // -----------------------------------------------------
        // Clear current rows
        // -----------------------------------------------------

        rows.getChildren().clear();


        // -----------------------------------------------------
        // Search text
        // -----------------------------------------------------

        String searchText =
                searchField == null
                        ? ""
                        : searchField.getText()
                                .trim()
                                .toLowerCase();


        // -----------------------------------------------------
        // Filter matching centres
        // -----------------------------------------------------

        List<CentreData> filtered =
                new ArrayList<>();


        for (
                CentreData centre
                : centres.values()
        ) {

            // -------------------------------------------------
            // Search matching
            // -------------------------------------------------

            boolean matchesSearch =
                    searchText.isEmpty()
                    ||
                    centre.name
                            .toLowerCase()
                            .contains(searchText)
                    ||
                    centre.email
                            .toLowerCase()
                            .contains(searchText)
                    ||
                    centre.mobile
                            .toLowerCase()
                            .contains(searchText)
                    ||
                    centre.address
                            .toLowerCase()
                            .contains(searchText);


            if (!matchesSearch) {

                continue;
            }


            // -------------------------------------------------
            // Status filter
            // -------------------------------------------------

            boolean matchesFilter =
                    selectedFilter.equalsIgnoreCase("All")
                    ||
                    normaliseStatus(
                            centre.status
                    ).equalsIgnoreCase(
                            selectedFilter
                    );


            if (!matchesFilter) {

                continue;
            }


            filtered.add(
                    centre
            );
        }


        // -----------------------------------------------------
        // Sort alphabetically
        // -----------------------------------------------------

        filtered.sort(
                Comparator.comparing(
                        centre ->
                                centre.name
                                        .toLowerCase()
                )
        );


        // -----------------------------------------------------
        // No centres
        // -----------------------------------------------------

        if (filtered.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            centres.isEmpty()
                                    ? "Loading centres..."
                                    : "No centres found."
                    );

            emptyLabel.setPadding(
                    new Insets(30)
            );

            emptyLabel.setStyle(
                    "-fx-text-fill: " + SECONDARY + ";" +
                    "-fx-font-size: 14px;"
            );

            rows.getChildren().add(
                    emptyLabel
            );

            return;
        }


        // -----------------------------------------------------
        // Create actual Firestore rows
        // -----------------------------------------------------

        for (
                CentreData centre
                : filtered
        ) {

            rows.getChildren().add(
                    createCentreRow(
                            centre
                    )
            );
        }
    }


    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header =
                new HBox();


        header.setPadding(
                new Insets(0, 28, 14, 28)
        );


        Label centre =
                createHeaderLabel(
                        "CENTRE"
                );


        Label address =
                createHeaderLabel(
                        "ADDRESS"
                );


        Label verification =
                createHeaderLabel(
                        "VERIFICATION"
                );


        Label status =
                createHeaderLabel(
                        "STATUS"
                );


        Label actions =
                createHeaderLabel(
                        "ACTIONS"
                );


        centre.setPrefWidth(
                195
        );


        address.setPrefWidth(
                145
        );


        verification.setPrefWidth(
                145
        );


        status.setPrefWidth(
                150
        );


        HBox.setHgrow(
                actions,
                Priority.ALWAYS
        );


        header.getChildren().addAll(
                centre,
                address,
                verification,
                status,
                actions
        );


        return header;
    }


    // =========================================================
    // CENTRE ROW
    // =========================================================

    private VBox createCentreRow(
            CentreData centre
    ) {

        VBox container =
                new VBox();


        container.setPadding(
                new Insets(
                        15,
                        28,
                        15,
                        28
                )
        );


        // =====================================================
        // MAIN ROW
        // =====================================================

        HBox row =
                new HBox();


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // CENTRE ICON
        // =====================================================

        String icon =
                getInitial(
                        centre.name
                );


        Label iconLabel =
                new Label(
                        icon
                );


        iconLabel.setAlignment(
                Pos.CENTER
        );


        iconLabel.setPrefSize(
                40,
                40
        );


        iconLabel.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, "
                        + PURPLE + ", " + LIGHT_PURPLE + ");" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // CENTRE NAME
        // =====================================================

        Label centreLabel =
                new Label(
                        centre.name
                );


        centreLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        centreLabel.setWrapText(
                true
        );


        HBox centreBox =
                new HBox(
                        12,
                        iconLabel,
                        centreLabel
                );


        centreBox.setAlignment(
                Pos.CENTER_LEFT
        );


        centreBox.setPrefWidth(
                195
        );


        // =====================================================
        // ADDRESS
        // =====================================================

        Label addressLabel =
                new Label(
                        centre.address
                );


        addressLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 13px;"
        );


        addressLabel.setWrapText(
                true
        );


        addressLabel.setPrefWidth(
                145
        );


        // =====================================================
        // VERIFICATION
        // =====================================================

        String verificationText =
                getVerificationText(
                        centre.status
                );


        Label verificationLabel =
                createStatus(
                        verificationText
                );


        verificationLabel.setPrefWidth(
                145
        );


        // =====================================================
        // STATUS
        // =====================================================

        String displayStatus =
                normaliseStatus(
                        centre.status
                );


        Label statusLabel =
                createStatus(
                        displayStatus
                );


        statusLabel.setPrefWidth(
                150
        );


        // =====================================================
        // ACTIONS
        // =====================================================

        VBox actions =
                new VBox(
                        5
                );


        actions.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // APPROVE
        // =====================================================

        Button approve =
                new Button(
                        "Approve"
                );


        approve.setPrefWidth(
                90
        );


        approve.setPrefHeight(
                35
        );


        approve.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", " + LIGHT_PURPLE + ");" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        approve.setOnMouseEntered(
                event -> approve.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + LIGHT_PURPLE + ", #C4B5FD);" +
                        "-fx-background-radius: 12;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        approve.setOnMouseExited(
                event -> approve.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + PURPLE + ", " + LIGHT_PURPLE + ");" +
                        "-fx-background-radius: 12;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        approve.setOnAction(
                event ->
                        updateCentreStatus(
                                centre,
                                "ACTIVE"
                        )
        );


        // =====================================================
        // REJECT
        // =====================================================

        Button reject =
                new Button(
                        "Reject"
                );


        reject.setPrefWidth(
                90
        );


        reject.setPrefHeight(
                35
        );


        reject.setStyle(
                "-fx-background-color: " + BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;" +
                "-fx-background-radius: 12;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        reject.setOnMouseEntered(
                event -> reject.setStyle(
                        "-fx-background-color: " + CARD_HOVER + ";" +
                        "-fx-border-color: " + PURPLE + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        reject.setOnMouseExited(
                event -> reject.setStyle(
                        "-fx-background-color: " + BACKGROUND + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;" +
                        "-fx-background-radius: 12;" +
                        "-fx-text-fill: " + TEXT + ";" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
                )
        );


        reject.setOnAction(
                event ->
                        updateCentreStatus(
                                centre,
                                "REJECTED"
                        )
        );


        // =====================================================
        // BLOCK
        // =====================================================

        Button block =
                new Button(
                        "⊘"
                );


        block.setPrefWidth(
                90
        );


        block.setPrefHeight(
                35
        );


        block.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #F87171;" +
                "-fx-font-size: 20px;" +
                "-fx-cursor: hand;"
        );


        block.setOnAction(
                event ->
                        updateCentreStatus(
                                centre,
                                "BLOCKED"
                        )
        );


        actions.getChildren().addAll(
                approve,
                reject,
                block
        );


        HBox.setHgrow(
                actions,
                Priority.ALWAYS
        );


        // =====================================================
        // ADD ROW COMPONENTS
        // =====================================================

        row.getChildren().addAll(
                centreBox,
                addressLabel,
                verificationLabel,
                statusLabel,
                actions
        );


        // =====================================================
        // CONTAINER
        // =====================================================

        container.getChildren().add(
                row
        );


        container.setStyle(
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );


        // =====================================================
        // ROW HOVER
        // =====================================================

        container.setOnMouseEntered(
                event -> container.setStyle(
                        "-fx-background-color: " + CARD_HOVER + ";" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;"
                )
        );


        container.setOnMouseExited(
                event -> container.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-width: 0 0 1 0;"
                )
        );


        return container;
    }


    // =========================================================
    // UPDATE CENTRE STATUS
    // =========================================================

    private void updateCentreStatus(
            CentreData centre,
            String newStatus
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Centre Status"
        );


        confirmation.setHeaderText(
                centre.name
        );


        confirmation.setContentText(
                "Change centre status to "
                        + normaliseStatus(newStatus)
                        + "?"
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
                                    "XeroxCentres"
                            )
                            .document(
                                    centre.uid
                            )
                            .update(
                                    "status",
                                    newStatus
                            )
                            .addListener(
                                    () -> {

                                        Platform.runLater(
                                                () -> {

                                                    showInfo(
                                                            "Centre status updated successfully."
                                                    );
                                                }
                                        );

                                    },
                                    Runnable::run
                            );
                        }
                );
    }


    // =========================================================
    // NORMALISE STATUS
    // =========================================================

    private String normaliseStatus(
            String status
    ) {

        if (status == null) {

            return "Pending";
        }


        switch (
                status.trim().toUpperCase()
        ) {

            case "ACTIVE":
                return "Active";

            case "PENDING":
                return "Pending";

            case "REJECTED":
                return "Rejected";

            case "BLOCKED":
                return "Blocked";

            case "INACTIVE":
                return "Inactive";

            default:
                return status;
        }
    }


    // =========================================================
    // VERIFICATION TEXT
    // =========================================================

    private String getVerificationText(
            String status
    ) {

        if (status == null) {

            return "Pending";
        }


        switch (
                status.trim().toUpperCase()
        ) {

            case "ACTIVE":
                return "Verified";

            case "PENDING":
                return "Pending";

            case "REJECTED":
                return "Rejected";

            case "BLOCKED":
                return "Blocked";

            default:
                return "Pending";
        }
    }


    // =========================================================
    // GET INITIAL
    // =========================================================

    private String getInitial(
            String name
    ) {

        if (
                name == null ||
                name.trim().isEmpty()
        ) {

            return "?";
        }


        return name
                .trim()
                .substring(
                        0,
                        1
                )
                .toUpperCase();
    }


    // =========================================================
    // HEADER LABEL
    // =========================================================

    private Label createHeaderLabel(
            String text
    ) {

        Label label =
                new Label(
                        text
                );


        label.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );


        return label;
    }


    // =========================================================
    // STATUS LABEL
    // =========================================================

    private Label createStatus(
            String text
    ) {

        Label label =
                new Label(
                        text
                );


        // -----------------------------------------------------
        // VERIFIED / ACTIVE
        // -----------------------------------------------------

        if (
                text.equalsIgnoreCase("Verified")
                ||
                text.equalsIgnoreCase("Active")
        ) {

            label.setStyle(
                    "-fx-background-color: rgba(139,92,246,0.16);" +
                    "-fx-background-radius: 12;" +
                    "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 7 12;"
            );


        // -----------------------------------------------------
        // PENDING
        // -----------------------------------------------------

        } else if (
                text.equalsIgnoreCase("Pending")
        ) {

            label.setStyle(
                    "-fx-background-color: rgba(167,139,250,0.12);" +
                    "-fx-background-radius: 12;" +
                    "-fx-text-fill: #C4B5FD;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 7 12;"
            );


        // -----------------------------------------------------
        // REJECTED / BLOCKED
        // -----------------------------------------------------

        } else {

            label.setStyle(
                    "-fx-background-color: rgba(239,68,68,0.15);" +
                    "-fx-background-radius: 12;" +
                    "-fx-text-fill: #F87171;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 7 12;"
            );
        }


        return label;
    }


    // =========================================================
    // SHOW ERROR ROW
    // =========================================================

    private void showErrorRow(
            String message
    ) {

        if (rows == null) {

            return;
        }


        rows.getChildren().clear();


        Label errorLabel =
                new Label(
                        message
                );


        errorLabel.setPadding(
                new Insets(30)
        );


        errorLabel.setStyle(
                "-fx-text-fill: #F87171;" +
                "-fx-font-size: 14px;"
        );


        rows.getChildren().add(
                errorLabel
        );
    }


    // =========================================================
    // SHOW INFO
    // =========================================================

    private void showInfo(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
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


        alert.show();
    }


    // =========================================================
    // STOP REAL-TIME LISTENER
    // =========================================================

    public void stopRealtimeListener() {

        if (
                centresListener != null
        ) {

            centresListener.remove();

            centresListener = null;
        }
    }


    // =========================================================
    // INTERNAL CENTRE DATA CLASS
    // =========================================================

    private static class CentreData {

        private final String uid;

        private final String name;

        private final String email;

        private final String mobile;

        private final String address;

        private final String status;


        private CentreData(
                String uid,
                String name,
                String email,
                String mobile,
                String address,
                String status
        ) {

            this.uid = uid;

            this.name = name;

            this.email = email;

            this.mobile = mobile;

            this.address = address;

            this.status = status;
        }
    }
}