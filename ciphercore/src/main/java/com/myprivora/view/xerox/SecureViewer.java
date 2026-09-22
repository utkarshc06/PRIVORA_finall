package com.myprivora.view.xerox;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SecureViewer {

    // =========================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================
    private static final String BG = ClayTheme.OBSIDIAN_DEEP;
    private static final String SIDEBG = ClayTheme.OBSIDIAN_GLASS;
    private static final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private static final String CARD2 = ClayTheme.OBSIDIAN_SURFACE;
    private static final String BORDER = ClayTheme.CARD_BORDER_COLOR;
    private static final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private static final String PURPLE2 = ClayTheme.CYAN_LIGHT;
    private static final String TEXT = ClayTheme.FROST_WHITE;
    private static final String MUTED = ClayTheme.FROST_MUTED;
    private static final String RED = ClayTheme.CYAN_PRIMARY;
    private static final String BLUE = ClayTheme.CYAN_ACCENT;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public Node getContent() {

        VBox main = new VBox(22);
        main.setPadding(new Insets(32, 34, 40, 34));
        main.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        // =========================
        // HEADER
        // =========================

        Label title = new Label("Secure viewer");
        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Read-only, watermarked view — restricted actions enforced."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 16px;"
        );

        VBox header = new VBox(6, title, subtitle);

        // =========================
        // RESTRICTIONS BAR
        // =========================

        HBox restrictions = new HBox(10);
        restrictions.setAlignment(Pos.CENTER_LEFT);
        restrictions.setPadding(new Insets(14, 18, 14, 18));

        restrictions.setStyle(
                "-fx-background-color: " + SIDEBG + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        Label active = new Label("Restrictions active");
        active.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        Label download = restrictionLabel("Download disabled");
        Label export = restrictionLabel("Export disabled");
        Label copy = restrictionLabel("Copy disabled");
        Label forward = restrictionLabel("Forward disabled");

        restrictions.getChildren().addAll(
                active,
                download,
                export,
                copy,
                forward
        );

        // =========================
        // DOCUMENT VIEWER CARD
        // =========================

        VBox viewerCard = new VBox();
        viewerCard.setPadding(new Insets(28));
        viewerCard.setMinHeight(520);

        viewerCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 24;" +
                "-fx-background-radius: 24;"
        );

        // Inner document area

        StackPane documentArea = new StackPane();
        documentArea.setMinHeight(460);

        documentArea.setStyle(
                "-fx-background-color: " + ClayTheme.OBSIDIAN_SURFACE + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        // =========================
        // FAKE DOCUMENT
        // =========================

        VBox document = new VBox(12);
        document.setPadding(new Insets(30));
        document.setPrefWidth(530);
        document.setMaxWidth(530);
        document.setPrefHeight(230);

        document.setStyle(
                "-fx-background-color: " + ClayTheme.OBSIDIAN_CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 240, 255, 0.12), 20, 0.2, 0, 5);"
        );

        Label govt = new Label("GOVERNMENT OF INDIA");
        govt.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        Label aadhaar = new Label("Aadhaar Card");
        aadhaar.setStyle(
                "-fx-text-fill: " + ClayTheme.FROST_WHITE + ";" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        HBox documentInfo = new HBox(18);
        documentInfo.setAlignment(Pos.CENTER_LEFT);

        StackPane photo = new StackPane();
        photo.setPrefSize(95, 95);

        photo.setStyle(
                "-fx-background-color: rgba(0, 240, 255, 0.1);" +
                "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;"
        );

        Circle person = new Circle(25);
        person.setFill(Color.web(ClayTheme.CYAN_PRIMARY));

        photo.getChildren().add(person);

        VBox fakeDetails = new VBox(10);

        Region line1 = createLine(180);
        Region line2 = createLine(230);
        Region line3 = createLine(150);

        fakeDetails.getChildren().addAll(
                line1,
                line2,
                line3
        );

        documentInfo.getChildren().addAll(
                photo,
                fakeDetails
        );

        document.getChildren().addAll(
                govt,
                aadhaar,
                documentInfo
        );

        // =========================
        // WATERMARK
        // =========================

        Label watermark1 = watermark("VANISHFILE · VF-8823-KL · SHARMA XEROX");
        watermark1.setRotate(-18);

        Label watermark2 = watermark("VANISHFILE · VF-8823-KL · SHARMA XEROX");
        watermark2.setRotate(-18);

        Label watermark3 = watermark("VANISHFILE · VF-8823-KL · SHARMA XEROX");
        watermark3.setRotate(-18);

        StackPane.setAlignment(watermark1, Pos.TOP_LEFT);
        StackPane.setMargin(watermark1, new Insets(35, 0, 0, 80));

        StackPane.setAlignment(watermark2, Pos.CENTER_RIGHT);
        StackPane.setMargin(watermark2, new Insets(0, 80, 0, 0));

        StackPane.setAlignment(watermark3, Pos.BOTTOM_LEFT);
        StackPane.setMargin(watermark3, new Insets(0, 0, 40, 150));

        documentArea.getChildren().addAll(
                watermark1,
                watermark2,
                watermark3,
                document
        );

        viewerCard.getChildren().add(documentArea);

        // =========================
        // SESSION CONTROLS
        // =========================

        VBox controlsCard = new VBox();

        controlsCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 24;" +
                "-fx-background-radius: 24;"
        );

        Label controlsTitle = new Label("Session controls");

        controlsTitle.setPadding(
                new Insets(20, 28, 20, 28)
        );

        controlsTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1 0;"
        );

        VBox buttons = new VBox(12);
        buttons.setPadding(new Insets(22, 28, 26, 28));

        Button print = createPrimaryButton("Print Document");

        Button approval = createSecondaryButton(
                "Request Approval"
        );

        Button close = createDangerButton(
                "Close Session"
        );

        Label timer = new Label(
                "Time remaining · 14:22"
        );

        timer.setPadding(
                new Insets(13, 15, 13, 15)
        );

        timer.setMaxWidth(Double.MAX_VALUE);

        timer.setStyle(
                "-fx-background-color: rgba(0, 240, 255, 0.08);" +
                "-fx-border-color: rgba(0, 240, 255, 0.25);" +
                "-fx-border-radius: 18;" +
                "-fx-background-radius: 18;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        buttons.getChildren().addAll(
                print,
                approval,
                close,
                timer
        );

        controlsCard.getChildren().addAll(
                controlsTitle,
                buttons
        );

        main.getChildren().addAll(
                header,
                restrictions,
                viewerCard,
                controlsCard
        );

        VBox.setVgrow(viewerCard, Priority.ALWAYS);

        return main;
    }

    // =========================================================
    // RESTRICTION LABEL
    // =========================================================

    private Label restrictionLabel(String text) {

        Label label = new Label(text);

        label.setPadding(
                new Insets(8, 13, 8, 13)
        );

        label.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // DOCUMENT LINE
    // =========================================================

    private Region createLine(double width) {

        Region line = new Region();

        line.setPrefWidth(width);
        line.setPrefHeight(9);

        line.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                "-fx-background-radius: 6;"
        );

        return line;
    }

    // =========================================================
    // WATERMARK
    // =========================================================

    private Label watermark(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: rgba(0, 240, 255, 0.18);" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private Button createPrimaryButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);

        button.setStyle(
                "-fx-background-color: linear-gradient(to right, #00F0FF, #00B4D8);" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        ClayTheme.applyButtonHover(button, true);

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    private Button createSecondaryButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(43);

        button.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.05);" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        ClayTheme.applyButtonHover(button, false);

        return button;
    }

    // =========================================================
    // DANGER BUTTON
    // =========================================================

    private Button createDangerButton(String text) {

        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(43);

        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        ClayTheme.applyButtonHover(button, false);

        return button;
    }
}