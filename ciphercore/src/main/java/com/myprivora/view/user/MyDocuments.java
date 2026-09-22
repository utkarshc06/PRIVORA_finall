package com.myprivora.view.user;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.Document;
import com.myprivora.model.User;
import com.myprivora.session.SessionManager;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MyDocuments {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private static final String BACKGROUND = "#080C16";
    private static final String CARD = "rgba(13, 19, 34, 0.75)";
    private static final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private static final String PURPLE = "#00F0FF";
    private static final String LIGHT_PURPLE = "#33F3FF";

    private static final String TEXT = "#FFFFFF";
    private static final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";

    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore db =
            DatabaseConfig.getFirestore();

    private ListenerRegistration listenerRegistration;

    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox main =
                new VBox(22);

        main.setPadding(
                new Insets(
                        28,
                        38,
                        40,
                        38
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
                        "My documents"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "All documents you've ever shared."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 14px;"
        );

        header.getChildren().addAll(
                title,
                subtitle
        );

        // =====================================================
        // DOCUMENT CARD
        // =====================================================

        VBox documentCard =
                new VBox();

        documentCard.setStyle(
                "-fx-background-color: "
                        + CARD + ";" +
                "-fx-border-color: "
                        + CARD_BORDER + ";" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
        );

        // =====================================================
        // TABLE HEADER
        // =====================================================

        HBox tableHeader =
                createTableHeader();

        HBox separator =
                createSeparator();

        // =====================================================
        // DOCUMENT LIST
        // =====================================================

        VBox documentList =
                new VBox();

        documentCard.getChildren().addAll(
                tableHeader,
                separator,
                documentList
        );

        main.getChildren().addAll(
                header,
                documentCard
        );

        // =====================================================
        // START REALTIME FIRESTORE
        // =====================================================

        startRealtimeListener(
                documentList
        );

        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane();

        scrollPane.setContent(
                main
        );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";" +
                "-fx-border-color: transparent;"
        );

        return scrollPane;
    }

    // =========================================================
    // REALTIME FIRESTORE LISTENER
    // =========================================================

    private void startRealtimeListener(
            VBox documentList) {

        // -----------------------------------------------------
        // CURRENT LOGGED-IN USER
        // -----------------------------------------------------

        User currentUser =
                SessionManager.getCurrentUser();

        if (currentUser == null) {

            System.out.println(
                    "[MyDocuments] "
                            + "No logged-in user."
            );

            showEmptyMessage(
                    documentList,
                    "Please login first."
            );

            return;
        }

        String userId =
                currentUser.getUserId();

        if (userId == null
                || userId.trim().isEmpty()) {

            System.out.println(
                    "[MyDocuments] "
                            + "User ID is empty."
            );

            showEmptyMessage(
                    documentList,
                    "User information not available."
            );

            return;
        }

        System.out.println(
                "[MyDocuments] Logged-in UID: "
                        + userId
        );

        // -----------------------------------------------------
        // REMOVE OLD LISTENER
        // -----------------------------------------------------

        stopRealtimeListener();

        // -----------------------------------------------------
        // FIRESTORE QUERY
        // -----------------------------------------------------

        Query query =
                db.collection("Documents")
                        .whereEqualTo(
                                "ownerId",
                                userId
                        );

        // -----------------------------------------------------
        // REALTIME LISTENER
        // -----------------------------------------------------

        listenerRegistration =
                query.addSnapshotListener(
                        (snapshots, error) -> {

                            // -------------------------------
                            // ERROR
                            // -------------------------------

                            if (error != null) {

                                System.err.println(
                                        "[MyDocuments] "
                                                + "Firestore error:"
                                );

                                error.printStackTrace();

                                return;
                            }

                            if (snapshots == null) {
                                return;
                            }

                            System.out.println(
                                    "[MyDocuments] "
                                            + "Documents found: "
                                            + snapshots.size()
                            );

                            Platform.runLater(
                                    () -> {

                                        documentList
                                                .getChildren()
                                                .clear();

                                        // -----------------------
                                        // NO DOCUMENTS
                                        // -----------------------

                                        if (snapshots.isEmpty()) {

                                            showEmptyMessage(
                                                    documentList,
                                                    "No documents uploaded yet."
                                            );

                                            return;
                                        }

                                        // -----------------------
                                        // ADD DOCUMENTS
                                        // -----------------------

                                        for (
                                                com.google.cloud.firestore.DocumentSnapshot snapshot
                                                : snapshots.getDocuments()
                                        ) {

                                            try {

                                                Document document =
                                                        snapshot.toObject(
                                                                Document.class
                                                        );

                                                if (document == null) {
                                                    continue;
                                                }

                                                String fileName =
                                                        getSafeValue(
                                                                document.getFileName(),
                                                                "Unnamed document"
                                                        );

                                                String status =
                                                        getSafeValue(
                                                                document.getStatus(),
                                                                "Pending"
                                                        );

                                                String date =
                                                        getSafeValue(
                                                                document.getUploadedAt(),
                                                                "Unknown"
                                                        );

                                                /*
                                                 * Purpose is not present
                                                 * in current Document model.
                                                 */
                                                String purpose =
                                                        "—";

                                                VBox row =
                                                        createDocumentRow(
                                                                fileName,
                                                                purpose,
                                                                status,
                                                                date
                                                        );

                                                documentList
                                                        .getChildren()
                                                        .add(row);

                                            } catch (Exception e) {

                                                System.err.println(
                                                        "[MyDocuments] "
                                                                + "Error reading document:"
                                                );

                                                e.printStackTrace();
                                            }
                                        }
                                    }
                            );
                        }
                );
    }

    // =========================================================
    // EMPTY MESSAGE
    // =========================================================

    private void showEmptyMessage(
            VBox documentList,
            String message) {

        Platform.runLater(
                () -> {

                    documentList
                            .getChildren()
                            .clear();

                    Label empty =
                            new Label(
                                    message
                            );

                    empty.setStyle(
                            "-fx-text-fill: "
                                    + SECONDARY_TEXT + ";" +
                            "-fx-font-size: 14px;" +
                            "-fx-padding: 30px;"
                    );

                    documentList
                            .getChildren()
                            .add(
                                    empty
                            );
                }
        );
    }

    // =========================================================
    // SAFE VALUE
    // =========================================================

    private String getSafeValue(
            String value,
            String defaultValue) {

        if (value == null
                || value.trim().isEmpty()) {

            return defaultValue;
        }

        return value;
    }

    // =========================================================
    // TABLE HEADER
    // =========================================================

    private HBox createTableHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPadding(
                new Insets(
                        20,
                        25,
                        16,
                        25
                )
        );

        Label document =
                createHeaderLabel(
                        "DOCUMENT"
                );

        Label purpose =
                createHeaderLabel(
                        "PURPOSE"
                );

        Label status =
                createHeaderLabel(
                        "STATUS"
                );

        Label date =
                createHeaderLabel(
                        "DATE"
                );

        Label actions =
                createHeaderLabel(
                        "ACTIONS"
                );

        document.setPrefWidth(240);
        purpose.setPrefWidth(125);
        status.setPrefWidth(135);
        date.setPrefWidth(155);
        actions.setPrefWidth(100);

        header.getChildren().addAll(
                document,
                purpose,
                status,
                date,
                actions
        );

        return header;
    }

    // =========================================================
    // HEADER LABEL
    // =========================================================

    private Label createHeaderLabel(
            String text) {

        Label label =
                new Label(text);

        label.setStyle(
                "-fx-text-fill: #817990;" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // DOCUMENT ROW
    // =========================================================

    private VBox createDocumentRow(
            String fileName,
            String purpose,
            String status,
            String date) {

        VBox container =
                new VBox();

        HBox row =
                new HBox();

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        row.setPadding(
                new Insets(
                        18,
                        25,
                        18,
                        25
                )
        );

        // =====================================================
        // DOCUMENT
        // =====================================================

        HBox documentBox =
                new HBox(13);

        documentBox.setAlignment(
                Pos.CENTER_LEFT
        );

        documentBox.setPrefWidth(
                240
        );

        StackPane documentIcon =
                createDocumentIcon();

        Label fileLabel =
                new Label(
                        fileName
                );

        fileLabel.setWrapText(
                true
        );

        fileLabel.setMaxWidth(
                170
        );

        fileLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        documentBox.getChildren().addAll(
                documentIcon,
                fileLabel
        );

        // =====================================================
        // PURPOSE
        // =====================================================

        Label purposeLabel =
                new Label(
                        purpose
                );

        purposeLabel.setPrefWidth(
                125
        );

        purposeLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );

        // =====================================================
        // STATUS
        // =====================================================

        StackPane statusBadge =
                createStatusBadge(
                        status
                );

        statusBadge.setPrefWidth(
                135
        );

        statusBadge.setAlignment(
                Pos.CENTER_LEFT
        );

        // =====================================================
        // DATE
        // =====================================================

        Label dateLabel =
                new Label(
                        date
                );

        dateLabel.setPrefWidth(
                155
        );

        dateLabel.setWrapText(
                true
        );

        dateLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );

        // =====================================================
        // ACTIONS
        // =====================================================

        VBox actions =
                createActions(
                        fileName
                );

        actions.setPrefWidth(
                100
        );

        // =====================================================
        // ADD COLUMNS
        // =====================================================

        row.getChildren().addAll(
                documentBox,
                purposeLabel,
                statusBadge,
                dateLabel,
                actions
        );

        // =====================================================
        // HOVER
        // =====================================================

        row.setOnMouseEntered(
                event -> {

                    container.setStyle(
                            "-fx-background-color: #1B1828;"
                    );
                }
        );

        row.setOnMouseExited(
                event -> {

                    container.setStyle(
                            "-fx-background-color: transparent;"
                    );
                }
        );

        container.getChildren().add(
                row
        );

        container.getChildren().add(
                createSeparator()
        );

        return container;
    }

    // =========================================================
    // DOCUMENT ICON
    // =========================================================

    private StackPane createDocumentIcon() {

        StackPane iconBox =
                new StackPane();

        iconBox.setPrefSize(
                42,
                42
        );

        iconBox.setMinSize(
                42,
                42
        );

        iconBox.setMaxSize(
                42,
                42
        );

        Circle circle =
                new Circle(
                        21,
                        Color.web(
                                "#211A2E"
                        )
                );

        Label icon =
                new Label(
                        "▤"
                );

        icon.setStyle(
                "-fx-text-fill: "
                        + LIGHT_PURPLE + ";" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;"
        );

        iconBox.getChildren().addAll(
                circle,
                icon
        );

        return iconBox;
    }

    // =========================================================
    // STATUS BADGE
    // =========================================================

    private StackPane createStatusBadge(
            String status) {

        StackPane badge =
                new StackPane();

        Label label =
                new Label(
                        status
                );

        String background;
        String border;
        String textColor;

        switch (
                status.toLowerCase()
        ) {

            case "active":

                background =
                        "#132E2A";

                border =
                        "#1F6B60";

                textColor =
                        "#5EEAD4";

                break;

            case "completed":

                background =
                        "#211A2E";

                border =
                        "#6D28D9";

                textColor =
                        "#A78BFA";

                break;

            case "pending":

                background =
                        "#352B1C";

                border =
                        "#6D572B";

                textColor =
                        "#C9A95A";

                break;

            case "expired":

                background =
                        "#1E1B25";

                border =
                        "#3A3545";

                textColor =
                        "#9D96A8";

                break;

            default:

                background =
                        "#211A2E";

                border =
                        PURPLE;

                textColor =
                        LIGHT_PURPLE;

                break;
        }

        label.setStyle(
                "-fx-background-color: "
                        + background + ";" +
                "-fx-border-color: "
                        + border + ";" +
                "-fx-border-radius: 15;" +
                "-fx-background-radius: 15;" +
                "-fx-text-fill: "
                        + textColor + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 5 12 5 12;"
        );

        badge.setAlignment(
                Pos.CENTER_LEFT
        );

        badge.getChildren().add(
                label
        );

        return badge;
    }

    // =========================================================
    // ACTIONS
    // =========================================================

    private VBox createActions(
            String fileName) {

        VBox actions =
                new VBox(7);

        actions.setAlignment(
                Pos.CENTER_LEFT
        );

        Label view =
                new Label(
                        "View"
                );

        Label share =
                new Label(
                        "Share"
                );

        setActionStyle(
                view,
                false
        );

        setActionStyle(
                share,
                false
        );

        view.setOnMouseEntered(
                event ->
                        setActionStyle(
                                view,
                                true
                        )
        );

        view.setOnMouseExited(
                event ->
                        setActionStyle(
                                view,
                                false
                        )
        );

        share.setOnMouseEntered(
                event ->
                        setActionStyle(
                                share,
                                true
                        )
        );

        share.setOnMouseExited(
                event ->
                        setActionStyle(
                                share,
                                false
                        )
        );

        view.setOnMouseClicked(
                event -> {

                    System.out.println(
                            "View document: "
                                    + fileName
                    );
                }
        );

        share.setOnMouseClicked(
                event -> {

                    System.out.println(
                            "Share document: "
                                    + fileName
                    );
                }
        );

        actions.getChildren().addAll(
                view,
                share
        );

        return actions;
    }

    // =========================================================
    // ACTION STYLE
    // =========================================================

    private void setActionStyle(
            Label label,
            boolean hover) {

        if (hover) {

            label.setStyle(
                    "-fx-text-fill: "
                            + LIGHT_PURPLE + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
            );

        } else {

            label.setStyle(
                    "-fx-text-fill: "
                            + TEXT + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;"
            );
        }
    }

    // =========================================================
    // SEPARATOR
    // =========================================================

    private HBox createSeparator() {

        HBox separator =
                new HBox();

        separator.setPrefHeight(
                1
        );

        separator.setMaxHeight(
                1
        );

        separator.setStyle(
                "-fx-background-color: "
                        + CARD_BORDER + ";"
        );

        return separator;
    }

    // =========================================================
    // STOP REALTIME LISTENER
    // =========================================================

    public void stopRealtimeListener() {

        if (listenerRegistration != null) {

            listenerRegistration.remove();

            listenerRegistration = null;

            System.out.println(
                    "[MyDocuments] "
                            + "Realtime listener stopped."
            );
        }
    }

    // =========================================================
    // DISPOSE
    // =========================================================

    public void dispose() {

        stopRealtimeListener();
    }
}