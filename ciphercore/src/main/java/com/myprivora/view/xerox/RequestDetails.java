
package com.myprivora.view.xerox;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Base64;

import com.myprivora.dao.DocumentDAO;
import com.myprivora.dao.PrintRequestDAO;
import com.myprivora.extras.AesSecurityService;
import com.myprivora.extras.CloudinaryService;
import com.myprivora.extras.KeyProtectionService;
import com.myprivora.extras.PinSecurityService;
import com.myprivora.extras.SecurePrintService;
import com.myprivora.model.Document;
import com.myprivora.model.PrintRequest;
import com.myprivora.session.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * ============================================================
 * REQUEST DETAILS
 * ============================================================
 *
 * Xerox-side secure printing workflow:
 *
 * PENDING
 *    ↓
 * ACCEPTED
 *    ↓
 * Enter PIN
 *    ↓
 * APPROVED
 *    ↓
 * Click PRINT
 *    ↓
 * PRINTING
 *    ↓
 * RSA decrypt AES key
 *    ↓
 * Download encrypted document
 *    ↓
 * AES-GCM decrypt ONLY in RAM
 *    ↓
 * PDFBox
 *    ↓
 * PrinterJob
 *    ↓
 * COMPLETED / APPROVED
 *    ↓
 * Printing Done Successfully Scene
 *
 * No Secure Viewer.
 * No plaintext file saved to disk.
 *
 * ============================================================
 */
public class RequestDetails {

    // =========================================================
    // CURRENT REQUEST
    // =========================================================

    private final PrintRequest request;


    // =========================================================
    // DAOS
    // =========================================================

    private final PrintRequestDAO printRequestDAO =
            new PrintRequestDAO();

    private final DocumentDAO documentDAO =
            new DocumentDAO();


    // =========================================================
    // SECURITY SERVICES
    // =========================================================

    private final KeyProtectionService keyProtectionService =
            new KeyProtectionService();

    private final AesSecurityService aesSecurityService =
            new AesSecurityService();

    private final SecurePrintService securePrintService =
            new SecurePrintService();


    // =========================================================
    // PIN STATE
    // =========================================================

    private boolean pinVerified = false;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public RequestDetails(PrintRequest request) {

        this.request = request;
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        VBox root =
                new VBox(20);

        root.setPadding(
                new Insets(30)
        );

        root.setAlignment(
                Pos.TOP_LEFT
        );

        root.setStyle(
                "-fx-background-color: #080C16;"
        );


        // =====================================================
        // TITLE
        // =====================================================

        Label title =
                new Label(
                        "Request Details"
                );

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


        // =====================================================
        // SUBTITLE
        // =====================================================

        Label subtitle =
                new Label(
                        "Review the request and securely authorize document access."
                );

        subtitle.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        subtitle.setTextFill(
                Color.web("rgba(255,255,255,0.70)")
        );


        // =====================================================
        // REQUEST INFORMATION CARD
        // =====================================================

        VBox requestCard =
                new VBox(12);

        requestCard.setPadding(
                new Insets(20)
        );

        requestCard.setStyle(
                "-fx-background-color: rgba(13, 19, 34, 0.75);" +
                "-fx-background-radius: 16;" +
                "-fx-border-color: rgba(255, 255, 255, 0.12);" +
                "-fx-border-radius: 16;"
        );


        Label requestTitle =
                new Label(
                        "Request Information"
                );

        requestTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        requestTitle.setTextFill(
                Color.WHITE
        );


        Label documentName =
                createInfoLabel(
                        "Document",
                        request.getDocumentName()
                );


        Label xeroxName =
                createInfoLabel(
                        "Xerox Centre",
                        request.getXeroxName()
                );


        Label copies =
                createInfoLabel(
                        "Print Copies",
                        String.valueOf(
                                request.getPrintCopies()
                        )
                );


        Label expiry =
                createInfoLabel(
                        "Expiry",
                        request.getExpiryMinutes()
                                + " minutes"
                );


        Label status =
                createInfoLabel(
                        "Status",
                        request.getStatus()
                );


        requestCard.getChildren().addAll(
                requestTitle,
                documentName,
                xeroxName,
                copies,
                expiry,
                status
        );


        // =====================================================
        // PRIVACY CARD
        // =====================================================

        VBox privacyCard =
                new VBox(12);

        privacyCard.setPadding(
                new Insets(20)
        );

        privacyCard.setStyle(
                "-fx-background-color: #18132d;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #302653;" +
                "-fx-border-radius: 14;"
        );


        Label privacyTitle =
                new Label(
                        "Privacy & Security"
                );

        privacyTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        privacyTitle.setTextFill(
                Color.WHITE
        );


        Label privacyText =
                new Label(
                        "The document is stored in encrypted form. " +
                        "The AES key is protected using the Xerox Centre's " +
                        "RSA public key. Enter the user's PIN to authorize printing."
                );

        privacyText.setWrapText(true);

        privacyText.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );

        privacyText.setTextFill(
                Color.web("#aaa4bd")
        );


        privacyCard.getChildren().addAll(
                privacyTitle,
                privacyText
        );


        // =====================================================
        // PIN CARD
        // =====================================================

        VBox pinCard =
                new VBox(15);

        pinCard.setPadding(
                new Insets(20)
        );

        pinCard.setStyle(
                "-fx-background-color: #18132d;" +
                "-fx-background-radius: 14;" +
                "-fx-border-color: #302653;" +
                "-fx-border-radius: 14;"
        );


        Label pinTitle =
                new Label(
                        "Enter Security PIN"
                );

        pinTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        18
                )
        );

        pinTitle.setTextFill(
                Color.WHITE
        );


        Label pinDescription =
                new Label(
                        "Enter the 4-digit PIN provided by the document owner."
                );

        pinDescription.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        pinDescription.setTextFill(
                Color.web("#aaa4bd")
        );


        // =====================================================
        // PIN FIELD
        // =====================================================

        javafx.scene.control.PasswordField pinField =
                new javafx.scene.control.PasswordField();

        pinField.setPromptText(
                "Enter 4-digit PIN"
        );

        pinField.setMaxWidth(
                300
        );

        pinField.setStyle(
                "-fx-background-color: #0f0b1f;" +
                "-fx-text-fill: white;" +
                "-fx-prompt-text-fill: #77718c;" +
                "-fx-border-color: #3b315e;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 12;"
        );


        // =====================================================
        // MESSAGE
        // =====================================================

        Label messageLabel =
                new Label();

        messageLabel.setWrapText(true);

        messageLabel.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        messageLabel.setTextFill(
                Color.web("#aaa4bd")
        );


        // =====================================================
        // VERIFY BUTTON
        // =====================================================

        Button verifyButton =
                createBlueButton(
                        "Verify PIN"
                );


        // =====================================================
        // PRINT BUTTON
        // =====================================================

        Button printButton =
                createBlueButton(
                        "Print"
                );

        printButton.setDisable(
                true
        );


        // =====================================================
        // REJECT BUTTON
        // =====================================================

        Button rejectButton =
                new Button(
                        "Reject Request"
                );

        rejectButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #ff6b6b;" +
                "-fx-border-color: #ff6b6b;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10 20;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // ACCEPT REQUEST
        // =====================================================

        acceptRequest();


        // =====================================================
        // VERIFY ACTION
        // =====================================================

        verifyButton.setOnAction(event -> {

            verifyPin(
                    pinField,
                    messageLabel,
                    printButton
            );

        });


        // =====================================================
        // PRINT ACTION
        // =====================================================

        printButton.setOnAction(event -> {

            printSecureDocument(
                    messageLabel,
                    printButton,
                    verifyButton
            );

        });


        // =====================================================
        // REJECT ACTION
        // =====================================================

        rejectButton.setOnAction(event -> {

            rejectRequest(
                    messageLabel,
                    printButton,
                    verifyButton
            );

        });


        // =====================================================
        // BUTTON BOX
        // =====================================================

        javafx.scene.layout.HBox buttonBox =
                new javafx.scene.layout.HBox(12);

        buttonBox.setAlignment(
                Pos.CENTER_LEFT
        );

        buttonBox.getChildren().addAll(
                verifyButton,
                printButton,
                rejectButton
        );


        // =====================================================
        // PIN CARD CONTENT
        // =====================================================

        pinCard.getChildren().addAll(
                pinTitle,
                pinDescription,
                pinField,
                buttonBox,
                messageLabel
        );


        // =====================================================
        // ROOT
        // =====================================================

        root.getChildren().addAll(
                title,
                subtitle,
                requestCard,
                privacyCard,
                pinCard
        );


        return root;
    }


    // =========================================================
    // ACCEPT REQUEST
    // =========================================================

    private void acceptRequest() {

        try {

            String currentXeroxId =
                    SessionManager.getUserId();


            if (currentXeroxId == null ||
                    currentXeroxId.isBlank()) {

                System.out.println(
                        "[RequestDetails] "
                                + "Cannot accept request: Xerox not logged in."
                );

                return;
            }


            PrintRequest latestRequest =
                    printRequestDAO.getPrintRequestById(
                            request.getRequestId()
                    );


            if (latestRequest == null) {

                System.out.println(
                        "[RequestDetails] "
                                + "Cannot accept request: request not found."
                );

                return;
            }


            if (!currentXeroxId.equals(
                    latestRequest.getXeroxId()
            )) {

                System.out.println(
                        "[SECURITY] "
                                + "Request does not belong to current Xerox."
                );

                return;
            }


            if (!"PENDING".equalsIgnoreCase(
                    latestRequest.getStatus()
            )) {

                System.out.println(
                        "[RequestDetails] "
                                + "Request is already "
                                + latestRequest.getStatus()
                );

                return;
            }


            boolean updated =
                    printRequestDAO.updateStatus(
                            latestRequest.getRequestId(),
                            "ACCEPTED"
                    );


            if (updated) {

                System.out.println(
                        "[REAL-TIME] "
                                + "Request status changed: "
                                + "PENDING → ACCEPTED"
                );

            } else {

                System.out.println(
                        "[RequestDetails] "
                                + "Failed to accept request."
                );
            }


        } catch (Exception e) {

            System.out.println(
                    "[RequestDetails] "
                            + "Accept request failed."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // VERIFY PIN
    // =========================================================

    private void verifyPin(
            javafx.scene.control.PasswordField pinField,
            Label messageLabel,
            Button printButton) {


        String enteredPin =
                pinField.getText();


        // -----------------------------------------------------
        // Validate PIN
        // -----------------------------------------------------

        if (enteredPin == null ||
                !enteredPin.matches("\\d{4}")) {

            pinVerified = false;

            printButton.setDisable(
                    true
            );

            messageLabel.setText(
                    "Please enter a valid 4-digit PIN."
            );

            messageLabel.setTextFill(
                    Color.web("#ff6b6b")
            );

            return;
        }


        try {

            // -------------------------------------------------
            // Get latest request
            // -------------------------------------------------

            PrintRequest latestRequest =
                    printRequestDAO.getPrintRequestById(
                            request.getRequestId()
                    );


            if (latestRequest == null) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                messageLabel.setText(
                        "Request could not be found."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                return;
            }


            // -------------------------------------------------
            // Check status
            // -------------------------------------------------

            String currentStatus =
                    latestRequest.getStatus();


            if (!"ACCEPTED".equalsIgnoreCase(
                    currentStatus) &&
                    !"APPROVED".equalsIgnoreCase(
                            currentStatus)) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                messageLabel.setText(
                        "This request is not available for PIN verification."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                return;
            }


            // -------------------------------------------------
            // Verify Xerox
            // -------------------------------------------------

            String currentXeroxId =
                    SessionManager.getUserId();


            if (currentXeroxId == null ||
                    !currentXeroxId.equals(
                            latestRequest.getXeroxId()
                    )) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                messageLabel.setText(
                        "Security error: this request does not belong "
                                + "to the current Xerox Centre."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                return;
            }


            // -------------------------------------------------
            // Check expiry
            // -------------------------------------------------

            if (isRequestExpired(
                    latestRequest,
                    messageLabel)) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                return;
            }


            // -------------------------------------------------
            // Get stored PIN hash
            // -------------------------------------------------

            String storedPinHash =
                    latestRequest.getPinHash();


            if (storedPinHash == null ||
                    storedPinHash.isBlank()) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                messageLabel.setText(
                        "Security error: PIN hash is missing."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                return;
            }


            // -------------------------------------------------
            // Verify PIN
            // -------------------------------------------------

            boolean pinValid =
                    PinSecurityService.verifyPin(
                            enteredPin,
                            storedPinHash
                    );


            // -------------------------------------------------
            // Incorrect PIN
            // -------------------------------------------------

            if (!pinValid) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                messageLabel.setText(
                        "Incorrect PIN. Print remains disabled."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                System.out.println(
                        "[SECURITY] PIN verification failed."
                );

                return;
            }


            // -------------------------------------------------
            // PIN VERIFIED
            // -------------------------------------------------

            pinVerified = true;

            printButton.setDisable(
                    false
            );


            // =================================================
            // REAL-TIME STATUS
            // =================================================

            boolean approved =
                    printRequestDAO.updateStatus(
                            latestRequest.getRequestId(),
                            "APPROVED"
                    );


            if (approved) {

                System.out.println(
                        "[REAL-TIME] "
                                + "Request status changed: "
                                + "ACCEPTED → APPROVED"
                );

            } else {

                System.out.println(
                        "[RequestDetails] "
                                + "Warning: PIN verified but "
                                + "APPROVED status could not be saved."
                );
            }


            messageLabel.setText(
                    "PIN verified successfully. Print is now enabled."
            );

            messageLabel.setTextFill(
                    Color.web("#62e6a7")
            );


            System.out.println(
                    "[SECURITY] PIN verification successful."
            );

            System.out.println(
                    "[SECURITY] Print button enabled."
            );


        } catch (Exception e) {

            pinVerified = false;

            printButton.setDisable(
                    true
            );

            System.out.println(
                    "[RequestDetails] PIN verification failed."
            );

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to verify PIN."
            );

            messageLabel.setTextFill(
                    Color.web("#ff6b6b")
            );
        }
    }


    // =========================================================
    // SECURE PRINT
    // =========================================================

    private void printSecureDocument(
            Label messageLabel,
            Button printButton,
            Button verifyButton) {


        // -----------------------------------------------------
        // PIN CHECK
        // -----------------------------------------------------

        if (!pinVerified) {

            messageLabel.setText(
                    "Please verify the PIN before printing."
            );

            messageLabel.setTextFill(
                    Color.web("#ff6b6b")
            );

            printButton.setDisable(
                    true
            );

            return;
        }


        // =====================================================
        // SENSITIVE MEMORY BUFFERS
        // =====================================================

        byte[] encryptedAesKey = null;

        byte[] recoveredAesKey = null;

        byte[] encryptedDocument = null;

        byte[] decryptedDocument = null;


        try {

            // =================================================
            // CURRENT XEROX ID
            // =================================================

            String currentXeroxId =
                    SessionManager.getUserId();


            if (currentXeroxId == null ||
                    currentXeroxId.isBlank()) {

                throw new SecurityException(
                        "Current Xerox Centre is not authenticated."
                );
            }


            // =================================================
            // GET LATEST REQUEST
            // =================================================

            PrintRequest latestRequest =
                    printRequestDAO.getPrintRequestById(
                            request.getRequestId()
                    );


            if (latestRequest == null) {

                throw new SecurityException(
                        "Print request could not be found."
                );
            }


            // =================================================
            // CHECK STATUS
            // =================================================

            if (!"APPROVED".equalsIgnoreCase(
                    latestRequest.getStatus())) {

                throw new SecurityException(
                        "Request is not approved for printing."
                );
            }


            // =================================================
            // CHECK XEROX OWNERSHIP
            // =================================================

            if (!currentXeroxId.equals(
                    latestRequest.getXeroxId()
            )) {

                throw new SecurityException(
                        "This request does not belong "
                                + "to the current Xerox Centre."
                );
            }


            // =================================================
            // CHECK EXPIRY
            // =================================================

            if (isRequestExpired(
                    latestRequest,
                    messageLabel)) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                return;
            }


            // =================================================
            // REAL-TIME STATUS: PRINTING
            // =================================================

            boolean printingStatusUpdated =
                    printRequestDAO.updateStatus(
                            latestRequest.getRequestId(),
                            "PRINTING"
                    );


            if (printingStatusUpdated) {

                System.out.println(
                        "[REAL-TIME] "
                                + "Request status changed: "
                                + "APPROVED → PRINTING"
                );

            } else {

                throw new SecurityException(
                        "Unable to mark request as PRINTING."
                );
            }


            // =================================================
            // GET ENCRYPTED AES KEY
            // =================================================

            String encryptedAesKeyBase64 =
                    latestRequest.getEncryptedAesKey();


            if (encryptedAesKeyBase64 == null ||
                    encryptedAesKeyBase64.isBlank()) {

                throw new SecurityException(
                        "Encrypted AES key is missing."
                );
            }


            encryptedAesKey =
                    Base64.getDecoder().decode(
                            encryptedAesKeyBase64
                    );


            System.out.println(
                    "[RequestDetails] "
                            + "Encrypted AES key received."
            );


            // =================================================
            // LOAD XEROX PRIVATE KEY
            // =================================================

            PrivateKey privateKey =
                    keyProtectionService
                            .loadXeroxPrivateKey(
                                    currentXeroxId
                            );


            if (privateKey == null) {

                throw new SecurityException(
                        "Xerox private key could not be loaded."
                );
            }


            System.out.println(
                    "[RequestDetails] "
                            + "Xerox private key loaded."
            );


            // =================================================
            // RSA DECRYPT AES KEY
            // =================================================

            recoveredAesKey =
                    keyProtectionService.decryptAesKey(
                            encryptedAesKey,
                            privateKey
                    );


            if (recoveredAesKey == null ||
                    recoveredAesKey.length != 32) {

                throw new SecurityException(
                        "Invalid recovered AES-256 key."
                );
            }


            System.out.println(
                    "[SECURITY] RSA successfully recovered "
                            + "32-byte AES-256 key."
            );


            // =================================================
            // GET DOCUMENT METADATA
            // =================================================

            Document document =
                    documentDAO.getDocumentById(
                            latestRequest.getDocumentId()
                    );


            if (document == null) {

                throw new SecurityException(
                        "Document could not be found."
                );
            }


            System.out.println(
                    "[RequestDetails] "
                            + "Document metadata found."
            );


            // =================================================
            // VERIFY DOCUMENT OWNER
            // =================================================

            if (document.getOwnerId() == null ||
                    latestRequest.getUserId() == null ||
                    !latestRequest.getUserId()
                            .equals(
                                    document.getOwnerId()
                            )) {

                throw new SecurityException(
                        "Document ownership mismatch."
                );
            }


            System.out.println(
                    "[SECURITY] Document ownership verified."
            );


            // =================================================
            // GET ENCRYPTED CLOUDINARY URL
            // =================================================

            String cloudinaryUrl =
                    document.getFilePath();


            if (cloudinaryUrl == null ||
                    cloudinaryUrl.isBlank()) {

                throw new SecurityException(
                        "Encrypted document storage URL is missing."
                );
            }


            // =================================================
            // CLOUDINARY SERVICE
            // =================================================

            CloudinaryService cloudinaryService =
                    new CloudinaryService(
                            "ubvsi7hx",
                            "839559224798321",
                            "zqghSbVOPEOHs-X4JHU4L__Rpg0"
                    );


            // =================================================
            // DOWNLOAD ENCRYPTED DOCUMENT
            // =================================================

            System.out.println(
                    "[RequestDetails] "
                            + "Downloading encrypted document..."
            );


            encryptedDocument =
                    cloudinaryService
                            .downloadDirectToMemory(
                                    cloudinaryUrl
                            );


            if (encryptedDocument == null ||
                    encryptedDocument.length == 0) {

                throw new SecurityException(
                        "Downloaded encrypted document is empty."
                );
            }


            System.out.println(
                    "[RequestDetails] "
                            + "Encrypted document downloaded."
            );


            // =================================================
            // AES-GCM DECRYPT ONLY IN MEMORY
            // =================================================

            decryptedDocument =
                    aesSecurityService.decryptInMemory(
                            encryptedDocument,
                            recoveredAesKey
                    );


            if (decryptedDocument == null ||
                    decryptedDocument.length == 0) {

                throw new SecurityException(
                        "Document decryption failed."
                );
            }


            System.out.println(
                    "[SECURITY] "
                            + "Document decrypted in RAM."
            );


            System.out.println(
                    "[SECURITY] "
                            + "No plaintext document file created."
            );


            // =================================================
            // PRINT
            // =================================================

            int copies =
                    latestRequest.getPrintCopies();


            if (copies <= 0) {

                copies = 1;
            }


            System.out.println(
                    "[RequestDetails] "
                            + "Sending document to printer."
            );


            System.out.println(
                    "[RequestDetails] "
                            + "Copies = "
                            + copies
            );


            // =================================================
            // ACTUAL SECURE PRINT
            // =================================================

            securePrintService.printInMemoryDocument(
                    decryptedDocument,
                    copies,
                    null
            );


            // =================================================
            // PRINT SUCCESS
            // =================================================

            System.out.println(
                    "[RequestDetails] "
                            + "Print job completed successfully."
            );


            // =================================================
            // UPDATE PRINTED COUNT
            // =================================================

            int previousPrintedCount =
                    latestRequest.getPrintedCount();


            int newPrintedCount =
                    previousPrintedCount
                            + copies;


            latestRequest.setPrintedCount(
                    newPrintedCount
            );


            // =================================================
            // UPDATE STATUS
            // =================================================

            if (newPrintedCount >=
                    latestRequest.getPrintCopies()) {

                latestRequest.setStatus(
                        "COMPLETED"
                );

            } else {

                /*
                 * If the requested number of copies has not
                 * yet been reached, keep the request approved
                 * for another print operation.
                 */

                latestRequest.setStatus(
                        "APPROVED"
                );
            }


            // =================================================
            // SAVE UPDATED REQUEST
            // =================================================

            boolean updated =
                    printRequestDAO.updatePrintRequest(
                            latestRequest
                    );


            if (!updated) {

                System.out.println(
                        "[RequestDetails] "
                                + "Warning: print succeeded but "
                                + "request update failed."
                );

            } else {

                System.out.println(
                        "[REAL-TIME] "
                                + "Request status updated to "
                                + latestRequest.getStatus()
                );

                System.out.println(
                        "[REAL-TIME] "
                                + "Printed count = "
                                + newPrintedCount
                                + " / "
                                + latestRequest.getPrintCopies()
                );
            }


            // =================================================
            // SUCCESS MESSAGE
            // =================================================

            if ("COMPLETED".equalsIgnoreCase(
                    latestRequest.getStatus())) {

                messageLabel.setText(
                        "All requested copies have been printed successfully."
                );

            } else {

                messageLabel.setText(
                        "Print job completed successfully."
                );
            }

            messageLabel.setTextFill(
                    Color.web("#62e6a7")
            );


            // =================================================
            // DISABLE BUTTONS IF COMPLETED
            // =================================================

            if ("COMPLETED".equalsIgnoreCase(
                    latestRequest.getStatus())) {

                printButton.setDisable(
                        true
                );

                verifyButton.setDisable(
                        true
                );

                pinVerified = false;
            }


            // =================================================
            // SHOW PRINT SUCCESS SCENE
            // =================================================
            //
            // This is intentionally AFTER:
            //
            // 1. Secure document processing
            // 2. PrinterJob.print()
            // 3. Printed count update
            // 4. Firestore request update
            //
            // Therefore the success scene is shown only
            // after the print operation succeeds.
            //
            // =================================================

            showPrintSuccessScene(
                    printButton
            );


        } catch (Exception e) {

            System.out.println(
                    "[RequestDetails] "
                            + "Secure printing failed."
            );

            e.printStackTrace();


            messageLabel.setText(
                    "Secure printing failed: "
                            + (
                            e.getMessage() == null
                                    ? "Unknown error."
                                    : e.getMessage()
                    )
            );

            messageLabel.setTextFill(
                    Color.web("#ff6b6b")
            );

        } finally {

            // =================================================
            // CLEAR PLAINTEXT DOCUMENT
            // =================================================

            if (decryptedDocument != null) {

                aesSecurityService.clearBytes(
                        decryptedDocument
                );

                System.out.println(
                        "[SECURITY] "
                                + "Plaintext document cleared from RAM."
                );
            }


            // =================================================
            // CLEAR ENCRYPTED DOCUMENT
            // =================================================

            if (encryptedDocument != null) {

                aesSecurityService.clearBytes(
                        encryptedDocument
                );

                System.out.println(
                        "[SECURITY] "
                                + "Encrypted document buffer cleared."
                );
            }


            // =================================================
            // CLEAR AES KEY
            // =================================================

            if (recoveredAesKey != null) {

                aesSecurityService.clearKey(
                        recoveredAesKey
                );

                System.out.println(
                        "[SECURITY] "
                                + "Recovered AES key cleared."
                );
            }


            // =================================================
            // CLEAR ENCRYPTED AES KEY
            // =================================================

            if (encryptedAesKey != null) {

                aesSecurityService.clearBytes(
                        encryptedAesKey
                );

                System.out.println(
                        "[SECURITY] "
                                + "Encrypted AES key cleared."
                );
            }
        }
    }


    // =========================================================
    // PRINT SUCCESS SCENE
    // =========================================================
    //
    // Small popup displayed after successful printing.
    //
    // =========================================================

    private void showPrintSuccessScene(
            Button printButton) {

        // =====================================================
        // CREATE SUCCESS WINDOW
        // =====================================================

        Stage successStage =
                new Stage();

        successStage.initStyle(
                StageStyle.UNDECORATED
        );

        successStage.initModality(
                Modality.APPLICATION_MODAL
        );


        // =====================================================
        // SUCCESS ICON
        // =====================================================

        Label successIcon =
                new Label("✓");

        successIcon.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        42
                )
        );

        successIcon.setTextFill(
                Color.web("#62e6a7")
        );

        successIcon.setAlignment(
                Pos.CENTER
        );


        // =====================================================
        // SUCCESS TITLE
        // =====================================================

        Label successTitle =
                new Label(
                        "Printing Done Successfully"
                );

        successTitle.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        20
                )
        );

        successTitle.setTextFill(
                Color.WHITE
        );

        successTitle.setAlignment(
                Pos.CENTER
        );


        // =====================================================
        // SUCCESS DESCRIPTION
        // =====================================================

        Label successDescription =
                new Label(
                        "Your document has been printed successfully."
                );

        successDescription.setFont(
                Font.font(
                        "Arial",
                        13
                )
        );

        successDescription.setTextFill(
                Color.web("#aaa4bd")
        );

        successDescription.setWrapText(
                true
        );

        successDescription.setAlignment(
                Pos.CENTER
        );


        // =====================================================
        // OK BUTTON
        // =====================================================

        Button okButton =
                new Button(
                        "OK"
                );

        okButton.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );

        okButton.setTextFill(
                Color.WHITE
        );

        okButton.setStyle(
                "-fx-background-color: linear-gradient(" +
                        "to right, #5b3cc4, #2563eb" +
                        ");" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10 35;" +
                "-fx-cursor: hand;"
        );


        // =====================================================
        // OK BUTTON HOVER
        // =====================================================

        okButton.setOnMouseEntered(event -> {

            okButton.setStyle(
                    "-fx-background-color: linear-gradient(" +
                            "to right, #6d4de0, #3474f0" +
                            ");" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10 35;" +
                    "-fx-cursor: hand;"
            );
        });


        okButton.setOnMouseExited(event -> {

            okButton.setStyle(
                    "-fx-background-color: linear-gradient(" +
                            "to right, #5b3cc4, #2563eb" +
                            ");" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 10 35;" +
                    "-fx-cursor: hand;"
            );
        });


        // =====================================================
        // CLOSE SUCCESS WINDOW
        // =====================================================

        okButton.setOnAction(event -> {

            successStage.close();

        });


        // =====================================================
        // SUCCESS CONTENT
        // =====================================================

        VBox successRoot =
                new VBox(15);

        successRoot.setAlignment(
                Pos.CENTER
        );

        successRoot.setPadding(
                new Insets(30)
        );

        successRoot.setPrefWidth(
                390
        );

        successRoot.setPrefHeight(
                270
        );

        successRoot.setStyle(
                "-fx-background-color: #18132d;" +
                "-fx-background-radius: 18;" +
                "-fx-border-color: #302653;" +
                "-fx-border-radius: 18;"
        );


        successRoot.getChildren().addAll(
                successIcon,
                successTitle,
                successDescription,
                okButton
        );


        // =====================================================
        // CREATE SCENE
        // =====================================================

        Scene successScene =
                new Scene(
                        successRoot
                );


        successScene.setFill(
                Color.TRANSPARENT
        );


        // =====================================================
        // SET SCENE
        // =====================================================

        successStage.setScene(
                successScene
        );


        // =====================================================
        // USE CURRENT WINDOW AS OWNER
        // =====================================================

        if (printButton.getScene() != null &&
                printButton.getScene().getWindow() != null) {

            successStage.initOwner(
                    printButton
                            .getScene()
                            .getWindow()
            );
        }


        // =====================================================
        // SHOW SUCCESS WINDOW
        // =====================================================

        successStage.showAndWait();
    }


    // =========================================================
    // CHECK EXPIRY
    // =========================================================

    private boolean isRequestExpired(
            PrintRequest latestRequest,
            Label messageLabel) {


        String expiresAtString =
                latestRequest.getExpiresAt();


        if (expiresAtString == null ||
                expiresAtString.isBlank()) {

            return false;
        }


        try {

            Instant expiresAt =
                    Instant.parse(
                            expiresAtString
                    );


            if (Instant.now()
                    .isAfter(expiresAt)) {


                printRequestDAO.updateStatus(
                        latestRequest.getRequestId(),
                        "EXPIRED"
                );


                messageLabel.setText(
                        "This print request has expired."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );


                System.out.println(
                        "[SECURITY] "
                                + "Print request expired."
                );


                return true;
            }


        } catch (Exception e) {

            System.out.println(
                    "[RequestDetails] "
                            + "Could not parse expiry time."
            );

            e.printStackTrace();
        }


        return false;
    }


    // =========================================================
    // REJECT REQUEST
    // =========================================================

    private void rejectRequest(
            Label messageLabel,
            Button printButton,
            Button verifyButton) {


        try {

            PrintRequest latestRequest =
                    printRequestDAO.getPrintRequestById(
                            request.getRequestId()
                    );


            if (latestRequest == null) {

                messageLabel.setText(
                        "Request could not be found."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );

                return;
            }


            boolean updated =
                    printRequestDAO.updateStatus(
                            latestRequest.getRequestId(),
                            "REJECTED"
                    );


            if (updated) {

                pinVerified = false;

                printButton.setDisable(
                        true
                );

                verifyButton.setDisable(
                        true
                );


                messageLabel.setText(
                        "Request rejected successfully."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );


                System.out.println(
                        "[REAL-TIME] "
                                + "Request status changed to REJECTED."
                );

            } else {

                messageLabel.setText(
                        "Unable to reject request."
                );

                messageLabel.setTextFill(
                        Color.web("#ff6b6b")
                );
            }


        } catch (Exception e) {

            e.printStackTrace();


            messageLabel.setText(
                    "Unable to reject request."
            );

            messageLabel.setTextFill(
                    Color.web("#ff6b6b")
            );
        }
    }


    // =========================================================
    // INFORMATION LABEL
    // =========================================================

    private Label createInfoLabel(
            String title,
            String value) {


        Label label =
                new Label(
                        title
                                + ": "
                                + (
                                value == null
                                        ? "N/A"
                                        : value
                        )
                );


        label.setFont(
                Font.font(
                        "Arial",
                        14
                )
        );


        label.setTextFill(
                Color.web("#d8d3e5")
        );


        return label;
    }


    // =========================================================
    // BLUE BUTTON
    // =========================================================

    private Button createBlueButton(
            String text) {


        Button button =
                new Button(
                        text
                );


        button.setFont(
                Font.font(
                        "Arial",
                        FontWeight.BOLD,
                        14
                )
        );


        button.setTextFill(
                Color.WHITE
        );


        button.setStyle(
                "-fx-background-color: linear-gradient(" +
                        "to right, #5b3cc4, #2563eb" +
                        ");" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 12 22;" +
                "-fx-cursor: hand;"
        );


        button.setOnMouseEntered(event -> {

            if (!button.isDisabled()) {

                button.setStyle(
                        "-fx-background-color: linear-gradient(" +
                                "to right, #6d4de0, #3474f0" +
                                ");" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12 22;" +
                        "-fx-cursor: hand;"
                );
            }
        });


        button.setOnMouseExited(event -> {

            button.setStyle(
                    "-fx-background-color: linear-gradient(" +
                            "to right, #5b3cc4, #2563eb" +
                            ");" +
                    "-fx-background-radius: 10;" +
                    "-fx-padding: 12 22;" +
                    "-fx-cursor: hand;"
            );
        });


        return button;
    }
}

