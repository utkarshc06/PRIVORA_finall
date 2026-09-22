package com.myprivora.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Purpose {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String BACKGROUND = "#080C16";
    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00F0FF";
    private final String DEEP_PURPLE = "#00B4D8";
    private final String LIGHT_PURPLE = "#33F3FF";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY = "rgba(255, 255, 255, 0.70)";


    // =========================================================
    // NEXT ACTION
    // =========================================================

    private final Runnable nextAction;


    // =========================================================
    // SELECTED CARD
    // =========================================================

    private VBox selectedCard;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Purpose(Runnable nextAction) {

        this.nextAction = nextAction;

    }


    // =========================================================
    // CONTENT
    // =========================================================

    public VBox getContent() {

        VBox main = new VBox(25);

        main.setPadding(
                new Insets(35, 45, 40, 45)
        );

        main.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADING
        // =====================================================

        Label title = new Label(
                "Choose a purpose"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle = new Label(
                "This determines the default privacy rules we apply."
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
        // PURPOSE GRID
        // =====================================================

        GridPane grid = new GridPane();

        grid.setHgap(22);
        grid.setVgap(22);


        // =====================================================
        // PURPOSE CARDS
        // =====================================================

        VBox printing = createPurposeCard(
                "▣",
                "Printing",
                "Physical print copies"
        );


        VBox scanning = createPurposeCard(
                "⌗",
                "Scanning",
                "Digital scan output"
        );


        VBox formFilling = createPurposeCard(
                "▧",
                "Form Filling",
                "Assisted form entry"
        );


        VBox verification = createPurposeCard(
                "♢",
                "Verification",
                "KYC / ID check"
        );


        VBox submission = createPurposeCard(
                "▤",
                "Submission",
                "Send to authority"
        );


        VBox others = createPurposeCard(
                "✧",
                "Others",
                "Something else"
        );


        // =====================================================
        // GRID POSITION
        // =====================================================

        grid.add(
                printing,
                0,
                0
        );

        grid.add(
                scanning,
                1,
                0
        );

        grid.add(
                formFilling,
                2,
                0
        );


        grid.add(
                verification,
                0,
                1
        );

        grid.add(
                submission,
                1,
                1
        );

        grid.add(
                others,
                2,
                1
        );


        // =====================================================
        // DEFAULT SELECTION
        // =====================================================

        selectCard(printing);


        // =====================================================
        // NEXT BUTTON
        // =====================================================

        HBox buttonBox = new HBox();

        buttonBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        Button nextButton = new Button(
                "Next   →"
        );


        nextButton.setPrefWidth(
                135
        );

        nextButton.setPrefHeight(
                50
        );


        nextButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + DEEP_PURPLE + ", "
                        + PURPLE + ");" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(" +
                    "gaussian, " +
                    "rgba(139,92,246,0.25), " +
                    "12, 0.3, 0, 3);"
        );


        // =====================================================
        // NEXT CLICK
        // =====================================================

        nextButton.setOnAction(e -> {

            if (nextAction != null) {

                nextAction.run();

            }

        });


        // =====================================================
        // NEXT HOVER
        // =====================================================

        nextButton.setOnMouseEntered(e -> {

            nextButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, "
                            + PURPLE + ", "
                            + LIGHT_PURPLE + ");" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                        "gaussian, " +
                        "rgba(139,92,246,0.50), " +
                        "18, 0.4, 0, 4);"
            );


            nextButton.setScaleX(1.03);
            nextButton.setScaleY(1.03);

        });


        nextButton.setOnMouseExited(e -> {

            nextButton.setStyle(
                    "-fx-background-color: linear-gradient(to right, "
                            + DEEP_PURPLE + ", "
                            + PURPLE + ");" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 16px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                        "gaussian, " +
                        "rgba(139,92,246,0.25), " +
                        "12, 0.3, 0, 3);"
            );


            nextButton.setScaleX(1);
            nextButton.setScaleY(1);

        });


        buttonBox.getChildren().add(
                nextButton
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                heading,
                grid,
                buttonBox
        );


        return main;
    }


    // =========================================================
    // CREATE PURPOSE CARD
    // =========================================================

    private VBox createPurposeCard(
            String icon,
            String title,
            String description) {


        VBox card = new VBox(12);

        card.setPrefWidth(
                285
        );

        card.setPrefHeight(
                180
        );

        card.setPadding(
                new Insets(25)
        );


        // =====================================================
        // DEFAULT STYLE
        // =====================================================

        setNormalStyle(card);


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
                52,
                52
        );


        iconLabel.setStyle(
                "-fx-background-color: #241C35;" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label titleLabel = new Label(
                title
        );


        titleLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel = new Label(
                description
        );


        descriptionLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;"
        );


        // =====================================================
        // ADD CONTENT
        // =====================================================

        card.getChildren().addAll(
                iconLabel,
                titleLabel,
                descriptionLabel
        );


        // =====================================================
        // CLICK
        // =====================================================

        card.setOnMouseClicked(e -> {

            selectCard(card);

        });


        // =====================================================
        // HOVER
        // =====================================================

        card.setOnMouseEntered(e -> {

            if (card != selectedCard) {

                card.setStyle(
                        "-fx-background-color: #1B1828;" +
                        "-fx-border-color: #6D28D9;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;" +
                        "-fx-background-radius: 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(" +
                            "gaussian, " +
                            "rgba(139,92,246,0.20), " +
                            "12, 0.3, 0, 3);"
                );


                card.setScaleX(1.02);
                card.setScaleY(1.02);

            }

        });


        card.setOnMouseExited(e -> {

            if (card != selectedCard) {

                setNormalStyle(card);

                card.setScaleX(1);
                card.setScaleY(1);

            }

        });


        return card;
    }


    // =========================================================
    // SELECT CARD
    // =========================================================

    private void selectCard(
            VBox card) {


        // =====================================================
        // REMOVE PREVIOUS SELECTION
        // =====================================================

        if (selectedCard != null) {

            setNormalStyle(
                    selectedCard
            );


            selectedCard.setScaleX(1);
            selectedCard.setScaleY(1);

        }


        // =====================================================
        // SELECT NEW CARD
        // =====================================================

        selectedCard = card;


        card.setStyle(
                "-fx-background-color: #1B1828;" +
                "-fx-border-color: " + PURPLE + ";" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(" +
                    "gaussian, " +
                    "rgba(139,92,246,0.30), " +
                    "15, 0.35, 0, 3);"
        );


        // =====================================================
        // SELECTED ICON
        // =====================================================

        Label iconLabel =
                (Label) card.getChildren().get(0);


        iconLabel.setStyle(
                "-fx-background-color: linear-gradient("
                        + "to bottom right, "
                        + DEEP_PURPLE + ", "
                        + PURPLE + ");" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 23px;" +
                "-fx-font-weight: bold;"
        );

    }


    // =========================================================
    // NORMAL CARD STYLE
    // =========================================================

    private void setNormalStyle(
            VBox card) {


        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
        );


        // =====================================================
        // NORMAL ICON
        // =====================================================

        if (!card.getChildren().isEmpty()) {

            Label iconLabel =
                    (Label) card.getChildren().get(0);


            iconLabel.setStyle(
                    "-fx-background-color: #241C35;" +
                    "-fx-background-radius: 50%;" +
                    "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                    "-fx-font-size: 23px;" +
                    "-fx-font-weight: bold;"
            );

        }

    }
}