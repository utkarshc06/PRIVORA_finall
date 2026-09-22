package com.myprivora.view.xerox;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ActiveSessions {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private static final String BG = ClayTheme.OBSIDIAN_DEEP;
    private static final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private static final String CARD_ALT = ClayTheme.OBSIDIAN_SURFACE;
    private static final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private static final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private static final String PURPLE_LIGHT = ClayTheme.CYAN_LIGHT;

    private static final String TEXT = ClayTheme.FROST_WHITE;
    private static final String MUTED = ClayTheme.FROST_MUTED;

    private static final String GREEN = ClayTheme.CYAN_PRIMARY;
    private static final String BLUE = ClayTheme.CYAN_ACCENT;
    private static final String RED = ClayTheme.FROST_MUTED;
    private static final String ORANGE = ClayTheme.CYAN_LIGHT;

    private VBox content;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public ActiveSessions() {
    }

    // =========================================================
    // GET CONTENT
    // =========================================================

    public VBox getContent() {

        content = new VBox(25);

        content.setPadding(
                new Insets(30, 34, 40, 34)
        );

        content.setFillWidth(true);

        content.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        // =====================================================
        // HEADING
        // =====================================================

        Label title = new Label("Active sessions");

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 36px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label(
                "Currently running print sessions."
        );

        subtitle.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 16px;"
        );

        VBox heading = new VBox(
                5,
                title,
                subtitle
        );

        // =====================================================
        // TABLE CARD
        // =====================================================

        VBox tableCard = new VBox();

        tableCard.setPadding(
                new Insets(25, 30, 20, 30)
        );

        tableCard.setMaxWidth(Double.MAX_VALUE);

        tableCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 22px;" +
                "-fx-background-radius: 22px;"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox header = createHeader();

        tableCard.getChildren().add(header);

        // =====================================================
        // EMPTY STATE
        // =====================================================

        VBox emptyState = createEmptyState();

        tableCard.getChildren().add(emptyState);

        content.getChildren().addAll(
                heading,
                tableCard
        );

        VBox.setVgrow(tableCard, Priority.ALWAYS);

        return content;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createHeader() {

        HBox header = new HBox();

        header.setAlignment(Pos.CENTER_LEFT);

        header.setPadding(
                new Insets(0, 15, 16, 15)
        );

        Label customer = headerLabel("CUSTOMER");
        Label purpose = headerLabel("PURPOSE");
        Label time = headerLabel("TIME");
        Label status = headerLabel("STATUS");
        Label actions = headerLabel("ACTIONS");

        HBox.setHgrow(customer, Priority.ALWAYS);
        HBox.setHgrow(purpose, Priority.ALWAYS);
        HBox.setHgrow(time, Priority.ALWAYS);
        HBox.setHgrow(status, Priority.ALWAYS);

        customer.setPrefWidth(300);
        purpose.setPrefWidth(220);
        time.setPrefWidth(180);
        status.setPrefWidth(220);

        actions.setPrefWidth(130);

        header.getChildren().addAll(
                customer,
                purpose,
                time,
                status,
                actions
        );

        return header;
    }

    // =========================================================
    // EMPTY STATE
    // =========================================================

    private VBox createEmptyState() {

        VBox box = new VBox(14);

        box.setAlignment(Pos.CENTER);

        box.setPadding(
                new Insets(80, 20, 80, 20)
        );

        javafx.scene.Node icon = ClayTheme.createLottieAnimation("pulse_radar.gif", 56, 56);

        Label title = new Label(
                "No active sessions"
        );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 20px;" +
                "-fx-font-weight: bold;"
        );

        Label description = new Label(
                "Currently there are no running print sessions."
        );

        description.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 14px;"
        );

        box.getChildren().addAll(
                icon,
                title,
                description
        );

        return box;
    }

    // =========================================================
    // ADD SESSION
    // Firebase se data aane par ye method use karna
    // =========================================================

    public void addSession(
            String customer,
            String sessionId,
            String purpose,
            String time,
            String status
    ) {

        if (content == null) {
            getContent();
        }

        VBox tableCard =
                (VBox) content.getChildren().get(1);

        // Empty state remove karo
        if (tableCard.getChildren().size() > 1) {
            tableCard.getChildren().remove(1);
        }

        HBox row = createSessionRow(
                customer,
                sessionId,
                purpose,
                time,
                status
        );

        tableCard.getChildren().add(row);
    }

    // =========================================================
    // SESSION ROW
    // =========================================================

    private HBox createSessionRow(
            String customer,
            String sessionId,
            String purpose,
            String time,
            String status
    ) {

        HBox row = new HBox();

        row.setAlignment(Pos.CENTER_LEFT);

        row.setPadding(
                new Insets(14, 15, 14, 15)
        );

        row.setSpacing(10);

        row.setStyle(
                "-fx-background-color: " + CARD_ALT + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 0 0 1px 0;"
        );

        // =====================================================
        // CUSTOMER
        // =====================================================

        HBox customerBox =
                createCustomer(customer, sessionId);

        // =====================================================
        // PURPOSE
        // =====================================================

        Label purposeLabel =
                normalLabel(purpose);

        // =====================================================
        // TIME
        // =====================================================

        Label timeLabel =
                normalLabel(time);

        // =====================================================
        // STATUS
        // =====================================================

        Label statusLabel =
                createStatus(status);

        // =====================================================
        // ACTION
        // =====================================================

        HBox actions =
                createActions(status);

        HBox.setHgrow(customerBox, Priority.ALWAYS);
        HBox.setHgrow(purposeLabel, Priority.ALWAYS);
        HBox.setHgrow(timeLabel, Priority.ALWAYS);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        customerBox.setPrefWidth(300);
        purposeLabel.setPrefWidth(220);
        timeLabel.setPrefWidth(180);
        statusLabel.setPrefWidth(220);
        actions.setPrefWidth(130);

        row.getChildren().addAll(
                customerBox,
                purposeLabel,
                timeLabel,
                statusLabel,
                actions
        );

        return row;
    }

    // =========================================================
    // CUSTOMER
    // =========================================================

    private HBox createCustomer(
            String customer,
            String sessionId
    ) {

        HBox box = new HBox(12);

        box.setAlignment(Pos.CENTER_LEFT);

        String initials = getInitials(customer);

        Circle circle = new Circle(22);

        circle.setFill(
                Color.web(PURPLE)
        );

        Label initialLabel =
                new Label(initials);

        initialLabel.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        VBox nameBox = new VBox(2);

        Label name =
                new Label(customer);

        name.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );

        Label id =
                new Label(sessionId);

        id.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 13px;"
        );

        nameBox.getChildren().addAll(
                name,
                id
        );

        StackPaneCircle stack =
                new StackPaneCircle(
                        circle,
                        initialLabel
                );

        box.getChildren().addAll(
                stack,
                nameBox
        );

        return box;
    }

    // =========================================================
    // STATUS
    // =========================================================

    private Label createStatus(String status) {

        Label label =
                new Label(status);

        String color;

        switch (status.toLowerCase()) {

            case "active":
                color = GREEN;
                break;

            case "completed":
                color = BLUE;
                break;

            case "rejected":
                color = RED;
                break;

            case "pending":
            default:
                color = ORANGE;
                break;
        }

        label.setStyle(
                "-fx-text-fill: " + color + ";" +
                "-fx-background-color: " + color + "22;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-radius: 15px;" +
                "-fx-background-radius: 15px;" +
                "-fx-padding: 5px 14px;" +
                "-fx-font-size: 13px;"
        );

        return label;
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    private HBox createActions(String status) {

        HBox box = new HBox(8);

        box.setAlignment(Pos.CENTER_LEFT);

        if (status.equalsIgnoreCase("pending")) {

            Button accept =
                    new Button("Accept");

            accept.setStyle(
                    "-fx-background-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                    "-fx-text-fill: #080C16;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 18px;" +
                    "-fx-padding: 8px 15px;" +
                    "-fx-cursor: hand;"
            );
            ClayTheme.applyButtonHover(accept, true);

            Button reject =
                    new Button("Reject");

            reject.setStyle(
                    "-fx-background-color: rgba(255, 255, 255, 0.06);" +
                    "-fx-text-fill: " + ClayTheme.FROST_WHITE + ";" +
                    "-fx-border-color: " + ClayTheme.CARD_BORDER_COLOR + ";" +
                    "-fx-border-radius: 18px;" +
                    "-fx-background-radius: 18px;" +
                    "-fx-padding: 8px 15px;" +
                    "-fx-cursor: hand;"
            );
            ClayTheme.applyButtonHover(reject, false);

            box.getChildren().addAll(
                    accept,
                    reject
            );

        } else {

            Button open =
                    new Button("Open");

            open.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + TEXT + ";" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
            );
            ClayTheme.applyButtonHover(open, false);

            box.getChildren().add(open);
        }

        return box;
    }

    // =========================================================
    // LABEL HELPERS
    // =========================================================

    private Label headerLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + PURPLE_LIGHT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    private Label normalLabel(String text) {

        Label label = new Label(text);

        label.setStyle(
                "-fx-text-fill: " + MUTED + ";" +
                "-fx-font-size: 15px;"
        );

        return label;
    }

    // =========================================================
    // INITIALS
    // =========================================================

    private String getInitials(String name) {

        if (name == null || name.isEmpty()) {
            return "?";
        }

        String[] parts = name.trim().split("\\s+");

        if (parts.length == 1) {
            return parts[0]
                    .substring(0, 1)
                    .toUpperCase();
        }

        return (
                parts[0].substring(0, 1) +
                parts[parts.length - 1]
                        .substring(0, 1)
        ).toUpperCase();
    }

    // =========================================================
    // SCENE
    // =========================================================

    public javafx.scene.Scene getScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: " + BG + ";"
        );

        root.setCenter(
                getContent()
        );

        return new javafx.scene.Scene(
                root,
                1200,
                800
        );
    }

    // =========================================================
    // SMALL STACKPANE HELPER
    // =========================================================

    private static class StackPaneCircle
            extends javafx.scene.layout.StackPane {

        StackPaneCircle(
                Circle circle,
                Label label
        ) {

            getChildren().addAll(
                    circle,
                    label
            );

            setAlignment(Pos.CENTER);
        }
    }
}