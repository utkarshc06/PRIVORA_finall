package com.myprivora.view.user;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Permissions {

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
    // NAVIGATION CALLBACK
    // =========================================================

    private final Runnable nextAction;


    // =========================================================
    // SELECTED VALUES
    // =========================================================

    private int selectedPrintLimit = 3;

    private int selectedExpiryMinutes = 5;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Permissions(Runnable nextAction) {

        /*
         * Never allow nextAction to become null.
         *
         * If UserDashboard sends a callback,
         * that callback will be stored.
         *
         * If it sends null, an empty action is used.
         */

        this.nextAction =
                nextAction != null
                        ? nextAction
                        : () -> {
                        };

        System.out.println(
                "[Permissions] Permissions page created."
        );

        System.out.println(
                "[Permissions] Navigation callback received = "
                        + (nextAction != null)
        );
    }


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public Permissions() {

        this(() -> {
        });
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        System.out.println(
                "[Permissions] getContent() called."
        );

        VBox main =
                new VBox(25);

        main.setPadding(
                new Insets(
                        30,
                        35,
                        40,
                        35
                )
        );

        main.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        VBox header =
                new VBox(5);


        Label title =
                new Label(
                        "Permission settings"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Set the print limit and expiry time for this request."
                );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 15px;"
        );


        header.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // PERMISSION GRID
        // =====================================================

        GridPane grid =
                new GridPane();

        grid.setHgap(20);

        grid.setVgap(20);


        // =====================================================
        // PRINT LIMIT
        // =====================================================

        VBox printLimit =
                createPrintLimitCard();


        // =====================================================
        // EXPIRY TIME
        // =====================================================

        VBox expiryTime =
                createExpiryCard();


        // =====================================================
        // ADD ONLY TWO CARDS
        // =====================================================

        grid.add(
                printLimit,
                0,
                0
        );

        grid.add(
                expiryTime,
                1,
                0
        );


        // =====================================================
        // COLUMN 1
        // =====================================================

        ColumnConstraints column1 =
                new ColumnConstraints();

        column1.setPercentWidth(50);

        column1.setHgrow(
                Priority.ALWAYS
        );


        // =====================================================
        // COLUMN 2
        // =====================================================

        ColumnConstraints column2 =
                new ColumnConstraints();

        column2.setPercentWidth(50);

        column2.setHgrow(
                Priority.ALWAYS
        );


        grid.getColumnConstraints().addAll(
                column1,
                column2
        );


        // =====================================================
        // SAVE & CONTINUE BUTTON
        // =====================================================

        HBox buttonBox =
                new HBox();

        buttonBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        Button saveButton =
                new Button(
                        "▣   Save & Continue"
                );


        saveButton.setPrefWidth(
                165
        );

        saveButton.setPrefHeight(
                48
        );


        saveButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", "
                        + DEEP_PURPLE + ");" +
                "-fx-border-color: transparent;" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // SAVE BUTTON HOVER
        // =====================================================

        saveButton.setOnMouseEntered(
                e -> {

                    saveButton.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + LIGHT_PURPLE + ", "
                                    + PURPLE + ");" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(" +
                            "gaussian, rgba(139,92,246,0.35), " +
                            "15, 0.4, 0, 3);"
                    );

                    saveButton.setScaleX(1.03);

                    saveButton.setScaleY(1.03);
                }
        );


        saveButton.setOnMouseExited(
                e -> {

                    saveButton.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + PURPLE + ", "
                                    + DEEP_PURPLE + ");" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-radius: 15;" +
                            "-fx-background-radius: 15;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
                    );

                    saveButton.setScaleX(1);

                    saveButton.setScaleY(1);
                }
        );


        // =====================================================
        // SAVE ACTION
        // =====================================================

        saveButton.setOnAction(
                e -> {

                    System.out.println(
                            "================================================="
                    );

                    System.out.println(
                            "[Permissions] SAVE & CONTINUE CLICKED"
                    );

                    System.out.println(
                            "[Permissions] Print Limit = "
                                    + selectedPrintLimit
                    );

                    System.out.println(
                            "[Permissions] Expiry = "
                                    + selectedExpiryMinutes
                                    + " minutes"
                    );


                    // -----------------------------------------
                    // VALIDATE PRINT LIMIT
                    // -----------------------------------------

                    if (selectedPrintLimit < 1
                            || selectedPrintLimit > 99) {

                        System.out.println(
                                "[Permissions] ERROR: Invalid print limit."
                        );

                        return;
                    }


                    // -----------------------------------------
                    // VALIDATE EXPIRY
                    // -----------------------------------------

                    if (selectedExpiryMinutes < 1
                            || selectedExpiryMinutes > 10) {

                        System.out.println(
                                "[Permissions] ERROR: Invalid expiry."
                        );

                        return;
                    }


                    System.out.println(
                            "[Permissions] Validation successful."
                    );


                    // -----------------------------------------
                    // NAVIGATION
                    // -----------------------------------------

                    System.out.println(
                            "[Permissions] Calling nextAction.run()..."
                    );


                    try {

                        nextAction.run();

                        System.out.println(
                                "[Permissions] nextAction.run() completed."
                        );

                    } catch (Exception ex) {

                        System.out.println(
                                "[Permissions] ERROR while executing "
                                        + "nextAction:"
                        );

                        ex.printStackTrace();
                    }


                    System.out.println(
                            "================================================="
                    );
                }
        );


        buttonBox.getChildren().add(
                saveButton
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                header,
                grid,
                buttonBox
        );


        return main;
    }


    // =========================================================
    // PRINT LIMIT CARD
    // =========================================================

    private VBox createPrintLimitCard() {

        VBox card =
                createCard();


        HBox row =
                new HBox(18);

        row.setAlignment(
                Pos.CENTER_LEFT
        );


        StackPane icon =
                createIcon("▣");


        VBox textBox =
                new VBox(4);


        Label title =
                new Label(
                        "Print Limit"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );


        Label description =
                new Label(
                        "Cap total number of prints"
                );

        description.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 13px;"
        );


        textBox.getChildren().addAll(
                title,
                description
        );


        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        // =====================================================
        // COUNTER
        // =====================================================

        HBox counter =
                new HBox();

        counter.setAlignment(
                Pos.CENTER
        );

        counter.setPrefHeight(
                52
        );

        counter.setStyle(
                "-fx-background-color: #1B1926;" +
                "-fx-border-color: #302A40;" +
                "-fx-border-radius: 25;" +
                "-fx-background-radius: 25;"
        );


        Button minus =
                new Button("−");


        Button plus =
                new Button("+");


        Label number =
                new Label(
                        String.valueOf(
                                selectedPrintLimit
                        )
                );


        number.setMinWidth(
                45
        );

        number.setAlignment(
                Pos.CENTER
        );

        number.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );


        styleCounterButton(
                minus
        );

        styleCounterButton(
                plus
        );


        // =====================================================
        // MINUS ACTION
        // =====================================================

        minus.setOnAction(
                e -> {

                    if (selectedPrintLimit > 1) {

                        selectedPrintLimit--;

                        number.setText(
                                String.valueOf(
                                        selectedPrintLimit
                                )
                        );

                        System.out.println(
                                "[Permissions] Print Limit decreased to "
                                        + selectedPrintLimit
                        );
                    }
                }
        );


        // =====================================================
        // PLUS ACTION
        // =====================================================

        plus.setOnAction(
                e -> {

                    if (selectedPrintLimit < 99) {

                        selectedPrintLimit++;

                        number.setText(
                                String.valueOf(
                                        selectedPrintLimit
                                )
                        );

                        System.out.println(
                                "[Permissions] Print Limit increased to "
                                        + selectedPrintLimit
                        );
                    }
                }
        );


        counter.getChildren().addAll(
                minus,
                number,
                plus
        );


        row.getChildren().addAll(
                icon,
                textBox,
                counter
        );


        card.getChildren().add(
                row
        );


        return card;
    }


    // =========================================================
    // EXPIRY CARD
    // =========================================================

    private VBox createExpiryCard() {

        VBox card =
                createCard();


        HBox row =
                new HBox(18);

        row.setAlignment(
                Pos.CENTER_LEFT
        );


        StackPane icon =
                createIcon("◷");


        VBox textBox =
                new VBox(4);


        Label title =
                new Label(
                        "Expiry Time"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );


        Label description =
                new Label(
                        "Automatically expire request"
                );

        description.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 13px;"
        );


        textBox.getChildren().addAll(
                title,
                description
        );


        HBox.setHgrow(
                textBox,
                Priority.ALWAYS
        );


        // =====================================================
        // EXPIRY COMBO BOX
        // =====================================================

        ComboBox<String> expiry =
                new ComboBox<>();


        expiry.getItems().addAll(
                "1 min",
                "2 min",
                "3 min",
                "4 min",
                "5 min",
                "6 min",
                "7 min",
                "8 min",
                "9 min",
                "10 min"
        );


        expiry.setValue(
                "5 min"
        );


        expiry.setPrefWidth(
                125
        );

        expiry.setPrefHeight(
                42
        );


        // =====================================================
        // FIXED COMBOBOX TEXT VISIBILITY
        // =====================================================
        //
        // The ComboBox background is dark.
        //
        // JavaFX may use a dark default text color for
        // the selected value.
        //
        // Therefore we explicitly set:
        //
        // 1. Selected value text = white
        // 2. Dropdown item text = white
        // 3. Dropdown background = dark
        //
        // =====================================================

        expiry.setStyle(
                "-fx-background-color: #1B1926;" +
                "-fx-border-color: #302A40;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // MAKE SELECTED VALUE VISIBLE
        // =====================================================

        expiry.getEditor().setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-background-color: transparent;"
        );


        // =====================================================
        // STYLE THE COMBOBOX LIST CELL
        // =====================================================
        //
        // The selected value inside a JavaFX ComboBox is
        // actually displayed using a ListCell.
        //
        // This is the important fix.
        //
        // =====================================================

        expiry.setCellFactory(
                comboBox -> {

                    javafx.scene.control.ListCell<String> cell =
                            new javafx.scene.control.ListCell<String>() {

                                @Override
                                protected void updateItem(
                                        String item,
                                        boolean empty) {

                                    super.updateItem(
                                            item,
                                            empty
                                    );

                                    if (empty ||
                                            item == null) {

                                        setText(null);

                                        setStyle(
                                                "-fx-background-color: #1B1926;"
                                        );

                                    } else {

                                        setText(
                                                item
                                        );

                                        setStyle(
                                                "-fx-background-color: #1B1926;" +
                                                "-fx-text-fill: " + TEXT + ";" +
                                                "-fx-font-size: 14px;" +
                                                "-fx-font-weight: bold;"
                                        );
                                    }
                                }
                            };

                    return cell;
                }
        );


        // =====================================================
        // STYLE SELECTED VALUE CELL
        // =====================================================

        expiry.setButtonCell(
                new javafx.scene.control.ListCell<String>() {

                    @Override
                    protected void updateItem(
                            String item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty ||
                                item == null) {

                            setText(null);

                            setStyle(
                                    "-fx-background-color: transparent;" +
                                    "-fx-text-fill: " + TEXT + ";"
                            );

                        } else {

                            setText(
                                    item
                            );

                            setStyle(
                                    "-fx-background-color: transparent;" +
                                    "-fx-text-fill: " + TEXT + ";" +
                                    "-fx-font-size: 14px;" +
                                    "-fx-font-weight: bold;"
                            );
                        }
                    }
                }
        );


        // =====================================================
        // EXPIRY SELECTION
        // =====================================================

        expiry.setOnAction(
                e -> {

                    String selected =
                            expiry.getValue();


                    if (selected != null) {

                        selectedExpiryMinutes =
                                Integer.parseInt(
                                        selected.replace(
                                                " min",
                                                ""
                                        )
                                );


                        System.out.println(
                                "[Permissions] Selected expiry = "
                                        + selectedExpiryMinutes
                                        + " minutes"
                        );
                    }
                }
        );


        row.getChildren().addAll(
                icon,
                textBox,
                expiry
        );


        card.getChildren().add(
                row
        );


        return card;
    }


    // =========================================================
    // GET PRINT LIMIT
    // =========================================================

    public int getSelectedPrintLimit() {

        return selectedPrintLimit;
    }


    // =========================================================
    // GET EXPIRY TIME
    // =========================================================

    public int getSelectedExpiryMinutes() {

        return selectedExpiryMinutes;
    }


    // =========================================================
    // CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox();


        card.setPrefHeight(
                125
        );

        card.setMinHeight(
                115
        );


        card.setPadding(
                new Insets(
                        20,
                        25,
                        20,
                        25
                )
        );


        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;"
        );


        // =====================================================
        // CARD HOVER
        // =====================================================

        card.setOnMouseEntered(
                e -> {

                    card.setStyle(
                            "-fx-background-color: #1B1828;" +
                            "-fx-border-color: #4C3575;" +
                            "-fx-border-radius: 22;" +
                            "-fx-background-radius: 22;"
                    );
                }
        );


        card.setOnMouseExited(
                e -> {

                    card.setStyle(
                            "-fx-background-color: " + CARD + ";" +
                            "-fx-border-color: " + BORDER + ";" +
                            "-fx-border-radius: 22;" +
                            "-fx-background-radius: 22;"
                    );
                }
        );


        return card;
    }


    // =========================================================
    // ICON
    // =========================================================

    private StackPane createIcon(
            String iconText) {

        Circle circle =
                new Circle(
                        25
                );


        circle.setFill(
                Color.web(
                        PURPLE
                )
        );


        Label iconLabel =
                new Label(
                        iconText
                );


        iconLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );


        StackPane box =
                new StackPane();


        box.setPrefSize(
                50,
                50
        );


        box.getChildren().addAll(
                circle,
                iconLabel
        );


        return box;
    }


    // =========================================================
    // COUNTER BUTTON STYLE
    // =========================================================

    private void styleCounterButton(
            Button button) {

        button.setPrefWidth(
                42
        );

        button.setPrefHeight(
                42
        );


        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );


        button.setOnMouseEntered(
                e -> {

                    button.setStyle(
                            "-fx-background-color: #29213D;" +
                            "-fx-text-fill: " + LIGHT_PURPLE + ";" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
                    );
                }
        );


        button.setOnMouseExited(
                e -> {

                    button.setStyle(
                            "-fx-background-color: transparent;" +
                            "-fx-text-fill: " + TEXT + ";" +
                            "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;"
                    );
                }
        );
    }
}