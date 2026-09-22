package com.myprivora.view.auth;

import com.myprivora.view.admin.AdminDashboard;
import com.myprivora.view.landing.HomePage;
import com.myprivora.view.theme.ClayTheme;
import com.myprivora.view.user.UserDashboard;
import com.myprivora.view.xerox.XeroxDashboard;
import com.myprivora.controller.LoginController;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class LoginPage {

    private Scene loginScene;

    // Selected role card
    private VBox selectedCard;

    // LOGIN SCENE
    public Scene getLoginScene(Runnable rhp) {

        LoginController loginController = new LoginController();

        // MAIN ROOT
        BorderPane root = new BorderPane();
        root.setStyle(ClayTheme.MAIN_BACKGROUND);

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 32, 14, 32));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle(
                "-fx-background-color: rgba(8, 12, 22, 0.70);" +
                "-fx-border-color: rgba(255, 255, 255, 0.08);" +
                "-fx-border-width: 0 0 1px 0;"
        );

        // BRAND
        HBox brand = ClayTheme.createBrandHeader(() -> {
            if (rhp != null) rhp.run();
        });

        // CREATE ACCOUNT BUTTON
        Button createAccount = createSimpleButton("Create account");
        createAccount.setOnAction(e -> {
            Runnable rlp = this::backtologin;
            RegisterPage obj1 = new RegisterPage();
            HomePage.homeStage.setScene(obj1.getRegisterScene(rlp));
        });

        HBox.setHgrow(brand, Priority.ALWAYS);
        topBar.getChildren().addAll(brand, createAccount);

        // LOGIN CARD
        VBox loginCard = new VBox();
        loginCard.setPadding(new Insets(34));
        loginCard.setSpacing(16);
        loginCard.setMaxWidth(460);
        loginCard.setStyle(ClayTheme.GLASS_CARD);

        // Animated Shield / Vault Lottie
        ImageView vaultAnim = ClayTheme.createImageViewSafe("/assets/animations/shield_vault.gif", 64, 64);
        StackPane iconPane = new StackPane(vaultAnim);
        iconPane.setPadding(new Insets(0, 0, 8, 0));

        // WELCOME TEXT
        Label welcome = new Label("Welcome back");
        welcome.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Sign in to your PRIVORA zero-trust console");
        subtitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 13px;"
        );

        VBox headingBox = new VBox(5, iconPane, welcome, subtitle);
        headingBox.setAlignment(Pos.CENTER);

        // ROLE SELECTOR CARDS
        VBox userCard = createRoleCard(
                "♙",
                "User",
                "Upload & share\ndocuments"
        );

        VBox xeroxCard = createRoleCard(
                "▥",
                "Xerox Centre",
                "Handle secure\nprint jobs"
        );

        VBox adminCard = createRoleCard(
                "♢",
                "Admin",
                "Manage platform\ntelemetry"
        );

        HBox roleBox = new HBox(12, userCard, xeroxCard, adminCard);
        roleBox.setAlignment(Pos.CENTER);

        final String[] selectedRole = { "User" };

        selectRoleCard(userCard, xeroxCard, adminCard);

        Button signInButton = createBlueButton("Sign in as User");

        userCard.setOnMouseClicked(e -> {
            selectedRole[0] = "User";
            selectRoleCard(userCard, xeroxCard, adminCard);
            signInButton.setText("Sign in as User");
        });

        xeroxCard.setOnMouseClicked(e -> {
            selectedRole[0] = "Xerox";
            selectRoleCard(xeroxCard, userCard, adminCard);
            signInButton.setText("Sign in as Xerox Centre");
        });

        adminCard.setOnMouseClicked(e -> {
            selectedRole[0] = "Admin";
            selectRoleCard(adminCard, userCard, xeroxCard);
            signInButton.setText("Sign in as Admin");
        });

        // EMAIL INPUT
        Label emailLabel = new Label("Email Address");
        emailLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        TextField emailField = new TextField();
        emailField.setPromptText("name@example.com");
        emailField.setStyle(ClayTheme.GLASS_INPUT);

        // PASSWORD INPUT
        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("••••••••••••");
        passwordField.setStyle(ClayTheme.GLASS_INPUT);

        Label loginMessage = new Label();
        loginMessage.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        // SIGN IN ACTION
        signInButton.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String role = selectedRole[0];

            if (email.isEmpty()) {
                loginMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                loginMessage.setText("Please enter your email.");
                return;
            }

            if (password.isEmpty()) {
                loginMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                loginMessage.setText("Please enter your password.");
                return;
            }

            // Show loading state
            signInButton.setDisable(true);
            signInButton.setText("Authenticating...");
            signInButton.setGraphic(ClayTheme.createLoader());
            loginMessage.setText("");

            javafx.concurrent.Task<Boolean> loginTask = new javafx.concurrent.Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return loginController.login(email, password, role);
                }
            };

            loginTask.setOnSucceeded(event -> {
                boolean loginSuccess = loginTask.getValue();
                signInButton.setDisable(false);
                signInButton.setText("Sign in");
                signInButton.setGraphic(null);

                if (loginSuccess) {
                    loginMessage.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 12px;");
                    loginMessage.setText("Authentication successful!");

                    if (role.equals("User")) {
                        Runnable rd = this::backtologin;
                        UserDashboard userDashboard = new UserDashboard();
                        HomePage.homeStage.setScene(userDashboard.getUserDashboardScene(rd));
                    } else if (role.equals("Xerox")) {
                        Runnable rd = this::backtologin;
                        XeroxDashboard xeroxDashboard = new XeroxDashboard();
                        HomePage.homeStage.setScene(xeroxDashboard.getXeroxDashboardScene(rd));
                    } else if (role.equals("Admin")) {
                        Runnable rd = this::backtologin;
                        AdminDashboard adminDashboard = new AdminDashboard();
                        HomePage.homeStage.setScene(adminDashboard.getAdminDashboardScene(rd));
                    }
                } else {
                    loginMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                    loginMessage.setText("Invalid credentials or unverified role.");
                }
            });

            loginTask.setOnFailed(event -> {
                signInButton.setDisable(false);
                signInButton.setText("Sign in");
                signInButton.setGraphic(null);
                loginMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                loginMessage.setText("Network or server error.");
            });

            new Thread(loginTask).start();
        });

        Button createLinkButton = createLinkButton("New here? Create an account");
        createLinkButton.setOnAction(e -> {
            Runnable rlp = this::backtologin;
            RegisterPage registerPage = new RegisterPage();
            HomePage.homeStage.setScene(registerPage.getRegisterScene(rlp));
        });

        loginCard.getChildren().addAll(
                headingBox,
                roleBox,
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                loginMessage,
                signInButton,
                createLinkButton
        );

        StackPane centerBox = new StackPane(loginCard);
        centerBox.setPadding(new Insets(40));
        centerBox.setAlignment(Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        root.setTop(topBar);
        root.setCenter(scrollPane);

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        loginScene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        try {
            loginScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        ClayTheme.fadeInSlide(loginCard, 25);
        return loginScene;
    }

    private VBox createRoleCard(String icon, String title, String description) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-background-color: " + ClayTheme.CYAN_TINT + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-padding: 8px 12px;" +
                "-fx-font-size: 15px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel = new Label(description);
        descriptionLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 10px;"
        );

        VBox card = new VBox(6, iconLabel, titleLabel, descriptionLabel);
        card.setPadding(new Insets(14));
        card.setPrefWidth(125);
        card.setPrefHeight(110);
        card.setStyle(ClayTheme.GLASS_CARD);
        card.setCursor(Cursor.HAND);

        card.setOnMouseEntered(e -> {
            if (card == selectedCard) return;
            card.setStyle(ClayTheme.GLASS_CARD_HOVER);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.04);
            st.setToY(1.04);
            st.play();
        });

        card.setOnMouseExited(e -> {
            if (card == selectedCard) return;
            card.setStyle(ClayTheme.GLASS_CARD);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return card;
    }

    private void selectRoleCard(VBox selected, VBox other1, VBox other2) {
        selectedCard = selected;
        selected.setStyle(ClayTheme.GLASS_CARD_SELECTED);
        other1.setStyle(ClayTheme.GLASS_CARD);
        other2.setStyle(ClayTheme.GLASS_CARD);
    }

    private Button createBlueButton(String text) {
        Button btn = ClayTheme.createPrimaryButton(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }

    private Button createSimpleButton(String text) {
        return ClayTheme.createGlassButton(text);
    }

    private Button createLinkButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;"
        );
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ClayTheme.CYAN_HOVER + ";" +
                "-fx-underline: true;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-underline: false;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        ));
        return button;
    }

    public void backtologin() {
        if (HomePage.homeStage != null && loginScene != null) {
            HomePage.homeStage.setScene(loginScene);
        }
    }
}