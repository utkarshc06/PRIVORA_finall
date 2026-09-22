package com.myprivora.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Notifications {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String BACKGROUND = "#080C16";
    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String CARD_HOVER = "rgba(18, 27, 48, 0.85)";
    private final String BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00F0FF";
    private final String PURPLE_DARK = "#00B4D8";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY = "rgba(255, 255, 255, 0.70)";

    private final String GREEN = "#00F0FF";
    private final String ORANGE = "#00B4D8";
    private final String RED = "rgba(255, 255, 255, 0.85)";


    // =========================================================
    // CONTENT
    // =========================================================

    public VBox getContent() {

        VBox main = new VBox(25);

        main.setPadding(
                new Insets(35, 40, 40, 40)
        );

        main.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title = new Label(
                "Notifications"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle = new Label(
                "Latest updates on your documents & sessions."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 16px;"
        );


        VBox heading = new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // NOTIFICATION GRID
        // =====================================================

        GridPane notificationGrid = new GridPane();

        notificationGrid.setHgap(20);
        notificationGrid.setVgap(20);

        notificationGrid.setPadding(
                new Insets(5, 0, 10, 0)
        );


        // =====================================================
        // NOTIFICATION 1
        // =====================================================

        VBox printStarted = createNotificationCard(
                "▣",
                "Print started",
                "Sharma Xerox Point started printing Aadhaar-Card.pdf.",
                "2 min ago",
                PURPLE
        );


        // =====================================================
        // NOTIFICATION 2
        // =====================================================

        VBox printCompleted = createNotificationCard(
                "✓",
                "Print completed",
                "3 of 3 prints delivered. Session closed.",
                "30 min ago",
                GREEN
        );


        // =====================================================
        // NOTIFICATION 3
        // =====================================================

        VBox sessionExpired = createNotificationCard(
                "◷",
                "Session expired",
                "PAN-Form.pdf session expired without use.",
                "Yesterday",
                ORANGE
        );


        // =====================================================
        // NOTIFICATION 4
        // =====================================================

        VBox requestRejected = createNotificationCard(
                "×",
                "Request rejected",
                "QuickPrint Hub rejected your request.",
                "2 days ago",
                RED
        );


        // =====================================================
        // EXTRA NOTIFICATIONS
        // =====================================================

        VBox documentUploaded = createNotificationCard(
                "⇧",
                "Document uploaded",
                "Your document has been successfully uploaded.",
                "2 days ago",
                PURPLE
        );


        VBox centreSelected = createNotificationCard(
                "♜",
                "Centre selected",
                "Your Xerox centre has been selected successfully.",
                "3 days ago",
                PURPLE
        );


        VBox permissionUpdated = createNotificationCard(
                "♢",
                "Permissions updated",
                "Privacy permissions for your document were updated.",
                "3 days ago",
                GREEN
        );


        VBox requestCreated = createNotificationCard(
                "◴",
                "Request created",
                "Your document request has been created successfully.",
                "4 days ago",
                PURPLE
        );


        // =====================================================
        // ADD TO GRID
        // =====================================================

        notificationGrid.add(
                printStarted,
                0,
                0
        );

        notificationGrid.add(
                printCompleted,
                1,
                0
        );


        notificationGrid.add(
                sessionExpired,
                0,
                1
        );

        notificationGrid.add(
                requestRejected,
                1,
                1
        );


        notificationGrid.add(
                documentUploaded,
                0,
                2
        );

        notificationGrid.add(
                centreSelected,
                1,
                2
        );


        notificationGrid.add(
                permissionUpdated,
                0,
                3
        );

        notificationGrid.add(
                requestCreated,
                1,
                3
        );


        // =====================================================
        // COLUMN WIDTH
        // =====================================================

        javafx.scene.layout.ColumnConstraints column1 =
                new javafx.scene.layout.ColumnConstraints();

        javafx.scene.layout.ColumnConstraints column2 =
                new javafx.scene.layout.ColumnConstraints();

        column1.setPercentWidth(50);
        column2.setPercentWidth(50);

        notificationGrid.getColumnConstraints().addAll(
                column1,
                column2
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(notificationGrid);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle("""
                -fx-background-color: transparent;
                -fx-background: transparent;
                -fx-border-color: transparent;
                """);


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


        return main;
    }


    // =========================================================
    // CREATE NOTIFICATION CARD
    // =========================================================

    private VBox createNotificationCard(
            String icon,
            String title,
            String description,
            String time,
            String iconColor) {


        VBox card = new VBox(8);

        card.setPrefHeight(155);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setPadding(
                new Insets(22)
        );

        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // TOP SECTION
        // =====================================================

        HBox top = new HBox(15);

        top.setAlignment(
                Pos.TOP_LEFT
        );


        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel = new Label(
                icon
        );

        iconLabel.setAlignment(
                Pos.CENTER
        );

        iconLabel.setPrefSize(
                50,
                50
        );

        iconLabel.setMinSize(
                50,
                50
        );

        iconLabel.setStyle(
                "-fx-background-color: " + iconColor + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel = new Label(
                title
        );

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        VBox titleBox = new VBox();

        titleBox.getChildren().add(
                titleLabel
        );


        top.getChildren().addAll(
                iconLabel,
                titleBox
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel = new Label(
                description
        );

        descriptionLabel.setWrapText(true);

        descriptionLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        descriptionLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;"
        );


        // =====================================================
        // TIME
        // =====================================================

        Label timeLabel = new Label(
                time
        );

        timeLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // ADD TO CARD
        // =====================================================

        card.getChildren().addAll(
                top,
                descriptionLabel,
                timeLabel
        );


        // =====================================================
        // HOVER
        // =====================================================

        card.setOnMouseEntered(e -> {

            card.setStyle(
                    "-fx-background-color: " + CARD_HOVER + ";" +
                    "-fx-border-color: " + iconColor + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 22;" +
                    "-fx-background-radius: 22;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                    "gaussian, rgba(139,92,246,0.25), " +
                    "15, 0.35, 0, 4);"
            );

            card.setScaleX(1.015);
            card.setScaleY(1.015);

        });


        // =====================================================
        // MOUSE EXIT
        // =====================================================

        card.setOnMouseExited(e -> {

            card.setStyle(
                    "-fx-background-color: " + CARD + ";" +
                    "-fx-border-color: " + BORDER + ";" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 22;" +
                    "-fx-background-radius: 22;" +
                    "-fx-cursor: hand;"
            );

            card.setScaleX(1);
            card.setScaleY(1);

        });


        return card;
    }
}