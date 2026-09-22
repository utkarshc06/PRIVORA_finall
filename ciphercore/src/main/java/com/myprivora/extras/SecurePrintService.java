package com.myprivora.extras;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PageRanges;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.printing.PDFPageable;

public class SecurePrintService {

    // =========================================================
    // REAL PHYSICAL PRINTER
    // =========================================================

    /*
     * IMPORTANT:
     * This must match the printer name shown in
     * Windows -> Printers & scanners.
     *
     * If Windows shows a slightly different name,
     * change this value accordingly.
     */
    private static final String PRINTER_NAME =
            "HP LaserJet M1005";

    // =========================================================
    // GET AVAILABLE PHYSICAL PRINTERS
    // =========================================================

    public static List<String> getAvailablePrinterNames() {

        List<String> printerNames =
                new ArrayList<>();

        PrintService[] services =
                PrinterJob.lookupPrintServices();

        for (PrintService service : services) {

            String printerName =
                    service.getName();

            if (!isVirtualFilePrinter(printerName)) {

                printerNames.add(printerName);

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Physical printer available: "
                                + printerName
                );

            } else {

                System.out.println(
                        "[SECURITY LOG - SecurePrintService] "
                                + "Blocked virtual/file printer: "
                                + printerName
                );
            }
        }

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Available physical printers: "
                        + printerNames
        );

        return printerNames;
    }

    // =========================================================
    // CHECK VIRTUAL / FILE PRINTER
    // =========================================================

    public static boolean isVirtualFilePrinter(
            String printerName) {

        if (printerName == null) {
            return false;
        }

        String name =
                printerName
                        .toLowerCase()
                        .trim();

        return name.contains("microsoft print to pdf")
                || name.contains(
                        "microsoft xps document writer"
                )
                || name.contains("onenote")
                || name.contains("send to onenote")
                || name.contains("fax")
                || name.contains("pdf")
                || name.contains("file");
    }

    // =========================================================
    // FIND PRINTER BY NAME
    // =========================================================

    private static PrintService findPrinterByName(
            String targetPrinterName) {

        if (targetPrinterName == null
                || targetPrinterName.trim().isEmpty()) {

            return null;
        }

        PrintService[] services =
                PrinterJob.lookupPrintServices();

        for (PrintService service : services) {

            String printerName =
                    service.getName();

            System.out.println(
                    "[LOG - SecurePrintService] "
                            + "Checking printer: "
                            + printerName
            );

            if (printerName.equalsIgnoreCase(
                    targetPrinterName.trim()
            )) {

                // ---------------------------------------------
                // SECURITY CHECK
                // ---------------------------------------------

                if (isVirtualFilePrinter(printerName)) {

                    System.out.println(
                            "[SECURITY LOG - SecurePrintService] "
                                    + "Requested printer is virtual/file printer: "
                                    + printerName
                    );

                    return null;
                }

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Physical printer found: "
                                + printerName
                );

                return service;
            }
        }

        return null;
    }

    // =========================================================
    // FIND DEFAULT PHYSICAL PRINTER
    // =========================================================

    private static PrintService findPhysicalPrinter() {

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Searching for physical printer..."
        );

        PrintService[] services =
                PrinterJob.lookupPrintServices();

        for (PrintService service : services) {

            String printerName =
                    service.getName();

            System.out.println(
                    "[LOG - SecurePrintService] "
                            + "Checking printer: "
                            + printerName
            );

            if (!isVirtualFilePrinter(printerName)) {

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Physical printer found: "
                                + printerName
                );

                return service;
            }

            System.out.println(
                    "[SECURITY LOG - SecurePrintService] "
                            + "Skipping virtual printer: "
                            + printerName
            );
        }

        return null;
    }

    // =========================================================
    // SIMPLE PRINT
    // =========================================================

    public void printInMemoryDocument(
            byte[] documentBytes)
            throws Exception {

        printInMemoryDocument(
                documentBytes,
                1,
                PRINTER_NAME
        );
    }

    // =========================================================
    // PRINT WITH QUANTITY
    // =========================================================

    public void printInMemoryDocument(
            byte[] documentBytes,
            int quantity)
            throws Exception {

        printInMemoryDocument(
                documentBytes,
                quantity,
                PRINTER_NAME
        );
    }

    // =========================================================
    // PRINT WITH QUANTITY + SELECTED PRINTER
    // =========================================================

    public void printInMemoryDocument(
            byte[] documentBytes,
            int quantity,
            String targetPrinterName)
            throws Exception {

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Starting secure in-memory print."
        );

        // =====================================================
        // VALIDATE DOCUMENT
        // =====================================================

        if (documentBytes == null) {

            throw new IllegalArgumentException(
                    "Document memory buffer is null."
            );
        }

        if (documentBytes.length == 0) {

            throw new IllegalArgumentException(
                    "Document memory buffer is empty."
            );
        }

        // =====================================================
        // VALIDATE QUANTITY
        // =====================================================

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Print quantity must be greater than zero."
            );
        }

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Document bytes received in RAM: "
                        + documentBytes.length
        );

        // =====================================================
        // DETECT DOCUMENT TYPE
        // =====================================================

        boolean isPdf =
                isPdf(documentBytes);

        boolean isImage =
                isImage(documentBytes);

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Document type = "
                        + (isPdf
                        ? "PDF"
                        : isImage
                        ? "IMAGE"
                        : "UNKNOWN")
        );

        if (!isPdf && !isImage) {

            throw new IllegalArgumentException(
                    "Unsupported document format. "
                            + "Only PDF, PNG, JPG and JPEG are supported."
            );
        }

        // =====================================================
        // PRINTABLE PDF IN RAM
        // =====================================================

        byte[] printablePdfBytes = null;

        boolean generatedTemporaryPdf = false;

        try {

            // =================================================
            // IMAGE -> PDF IN RAM
            // =================================================

            if (isImage) {

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Image detected."
                );

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Converting image to PDF in RAM..."
                );

                printablePdfBytes =
                        convertImageToPdfInMemory(
                                documentBytes
                        );

                generatedTemporaryPdf = true;

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Image converted to PDF in RAM."
                );

            } else {

                printablePdfBytes =
                        documentBytes;
            }

            // =================================================
            // LOAD PDF FROM RAM
            // =================================================

            try (PDDocument document =
                         Loader.loadPDF(
                                 printablePdfBytes
                         )) {

                int pageCount =
                        document.getNumberOfPages();

                if (pageCount <= 0) {

                    throw new PrinterException(
                            "PDF contains no printable pages."
                    );
                }

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "PDF loaded successfully."
                );

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Page count: "
                                + pageCount
                );

                // =============================================
                // CREATE PRINTER JOB
                // =============================================

                PrinterJob printerJob =
                        PrinterJob.getPrinterJob();

                // =============================================
                // SELECT PHYSICAL PRINTER
                // =============================================

                PrintService selectedPrinter;

                if (targetPrinterName != null
                        && !targetPrinterName
                        .trim()
                        .isEmpty()) {

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "Requested printer: "
                                    + targetPrinterName
                    );

                    selectedPrinter =
                            findPrinterByName(
                                    targetPrinterName
                            );

                    if (selectedPrinter == null) {

                        throw new PrinterException(
                                "Physical printer was not found: "
                                        + targetPrinterName
                                        + "\n\n"
                                        + "Check Windows Printers & scanners "
                                        + "and verify the exact printer name."
                        );
                    }

                } else {

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "No printer name supplied."
                    );

                    selectedPrinter =
                            findPhysicalPrinter();

                    if (selectedPrinter == null) {

                        throw new PrinterException(
                                "No physical printer is available."
                        );
                    }
                }

                // =============================================
                // SECURITY CHECK
                // =============================================

                if (isVirtualFilePrinter(
                        selectedPrinter.getName()
                )) {

                    throw new SecurityException(
                            "Printing blocked. "
                                    + "Virtual/file printers are not allowed: "
                                    + selectedPrinter.getName()
                    );
                }

                // =============================================
                // SET PHYSICAL PRINTER
                // =============================================

                printerJob.setPrintService(
                        selectedPrinter
                );

                String printerName =
                        selectedPrinter.getName();

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Physical printer selected: "
                                + printerName
                );

                // =============================================
                // VERIFY ACTIVE PRINTER
                // =============================================

                PrintService activePrinter =
                        printerJob.getPrintService();

                if (activePrinter == null) {

                    throw new PrinterException(
                            "No active printer is available."
                    );
                }

                String activePrinterName =
                        activePrinter.getName();

                System.out.println(
                        "[LOG - SecurePrintService] "
                                + "Active printer: "
                                + activePrinterName
                );

                // =============================================
                // FINAL SECURITY CHECK
                // =============================================

                if (isVirtualFilePrinter(
                        activePrinterName
                )) {

                    throw new SecurityException(
                            "SECURITY POLICY BLOCK: "
                                    + "Virtual/file printer detected: "
                                    + activePrinterName
                    );
                }

                // =============================================
                // CREATE PRINTING DOCUMENT
                // =============================================

                try (PDDocument spoolDocument =
                             new PDDocument()) {

                    // =========================================
                    // DUPLICATE PAGES FOR REQUESTED COPIES
                    // =========================================

                    for (int copy = 1;
                         copy <= quantity;
                         copy++) {

                        for (int page = 0;
                             page < pageCount;
                             page++) {

                            spoolDocument.importPage(
                                    document.getPage(page)
                            );
                        }
                    }

                    int totalPages =
                            spoolDocument.getNumberOfPages();

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "Prepared "
                                    + totalPages
                                    + " physical pages."
                    );

                    // =========================================
                    // PDF PAGEABLE
                    // =========================================

                    PDFPageable pageable =
                            new PDFPageable(
                                    spoolDocument
                            );

                    printerJob.setPageable(
                            pageable
                    );

                    // =========================================
                    // PRINT ATTRIBUTES
                    // =========================================

                    PrintRequestAttributeSet attributes =
                            new HashPrintRequestAttributeSet();

                    /*
                     * Pages have already been duplicated
                     * according to quantity.
                     *
                     * Therefore Copies = 1.
                     */
                    attributes.add(
                            new Copies(1)
                    );

                    attributes.add(
                            new PageRanges(
                                    1,
                                    totalPages
                            )
                    );

                    // =========================================
                    // IMPORTANT
                    // =========================================
                    //
                    // DO NOT CALL:
                    //
                    // printerJob.printDialog();
                    //
                    // DO NOT CALL:
                    //
                    // printerJob.pageDialog();
                    //
                    // These are not required.
                    //
                    // We directly submit the job to the
                    // selected physical printer.
                    //
                    // =========================================

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "Sending print job directly "
                                    + "to physical printer..."
                    );

                    // =========================================
                    // DIRECT PRINT
                    // =========================================

                    printerJob.print(
                            attributes
                    );

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "Print job submitted successfully."
                    );

                    System.out.println(
                            "[LOG - SecurePrintService] "
                                    + "Printer: "
                                    + activePrinterName
                    );

                    System.out.println(
                            "[SECURITY LOG - SecurePrintService] "
                                    + "Document sent directly "
                                    + "to physical printer."
                    );
                }
            }

        } finally {

            // =================================================
            // CLEAR GENERATED PDF FROM RAM
            // =================================================

            if (generatedTemporaryPdf
                    && printablePdfBytes != null) {

                Arrays.fill(
                        printablePdfBytes,
                        (byte) 0
                );

                System.out.println(
                        "[SECURITY] "
                                + "Temporary printable PDF cleared from RAM."
                );
            }
        }
    }

    // =========================================================
    // CHECK PDF
    // =========================================================

    private boolean isPdf(
            byte[] data) {

        if (data == null
                || data.length < 4) {

            return false;
        }

        return data[0] == '%'
                && data[1] == 'P'
                && data[2] == 'D'
                && data[3] == 'F';
    }

    // =========================================================
    // CHECK IMAGE
    // =========================================================

    private boolean isImage(
            byte[] data) {

        try {

            BufferedImage image =
                    ImageIO.read(
                            new ByteArrayInputStream(
                                    data
                            )
                    );

            if (image != null) {

                image.flush();

                return true;
            }

            return false;

        } catch (Exception e) {

            return false;
        }
    }

    // =========================================================
    // CONVERT IMAGE TO PDF IN MEMORY
    // =========================================================

    private byte[] convertImageToPdfInMemory(
            byte[] imageBytes)
            throws Exception {

        BufferedImage image =
                ImageIO.read(
                        new ByteArrayInputStream(
                                imageBytes
                        )
                );

        if (image == null) {

            throw new IllegalArgumentException(
                    "Unable to read image."
            );
        }

        System.out.println(
                "[LOG - SecurePrintService] "
                        + "Image width = "
                        + image.getWidth()
                        + ", height = "
                        + image.getHeight()
        );

        try (PDDocument pdf =
                     new PDDocument();
             ByteArrayOutputStream output =
                     new ByteArrayOutputStream()) {

            // =================================================
            // CREATE A4 PAGE
            // =================================================

            PDRectangle pageSize =
                    PDRectangle.A4;

            PDPage page =
                    new PDPage(
                            pageSize
                    );

            pdf.addPage(page);

            // =================================================
            // CREATE RGB IMAGE
            // =================================================

            BufferedImage printableImage =
                    new BufferedImage(
                            image.getWidth(),
                            image.getHeight(),
                            BufferedImage.TYPE_INT_RGB
                    );

            Graphics2D graphics =
                    printableImage.createGraphics();

            graphics.drawImage(
                    image,
                    0,
                    0,
                    null
            );

            graphics.dispose();

            // =================================================
            // CREATE PDF IMAGE
            // =================================================

            PDImageXObject pdfImage =
                    LosslessFactory.createFromImage(
                            pdf,
                            printableImage
                    );

            // =================================================
            // CALCULATE IMAGE SIZE
            // =================================================

            float pageWidth =
                    pageSize.getWidth();

            float pageHeight =
                    pageSize.getHeight();

            float imageWidth =
                    pdfImage.getWidth();

            float imageHeight =
                    pdfImage.getHeight();

            float scaleX =
                    pageWidth / imageWidth;

            float scaleY =
                    pageHeight / imageHeight;

            float scale =
                    Math.min(
                            scaleX,
                            scaleY
                    );

            float finalWidth =
                    imageWidth * scale;

            float finalHeight =
                    imageHeight * scale;

            float x =
                    (pageWidth - finalWidth) / 2;

            float y =
                    (pageHeight - finalHeight) / 2;

            // =================================================
            // DRAW IMAGE ON PDF PAGE
            // =================================================

            try (org.apache.pdfbox.pdmodel.PDPageContentStream contentStream =
                         new org.apache.pdfbox.pdmodel.PDPageContentStream(
                                 pdf,
                                 page
                         )) {

                contentStream.drawImage(
                        pdfImage,
                        x,
                        y,
                        finalWidth,
                        finalHeight
                );
            }

            // =================================================
            // SAVE PDF ONLY IN RAM
            // =================================================

            pdf.save(
                    output
            );

            // =================================================
            // CLEAR PRINTABLE IMAGE
            // =================================================

            for (int yPixel = 0;
                 yPixel < printableImage.getHeight();
                 yPixel++) {

                for (int xPixel = 0;
                     xPixel < printableImage.getWidth();
                     xPixel++) {

                    printableImage.setRGB(
                            xPixel,
                            yPixel,
                            0
                    );
                }
            }

            printableImage.flush();
            image.flush();

            return output.toByteArray();
        }
    }
}