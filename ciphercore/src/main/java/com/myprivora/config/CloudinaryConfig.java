
package com.myprivora.config;

import com.myprivora.extras.CloudinaryService;

public final class CloudinaryConfig {

    /*
     * =========================================================
     * CLOUDINARY CONFIGURATION
     * =========================================================
     *
     * IMPORTANT:
     *
     * These values are intentionally NOT written here.
     *
     * Configure them as Windows environment variables:
     *
     * PRIVORA_CLOUDINARY_CLOUD_NAME
     * PRIVORA_CLOUDINARY_API_KEY
     * PRIVORA_CLOUDINARY_API_SECRET
     *
     * This allows every PRIVORA component to use the
     * same Cloudinary configuration.
     *
     * =========================================================
     */

    private static final String CLOUD_NAME =
            "PRIVORA_CLOUDINARY_CLOUD_NAME";

    private static final String API_KEY =
            "PRIVORA_CLOUDINARY_API_KEY";

    private static final String API_SECRET =
            "PRIVORA_CLOUDINARY_API_SECRET";


    // =========================================================
    // PREVENT OBJECT CREATION
    // =========================================================

    private CloudinaryConfig() {
    }


    // =========================================================
    // CREATE CLOUDINARY SERVICE
    // =========================================================

    public static CloudinaryService getService() {

        String cloudName =
                System.getenv(CLOUD_NAME);

        String apiKey =
                System.getenv(API_KEY);

        String apiSecret =
                System.getenv(API_SECRET);


        // =====================================================
        // VALIDATE CONFIGURATION
        // =====================================================

        if (cloudName == null
                || cloudName.isBlank()) {

            throw new IllegalStateException(
                    "Cloudinary cloud name is not configured. "
                            + "Set environment variable: "
                            + CLOUD_NAME
            );
        }


        if (apiKey == null
                || apiKey.isBlank()) {

            throw new IllegalStateException(
                    "Cloudinary API key is not configured. "
                            + "Set environment variable: "
                            + API_KEY
            );
        }


        if (apiSecret == null
                || apiSecret.isBlank()) {

            throw new IllegalStateException(
                    "Cloudinary API secret is not configured. "
                            + "Set environment variable: "
                            + API_SECRET
            );
        }


        // =====================================================
        // CREATE SERVICE
        // =====================================================

        return new CloudinaryService(
                cloudName,
                apiKey,
                apiSecret
        );
    }
}

