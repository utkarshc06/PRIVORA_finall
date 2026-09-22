
package com.myprivora.view.user;

import com.myprivora.extras.PinSecurityService;

import java.util.function.Consumer;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SetPin {

    // =========================================================
    // COLORS - 3-COLOR GLASSMORPHISM (LASER CYAN, OBSIDIAN, FROST WHITE)
    // =========================================================

    private static final String BACKGROUND = "#080C16";
    private static final String CARD = "rgba(13, 19, 34, 0.75)";
    private static final String BORDER = "rgba(255, 255, 255, 0.12)";

    private static final String PURPLE = "#00F0FF";
    private static final String DEEP_PURPLE = "#00B4D8";
    private static final String LIGHT_PURPLE = "#33F3FF";

    private static final String TEXT = "#FFFFFF";
    private static final String SECONDARY = "rgba(255, 255, 255, 0.70)";


    // =========================================================
    // DOCUMENT INFORMATION
    // =========================================================

    private final String documentId;

    private final String documentName;

    private final int printLimit;

    private final int expiryMinutes;


    // =========================================================
    // CALLBACKS
    // =========================================================

    /*
     * This callback sends the generated PIN hash
     * back to UserDashboard.
     */
    private final Consumer<String> pinHashAction;


    /*
     * This callback moves the application
     * from Set PIN → Select Centre.
     */
    private final Runnable nextAction;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SetPin(
            String documentId,
            String documentName,
            int printLimit,
            int expiryMinutes,
            Consumer<String> pinHashAction,
            Runnable nextAction) {

        this.documentId = documentId;

        this.documentName = documentName;

        this.printLimit = printLimit;

        this.expiryMinutes = expiryMinutes;

        this.pinHashAction =
                pinHashAction != null
                        ? pinHashAction
                        : hash -> {
                        };

        this.nextAction =
                nextAction != null
                        ? nextAction
                        : () -> {
                        };


        System.out.println(
                "[SetPin] Set PIN page created."
        );
    }


    // =========================================================
    // DEFAULT CONSTRUCTOR
    // =========================================================

    public SetPin() {

        this(
                null,
                null,
                3,
                5,
                hash -> {
                },
                () -> {
                }
        );
    }


    // =========================================================
    // MAIN CONTENT
    // =========================================================

    public VBox getContent() {

        System.out.println(
                "[SetPin] getContent() called."
        );


        VBox main =
                new VBox(25);


        main.setAlignment(
                Pos.TOP_CENTER
        );


        main.setPadding(
                new Insets(
                        35,
                        50,
                        40,
                        50
                )
        );


        main.setStyle(
                "-fx-background-color: "
                        + BACKGROUND
                        + ";"
        );


        // =====================================================
        // HEADER WITH LOTTIE VAULT ANIMATION
        // =====================================================

        VBox header =
                new VBox(8);


        header.setAlignment(
                Pos.CENTER
        );

        javafx.scene.image.ImageView vaultAnim =
                com.myprivora.view.theme.ClayTheme.createImageViewSafe("/assets/animations/shield_vault.gif", 56, 56);

        Label title =
                new Label(
                        "Set Privacy PIN"
                );


        title.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );


        Label subtitle =
                new Label(
                        "Create a 4-digit PIN to protect this print request."
                );


        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 15px;"
        );


        header.getChildren().addAll(
                vaultAnim,
                title,
                subtitle
        );


        // =====================================================
        // DOCUMENT INFORMATION
        // =====================================================

        String displayDocumentName =
                documentName == null
                        || documentName.isBlank()
                        ? "Selected Document"
                        : documentName;


        Label documentLabel =
                new Label(
                        "Document: "
                                + displayDocumentName
                );


        documentLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 13px;"
        );


        // =====================================================
        // PIN CARD
        // =====================================================

        VBox card =
                new VBox(16);


        card.setAlignment(
                Pos.CENTER
        );


        card.setPadding(
                new Insets(35)
        );


        card.setMaxWidth(
                500
        );


        card.setStyle(
                "-fx-background-color: "
                        + CARD
                        + ";" +
                "-fx-border-color: "
                        + BORDER
                        + ";" +
                "-fx-border-width: 1;" +
                "-fx-border-radius: 22;" +
                "-fx-background-radius: 22;" +
                "-fx-effect: dropshadow(" +
                "gaussian, rgba(0,0,0,0.45), " +
                "25, 0.25, 0, 8);"
        );


        // =====================================================
        // FIRST PIN
        // =====================================================

        Label pinLabel =
                new Label(
                        "Enter 4-Digit PIN"
                );


        pinLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );


        PasswordField pinField =
                new PasswordField();


        pinField.setPromptText(
                "Enter PIN"
        );


        pinField.setPrefWidth(
                300
        );


        pinField.setPrefHeight(
                48
        );


        pinField.setAlignment(
                Pos.CENTER
        );


        stylePinField(
                pinField
        );


        // =====================================================
        // CONFIRM PIN
        // =====================================================

        Label confirmLabel =
                new Label(
                        "Confirm PIN"
                );


        confirmLabel.setStyle(
                "-fx-text-fill: "
                        + TEXT
                        + ";" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;"
        );


        PasswordField confirmPinField =
                new PasswordField();


        confirmPinField.setPromptText(
                "Re-enter PIN"
        );


        confirmPinField.setPrefWidth(
                300
        );


        confirmPinField.setPrefHeight(
                48
        );


        confirmPinField.setAlignment(
                Pos.CENTER
        );


        stylePinField(
                confirmPinField
        );


        // =====================================================
        // MESSAGE
        // =====================================================

        Label message =
                new Label();


        message.setWrapText(
                true
        );


        message.setAlignment(
                Pos.CENTER
        );


        message.setMaxWidth(
                350
        );


        message.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 13px;"
        );


        // =====================================================
        // CONTINUE BUTTON
        // =====================================================

        Button continueButton =
                createBlueButton(
                        "Continue"
                );


        // =====================================================
        // CONTINUE ACTION
        // =====================================================

        continueButton.setOnAction(
                event -> {

                    System.out.println(
                            "================================================="
                    );

                    System.out.println(
                            "[SetPin] CONTINUE CLICKED"
                    );


                    String pin =
                            pinField.getText();


                    String confirmPin =
                            confirmPinField.getText();


                    // -----------------------------------------
                    // CHECK PIN
                    // -----------------------------------------

                    if (pin == null
                            || !pin.matches("\\d{4}")) {

                        message.setText(
                                "PIN must contain exactly 4 digits."
                        );

                        message.setStyle(
                                "-fx-text-fill: #F87171;" +
                                "-fx-font-size: 13px;"
                        );

                        System.out.println(
                                "[SetPin] ERROR: Invalid PIN."
                        );

                        return;
                    }


                    // -----------------------------------------
                    // CHECK CONFIRM PIN
                    // -----------------------------------------

                    if (confirmPin == null
                            || !confirmPin.matches("\\d{4}")) {

                        message.setText(
                                "Please confirm your 4-digit PIN."
                        );

                        message.setStyle(
                                "-fx-text-fill: #F87171;" +
                                "-fx-font-size: 13px;"
                        );

                        System.out.println(
                                "[SetPin] ERROR: Invalid confirmation PIN."
                        );

                        return;
                    }


                    // -----------------------------------------
                    // COMPARE PIN
                    // -----------------------------------------

                    if (!pin.equals(confirmPin)) {

                        message.setText(
                                "PINs do not match."
                        );

                        message.setStyle(
                                "-fx-text-fill: #F87171;" +
                                "-fx-font-size: 13px;"
                        );

                        System.out.println(
                                "[SetPin] ERROR: PIN mismatch."
                        );

                        return;
                    }


                    // -----------------------------------------
                    // HASH PIN
                    // -----------------------------------------

                    try {

                        String pinHash =
                                PinSecurityService.hashPin(
                                        pin
                                );


                        System.out.println(
                                "[SetPin] PIN validation successful."
                        );

                        System.out.println(
                                "[SetPin] PIN hash generated."
                        );

                        System.out.println(
                                "[SetPin] Raw PIN will NOT be stored."
                        );


                        // -------------------------------------
                        // SEND HASH TO USER DASHBOARD
                        // -------------------------------------

                        pinHashAction.accept(
                                pinHash
                        );


                        System.out.println(
                                "[SetPin] PIN hash sent to UserDashboard."
                        );


                        // -------------------------------------
                        // SUCCESS MESSAGE
                        // -------------------------------------

                        message.setText(
                                "PIN secured successfully."
                        );


                        message.setStyle(
                                "-fx-text-fill: #86EFAC;" +
                                "-fx-font-size: 13px;"
                        );


                        // -------------------------------------
                        // MOVE TO SELECT CENTRE
                        // -------------------------------------

                        System.out.println(
                                "[SetPin] Moving to Select Centre..."
                        );


                        nextAction.run();


                        System.out.println(
                                "[SetPin] Navigation completed."
                        );


                        System.out.println(
                                "================================================="
                        );


                    } catch (Exception ex) {

                        System.out.println(
                                "[SetPin] ERROR while hashing PIN:"
                        );

                        ex.printStackTrace();


                        message.setText(
                                "Unable to secure PIN. Please try again."
                        );


                        message.setStyle(
                                "-fx-text-fill: #F87171;" +
                                "-fx-font-size: 13px;"
                        );
                    }
                }
        );


        // =====================================================
        // CARD CONTENT
        // =====================================================

        card.getChildren().addAll(
                pinLabel,
                pinField,
                confirmLabel,
                confirmPinField,
                message,
                continueButton
        );


        // =====================================================
        // SECURITY INFORMATION
        // =====================================================

        Label securityInfo =
                new Label(
                        "Your PIN is converted into a secure hash. "
                                + "The original PIN is never stored."
                );


        securityInfo.setWrapText(
                true
        );


        securityInfo.setMaxWidth(
                500
        );


        securityInfo.setAlignment(
                Pos.CENTER
        );


        securityInfo.setStyle(
                "-fx-text-fill: "
                        + SECONDARY
                        + ";" +
                "-fx-font-size: 12px;"
        );


        // =====================================================
        // ADD EVERYTHING
        // =====================================================

        main.getChildren().addAll(
                header,
                documentLabel,
                card,
                securityInfo
        );


        return main;
    }


    // =========================================================
    // PIN FIELD STYLE
    // =========================================================

    private void stylePinField(
            PasswordField field) {

        field.setStyle(
                "-fx-background-color: #101620;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #302A40;" +
                "-fx-border-radius: 12;" +
                "-fx-border-width: 1;" +
                "-fx-text-fill: #F5F3FF;" +
                "-fx-prompt-text-fill: #6F687D;" +
                "-fx-font-size: 18px;" +
                "-fx-padding: 12px;" +
                "-fx-alignment: center;"
        );


        field.setOnMouseEntered(
                e -> {

                    field.setStyle(
                            "-fx-background-color: #101620;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #6D28D9;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-width: 1;" +
                            "-fx-text-fill: #F5F3FF;" +
                            "-fx-prompt-text-fill: #6F687D;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 12px;" +
                            "-fx-alignment: center;" +
                            "-fx-effect: dropshadow(" +
                            "gaussian, rgba(139,92,246,0.25), " +
                            "10, 0.3, 0, 2);"
                    );
                }
        );


        field.setOnMouseExited(
                e -> {

                    field.setStyle(
                            "-fx-background-color: #101620;" +
                            "-fx-background-radius: 12;" +
                            "-fx-border-color: #302A40;" +
                            "-fx-border-radius: 12;" +
                            "-fx-border-width: 1;" +
                            "-fx-text-fill: #F5F3FF;" +
                            "-fx-prompt-text-fill: #6F687D;" +
                            "-fx-font-size: 18px;" +
                            "-fx-padding: 12px;" +
                            "-fx-alignment: center;"
                    );
                }
        );
    }


    // =========================================================
    // BLUE BUTTON
    // =========================================================

    private Button createBlueButton(
            String text) {

        Button button =
                new Button(text);


        button.setPrefWidth(
                300
        );


        button.setPrefHeight(
                48
        );


        button.setStyle(
                "-fx-background-color: linear-gradient(to right, "
                        + PURPLE + ", "
                        + DEEP_PURPLE + ");" +
                "-fx-border-color: transparent;" +
                "-fx-border-radius: 14;" +
                "-fx-background-radius: 14;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(" +
                "gaussian, rgba(139,92,246,0.35), " +
                "15, 0.3, 0, 4);"
        );


        // =====================================================
        // SCALE ANIMATION
        // =====================================================

        ScaleTransition scaleUp =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );


        scaleUp.setToX(
                1.03
        );


        scaleUp.setToY(
                1.03
        );


        ScaleTransition scaleDown =
                new ScaleTransition(
                        Duration.millis(120),
                        button
                );


        scaleDown.setToX(
                1
        );


        scaleDown.setToY(
                1
        );


        // =====================================================
        // HOVER IN
        // =====================================================

        button.setOnMouseEntered(
                e -> {

                    scaleDown.stop();

                    scaleUp.playFromStart();


                    button.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + LIGHT_PURPLE + ", "
                                    + PURPLE + ");" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(" +
                            "gaussian, rgba(139,92,246,0.55), " +
                            "18, 0.4, 0, 5);"
                    );
                }
        );


        // =====================================================
        // HOVER OUT
        // =====================================================

        button.setOnMouseExited(
                e -> {

                    scaleUp.stop();

                    scaleDown.playFromStart();


                    button.setStyle(
                            "-fx-background-color: linear-gradient(to right, "
                                    + PURPLE + ", "
                                    + DEEP_PURPLE + ");" +
                            "-fx-border-color: transparent;" +
                            "-fx-border-radius: 14;" +
                            "-fx-background-radius: 14;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-cursor: hand;" +
                            "-fx-effect: dropshadow(" +
                            "gaussian, rgba(139,92,246,0.35), " +
                            "15, 0.3, 0, 4);"
                    );
                }
        );


        return button;
    }
}

