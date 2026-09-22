package com.myprivora.extras;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

public class CloudinaryService {

    private final Cloudinary cloudinary;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CloudinaryService(
            String cloudName,
            String apiKey,
            String apiSecret) {

        System.out.println(
                "[LOG - CloudinaryService] "
                        + "Initializing Cloudinary..."
        );

        cloudinary =
                new Cloudinary(
                        ObjectUtils.asMap(
                                "cloud_name",
                                cloudName,

                                "api_key",
                                apiKey,

                                "api_secret",
                                apiSecret,

                                "secure",
                                true
                        )
                );
    }

    // =========================================================
    // UPLOAD FROM MEMORY
    // =========================================================

    @SuppressWarnings("unchecked")
    public String[] uploadDirectFromMemory(
            byte[] documentBytes) throws IOException {

        if (documentBytes == null
                || documentBytes.length == 0) {

            throw new IllegalArgumentException(
                    "Document memory buffer is empty."
            );
        }

        System.out.println(
                "[LOG - CloudinaryService] Uploading "
                        + documentBytes.length
                        + " encrypted bytes..."
        );

        Map<String, Object> uploadParams =
                ObjectUtils.asMap(
                        "resource_type",
                        "auto",

                        "use_filename",
                        false,

                        "unique_filename",
                        true
                );

        Map<String, Object> uploadResult =
                cloudinary
                        .uploader()
                        .upload(
                                documentBytes,
                                uploadParams
                        );

        Object secureUrl =
                uploadResult.get("secure_url");

        Object publicId =
                uploadResult.get("public_id");

        Object resourceType =
                uploadResult.get("resource_type");

        if (secureUrl == null) {

            throw new IOException(
                    "Cloudinary did not return secure_url."
            );
        }

        String url =
                secureUrl.toString();

        String publicIdString =
                publicId != null
                        ? publicId.toString()
                        : "";

        String resourceTypeString =
                resourceType != null
                        ? resourceType.toString()
                        : "raw";

        System.out.println(
                "[LOG - CloudinaryService] "
                        + "Upload successful."
        );

        return new String[] {
                url,
                publicIdString,
                resourceTypeString
        };
    }

    // =========================================================
    // DOWNLOAD TO MEMORY
    // =========================================================

    public byte[] downloadDirectToMemory(
            String urlStr) throws IOException {

        if (urlStr == null
                || urlStr.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Cloudinary URL is empty."
            );
        }

        System.out.println(
                "[LOG - CloudinaryService] "
                        + "Downloading encrypted data to RAM..."
        );

        URL url =
                new URL(urlStr);

        try (
                InputStream input =
                        url.openStream();

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {

            byte[] buffer =
                    new byte[8192];

            int bytesRead;

            while (
                    (bytesRead =
                            input.read(buffer))
                            != -1
            ) {

                output.write(
                        buffer,
                        0,
                        bytesRead
                );
            }

            byte[] result =
                    output.toByteArray();

            System.out.println(
                    "[LOG - CloudinaryService] "
                            + "Download successful: "
                            + result.length
                            + " bytes."
            );

            return result;
        }
    }

    // =========================================================
    // DELETE CLOUD FILE
    // =========================================================

    public boolean deleteFromCloud(
            String publicId,
            String resourceType) {

        if (publicId == null
                || publicId.trim().isEmpty()) {

            return false;
        }

        try {

            Map<String, Object> params =
                    ObjectUtils.asMap(

                            "resource_type",
                            resourceType != null
                                    ? resourceType
                                    : "raw",

                            "invalidate",
                            true
                    );

            Map<String, Object> result =
                    cloudinary
                            .uploader()
                            .destroy(
                                    publicId,
                                    params
                            );

            return "ok".equalsIgnoreCase(
                    String.valueOf(
                            result.get("result")
                    )
            );

        } catch (Exception e) {

            System.err.println(
                    "[ERROR - CloudinaryService] "
                            + e.getMessage()
            );

            return false;
        }
    }
}