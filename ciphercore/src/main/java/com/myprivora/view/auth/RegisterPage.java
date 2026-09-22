package com.myprivora.view.auth;

import com.myprivora.controller.RegisterController;
import com.myprivora.view.landing.HomePage;
import com.myprivora.view.theme.ClayTheme;

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

public class RegisterPage {

    private Scene registerScene;

    // Selected role
    private String selectedRole = "USER";

    // Reference for selected card
    private VBox selectedRoleCardReference;

    public Scene getRegisterScene(Runnable rlp) {

        RegisterController registerController = new RegisterController();

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
        HBox brand = ClayTheme.createBrandHeader(rlp);

        // SIGN IN TOP BUTTON
        Button signInTop = createSimpleButton("Sign in");
        signInTop.setOnAction(e -> rlp.run());

        HBox.setHgrow(brand, Priority.ALWAYS);
        topBar.getChildren().addAll(brand, signInTop);

        // MAIN CONTENT
        VBox content = new VBox();
        content.setSpacing(24);
        content.setPadding(new Insets(35, 40, 50, 40));
        content.setMaxWidth(860);

        // JOIN PRIVORA HERO CARD
        VBox infoCard = new VBox();
        infoCard.setSpacing(14);
        infoCard.setPadding(new Insets(28));
        infoCard.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(14, 22, 42, 0.85), rgba(10, 30, 56, 0.85));" +
                "-fx-background-radius: 20px;" +
                "-fx-border-color: " + ClayTheme.BORDER_ACTIVE + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 20px;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 20, 0.3, 0, 4);"
        );

        HBox infoTop = new HBox(16);
        infoTop.setAlignment(Pos.CENTER_LEFT);

        ImageView vaultAnim = ClayTheme.createImageViewSafe("/assets/animations/shield_vault.gif", 48, 48);

        VBox infoTitleBox = new VBox(4);
        Label joinTitle = new Label("Join PRIVORA");
        joinTitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 24px;" +
                "-fx-font-weight: bold;"
        );

        Label joinDescription = new Label(
                "A privacy-first zero-trust architecture to share sensitive documents with Xerox print shops safely."
        );
        joinDescription.setWrapText(true);
        joinDescription.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 13px;"
        );
        infoTitleBox.getChildren().addAll(joinTitle, joinDescription);
        infoTop.getChildren().addAll(vaultAnim, infoTitleBox);

        Label feature1 = createFeature("End-to-end cryptographic privacy verification");
        Label feature2 = createFeature("Automated PII optical detection & masking");
        Label feature3 = createFeature("Cryptographic session expiry & print limits");
        Label feature4 = createFeature("Hardware signed digital privacy receipts");

        HBox featuresRow = new HBox(20,
                new VBox(6, feature1, feature2),
                new VBox(6, feature3, feature4)
        );

        infoCard.getChildren().addAll(infoTop, featuresRow);

        // REGISTER CARD
        VBox registerCard = new VBox();
        registerCard.setSpacing(14);
        registerCard.setPadding(new Insets(30));
        registerCard.setStyle(ClayTheme.GLASS_CARD);

        Label createTitle = new Label("Create your account");
        createTitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 22px;" +
                "-fx-font-weight: bold;"
        );

        Label createSubtitle = new Label("Takes less than 60 seconds to get secured");
        createSubtitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 12px;"
        );

        VBox heading = new VBox(4, createTitle, createSubtitle);

        // ROLE CARDS
        VBox userCard = createRoleCard("♙", "User", "Share documents securely");
        VBox xeroxCard = createRoleCard("▥", "Xerox Centre", "Handle verified print jobs");

        HBox roleBox = new HBox(14, userCard, xeroxCard);
        roleBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(userCard, Priority.ALWAYS);
        HBox.setHgrow(xeroxCard, Priority.ALWAYS);

        selectRoleCard(userCard, xeroxCard);

        userCard.setOnMouseClicked(e -> {
            selectedRole = "USER";
            selectRoleCard(userCard, xeroxCard);
        });

        xeroxCard.setOnMouseClicked(e -> {
            selectedRole = "XEROX";
            selectRoleCard(xeroxCard, userCard);
        });

        // INPUT FIELDS
        Label fullNameLabel = createInputLabel("Full name");
        TextField fullNameField = createTextField("e.g. John Doe");

        Label emailLabel = createInputLabel("Email Address");
        TextField emailField = createTextField("you@example.com");

        Label mobileLabel = createInputLabel("Mobile Number");
        TextField mobileField = createTextField("+91 98765 43210");

        VBox emailBox = new VBox(6, emailLabel, emailField);
        VBox mobileBox = new VBox(6, mobileLabel, mobileField);

        HBox emailMobileBox = new HBox(14, emailBox, mobileBox);
        HBox.setHgrow(emailBox, Priority.ALWAYS);
        HBox.setHgrow(mobileBox, Priority.ALWAYS);

        Label passwordLabel = createInputLabel("Password");
        PasswordField passwordField = createPasswordField("••••••••••••");

        Label confirmLabel = createInputLabel("Confirm Password");
        PasswordField confirmField = createPasswordField("••••••••••••");

        Label registerMessage = new Label();
        registerMessage.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );

        VBox passwordBox = new VBox(6, passwordLabel, passwordField);
        VBox confirmBox = new VBox(6, confirmLabel, confirmField);

        HBox passwordRow = new HBox(14, passwordBox, confirmBox);
        HBox.setHgrow(passwordBox, Priority.ALWAYS);
        HBox.setHgrow(confirmBox, Priority.ALWAYS);

        Button createAccountButton = createBlueButton("Create Account");

        createAccountButton.setOnAction(e -> {
            String name = fullNameField.getText();
            String email = emailField.getText();
            String mobile = mobileField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmField.getText();
            String role = selectedRole;

            if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                registerMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                registerMessage.setText("Please fill all required fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                registerMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                registerMessage.setText("Passwords do not match.");
                return;
            }

            RegisterController controller = new RegisterController();
            boolean flag = controller.signUp(name, email, mobile, password, role);

            if (flag) {
                registerMessage.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 12px;");
                registerMessage.setText("Account created successfully!");
                rlp.run();
            } else {
                registerMessage.setStyle("-fx-text-fill: #FF6B6B; -fx-font-size: 12px;");
                registerMessage.setText("Registration failed. Please try again.");
            }
        });

        Label alreadyAccount = new Label("Already have an account?");
        alreadyAccount.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 12px;"
        );

        Button bottomSignIn = createLinkButton("Sign in");
        bottomSignIn.setOnAction(e -> rlp.run());

        HBox bottomLogin = new HBox(6, alreadyAccount, bottomSignIn);
        bottomLogin.setAlignment(Pos.CENTER);

        registerCard.getChildren().addAll(
                heading,
                roleBox,
                fullNameLabel,
                fullNameField,
                emailMobileBox,
                passwordRow,
                registerMessage,
                createAccountButton,
                bottomLogin
        );

        content.getChildren().addAll(infoCard, registerCard);

        StackPane centerBox = new StackPane(content);
        centerBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        root.setTop(topBar);
        root.setCenter(scrollPane);

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        registerScene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        try {
            registerScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        HomePage.homeStage.setScene(registerScene);
        HomePage.homeStage.setMaximized(true);

        ClayTheme.fadeInSlide(content, 25);
        return registerScene;
    }

    private Label createFeature(String text) {
        Label label = new Label("◈  " + text);
        label.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 12px;"
        );
        return label;
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
                "-fx-font-size: 11px;"
        );

        VBox card = new VBox(6, iconLabel, titleLabel, descriptionLabel);
        card.setPadding(new Insets(14));
        card.setPrefHeight(105);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle(ClayTheme.GLASS_CARD);
        card.setCursor(Cursor.HAND);

        card.setOnMouseEntered(e -> {
            if (card == getSelectedCard()) return;
            card.setStyle(ClayTheme.GLASS_CARD_HOVER);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });

        card.setOnMouseExited(e -> {
            if (card == getSelectedCard()) {
                card.setStyle(ClayTheme.GLASS_CARD_SELECTED);
            } else {
                card.setStyle(ClayTheme.GLASS_CARD);
            }
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return card;
    }

    private void selectRoleCard(VBox selected, VBox other) {
        selectedRoleCardReference = selected;
        selected.setStyle(ClayTheme.GLASS_CARD_SELECTED);
        other.setStyle(ClayTheme.GLASS_CARD);
    }

    private VBox getSelectedCard() {
        return selectedRoleCardReference;
    }

    private Label createInputLabel(String text) {
        Label label = new Label(text);
        label.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle(ClayTheme.GLASS_INPUT);
        return field;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setStyle(ClayTheme.GLASS_INPUT);
        return field;
    }

    private Button createBlueButton(String text) {
        Button button = ClayTheme.createPrimaryButton(text);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
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
}