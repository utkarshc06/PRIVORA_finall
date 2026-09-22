package com.myprivora.view.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.myprivora.dao.PrintRequestDAO;
import com.myprivora.dao.XeroxCentreDAO;
import com.myprivora.extras.KeyProtectionService;
import com.myprivora.model.PrintRequest;
import com.myprivora.model.XeroxCentre;
import com.myprivora.session.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SelectCentre {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String BACKGROUND = "#080C16";
    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00F0FF";
    private final String VIOLET = "#33F3FF";
    private final String DEEP_PURPLE = "#00B4D8";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";


    // =========================================================
    // DAO
    // =========================================================

    private final XeroxCentreDAO xeroxCentreDAO =
            new XeroxCentreDAO();

    private final PrintRequestDAO printRequestDAO =
            new PrintRequestDAO();


    // =========================================================
    // KEY PROTECTION SERVICE
    // =========================================================

    private final KeyProtectionService keyProtectionService =
            new KeyProtectionService();


    // =========================================================
    // WORKFLOW DATA
    // =========================================================

    private final String documentId;

    private final String documentName;

    private final int printLimit;

    private final int expiryMinutes;

    /*
     * SHA-256 hash of the user's privacy PIN.
     *
     * Raw PIN is never stored here.
     */
    private final String pinHash;


    /*
     * AES-256 key belonging to the current document.
     *
     * This key exists only in RAM.
     *
     * IMPORTANT:
     *
     * We NEVER store this raw key in Firestore.
     */
    private final byte[] aesKey;


    // =========================================================
    // NAVIGATION
    // =========================================================

    private final Runnable nextAction;


    // =========================================================
    // SELECTED CENTRE
    // =========================================================

    private String selectedCentreUid;

    private String selectedCentreName;


    // =========================================================
    // CENTRES
    // =========================================================

    private final ObservableList<XeroxCentre> centres =
            FXCollections.observableArrayList();


    // =========================================================
    // GRID
    // =========================================================

    private GridPane centreGrid;


    // =========================================================
    // SEND BUTTON
    // =========================================================

    private Button sendRequestButton;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SelectCentre(
            String documentId,
            String documentName,
            int printLimit,
            int expiryMinutes,
            String pinHash,
            byte[] aesKey,
            Runnable nextAction) {

        this.documentId = documentId;

        this.documentName = documentName;

        this.printLimit = printLimit;

        this.expiryMinutes = expiryMinutes;

        this.pinHash = pinHash;

        this.aesKey = aesKey;

        this.nextAction = nextAction;
    }


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public SelectCentre() {

        this(
                null,
                null,
                3,
                5,
                null,
                null,
                () -> {}
        );
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox main =
                new VBox(22);

        main.setPadding(
                new Insets(
                        32,
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
        // TITLE
        // =====================================================

        Label title =
                new Label(
                        "Select a xerox centre"
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
                        "Choose a verified centre near you."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 15px;"
        );


        VBox heading =
                new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // SEARCH BOX
        // =====================================================

        HBox searchBox =
                new HBox(12);

        searchBox.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBox.setPadding(
                new Insets(
                        0,
                        18,
                        0,
                        18
                )
        );

        searchBox.setMaxWidth(650);

        searchBox.setPrefHeight(58);

        searchBox.setStyle(
                "-fx-background-color: #151827;"
                        + "-fx-background-radius: 30;"
                        + "-fx-border-color: #252B3D;"
                        + "-fx-border-radius: 30;"
        );


        Label searchIcon =
                new Label("⌕");

        searchIcon.setStyle(
                "-fx-text-fill: #9D96B5;"
                        + "-fx-font-size: 27px;"
        );


        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search by name or area..."
        );

        searchField.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-prompt-text-fill: #81798F;"
                        + "-fx-font-size: 16px;"
                        + "-fx-border-width: 0;"
        );


        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );


        searchBox.getChildren().addAll(
                searchIcon,
                searchField
        );


        // =====================================================
        // CENTRE GRID
        // =====================================================

        centreGrid =
                new GridPane();

        centreGrid.setHgap(20);

        centreGrid.setVgap(20);

        centreGrid.setMaxWidth(
                Double.MAX_VALUE
        );


        ColumnConstraints column1 =
                new ColumnConstraints();

        column1.setPercentWidth(50);

        column1.setHgrow(
                Priority.ALWAYS
        );


        ColumnConstraints column2 =
                new ColumnConstraints();

        column2.setPercentWidth(50);

        column2.setHgrow(
                Priority.ALWAYS
        );


        centreGrid
                .getColumnConstraints()
                .addAll(
                        column1,
                        column2
                );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        centreGrid
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );


        // =====================================================
        // SEARCH ACTION
        // =====================================================

        searchField.textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                filterCentres(newValue)
                );


        // =====================================================
        // SEND REQUEST BUTTON
        // =====================================================

        sendRequestButton =
                new Button(
                        "Send Print Request"
                );

        sendRequestButton.setDisable(true);

        sendRequestButton.setPrefHeight(52);

        sendRequestButton.setMaxWidth(
                Double.MAX_VALUE
        );

        setDisabledButtonStyle();


        sendRequestButton.setOnAction(
                e -> sendPrintRequest()
        );


        HBox.setHgrow(
                sendRequestButton,
                Priority.ALWAYS
        );


        // =====================================================
        // REQUEST INFORMATION
        // =====================================================

        Label requestInfo =
                new Label(
                        "Print limit: "
                                + printLimit
                                + " copies   •   Expiry: "
                                + expiryMinutes
                                + " minutes"
                );

        requestInfo.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 13px;"
        );


        VBox requestArea =
                new VBox(8);

        requestArea.setAlignment(
                Pos.CENTER_RIGHT
        );

        requestArea.getChildren().addAll(
                requestInfo,
                sendRequestButton
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                heading,
                searchBox,
                scrollPane,
                requestArea
        );


        // =====================================================
        // LOAD CENTRES
        // =====================================================

        loadCentres();


        return main;
    }


    // =========================================================
    // LOAD CENTRES
    // =========================================================

    private void loadCentres() {

        centreGrid.getChildren().clear();


        Label loading =
                new Label(
                        "Loading Xerox centres..."
                );


        loading.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 15px;"
        );


        centreGrid.add(
                loading,
                0,
                0
        );


        try {

            List<XeroxCentre> loadedCentres =
                    xeroxCentreDAO
                            .getActiveXeroxCentres();


            centres.clear();

            centres.addAll(
                    loadedCentres
            );


            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "[SelectCentre] ACTIVE CENTRES LOADED = "
                            + loadedCentres.size()
            );


            for (XeroxCentre centre :
                    loadedCentres) {

                System.out.println(
                        "[SelectCentre] Centre Name = "
                                + centre.getName()
                );

                System.out.println(
                        "[SelectCentre] Centre UID = "
                                + centre.getUid()
                );

                System.out.println(
                        "[SelectCentre] Centre Email = "
                                + centre.getEmail()
                );

                System.out.println(
                        "[SelectCentre] Public Key Available = "
                                + (
                                centre.getPublicKey() != null
                                        && !centre.getPublicKey().isBlank()
                        )
                );
            }


            System.out.println(
                    "======================================"
            );


            displayCentres(
                    loadedCentres
            );


        } catch (Exception e) {

            e.printStackTrace();


            centreGrid.getChildren().clear();


            Label error =
                    new Label(
                            "Unable to load Xerox centres."
                    );


            error.setStyle(
                    "-fx-text-fill: #EF4444;"
                            + "-fx-font-size: 15px;"
            );


            centreGrid.add(
                    error,
                    0,
                    0
            );
        }
    }


    // =========================================================
    // FILTER CENTRES
    // =========================================================

    private void filterCentres(
            String searchText) {

        String search =
                searchText
                        .trim()
                        .toLowerCase();


        List<XeroxCentre> filtered =
                new ArrayList<>();


        for (XeroxCentre centre :
                centres) {

            String name =
                    centre.getName() == null
                            ? ""
                            : centre.getName()
                                    .toLowerCase();


            String email =
                    centre.getEmail() == null
                            ? ""
                            : centre.getEmail()
                                    .toLowerCase();


            String mobile =
                    centre.getMobile() == null
                            ? ""
                            : centre.getMobile()
                                    .toLowerCase();


            if (name.contains(search)
                    || email.contains(search)
                    || mobile.contains(search)) {

                filtered.add(
                        centre
                );
            }
        }


        displayCentres(
                filtered
        );
    }


    // =========================================================
    // DISPLAY CENTRES
    // =========================================================

    private void displayCentres(
            List<XeroxCentre> centreList) {

        centreGrid.getChildren().clear();


        if (centreList.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No active Xerox centres found."
                    );


            emptyLabel.setStyle(
                    "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
                            + "-fx-font-size: 15px;"
            );


            centreGrid.add(
                    emptyLabel,
                    0,
                    0
            );


            return;
        }


        int column = 0;

        int row = 0;


        for (XeroxCentre centre :
                centreList) {

            VBox card =
                    createCentreCard(
                            centre
                    );


            centreGrid.add(
                    card,
                    column,
                    row
            );


            column++;


            if (column == 2) {

                column = 0;

                row++;
            }
        }
    }


    // =========================================================
    // CENTRE CARD
    // =========================================================

    private VBox createCentreCard(
            XeroxCentre centre) {

        VBox card =
                new VBox(14);


        card.setPadding(
                new Insets(22)
        );


        card.setMinHeight(145);


        card.setMaxWidth(
                Double.MAX_VALUE
        );


        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-border-color: "
                        + CARD_BORDER
                        + ";"
                        + "-fx-border-radius: 20;"
                        + "-fx-background-radius: 20;"
        );


        // =====================================================
        // TOP ROW
        // =====================================================

        HBox topRow =
                new HBox(15);


        topRow.setAlignment(
                Pos.CENTER_LEFT
        );


        Circle iconCircle =
                new Circle(27);


        iconCircle.setFill(
                Color.web(
                        DEEP_PURPLE
                )
        );


        Label icon =
                new Label("▥");


        icon.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 19px;"
                        + "-fx-font-weight: bold;"
        );


        StackPane iconBox =
                new StackPane();


        iconBox.setPrefSize(
                54,
                54
        );


        iconBox.getChildren().addAll(
                iconCircle,
                icon
        );


        String centreName =
                centre.getName() == null
                        ? "Unnamed Centre"
                        : centre.getName();


        Label name =
                new Label(
                        centreName
                );


        name.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 17px;"
                        + "-fx-font-weight: bold;"
        );


        HBox.setHgrow(
                name,
                Priority.ALWAYS
        );


        Label verified =
                new Label("✓");


        verified.setStyle(
                "-fx-text-fill: "
                        + VIOLET
                        + ";"
                        + "-fx-font-size: 16px;"
                        + "-fx-font-weight: bold;"
        );


        topRow.getChildren().addAll(
                iconBox,
                name,
                verified
        );


        // =====================================================
        // INFORMATION ROW
        // =====================================================

        HBox infoRow =
                new HBox(16);


        infoRow.setAlignment(
                Pos.CENTER_LEFT
        );


        Label ratingLabel =
                new Label(
                        "★ New"
                );


        ratingLabel.setStyle(
                "-fx-text-fill: #F4C95D;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );


        Label mobileLabel =
                new Label(
                        "☎ "
                                + (
                                centre.getMobile()
                                        == null
                                ? "N/A"
                                : centre.getMobile()
                        )
                );


        mobileLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 13px;"
        );


        Label statusLabel =
                new Label(
                        "Active"
                );


        statusLabel.setPadding(
                new Insets(
                        5,
                        12,
                        5,
                        12
                )
        );


        statusLabel.setStyle(
                "-fx-background-color: #241C36;"
                        + "-fx-text-fill: "
                        + VIOLET
                        + ";"
                        + "-fx-background-radius: 15;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );


        // =====================================================
        // SELECT BUTTON
        // =====================================================

        Button selectButton =
                new Button(
                        "Select"
                );


        setSelectButtonStyle(
                selectButton
        );


        selectButton.setOnMouseEntered(e -> {

            if (!selectButton.getText()
                    .equals("Selected ✓")) {

                selectButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + VIOLET
                                + ", "
                                + PURPLE
                                + ");"
                                + "-fx-background-radius: 18;"
                                + "-fx-text-fill: white;"
                                + "-fx-font-size: 13px;"
                                + "-fx-font-weight: bold;"
                                + "-fx-padding: 9 17 9 17;"
                                + "-fx-cursor: hand;"
                );
            }


            selectButton.setScaleX(1.04);

            selectButton.setScaleY(1.04);
        });


        selectButton.setOnMouseExited(e -> {

            if (!selectButton.getText()
                    .equals("Selected ✓")) {

                setSelectButtonStyle(
                        selectButton
                );
            }


            selectButton.setScaleX(1);

            selectButton.setScaleY(1);
        });


        // =====================================================
        // SELECT ACTION
        // =====================================================

        selectButton.setOnAction(e -> {

            selectedCentreUid =
                    centre.getUid();


            selectedCentreName =
                    centre.getName();


            System.out.println(
                    "======================================"
            );


            System.out.println(
                    "[SelectCentre] CENTRE SELECTED"
            );


            System.out.println(
                    "[SelectCentre] Centre Name = "
                            + selectedCentreName
            );


            System.out.println(
                    "[SelectCentre] Centre UID = "
                            + selectedCentreUid
            );


            System.out.println(
                    "[SelectCentre] Current User UID = "
                            + SessionManager.getUserId()
            );


            System.out.println(
                    "[SelectCentre] Public Key Available = "
                            + (
                            centre.getPublicKey() != null
                                    && !centre.getPublicKey().isBlank()
                    )
            );


            System.out.println(
                    "======================================"
            );


            if (selectedCentreUid == null
                    || selectedCentreUid.trim().isEmpty()) {

                showError(
                        "Selected Xerox centre does not have a valid Firebase UID."
                );


                return;
            }


            selectButton.setText(
                    "Selected ✓"
            );


            selectButton.setStyle(
                    "-fx-background-color: #3B2A5A;"
                            + "-fx-background-radius: 18;"
                            + "-fx-text-fill: "
                            + VIOLET
                            + ";"
                            + "-fx-font-size: 13px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-padding: 9 17 9 17;"
                            + "-fx-cursor: hand;"
            );


            updateSendButtonState();
        });


        // =====================================================
        // BOTTOM ROW
        // =====================================================

        HBox bottomRow =
                new HBox(14);


        bottomRow.setAlignment(
                Pos.CENTER_LEFT
        );


        HBox.setHgrow(
                infoRow,
                Priority.ALWAYS
        );


        infoRow.getChildren().addAll(
                ratingLabel,
                mobileLabel,
                statusLabel
        );


        bottomRow.getChildren().addAll(
                infoRow,
                selectButton
        );


        card.getChildren().addAll(
                topRow,
                bottomRow
        );


        // =====================================================
        // CARD HOVER
        // =====================================================

        card.setOnMouseEntered(e -> {

            card.setStyle(
                    "-fx-background-color: #1B1828;"
                            + "-fx-border-color: "
                            + PURPLE
                            + ";"
                            + "-fx-border-radius: 20;"
                            + "-fx-background-radius: 20;"
                            + "-fx-effect: dropshadow("
                            + "gaussian, "
                            + "rgba(139,92,246,0.22), "
                            + "18, 0.3, 0, 3);"
            );


            card.setScaleX(1.01);

            card.setScaleY(1.01);
        });


        card.setOnMouseExited(e -> {

            card.setStyle(
                    "-fx-background-color: "
                            + CARD
                            + ";"
                            + "-fx-border-color: "
                            + CARD_BORDER
                            + ";"
                            + "-fx-border-radius: 20;"
                            + "-fx-background-radius: 20;"
            );


            card.setScaleX(1);

            card.setScaleY(1);
        });


        return card;
    }


    // =========================================================
    // SEND BUTTON STATE
    // =========================================================

    private void updateSendButtonState() {

        if (sendRequestButton == null) {
            return;
        }


        boolean centreSelected =
                selectedCentreUid != null
                        && !selectedCentreUid.isBlank();


        boolean validPinHash =
                pinHash != null
                        && !pinHash.isBlank();


        boolean validAesKey =
                aesKey != null
                        && aesKey.length == 32;


        boolean enabled =
                centreSelected
                        && validPinHash
                        && validAesKey;


        sendRequestButton.setDisable(
                !enabled
        );


        if (enabled) {

            setEnabledButtonStyle();

        } else {

            setDisabledButtonStyle();
        }
    }


    // =========================================================
    // ENABLED BUTTON STYLE
    // =========================================================

    private void setEnabledButtonStyle() {

        sendRequestButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE
                        + ", "
                        + DEEP_PURPLE
                        + ");"
                        + "-fx-background-radius: 16;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 12 24 12 24;"
                        + "-fx-cursor: hand;"
        );


        sendRequestButton.setOnMouseEntered(e -> {

            sendRequestButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, "
                            + VIOLET
                            + ", "
                            + PURPLE
                            + ");"
                            + "-fx-background-radius: 16;"
                            + "-fx-text-fill: white;"
                            + "-fx-font-size: 15px;"
                            + "-fx-font-weight: bold;"
                            + "-fx-padding: 12 24 12 24;"
                            + "-fx-cursor: hand;"
            );


            sendRequestButton.setScaleX(1.02);

            sendRequestButton.setScaleY(1.02);
        });


        sendRequestButton.setOnMouseExited(e -> {

            setEnabledButtonStyleWithoutHandlers();

            sendRequestButton.setScaleX(1);

            sendRequestButton.setScaleY(1);
        });
    }


    // =========================================================
    // ENABLED BUTTON STYLE WITHOUT HANDLERS
    // =========================================================

    private void setEnabledButtonStyleWithoutHandlers() {

        sendRequestButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE
                        + ", "
                        + DEEP_PURPLE
                        + ");"
                        + "-fx-background-radius: 16;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 12 24 12 24;"
                        + "-fx-cursor: hand;"
        );
    }


    // =========================================================
    // DISABLED BUTTON STYLE
    // =========================================================

    private void setDisabledButtonStyle() {

        sendRequestButton.setStyle(
                "-fx-background-color: #302A3D;"
                        + "-fx-background-radius: 16;"
                        + "-fx-text-fill: #777084;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 12 24 12 24;"
        );


        sendRequestButton.setScaleX(1);

        sendRequestButton.setScaleY(1);
    }


    // =========================================================
    // SELECT BUTTON STYLE
    // =========================================================

    private void setSelectButtonStyle(
            Button button) {

        button.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE
                        + ", "
                        + DEEP_PURPLE
                        + ");"
                        + "-fx-background-radius: 18;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 9 17 9 17;"
                        + "-fx-cursor: hand;"
        );
    }


    // =========================================================
    // SEND PRINT REQUEST
    // =========================================================

    private void sendPrintRequest() {

        // =====================================================
        // USER SESSION
        // =====================================================

        String userId =
                SessionManager.getUserId();


        System.out.println(
                "======================================"
        );


        System.out.println(
                "[SelectCentre] SENDING PRINT REQUEST"
        );


        System.out.println(
                "[SelectCentre] Logged-in User UID = "
                        + userId
        );


        System.out.println(
                "[SelectCentre] Selected Centre UID = "
                        + selectedCentreUid
        );


        System.out.println(
                "[SelectCentre] Selected Centre Name = "
                        + selectedCentreName
        );


        if (userId == null
                || userId.trim().isEmpty()) {

            showError(
                    "User session not found. Please login again."
            );


            return;
        }


        // =====================================================
        // DOCUMENT VALIDATION
        // =====================================================

        if (documentId == null
                || documentId.trim().isEmpty()) {

            showError(
                    "Document information is missing."
            );


            return;
        }


        if (documentName == null
                || documentName.trim().isEmpty()) {

            showError(
                    "Document name is missing."
            );


            return;
        }


        // =====================================================
        // PRINT LIMIT
        // =====================================================

        if (printLimit < 1) {

            showError(
                    "Print limit must be at least 1."
            );


            return;
        }


        // =====================================================
        // EXPIRY
        // =====================================================

        if (expiryMinutes < 1
                || expiryMinutes > 10) {

            showError(
                    "Expiry time must be between 1 and 10 minutes."
            );


            return;
        }


        // =====================================================
        // CENTRE VALIDATION
        // =====================================================

        if (selectedCentreUid == null
                || selectedCentreUid.trim().isEmpty()) {

            showError(
                    "Please select a Xerox centre."
            );


            return;
        }


        if (selectedCentreName == null
                || selectedCentreName.trim().isEmpty()) {

            showError(
                    "Selected Xerox centre name is missing."
            );


            return;
        }


        // =====================================================
        // PIN HASH VALIDATION
        // =====================================================

        if (pinHash == null
                || pinHash.trim().isEmpty()) {

            showError(
                    "Privacy PIN is missing. Please set the privacy PIN before continuing."
            );


            return;
        }


        // =====================================================
        // AES KEY VALIDATION
        // =====================================================

        if (aesKey == null
                || aesKey.length != 32) {

            showError(
                    "Document encryption key is unavailable. Please upload the document again."
            );


            return;
        }


        try {

            // =================================================
            // GET SELECTED XEROX CENTRE
            // =================================================

            System.out.println(
                    "[SelectCentre] Loading selected Xerox centre..."
            );


            XeroxCentre selectedCentre =
                    xeroxCentreDAO
                            .getXeroxCentreById(
                                    selectedCentreUid
                            );


            if (selectedCentre == null) {

                showError(
                        "Selected Xerox centre could not be found."
                );


                return;
            }


            // =================================================
            // GET XEROX PUBLIC KEY
            // =================================================

            String xeroxPublicKeyBase64 =
                    selectedCentre.getPublicKey();


            if (xeroxPublicKeyBase64 == null
                    || xeroxPublicKeyBase64.isBlank()) {

                showError(
                        "Selected Xerox centre does not have a registered public key."
                );


                return;
            }


            System.out.println(
                    "[SelectCentre] Xerox public key found."
            );


            // =================================================
            // CONVERT PUBLIC KEY
            // =================================================

            java.security.PublicKey xeroxPublicKey =
                    keyProtectionService
                            .publicKeyFromBase64(
                                    xeroxPublicKeyBase64
                            );


            System.out.println(
                    "[SelectCentre] Xerox public key loaded successfully."
            );


            // =================================================
            // COPY AES KEY
            // =================================================

            /*
             * We make a temporary copy.
             *
             * This allows us to clear the temporary AES key
             * after RSA encryption without destroying the
             * original AES key still held by UserDashboard.
             */

            byte[] temporaryAesKey =
                    Arrays.copyOf(
                            aesKey,
                            aesKey.length
                    );


            byte[] encryptedAesKeyBytes = null;


            try {

                // =================================================
                // RSA ENCRYPT AES KEY
                // =================================================

                System.out.println(
                        "[SelectCentre] Encrypting AES-256 key with Xerox public key..."
                );


                encryptedAesKeyBytes =
                        keyProtectionService
                                .encryptAesKey(
                                        temporaryAesKey,
                                        xeroxPublicKey
                                );


                System.out.println(
                        "[SelectCentre] AES key encrypted successfully."
                );


                // =================================================
                // CONVERT ENCRYPTED KEY TO BASE64
                // =================================================

                String encryptedAesKey =
                        keyProtectionService
                                .bytesToBase64(
                                        encryptedAesKeyBytes
                                );


                System.out.println(
                        "[SelectCentre] encryptedAesKey generated."
                );


                System.out.println(
                        "[SelectCentre] encryptedAesKey length = "
                                + encryptedAesKey.length()
                );


                // =================================================
                // TIMESTAMPS
                // =================================================

                Instant now =
                        Instant.now();


                Instant expiry =
                        now.plusSeconds(
                                expiryMinutes * 60L
                        );


                // =================================================
                // CREATE REQUEST
                // =================================================

                PrintRequest request =
                        new PrintRequest();


                request.setRequestId(null);


                request.setDocumentId(
                        documentId
                );


                request.setDocumentName(
                        documentName
                );


                request.setUserId(
                        userId
                );


                request.setXeroxId(
                        selectedCentreUid
                );


                request.setXeroxName(
                        selectedCentreName
                );


                request.setPrintCopies(
                        printLimit
                );


                request.setPrintedCount(
                        0
                );


                request.setExpiryMinutes(
                        expiryMinutes
                );


                request.setRequestedAt(
                        now.toString()
                );


                request.setExpiresAt(
                        expiry.toString()
                );


                request.setStatus(
                        "PENDING"
                );


                // =================================================
                // STORE PIN HASH
                // =================================================

                request.setPinHash(
                        pinHash
                );


                // =================================================
                // STORE ENCRYPTED AES KEY
                // =================================================

                request.setEncryptedAesKey(
                        encryptedAesKey
                );


                // =================================================
                // FINAL DEBUG
                // =================================================

                System.out.println(
                        "--------------------------------------"
                );


                System.out.println(
                        "[SelectCentre] REQUEST DATA"
                );


                System.out.println(
                        "requestId = "
                                + request.getRequestId()
                );


                System.out.println(
                        "documentId = "
                                + request.getDocumentId()
                );


                System.out.println(
                        "documentName = "
                                + request.getDocumentName()
                );


                System.out.println(
                        "userId = "
                                + request.getUserId()
                );


                System.out.println(
                        "xeroxId = "
                                + request.getXeroxId()
                );


                System.out.println(
                        "xeroxName = "
                                + request.getXeroxName()
                );


                System.out.println(
                        "printCopies = "
                                + request.getPrintCopies()
                );


                System.out.println(
                        "printedCount = "
                                + request.getPrintedCount()
                );


                System.out.println(
                        "expiryMinutes = "
                                + request.getExpiryMinutes()
                );


                System.out.println(
                        "requestedAt = "
                                + request.getRequestedAt()
                );


                System.out.println(
                        "expiresAt = "
                                + request.getExpiresAt()
                );


                System.out.println(
                        "status = "
                                + request.getStatus()
                );


                System.out.println(
                        "pinHash exists = "
                                + (
                                request.getPinHash() != null
                                        && !request.getPinHash().isBlank()
                        )
                );


                System.out.println(
                        "encryptedAesKey exists = "
                                + (
                                request.getEncryptedAesKey() != null
                                        && !request.getEncryptedAesKey().isBlank()
                        )
                );


                /*
                 * NEVER print the actual PIN.
                 *
                 * NEVER print the raw AES key.
                 *
                 * NEVER print the private key.
                 */


                System.out.println(
                        "--------------------------------------"
                );


                // =================================================
                // SAVE REQUEST
                // =================================================

                boolean saved =
                        printRequestDAO
                                .createPrintRequest(
                                        request
                                );


                System.out.println(
                        "[SelectCentre] Firestore save result = "
                                + saved
                );


                if (!saved) {

                    showError(
                            "Print request could not be created."
                    );


                    return;
                }


                // =================================================
                // SUCCESS
                // =================================================

                System.out.println(
                        "======================================"
                );


                System.out.println(
                        "[SelectCentre] PRINT REQUEST CREATED"
                );


                System.out.println(
                        "Request ID = "
                                + request.getRequestId()
                );


                System.out.println(
                        "User UID = "
                                + request.getUserId()
                );


                System.out.println(
                        "Xerox UID = "
                                + request.getXeroxId()
                );


                System.out.println(
                        "Xerox Name = "
                                + request.getXeroxName()
                );


                System.out.println(
                        "Status = "
                                + request.getStatus()
                );


                System.out.println(
                        "PIN hash stored = YES"
                );


                System.out.println(
                        "Encrypted AES key stored = YES"
                );


                System.out.println(
                        "======================================"
                );


                showSuccess();


            } finally {

                // =================================================
                // CLEAR TEMPORARY AES KEY
                // =================================================

                Arrays.fill(
                        temporaryAesKey,
                        (byte) 0
                );


                // =================================================
                // CLEAR TEMPORARY ENCRYPTED KEY
                // =================================================

                if (encryptedAesKeyBytes != null) {

                    Arrays.fill(
                            encryptedAesKeyBytes,
                            (byte) 0
                    );
                }


                System.out.println(
                        "[SelectCentre] Temporary key material cleared from RAM."
                );
            }


        } catch (Exception e) {

            System.err.println(
                    "[SelectCentre] ERROR SENDING REQUEST"
            );


            e.printStackTrace();


            showError(
                    "An error occurred while sending the print request."
            );
        }
    }


    // =========================================================
    // SUCCESS
    // =========================================================

    private void showSuccess() {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                "PRIVORA"
        );


        alert.setHeaderText(
                "Print Request Sent"
        );


        alert.setContentText(
                "Your document has been sent to "
                        + selectedCentreName
                        + ".\n\n"
                        + "Print Limit: "
                        + printLimit
                        + " copies\n"
                        + "Expiry: "
                        + expiryMinutes
                        + " minutes\n"
                        + "Status: PENDING\n\n"
                        + "The Xerox centre must verify your privacy PIN before printing."
        );


        alert.showAndWait();


        if (nextAction != null) {

            nextAction.run();
        }
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
                "Print Request Error"
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public String getSelectedCentreUid() {

        return selectedCentreUid;
    }


    public String getSelectedCentreName() {

        return selectedCentreName;
    }
}