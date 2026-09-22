package com.myprivora.view.theme;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * Unified Claymorphism Design System for Privora (Ciphercore).
 * Strictly governed by a 3-color palette suited for Xerox & User workflows:
 * 1. Primary: Soft Xerox Cyan (#00C2D1)
 * 2. Background: Matte Cloud White (#F4F7FC / #FFFFFF)
 * 3. Text/Shadow: Deep Slate / Charcoal (#2D3748 / #A3B1C6)
 */
public final class ClayTheme {

    private ClayTheme() {}

    // =========================================================
    // THE 3 CANONICAL COLORS (CLAYMORPHISM)
    // =========================================================

    public static final String CYAN_PRIMARY = "#00C2D1";
    public static final String CYAN_HOVER   = "#00E0F0";
    public static final String CYAN_DARK    = "#009DAA";
    public static final String CYAN_GLOW    = "rgba(0, 194, 209, 0.4)";
    public static final String CYAN_TINT    = "rgba(0, 194, 209, 0.1)";

    public static final String BG_MATTE     = "#0B0F19";
    public static final String BG_CARD      = "#131A2A";
    public static final String BG_INPUT     = "#1A233A";

    public static final String TEXT_DARK    = "#F8FAFC";
    public static final String TEXT_MUTED   = "#A0AEC0";
    public static final String SHADOW_DARK  = "rgba(0, 0, 0, 0.6)";
    public static final String SHADOW_LIGHT = "rgba(0, 0, 0, 0.8)";
    public static final String BORDER_DARK  = "#2D3748";

    // Semantic aliases to minimize refactoring pain for views
    public static final String OBSIDIAN_DEEP    = BG_MATTE;
    public static final String OBSIDIAN_GLASS   = BG_CARD;
    public static final String OBSIDIAN_SURFACE = "#F8FAFC";
    public static final String OBSIDIAN_CARD    = BG_CARD;
    public static final String FROST_WHITE      = TEXT_DARK; // inverted
    public static final String FROST_MUTED      = TEXT_MUTED; // inverted
    public static final String WHITE_TEXT       = TEXT_DARK;
    public static final String WHITE_MUTED      = TEXT_MUTED;
    public static final String CYAN_LIGHT       = CYAN_HOVER;
    public static final String CYAN_ACCENT      = CYAN_DARK;
    
    // Add missing aliases for compilation compatibility
    public static final String CARD_BORDER_COLOR= BORDER_DARK;
    public static final String WHITE_DIM        = TEXT_MUTED;
    public static final String BORDER_ACTIVE    = CYAN_PRIMARY;
    public static final String CYAN_TINT_STRONG = "rgba(0, 194, 209, 0.2)";
    public static final String BG_OBSIDIAN      = BG_MATTE;
    public static final String FROST_BORDER     = BORDER_DARK;

    // =========================================================
    // MASTER CSS STYLES (CLAYMORPHISM)
    // =========================================================

    public static final String MAIN_BACKGROUND =
            "-fx-background-color: " + BG_MATTE + ";";

    public static final String SIDEBAR_BACKGROUND =
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER_DARK + ";" +
            "-fx-border-width: 0 1px 0 0;";

    public static final String GLASS_CARD =
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER_DARK + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, " + SHADOW_DARK + ", 15, 0.1, 0, 4);";

    public static final String GLASS_CARD_HOVER =
            "-fx-background-color: #1A243B;" +
            "-fx-border-color: " + CYAN_TINT + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, " + SHADOW_DARK + ", 20, 0.2, 0, 8);";

    public static final String GLASS_CARD_SELECTED =
            "-fx-background-color: " + CYAN_TINT + ";" +
            "-fx-border-color: " + CYAN_PRIMARY + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 12px;" +
            "-fx-background-radius: 12px;" +
            "-fx-effect: dropshadow(gaussian, " + CYAN_GLOW + ", 15, 0.2, 0, 0);";

    public static final String GLASS_INPUT =
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-border-color: " + BORDER_DARK + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 16 12 16;" +
            "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 2);";

    public static final String GLASS_INPUT_FOCUSED =
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-border-color: " + CYAN_PRIMARY + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12 16 12 16;" +
            "-fx-effect: dropshadow(gaussian, " + CYAN_GLOW + ", 8, 0.1, 0, 0);";

    public static final String PRIMARY_BUTTON =
            "-fx-background-color: " + CYAN_PRIMARY + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + BG_MATTE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + CYAN_GLOW + ", 10, 0.2, 0, 2);";

    public static final String PRIMARY_BUTTON_HOVER =
            "-fx-background-color: " + CYAN_HOVER + ";" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + BG_MATTE + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24 12 24;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + CYAN_GLOW + ", 15, 0.3, 0, 4);";

    public static final String SECONDARY_BUTTON =
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + BORDER_DARK + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24 12 24;" +
            "-fx-cursor: hand;";

    public static final String SECONDARY_BUTTON_HOVER =
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + TEXT_MUTED + ";" +
            "-fx-border-width: 1px;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 12 24 12 24;" +
            "-fx-cursor: hand;";

    public static final String BADGE_CYAN =
            "-fx-background-color: " + CYAN_TINT + ";" +
            "-fx-background-radius: 16px;" +
            "-fx-text-fill: " + CYAN_DARK + ";" +
            "-fx-padding: 6 14 6 14;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;";

    public static final String BADGE_GLASS =
            "-fx-background-color: " + BG_INPUT + ";" +
            "-fx-background-radius: 16px;" +
            "-fx-text-fill: " + TEXT_DARK + ";" +
            "-fx-padding: 6 14 6 14;" +
            "-fx-font-size: 12px;";

    // =========================================================
    // UI FACTORIES & HELPERS
    // =========================================================

    public static Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(PRIMARY_BUTTON);
        btn.setCursor(Cursor.HAND);
        applyButtonHover(btn, true);
        return btn;
    }

    public static Button createGlassButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(SECONDARY_BUTTON);
        btn.setCursor(Cursor.HAND);
        applyButtonHover(btn, false);
        return btn;
    }

    public static void applyButtonHover(Button button, boolean isPrimary) {
        button.setOnMouseEntered(e -> {
            button.setStyle(isPrimary ? PRIMARY_BUTTON_HOVER : SECONDARY_BUTTON_HOVER);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.04);
            st.setToY(1.04);
            st.play();
        });
        button.setOnMouseExited(e -> {
            button.setStyle(isPrimary ? PRIMARY_BUTTON : SECONDARY_BUTTON);
            ScaleTransition st = new ScaleTransition(Duration.millis(120), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    public static void applyCardHover(Node card) {
        card.setOnMouseEntered(e -> {
            card.setStyle(GLASS_CARD_HOVER);
            ScaleTransition st = new ScaleTransition(Duration.millis(140), card);
            st.setToX(1.02);
            st.setToY(1.02);
            st.play();
        });
        card.setOnMouseExited(e -> {
            card.setStyle(GLASS_CARD);
            ScaleTransition st = new ScaleTransition(Duration.millis(140), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    public static void fadeInSlide(Node node, double fromY) {
        node.setOpacity(0);
        node.setTranslateY(fromY);

        FadeTransition ft = new FadeTransition(Duration.millis(600), node);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(550), node);
        tt.setToY(0);

        ft.play();
        tt.play();
    }

    public static void pulseGlow(Node node) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(node.opacityProperty(), 0.85)),
                new KeyFrame(Duration.millis(1000), new KeyValue(node.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(2000), new KeyValue(node.opacityProperty(), 0.85))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    public static ImageView createImageViewSafe(String resourcePath, double width, double height) {
        try {
            InputStream is = ClayTheme.class.getResourceAsStream(resourcePath);
            if (is != null) {
                Image img = new Image(is);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                return iv;
            }
        } catch (Exception ignored) {}
        return new ImageView();
    }

    public static ImageView createLottieAnimation(String gifName, double width, double height) {
        return createImageViewSafe("/assets/animations/" + gifName, width, height);
    }

    public static ProgressIndicator createLoader() {
        ProgressIndicator loader = new ProgressIndicator();
        loader.setStyle("-fx-progress-color: " + CYAN_PRIMARY + ";");
        loader.setMaxSize(40, 40);
        return loader;
    }

    public static HBox createBrandHeader(Runnable onHome) {
        Circle logoGlow = new Circle(18);
        logoGlow.setFill(Color.web(CYAN_PRIMARY));

        ImageView shieldView = createImageViewSafe("/assets/images/privimg.jpeg", 24, 24);
        StackPane shieldBox = new StackPane(shieldView);
        shieldBox.setPrefSize(38, 38);
        shieldBox.setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                "-fx-background-radius: 50%;" +
                "-fx-effect: dropshadow(gaussian, " + SHADOW_DARK + ", 10, 0.2, 4, 4);"
        );

        StackPane logoBox = new StackPane(logoGlow, shieldBox);

        Label brandName = new Label("PRIVORA");
        brandName.setStyle(
                "-fx-text-fill: " + TEXT_DARK + ";" +
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );

        Label brandTagline = new Label("ZERO-TRUST PRINT ARCHITECTURE");
        brandTagline.setStyle(
                "-fx-text-fill: " + CYAN_PRIMARY + ";" +
                "-fx-font-size: 8px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1.5px;"
        );

        VBox text = new VBox(1, brandName, brandTagline);
        HBox brand = new HBox(12, logoBox, text);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setCursor(Cursor.HAND);
        if (onHome != null) {
            brand.setOnMouseClicked(e -> onHome.run());
        }
        return brand;
    }
}
