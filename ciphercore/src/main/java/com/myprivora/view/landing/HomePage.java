package com.myprivora.view.landing;

import com.myprivora.view.auth.LoginPage;
import com.myprivora.view.theme.ClayTheme;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomePage extends Application {

    public static Stage homeStage;
    private Scene homeScene;

    @Override
    public void start(Stage stage) throws Exception {

        homeStage = stage;

        // =========================================================
        // MAIN ROOT
        // =========================================================

        BorderPane root = new BorderPane();
        root.setStyle(ClayTheme.MAIN_BACKGROUND);

        // Ambient background subtle glow behind main area
        Circle ambientGlow = new Circle(320);
        ambientGlow.setFill(Color.web(ClayTheme.CYAN_PRIMARY, 0.05));
        ambientGlow.setEffect(new javafx.scene.effect.GaussianBlur(160));
        ambientGlow.setMouseTransparent(true);

        StackPane bgStack = new StackPane();
        bgStack.getChildren().add(ambientGlow);
        StackPane.setAlignment(ambientGlow, Pos.TOP_RIGHT);

        // TOP NAVIGATION BAR
        HBox navigationBar = new HBox();
        navigationBar.setPadding(new Insets(14, 32, 14, 32));
        navigationBar.setAlignment(Pos.CENTER_LEFT);
        navigationBar.setSpacing(15);
        navigationBar.setStyle(
                "-fx-background-color: rgba(8, 12, 22, 0.70);" +
                "-fx-border-color: rgba(255, 255, 255, 0.08);" +
                "-fx-border-width: 0 0 1px 0;"
        );

        // BRAND
        HBox brand = ClayTheme.createBrandHeader(this::backtohome);

        // ABOUT US BUTTON
        Button aboutUsBtn = createNavButton("About Us");
                aboutUsBtn.setOnAction(e -> openAboutUs());

        // SIGN IN BUTTON
        Button signInTop = createNavButton("Sign in");
        signInTop.setOnAction(e -> {
            Runnable rhp = this::backtohome;
            LoginPage obj1 = new LoginPage();
            homeStage.setScene(obj1.getLoginScene(rhp));
        });

        HBox rightNavigation = new HBox(15, aboutUsBtn, signInTop);
        rightNavigation.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightNavigation, Priority.ALWAYS);

        navigationBar.getChildren().addAll(brand, rightNavigation);

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox();
        content.setPadding(new Insets(35, 40, 80, 40));
        content.setSpacing(28);

        // BADGE
        Label badge = new Label("◈  ZERO-TRUST DOCUMENT SHARING & SECURE XEROX PRINTING");
        badge.setStyle(ClayTheme.BADGE_CYAN);

        // MAIN HEADING
        Label heading1 = new Label("Share sensitive docs.");
        heading1.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 48px;" +
                "-fx-font-weight: bold;"
        );

        Label heading2 = new Label("Set the rules.");
        heading2.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 48px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 22, 0.4, 0, 0);"
        );

        Label heading3 = new Label("Stay in control.");
        heading3.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 48px;" +
                "-fx-font-weight: bold;"
        );

        HBox heading = new HBox(12, heading1, heading2, heading3);
        heading.setAlignment(Pos.CENTER_LEFT);

        // DESCRIPTION
        Label description = new Label(
                "PRIVORA scans, redacts and locks your documents with cryptographic limits,\n" +
                "print caps and watermarks — so Xerox print shops see exactly what you permit, and nothing more."
        );
        description.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 16px;" +
                "-fx-line-spacing: 6px;"
        );

        // LOGIN BUTTON
        Button loginButton = createBlueButton("Launch Application  →");
        loginButton.setOnAction(e -> {
            Runnable rhp = this::backtohome;
            LoginPage obj1 = new LoginPage();
            homeStage.setScene(obj1.getLoginScene(rhp));
        });

        HBox actionButtons = new HBox(loginButton);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        // STATISTICS CARDS
        VBox documentsCard = createStatCard("10k+", "Documents\nsecured");
        VBox centresCard = createStatCard("500+", "Xerox centres\nverified");
        VBox privacyCard = createStatCard("99.9%", "Zero-leakage\nprivacy score");

        HBox statistics = new HBox(18, documentsCard, centresCard, privacyCard);
        statistics.setAlignment(Pos.CENTER_LEFT);

        // =========================================================
        // SECURE SESSION HERO CARD WITH LOTTIE SCAN ANIMATION
        // =========================================================

        VBox secureSession = new VBox();
        secureSession.setPadding(new Insets(26));
        secureSession.setSpacing(20);
        secureSession.setStyle(ClayTheme.GLASS_CARD);

        VBox sessionBox = new VBox();
        sessionBox.setPadding(new Insets(24));
        sessionBox.setSpacing(16);
        sessionBox.setStyle(
                "-fx-background-color: linear-gradient(to right, rgba(14, 23, 44, 0.85), rgba(8, 28, 54, 0.85), rgba(0, 150, 180, 0.45));" +
                "-fx-background-radius: 18px;" +
                "-fx-border-color: " + ClayTheme.BORDER_ACTIVE + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 18px;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 25, 0.35, 0, 4);"
        );

        HBox sessionTop = new HBox(16);
        sessionTop.setAlignment(Pos.CENTER_LEFT);

        // Embedded Lottie Animated Scanner
        ImageView scanAnim = ClayTheme.createImageViewSafe("/assets/animations/laser_scan.gif", 52, 52);
        StackPane scanBox = new StackPane(scanAnim);
        scanBox.setPrefSize(56, 56);
        scanBox.setStyle(
                "-fx-background-color: rgba(0, 240, 255, 0.12);" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-border-radius: 14px;"
        );

        VBox sessionText = new VBox(3);
        Label secureText = new Label("Live Encrypted Print Session");
        secureText.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 13px; -fx-font-weight: bold;");

        Label fileName = new Label("Aadhaar-Card-Redacted.pdf");
        fileName.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        sessionText.getChildren().addAll(secureText, fileName);
        HBox.setHgrow(sessionText, Priority.ALWAYS);

        Label liveLabel = new Label("● LIVE ACTIVE");
        liveLabel.setStyle(
                "-fx-background-color: " + ClayTheme.CYAN_TINT_STRONG + ";" +
                "-fx-background-radius: 15px;" +
                "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-border-radius: 15px;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-padding: 6 14 6 14;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );
        ClayTheme.pulseGlow(liveLabel);

        sessionTop.getChildren().addAll(scanBox, sessionText, liveLabel);

        Label printLabel = createGlassSessionPill("Print Cap: 2/3");
        Label timeLabel = createGlassSessionPill("Expiry: 12:44 left");
        Label watermarkLabel = createGlassSessionPill("Watermarked & Restricted");

        HBox sessionInfo = new HBox(12, printLabel, timeLabel, watermarkLabel);
        HBox.setHgrow(printLabel, Priority.ALWAYS);
        HBox.setHgrow(timeLabel, Priority.ALWAYS);
        HBox.setHgrow(watermarkLabel, Priority.ALWAYS);

        sessionBox.getChildren().addAll(sessionTop, sessionInfo);

        VBox aiCard = createFeatureCard(
                "◈",
                "AI Optical Privacy Scan",
                "Auto-detects and masks Aadhaar, PAN, phone numbers, and signatures before printing."
        );

        VBox ruleCard = createFeatureCard(
                "♢",
                "Cryptographic Xerox Access",
                "Hardware-enforced print caps, auto-destruct timers, and tamper-proof watermarking."
        );

        HBox featureCards = new HBox(16, aiCard, ruleCard);
        HBox.setHgrow(aiCard, Priority.ALWAYS);
        HBox.setHgrow(ruleCard, Priority.ALWAYS);

        secureSession.getChildren().addAll(sessionBox, featureCards);

        // PRIVACY SCORE BADGE
        Label privacyScore = new Label("Zero-Trust Privacy Score · 98.6% Secure");
        privacyScore.setStyle(
                "-fx-background-color: rgba(0, 240, 255, 0.15);" +
                "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-border-radius: 20px;" +
                "-fx-background-radius: 20px;" +
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-padding: 10 20 10 20;" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 16, 0.35, 0, 0);"
        );

        StackPane scoreContainer = new StackPane(privacyScore);
        scoreContainer.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(
                badge,
                heading,
                description,
                actionButtons,
                statistics,
                secureSession,
                scoreContainer
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        bgStack.getChildren().add(scrollPane);

        root.setTop(navigationBar);
        root.setCenter(bgStack);

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        homeScene = new Scene(
                root,
                bounds.getWidth(),
                bounds.getHeight()
        );

        try {
            homeScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        stage.setTitle("PRIVORA - Zero-Trust Document Sharing & Secure Printing");
        stage.setScene(homeScene);
        homeStage.setResizable(false);
        stage.show();

        // Smooth Page Entrance
        ClayTheme.fadeInSlide(content, 35);
    }

    private Button createBlueButton(String text) {
        return ClayTheme.createPrimaryButton(text);
    }

    private Button createNavButton(String text) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + ClayTheme.CYAN_TINT + ";" +
                    "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                    "-fx-border-radius: 14px;" +
                    "-fx-background-radius: 14px;" +
                    "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 8 16 8 16;" +
                    "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 15, 0.4, 0, 0);"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 8 16 8 16;"
            );
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        return button;
    }

    private Label createGlassSessionPill(String text) {
        Label lbl = new Label(text);
        lbl.setAlignment(Pos.CENTER);
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.08);" +
                "-fx-border-color: rgba(255, 255, 255, 0.15);" +
                "-fx-border-radius: 14px;" +
                "-fx-background-radius: 14px;" +
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-padding: 10 18 10 18;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;"
        );
        return lbl;
    }

    private VBox createStatCard(String number, String text) {
        Label numberLabel = new Label(number);
        numberLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 12, 0.35, 0, 0);"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 12px;"
        );

        VBox card = new VBox(6, numberLabel, textLabel);
        card.setPadding(new Insets(18));
        card.setPrefWidth(140);
        card.setPrefHeight(96);
        card.setStyle(ClayTheme.GLASS_CARD);
        ClayTheme.applyCardHover(card);

        return card;
    }

    private VBox createFeatureCard(String icon, String title, String description) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-background-color: " + ClayTheme.CYAN_TINT + ";" +
                "-fx-border-color: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-border-radius: 10px;" +
                "-fx-background-radius: 10px;" +
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 18px;" +
                "-fx-padding: 6px 12px;"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 11px;" +
                "-fx-line-spacing: 3px;"
        );

        VBox card = new VBox(8, iconLabel, titleLabel, descriptionLabel);
        card.setPadding(new Insets(18));
        card.setMinHeight(115);
        card.setStyle(ClayTheme.GLASS_CARD);
        ClayTheme.applyCardHover(card);

        return card;
    }

    public void backtohome() {
        homeStage.setScene(homeScene);
    }

        private void openAboutUs() {
                AboutUs aboutUs = new AboutUs();
                homeStage.setScene(aboutUs.getAboutUsScene(this::backtohome));
        }
}