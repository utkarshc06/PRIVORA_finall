package com.myprivora.dao;

import com.myprivora.config.DatabaseConfig;
import com.myprivora.model.PrintRequest;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PrintRequestDAO {

    private static final String COLLECTION =
            "PrintRequests";


    // =========================================================
    // CREATE PRINT REQUEST
    // =========================================================

    public boolean createPrintRequest(PrintRequest request) {

        try {

            if (request.getRequestId() == null ||
                    request.getRequestId().isBlank()) {

                request.setRequestId(
                        UUID.randomUUID().toString()
                );
            }


            DatabaseConfig.getFirestore()
                    .collection(COLLECTION)
                    .document(request.getRequestId())
                    .create(request)
                    .get();


            System.out.println(
                    "[PrintRequestDAO] Print request created."
            );

            System.out.println(
                    "[PrintRequestDAO] Request ID = "
                    + request.getRequestId()
            );

            System.out.println(
                    "[PrintRequestDAO] User ID = "
                    + request.getUserId()
            );

            System.out.println(
                    "[PrintRequestDAO] Xerox ID = "
                    + request.getXeroxId()
            );

            System.out.println(
                    "[PrintRequestDAO] Status = "
                    + request.getStatus()
            );

            System.out.println(
                    "[PrintRequestDAO] Expires At = "
                    + request.getExpiresAt()
            );

            return true;


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Failed to create request."
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // GET REQUEST BY ID
    // =========================================================

    public PrintRequest getPrintRequestById(String requestId) {

        try {

            if (requestId == null ||
                    requestId.isBlank()) {

                return null;
            }


            DocumentSnapshot document =
                    DatabaseConfig.getFirestore()
                            .collection(COLLECTION)
                            .document(requestId)
                            .get()
                            .get();


            if (!document.exists()) {

                return null;
            }


            return document.toObject(
                    PrintRequest.class
            );


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Error fetching request: "
                    + requestId
            );

            e.printStackTrace();

            return null;
        }
    }


    // =========================================================
    // GET REQUESTS BY USER
    // =========================================================

    public List<PrintRequest> getRequestsByUser(
            String userId
    ) {

        List<PrintRequest> requests =
                new ArrayList<>();


        try {

            QuerySnapshot snapshot =
                    DatabaseConfig.getFirestore()
                            .collection(COLLECTION)
                            .whereEqualTo(
                                    "userId",
                                    userId
                            )
                            .get()
                            .get();


            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                PrintRequest request =
                        document.toObject(
                                PrintRequest.class
                        );

                if (request != null) {

                    requests.add(request);
                }
            }


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Error loading user requests."
            );

            e.printStackTrace();
        }


        return requests;
    }


    // =========================================================
    // GET REQUESTS BY XEROX
    // =========================================================

    public List<PrintRequest> getRequestsByXerox(
            String xeroxId
    ) {

        List<PrintRequest> requests =
                new ArrayList<>();


        try {

            QuerySnapshot snapshot =
                    DatabaseConfig.getFirestore()
                            .collection(COLLECTION)
                            .whereEqualTo(
                                    "xeroxId",
                                    xeroxId
                            )
                            .get()
                            .get();


            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                PrintRequest request =
                        document.toObject(
                                PrintRequest.class
                        );

                if (request != null) {

                    requests.add(request);
                }
            }


            sortByRequestedAt(requests);


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Error loading Xerox requests."
            );

            e.printStackTrace();
        }


        return requests;
    }


    // =========================================================
    // GET PENDING REQUESTS BY XEROX
    // =========================================================

    public List<PrintRequest> getPendingRequestsByXerox(
            String xeroxId
    ) {

        List<PrintRequest> requests =
                new ArrayList<>();


        try {

            if (xeroxId == null ||
                    xeroxId.isBlank()) {

                System.out.println(
                        "[PrintRequestDAO] Xerox ID is empty."
                );

                return requests;
            }


            /*
             * IMPORTANT:
             *
             * We intentionally DO NOT use:
             *
             * orderBy("requestedAt")
             *
             * because Firestore may require a
             * composite index.
             */
            QuerySnapshot snapshot =
                    DatabaseConfig.getFirestore()
                            .collection(COLLECTION)
                            .whereEqualTo(
                                    "xeroxId",
                                    xeroxId
                            )
                            .whereEqualTo(
                                    "status",
                                    "PENDING"
                            )
                            .get()
                            .get();


            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                PrintRequest request =
                        document.toObject(
                                PrintRequest.class
                        );

                if (request != null) {

                    requests.add(request);
                }
            }


            sortByRequestedAt(requests);


            System.out.println(
                    "[PrintRequestDAO] Pending requests for Xerox "
                    + xeroxId
                    + " = "
                    + requests.size()
            );


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] ERROR loading pending requests."
            );

            e.printStackTrace();
        }


        return requests;
    }


    // =========================================================
    // SORT REQUESTS
    // =========================================================

    private void sortByRequestedAt(
            List<PrintRequest> requests
    ) {

        requests.sort(
                Comparator.comparing(
                        PrintRequest::getRequestedAt,
                        Comparator.nullsFirst(
                                String::compareTo
                        )
                ).reversed()
        );
    }


    // =========================================================
    // UPDATE COMPLETE REQUEST
    // =========================================================

    public boolean updatePrintRequest(
            PrintRequest request
    ) {

        try {

            if (request == null ||
                    request.getRequestId() == null ||
                    request.getRequestId().isBlank()) {

                return false;
            }


            DatabaseConfig.getFirestore()
                    .collection(COLLECTION)
                    .document(request.getRequestId())
                    .set(request)
                    .get();


            System.out.println(
                    "[PrintRequestDAO] Request updated: "
                    + request.getRequestId()
            );

            return true;


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Failed to update request."
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // UPDATE STATUS
    // =========================================================

    public boolean updateStatus(
            String requestId,
            String newStatus
    ) {

        try {

            if (requestId == null ||
                    requestId.isBlank()) {

                return false;
            }


            if (newStatus == null ||
                    newStatus.isBlank()) {

                return false;
            }


            DatabaseConfig.getFirestore()
                    .collection(COLLECTION)
                    .document(requestId)
                    .update(
                            "status",
                            newStatus
                    )
                    .get();


            System.out.println(
                    "[PrintRequestDAO] Status updated: "
                    + requestId
                    + " -> "
                    + newStatus
            );


            return true;


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Failed to update status."
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // UPDATE PRINTED COUNT
    // =========================================================

    public boolean updatePrintedCount(
            String requestId,
            int printedCount
    ) {

        try {

            DatabaseConfig.getFirestore()
                    .collection(COLLECTION)
                    .document(requestId)
                    .update(
                            "printedCount",
                            printedCount
                    )
                    .get();


            return true;


        } catch (Exception e) {

            System.out.println(
                    "[PrintRequestDAO] Failed to update printed count."
            );

            e.printStackTrace();

            return false;
        }
    }
}