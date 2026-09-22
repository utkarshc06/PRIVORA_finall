
package com.myprivora.extras;

import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesSecurityService {

    // =========================================================
    // AES CONFIGURATION
    // =========================================================

    private static final int AES_KEY_SIZE = 256;

    private static final int GCM_IV_LENGTH = 12;

    private static final int GCM_TAG_LENGTH = 128;


    // =========================================================
    // RANDOM AES KEY GENERATION
    // =========================================================

    /**
     * Generates a new random AES-256 key.
     *
     * This key is completely separate from the
     * user's 4-digit Privacy PIN.
     */
    public byte[] generateRandomKey() throws Exception {

        System.out.println(
                "[LOG - AesSecurity] Generating random AES-256 key..."
        );

        KeyGenerator keyGenerator =
                KeyGenerator.getInstance("AES");

        keyGenerator.init(AES_KEY_SIZE);

        SecretKey secretKey =
                keyGenerator.generateKey();

        byte[] key =
                secretKey.getEncoded();

        System.out.println(
                "[LOG - AesSecurity] AES-256 key generated successfully."
        );

        System.out.println(
                "[LOG - AesSecurity] Key length = "
                        + key.length
                        + " bytes."
        );

        return key;
    }


    // =========================================================
    // CREATE SECRET KEY FROM BYTES
    // =========================================================

    private SecretKeySpec createSecretKey(
            byte[] key) {

        if (key == null || key.length != 32) {

            throw new IllegalArgumentException(
                    "AES-256 key must contain exactly 32 bytes."
            );
        }

        return new SecretKeySpec(
                key,
                "AES"
        );
    }


    // =========================================================
    // ENCRYPT DOCUMENT
    // =========================================================

    /**
     * Encrypts document data using AES-256-GCM.
     *
     * The AES key is supplied separately.
     *
     * The Privacy PIN is NOT used here.
     *
     * Encrypted format:
     *
     * [ 12-byte IV ][ encrypted data + authentication tag ]
     */
    public byte[] encryptInMemory(
            byte[] rawData,
            byte[] aesKey) throws Exception {

        if (rawData == null
                || rawData.length == 0) {

            throw new IllegalArgumentException(
                    "Cannot encrypt empty or null data."
            );
        }

        if (aesKey == null
                || aesKey.length != 32) {

            throw new IllegalArgumentException(
                    "Valid AES-256 key is required."
            );
        }


        System.out.println(
                "[LOG - AesSecurity] Encrypting "
                        + rawData.length
                        + " bytes in memory..."
        );


        // -----------------------------------------------------
        // CREATE AES KEY
        // -----------------------------------------------------

        SecretKeySpec secretKey =
                createSecretKey(aesKey);


        // -----------------------------------------------------
        // GENERATE RANDOM IV
        // -----------------------------------------------------

        byte[] iv =
                new byte[GCM_IV_LENGTH];

        SecureRandom secureRandom =
                new SecureRandom();

        secureRandom.nextBytes(iv);


        System.out.println(
                "[LOG - AesSecurity] Random GCM IV generated."
        );


        // -----------------------------------------------------
        // CREATE CIPHER
        // -----------------------------------------------------

        Cipher cipher =
                Cipher.getInstance(
                        "AES/GCM/NoPadding"
                );


        GCMParameterSpec gcmSpec =
                new GCMParameterSpec(
                        GCM_TAG_LENGTH,
                        iv
                );


        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                gcmSpec
        );


        // -----------------------------------------------------
        // ENCRYPT
        // -----------------------------------------------------

        byte[] encryptedData =
                cipher.doFinal(rawData);


        // -----------------------------------------------------
        // COMBINE IV + ENCRYPTED DATA
        // -----------------------------------------------------

        /*
         *
         * Final encrypted format:
         *
         * [ IV ][ encrypted data ][ authentication tag ]
         *
         */

        byte[] result =
                new byte[
                        iv.length
                                + encryptedData.length
                ];


        System.arraycopy(
                iv,
                0,
                result,
                0,
                iv.length
        );


        System.arraycopy(
                encryptedData,
                0,
                result,
                iv.length,
                encryptedData.length
        );


        System.out.println(
                "[LOG - AesSecurity] Encryption successful."
        );

        System.out.println(
                "[LOG - AesSecurity] Encrypted size = "
                        + result.length
                        + " bytes."
        );


        return result;
    }


    // =========================================================
    // DECRYPT DOCUMENT
    // =========================================================

    /**
     * Decrypts AES-256-GCM encrypted document data.
     *
     * The same AES key used during encryption
     * must be supplied.
     *
     * The Privacy PIN is NOT used for decryption here.
     */
    public byte[] decryptInMemory(
            byte[] encryptedData,
            byte[] aesKey) throws Exception {

        if (encryptedData == null
                || encryptedData.length <= GCM_IV_LENGTH) {

            throw new IllegalArgumentException(
                    "Invalid encrypted data."
            );
        }

        if (aesKey == null
                || aesKey.length != 32) {

            throw new IllegalArgumentException(
                    "Valid AES-256 key is required."
            );
        }


        try {

            System.out.println(
                    "[LOG - AesSecurity] Decrypting encrypted "
                            + encryptedData.length
                            + " bytes..."
            );


            // -------------------------------------------------
            // CREATE AES KEY
            // -------------------------------------------------

            SecretKeySpec secretKey =
                    createSecretKey(aesKey);


            // -------------------------------------------------
            // EXTRACT IV
            // -------------------------------------------------

            byte[] iv =
                    Arrays.copyOfRange(
                            encryptedData,
                            0,
                            GCM_IV_LENGTH
                    );


            // -------------------------------------------------
            // EXTRACT CIPHER TEXT
            // -------------------------------------------------

            byte[] cipherText =
                    Arrays.copyOfRange(
                            encryptedData,
                            GCM_IV_LENGTH,
                            encryptedData.length
                    );


            // -------------------------------------------------
            // CREATE CIPHER
            // -------------------------------------------------

            Cipher cipher =
                    Cipher.getInstance(
                            "AES/GCM/NoPadding"
                    );


            GCMParameterSpec gcmSpec =
                    new GCMParameterSpec(
                            GCM_TAG_LENGTH,
                            iv
                    );


            cipher.init(
                    Cipher.DECRYPT_MODE,
                    secretKey,
                    gcmSpec
            );


            // -------------------------------------------------
            // DECRYPT
            // -------------------------------------------------

            byte[] decryptedData =
                    cipher.doFinal(cipherText);


            System.out.println(
                    "[LOG - AesSecurity] Decryption successful."
            );


            System.out.println(
                    "[LOG - AesSecurity] Decrypted size = "
                            + decryptedData.length
                            + " bytes."
            );


            return decryptedData;

        } catch (Exception e) {

            System.err.println(
                    "[ERROR - AesSecurity] "
                            + "AES decryption failed."
            );

            throw new SecurityException(
                    "Unable to decrypt document.",
                    e
            );
        }
    }


    // =========================================================
    // CLEAR KEY FROM MEMORY
    // =========================================================

    /**
     * Clears an AES key from the supplied byte array.
     *
     * This helps reduce the amount of sensitive key material
     * remaining in memory after the key is no longer required.
     */
    public void clearKey(
            byte[] aesKey) {

        if (aesKey == null) {
            return;
        }

        Arrays.fill(
                aesKey,
                (byte) 0
        );

        System.out.println(
                "[LOG - AesSecurity] AES key cleared from memory."
        );
    }


    // =========================================================
    // CLEAR BYTE ARRAY FROM MEMORY
    // =========================================================

    /**
     * Utility method for clearing sensitive byte arrays.
     */
    public void clearBytes(
            byte[] data) {

        if (data == null) {
            return;
        }

        Arrays.fill(
                data,
                (byte) 0
        );
    }
}

