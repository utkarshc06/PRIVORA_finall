package com.myprivora.extras;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.spec.PKCS8EncodedKeySpec;

import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.crypto.Cipher;

public class KeyProtectionService {

    // =========================================================
    // RSA CONFIGURATION
    // =========================================================

    private static final int RSA_KEY_SIZE = 2048;

    private static final String RSA_ALGORITHM = "RSA";

    /*
     * RSA-OAEP with SHA-256.
     *
     * We explicitly configure:
     *
     * Main digest  = SHA-256
     * MGF1 digest  = SHA-256
     */
    private static final String RSA_TRANSFORMATION =
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";


    // =========================================================
    // OAEP PARAMETERS
    // =========================================================

    private static final OAEPParameterSpec OAEP_SHA256 =
            new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT
            );


    // =========================================================
    // GENERATE RSA KEY PAIR
    // =========================================================

    public KeyPair generateKeyPair()
            throws Exception {

        System.out.println(
                "[LOG - KeyProtection] "
                        + "Generating RSA key pair..."
        );

        KeyPairGenerator generator =
                KeyPairGenerator.getInstance(
                        RSA_ALGORITHM
                );

        generator.initialize(
                RSA_KEY_SIZE
        );

        KeyPair keyPair =
                generator.generateKeyPair();

        System.out.println(
                "[LOG - KeyProtection] "
                        + "RSA key pair generated."
        );

        return keyPair;
    }


    // =========================================================
    // PUBLIC KEY → BASE64
    // =========================================================

    public String publicKeyToBase64(
            PublicKey publicKey) {

        if (publicKey == null) {

            throw new IllegalArgumentException(
                    "Public key cannot be null."
            );
        }

        return Base64.getEncoder()
                .encodeToString(
                        publicKey.getEncoded()
                );
    }


    // =========================================================
    // PRIVATE KEY → BASE64
    // =========================================================

    public String privateKeyToBase64(
            PrivateKey privateKey) {

        if (privateKey == null) {

            throw new IllegalArgumentException(
                    "Private key cannot be null."
            );
        }

        return Base64.getEncoder()
                .encodeToString(
                        privateKey.getEncoded()
                );
    }


    // =========================================================
    // BASE64 → PUBLIC KEY
    // =========================================================

    public PublicKey publicKeyFromBase64(
            String base64)
            throws Exception {

        if (base64 == null
                || base64.isBlank()) {

            throw new IllegalArgumentException(
                    "Public key is empty."
            );
        }

        byte[] keyBytes =
                Base64.getDecoder()
                        .decode(base64);

        X509EncodedKeySpec keySpec =
                new X509EncodedKeySpec(
                        keyBytes
                );

        KeyFactory keyFactory =
                KeyFactory.getInstance(
                        RSA_ALGORITHM
                );

        return keyFactory.generatePublic(
                keySpec
        );
    }


    // =========================================================
    // BASE64 → PRIVATE KEY
    // =========================================================

    public PrivateKey privateKeyFromBase64(
            String base64)
            throws Exception {

        if (base64 == null
                || base64.isBlank()) {

            throw new IllegalArgumentException(
                    "Private key is empty."
            );
        }

        byte[] keyBytes =
                Base64.getDecoder()
                        .decode(base64);

        PKCS8EncodedKeySpec keySpec =
                new PKCS8EncodedKeySpec(
                        keyBytes
                );

        KeyFactory keyFactory =
                KeyFactory.getInstance(
                        RSA_ALGORITHM
                );

        return keyFactory.generatePrivate(
                keySpec
        );
    }


    // =========================================================
    // ENCRYPT AES KEY WITH XEROX PUBLIC KEY
    // =========================================================

    public byte[] encryptAesKey(
            byte[] aesKey,
            PublicKey xeroxPublicKey)
            throws Exception {

        if (aesKey == null
                || aesKey.length != 32) {

            throw new IllegalArgumentException(
                    "AES key must contain exactly 32 bytes."
            );
        }

        if (xeroxPublicKey == null) {

            throw new IllegalArgumentException(
                    "Xerox public key cannot be null."
            );
        }

        System.out.println(
                "[LOG - KeyProtection] "
                        + "Protecting AES-256 key with "
                        + "Xerox public key..."
        );

        Cipher cipher =
                Cipher.getInstance(
                        RSA_TRANSFORMATION
                );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                xeroxPublicKey,
                OAEP_SHA256
        );

        byte[] encryptedKey =
                cipher.doFinal(aesKey);

        System.out.println(
                "[LOG - KeyProtection] "
                        + "AES key protected successfully."
        );

        return encryptedKey;
    }


    // =========================================================
    // DECRYPT AES KEY WITH XEROX PRIVATE KEY
    // =========================================================

    public byte[] decryptAesKey(
            byte[] encryptedAesKey,
            PrivateKey xeroxPrivateKey)
            throws Exception {

        if (encryptedAesKey == null
                || encryptedAesKey.length == 0) {

            throw new IllegalArgumentException(
                    "Encrypted AES key is empty."
            );
        }

        if (xeroxPrivateKey == null) {

            throw new IllegalArgumentException(
                    "Xerox private key cannot be null."
            );
        }

        System.out.println(
                "[LOG - KeyProtection] "
                        + "Recovering AES key using "
                        + "Xerox private key..."
        );

        Cipher cipher =
                Cipher.getInstance(
                        RSA_TRANSFORMATION
                );

        cipher.init(
                Cipher.DECRYPT_MODE,
                xeroxPrivateKey,
                OAEP_SHA256
        );

        byte[] aesKey =
                cipher.doFinal(
                        encryptedAesKey
                );

        if (aesKey.length != 32) {

            throw new SecurityException(
                    "Recovered AES key is invalid."
            );
        }

        System.out.println(
                "[LOG - KeyProtection] "
                        + "AES-256 key recovered successfully."
        );

        return aesKey;
    }


    // =========================================================
    // BYTE[] → BASE64
    // =========================================================

    public String bytesToBase64(
            byte[] data) {

        if (data == null
                || data.length == 0) {

            throw new IllegalArgumentException(
                    "Data cannot be empty."
            );
        }

        return Base64.getEncoder()
                .encodeToString(data);
    }


    // =========================================================
    // BASE64 → BYTE[]
    // =========================================================

    public byte[] base64ToBytes(
            String base64) {

        if (base64 == null
                || base64.isBlank()) {

            throw new IllegalArgumentException(
                    "Base64 data is empty."
            );
        }

        return Base64.getDecoder()
                .decode(base64);
    }

    // =========================================================
// LOAD XEROX PRIVATE KEY FROM LOCAL MACHINE
// =========================================================
//
// Private key location:
//
// C:\Users\<WindowsUser>\.privora\keys\<XeroxUID>.private.key
//
// The private key is NEVER stored in Firestore.
//
// =========================================================

public PrivateKey loadXeroxPrivateKey(
        String xeroxUid)
        throws Exception {

    if (xeroxUid == null
            || xeroxUid.isBlank()) {

        throw new IllegalArgumentException(
                "Xerox UID cannot be empty."
        );
    }


    // =====================================================
    // KEY DIRECTORY
    // =====================================================

    Path keyDirectory =
            Paths.get(
                    System.getProperty("user.home"),
                    ".privora",
                    "keys"
            );


    // =====================================================
    // PRIVATE KEY FILE
    // =====================================================

    Path privateKeyFile =
            keyDirectory.resolve(
                    xeroxUid + ".private.key"
            );


    System.out.println(
            "[LOG - KeyProtection] "
                    + "Looking for Xerox private key..."
    );


    System.out.println(
            "[LOG - KeyProtection] "
                    + "Private key file exists = "
                    + Files.exists(privateKeyFile)
    );


    // =====================================================
    // CHECK FILE
    // =====================================================

    if (!Files.exists(privateKeyFile)) {

        throw new SecurityException(
                "Xerox private key was not found on this computer."
        );
    }


    if (!Files.isRegularFile(privateKeyFile)) {

        throw new SecurityException(
                "Xerox private key path is not a valid file."
        );
    }


    // =====================================================
    // READ BASE64 PRIVATE KEY
    // =====================================================

    String privateKeyBase64 =
            Files.readString(
                    privateKeyFile
            ).trim();


    if (privateKeyBase64.isBlank()) {

        throw new SecurityException(
                "Xerox private key file is empty."
        );
    }


    // =====================================================
    // CONVERT BASE64 → PRIVATE KEY
    // =====================================================

    PrivateKey privateKey =
            privateKeyFromBase64(
                    privateKeyBase64
            );


    System.out.println(
            "[LOG - KeyProtection] "
                    + "Xerox private key loaded successfully."
    );


    return privateKey;
}
}