
package com.myprivora.extras;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * ============================================================
 * PinSecurityService
 * ============================================================
 *
 * Handles secure hashing and verification of the 4-digit
 * privacy PIN.
 *
 * IMPORTANT:
 *
 * The original PIN is NEVER stored in Firestore.
 *
 * Only the SHA-256 hash is stored.
 *
 * Example:
 *
 * User PIN:
 *      1234
 *
 * Firestore:
 *      pinHash = SHA-256(1234)
 *
 * Xerox enters:
 *      1234
 *
 * Application:
 *      SHA-256(1234)
 *          ↓
 *      compare with stored pinHash
 *
 * ============================================================
 */
public final class PinSecurityService {

    private PinSecurityService() {
        // Prevent object creation.
    }


    // =========================================================
    // HASH PIN
    // =========================================================

    public static String hashPin(String pin) throws Exception {

        // -----------------------------------------------------
        // Validate PIN
        // -----------------------------------------------------

        if (pin == null || !pin.matches("\\d{4}")) {

            throw new IllegalArgumentException(
                    "PIN must contain exactly 4 digits."
            );
        }


        // -----------------------------------------------------
        // SHA-256
        // -----------------------------------------------------

        MessageDigest digest =
                MessageDigest.getInstance("SHA-256");


        byte[] hash =
                digest.digest(
                        pin.getBytes(StandardCharsets.UTF_8)
                );


        // -----------------------------------------------------
        // Convert byte[] to hexadecimal String
        // -----------------------------------------------------

        StringBuilder hex =
                new StringBuilder();


        for (byte b : hash) {

            String value =
                    Integer.toHexString(
                            0xff & b
                    );


            if (value.length() == 1) {

                hex.append('0');
            }


            hex.append(value);
        }


        return hex.toString();
    }


    // =========================================================
    // VERIFY PIN
    // =========================================================

    public static boolean verifyPin(
            String enteredPin,
            String storedHash) {

        try {

            // -------------------------------------------------
            // Validate input
            // -------------------------------------------------

            if (enteredPin == null
                    || storedHash == null
                    || !enteredPin.matches("\\d{4}")) {

                return false;
            }


            // -------------------------------------------------
            // Hash entered PIN
            // -------------------------------------------------

            String enteredHash =
                    hashPin(
                            enteredPin
                    );


            // -------------------------------------------------
            // Constant-time comparison
            // -------------------------------------------------

            return MessageDigest.isEqual(
                    enteredHash.getBytes(
                            StandardCharsets.UTF_8
                    ),
                    storedHash.getBytes(
                            StandardCharsets.UTF_8
                    )
            );


        } catch (Exception e) {

            System.err.println(
                    "[PinSecurityService] PIN verification error."
            );

            e.printStackTrace();

            return false;
        }
    }
}

