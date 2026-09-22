
package com.myprivora.view.user;

import com.myprivora.config.CloudinaryConfig;
import com.myprivora.config.DatabaseConfig;
import com.myprivora.extras.AesSecurityService;
import com.myprivora.extras.CloudinaryService;
import com.myprivora.model.Document;
import com.myprivora.model.User;
import com.myprivora.session.SessionManager;
import com.google.cloud.firestore.Firestore;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.File;
import java.nio.file.Files;
import java.time.Instant;
import java.util.UUID;


public class UploadDocument {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private final String BACKGROUND = "#080C16";
    private final String CARD = "rgba(13, 19, 34, 0.75)";
    private final String CARD_BORDER = "rgba(255, 255, 255, 0.12)";

    private final String PURPLE = "#00F0FF";
    private final String DEEP_PURPLE = "#00B4D8";
    private final String LIGHT_PURPLE = "#33F3FF";

    private final String TEXT = "#FFFFFF";
    private final String SECONDARY_TEXT = "rgba(255, 255, 255, 0.70)";


    // =========================================================
    // DASHBOARD
    // =========================================================

    private final UserDashboard dashboard;


    // =========================================================
    // SECURITY
    // =========================================================

    private final AesSecurityService aesSecurityService =
            new AesSecurityService();


    /*
     * AES-256 key.
     *
     * Exists only in RAM while this upload workflow is active.
     */
    private byte[] aesKey;


    /*
     * Encrypted document.
     *
     * Also exists only in RAM until transferred to dashboard.
     */
    private byte[] encryptedDocumentMemory;


    // =========================================================
    // CLOUDINARY
    // =========================================================

    private final CloudinaryService cloudinaryService;


    // =========================================================
    // FIRESTORE
    // =========================================================

    private final Firestore firestore =
            DatabaseConfig.getFirestore();


    // =========================================================
    // FILE INFORMATION
    // =========================================================

    private File selectedDocumentFile;


    /*
     * Category is currently used by the UI.
     *
     * Document.java currently does not contain a category field.
     */
    private String selectedCategory = "Other";


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public UploadDocument(UserDashboard dashboard) {

        this.dashboard = dashboard;

        /*
         * Get Cloudinary configuration from the shared
         * CloudinaryConfig class.
         *
         * No credentials are stored in this class.
         */
        this.cloudinaryService =
                CloudinaryConfig.getService();
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public ScrollPane getContent() {

        VBox root =
                new VBox(24);

        root.setPadding(
                new Insets(
                        30,
                        35,
                        35,
                        35
                )
        );

        root.setStyle(
                "-fx-background-color: " + BACKGROUND + ";"
        );


        // =====================================================
        // HEADER
        // =====================================================

        Label title =
                new Label("Upload document");

        title.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Upload and protect your document before sharing it."
                );

        subtitle.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 14px;"
        );


        VBox header =
                new VBox(
                        6,
                        title,
                        subtitle
                );


        // =====================================================
        // UPLOAD CARD
        // =====================================================

        VBox uploadCard =
                new VBox(18);

        uploadCard.setPadding(
                new Insets(25)
        );

        uploadCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 20;"
        );


        Label uploadTitle =
                new Label("Select document");

        uploadTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // DROP AREA
        // =====================================================

        StackPane dropArea =
                new StackPane();

        dropArea.setPrefHeight(200);

        dropArea.setStyle(
                "-fx-background-color: rgba(14, 22, 40, 0.65);" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: rgba(0, 240, 255, 0.35);" +
                "-fx-border-radius: 18;" +
                "-fx-border-style: dashed;"
        );

        javafx.scene.image.ImageView uploadAnim =
                com.myprivora.view.theme.ClayTheme.createImageViewSafe("/assets/animations/cloud_upload.gif", 54, 54);

        Label uploadText =
                new Label(
                        "Click to select a document"
                );

        uploadText.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;"
        );


        Label supportedText =
                new Label(
                        "PDF, JPG, JPEG, PNG • Maximum 20 MB • Zero-Knowledge Encrypted"
                );

        supportedText.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        VBox uploadInfo =
                new VBox(
                        8,
                        uploadAnim,
                        uploadText,
                        supportedText
                );

        uploadInfo.setAlignment(
                Pos.CENTER
        );


        dropArea.getChildren().add(
                uploadInfo
        );


        // =====================================================
        // HOVER ANIMATION
        // =====================================================

        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        dropArea
                );

        scaleUp.setToX(1.02);
        scaleUp.setToY(1.02);


        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        dropArea
                );

        scaleDown.setToX(1);
        scaleDown.setToY(1);


        dropArea.setOnMouseEntered(e -> {

            scaleDown.stop();

            scaleUp.playFromStart();

            dropArea.setStyle(
                    "-fx-background-color: rgba(0, 240, 255, 0.08);" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: " + PURPLE + ";" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-style: dashed;" +
                    "-fx-effect: dropshadow(" +
                    "gaussian," +
                    "rgba(0, 240, 255, 0.35)," +
                    "18,0.3,0,2" +
                    ");"
            );
        });


        dropArea.setOnMouseExited(e -> {

            scaleUp.stop();

            scaleDown.playFromStart();

            dropArea.setStyle(
                    "-fx-background-color: rgba(14, 22, 40, 0.65);" +
                    "-fx-background-radius: 18;" +
                    "-fx-border-color: rgba(0, 240, 255, 0.35);" +
                    "-fx-border-radius: 18;" +
                    "-fx-border-style: dashed;"
            );
        });


        // =====================================================
        // FILE CHOOSER
        // =====================================================

        dropArea.setOnMouseClicked(e -> {

            Window window =
                    dropArea.getScene() != null
                            ? dropArea.getScene().getWindow()
                            : null;


            FileChooser fileChooser =
                    new FileChooser();

            fileChooser.setTitle(
                    "Select Document"
            );


            fileChooser.getExtensionFilters().addAll(

                    new FileChooser.ExtensionFilter(
                            "Supported Documents",
                            "*.pdf",
                            "*.jpg",
                            "*.jpeg",
                            "*.png"
                    ),

                    new FileChooser.ExtensionFilter(
                            "PDF Files",
                            "*.pdf"
                    ),

                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.jpg",
                            "*.jpeg",
                            "*.png"
                    )
            );


            File file =
                    fileChooser.showOpenDialog(window);


            if (file != null) {

                selectedDocumentFile = file;

                uploadText.setText(
                        file.getName()
                );

                supportedText.setText(
                        formatFileSize(
                                file.length()
                        )
                );
            }
        });


        uploadCard.getChildren().addAll(
                uploadTitle,
                dropArea
        );


        // =====================================================
        // DOCUMENT DETAILS CARD
        // =====================================================

        VBox detailsCard =
                new VBox(18);

        detailsCard.setPadding(
                new Insets(25)
        );

        detailsCard.setStyle(
                "-fx-background-color: " + CARD + ";" +
                "-fx-background-radius: 20;" +
                "-fx-border-color: " + CARD_BORDER + ";" +
                "-fx-border-radius: 20;"
        );


        Label detailsTitle =
                new Label("Document details");

        detailsTitle.setStyle(
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;"
        );


        // =====================================================
        // DOCUMENT NAME
        // =====================================================

        Label nameLabel =
                new Label("Document name");

        nameLabel.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        TextField documentName =
                new TextField();

        documentName.setPromptText(
                "Enter document name"
        );

        documentName.setStyle(
                "-fx-background-color: #100E16;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: #625B76;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #352D49;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 12;"
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        Label descriptionLabel =
                new Label("Description");

        descriptionLabel.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        TextArea description =
                new TextArea();

        description.setPromptText(
                "Enter a short description"
        );

        description.setPrefRowCount(4);

        description.setWrapText(true);

        description.setStyle(
                "-fx-control-inner-background: #100E16;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-prompt-text-fill: #625B76;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #352D49;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 12;"
        );


        // =====================================================
        // CATEGORY
        // =====================================================

        Label categoryLabel =
                new Label("Category");

        categoryLabel.setStyle(
                "-fx-text-fill: " + SECONDARY_TEXT + ";" +
                "-fx-font-size: 13px;"
        );


        HBox categoryBox =
                new HBox(10);


        Button idProofButton =
                createCategoryButton(
                        "ID Proof"
                );


        Button certificateButton =
                createCategoryButton(
                        "Certificate"
                );


        Button formButton =
                createCategoryButton(
                        "Form"
                );


        Button otherButton =
                createCategoryButton(
                        "Other"
                );


        categoryBox.getChildren().addAll(
                idProofButton,
                certificateButton,
                formButton,
                otherButton
        );


        // =====================================================
        // DEFAULT CATEGORY
        // =====================================================

        setSelectedCategoryButton(
                otherButton,
                idProofButton,
                certificateButton,
                formButton
        );


        // =====================================================
        // CATEGORY ACTIONS
        // =====================================================

        idProofButton.setOnAction(e -> {

            selectedCategory =
                    "ID Proof";

            setSelectedCategoryButton(
                    idProofButton,
                    certificateButton,
                    formButton,
                    otherButton
            );
        });


        certificateButton.setOnAction(e -> {

            selectedCategory =
                    "Certificate";

            setSelectedCategoryButton(
                    certificateButton,
                    idProofButton,
                    formButton,
                    otherButton
            );
        });


        formButton.setOnAction(e -> {

            selectedCategory =
                    "Form";

            setSelectedCategoryButton(
                    formButton,
                    idProofButton,
                    certificateButton,
                    otherButton
            );
        });


        otherButton.setOnAction(e -> {

            selectedCategory =
                    "Other";

            setSelectedCategoryButton(
                    otherButton,
                    idProofButton,
                    certificateButton,
                    formButton
            );
        });


        // =====================================================
        // CONTINUE BUTTON
        // =====================================================

        Button continueButton =
                createBlueButton(
                        "Continue"
                );


        continueButton.setOnAction(e -> {

            uploadDocument(
                    documentName.getText(),
                    description.getText()
            );
        });


        HBox buttonBox =
                new HBox(
                        continueButton
                );

        buttonBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        detailsCard.getChildren().addAll(
                detailsTitle,

                nameLabel,
                documentName,

                descriptionLabel,
                description,

                categoryLabel,
                categoryBox,

                buttonBox
        );


        // =====================================================
        // ROOT
        // =====================================================

        root.getChildren().addAll(
                header,
                uploadCard,
                detailsCard
        );


        VBox.setVgrow(
                detailsCard,
                Priority.ALWAYS
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(root);

        scrollPane.setFitToWidth(true);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background: " + BACKGROUND + ";" +
                "-fx-background-color: " + BACKGROUND + ";"
        );


        return scrollPane;
    }


    // =========================================================
    // UPLOAD DOCUMENT
    // =========================================================

    private void uploadDocument(
            String documentName,
            String description) {

        byte[] rawDocumentBytes = null;

        try {

            // =================================================
            // VALIDATE FILE
            // =================================================

            if (selectedDocumentFile == null) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "No document selected",
                        "Please select a document first."
                );

                return;
            }


            // =================================================
            // VALIDATE FILE SIZE
            // =================================================

            long fileSize =
                    selectedDocumentFile.length();

            long maxSize =
                    20L * 1024L * 1024L;


            if (fileSize > maxSize) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "File too large",
                        "Document size must be less than 20 MB."
                );

                return;
            }


            // =================================================
            // VALIDATE DOCUMENT NAME
            // =================================================

            if (documentName == null
                    || documentName.trim().isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Document name required",
                        "Please enter a document name."
                );

                return;
            }


            // =================================================
            // VALIDATE SESSION
            // =================================================

            User currentUser =
                    SessionManager.getCurrentUser();


            if (currentUser == null
                    || currentUser.getUserId() == null
                    || currentUser.getUserId().trim().isEmpty()) {

                showAlert(
                        Alert.AlertType.ERROR,
                        "Session error",
                        "No logged-in user found."
                );

                return;
            }


            // =================================================
            // START LOG
            // =================================================

            System.out.println(
                    "[UploadDocument] "
                            + "Starting secure upload..."
            );


            System.out.println(
                    "[UploadDocument] File = "
                            + selectedDocumentFile.getName()
            );


            System.out.println(
                    "[UploadDocument] Size = "
                            + fileSize
                            + " bytes"
            );


            System.out.println(
                    "[UploadDocument] Category = "
                            + selectedCategory
            );


            // =================================================
            // READ ORIGINAL FILE INTO RAM
            // =================================================

            rawDocumentBytes =
                    Files.readAllBytes(
                            selectedDocumentFile.toPath()
                    );


            System.out.println(
                    "[UploadDocument] "
                            + "Original document loaded into RAM."
            );


            // =================================================
            // GENERATE RANDOM AES-256 KEY
            // =================================================

            aesKey =
                    aesSecurityService.generateRandomKey();


            System.out.println(
                    "[UploadDocument] "
                            + "Random AES-256 key generated."
            );


            // =================================================
            // ENCRYPT DOCUMENT IN RAM
            // =================================================

            encryptedDocumentMemory =
                    aesSecurityService.encryptInMemory(
                            rawDocumentBytes,
                            aesKey
                    );


            System.out.println(
                    "[UploadDocument] "
                            + "Document encrypted in memory."
            );


            // =================================================
            // CLEAR ORIGINAL DOCUMENT
            // =================================================

            aesSecurityService.clearBytes(
                    rawDocumentBytes
            );

            rawDocumentBytes = null;


            System.out.println(
                    "[UploadDocument] "
                            + "Original document bytes cleared."
            );


            // =================================================
            // UPLOAD ENCRYPTED DATA TO CLOUDINARY
            // =================================================

            System.out.println(
                    "[UploadDocument] "
                            + "Uploading encrypted data..."
            );


            String[] uploadResult =
                    cloudinaryService
                            .uploadDirectFromMemory(
                                    encryptedDocumentMemory
                            );


            // =================================================
            // CLOUDINARY RESULT
            // =================================================

            if (uploadResult == null
                    || uploadResult.length < 3) {

                throw new IllegalStateException(
                        "Cloudinary did not return complete upload information."
                );
            }


            String uploadedUrl =
                    uploadResult[0];

            String publicId =
                    uploadResult[1];

            String resourceType =
                    uploadResult[2];


            if (uploadedUrl == null
                    || uploadedUrl.isBlank()) {

                throw new IllegalStateException(
                        "Cloudinary secure URL is missing."
                );
            }


            if (publicId == null
                    || publicId.isBlank()) {

                throw new IllegalStateException(
                        "Cloudinary public ID is missing."
                );
            }


            if (resourceType == null
                    || resourceType.isBlank()) {

                throw new IllegalStateException(
                        "Cloudinary resource type is missing."
                );
            }


            System.out.println(
                    "[UploadDocument] "
                            + "Cloudinary upload successful."
            );


            System.out.println(
                    "[UploadDocument] "
                            + "Public ID received."
            );


            System.out.println(
                    "[UploadDocument] "
                            + "Resource type received."
            );


            // =================================================
            // GENERATE FIRESTORE DOCUMENT ID
            // =================================================

            String documentId =
                    UUID.randomUUID().toString();


            // =================================================
            // CREATE UPLOAD TIMESTAMP
            // =================================================

            String uploadedAt =
                    Instant.now().toString();


            /*
             * Document expiry is controlled later by the
             * print-request / permission workflow.
             */
            String expiryTime = null;


            // =================================================
            // CREATE DOCUMENT MODEL
            // =================================================

            Document document =
                    new Document();


            document.setDocumentId(
                    documentId
            );


            document.setOwnerId(
                    currentUser.getUserId()
            );


            document.setFileName(
                    documentName.trim()
            );


            document.setFilePath(
                    uploadedUrl
            );


            document.setUploadedAt(
                    uploadedAt
            );


            document.setExpiryTime(
                    expiryTime
            );


            document.setStatus(
                    "ACTIVE"
            );


            // =================================================
            // IMPORTANT CLOUDINARY METADATA
            // =================================================
            //
            // These two values are required later when the
            // expiration service deletes the encrypted object.
            //

            document.setCloudinaryPublicId(
                    publicId
            );


            document.setCloudinaryResourceType(
                    resourceType
            );


            // =================================================
            // SAVE DOCUMENT TO FIRESTORE
            // =================================================

            firestore
                    .collection("Documents")
                    .document(documentId)
                    .set(document)
                    .get();


            System.out.println(
                    "[UploadDocument] "
                            + "Document metadata saved."
            );


            System.out.println(
                    "[UploadDocument] "
                            + "Document ID = "
                            + documentId
            );


            // =================================================
            // PASS DOCUMENT INFORMATION TO DASHBOARD
            // =================================================

            if (dashboard != null) {

                dashboard.setSelectedDocument(
                        documentId,
                        documentName.trim()
                );


                // =================================================
                // TRANSFER ENCRYPTED DOCUMENT
                // =================================================

                dashboard.setEncryptedDocumentMemory(
                        encryptedDocumentMemory
                );


                /*
                 * Ownership transferred to dashboard.
                 */
                encryptedDocumentMemory = null;


                // =================================================
                // TRANSFER AES KEY
                // =================================================

                dashboard.setAesKey(
                        aesKey
                );


                /*
                 * Ownership transferred to dashboard.
                 */
                aesKey = null;


                System.out.println(
                        "[UploadDocument] "
                                + "Encrypted document transferred "
                                + "to UserDashboard."
                );


                System.out.println(
                        "[UploadDocument] "
                                + "AES key transferred "
                                + "to UserDashboard."
                );
            }


            // =================================================
            // SUCCESS
            // =================================================

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Upload successful",
                    "Your document has been encrypted and uploaded securely."
            );


            // =================================================
            // NEXT STEP
            // =================================================

            if (dashboard != null) {

                dashboard.openPurposePage();
            }


        } catch (Exception e) {

            System.err.println(
                    "[UploadDocument] "
                            + "Secure upload failed."
            );

            e.printStackTrace();


            // =================================================
            // CLEAR ORIGINAL DATA
            // =================================================

            if (rawDocumentBytes != null) {

                aesSecurityService.clearBytes(
                        rawDocumentBytes
                );

                rawDocumentBytes = null;
            }


            // =================================================
            // CLEAR ENCRYPTED DATA
            // =================================================

            if (encryptedDocumentMemory != null) {

                aesSecurityService.clearBytes(
                        encryptedDocumentMemory
                );

                encryptedDocumentMemory = null;
            }


            // =================================================
            // CLEAR AES KEY
            // =================================================

            if (aesKey != null) {

                aesSecurityService.clearKey(
                        aesKey
                );

                aesKey = null;
            }


            // =================================================
            // ERROR
            // =================================================

            showAlert(
                    Alert.AlertType.ERROR,
                    "Upload failed",
                    "Unable to securely upload the document.\n\n"
                            + (
                            e.getMessage() != null
                                    ? e.getMessage()
                                    : "Unexpected error occurred."
                    )
            );
        }
    }


    // =========================================================
    // CATEGORY BUTTON
    // =========================================================

    private Button createCategoryButton(
            String text) {

        Button button =
                new Button(text);


        button.setPrefHeight(42);


        button.setStyle(
                "-fx-background-color: #211A2E;" +
                "-fx-text-fill: " + TEXT + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #352D49;" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 0 18;" +
                "-fx-cursor: hand;"
        );


        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );

        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);


        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );

        scaleDown.setToX(1);
        scaleDown.setToY(1);


        button.setOnMouseEntered(e -> {

            scaleDown.stop();

            scaleUp.playFromStart();


            button.setStyle(
                    "-fx-background-color: #2A2140;" +
                    "-fx-text-fill: " + TEXT + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + PURPLE + ";" +
                    "-fx-border-radius: 12;" +
                    "-fx-padding: 0 18;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                    "gaussian," +
                    "rgba(139,92,246,0.25)," +
                    "12,0.3,0,2" +
                    ");"
            );
        });


        button.setOnMouseExited(e -> {

            scaleUp.stop();

            scaleDown.playFromStart();


            button.setStyle(
                    "-fx-background-color: #211A2E;" +
                    "-fx-text-fill: " + TEXT + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #352D49;" +
                    "-fx-border-radius: 12;" +
                    "-fx-padding: 0 18;" +
                    "-fx-cursor: hand;"
            );
        });


        return button;
    }


    // =========================================================
    // SELECTED CATEGORY STYLE
    // =========================================================

    private void setSelectedCategoryButton(
            Button selected,
            Button... others) {

        selected.setStyle(
                "-fx-background-color: linear-gradient(" +
                "to right, " +
                DEEP_PURPLE + ", " +
                PURPLE +
                ");" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + LIGHT_PURPLE + ";" +
                "-fx-border-radius: 12;" +
                "-fx-padding: 0 18;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(" +
                "gaussian," +
                "rgba(139,92,246,0.40)," +
                "14,0.4,0,2" +
                ");"
        );


        for (Button button : others) {

            button.setStyle(
                    "-fx-background-color: #211A2E;" +
                    "-fx-text-fill: " + TEXT + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #352D49;" +
                    "-fx-border-radius: 12;" +
                    "-fx-padding: 0 18;" +
                    "-fx-cursor: hand;"
            );
        }
    }


    // =========================================================
    // MAIN PURPLE BUTTON
    // =========================================================

    private Button createBlueButton(
            String text) {

        Button button =
                new Button(text);


        button.setPrefHeight(46);

        button.setPrefWidth(150);


        button.setStyle(
                "-fx-background-color: linear-gradient(" +
                "to right, " +
                DEEP_PURPLE + ", " +
                PURPLE +
                ");" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(" +
                "gaussian," +
                "rgba(139,92,246,0.35)," +
                "15,0.4,0,3" +
                ");"
        );


        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );

        scaleUp.setToX(1.04);

        scaleUp.setToY(1.04);


        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );

        scaleDown.setToX(1);

        scaleDown.setToY(1);


        button.setOnMouseEntered(e -> {

            scaleDown.stop();

            scaleUp.playFromStart();


            button.setStyle(
                    "-fx-background-color: linear-gradient(" +
                    "to right, " +
                    PURPLE + ", " +
                    LIGHT_PURPLE +
                    ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 14;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                    "gaussian," +
                    "rgba(139,92,246,0.50)," +
                    "18,0.45,0,3" +
                    ");"
            );
        });


        button.setOnMouseExited(e -> {

            scaleUp.stop();

            scaleDown.playFromStart();


            button.setStyle(
                    "-fx-background-color: linear-gradient(" +
                    "to right, " +
                    DEEP_PURPLE + ", " +
                    PURPLE +
                    ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-radius: 14;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(" +
                    "gaussian," +
                    "rgba(139,92,246,0.35)," +
                    "15,0.4,0,3" +
                    ");"
            );
        });


        return button;
    }


    // =========================================================
    // FILE SIZE
    // =========================================================

    private String formatFileSize(
            long bytes) {

        if (bytes < 1024) {

            return bytes + " B";
        }


        if (bytes < 1024 * 1024) {

            return String.format(
                    "%.2f KB",
                    bytes / 1024.0
            );
        }


        return String.format(
                "%.2f MB",
                bytes / (1024.0 * 1024.0)
        );
    }


    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(
                        type,
                        message,
                        ButtonType.OK
                );


        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.showAndWait();
    }
}

