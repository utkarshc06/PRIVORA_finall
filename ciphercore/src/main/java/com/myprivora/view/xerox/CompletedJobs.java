package com.myprivora.view.xerox;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CompletedJobs {

    private final BorderPane root;

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private static final String BG = ClayTheme.OBSIDIAN_DEEP;
    private static final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private static final String CARD_ALT = ClayTheme.OBSIDIAN_SURFACE;
    private static final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private static final String TEXT = ClayTheme.FROST_WHITE;
    private static final String SECONDARY = ClayTheme.FROST_MUTED;

    private static final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private static final String PURPLE_LIGHT = ClayTheme.CYAN_LIGHT;
    private static final String PURPLE_BG = "rgba(0, 240, 255, 0.12)";

    private static final String RED = ClayTheme.FROST_MUTED;
    private static final String RED_BG = "rgba(255, 255, 255, 0.06)";

    private static final String GRAY_BG = "rgba(255, 255, 255, 0.04)";


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CompletedJobs() {

        root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        root.setCenter(createMainContent());
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    private VBox createMainContent() {

        VBox content = new VBox(25);

        content.setPadding(
                new Insets(40)
        );

        content.setFillWidth(true);

        // -----------------------------------------------------
        // TITLE
        // -----------------------------------------------------

        Label title = new Label(
                "Completed jobs"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 40px;" +
                "-fx-font-weight: bold;"
        );


        // -----------------------------------------------------
        // SUBTITLE
        // -----------------------------------------------------

        Label subtitle = new Label(
                "Every finished job with a full audit trail."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 18px;"
        );


        VBox heading = new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // -----------------------------------------------------
        // TABLE
        // -----------------------------------------------------

        VBox tableCard = createTableCard();


        content.getChildren().addAll(
                heading,
                tableCard
        );

        return content;
    }


    // =========================================================
    // TABLE CARD
    // =========================================================

    private VBox createTableCard() {

        VBox card = new VBox();

        card.setPadding(
                new Insets(20, 30, 25, 30)
        );

        card.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 28px;" +
                "-fx-background-radius: 28px;"
        );


        GridPane table = new GridPane();

        table.setHgap(20);
        table.setVgap(0);

        table.setMaxWidth(Double.MAX_VALUE);


        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        ColumnConstraints customerColumn =
                new ColumnConstraints();

        customerColumn.setPercentWidth(22);


        ColumnConstraints purposeColumn =
                new ColumnConstraints();

        purposeColumn.setPercentWidth(20);


        ColumnConstraints dateColumn =
                new ColumnConstraints();

        dateColumn.setPercentWidth(20);


        ColumnConstraints printsColumn =
                new ColumnConstraints();

        printsColumn.setPercentWidth(13);


        ColumnConstraints statusColumn =
                new ColumnConstraints();

        statusColumn.setPercentWidth(25);


        table.getColumnConstraints().addAll(
                customerColumn,
                purposeColumn,
                dateColumn,
                printsColumn,
                statusColumn
        );


        // =====================================================
        // HEADER
        // =====================================================

        addHeader(
                table,
                "CUSTOMER",
                0
        );

        addHeader(
                table,
                "PURPOSE",
                1
        );

        addHeader(
                table,
                "DATE",
                2
        );

        addHeader(
                table,
                "PRINTS",
                3
        );

        addHeader(
                table,
                "STATUS",
                4
        );


        addHorizontalLine(
                table,
                1
        );


        // =====================================================
        // COMPLETED JOB 1
        // =====================================================

        addJobRow(
                table,
                2,
                "AS",
                "Aditi Sharma",
                "Printing",
                "Jul 25, 10:38",
                "3/3",
                "Completed"
        );


        // =====================================================
        // COMPLETED JOB 2
        // =====================================================

        addJobRow(
                table,
                3,
                "RV",
                "Rahul Verma",
                "Scanning",
                "Jul 25, 09:12",
                "1/1",
                "Completed"
        );


        // =====================================================
        // COMPLETED JOB 3
        // =====================================================

        addJobRow(
                table,
                4,
                "PN",
                "Priya Nair",
                "Form Filling",
                "Jul 24, 17:41",
                "2/2",
                "Completed"
        );


        // =====================================================
        // EXPIRED JOB
        // =====================================================

        addJobRow(
                table,
                5,
                "KM",
                "Kunal Mehta",
                "Verification",
                "Jul 24, 15:22",
                "0/1",
                "Expired"
        );


        // =====================================================
        // REJECTED JOB
        // =====================================================

        addJobRow(
                table,
                6,
                "SI",
                "Sana Iqbal",
                "Printing",
                "Jul 23, 11:03",
                "0/2",
                "Rejected"
        );


        card.getChildren().add(
                table
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        return card;
    }


    // =========================================================
    // HEADER
    // =========================================================

    private void addHeader(
            GridPane table,
            String text,
            int column
    ) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );

        GridPane.setMargin(
                label,
                new Insets(5, 0, 18, 0)
        );

        table.add(
                label,
                column,
                0
        );
    }


    // =========================================================
    // HORIZONTAL LINE
    // =========================================================

    private void addHorizontalLine(
            GridPane table,
            int row
    ) {

        HBox line = new HBox();

        line.setPrefHeight(1);

        line.setMaxWidth(
                Double.MAX_VALUE
        );

        line.setStyle(
                "-fx-background-color: " + BORDER + ";"
        );

        GridPane.setColumnSpan(
                line,
                5
        );

        table.add(
                line,
                0,
                row
        );
    }


    // =========================================================
    // JOB ROW
    // =========================================================

    private void addJobRow(
            GridPane table,
            int row,
            String initials,
            String customerName,
            String purpose,
            String date,
            String prints,
            String status
    ) {

        // -----------------------------------------------------
        // CUSTOMER
        // -----------------------------------------------------

        HBox customerBox =
                new HBox(15);

        customerBox.setAlignment(
                Pos.CENTER_LEFT
        );


        // -----------------------------------------------------
        // AVATAR
        // -----------------------------------------------------

        Circle avatar =
                new Circle(22);

        avatar.setFill(
                Color.web(PURPLE)
        );


        Label initialsLabel =
                new Label(initials);

        initialsLabel.setStyle(
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        StackPane avatarBox =
                new StackPane();

        avatarBox.setPrefSize(
                44,
                44
        );

        avatarBox.setMinSize(
                44,
                44
        );

        avatarBox.setMaxSize(
                44,
                44
        );

        avatarBox.setAlignment(
                Pos.CENTER
        );

        avatarBox.getChildren().addAll(
                avatar,
                initialsLabel
        );


        // -----------------------------------------------------
        // CUSTOMER INFORMATION
        // -----------------------------------------------------

        VBox customerInfo =
                new VBox(2);


        Label name =
                new Label(customerName);

        name.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        Label session =
                new Label(
                        getSessionId(customerName)
                );

        session.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 14px;"
        );


        customerInfo.getChildren().addAll(
                name,
                session
        );


        customerBox.getChildren().addAll(
                avatarBox,
                customerInfo
        );


        // -----------------------------------------------------
        // PURPOSE
        // -----------------------------------------------------

        Label purposeLabel =
                new Label(purpose);

        purposeLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 17px;"
        );


        // -----------------------------------------------------
        // DATE
        // -----------------------------------------------------

        Label dateLabel =
                new Label(date);

        dateLabel.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 17px;"
        );


        // -----------------------------------------------------
        // PRINTS
        // -----------------------------------------------------

        Label printsLabel =
                new Label(prints);

        printsLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        // -----------------------------------------------------
        // STATUS
        // -----------------------------------------------------

        Label statusLabel =
                createStatus(status);


        // -----------------------------------------------------
        // ADD TO TABLE
        // -----------------------------------------------------

        table.add(
                customerBox,
                0,
                row
        );

        table.add(
                purposeLabel,
                1,
                row
        );

        table.add(
                dateLabel,
                2,
                row
        );

        table.add(
                printsLabel,
                3,
                row
        );

        table.add(
                statusLabel,
                4,
                row
        );


        // -----------------------------------------------------
        // ROW PADDING
        // -----------------------------------------------------

        GridPane.setMargin(
                customerBox,
                new Insets(14, 0, 14, 0)
        );

        GridPane.setMargin(
                purposeLabel,
                new Insets(14, 0, 14, 0)
        );

        GridPane.setMargin(
                dateLabel,
                new Insets(14, 0, 14, 0)
        );

        GridPane.setMargin(
                printsLabel,
                new Insets(14, 0, 14, 0)
        );

        GridPane.setMargin(
                statusLabel,
                new Insets(14, 0, 14, 0)
        );
    }


    // =========================================================
    // STATUS BADGE
    // =========================================================

    private Label createStatus(
            String status
    ) {

        Label label =
                new Label(status);

        label.setPadding(
                new Insets(
                        5,
                        12,
                        5,
                        12
                )
        );


        // -----------------------------------------------------
        // COMPLETED = PURPLE
        // -----------------------------------------------------

        if (status.equalsIgnoreCase("Completed")) {

            label.setStyle(
                    "-fx-text-fill: " + PURPLE_LIGHT + ";" +
                    "-fx-background-color: " + PURPLE_BG + ";" +
                    "-fx-border-color: " + PURPLE + ";" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-radius: 20px;" +
                    "-fx-background-radius: 20px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
        }


        // -----------------------------------------------------
        // REJECTED = RED
        // -----------------------------------------------------

        else if (
                status.equalsIgnoreCase("Rejected")
        ) {

            label.setStyle(
                    "-fx-text-fill: " + RED + ";" +
                    "-fx-background-color: " + RED_BG + ";" +
                    "-fx-border-color: #7A2935;" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-radius: 20px;" +
                    "-fx-background-radius: 20px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
        }


        // -----------------------------------------------------
        // EXPIRED = GREY
        // -----------------------------------------------------

        else {

            label.setStyle(
                    "-fx-text-fill: " + SECONDARY + ";" +
                    "-fx-background-color: " + GRAY_BG + ";" +
                    "-fx-border-color: " + BORDER + ";" +
                    "-fx-border-width: 1px;" +
                    "-fx-border-radius: 20px;" +
                    "-fx-background-radius: 20px;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
        }

        return label;
    }


    // =========================================================
    // SESSION ID
    // =========================================================

    private String getSessionId(
            String customer
    ) {

        if (customer.equals("Aditi Sharma")) {
            return "VF-8823-KL";
        }

        if (customer.equals("Rahul Verma")) {
            return "VF-8824-MP";
        }

        if (customer.equals("Priya Nair")) {
            return "VF-8825-XZ";
        }

        if (customer.equals("Kunal Mehta")) {
            return "VF-8820-AA";
        }

        if (customer.equals("Sana Iqbal")) {
            return "VF-8819-BR";
        }

        return "VF-0000-XX";
    }


    // =========================================================
    // GET CONTENT
    // =========================================================

    public Node getContent() {

        return root;
    }
}