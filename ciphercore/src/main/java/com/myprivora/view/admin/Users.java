
package com.myprivora.view.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.dao.UserDAO;
import com.myprivora.model.User;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Admin - User Management
 *
 * Realtime Firestore collection:
 *
 *      Users
 *
 * Features:
 *
 *      - Realtime user list
 *      - Search
 *      - Role filter
 *      - Edit user
 *      - Suspend / activate user
 *      - Delete user
 */
import com.myprivora.view.theme.ClayTheme;

public class Users {

    // =========================================================
    // 3-COLOR GLASSMORPHISM THEME (Laser Cyan / Obsidian Glass / Frost White)
    // =========================================================

    private final String BACKGROUND = ClayTheme.OBSIDIAN_DEEP;
    private final String CARD = ClayTheme.OBSIDIAN_GLASS;
    private final String CARD_HOVER = ClayTheme.OBSIDIAN_SURFACE;
    private final String BORDER = ClayTheme.CARD_BORDER_COLOR;

    private final String PURPLE = ClayTheme.CYAN_PRIMARY;
    private final String DEEP_PURPLE = ClayTheme.CYAN_ACCENT;
    private final String LIGHT_PURPLE = ClayTheme.CYAN_LIGHT;

    private final String TEXT = ClayTheme.FROST_WHITE;
    private final String SECONDARY = ClayTheme.FROST_MUTED;


    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore db =
            DatabaseConfig.getFirestore();


    // =========================================================
    // DAO
    // =========================================================

    private final UserDAO userDAO =
            new UserDAO();


    // =========================================================
    // REALTIME LISTENER
    // =========================================================

    private ListenerRegistration usersListener;


    // =========================================================
    // DATA
    // =========================================================

    private final List<UserItem> allUsers =
            new ArrayList<>();


    // =========================================================
    // UI
    // =========================================================

    private VBox usersList;

    private TextField searchField;

    private ComboBox<String> roleFilter;


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox main =
                new VBox(24);

        main.setPadding(
                new Insets(35, 40, 40, 40)
        );

        main.setStyle(
                "-fx-background-color: "
                        + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        Label title =
                new Label(
                        "User management"
                );

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Manage user accounts across PRIVORA."
                );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY + ";" +
                "-fx-font-size: 16px;"
        );


        VBox heading =
                new VBox(5);

        heading.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // MAIN CARD
        // =====================================================

        VBox userCard =
                new VBox(18);

        userCard.setPadding(
                new Insets(25, 25, 20, 25)
        );

        userCard.setMaxWidth(
                Double.MAX_VALUE
        );

        userCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;"
        );


        // =====================================================
        // SEARCH + FILTER
        // =====================================================

        HBox searchRow =
                new HBox(12);

        searchRow.setAlignment(
                Pos.CENTER_LEFT
        );


        searchField =
                new TextField();

        searchField.setPromptText(
                "Search users by name or email..."
        );

        searchField.setStyle(
                "-fx-background-color: #171D2B;" +
                "-fx-background-radius: 22;" +
                "-fx-border-color: #302A43;" +
                "-fx-border-radius: 22;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: #777084;" +
                "-fx-font-size: 13px;" +
                "-fx-padding: 11 18 11 18;"
        );


        HBox.setHgrow(
                searchField,
                Priority.ALWAYS
        );


        // =====================================================
        // ROLE FILTER
        // =====================================================

        roleFilter =
                new ComboBox<>();

        roleFilter.getItems().addAll(
                "All Roles",
                "User",
                "Admin",
                "Centre"
        );

        roleFilter.setValue(
                "All Roles"
        );

        roleFilter.setPrefWidth(
                130
        );

        roleFilter.setStyle(
                "-fx-background-color: #17131F;" +
                "-fx-border-color: #39304D;" +
                "-fx-border-radius: 20;" +
                "-fx-background-radius: 20;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 12px;"
        );


        // =====================================================
        // ADD BUTTON
        // =====================================================

        Button addButton =
                new Button(
                        "+   Add"
                );

        addButton.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", "
                        + DEEP_PURPLE + ");" +
                "-fx-background-radius: 22;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-cursor: hand;"
        );


        addButton.setOnMouseEntered(e ->
                addButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + LIGHT_PURPLE + ", "
                                + PURPLE + ");" +
                        "-fx-background-radius: 22;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
                )
        );


        addButton.setOnMouseExited(e ->
                addButton.setStyle(
                        "-fx-background-color: linear-gradient(to right, "
                                + PURPLE + ", "
                                + DEEP_PURPLE + ");" +
                        "-fx-background-radius: 22;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 20 10 20;" +
                        "-fx-cursor: hand;"
                )
        );


        /*
         * Adding users should normally be done through
         * Firebase Authentication + RegisterController.
         *
         * We therefore do NOT create a Firestore-only user here,
         * because that would create an account without Firebase
         * Authentication credentials.
         */
        addButton.setOnAction(e ->
                showMessage(
                        "Add User",
                        "New users should be created through the "
                                + "PRIVORA registration process."
                )
        );


        searchRow.getChildren().addAll(
                searchField,
                roleFilter,
                addButton
        );


        // =====================================================
        // COLUMN HEADERS
        // =====================================================

        HBox headerRow =
                new HBox();

        headerRow.setPadding(
                new Insets(8, 15, 8, 15)
        );


        Label nameHeader =
                createHeader("NAME");

        Label emailHeader =
                createHeader("EMAIL");

        Label roleHeader =
                createHeader("ROLE");

        Label statusHeader =
                createHeader("STATUS");

        Label actionsHeader =
                createHeader("ACTIONS");


        nameHeader.setPrefWidth(190);
        emailHeader.setPrefWidth(220);
        roleHeader.setPrefWidth(110);
        statusHeader.setPrefWidth(130);
        actionsHeader.setPrefWidth(180);


        headerRow.getChildren().addAll(
                nameHeader,
                emailHeader,
                roleHeader,
                statusHeader,
                actionsHeader
        );


        // =====================================================
        // SEPARATOR
        // =====================================================

        HBox separator =
                new HBox();

        separator.setPrefHeight(1);

        separator.setStyle(
                "-fx-background-color: " + BORDER + ";"
        );


        // =====================================================
        // USER LIST
        // =====================================================

        usersList =
                new VBox();

        usersList.setSpacing(0);


        // =====================================================
        // ADD CARD CONTENT
        // =====================================================

        userCard.getChildren().addAll(
                searchRow,
                headerRow,
                separator,
                usersList
        );


        // =====================================================
        // ADD MAIN CONTENT
        // =====================================================

        main.getChildren().addAll(
                heading,
                userCard
        );


        // =====================================================
        // SEARCH LISTENER
        // =====================================================

        searchField.textProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                refreshUserList()
                );


        // =====================================================
        // FILTER LISTENER
        // =====================================================

        roleFilter.valueProperty()
                .addListener(
                        (observable, oldValue, newValue) ->
                                refreshUserList()
                );


        // =====================================================
        // SCROLL
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(main);

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
                "-fx-background-color: transparent;" +
                "-fx-background: transparent;" +
                "-fx-border-color: transparent;"
        );


        // =====================================================
        // START REALTIME LISTENER
        // =====================================================

        startRealtimeListener();


        return scrollPane;
    }


    // =========================================================
    // FIRESTORE REALTIME LISTENER
    // =========================================================

    private void startRealtimeListener() {

        stopRealtimeListener();


        usersListener =
                db.collection("Users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null) {

                                        System.out.println(
                                                "[Users] "
                                                + "Firestore listener error."
                                        );

                                        error.printStackTrace();

                                        return;
                                    }


                                    if (snapshot == null) {
                                        return;
                                    }


                                    List<UserItem> temp =
                                            new ArrayList<>();


                                    for (DocumentSnapshot document :
                                            snapshot.getDocuments()) {

                                        User user =
                                                document.toObject(
                                                        User.class
                                                );


                                        if (user == null) {
                                            continue;
                                        }


                                        /*
                                         * IMPORTANT:
                                         *
                                         * Firestore document ID is the
                                         * Firebase UID.
                                         *
                                         * User.toObject() does not
                                         * automatically populate it.
                                         */
                                        user.setUserId(
                                                document.getId()
                                        );


                                        String status =
                                                document.getString(
                                                        "status"
                                                );


                                        if (status == null
                                                || status.isBlank()) {

                                            status = "Active";
                                        }


                                        temp.add(
                                                new UserItem(
                                                        user,
                                                        status
                                                )
                                        );
                                    }


                                    Platform.runLater(() -> {

                                        allUsers.clear();

                                        allUsers.addAll(
                                                temp
                                        );

                                        refreshUserList();
                                    });
                                }
                        );
    }


    // =========================================================
    // REFRESH USER LIST
    // =========================================================

    private void refreshUserList() {

        if (usersList == null) {
            return;
        }


        usersList.getChildren().clear();


        String searchText =
                searchField == null
                        ? ""
                        : searchField.getText();


        String selectedRole =
                roleFilter == null
                        ? "All Roles"
                        : roleFilter.getValue();


        if (searchText == null) {
            searchText = "";
        }


        searchText =
                searchText
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );


        int visibleUsers = 0;


        for (UserItem item : allUsers) {

            User user =
                    item.user;


            String name =
                    safe(
                            user.getName()
                    );


            String email =
                    safe(
                            user.getEmail()
                    );


            String role =
                    normalizeRole(
                            user.getRole()
                    );


            boolean matchesSearch =
                    searchText.isBlank()
                            || name.toLowerCase(
                                    Locale.ROOT
                              ).contains(searchText)
                            || email.toLowerCase(
                                    Locale.ROOT
                              ).contains(searchText);


            boolean matchesRole =
                    selectedRole == null
                            || selectedRole.equals(
                                    "All Roles"
                            )
                            || role.equalsIgnoreCase(
                                    selectedRole
                            );


            if (!matchesSearch
                    || !matchesRole) {

                continue;
            }


            usersList.getChildren().add(
                    createUserRow(
                            item
                    )
            );


            visibleUsers++;
        }


        // =====================================================
        // EMPTY STATE
        // =====================================================

        if (visibleUsers == 0) {

            Label empty =
                    new Label(
                            allUsers.isEmpty()
                                    ? "No users found in Firestore."
                                    : "No users match your search."
                    );

            empty.setStyle(
                    "-fx-text-fill: "
                            + SECONDARY + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-padding: 25;"
            );


            usersList.getChildren().add(
                    empty
            );
        }
    }


    // =========================================================
    // CREATE USER ROW
    // =========================================================

    private HBox createUserRow(
            UserItem item) {


        User user =
                item.user;


        String status =
                item.status;


        HBox row =
                new HBox();


        row.setMinHeight(
                80
        );


        row.setAlignment(
                Pos.CENTER_LEFT
        );


        row.setPadding(
                new Insets(10, 15, 10, 15)
        );


        row.setStyle(
                "-fx-background-color: transparent;"
        );


        // =====================================================
        // AVATAR
        // =====================================================

        Label avatar =
                new Label(
                        createInitials(
                                user.getName(),
                                user.getEmail()
                        )
                );


        avatar.setAlignment(
                Pos.CENTER
        );


        avatar.setMinSize(
                42,
                42
        );


        avatar.setMaxSize(
                42,
                42
        );


        avatar.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, "
                        + PURPLE + ", #3B82F6);" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // NAME
        // =====================================================

        String name =
                safe(user.getName());


        if (name.isBlank()) {
            name = "Unnamed User";
        }


        Label nameLabel =
                new Label(name);


        nameLabel.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        VBox nameBox =
                new VBox(
                        nameLabel
                );


        HBox nameContainer =
                new HBox(
                        12,
                        avatar,
                        nameBox
                );


        nameContainer.setAlignment(
                Pos.CENTER_LEFT
        );


        nameContainer.setPrefWidth(
                190
        );


        // =====================================================
        // EMAIL
        // =====================================================

        Label emailLabel =
                new Label(
                        safe(user.getEmail())
                );


        emailLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY + ";" +
                "-fx-font-size: 12px;"
        );


        emailLabel.setPrefWidth(
                220
        );


        // =====================================================
        // ROLE
        // =====================================================

        String role =
                normalizeRole(
                        user.getRole()
                );


        Label roleLabel =
                new Label(role);


        roleLabel.setAlignment(
                Pos.CENTER
        );


        roleLabel.setStyle(
                "-fx-background-color: #211A30;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: #C9B9E8;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 6 12;"
        );


        HBox roleBox =
                new HBox(
                        roleLabel
                );


        roleBox.setPrefWidth(
                110
        );


        roleBox.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // STATUS
        // =====================================================

        Label statusLabel =
                createStatusLabel(
                        status
                );


        HBox statusBox =
                new HBox(
                        statusLabel
                );


        statusBox.setPrefWidth(
                130
        );


        statusBox.setAlignment(
                Pos.CENTER_LEFT
        );


        // =====================================================
        // ACTION BUTTONS
        // =====================================================

        Button editButton =
                new Button("✎");


        Button suspendButton =
                new Button(
                        isActive(status)
                                ? "⊘"
                                : "✓"
                );


        Button deleteButton =
                new Button("⌫");


        styleActionButton(
                editButton,
                LIGHT_PURPLE
        );


        styleActionButton(
                suspendButton,
                "#C5B8D5"
        );


        styleActionButton(
                deleteButton,
                "#EF6B73"
        );


        // =====================================================
        // EDIT
        // =====================================================

        editButton.setOnAction(
                e -> showEditDialog(item)
        );


        // =====================================================
        // SUSPEND / ACTIVATE
        // =====================================================

        suspendButton.setOnAction(
                e -> toggleUserStatus(item)
        );


        // =====================================================
        // DELETE
        // =====================================================

        deleteButton.setOnAction(
                e -> deleteUser(item)
        );


        HBox actions =
                new HBox(
                        8,
                        editButton,
                        suspendButton,
                        deleteButton
                );


        actions.setAlignment(
                Pos.CENTER_LEFT
        );


        actions.setPrefWidth(
                180
        );


        // =====================================================
        // ADD ROW
        // =====================================================

        row.getChildren().addAll(
                nameContainer,
                emailLabel,
                roleBox,
                statusBox,
                actions
        );


        // =====================================================
        // HOVER
        // =====================================================

        row.setOnMouseEntered(e ->
                row.setStyle(
                        "-fx-background-color: "
                                + CARD_HOVER + ";" +
                        "-fx-background-radius: 12;"
                )
        );


        row.setOnMouseExited(e ->
                row.setStyle(
                        "-fx-background-color: transparent;"
                )
        );


        return row;
    }


    // =========================================================
    // STATUS LABEL
    // =========================================================

    private Label createStatusLabel(
            String status) {


        Label label =
                new Label(status);


        if (isActive(status)) {

            label.setStyle(
                    "-fx-background-color: #17352F;" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: #4ADE80;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 6 12;"
            );

        } else if (
                status.equalsIgnoreCase("Pending")) {

            label.setStyle(
                    "-fx-background-color: #3A3020;" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: #FBBF24;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 6 12;"
            );

        } else if (
                status.equalsIgnoreCase("Suspended")) {

            label.setStyle(
                    "-fx-background-color: #382329;" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: #FB7185;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 6 12;"
            );

        } else {

            label.setStyle(
                    "-fx-background-color: #282735;" +
                    "-fx-background-radius: 14;" +
                    "-fx-text-fill: #9CA3AF;" +
                    "-fx-font-size: 10px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 6 12;"
            );
        }


        return label;
    }


    // =========================================================
    // TOGGLE USER STATUS
    // =========================================================

    private void toggleUserStatus(
            UserItem item) {


        User user =
                item.user;


        String currentStatus =
                item.status;


        String newStatus =
                isActive(currentStatus)
                        ? "Suspended"
                        : "Active";


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                newStatus.equals("Suspended")
                        ? "Suspend User"
                        : "Activate User"
        );


        confirmation.setHeaderText(
                null
        );


        confirmation.setContentText(
                newStatus.equals("Suspended")
                        ? "Suspend " + user.getName() + "?"
                        : "Activate " + user.getName() + "?"
        );


        confirmation.showAndWait()
                .ifPresent(
                        result -> {

                            if (result != ButtonType.OK) {
                                return;
                            }


                            try {

                                db.collection("Users")
                                        .document(
                                                user.getUserId()
                                        )
                                        .update(
                                                "status",
                                                newStatus
                                        )
                                        .get();


                                System.out.println(
                                        "[Users] User status changed to "
                                                + newStatus
                                );

                            } catch (Exception ex) {

                                ex.printStackTrace();

                                showError(
                                        "Unable to update user status.",
                                        ex.getMessage()
                                );
                            }
                        }
                );
    }


    // =========================================================
    // DELETE USER
    // =========================================================

    private void deleteUser(
            UserItem item) {


        User user =
                item.user;


        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete User"
        );


        confirmation.setHeaderText(
                null
        );


        confirmation.setContentText(
                "Delete "
                        + user.getName()
                        + " permanently from Firestore?"
        );


        confirmation.showAndWait()
                .ifPresent(
                        result -> {

                            if (result != ButtonType.OK) {
                                return;
                            }


                            try {

                                userDAO.deleteUser(
                                        user.getUserId()
                                );


                                System.out.println(
                                        "[Users] User deleted: "
                                                + user.getUserId()
                                );

                            } catch (Exception ex) {

                                ex.printStackTrace();

                                showError(
                                        "Unable to delete user.",
                                        ex.getMessage()
                                );
                            }
                        }
                );
    }


    // =========================================================
    // EDIT USER
    // =========================================================

    private void showEditDialog(
            UserItem item) {


        User user =
                item.user;


        Dialog<ButtonType> dialog =
                new Dialog<>();


        dialog.setTitle(
                "Edit User"
        );


        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        ButtonType.OK,
                        ButtonType.CANCEL
                );


        VBox content =
                new VBox(12);


        content.setPadding(
                new Insets(20)
        );


        TextField nameField =
                new TextField(
                        safe(user.getName())
                );


        nameField.setPromptText(
                "Name"
        );


        TextField mobileField =
                new TextField(
                        safe(user.getMobile())
                );


        mobileField.setPromptText(
                "Mobile"
        );


        ComboBox<String> roleBox =
                new ComboBox<>();


        roleBox.getItems().addAll(
                "User",
                "Admin",
                "Centre"
        );


        String currentRole =
                normalizeRole(
                        user.getRole()
                );


        if (!roleBox.getItems()
                .contains(currentRole)) {

            roleBox.getItems().add(
                    currentRole
            );
        }


        roleBox.setValue(
                currentRole
        );


        content.getChildren().addAll(
                createDialogLabel("Name"),
                nameField,
                createDialogLabel("Mobile"),
                mobileField,
                createDialogLabel("Role"),
                roleBox
        );


        dialog.getDialogPane()
                .setContent(
                        content
                );


        dialog.showAndWait()
                .ifPresent(
                        result -> {

                            if (result != ButtonType.OK) {
                                return;
                            }


                            String newName =
                                    nameField.getText()
                                            .trim();


                            String newMobile =
                                    mobileField.getText()
                                            .trim();


                            String newRole =
                                    roleBox.getValue();


                            if (newName.isBlank()) {

                                showError(
                                        "Name cannot be empty.",
                                        null
                                );

                                return;
                            }


                            try {

                                db.collection("Users")
                                        .document(
                                                user.getUserId()
                                        )
                                        .update(
                                                "name",
                                                newName,
                                                "mobile",
                                                newMobile,
                                                "role",
                                                newRole
                                        )
                                        .get();


                                System.out.println(
                                        "[Users] User updated: "
                                                + user.getUserId()
                                );

                            } catch (Exception ex) {

                                ex.printStackTrace();

                                showError(
                                        "Unable to update user.",
                                        ex.getMessage()
                                );
                            }
                        }
                );
    }


    // =========================================================
    // DIALOG LABEL
    // =========================================================

    private Label createDialogLabel(
            String text) {

        Label label =
                new Label(text);


        label.setStyle(
                "-fx-text-fill: #5B5568;" +
                "-fx-font-weight: bold;"
        );


        return label;
    }


    // =========================================================
    // HEADER LABEL
    // =========================================================

    private Label createHeader(
            String text) {

        Label label =
                new Label(text);


        label.setStyle(
                "-fx-text-fill: #81758F;" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;"
        );


        return label;
    }


    // =========================================================
    // ACTION BUTTON
    // =========================================================

    private void styleActionButton(
            Button button,
            String textColor) {


        button.setMinSize(
                32,
                32
        );


        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-font-size: 17px;" +
                "-fx-padding: 4;" +
                "-fx-cursor: hand;"
        );


        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: #241A32;" +
                        "-fx-background-radius: 10;" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-size: 17px;" +
                        "-fx-padding: 4;" +
                        "-fx-cursor: hand;"
                )
        );


        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + textColor + ";" +
                        "-fx-font-size: 17px;" +
                        "-fx-padding: 4;" +
                        "-fx-cursor: hand;"
                )
        );
    }


    // =========================================================
    // INITIALS
    // =========================================================

    private String createInitials(
            String name,
            String email) {


        String value =
                safe(name);


        if (value.isBlank()) {

            value =
                    safe(email);
        }


        if (value.isBlank()) {
            return "?";
        }


        String[] parts =
                value.trim().split("\\s+");


        if (parts.length >= 2) {

            return (
                    parts[0].charAt(0)
                            + ""
                            + parts[1].charAt(0)
            ).toUpperCase(
                    Locale.ROOT
            );
        }


        return value.substring(
                0,
                1
        ).toUpperCase(
                Locale.ROOT
        );
    }


    // =========================================================
    // NORMALIZE ROLE
    // =========================================================

    private String normalizeRole(
            String role) {


        if (role == null
                || role.isBlank()) {

            return "Unknown";
        }


        if (role.equalsIgnoreCase("USER")) {
            return "User";
        }


        if (role.equalsIgnoreCase("ADMIN")) {
            return "Admin";
        }


        if (role.equalsIgnoreCase("XEROX")
                || role.equalsIgnoreCase("CENTRE")
                || role.equalsIgnoreCase("CENTER")) {

            return "Centre";
        }


        return role;
    }


    // =========================================================
    // ACTIVE STATUS
    // =========================================================

    private boolean isActive(
            String status) {

        return status != null
                && status.equalsIgnoreCase(
                        "Active"
                );
    }


    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }


    // =========================================================
    // INFORMATION MESSAGE
    // =========================================================

    private void showMessage(
            String title,
            String message) {


        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );


        alert.setTitle(
                title
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private void showError(
            String message,
            String details) {


        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "Error"
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                details == null
                        ? message
                        : message
                                + "\n\n"
                                + details
        );


        alert.showAndWait();
    }


    // =========================================================
    // STOP REALTIME LISTENER
    // =========================================================

    public void stopRealtimeListener() {

        if (usersListener != null) {

            usersListener.remove();

            usersListener = null;
        }
    }


    // =========================================================
    // USER ITEM
    // =========================================================

    private static class UserItem {

        private final User user;

        private final String status;


        private UserItem(
                User user,
                String status) {

            this.user = user;

            this.status = status;
        }
    }
}

