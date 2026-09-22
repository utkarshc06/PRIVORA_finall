
package com.myprivora.view.user;

import com.myprivora.extras.MemoryLifecycleManager;
import com.myprivora.view.landing.HomePage;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Arrays;

public class UserDashboard {

    private Scene userScene;

    private VBox menuBox;

    private StackPane rightContent;

    private HBox selectedItem;


    // =========================================================
    // CURRENT DASHBOARD INSTANCE
    // =========================================================

    private UDashboard currentDashboard;


    // =========================================================
    // PRINT WORKFLOW DATA
    // =========================================================

    private String selectedDocumentId;

    private String selectedDocumentName;

    private int selectedPrintLimit = 3;

    private int selectedExpiryMinutes = 5;


    // =========================================================
    // PRIVACY PIN HASH
    // =========================================================
    // IMPORTANT:
    // PIN is created ONLY in SetPin.java.
    //
    // UserDashboard only stores the already-created
    // SHA-256 PIN hash.
    // =========================================================

    private String selectedPinHash;


    // =========================================================
    // ENCRYPTED DOCUMENT MEMORY
    // =========================================================

    private byte[] encryptedDocumentMemory;


    // =========================================================
    // AES-256 KEY
    // =========================================================
    //
    // The AES key is generated in UploadDocument.java.
    //
    // UserDashboard temporarily keeps the key in RAM so that
    // Secure Viewer can decrypt the encrypted document.
    //
    // IMPORTANT:
    // The AES key is NEVER stored in Firestore.
    // =========================================================

    private byte[] aesKey;


    // =========================================================
    // MEMORY LIFECYCLE MANAGER
    // =========================================================

    private final MemoryLifecycleManager memoryLifecycleManager =
            new MemoryLifecycleManager();


    // =========================================================
    // MAIN USER SCENE
    // =========================================================

    public Scene getUserDashboardScene(Runnable rd) {

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

        logoSection.setAlignment(
                Pos.CENTER_LEFT
        );

        logoSection.setPadding(
                new Insets(
                        20,
                        18,
                        20,
                        18
                )
        );


        Circle logoCircle =
                new Circle(20);

        logoCircle.setFill(
                Color.web("#00F0FF")
        );


        Image shieldImage =
                new Image(
                        "/assets/images/privimg.jpeg"
                );


        ImageView shieldView =
                new ImageView(
                        shieldImage
                );

        shieldView.setFitWidth(28);

        shieldView.setFitHeight(28);

        shieldView.setPreserveRatio(true);


        StackPane shieldBox =
                new StackPane(shieldView);

        shieldBox.setPrefSize(
                42,
                42
        );

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

        logoBox.setPrefSize(
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

        appName.setStyle("""
                -fx-text-fill: #F5F3FF;
                -fx-font-size: 20px;
                -fx-font-weight: bold;
                """);


        Label subtitle =
                new Label(
                        "PRIVACY CONTROLLED"
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
        // USER PANEL TITLE
        // =====================================================

        Label panelTitle =
                new Label("USER PANEL");

        panelTitle.setPadding(
                new Insets(
                        15,
                        20,
                        10,
                        20
                )
        );

        panelTitle.setStyle("""
                -fx-text-fill: rgba(255, 255, 255, 0.45);
                -fx-font-size: 10px;
                -fx-font-weight: bold;
                -fx-letter-spacing: 1.5px;
                """);


        // =====================================================
        // MENU
        // =====================================================

        menuBox =
                new VBox(5);

        menuBox.setPadding(
                new Insets(
                        5,
                        10,
                        10,
                        10
                )
        );


        String[][] menuItems = {

                {"▦", "Dashboard"},
                {"⇧", "Upload Document"},
                {"▣", "Purpose"},
                {"♢", "Permissions"},
                {"♜", "Select Centre"},
                {"◴", "Request Status"},
                {"▤", "My Documents"},
                {"〽", "Activity"},
        
                {"⚙", "Profile"}

        };


        // =====================================================
        // CREATE ALL MENU ITEMS
        // =====================================================

        for (int i = 0;
             i < menuItems.length;
             i++) {

            HBox item =
                    createMenuItem(
                            menuItems[i][0],
                            menuItems[i][1]
                    );


            menuBox.getChildren().add(
                    item
            );


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
                new Insets(
                        18,
                        25,
                        22,
                        25
                )
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
                    "[UserDashboard] Logout clicked."
            );


            // Dispose current page
            disposeCurrentDashboard();


            // Stop RAM lifecycle
            memoryLifecycleManager
                    .cancelActiveLifecycle();


            // Clear encrypted document
            clearEncryptedDocumentMemory();


            // Clear AES key
            clearAesKey();


            // Clear PIN hash
            selectedPinHash = null;


            // Clear workflow data
            selectedDocumentId = null;
            selectedDocumentName = null;
            selectedPrintLimit = 3;
            selectedExpiryMinutes = 5;


            // Run logout callback
            if (rd != null) {

                rd.run();

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
        userScene =
        new Scene(
                root,
                bounds.getWidth(),
                bounds.getHeight()
        );

        try {
            userScene.getStylesheets().add(
                    getClass().getResource("/assets/claymorphism.css").toExternalForm()
            );
        } catch (Exception ignored) {}

        HomePage.homeStage.setScene(userScene);
        HomePage.homeStage.setMaximized(true);

        return userScene;
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
                    "[UserDashboard] Sidebar clicked: "
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


                case "Upload Document":

                    showUploadDocument();

                    break;


                case "Purpose":

                    showPurpose();

                    break;


                case "Permissions":

                    showPermissions();

                    break;


                case "Select Centre":

                    showSelectCentre();

                    break;


                case "Request Status":

                    showRequestStatus();

                    break;


                case "My Documents":

                    showMyDocuments();

                    break;


                case "Activity":

                    showActivity();

                    break;




                case "Profile":

                    showProfile();

                    break;


                default:

                    System.out.println(
                            "[UserDashboard] "
                                    + "No navigation action for: "
                                    + text
                    );

                    break;
            }

        } catch (Exception ex) {

            System.err.println(
                    "[UserDashboard] "
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

                    // Reset all menu items
                    for (javafx.scene.Node other :
                            menuBox.getChildren()) {

                        if (other instanceof HBox otherItem) {

                            setNormal(otherItem);

                        }
                    }


                    // Highlight requested menu
                    setSelected(item);


                    System.out.println(
                            "[UserDashboard] "
                                    + "Sidebar selected: "
                                    + menuName
                    );


                    return;
                }
            }
        }
    }


    // =========================================================
    // SELECT UPLOAD DOCUMENT MENU
    // =========================================================

    private void selectUploadDocumentMenu() {

        selectMenuByName(
                "Upload Document"
        );
    }


    // =========================================================
    // SELECT PURPOSE MENU
    // =========================================================

    private void selectPurposeMenu() {

        selectMenuByName(
                "Purpose"
        );
    }


    // =========================================================
    // SELECT PERMISSIONS MENU
    // =========================================================

    private void selectPermissionsMenu() {

        selectMenuByName(
                "Permissions"
        );
    }


    // =========================================================
    // SELECT CENTRE MENU
    // =========================================================

    private void selectSelectCentreMenu() {

        selectMenuByName(
                "Select Centre"
        );
    }


    // =========================================================
    // SELECT REQUEST STATUS MENU
    // =========================================================

    private void selectRequestStatusMenu() {

        selectMenuByName(
                "Request Status"
        );
    }


    // =========================================================
    // SELECT ACTIVITY MENU
    // =========================================================

    private void selectActivityMenu() {

        selectMenuByName(
                "Activity"
        );
    }


    // =========================================================
    // SELECT NOTIFICATIONS MENU
    // =========================================================

  /*   private void selectNotificationsMenu() {

        selectMenuByName(
                "Notifications"
        );
    }

*/
    // =========================================================
    // OPEN PURPOSE PAGE
    // =========================================================

    public void openPurposePage() {

        selectPurposeMenu();

        showPurpose();
    }


    // =========================================================
    // DASHBOARD
    // =========================================================

    private void showDashboard() {

        System.out.println(
                "[UserDashboard] Opening Dashboard..."
        );


        selectMenuByName(
                "Dashboard"
        );


        disposeCurrentDashboard();


        currentDashboard =
                new UDashboard();


        rightContent.getChildren().setAll(
                currentDashboard.getContent()
        );


        // =====================================================
        // NEW UPLOAD BUTTON
        // =====================================================

        if (currentDashboard.getNewUploadButton()
                != null) {

            currentDashboard
                    .getNewUploadButton()
                    .setOnAction(e -> {

                        selectUploadDocumentMenu();

                        showUploadDocument();

                    });
        }


        // =====================================================
        // QUICK UPLOAD ACTION
        // =====================================================

        if (currentDashboard.getUploadDocumentAction()
                != null) {

            currentDashboard
                    .getUploadDocumentAction()
                    .setOnMouseClicked(e -> {

                        selectUploadDocumentMenu();

                        showUploadDocument();

                    });
        }
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
    // UPLOAD DOCUMENT
    // =========================================================

    private void showUploadDocument() {

        System.out.println(
                "[UserDashboard] Opening Upload Document..."
        );


        selectUploadDocumentMenu();


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new UploadDocument(this)
                        .getContent()
        );
    }


    // =========================================================
    // PURPOSE
    // =========================================================

    private void showPurpose() {

        System.out.println(
                "[UserDashboard] Opening Purpose..."
        );


        selectPurposeMenu();


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(

                new Purpose(() -> {

                    System.out.println(
                            "[UserDashboard] "
                                    + "Purpose → Permissions"
                    );


                    showPermissions();

                }).getContent()

        );
    }


    // =========================================================
    // PERMISSIONS
    // =========================================================

    private void showPermissions() {

        System.out.println(
                "[UserDashboard] Opening Permissions..."
        );


        selectPermissionsMenu();


        disposeCurrentDashboard();


        // =====================================================
        // HOLDER
        // =====================================================

        final Permissions[] permissionsHolder =
                new Permissions[1];


        // =====================================================
        // CREATE PERMISSIONS
        // =====================================================

        Permissions permissions =
                new Permissions(() -> {

                    System.out.println(
                            "[UserDashboard] "
                                    + "Permissions callback reached."
                    );


                    Permissions currentPermissions =
                            permissionsHolder[0];


                    if (currentPermissions == null) {

                        System.out.println(
                                "[UserDashboard] "
                                        + "Permissions object missing."
                        );

                        return;
                    }


                    // =========================================
                    // SAVE PRINT LIMIT
                    // =========================================

                    selectedPrintLimit =
                            currentPermissions
                                    .getSelectedPrintLimit();


                    // =========================================
                    // SAVE EXPIRY
                    // =========================================

                    selectedExpiryMinutes =
                            currentPermissions
                                    .getSelectedExpiryMinutes();


                    System.out.println(
                            "[UserDashboard] Print Limit = "
                                    + selectedPrintLimit
                    );


                    System.out.println(
                            "[UserDashboard] Expiry = "
                                    + selectedExpiryMinutes
                                    + " minutes"
                    );


                    // =========================================
                    // START RAM LIFECYCLE
                    // =========================================

                    startEncryptedMemoryLifecycle();


                    // =========================================
                    // GO TO SET PIN
                    // =========================================

                    System.out.println(
                            "[UserDashboard] "
                                    + "Permissions → Set PIN"
                    );


                    SetPin setPin =
                            new SetPin(

                                    selectedDocumentId,

                                    selectedDocumentName,

                                    selectedPrintLimit,

                                    selectedExpiryMinutes,


                                    // -------------------------
                                    // RECEIVE PIN HASH
                                    // -------------------------

                                    this::setSelectedPinHash,


                                    // -------------------------
                                    // NEXT ACTION
                                    // -------------------------

                                    () -> {

                                        System.out.println(
                                                "[UserDashboard] "
                                                        + "Set PIN → "
                                                        + "Select Centre"
                                        );


                                        showSelectCentre();

                                    }
                            );


                    rightContent.getChildren().setAll(
                            setPin.getContent()
                    );

                });


        permissionsHolder[0] =
                permissions;


        // =====================================================
        // SHOW PERMISSIONS
        // =====================================================

        rightContent.getChildren().setAll(
                permissions.getContent()
        );
    }


    // =========================================================
    // RECEIVE ENCRYPTED DOCUMENT
    // =========================================================

    public synchronized void setEncryptedDocumentMemory(
            byte[] encryptedData) {

        memoryLifecycleManager
                .cancelActiveLifecycle();


        clearEncryptedDocumentMemory();


        if (encryptedData == null
                || encryptedData.length == 0) {

            System.out.println(
                    "[UserDashboard] "
                            + "No encrypted document memory received."
            );

            return;
        }


        encryptedDocumentMemory =
                encryptedData;


        System.out.println(
                "[UserDashboard] "
                        + "Encrypted document memory received: "
                        + encryptedData.length
                        + " bytes."
        );
    }

public synchronized byte[] getEncryptedDocumentMemory() {

    return encryptedDocumentMemory;
}
    // =========================================================
    // RECEIVE AES KEY
    // =========================================================
    //
    // Called by UploadDocument.java after generating
    // the random AES-256 key.
    //
    // The key remains ONLY in RAM.
    // =========================================================

    public synchronized void setAesKey(
            byte[] aesKey) {

        // Clear previously stored key first
        clearAesKey();


        if (aesKey == null
                || aesKey.length == 0) {

            System.out.println(
                    "[UserDashboard] "
                            + "No AES key received."
            );

            return;
        }


        this.aesKey =
                aesKey;


        System.out.println(
                "[UserDashboard] "
                        + "AES key received and stored in RAM."
        );


        System.out.println(
                "[UserDashboard] AES key length = "
                        + aesKey.length
                        + " bytes."
        );
    }


    // =========================================================
    // GET AES KEY
    // =========================================================
    //
    // Secure Viewer will use this to decrypt the encrypted
    // document.
    // =========================================================

    public synchronized byte[] getAesKey() {

        return aesKey;
    }


    // =========================================================
    // CLEAR AES KEY
    // =========================================================

    public synchronized void clearAesKey() {

        if (aesKey != null) {

            Arrays.fill(
                    aesKey,
                    (byte) 0
            );


            aesKey = null;


            System.out.println(
                    "[UserDashboard] "
                            + "AES key cleared from RAM."
            );
        }
    }


    // =========================================================
    // START ENCRYPTED MEMORY LIFECYCLE
    // =========================================================

    private synchronized void startEncryptedMemoryLifecycle() {

        if (encryptedDocumentMemory == null
                || encryptedDocumentMemory.length == 0) {

            System.out.println(
                    "[UserDashboard] "
                            + "No encrypted document memory "
                            + "available for lifecycle."
            );

            return;
        }


        System.out.println(
                "[UserDashboard] "
                        + "Starting encrypted document RAM lifecycle."
        );


        System.out.println(
                "[UserDashboard] Lifecycle = "
                        + selectedExpiryMinutes
                        + " minutes."
        );


        memoryLifecycleManager.startLifecycle(

                encryptedDocumentMemory,

                selectedExpiryMinutes,

                unused -> {

                    synchronized (
                            UserDashboard.this
                    ) {

                        // Clear encrypted bytes
                        if (encryptedDocumentMemory != null) {

                            Arrays.fill(
                                    encryptedDocumentMemory,
                                    (byte) 0
                            );
                        }


                        encryptedDocumentMemory =
                                null;


                        // Clear AES key
                        if (aesKey != null) {

                            Arrays.fill(
                                    aesKey,
                                    (byte) 0
                            );
                        }


                        aesKey =
                                null;

                    }


                    System.out.println(
                            "[UserDashboard] "
                                    + "Encrypted document RAM "
                                    + "reference removed."
                    );


                    System.out.println(
                            "[UserDashboard] "
                                    + "AES key cleared from RAM "
                                    + "after lifecycle expiry."
                    );

                }
        );
    }


    // =========================================================
    // CLEAR ENCRYPTED DOCUMENT MEMORY
    // =========================================================

    private synchronized void clearEncryptedDocumentMemory() {

        if (encryptedDocumentMemory != null) {

            Arrays.fill(
                    encryptedDocumentMemory,
                    (byte) 0
            );


            encryptedDocumentMemory =
                    null;


            System.out.println(
                    "[UserDashboard] "
                            + "Encrypted document memory "
                            + "cleared from RAM."
            );
        }
    }


    // =========================================================
    // SELECT CENTRE
    // =========================================================

    private void showSelectCentre() {

        System.out.println(
                "[UserDashboard] Opening Select Centre..."
        );


        // =====================================================
        // UPDATE LEFT SIDEBAR
        // =====================================================

        selectSelectCentreMenu();


        disposeCurrentDashboard();


        System.out.println(
                "[UserDashboard] Document ID = "
                        + selectedDocumentId
        );


        System.out.println(
                "[UserDashboard] Document Name = "
                        + selectedDocumentName
        );


        System.out.println(
                "[UserDashboard] Print Limit = "
                        + selectedPrintLimit
        );


        System.out.println(
                "[UserDashboard] Expiry = "
                        + selectedExpiryMinutes
                        + " minutes"
        );


        System.out.println(
                "[UserDashboard] PIN Hash available = "
                        + (selectedPinHash != null
                        && !selectedPinHash.isBlank())
        );


        // =====================================================
        // CREATE SELECT CENTRE
        // =====================================================

        SelectCentre selectCentre =
                new SelectCentre(

                        selectedDocumentId,

                        selectedDocumentName,

                        selectedPrintLimit,

                        selectedExpiryMinutes,

                        selectedPinHash,

                        aesKey,

                        () -> {

                            System.out.println(
                                    "[UserDashboard] "
                                            + "Select Centre → "
                                            + "Request Status"
                            );


                            showRequestStatus();

                        }
                );


        rightContent.getChildren().setAll(
                selectCentre.getContent()
        );
    }


    // =========================================================
    // REQUEST STATUS
    // =========================================================

    private void showRequestStatus() {

        System.out.println(
                "[UserDashboard] Opening Request Status..."
        );


        selectRequestStatusMenu();


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new RequestStatus()
                        .getContent()
        );
    }


    // =========================================================
    // MY DOCUMENTS
    // =========================================================

    private void showMyDocuments() {

        System.out.println(
                "[UserDashboard] Opening My Documents..."
        );


        selectMenuByName(
                "My Documents"
        );


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new MyDocuments()
                        .getContent()
        );
    }


    // =========================================================
    // ACTIVITY
    // =========================================================

    private void showActivity() {

        System.out.println(
                "[UserDashboard] Opening Activity..."
        );


        selectActivityMenu();


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new Activity()
                        .getContent()
        );
    }


    // =========================================================
    // PRIVACY RECEIPT
    // =========================================================
/* 
    private void showPrivacyReceipt() {

        System.out.println(
                "[UserDashboard] Opening Privacy Receipt..."
        );


        selectMenuByName(
                "Privacy Receipt"
        );


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new PrivacyReceipt()
                        .getContent()
        );
    }
*/

    // =========================================================
    // NOTIFICATIONS
    // =========================================================

  /*   private void showNotifications() {

        System.out.println(
                "[UserDashboard] Opening Notifications..."
        );


        selectNotificationsMenu();


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new Notifications()
                        .getContent()
        );
    }
*/

    // =========================================================
    // PROFILE
    // =========================================================

    private void showProfile() {

        System.out.println(
                "[UserDashboard] Opening Profile..."
        );


        selectMenuByName(
                "Profile"
        );


        disposeCurrentDashboard();


        rightContent.getChildren().setAll(
                new Profile()
                        .getContent()
        );
    }


    // =========================================================
    // WORKFLOW DATA GETTERS
    // =========================================================

    public String getSelectedDocumentId() {

        return selectedDocumentId;
    }


    public String getSelectedDocumentName() {

        return selectedDocumentName;
    }


    public int getSelectedPrintLimit() {

        return selectedPrintLimit;
    }


    public int getSelectedExpiryMinutes() {

        return selectedExpiryMinutes;
    }


    // =========================================================
    // PIN HASH GETTER
    // =========================================================

    public String getSelectedPinHash() {

        return selectedPinHash;
    }


    // =========================================================
    // WORKFLOW DATA SETTER
    // =========================================================

    public void setSelectedDocument(
            String documentId,
            String documentName) {

        this.selectedDocumentId =
                documentId;

        this.selectedDocumentName =
                documentName;


        System.out.println(
                "[UserDashboard] Selected Document ID = "
                        + documentId
        );


        System.out.println(
                "[UserDashboard] Selected Document Name = "
                        + documentName
        );
    }


    // =========================================================
    // SET PIN HASH
    // =========================================================
    //
    // Called by SetPin.java AFTER the PIN has been hashed.
    //
    // UserDashboard does NOT create the PIN.
    // =========================================================

    public void setSelectedPinHash(
            String pinHash) {

        this.selectedPinHash =
                pinHash;


        System.out.println(
                "[UserDashboard] "
                        + "Privacy PIN hash received."
        );
    }
}

