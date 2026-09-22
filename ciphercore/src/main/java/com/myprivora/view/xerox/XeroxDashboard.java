
package com.myprivora.view.xerox;

import com.myprivora.model.PrintRequest;
import com.myprivora.session.SessionManager;
import com.myprivora.view.landing.HomePage;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class XeroxDashboard {

    private Scene xeroxScene;

    private VBox menuBox;

    private StackPane rightContent;

    private HBox selectedItem;


    // =========================================================
    // CURRENT DASHBOARD INSTANCE
    // =========================================================

    private XDashboard currentDashboard;


    // =========================================================
    // CURRENT PROFILE INSTANCE
    // =========================================================

    private Profile currentProfile;


    // =========================================================
    // LOGOUT CALLBACK
    // =========================================================

    private Runnable logoutAction;


    // =========================================================
    // CURRENT SELECTED REQUEST
    // =========================================================

    private PrintRequest selectedRequest;


    // =========================================================
    // MAIN XEROX SCENE
    // =========================================================

    public Scene getXeroxDashboardScene(Runnable rd) {

        logoutAction = rd;


        // =====================================================
        // SIDEBAR
        // =====================================================

        VBox sidebar = new VBox();

        sidebar.setPrefWidth(270);
        sidebar.setMinWidth(270);

        sidebar.setStyle("""
                -fx-background-color: rgba(9, 14, 26, 0.95);
                -fx-border-color: rgba(255, 255, 255, 0.08);
                -fx-border-width: 0 1 0 0;
                """);


        // =====================================================
        // LOGO
        // =====================================================

        HBox logoSection = new HBox(12);

        logoSection.setAlignment(Pos.CENTER_LEFT);

        logoSection.setPadding(
                new Insets(20, 18, 20, 18)
        );


        Circle logoCircle = new Circle(20);

        logoCircle.setFill(
                Color.web("#00F0FF")
        );


        javafx.scene.image.Image shieldImage =
                new javafx.scene.image.Image(
                        "/assets/images/privimg.jpeg"
                );


        javafx.scene.image.ImageView shieldView =
                new javafx.scene.image.ImageView(
                        shieldImage
                );

        shieldView.setFitWidth(28);
        shieldView.setFitHeight(28);
        shieldView.setPreserveRatio(true);


        StackPane shieldBox =
                new StackPane(shieldView);

        shieldBox.setPrefSize(42, 42);

        shieldBox.setStyle("""
                -fx-background-color: #00D2FF;
                -fx-background-radius: 50%;
                -fx-effect: dropshadow(
                    gaussian,
                    rgba(0,240,255,0.65),
                    15,
                    0.5,
                    0,
                    0
                );
                """);


        StackPane logoBox =
                new StackPane();

        logoBox.setPrefSize(42, 42);

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

        appName.setStyle("""
                -fx-text-fill: #FFFFFF;
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                """);


        Label subtitle =
                new Label(
                        "XEROX SECURE PRINT"
                );

        subtitle.setStyle("""
                -fx-text-fill: #00F0FF;
                -fx-font-size: 9px;
                -fx-font-weight: bold;
                -fx-letter-spacing: 1px;
                """);


        titleBox.getChildren().addAll(
                appName,
                subtitle
        );


        logoSection.getChildren().addAll(
                logoBox,
                titleBox
        );


        // =====================================================
        // XEROX PANEL TITLE
        // =====================================================

        Label panelTitle =
                new Label("XEROX PANEL");

        panelTitle.setPadding(
                new Insets(15, 20, 10, 20)
        );

        panelTitle.setStyle("""
                -fx-text-fill: #756A86;
                -fx-font-size: 10px;
                -fx-font-weight: bold;
                """);


        // =====================================================
        // MENU
        // =====================================================

        menuBox =
                new VBox(5);

        menuBox.setPadding(
                new Insets(5, 10, 10, 10)
        );


        String[][] menuItems = {

                {"▦", "Dashboard"},
                {"⇩", "Incoming Requests"},
                {"◈", "Request Details"},
                
                {"◴", "Print History"},
                {"⚙", "Profile"}

        };


        // =====================================================
        // CREATE ALL MENU ITEMS
        // =====================================================

        for (int i = 0; i < menuItems.length; i++) {

            HBox item =
                    createMenuItem(
                            menuItems[i][0],
                            menuItems[i][1]
                    );


            menuBox.getChildren().add(item);


            if (i == 0) {

                setSelected(item);

            }
        }


        // =====================================================
        // MENU SCROLL
        // =====================================================

        ScrollPane menuScroll =
                new ScrollPane(menuBox);

        menuScroll.setFitToWidth(true);

        menuScroll.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        menuScroll.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        menuScroll.setStyle("""
                -fx-background-color: transparent;
                -fx-background: transparent;
                """);


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

        logoutSection.setStyle("""
                -fx-border-color:
                    rgba(255, 255, 255, 0.08)
                    transparent
                    transparent
                    transparent;

                -fx-border-width: 1 0 0 0;

                -fx-cursor: hand;
                """);


        Label logoutIcon =
                new Label("⎋");

        logoutIcon.setStyle("""
                -fx-text-fill: #FF4B4B;
                -fx-font-size: 19px;
                """);


        Label logoutText =
                new Label("Logout");

        logoutText.setStyle("""
                -fx-text-fill: #FF4B4B;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                """);


        logoutSection.getChildren().addAll(
                logoutIcon,
                logoutText
        );


        // =====================================================
        // LOGOUT ACTION
        // =====================================================

        logoutSection.setOnMouseClicked(e -> {

            System.out.println(
                    "[XeroxDashboard] Logout clicked."
            );


            dispose();


            SessionManager.clear();


            if (logoutAction != null) {

                logoutAction.run();

            }

        });


        // =====================================================
        // ADD SIDEBAR COMPONENTS
        // =====================================================

        sidebar.getChildren().addAll(
                logoSection,
                panelTitle,
                menuScroll,
                logoutSection
        );


        // =====================================================
        // RIGHT CONTENT
        // =====================================================

        rightContent =
                new StackPane();

        rightContent.setStyle(
                "-fx-background-color: #080C16;"
        );


        // =====================================================
        // SHOW DASHBOARD FIRST
        // =====================================================

        showDashboard();


        // =====================================================
        // ROOT
        // =====================================================

        HBox root =
                new HBox();

        root.getChildren().addAll(
                sidebar,
                rightContent
        );


        HBox.setHgrow(
                rightContent,
                Priority.ALWAYS
        );


        // =====================================================
        // SCENE
        // =====================================================

        javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        xeroxScene =
        new Scene(
                root,
                bounds.getWidth(),
                bounds.getHeight()
        );

        try {
            xeroxScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        HomePage.homeStage.setScene(xeroxScene);
        HomePage.homeStage.setMaximized(true);

        return xeroxScene;
    }


    // =========================================================
    // GET SCENE
    // =========================================================

    public Scene getScene() {

        if (xeroxScene == null) {

            getXeroxDashboardScene(null);

        }

        return xeroxScene;
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
                new Insets(
                        11,
                        15,
                        11,
                        15
                )
        );

        item.setMaxWidth(
                Double.MAX_VALUE
        );

        item.setStyle("""
                -fx-background-color: transparent;
                -fx-background-radius: 18;
                -fx-cursor: hand;
                """);


        // =====================================================
        // ICON
        // =====================================================

        Label iconLabel =
                new Label(icon);

        iconLabel.setPrefWidth(20);

        iconLabel.setStyle("""
                -fx-text-fill: #9E94AD;
                -fx-font-size: 16px;
                """);


        // =====================================================
        // TEXT
        // =====================================================

        Label textLabel =
                new Label(text);

        textLabel.setStyle("""
                -fx-text-fill: #D8D2E2;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                """);


        item.getChildren().addAll(
                iconLabel,
                textLabel
        );


        // =====================================================
        // CLICK
        // =====================================================

        item.setOnMouseClicked(e -> {

            System.out.println(
                    "[XeroxDashboard] Sidebar clicked: "
                            + text
            );


            selectMenuItem(item);

        });


        // =====================================================
        // HOVER ANIMATION
        // =====================================================

        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        item
                );

        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);


        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        item
                );

        scaleDown.setToX(1);
        scaleDown.setToY(1);


        item.setOnMouseEntered(e -> {

            if (item != selectedItem) {

                scaleDown.stop();

                scaleUp.playFromStart();

                item.setStyle("""
                        -fx-background-color: rgba(0, 240, 255, 0.10);
                        -fx-background-radius: 12;
                        -fx-cursor: hand;
                        -fx-effect: dropshadow(
                            gaussian,
                            rgba(0, 240, 255, 0.25),
                            10,
                            0.25,
                            0,
                            0
                        );
                        """);
            }

        });


        item.setOnMouseExited(e -> {

            if (item != selectedItem) {

                scaleUp.stop();

                scaleDown.playFromStart();

                setNormal(item);

            }

        });


        return item;
    }


    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================

    private void selectMenuItem(HBox item) {

        if (item == null) {

            return;
        }


        // =====================================================
        // RESET ALL ITEMS
        // =====================================================

        for (javafx.scene.Node node :
                menuBox.getChildren()) {

            if (node instanceof HBox menuItem) {

                setNormal(menuItem);

            }
        }


        // =====================================================
        // SELECT CURRENT ITEM
        // =====================================================

        setSelected(item);


        // =====================================================
        // GET MENU TEXT
        // =====================================================

        if (item.getChildren().size() < 2) {

            return;
        }


        if (!(item.getChildren().get(1)
                instanceof Label textLabel)) {

            return;
        }


        String text =
                textLabel.getText();


        // =====================================================
        // NAVIGATION
        // =====================================================

        try {

            switch (text) {

                case "Dashboard":

                    showDashboard();

                    break;


                case "Incoming Requests":

                    showIncomingRequests();

                    break;


                case "Request Details":

                    showRequestDetails(
                            selectedRequest
                    );

                    break;


                

               


                case "Print History":

                    showHistory();

                    break;


                case "Profile":

                    showProfile();

                    break;


                default:

                    System.out.println(
                            "[XeroxDashboard] "
                                    + "No navigation action for: "
                                    + text
                    );

                    break;
            }

        } catch (Exception ex) {

            System.err.println(
                    "[XeroxDashboard] "
                            + "Error opening page: "
                            + text
            );

            ex.printStackTrace();

        }
    }


    // =========================================================
    // SELECTED MENU
    // =========================================================

    private void setSelected(HBox item) {

        if (item == null) {

            return;
        }


        selectedItem =
                item;


        item.setScaleX(1);

        item.setScaleY(1);


        item.setStyle("""
                -fx-background-color:
                    linear-gradient(
                        to right,
                        rgba(0, 240, 255, 0.22),
                        rgba(0, 240, 255, 0.05)
                    );
                -fx-border-color: transparent transparent transparent #00F0FF;
                -fx-border-width: 0 0 0 3px;
                -fx-background-radius: 12;
                -fx-border-radius: 12;
                -fx-cursor: hand;
                -fx-effect: dropshadow(
                    gaussian,
                    rgba(0, 240, 255, 0.35),
                    14,
                    0.3,
                    0,
                    2
                );
                """);


        if (item.getChildren().size() >= 2) {

            Label icon =
                    (Label) item.getChildren().get(0);

            Label text =
                    (Label) item.getChildren().get(1);


            icon.setStyle("""
                    -fx-text-fill: #00F0FF;
                    -fx-font-size: 16px;
                    """);


            text.setStyle("""
                    -fx-text-fill: #FFFFFF;
                    -fx-font-size: 14px;
                    -fx-font-weight: bold;
                    """);
        }
    }


    // =========================================================
    // NORMAL MENU
    // =========================================================

    private void setNormal(HBox item) {

        if (item == null) {

            return;
        }


        item.setScaleX(1);

        item.setScaleY(1);


        item.setStyle("""
                -fx-background-color: transparent;
                -fx-background-radius: 12;
                -fx-cursor: hand;
                """);


        if (item.getChildren().size() >= 2) {

            Label icon =
                    (Label) item.getChildren().get(0);

            Label text =
                    (Label) item.getChildren().get(1);


            icon.setStyle("""
                    -fx-text-fill: rgba(255, 255, 255, 0.45);
                    -fx-font-size: 16px;
                    """);


            text.setStyle("""
                    -fx-text-fill: rgba(255, 255, 255, 0.75);
                    -fx-font-size: 14px;
                    -fx-font-weight: bold;
                    """);
        }
    }


    // =========================================================
    // SELECT MENU BY NAME
    // =========================================================

    private void selectMenuByName(
            String menuName) {

        if (menuBox == null) {

            return;
        }


        for (javafx.scene.Node node :
                menuBox.getChildren()) {

            if (node instanceof HBox item) {

                if (item.getChildren().size() < 2) {

                    continue;
                }


                if (!(item.getChildren().get(1)
                        instanceof Label label)) {

                    continue;
                }


                if (menuName.equals(
                        label.getText()
                )) {

                    // =========================================
                    // RESET ALL MENU ITEMS
                    // =========================================

                    for (javafx.scene.Node other :
                            menuBox.getChildren()) {

                        if (other instanceof HBox otherItem) {

                            setNormal(otherItem);

                        }
                    }


                    // =========================================
                    // HIGHLIGHT REQUESTED MENU
                    // =========================================

                    setSelected(item);


                    System.out.println(
                            "[XeroxDashboard] "
                                    + "Sidebar selected: "
                                    + menuName
                    );


                    return;
                }
            }
        }
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    private void showDashboard() {

        System.out.println(
                "[XeroxDashboard] Opening Dashboard..."
        );


        selectMenuByName(
                "Dashboard"
        );


        disposeCurrentDashboard();


        currentDashboard =
                new XDashboard();


        rightContent.getChildren().setAll(
                currentDashboard.getContent()
        );
    }


    // =========================================================
    // DISPOSE CURRENT DASHBOARD
    // =========================================================

    private void disposeCurrentDashboard() {

        if (currentDashboard != null) {

            currentDashboard.dispose();

            currentDashboard = null;

        }
    }


    // =========================================================
    // INCOMING REQUESTS
    // =========================================================

    private void showIncomingRequests() {

        System.out.println(
                "[XeroxDashboard] "
                        + "Opening Incoming Requests..."
        );


        selectMenuByName(
                "Incoming Requests"
        );


        disposeCurrentDashboard();


        IncomingRequests incomingRequests =
                new IncomingRequests(

                        request -> {

                            System.out.println(
                                    "[XeroxDashboard] "
                                            + "Request selected."
                            );


                            selectedRequest =
                                    request;


                            // =================================
                            // OPEN REQUEST DETAILS
                            // =================================

                            showRequestDetails(
                                    request
                            );

                        }
                );


        rightContent.getChildren().setAll(
                incomingRequests.getContent()
        );
    }


    // =========================================================
    // REQUEST DETAILS
    // =========================================================

    private void showRequestDetails(
            PrintRequest request) {

        System.out.println(
                "[XeroxDashboard] "
                        + "Opening Request Details..."
        );


        // =====================================================
        // CHANGE LEFT SIDEBAR SELECTION
        // =====================================================

        selectMenuByName(
                "Request Details"
        );


        disposeCurrentDashboard();


        // =====================================================
        // NO REQUEST SELECTED
        // =====================================================

        if (request == null) {

            VBox emptyBox =
                    new VBox(15);

            emptyBox.setAlignment(
                    Pos.CENTER
            );


            Label title =
                    new Label(
                            "No Request Selected"
                    );

            title.setFont(
                    Font.font(
                            "Arial",
                            FontWeight.BOLD,
                            24
                    )
            );

            title.setTextFill(
                    Color.WHITE
            );


            Label message =
                    new Label(
                            "Select a request from Incoming Requests."
                    );

            message.setFont(
                    Font.font(
                            "Arial",
                            14
                    )
            );

            message.setTextFill(
                    Color.web("#BDA9CE")
            );


            emptyBox.getChildren().addAll(
                    title,
                    message
            );


            rightContent.getChildren().setAll(
                    emptyBox
            );


            return;
        }


        // =====================================================
        // REQUEST DETAILS PAGE
        // =====================================================

        RequestDetails requestDetails =
                new RequestDetails(
                        request
                );


        rightContent.getChildren().setAll(
                requestDetails.getContent()
        );
    }

/* 
    // =========================================================
    // SECURE VIEWER
    // =========================================================

    private void showSecureViewer() {

        System.out.println(
                "[XeroxDashboard] "
                        + "Opening Secure Viewer..."
        );


        selectMenuByName(
                "Secure Viewer"
        );


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(

                createPlaceholder(
                        "Secure Viewer",
                        "Secure document viewing will appear here."
                )

        );
    }


    // =========================================================
    // PRINTER STATUS
    // =========================================================

    private void showPrinterStatus() {

        System.out.println(
                "[XeroxDashboard] "
                        + "Opening Printer Status..."
        );


        selectMenuByName(
                "Printer Status"
        );


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(

                createPlaceholder(
                        "Printer Status",
                        "Printer status and availability will appear here."
                )

        );
    }
*/

  private void showHistory() {

    System.out.println(
            "[XeroxDashboard] Opening History..."
    );

    selectMenuByName(
            "History"
    );

    disposeCurrentDashboard();

    PrintHistory printHistory =
            new PrintHistory();

    rightContent.getChildren().setAll(
            printHistory.getContent()
    );
}

    // =========================================================
    // PROFILE
    // =========================================================

    private void showProfile() {

        System.out.println(
                "[XeroxDashboard] "
                        + "Opening Profile..."
        );


        // =====================================================
        // SELECT PROFILE IN SIDEBAR
        // =====================================================

        selectMenuByName(
                "Profile"
        );


        // =====================================================
        // DISPOSE DASHBOARD
        // =====================================================

        disposeCurrentDashboard();


        // =====================================================
        // CREATE PROFILE
        // =====================================================

        currentProfile =
                new Profile();


        // =====================================================
        // SHOW PROFILE ON RIGHT SIDE
        // =====================================================

        rightContent.getChildren().setAll(
                currentProfile.getContent()
        );


        System.out.println(
                "[XeroxDashboard] "
                        + "Profile opened successfully."
        );
    }


    // =========================================================
    // PLACEHOLDER
    // =========================================================

    private VBox createPlaceholder(
            String titleText,
            String messageText) {

        VBox box =
                new VBox(15);

        box.setAlignment(
                Pos.CENTER
        );


        Label title =
                new Label(titleText);

        title.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        28
                )
        );

        title.setTextFill(
                Color.WHITE
        );


        Label message =
                new Label(messageText);

        message.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        message.setTextFill(
                Color.web("#BDA9CE")
        );


        box.getChildren().addAll(
                title,
                message
        );


        return box;
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void handleLogout() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Logout"
        );

        alert.setHeaderText(
                "Logout from PRIVORA?"
        );

        alert.setContentText(
                "Are you sure you want to logout?"
        );


        alert.showAndWait().ifPresent(
                result -> {

                    if (result ==
                            javafx.scene.control.ButtonType.OK) {

                        dispose();

                        SessionManager.clear();


                        if (logoutAction != null) {

                            logoutAction.run();

                        }

                    }

                }
        );
    }


    // =========================================================
    // DISPOSE
    // =========================================================

    public void dispose() {

        if (currentDashboard != null) {

            currentDashboard.dispose();

            currentDashboard = null;

        }


        currentProfile = null;


        System.out.println(
                "[XeroxDashboard] Dashboard disposed."
        );
    }
}

