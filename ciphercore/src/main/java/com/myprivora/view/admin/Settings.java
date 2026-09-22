package com.myprivora.view.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import com.myprivora.view.theme.ClayTheme;

public class Settings {

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
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox root =
                new VBox();

        root.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );

        // =====================================================
        // SCROLL CONTENT
        // =====================================================

        VBox scrollContent =
                new VBox(22);

        scrollContent.setPadding(
                new Insets(
                        30,
                        38,
                        40,
                        38
                )
        );

        scrollContent.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );

        // =====================================================
        // HEADING
        // =====================================================

        Label title =
                new Label(
                        "Platform settings"
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
                        "Configure PRIVORA document, printing and access defaults."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 15px;"
        );

        VBox heading =
                new VBox(
                        5,
                        title,
                        subtitle
                );

        // =====================================================
        // DOCUMENT SETTINGS
        // =====================================================

        VBox documentCard =
                createCard();

        VBox documentHeader =
                createCardHeader(
                        "Document settings",
                        "Default behaviour for uploaded documents."
                );

        VBox documentContent =
                new VBox(20);

        documentContent.setPadding(
                new Insets(
                        25,
                        28,
                        28,
                        28
                )
        );

        // -----------------------------------------------------
        // ROW 1
        // -----------------------------------------------------

        VBox expiryBox =
                createInputBox(
                        "Default expiry time",
                        "15",
                        "Minutes"
                );

        VBox uploadSizeBox =
                createInputBox(
                        "Maximum upload size",
                        "20",
                        "MB"
                );

        HBox documentRow1 =
                new HBox(
                        22,
                        expiryBox,
                        uploadSizeBox
                );

        HBox.setHgrow(
                expiryBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                uploadSizeBox,
                Priority.ALWAYS
        );

        // -----------------------------------------------------
        // ROW 2
        // -----------------------------------------------------

        VBox printLimitBox =
                createInputBox(
                        "Default print limit",
                        "3",
                        "Copies"
                );

        VBox maxPrintLimitBox =
                createInputBox(
                        "Maximum print limit",
                        "10",
                        "Copies"
                );

        HBox documentRow2 =
                new HBox(
                        22,
                        printLimitBox,
                        maxPrintLimitBox
                );

        HBox.setHgrow(
                printLimitBox,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                maxPrintLimitBox,
                Priority.ALWAYS
        );

        documentContent.getChildren().addAll(
                documentRow1,
                documentRow2
        );

        documentCard.getChildren().addAll(
                documentHeader,
                documentContent
        );

        // =====================================================
        // PRIVACY & SECURITY
        // =====================================================

        VBox privacyCard =
                createCard();

        VBox privacyHeader =
                createCardHeader(
                        "Privacy & security",
                        "Controls related to document protection and secure printing."
                );

        VBox privacyContent =
                new VBox();

        privacyContent.setPadding(
                new Insets(
                        8,
                        28,
                        8,
                        28
                )
        );

        // -----------------------------------------------------
        // OPTION 1
        // -----------------------------------------------------

        HBox pinOption =
                createSecurityOption(
                        "Require PIN before printing",
                        "A valid document PIN must be verified before a Xerox centre can print.",
                        true
                );

        // -----------------------------------------------------
        // OPTION 2
        // -----------------------------------------------------

        HBox expiryOption =
                createSecurityOption(
                        "Automatically expire print requests",
                        "Expired requests are marked EXPIRED and are no longer printable.",
                        true
                );

        // -----------------------------------------------------
        // OPTION 3
        // -----------------------------------------------------

        HBox encryptedOption =
                createSecurityOption(
                        "Use encrypted document storage",
                        "Documents uploaded to cloud storage remain encrypted before upload.",
                        true
                );

        // -----------------------------------------------------
        // OPTION 4
        // -----------------------------------------------------

        HBox memoryOption =
                createSecurityOption(
                        "Clear sensitive data from memory",
                        "Temporary document and encryption buffers are cleared after processing.",
                        true
                );

        privacyContent.getChildren().addAll(
                pinOption,
                expiryOption,
                encryptedOption,
                memoryOption
        );

        privacyCard.getChildren().addAll(
                privacyHeader,
                privacyContent
        );

        // =====================================================
        // PRINTING SETTINGS
        // =====================================================

        VBox printingCard =
                createCard();

        VBox printingHeader =
                createCardHeader(
                        "Printing settings",
                        "Configure how PRIVORA handles Xerox print requests."
                );

        VBox printingContent =
                new VBox();

        printingContent.setPadding(
                new Insets(
                        8,
                        28,
                        8,
                        28
                )
        );

        HBox printApprovalOption =
                createSecurityOption(
                        "Require Xerox centre approval",
                        "A centre must accept the request before printing can proceed.",
                        true
                );

        HBox printLimitOption =
                createSecurityOption(
                        "Enforce print limits",
                        "The system prevents printing beyond the allowed copy limit.",
                        true
                );

        HBox expiryCheckOption =
                createSecurityOption(
                        "Check request expiry before printing",
                        "Printing is blocked when the request has expired.",
                        true
                );

        HBox centreStatusOption =
                createSecurityOption(
                        "Allow printing only from active centres",
                        "Blocked or rejected Xerox centres cannot process requests.",
                        true
                );

        printingContent.getChildren().addAll(
                printApprovalOption,
                printLimitOption,
                expiryCheckOption,
                centreStatusOption
        );

        printingCard.getChildren().addAll(
                printingHeader,
                printingContent
        );

        // =====================================================
        // XEROX CENTRE SETTINGS
        // =====================================================

        VBox centreCard =
                createCard();

        VBox centreHeader =
                createCardHeader(
                        "Xerox centre controls",
                        "Manage rules applied to registered printing centres."
                );

        VBox centreContent =
                new VBox();

        centreContent.setPadding(
                new Insets(
                        8,
                        28,
                        8,
                        28
                )
        );

        HBox activeCentreOption =
                createSecurityOption(
                        "Allow only verified centres",
                        "Only centres with an approved ACTIVE status can receive requests.",
                        true
                );

        HBox centreAvailabilityOption =
                createSecurityOption(
                        "Respect centre availability",
                        "Unavailable centres should not be offered for new print requests.",
                        true
                );

        HBox publicKeyOption =
                createSecurityOption(
                        "Require centre encryption key",
                        "A Xerox centre must have a registered public key for secure key exchange.",
                        true
                );

        centreContent.getChildren().addAll(
                activeCentreOption,
                centreAvailabilityOption,
                publicKeyOption
        );

        centreCard.getChildren().addAll(
                centreHeader,
                centreContent
        );

        // =====================================================
        // ADMIN SETTINGS
        // =====================================================

        VBox adminCard =
                createCard();

        VBox adminHeader =
                createCardHeader(
                        "Admin controls",
                        "Administrative behaviour for the PRIVORA platform."
                );

        VBox adminContent =
                new VBox();

        adminContent.setPadding(
                new Insets(
                        8,
                        28,
                        8,
                        28
                )
        );

        HBox realtimeOption =
                createSecurityOption(
                        "Enable real-time dashboard updates",
                        "Admin screens automatically refresh when Firestore data changes.",
                        true
                );

        HBox activityOption =
                createSecurityOption(
                        "Track print request activity",
                        "Maintain request status information for monitoring and reporting.",
                        true
                );

        HBox expirationOption =
                createSecurityOption(
                        "Run document expiration checks",
                        "PRIVORA periodically checks active requests for expiry.",
                        true
                );

        adminContent.getChildren().addAll(
                realtimeOption,
                activityOption,
                expirationOption
        );

        adminCard.getChildren().addAll(
                adminHeader,
                adminContent
        );

        // =====================================================
        // SYSTEM INFORMATION
        // =====================================================

        VBox systemCard =
                createCard();

        VBox systemHeader =
                createCardHeader(
                        "System information",
                        "Current PRIVORA platform configuration."
                );

        VBox systemContent =
                new VBox(
                        15
                );

        systemContent.setPadding(
                new Insets(
                        22,
                        28,
                        25,
                        28
                )
        );

        systemContent.getChildren().addAll(
                createInfoRow(
                        "Application",
                        "PRIVORA"
                ),
                createInfoRow(
                        "Team",
                        "Ciphercore"
                ),
                createInfoRow(
                        "Platform",
                        "JavaFX Desktop Application"
                ),
                createInfoRow(
                        "Database",
                        "Firebase Firestore"
                ),
                createInfoRow(
                        "Document protection",
                        "AES-GCM + RSA-OAEP"
                ),
                createInfoRow(
                        "Cloud storage",
                        "Cloudinary"
                )
        );

        systemCard.getChildren().addAll(
                systemHeader,
                systemContent
        );

        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        scrollContent.getChildren().addAll(
                heading,
                documentCard,
                privacyCard,
                printingCard,
                centreCard,
                adminCard,
                systemCard
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        scrollContent
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
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
                        + "-fx-background: "
                        + BACKGROUND
                        + ";"
                        + "-fx-border-color: transparent;"
        );

        VBox.setVgrow(
                scrollPane,
                Priority.ALWAYS
        );

        root.getChildren().add(
                scrollPane
        );

        return root;
    }

    // =========================================================
    // CREATE CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox();

        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";"
                        + "-fx-background-radius: 22;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 22;"
        );

        return card;
    }

    // =========================================================
    // CARD HEADER
    // =========================================================

    private VBox createCardHeader(
            String title,
            String description
    ) {

        VBox header =
                new VBox(5);

        header.setPadding(
                new Insets(
                        20,
                        28,
                        20,
                        28
                )
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 18px;"
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
                        + "-fx-font-size: 12px;"
        );

        header.getChildren().addAll(
                titleLabel,
                descriptionLabel
        );

        header.setStyle(
                "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-width: 0 0 1 0;"
        );

        return header;
    }

    // =========================================================
    // INPUT BOX
    // =========================================================

    private VBox createInputBox(
            String title,
            String value,
            String suffix
    ) {

        VBox box =
                new VBox(8);

        Label label =
                new Label(
                        title
                );

        label.setStyle(
                "-fx-text-fill: #E5E1EC;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );

        HBox inputContainer =
                new HBox();

        inputContainer.setAlignment(
                Pos.CENTER_LEFT
        );

        inputContainer.setStyle(
                "-fx-background-color: #0F1420;"
                        + "-fx-background-radius: 16;"
                        + "-fx-border-color: #302A43;"
                        + "-fx-border-radius: 16;"
        );

        TextField field =
                new TextField(
                        value
                );

        field.setPrefHeight(
                50
        );

        field.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-prompt-text-fill: #6F687A;"
                        + "-fx-font-size: 14px;"
                        + "-fx-padding: 0 14;"
        );

        HBox.setHgrow(
                field,
                Priority.ALWAYS
        );

        Label suffixLabel =
                new Label(
                        suffix
                );

        suffixLabel.setPadding(
                new Insets(
                        0,
                        15,
                        0,
                        5
                )
        );

        suffixLabel.setStyle(
                "-fx-text-fill: "
                        + LIGHT_PURPLE
                        + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        inputContainer.getChildren().addAll(
                field,
                suffixLabel
        );

        box.getChildren().addAll(
                label,
                inputContainer
        );

        HBox.setHgrow(
                box,
                Priority.ALWAYS
        );

        return box;
    }

    // =========================================================
    // SECURITY OPTION
    // =========================================================

    private HBox createSecurityOption(
            String title,
            String description,
            boolean selected
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        15,
                        0,
                        15,
                        0
                )
        );

        // =====================================================
        // TEXT
        // =====================================================

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-text-fill: #E5E1EC;"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );

        Label descriptionLabel =
                new Label(
                        description
                );

        descriptionLabel.setWrapText(
                true
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 11px;"
        );

        VBox textBox =
                new VBox(
                        4,
                        titleLabel,
                        descriptionLabel
                );

        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );

        // =====================================================
        // CHECKBOX
        // =====================================================

        CheckBox toggle =
                new CheckBox();

        toggle.setSelected(
                selected
        );

        toggle.setStyle(
                "-fx-cursor: hand;"
        );

        // =====================================================
        // ROW
        // =====================================================

        row.getChildren().addAll(
                textBox,
                toggle
        );

        // =====================================================
        // HOVER
        // =====================================================

        row.setOnMouseEntered(
                e ->
                        row.setStyle(
                                "-fx-background-color: "
                                        + CARD_HOVER
                                        + ";"
                                        + "-fx-background-radius: 12;"
                        )
        );

        row.setOnMouseExited(
                e ->
                        row.setStyle(
                                "-fx-background-color: transparent;"
                        )
        );

        return row;
    }

    // =========================================================
    // SYSTEM INFO ROW
    // =========================================================

    private HBox createInfoRow(
            String title,
            String value
    ) {

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        Label titleLabel =
                new Label(
                        title
                );

        titleLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        Label valueLabel =
                new Label(
                        value
                );

        valueLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        HBox.setHgrow(
                titleLabel,
                Priority.ALWAYS
        );

        row.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return row;
    }
}