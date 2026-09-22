package com.myprivora.view.landing;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class AboutUs {

    private Scene aboutUsScene;
    private Runnable onBack;

    public Scene getAboutUsScene(Runnable onBack) {
        this.onBack = onBack;

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
        HBox brand = ClayTheme.createBrandHeader(this.onBack);

        // BACK BUTTON
        Button backBtn = new Button("← Back to Home");
        backBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 16 8 16;" +
                "-fx-cursor: hand;"
        );
        backBtn.setOnMouseEntered(e -> {
            backBtn.setStyle(
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
        });
        backBtn.setOnMouseExited(e -> {
            backBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 8 16 8 16;"
            );
        });
        backBtn.setOnAction(e -> onBack.run());

        HBox rightNavigation = new HBox(backBtn);
        rightNavigation.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightNavigation, Priority.ALWAYS);

        navigationBar.getChildren().addAll(brand, rightNavigation);

        // =========================================================
        // MAIN CONTENT
        // =========================================================

        VBox content = new VBox();
        content.setPadding(new Insets(35, 80, 80, 80));
        content.setSpacing(24);
        content.setAlignment(Pos.TOP_CENTER);

        // Special Thanks Title
        Label specialThanksTitle = new Label("Special Thanks");
        specialThanksTitle.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(gaussian, " + ClayTheme.CYAN_GLOW + ", 15, 0.3, 0, 0);"
        );
        VBox.setMargin(specialThanksTitle, new Insets(0, 0, 10, 0));

        // 1. Shashi Sir Card
        VBox shashiCard = new VBox();
        shashiCard.setPadding(new Insets(24));
        shashiCard.setSpacing(15);
        shashiCard.setStyle(ClayTheme.GLASS_CARD);
        shashiCard.setMaxWidth(800);

        HBox shashiContent = new HBox(20);
        shashiContent.setAlignment(Pos.CENTER_LEFT);
        
        // Placeholder for Shashi Sir Image (using a Circle for now if no image)
        Circle shashiImg = new Circle(40);
        shashiImg.setFill(Color.web(ClayTheme.CYAN_TINT_STRONG));
        shashiImg.setStroke(Color.web(ClayTheme.CYAN_PRIMARY));
        shashiImg.setStrokeWidth(2);

        VBox shashiText = new VBox(5);
        Label shashiName = new Label("Shashi Sir");
        shashiName.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 24px; -fx-font-weight: bold;");
        Label core2web = new Label("Core2Web");
        core2web.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label shashiDesc = new Label("Thank you for your valuable guidance, support and knowledge throughout our project.");
        shashiDesc.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 14px; -fx-wrap-text: true;");
        
        shashiText.getChildren().addAll(shashiName, core2web, shashiDesc);
        HBox.setHgrow(shashiText, Priority.ALWAYS);
        
        // Placeholder for Core2Web Logo
        Region c2wLogo = new Region();
        c2wLogo.setPrefSize(80, 80);
        c2wLogo.setStyle(
            "-fx-background-color: rgba(0, 240, 255, 0.1);" +
            "-fx-background-radius: 10px;" +
            "-fx-border-color: rgba(255, 255, 255, 0.1);" +
            "-fx-border-radius: 10px;"
        );

        shashiContent.getChildren().addAll(shashiImg, shashiText, c2wLogo);
        shashiCard.getChildren().add(shashiContent);
        ClayTheme.applyCardHover(shashiCard);


        // 2. Instructors Card
        VBox instructorsCard = createSectionCard("Thanks to Instructors", 
            "• Sachin Sir\n• Pramod Sir\n• Akshay Sir");
        
        // 3. Super Mentors Card
        VBox superMentorsCard = createSectionCard("Thanks to Super Mentors", 
            "• Shiv Sir\n• Subodh Sir");
        
        // 4. Mentors & Team Leads Card
        VBox mentorsCard = createSectionCard("Thanks to Mentors & Team Leads", 
            "We sincerely thank all our mentors and team leads for their continuous guidance, support, suggestions and encouragement during the development of ParkShare.");


        content.getChildren().addAll(specialThanksTitle, shashiCard, instructorsCard, superMentorsCard, mentorsCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        bgStack.getChildren().add(scrollPane);

        root.setTop(navigationBar);
        root.setCenter(bgStack);

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        aboutUsScene = new Scene(
                root,
                bounds.getWidth(),
                bounds.getHeight()
        );

        try {
            aboutUsScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        // Smooth Page Entrance
        ClayTheme.fadeInSlide(content, 35);

        return aboutUsScene;
    }

    private VBox createSectionCard(String title, String contentText) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20, 24, 20, 24));
        card.setStyle(ClayTheme.GLASS_CARD);
        card.setMaxWidth(800);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label contentLabel = new Label(contentText);
        contentLabel.setStyle("-fx-text-fill: " + ClayTheme.WHITE_MUTED + "; -fx-font-size: 15px; -fx-line-spacing: 5px;");
        contentLabel.setWrapText(true);

        card.getChildren().addAll(titleLabel, contentLabel);
        ClayTheme.applyCardHover(card);
        
        return card;
    }
}
