
package com.myprivora.extras;

import javafx.application.Platform;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class MemoryLifecycleManager {

    // =========================================================
    // ALLOWED EXPIRY RANGE
    // =========================================================

    private static final int MIN_EXPIRY_MINUTES = 1;

    private static final int MAX_EXPIRY_MINUTES = 10;


    // =========================================================
    // ACTIVE TIMER
    // =========================================================

    private Timer activeTimer;


    // =========================================================
    // START LIFECYCLE
    // =========================================================

    public synchronized void startLifecycle(
            byte[] documentMemoryStore,
            int expiryMinutes,
            Consumer<Void> uiPurgeCallback) {

        // -----------------------------------------------------
        // VALIDATE EXPIRY
        // -----------------------------------------------------

        if (expiryMinutes < MIN_EXPIRY_MINUTES
                || expiryMinutes > MAX_EXPIRY_MINUTES) {

            throw new IllegalArgumentException(
                    "Expiry time must be between "
                            + MIN_EXPIRY_MINUTES
                            + " and "
                            + MAX_EXPIRY_MINUTES
                            + " minutes."
            );
        }


        // -----------------------------------------------------
        // CANCEL PREVIOUS TIMER
        // -----------------------------------------------------

        cancelActiveLifecycle();


        // -----------------------------------------------------
        // CALCULATE LIFECYCLE DURATION
        // -----------------------------------------------------

        long lifecycleDurationMs =
                expiryMinutes * 60_000L;


        System.out.println(
                "[LOG - LifecycleManager] "
                        + "Starting "
                        + expiryMinutes
                        + "-minute RAM lifecycle."
        );


        // -----------------------------------------------------
        // CREATE TIMER
        // -----------------------------------------------------

        activeTimer =
                new Timer(
                        "Memory-Sanitization-Timer",
                        true
                );


        // -----------------------------------------------------
        // SCHEDULE SANITIZATION
        // -----------------------------------------------------

        activeTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {

                        sanitize(
                                documentMemoryStore,
                                expiryMinutes,
                                uiPurgeCallback
                        );
                    }

                },
                lifecycleDurationMs
        );
    }


    // =========================================================
    // CANCEL TIMER
    // =========================================================

    public synchronized void cancelActiveLifecycle() {

        if (activeTimer != null) {

            System.out.println(
                    "[LOG - LifecycleManager] "
                            + "Canceling active timer."
            );

            activeTimer.cancel();

            activeTimer.purge();

            activeTimer = null;
        }
    }


    // =========================================================
    // SANITIZE RAM
    // =========================================================

    private void sanitize(
            byte[] documentMemoryStore,
            int expiryMinutes,
            Consumer<Void> uiPurgeCallback) {


        System.out.println(
                "[LOG - LifecycleManager] "
                        + expiryMinutes
                        + "-minute TTL reached."
        );


        // -----------------------------------------------------
        // OVERWRITE RAM BUFFER
        // -----------------------------------------------------

        if (documentMemoryStore != null) {

            Arrays.fill(
                    documentMemoryStore,
                    (byte) 0
            );

            System.out.println(
                    "[LOG - LifecycleManager] "
                            + "RAM buffer overwritten."
            );
        }


        // -----------------------------------------------------
        // REMOVE UI REFERENCE
        // -----------------------------------------------------

        Platform.runLater(() -> {

            if (uiPurgeCallback != null) {

                uiPurgeCallback.accept(null);
            }
        });


        // -----------------------------------------------------
        // REQUEST GARBAGE COLLECTION
        // -----------------------------------------------------

        System.gc();


        System.out.println(
                "[LOG - LifecycleManager] "
                        + "RAM lifecycle completed."
        );
    }
}

