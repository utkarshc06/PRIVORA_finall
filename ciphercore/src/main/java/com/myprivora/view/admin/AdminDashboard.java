package com.myprivora.view.admin;

import com.myprivora.view.landing.HomePage;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.util.Duration;

import com.myprivora.view.theme.ClayTheme;

public class AdminDashboard {

    // =========================================================
    // SCENE
    // =========================================================

    private Scene adminDashboardScene;

    // =========================================================
    // CONTENT AREA
    // =========================================================

    private VBox contentArea;

    // =========================================================
    // SELECTED MENU ITEM
    // =========================================================

    private HBox selectedMenuItem;


    // =========================================================
    // ADMIN DASHBOARD SCENE
    // =========================================================

    public Scene getAdminDashboardScene(Runnable ral) {

        // =====================================================
        // ROOT
        // =====================================================

        BorderPane root = new BorderPane();

        root.setStyle(
                "-fx-background-color: " + ClayTheme.OBSIDIAN_DEEP + ";"
        );


        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(270);
        sidebar.setMinWidth(270);

        sidebar.setStyle(
                ClayTheme.SIDEBAR_BACKGROUND
        );


        // =====================================================
        // LOGO SECTION
        // =====================================================

        HBox logoSection = new HBox(12);

        logoSection.setAlignment(
                Pos.CENTER_LEFT
        );

        logoSection.setPadding(
                new Insets(20, 18, 20, 18)
        );


        // =====================================================
        // PRIVORA LOGO
        // =====================================================

        Circle logoCircle =
                new Circle(20);

        logoCircle.setFill(
                Color.web(ClayTheme.CYAN_PRIMARY)
        );


        Image shieldImage =
                new Image(
                        getClass()
                                .getResource(
                                        "/assets/images/privimg.jpeg"
                                )
                                .toExternalForm()
                );


        ImageView shieldView =
                new ImageView(shieldImage);

        shieldView.setFitWidth(28);
        shieldView.setFitHeight(28);
        shieldView.setPreserveRatio(true);


        StackPane shieldBox =
                new StackPane(
                        shieldView
                );

        shieldBox.setPrefSize(
                42,
                42
        );

        shieldBox.setMaxSize(
                42,
                42
        );

        shieldBox.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #00D2FF, #00B4D8);" +
                "-fx-background-radius: 50%;" +
                "-fx-effect: dropshadow(" +
                "gaussian, rgba(0, 240, 255, 0.45)," +
                "15, 0.5, 0, 0);"
        );


        StackPane logoBox =
                new StackPane();

        logoBox.setPrefSize(
                42,
                42
        );

        logoBox.setMaxSize(
                42,
                42
        );

        logoBox.getChildren().addAll(
                logoCircle,
                shieldBox
        );


        // =====================================================
        // TITLE
        // =====================================================

        VBox titleBox =
                new VBox(2);


        Label appName =
                new Label("PRIVORA");

        appName.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 21px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "PRIVACY CONTROLLED"
                );

        subtitle.setStyle(
                "-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + ";" +
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1.5px;"
        );


        titleBox.getChildren().addAll(
                appName,
                subtitle
        );


        logoSection.getChildren().addAll(
                logoBox,
                titleBox
        );


        // =====================================================
        // ADMIN PANEL TITLE
        // =====================================================

        Label panelTitle =
                new Label("ADMIN PANEL");

        panelTitle.setPadding(
                new Insets(14, 20, 10, 20)
        );

        panelTitle.setStyle(
                "-fx-text-fill: " + ClayTheme.TEXT_MUTED + ";" +
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;"
        );


        // =====================================================
        // MENU BOX
        // =====================================================

        VBox menuBox =
                new VBox(5);

        menuBox.setPadding(
                new Insets(5, 10, 10, 10)
        );


        // =====================================================
        // MENU ITEMS
        // =====================================================

        String[][] menuItems = {

                {"▦", "Dashboard"},
                {"♙", "Users"},
                {"♜", "Centres"},
                {"▤", "Documents"},
                {"▣", "Reports"},
                {"⚙", "Settings"},
                {"◈", "Analytics"},
                {"〽", "Activity Logs"}

        };


        for (int i = 0;
             i < menuItems.length;
             i++) {

            HBox item =
                    createMenuItem(
                            menuItems[i][0],
                            menuItems[i][1]
                    );


            if (i == 0) {

                setSelected(item);

            }


            menuBox.getChildren().add(
                    item
            );
        }


        // =====================================================
        // MENU SCROLL
        // =====================================================

        ScrollPane menuScroll =
                new ScrollPane(
                        menuBox
                );

        menuScroll.setFitToWidth(
                true
        );

        menuScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        menuScroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        menuScroll.setStyle(
                "-fx-background-color: #121019;" +
                "-fx-background: #121019;" +
                "-fx-border-color: transparent;"
        );


        VBox.setVgrow(
                menuScroll,
                Priority.ALWAYS
        );


        // =====================================================
        // LOGOUT
        // =====================================================

        HBox logoutSection =
                new HBox(15);

        logoutSection.setAlignment(
                Pos.CENTER_LEFT
        );

        logoutSection.setPadding(
                new Insets(18, 25, 22, 25)
        );

        logoutSection.setStyle(
                "-fx-border-color: rgba(255, 255, 255, 0.08) transparent transparent transparent;" +
                "-fx-border-width: 1 0 0 0;" +
                "-fx-cursor: hand;"
        );


        Label logoutIcon =
                new Label("⎋");

        logoutIcon.setStyle(
                "-fx-text-fill: #FF4B4B;" +
                "-fx-font-size: 20px;"
        );


        Label logoutText =
                new Label("Logout");

        logoutText.setStyle(
                "-fx-text-fill: #FF4B4B;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        logoutSection.getChildren().addAll(
                logoutIcon,
                logoutText
        );


        // =====================================================
        // LOGOUT HOVER
        // =====================================================

        logoutSection.setOnMouseEntered(e -> {

            logoutText.setStyle(
                    "-fx-text-fill: #FF4B4B;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
            logoutIcon.setStyle(
                    "-fx-text-fill: #FF4B4B;" +
                    "-fx-font-size: 20px;"
            );


            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(120),
                            logoutSection
                    );

            scale.setToX(1.03);
            scale.setToY(1.03);
            scale.play();

        });


        logoutSection.setOnMouseExited(e -> {

            logoutText.setStyle(
                    "-fx-text-fill: #FF4B4B;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;"
            );
            logoutIcon.setStyle(
                    "-fx-text-fill: #FF4B4B;" +
                    "-fx-font-size: 20px;"
            );


            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(120),
                            logoutSection
                    );

            scale.setToX(1);
            scale.setToY(1);
            scale.play();

        });


        // =====================================================
        // LOGOUT ACTION
        // =====================================================

        logoutSection.setOnMouseClicked(e -> {

            if (ral != null) {

                ral.run();

            }

        });


        // =====================================================
        // SIDEBAR
        // =====================================================

        sidebar.getChildren().addAll(
                logoSection,
                panelTitle,
                menuScroll,
                logoutSection
        );


        // =====================================================
        // RIGHT CONTENT AREA
        // =====================================================

        contentArea =
                new VBox();

        contentArea.setStyle(
                "-fx-background-color: " + ClayTheme.OBSIDIAN_DEEP + ";"
        );


        // =====================================================
        // DEFAULT DASHBOARD
        // =====================================================

        contentArea.getChildren().add(
                new ADashboard().getContent()
        );


        // =====================================================
        // ROOT
        // =====================================================

        root.setLeft(
                sidebar
        );

        root.setCenter(
                contentArea
        );


        // =====================================================
        // SCENE
        // =====================================================

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        adminDashboardScene =
        new Scene(
                root,
                bounds.getWidth(),
                bounds.getHeight()
        );

        HomePage.homeStage.setScene(adminDashboardScene);
        HomePage.homeStage.setMaximized(true);

        return adminDashboardScene;
    }


    // =========================================================
    // CREATE MENU ITEM
    // =========================================================

    private HBox createMenuItem(
            String icon,
            String text) {


        HBox item =
                new HBox(15);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(12, 15, 12, 15)
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background-radius: 18;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefWidth(
                22
        );

        iconLabel.setStyle(
                "-fx-text-fill: #9B93AA;" +
                "-fx-font-size: 17px;"
        );


        // =====================================================
        // TEXT
        // =====================================================

        Label textLabel =
                new Label(text);

        textLabel.setStyle(
                "-fx-text-fill: #D9D3E3;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );


        item.getChildren().addAll(
                iconLabel,
                textLabel
        );


        // =====================================================
        // HOVER
        // =====================================================

        item.setOnMouseEntered(e -> {

            if (item != selectedMenuItem) {

                item.setStyle(
                        "-fx-background-color: rgba(255, 255, 255, 0.06);" +
                        "-fx-border-color: rgba(0, 240, 255, 0.35);" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(" +
                        "gaussian, rgba(0, 240, 255, 0.20)," +
                        "12, 0.3, 0, 2);"
                );
            }


            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(120),
                            item
                    );

            scale.setToX(1.02);
            scale.setToY(1.02);

            scale.play();

        });


        // =====================================================
        // EXIT
        // =====================================================

        item.setOnMouseExited(e -> {

            if (item != selectedMenuItem) {

                setNormal(item);

            }


            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(120),
                            item
                    );

            scale.setToX(1);
            scale.setToY(1);

            scale.play();

        });


        // =====================================================
        // CLICK
        // =====================================================

        item.setOnMouseClicked(e -> {

            if (selectedMenuItem != null) {

                setNormal(
                        selectedMenuItem
                );

            }


            setSelected(item);

            selectedMenuItem = item;

            changePage(text);

        });


        return item;
    }


    // =========================================================
    // CHANGE PAGE
    // =========================================================

    private void changePage(
            String pageName) {


        contentArea.getChildren().clear();


        switch (pageName) {

            case "Dashboard":

                contentArea.getChildren().add(
                        new ADashboard().getContent()
                );

                break;


            case "Users":

                contentArea.getChildren().add(
                        new Users().getContent()
                );

                break;


            case "Centres":

                contentArea.getChildren().add(
                        new Centres().getContent()
                );

                break;


            case "Documents":

                contentArea.getChildren().add(
                        new Documents().getContent()
                );

                break;


            case "Reports":

                contentArea.getChildren().add(
                        new Reports().getContent()
                );

                break;


            case "Settings":

                contentArea.getChildren().add(
                        new Settings().getContent()
                );

                break;


            case "Analytics":

                contentArea.getChildren().add(
                        new Analytics().getContent()
                );

                break;


            case "Activity Logs":

                contentArea.getChildren().add(
                        new ActivityLogs().getContent()
                );

                break;
        }
    }


    // =========================================================
    // SELECTED MENU
    // =========================================================

    private void setSelected(
            HBox item) {


        selectedMenuItem = item;


        item.setScaleX(1);
        item.setScaleY(1);


        item.setStyle(
                "-fx-background-color: linear-gradient(" +
                "to right, #00D2FF, #00F0FF);" +
                "-fx-background-radius: 18;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(" +
                "gaussian, rgba(0, 240, 255, 0.45)," +
                "18, 0.45, 0, 3);"
        );


        Label icon =
                (Label) item.getChildren().get(0);


        Label text =
                (Label) item.getChildren().get(1);


        icon.setStyle(
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );


        text.setStyle(
                "-fx-text-fill: #080C16;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );
    }


    // =========================================================
    // NORMAL MENU
    // =========================================================

    private void setNormal(
            HBox item) {


        item.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-background-radius: 18;" +
                "-fx-cursor: hand;"
        );


        Label icon =
                (Label) item.getChildren().get(0);


        Label text =
                (Label) item.getChildren().get(1);


        icon.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_DIM + ";" +
                "-fx-font-size: 17px;"
        );


        text.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
        );
    }
}