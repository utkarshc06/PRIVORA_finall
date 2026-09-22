package com.myprivora.view.user;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PrivacyReceipt {

    public VBox getContent() {

        VBox box = new VBox(22);
        box.setPadding(new Insets(35, 40, 40, 40));
        box.setStyle("-fx-background-color: " + ClayTheme.BG_OBSIDIAN + ";");

        Label title = new Label("Privacy & Audit Receipts");
        title.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Cryptographically signed digital vouchers verifying zero data retention.");
        subtitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 14px;"
        );

        VBox header = new VBox(4, title, subtitle);

        // CERTIFICATE CARD
        VBox receiptCard = new VBox(18);
        receiptCard.setPadding(new Insets(28));
        receiptCard.setMaxWidth(680);
        receiptCard.setStyle(ClayTheme.GLASS_CARD);

        HBox topRow = new HBox(16);
        topRow.setAlignment(Pos.CENTER_LEFT);

        ImageView shieldAnim = ClayTheme.createImageViewSafe("/assets/animations/shield_vault.gif", 52, 52);

        VBox titleArea = new VBox(2);
        Label docTitle = new Label("Zero-Trust Verification Voucher");
        docTitle.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label certId = new Label("Voucher ID: PRV-2026-X794-SEC");
        certId.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        titleArea.getChildren().addAll(docTitle, certId);
        HBox.setHgrow(titleArea, Priority.ALWAYS);

        Label badge = new Label("VERIFIED SECURE");
        badge.setStyle(ClayTheme.BADGE_CYAN);

        topRow.getChildren().addAll(shieldAnim, titleArea, badge);

        // METRICS GRID
        HBox row1 = createMetricRow("Encryption Standard:", "AES-256-GCM Hardware Sealed");
        HBox row2 = createMetricRow("Storage Policy:", "Ephemerally Purged from Server Memory");
        HBox row3 = createMetricRow("Print Operator Limit:", "Cryptographically Capped (3/3)");
        HBox row4 = createMetricRow("Watermark Status:", "Embedded Device ID & Timestamp");

        VBox metricsBox = new VBox(10, row1, row2, row3, row4);
        metricsBox.setPadding(new Insets(14));
        metricsBox.setStyle(
                "-fx-background-color: " + ClayTheme.BG_INPUT + ";" +
                "-fx-border-color: " + ClayTheme.FROST_BORDER + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;"
        );

        Button exportBtn = ClayTheme.createPrimaryButton("Export Signed Verification Record");

        receiptCard.getChildren().addAll(topRow, metricsBox, exportBtn);
        ClayTheme.applyCardHover(receiptCard);

        box.getChildren().addAll(header, receiptCard);
        return box;
    }

    private HBox createMetricRow(String key, String value) {
        Label keyLabel = new Label(key);
        keyLabel.setStyle("-fx-text-fill: " + ClayTheme.WHITE_MUTED + "; -fx-font-size: 13px;");

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox row = new HBox(keyLabel, valLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(keyLabel, Priority.ALWAYS);
        return row;
    }
}