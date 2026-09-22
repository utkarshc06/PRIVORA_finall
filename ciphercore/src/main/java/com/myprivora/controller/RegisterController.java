package com.myprivora.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

import com.myprivora.dao.UserDAO;
import com.myprivora.dao.XeroxCentreDAO;
import com.myprivora.extras.KeyProtectionService;
import com.myprivora.model.User;
import com.myprivora.model.XeroxCentre;

public class RegisterController {

    /*
     * Keep your existing Firebase API key here.
     *
     * Do not share it publicly.
     */
    private final String API_KEY =
            "A";

    private final UserDAO userDAO =
            new UserDAO();

    private final XeroxCentreDAO xeroxCentreDAO =
            new XeroxCentreDAO();

    private final KeyProtectionService
            keyProtectionService =
                    new KeyProtectionService();


    // =========================================================
    // SIGN UP
    // =========================================================

    public boolean signUp(
            String name,
            String email,
            String mobile,
            String password,
            String role) {

        try {

            // =================================================
            // FIREBASE AUTHENTICATION
            // =================================================

            JSONObject requestBody =
                    new JSONObject();

            requestBody.put(
                    "email",
                    email
            );

            requestBody.put(
                    "password",
                    password
            );

            requestBody.put(
                    "returnSecureToken",
                    true
            );


            HttpRequest request =
                    HttpRequest.newBuilder()

                            .uri(
                                    URI.create(
                                            "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key="
                                                    + API_KEY
                                    )
                            )

                            .header(
                                    "Content-Type",
                                    "application/json"
                            )

                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(
                                                    requestBody.toString()
                                            )
                            )

                            .build();


            HttpClient client =
                    HttpClient.newHttpClient();


            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );


            // =================================================
            // CHECK FIREBASE RESPONSE
            // =================================================

            JSONObject responseJson =
                    new JSONObject(
                            response.body()
                    );


            if (response.statusCode() != 200) {

                System.out.println(
                        "Registration failed:"
                );

                System.out.println(
                        responseJson.toString(2)
                );

                return false;
            }


            // =================================================
            // GET FIREBASE UID
            // =================================================

            String uid =
                    responseJson.getString(
                            "localId"
                    );


            System.out.println(
                    "Firebase account created."
            );

            System.out.println(
                    "UID: " + uid
            );


            // =================================================
            // CREATE USER OBJECT
            // =================================================

            User user =
                    new User(
                            uid,
                            name,
                            email,
                            mobile,
                            null,
                            role
                    );


            // =================================================
            // SAVE USER IN FIRESTORE
            // =================================================

            boolean userSaved =
                    userDAO.saveUser(user);


            if (!userSaved) {

                System.out.println(
                        "User could not be saved in Firestore."
                );

                return false;
            }


            // =================================================
            // IF ROLE IS XEROX
            // CREATE XEROX CENTRE
            // =================================================

            if ("XEROX".equalsIgnoreCase(
                    role.trim()
            )) {

                // =================================================
                // GENERATE XEROX RSA KEY PAIR
                // =================================================

                System.out.println(
                        "[LOG - RegisterController] "
                                + "Generating Xerox RSA key pair..."
                );

                KeyPair keyPair =
                        keyProtectionService
                                .generateKeyPair();


                // =================================================
                // CONVERT PUBLIC KEY TO BASE64
                // =================================================

                String publicKey =
                        keyProtectionService
                                .publicKeyToBase64(
                                        keyPair.getPublic()
                                );


                // =================================================
                // CREATE XEROX CENTRE
                // =================================================

                XeroxCentre centre =
                        new XeroxCentre(
                                uid,
                                name,
                                email,
                                mobile,
                                "XEROX",
                                "ACTIVE",
                                true
                        );


                /*
                 * Store ONLY the public key in Firestore.
                 *
                 * The private key is NOT stored in Firestore.
                 */
                centre.setPublicKey(
                        publicKey
                );


                // =================================================
                // SAVE XEROX CENTRE
                // =================================================

                boolean centreSaved =
                        xeroxCentreDAO
                                .saveXeroxCentre(
                                        centre
                                );


                if (!centreSaved) {

                    System.out.println(
                            "User was created, but Xerox centre profile could not be created."
                    );

                    return false;
                }


                // =================================================
                // SAVE PRIVATE KEY LOCALLY
                // =================================================

                savePrivateKeyLocally(
                        uid,
                        keyPair
                );


                System.out.println(
                        "Xerox centre profile created successfully."
                );

                System.out.println(
                        "Xerox public key saved to Firestore."
                );

                System.out.println(
                        "Xerox private key saved locally."
                );
            }


            System.out.println(
                    "Registration completed successfully."
            );

            return true;


        } catch (Exception e) {

            System.out.println(
                    "Error during registration."
            );

            e.printStackTrace();

            return false;
        }
    }


    // =========================================================
    // SAVE PRIVATE KEY LOCALLY
    // =========================================================

    private void savePrivateKeyLocally(
            String uid,
            KeyPair keyPair)
            throws IOException {

        if (uid == null
                || uid.isBlank()) {

            throw new IllegalArgumentException(
                    "Xerox UID cannot be empty."
            );
        }

        if (keyPair == null
                || keyPair.getPrivate() == null) {

            throw new IllegalArgumentException(
                    "Private key cannot be null."
            );
        }


        // =====================================================
        // PRIVORA LOCAL KEY DIRECTORY
        // =====================================================

        Path keyDirectory =
                Paths.get(
                        System.getProperty("user.home"),
                        ".privora",
                        "keys"
                );


        Files.createDirectories(
                keyDirectory
        );


        // =====================================================
        // PRIVATE KEY FILE
        // =====================================================

        Path privateKeyFile =
                keyDirectory.resolve(
                        uid + ".private.key"
                );


        // =====================================================
        // PRIVATE KEY → BASE64
        // =====================================================

        String privateKeyBase64 =
                keyProtectionService
                        .privateKeyToBase64(
                                keyPair.getPrivate()
                        );


        // =====================================================
        // WRITE PRIVATE KEY
        // =====================================================

        Files.writeString(
                privateKeyFile,
                privateKeyBase64,
                StandardCharsets.UTF_8
        );


        System.out.println(
                "[LOG - RegisterController] "
                        + "Private key stored locally at:"
        );

        System.out.println(
                privateKeyFile
        );
    }
}