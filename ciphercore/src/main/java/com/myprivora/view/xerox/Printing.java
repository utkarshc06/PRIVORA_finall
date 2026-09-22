package com.myprivora.view.xerox;

import com.myprivora.view.theme.ClayTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class Printing {

    public VBox getContent() {

        VBox box = new VBox(22);
        box.setPadding(new Insets(35, 40, 40, 40));
        box.setStyle("-fx-background-color: " + ClayTheme.BG_OBSIDIAN + ";");

        Label title = new Label("Xerox Laser Printing Monitor");
        title.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_TEXT + ";" +
                "-fx-font-size: 32px;" +
                "-fx-font-weight: bold;"
        );

        Label subtitle = new Label("Real-time optical printhead status and cryptographic hardware spooler.");
        subtitle.setStyle(
                "-fx-text-fill: " + ClayTheme.WHITE_MUTED + ";" +
                "-fx-font-size: 15px;"
        );

        VBox header = new VBox(4, title, subtitle);

        // MAIN MONITOR CARD
        VBox monitorCard = new VBox(20);
        monitorCard.setPadding(new Insets(28));
        monitorCard.setMaxWidth(720);
        monitorCard.setStyle(ClayTheme.GLASS_CARD);

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Animated Xerox Laser Printer Feed
        ImageView printerAnim = ClayTheme.createImageViewSafe("/assets/animations/printer_feed.gif", 72, 72);

        VBox jobInfo = new VBox(4);
        Label jobName = new Label("Active Job: Aadhaar-Card-Redacted.pdf");
        jobName.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label spoolerStatus = new Label("● Hardware Spooler: Laser Head Active (600 DPI)");
        spoolerStatus.setStyle("-fx-text-fill: " + ClayTheme.CYAN_PRIMARY + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        ClayTheme.pulseGlow(spoolerStatus);

        jobInfo.getChildren().addAll(jobName, spoolerStatus);
        HBox.setHgrow(jobInfo, Priority.ALWAYS);

        Label badge = new Label("PRINTING 2/3");
        badge.setStyle(ClayTheme.BADGE_CYAN);

        topRow.getChildren().addAll(printerAnim, jobInfo, badge);

        // PROGRESS BAR
        ProgressBar progressBar = new ProgressBar(0.66);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setStyle("-fx-accent: #00F0FF; -fx-control-inner-background: rgba(14, 22, 40, 0.85);");

        HBox metaRow1 = createMetaRow("Security Sealed Watermark:", "XEROX-STATION-049 #CONFIDENTIAL");
        HBox metaRow2 = createMetaRow("Memory Lifecycle:", "AES-256 Key Scheduled for Instant Memory Wipe");
        HBox metaRow3 = createMetaRow("Optical Laser Beam:", "Calibrated (Zero Physical Toner Residue)");

        VBox metaContainer = new VBox(10, metaRow1, metaRow2, metaRow3);
        metaContainer.setPadding(new Insets(14));
        metaContainer.setStyle(
                "-fx-background-color: " + ClayTheme.BG_INPUT + ";" +
                "-fx-border-color: " + ClayTheme.FROST_BORDER + ";" +
                "-fx-border-radius: 12px;" +
                "-fx-background-radius: 12px;"
        );

        // ACTIONS
        Button pauseBtn = ClayTheme.createGlassButton("Pause Hardware Feed");
        Button verifyBtn = ClayTheme.createGlassButton("Verify Optical Watermark");
        Button completeBtn = ClayTheme.createPrimaryButton("Complete Job & Wipe Key");

        HBox actions = new HBox(12, pauseBtn, verifyBtn, completeBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        monitorCard.getChildren().addAll(topRow, progressBar, metaContainer, actions);
        ClayTheme.applyCardHover(monitorCard);

        box.getChildren().addAll(header, monitorCard);
        return box;
    }

    private HBox createMetaRow(String label, String value) {
        Label l = new Label(label);
        l.setStyle("-fx-text-fill: " + ClayTheme.WHITE_MUTED + "; -fx-font-size: 13px;");

        Label v = new Label(value);
        v.setStyle("-fx-text-fill: " + ClayTheme.WHITE_TEXT + "; -fx-font-size: 13px; -fx-font-weight: bold;");

        HBox r = new HBox(l, v);
        r.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(l, Priority.ALWAYS);
        return r;
    }
}