package com.myprivora.service;

import com.myprivora.dao.PrintRequestDAO;
import com.myprivora.model.PrintRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PrintRequestExpiryService {

    private final PrintRequestDAO printRequestDAO;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    private final Map<String, ScheduledFuture<?>> scheduledRequests =
            new ConcurrentHashMap<>();


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public PrintRequestExpiryService() {
        this.printRequestDAO = new PrintRequestDAO();
    }


    // =========================================================
    // SCHEDULE ONE REQUEST
    // =========================================================

    public void schedule(PrintRequest request) {

        if (request == null) {
            return;
        }

        String requestId = request.getRequestId();

        if (requestId == null || requestId.isBlank()) {
            return;
        }

        String status = request.getStatus();

        /*
         * Only PENDING requests are allowed to expire.
         */
        if (!"PENDING".equalsIgnoreCase(status)) {
            return;
        }

        String expiresAt = request.getExpiresAt();

        if (expiresAt == null || expiresAt.isBlank()) {
            return;
        }


        try {

            Instant expiryTime = Instant.parse(expiresAt);
            Instant now = Instant.now();

            long delayMillis =
                    Duration.between(now, expiryTime).toMillis();


            /*
             * If expiry time has already passed,
             * expire immediately.
             */
            if (delayMillis <= 0) {

                expireRequest(requestId);

                return;
            }


            /*
             * Cancel previous timer for same request
             * if one already exists.
             */
            cancel(requestId);


            ScheduledFuture<?> future =
                    scheduler.schedule(
                            () -> expireRequest(requestId),
                            delayMillis,
                            TimeUnit.MILLISECONDS
                    );


            scheduledRequests.put(requestId, future);


            System.out.println(
                    "[ExpiryService] Request " +
                    requestId +
                    " scheduled to expire at " +
                    expiresAt
            );

        } catch (Exception e) {

            System.out.println(
                    "[ExpiryService] Invalid expiry time for request "
                    + requestId
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // SCHEDULE MULTIPLE REQUESTS
    // =========================================================

    public void scheduleAll(List<PrintRequest> requests) {

        if (requests == null) {
            return;
        }

        for (PrintRequest request : requests) {

            schedule(request);
        }
    }


    // =========================================================
    // EXPIRE REQUEST
    // =========================================================

    private void expireRequest(String requestId) {

        try {

            /*
             * Fetch the latest version from Firestore.
             *
             * This is VERY IMPORTANT.
             *
             * The request might have been accepted/rejected
             * while our timer was waiting.
             */
            PrintRequest latestRequest =
                    printRequestDAO.getPrintRequestById(requestId);


            if (latestRequest == null) {

                System.out.println(
                        "[ExpiryService] Request not found: "
                        + requestId
                );

                scheduledRequests.remove(requestId);

                return;
            }


            String currentStatus =
                    latestRequest.getStatus();


            /*
             * Only PENDING requests can become EXPIRED.
             */
            if (!"PENDING".equalsIgnoreCase(currentStatus)) {

                System.out.println(
                        "[ExpiryService] Request "
                        + requestId
                        + " is already "
                        + currentStatus
                        + ". No expiration."
                );

                scheduledRequests.remove(requestId);

                return;
            }


            /*
             * Double-check the actual expiry time.
             */
            String expiresAt =
                    latestRequest.getExpiresAt();

            if (expiresAt == null || expiresAt.isBlank()) {

                scheduledRequests.remove(requestId);

                return;
            }


            Instant expiryTime =
                    Instant.parse(expiresAt);


            /*
             * If the expiry time has NOT actually arrived,
             * schedule again for the remaining time.
             */
            if (Instant.now().isBefore(expiryTime)) {

                schedule(latestRequest);

                return;
            }


            /*
             * Finally change:
             *
             * PENDING -> EXPIRED
             */
            boolean updated =
                    printRequestDAO.updateStatus(
                            requestId,
                            "EXPIRED"
                    );


            if (updated) {

                System.out.println(
                        "[ExpiryService] Request "
                        + requestId
                        + " changed: PENDING -> EXPIRED"
                );

            } else {

                System.out.println(
                        "[ExpiryService] Failed to expire request "
                        + requestId
                );
            }


            scheduledRequests.remove(requestId);


        } catch (Exception e) {

            System.out.println(
                    "[ExpiryService] Error while expiring request "
                    + requestId
            );

            e.printStackTrace();

            scheduledRequests.remove(requestId);
        }
    }


    // =========================================================
    // CANCEL TIMER
    // =========================================================

    public void cancel(String requestId) {

        if (requestId == null || requestId.isBlank()) {
            return;
        }

        ScheduledFuture<?> future =
                scheduledRequests.remove(requestId);

        if (future != null) {

            future.cancel(false);

            System.out.println(
                    "[ExpiryService] Timer cancelled for "
                    + requestId
            );
        }
    }


    // =========================================================
    // SHUTDOWN
    // =========================================================

    public void shutdown() {

        for (ScheduledFuture<?> future :
                scheduledRequests.values()) {

            if (future != null) {
                future.cancel(false);
            }
        }

        scheduledRequests.clear();

        scheduler.shutdownNow();

        System.out.println(
                "[ExpiryService] Scheduler stopped."
        );
    }
}