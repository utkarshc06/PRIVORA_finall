
package com.myprivora.extras;

import com.myprivora.config.CloudinaryConfig;
import com.myprivora.config.DatabaseConfig;
import com.myprivora.dao.DocumentDAO;
import com.myprivora.dao.PrintRequestDAO;
import com.myprivora.model.Document;
import com.myprivora.model.PrintRequest;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public final class DocumentExpirationService {

    // =========================================================
    // SERVICE CONTROL
    // =========================================================

    private static final Object LOCK =
            new Object();

    private static ScheduledExecutorService scheduler;

    private static volatile boolean started =
            false;


    // =========================================================
    // CHECK INTERVAL
    // =========================================================

    private static final long CHECK_INTERVAL_SECONDS =
            30;


    // =========================================================
    // DAOs
    // =========================================================

    private static final DocumentDAO documentDAO =
            new DocumentDAO();

    private static final PrintRequestDAO printRequestDAO =
            new PrintRequestDAO();


    // =========================================================
    // PRIVATE CONSTRUCTOR
    // =========================================================

    private DocumentExpirationService() {
    }


    // =========================================================
    // START
    // =========================================================

    public static void start() {

        synchronized (LOCK) {

            if (started) {

                System.out.println(
                        "[DocumentExpirationService] "
                                + "Service already running."
                );

                return;
            }


            scheduler =
                    Executors.newSingleThreadScheduledExecutor(
                            runnable -> {

                                Thread thread =
                                        new Thread(
                                                runnable,
                                                "PRIVORA-Expiration-Thread"
                                        );

                                thread.setDaemon(true);

                                return thread;
                            }
                    );


            scheduler.scheduleAtFixedRate(
                    DocumentExpirationService::safeExpirationCheck,
                    0,
                    CHECK_INTERVAL_SECONDS,
                    TimeUnit.SECONDS
            );


            started = true;


            System.out.println(
                    "================================================="
            );


            System.out.println(
                    "[DocumentExpirationService] "
                            + "Background expiration service started."
            );


            System.out.println(
                    "[DocumentExpirationService] "
                            + "Checking every "
                            + CHECK_INTERVAL_SECONDS
                            + " seconds."
            );


            System.out.println(
                    "================================================="
            );
        }
    }


    // =========================================================
    // STOP
    // =========================================================

    public static void stop() {

        synchronized (LOCK) {

            if (!started) {

                return;
            }


            if (scheduler != null) {

                scheduler.shutdownNow();

                scheduler = null;
            }


            started = false;


            System.out.println(
                    "[DocumentExpirationService] "
                            + "Background service stopped."
            );
        }
    }


    // =========================================================
    // SAFE CHECK
    // =========================================================

    private static void safeExpirationCheck() {

        try {

            processExpiredRequests();

        } catch (Exception e) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Expiration check failed."
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // PROCESS REQUESTS
    // =========================================================

    private static void processExpiredRequests()
            throws Exception {

        QuerySnapshot snapshot =
                DatabaseConfig
                        .getFirestore()
                        .collection("PrintRequests")
                        .get()
                        .get();


        if (snapshot.getDocuments().isEmpty()) {

            System.out.println(
                    "[DocumentExpirationService] "
                            + "No print requests found."
            );

            return;
        }


        Instant now =
                Instant.now();


        for (DocumentSnapshot firestoreDocument :
                snapshot.getDocuments()) {

            PrintRequest request =
                    firestoreDocument.toObject(
                            PrintRequest.class
                    );


            if (request == null) {

                continue;
            }


            processRequest(
                    request,
                    now
            );
        }
    }


    // =========================================================
    // PROCESS ONE REQUEST
    // =========================================================

    private static void processRequest(
            PrintRequest request,
            Instant now) {

        String requestId =
                request.getRequestId();


        if (requestId == null
                || requestId.isBlank()) {

            return;
        }


        String status =
                request.getStatus();


        /*
         * Only pending requests need expiration checking.
         */
        if (!"PENDING".equalsIgnoreCase(status)) {

            return;
        }


        String expiresAt =
                request.getExpiresAt();


        if (expiresAt == null
                || expiresAt.isBlank()) {

            System.out.println(
                    "[DocumentExpirationService] "
                            + "No expiry time for request: "
                            + requestId
            );

            return;
        }


        // =====================================================
        // PARSE EXPIRY
        // =====================================================

        Instant expiryInstant;


        try {

            expiryInstant =
                    Instant.parse(expiresAt);

        } catch (Exception e) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Invalid expiry timestamp."
            );

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Request ID = "
                            + requestId
            );

            return;
        }


        // =====================================================
        // NOT EXPIRED
        // =====================================================

        if (now.isBefore(expiryInstant)) {

            return;
        }


        // =====================================================
        // EXPIRED
        // =====================================================

        System.out.println(
                "-------------------------------------------------"
        );


        System.out.println(
                "[DocumentExpirationService] "
                        + "EXPIRED REQUEST FOUND"
        );


        System.out.println(
                "[DocumentExpirationService] "
                        + "Request ID = "
                        + requestId
        );


        System.out.println(
                "[DocumentExpirationService] "
                        + "Document ID = "
                        + request.getDocumentId()
        );


        System.out.println(
                "[DocumentExpirationService] "
                        + "Expired At = "
                        + expiresAt
        );


        // =====================================================
        // MARK PRINT REQUEST EXPIRED
        // =====================================================

        boolean requestUpdated =
                printRequestDAO.updateStatus(
                        requestId,
                        "EXPIRED"
                );


        if (!requestUpdated) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Failed to mark request EXPIRED."
            );

            return;
        }


        System.out.println(
                "[DocumentExpirationService] "
                        + "PrintRequest marked EXPIRED."
        );


        // =====================================================
        // EXPIRE DOCUMENT
        // =====================================================

        expireDocument(
                request.getDocumentId()
        );


        System.out.println(
                "[DocumentExpirationService] "
                        + "Expiration processing completed."
        );


        System.out.println(
                "-------------------------------------------------"
        );
    }


    // =========================================================
    // EXPIRE DOCUMENT
    // =========================================================

    private static void expireDocument(
            String documentId) {

        if (documentId == null
                || documentId.isBlank()) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Document ID is missing."
            );

            return;
        }


        // =====================================================
        // GET DOCUMENT
        // =====================================================

        Document document =
                documentDAO.getDocumentById(
                        documentId
                );


        if (document == null) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Document not found: "
                            + documentId
            );

            return;
        }


        // =====================================================
        // CLOUDINARY METADATA
        // =====================================================

        String publicId =
                document.getCloudinaryPublicId();


        String resourceType =
                document.getCloudinaryResourceType();


        if (publicId == null
                || publicId.isBlank()) {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Cloudinary Public ID is missing."
            );

        } else {

            try {

                // =================================================
                // SHARED CLOUDINARY CONFIG
                // =================================================

                CloudinaryService cloudinaryService =
                        CloudinaryConfig.getService();


                // =================================================
                // DELETE ENCRYPTED OBJECT
                // =================================================

                boolean deleted =
                        cloudinaryService.deleteFromCloud(
                                publicId,
                                resourceType
                        );


                if (deleted) {

                    System.out.println(
                            "[DocumentExpirationService] "
                                    + "Encrypted document deleted "
                                    + "from Cloudinary."
                    );

                } else {

                    System.err.println(
                            "[DocumentExpirationService] "
                                    + "Cloudinary deletion returned false."
                    );
                }


            } catch (Exception e) {

                System.err.println(
                        "[DocumentExpirationService] "
                                + "Cloudinary deletion failed."
                );

                e.printStackTrace();
            }
        }


        // =====================================================
        // MARK DOCUMENT EXPIRED
        // =====================================================

        boolean documentUpdated =
                documentDAO.updateStatus(
                        documentId,
                        "EXPIRED"
                );


        if (documentUpdated) {

            System.out.println(
                    "[DocumentExpirationService] "
                            + "Document marked EXPIRED."
            );

        } else {

            System.err.println(
                    "[DocumentExpirationService] "
                            + "Failed to mark document EXPIRED."
            );
        }
    }
}

